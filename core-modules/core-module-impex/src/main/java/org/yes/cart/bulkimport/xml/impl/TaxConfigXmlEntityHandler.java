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

import org.apache.commons.lang.StringUtils;
import org.yes.cart.bulkimport.xml.XmlEntityImportHandler;
import org.yes.cart.bulkimport.xml.internal.EntityImportModeType;
import org.yes.cart.bulkimport.xml.internal.TaxConfigType;
import org.yes.cart.domain.entity.TaxConfig;
import org.yes.cart.service.domain.TaxConfigService;
import org.yes.cart.service.domain.TaxService;

/**
 * User: denispavlov
 * Date: 05/11/2018
 * Time: 22:23
 */
public class TaxConfigXmlEntityHandler extends AbstractXmlEntityHandler<TaxConfigType, TaxConfig> implements XmlEntityImportHandler<TaxConfigType> {

    private TaxConfigService taxConfigService;
    private TaxService taxService;

    public TaxConfigXmlEntityHandler() {
        super("tax-config");
    }

    @Override
    protected void delete(final TaxConfig tax) {
        this.taxConfigService.delete(tax);
        this.taxConfigService.getGenericDao().flush();
    }

    @Override
    protected void saveOrUpdate(final TaxConfig domain, final TaxConfigType xmlType, final EntityImportModeType mode) {

        domain.setProductCode(StringUtils.isNotBlank(xmlType.getSku()) ? xmlType.getSku() : null);
        if (xmlType.getTaxRegion() != null) {
            domain.setStateCode(StringUtils.isNotBlank(xmlType.getTaxRegion().getRegionCode()) ? xmlType.getTaxRegion().getRegionCode() : null);
            domain.setCountryCode(StringUtils.isNotBlank(xmlType.getTaxRegion().getIso31661Alpha2()) ? xmlType.getTaxRegion().getIso31661Alpha2() : null);
        }

        if (domain.getTaxConfigId() == 0L) {
            this.taxConfigService.create(domain);
        } else {
            this.taxConfigService.update(domain);
        }
        this.taxConfigService.getGenericDao().flush();
        this.taxConfigService.getGenericDao().evict(domain);
    }

    @Override
    protected TaxConfig getOrCreate(final TaxConfigType xmlType) {
        TaxConfig taxCfg = this.taxConfigService.findSingleByCriteria(" where e.guid = ?1", xmlType.getGuid());
        if (taxCfg != null) {
            return taxCfg;
        }
        taxCfg = this.taxConfigService.getGenericDao().getEntityFactory().getByIface(TaxConfig.class);
        taxCfg.setGuid(xmlType.getGuid());
        taxCfg.setTax(this.taxService.findSingleByCriteria(" where e.guid = ?1", xmlType.getTax()));
        return taxCfg;
    }

    @Override
    protected EntityImportModeType determineImportMode(final TaxConfigType xmlType) {
        return xmlType.getImportMode() != null ? xmlType.getImportMode() : EntityImportModeType.MERGE;
    }

    @Override
    protected boolean isNew(final TaxConfig domain) {
        return domain.getTaxConfigId() == 0L;
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
     * @param taxConfigService config service
     */
    public void setTaxConfigService(final TaxConfigService taxConfigService) {
        this.taxConfigService = taxConfigService;
    }
}
