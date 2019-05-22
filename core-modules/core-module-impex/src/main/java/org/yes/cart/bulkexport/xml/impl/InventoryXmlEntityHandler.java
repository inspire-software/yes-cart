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

import java.io.OutputStreamWriter;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 03/11/2018
 * Time: 11:42
 */
public class InventoryXmlEntityHandler extends AbstractXmlEntityHandler<SkuWarehouse> {


    public InventoryXmlEntityHandler() {
        super("inventory");
    }

    @Override
    public void handle(final JobStatusListener statusListener,
                       final XmlExportDescriptor xmlExportDescriptor,
                       final ImpExTuple<String, SkuWarehouse> tuple,
                       final XmlValueAdapter xmlValueAdapter,
                       final String fileToExport,
                       final OutputStreamWriter writer,
                       final Map<String, Integer> entityCount) throws Exception {

        handleInternal(tagInventory(null, tuple.getData()), writer, entityCount);

    }


    Tag tagInventory(final Tag parent, final SkuWarehouse inventory) {

        final Tag tag = tag(parent, "stock")
                .attr("id", inventory.getSkuWarehouseId())
                .attr("guid", inventory.getGuid())
                .attr("sku", inventory.getSkuCode())
                .attr("warehouse", inventory.getWarehouse().getCode());

        if (inventory.getQuantity() != null) {
                   tag(tag, "quantity")
                    .attr("type", "stock")
                        .chars(inventory.getQuantity().toPlainString())
                    .end();
        }
        if (inventory.getReserved() != null) {
                   tag(tag, "quantity")
                    .attr("type", "reserved")
                        .chars(inventory.getReserved().toPlainString())
                    .end();
        }
        return tag
                .tag("quantity")
                    .attr("type", "ats")
                    .chars(inventory.getAvailableToSell().toPlainString())
                .end()
            .end();

    }

}
