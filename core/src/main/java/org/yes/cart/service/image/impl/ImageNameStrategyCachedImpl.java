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

package org.yes.cart.service.image.impl;

import org.springframework.cache.annotation.Cacheable;
import org.yes.cart.service.image.ImageNameStrategy;

/**
 * User: denispavlov
 * Date: 28/01/2017
 * Time: 19:12
 */
public class ImageNameStrategyCachedImpl implements ImageNameStrategy {

    private final ImageNameStrategy imageNameStrategy;

    public ImageNameStrategyCachedImpl(final ImageNameStrategy imageNameStrategy) {
        this.imageNameStrategy = imageNameStrategy;
    }

    /** {@inheritDoc} */
    public String getUrlPath() {
        return imageNameStrategy.getUrlPath();
    }

    /** {@inheritDoc} */
    public String getRelativeInternalRootDirectory() {
        return imageNameStrategy.getRelativeInternalRootDirectory();
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "imageNameStrategy-resolveObjectCode")
    public String resolveObjectCode(final String url) {
        return imageNameStrategy.resolveObjectCode(url);
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "imageNameStrategy-resolveLocale")
    public String resolveLocale(final String url) {
        return imageNameStrategy.resolveLocale(url);
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "imageNameStrategy-resolveSuffix")
    public String resolveSuffix(final String url) {
        return imageNameStrategy.resolveSuffix(url);
    }

    /** {@inheritDoc} */
    public String resolveFileName(final String url) {
        return imageNameStrategy.resolveFileName(url);
    }

    /** {@inheritDoc} */
    public String resolveRelativeInternalFileNamePath(final String fileName, final String code, final String locale) {
        return imageNameStrategy.resolveRelativeInternalFileNamePath(fileName, code, locale);
    }

    /** {@inheritDoc} */
    public String resolveRelativeInternalFileNamePath(final String fileName, final String code, final String locale, final String width, final String height) {
        return imageNameStrategy.resolveRelativeInternalFileNamePath(fileName, code, locale, width, height);
    }

    /** {@inheritDoc} */
    public String createRollingFileName(final String fileName, final String code, final String suffix, final String locale) {
        return imageNameStrategy.createRollingFileName(fileName, code, suffix, locale);
    }
}
