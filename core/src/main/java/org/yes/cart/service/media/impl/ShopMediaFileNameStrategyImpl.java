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

package org.yes.cart.service.media.impl;

import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.AttrValueShop;
import org.yes.cart.service.misc.LanguageService;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class ShopMediaFileNameStrategyImpl extends AbstractMediaFileNameStrategyImpl {

    private final GenericDAO<AttrValueShop, Long> attrValueShopDao;

    /**
     * Construct image name strategy
     *
     * @param urlPath                        URL path that identifies this strategy
     * @param relativeInternalRootDirectory  internal image relative path root directory without {@link java.io.File#separator}. E.g. "brand"
     * @param attrValueShopDao               shop attribute dao
     * @param languageService                language service
     */
    public ShopMediaFileNameStrategyImpl(final String urlPath,
                                         final String relativeInternalRootDirectory,
                                         final GenericDAO<AttrValueShop, Long> attrValueShopDao,
                                         final LanguageService languageService) {
        super(urlPath, relativeInternalRootDirectory, languageService);
        this.attrValueShopDao = attrValueShopDao;
    }

    /**
     * {@inheritDoc}
     */
    protected String resolveObjectCodeInternal(final String url) {

        final String val = resolveFileName(url);

        final String code = attrValueShopDao.findSingleByNamedQuery("SHOP.CODE.BY.MEDIAFILE.NAME", val, getAttributePrefix() + '%');

        if (code != null) {
            return code;
        }

        return null;

    }

}
