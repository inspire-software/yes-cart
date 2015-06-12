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

package org.yes.cart.web.service.wicketsupport.impl;

import org.yes.cart.service.domain.ContentServiceTemplateSupport;
import org.yes.cart.web.service.wicketsupport.LinksSupport;
import org.yes.cart.web.service.wicketsupport.PaginationSupport;
import org.yes.cart.web.service.wicketsupport.WicketSupportFacade;
import org.yes.cart.web.support.constants.WebParametersKeys;
import org.yes.cart.web.util.WicketUtil;

/**
 * User: denispavlov
 * Date: 13-06-28
 * Time: 9:27 AM
 */
public class WicketSupportFacadeImpl implements WicketSupportFacade {

    private final LinksSupport linksSupport;
    private final PaginationSupport paginationSupport;
    private final ContentServiceTemplateSupport templateSupport;
    private final WicketUtil wicketUtil;

    public WicketSupportFacadeImpl(final LinksSupport linksSupport,
                                   final PaginationSupport paginationSupport,
                                   final ContentServiceTemplateSupport templateSupport,
                                   final WicketUtil wicketUtil) {
        this.linksSupport = linksSupport;
        this.paginationSupport = paginationSupport;
        this.templateSupport = templateSupport;
        this.wicketUtil = wicketUtil;

        this.templateSupport.registerFunction("contentURL", new WicketUrlTemplateFunctionProviderImpl(this.wicketUtil, WebParametersKeys.CONTENT_ID));
        this.templateSupport.registerFunction("categoryURL", new WicketUrlTemplateFunctionProviderImpl(this.wicketUtil, WebParametersKeys.CATEGORY_ID));
        this.templateSupport.registerFunction("productURL", new WicketUrlTemplateFunctionProviderImpl(this.wicketUtil, WebParametersKeys.PRODUCT_ID));
        this.templateSupport.registerFunction("skuURL", new WicketUrlTemplateFunctionProviderImpl(this.wicketUtil, WebParametersKeys.SKU_ID));
        this.templateSupport.registerFunction("URL", new WicketUrlTemplateFunctionProviderImpl(this.wicketUtil));

    }

    /** {@inheritDoc} */
    @Override
    public LinksSupport links() {
        return linksSupport;
    }

    /** {@inheritDoc} */
    @Override
    public PaginationSupport pagination() {
        return paginationSupport;
    }
}
