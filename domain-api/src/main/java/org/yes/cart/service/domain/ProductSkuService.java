package org.yes.cart.service.domain;

import org.yes.cart.domain.entity.ProductSku;

import java.util.Collection;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface ProductSkuService extends GenericService<ProductSku> {

    /**
     * Get all product SKUs.
     * @param productId product id
     * @return list of product skus.
     */
    Collection<ProductSku> getAllProductSkus(long productId);


    /**
     * Get product sku by code.
     * @param skuCode given sku code.
     * @return product sku if found, otherwise null
     */
    ProductSku getProductSkuBySkuCode(String skuCode);


}
