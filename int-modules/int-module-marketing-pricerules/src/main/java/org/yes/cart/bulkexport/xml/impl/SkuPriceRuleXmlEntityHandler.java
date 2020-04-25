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

package org.yes.cart.bulkexport.xml.impl;

import org.yes.cart.bulkcommon.model.ImpExTuple;
import org.yes.cart.bulkcommon.xml.XmlValueAdapter;
import org.yes.cart.bulkexport.xml.XmlExportDescriptor;
import org.yes.cart.domain.entity.SkuPriceRule;
import org.yes.cart.service.async.JobStatusListener;

import java.io.OutputStreamWriter;

/**
 * User: denispavlov
 * Date: 04/11/2018
 * Time: 14:30
 */
public class SkuPriceRuleXmlEntityHandler extends AbstractXmlEntityHandler<SkuPriceRule> {

    public SkuPriceRuleXmlEntityHandler() {
        super("price-rules");
    }

    @Override
    public void handle(final JobStatusListener statusListener,
                       final XmlExportDescriptor xmlExportDescriptor,
                       final ImpExTuple<String, SkuPriceRule> tuple,
                       final XmlValueAdapter xmlValueAdapter,
                       final String fileToExport,
                       final OutputStreamWriter writer) throws Exception {

        handleInternal(tagPrice(null, tuple.getData()), writer, statusListener);

    }

    Tag tagPrice(final Tag parent, final SkuPriceRule priceRule) {

        return tag(parent, "price-rule")
                .attr("id", priceRule.getSkuPriceRuleId())
                .attr("guid", priceRule.getGuid())
                .attr("code", priceRule.getCode())
                .attr("shop", priceRule.getShopCode())
                .attr("currency", priceRule.getCurrency())
                .attr("rank", priceRule.getRank())
                .tagCdata("name", priceRule.getName())
                .tagCdata("description", priceRule.getDescription())
                .tagList("tags", "tag", priceRule.getTag(), ' ')
                .tag("availability")
                    .attr("disabled", !priceRule.isEnabled())
                    .tagTime("available-from", priceRule.getEnabledFrom())
                    .tagTime("available-to", priceRule.getEnabledTo())
                .end()
                .tag("configuration")
                    .attr("action", priceRule.getRuleAction())
                    .attr("margin-percent", priceRule.getMarginPercent())
                    .attr("margin-amount", priceRule.getMarginAmount())
                    .attr("add-default-tax", priceRule.isAddDefaultTax())
                    .attr("rounding-unit", priceRule.getRoundingUnit())
                    .attr("price-tag", priceRule.getPriceTag())
                    .attr("price-ref", priceRule.getPriceRef())
                    .attr("price-policy", priceRule.getPricePolicy())
                    .cdata(priceRule.getEligibilityCondition())
                .end()
                .tagTime(priceRule)
                .end();

    }

}
