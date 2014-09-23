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
 * Search engine optimizations on images.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface SeoImage extends Auditable {

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
     * @param alt alternative text
     */
    void setAlt(String alt);

    /**
     * Get alternative text.
     *
     * @return alternative text
     */
    String getDisplayAlt();


    /**
     * Set alternative text.
     *
     * @param alt alternative text
     */
    void setDisplayAlt(String alt);

    /**
     * Get image title.
     *
     * @return image title
     */
    String getTitle();

    /**
     * Set image title.
     *
     * @param title image title.
     */
    void setTitle(String title);

    /**
     * Get image title.
     *
     * @return image title
     */
    String getDisplayTitle();

    /**
     * Set image title.
     *
     * @param title image title.
     */
    void setDisplayTitle(String title);

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
