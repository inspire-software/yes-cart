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

import java.util.Set;

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
            encodeSegment(url, pair.getKey(), pair.getValue());
        }
        return url;
    }

    /**
     * Encode a segment of URI into SEO friendly version.
     *
     * @param idName parameters name
     * @param idValueToEncode id to encode into URI
     */
    private void encodeSegment(final Url url, final String idName, final String idValueToEncode) {

        final String rez;

        if (seoEnabled) {
            if (WebParametersKeys.CATEGORY_ID.equals(idName)) {

                final String categoryId = bookmarkService.saveBookmarkForCategory(idValueToEncode);
                rez = categoryId != null ? categoryId : idValueToEncode;

            } else if (WebParametersKeys.PRODUCT_ID.equals(idName)) {

                final String productId = bookmarkService.saveBookmarkForProduct(idValueToEncode);
                rez = productId != null ? productId : idValueToEncode;

            } else if (WebParametersKeys.SKU_ID.equals(idName)) {

                final String skuId = bookmarkService.saveBookmarkForSku(idValueToEncode);
                rez = skuId != null ? skuId : idValueToEncode;

            } else if (WebParametersKeys.CONTENT_ID.equals(idName)) {

                final String contentId = bookmarkService.saveBookmarkForContent(idValueToEncode);
                rez = contentId != null ? contentId : idValueToEncode;

            } else if (WebParametersKeys.PAGE_TYPE.equals(idName)) {

                rez = null; // internal param

            } else {

                rez = idValueToEncode;

            }

        } else {

            rez = idValueToEncode;

        }

        /*
        * Encoder must not return null as it results in NPE in wicket urlFor() therefore no
        * need to output segments that do not have value.
        *
        * If we get situations like this then probably we either:
        * 1. Used seo uri instead of object ID in PageParameters of BookmarkableLink - which is fine
        * 2. BookmarkService was unable to look up object by ID - which will be dealt with upon rendering the page
        */
        if (rez != null) {
            url.getSegments().add(idName);
            url.getSegments().add(rez);
        }
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
                decodeSegment(parameters, name, segment);
                name = null;
            }
        }
        // enhance the page parameters to understand which page we are currently on
        final Set<String> namedKeys = parameters.getNamedKeys();
        if (namedKeys.contains(WebParametersKeys.SKU_ID)) {
            parameters.set(WebParametersKeys.PAGE_TYPE, WebParametersKeys.SKU_ID);
        } else if (namedKeys.contains(WebParametersKeys.PRODUCT_ID)) {
            parameters.set(WebParametersKeys.PAGE_TYPE, WebParametersKeys.PRODUCT_ID);
        } else if (namedKeys.contains(WebParametersKeys.CONTENT_ID)) {
            parameters.set(WebParametersKeys.PAGE_TYPE, WebParametersKeys.CONTENT_ID);
        } else if (namedKeys.contains(WebParametersKeys.CATEGORY_ID)) {
            parameters.set(WebParametersKeys.PAGE_TYPE, WebParametersKeys.CATEGORY_ID);
        } else {
            parameters.set(WebParametersKeys.PAGE_TYPE, "");
        }

        return parameters;
    }

    private void decodeSegment(final PageParameters pageParameters, final String idName, final String idValueToDecode) {

        String seoId = idValueToDecode;
        if (seoEnabled && !NumberUtils.isDigits(idValueToDecode)) {
            if (WebParametersKeys.CATEGORY_ID.equals(idName)) {
                final String id = bookmarkService.getCategoryForURI(idValueToDecode);
                if (id != null) {
                    seoId = id;
                }
            } else if (WebParametersKeys.PRODUCT_ID.equals(idName)) {
                final String id = bookmarkService.getProductForURI(idValueToDecode);
                if (id != null) {
                    seoId = id;
                }
            } else if (WebParametersKeys.SKU_ID.equals(idName)) {
                final String id = bookmarkService.getSkuForURI(idValueToDecode);
                if (id != null) {
                    seoId = id;
                }
            } else if (WebParametersKeys.CONTENT_ID.equals(idName)) {
                final String id = bookmarkService.getContentForURI(idValueToDecode);
                if (id != null) {
                    seoId = id;
                }
            }
        }

        pageParameters.add(idName, seoId);

    }


}
