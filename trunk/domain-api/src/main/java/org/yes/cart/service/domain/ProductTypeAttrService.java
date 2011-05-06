package org.yes.cart.service.domain;

import org.yes.cart.domain.entity.ProductTypeAttr;

import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface ProductTypeAttrService extends GenericService<ProductTypeAttr>{

    /**
     * Get the list of {@link ProductTypeAttr} , that belong to product type.
     * @param productTypeId given product ype id
     * @return list of {@link ProductTypeAttr}
     */
    List<ProductTypeAttr> getByProductTypeId(long productTypeId);

}
