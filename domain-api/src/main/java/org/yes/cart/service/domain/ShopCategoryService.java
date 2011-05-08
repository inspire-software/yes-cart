package org.yes.cart.service.domain;

import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.entity.ShopCategory;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface ShopCategoryService extends GenericService<ShopCategory> {

    /**
     * Delete all relation between shops and given category
     *
     * @param caterogy category pk
     */
    void deleteAll(Category caterogy);

}
