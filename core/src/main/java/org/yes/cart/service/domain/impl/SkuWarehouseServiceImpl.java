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

import org.apache.commons.lang.StringUtils;
import org.yes.cart.constants.Constants;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.Product;
import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.domain.entity.SkuWarehouse;
import org.yes.cart.domain.entity.Warehouse;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.service.domain.SkuWarehouseService;
import org.yes.cart.utils.HQLUtils;
import org.yes.cart.utils.MoneyUtils;
import org.yes.cart.utils.TimeContext;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class SkuWarehouseServiceImpl extends BaseGenericServiceImpl<SkuWarehouse> implements SkuWarehouseService {

    private static final BigDecimal ZERO = MoneyUtils.ZERO;

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
    @Override
    public List<SkuWarehouse> getProductSkusOnWarehouse(final long productId, final long warehouseId) {
        final Product product = productService.getProductById(productId, true);
        final Set<String> skus = new HashSet<>();
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
    @Override
    public BigDecimal reservation(final Warehouse warehouse, final String productSkuCode, final BigDecimal reserveQty) {

        return reservation(warehouse, productSkuCode, reserveQty, false);

    }


    /**
     * {@inheritDoc}
     */
    @Override
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

        } else if (skuWarehouse.getAvailability() == SkuWarehouse.AVAILABILITY_ALWAYS) {

            return ZERO;

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
    @Override
    public BigDecimal voidReservation(final Warehouse warehouse, final String productSkuCode, final BigDecimal voidQty) {
        final SkuWarehouse skuWarehouse = findByWarehouseSkuForUpdate(warehouse, productSkuCode);

        if (skuWarehouse == null) {
            return voidQty.setScale(Constants.DEFAULT_SCALE, RoundingMode.HALF_UP);
        } else if (skuWarehouse.getAvailability() == SkuWarehouse.AVAILABILITY_ALWAYS) {
            return ZERO;
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
    @Override
    public BigDecimal credit(final Warehouse warehouse, final String productSkuCode, final BigDecimal addQty) {
        final SkuWarehouse skuWarehouse = findByWarehouseSkuForUpdate(warehouse, productSkuCode);

        if (skuWarehouse == null) {
            final SkuWarehouse newSkuWarehouse = getGenericDao().getEntityFactory().getByIface(SkuWarehouse.class);
            newSkuWarehouse.setQuantity(addQty);
            newSkuWarehouse.setReserved(BigDecimal.ZERO);
            newSkuWarehouse.setSkuCode(productSkuCode);
            newSkuWarehouse.setWarehouse(warehouse);
            create(newSkuWarehouse);
        } else if (skuWarehouse.getAvailability() == SkuWarehouse.AVAILABILITY_ALWAYS) {
            return ZERO;
        } else {
            skuWarehouse.setQuantity(skuWarehouse.getQuantity().add(addQty));
            update(skuWarehouse);
        }
        return BigDecimal.ZERO;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal debit(final Warehouse warehouse, final String productSkuCode, final BigDecimal debitQty) {

        final SkuWarehouse skuWarehouse = findByWarehouseSkuForUpdate(warehouse, productSkuCode);

        if (skuWarehouse == null) {
            return debitQty.setScale(Constants.DEFAULT_SCALE, RoundingMode.HALF_UP);
        } else if (skuWarehouse.getAvailability() == SkuWarehouse.AVAILABILITY_ALWAYS) {
            return ZERO;
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
    @Override
    public SkuWarehouse create(SkuWarehouse instance) {
        return super.create(instance);
    }

    /** {@inheritDoc}*/
    @Override
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
    @Override
    public SkuWarehouse findByWarehouseSku(final Warehouse warehouse, final String productSkuCode) {
        return getGenericDao().findSingleByNamedQuery(
                "SKUS.ON.WAREHOUSE.BY.SKUCODE.WAREHOUSEID",
                productSkuCode,
                warehouse.getWarehouseId());
    }

    /** {@inheritDoc} */
    @Override
    public List<String> findProductSkuForWhichInventoryChangedAfter(final Instant lastUpdate) {
        return (List) getGenericDao().findQueryObjectByNamedQuery("SKUCODE.FOR.SKUWAREHOUSE.CHANGED.SINCE", lastUpdate);
    }

    /** {@inheritDoc} */
    @Override
    public List<String> findProductSkuByUnavailableBefore(final LocalDateTime before) {

        return (List) this.getGenericDao().findByNamedQuery("SKUWAREHOUSE.SKU.DISCONTINUED", before, Boolean.TRUE);

    }



    private Pair<String, Object[]> findSkuWarehouseQuery(final boolean count,
                                                         final String sort,
                                                         final boolean sortDescending,
                                                         final Map<String, List> filter) {

        final Map<String, List> currentFilter = filter != null ? new HashMap<>(filter) : null;

        final StringBuilder hqlCriteria = new StringBuilder();
        final List<Object> params = new ArrayList<>();

        if (count) {
            hqlCriteria.append("select count(w.skuWarehouseId) from SkuWarehouseEntity w ");
        } else {
            hqlCriteria.append("select w from SkuWarehouseEntity w ");
        }

        final List shopIds = currentFilter != null ? currentFilter.remove("warehouseIds") : null;
        if (shopIds != null) {
            hqlCriteria.append(" where (w.warehouse.warehouseId in (?1)) ");
            params.add(shopIds);
        }

        HQLUtils.appendFilterCriteria(hqlCriteria, params, "w", currentFilter);

        if (StringUtils.isNotBlank(sort)) {

            hqlCriteria.append(" order by w." + sort + " " + (sortDescending ? "desc" : "asc"));

        }

        return new Pair<>(
                hqlCriteria.toString(),
                params.toArray(new Object[params.size()])
        );

    }




    /** {@inheritDoc} */
    @Override
    public List<SkuWarehouse> findSkuWarehouses(final int start, final int offset, final String sort, final boolean sortDescending, final Map<String, List> filter) {

        final Pair<String, Object[]> query = findSkuWarehouseQuery(false, sort, sortDescending, filter);

        return getGenericDao().findRangeByQuery(
                query.getFirst(),
                start, offset,
                query.getSecond()
        );
    }

    /** {@inheritDoc} */
    @Override
    public int findSkuWarehouseCount(final Map<String, List> filter) {

        final Pair<String, Object[]> query = findSkuWarehouseQuery(true, null, false, filter);

        return getGenericDao().findCountByQuery(
                query.getFirst(),
                query.getSecond()
        );
    }

    /** IoC.*/
    public void setProductService(ProductService productService) {
        this.productService = productService;
    }

}
