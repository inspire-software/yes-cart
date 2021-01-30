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
import org.yes.cart.bulkjob.cron.AbstractCronJobProcessorImpl;
import org.yes.cart.constants.Constants;
import org.yes.cart.domain.entity.Job;
import org.yes.cart.domain.entity.JobDefinition;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.async.JobStatusAware;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.async.impl.JobStatusListenerImpl;
import org.yes.cart.service.async.impl.JobStatusListenerWithLoggerImpl;
import org.yes.cart.service.async.model.JobStatus;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.service.domain.SystemService;
import org.yes.cart.stream.io.IOItem;
import org.yes.cart.stream.io.IOProvider;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * User: denispavlov
 * Date: 05/06/2018
 * Time: 21:00
 */
public class LocalFileShareProductImageVaultCleanupProcessorImpl extends AbstractCronJobProcessorImpl
        implements JobStatusAware {

    private static final Logger LOG = LoggerFactory.getLogger(LocalFileShareProductImageVaultCleanupProcessorImpl.class);

    private static final String ELIGIBLE_IMG_COUNTER = "Eligible image codes";
    private static final String REMOVED_IMG_COUNTER = "Removed image codes";

    private SystemService systemService;
    private IOProvider ioProvider;
    private ProductService productService;

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

        listener.notifyInfo("Cleaning product imagevault {}", imgVault);

        if (!ioProvider.exists(imgVault, Collections.emptyMap())) {
            listener.notifyInfo("Scanning imagevault '{}' -> '{}' failed because either this is not a local file system path or directory does not exist",
                    imgVault, ioProvider.nativePath(imgVault, Collections.emptyMap()));
            listener.notifyCompleted();
            return new Pair<>(listener.getLatestStatus(), null);
        }

        final String productImageVault = ioProvider.path(imgVault, "product", Collections.emptyMap());

        if (!ioProvider.exists(productImageVault, Collections.emptyMap())) {
            listener.notifyInfo("Cleaning product imagevault '{}' -> '{}' failed because either this is not a local file system path or directory does not exist",
                    productImageVault, ioProvider.nativePath(productImageVault, Collections.emptyMap()));
            listener.notifyCompleted();
            return new Pair<>(listener.getLatestStatus(), null);
        }

        final Properties config = readContextAsProperties(context, job, definition);

        final boolean delete = "DELETE".equalsIgnoreCase(config.getProperty("clean-mode"));

        scanRoot(productImageVault, delete);

        listener.notifyCompleted();

        return new Pair<>(listener.getLatestStatus(), null);

    }

    private int scanRoot(final String root, final boolean delete) {

        listener.notifyInfo("Cleaning product directory '{}' -> '{}' in mode {}",
                root, ioProvider.nativePath(root, Collections.emptyMap()), delete ? "DELETE" : "SCAN");

        final List<IOItem> letters = ioProvider.list(root, Collections.emptyMap());
        if (CollectionUtils.isEmpty(letters)) {
            return 0;
        }

        int count = 0;
        int lettersCount = 0;
        int totalLetters = letters.size();

        for (final IOItem letter : letters) {

            int letterProgress = Math.round(lettersCount++ * 100/totalLetters);

            // Only look at single letter directories at top level
            if (letter.getName().length() == 1) {

                final String letterPath = ioProvider.path(letter.getPath(), letter.getName(), Collections.emptyMap());

                listener.notifyPing("Cleaning product imagevault directory {}% {}", letterProgress, letter);

                final List<IOItem> codes = ioProvider.list(letterPath, Collections.emptyMap());
                if (CollectionUtils.isEmpty(codes)) {
                    continue;
                }

                for (final IOItem code : codes) {

                    if (Constants.NO_IMAGE.equals(code.getName())) {
                        continue; // Must not remove noimage directory
                    }

                    final Long productId = productService.findProductIdByCode(code.getName());
                    if (productId == null) {
                        final Long skuId = productService.findProductSkuIdByCode(code.getName());
                        if (skuId == null) {

                            listener.notifyInfo("Directory '{}' is eligible for removal because there is no product or SKU with this code", code);
                            listener.count(ELIGIBLE_IMG_COUNTER);

                            if (delete) {

                                final String codePath = ioProvider.path(code.getPath(), code.getName(), Collections.emptyMap());

                                try {
                                    ioProvider.delete(codePath, Collections.emptyMap());
                                    listener.notifyInfo("Removed directory {}", code);
                                    listener.count(REMOVED_IMG_COUNTER);
                                    count++;
                                } catch (Exception exp) {
                                    listener.notifyError("Unable to delete directory {}", code);
                                }

                            }

                        }

                    }

                }

            }

        }

        if (count > 0) {
            listener.notifyInfo("Cleaning product imagevault directory 100% {} ... found {} orphan codes", root, count);
        } else {
            listener.notifyInfo("Cleaning product imagevault directory 100% {} ... no orphan codes found", root);
        }

        return count;
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
     * @param productService service
     */
    public void setProductService(final ProductService productService) {
        this.productService = productService;
    }
}
