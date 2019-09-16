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
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.yes.cart.constants.Constants;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.dto.InventoryDTO;
import org.yes.cart.domain.dto.ProductDTO;
import org.yes.cart.domain.dto.WarehouseDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.domain.entity.SkuWarehouse;
import org.yes.cart.domain.entity.Warehouse;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.domain.SkuWarehouseService;
import org.yes.cart.service.dto.DtoInventoryService;
import org.yes.cart.service.dto.DtoWarehouseService;
import org.yes.cart.utils.HQLUtils;
import org.yes.cart.utils.MoneyUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * User: denispavlov
 * Date: 12-11-29
 * Time: 7:11 PM
 */
public class DtoInventoryServiceImpl implements DtoInventoryService {

    private final DtoWarehouseService dtoWarehouseService;

    private final SkuWarehouseService skuWarehouseService;
    private final GenericDAO<SkuWarehouse, Long> skuWarehouseDAO;
    private final GenericDAO<ProductSku, Long> productSkuDAO;
    private final GenericDAO<Warehouse, Long> warehouseDAO;
    private final DtoFactory dtoFactory;
    private final AdaptersRepository adaptersRepository;

    private final Assembler skuWarehouseAsm;

    public DtoInventoryServiceImpl(final DtoWarehouseService dtoWarehouseService,
                                   final SkuWarehouseService skuWarehouseService,
                                   final GenericDAO<ProductSku, Long> productSkuDAO,
                                   final GenericDAO<Warehouse, Long> warehouseDAO,
                                   final DtoFactory dtoFactory,
                                   final AdaptersRepository adaptersRepository) {
        this.dtoWarehouseService = dtoWarehouseService;
        this.skuWarehouseService = skuWarehouseService;
        this.skuWarehouseDAO = skuWarehouseService.getGenericDao();
        this.productSkuDAO = productSkuDAO;
        this.warehouseDAO = warehouseDAO;
        this.dtoFactory = dtoFactory;
        this.adaptersRepository = adaptersRepository;

        this.skuWarehouseAsm = DTOAssembler.newAssembler(
                this.dtoFactory.getImplClass(InventoryDTO.class),
                this.skuWarehouseDAO.getEntityFactory().getImplClass(SkuWarehouse.class));
    }

    private final static char[] CODE = new char[] { '!' };
    private final static char[] LOW_OR_RESERVED = new char[] { '-', '+' };
    static {
        Arrays.sort(LOW_OR_RESERVED);
    }

    /** {@inheritDoc} */
    @Override
    public List<InventoryDTO> findBy(final long warehouseId, final String filter, final int page, final int pageSize) throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final List<InventoryDTO> inventory = new ArrayList<>();

        List<SkuWarehouse> entities = Collections.emptyList();

        if (warehouseId > 0L) {
            // only allow lists for warehouse inventory lists

            if (StringUtils.isNotBlank(filter)) {

                final Pair<LocalDateTime, LocalDateTime> dateSearch = ComplexSearchUtils.checkDateRangeSearch(filter);

                if (dateSearch != null) {

                    entities = skuWarehouseDAO.findRangeByCriteria(
                            " where (?1 is null or e.availablefrom <= ?1) and (?2 is null or e.availableto >= ?2) order by e.skuCode",
                            page * pageSize, pageSize,
                            dateSearch.getFirst(),
                            dateSearch.getSecond()
                    );

                } else {

                    final Pair<String, BigDecimal> lowOrReserved = ComplexSearchUtils.checkNumericSearch(filter, LOW_OR_RESERVED, Constants.INVENTORY_SCALE);

                    if (lowOrReserved != null) {

                        if ("-".equals(lowOrReserved.getFirst())) {
                            entities = skuWarehouseDAO.findRangeByCriteria(
                                    " where e.warehouse.warehouseId = ?1 and e.quantity <= ?2",
                                    page * pageSize, pageSize,
                                    warehouseId,
                                    lowOrReserved.getSecond()
                            );
                        } else if ("+".equals(lowOrReserved.getFirst())) {
                            entities = skuWarehouseDAO.findRangeByCriteria(
                                    " where e.warehouse.warehouseId = ?1 and e.reserved >= ?2",
                                    page * pageSize, pageSize,
                                    warehouseId,
                                    lowOrReserved.getSecond()
                            );
                        }

                    } else {

                        final Pair<String, String> byCode = ComplexSearchUtils.checkSpecialSearch(filter, CODE);

                        if (byCode != null) {

                            final List<ProductSku> skus = productSkuDAO.findRangeByCriteria(
                                    " where lower(e.product.code) = ?1 or lower(e.product.manufacturerCode) = ?1 or lower(e.product.pimCode) = ?1 or lower(e.barCode) = ?1 or lower(e.manufacturerCode) = ?1",
                                    0, pageSize,
                                    HQLUtils.criteriaIeq(byCode.getSecond())
                            );

                            final List<String> skuCodes = new ArrayList<>();
                            for (final ProductSku sku : skus) {
                                skuCodes.add(sku.getCode()); // sku codes from product match
                            }

                            if (skuCodes.isEmpty()) {

                                entities = skuWarehouseDAO.findRangeByCriteria(
                                        " where e.warehouse.warehouseId = ?1 and lower(e.skuCode) = ?2 order by e.skuCode",
                                        page * pageSize, pageSize,
                                        warehouseId,
                                        HQLUtils.criteriaIeq(byCode.getSecond())
                                );

                            } else {

                                entities = skuWarehouseDAO.findRangeByCriteria(
                                        " where e.warehouse.warehouseId = ?1 and (e.skuCode in (?2) or lower(e.skuCode) = ?3) order by e.skuCode",
                                        page * pageSize, pageSize,
                                        warehouseId,
                                        skuCodes,
                                        HQLUtils.criteriaIeq(byCode.getSecond())
                                );

                            }


                        } else {

                            final List<ProductSku> skus = productSkuDAO.findRangeByCriteria(
                                    " where lower(e.product.code) like ?1 or lower(e.product.name) like ?1 or lower(e.name) like ?1",
                                    0, pageSize,
                                    HQLUtils.criteriaIlikeAnywhere(filter)
                            );

                            final List<String> skuCodes = new ArrayList<>();
                            for (final ProductSku sku : skus) {
                                skuCodes.add(sku.getCode()); // sku codes from product match
                            }

                            if (skuCodes.isEmpty()) {

                                entities = skuWarehouseDAO.findRangeByCriteria(
                                        " where e.warehouse.warehouseId = ?1 and lower(e.skuCode) like ?2 order by e.skuCode",
                                        page * pageSize, pageSize,
                                        warehouseId,
                                        HQLUtils.criteriaIlikeAnywhere(filter)
                                );

                            } else {

                                entities = skuWarehouseDAO.findRangeByCriteria(
                                        " where e.warehouse.warehouseId = ?1 and (e.skuCode in (?2) or lower(e.skuCode) like ?3) order by e.skuCode",
                                        page * pageSize, pageSize,
                                        warehouseId,
                                        skuCodes,
                                        HQLUtils.criteriaIlikeAnywhere(filter)
                                );

                            }
                        }
                    }
                }
            } else {

                entities = skuWarehouseDAO.findRangeByCriteria(
                        " where e.warehouse.warehouseId = ?1 order by e.skuCode",
                        page * pageSize, pageSize,
                        warehouseId
                );

            }

            final Map<String, Object> adapters = adaptersRepository.getAll();
            for (final SkuWarehouse entity : entities) {
                final InventoryDTO dto = dtoFactory.getByIface(InventoryDTO.class);
                skuWarehouseAsm.assembleDto(dto, entity, adapters, dtoFactory);
                inventory.add(dto);
            }

        }

        return inventory;

    }

    /** {@inheritDoc} */
    @Override
    public List<WarehouseDTO> getWarehouses() throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return dtoWarehouseService.getAll();
    }

    /** {@inheritDoc} */
    @Override
    public void removeInventory(final long skuWarehouseId) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        dtoWarehouseService.removeSkuOnWarehouse(skuWarehouseId);
    }

    /** {@inheritDoc} */
    @Override
    public InventoryDTO getInventory(final long skuWarehouseId) throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final InventoryDTO dto = dtoFactory.getByIface(InventoryDTO.class);
        final Map<String, Object> adapters = adaptersRepository.getAll();
        final SkuWarehouse entity = skuWarehouseDAO.findById(skuWarehouseId);
        skuWarehouseAsm.assembleDto(dto, entity, adapters, dtoFactory);

        return dto;
    }

    /** {@inheritDoc} */
    @Override
    public InventoryDTO createInventory(final InventoryDTO inventory) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return saveInventory(inventory);
    }

    /** {@inheritDoc} */
    @Override
    public InventoryDTO updateInventory(final InventoryDTO inventory) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return saveInventory(inventory);
    }

    private InventoryDTO saveInventory(final InventoryDTO inventory) throws UnmappedInterfaceException, UnableToCreateInstanceException {

        SkuWarehouse entity = null;
        if (inventory.getSkuWarehouseId() > 0) {
            // check by id
            entity = skuWarehouseDAO.findById(inventory.getSkuWarehouseId());
        }
        if (entity == null) {

            // check by unique combination
            final List<SkuWarehouse> candidates = skuWarehouseDAO.findRangeByCriteria(
                    " where e.warehouse.code = ?1 and e.skuCode = ?2",
                    0, 1,
                    inventory.getWarehouseCode(),
                    inventory.getSkuCode()
            );
            if (CollectionUtils.isNotEmpty(candidates)) {
                // TODO: this will work but may be potentially dangerous feature since we do edit from Add.
                // entity = candidates.get(0);
                throw new IllegalArgumentException("Duplicate entry for product: " + inventory.getSkuCode() + " for warehouse: " + inventory.getWarehouseCode());
            }
        }

        if (entity == null) {
            final List<Warehouse> warehouses = warehouseDAO.findByCriteria(" where e.code = ?1", inventory.getWarehouseCode());
            if (warehouses == null || warehouses.size() != 1) {
                throw new UnableToCreateInstanceException("Invalid warehouse: " + inventory.getWarehouseCode(), null);
            }

            entity = skuWarehouseDAO.getEntityFactory().getByIface(SkuWarehouse.class);
            entity.setSkuCode(inventory.getSkuCode());
            entity.setWarehouse(warehouses.get(0));
        }

        cleanOrderQuantities(inventory);

        final Map<String, Object> adapters = adaptersRepository.getAll();

        skuWarehouseAsm.assembleEntity(inventory, entity, adapters, dtoFactory);
        // use service since we flush cache there
        if (entity.getSkuWarehouseId() > 0L) {
            skuWarehouseService.update(entity);
        } else {
            skuWarehouseService.create(entity);
        }

        skuWarehouseAsm.assembleDto(inventory, entity, adapters, dtoFactory);

        return inventory;
    }

    private void cleanOrderQuantities(final InventoryDTO inventoryDTO) {
        if (MoneyUtils.isFirstEqualToSecond(BigDecimal.ZERO, inventoryDTO.getMinOrderQuantity())) {
            inventoryDTO.setMinOrderQuantity(null);
        }
        if (MoneyUtils.isFirstEqualToSecond(BigDecimal.ZERO, inventoryDTO.getMaxOrderQuantity())) {
            inventoryDTO.setMaxOrderQuantity(null);
        }
        if (MoneyUtils.isFirstEqualToSecond(BigDecimal.ZERO, inventoryDTO.getStepOrderQuantity())) {
            inventoryDTO.setStepOrderQuantity(null);
        }
    }

}
