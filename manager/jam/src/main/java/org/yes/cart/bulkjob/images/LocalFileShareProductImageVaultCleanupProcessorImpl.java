/*
 * Copyright (C) Denys Pavlov, Igor Azarny - All Rights Reserved
 * Unauthorized copying, modification or redistribution of this file
 * via any medium is strictly prohibited without explicit written permission.
 * Proprietary and confidential.
 */

package org.yes.cart.bulkjob.images;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.constants.Constants;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.service.domain.SystemService;
import org.yes.cart.stream.io.FileSystemIOProvider;

import java.io.File;
import java.util.Collections;

/**
 * User: denispavlov
 * Date: 05/06/2018
 * Time: 21:00
 */
public class LocalFileShareProductImageVaultCleanupProcessorImpl implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(LocalFileShareProductImageVaultCleanupProcessorImpl.class);

    private final SystemService systemService;
    private final FileSystemIOProvider ioProvider;
    private final ProductService productService;

    private String processingModeAttribute = "SCAN";

    public LocalFileShareProductImageVaultCleanupProcessorImpl(final SystemService systemService,
                                                               final FileSystemIOProvider ioProvider,
                                                               final ProductService productService) {
        this.systemService = systemService;
        this.ioProvider = ioProvider;
        this.productService = productService;
    }

    @Override
    public void run() {

        final String imgVault = systemService.getImageRepositoryDirectory();

        LOG.info("Cleaning product imagevault {}", imgVault);

        final File imageVault = ioProvider.resolveFileFromUri(imgVault, Collections.EMPTY_MAP);
        if (imageVault == null || !imageVault.exists()) {
            LOG.info("Cleaning product imagevault {} failed because either this is not a local file system path or directory does not exist", imageVault);
            return;
        }

        final File productImageVault = new File(imageVault, "product");

        if (!productImageVault.exists()) {
            LOG.info("Cleaning product imagevault {} failed because either this is not a local file system path or directory does not exist", productImageVault);
            return;
        }

        final int removed = scanRoot(productImageVault);

        LOG.info("Cleaning product imagevault ... completed, removed {}", removed);

    }

    private int scanRoot(final File dir) {

        final boolean delete = "DELETE".equals(systemService.getAttributeValue(this.processingModeAttribute));

        LOG.info("Cleaning product directory {} in mode {}", dir.getAbsolutePath(), delete ? "DELETE" : "SCAN");

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

                LOG.info("Cleaning product imagevault directory {}% {}", letterProgress, letter.getAbsolutePath());

                final File[] codes = letter.listFiles();
                if (codes == null) {
                    continue;
                }

                for (final File code : codes) {

                    if (Constants.NO_IMAGE.equals(code.getName())) {
                        continue; // Must not remove noimage directory
                    }

                    final Long productId = productService.findProductIdByCode(code.getName());
                    if (productId == null) {
                        final Long skuId = productService.findProductSkuIdByCode(code.getName());
                        if (skuId == null) {

                            LOG.info("Directory {} is eligible for removal because there is no product or SKU with this code", code.getAbsolutePath());

                            if (delete) {

                                final File[] images = code.listFiles();
                                if (images == null) {
                                    continue;
                                }

                                boolean hasFailures = false;
                                for (final File image : images) {

                                    if (!image.delete()) {
                                        LOG.error("Unable to delete file {}", image.getAbsolutePath());
                                        hasFailures = true;
                                    } else {
                                        LOG.info("Removed file {}", image.getAbsolutePath());
                                        count++;
                                    }
                                }

                                if (!hasFailures) {

                                    if (code.delete()) {
                                        LOG.error("Unable to delete directory {}", code.getAbsolutePath());
                                    } else {
                                        LOG.info("Removed directory {}", code.getAbsolutePath());
                                    }

                                }

                            }

                        }
                    }

                }

            }

        }

        if (count > 0) {
            LOG.info("Cleaning product imagevault directory 100% {} ... found {} orphan images", dir.getAbsolutePath(), count);
        } else {
            LOG.info("Cleaning product imagevault directory 100% {} ... no orphan images found", dir.getAbsolutePath());
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
