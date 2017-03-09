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

package org.yes.cart.service.domain.impl;

import groovy.text.SimpleTemplateEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.yes.cart.service.domain.TemplateSupport;
import org.yes.cart.util.log.Markers;

import java.io.Serializable;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 21/12/2015
 * Time: 18:08
 */
public class GroovySimpleTemplateSupportImpl implements TemplateSupport {

    private static final Logger LOG = LoggerFactory.getLogger(GroovySimpleTemplateSupportImpl.class);

    private final Cache TEMPLATE_CACHE;
    private final SimpleTemplateEngine engine = new SimpleTemplateEngine();

    public GroovySimpleTemplateSupportImpl(final CacheManager cacheManager) {

        TEMPLATE_CACHE = cacheManager.getCache("contentService-templateSupport");

    }


    private Template getTemplateFromValueWrapper(final Cache.ValueWrapper wrapper) {
        if (wrapper != null) {
            return (Template) wrapper.get();
        }
        return null;
    }

    @Override
    public Template get(final String template) {

        final Template tmp = getTemplateFromValueWrapper(TEMPLATE_CACHE.get(template));
        if (tmp == null) {

            try {

                final groovy.text.Template gtmp = engine.createTemplate(template);
                final Template tmp2 = new GroovyTemplateImpl(gtmp);
                TEMPLATE_CACHE.put(template, tmp2);
                return tmp2;

            } catch (Exception cnfe) {

                LOG.error(Markers.alert(), "Unable to process groovy template: " + cnfe.getMessage() + "\n" + template, cnfe);

            }

            return new NullTemplateImpl();

        }
        return tmp;
    }


    public static class GroovyTemplateImpl implements Template, Serializable {

        private final groovy.text.Template gtmp;

        public GroovyTemplateImpl(final groovy.text.Template gtmp) {
            this.gtmp = gtmp;
        }

        /** {@inheritDoc} */
        @Override
        public String make(final Map binding) {
            return gtmp.make(binding).toString();
        }
    }

    public static class NullTemplateImpl implements Template {
        /** {@inheritDoc} */
        @Override
        public String make(final Map binding) {
            return "";
        }

    }

}
