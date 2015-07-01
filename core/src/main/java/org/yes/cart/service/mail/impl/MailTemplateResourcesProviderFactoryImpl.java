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

import org.apache.commons.lang.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.yes.cart.service.mail.MailTemplateResourcesProvider;

import java.io.IOException;
import java.util.List;

/**
 * Factory to get resources using various providers.
 *
 * User: denispavlov
 * Date: 09/01/2015
 * Time: 17:43
 */
public class MailTemplateResourcesProviderFactoryImpl implements MailTemplateResourcesProvider {

    private final List<MailTemplateResourcesProvider> chain;

    public MailTemplateResourcesProviderFactoryImpl(final List<MailTemplateResourcesProvider> chain) {
        this.chain = chain;
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "themeService-mailTemplate")
    public String getTemplate(final List<String> mailTemplateChain,
                              final String shopCode,
                              final String locale,
                              final String templateName,
                              final String ext) throws IOException {
        for (final MailTemplateResourcesProvider provider : chain) {
            final String template = provider.getTemplate(mailTemplateChain, shopCode, locale, templateName, ext);
            if (StringUtils.isNotBlank(template)) {
                return template;
            }
        }

        throw new RuntimeException("mail template does not exist: " + templateName + ext);

    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "themeService-mailResource")
    public byte[] getResource(final List<String> mailTemplateChain,
                              final String shopCode,
                              final String locale,
                              final String templateName,
                              final String resourceFilename) throws IOException {
        for (final MailTemplateResourcesProvider provider : chain) {
            final byte[] resource = provider.getResource(mailTemplateChain, shopCode, locale, templateName, resourceFilename);
            if (resource != null && resource.length > 0) {
                return resource;
            }
        }

        throw new RuntimeException("mail resource does not exist: " + templateName + "/" + resourceFilename);

    }
}
