package org.yes.cart.remote.service.impl;

import org.yes.cart.domain.dto.ShopWarehouseDTO;
import org.yes.cart.domain.dto.SkuWarehouseDTO;
import org.yes.cart.domain.dto.WarehouseDTO;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.remote.service.RemoteWarehouseService;
import org.yes.cart.service.domain.SkuWarehouseService;
import org.yes.cart.service.dto.DtoWarehouseService;

import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 15-May-2011
 * Time: 17:22:15
 */
public class RemoteWarehouseServiceImpl
        extends AbstractRemoteService<WarehouseDTO>
        implements RemoteWarehouseService {

    private final DtoWarehouseService dtoWarehouseService;


    /**
     * Construct service.
     *
     * @param service dto service to use
     */
    public RemoteWarehouseServiceImpl(final DtoWarehouseService service) {
        super(service);
        this.dtoWarehouseService = service;
    }


    /**
     * {@inheritDoc
     */
    public List<WarehouseDTO> findByShopId(final long shopId)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return dtoWarehouseService.findByShopId(shopId);
    }

    public void setShopWarehouseRank(long shopWarehouseId, int newRank) {
        dtoWarehouseService.setShopWarehouseRank(shopWarehouseId, newRank);
    }

    /**
     * {@inheritDoc
     */
    public ShopWarehouseDTO assignWarehouse(final long warehouseId, final long shopId)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return dtoWarehouseService.assignWarehouse(warehouseId, shopId);
    }

    /**
     * {@inheritDoc
     */
    public void unassignWarehouse(final long warehouseId, final long shopId) {
        dtoWarehouseService.unassignWarehouse(warehouseId, shopId);
    }

    /**
     * {@inheritDoc
     */
    public void removeSkuOnWarehouse(final long skuWarehouseId) {
        dtoWarehouseService.removeSkuOnWarehouse(skuWarehouseId);
    }

    /**
     * {@inheritDoc
     */
    public SkuWarehouseDTO createSkuOnWarehouse(final SkuWarehouseDTO skuWarehouseDTO) {
        return dtoWarehouseService.createSkuOnWarehouse(skuWarehouseDTO);
    }

    /**
     * {@inheritDoc
     */
    public SkuWarehouseDTO updateSkuOnWarehouse(final SkuWarehouseDTO skuWarehouseDTO) {
        return dtoWarehouseService.updateSkuOnWarehouse(skuWarehouseDTO);
    }

    /**
     * {@inheritDoc
     */
    public List<SkuWarehouseDTO> findProductSkusOnWarehouse(final long productId, final long warehouseId) {
        return dtoWarehouseService.findProductSkusOnWarehouse(productId, warehouseId);
    }


    /**
     * {@inheritDoc
     */
    public SkuWarehouseService getSkuWarehouseService() {
        return dtoWarehouseService.getSkuWarehouseService();
    }

}
