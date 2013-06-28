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

package org.yes.cart.web.util;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.mapper.parameter.IPageParametersEncoder;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.yes.cart.web.support.constants.WebParametersKeys;
import org.yes.cart.web.support.seo.BookmarkService;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 6 aug 11
 * Time: 5:06 PM
 */
public class SeoBookmarkablePageParametersEncoder implements IPageParametersEncoder {

    private final BookmarkService bookmarkService;
    private final boolean seoEnabled;

    /**
     * Construct page parameter encoder.
     *
     * @param bookmarkService bookmark service
     * @param seoEnabled      is seo url rewriting enabled.
     */
    public SeoBookmarkablePageParametersEncoder(final BookmarkService bookmarkService,
                                                final boolean seoEnabled) {
        this.bookmarkService = bookmarkService;
        this.seoEnabled = seoEnabled;
    }

    /**
     * {@inheritDoc}
     */
    public Url encodePageParameters(final PageParameters pageParameters) {
        final Url url = new Url();
        for (PageParameters.NamedPair pair : pageParameters.getAllNamed()) {
            url.getSegments().add(pair.getKey());
            url.getSegments().add(encodeId(pair.getKey(), pair.getValue()));
        }
        return url;
    }

    /**
     * @param idName parameters name
     * @param idValueToEncode id to encode into URI
     * @return URI
     */
    private String encodeId(final String idName, final String idValueToEncode) {
        if (seoEnabled) {
            final String rez;
            if (WebParametersKeys.CATEGORY_ID.equals(idName)) {

                rez = bookmarkService.saveBookmarkForCategory(idValueToEncode);

            } else if (WebParametersKeys.PRODUCT_ID.equals(idName)) {

                rez = bookmarkService.saveBookmarkForProduct(idValueToEncode);

            } else if (WebParametersKeys.SKU_ID.equals(idName)) {

                rez = bookmarkService.saveBookmarkForSku(idValueToEncode);

            } else if (WebParametersKeys.CONTENT_ID.equals(idName)) {

                rez = bookmarkService.saveBookmarkForContent(idValueToEncode);

            } else {

                rez = idValueToEncode;

            }

            /*
             * Encoder must not return null as is results in NPE in wicket urlFor().
             * If we get situations like this then probably we either:
             * 1. Used seo uri instead of object ID in PageParameters of BookmarkableLink - which is fine
             * 2. BookmarkService was unable to look up object by ID - which will be deals with upon rendering the page
             */
            return rez != null ? rez : idValueToEncode;
        }

        return idValueToEncode;
    }


    /**
     * {@inheritDoc}
     */
    public PageParameters decodePageParameters(final Request request) {
        final PageParameters parameters = new PageParameters();
        String name = null;
        for (String segment : request.getUrl().getSegments()) {
            if (name == null) {
                name = segment;
            } else {
                parameters.add(name, decodeId(name, segment));
                name = null;
            }
        }
        return parameters;
    }


    private String decodeId(final String idName, final String idValueToDecode) {
        if (seoEnabled && !NumberUtils.isDigits(idValueToDecode)) {
            if (WebParametersKeys.CATEGORY_ID.equals(idName)) {
                final String id = bookmarkService.getCategoryForURI(idValueToDecode);
                if (id != null) {
                    return id;
                }
            } else if (WebParametersKeys.PRODUCT_ID.equals(idName)) {
                final String id = bookmarkService.getProductForURI(idValueToDecode);
                if (id != null) {
                    return id;
                }
            } else if (WebParametersKeys.SKU_ID.equals(idName)) {
                final String id = bookmarkService.getSkuForURI(idValueToDecode);
                if (id != null) {
                    return id;
                }
            } else if (WebParametersKeys.CONTENT_ID.equals(idName)) {
                final String id = bookmarkService.getContentForURI(idValueToDecode);
                if (id != null) {
                    return id;
                }
            }

        }
        return idValueToDecode;
    }


}
