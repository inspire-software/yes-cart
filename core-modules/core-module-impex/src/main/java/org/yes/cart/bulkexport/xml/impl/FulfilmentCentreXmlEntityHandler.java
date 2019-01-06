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
import org.yes.cart.domain.entity.Warehouse;
import org.yes.cart.service.async.JobStatusListener;

/**
 * User: denispavlov
 * Date: 26/10/2018
 * Time: 08:08
 */
public class FulfilmentCentreXmlEntityHandler extends AbstractXmlEntityHandler<Warehouse> {

    public FulfilmentCentreXmlEntityHandler() {
        super("fulfilment-centres");
    }

    @Override
    public String handle(final JobStatusListener statusListener,
                         final XmlExportDescriptor xmlExportDescriptor,
                         final ImpExTuple<String, Warehouse> tuple,
                         final XmlValueAdapter xmlValueAdapter,
                         final String fileToExport) {

        return tagAttribute(null, tuple.getData()).toXml();
        
    }

    Tag tagAttribute(final Tag parent, final Warehouse fc) {

        return tag(parent, "fulfilment-centre")
                .attr("id", fc.getWarehouseId())
                .attr("guid", fc.getGuid())
                .attr("code", fc.getCode())
                    .tagCdata("name", fc.getName())
                    .tagI18n("display-name", fc.getDisplayName())
                    .tagCdata("description", fc.getDescription())
                    .tag("location")
                        .tagChars("iso-3166-1-alpha2", fc.getCountryCode())
                        .tagChars("region-code", fc.getStateCode())
                        .tagChars("city", fc.getCity())
                        .tagChars("postcode", fc.getPostcode())
                    .end()
                    .tag("configuration")
                        .tagNum("standard-stock-lead-time", fc.getDefaultStandardStockLeadTime())
                        .tagNum("backorder-stock-lead-time", fc.getDefaultBackorderStockLeadTime())
                        .tagBool("multiple-shipping-supported", fc.isMultipleShippingSupported())
                    .end()
                    .tagTime(fc)
                .end();

    }

}
