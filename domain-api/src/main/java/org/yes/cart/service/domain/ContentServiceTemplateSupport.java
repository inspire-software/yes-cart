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

package org.yes.cart.service.domain;

import java.util.Map;

/**
 * User: denispavlov
 * Date: 25/04/2014
 * Time: 12:58
 */
public interface ContentServiceTemplateSupport {

    interface FunctionProvider {

        Object doAction(Object ... params);

    }

    /**
     * Process template by evaluating dynamic content with respect to given context.
     *
     *
     * @param template template
     * @param locale locale
     * @param context variables to evaluate in template
     *
     * @return processed dynamic content
     */
    String processTemplate(String template, final String locale, Map<String, Object> context);

    /**
     * Mechanism of extending the existing template engine functionality.
     *
     * Function providers facilitate the execution of code which is marked by function name.
     *
     * E.g. for template
     *
     *          Click here to view <a href="productUrl('SKU-001-uri')">SKU-001</a>
     *
     *      function provider must be registered like so:
     *
     *          registerFunction('productUrl', provider)
     *
     * @param name name of function as it will be used in templates
     * @param functionProvider function provider
     */
    void registerFunction(String name, FunctionProvider functionProvider);

}
