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

import org.yes.cart.domain.entity.Product;
import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.domain.entity.SkuWarehouse;
import org.yes.cart.domain.entity.Warehouse;
import org.yes.cart.domain.misc.Pair;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface SkuWarehouseService extends GenericService<SkuWarehouse> {

    /**
     * Find product skus quantity objects on given warehouse.
     *
     * @param productId   given product id
     * @param warehouseId given warehouse id.
     * @return list of founded {@link SkuWarehouse}
     */
    List<SkuWarehouse> findProductSkusOnWarehouse(long productId, long warehouseId);

    /**
     * Find ATS value per product sku for product.
     *
     * @param product product
     * @param warehouses warehouses to consider
     *
     * @return SKU code -> ATS map
     */
    Map<String, BigDecimal> findProductAvailableToSellQuantity(Product product, Collection<Warehouse> warehouses);

    /**
     * Find ATS value per product sku for product.
     *
     * @param productSku product sku
     * @param warehouses warehouses to consider
     *
     * @return SKU code -> ATS map
     */
    Map<String, BigDecimal> findProductSkuAvailableToSellQuantity(ProductSku productSku, Collection<Warehouse> warehouses);

    /**
     * Reserve quantity of skus on warehouse. Method returns the rest to reserve if quantity of skus is not enough
     * to satisfy this request. Example particular shop has two warehouses with 5 and 7 particular skus,
     * but need to reserve 9 skus. In this case return value will be 4 if first warehouse to reserve was with 5 skus.
     * Second example 10 skus on warehouse and 3 reserved will allow to reserve 7 skus only
     *
     * @param warehouse  warehouse
     * @param productSkuCode sku to reserve
     * @param reserveQty quantity to reserve
     * @return the rest to reserve or BigDecimal.ZERO if was reserved successful.
     */
    BigDecimal reservation(Warehouse warehouse, String productSkuCode, BigDecimal reserveQty);

    /**
     * Credit quantity on warehouse after order cancel.
     *
     * @param warehouse  warehouse
     * @param productSkuCode sku to credit
     * @param voidQty    quantity to credit
     * @return the rest of quantity to adjust on other warehouse, that belong to the same shop. Ten items to
     *         void reservation on warehouse, that has 100 qty and 2 reserved will return 8 and update sku quantity to 100 and 0.
     *         <p/>
     *         If no records was on warehouse ten will be returned, and 10, 0 record will be created
     */
    BigDecimal voidReservation(Warehouse warehouse, String productSkuCode, BigDecimal voidQty);


    /**
     * Update quantity on warehouse .
     *
     * @param warehouse  warehouse
     * @param productSkuCode sku to reserve
     * @param debitQty   quantity to reserve
     * @return the rest of qty to adjust on other warehouse, that belong to the same shop.
     */
    BigDecimal debit(Warehouse warehouse, String productSkuCode, BigDecimal debitQty);


    /**
     * Add quantity on warehouse.
     *
     * @param warehouse  warehouse
     * @param productSkuCode sku to credit
     * @param addQty     quantity to add
     * @return BigDecimal.ZERO
     *         If no records was on warehouse ten will be returned, and 10, 0 record will be created
     */
    BigDecimal credit(Warehouse warehouse, String productSkuCode, BigDecimal addQty);

    /**
     * Get the sku's Quantity - Reserved quantity pair.
     *
     * @param warehouses list of warehouses where
     * @param productSkuCode sku code
     * @return pair of available and reserved quantity
     */
    Pair<BigDecimal, BigDecimal> getQuantity(List<Warehouse> warehouses, String productSkuCode);


    /**
     * Find product sku record on given warehouse.
     *
     * @param warehouse  given warehouse
     * @param productSkuCode given product sku
     * @return {@link SkuWarehouse} if found otherwise null.
     */
    SkuWarehouse findByWarehouseSku(Warehouse warehouse, String productSkuCode);

    /**
     * Check if sku has pre-order availability.
     *
     *
     * @param productSkuCode sku warehouse entity
     * @param checkAvailabilityDates check availability date
     *
     * @return true, if sku has pre-order availability (and optionally within availability dates).
     */
    boolean isSkuAvailabilityPreorderOrBackorder(String productSkuCode, final boolean checkAvailabilityDates);

    /**
     * Find PK of product sku's inventory of which had changed since given date (i.e. updateDate >= lastUpdate)
     *
     * @param lastUpdate last modification of sku warehouse (inclusive)
     *
     * @return list of PKs
     */
    List<String> findProductSkuForWhichInventoryChangedAfter(Date lastUpdate);

    /**
     * Push orders , that are awaiting for inventory
     *
     * @param productSkuCode product SKU PK
     */
    void updateOrdersAwaitingForInventory(String productSkuCode);

}
