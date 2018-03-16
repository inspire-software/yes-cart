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

package org.yes.cart.service.domain.impl;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.dao.ResultsIteratorCallback;
import org.yes.cart.domain.entity.SeoImage;
import org.yes.cart.service.domain.ImageService;
import org.yes.cart.service.media.MediaFileNameStrategy;

import java.io.IOException;
import java.util.List;

/**
 * User: denispavlov
 * Date: 28/01/2017
 * Time: 16:49
 */
public class ImageServiceCachedImpl implements ImageService {

    private final ImageService imageService;

    public ImageServiceCachedImpl(final ImageService imageService) {
        this.imageService = imageService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] resizeImage(final String original,
                              final String resized,
                              final String width,
                              final String height) {
        return imageService.resizeImage(original, resized, width, height);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] resizeImage(final String original,
                              final byte[] content,
                              final String width,
                              final String height,
                              final boolean cropToFit) {
        return imageService.resizeImage(original, content, width, height, cropToFit);
    }

    @Override
    public byte[] resizeImage(final String original,
                              final byte[] content,
                              final String width,
                              final String height) {
        return imageService.resizeImage(original, content, width, height);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSizeAllowed(final String size) {
        return imageService.isSizeAllowed(size);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSizeAllowed(final String width, final String height) {
        return imageService.isSizeAllowed(width, height);
    }

    /** {@inheritDoc} */
    @Override
    public MediaFileNameStrategy getImageNameStrategy(final String url) {
        return imageService.getImageNameStrategy(url);
    }

    /** {@inheritDoc} */
    @Override
    public byte[] resizeImage(final String original, final String resized, final String width, final String height, final boolean cropToFit) {
        return imageService.resizeImage(original, resized, width, height, cropToFit);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isImageInRepository(final String fullFileName,
                                       final String code,
                                       final String storagePrefix,
                                       final String pathToRepository) {
        return imageService.isImageInRepository(fullFileName, code, storagePrefix, pathToRepository);
    }

    /** {@inheritDoc} */
    @Override
    public String addImageToRepository(final String fullFileName,
                                       final String code,
                                       final byte[] imgBody,
                                       final String storagePrefix,
                                       final String pathToRepository) throws IOException {
        return imageService.addImageToRepository(fullFileName, code, imgBody, storagePrefix, pathToRepository);
    }

    /** {@inheritDoc} */
    @Override
    public byte[] imageToByteArray(final String fileName, final String code, final String storagePrefix, final String pathToRepository) throws IOException {
        return imageService.imageToByteArray(fileName, code, storagePrefix, pathToRepository);
    }

    /** {@inheritDoc}*/
    @Override
    public boolean deleteImage(final String imageFileName, final String storagePrefix, final String pathToRepository) {
        return imageService.deleteImage(imageFileName, storagePrefix, pathToRepository);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable(value = "imageService-seoImage")
    public SeoImage getSeoImage(final String imageName) {
        return imageService.getSeoImage(imageName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CacheEvict(value = {
            "imageService-seoImage"
    }, allEntries = true)
    public SeoImage update(final SeoImage instance) {
        return imageService.update(instance);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<SeoImage> findAll() {
        return imageService.findAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void findAllIterator(final ResultsIteratorCallback<SeoImage> callback) {
        imageService.findAllIterator(callback);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SeoImage findById(final long pk) {
        return imageService.findById(pk);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SeoImage create(final SeoImage instance) {
        return imageService.create(instance);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CacheEvict(value = {
            "imageService-seoImage"
    }, allEntries = true)
    public void delete(final SeoImage instance) {
        imageService.delete(instance);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<SeoImage> findByCriteria(final String eCriteria, final Object... parameters) {
        return imageService.findByCriteria(eCriteria, parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int findCountByCriteria(final String eCriteria, final Object... parameters) {
        return imageService.findCountByCriteria(eCriteria, parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SeoImage findSingleByCriteria(final String eCriteria, final Object... parameters) {
        return imageService.findSingleByCriteria(eCriteria, parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GenericDAO<SeoImage, Long> getGenericDao() {
        return imageService.getGenericDao();
    }
}
