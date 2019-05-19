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
import org.yes.cart.domain.entity.Tax;
import org.yes.cart.service.async.JobStatusListener;

import java.io.OutputStreamWriter;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 26/10/2018
 * Time: 08:08
 */
public class TaxXmlEntityHandler extends AbstractXmlEntityHandler<Tax> {

    public TaxXmlEntityHandler() {
        super("taxes");
    }

    @Override
    public void handle(final JobStatusListener statusListener,
                       final XmlExportDescriptor xmlExportDescriptor,
                       final ImpExTuple<String, Tax> tuple,
                       final XmlValueAdapter xmlValueAdapter,
                       final String fileToExport,
                       final OutputStreamWriter writer,
                       final Map<String, Integer> entityCount) throws Exception {

        handleInternal(tagTax(null, tuple.getData()), writer, entityCount);

    }

    Tag tagTax(final Tag parent, final Tax tax) {

        return tag(parent, "tax")
                .attr("id", tax.getTaxId())
                .attr("guid", tax.getGuid())
                .attr("code", tax.getCode())
                .attr("shop", tax.getShopCode())
                .attr("currency", tax.getCurrency())
                    .tagCdata("description", tax.getDescription())
                    .tag("rate")
                        .attr("gross", tax.getExclusiveOfPrice())
                        .chars(tax.getTaxRate().toPlainString())
                    .end()
                    .tagTime(tax)
                .end();

    }

}
