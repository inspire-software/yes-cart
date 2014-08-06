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

package org.yes.cart.domain.dto;

import org.yes.cart.domain.entity.Identifiable;

import java.util.Map;

/**
 * Image SEO DTO interface.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface SeoImageDTO extends Identifiable {

    /**
     * Get image name.
     *
     * @return image name.
     */
    String getImageName();

    /**
     * Set image name.
     *
     * @param imageName image name.
     */
    void setImageName(String imageName);


    /**
     * Get alternative text.
     *
     * @return alternative text
     */
    String getAlt();


    /**
     * Set alternative text.
     *
     * @param alt
     */
    void setAlt(String alt);

    /**
     * Display alt.
     *
     * @return localised locale => name pairs.
     */
    Map<String, String> getDisplayAlts();

    /**
     * Set display alt
     *
     * @param alts localised locale => name pairs
     */
    void setDisplayAlts(Map<String, String> alts);

    /**
     * Get image title.
     *
     * @return image title
     */
    String getTitle();

    /**
     * Set image title.
     *
     * @param title
     */
    void setTitle(String title);

    /**
     * Display title.
     *
     * @return localised locale => name pairs.
     */
    Map<String, String> getDisplayTitles();

    /**
     * Set display title
     *
     * @param titles localised locale => name pairs
     */
    void setDisplayTitles(Map<String, String> titles);

    /**
     * Get pk value.
     *
     * @return pk value
     */
    long getSeoImageId();

    /**
     * Set pk value.
     *
     * @param seoImageId pk value.
     */
    void setSeoImageId(long seoImageId);

}
