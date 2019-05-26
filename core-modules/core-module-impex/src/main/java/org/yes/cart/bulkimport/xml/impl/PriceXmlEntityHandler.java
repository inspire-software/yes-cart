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
import org.yes.cart.bulkimport.xml.internal.PriceType;
import org.yes.cart.domain.entity.SkuPrice;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.domain.PriceService;
import org.yes.cart.service.domain.ShopService;

import java.util.Map;

/**
 * User: denispavlov
 * Date: 05/11/2018
 * Time: 22:23
 */
public class PriceXmlEntityHandler extends AbstractXmlEntityHandler<PriceType, SkuPrice> implements XmlEntityImportHandler<PriceType, SkuPrice> {

    private PriceService priceService;
    private ShopService shopService;

    public PriceXmlEntityHandler() {
        super("price");
    }

    @Override
    protected void delete(final JobStatusListener statusListener, final SkuPrice price, final Map<String, Integer> entityCount) {
        this.priceService.delete(price);
        this.priceService.getGenericDao().flush();
    }

    @Override
    protected void saveOrUpdate(final JobStatusListener statusListener, final SkuPrice domain, final PriceType xmlType, final EntityImportModeType mode, final Map<String, Integer> entityCount) {

        domain.setSkuCode(xmlType.getSku());
        domain.setCurrency(xmlType.getCurrency());
        domain.setQuantity(xmlType.getQuantity());
        domain.setPriceUponRequest(xmlType.isRequest());
        domain.setPriceOnOffer(xmlType.isOffer());
        domain.setRegularPrice(xmlType.getListPrice());
        domain.setSalePrice(xmlType.getSalePrice());
        domain.setMinimalPrice(xmlType.getMinimalPrice());
        if (xmlType.getAvailability() != null) {
            domain.setSalefrom(processLDT(xmlType.getAvailability().getAvailableFrom()));
            domain.setSaleto(processLDT(xmlType.getAvailability().getAvailableTo()));
        }
        domain.setTag(processTags(xmlType.getTags(), domain.getTag()));
        if (xmlType.getPricingPolicy() != null) {
            domain.setPricingPolicy(xmlType.getPricingPolicy().getPolicy());
            domain.setRef(xmlType.getPricingPolicy().getRef());
        }

        if (domain.getSkuPriceId() == 0L) {
            this.priceService.create(domain);
        } else {
            this.priceService.update(domain);
        }
        this.priceService.getGenericDao().flush();
        this.priceService.getGenericDao().evict(domain);
    }

    @Override
    protected SkuPrice getOrCreate(final JobStatusListener statusListener, final PriceType xmlType, final Map<String, Integer> entityCount) {
        SkuPrice price = this.priceService.findSingleByCriteria(" where e.guid = ?1", xmlType.getGuid());
        if (price != null) {
            return price;
        }
        price = this.priceService.getGenericDao().getEntityFactory().getByIface(SkuPrice.class);
        price.setGuid(xmlType.getGuid());
        price.setShop(this.shopService.findSingleByCriteria(" where e.code = ?1", xmlType.getShop()));
        return price;
    }

    @Override
    protected EntityImportModeType determineImportMode(final PriceType xmlType) {
        return xmlType.getImportMode() != null ? xmlType.getImportMode() : EntityImportModeType.MERGE;
    }

    @Override
    protected boolean isNew(final SkuPrice domain) {
        return domain.getSkuPriceId() == 0L;
    }

    /**
     * Spring IoC.
     *
     * @param priceService price service
     */
    public void setPriceService(final PriceService priceService) {
        this.priceService = priceService;
    }

    /**
     * Spring IoC.
     *
     * @param shopService shop service
     */
    public void setShopService(final ShopService shopService) {
        this.shopService = shopService;
    }
}
