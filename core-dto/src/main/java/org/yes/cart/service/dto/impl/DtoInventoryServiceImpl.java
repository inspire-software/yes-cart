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
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.util.StringUtils;
import org.yes.cart.constants.Constants;
import org.yes.cart.dao.CriteriaTuner;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.dto.InventoryDTO;
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
import org.yes.cart.service.dto.support.InventoryFilter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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

    /** {@inheritDoc} */
    public List<InventoryDTO> getInventoryList(final InventoryFilter filter) {

        final List<InventoryDTO> inventory = new ArrayList<InventoryDTO>();

        if (filter.getWarehouse() != null) {
            // only allow lists for warehouse inventory lists



            final List<Criterion> criteria = new ArrayList<Criterion>();
            criteria.add(Restrictions.eq("warehouse.warehouseId", filter.getWarehouse().getWarehouseId()));
            if (StringUtils.hasLength(filter.getProductCode())) {

                if (filter.getProductCodeExact()) {

                    final List<ProductSku> skus = productSkuDAO.findByCriteria(new CriteriaTuner() {
                        public void tune(final Criteria crit) {
                            crit.createAlias("product", "prod");
                            crit.setFetchMode("prod", FetchMode.JOIN);
                        }
                    }, Restrictions.or(
                            Restrictions.or(
                                    Restrictions.eq("prod.code", filter.getProductCode()),
                                    Restrictions.eq("code", filter.getProductCode())
                            ),
                            Restrictions.or(
                                    Restrictions.eq("prod.name", filter.getProductCode()),
                                    Restrictions.eq("name", filter.getProductCode())
                            )
                    ));

                    final List<String> skuCodes = new ArrayList<String>();
                    skuCodes.add(filter.getProductCode()); // original for standalone inventory
                    for (final ProductSku sku : skus) {
                        skuCodes.add(sku.getCode()); // sku codes from product match
                    }

                    criteria.add(Restrictions.in("skuCode", skuCodes));

                } else {

                    final List<ProductSku> skus = productSkuDAO.findByCriteria(new CriteriaTuner() {
                        public void tune(final Criteria crit) {
                            crit.createAlias("product", "prod");
                            crit.setFetchMode("prod", FetchMode.JOIN);
                        }
                    }, Restrictions.or(
                            Restrictions.or(
                                    Restrictions.ilike("prod.code", filter.getProductCode(), MatchMode.ANYWHERE),
                                    Restrictions.ilike("code", filter.getProductCode(), MatchMode.ANYWHERE)
                            ),
                            Restrictions.or(
                                    Restrictions.ilike("prod.name", filter.getProductCode(), MatchMode.ANYWHERE),
                                    Restrictions.ilike("name", filter.getProductCode(), MatchMode.ANYWHERE)
                            )
                    ));

                    final List<String> skuCodes = new ArrayList<String>();
                    for (final ProductSku sku : skus) {
                        skuCodes.add(sku.getCode()); // sku codes from product match
                    }

                    if (skuCodes.isEmpty()) {
                        criteria.add(Restrictions.ilike("skuCode", filter.getProductCode(), MatchMode.ANYWHERE));
                    } else {
                        criteria.add(
                                Restrictions.or(
                                        Restrictions.ilike("skuCode", filter.getProductCode(), MatchMode.ANYWHERE),
                                        Restrictions.in("skuCode", skuCodes)
                                )
                        );
                    }
                }
            }

            final List<SkuWarehouse> entities = skuWarehouseDAO.findByCriteria(criteria.toArray(new Criterion[criteria.size()]));

            final Map<String, Object> adapters = adaptersRepository.getAll();
            for (final SkuWarehouse entity : entities) {
                final InventoryDTO dto = dtoFactory.getByIface(InventoryDTO.class);
                skuWarehouseAsm.assembleDto(dto, entity, adapters, dtoFactory);
                inventory.add(dto);
            }

        }

        return inventory;
    }

    private final static char[] CODE = new char[] { '!' };
    private final static char[] LOW_OR_RESERVED = new char[] { '-', '+' };
    static {
        Arrays.sort(LOW_OR_RESERVED);
    }

    private final static Order[] INVENTORY_ORDER = new Order[] { Order.asc("skuCode") };

    /** {@inheritDoc} */
    public List<InventoryDTO> findBy(final long warehouseId, final String filter, final int page, final int pageSize) throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final List<InventoryDTO> inventory = new ArrayList<InventoryDTO>();

        if (warehouseId > 0L) {
            // only allow lists for warehouse inventory lists

            final List<Criterion> criteria = new ArrayList<Criterion>();
            criteria.add(Restrictions.eq("warehouse.warehouseId", warehouseId));
            if (StringUtils.hasLength(filter)) {

                final Pair<String, BigDecimal> lowOrReserved = ComplexSearchUtils.checkNumericSearch(filter, LOW_OR_RESERVED, Constants.INVENTORY_SCALE);

                if (lowOrReserved != null) {

                    if ("-".equals(lowOrReserved.getFirst())) {
                        criteria.add(Restrictions.le("quantity", lowOrReserved.getSecond()));
                    } else if ("+".equals(lowOrReserved.getFirst())) {
                        criteria.add(Restrictions.ge("reserved", lowOrReserved.getSecond()));
                    }

                } else {

                    final Pair<String, String> byCode = ComplexSearchUtils.checkSpecialSearch(filter, CODE);

                    if (byCode != null) {


                        final List<ProductSku> skus = productSkuDAO.findByCriteria(new CriteriaTuner() {
                            public void tune(final Criteria crit) {
                                crit.createAlias("product", "prod");
                                crit.setFetchMode("prod", FetchMode.JOIN);
                            }
                        }, Restrictions.or(
                                Restrictions.or(
                                        Restrictions.or(
                                                Restrictions.ilike("prod.code", byCode.getSecond(), MatchMode.EXACT),
                                                Restrictions.ilike("barCode", byCode.getSecond(), MatchMode.EXACT)
                                        ),
                                        Restrictions.or(
                                                Restrictions.ilike("code", byCode.getSecond(), MatchMode.EXACT),
                                                Restrictions.ilike("manufacturerCode", byCode.getSecond(), MatchMode.EXACT)
                                        )

                                ),
                                Restrictions.or(
                                        Restrictions.ilike("prod.manufacturerCode", byCode.getSecond(), MatchMode.EXACT),
                                        Restrictions.ilike("prod.pimCode", byCode.getSecond(), MatchMode.EXACT)
                                )
                        ));

                        final List<String> skuCodes = new ArrayList<String>();
                        for (final ProductSku sku : skus) {
                            skuCodes.add(sku.getCode()); // sku codes from product match
                        }

                        if (skuCodes.isEmpty()) {
                            criteria.add(Restrictions.ilike("skuCode", byCode.getSecond(), MatchMode.EXACT));
                        } else {
                            criteria.add(
                                    Restrictions.or(
                                            Restrictions.ilike("skuCode", byCode.getSecond(), MatchMode.EXACT),
                                            Restrictions.in("skuCode", skuCodes)
                                    )
                            );
                        }


                    } else {

                        final List<ProductSku> skus = productSkuDAO.findByCriteria(new CriteriaTuner() {
                            public void tune(final Criteria crit) {
                                crit.createAlias("product", "prod");
                                crit.setFetchMode("prod", FetchMode.JOIN);
                            }
                        }, Restrictions.or(
                                Restrictions.or(
                                        Restrictions.ilike("prod.code", filter, MatchMode.ANYWHERE),
                                        Restrictions.ilike("code", filter, MatchMode.ANYWHERE)
                                ),
                                Restrictions.or(
                                        Restrictions.ilike("prod.name", filter, MatchMode.ANYWHERE),
                                        Restrictions.ilike("name", filter, MatchMode.ANYWHERE)
                                )
                        ));

                        final List<String> skuCodes = new ArrayList<String>();
                        for (final ProductSku sku : skus) {
                            skuCodes.add(sku.getCode()); // sku codes from product match
                        }

                        if (skuCodes.isEmpty()) {
                            criteria.add(Restrictions.ilike("skuCode", filter, MatchMode.ANYWHERE));
                        } else {
                            criteria.add(
                                    Restrictions.or(
                                            Restrictions.ilike("skuCode", filter, MatchMode.ANYWHERE),
                                            Restrictions.in("skuCode", skuCodes)
                                    )
                            );
                        }
                    }
                }
            }

            final List<SkuWarehouse> entities = skuWarehouseDAO.findByCriteria(page * pageSize, pageSize, criteria.toArray(new Criterion[criteria.size()]), INVENTORY_ORDER);

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
            criteria.add(Restrictions.eq("skuCode", inventory.getSkuCode()));

            final List<SkuWarehouse> candidates = skuWarehouseDAO.findByCriteria(new CriteriaTuner() {
                public void tune(final Criteria crit) {
                    crit.createAlias("warehouse", "warehouse");
                    crit.setFetchMode("warehouse", FetchMode.JOIN);
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
            final List<Warehouse> warehouses = warehouseDAO.findByCriteria(Restrictions.eq("code", inventory.getWarehouseCode()));
            if (warehouses == null || warehouses.size() != 1) {
                throw new UnableToCreateInstanceException("Invalid warehouse: " + inventory.getWarehouseCode(), null);
            }

            entity = skuWarehouseDAO.getEntityFactory().getByIface(SkuWarehouse.class);
            entity.setSkuCode(inventory.getSkuCode());
            entity.setWarehouse(warehouses.get(0));
        }

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

}
