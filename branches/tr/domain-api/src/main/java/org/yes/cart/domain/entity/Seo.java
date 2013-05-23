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

package org.yes.cart.domain.entity;

/**
 * Seo interface.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */

public interface Seo {

    /**
     * Get uri
     *
     * @return uri
     */
    String getUri();

    /**
     * Set uri.
     *
     * @param uri uri.
     */
    void setUri(String uri);

    /**
     * Get title.
     *
     * @return title.
     */
    String getTitle();

    /**
     * Set title.
     *
     * @param title title
     */
    void setTitle(String title);

    /**
     * Get meta keywords.
     *
     * @return meta keywords
     */
    String getMetakeywords();

    /**
     * Set meta keywords
     *
     * @param metakeywords meta keywords
     */
    void setMetakeywords(String metakeywords);

    /**
     * Get meta description.
     *
     * @return meta description
     */
    String getMetadescription();

    /**
     * Set meta description.
     *
     * @param metadescription meta description.
     */
    void setMetadescription(String metadescription);

}


