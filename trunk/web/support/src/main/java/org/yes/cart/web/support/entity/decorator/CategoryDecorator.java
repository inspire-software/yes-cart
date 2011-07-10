package org.yes.cart.web.support.entity.decorator;

import org.yes.cart.domain.entity.Category;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/4/11
 * Time: 8:16 PM
 */
public interface CategoryDecorator  extends Category {

    /**
     * Get category image.
     * @return category image url, depending from strategy.
     */
    String getCategoryImage();

}
