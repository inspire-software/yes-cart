/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
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

import org.apache.commons.lang.math.NumberUtils;
import org.yes.cart.domain.dto.InventoryDTO;
import org.yes.cart.domain.dto.WarehouseDTO;
import org.yes.cart.remote.service.RemoteInventoryService;
import org.yes.cart.remote.service.RemoteWarehouseService;
import org.yes.cart.report.ReportWorker;
import org.yes.cart.service.dto.support.impl.InventoryFilterImpl;

import java.util.*;

/**
 * User: denispavlov
 * Date: 12/11/2014
 * Time: 15:19
 */
public class InventoryReportWorker implements ReportWorker {

    private final RemoteWarehouseService remoteWarehouseService;
    private final RemoteInventoryService remoteInventoryService;

    public InventoryReportWorker(final RemoteWarehouseService remoteWarehouseService,
                                 final RemoteInventoryService remoteInventoryService) {
        this.remoteWarehouseService = remoteWarehouseService;
        this.remoteInventoryService = remoteInventoryService;
    }

    /**
     * {@inheritDoc}
     */
    public List<ReportPair> getParameterValues(final String lang, final String param, final Map<String, Object> currentSelection) {
        if ("warehouse".equals(param)) {
            try {
                final List<WarehouseDTO> warehouses = remoteInventoryService.getWarehouses();
                final List<ReportPair> select = new ArrayList<ReportPair>();
                for (final WarehouseDTO warehouse : warehouses) {
                    select.add(new ReportPair(warehouse.getName(), String.valueOf(warehouse.getWarehouseId())));
                }
                return select;
            } catch (Exception e) {
                return Collections.emptyList();
            }
        }

        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    public List<Object> getResult(final String lang, final Map<String, Object> currentSelection) {
        final String warehouse = (String) currentSelection.get("warehouse");
        final String skuCode = (String) currentSelection.get("skuCode");
        final long warehouseId = NumberUtils.toLong(warehouse);
        if (warehouseId > 0L) {
            try {
                final WarehouseDTO warehouseDTO = remoteWarehouseService.getById(warehouseId);
                final InventoryFilterImpl inventoryFilter = new InventoryFilterImpl();
                inventoryFilter.setWarehouse(warehouseDTO);
                inventoryFilter.setProductCode(skuCode);
                final List<InventoryDTO> result = remoteInventoryService.getInventoryList(inventoryFilter);
                Collections.sort(result, new Comparator<InventoryDTO>() {
                    @Override
                    public int compare(final InventoryDTO i1, final InventoryDTO i2) {
                        int comp = i1.getSkuCode().compareTo(i2.getSkuCode());
                        if (comp == 0) {
                            comp = i1.getQuantity().compareTo(i2.getQuantity());
                            if (comp == 0) {
                                return 1;
                            }
                        }
                        return comp;
                    }
                });

                return (List) result;
            } catch (Exception e) {
                // do nothing
            }
        }
        return Collections.emptyList();
    }
}
