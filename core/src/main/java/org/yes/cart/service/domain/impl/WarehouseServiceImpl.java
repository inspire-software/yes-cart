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

package org.yes.cart.service.domain.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.entity.ShopWarehouse;
import org.yes.cart.domain.entity.Warehouse;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.domain.WarehouseService;
import org.yes.cart.utils.HQLUtils;

import java.util.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class WarehouseServiceImpl extends BaseGenericServiceImpl<Warehouse> implements WarehouseService {

    private final static int DEFAULT_USAGE_RANK = 100;

    private final GenericDAO<Shop, Long> shopDao;
    private final GenericDAO<ShopWarehouse, Long> shopWarehouseDao;

    /**
     * Construct warehouse service
     * @param genericDao warehouse dao to use.
     * @param shopDao whop dao to use.
     * @param shopWarehouseDao shop warehouse dao to use
     */
    public WarehouseServiceImpl(
            final GenericDAO<Warehouse, Long> genericDao,
            final GenericDAO<Shop, Long> shopDao,
            final GenericDAO<ShopWarehouse, Long> shopWarehouseDao) {
        super(genericDao);
        this.shopDao = shopDao;
        this.shopWarehouseDao = shopWarehouseDao;
    }

    /** {@inheritDoc} */
    @Override
    public List<Warehouse> findByShopId(final long shopId, boolean includeDisabled) {
        if (includeDisabled) {
            return new ArrayList<>(getGenericDao().findByNamedQuery("ASSIGNED.WAREHOUSES.TO.SHOP", shopId));
        }
        return new ArrayList<>(getGenericDao().findByNamedQuery("ASSIGNED.WAREHOUSES.TO.SHOP.DISABLED", shopId, Boolean.FALSE));
    }

    /** {@inheritDoc} */
    @Override
    public List<Warehouse> getByShopId(final long shopId, boolean includeDisabled) {
        return findByShopId(shopId, includeDisabled);
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, Warehouse> getByShopIdMapped(final long shopId, final boolean includeDisabled) {
        final List<Warehouse> warehouses = getByShopId(shopId, false);
        final Map<String, Warehouse> warehouseByCode = new HashMap<>();
        for (final Warehouse warehouse : warehouses) {
            warehouseByCode.put(warehouse.getCode(), warehouse);
        }
        return warehouseByCode;
    }

    /** {@inheritDoc} */
    @Override
    public void updateShopWarehouseRank(final long shopWarehouseId, final int newRank) {
         getGenericDao().executeUpdate(
                "WAREHOUSE.UPDATE.RANK",
                shopWarehouseId, newRank
        );
    }

    /** {@inheritDoc} */
    @Override
    public ShopWarehouse findShopWarehouseById(final long shopWarehouseId) {

        return shopWarehouseDao.findById(shopWarehouseId);

    }

    /** {@inheritDoc} */
    @Override
    public void assignWarehouse(final long warehouseId, final long shopId, final boolean soft) {

        final Warehouse warehouse = findById(warehouseId);
        final Collection<ShopWarehouse> assigned = warehouse.getWarehouseShop();
        for (final ShopWarehouse shop : assigned) {
            if (shop.getShop().getShopId() == shopId) {
                if (shop.isDisabled() && !soft) {
                    shop.setDisabled(false);
                    update(warehouse);
                }
                return;
            }
        }

        final Shop shop = shopDao.findById(shopId);
        if (shop != null) {
            final ShopWarehouse shopWarehouse = shopWarehouseDao.getEntityFactory().getByIface(ShopWarehouse.class);
            shopWarehouse.setWarehouse(warehouse);
            shopWarehouse.setShop(shop);
            shopWarehouse.setRank(DEFAULT_USAGE_RANK);
            shopWarehouse.setDisabled(soft);
            assigned.add(shopWarehouse);
        }
        update(warehouse);
    }

    /** {@inheritDoc} */
    @Override
    public void unassignWarehouse(final long warehouseId, final long shopId, final boolean soft) {

        final Warehouse warehouse = findById(warehouseId);
        final Iterator<ShopWarehouse> assigned = warehouse.getWarehouseShop().iterator();
        while (assigned.hasNext()) {
            final ShopWarehouse shop = assigned.next();
            if (shop.getShop().getShopId() == shopId) {
                if (soft) {
                    shop.setDisabled(true);
                } else {
                    assigned.remove();
                }
                update(warehouse);
                break;
            }
        }

    }


    private Pair<String, Object[]> findWarehouseQuery(final boolean count,
                                                      final String sort,
                                                      final boolean sortDescending,
                                                      final Map<String, List> filter) {

        final Map<String, List> currentFilter = filter != null ? new HashMap<>(filter) : null;

        final StringBuilder hqlCriteria = new StringBuilder();
        final List<Object> params = new ArrayList<>();

        if (count) {
            hqlCriteria.append("select count(distinct w.warehouseId) from WarehouseEntity w left join w.warehouseShop wse ");
        } else {
            hqlCriteria.append("select distinct w from WarehouseEntity w left join fetch w.warehouseShop wse ");
        }

        final List shops = currentFilter != null ? currentFilter.remove("shopIds") : null;
        if (CollectionUtils.isNotEmpty(shops)) {
            hqlCriteria.append(" where (wse.shop.shopId in (?1)) ");
            params.add(shops);
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



    /**
     * {@inheritDoc}
     */
    @Override
    public List<Warehouse> findWarehouses(final int start, final int offset, final String sort, final boolean sortDescending, final Map<String, List> filter) {

        final Pair<String, Object[]> query = findWarehouseQuery(false, sort, sortDescending, filter);

        return getGenericDao().findRangeByQuery(
                query.getFirst(),
                start, offset,
                query.getSecond()
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int findWarehouseCount(final Map<String, List> filter) {

        final Pair<String, Object[]> query = findWarehouseQuery(true, null, false, filter);

        return getGenericDao().findCountByQuery(
                query.getFirst(),
                query.getSecond()
        );
    }
}
