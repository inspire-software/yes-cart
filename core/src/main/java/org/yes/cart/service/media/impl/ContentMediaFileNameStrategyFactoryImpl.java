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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.config.ConfigurationRegistry;
import org.yes.cart.service.media.MediaFileNameStrategy;

/**
 * User: denispavlov
 * Date: 27/04/2019
 * Time: 15:19
 */
public class ContentMediaFileNameStrategyFactoryImpl implements MediaFileNameStrategy, ConfigurationRegistry<String, MediaFileNameStrategy> {

    private static final Logger LOG = LoggerFactory.getLogger(ContentMediaFileNameStrategyFactoryImpl.class);

    private MediaFileNameStrategy strategy;
    private final MediaFileNameStrategy fallback;

    private String allowKey;


    public ContentMediaFileNameStrategyFactoryImpl(final MediaFileNameStrategy strategy) {
        this.strategy = strategy;
        this.fallback = strategy;
    }

    @Override
    public String getUrlPath() {
        return strategy.getUrlPath();
    }

    @Override
    public String getRelativeInternalRootDirectory() {
        return strategy.getRelativeInternalRootDirectory();
    }

    @Override
    public String resolveFileName(final String url) {
        return strategy.resolveFileName(url);
    }

    @Override
    public String resolveObjectCode(final String url) {
        return strategy.resolveObjectCode(url);
    }

    @Override
    public String resolveLocale(final String url) {
        return strategy.resolveLocale(url);
    }

    @Override
    public String resolveSuffix(final String url) {
        return strategy.resolveSuffix(url);
    }

    @Override
    public String resolveRelativeInternalFileNamePath(final String fileName, final String code, final String locale) {
        return strategy.resolveRelativeInternalFileNamePath(fileName, code, locale);
    }

    @Override
    public String resolveRelativeInternalFileNamePath(final String fileName, final String code, final String locale, final String width, final String height) {
        return strategy.resolveRelativeInternalFileNamePath(fileName, code, locale, width, height);
    }

    @Override
    public String createRollingFileName(final String fileName, final String code, final String suffix, final String locale) {
        return strategy.createRollingFileName(fileName, code, suffix, locale);
    }

    @Override
    public boolean supports(final String cfgProperty, final Object configuration) {
        return (configuration instanceof MediaFileNameStrategy ||
                (configuration instanceof Class && MediaFileNameStrategy.class.isAssignableFrom((Class<?>) configuration)))
                && this.allowKey.equals(cfgProperty);

    }

    @Override
    public void register(final String key, final MediaFileNameStrategy strategy) {

        if (strategy != null) {
            LOG.debug("Custom CMS settings registering media file strategy {}", strategy.getClass());
            this.strategy = strategy;
        } else {
            LOG.debug("Custom CMS settings registering DEFAULT media file strategy {}", fallback.getClass());
            this.strategy = fallback;
        }

    }

    /**
     * Spring IoC.
     *
     * @param allowKey allow configuration key
     */
    public void setAllowKey(final String allowKey) {
        this.allowKey = allowKey;
    }
}
