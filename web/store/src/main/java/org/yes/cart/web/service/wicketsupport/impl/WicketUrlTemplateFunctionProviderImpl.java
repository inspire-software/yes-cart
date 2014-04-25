package org.yes.cart.web.service.wicketsupport.impl;

import org.yes.cart.service.domain.ContentServiceTemplateSupport;
import org.yes.cart.web.util.WicketUtil;

/**
 * User: denispavlov
 * Date: 25/04/2014
 * Time: 16:37
 */
public class WicketUrlTemplateFunctionProviderImpl implements ContentServiceTemplateSupport.FunctionProvider {

    private final String paramName;

    public WicketUrlTemplateFunctionProviderImpl() {
        this.paramName = "";
    }

    public WicketUrlTemplateFunctionProviderImpl(final String paramName) {
        this.paramName = "/" + paramName;
    }

    @Override
    public Object doAction(final Object... params) {

        final StringBuilder url = new StringBuilder(WicketUtil.getHttpServletRequest().getContextPath());
        if (params != null && params.length >= 1) {

            final String uri = String.valueOf(params[0]);
            url.append(this.paramName).append('/').append(uri);

        }
        return url.toString();
    }

}
