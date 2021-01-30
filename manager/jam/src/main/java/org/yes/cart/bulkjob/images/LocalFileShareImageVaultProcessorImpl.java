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

package org.yes.cart.bulkjob.images;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.yes.cart.bulkimport.image.ImageImportDomainObjectStrategy;
import org.yes.cart.bulkjob.cron.AbstractCronJobProcessorImpl;
import org.yes.cart.bulkjob.impl.BulkJobAutoContextImpl;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.entity.Job;
import org.yes.cart.domain.entity.JobDefinition;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.async.AsyncContextFactory;
import org.yes.cart.service.async.JobStatusAware;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.async.impl.JobStatusListenerImpl;
import org.yes.cart.service.async.impl.JobStatusListenerWithLoggerImpl;
import org.yes.cart.service.async.model.AsyncContext;
import org.yes.cart.service.async.model.JobStatus;
import org.yes.cart.service.async.utils.RunAsUserAuthentication;
import org.yes.cart.service.cluster.ClusterService;
import org.yes.cart.service.cluster.ReindexService;
import org.yes.cart.service.domain.SystemService;
import org.yes.cart.service.media.MediaFileNameStrategy;
import org.yes.cart.stream.io.IOItem;
import org.yes.cart.stream.io.IOProvider;

import java.time.Instant;
import java.util.*;

/**
 * User: denispavlov
 * Date: 10/11/2015
 * Time: 14:36
 */
public class LocalFileShareImageVaultProcessorImpl extends AbstractCronJobProcessorImpl
        implements JobStatusAware {

    private static final Logger LOG = LoggerFactory.getLogger(LocalFileShareImageVaultProcessorImpl.class);

    private static final String SKIP_COUNTER = " skip";
    private static final String REATTACHED_COUNTER = " reattached";

    private static final long INDEX_GET_READY_TIMEOUT = 5000L;
    private static final long INDEX_PING_INTERVAL = 15000L;
    private static final long WARMUP_GET_READY_TIMEOUT = 15000L;

    private SystemService systemService;
    private IOProvider ioProvider;
    private MediaFileNameStrategy[] imageNameStrategies;
    private ImageImportDomainObjectStrategy[] imageImportStrategies;
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

        final String imgVault = systemService.getImageRepositoryDirectory();

        listener.notifyInfo("Scanning imagevault {}", imgVault);

        if (!ioProvider.exists(imgVault, Collections.emptyMap())) {
            listener.notifyInfo("Scanning imagevault '{}' -> '{}' failed because either this is not a local file system path or directory does not exist",
                    imgVault, ioProvider.nativePath(imgVault, Collections.emptyMap()));
            listener.notifyCompleted();
            return new Pair<>(listener.getLatestStatus(), null);
        }

        final Properties configuration = readContextAsProperties(context, job, definition);

        try {

            final String user = configuration.getProperty("config.user");
            final String pass = configuration.getProperty("config.pass");

            final Authentication shopAuth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user, pass));
            if (shopAuth.isAuthenticated()) {
                SecurityContextHolder.getContext().setAuthentication(new RunAsUserAuthentication(user, pass, shopAuth.getAuthorities()));

                final int reattached = scanRoot(imgVault);

                final boolean reindex = Boolean.valueOf(configuration.getProperty("config.reindex"));

                if (reindex && reattached > 0) {

                    try {
                        final AsyncContext cacheCtx = createCtx(AttributeNamesKeys.System.SYSTEM_CONNECTOR_TIMEOUT_MS);
                        clusterService.evictAllCache(cacheCtx);

                        final AsyncContext reindexCtx = createCtx(AttributeNamesKeys.System.SYSTEM_CONNECTOR_PRODUCT_BULK_INDEX_TIMEOUT_MS);
                        Thread.sleep(INDEX_GET_READY_TIMEOUT); // let cache invalidation run before index
                        final JobStatus indexToken = reindexService.reindexAllProducts(reindexCtx);
                        while (true) {
                            Thread.sleep(INDEX_PING_INTERVAL);
                            JobStatus reindexStatus = reindexService.getIndexJobStatus(reindexCtx, indexToken.getToken());
                            if (reindexStatus.getState() == JobStatus.State.FINISHED) {

                                listener.notifyInfo("Re-indexed products ... completed [{}]", reindexStatus.getCompletion());

                                clusterService.evictAllCache(cacheCtx);
                                Thread.sleep(WARMUP_GET_READY_TIMEOUT);
                                clusterService.warmUp(cacheCtx);
                                break;
                            }
                        }
                    } catch (Exception exp) {
                        listener.notifyError(exp.getMessage(), exp);
                    }

                }

            } else {
                listener.notifyWarning("Invalid credentials for image vault scanner");
            }

        } finally {
            // Clear auth
            SecurityContextHolder.clearContext();
        }

        listener.notifyCompleted();
        
        return new Pair<>(listener.getLatestStatus(), null);
    }

    private int scanRoot(final String imageVault) {

        final Set<String> scanned = new HashSet<>();
        int count = 0;

        for (final MediaFileNameStrategy strategy : imageNameStrategies) {

            if (scanned.contains(strategy.getRelativeInternalRootDirectory())) {
                continue; // skip duplicates (e.g. product + sku)
            }

            scanned.add(strategy.getRelativeInternalRootDirectory());

            final String subRoot = ioProvider.path(imageVault, strategy.getRelativeInternalRootDirectory(), Collections.emptyMap());

            if (ioProvider.exists(subRoot, Collections.emptyMap())) {

                count += scanSubRoot(subRoot, strategy);

            } else {
                listener.notifyInfo("Sub root '{}' -> '{}' is missing... skipping",
                        subRoot, ioProvider.nativePath(subRoot, Collections.emptyMap()));
            }

        }
        return count;
    }

    private int scanSubRoot(final String subRoot, final MediaFileNameStrategy strategy) {

        listener.notifyInfo("Scanning imagevault directory {}", subRoot);

        final List<IOItem> letters = ioProvider.list(subRoot, Collections.emptyMap());
        if (CollectionUtils.isEmpty(letters)) {
            return 0;
        }

        int lettersCount = 0;
        int totalLetters = letters.size();

        final String skipKey = strategy.getUrlPath() + SKIP_COUNTER;
        final String reattachKey = strategy.getUrlPath() + REATTACHED_COUNTER;

        for (final IOItem letter : letters) {

            int letterProgress = Math.round(lettersCount++ * 100/totalLetters);

            // Only look at single letter directories at top level
            if (letter.getName().length() == 1) {

                final String letterPath = ioProvider.path(letter.getPath(), letter.getName(), Collections.emptyMap());

                listener.notifyPing("Scanning imagevault directory {}% {}", letterProgress, letter);

                final List<IOItem> codes = ioProvider.list(letterPath, Collections.emptyMap());
                if (CollectionUtils.isEmpty(codes)) {
                    continue;
                }

                for (final IOItem code : codes) {

                    final String codePath = ioProvider.path(code.getPath(), code.getName(), Collections.emptyMap());

                    listener.notifyInfo("Scanning imagevault directory {}% {}", letterProgress, code);

                    final List<IOItem> images = ioProvider.list(codePath, Collections.emptyMap());
                    if (images == null) {
                        continue;
                    }

                    for (final IOItem image : images) {

                        listener.notifyInfo("Evaluating file {}% {}", letterProgress, image);

                        final String fileName = image.getName();
                        final String objectCode = code.getName();
                        final String locale = strategy.resolveLocale(fileName);
                        final String suffix = strategy.resolveSuffix(fileName);

                        boolean success = false;
                        for (final ImageImportDomainObjectStrategy domainStrategy : imageImportStrategies) {
                            if (domainStrategy.supports(strategy.getUrlPath())) {
                                success |= domainStrategy.doImageImport(listener, fileName, objectCode, suffix, locale);
                            }
                        }

                        if (!success) {
                            listener.count(skipKey);
                            listener.notifyInfo("Skipped file {}", image);
                        } else {
                            listener.count(reattachKey);
                            listener.notifyInfo("Reattached file {}", image);
                        }
                    }

                }

            }

        }

        return listener.getCount(reattachKey);
    }

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
     * @param systemService service
     */
    public void setSystemService(final SystemService systemService) {
        this.systemService = systemService;
    }

    /**
     * Spring IoC.
     *
     * @param ioProvider service
     */
    public void setIoProvider(final IOProvider ioProvider) {
        this.ioProvider = ioProvider;
    }

    /**
     * Spring IoC.
     *
     * @param imageNameStrategies service
     */
    public void setImageNameStrategies(final MediaFileNameStrategy[] imageNameStrategies) {
        this.imageNameStrategies = imageNameStrategies;
    }

    /**
     * Spring IoC.
     *
     * @param imageImportStrategies service
     */
    public void setImageImportStrategies(final ImageImportDomainObjectStrategy[] imageImportStrategies) {
        this.imageImportStrategies = imageImportStrategies;
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
