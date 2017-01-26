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

package org.yes.cart.web.support.service.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.domain.AttributeService;
import org.yes.cart.service.domain.CategoryService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.support.constants.CentralViewLabel;
import org.yes.cart.web.support.constants.WebParametersKeys;
import org.yes.cart.web.support.service.CentralViewResolver;
import org.yes.cart.web.support.util.HttpUtil;

import java.util.Map;
import java.util.Set;

/**
 * User: denispavlov
 * Date: 10/04/2015
 * Time: 08:14
 */
public class CentralViewResolverSearchImpl implements CentralViewResolver {

    private static final Pair<String, String> DEFAULT = new Pair<String, String>(CentralViewLabel.SEARCH_LIST, CentralViewLabel.SEARCH_LIST);

    private final ShopService shopService;
    private final AttributeService attributeService;
    private final CategoryService categoryService;


    public CentralViewResolverSearchImpl(final ShopService shopService,
                                         final AttributeService attributeService,
                                         final CategoryService categoryService) {
        this.shopService = shopService;
        this.attributeService = attributeService;
        this.categoryService = categoryService;
    }

    /**
     * Resolve search view if applicable.
     * <p>
     * Rules:<p>
     * 1. If there is {@link WebParametersKeys#QUERY} and no category then use  {@link CentralViewLabel#SEARCH_LIST} as this is global search<p>
     * 2. If there is navigatable attribute code ({@link org.yes.cart.service.domain.AttributeService#getAllNavigatableAttributeCodes()})
     *     and no category then use  {@link CentralViewLabel#SEARCH_LIST} as this is filtered nav search<p>
     * 3. If there is {@link WebParametersKeys#CATEGORY_ID} then use category product type ui search template<p>
     *
     * @param parameters            request parameters map
     *
     * @return search label if this is a search request or null (if not applicable)
     */
    @Override
    public Pair<String, String> resolveMainPanelRendererLabel(final Map parameters) {
        if (parameters.containsKey(WebParametersKeys.QUERY) ||
                isAttributiveFilteredNavigation(attributeService.getAllNavigatableAttributeCodes(), parameters)) {  // list of product attributes plus brand and price
            if (parameters.containsKey(WebParametersKeys.CATEGORY_ID)) {
                final long categoryId = NumberUtils.toLong(HttpUtil.getSingleValue(parameters.get(WebParametersKeys.CATEGORY_ID)));
                if (categoryId > 0L) {

                    final long shopId = ApplicationDirector.getShoppingCart().getShoppingContext().getShopId();

                    final String searchTemplate = shopService.getShopCategorySearchTemplate(shopId, categoryId);
                    if (StringUtils.isNotBlank(searchTemplate)) {
                        return new Pair<String, String>(searchTemplate, CentralViewLabel.SEARCH_LIST);
                    }
                }
            }
            return DEFAULT;
        }
        return null;
    }


    private boolean isAttributiveFilteredNavigation(final Set<String> allowedAttributeNames,
                                                    final Map parameters) {
        for (Object obj : parameters.keySet()) {
            final String decodedParameterKeyName = (String) obj;
            if (allowedAttributeNames.contains(decodedParameterKeyName)) {
                return true;
            }
        }
        return false;
    }

}
