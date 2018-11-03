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
import org.yes.cart.domain.entity.SkuWarehouse;
import org.yes.cart.service.async.JobStatusListener;

/**
 * User: denispavlov
 * Date: 03/11/2018
 * Time: 11:42
 */
public class SkuWarehouseXmlEntityHandler extends AbstractXmlEntityHandler<SkuWarehouse> {


    public SkuWarehouseXmlEntityHandler() {
        super("inventory");
    }

    @Override
    public String handle(final JobStatusListener statusListener,
                         final XmlExportDescriptor xmlExportDescriptor,
                         final ImpExTuple<String, SkuWarehouse> tuple,
                         final XmlValueAdapter xmlValueAdapter,
                         final String fileToExport) {

        return tagInventory(null, tuple.getData()).toXml();

    }


    Tag tagInventory(final Tag parent, final SkuWarehouse inventory) {

        return tag(parent, "record")
                .attr("id", inventory.getSkuWarehouseId())
                .attr("guid", inventory.getGuid())
                .attr("sku", inventory.getSkuCode())
                .attr("warehouse", inventory.getWarehouse().getCode())
                .tag("quantity")
                    .attr("type", "stock")
                    .chars(inventory.getQuantity().toPlainString())
                .end()
                .tag("quantity")
                    .attr("type", "reserved")
                    .chars(inventory.getReserved().toPlainString())
                .end()
                .tag("quantity")
                    .attr("type", "ats")
                    .chars(inventory.getAvailableToSell().toPlainString())
                .end()
            .end();

    }

}
