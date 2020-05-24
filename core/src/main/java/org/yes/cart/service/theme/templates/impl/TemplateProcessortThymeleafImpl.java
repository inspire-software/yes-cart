/*
 * Copyright 2009 Inspire-Software.com
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

package org.yes.cart.service.theme.templates.impl;

import org.yes.cart.service.theme.templates.TemplateProcessor;
import org.yes.cart.service.theme.templates.TemplateSupport;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * User: denispavlov
 * Date: 17/02/2019
 * Time: 20:11
 */
public class TemplateProcessortThymeleafImpl implements TemplateProcessor {

    private final Map<String, FunctionProvider> functions = new HashMap<>();
    private final Map<String, Locale> localeCache = new ConcurrentHashMap<>();

    private final TemplateSupport templateSupport;

    public TemplateProcessortThymeleafImpl(final TemplateSupport templateSupport) {
        this.templateSupport = templateSupport;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(final String template, final String locale, final Map<String, Object> context) {
        // support for either attribute namespace or inline expression
        return template.contains(" th:") || template.contains("[[${") ;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String processTemplate(final String template, final String locale, final Map<String, Object> context) {

        // Add context as variable to allow smuggling in object into template
        final Map<String, Object> fullContext = new HashMap<>(context);
        fullContext.put("locale", locale);
        fullContext.put("localeObject", lazyLoad(locale));
        appendFunctions(locale, fullContext);
        fullContext.put("context", fullContext);

        return this.templateSupport.get(template).make(fullContext);

    }

    Locale lazyLoad(final String lang) {
        if (lang == null) {
            return null;
        }
        return this.localeCache.computeIfAbsent(lang, Locale::new);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerFunction(final String name, final FunctionProvider functionProvider) {

        functions.put(name, functionProvider);

    }

    private void appendFunctions(final String locale, final Map<String, Object> fullContext) {

        for (final Map.Entry<String, FunctionProvider> entry : functions.entrySet()) {

            fullContext.put(entry.getKey(), new FunctionsWrapper(entry.getValue(), locale, fullContext));

        }

    }

    private static class FunctionsWrapper {

        private final FunctionProvider provider;
        private final String locale;
        private final Map<String, Object> context;

        private FunctionsWrapper(final FunctionProvider provider,
                                 final String locale,
                                 final Map<String, Object> context) {
            this.provider = provider;
            this.locale = locale;
            this.context = context;
        }

        public Object func(final Object... params) {
            if (params == null || params.length == 0) {
                return provider.doAction(null, locale, context);
            }
            final Object[] full = new Object[params.length + 2];
            System.arraycopy(params, 0, full, 0, params.length);
            full[params.length] = locale;
            full[params.length + 1] = context;
            return provider.doAction(full);
        }

    }

}
