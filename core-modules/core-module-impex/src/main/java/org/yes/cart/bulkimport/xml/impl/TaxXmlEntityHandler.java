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
import org.yes.cart.bulkimport.xml.internal.TaxType;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.entity.Tax;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.service.domain.TaxService;

/**
 * User: denispavlov
 * Date: 05/11/2018
 * Time: 22:23
 */
public class TaxXmlEntityHandler extends AbstractXmlEntityHandler<TaxType, Tax> implements XmlEntityImportHandler<TaxType, Tax> {

    private TaxService taxService;
    private ShopService shopService;

    public TaxXmlEntityHandler() {
        super("tax");
    }

    @Override
    protected void delete(final JobStatusListener statusListener, final Tax tax) {
        this.taxService.delete(tax);
        this.taxService.getGenericDao().flush();
    }

    @Override
    protected void saveOrUpdate(final JobStatusListener statusListener, final Tax domain, final TaxType xmlType, final EntityImportModeType mode) {

        domain.setCode(xmlType.getCode());
        domain.setCurrency(xmlType.getCurrency());

        domain.setTaxRate(xmlType.getRate().getValue());
        domain.setExclusiveOfPrice(xmlType.getRate().isGross());

        domain.setDescription(xmlType.getDescription());

        if (domain.getTaxId() == 0L) {
            this.taxService.create(domain);
        } else {
            this.taxService.update(domain);
        }
        this.taxService.getGenericDao().flush();
        this.taxService.getGenericDao().evict(domain);
    }

    @Override
    protected Tax getOrCreate(final JobStatusListener statusListener, final TaxType xmlType) {
        Tax tax = this.taxService.findSingleByCriteria(" where e.guid = ?1", xmlType.getGuid());
        if (tax != null) {
            return tax;
        }
        tax = this.taxService.getGenericDao().getEntityFactory().getByIface(Tax.class);
        tax.setGuid(xmlType.getGuid());
        final Shop shop = this.shopService.findSingleByCriteria(" where e.code = ?1", xmlType.getShop());
        if (shop != null) {
            tax.setShopCode(shop.getCode());
        }
        return tax;
    }

    @Override
    protected EntityImportModeType determineImportMode(final TaxType xmlType) {
        return xmlType.getImportMode() != null ? xmlType.getImportMode() : EntityImportModeType.MERGE;
    }

    @Override
    protected boolean isNew(final Tax domain) {
        return domain.getTaxId() == 0L;
    }

    /**
     * Spring IoC.
     *
     * @param taxService tax service
     */
    public void setTaxService(final TaxService taxService) {
        this.taxService = taxService;
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
