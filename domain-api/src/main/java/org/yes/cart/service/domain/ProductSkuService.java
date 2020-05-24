/*
 * Copyright 2009 Inspire-Software.com
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

import org.yes.cart.domain.dto.ProductSkuSearchResultPageDTO;
import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.domain.entity.SkuPrice;
import org.yes.cart.domain.entity.SkuWarehouse;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.search.dto.NavigationContext;

import java.util.Collection;
import java.util.List;
import java.util.Map;

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
     * @param navigationContext         navigation context
     *
     * @return list of products SKU
     */
    ProductSkuSearchResultPageDTO getProductSkuSearchResultDTOByQuery(NavigationContext navigationContext);


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
     * Remove all product options.
     *
     * @param productId product pk value
     */
    void removeAllOptions(long productId);

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

    /**
     * Remove all items for wishlists for given sku
     *
     * @param sku  given sku.
     */
    void removeAllWishLists(final ProductSku sku);

    /**
     * Remove all items options for given sku
     *
     * @param sku  given sku.
     */
    void removeAllOptions(final ProductSku sku);


    /**
     * Find SKU by given search criteria. Search will be performed using like operation.
     *
     * @param start             start
     * @param offset            page size
     * @param sort              optional sort property
     * @param sortDescending    optional sort property direction
     * @param filter            optional filters (e.g. name, guid)
     *
     * @return list of SKU.
     */
    List<ProductSku> findProductSkus(int start,
                                     int offset,
                                     String sort,
                                     boolean sortDescending,
                                     Map<String, List> filter);

    /**
     * Find SKU by given search criteria. Search will be performed using like operation.
     *
     * @param filter            optional filters (e.g. name, guid)
     *
     * @return count
     */
    int findProductSkuCount(Map<String, List> filter);



}
