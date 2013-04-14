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

package org.yes.cart.service.domain.impl;

import org.hibernate.criterion.Restrictions;
import org.yes.cart.constants.Constants;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.domain.entity.SkuWarehouse;
import org.yes.cart.domain.entity.Warehouse;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.domain.SkuWarehouseService;
import org.yes.cart.util.MoneyUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class SkuWarehouseServiceImpl extends BaseGenericServiceImpl<SkuWarehouse> implements SkuWarehouseService {


    /**
     * Construct sku warehouse service.
     *
     * @param genericDao     dao to use.
     */
    public SkuWarehouseServiceImpl(final GenericDAO<SkuWarehouse, Long> genericDao) {
        super(genericDao);
    }

    /**
     * {@inheritDoc}
     */
    public List<SkuWarehouse> findProductSkusOnWarehouse(final long productId, final long warehouseId) {
        return getGenericDao().findByNamedQuery(
                "SKUS.ON.WAREHOUSE",
                productId,
                warehouseId);
    }


    /**
     * Get the sku's Quantity - Reserved quantity pair.
     *
     * @param warehouses list of warehouses where
     * @param productSku sku
     * @return pair of available and reserved quantity
     */
    public Pair<BigDecimal, BigDecimal> getQuantity(final List<Warehouse> warehouses, final ProductSku productSku) {

        final List<Object> warehouseIdList = new ArrayList<Object>(warehouses.size());
        for (Warehouse wh : warehouses) {
            warehouseIdList.add(wh.getWarehouseId());
        }

        final List rez = getGenericDao().findQueryObjectsByNamedQueryWithList(
                "SKU.QTY.ON.WAREHOUSES",
                warehouseIdList,
                productSku.getCode()
        );

        BigDecimal quantity = BigDecimal.ZERO.setScale(Constants.DEFAULT_SCALE);
        BigDecimal reserved = BigDecimal.ZERO.setScale(Constants.DEFAULT_SCALE);

        if (!rez.isEmpty()) {
            final Object obj[] = (Object[]) rez.get(0);
            if (obj.length > 0 && obj[0] != null) {
                quantity = ((BigDecimal) obj[0]).setScale(Constants.DEFAULT_SCALE);
            }
            if (obj.length > 1 && obj[1] != null) {
                reserved = ((BigDecimal) obj[1]).setScale(Constants.DEFAULT_SCALE);
            }
        }

        return new Pair<BigDecimal, BigDecimal>(quantity, reserved);

    }


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
    public BigDecimal reservation(final Warehouse warehouse, final ProductSku productSku, final BigDecimal reserveQty) {

        final SkuWarehouse skuWarehouse = findByWarehouseSku(warehouse, productSku);

        if (skuWarehouse == null) {
            return reserveQty.setScale(Constants.DEFAULT_SCALE);
        } else {
            BigDecimal canReserve = skuWarehouse.getQuantity().subtract(
                    MoneyUtils.notNull(skuWarehouse.getReserved(), BigDecimal.ZERO.setScale(Constants.DEFAULT_SCALE)));

            BigDecimal rest = canReserve.subtract(reserveQty);
            if (MoneyUtils.isFirstBiggerThanOrEqualToSecond(rest, BigDecimal.ZERO)) {
                skuWarehouse.setReserved(
                        MoneyUtils.notNull(skuWarehouse.getReserved(), BigDecimal.ZERO.setScale(Constants.DEFAULT_SCALE)).add(reserveQty));
                update(skuWarehouse);
                return BigDecimal.ZERO.setScale(Constants.DEFAULT_SCALE);
            } else {
                skuWarehouse.setReserved(skuWarehouse.getQuantity());
                update(skuWarehouse);
                return rest.abs().setScale(Constants.DEFAULT_SCALE);
            }
        }


    }


    /**
     * {@inheritDoc}
     */
    public BigDecimal voidReservation(final Warehouse warehouse, final ProductSku productSku, final BigDecimal voidQty) {
        final SkuWarehouse skuWarehouse = findByWarehouseSku(warehouse, productSku);

        if (skuWarehouse == null) {
            return voidQty.setScale(Constants.DEFAULT_SCALE);
        } else {
            BigDecimal canVoid = MoneyUtils.notNull(skuWarehouse.getReserved(), BigDecimal.ZERO.setScale(Constants.DEFAULT_SCALE)).min(voidQty);
            BigDecimal rest = MoneyUtils.notNull(skuWarehouse.getReserved(), BigDecimal.ZERO.setScale(Constants.DEFAULT_SCALE)).subtract(voidQty);
            skuWarehouse.setReserved(MoneyUtils.notNull(skuWarehouse.getReserved(), BigDecimal.ZERO.setScale(Constants.DEFAULT_SCALE)).subtract(canVoid));
            update(skuWarehouse);
            if (MoneyUtils.isFirstBiggerThanOrEqualToSecond(rest, BigDecimal.ZERO)) {
                return BigDecimal.ZERO.setScale(Constants.DEFAULT_SCALE);
            } else {
                return rest.abs().setScale(Constants.DEFAULT_SCALE);
            }

        }
    }


    /**
     * {@inheritDoc}
     */
    public BigDecimal credit(final Warehouse warehouse, final ProductSku productSku, final BigDecimal addQty) {
        final SkuWarehouse skuWarehouse = findByWarehouseSku(warehouse, productSku);

        if (skuWarehouse == null) {
            final SkuWarehouse newSkuWarehouse = getGenericDao().getEntityFactory().getByIface(SkuWarehouse.class);
            newSkuWarehouse.setQuantity(addQty);
            newSkuWarehouse.setSku(productSku);
            newSkuWarehouse.setWarehouse(warehouse);
            create(newSkuWarehouse);
        } else {
            skuWarehouse.setQuantity(skuWarehouse.getQuantity().add(addQty));
            update(skuWarehouse);
        }
        return BigDecimal.ZERO;

    }


    /**
     * Debit (decrease) quantity of given sku on particular warehouse.
     *
     * @param warehouse  warehouse
     * @param productSku sku to debit
     * @param debitQty   quantity to debit
     * @return the rest of qty to adjust on other warehouse, that belong to the same shop.
     */
    public BigDecimal debit(final Warehouse warehouse, final ProductSku productSku, final BigDecimal debitQty) {

        final SkuWarehouse skuWarehouse = findByWarehouseSku(warehouse, productSku);

        if (skuWarehouse == null) {
            return debitQty.setScale(Constants.DEFAULT_SCALE);
        } else {
            BigDecimal canDebit = skuWarehouse.getQuantity().min(debitQty);
            BigDecimal rest = skuWarehouse.getQuantity().subtract(debitQty);
            skuWarehouse.setQuantity(skuWarehouse.getQuantity().subtract(canDebit));
            update(skuWarehouse);
            if (MoneyUtils.isFirstBiggerThanOrEqualToSecond(BigDecimal.ZERO, rest)) {
                return rest.abs().setScale(Constants.DEFAULT_SCALE);
            } else {
                return BigDecimal.ZERO.setScale(Constants.DEFAULT_SCALE);
            }
        }

    }


    /**
     * Find product sku record on given warehouse.
     *
     * @param warehouse  given warehouse
     * @param productSku given product sku
     * @return {@link SkuWarehouse} if founf otherwise null.
     */
    public SkuWarehouse findByWarehouseSku(final Warehouse warehouse, final ProductSku productSku) {
        return getGenericDao().findSingleByCriteria(
                Restrictions.eq("warehouse", warehouse),
                Restrictions.eq("sku", productSku)
        );
    }


}
