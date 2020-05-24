/*
 * Copyright 2009 Inspire-Software.com
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
import org.yes.cart.domain.entity.TaxConfig;
import org.yes.cart.service.async.JobStatusListener;

import java.io.OutputStreamWriter;

/**
 * User: denispavlov
 * Date: 26/10/2018
 * Time: 08:08
 */
public class TaxConfigXmlEntityHandler extends AbstractXmlEntityHandler<TaxConfig> {

    public TaxConfigXmlEntityHandler() {
        super("tax-configs");
    }

    @Override
    public void handle(final JobStatusListener statusListener,
                       final XmlExportDescriptor xmlExportDescriptor,
                       final ImpExTuple<String, TaxConfig> tuple,
                       final XmlValueAdapter xmlValueAdapter,
                       final String fileToExport,
                       final OutputStreamWriter writer) throws Exception {

        handleInternal(tagTaxConfig(null, tuple.getData()), writer, statusListener);

    }

    Tag tagTaxConfig(final Tag parent, final TaxConfig taxConfig) {

        return tag(parent, "tax-config")
                .attr("id", taxConfig.getTaxConfigId())
                .attr("guid", taxConfig.getGuid())
                .attr("tax", taxConfig.getTax().getCode())
                    .tagChars("sku", taxConfig.getProductCode())
                    .tag("tax-region")
                        .attr("iso-3166-1-alpha2", taxConfig.getCountryCode())
                        .attr("region-code", taxConfig.getStateCode())
                    .end()
                    .tagTime(taxConfig)
                .end();

    }

}
