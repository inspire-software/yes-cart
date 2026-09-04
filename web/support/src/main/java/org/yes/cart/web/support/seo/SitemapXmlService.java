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
     * Use {@link #retrieveSitemapXmlStream(String)} to retrieve generated file.
     *
     * @param shopCode code of the shop to generate sitemap.xml for
     *
     * @return true if file was successfully generated and is present
     */
    boolean generateSitemapXmlAndRetain(String shopCode);

    /**
     * Generate sitemap XML file and store it in a designated location for later retrieval.
     *
     * Use {@link #retrieveSitemapXmlStream(String)} to retrieve generated file.
     *
     * @param shopCode code of the shop to generate sitemap.xml for
     * @param multipart splits sitemap into main file (home, categories and content) and product file(s)
     * @param maxProducts maximum number of product URLs in one file
     *
     * @return true if file was successfully generated and is present
     */
    boolean generateSitemapXmlAndRetain(String shopCode, boolean multipart, int maxProducts);


    /**
     * Generate or retrieve pre-generated sitemap XML file.
     *
     * @param shopCode code of the shop to generate sitemap.xml for
     *
     * @return XML
     */
    InputStream retrieveSitemapXmlStream(String shopCode);


    /**
     * Generate or retrieve pre-generated sitemap XML file.
     *
     * @param shopCode code of the shop to generate sitemap.xml for
     * @param suffix suffix for multipart sitemap ("main" or "product-1", "product-2" ... "product-N")
     *
     * @return XML
     */
    InputStream retrieveSitemapXmlStream(String shopCode, String suffix);

}
