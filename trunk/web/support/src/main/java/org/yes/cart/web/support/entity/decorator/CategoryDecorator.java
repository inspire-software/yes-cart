package org.yes.cart.web.support.entity.decorator;

import org.yes.cart.domain.entity.Category;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/4/11
 * Time: 8:16 PM
 */
public interface CategoryDecorator  extends Category, Depictable {

    /**
     * Get category image.
     * @return category image url, depending from strategy.
     */
    //String getCategoryImage();

    /**
     * Get category image with give width and height.
     * @param width image width to get correct url
     * @param height image height to get correct url
     *
     * @return category image url, depending from strategy.
     */
    //String getCategoryImage(String width, String height);

    /**
     * Get category image width.
     * @return  image width.
     */
    //String getCategoryImageWidth();

    /**
     * Get category image height.
     * @return     image height.
     * */
    //String getCategoryImageHeight();


}
