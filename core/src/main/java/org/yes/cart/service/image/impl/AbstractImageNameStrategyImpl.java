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
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.cache.annotation.Cacheable;
import org.yes.cart.constants.Constants;
import org.yes.cart.service.image.ImageNameStrategy;
import org.yes.cart.service.misc.LanguageService;

import java.io.File;
import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public abstract class AbstractImageNameStrategyImpl implements ImageNameStrategy {

    private final String urlPath;
    private final String relativeInternalRootDirectory;
    private final LanguageService languageService;

    /**
     * Construct image name strategy
     *
     * @param urlPath URL path that identifies this strategy
     * @param relativeInternalRootDirectory  internal image relative path root directory without {@see File#separator}. E.g. "category"
     * @param languageService language service
     */
    protected AbstractImageNameStrategyImpl(final String urlPath,
                                            final String relativeInternalRootDirectory,
                                            final LanguageService languageService) {
        this.urlPath = urlPath;
        this.languageService = languageService;
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

            final int extPos = url.lastIndexOf('.');
            if (extPos > -1) {

                final String urlNoExt = url.substring(0, extPos);

                if (urlNoExt.indexOf('_') > -1) {

                    final String locale = resolveLocale(url);
                    final String urlNoLocale;
                    if (locale != null) {
                        urlNoLocale = urlNoExt.substring(0, urlNoExt.length() - locale.length() - 1);
                    } else {
                        urlNoLocale = urlNoExt;
                    }

                    if (urlNoLocale.indexOf('_') > -1 && StringUtils.countMatches(urlNoLocale, "_") > 1) {
                        final String[] nameParts = urlNoLocale.split("_");
                        final String candidate = nameParts[nameParts.length - 2];
                        if (nameParts[nameParts.length - 1].length() == 1) {
                            final char csuf = nameParts[nameParts.length - 1].charAt(0);
                            if (csuf >= 'a' && csuf <= 'g') {
                                return candidate;
                            }
                        }
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

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "imageNameStrategy-resolveLocale")
    public String resolveLocale(final String url) {

        if (StringUtils.isNotBlank(url)) {

            final int extPos = url.lastIndexOf('.');
            if (extPos > -1) {

                final String urlNoExt = url.substring(0, extPos);

                if (urlNoExt.indexOf('_') > -1) {
                    final List<String> languages = languageService.getSupportedLanguages();

                    for (final String language : languages) {
                        if (urlNoExt.endsWith(language) && urlNoExt.charAt(urlNoExt.length() - language.length() - 1) == '_') {
                            return language;
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "imageNameStrategy-resolveSuffix")
    public String resolveSuffix(final String url) {

        if (StringUtils.isNotBlank(url)) {

            final int extPos = url.lastIndexOf('.');
            if (extPos > -1) {

                final String urlNoExt = url.substring(0, extPos);

                if (urlNoExt.indexOf('_') > -1) {

                    final String locale = resolveLocale(url);
                    final String urlNoLocale;
                    if (locale != null) {
                        urlNoLocale = urlNoExt.substring(0, urlNoExt.length() - locale.length() - 1);
                    } else {
                        urlNoLocale = urlNoExt;
                    }

                    if (urlNoLocale.indexOf('_') > -1 && StringUtils.countMatches(urlNoLocale, "_") > 1) {
                        final String[] nameParts = urlNoLocale.split("_");
                        if (nameParts[nameParts.length - 1].length() == 1) {
                            final char csuf = nameParts[nameParts.length - 1].charAt(0);
                            if (csuf >= 'a' && csuf <= 'g') {
                                return String.valueOf(0 + csuf - 'a');
                            }
                        }
                    }

                }
            }

        }

        return "0";
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
    public String resolveRelativeInternalFileNamePath(final String fileName, final String code, final String locale) {
        return resolveRelativeInternalFileNamePath(fileName, code, locale, null, null);
    }


    /** {@inheritDoc} */
    public String resolveRelativeInternalFileNamePath(final String fileName, final String code, final String locale, final String width, final String height) {
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

    /**
     * {@inheritDoc}
     */
    public String createRollingFileName(final String fullFileName, final String code, final String suffix, final String locale) {

        final String systemPart =
                          (Constants.NO_IMAGE.equals(code) ? "" : "_" + code)
                        + "_" + (char)(NumberUtils.toInt(suffix) + 'a')
                        + (StringUtils.isNotEmpty(locale) ? "_" + locale : "");

        final int posExt = fullFileName.lastIndexOf('.');
        final String fileName;
        final String fileExt;
        if (posExt == -1) {
            fileName = fullFileName;
            fileExt = "";
        } else {
            fileName = fullFileName.substring(0, posExt);
            fileExt = fullFileName.substring(posExt); // including '.'
        }

        final String mainPart;
        if (fileName.endsWith(systemPart)) {
            mainPart = fileName.substring(0, fileName.length() - systemPart.length());
        } else {
            mainPart = fileName;
        }

        final int posRollingNumber = mainPart.lastIndexOf('-');
        if (posRollingNumber == -1 || (mainPart.length() < posRollingNumber + 1) || !NumberUtils.isDigits(mainPart.substring(posRollingNumber + 1))) {
            return mainPart + "-1" + systemPart + fileExt;
        }
        return mainPart.substring(0, posRollingNumber) + "-" + (NumberUtils.toInt(mainPart.substring(posRollingNumber + 1)) + 1) + systemPart + fileExt;
    }
}
