package org.yes.cart.service.domain.impl;

import groovy.text.SimpleTemplateEngine;
import groovy.text.Template;
import org.yes.cart.service.domain.ContentServiceTemplateSupport;
import org.yes.cart.util.ShopCodeContext;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 25/04/2014
 * Time: 13:00
 */
public class ContentServiceTemplateSupportGroovyImpl implements ContentServiceTemplateSupport {

    private final SimpleTemplateEngine engine = new SimpleTemplateEngine();

    private final Map<String, FunctionProvider> functions = new HashMap<String, FunctionProvider>();
    private final Map<String, FunctionProvider> functionsCtx = new HashMap<String, FunctionProvider>();

    /**
     * {@inheritDoc}
     */
    @Override
    public String processTemplate(final String template, final String locale, final Map<String, Object> context) {

        try {

            final StringBuilder templateWithFuncs = new StringBuilder();
            appendCustomFunctions(templateWithFuncs);
            templateWithFuncs.append(template);

            // Add context as variable to allow smuggling in object into template
            final Map<String, Object> fullContext = new HashMap<String, Object>(context);
            fullContext.put("locale", locale);
            fullContext.putAll(functionsCtx);
            fullContext.put("context", fullContext);

            final Template tmp = engine.createTemplate(templateWithFuncs.toString());

            return tmp.make(fullContext).toString();

        } catch (ClassNotFoundException cnfe) {

            ShopCodeContext.getLog(this).error("Unable to process template: " + cnfe.getMessage() + "\n" + template, cnfe);

        } catch (IOException ioe) {

            ShopCodeContext.getLog(this).error("Unable to process template: " + ioe.getMessage() + "\n" + template, ioe);

        }

        return ""; // Nothing
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
