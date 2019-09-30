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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.yes.cart.bulkcommon.model.ImpExTuple;
import org.yes.cart.bulkcommon.xml.XmlValueAdapter;
import org.yes.cart.bulkexport.xml.XmlExportDescriptor;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.domain.PromotionCouponService;

import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 23/03/2019
 * Time: 07:09
 */
public class CustomerOrderXmlEntityHandler extends AbstractXmlEntityHandler<CustomerOrder>  {

    private final AddressXmlEntityHandler addressHandler = new AddressXmlEntityHandler();

    private PromotionCouponService promotionCouponService;

    public CustomerOrderXmlEntityHandler() {
        super("customer-orders");
    }

    @Override
    public void handle(final JobStatusListener statusListener,
                       final XmlExportDescriptor xmlExportDescriptor,
                       final ImpExTuple<String, CustomerOrder> tuple,
                       final XmlValueAdapter xmlValueAdapter,
                       final String fileToExport,
                       final OutputStreamWriter writer,
                       final Map<String, Integer> entityCount) throws Exception {

        handleInternal(tagCustomerOrder(null, tuple.getData()), writer, entityCount);

    }

    Tag tagCustomerOrder(final Tag parent, final CustomerOrder customerOrder) {

        final Tag oTag = tag(parent, "customer-order")
                .attr("id", customerOrder.getCustomerorderId())
                .attr("guid", customerOrder.getGuid())
                .attr("order-number", customerOrder.getOrdernum())
                .attr("shop-code", customerOrder.getShop().getCode());

        oTag.tag("configuration")
                .tagChars("payment-gateway", customerOrder.getPgLabel())
                .tagChars("cart-guid", customerOrder.getCartGuid())
                .tagTime("order-date", customerOrder.getOrderTimestamp())
                .tagChars("locale", customerOrder.getLocale())
                .tagChars("ip", customerOrder.getOrderIp())
                .tagBool("guest", customerOrder.getCustomer() == null || customerOrder.getCustomer().isGuest())
                .end();

        oTag.tag("order-state")
                .tagChars("status", customerOrder.getOrderStatus())
                .tagChars("eligible-for-export", customerOrder.getEligibleForExport())
                .tagBool("block-export", customerOrder.isBlockExport())
                .tagTime("last-export-date", customerOrder.getLastExportDate())
                .tagChars("last-export-status", customerOrder.getLastExportStatus())
                .tagChars("last-export-order-status", customerOrder.getLastExportOrderStatus())
                .end();


        final Tag cdTag = oTag.tag("contact-details");

            cdTag
                .attr("customer-code", customerOrder.getCustomer() != null ? customerOrder.getCustomer().getGuid() : null)
                .tagChars("email", customerOrder.getEmail())
                .tagChars("salutation", customerOrder.getSalutation())
                .tagChars("firstname", customerOrder.getFirstname())
                .tagChars("middlename", customerOrder.getMiddlename())
                .tagChars("lastname", customerOrder.getLastname());

            if (StringUtils.isNotBlank(customerOrder.getShippingAddress())) {
                final Tag saTag = cdTag.tag("shipping-address");
                    saTag
                            .tagCdata("formatted", customerOrder.getShippingAddress());
                    if (customerOrder.getShippingAddressDetails() != null) {
                        this.addressHandler.tagAddress(saTag, customerOrder.getShippingAddressDetails());
                    }
                    saTag.end();
            }

            if (StringUtils.isNotBlank(customerOrder.getBillingAddress())) {
                final Tag baTag = cdTag.tag("billing-address");
                    baTag
                            .tagCdata("formatted", customerOrder.getBillingAddress());
                    if (customerOrder.getBillingAddressDetails() != null) {
                        this.addressHandler.tagAddress(baTag, customerOrder.getBillingAddressDetails());
                    }
                    baTag.end();
            }

            cdTag.tagCdata("remarks", customerOrder.getOrderMessage());

            cdTag.end();

        final Tag adTag = oTag.tag("amount-due");
            adTag
                .attr("currency", customerOrder.getCurrency())
                .tagNum("list-price", customerOrder.getListPrice())
                .tagNum("price", customerOrder.getPrice())
                .tagNum("price-net", customerOrder.getNetPrice())
                .tagNum("price-gross", customerOrder.getGrossPrice());

            final Tag adpTag = adTag.tag("order-promotions");
                adpTag.attr("applied", customerOrder.isPromoApplied());
                if (customerOrder.isPromoApplied()) {
                    adpTag.tagList("promotions", "code", customerOrder.getAppliedPromo(), ',');
                }
                if (CollectionUtils.isNotEmpty(customerOrder.getCoupons())) {
                    final Tag adpcTag = adpTag.tag("coupons");
                    for (final PromotionCouponUsage usage : customerOrder.getCoupons()) {
                        adpcTag.tagChars("code", usage.getCouponCode());
                    }
                    adpcTag.end();
                }
                adpTag.end();

            adTag.end();

        oTag.tag("b2b")
                .tagChars("reference", customerOrder.getB2bRef())
                .tagChars("employee-id", customerOrder.getB2bEmployeeId())
                .tagChars("charge-id", customerOrder.getB2bChargeId())
                .tagBool("require-approval", customerOrder.isB2bRequireApprove())
                .tagChars("approved-by", customerOrder.getB2bApprovedBy())
                .tagTime("approved-date", customerOrder.getB2bApprovedDate())
                .tagCdata("remarks", customerOrder.getB2bRemarks())
                .end();

        final Tag oiTag = oTag.tag("order-items");

        for (final CustomerOrderDet item : customerOrder.getOrderDetail()) {

            final Tag oiiTag = oiTag.tag("order-item");

            oiiTag
                    .attr("id", item.getCustomerOrderDetId())
                    .attr("guid", item.getGuid())
                    .tagChars("sku", item.getProductSkuCode())
                    .tagChars("product-name", item.getProductName())
                    .tag("fulfilment")
                        .tagNum("ordered-quantity", item.getQty())
                        .tagChars("supplier-code", item.getSupplierCode())
                        .tagTime("delivery-estimated-min", item.getDeliveryEstimatedMin())
                        .tagTime("delivery-estimated-max", item.getDeliveryEstimatedMax())
                        .tagTime("delivery-guaranteed", item.getDeliveryGuaranteed())
                        .tagCdata("remarks", item.getDeliveryRemarks())
                    .end();

                final Pair<String, I18NModel> cost = item.getValue("ItemCostPrice");

                if (cost != null) {

                    oiiTag.tag("item-cost")
                            .attr("currency", customerOrder.getCurrency())
                            .tagNum("list-price", new BigDecimal(cost.getFirst()))
                            .end();

                }

                final Tag ipTag = oiiTag.tag("item-price");
                ipTag
                    .attr("currency", customerOrder.getCurrency())
                    .tagNum("list-price", item.getListPrice())
                    .tagNum("sale-price", item.getSalePrice())
                    .tagNum("price", item.getPrice())
                    .tagNum("price-net", item.getNetPrice())
                    .tagNum("price-gross", item.getGrossPrice())
                    .tag("tax")
                        .attr("rate", item.getTaxRate())
                        .attr("code", item.getTaxCode())
                        .attr("exclusive-of-price", item.isTaxExclusiveOfPrice())
                    .end();

                    final Tag oipTag = ipTag.tag("item-promotions");
                    oipTag
                            .attr("applied", item.isPromoApplied())
                            .attr("gift", item.isGift())
                            .attr("fixed-price", item.isFixedPrice());
                    if (item.isPromoApplied()) {
                        oipTag.tagList("promotions", "code", item.getAppliedPromo(), ',');
                    }
                    oipTag.end();

                ipTag.end();

            oiiTag.tag("b2b")
                    .tagCdata("remarks", item.getB2bRemarks())
                    .end();

            oiiTag.tagExt(item.getAllValues());

            oiiTag.end();

        }

        oiTag.end();

        final Tag shTag = oTag.tag("shipments");
            shTag
                    .attr("multiple-shipments", customerOrder.isMultipleShipmentOption())
                    .tagTime("requested-delivery-date", customerOrder.getRequestedDeliveryDate());

        if (CollectionUtils.isNotEmpty(customerOrder.getDelivery())) {

            final Tag shdTag = shTag.tag("deliveries");

            for (final CustomerOrderDelivery cod : customerOrder.getDelivery()) {

                final Tag shdd = shdTag.tag("delivery");
                    shdd
                            .attr("id", cod.getCustomerOrderDeliveryId())
                            .attr("guid", cod.getGuid())
                            .attr("delivery-number", cod.getDeliveryNum())
                            .attr("delivery-group", cod.getDeliveryGroup())
                            .tag("shipping")
                                .tagChars("shipping-method", cod.getCarrierSla().getGuid())
                                .tagChars("tracking-reference", cod.getRefNo())
                                .tagTime("requested-delivery-date", cod.getRequestedDeliveryDate())
                                .tagTime("delivery-estimated-min", cod.getDeliveryEstimatedMin())
                                .tagTime("delivery-estimated-max", cod.getDeliveryEstimatedMax())
                                .tagTime("delivery-guaranteed", cod.getDeliveryGuaranteed())
                                .tagTime("delivery-confirmed", cod.getDeliveryConfirmed())
                                .tagCdata("remarks", cod.getDeliveryRemarks())
                            .end();


                final Tag scTag = shdd.tag("shipping-cost");
                    scTag
                            .attr("currency", customerOrder.getCurrency())
                            .tagNum("list-price", cod.getListPrice())
                            .tagNum("sale-price", cod.getPrice())
                            .tagNum("price", cod.getPrice())
                            .tagNum("price-net", cod.getNetPrice())
                            .tagNum("price-gross", cod.getGrossPrice())
                            .tag("tax")
                                .attr("rate", cod.getTaxRate())
                                .attr("code", cod.getTaxCode())
                                .attr("exclusive-of-price", cod.isTaxExclusiveOfPrice())
                            .end();

                        final Tag scpTag = scTag.tag("shipping-promotions");
                        scpTag.attr("applied", cod.isPromoApplied());
                        if (cod.isPromoApplied()) {
                            scpTag.tagList("promotions", "code", cod.getAppliedPromo(), ',');
                        }
                        scpTag.end();

                    scTag.end();

                shdd.tag("delivery-state")
                        .tagChars("status", cod.getDeliveryStatus())
                        .tagChars("eligible-for-export", cod.getEligibleForExport())
                        .tagBool("block-export", cod.isBlockExport())
                        .tagTime("last-export-date", cod.getLastExportDate())
                        .tagChars("last-export-status", cod.getLastExportStatus())
                        .tagChars("last-export-delivery-status", cod.getLastExportDeliveryStatus())
                        .end();


                final Tag diTag = shdd.tag("delivery-items");

                for (final CustomerOrderDeliveryDet item : cod.getDetail()) {

                    final Tag oiiTag = diTag.tag("delivery-item");

                    oiiTag
                            .attr("id", item.getCustomerOrderDeliveryDetId())
                            .attr("guid", item.getGuid())
                            .tagChars("sku", item.getProductSkuCode())
                            .tagChars("product-name", item.getProductName())
                            .tag("fulfilment")
                                .tagNum("ordered-quantity", item.getQty())
                                .tagNum("delivered-quantity", item.getDeliveredQuantity())
                                .tagChars("supplier-code", item.getSupplierCode())
                                .tagTime("delivery-estimated-min", item.getDeliveryEstimatedMin())
                                .tagTime("delivery-estimated-max", item.getDeliveryEstimatedMax())
                                .tagTime("delivery-guaranteed", item.getDeliveryGuaranteed())
                                .tagTime("delivery-confirmed", item.getDeliveryConfirmed())
                                .tagChars("invoice-number", item.getSupplierInvoiceNo())
                                .tagTime("invoice-date", item.getSupplierInvoiceDate())
                                .tagCdata("remarks", item.getDeliveryRemarks())
                            .end();

                    final Pair<String, I18NModel> cost = item.getValue("ItemCostPrice");

                    if (cost != null) {

                        oiiTag.tag("item-cost")
                                .attr("currency", customerOrder.getCurrency())
                                .tagNum("list-price", new BigDecimal(cost.getFirst()))
                                .end();

                    }

                    final Tag ipTag = oiiTag.tag("item-price");
                    ipTag
                            .attr("currency", customerOrder.getCurrency())
                            .tagNum("list-price", item.getPrice())
                            .tagNum("sale-price", item.getPrice())
                            .tagNum("price", item.getPrice())
                            .tagNum("price-net", item.getNetPrice())
                            .tagNum("price-gross", item.getGrossPrice())
                            .tag("tax")
                                .attr("rate", item.getTaxRate())
                                .attr("code", item.getTaxCode())
                                .attr("exclusive-of-price", item.isTaxExclusiveOfPrice())
                            .end();

                    final Tag dipTag = ipTag.tag("item-promotions");
                    dipTag
                            .attr("applied", item.isPromoApplied())
                            .attr("gift", item.isGift())
                            .attr("fixed-price", item.isFixedPrice());
                        if (item.isPromoApplied()) {
                            dipTag.tagList("promotions", "code", item.getAppliedPromo(), ',');
                        }
                    dipTag.end();

                    ipTag.end();

                    oiiTag.tag("b2b")
                            .tagCdata("remarks", item.getB2bRemarks())
                            .end();

                    oiiTag.tagExt(item.getAllValues());

                    oiiTag.end();

                }

                diTag.end();



                shdd.end();

            }

            shdTag.end();

        }

        shTag.end();


        oTag.tagExt(customerOrder.getAllValues());

        return oTag.tagTime(customerOrder).end();

    }

    /**
     * Spring IoC.
     *
     * @param prettyPrint set pretty print mode (new lines and indents)
     */
    @Override
    public void setPrettyPrint(final boolean prettyPrint) {
        super.setPrettyPrint(prettyPrint);
        this.addressHandler.setPrettyPrint(prettyPrint);
    }

    /**
     * Spring IoC.
     *
     * @param promotionCouponService coupon service
     */
    public void setPromotionCouponService(final PromotionCouponService promotionCouponService) {
        this.promotionCouponService = promotionCouponService;
    }
}
