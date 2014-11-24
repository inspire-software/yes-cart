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

package org.yes.cart.web.service.apisupport.impl;

import org.yes.cart.domain.entity.Shop;
import org.yes.cart.service.domain.ContentServiceTemplateSupport;
import org.yes.cart.web.application.ApplicationDirector;

/**
 * User: denispavlov
 * Date: 25/08/2014
 * Time: 00:54
 */
public class ApiUrlTemplateFunctionProviderImpl implements ContentServiceTemplateSupport.FunctionProvider {

    private final String paramName;

    public ApiUrlTemplateFunctionProviderImpl() {
        this.paramName = "";
    }

    public ApiUrlTemplateFunctionProviderImpl(final String paramName) {
        this.paramName = "/" + paramName;
    }

    @Override
    public Object doAction(final Object... params) {

        final Shop shop = ApplicationDirector.getCurrentShop();
        final String defaultUrl = shop.getDefaultShopUrl();
        final StringBuilder url = new StringBuilder(defaultUrl);
        if (defaultUrl.endsWith("/")) {
            url.append("yes-shop"); // TODO: add attribute to SHOP to store this
        } else {
            url.append("/yes-shop");
        }
        if (params != null && params.length >= 1) {

            final String uri = String.valueOf(params[0]);
            url.append(this.paramName).append('/').append(uri);

        }
        return url.toString();
    }



}
