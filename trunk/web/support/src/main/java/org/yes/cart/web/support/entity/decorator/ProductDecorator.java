package org.yes.cart.web.support.entity.decorator;

import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.entity.Product;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/13/11
 * Time: 9:39 PM
 */
public interface ProductDecorator extends Product {

    /**
     * Get product image.
     * @return category image url, depending from strategy.
     */
    String getProductImage();

    /**
     * Get product image with give width and height.
     * @param width image width to get correct url
     * @param height image height to get correct url
     *
     * @return product image url, depending from strategy.
     */
    String getProductImage(String width, String height);

    /**
     * Get product image width in particular category.
     * @param category optional given category
     * @return  image width.
     */
    String getProductImageWidth(Category category);

    /**
     * Get product image height in partucular category.
     * @param category given category
     * @return     image height.
     */
    String getProductImageHeight(Category category);

}
