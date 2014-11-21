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

package org.yes.cart.web.support.service;

import org.apache.lucene.search.Query;
import org.yes.cart.domain.dto.ProductSearchResultDTO;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.misc.Pair;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 20/11/2014
 * Time: 19:53
 */
public interface ProductServiceFacade {

    /**
     * Get SKU with attributes (better caching).
     *
     * @param skuId SKU
     *
     * @return SKU with attributes
     */
    ProductSku getSkuById(Long skuId);

    /**
     * Get Product with attributes (better caching).
     *
     * @param productId PK
     *
     * @return product with attributes
     */
    Product getProductById(Long productId);

    /**
     * Get product sku by code.
     *
     * @param skuCode given sku code.
     * @return product sku if found, otherwise null
     */
    ProductSku getProductSkuBySkuCode(String skuCode);


    /**
     * Get the grouped product attributes, with values. The result can be represented in following form:
     * Shipment details:
     * weight: 17 Kg
     * length: 15 Cm
     * height: 20 Cm
     * width: 35 Cm
     * Power:
     * Charger: 200/110
     * Battery type: Lithium
     *
     * So the hierarchy returned for the above example will be:
     * Map
     *    Entry[1001, Shipment details] =>
     *      Map
     *          Entry [10010, weight] =>
     *              List
     *                  [100001, 17 Kg]
     *          Entry [10011, length] =>
     *              List
     *                  [100002, 15 cm]
     *  ... etc
     *
     *  If this is SKU then it should inherit the attributes of the product,
     *  If this is just product then we only display product attributes
     *
     * @param locale locale
     * @param productId  product ID
     * @param skuId sku ID
     * @param productTypeId product type id
     * @return hierarchy of attributes for this product or sku.
     */
    Map<Pair<String, String>, Map<Pair<String, String>, List<Pair<String, String>>>> getProductAttributes(String locale,
                                                                                                          long productId,
                                                                                                          long skuId,
                                                                                                          long productTypeId);


    /**
     * Get all product associations by association type.
     *
     * @param productId       product primary key
     * @param shopId          current shop
     * @param associationType association code [up, cross, etc]
     *
     * @return list of product associations
     */
    List<ProductSearchResultDTO> getProductAssociations(long productId, long shopId, String associationType);

    /**
     * Get featured products for given category. Limit is set by the category.
     *
     * @param categoryId      category (optional)
     * @param shopId          current shop
     *
     * @return list of featured products
     */
    List<ProductSearchResultDTO> getFeaturedProducts(long categoryId, long shopId);

    /**
     * Get new products for given category. Limit is set by the category.
     *
     * @param categoryId      category (optional)
     * @param shopId          current shop
     *
     * @return list of new products
     */
    List<ProductSearchResultDTO> getNewProducts(long categoryId, long shopId);

    /**
     * Get new products for given category. Limit is set by the category.
     *
     * @param productIds      list of products
     * @param categoryId      category (optional), specify -1 for no limit
     * @param shopId          current shop
     *
     * @return list of new products
     */
    List<ProductSearchResultDTO> getListProducts(List<String> productIds, long categoryId, long shopId);

    /**
     * Get the all products , that match the given query
     *
     * @param query         lucene query
     * @param firstResult   index of first result
     * @param maxResults    quantity results to return
     * @param sortFieldName sort field name (specify null for no sorting)
     * @param descendingSort sort the search result in reverse if true
     * @return list of products
     */
    List<ProductSearchResultDTO> getListProducts(Query query,
                                                 int firstResult,
                                                 int maxResults,
                                                 String sortFieldName,
                                                 boolean descendingSort);

    /**
     * Get the quantity of products in particular category.
     *
     * @param query lucene query
     *
     * @return quantity of products
     */
    int getListProductsCount(Query query);


    /**
     * Get product availability.
     *
     * @param product product
     * @param shopId  current shop
     *
     * @return availability model
     */
    ProductAvailabilityModel getProductAvailability(ProductSearchResultDTO product, long shopId);

    /**
     * Get product availability.
     *
     * @param product product
     * @param shopId  current shop
     *
     * @return availability model
     */
    ProductAvailabilityModel getProductAvailability(Product product, long shopId);

    /**
     * Get product availability.
     *
     * @param product product
     * @param shopId  current shop
     *
     * @return availability model
     */
    ProductAvailabilityModel getProductAvailability(ProductSku product, long shopId);

    /**
     * Quantity model.
     *
     * @param cartQty quantity of given sku in cart
     * @param product required product
     *
     * @return quantity model
     */
    ProductQuantityModel getProductQuantity(BigDecimal cartQty, Product product);

    /**
     * Quantity model.
     *
     * @param cartQty quantity of given sku in cart
     * @param product required product
     *
     * @return quantity model
     */
    ProductQuantityModel getProductQuantity(BigDecimal cartQty, ProductSku product);

    /**
     * Quantity model.
     *
     * @param cartQty quantity of given sku in cart
     * @param product required product
     *
     * @return quantity model
     */
    ProductQuantityModel getProductQuantity(BigDecimal cartQty, ProductSearchResultDTO product);


    /**
     * Get currently active SKU price (or blank object).
     *
     * @param productId product id (optional)
     * @param skuCode   selected SKU (optional)
     * @param quantity  quantity tier
     * @param currency  currency
     * @param shopId    current shop
     *
     * @return active product/SKU price (or blank object)
     */
    SkuPrice getSkuPrice(final Long productId, final String skuCode, final BigDecimal quantity, String currency, long shopId);

    /**
     * Get prices for all SKU quantity tiers.
     *
     * @param productId product id (optional)
     * @param skuCode   selected SKU (optional)
     * @param currency  currency
     * @param shopId    current shop
     *
     * @return active product/SKU prices (or blank object)
     */
    Collection<SkuPrice> getSkuPrices(final Long productId, final String skuCode, String currency, long shopId);

}
