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
import org.yes.cart.domain.entity.AttrValueContent;
import org.yes.cart.service.misc.LanguageService;

import java.util.List;

/**
 * User: denispavlov
 * Date: 27/04/2019
 * Time: 15:14
 */
public class ContentCMS3MediaFileNameStrategyImpl extends AbstractMediaFileNameStrategyImpl {

    private final GenericDAO<AttrValueContent, Long> attrValueEntityContentDao;

    /**
     * Construct image name strategy
     *
     * @param urlPath                        URL path that identifies this strategy
     * @param relativeInternalRootDirectory  internal image relative path root directory without {@link java.io.File#separator}. E.g. "category"
     * @param attrValueContentDao            content attributes dao
     * @param languageService                language service
     */
    public ContentCMS3MediaFileNameStrategyImpl(final String urlPath,
                                                final String relativeInternalRootDirectory,
                                                final GenericDAO<AttrValueContent, Long> attrValueContentDao,
                                                final LanguageService languageService) {
        super(urlPath, relativeInternalRootDirectory, languageService);
        this.attrValueEntityContentDao = attrValueContentDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
    @Override
    protected String resolveObjectCodeInternal(final String url) {

        final String val = resolveFileName(url);

        final List<Object[]> uriAndGuid = (List) attrValueEntityContentDao.findQueryObjectByNamedQuery("CONTENT.URI.AND.GUID.BY.MEDIAFILE.NAME", val, getAttributePrefix() + '%');

        if (uriAndGuid != null && !uriAndGuid.isEmpty()) {
            final Object[] uriAndGuidPair = uriAndGuid.get(0);
            if (uriAndGuidPair[0] instanceof String) {
                return (String) uriAndGuidPair[0];
            } else if (uriAndGuidPair[1] instanceof String) {
                return (String) uriAndGuidPair[1];
            }
        }

        return null;

    }

}
