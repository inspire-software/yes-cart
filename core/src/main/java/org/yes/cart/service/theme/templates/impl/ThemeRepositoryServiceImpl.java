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

package org.yes.cart.service.theme.templates.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.config.ConfigurationRegistry;
import org.yes.cart.service.domain.ContentService;
import org.yes.cart.service.theme.templates.ThemeRepositoryService;

import java.io.InputStream;

/**
 * Date: 19/09/2020
 * Time: 12:59
 */
public class ThemeRepositoryServiceImpl
        implements ThemeRepositoryService, ConfigurationRegistry<String, ThemeRepositoryService> {

    private static final Logger LOG = LoggerFactory.getLogger(ThemeRepositoryServiceImpl.class);

    private ThemeRepositoryService themeRepositoryService;

    /** {@inheritDoc} */
    @Override
    public InputStream getSource(final String path) {
        if (themeRepositoryService != null) {
            return themeRepositoryService.getSource(path);
        }
        LOG.error("Theme repository is used before it is initialized. This could be slow application context load effect, however please ensure that themeRepositoryServiceDefault is defined.");
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public boolean supports(final String cfgProperty, final Object configuration) {
        return configuration instanceof ContentService ||
                (configuration instanceof Class && ThemeRepositoryService.class.isAssignableFrom((Class<?>) configuration));
    }

    /** {@inheritDoc} */
    @Override
    public void register(final String key, final ThemeRepositoryService themeRepositoryService) {

        if (themeRepositoryService != null) {
            LOG.debug("Custom Theme repository settings registering service {}", themeRepositoryService.getClass());
            this.themeRepositoryService = themeRepositoryService;
        }

    }
}
