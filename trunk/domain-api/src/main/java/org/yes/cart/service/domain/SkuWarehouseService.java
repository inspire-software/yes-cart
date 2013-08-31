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

import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.domain.entity.SkuWarehouse;
import org.yes.cart.domain.entity.Warehouse;
import org.yes.cart.domain.misc.Pair;

import java.math.BigDecimal;
import java.util.Date;
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
     * @return list of founded {@link SkuWarehouse}
     */
    List<SkuWarehouse> findProductSkusOnWarehouse(long productId, long warehouseId);


    /**
     * Reserve quantity of skus on warehouse. Method returns the reset to reserve if quantity of skus not enough
     * to satisfy this resuest. Example particular shop has two warehouses with 5 and 7 patricular skus,
     * but need to reserve 9 skus. In this case return value will be 4 if first warehouse to reserve was with 5 skus.
     * Second example 10 skus on warehouse and 3 reserver will allow to reserve 7 skus only
     *
     * @param warehouse  warehouse
     * @param productSku sku to reserve
     * @param reserveQty quantity to reserve
     * @return the rest to reserve or BigDecimal.ZERO if was reserved succsessful.
     */
    BigDecimal reservation(Warehouse warehouse, ProductSku productSku, BigDecimal reserveQty);

    /**
     * Credit quantity on warehouse after order cancel.
     *
     * @param warehouse  warehouse
     * @param productSku sku to credit
     * @param voidQty    quantity to credit
     * @return the rest of quantity to adjust on other warehouse, that belong to the same shop. Ten items to
     *         void reservation on warehouse, that has 100 qty and 2 reserved will return 8 and update sku quantity to 100 and 0.
     *         <p/>
     *         If no records was on warehouse ten will be returned, and 10, 0 record will be created
     */
    BigDecimal voidReservation(Warehouse warehouse, ProductSku productSku, BigDecimal voidQty);


    /**
     * Update quantity on warehouse .
     *
     * @param warehouse  warehouse
     * @param productSku sku to reserve
     * @param debitQty   quantity to reserve
     * @return the rest of qty to adjust on other warehouse, that belong to the same shop.
     */
    BigDecimal debit(Warehouse warehouse, ProductSku productSku, BigDecimal debitQty);


    /**
     * Add quantity on warehouse.
     *
     * @param warehouse  warehouse
     * @param productSku sku to
     * @param addQty     quantity to add
     * @return BigDecimal.ZERO
     *         If no records was on warehouse ten will be returned, and 10, 0 record will be created
     */
    BigDecimal credit(Warehouse warehouse, ProductSku productSku, BigDecimal addQty);

    /**
     * Get the sku's Quantity - Reserved quantity pair.
     *
     * @param warehouses list of warehouses where
     * @param productSku sku
     * @return pair of available and reserved quantity
     */
    Pair<BigDecimal, BigDecimal> getQuantity(List<Warehouse> warehouses, ProductSku productSku);


    /**
     * Find product sku record on given warehouse.
     *
     * @param warehouse  given warehouse
     * @param productSku given product sku
     * @return {@link SkuWarehouse} if founf otherwise null.
     */
    SkuWarehouse findByWarehouseSku(Warehouse warehouse, ProductSku productSku);

    /**
     * Check if sku has pre-order availability.
     *
     *
     * @param productSkuId sku warehouse entity
     * @param checkAvailabilityDates check availability date
     *
     * @return true, if sku has pre-order availability (and optionally within availability dates).
     */
    boolean isSkuAvailabilityPreorderOrBackorder(long productSkuId, final boolean checkAvailabilityDates);

    /**
     * Find PK of product sku's inventory of which had changed since given date (i.e. updateDate >= lastUpdate)
     *
     * @param lastUpdate last modification of sku warehouse (inclusive)
     *
     * @return list of PKs
     */
    List<Long> findProductSkuForWhichInventoryChangedAfter(Date lastUpdate);

    /**
     * Push orders , that are awaiting for inventory
     *
     * @param productSkuId product SKU PK
     */
    void updateOrdersAwaitingForInventory(long productSkuId);

}
