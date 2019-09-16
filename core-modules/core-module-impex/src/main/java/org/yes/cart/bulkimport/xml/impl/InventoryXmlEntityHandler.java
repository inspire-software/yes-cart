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

package org.yes.cart.bulkimport.xml.impl;

import org.yes.cart.bulkimport.xml.XmlEntityImportHandler;
import org.yes.cart.bulkimport.xml.internal.EntityImportModeType;
import org.yes.cart.bulkimport.xml.internal.QuantityType;
import org.yes.cart.bulkimport.xml.internal.QuantityTypeType;
import org.yes.cart.bulkimport.xml.internal.StockType;
import org.yes.cart.domain.entity.SkuWarehouse;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.domain.SkuWarehouseService;
import org.yes.cart.service.domain.WarehouseService;

import java.math.BigDecimal;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 05/11/2018
 * Time: 22:23
 */
public class InventoryXmlEntityHandler extends AbstractXmlEntityHandler<StockType, SkuWarehouse> implements XmlEntityImportHandler<StockType, SkuWarehouse> {

    private SkuWarehouseService skuWarehouseService;
    private WarehouseService warehouseService;

    public InventoryXmlEntityHandler() {
        super("stock");
    }

    @Override
    protected void delete(final JobStatusListener statusListener, final SkuWarehouse inventory, final Map<String, Integer> entityCount) {
        inventory.setQuantity(BigDecimal.ZERO);
        this.skuWarehouseService.update(inventory);
        this.skuWarehouseService.getGenericDao().flush();
        this.skuWarehouseService.getGenericDao().evict(inventory);
    }

    @Override
    protected void saveOrUpdate(final JobStatusListener statusListener, final SkuWarehouse domain, final StockType xmlType, final EntityImportModeType mode, final Map<String, Integer> entityCount) {

        if (xmlType.getAvailability() != null) {
            domain.setDisabled(xmlType.getAvailability().isDisabled());
            domain.setAvailablefrom(processLDT(xmlType.getAvailability().getAvailableFrom()));
            domain.setAvailableto(processLDT(xmlType.getAvailability().getAvailableTo()));
            domain.setReleaseDate(processLDT(xmlType.getAvailability().getReleaseDate()));
        }

        if (xmlType.getInventoryConfig() != null) {
            domain.setAvailability(xmlType.getInventoryConfig().getType());
            domain.setMinOrderQuantity(xmlType.getInventoryConfig().getMin());
            domain.setMaxOrderQuantity(xmlType.getInventoryConfig().getMax());
            domain.setStepOrderQuantity(xmlType.getInventoryConfig().getStep());
            domain.setFeatured(xmlType.getInventoryConfig().isFeatured());
        }

        for (final QuantityType qt : xmlType.getQuantity()) {
            if (qt.getType() == QuantityTypeType.STOCK) {
                domain.setQuantity(qt.getValue());
            }
        }
        if (domain.getSkuWarehouseId() == 0L) {
            this.skuWarehouseService.create(domain);
        } else {
            this.skuWarehouseService.update(domain);
        }
        this.skuWarehouseService.getGenericDao().flush();
        this.skuWarehouseService.getGenericDao().evict(domain);
    }

    @Override
    protected SkuWarehouse getOrCreate(final JobStatusListener statusListener, final StockType xmlType, final Map<String, Integer> entityCount) {
        SkuWarehouse inventory = this.skuWarehouseService.findSingleByCriteria(" where e.skuCode = ?1 and e.warehouse.code = ?2", xmlType.getSku(), xmlType.getWarehouse());
        if (inventory != null) {
            return inventory;
        }
        inventory = this.skuWarehouseService.getGenericDao().getEntityFactory().getByIface(SkuWarehouse.class);
        inventory.setSkuCode(xmlType.getSku());
        inventory.setAvailability(SkuWarehouse.AVAILABILITY_STANDARD);
        inventory.setFeatured(false);
        inventory.setWarehouse(this.warehouseService.findSingleByCriteria(" where e.code = ?1", xmlType.getWarehouse()));
        return inventory;
    }

    @Override
    protected EntityImportModeType determineImportMode(final StockType xmlType) {
        return xmlType.getImportMode() != null ? xmlType.getImportMode() : EntityImportModeType.MERGE;
    }

    @Override
    protected boolean isNew(final SkuWarehouse domain) {
        return domain.getSkuWarehouseId() == 0L;
    }

    /**
     * Spring IoC.
     *
     * @param skuWarehouseService SKU warehouse service
     */
    public void setSkuWarehouseService(final SkuWarehouseService skuWarehouseService) {
        this.skuWarehouseService = skuWarehouseService;
    }

    /**
     * Spring IoC.
     *
     * @param warehouseService warehouse service
     */
    public void setWarehouseService(final WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }
}
