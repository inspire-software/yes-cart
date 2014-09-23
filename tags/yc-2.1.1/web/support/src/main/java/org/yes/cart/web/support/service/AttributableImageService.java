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

package org.yes.cart.web.support.service;


import org.yes.cart.domain.entity.Attributable;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 8/8/11
 * Time: 2:30 PM
 */
public interface AttributableImageService {


    /**
     * Get the context image with image servlet url
     * and specified parameters.
     *
     * @param attributable                given attributable
     * @param httpServletContextPath http servlet request path
     * @param width                  image width
     * @param height                 image height.
     * @param attrName given image attribute name
     * @param attrVal optional attrValue, if it not provided service will try to get value from attributes
     * @return default context image url.
     */
    String getImage(Attributable attributable,
                    String httpServletContextPath,
                    String width,
                    String height,
                    String attrName,
                    String attrVal);

    /**
     * Get default image uri.
     *
     * @param imageName          name of image
     * @param width              image width
     * @param height             image height
     * @param servletContextPath http servlet request
     * @param object             product/sku/category
     * @return image uri.
     */
    String getImageURI(final String imageName,
                              final String width,
                              final String height,
                              final String servletContextPath,
                              final Object object);

}
