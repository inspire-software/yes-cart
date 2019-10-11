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

package org.yes.cart.report.impl;

import org.yes.cart.domain.vo.VoInventory;
import org.yes.cart.service.vo.VoFulfilmentService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 11/10/2019
 * Time: 16:20
 */
public class InventoryArrayReportWorker extends VoInventoryListReportWorker {


    public InventoryArrayReportWorker(final VoFulfilmentService fulfilmentService) {
        super(fulfilmentService);
    }

    @Override
    public List<Object> getResult(final String lang, final Map<String, Object> currentSelection) {
        final List<VoInventory> result = (List) super.getResult(lang, currentSelection);
        final List<Object[]> out = new ArrayList<>();

        out.add(new Object[] {
                "Fulfilment centre code",
                "Fulfilment centre name",
                "SKU",
                "Product name",
                "Quantity",
                "Reserved"
        });

        for (final VoInventory vo : result) {

            out.add(new Object[] {
                    vo.getWarehouseCode(),
                    vo.getWarehouseName(),
                    vo.getSkuCode(),
                    vo.getSkuName(),
                    vo.getQuantity(),
                    vo.getReserved()
            });

        }

        return (List) out;

    }
}
