
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
import org.yes.cart.bulkimport.xml.internal.PriceRuleType;
import org.yes.cart.domain.entity.SkuPriceRule;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.domain.GenericService;

/**
 * User: denispavlov
 * Date: 05/11/2018
 * Time: 22:23
 */
public class PriceRuleXmlEntityHandler extends AbstractXmlEntityHandler<PriceRuleType, SkuPriceRule> implements XmlEntityImportHandler<PriceRuleType, SkuPriceRule> {

    private GenericService<SkuPriceRule> priceRuleService;

    public PriceRuleXmlEntityHandler() {
        super("price-rule");
    }

    @Override
    protected void delete(final JobStatusListener statusListener, final SkuPriceRule rule) {
        this.priceRuleService.delete(rule);
        this.priceRuleService.getGenericDao().flush();
    }

    @Override
    protected void saveOrUpdate(final JobStatusListener statusListener, final SkuPriceRule domain, final PriceRuleType xmlType, final EntityImportModeType mode) {

        domain.setTag(processTags(xmlType.getTags(), domain.getTag()));
        domain.setName(xmlType.getName());
        domain.setDescription(xmlType.getDescription());
        if (xmlType.getRank() != null) {
            domain.setRank(xmlType.getRank());
        }

        if (xmlType.getAvailability() != null) {
            domain.setEnabled(!xmlType.getAvailability().isDisabled());
            domain.setEnabledFrom(processLDT(xmlType.getAvailability().getAvailableFrom()));
            domain.setEnabledTo(processLDT(xmlType.getAvailability().getAvailableTo()));
        }
        if (xmlType.getConfiguration() != null) {
            domain.setRuleAction(xmlType.getConfiguration().getAction());
            domain.setEligibilityCondition(xmlType.getConfiguration().getValue());
            domain.setMarginPercent(xmlType.getConfiguration().getMarginPercent());
            domain.setMarginAmount(xmlType.getConfiguration().getMarginAmount());
            domain.setRoundingUnit(xmlType.getConfiguration().getRoundingUnit());
            domain.setAddDefaultTax(xmlType.getConfiguration().isAddDefaultTax() != null && xmlType.getConfiguration().isAddDefaultTax());
            domain.setPriceTag(xmlType.getConfiguration().getPriceTag());
            domain.setPriceRef(xmlType.getConfiguration().getPriceRef());
            domain.setPricePolicy(xmlType.getConfiguration().getPricePolicy());
        }

        if (domain.getSkuPriceRuleId() == 0L) {
            this.priceRuleService.create(domain);
        } else {
            this.priceRuleService.update(domain);
        }
        this.priceRuleService.getGenericDao().flush();
        this.priceRuleService.getGenericDao().evict(domain);
    }

    @Override
    protected SkuPriceRule getOrCreate(final JobStatusListener statusListener, final PriceRuleType xmlType) {
        SkuPriceRule rule = this.priceRuleService.findSingleByCriteria(" where e.guid = ?1", xmlType.getGuid());
        if (rule != null) {
            return rule;
        }
        rule = this.priceRuleService.getGenericDao().getEntityFactory().getByIface(SkuPriceRule.class);
        rule.setGuid(xmlType.getCode());
        rule.setCode(xmlType.getCode());
        rule.setShopCode(xmlType.getShop());
        rule.setCurrency(xmlType.getCurrency());
        return rule;
    }

    @Override
    protected EntityImportModeType determineImportMode(final PriceRuleType xmlType) {
        return xmlType.getImportMode() != null ? xmlType.getImportMode() : EntityImportModeType.MERGE;
    }

    @Override
    protected boolean isNew(final SkuPriceRule domain) {
        return domain.getSkuPriceRuleId() == 0L;
    }

    /**
     * Spring IoC.
     *
     * @param priceRuleService price rule service
     */
    public void setPriceRuleService(final GenericService<SkuPriceRule> priceRuleService) {
        this.priceRuleService = priceRuleService;
    }
}
