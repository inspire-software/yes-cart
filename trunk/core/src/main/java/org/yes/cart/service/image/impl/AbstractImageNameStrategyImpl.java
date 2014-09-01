/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
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

import org.apache.commons.lang.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.yes.cart.constants.Constants;
import org.yes.cart.service.image.ImageNameStrategy;

import java.io.File;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public abstract class AbstractImageNameStrategyImpl implements ImageNameStrategy {

    private final String urlPath;
    private final String relativeInternalRootDirectory;

    /**
     * Construct image name strategy
     *
     * @param urlPath URL path that identifies this strategy
     * @param relativeInternalRootDirectory  internal image relative path root directory without {@see File#separator}. E.g. "category"
     */
    protected AbstractImageNameStrategyImpl(final String urlPath,
                                            final String relativeInternalRootDirectory) {
        this.urlPath = urlPath;
        this.relativeInternalRootDirectory = relativeInternalRootDirectory + File.separator;
    }

    /** {@inheritDoc} */
    public String getUrlPath() {
        return urlPath;
    }

    /** {@inheritDoc} */
    public String getRelativeInternalRootDirectory() {
        return relativeInternalRootDirectory;
    }

    /**
     * Image strategy specific resolution of object if extraction from the url failed.
     *
     * @param url url
     *
     * @return object code (or null)
     */
    protected abstract String resolveObjectCodeInternal(final String url);

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "imageNameStrategy-resolveObjectCode")
    public String resolveObjectCode(final String url) {

        if (StringUtils.isNotBlank(url)) {

            if (url.indexOf('_') > -1 && StringUtils.countMatches(url, "_") > 1) {
                final String[] nameParts = url.split("_");
                final String candidate = nameParts[nameParts.length - 2];
                if (nameParts[nameParts.length - 1].indexOf('.') == 1) {
                    final char csuf = nameParts[nameParts.length - 1].charAt(0);
                    if (csuf >= 'a' && csuf <= 'g') {
                        return candidate;
                    }
                }
            }

            final String code = resolveObjectCodeInternal(url);
            if (code != null) {
                return code;
            }

        }

        return Constants.NO_IMAGE;
    }

    /** {@inheritDoc} */
    public String resolveFileName(final String url) {

        // We only use File class to reuse native filename resolution mechanism
        final File file = new File(url);
        String fileName = file.getName();
        int idx = fileName.indexOf('?');
        if (idx > -1) {
            fileName = fileName.substring(0, idx);
        }
        return fileName;
    }

    /** {@inheritDoc} */
    public String resolveRelativeInternalFileNamePath(final String fileName, final String code) {
        return resolveRelativeInternalFileNamePath(fileName, code, null, null);
    }


    /** {@inheritDoc} */
    public String resolveRelativeInternalFileNamePath(final String fileName, final String code, final String width, final String height) {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(relativeInternalRootDirectory);
        if (width != null) {
            stringBuilder.append(width);
            stringBuilder.append('x');
            stringBuilder.append(height);
            stringBuilder.append(File.separator);
        }
        if (code != null) {
            stringBuilder.append(code.charAt(0));
            stringBuilder.append(File.separator);
            stringBuilder.append(code);
            stringBuilder.append(File.separator);
        }

        stringBuilder.append(fileName);
        return stringBuilder.toString();
    }

}
