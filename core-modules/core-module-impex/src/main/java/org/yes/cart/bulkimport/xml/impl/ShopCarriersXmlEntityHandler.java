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
import org.yes.cart.bulkimport.xml.internal.CollectionImportModeType;
import org.yes.cart.bulkimport.xml.internal.EntityImportModeType;
import org.yes.cart.bulkimport.xml.internal.ShopCarrierType;
import org.yes.cart.bulkimport.xml.internal.ShopCarriersCodeType;
import org.yes.cart.domain.entity.Carrier;
import org.yes.cart.domain.entity.CarrierShop;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.domain.CarrierService;
import org.yes.cart.service.domain.ShopService;

import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 05/11/2018
 * Time: 22:23
 */
public class ShopCarriersXmlEntityHandler extends AbstractXmlEntityHandler<ShopCarriersCodeType, Shop> implements XmlEntityImportHandler<ShopCarriersCodeType, Shop> {

    private CarrierService carrierService;
    private ShopService shopService;

    public ShopCarriersXmlEntityHandler() {
        super("shop-carriers");
    }

    @Override
    protected void delete(final JobStatusListener statusListener, final Shop shop, final Map<String, Integer> entityCount) {
        throw new UnsupportedOperationException("Shop delete mode is not supported");
    }

    @Override
    protected void saveOrUpdate(final JobStatusListener statusListener, final Shop domain, final ShopCarriersCodeType xmlType, final EntityImportModeType mode, final Map<String, Integer> entityCount) {

        if (domain != null) {
            processCarriers(statusListener, domain, xmlType);

            if (domain.getShopId() == 0L) {
                this.shopService.create(domain);
            } else {
                this.shopService.update(domain);
            }
            this.shopService.getGenericDao().flush();
            this.shopService.getGenericDao().evict(domain);
        }

    }

    private void processCarriers(final JobStatusListener statusListener, final Shop domain, final ShopCarriersCodeType xmlType) {

        final CollectionImportModeType collectionMode = xmlType.getImportMode() != null ? xmlType.getImportMode() : CollectionImportModeType.MERGE;

        final List<Carrier> carriers = this.carrierService.findCarriersByShopId(domain.getShopId(), true);

        if (collectionMode == CollectionImportModeType.REPLACE) {

            for (final Carrier carrier : carriers) {

                carrier.getShops().removeIf(sc -> sc.getShop().getShopId() == domain.getShopId());
                this.carrierService.update(carrier);
                this.carrierService.getGenericDao().flush();

            }

        }

        for (final ShopCarrierType sc : xmlType.getShopCarrier()) {
            final EntityImportModeType itemMode = sc.getImportMode() != null ? sc.getImportMode() : EntityImportModeType.MERGE;
            if (itemMode == EntityImportModeType.DELETE) {
                if (sc.getGuid() != null) {
                    processCarriersRemove(domain, sc);
                }
            } else {
                processCarriersSave(statusListener, domain, sc);
            }
        }

    }

    private void processCarriersSave(final JobStatusListener statusListener, final Shop domain, final ShopCarrierType cr) {

        final Carrier carrier = this.carrierService.findSingleByCriteria(" where e.guid = ?1", cr.getGuid());
        if (carrier == null) {
            statusListener.notifyWarning("Carrier {} for shop {} is not found and will be skipped", cr.getGuid(), domain.getCode());
            return;
        }

        for (final CarrierShop cs : carrier.getShops()) {
            if (domain.getShopId() == cs.getShop().getShopId()) {
                processCategoriesSaveBasic(cr, cs);
                this.carrierService.update(carrier);
                this.carrierService.getGenericDao().flush();
                return;
            }
        }
        final CarrierShop cs = this.carrierService.getGenericDao().getEntityFactory().getByIface(CarrierShop.class);
        cs.setShop(domain);
        cs.setCarrier(carrier);
        processCategoriesSaveBasic(cr, cs);
        carrier.getShops().add(cs);
        this.carrierService.update(carrier);
        this.carrierService.getGenericDao().flush();

    }

    private void processCategoriesSaveBasic(final ShopCarrierType cr, final CarrierShop cs) {
        cs.setDisabled(cr.isDisabled());
    }

    private void processCarriersRemove(final Shop domain, final ShopCarrierType cr) {

        final Carrier carrier = this.carrierService.findSingleByCriteria(" where e.guid = ?1", cr.getGuid());

        carrier.getShops().removeIf(sc -> sc.getShop().getShopId() == domain.getShopId());
        this.carrierService.update(carrier);
        this.carrierService.getGenericDao().flush();

    }

    @Override
    protected Shop getOrCreate(final JobStatusListener statusListener, final ShopCarriersCodeType xmlType, final Map<String, Integer> entityCount) {
        Shop shop = this.shopService.findSingleByCriteria(" where e.code = ?1", xmlType.getShopCode());
        if (shop != null) {
            return shop;
        }
        return null;
    }

    @Override
    protected EntityImportModeType determineImportMode(final ShopCarriersCodeType xmlType) {
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
     * @param carrierService carrier service
     */
    public void setCarrierService(final CarrierService carrierService) {
        this.carrierService = carrierService;
    }
}
