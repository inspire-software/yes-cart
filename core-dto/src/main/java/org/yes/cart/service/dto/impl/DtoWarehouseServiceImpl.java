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

package org.yes.cart.service.dto.impl;

import com.inspiresoftware.lib.dto.geda.adapter.repository.AdaptersRepository;
import com.inspiresoftware.lib.dto.geda.assembler.Assembler;
import com.inspiresoftware.lib.dto.geda.assembler.DTOAssembler;
import org.yes.cart.domain.dto.ShopDTO;
import org.yes.cart.domain.dto.SkuWarehouseDTO;
import org.yes.cart.domain.dto.WarehouseDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.dto.impl.ShopDTOImpl;
import org.yes.cart.domain.dto.impl.ShopWarehouseDTOImpl;
import org.yes.cart.domain.dto.impl.WarehouseDTOImpl;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.entity.ShopWarehouse;
import org.yes.cart.domain.entity.SkuWarehouse;
import org.yes.cart.domain.entity.Warehouse;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.domain.GenericService;
import org.yes.cart.service.domain.SkuWarehouseService;
import org.yes.cart.service.domain.WarehouseService;
import org.yes.cart.service.dto.DtoWarehouseService;

import java.math.BigDecimal;
import java.util.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DtoWarehouseServiceImpl
        extends AbstractDtoServiceImpl<WarehouseDTO, WarehouseDTOImpl, Warehouse>
        implements DtoWarehouseService {

    private final SkuWarehouseService skuWarehouseService;

    private final Assembler dtoSkuWarehouseAssembler;

    private final Assembler shopWarehouseAssembler;

    private final Assembler shopAssembler;


    /**
     * Construct dto service.
     *
     * @param dtoFactory              {@link org.yes.cart.domain.dto.factory.DtoFactory}
     * @param warehouseGenericService {@link org.yes.cart.service.domain.GenericService}
     * @param adaptersRepository      value converter
     * @param skuWarehouseService     service to manage sku qty on warehouses
     */
    public DtoWarehouseServiceImpl(final GenericService<Warehouse> warehouseGenericService,
                                   final DtoFactory dtoFactory,
                                   final AdaptersRepository adaptersRepository,
                                   final SkuWarehouseService skuWarehouseService) {
        super(dtoFactory, warehouseGenericService, adaptersRepository);
        this.skuWarehouseService = skuWarehouseService;
        dtoSkuWarehouseAssembler = DTOAssembler.newAssembler(
                dtoFactory.getImplClass(SkuWarehouseDTO.class),
                SkuWarehouse.class);

        shopWarehouseAssembler = DTOAssembler.newAssembler(
                ShopWarehouseDTOImpl.class, ShopWarehouse.class);

        shopAssembler = DTOAssembler.newAssembler(ShopDTOImpl.class, Shop.class);

    }

    /**
     * {@inheritDoc}
     */
    public Class<WarehouseDTO> getDtoIFace() {
        return WarehouseDTO.class;
    }

    /**
     * {@inheritDoc}
     */
    public Class<WarehouseDTOImpl> getDtoImpl() {
        return WarehouseDTOImpl.class;
    }

    /**
     * {@inheritDoc}
     */
    public Class<Warehouse> getEntityIFace() {
        return Warehouse.class;
    }


    @Override
    public Map<WarehouseDTO, Map<ShopDTO, Boolean>> getAllWithShops() throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<Warehouse> all = getService().findAll();
        final Map<WarehouseDTO, Map<ShopDTO, Boolean>> dtos = new LinkedHashMap<>(all.size() * 2);
        for (final Warehouse warehouse : all) {
            final WarehouseDTO dto = getNew();
            assembler.assembleDto(dto, warehouse, getAdaptersRepository(), entityFactory);
            createPostProcess(dto, warehouse);
            final Map<ShopDTO, Boolean> assignments = getShopAssignmentsForWarehouse(warehouse);
            dtos.put(dto, assignments);
        }
        return dtos;
    }

    /**
     * {@inheritDoc}
     */
    public Map<WarehouseDTO, Boolean> findAllByShopId(final long shopId) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return findByShopId(shopId, true);
    }

    /**
     * {@inheritDoc}
     */
    public Map<WarehouseDTO, Boolean> findByShopId(final long shopId, boolean includeDisabled) throws UnmappedInterfaceException, UnableToCreateInstanceException {

        List<Warehouse> warehouses = ((WarehouseService) service).getByShopId(shopId, includeDisabled);
        Set<Long> warehouseEnabledIds = new HashSet<Long>();
        if (includeDisabled) {
            for (final Warehouse warehouse : ((WarehouseService) service).getByShopId(shopId, false)) {
                warehouseEnabledIds.add(warehouse.getWarehouseId());
            }
        }
        List<WarehouseDTO> dtos = new ArrayList<WarehouseDTO>();
        fillDTOs(warehouses, dtos);
        Map<WarehouseDTO, Boolean> dtosAndFlag = new HashMap<WarehouseDTO, Boolean>();
        for (final WarehouseDTO dto : dtos) {
            dtosAndFlag.put(dto, includeDisabled && !warehouseEnabledIds.contains(dto.getWarehouseId()));
        }
        return dtosAndFlag;
    }

    @Override
    public Map<ShopDTO, Boolean> getAssignedWarehouseShops(final long warehouseId) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final Warehouse warehouse = getService().findById(warehouseId);
        return getShopAssignmentsForWarehouse(warehouse);
    }


    private Map<ShopDTO, Boolean> getShopAssignmentsForWarehouse(final Warehouse warehouse) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        if (warehouse == null) {
            return Collections.emptyMap();
        }
        final Collection<ShopWarehouse> assigned = warehouse.getWarehouseShop();
        final Map<Long, Boolean> enabledMap = new HashMap<>(assigned.size() * 2);
        for (final ShopWarehouse shop : assigned) {
            enabledMap.put(shop.getShop().getShopId(), shop.isDisabled());
        }
        final List<ShopDTO> shopDTOs = new ArrayList<ShopDTO>(assigned.size());
        fillCarrierShopsDTOs(shopDTOs, assigned);
        final Map<ShopDTO, Boolean> dtoPairs = new LinkedHashMap<>(shopDTOs.size());
        for (final ShopDTO dto : shopDTOs) {
            dtoPairs.put(dto, enabledMap.get(dto.getShopId()));
        }
        return dtoPairs;
    }


    /**
     * {@inheritDoc}
     */
    public void setShopWarehouseRank(final long shopWarehouseId, final int newRank) {
        ((WarehouseService) service).updateShopWarehouseRank(shopWarehouseId, newRank);
    }

    /**
     * {@inheritDoc}
     */
    public void assignWarehouse(final long warehouseId, final long shopId, final boolean soft)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        ((WarehouseService) service).assignWarehouse(warehouseId, shopId, soft);
    }

    /**
     * {@inheritDoc}
     */
    public void unassignWarehouse(final long warehouseId, final long shopId, final boolean soft) {
        ((WarehouseService) service).unassignWarehouse(warehouseId, shopId, soft);
    }


    /**
     * Dind product skus quantity objects on given warehouse.
     *
     * @param productId   given product id
     * @param warehouseId given warehouse id.
     * @return list of founded {@link SkuWarehouseDTO}
     */
    public List<SkuWarehouseDTO> findProductSkusOnWarehouse(final long productId, final long warehouseId) {
        final List<SkuWarehouse> skuWarehouses = skuWarehouseService.getProductSkusOnWarehouse(productId, warehouseId);
        final List<SkuWarehouseDTO> result = new ArrayList<SkuWarehouseDTO>(skuWarehouses.size());
        for (SkuWarehouse sw : skuWarehouses) {
            result.add(assembleSkuWarehouseDTO(sw));
        }
        return result;
    }

    /**
     * Create given {@link SkuWarehouseDTO}
     *
     * @param skuWarehouseDTO given {@link SkuWarehouseDTO}
     * @return created SkuWarehouseDTO.
     */
    public SkuWarehouseDTO createSkuOnWarehouse(final SkuWarehouseDTO skuWarehouseDTO) {
        SkuWarehouse skuWarehouse = skuWarehouseService.getGenericDao().getEntityFactory().getByIface(SkuWarehouse.class);
        dtoSkuWarehouseAssembler.assembleEntity(
                skuWarehouseDTO,
                skuWarehouse,
                getAdaptersRepository(),
                getAssemblerEntityFactory());
        skuWarehouse.setSkuCode(skuWarehouseDTO.getSkuCode());
        skuWarehouse = skuWarehouseService.create(skuWarehouse);
        return assembleSkuWarehouseDTO(skuWarehouse);
    }

    /**
     * Update given {@link SkuWarehouseDTO}
     *
     * @param skuWarehouseDTO given {@link SkuWarehouseDTO}
     * @return updated SkuWarehouseDTO.
     */
    public SkuWarehouseDTO updateSkuOnWarehouse(final SkuWarehouseDTO skuWarehouseDTO) {
        SkuWarehouse skuWarehouse = skuWarehouseService.findById(skuWarehouseDTO.getSkuWarehouseId());
        dtoSkuWarehouseAssembler.assembleEntity(
                skuWarehouseDTO,
                skuWarehouse,
                getAdaptersRepository(),
                getAssemblerEntityFactory());
        skuWarehouse = skuWarehouseService.update(skuWarehouse);
        return assembleSkuWarehouseDTO(skuWarehouse);

    }

    private SkuWarehouseDTO assembleSkuWarehouseDTO(final SkuWarehouse skuWarehouse) {
        SkuWarehouseDTO result = getAssemblerDtoFactory().getByIface(SkuWarehouseDTO.class);
        dtoSkuWarehouseAssembler.assembleDto(
                result,
                skuWarehouse,
                getAdaptersRepository(),
                getAssemblerDtoFactory());
        return result;
    }

    /**
     * Remove sku warehouse object by given pk value
     *
     * @param skuWarehouseId given pk value.
     */
    public void removeSkuOnWarehouse(final long skuWarehouseId) {
        final SkuWarehouse skuWarehouse = skuWarehouseService.findById(skuWarehouseId);
        skuWarehouse.setQuantity(BigDecimal.ZERO);
        // skuWarehouse.setReserved(BigDecimal.ZERO); Must not remove reservation!
        skuWarehouseService.update(skuWarehouse);
    }


    private void fillCarrierShopsDTOs(final List<ShopDTO> result, final Collection<ShopWarehouse> shops)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        for (ShopWarehouse shop : shops) {
            final ShopDTO shopDTO = dtoFactory.getByIface(ShopDTO.class);
            shopAssembler.assembleDto(shopDTO, shop.getShop(), getAdaptersRepository(), dtoFactory);
            result.add(shopDTO);
        }
    }


    /**
     * Get the {@link SkuWarehouseService}. Test usage only.
     *
     * @return {@link org.yes.cart.service.domain.SkuWarehouseService}
     */
    public SkuWarehouseService getSkuWarehouseService() {
        return skuWarehouseService;
    }
}
