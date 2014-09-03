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

package org.yes.cart.service.mail;

import java.io.IOException;
import java.util.List;

/**
 * Proxy to accessing mail resources on FS. This provider is responsible for
 * retrieval of generic resources to be made available for the mail composer.
 * Rationale is that we can cache this.
 *
 * User: denispavlov
 * Date: 03/09/2014
 * Time: 14:53
 */
public interface MailTemplateResourcesProvider {

    /**
     * Get template as string.
     *
     * @param mailTemplateChain path to template folder
     * @param locale            locale
     * @param templateName      template name
     * @param ext               file extension
     *
     * @return template if exists
     *
     * @throws java.io.IOException in case of io errors.
     */
    String getTemplate(final List<String> mailTemplateChain,
                       final String locale,
                       final String templateName,
                       final String ext) throws IOException;

    /**
     * Get template as string.
     *
     * @param mailTemplateChain path to template folder
     * @param locale            locale
     * @param templateName      template name
     * @param resourceFilename  file name for the resource
     *
     * @return template if exists
     *
     * @throws java.io.IOException in case of io errors.
     */
    byte[] getResource(final List<String> mailTemplateChain,
                       final String locale,
                       final String templateName,
                       final String resourceFilename) throws IOException;


}
