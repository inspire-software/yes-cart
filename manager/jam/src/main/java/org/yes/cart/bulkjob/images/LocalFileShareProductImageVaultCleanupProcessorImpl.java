/*
 * Copyright (C) Denys Pavlov, Igor Azarny - All Rights Reserved
 * Unauthorized copying, modification or redistribution of this file
 * via any medium is strictly prohibited without explicit written permission.
 * Proprietary and confidential.
 */

package org.yes.cart.bulkjob.images;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.constants.Constants;
import org.yes.cart.service.async.JobStatusAware;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.async.impl.JobStatusListenerLoggerWrapperImpl;
import org.yes.cart.service.async.model.JobStatus;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.service.domain.SystemService;
import org.yes.cart.stream.io.IOItem;
import org.yes.cart.stream.io.IOProvider;

import java.util.Collections;
import java.util.List;

/**
 * User: denispavlov
 * Date: 05/06/2018
 * Time: 21:00
 */
public class LocalFileShareProductImageVaultCleanupProcessorImpl implements Runnable, JobStatusAware {

    private static final Logger LOG = LoggerFactory.getLogger(LocalFileShareProductImageVaultCleanupProcessorImpl.class);

    private static final String ELIGIBLE_IMG_COUNTER = "Eligible image codes";
    private static final String REMOVED_IMG_COUNTER = "Removed image codes";

    private final SystemService systemService;
    private final IOProvider ioProvider;
    private final ProductService productService;

    private String processingModeAttribute = "SCAN";

    private final JobStatusListener listener = new JobStatusListenerLoggerWrapperImpl(LOG, "Image vault clean up", true);

    public LocalFileShareProductImageVaultCleanupProcessorImpl(final SystemService systemService,
                                                               final IOProvider ioProvider,
                                                               final ProductService productService) {
        this.systemService = systemService;
        this.ioProvider = ioProvider;
        this.productService = productService;
    }

    /** {@inheritDoc} */
    @Override
    public JobStatus getStatus(final String token) {
        return listener.getLatestStatus();
    }

    @Override
    public void run() {

        listener.reset();

        final String imgVault = systemService.getImageRepositoryDirectory();

        LOG.info("Cleaning product imagevault {}", imgVault);

        if (!ioProvider.exists(imgVault, Collections.emptyMap())) {
            LOG.info("Scanning imagevault '{}' -> '{}' failed because either this is not a local file system path or directory does not exist",
                    imgVault, ioProvider.nativePath(imgVault, Collections.emptyMap()));
            return;
        }

        final String productImageVault = ioProvider.path(imgVault, "product", Collections.emptyMap());

        if (!ioProvider.exists(productImageVault, Collections.emptyMap())) {
            LOG.info("Cleaning product imagevault '{}' -> '{}' failed because either this is not a local file system path or directory does not exist",
                    productImageVault, ioProvider.nativePath(productImageVault, Collections.emptyMap()));
            return;
        }

        scanRoot(productImageVault);

        listener.notifyCompleted();

    }

    private int scanRoot(final String root) {

        final boolean delete = "DELETE".equals(systemService.getAttributeValue(this.processingModeAttribute));

        LOG.info("Cleaning product directory '{}' -> '{}' in mode {}",
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
                listener.notifyMessage("Cleaning product imagevault directory {}% {}", letterProgress, letter);

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

                            LOG.info("Directory '{}' is eligible for removal because there is no product or SKU with this code", code);
                            listener.count(ELIGIBLE_IMG_COUNTER);

                            if (delete) {

                                final String codePath = ioProvider.path(code.getPath(), code.getName(), Collections.emptyMap());

                                try {
                                    ioProvider.delete(codePath, Collections.emptyMap());
                                    LOG.info("Removed directory {}", code);
                                    listener.count(REMOVED_IMG_COUNTER);
                                    count++;
                                } catch (Exception exp) {
                                    LOG.error("Unable to delete directory {}", code);
                                }

                            }

                        }

                    }

                }

            }

        }

        if (count > 0) {
            LOG.info("Cleaning product imagevault directory 100% {} ... found {} orphan codes", root, count);
        } else {
            LOG.info("Cleaning product imagevault directory 100% {} ... no orphan codes found", root);
        }

        return count;
    }

    /**
     * Spring IoC.
     *
     * @param processingModeAttribute attribute that contains processing mode
     */
    public void setProcessingModeAttribute(final String processingModeAttribute) {
        this.processingModeAttribute = processingModeAttribute;
    }
}
