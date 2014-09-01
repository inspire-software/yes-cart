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

import org.yes.cart.service.domain.ContentServiceTemplateSupport;
import org.yes.cart.web.service.apisupport.ApiSupportFacade;
import org.yes.cart.web.support.constants.WebParametersKeys;

/**
 * User: denispavlov
 * Date: 25/08/2014
 * Time: 00:51
 */
public class ApiSupportFacadeImpl implements ApiSupportFacade {

    private final ContentServiceTemplateSupport templateSupport;

    public ApiSupportFacadeImpl(final ContentServiceTemplateSupport templateSupport) {
        this.templateSupport = templateSupport;

        this.templateSupport.registerFunction("contentURL", new ApiUrlTemplateFunctionProviderImpl(WebParametersKeys.CONTENT_ID));
        this.templateSupport.registerFunction("categoryURL", new ApiUrlTemplateFunctionProviderImpl(WebParametersKeys.CATEGORY_ID));
        this.templateSupport.registerFunction("productURL", new ApiUrlTemplateFunctionProviderImpl(WebParametersKeys.PRODUCT_ID));
        this.templateSupport.registerFunction("skuURL", new ApiUrlTemplateFunctionProviderImpl(WebParametersKeys.SKU_ID));
        this.templateSupport.registerFunction("URL", new ApiUrlTemplateFunctionProviderImpl());

    }
}
