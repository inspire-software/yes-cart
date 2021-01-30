/*
 * Copyright 2009 Inspire-Software.com
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.yes.cart.bulkjob.bulkimport;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.yes.cart.bulkcommon.service.ImportDirectorService;
import org.yes.cart.bulkjob.cron.AbstractCronJobProcessorImpl;
import org.yes.cart.bulkjob.impl.BulkJobAutoContextImpl;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.entity.Job;
import org.yes.cart.domain.entity.JobDefinition;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.async.AsyncContextFactory;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.async.impl.JobStatusListenerImpl;
import org.yes.cart.service.async.impl.JobStatusListenerWithLoggerImpl;
import org.yes.cart.service.async.model.AsyncContext;
import org.yes.cart.service.async.model.JobStatus;
import org.yes.cart.service.async.utils.RunAsUserAuthentication;
import org.yes.cart.service.cluster.ClusterService;
import org.yes.cart.service.cluster.ReindexService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.utils.DateUtils;
import org.yes.cart.utils.ShopCodeContext;
import org.yes.cart.utils.log.Markers;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Local FS import listener allows to scan local FS directory structure to detect incoming
 * import files that can be sent to the import service automatically.
 * <p>
 * Expected directory structure is:
 * <pre>
 *     ROOT
 *      |- SHOP10
 *      |     |- archived
 *      |     |- incoming
 *      |     |- processed
 *      |     |- processing
 *      |
 *      |- SHOP11
 *      |     | ...
 *      | ...
 * </pre>
 * <p>
 * 'archived' directory is used by external processing system
 * <p>
 * 'config' directory is mandatory and contains config.properties file.
 * config/config.properties must be configured with import groups and corresponding file regex's so that bulk import
 * can associate import file with type of import group it should be processed with.
 * <p>
 * Example configurations:
 * <pre>
 *   file-import-root=/home/yc/server/share/autoimport
 *   SHOP10.config.0.group=YC DEMO: Initial Data
 *   SHOP10.config.0.regex=import\\.zip
 *   SHOP10.config.0.reindex=true
 *   SHOP10.config.0.user=admin@yes-cart.com
 *   SHOP10.config.0.pass=1234567
 *   SHOP10.config.1.group=YC DEMO: IceCat Catalog
 *   SHOP10.config.1.regex=import\\-EN,DE,UK,RU\\.zip
 *   SHOP10.config.1.reindex=true
 *   SHOP10.config.1.user=admin@yes-cart.com
 *   SHOP10.config.1.pass=1234567
 *   SHOP10.config.2.group=YC DEMO: Product images (IceCat)
 *   SHOP10.config.2.regex=import\\-EN,DE,UK,RU\\-img\\.zip
 *   SHOP10.config.2.reindex=true
 *   SHOP10.config.2.user=admin@yes-cart.com
 *   SHOP10.config.2.pass=1234567
 *   SHOP11.config.0.group=Another group
 *   SHOP11.config.0.regex=import\\.zip
 *   SHOP11.config.0.reindex=true
 *   SHOP11.config.0.user=admin@yes-cart.com
 *   SHOP11.config.0.pass=1234567
 * </pre>
 * <p>
 * 'incoming' directory is used by extrenal processing system to put raw import files
 * <p>
 * 'processed' directory is mandatory that this listener scans for ready to use import files
 * <p>
 * 'processing' directory is used by extrenal processing system to generate temporary files from files in 'incoming' before
 * they get assembled into actual import files and put to 'processed'.
 * <p>
 * Therefore:
 * <p>
 * External systems: 1) upload raw file to 'incoming', 2) process this file and generate any supporting intermediate files
 * in 'processing', 3) places ready to import file in 'processed'.
 * <p>
 * This listener: 1) reads 'config.properties', 2) scans files in 'processed' and matches them up to import groups, 3) launches
 * bulk import service for each files using appropriate import group.
 *
 * User: denispavlov
 * Date: 21/10/2015
 * Time: 17:05
 */
public class LocalFileShareImportListenerImpl extends AbstractCronJobProcessorImpl {

    private static final Logger LOG = LoggerFactory.getLogger(LocalFileShareImportListenerImpl.class);

    public static final long INDEX_GET_READY_TIMEOUT = 5000L;
    public static final long INDEX_PING_INTERVAL = 15000L;
    public static final long WARMUP_GET_READY_TIMEOUT = 15000L;

    private ShopService shopService;
    private ImportDirectorService importDirectorService;
    private ReindexService reindexService;
    private ClusterService clusterService;
    private AsyncContextFactory asyncContextFactory;

    private AuthenticationManager authenticationManager;

    private final JobStatusListener listener = new JobStatusListenerWithLoggerImpl(new JobStatusListenerImpl(), LOG);

    /** {@inheritDoc} */
    @Override
    public JobStatus getStatus(final String token) {
        return listener.getLatestStatus();
    }

    /** {@inheritDoc} */
    @Override
    public Pair<JobStatus, Instant> processInternal(final Map<String, Object> context, final Job job, final JobDefinition definition) {

        listener.reset();

        final Properties properties = readContextAsProperties(context, job, definition);

        listener.notifyPing("Auto import started");

        final String fsRoot = properties.getProperty("file-import-root");
        if (StringUtils.isBlank(fsRoot)) {
            listener.notifyError("file-import-root is not set... terminating");
            listener.notifyCompleted();
            return new Pair<>(listener.getLatestStatus(), null);
        }

        final File root = new File(fsRoot);
        final boolean exists = root.exists();
        final boolean readableDir = exists && root.canRead() && root.isDirectory();
        if (!readableDir) {
            listener.notifyError("file-import-root location '{}' is invalid (exists: {}, readable: {})  ... terminating",
                    root.getAbsolutePath(), exists ? "yes" : "no", "no");
            listener.notifyCompleted();
            return new Pair<>(listener.getLatestStatus(), null);
        }

        listener.notifyInfo("file-import-root location '{}'... ",
                root.getAbsolutePath());

        runRootScan(root, properties);

        listener.notifyCompleted();

        return new Pair<>(listener.getLatestStatus(), null);
        
    }

    private void runRootScan(final File root, final Properties properties) {

        final File[] shopDirs = root.listFiles();

        if (shopDirs != null) {

            final String importDirPath = importDirectorService.getImportDirectory();
            listener.notifyInfo("Detected import directory root {}", importDirPath);

            try {

                for (final File shopDir : shopDirs) {

                    runShopRootScan(shopDir, importDirPath, properties);

                }

            } catch (Exception exp) {

                listener.notifyError("Auto import failure: {}", exp.getMessage());
                LOG.error(Markers.alert(), "Auto import failure: " + exp.getMessage(), exp);

            }

        }
    }

    private void runShopRootScan(final File shopDir, final String importDirPath, final Properties properties) {

        listener.notifyPing("Auto import processing {}", shopDir.getAbsoluteFile());

        final Shop shop = shopService.getShopByCode(shopDir.getName());
        if (shop != null) {

            ShopCodeContext.setShopCode(shop.getCode());
            ShopCodeContext.setShopId(shop.getShopId());

            try {

                listener.notifyInfo("Scanning processed directory for shop {}", shop.getCode());

                final Map<Pattern, Map<String, String>> patternGroupMap = loadShopAutoImportConfigurations(shop.getCode(), properties);
                if (patternGroupMap.isEmpty()) {
                    listener.notifyInfo("Configurations are missing for shop {} ... skipping", shop.getCode());
                    return;
                }

                final File processed = ensureDirectoryExists(shopDir, shop, "processed");

                final File[] readyForImport = processed.listFiles();
                if (readyForImport == null || readyForImport.length == 0) {
                    listener.notifyInfo("No new files to import for shop {}", shop.getCode());
                    return;
                }

                final int total = readyForImport.length;
                int count = 1;
                for (final File toImport : prioritiseProcessedFiles(readyForImport)) {

                    listener.notifyInfo("Processing file {} of {}", count, total);
                    count++;

                    final String timestamp = DateUtils.autoImportTimestamp();

                    final File targetDirectory = new File(importDirPath + File.separator + PRINCIPAL + timestamp);
                    targetDirectory.mkdirs();

                    listener.notifyInfo("Moving file to '{}' for shop {}", targetDirectory.getAbsolutePath(), shop.getCode());

                    Map<String, String> groupData = null;
                    for (final Map.Entry<Pattern, Map<String, String>> group : patternGroupMap.entrySet()) {
                        if (group.getKey().matcher(toImport.getName()).matches()) {
                            groupData = group.getValue();
                            break;
                        }
                    }

                    if (groupData == null) {
                        listener.notifyWarning("Importing '{}' for shop {} ... skipping (no valid import group)", toImport.getAbsolutePath(), shop.getCode());
                        continue;
                    }

                    final String groupName = groupData.get("group");
                    final String user = groupData.get("user");
                    final String pass = groupData.get("pass");

                    final Authentication shopAuth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user, pass));
                    if (shopAuth.isAuthenticated()) {
                        SecurityContextHolder.getContext().setAuthentication(new RunAsUserAuthentication(user, pass, shopAuth.getAuthorities()));

                        final long startImport = getTimeNow();

                        if (!getImportGroupNames().contains(groupName)) {
                            listener.notifyWarning("Configuration {} is not a valid import group ... skipping", groupName);
                            continue;
                        }

                        listener.notifyInfo("Importing '{}' for shop {} using group {}", toImport.getAbsolutePath(), shop.getCode(), groupName);

                        final String destination = moveFileToImportDirectory(toImport, targetDirectory);


                        // Make this synchronous since we are already in async process
                        final JobStatus importStatus = importDirectorService.doImport(groupName, destination, false);
                        final String importToken = importStatus.getToken();

                        final long finishImport = getTimeNow();
                        final long msImport = (finishImport - startImport);
                        final long secImport = msImport > 0 ? msImport / 1000 : 0;

                        listener.notifyInfo("Importing '{}' for shop {} using group {} ... completed [{}] in {}s", toImport.getAbsolutePath(), shop.getCode(), groupName, importStatus.getCompletion(), secImport);

                        final AsyncContext cacheCtx = createCtx(AttributeNamesKeys.System.SYSTEM_CONNECTOR_TIMEOUT_MS);
                        clusterService.evictAllCache(cacheCtx);

                        if (importStatus.getCompletion() == JobStatus.Completion.OK) {

                            final boolean reindex = Boolean.valueOf(groupData.get("reindex"));
                            if (reindex) {

                                final long startIndex = getTimeNow();

                                listener.notifyInfo("Re-indexed products for shop {} using group {} ... starting", shop.getCode(), groupName);

                                final AsyncContext reindexCtx = createCtx(AttributeNamesKeys.System.SYSTEM_CONNECTOR_PRODUCT_BULK_INDEX_TIMEOUT_MS);
                                Thread.sleep(INDEX_GET_READY_TIMEOUT); // let cache invalidation run before index
                                final JobStatus indexToken = reindexService.reindexAllProducts(reindexCtx);
                                while (true) {
                                    Thread.sleep(INDEX_PING_INTERVAL);
                                    JobStatus reindexStatus = reindexService.getIndexJobStatus(reindexCtx, indexToken.getToken());
                                    if (reindexStatus.getState() == JobStatus.State.FINISHED) {

                                        final long finishIndex = getTimeNow();
                                        final long msIndex = (finishIndex - startIndex);
                                        final long secIndex = msIndex > 0 ? msIndex / 1000 : 0;

                                        LOG.info("Re-indexed products for shop {} using group {} ... completed [{}] in {}s", shop.getCode(), groupName, reindexStatus.getCompletion(), secIndex);

                                        clusterService.evictAllCache(cacheCtx);
                                        Thread.sleep(WARMUP_GET_READY_TIMEOUT);
                                        clusterService.warmUp(cacheCtx);

                                        break;
                                    }
                                }

                            }
                        }



                    } else {
                        listener.notifyWarning("Invalid credentials for '{}' for shop {} using group {}", user, shop.getCode(), groupName);
                    }

                }
            } catch (Exception exp) {
                listener.notifyError("Failed import configuration {}", exp, shop.getCode());
            } finally {
                ShopCodeContext.clear();
            }
        }

    }

    private long getTimeNow() {
        return System.currentTimeMillis();
    }

    private Map<Pattern, Map<String, String>> loadShopAutoImportConfigurations(final String shopCode, final Properties configuration) throws IOException {

        final Map<Pattern, Map<String, String>> patternGroupMap = new HashMap<>();

        boolean hasAtLeastOneConfig = false;
        for (int i = 0; true; i++) {

            final String cfgGroup = configuration.getProperty(shopCode + ".config." + i + ".group");
            if (StringUtils.isBlank(cfgGroup)) {
                break;  // finished as there are not more configs (by convention)
            }

            final String cfgRegex = configuration.getProperty(shopCode + ".config." + i + ".regex");
            if (StringUtils.isBlank(cfgRegex)) {
                listener.notifyWarning("Configuration {} has no regex ... skipping", cfgGroup);
                continue;
            }

            final String cfgUser = configuration.getProperty(shopCode + ".config." + i + ".user");
            if (StringUtils.isBlank(cfgUser)) {
                listener.notifyWarning("Configuration {} has no user ... skipping", cfgGroup);
                continue;
            }

            final String cfgPass = configuration.getProperty(shopCode + ".config." + i + ".pass");
            if (StringUtils.isBlank(cfgPass)) {
                listener.notifyWarning("Configuration {} has no password ... skipping", cfgGroup);
                continue;
            }

            final String cfgIndex = configuration.getProperty(shopCode + ".config." + i + ".reindex");

            try {
                final Pattern regex = Pattern.compile(cfgRegex);

                final Map<String, String> data = new HashMap<>();
                data.put("group", cfgGroup.trim());
                data.put("user", cfgUser.trim());
                data.put("pass", cfgPass.trim());
                data.put("reindex", cfgIndex.trim());

                patternGroupMap.put(regex, data);

                listener.notifyInfo("Configuration loaded for group: {} has regex {}", cfgGroup, cfgRegex);

                hasAtLeastOneConfig = true;
            } catch (Exception exp) {
                listener.notifyWarning("Configuration {} has INVALID regex {} ... skipping", cfgGroup, cfgRegex);
            }
        }
        if (!hasAtLeastOneConfig) {
            listener.notifyWarning("Configurations for {} does not have any configurations", shopCode);
        }
        return patternGroupMap;
    }

    private String moveFileToImportDirectory(final File toImport, final File importDirectory) throws IOException {

        if (importDirectory.exists()) {

            final File file = new File(importDirectory.getAbsolutePath(), toImport.getName());
            if (file.exists()) {
                throw new IllegalArgumentException("File: " + file.getName() + " is already being processed. If this is a different file - rename it and try again.");
            }

            if (!toImport.renameTo(file)) {
                throw new IOException("Unable to write to directory (rename failed): " + importDirectory.getAbsolutePath());
            }

            return file.getAbsolutePath();

        } else {
            throw new IOException("Unable to write to directory (directory does not exist): " + importDirectory.getAbsolutePath());
        }


    }

    private Set<String> getImportGroupNames() {

        final List<Map<String, String>> importGroupsMap = importDirectorService.getImportGroups("en");
        final Set<String> importGroupNames = new HashSet<>();
        for (final Map<String, String> group : importGroupsMap) {
            importGroupNames.add(group.get("name"));
        }
        listener.notifyInfo("Detected import groups: {}", StringUtils.join(importGroupNames, ","));

        return importGroupNames;

    }

    /**
     * As this is asynchronous process we can have multitude of files, maybe even for
     * multiple imports. Therefore to apply them correctly we need to sort the files in correct
     * chronological and optionally logical order.
     *
     * Default (this) implementation relies on the fact that the hint shall be imprinted in name of
     * the file via filename convention [randomname].yyyyMMddHHmmss.[ext]. If this is not the case
     * then files will be sorted by creation date.
     *
     * @param files files in processed directory
     *
     * @return prioritised list of files
     */
    protected List<File> prioritiseProcessedFiles(final File[] files) {
        final List<File> prioritised = new ArrayList<>(Arrays.asList(files));
        prioritised.sort(PRIORITY);
        return prioritised;
    }

    private static final Comparator<File> PRIORITY = new ProcessedFilesComparator();

    /**
     * Ensure that directory exists, so that we can scan it.
     *
     * @param shopDir root of shop import directory structure
     * @param shop shop instance
     * @param dirname name of directory under shop dir
     *
     * @return directory
     */
    protected File ensureDirectoryExists(final File shopDir, final Shop shop, final String dirname) throws IOException {

        final File dir = new File(shopDir, dirname);
        if (!dir.exists()) {
            listener.notifyInfo("Proactively creating '{}' sub directory for shop {}", dir.getAbsolutePath(), shop.getCode());
            if (!dir.mkdirs()) {
                throw new IOException("Failed to create '" +
                        dir.getAbsolutePath() + "' sub directory for shop " + shop.getCode());
            }
        }
        return dir;

    }

    private static final String PRINCIPAL = "AutoImport";

    private AsyncContext createCtx(final String cacheTimeOutKey) {
        final Map<String, Object> param = new HashMap<>();
        param.put(AsyncContext.TIMEOUT_KEY, cacheTimeOutKey);
        return createCtx(param);
    }

    private AsyncContext createCtx(final Map<String, Object> param) {
        try {
            // This is manual access via Admin
            return asyncContextFactory.getInstance(param);
        } catch (IllegalStateException exp) {
            // This is auto access with thread local
            return new BulkJobAutoContextImpl(param);
        }
    }

    /**
     * Spring IoC.
     *
     * @param shopService service
     */
    public void setShopService(final ShopService shopService) {
        this.shopService = shopService;
    }

    /**
     * Spring IoC.
     *
     * @param importDirectorService service
     */
    public void setImportDirectorService(final ImportDirectorService importDirectorService) {
        this.importDirectorService = importDirectorService;
    }

    /**
     * Spring IoC.
     *
     * @param reindexService service
     */
    public void setReindexService(final ReindexService reindexService) {
        this.reindexService = reindexService;
    }

    /**
     * Spring IoC.
     *
     * @param clusterService service
     */
    public void setClusterService(final ClusterService clusterService) {
        this.clusterService = clusterService;
    }

    /**
     * Spring IoC.
     *
     * @param asyncContextFactory service
     */
    public void setAsyncContextFactory(final AsyncContextFactory asyncContextFactory) {
        this.asyncContextFactory = asyncContextFactory;
    }

    /**
     * Spring IoC.
     *
     * @param authenticationManager service
     */
    public void setAuthenticationManager(final AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }
}
