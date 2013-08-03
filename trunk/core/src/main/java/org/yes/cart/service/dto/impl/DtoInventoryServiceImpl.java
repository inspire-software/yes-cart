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

package org.yes.cart.service.dto.impl;

import com.inspiresoftware.lib.dto.geda.adapter.repository.AdaptersRepository;
import com.inspiresoftware.lib.dto.geda.assembler.Assembler;
import com.inspiresoftware.lib.dto.geda.assembler.DTOAssembler;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.util.StringUtils;
import org.yes.cart.dao.CriteriaTuner;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.dto.InventoryDTO;
import org.yes.cart.domain.dto.WarehouseDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.domain.entity.SkuWarehouse;
import org.yes.cart.domain.entity.Warehouse;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.domain.SkuWarehouseService;
import org.yes.cart.service.dto.DtoInventoryService;
import org.yes.cart.service.dto.DtoWarehouseService;
import org.yes.cart.service.dto.support.InventoryFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 12-11-29
 * Time: 7:11 PM
 */
public class DtoInventoryServiceImpl implements DtoInventoryService {

    private final DtoWarehouseService dtoWarehouseService;

    private final GenericDAO<SkuWarehouse, Long> skuWarehouseDAO;
    private final GenericDAO<ProductSku, Long> productSkuDAO;
    private final GenericDAO<Warehouse, Long> warehouseDAO;
    private final DtoFactory dtoFactory;
    private final AdaptersRepository adaptersRepository;

    private final Assembler skuWarehouseAsm;

    public DtoInventoryServiceImpl(final DtoWarehouseService dtoWarehouseService,
                                   final GenericDAO<SkuWarehouse, Long> skuWarehouseDAO,
                                   final GenericDAO<ProductSku, Long> productSkuDAO,
                                   final GenericDAO<Warehouse, Long> warehouseDAO,
                                   final DtoFactory dtoFactory,
                                   final AdaptersRepository adaptersRepository) {
        this.dtoWarehouseService = dtoWarehouseService;
        this.skuWarehouseDAO = skuWarehouseDAO;
        this.productSkuDAO = productSkuDAO;
        this.warehouseDAO = warehouseDAO;
        this.dtoFactory = dtoFactory;
        this.adaptersRepository = adaptersRepository;

        this.skuWarehouseAsm = DTOAssembler.newAssembler(
                this.dtoFactory.getImplClass(InventoryDTO.class),
                this.skuWarehouseDAO.getEntityFactory().getImplClass(SkuWarehouse.class));
    }

    /** {@inheritDoc} */
    public List<InventoryDTO> getInventoryList(final InventoryFilter filter) {

        final List<InventoryDTO> inventory = new ArrayList<InventoryDTO>();

        if (filter.getWarehouse() != null) {
            // only allow lists for warehouse inventory lists

            final List<Criterion> criteria = new ArrayList<Criterion>();
            criteria.add(Restrictions.eq("warehouse.warehouseId", filter.getWarehouse().getId()));
            if (StringUtils.hasLength(filter.getProductCode())) {
                if (filter.getProductCodeExact()) {
                    criteria.add(
                            Restrictions.or(
                                Restrictions.eq("prod.code", filter.getProductCode()),
                                Restrictions.eq("sku.code", filter.getProductCode())
                            )
                    );
                } else {
                    criteria.add(
                            Restrictions.or(
                                    Restrictions.ilike("prod.code", filter.getProductCode(), MatchMode.ANYWHERE),
                                    Restrictions.ilike("sku.code", filter.getProductCode(), MatchMode.ANYWHERE)
                            )
                    );
                }
            }

            final List<SkuWarehouse> entities = skuWarehouseDAO.findByCriteria(new CriteriaTuner() {
                public void tune(final Criteria crit) {
                    crit.createAlias("sku", "sku");
                    crit.createAlias("warehouse", "warehouse");
                    crit.createAlias("sku.product", "prod");
                    crit.setFetchMode("warehouse", FetchMode.JOIN);
                    crit.setFetchMode("sku", FetchMode.JOIN);
                    crit.setFetchMode("prod", FetchMode.JOIN);
                }
            }, criteria.toArray(new Criterion[criteria.size()]));

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
    public List<WarehouseDTO> getWarehouses() throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return dtoWarehouseService.getAll();
    }

    /** {@inheritDoc} */
    public void removeInventory(final long skuWarehouseId) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        dtoWarehouseService.removeSkuOnWarehouse(skuWarehouseId);
    }

    /** {@inheritDoc} */
    public InventoryDTO getInventory(final long skuWarehouseId) throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final InventoryDTO dto = dtoFactory.getByIface(InventoryDTO.class);
        final Map<String, Object> adapters = adaptersRepository.getAll();
        final SkuWarehouse entity = skuWarehouseDAO.findById(skuWarehouseId);
        skuWarehouseAsm.assembleDto(dto, entity, adapters, dtoFactory);

        return dto;
    }

    /** {@inheritDoc} */
    public InventoryDTO createInventory(final InventoryDTO inventory) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return saveInventory(inventory);
    }

    /** {@inheritDoc} */
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
            final List<Criterion> criteria = new ArrayList<Criterion>();
            criteria.add(Restrictions.eq("warehouse.code", inventory.getWarehouseCode()));
            criteria.add(Restrictions.eq("sku.code", inventory.getSkuCode()));

            final List<SkuWarehouse> candidates = skuWarehouseDAO.findByCriteria(new CriteriaTuner() {
                public void tune(final Criteria crit) {
                    crit.createAlias("sku", "sku");
                    crit.createAlias("warehouse", "warehouse");
                    crit.setFetchMode("warehouse", FetchMode.JOIN);
                    crit.setFetchMode("sku", FetchMode.JOIN);
                    crit.setMaxResults(1);
                }
            }, criteria.toArray(new Criterion[criteria.size()]));
            if (candidates != null && candidates.size() > 0) {
                // TODO: this will work but may be potentially dangerous feature since we do edit from Add.
                // entity = candidates.get(0);
                throw new IllegalArgumentException("Duplicate entry for product: " + inventory.getSkuCode() + " for warehouse: " + inventory.getWarehouseCode());
            }
        }

        if (entity == null) {
            final List<ProductSku> skus = productSkuDAO.findByCriteria(Restrictions.eq("code", inventory.getSkuCode()));
            if (skus == null || skus.size() != 1) {
                throw new UnableToCreateInstanceException("Invalid SKU: " + inventory.getSkuCode(), null);
            }
            final List<Warehouse> warehouses = warehouseDAO.findByCriteria(Restrictions.eq("code", inventory.getWarehouseCode()));
            if (warehouses == null || warehouses.size() != 1) {
                throw new UnableToCreateInstanceException("Invalid warehouse: " + inventory.getWarehouseCode(), null);
            }

            entity = skuWarehouseDAO.getEntityFactory().getByIface(SkuWarehouse.class);
            entity.setSku(skus.get(0));
            entity.setWarehouse(warehouses.get(0));
        }

        final Map<String, Object> adapters = adaptersRepository.getAll();

        skuWarehouseAsm.assembleEntity(inventory, entity, adapters, dtoFactory);

        skuWarehouseDAO.saveOrUpdate(entity);

        skuWarehouseAsm.assembleDto(inventory, entity, adapters, dtoFactory);

        return inventory;
    }

}
