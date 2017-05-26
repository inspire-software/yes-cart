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

package org.yes.cart.bulkjob.images;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.yes.cart.bulkimport.image.ImageImportDomainObjectStrategy;
import org.yes.cart.bulkjob.impl.BulkJobAutoContextImpl;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.async.impl.JobStatusListenerLoggerWrapperImpl;
import org.yes.cart.service.async.model.AsyncContext;
import org.yes.cart.service.async.model.JobStatus;
import org.yes.cart.service.async.utils.RunAsUserAuthentication;
import org.yes.cart.service.cluster.ClusterService;
import org.yes.cart.service.cluster.ReindexService;
import org.yes.cart.service.domain.RuntimeAttributeService;
import org.yes.cart.service.domain.SystemService;
import org.yes.cart.service.media.MediaFileNameStrategy;
import org.yes.cart.stream.io.FileSystemIOProvider;
import org.yes.cart.web.service.ws.client.AsyncContextFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

/**
 * User: denispavlov
 * Date: 10/11/2015
 * Time: 14:36
 */
public class LocalFileShareImageVaultProcessorImpl implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(LocalFileShareImageVaultProcessorImpl.class);

    public static final long INDEX_GET_READY_TIMEOUT = 5000L;
    public static final long INDEX_PING_INTERVAL = 15000L;
    public static final long WARMUP_GET_READY_TIMEOUT = 15000L;

    private final SystemService systemService;
    private final FileSystemIOProvider ioProvider;
    private final MediaFileNameStrategy[] imageNameStrategies;
    private final ImageImportDomainObjectStrategy[] imageImportStrategies;
    private final ReindexService reindexService;
    private final ClusterService clusterService;
    private final AsyncContextFactory asyncContextFactory;
    private final RuntimeAttributeService runtimeAttributeService;

    private final AuthenticationManager authenticationManager;

    private boolean pauseInitialised = false;

    public LocalFileShareImageVaultProcessorImpl(final SystemService systemService,
                                                 final FileSystemIOProvider ioProvider,
                                                 final MediaFileNameStrategy[] imageNameStrategies,
                                                 final ImageImportDomainObjectStrategy[] imageImportStrategies,
                                                 final ReindexService reindexService,
                                                 final ClusterService clusterService,
                                                 final AsyncContextFactory asyncContextFactory,
                                                 final RuntimeAttributeService runtimeAttributeService,
                                                 final AuthenticationManager authenticationManager) {
        this.systemService = systemService;
        this.ioProvider = ioProvider;
        this.imageNameStrategies = imageNameStrategies;
        this.imageImportStrategies = imageImportStrategies;
        this.reindexService = reindexService;
        this.clusterService = clusterService;
        this.asyncContextFactory = asyncContextFactory;
        this.runtimeAttributeService = runtimeAttributeService;
        this.authenticationManager = authenticationManager;
    }

    public void run() {

        final String imgVault = systemService.getImageRepositoryDirectory();

        LOG.info("Scanning imagevault {}", imgVault);

        final File imageVault = ioProvider.resolveFileFromUri(imgVault, Collections.EMPTY_MAP);
        if (imageVault == null || !imageVault.exists()) {
            LOG.info("Scanning imagevault {} failed because either this is not a local file system path or directory does not exist", imgVault);
            return;
        }

        final File configProps = new File(imageVault, "config.properties");
        if (!configProps.exists()) {
            LOG.info("Configuration file is missing... skipping");
            return;
        }

        final Properties configuration = new Properties();
        try {
            configuration.load(new FileInputStream(configProps));
        } catch (IOException e) {
            LOG.info("Configuration file is corrupt... skipping", e);
            return;
        }

        try {

            final String user = configuration.getProperty("config.user");
            final String pass = configuration.getProperty("config.pass");

            final Authentication shopAuth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user, pass));
            if (shopAuth.isAuthenticated()) {
                SecurityContextHolder.getContext().setAuthentication(new RunAsUserAuthentication(user, pass, shopAuth.getAuthorities()));

                final int reattached = scanRoot(imageVault);

                final boolean reindex = Boolean.valueOf(configuration.getProperty("config.reindex"));

                if (reindex && reattached > 0) {

                    try {
                        final AsyncContext cacheCtx = createCtx(AttributeNamesKeys.System.SYSTEM_BACKDOOR_TIMEOUT_MS);
                        clusterService.evictAllCache(cacheCtx);

                        if (reindex) {
                            final AsyncContext reindexCtx = createCtx(AttributeNamesKeys.System.SYSTEM_BACKDOOR_PRODUCT_BULK_INDEX_TIMEOUT_MS);
                            Thread.sleep(INDEX_GET_READY_TIMEOUT); // let cache invalidation run before index
                            final String indexToken = reindexService.reindexAllProducts(reindexCtx);
                            while (true) {
                                Thread.sleep(INDEX_PING_INTERVAL);
                                JobStatus reindexStatus = reindexService.getIndexJobStatus(reindexCtx, indexToken);
                                if (reindexStatus.getState() == JobStatus.State.FINISHED) {

                                    LOG.info("Re-indexed products ... completed [{}]", new Object[]{ reindexStatus.getCompletion()});

                                    clusterService.evictAllCache(cacheCtx);
                                    Thread.sleep(WARMUP_GET_READY_TIMEOUT);
                                    clusterService.warmUp(cacheCtx);
                                    break;
                                }
                            }
                        }
                    } catch (Exception exp) {

                    }

                }

            } else {
                LOG.warn("Invalid credentials for image vault scanner");
            }

        } finally {
            // Clear auth
            SecurityContextHolder.clearContext();
        }

        LOG.info("Scanning imagevault ... completed");

    }

    private int scanRoot(final File imageVault) {

        final Set<String> scanned = new HashSet<String>();
        int count = 0;

        for (final MediaFileNameStrategy strategy : imageNameStrategies) {

            if (scanned.contains(strategy.getRelativeInternalRootDirectory())) {
                continue; // skip duplicates (e.g. product + sku)
            }

            scanned.add(strategy.getRelativeInternalRootDirectory());

            final File dir = new File(imageVault.getAbsolutePath() + File.separator + strategy.getRelativeInternalRootDirectory());

            if (dir.exists()) {

                count += scanRootDirectory(dir, strategy);

            }

        }
        return count;
    }

    private int scanRootDirectory(final File dir, final MediaFileNameStrategy strategy) {

        final JobStatusListener statusListener = new JobStatusListenerLoggerWrapperImpl(LOG);

        LOG.info("Scanning imagevault directory {}", dir.getAbsolutePath());

        final File[] letters = dir.listFiles();
        if (letters == null) {
            return 0;
        }

        int count = 0;
        int lettersCount = 0;
        int totalLetters = letters.length;

        for (final File letter : letters) {

            int letterProgress = Math.round(lettersCount++ * 100/totalLetters);

            // Only look at single letter directories at top level
            if (letter.getName().length() == 1) {

                LOG.info("Scanning imagevault directory {}% {}", letterProgress, letter.getAbsolutePath());

                final File[] codes = letter.listFiles();
                if (codes == null) {
                    continue;
                }

                for (final File code : codes) {

                    LOG.info("Scanning imagevault directory {}% {}", letterProgress, code.getAbsolutePath());

                    final File[] images = code.listFiles();
                    if (images == null) {
                        continue;
                    }

                    for (final File image : images) {

                        LOG.info("Evaluating file {}% {}", letterProgress, image.getAbsolutePath());

                        final String fileName = image.getName();
                        final String objectCode = code.getName();
                        final String locale = strategy.resolveLocale(fileName);
                        final String suffix = strategy.resolveSuffix(fileName);

                        boolean success = false;
                        for (final ImageImportDomainObjectStrategy domainStrategy : imageImportStrategies) {
                            if (domainStrategy.supports(strategy.getUrlPath())) {
                                success |= domainStrategy.doImageImport(statusListener, fileName, objectCode, suffix, locale);
                            }
                        }

                        if (!success) {
                            LOG.debug("Skipped file {}", image.getAbsolutePath());
                        } else {
                            LOG.info("Reattached file {}", image.getAbsolutePath());
                            count++;
                        }
                    }

                }

            }

        }

        if (count > 0) {
            LOG.info("Scanning imagevault directory 100% {} ... all images were attached", dir.getAbsolutePath());
        } else {
            LOG.info("Scanning imagevault directory 100% {} ... reattached {} images", dir.getAbsolutePath(), count);
        }

        return count;
    }

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
