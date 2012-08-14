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

import com.google.common.collect.MapMaker;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.mapper.parameter.IPageParametersEncoder;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.yes.cart.constants.Constants;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.service.domain.CategoryService;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.web.support.constants.WebParametersKeys;
import org.yes.cart.web.support.entity.decorator.impl.DecoratorUtil;
import org.yes.cart.web.support.entity.decorator.impl.ProductDecoratorImpl;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 6 aug 11
 * Time: 5:06 PM
 */
public class SeoBookmarkablePageParametersEncoder implements IPageParametersEncoder {

    private final CategoryService categoryService;
    private final ProductService productService;
    private final boolean seoEnabled;

    /**
     * Construct page parameter encoder.
     *
     * @param categoryService to get category seo info
     * @param productService  used to get product and sku seo info.
     * @param seoEnabled      is seo url rewriting enabled.
     */
    public SeoBookmarkablePageParametersEncoder(final CategoryService categoryService,
                                                final ProductService productService,
                                                final boolean seoEnabled) {
        this.categoryService = categoryService;
        this.productService = productService;
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

    public static final ConcurrentMap<String, String> CATEGORY_ENCODE_CACHE = new MapMaker().concurrencyLevel(16).softValues().expiration(Constants.DEFAULT_EXPIRATION_TIMEOUT, TimeUnit.MINUTES).makeMap();
    public static final ConcurrentMap<String, String> SKU_ENCODE_CACHE = new MapMaker().concurrencyLevel(16).softValues().expiration(Constants.DEFAULT_EXPIRATION_TIMEOUT, TimeUnit.MINUTES).makeMap();

    /**
     * TODO move appropriate endocing code to decorators.
     * TODO create reverse cache in decorators for fast decode
     * @param idName
     * @param idValueToEncode
     * @return
     */
    private String encodeId(final String idName, final String idValueToEncode) {
        if (seoEnabled) {
            final String rez;
            if (WebParametersKeys.CATEGORY_ID.equals(idName)) {
                String seo = CATEGORY_ENCODE_CACHE.get(idValueToEncode);
                if (seo == null) {
                    final Category category = categoryService.getById(NumberUtils.toLong(idValueToEncode));
                    if (category != null) {
                        seo = DecoratorUtil.encodeId(
                                idValueToEncode,
                                category.getSeo()
                        );
                    }
                    if (seo != null) {
                        CATEGORY_ENCODE_CACHE.put(idValueToEncode, seo);
                    }
                }
                rez = seo;
            } else if (WebParametersKeys.PRODUCT_ID.equals(idName)) {
                rez = ProductDecoratorImpl.getSeoUrlParameterValueProduct(idValueToEncode, productService);
            } else if (WebParametersKeys.SKU_ID.equals(idName)) {
                String seo = SKU_ENCODE_CACHE.get(idValueToEncode);
                if (seo == null) {
                    final ProductSku productSku = productService.getSkuById(NumberUtils.toLong(idValueToEncode));
                    if (productSku != null) {
                        seo = DecoratorUtil.encodeId(
                                idValueToEncode,
                                productSku.getSeo()
                        );
                    }
                    if (seo != null) {
                        ProductDecoratorImpl.PRODUCT_ENCODE_CACHE.put(idValueToEncode, seo);
                    }
                }
                rez = seo;

            } else {
                rez = idValueToEncode;

            }

            return rez;
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
            //todo use cache in decorators
            if (WebParametersKeys.CATEGORY_ID.equals(idName)) {
                final Long id = categoryService.getCategoryIdBySeoUri(idValueToDecode);
                if (id != null) {
                    return id.toString();
                }
            } else if (WebParametersKeys.PRODUCT_ID.equals(idName)) {
                final Long id = productService.getProductIdBySeoUri(idValueToDecode);
                if (id != null) {
                    return id.toString();
                }
            } else if (WebParametersKeys.SKU_ID.equals(idName)) {
                final Long id = productService.getProductSkuIdBySeoUri(idValueToDecode);
                if (id != null) {
                    return id.toString();
                }
            }

        }
        return idValueToDecode;
    }


}
