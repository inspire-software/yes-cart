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

package org.yes.cart.web.support.seo;

import java.io.InputStream;

/**
 * User: denispavlov
 * Date: 13-08-28
 * Time: 3:44 PM
 */
public interface SitemapXmlService {

    /**
     * Generate sitemap XML file.
     *
     * @param shopCode code of the shop to generate sitemap.xml for
     *
     * @return XML
     */
    String generateSitemapXml(String shopCode);

    /**
     * Generate sitemap XML file.
     *
     * @param shopCode code of the shop to generate sitemap.xml for
     *
     * @return XML
     */
    InputStream generateSitemapXmlStream(String shopCode);

    /**
     * Generate sitemap XML file and store it in a designated location for later retrieval.
     *
     * Use {@link #retrieveSitemapXmlStream(String)} to retirieve generated file.
     *
     * @param shopCode code of the shop to generate sitemap.xml for
     *
     * @return true if file was successfuly generated and is present
     */
    boolean generateSitemapXmlAndRetain(String shopCode);


    /**
     * Generate or retrieve pre-generated sitemap XML file.
     *
     * @param shopCode code of the shop to generate sitemap.xml for
     *
     * @return XML
     */
    InputStream retrieveSitemapXmlStream(String shopCode);

}
