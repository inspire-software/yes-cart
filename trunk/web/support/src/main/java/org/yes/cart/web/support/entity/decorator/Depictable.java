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

package org.yes.cart.web.support.entity.decorator;

import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.entity.SeoImage;
import org.yes.cart.domain.misc.Pair;

import java.util.List;

/**
 * Depictable interface for sku, product , brands, etc.
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/13/11
 * Time: 9:39 PM
 */
public interface Depictable {

    /**
     * Get pair of images attributes name and image file name for given language.
     *
     * @param lang language
     *
     * @return images attributes names.
     */
    List<Pair<String, String>> getImageAttributeFileNames(String lang);



    /**
     * Get product image with give width and height.
     *
     * @param width image width to get correct url
     * @param height image height to get correct url
     * @param imageAttributeName particular attribute name.
     * @param lang language
     *
     * @return product image url, depending from strategy.
     */
    String getImage(String width, String height, String imageAttributeName, String lang);


    /**
     * Get product image with give width and height.
     *
     * @param width image width to get correct url
     * @param height image height to get correct url
     * @param lang language
     *
     * @return product image url, depending from strategy.
     */
    String getDefaultImage(String width, String height, String lang);


    /**
     * Get width and height of depictable in given category.
     * @param category given category.
     * @return width and height of depictable
     */
    String [] getDefaultImageSize(Category category);

    /**
     * Get width and height of depictable in given category.
     * @param category given category.
     * @return width and height of depictable
     */
    String [] getThumbnailImageSize(Category category);


    /**
     * Get image seo information.
     * @param fileName image filename.
     * @return {@link SeoImage}  in cae if it present for given filename, otherwise null
     */
    SeoImage getSeoImage(String fileName);

}
