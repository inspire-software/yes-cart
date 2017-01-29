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

package org.yes.cart.service.domain.impl;

import org.apache.commons.collections.CollectionUtils;
import org.yes.cart.constants.Constants;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.Product;
import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.domain.entity.SkuWarehouse;
import org.yes.cart.domain.entity.Warehouse;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.service.domain.SkuWarehouseService;
import org.yes.cart.util.DomainApiUtils;
import org.yes.cart.util.MoneyUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class SkuWarehouseServiceImpl extends BaseGenericServiceImpl<SkuWarehouse> implements SkuWarehouseService {

    private static final BigDecimal ZERO = BigDecimal.ZERO.setScale(Constants.DEFAULT_SCALE, RoundingMode.HALF_UP);

    private ProductService productService;

    /**
     * Construct sku warehouse service.
     *
     * @param genericDao dao to use.
     */
    public SkuWarehouseServiceImpl(
            final GenericDAO<SkuWarehouse, Long> genericDao) {
        super(genericDao);
    }

    /**
     * {@inheritDoc}
     */
    public List<SkuWarehouse> getProductSkusOnWarehouse(final long productId, final long warehouseId) {
        final Product product = productService.getProductById(productId, true);
        final Set<String> skus = new HashSet<String>();
        for (final ProductSku productSku : product.getSku()) {
            skus.add(productSku.getCode());
        }
        return getGenericDao().findByNamedQuery(
                "SKUS.ON.WAREHOUSE.IN.SKUCODE.WAREHOUSEID",
                skus,
                warehouseId);
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, BigDecimal> getProductAvailableToSellQuantity(final long productId, final Collection<Warehouse> warehouses) {

        final Product product = productService.getProductById(productId, true);

        final Map<String, BigDecimal> qty = new HashMap<String, BigDecimal>();
        final Set<String> skuCodes = new HashSet<String>();
        for (final ProductSku sku : product.getSku()) {
            qty.put(sku.getCode(), BigDecimal.ZERO);
            skuCodes.add(sku.getCode());
        }

        if (qty.isEmpty() || CollectionUtils.isEmpty(warehouses)) {
            return qty;
        }

        final List<Long> whIds = new ArrayList<Long>();
        for (final Warehouse wh : warehouses) {
            whIds.add(wh.getWarehouseId());
        }

        final List<Object[]> skuQtyList = getGenericDao().findQueryObjectsByNamedQuery(
                "PRODUCT.SKU.QTY.ON.WAREHOUSES.IN.SKUCODE.IN.WAREHOUSEID",
                skuCodes,
                whIds);

        for (final Object[] skuQty : skuQtyList) {
            final String skuCode = (String) skuQty[0];
            final BigDecimal stock = (BigDecimal) skuQty[1];
            final BigDecimal reserved = (BigDecimal) skuQty[2];

            BigDecimal total = qty.get(skuCode);
            qty.put(skuCode, total.add(MoneyUtils.notNull(stock)).subtract(MoneyUtils.notNull(reserved)));
        }

        return qty;
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, BigDecimal> getProductSkuAvailableToSellQuantity(final String productSku, final Collection<Warehouse> warehouses) {

        final Map<String, BigDecimal> qty = new HashMap<String, BigDecimal>();
        qty.put(productSku, BigDecimal.ZERO);

        if (CollectionUtils.isEmpty(warehouses)) {
            return qty;
        }

        final Pair<BigDecimal, BigDecimal> qtyAndReserve = findQuantity(warehouses, productSku);

        qty.put(productSku,
                MoneyUtils.notNull(qtyAndReserve.getFirst()).subtract(MoneyUtils.notNull(qtyAndReserve.getSecond())));

        return qty;
    }

    /**
     * Get the sku's Quantity - Reserved quantity pair.
     *
     *
     * @param warehouses list of warehouses where
     * @param productSkuCode sku
     * @return pair of available and reserved quantity
     */
    public Pair<BigDecimal, BigDecimal> findQuantity(final Collection<Warehouse> warehouses, final String productSkuCode) {

        final List<Object> warehouseIdList = new ArrayList<Object>(warehouses.size());
        for (Warehouse wh : warehouses) {
            warehouseIdList.add(wh.getWarehouseId());
        }

        final List rez = getGenericDao().findQueryObjectsByNamedQuery(
                "SKU.QTY.ON.WAREHOUSES.IN.WAREHOUSEID.BY.SKUCODE",
                productSkuCode,
                warehouseIdList
        );

        BigDecimal quantity = ZERO;
        BigDecimal reserved = ZERO;

        if (!rez.isEmpty()) {
            final Object obj[] = (Object[]) rez.get(0);
            if (obj.length > 0 && obj[0] != null) {
                quantity = ((BigDecimal) obj[0]).setScale(Constants.DEFAULT_SCALE, RoundingMode.HALF_UP);
            }
            if (obj.length > 1 && obj[1] != null) {
                reserved = ((BigDecimal) obj[1]).setScale(Constants.DEFAULT_SCALE, RoundingMode.HALF_UP);
            }
        }

        return new Pair<BigDecimal, BigDecimal>(quantity, reserved);

    }


    /**
     * {@inheritDoc}
     */
    public BigDecimal reservation(final Warehouse warehouse, final String productSkuCode, final BigDecimal reserveQty) {

        return reservation(warehouse, productSkuCode, reserveQty, false);

    }


    /**
     * {@inheritDoc}
     */
    public BigDecimal reservation(final Warehouse warehouse, final String productSkuCode, final BigDecimal reserveQty, final boolean allowBackorder) {

        final SkuWarehouse skuWarehouse = findByWarehouseSkuForUpdate(warehouse, productSkuCode);

        if (skuWarehouse == null) {

            if (allowBackorder) {
                final SkuWarehouse newSkuEntry = getGenericDao().getEntityFactory().getByIface(SkuWarehouse.class);
                newSkuEntry.setWarehouse(warehouse);
                newSkuEntry.setSkuCode(productSkuCode);
                newSkuEntry.setQuantity(BigDecimal.ZERO);
                newSkuEntry.setReserved(reserveQty);
                create(newSkuEntry);
                return ZERO;
            }
            return reserveQty.setScale(Constants.DEFAULT_SCALE, RoundingMode.HALF_UP);

        } else {

            final BigDecimal rest;
            if (allowBackorder) {
                rest = BigDecimal.ZERO; // the remainder is zero
            } else {
                BigDecimal canReserve = skuWarehouse.getAvailableToSell();
                rest = canReserve.subtract(reserveQty);
            }

            if (MoneyUtils.isFirstBiggerThanOrEqualToSecond(rest, BigDecimal.ZERO)) {
                skuWarehouse.setReserved(
                        MoneyUtils.notNull(skuWarehouse.getReserved(), ZERO).add(reserveQty));
                update(skuWarehouse);
                return ZERO;
            } else {
                skuWarehouse.setReserved(skuWarehouse.getQuantity());
                update(skuWarehouse);
                return rest.abs().setScale(Constants.DEFAULT_SCALE, RoundingMode.HALF_UP);
            }
        }

    }

    /**
     * {@inheritDoc}
     */
    public BigDecimal voidReservation(final Warehouse warehouse, final String productSkuCode, final BigDecimal voidQty) {
        final SkuWarehouse skuWarehouse = findByWarehouseSkuForUpdate(warehouse, productSkuCode);

        if (skuWarehouse == null) {
            return voidQty.setScale(Constants.DEFAULT_SCALE, RoundingMode.HALF_UP);
        } else {
            BigDecimal canVoid = MoneyUtils.notNull(skuWarehouse.getReserved(), ZERO).min(voidQty);
            BigDecimal rest = MoneyUtils.notNull(skuWarehouse.getReserved(), ZERO).subtract(voidQty);
            skuWarehouse.setReserved(MoneyUtils.notNull(skuWarehouse.getReserved(), ZERO).subtract(canVoid));
            update(skuWarehouse);
            if (MoneyUtils.isFirstBiggerThanOrEqualToSecond(rest, BigDecimal.ZERO)) {
                return ZERO;
            } else {
                return rest.abs().setScale(Constants.DEFAULT_SCALE, RoundingMode.HALF_UP);
            }

        }
    }


    /**
     * {@inheritDoc}
     */
    public BigDecimal credit(final Warehouse warehouse, final String productSkuCode, final BigDecimal addQty) {
        final SkuWarehouse skuWarehouse = findByWarehouseSkuForUpdate(warehouse, productSkuCode);

        if (skuWarehouse == null) {
            final SkuWarehouse newSkuWarehouse = getGenericDao().getEntityFactory().getByIface(SkuWarehouse.class);
            newSkuWarehouse.setQuantity(addQty);
            newSkuWarehouse.setReserved(BigDecimal.ZERO);
            newSkuWarehouse.setSkuCode(productSkuCode);
            newSkuWarehouse.setWarehouse(warehouse);
            create(newSkuWarehouse);
        } else {
            skuWarehouse.setQuantity(skuWarehouse.getQuantity().add(addQty));
            update(skuWarehouse);
        }
        return BigDecimal.ZERO;

    }

    /**
     * {@inheritDoc}
     */
    public BigDecimal debit(final Warehouse warehouse, final String productSkuCode, final BigDecimal debitQty) {

        final SkuWarehouse skuWarehouse = findByWarehouseSkuForUpdate(warehouse, productSkuCode);

        if (skuWarehouse == null) {
            return debitQty.setScale(Constants.DEFAULT_SCALE, RoundingMode.HALF_UP);
        } else {
            BigDecimal canDebit = skuWarehouse.getQuantity().min(debitQty);
            BigDecimal rest = skuWarehouse.getQuantity().subtract(debitQty);
            skuWarehouse.setQuantity(skuWarehouse.getQuantity().subtract(canDebit));
            update(skuWarehouse);
            if (MoneyUtils.isFirstBiggerThanOrEqualToSecond(BigDecimal.ZERO, rest)) {
                return rest.abs().setScale(Constants.DEFAULT_SCALE, RoundingMode.HALF_UP);
            } else {
                return ZERO;
            }
        }

    }

    /** {@inheritDoc}*/
    public SkuWarehouse create(SkuWarehouse instance) {
        final SkuWarehouse rez = super.create(instance);
        return rez;
    }

    /** {@inheritDoc}*/
    public SkuWarehouse update(SkuWarehouse instance) {
        final SkuWarehouse rez = super.update(instance);
        getGenericDao().flush(); // Need to make changes immediately available
        return rez;
    }

    private SkuWarehouse findByWarehouseSkuForUpdate(final Warehouse warehouse, final String productSkuCode) {
        final SkuWarehouse inventory = findByWarehouseSku(warehouse, productSkuCode);
        if (inventory != null) {
            return getGenericDao().findById(inventory.getSkuWarehouseId(), true);
        }
        return null;
    }


    /** {@inheritDoc} */
    public SkuWarehouse findByWarehouseSku(final Warehouse warehouse, final String productSkuCode) {
        return getGenericDao().findSingleByNamedQuery(
                "SKUS.ON.WAREHOUSE.BY.SKUCODE.WAREHOUSEID",
                productSkuCode,
                warehouse.getWarehouseId());
    }

    /** {@inheritDoc} */
    public List<String> findProductSkuForWhichInventoryChangedAfter(final Date lastUpdate) {
        return (List) getGenericDao().findQueryObjectByNamedQuery("SKUCODE.FOR.SKUWAREHOUSE.CHANGED.SINCE", lastUpdate);
    }

    /** {@inheritDoc} */
    public boolean isSkuAvailabilityPreorderOrBackorder(final String productSkuCode, final boolean checkAvailabilityDates) {
        ProductSku sku = productService.getProductSkuByCode(productSkuCode);
        if (sku != null) {
            Product product = sku.getProduct();
            if (Product.AVAILABILITY_PREORDER == product.getAvailability()) {
                // for preorder do not check from date
                return !checkAvailabilityDates || DomainApiUtils.isObjectAvailableNow(true, null, product.getAvailableto(), new Date());
            } else if (Product.AVAILABILITY_BACKORDER == product.getAvailability()) {
                // for back order check both dates
                return !checkAvailabilityDates || DomainApiUtils.isObjectAvailableNow(true, product.getAvailablefrom(), product.getAvailableto(), new Date());
            }
        }
        return false;
    }

    /** IoC.*/
    public void setProductService(ProductService productService) {
        this.productService = productService;
    }

}
