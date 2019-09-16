/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
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

import org.yes.cart.domain.entity.SkuWarehouse;
import org.yes.cart.domain.entity.Warehouse;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

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
     *
     * @return list of founded {@link SkuWarehouse}
     */
    List<SkuWarehouse> getProductSkusOnWarehouse(long productId, long warehouseId);

    /**
     * Reserve quantity of skus on warehouse. Method returns the rest to reserve if quantity of skus is not enough
     * to satisfy this request. Example particular shop has two warehouses with 5 and 7 particular skus,
     * but need to reserve 9 skus. In this case return value will be 4 if first warehouse to reserve was with 5 skus.
     * Second example 10 skus on warehouse and 3 reserved will allow to reserve 7 skus only
     *
     * @param warehouse         warehouse
     * @param productSkuCode    sku to reserve
     * @param reserveQty        quantity to reserve
     *
     * @return the rest to reserve or BigDecimal.ZERO if was reserved successful.
     */
    BigDecimal reservation(Warehouse warehouse, String productSkuCode, BigDecimal reserveQty);

    /**
     * Reserve quantity of skus on warehouse. Method returns the rest to reserve if quantity of skus is not enough
     * to satisfy this request. Example particular shop has two warehouses with 5 and 7 particular skus,
     * but need to reserve 9 skus. In this case return value will be 4 if first warehouse to reserve was with 5 skus.
     * Second example 10 skus on warehouse and 3 reserved will allow to reserve 7 skus only
     *
     * @param warehouse         warehouse
     * @param productSkuCode    sku to reserve
     * @param reserveQty        quantity to reserve
     * @param allowBackorder    true indicates that we allow over-reservation as this is backorderable sku, false indicates
     *                          that reserve will not exceed inventory.
     *
     * @return the rest to reserve or BigDecimal.ZERO if was reserved successful.
     */
    BigDecimal reservation(Warehouse warehouse, String productSkuCode, BigDecimal reserveQty, boolean allowBackorder);

    /**
     * Credit quantity on warehouse after order cancel.
     *
     * @param warehouse         warehouse
     * @param productSkuCode    sku to credit
     * @param voidQty           quantity to credit
     *
     * @return the rest of quantity to adjust on other warehouse, that belong to the same shop. Ten items to
     *         void reservation on warehouse, that has 100 qty and 2 reserved will return 8 and update sku quantity to 100 and 0.
     *         <p/>
     *         If no records was on warehouse ten will be returned, and 10, 0 record will be created
     */
    BigDecimal voidReservation(Warehouse warehouse, String productSkuCode, BigDecimal voidQty);


    /**
     * Update quantity on warehouse .
     *
     * @param warehouse         warehouse
     * @param productSkuCode    sku to reserve
     * @param debitQty          quantity to reserve
     *
     * @return remainder of unallocated quantity.
     */
    BigDecimal debit(Warehouse warehouse, String productSkuCode, BigDecimal debitQty);


    /**
     * Add quantity on warehouse.
     *
     * @param warehouse         warehouse
     * @param productSkuCode    sku to credit
     * @param addQty            quantity to add
     *
     * @return BigDecimal.ZERO
     *         If no records was on warehouse ten will be returned, and 10, 0 record will be created
     */
    BigDecimal credit(Warehouse warehouse, String productSkuCode, BigDecimal addQty);


    /**
     * Find product sku record on given warehouse.
     *
     * @param warehouse  given warehouse
     * @param productSkuCode given product sku
     * @return {@link SkuWarehouse} if found otherwise null.
     */
    SkuWarehouse findByWarehouseSku(Warehouse warehouse, String productSkuCode);

    /**
     * Find PK of product sku's inventory of which had changed since given date (i.e. updateDate >= lastUpdate)
     *
     * @param lastUpdate last modification of sku warehouse (inclusive)
     *
     * @return list of PKs
     */
    List<String> findProductSkuForWhichInventoryChangedAfter(Instant lastUpdate);

    /**
     * Get product SKU for products with availableto < before
     *
     * @param before before date
     * @return product SKU if found otherwise null
     */
    List<String> findProductSkuByUnavailableBefore(LocalDateTime before);

}
