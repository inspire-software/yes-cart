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
import org.yes.cart.bulkimport.xml.internal.FulfilmentCentreType;
import org.yes.cart.domain.entity.Warehouse;
import org.yes.cart.service.domain.AttributeGroupService;
import org.yes.cart.service.domain.EtypeService;
import org.yes.cart.service.domain.WarehouseService;

/**
 * User: denispavlov
 * Date: 05/11/2018
 * Time: 22:23
 */
public class FulfilmentCentreXmlEntityHandler extends AbstractXmlEntityHandler<FulfilmentCentreType, Warehouse> implements XmlEntityImportHandler<FulfilmentCentreType> {

    private WarehouseService warehouseService;
    private AttributeGroupService attributeGroupService;
    private EtypeService etypeService;

    public FulfilmentCentreXmlEntityHandler() {
        super("fulfilment-centre");
    }

    @Override
    protected void delete(final Warehouse fc) {
        this.warehouseService.delete(fc);
        this.warehouseService.getGenericDao().flush();
    }

    @Override
    protected void saveOrUpdate(final Warehouse domain, final FulfilmentCentreType xmlType, final EntityImportModeType mode) {
        domain.setName(xmlType.getName());
        domain.setDisplayName(processI18n(xmlType.getDisplayName(), domain.getDisplayName()));
        domain.setDescription(xmlType.getDescription());
        if (xmlType.getLocation() != null) {
            domain.setCountryCode(xmlType.getLocation().getIso31661Alpha2());
            domain.setStateCode(xmlType.getLocation().getRegionCode());
            domain.setCity(xmlType.getLocation().getCity());
            domain.setPostcode(xmlType.getLocation().getPostcode());
        }
        if (xmlType.getConfiguration() != null) {
            domain.setDefaultStandardStockLeadTime(xmlType.getConfiguration().getStandardStockLeadTime());
            domain.setDefaultBackorderStockLeadTime(xmlType.getConfiguration().getBackorderStockLeadTime());
            domain.setMultipleShippingSupported(xmlType.getConfiguration().isMultipleShippingSupported());
        }
        if (domain.getWarehouseId() == 0L) {
            this.warehouseService.create(domain);
        } else {
            this.warehouseService.update(domain);
        }
        this.warehouseService.getGenericDao().flush();
        this.warehouseService.getGenericDao().evict(domain);
    }

    @Override
    protected Warehouse getOrCreate(final FulfilmentCentreType xmlType) {
        Warehouse warehouse = this.warehouseService.findSingleByCriteria(" where e.code = ?1", xmlType.getCode());
        if (warehouse != null) {
            return warehouse;
        }
        warehouse = this.warehouseService.getGenericDao().getEntityFactory().getByIface(Warehouse.class);
        warehouse.setGuid(xmlType.getCode());
        warehouse.setCode(xmlType.getCode());
        return warehouse;
    }

    @Override
    protected EntityImportModeType determineImportMode(final FulfilmentCentreType xmlType) {
        return xmlType.getImportMode() != null ? xmlType.getImportMode() : EntityImportModeType.MERGE;
    }

    @Override
    protected boolean isNew(final Warehouse domain) {
        return domain.getWarehouseId() == 0L;
    }

    /**
     * Spring IoC.
     *
     * @param warehouseService attribute service
     */
    public void setWarehouseService(final WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }
}
