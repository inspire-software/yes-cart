package org.yes.cart.service.domain.impl;

import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.entity.ShopWarehouse;
import org.yes.cart.domain.entity.Warehouse;
import org.yes.cart.service.domain.WarehouseService;

import java.util.List;

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
    public List<Warehouse> findByShopId(final long shopId) {
        return getGenericDao().findByNamedQuery("ASSIGNED.WAREHOUSES.TO.SHOP", shopId);
    }

    /** {@inheritDoc} */
    public void setShopWarehouseRank(final long shopWarehouseId, final int newRank) {
        final ShopWarehouse shopWarehouse = shopWarehouseDao.findById(shopWarehouseId);
        shopWarehouse.setRank(newRank);
        shopWarehouseDao.update(shopWarehouse);
    }

    /** {@inheritDoc} */
    public ShopWarehouse assignWarehouse(final long warehouseId, final long shopId) {
        final ShopWarehouse shopWarehouse = shopWarehouseDao.getEntityFactory().getByIface(ShopWarehouse.class);
        shopWarehouse.setWarehouse(getById(warehouseId));
        shopWarehouse.setShop(shopDao.findById(shopId));
        shopWarehouse.setRank(DEFAULT_USAGE_RANK);
        return shopWarehouseDao.create(shopWarehouse);
    }

    /** {@inheritDoc} */
    public void unassignWarehouse(final long warehouseId, final long shopId) {
        final ShopWarehouse shopWarehouse = shopWarehouseDao.findSingleByNamedQuery(
                "ASSIGNED.SHOPWAREHOUSE", warehouseId, shopId);
        shopWarehouseDao.delete(shopWarehouse);
    }

}
