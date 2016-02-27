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

package org.yes.cart.service.mail.impl;

import org.yes.cart.service.domain.TemplateSupport;
import org.yes.cart.service.mail.MailComposerTemplateSupport;

import java.util.HashMap;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 26/02/2016
 * Time: 13:00
 */
public class MailComposerTemplateSupportGroovyImpl implements MailComposerTemplateSupport {

    private final Map<String, FunctionProvider> functions = new HashMap<String, FunctionProvider>();
    private final Map<String, FunctionProvider> functionsCtx = new HashMap<String, FunctionProvider>();

    private final TemplateSupport templateSupport;

    public MailComposerTemplateSupportGroovyImpl(final TemplateSupport templateSupport) {
        this.templateSupport = templateSupport;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String processTemplate(final String template, final String locale, final Map<String, Object> context) {

        final StringBuilder templateWithFuncs = new StringBuilder();
        appendCustomFunctions(templateWithFuncs);
        templateWithFuncs.append(template);

        // Add context as variable to allow smuggling in object into template
        final Map<String, Object> fullContext = new HashMap<String, Object>(context);
        fullContext.put("locale", locale);
        fullContext.putAll(functionsCtx);
        fullContext.put("context", fullContext);

        return this.templateSupport.get(templateWithFuncs.toString()).make(fullContext);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerFunction(final String name, final FunctionProvider functionProvider) {

        functions.put(name, functionProvider);
        functionsCtx.put("func_" + name, functionProvider);

    }

    private void appendCustomFunctions(final StringBuilder templateWithFuncs) {

        templateWithFuncs.append("<% \n");

        for (final Map.Entry<String, FunctionProvider> function : functions.entrySet()) {

            templateWithFuncs
                    .append("def ").append(function.getKey()).append(" = {\n")
                    .append("   func_").append(function.getKey()).append(".doAction(it, locale, context)\n")
                    .append("}\n");

        }

        templateWithFuncs.append(" %>");

    }
}
