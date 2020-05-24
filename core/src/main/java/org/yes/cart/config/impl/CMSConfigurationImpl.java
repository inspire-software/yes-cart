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

package org.yes.cart.config.impl;

import org.yes.cart.service.domain.ContentService;
import org.yes.cart.service.domain.SystemService;
import org.yes.cart.service.dto.DtoContentService;
import org.yes.cart.service.media.MediaFileNameStrategy;

import java.util.Properties;

/**
 * User: denispavlov
 * Date: 22/04/2019
 * Time: 12:17
 */
public class CMSConfigurationImpl extends AbstractConfigurationImpl {

    public CMSConfigurationImpl(final SystemService systemService) {
        super(systemService);
    }

    void registerCustomContentService(final Properties properties) {

        final ContentService cs = determineConfiguration(properties, "CMS.contentService", ContentService.class);

        customise("CMS", "CMS", "contentService", ContentService.class, cs);

    }

    void registerCustomDtoContentService(final Properties properties) {

        final DtoContentService dtocs = determineConfiguration(properties, "CMS.dtoContentService", DtoContentService.class);

        customise("CMS", "CMS", "dtoContentService", DtoContentService.class, dtocs);

    }

    void registerMediaFileNameStrategy(final Properties properties) {

        final MediaFileNameStrategy fileStrategy = determineConfiguration(properties, "CMS.contentFileNameStrategy", MediaFileNameStrategy.class);
        customise("CMS", "CMS", "contentFileNameStrategy", MediaFileNameStrategy.class, fileStrategy);
        final MediaFileNameStrategy imageStrategy = determineConfiguration(properties, "CMS.contentImageNameStrategy", MediaFileNameStrategy.class);
        customise("CMS", "CMS", "contentImageNameStrategy", MediaFileNameStrategy.class, imageStrategy);

    }


    @Override
    protected void onConfigureEvent(final Properties properties) {

        registerCustomContentService(properties);
        registerCustomDtoContentService(properties);
        registerMediaFileNameStrategy(properties);

    }

}
