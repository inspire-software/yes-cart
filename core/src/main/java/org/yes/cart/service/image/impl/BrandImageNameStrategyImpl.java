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

import org.yes.cart.constants.Constants;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.AttrValueBrand;

import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class BrandImageNameStrategyImpl extends AbstractImageNameStrategyImpl {

    private final GenericDAO<AttrValueBrand, Long> attrValueBrandDao;

    /**
     * Construct image name strategy
     *
     * @param relativeInternalRootDirectory  internal image relative path root directory without {@see File#separator}. E.g. "brand"
     * @param attrValueBrandDao              brand attribute dao
     */
    public BrandImageNameStrategyImpl(final String relativeInternalRootDirectory,
                                      final GenericDAO<AttrValueBrand, Long> attrValueBrandDao) {
        super(Constants.BRAND_IMAGE_REPOSITORY_URL_PATTERN, relativeInternalRootDirectory);
        this.attrValueBrandDao = attrValueBrandDao;
    }

    /**
     * {@inheritDoc}
     */
    protected String resolveObjectCodeInternal(final String url) {

        final String val = resolveFileName(url);

        final Object[] nameAndGuid = attrValueBrandDao.findSingleByNamedQuery("BRAND.NAME.AND.GUID.BY.IMAGE.NAME", val);

        if (nameAndGuid != null && nameAndGuid.length == 2) {
            if (nameAndGuid[0] instanceof String) {
                return (String) nameAndGuid[0];
            } else if (nameAndGuid[1] instanceof String) {
                return (String) nameAndGuid[1];
            }
        }

        return null;

    }

}
