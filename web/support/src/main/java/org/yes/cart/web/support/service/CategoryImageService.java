package org.yes.cart.web.support.service;

import org.yes.cart.domain.entity.Category;

/**
 *
 * Responsible to get category image, that can depends from particular strategy and can be load balanced.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/4/11
 * Time: 6:38 PM
 */
public interface CategoryImageService {

    /**
     *
     * Get the context image with image servlet url
     * and specified parameters.
     *
     * @param category  given category
     * @param httpServletContextPath http servlet request path
     * @return context image url.
     */
    String getCategoryImage(Category category,
                            String httpServletContextPath);

    /**
     *
     * Get the context image with image servlet url
     * and specified parameters.
     *
     * @param category  given category
     * @param httpServletContextPath http servlet request path
     * @param width image width
     * @param height image height.
     * @return context image url.
     */
    String getCategoryImage(Category category,
                            String httpServletContextPath,
                            String width,
                            String height);

}
