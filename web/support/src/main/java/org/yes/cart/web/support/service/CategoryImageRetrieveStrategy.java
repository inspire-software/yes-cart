package org.yes.cart.web.support.service;

import org.yes.cart.domain.entity.Category;


/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 2011-May-17
 * Time: 13:02:49
 */
public interface CategoryImageRetrieveStrategy {

    /**
     * Get the image name
     *
     * @param category category
     * @return image name to show as category image
     */
    String getImageName(Category category);

    /**
     * Get image repository url pattern.
     *
     * @return image repository url pattern
     */
    String getImageRepositoryUrlPattern();

}
