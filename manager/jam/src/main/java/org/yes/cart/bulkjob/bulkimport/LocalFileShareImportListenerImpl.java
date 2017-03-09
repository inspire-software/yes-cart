/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.yes.cart.bulkcommon.service.ImportDirectorService;
import org.yes.cart.bulkjob.impl.BulkJobAutoContextImpl;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.service.async.model.AsyncContext;
import org.yes.cart.service.async.model.JobStatus;
import org.yes.cart.service.async.utils.RunAsUserAuthentication;
import org.yes.cart.service.cluster.ClusterService;
import org.yes.cart.service.cluster.ReindexService;
import org.yes.cart.service.domain.RuntimeAttributeService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.service.domain.SystemService;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.util.log.Markers;
import org.yes.cart.web.service.ws.client.AsyncContextFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Local FS import listener allows to scan local FS directory structure to detect imcoming
 * import files that can be sent to the import service automatically.
 * <p>
 * Expected directory structure is:
 * <pre>
 *     ROOT
 *      |- SHOP10
 *      |     |- archived
 *      |     |- config
 *      |     |    |- config.properties
 *      |     |- incoming
 *      |     |- processed
 *      |     |- processing
 *      |
 *      |- SHOP10
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
 *   config.0.group=YC DEMO: Initial Data
 *   config.0.regex=import\\.zip
 *   config.0.reindex=true
 *   config.0.user=admin@yes-cart.com
 *   config.0.pass=1234567
 *   config.1.group=YC DEMO: IceCat Catalog
 *   config.1.regex=import\\-EN,DE,UK,RU\\.zip
 *   config.1.reindex=true
 *   config.1.user=admin@yes-cart.com
 *   config.1.pass=1234567
 *   config.2.group=YC DEMO: Product images (IceCat)
 *   config.2.regex=import\\-EN,DE,UK,RU\\-img\\.zip
 *   config.2.reindex=true
 *   config.2.user=admin@yes-cart.com
 *   config.2.pass=1234567
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
public class LocalFileShareImportListenerImpl implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(LocalFileShareImportListenerImpl.class);

    private static final String FS_PREF = "JOB_LOCAL_FILE_IMPORT_FS_ROOT";
    public static final long INDEX_GET_READY_TIMEOUT = 5000L;
    public static final long INDEX_PING_INTERVAL = 15000L;
    public static final long WARMUP_GET_READY_TIMEOUT = 15000L;

    private final ShopService shopService;
    private final ImportDirectorService importDirectorService;
    private final ReindexService reindexService;
    private final ClusterService clusterService;
    private final AsyncContextFactory asyncContextFactory;
    private final SystemService systemService;
    private final RuntimeAttributeService runtimeAttributeService;

    private final AuthenticationManager authenticationManager;

    public LocalFileShareImportListenerImpl(final ShopService shopService,
                                            final ImportDirectorService importDirectorService,
                                            final ReindexService reindexService,
                                            final ClusterService clusterService,
                                            final AsyncContextFactory asyncContextFactory,
                                            final SystemService systemService,
                                            final RuntimeAttributeService runtimeAttributeService,
                                            final AuthenticationManager authenticationManager) {
        this.shopService = shopService;
        this.clusterService = clusterService;
        this.asyncContextFactory = asyncContextFactory;
        this.systemService = systemService;
        this.runtimeAttributeService = runtimeAttributeService;
        this.importDirectorService = importDirectorService;
        this.reindexService = reindexService;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public void run() {

        LOG.info("Local file share listener");

        final String fsRoot = systemService.getAttributeValue(FS_PREF);
        if (StringUtils.isBlank(fsRoot)) {
            LOG.error("{} is not set... terminating", FS_PREF);
            return;
        }

        final File root = new File(fsRoot);
        final boolean exists = root.exists();
        final boolean readableDir = exists && root.canRead() && root.isDirectory();
        if (!readableDir) {
            LOG.error("{} location '{}' is invalid (exists: {}, readable: {})  ... terminating",
                    new Object[]{FS_PREF, root.getAbsolutePath(), exists ? "yes" : "no", "no"});
            return;
        }

        LOG.info("{} location '{}'... ",
                new Object[]{FS_PREF, root.getAbsolutePath()});

        runRootScan(root);

        LOG.info("Local file share listener ... completed");

    }

    private void runRootScan(final File root) {

        final File[] shopDirs = root.listFiles();

        if (shopDirs != null) {

            SecurityContextHolder.getContext().setAuthentication(global);

            final String importDirPath = importDirectorService.getImportDirectory();
            LOG.info("Detected import directory root {}", importDirPath);

            final List<Map<String, String>> importGroupsMap = importDirectorService.getImportGroups("en");
            final Set<String> importGroupNames = new HashSet<String>();
            for (final Map<String, String> group : importGroupsMap) {
                importGroupNames.add(group.get("name"));
            }
            LOG.info("Detected import groups: {}", StringUtils.join(importGroupNames, ","));


            try {

                for (final File shopDir : shopDirs) {

                    runShopRootScan(shopDir, importDirPath, importGroupNames);

                }

            } catch (Exception exp) {

                LOG.error(Markers.alert(), "Auto import failure: " + exp.getMessage(), exp);

            } finally {

                SecurityContextHolder.clearContext();

            }

        }
    }

    private void runShopRootScan(final File shopDir, final String importDirPath, final Set<String> importGroupNames) {

        final Shop shop = shopService.getShopByCode(shopDir.getName());
        if (shop != null) {

            ShopCodeContext.setShopCode(shop.getCode());
            ShopCodeContext.setShopId(shop.getShopId());

            try {

                LOG.info("Scanning processed directory for shop {}", shop.getCode());

                final File config = ensureDirectoryExists(shopDir, shop, "config");
                final File configProps = new File(config, "config.properties");
                if (!configProps.exists()) {
                    LOG.info("Configuration file is missing for shop {} ... skipping", shop.getCode());
                    return;
                }

                final Map<Pattern, Map<String, String>> patternGroupMap = loadShopAutoImportConfigurations(configProps, importGroupNames);

                final File processed = ensureDirectoryExists(shopDir, shop, "processed");

                final File[] readyForImport = processed.listFiles();
                if (readyForImport == null || readyForImport.length == 0) {
                    LOG.info("No new files to import for shop {}", shop.getCode());
                    return;
                }

                final SimpleDateFormat format = new SimpleDateFormat("_yyyy-MMM-dd-hh-mm-ss-SSS");

                final int total = readyForImport.length;
                int count = 1;
                for (final File toImport : prioritiseProcessedFiles(readyForImport)) {

                    LOG.info("Processing file {} of {}", count, total);
                    count++;

                    final String timestamp = format.format(new Date());

                    final File targetDirectory = new File(importDirPath + File.separator + PRINCIPAL + timestamp);
                    targetDirectory.mkdirs();

                    LOG.info("Moving file to '{}' for shop {}", targetDirectory.getAbsolutePath(), shop.getCode());

                    Map<String, String> groupData = null;
                    for (final Map.Entry<Pattern, Map<String, String>> group : patternGroupMap.entrySet()) {
                        if (group.getKey().matcher(toImport.getName()).matches()) {
                            groupData = group.getValue();
                            break;
                        }
                    }

                    if (groupData == null) {
                        LOG.warn("Importing '{}' for shop {} ... skipping (no valid import group)", toImport.getAbsolutePath(), shop.getCode());
                        continue;
                    }

                    final String groupName = groupData.get("group");

                    LOG.info("Importing '{}' for shop {} using group {}", new Object[] { toImport.getAbsolutePath(), shop.getCode(), groupName });

                    final String destination = moveFileToImportDirectory(toImport, targetDirectory);


                    try {

                        final String user = groupData.get("user");
                        final String pass = groupData.get("pass");

                        final Authentication shopAuth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user, pass));
                        if (shopAuth.isAuthenticated()) {
                            SecurityContextHolder.getContext().setAuthentication(new RunAsUserAuthentication(user, pass, shopAuth.getAuthorities()));

                            final long startImport = getTimeNow();

                            // Make this synchronous since we are already in async process
                            final String importToken = importDirectorService.doImport(groupName, destination, false);
                            final JobStatus importStatus = importDirectorService.getImportStatus(importToken);

                            final long finishImport = getTimeNow();
                            final long msImport = (finishImport - startImport);
                            final long secImport = msImport > 0 ? msImport / 1000 : 0;

                            LOG.info("Importing '{}' for shop {} using group {} ... completed [{}] in {}s", new Object[] { toImport.getAbsolutePath(), shop.getCode(), groupName, importStatus.getCompletion(), secImport });

                            final AsyncContext cacheCtx = createCtx(AttributeNamesKeys.System.SYSTEM_BACKDOOR_TIMEOUT_MS);
                            clusterService.evictAllCache(cacheCtx);

                            if (importStatus.getCompletion() == JobStatus.Completion.OK) {

                                final boolean reindex = Boolean.valueOf(groupData.get("reindex"));
                                if (reindex) {

                                    final long startIndex = getTimeNow();

                                    LOG.info("Re-indexed products for shop {} using group {} ... starting", new Object[] { shop.getCode(), groupName });

                                    final AsyncContext reindexCtx = createCtx(AttributeNamesKeys.System.SYSTEM_BACKDOOR_PRODUCT_BULK_INDEX_TIMEOUT_MS);
                                    Thread.sleep(INDEX_GET_READY_TIMEOUT); // let cache invalidation run before index
                                    final String indexToken = reindexService.reindexAllProducts(reindexCtx);
                                    while (true) {
                                        Thread.sleep(INDEX_PING_INTERVAL);
                                        JobStatus reindexStatus = reindexService.getIndexJobStatus(reindexCtx, indexToken);
                                        if (reindexStatus.getState() == JobStatus.State.FINISHED) {

                                            final long finishIndex = getTimeNow();
                                            final long msIndex = (finishIndex - startIndex);
                                            final long secIndex = msIndex > 0 ? msIndex / 1000 : 0;

                                            LOG.info("Re-indexed products for shop {} using group {} ... completed [{}] in {}s", new Object[] { shop.getCode(), groupName, reindexStatus.getCompletion(), secIndex });

                                            clusterService.evictAllCache(cacheCtx);
                                            Thread.sleep(WARMUP_GET_READY_TIMEOUT);
                                            clusterService.warmUp(cacheCtx);

                                            break;
                                        }
                                    }

                                }
                            }



                        } else {
                            LOG.warn("Invalid credentials for '{}' for shop {} using group {}", new Object[]{user, shop.getCode(), groupName});
                        }

                    } finally {
                        // Reinstate global context of AutoImport
                        SecurityContextHolder.getContext().setAuthentication(global);
                    }


                }
            } catch (Exception exp) {
                LOG.error("Failed import configuration " + shop.getCode(), exp);
            } finally {
                ShopCodeContext.clear();
            }
        }

    }

    private long getTimeNow() {
        return System.currentTimeMillis();
    }

    private Map<Pattern, Map<String, String>> loadShopAutoImportConfigurations(final File configProps, final Set<String> importGroupNames) throws IOException {

        final Properties configuration = new Properties();
        configuration.load(new FileInputStream(configProps));

        final Map<Pattern, Map<String, String>> patternGroupMap = new HashMap<Pattern, Map<String, String>>();

        for (int i = 0; true; i++) {

            final String cfgGroup = configuration.getProperty("config." + i + ".group");
            if (StringUtils.isBlank(cfgGroup)) {
                break;  // finished as there are not more configs (by convention)
            } else if (!importGroupNames.contains(cfgGroup)) {
                LOG.warn("Configuration {} is not a valid import group ... skipping", cfgGroup);
                continue;
            }

            final String cfgRegex = configuration.getProperty("config." + i + ".regex");
            if (StringUtils.isBlank(cfgRegex)) {
                LOG.warn("Configuration {} has no regex ... skipping", cfgGroup);
                continue;
            }

            final String cfgUser = configuration.getProperty("config." + i + ".user");
            if (StringUtils.isBlank(cfgUser)) {
                LOG.warn("Configuration {} has no user ... skipping", cfgGroup);
                continue;
            }

            final String cfgPass = configuration.getProperty("config." + i + ".pass");
            if (StringUtils.isBlank(cfgPass)) {
                LOG.warn("Configuration {} has no password ... skipping", cfgGroup);
                continue;
            }

            final String cfgIndex = configuration.getProperty("config." + i + ".reindex");

            try {
                final Pattern regex = Pattern.compile(cfgRegex);

                final Map<String, String> data = new HashMap<String, String>();
                data.put("group", cfgGroup);
                data.put("user", cfgUser);
                data.put("pass", cfgPass);
                data.put("reindex", cfgIndex);

                patternGroupMap.put(regex, data);

                LOG.info("Configuration {} has regex {}", cfgGroup, cfgRegex);

            } catch (Exception exp) {
                LOG.warn("Configuration {} has INVALID regex {} ... skipping", cfgGroup, cfgRegex);
            }
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
        final List<File> prioritised = new ArrayList<File>(Arrays.asList(files));
        Collections.sort(prioritised, PRIORITY);
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
            LOG.info("Proactively creating '{}' sub directory for shop {}", dir.getAbsolutePath(), shop.getCode());
            if (!dir.mkdirs()) {
                throw new IOException("Failed to create '" +
                        dir.getAbsolutePath() + "' sub directory for shop " + shop.getCode());
            }
        }
        return dir;

    }

    private static final String PRINCIPAL = "AutoImport";

    /**
     * This is a custom authentication to allow listener to send command to bulk import service
     */
    private final Authentication global = new RunAsUserAuthentication(
            PRINCIPAL, null,
            Arrays.asList(new GrantedAuthority[]{
                    new SimpleGrantedAuthority("ROLE_SMADMIN"),
                    new SimpleGrantedAuthority("ROLE_AUTO")
            })
    );

    private AsyncContext createCtx(final String cacheTimeOutKey) {
        final Map<String, Object> param = new HashMap<String, Object>();
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

}
