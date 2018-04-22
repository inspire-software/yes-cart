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

package org.yes.cart.orderexport;

import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.orderexport.manual.ManualStateOrderExporterImpl;

import java.util.Collection;

/**
 * Order auto export processor utilises {@link CustomerOrder#getEligibleForExport()}
 * in order to trigger {@link OrderExporter}s that react to specific states the order is in.
 *
 * Thus it is possible to craft complex order export flows and allow specific order exports at certain
 * events.
 *
 * Out of the box the eligibility field is populated by OrderAutoExportListenerImpl and sets the following
 * values:
 *
 * INITPAID  triggered after successful "evt.pending" (online payment) and "evt.payment.confirmed" (offline
 *           payment) thus ensuring providing a hook for initial export after the order payment has been
 *           confirmed (either authorized, captured or confirmed by manager)
 *
 * DELIVERY  triggered by events "evt.delivery.update" (triggered by an automatic fulfilment feed) and
 *           "evt.release.to.shipment" triggered by manager
 *
 * CANCELLED triggered by event "evt.order.cancel.refund" and provides hook for cancelled orders export
 *
 * RETURNED  triggered by event "evt.order.cancel.refund" and provides hook for cancelled orders export
 *
 * Each of these export eligibility values can be used by {@link OrderExporter#supports(CustomerOrder, Collection)}
 * API so that only exporters that allowed to export this type of eligibility are triggered.
 *
 * These values are just guidelines as eligibility field is just a String. Therefore it is possible to create a
 * chain of exporters as well if exporter sets the "next" eligibility status on the order.
 *
 * For example we have two exporters:
 * 1) XMLExporter, which say exports the initial order to an external system and
 * 2) {@link org.yes.cart.orderexport.mail.EmailNotificationOrderExporterImpl} that sends an email to fulfilment
 *    centre notifying then of new confirmed order to be assembled
 *
 * In such setup we can implment XMLExporter in such a way that is supports the "INITPAID" export type, generates
 * the XML and then at the end of its execution and generates {@link OrderExporter.ExportResult} that provides
 * "FCNOTIFY" as getNextExportEligibilityForOrder(). Thus the export processor will update the eligibity field to
 * this value.
 *
 * Then we can configure email notification exporter to trigger on WH001.FCNOTIFY=neworder@wh001.com. Thus in the next
 * cycle auto export processor will process this order it will trigger the email notification.
 *
 * Using this approach complex export flows can be created with ease.
 *
 * The auto export processor only picks up orders that are not blocked ({@link CustomerOrder#isBlockExport()}). This
 * flag on the order is reserved for situation when auto exporting must be prevented, either because there is a problem
 * or in some cases because export should be triggered manually (e.g. manager of a shop selectively chooses to export
 * some orders and ignore the others, or there is an additional manual "approval" mechanism in place).
 *
 * To account for these gaps {@link ManualStateOrderExporterImpl} can be configured say to
 * put order into XMLEXPORT eligibility and set block flag. Setting a block flag will expose "manual export" option in
 * Admin app on this order.
 *
 * User: denispavlov
 * Date: 20/02/2017
 * Time: 11:33
 */
public interface OrderAutoExportProcessor extends Runnable {

    /**
     * Process single order eligible for export.
     *
     * @param customerOrderId order eligible for export
     */
    void processSingleOrder(Long customerOrderId) throws ExportProcessorException;

    /**
     * Process single order eligible for export.
     *
     * @param customerOrderId order eligible for export
     * @param exporter exporter that failed
     * @param error error message
     */
    void markFailedOrder(Long customerOrderId, String exporter, String error);

    /**
     * Register order exporter.
     *
     * @param orderExporter order exporter
     */
    void registerExporter(OrderExporter orderExporter);

}
