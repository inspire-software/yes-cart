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

package org.yes.cart.service.theme.templates.impl;

import org.yes.cart.service.theme.templates.TemplateProcessor;

import java.util.Map;

/**
 * User: denispavlov
 * Date: 17/02/2019
 * Time: 20:15
 */
public class TemplateProcessorFactoryImpl implements TemplateProcessor {

    private final TemplateProcessor[] processors;

    public TemplateProcessorFactoryImpl(final TemplateProcessor[] processors) {
        this.processors = processors;
    }

    /** {@inheritDoc} */
    @Override
    public boolean supports(final String template, final String locale, final Map<String, Object> context) {
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public String processTemplate(final String template, final String locale, final Map<String, Object> context) {
        for (final TemplateProcessor support : processors) {
            if (support.supports(template, locale, context)) {
                return support.processTemplate(template, locale, context);
            }
        }
        return null;
    }

    @Override
    public void registerFunction(final String name, final FunctionProvider functionProvider) {
        for (final TemplateProcessor support : processors) {
            support.registerFunction(name, functionProvider);
        }
    }
}
