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

package org.yes.cart.service.media.impl;

import org.springframework.cache.annotation.Cacheable;
import org.yes.cart.service.media.MediaFileNameStrategy;

/**
 * User: denispavlov
 * Date: 28/01/2017
 * Time: 19:12
 */
public class MediaFileNameStrategyCachedImpl implements MediaFileNameStrategy {

    private final MediaFileNameStrategy mediaFileNameStrategy;

    public MediaFileNameStrategyCachedImpl(final MediaFileNameStrategy mediaFileNameStrategy) {
        this.mediaFileNameStrategy = mediaFileNameStrategy;
    }

    /** {@inheritDoc} */
    @Override
    public String getUrlPath() {
        return mediaFileNameStrategy.getUrlPath();
    }

    /** {@inheritDoc} */
    @Override
    public String getRelativeInternalRootDirectory() {
        return mediaFileNameStrategy.getRelativeInternalRootDirectory();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable(value = "imageNameStrategy-resolveObjectCode")
    public String resolveObjectCode(final String url) {
        return mediaFileNameStrategy.resolveObjectCode(url);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable(value = "imageNameStrategy-resolveLocale")
    public String resolveLocale(final String url) {
        return mediaFileNameStrategy.resolveLocale(url);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable(value = "imageNameStrategy-resolveSuffix")
    public String resolveSuffix(final String url) {
        return mediaFileNameStrategy.resolveSuffix(url);
    }

    /** {@inheritDoc} */
    @Override
    public String resolveFileName(final String url) {
        return mediaFileNameStrategy.resolveFileName(url);
    }

    /** {@inheritDoc} */
    @Override
    public String resolveRelativeInternalFileNamePath(final String fileName, final String code, final String locale) {
        return mediaFileNameStrategy.resolveRelativeInternalFileNamePath(fileName, code, locale);
    }

    /** {@inheritDoc} */
    @Override
    public String resolveRelativeInternalFileNamePath(final String fileName, final String code, final String locale, final String width, final String height) {
        return mediaFileNameStrategy.resolveRelativeInternalFileNamePath(fileName, code, locale, width, height);
    }

    /** {@inheritDoc} */
    @Override
    public String createRollingFileName(final String fileName, final String code, final String suffix, final String locale) {
        return mediaFileNameStrategy.createRollingFileName(fileName, code, suffix, locale);
    }
}
