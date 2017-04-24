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

package org.yes.cart.orderexport.impl;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.CustomerOrderDelivery;
import org.yes.cart.orderexport.ExportProcessorException;
import org.yes.cart.orderexport.OrderAutoExportProcessor;
import org.yes.cart.orderexport.OrderExporter;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.util.log.Markers;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * User: denispavlov
 * Date: 21/02/2017
 * Time: 08:56
 */
public class OrderAutoExportProcessorImpl implements OrderAutoExportProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(OrderAutoExportProcessorImpl.class);

    private final CustomerOrderService customerOrderService;

    private final Set<OrderExporter> orderExporters = new HashSet<OrderExporter>();

    private final ThreadLocal<SimpleDateFormat> formatter = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };

    public OrderAutoExportProcessorImpl(final CustomerOrderService customerOrderService) {
        this.customerOrderService = customerOrderService;
    }

    @Override
    public void run() {

        LOG.info("Auto export processor start");

        final List<Long> eligible = this.customerOrderService.findEligibleForExportOrderIds();
        for (final Long customerOrderId : eligible) {
            try {
                proxy().processSingleOrder(customerOrderId);
            } catch (ExportProcessorException exp1) {
                LOG.error(Markers.alert(), "Failed to auto export order: " + customerOrderId + " [" + exp1.getExporter() + "]", exp1);
                proxy().markFailedOrder(customerOrderId, exp1.getExporter(), exp1.getMessage());
            } catch (Exception exp2) {
                LOG.error(Markers.alert(), "Failed to auto export order: " + customerOrderId, exp2);
                proxy().markFailedOrder(customerOrderId, "GENERIC", exp2.getMessage());
            }
        }

        LOG.info("Auto export processor finished ... {} orders", eligible.size());

    }

    /** {@inheritDoc} */
    @Override
    public void processSingleOrder(final Long customerOrderId) throws ExportProcessorException {

        final CustomerOrder customerOrder = customerOrderService.findById(customerOrderId);

        final Collection<CustomerOrderDelivery> eligibleDeliveries = new ArrayList<CustomerOrderDelivery>();
        if (StringUtils.isNotBlank(customerOrder.getEligibleForExport())) {
            for (final CustomerOrderDelivery delivery : customerOrder.getDelivery()) {
                if (StringUtils.isNotBlank(delivery.getEligibleForExport())) {
                    eligibleDeliveries.add(delivery);
                }
            }
        }

        final Set<Long> exported = new HashSet<Long>();
        final Map<String, String> audit = new HashMap<String, String>();
        String nextOrderEligibility = null;
        Map<Long, String> nextDeliveryEligibility = new HashMap<Long, String>();

        if (eligibleDeliveries.isEmpty()) {
            LOG.warn("Auto export for order {} in {} has no eligible deliveries. at least one delivery must be marked as eligible.",
                    customerOrder.getOrdernum(), customerOrder.getOrderStatus());
        } else {

            final Set<OrderExporter> exporters = determineApplicableExporters(customerOrder, eligibleDeliveries);
            if (!exporters.isEmpty()) {

                LOG.info("Order {} in {} is eligible for auto export", customerOrder.getOrdernum(), customerOrder.getOrderStatus());
                for (final OrderExporter exporter : sortByPriority(exporters)) {

                    try {
                        final OrderExporter.ExportResult result = exporter.export(customerOrder, eligibleDeliveries);
                        exported.addAll(result.getExportedDeliveryIds());
                        audit.putAll(result.getOrderAuditParams());

                        if (StringUtils.isNotBlank(result.getNextExportEligibilityForOrder())) {

                            if (nextOrderEligibility != null) {
                                LOG.error(Markers.alert(),
                                        "Multiple exporters set next eligible auto export for order {} eligible for {}",
                                        customerOrder.getOrdernum(), customerOrder.getEligibleForExport());
                            } else {
                                // Mark for next eligibility (we can only support one)
                                nextOrderEligibility = result.getNextExportEligibilityForOrder();
                                nextDeliveryEligibility.putAll(result.getNextExportEligibilityForDelivery());
                            }

                        }
                    } catch (Exception exp) {
                        throw new ExportProcessorException(exp, exporter.getExporterId());
                    }

                }

            }

            for (final CustomerOrderDelivery delivery : eligibleDeliveries) {

                if (!delivery.isBlockExport()) {
                    // Set next eligibility if we have one
                    delivery.setEligibleForExport(nextDeliveryEligibility.get(delivery.getCustomerOrderDeliveryId()));
                    if (exported.contains(delivery.getCustomerOrderDeliveryId())) {
                        delivery.setLastExportDate(new Date());
                        delivery.setLastExportDeliveryStatus(delivery.getDeliveryStatus());
                        delivery.setLastExportStatus(null); // No status - OK
                        LOG.info("Delivery {} exported", delivery.getDeliveryNum());
                    } else {
                        LOG.info("Delivery {} is not exported (possibly no valid exporter?)", delivery.getDeliveryNum());
                    }
                } else {
                    LOG.info("Delivery {} was marked as blocked", delivery.getDeliveryNum());
                }

            }

        }

        if (!customerOrder.isBlockExport()) {
            // Set next eligibility if we have one
            customerOrder.setEligibleForExport(nextOrderEligibility);
            if (!exported.isEmpty()) {
                customerOrder.setLastExportDate(new Date());
                customerOrder.setLastExportOrderStatus(customerOrder.getOrderStatus());
                customerOrder.setLastExportStatus(null); // No status - OK
                LOG.info("Order {} exported", customerOrder.getOrdernum());
            } else {
                LOG.info("Order {} is not exported (possibly no valid exporter?)", customerOrder.getOrdernum());
            }
        } else {
            LOG.info("Order {} was marked as blocked", customerOrder.getOrdernum());
        }
        for (final Map.Entry<String, String> auditEntry : audit.entrySet()) {
            customerOrder.putValue(auditEntry.getKey(), auditEntry.getValue(), "AUDITEXPORT");
        }
        customerOrderService.update(customerOrder);
    }

    /*
        Sort for more deterministic execution of the processors
     */
    private OrderExporter[] sortByPriority(final Set<OrderExporter> exporters) {
        final OrderExporter[] sorted = exporters.toArray(new OrderExporter[exporters.size()]);
        Arrays.sort(sorted, BY_PRIORITY);
        return sorted;
    }

    private static Comparator<OrderExporter> BY_PRIORITY = new Comparator<OrderExporter>() {
        @Override
        public int compare(final OrderExporter o1, final OrderExporter o2) {
            return Integer.compare(o1.getPriority(), o2.getPriority());
        }
    };

    /** {@inheritDoc} */
    @Override
    public void markFailedOrder(final Long customerOrderId, final String exporter, final String error) {

        final CustomerOrder customerOrder = customerOrderService.findById(customerOrderId);
        customerOrder.setLastExportOrderStatus(customerOrder.getOrderStatus());
        customerOrder.setLastExportDate(new Date());
        customerOrder.setLastExportStatus(error);
        // Key is Exporter+Suffix because we want to overwrite this with last error by this exporter (in case this error
        // is persistent, so that we do not flood the audit table)
        customerOrder.putValue(exporter + ": ERROR", formatter.get().format(new Date()) + ": " + error, "AUDITEXPORT");
        customerOrderService.update(customerOrder);

    }

    protected Set<OrderExporter> determineApplicableExporters(final CustomerOrder customerOrder, final Collection<CustomerOrderDelivery> orderDeliveries) {
        final Set<OrderExporter> applicable = new HashSet<OrderExporter>();
        for (final OrderExporter orderExporter : this.orderExporters) {
            if (orderExporter.supports(customerOrder, orderDeliveries)) {
                applicable.add(orderExporter);
            }
        }
        return applicable;
    }

    /** {@inheritDoc} */
    @Override
    public void registerExporter(final OrderExporter orderExporter) {

        this.orderExporters.add(orderExporter);

    }


    private OrderAutoExportProcessor proxy;

    OrderAutoExportProcessor proxy() {
        if (proxy == null) {
            proxy = getSelfProxy();
        }
        return proxy;
    }

    /**
     * Spring IoC.
     *
     * @return self proxy
     */
    public OrderAutoExportProcessor getSelfProxy() {
        return null;
    }


}
