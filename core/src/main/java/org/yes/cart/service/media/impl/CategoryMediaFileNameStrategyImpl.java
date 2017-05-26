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

import org.apache.commons.lang.StringUtils;
import org.yes.cart.constants.Constants;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.AttrValueCategory;
import org.yes.cart.service.misc.LanguageService;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class CategoryMediaFileNameStrategyImpl extends AbstractMediaFileNameStrategyImpl {

    private final GenericDAO<AttrValueCategory, Long> attrValueEntityCategoryDao;

    /**
     * Construct image name strategy
     *
     * @param urlPath                        URL path that identifies this strategy
     * @param relativeInternalRootDirectory  internal image relative path root directory without {@link java.io.File#separator}. E.g. "category"
     * @param attrValueCategoryDao           category attributes dao
     * @param languageService                language service
     */
    public CategoryMediaFileNameStrategyImpl(final String urlPath,
                                             final String relativeInternalRootDirectory,
                                             final GenericDAO<AttrValueCategory, Long> attrValueCategoryDao,
                                             final LanguageService languageService) {
        super(urlPath, relativeInternalRootDirectory, languageService);
        this.attrValueEntityCategoryDao = attrValueCategoryDao;
    }

    /**
     * {@inheritDoc}
     */
    public String resolveObjectCode(final String url) {

        if (StringUtils.isNotBlank(url)) {
            // Categories codes are used from URI which may have multiple undescores so always need to resolve the code
            // from the domain object
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
    protected String resolveObjectCodeInternal(final String url) {

        final String val = resolveFileName(url);

        final Object[] uriAndGuid = attrValueEntityCategoryDao.findSingleByNamedQuery("CATEGORY.URI.AND.GUID.BY.MEDIAFILE.NAME", val, getAttributePrefix() + '%');

        if (uriAndGuid != null && uriAndGuid.length == 2) {
            if (uriAndGuid[0] instanceof String) {
                return (String) uriAndGuid[0];
            } else if (uriAndGuid[1] instanceof String) {
                return (String) uriAndGuid[1];
            }
        }

        return null;

    }

}
