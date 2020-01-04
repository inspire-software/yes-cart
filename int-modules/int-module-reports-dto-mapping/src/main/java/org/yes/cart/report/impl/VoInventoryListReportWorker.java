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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.yes.cart.domain.vo.VoFulfilmentCentre;
import org.yes.cart.domain.vo.VoSearchContext;
import org.yes.cart.report.ReportPair;
import org.yes.cart.report.ReportWorker;
import org.yes.cart.service.vo.VoFulfilmentService;

import java.util.*;

/**
 * User: denispavlov
 * Date: 12/11/2014
 * Time: 15:19
 */
public class VoInventoryListReportWorker implements ReportWorker {

    private final VoFulfilmentService fulfilmentService;

    public VoInventoryListReportWorker(final VoFulfilmentService fulfilmentService) {
        this.fulfilmentService = fulfilmentService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ReportPair> getParameterValues(final String lang, final String param, final Map<String, Object> currentSelection) {
        if ("warehouse".equals(param)) {
            try {
                final VoSearchContext context = new VoSearchContext();
                context.setSize(100);
                final List<VoFulfilmentCentre> warehouses = fulfilmentService.getFilteredFulfilmentCentres(context).getItems();
                final List<ReportPair> select = new ArrayList<>();
                for (final VoFulfilmentCentre warehouse : warehouses) {
                    select.add(new ReportPair(warehouse.getCode() + ": " + warehouse.getName(), String.valueOf(warehouse.getWarehouseId())));
                }
                select.sort((a, b) -> a.getLabel().compareToIgnoreCase(b.getLabel()));
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
    @Override
    public List<Object> getResult(final String lang, final Map<String, Object> currentSelection) {
        final String warehouse = (String) currentSelection.get("warehouse");
        final String skuCode = (String) currentSelection.get("skuCode");
        final long warehouseId = NumberUtils.toLong(warehouse);
        if (warehouseId > 0L) {
            try {
                final VoSearchContext ctx = new VoSearchContext();
                if (StringUtils.isNotBlank(skuCode)) {
                    ctx.setParameters(Collections.singletonMap("filter", Collections.singletonList(skuCode)));
                }
                ctx.setSize(Integer.MAX_VALUE);

                return (List) fulfilmentService.getFilteredInventory(warehouseId, ctx).getItems();
            } catch (Exception e) {
                // do nothing
            }
        }
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Object> getEnhancedParameterValues(final List<Object> result, final Map<String, Object> currentSelection) {
        return new HashMap<>(currentSelection);
    }

}
