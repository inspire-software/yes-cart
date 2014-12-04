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

package org.yes.cart.service.domain;

import org.apache.lucene.search.Query;
import org.yes.cart.domain.dto.ProductSkuSearchResultDTO;
import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.domain.entity.SkuPrice;
import org.yes.cart.domain.entity.SkuWarehouse;
import org.yes.cart.domain.misc.Pair;

import java.util.Collection;
import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface ProductSkuService extends GenericService<ProductSku> {

    /**
     * Get all product SKUs.
     *
     * @param productId product id
     * @return list of product skus.
     */
    Collection<ProductSku> getAllProductSkus(long productId);


    /**
     * Get product sku by code.
     *
     * @param skuCode given sku code.
     * @return product sku if found, otherwise null
     */
    ProductSku findProductSkuBySkuCode(String skuCode);

    /**
     * Get product sku by code.
     *
     * @param skuCode given sku code.
     * @return product sku if found, otherwise null
     */
    ProductSku getProductSkuBySkuCode(String skuCode);


    /**
     * Get the all products SKU, that match the given query
     *
     * @param query         lucene query
     *
     * @return list of products SKU
     */
    List<ProductSkuSearchResultDTO> getProductSkuSearchResultDTOByQuery(Query query);


    /**
     * Get all sku prices for all shops.
     *
     * @param productId product pk value
     */
    List<Pair<String, SkuPrice>> getAllPrices(long productId);

    /**
     * Get all sku inventory for all shops.
     *
     * @param productId product pk value
     */
    List<Pair<String, SkuWarehouse>> getAllInventory(long productId);

    /**
     * Remove all sku prices from all shops.
     *
     * @param productId product pk value
     */
    void removeAllPrices(long productId);

    /**
     * Remove from all warehouses.
     *
     * @param productId product pk value
     */
    void removeAllInventory(long productId);

    /**
     * Remove all prices for given sku.
     *
     * @param sku  given sku.
     */
    void removeAllPrices(final ProductSku sku);

    /**
     * Remove all items for warehouse for given sku
     *
     * @param sku  given sku.
     */
    void removeAllInventory(final ProductSku sku);


}
