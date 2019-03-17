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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.bulkimport.xml.XmlEntityImportHandler;
import org.yes.cart.bulkimport.xml.internal.CollectionImportModeType;
import org.yes.cart.bulkimport.xml.internal.EntityImportModeType;
import org.yes.cart.bulkimport.xml.internal.ShopFulfilmentCentreType;
import org.yes.cart.bulkimport.xml.internal.ShopFulfilmentCentresCodeType;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.entity.ShopWarehouse;
import org.yes.cart.domain.entity.Warehouse;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.service.domain.WarehouseService;

import java.util.List;

/**
 * User: denispavlov
 * Date: 05/11/2018
 * Time: 22:23
 */
public class ShopFulfilmentCentreXmlEntityHandler extends AbstractXmlEntityHandler<ShopFulfilmentCentresCodeType, Shop> implements XmlEntityImportHandler<ShopFulfilmentCentresCodeType> {

    private static final Logger LOG = LoggerFactory.getLogger(ShopFulfilmentCentreXmlEntityHandler.class);

    private WarehouseService warehouseService;
    private ShopService shopService;

    public ShopFulfilmentCentreXmlEntityHandler() {
        super("shop-fulfilment-centres");
    }

    @Override
    protected void delete(final Shop shop) {
        throw new UnsupportedOperationException("Shop delete mode is not supported");
    }

    @Override
    protected void saveOrUpdate(final Shop domain, final ShopFulfilmentCentresCodeType xmlType, final EntityImportModeType mode) {

        if (domain != null) {
            processCentres(domain, xmlType);

            if (domain.getShopId() == 0L) {
                this.shopService.create(domain);
            } else {
                this.shopService.update(domain);
            }
            this.shopService.getGenericDao().flush();
            this.shopService.getGenericDao().evict(domain);
        }

    }

    private void processCentres(final Shop domain, final ShopFulfilmentCentresCodeType xmlType) {

        final CollectionImportModeType collectionMode = xmlType.getImportMode() != null ? xmlType.getImportMode() : CollectionImportModeType.MERGE;

        final List<Warehouse> centres = this.warehouseService.findByShopId(domain.getShopId(), true);

        if (collectionMode == CollectionImportModeType.REPLACE) {

            for (final Warehouse centre : centres) {

                centre.getWarehouseShop().removeIf(ws -> ws.getShop().getShopId() == domain.getShopId());
                this.warehouseService.update(centre);
                this.warehouseService.getGenericDao().flush();

            }

        }

        for (final ShopFulfilmentCentreType ff : xmlType.getShopFulfilmentCentre()) {
            final EntityImportModeType itemMode = ff.getImportMode() != null ? ff.getImportMode() : EntityImportModeType.MERGE;
            if (itemMode == EntityImportModeType.DELETE) {
                if (ff.getGuid() != null) {
                    processCarriersRemove(domain, ff);
                }
            } else {
                processCentresSave(domain, ff);
            }
        }

    }

    private void processCentresSave(final Shop domain, final ShopFulfilmentCentreType ff) {

        final Warehouse centre = this.warehouseService.findSingleByCriteria(" where e.guid = ?1", ff.getGuid());
        if (centre == null) {
            LOG.warn("Fulfilment centre {} for shop {} is not found and will be skipped", ff.getGuid(), domain.getCode());
            return;
        }

        for (final ShopWarehouse sw : centre.getWarehouseShop()) {
            if (domain.getShopId() == sw.getShop().getShopId()) {
                processCentresSaveBasic(ff, sw);
                this.warehouseService.update(centre);
                this.warehouseService.getGenericDao().flush();
                return;
            }
        }
        final ShopWarehouse cs = this.warehouseService.getGenericDao().getEntityFactory().getByIface(ShopWarehouse.class);
        cs.setShop(domain);
        cs.setWarehouse(centre);
        processCentresSaveBasic(ff, cs);
        centre.getWarehouseShop().add(cs);
        this.warehouseService.update(centre);
        this.warehouseService.getGenericDao().flush();

    }

    private void processCentresSaveBasic(final ShopFulfilmentCentreType cr, final ShopWarehouse sw) {
        sw.setDisabled(cr.isDisabled());
    }

    private void processCarriersRemove(final Shop domain, final ShopFulfilmentCentreType cr) {

        final Warehouse centre = this.warehouseService.findSingleByCriteria(" where e.guid = ?1", cr.getGuid());

        centre.getWarehouseShop().removeIf(ws -> ws.getShop().getShopId() == domain.getShopId());
        this.warehouseService.update(centre);
        this.warehouseService.getGenericDao().flush();

    }

    @Override
    protected Shop getOrCreate(final ShopFulfilmentCentresCodeType xmlType) {
        Shop shop = this.shopService.findSingleByCriteria(" where e.code = ?1", xmlType.getShopCode());
        if (shop != null) {
            return shop;
        }
        return null;
    }

    @Override
    protected EntityImportModeType determineImportMode(final ShopFulfilmentCentresCodeType xmlType) {
        return EntityImportModeType.MERGE;
    }

    @Override
    protected boolean isNew(final Shop domain) {
        return domain.getShopId() == 0L;
    }


    /**
     * Spring IoC.
     *
     * @param shopService shop service
     */
    public void setShopService(final ShopService shopService) {
        this.shopService = shopService;
    }

    /**
     * Spring IoC.
     *
     * @param warehouseService centre service
     */
    public void setWarehouseService(final WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }
}
