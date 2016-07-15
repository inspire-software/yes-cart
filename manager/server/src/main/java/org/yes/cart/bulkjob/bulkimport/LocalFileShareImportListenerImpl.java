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

import edu.emory.mathcs.backport.java.util.Arrays;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.yes.cart.bulkcommon.service.ImportDirectorService;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.remote.service.ReindexService;
import org.yes.cart.remote.service.RemoteDevService;
import org.yes.cart.service.async.model.JobStatus;
import org.yes.cart.service.async.utils.RunAsUserAuthentication;
import org.yes.cart.service.domain.RuntimeAttributeService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.service.domain.SystemService;
import org.yes.cart.util.ShopCodeContext;

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

    private static final String PAUSE_PREF = "JOB_LOCAL_FILE_IMPORT_PAUSE";
    private static final String FS_PREF = "JOB_LOCAL_FILE_IMPORT_FS_ROOT";
    public static final long INDEX_GET_READY_TIMEOUT = 5000L;
    public static final long INDEX_PING_INTERVAL = 15000L;
    public static final long WARMUP_GET_READY_TIMEOUT = 15000L;

    private final ShopService shopService;
    private final ImportDirectorService importDirectorService;
    private final ReindexService reindexService;
    private final RemoteDevService remoteDevService;
    private final SystemService systemService;
    private final RuntimeAttributeService runtimeAttributeService;

    private final AuthenticationManager authenticationManager;

    private boolean pauseInitialised = false;

    public LocalFileShareImportListenerImpl(final ShopService shopService,
                                            final ImportDirectorService importDirectorService,
                                            final ReindexService reindexService,
                                            final RemoteDevService remoteDevService,
                                            final SystemService systemService,
                                            final RuntimeAttributeService runtimeAttributeService,
                                            final AuthenticationManager authenticationManager) {
        this.shopService = shopService;
        this.remoteDevService = remoteDevService;
        this.systemService = systemService;
        this.runtimeAttributeService = runtimeAttributeService;
        this.importDirectorService = importDirectorService;
        this.reindexService = reindexService;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public void run() {

        if (!pauseInitialised) {
            if (!systemService.getAttributeValues().keySet().contains(PAUSE_PREF)) {
                synchronized (SystemService.class) {
                    runtimeAttributeService.create(PAUSE_PREF, "SYSTEM", "Boolean");
                    systemService.updateAttributeValue(PAUSE_PREF, Boolean.TRUE.toString());
                }
            }
            pauseInitialised = true;
        }

        final String paused = systemService.getAttributeValue(PAUSE_PREF);
        if (Boolean.valueOf(paused)) {
            return;
        }

        final Logger defaultLog = ShopCodeContext.getLog(this);

        final long start = System.currentTimeMillis();

        defaultLog.info("Local file share listener");

        final String fsRoot = systemService.getAttributeValue(FS_PREF);
        if (StringUtils.isBlank(fsRoot)) {
            defaultLog.error("{} is not set... terminating", FS_PREF);
            return;
        }

        final File root = new File(fsRoot);
        final boolean exists = root.exists();
        final boolean readableDir = exists && root.canRead() && root.isDirectory();
        if (!readableDir) {
            defaultLog.error("{} location '{}' is invalid (exists: {}, readable: {})  ... terminating",
                    new Object[]{FS_PREF, root.getAbsolutePath(), exists ? "yes" : "no", "no"});
            return;
        }

        defaultLog.info("{} location '{}'... ",
                new Object[]{FS_PREF, root.getAbsolutePath()});

        runRootScan(defaultLog, root);

        final long finish = System.currentTimeMillis();

        final long ms = (finish - start);

        defaultLog.info("Local file share listener ... completed in {}s", (ms > 0 ? ms / 1000 : 0));

    }

    private void runRootScan(final Logger log, final File root) {

        final File[] shopDirs = root.listFiles();

        if (shopDirs != null) {

            SecurityContextHolder.getContext().setAuthentication(global);

            final String importDirPath = importDirectorService.getImportDirectory();
            log.info("Detected import directory root {}", importDirPath);

            final List<Map<String, String>> importGroupsMap = importDirectorService.getImportGroups("en");
            final Set<String> importGroupNames = new HashSet<String>();
            for (final Map<String, String> group : importGroupsMap) {
                importGroupNames.add(group.get("name"));
            }
            log.info("Detected import groups: {}", StringUtils.join(importGroupNames, ","));


            try {

                for (final File shopDir : shopDirs) {

                    runShopRootScan(log, shopDir, importDirPath, importGroupNames);

                }

            } catch (Exception exp) {

                log.error("Encountered fatal error: " + exp.getMessage() + "... stopping scheduled task", exp);

            } finally {

                SecurityContextHolder.clearContext();

            }

        }
    }

    private void runShopRootScan(final Logger defaultLog, final File shopDir, final String importDirPath, final Set<String> importGroupNames) {

        final Shop shop = shopService.getShopByCode(shopDir.getName());
        if (shop != null) {

            ShopCodeContext.setShopCode(shop.getCode());
            ShopCodeContext.setShopId(shop.getShopId());

            final Logger shopLog = ShopCodeContext.getLog(this);

            try {

                defaultLog.info("Scanning processed directory for shop {}", shop.getCode());
                shopLog.info("Scanning processed directory");

                final File config = ensureDirectoryExists(defaultLog, shopLog, shopDir, shop, "config");
                final File configProps = new File(config, "config.properties");
                if (!configProps.exists()) {
                    defaultLog.info("Configuration file is missing for shop {} ... skipping", shop.getCode());
                    shopLog.info("Configuration file is missing ... skipping");
                    return;
                }

                final Map<Pattern, Map<String, String>> patternGroupMap = loadShopAutoImportConfigurations(shopLog, configProps, importGroupNames);

                final File processed = ensureDirectoryExists(defaultLog, shopLog, shopDir, shop, "processed");

                final File[] readyForImport = processed.listFiles();
                if (readyForImport == null || readyForImport.length == 0) {
                    defaultLog.info("No new files to import for shop {}", shop.getCode());
                    shopLog.info("No new files to import for shop {}", shop.getCode());
                    return;
                }

                final SimpleDateFormat format = new SimpleDateFormat("_yyyy-MMM-dd-hh-mm-ss-SSS");

                for (final File toImport : prioritiseProcessedFiles(readyForImport)) {

                    final String timestamp = format.format(new Date());

                    final File targetDirectory = new File(importDirPath + File.separator + PRINCIPAL + timestamp);
                    targetDirectory.mkdirs();

                    defaultLog.info("Moving files to '{}' for shop {}", targetDirectory.getAbsolutePath(), shop.getCode());
                    shopLog.info("Moving files to '{}' for shop {}", targetDirectory.getAbsolutePath(), shop.getCode());

                    Map<String, String> groupData = null;
                    for (final Map.Entry<Pattern, Map<String, String>> group : patternGroupMap.entrySet()) {
                        if (group.getKey().matcher(toImport.getName()).matches()) {
                            groupData = group.getValue();
                            break;
                        }
                    }

                    if (groupData == null) {
                        defaultLog.warn("Importing '{}' for shop {} ... skipping (no valid import group)", toImport.getAbsolutePath(), shop.getCode());
                        shopLog.warn("Importing '{}' for shop {} ... skipping (no valid import group)", toImport.getAbsolutePath(), shop.getCode());
                        continue;
                    }

                    final String groupName = groupData.get("group");

                    defaultLog.info("Importing '{}' for shop {} using group {}", new Object[] { toImport.getAbsolutePath(), shop.getCode(), groupName });
                    shopLog.info("Importing '{}' for shop {} using group {}", new Object[] { toImport.getAbsolutePath(), shop.getCode(), groupName });

                    final String destination = moveFileToImportDirectory(toImport, targetDirectory);


                    try {

                        final String user = groupData.get("user");
                        final String pass = groupData.get("pass");

                        final Authentication shopAuth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user, pass));
                        if (shopAuth.isAuthenticated()) {
                            SecurityContextHolder.getContext().setAuthentication(new RunAsUserAuthentication(user, pass, shopAuth.getAuthorities()));

                            // Make this synchronous since we are already in async process
                            final String importToken = importDirectorService.doImport(groupName, destination, false);
                            final JobStatus importStatus = importDirectorService.getImportStatus(importToken);

                            defaultLog.info("Importing '{}' for shop {} using group {} ... completed [{}]", new Object[] { toImport.getAbsolutePath(), shop.getCode(), groupName, importStatus.getCompletion() });
                            shopLog.info("Importing '{}' for shop {} using group {} ... completed [{}]", new Object[]{toImport.getAbsolutePath(), shop.getCode(), groupName, importStatus.getCompletion()});

                            remoteDevService.evictAllCache();

                            if (importStatus.getCompletion() == JobStatus.Completion.OK) {

                                final boolean reindex = Boolean.valueOf(groupData.get("reindex"));
                                if (reindex) {
                                    
                                    defaultLog.info("Re-indexed products for shop {} using group {} ... starting", new Object[] { shop.getCode(), groupName });
                                    shopLog.info("Re-indexed products for shop {} using group {} ... starting", new Object[] { shop.getCode(), groupName });

                                    Thread.sleep(INDEX_GET_READY_TIMEOUT); // let cache invalidation run before index
                                    final String indexToken = reindexService.reindexShopProducts(ShopCodeContext.getShopId());
                                    while (true) {
                                        Thread.sleep(INDEX_PING_INTERVAL);
                                        JobStatus reindexStatus = reindexService.getIndexAllStatus(indexToken);
                                        if (reindexStatus.getState() == JobStatus.State.FINISHED) {

                                            defaultLog.info("Re-indexed products for shop {} using group {} ... completed [{}]", new Object[] { shop.getCode(), groupName, reindexStatus.getCompletion() });
                                            shopLog.info("Re-indexed products for shop {} using group {} ... completed [{}]", new Object[] { shop.getCode(), groupName, reindexStatus.getCompletion() });

                                            remoteDevService.evictAllCache();
                                            Thread.sleep(WARMUP_GET_READY_TIMEOUT);
                                            remoteDevService.warmUp();

                                            break;
                                        }
                                    }
                                }
                            }



                        } else {
                            defaultLog.warn("Invalid credentials for '{}' for shop {} using group {}", new Object[]{user, shop.getCode(), groupName});
                            shopLog.warn("Invalid credentials for '{}' for shop {} using group {}", new Object[]{user, shop.getCode(), groupName});
                        }

                    } finally {
                        // Reinstate global context of AutoImport
                        SecurityContextHolder.getContext().setAuthentication(global);
                    }


                }
            } catch (Exception exp) {
                defaultLog.error("Failed import configuration " + shop.getCode(), exp);
                shopLog.error("Failed import configuration " + shop.getCode(), exp);
            } finally {
                ShopCodeContext.clear();
            }
        }

    }

    private Map<Pattern, Map<String, String>> loadShopAutoImportConfigurations(final Logger shopLog, final File configProps, final Set<String> importGroupNames) throws IOException {

        final Properties configuration = new Properties();
        configuration.load(new FileInputStream(configProps));

        final Map<Pattern, Map<String, String>> patternGroupMap = new HashMap<Pattern, Map<String, String>>();

        for (int i = 0; true; i++) {

            final String cfgGroup = configuration.getProperty("config." + i + ".group");
            if (StringUtils.isBlank(cfgGroup)) {
                break;  // finished as there are not more configs (by convention)
            } else if (!importGroupNames.contains(cfgGroup)) {
                shopLog.warn("Configuration {} is not a valid import group ... skipping", cfgGroup);
                continue;
            }

            final String cfgRegex = configuration.getProperty("config." + i + ".regex");
            if (StringUtils.isBlank(cfgRegex)) {
                shopLog.warn("Configuration {} has no regex ... skipping", cfgGroup);
                continue;
            }

            final String cfgUser = configuration.getProperty("config." + i + ".user");
            if (StringUtils.isBlank(cfgUser)) {
                shopLog.warn("Configuration {} has no user ... skipping", cfgGroup);
                continue;
            }

            final String cfgPass = configuration.getProperty("config." + i + ".pass");
            if (StringUtils.isBlank(cfgPass)) {
                shopLog.warn("Configuration {} has no password ... skipping", cfgGroup);
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

                shopLog.info("Configuration {} has regex {}", cfgGroup, cfgRegex);

            } catch (Exception exp) {
                shopLog.warn("Configuration {} has INVALID regex {} ... skipping", cfgGroup, cfgRegex);
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
     * @param defaultLog current log
     * @param shopDir root of shop import directory structure
     * @param shop shop instance
     * @param dirname name of directory under shop dir
     *
     * @return directory
     */
    protected File ensureDirectoryExists(final Logger defaultLog, final Logger shopLog, final File shopDir, final Shop shop, final String dirname) throws IOException {

        final File dir = new File(shopDir, dirname);
        if (!dir.exists()) {
            defaultLog.info("Proactively creating '{}' sub directory for shop {}", dir.getAbsolutePath(), shop.getCode());
            shopLog.info("Proactively creating '{}' sub directory for shop {}", dir.getAbsolutePath(), shop.getCode());
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
}
