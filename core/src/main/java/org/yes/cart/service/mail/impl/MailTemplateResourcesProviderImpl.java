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

package org.yes.cart.service.mail.impl;

import org.apache.commons.io.IOUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.context.ServletContextAware;
import org.yes.cart.service.mail.MailTemplateResourcesProvider;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Mail template resolver relies on the fact that all templates are shipped with the corresponding
 * war file and thus are locatable within webapp using servlet context.
 *
 * Using shared resources bring a lot of complexity so it is better to keep all templates with each war
 * file thus giving a complete deployable. Updates still can be achieved by copying extra resources
 * (e.g. additional themes) staright into exploded war directory.
 *
 * User: denispavlov
 * Date: 03/09/2014
 * Time: 15:00
 */
public class MailTemplateResourcesProviderImpl implements MailTemplateResourcesProvider, ServletContextAware {

    private ServletContext servletContext;


    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "themeService-mailTemplate")
    public String getTemplate(final List<String> mailTemplateChain,
                              final String locale,
                              final String templateName,
                              final String ext) throws IOException {

        for (final String mailTemplate : mailTemplateChain) {
            final String fullFileNameLocale = mailTemplate + templateName + "/" + templateName + "_" + locale + ext;
            final InputStream streamLocale = servletContext.getResourceAsStream(fullFileNameLocale);
            if (streamLocale != null) {
                return IOUtils.toString(streamLocale, "UTF-8");
            }
            final String fullFileName = mailTemplate + templateName + "/" + templateName + ext;
            final InputStream stream = servletContext.getResourceAsStream(fullFileName);
            if (stream != null) {
                return IOUtils.toString(stream, "UTF-8");
            }
        }

        throw new RuntimeException("mail template does not exist: " + templateName + ext);

    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "themeService-mailResource")
    public byte[] getResource(final List<String> mailTemplateChain,
                              final String locale,
                              final String templateName,
                              final String resourceFilename) throws IOException {

        for (final String mailTemplate : mailTemplateChain) {
            final InputStream streamLocale = servletContext.getResourceAsStream(mailTemplate + templateName + "/resources_" + locale + "/" + resourceFilename);
            if (streamLocale != null) {
                return IOUtils.toByteArray(streamLocale);
            }
            final InputStream stream = servletContext.getResourceAsStream(mailTemplate + templateName + "/resources/" + resourceFilename);
            if (stream != null) {
                return IOUtils.toByteArray(stream);
            }
        }

        throw new RuntimeException("mail resource does not exist: " + templateName + "/" + resourceFilename);

    }

    /**
     * {@inheritDoc}
     */
    public void setServletContext(final ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
