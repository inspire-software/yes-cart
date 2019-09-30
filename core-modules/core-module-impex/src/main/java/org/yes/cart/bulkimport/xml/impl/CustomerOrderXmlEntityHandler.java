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

package org.yes.cart.bulkimport.xml.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.yes.cart.bulkcommon.model.ImpExTuple;
import org.yes.cart.bulkimport.xml.XmlEntityImportHandler;
import org.yes.cart.bulkimport.xml.internal.*;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.i18n.impl.NonI18NModel;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.domain.*;
import org.yes.cart.utils.DateUtils;

import java.util.Map;

/**
 * User: denispavlov
 * Date: 24/03/2019
 * Time: 12:15
 */
public class CustomerOrderXmlEntityHandler extends AbstractXmlEntityHandler<CustomerOrderType, CustomerOrder> implements XmlEntityImportHandler<CustomerOrderType, CustomerOrder> {

    private CustomerOrderService customerOrderService;
    private ShopService shopService;
    private CustomerService customerService;
    private PromotionCouponService promotionCouponService;
    private CarrierSlaService carrierSlaService;

    private XmlEntityImportHandler<AddressType, Address> addressXmlEntityImportHandler;

    public CustomerOrderXmlEntityHandler() {
        super("customer-order");
    }

    @Override
    protected void delete(final JobStatusListener statusListener, final CustomerOrder customerOrder, final Map<String, Integer> entityCount) {
        this.customerOrderService.delete(customerOrder);
        this.customerOrderService.getGenericDao().flush();
    }

    @Override
    protected void saveOrUpdate(final JobStatusListener statusListener, final CustomerOrder domain, final CustomerOrderType xmlType, final EntityImportModeType mode, final Map<String, Integer> entityCount) {

        // Order state is updatable
        processOrderState(domain, xmlType);


        if (domain.getCustomerorderId() == 0L) {
            this.customerOrderService.create(domain);
        } else {
            this.customerOrderService.update(domain);
        }
        this.customerOrderService.getGenericDao().flush();
        this.customerOrderService.getGenericDao().evict(domain);
    }

    private void processOrderState(final CustomerOrder domain, final CustomerOrderType xmlType) {
        if (xmlType.getOrderState() != null) {

            domain.setOrderStatus(xmlType.getOrderState().getStatus());
            domain.setEligibleForExport(xmlType.getOrderState().getEligibleForExport());
            domain.setBlockExport(xmlType.getOrderState().isBlockExport());
            domain.setLastExportDate(DateUtils.iParseSDT(xmlType.getOrderState().getLastExportDate()));
            domain.setLastExportStatus(xmlType.getOrderState().getLastExportStatus());
            domain.setLastExportOrderStatus(xmlType.getOrderState().getLastExportOrderStatus());

        }
    }

    @Override
    protected CustomerOrder getOrCreate(final JobStatusListener statusListener, final CustomerOrderType xmlType, final Map<String, Integer> entityCount) {
        CustomerOrder customerOrder = this.customerOrderService.findSingleByCriteria(" where e.guid = ?1", xmlType.getGuid());
        if (customerOrder != null) {
            return customerOrder;
        }
        customerOrder = this.customerOrderService.getGenericDao().getEntityFactory().getByIface(CustomerOrder.class);
        customerOrder.setShop(this.shopService.getShopByCode(xmlType.getShopCode()));
        customerOrder.setGuid(xmlType.getGuid());
        customerOrder.setOrdernum(xmlType.getOrderNumber());

        // configuration not updatable
        customerOrder.setPgLabel(xmlType.getConfiguration().getPaymentGateway());
        customerOrder.setCartGuid(xmlType.getConfiguration().getCartGuid());
        customerOrder.setOrderTimestamp(DateUtils.ldtParseSDT(xmlType.getConfiguration().getOrderDate()));
        customerOrder.setLocale(xmlType.getConfiguration().getLocale());
        customerOrder.setOrderIp(xmlType.getConfiguration().getIp());

        processContactDetails(statusListener, xmlType, customerOrder, entityCount);

        processOrderAmount(xmlType, customerOrder);

        processOrderB2B(xmlType, customerOrder);

        processOrderedDetails(xmlType, customerOrder);

        processShipments(xmlType, customerOrder);

        if (xmlType.getCustomAttributes() != null) {

            for (final CustomAttributeType ca : xmlType.getCustomAttributes().getCustomAttribute()) {

                customerOrder.putValue(ca.getAttribute(), ca.getCustomValue(), getI18nForCustomAttribute(ca));

            }

        }


        return customerOrder;
    }

    private void processShipments(final CustomerOrderType xmlType, final CustomerOrder customerOrder) {

        if (xmlType.getShipments() != null) {

            customerOrder.setRequestedDeliveryDate(DateUtils.ldtParseSDT(xmlType.getShipments().getRequestedDeliveryDate()));
            customerOrder.setMultipleShipmentOption(xmlType.getShipments().isMultipleShipments());

            if (xmlType.getShipments().getDeliveries() != null) {

                for (final OrderDeliveryType deliveryType : xmlType.getShipments().getDeliveries().getDelivery()) {

                    processDelivery(deliveryType, customerOrder);

                }

            }

        }

    }

    private void processDelivery(final OrderDeliveryType deliveryType, final CustomerOrder customerOrder) {

        final CustomerOrderDelivery delivery = this.customerOrderService.getGenericDao().getEntityFactory().getByIface(CustomerOrderDelivery.class);

        delivery.setCustomerOrder(customerOrder);

        delivery.setGuid(deliveryType.getGuid());
        delivery.setDeliveryNum(deliveryType.getDeliveryNumber());
        delivery.setDeliveryGroup(deliveryType.getDeliveryGroup());

        processDeliveryState(deliveryType, delivery);

        processDeliveryShipping(deliveryType, delivery);

        processDeliveryShippingCost(deliveryType, delivery);

        processDeliveryItems(deliveryType, delivery);

        customerOrder.getDelivery().add(delivery);

    }

    private void processDeliveryItems(final OrderDeliveryType deliveryType, final CustomerOrderDelivery delivery) {
        if (deliveryType.getDeliveryItems() != null) {

            for (final DeliveryItemType item : deliveryType.getDeliveryItems().getDeliveryItem()) {

                final CustomerOrderDeliveryDet detail = this.customerOrderService.getGenericDao().getEntityFactory().getByIface(CustomerOrderDeliveryDet.class);

                detail.setDelivery(delivery);
                detail.setGuid(item.getGuid());
                detail.setProductSkuCode(item.getSku());
                detail.setProductName(item.getProductName());
                detail.setQty(item.getFulfilment().getOrderedQuantity());
                detail.setDeliveredQuantity(item.getFulfilment().getDeliveredQuantity());
                detail.setSupplierCode(item.getFulfilment().getSupplierCode());
                detail.setDeliveryEstimatedMin(DateUtils.ldtParseSDT(item.getFulfilment().getDeliveryEstimatedMin()));
                detail.setDeliveryEstimatedMax(DateUtils.ldtParseSDT(item.getFulfilment().getDeliveryEstimatedMax()));
                detail.setDeliveryGuaranteed(DateUtils.ldtParseSDT(item.getFulfilment().getDeliveryGuaranteed()));
                detail.setDeliveryConfirmed(DateUtils.ldtParseSDT(item.getFulfilment().getDeliveryConfirmed()));
                detail.setSupplierInvoiceNo(item.getFulfilment().getInvoiceNumber());
                detail.setSupplierInvoiceDate(DateUtils.ldParseSDT(item.getFulfilment().getInvoiceDate()));
                detail.setDeliveryRemarks(item.getFulfilment().getRemarks());

                // TODO: Cost price

                detail.setListPrice(item.getItemPrice().getListPrice());
                detail.setSalePrice(item.getItemPrice().getSalePrice());
                detail.setPrice(item.getItemPrice().getPrice());
                detail.setNetPrice(item.getItemPrice().getPriceNet());
                detail.setGrossPrice(item.getItemPrice().getPriceGross());
                detail.setTaxCode(item.getItemPrice().getTax().getCode());
                detail.setTaxRate(item.getItemPrice().getTax().getRate());
                detail.setTaxExclusiveOfPrice(item.getItemPrice().getTax().isExclusiveOfPrice());

                if (item.getItemPrice().getItemPromotions() != null) {

                    detail.setGift(item.getItemPrice().getItemPromotions().isGift());
                    detail.setFixedPrice(item.getItemPrice().getItemPromotions().isFixedPrice());
                    detail.setPromoApplied(item.getItemPrice().getItemPromotions().isApplied());

                    if (item.getItemPrice().getItemPromotions().getPromotions() != null) {
                        detail.setAppliedPromo(processCodesCsv(item.getItemPrice().getItemPromotions().getPromotions().getCode(), ','));
                    }

                }

                if (item.getB2B() != null) {
                    detail.setB2bRemarks(item.getB2B().getRemarks());
                }

                if (item.getCustomAttributes() != null) {

                    for (final CustomAttributeType ca : item.getCustomAttributes().getCustomAttribute()) {

                        detail.putValue(ca.getAttribute(), ca.getCustomValue(), getI18nForCustomAttribute(ca));

                    }

                }

                delivery.getDetail().add(detail);

            }

        }
    }

    private void processDeliveryState(final OrderDeliveryType deliveryType, final CustomerOrderDelivery delivery) {
        if (deliveryType.getDeliveryState() != null) {

            delivery.setDeliveryStatus(deliveryType.getDeliveryState().getStatus());
            delivery.setEligibleForExport(deliveryType.getDeliveryState().getEligibleForExport());
            delivery.setBlockExport(deliveryType.getDeliveryState().isBlockExport());
            delivery.setLastExportDate(DateUtils.iParseSDT(deliveryType.getDeliveryState().getLastExportDate()));
            delivery.setLastExportStatus(deliveryType.getDeliveryState().getLastExportStatus());
            delivery.setLastExportDeliveryStatus(deliveryType.getDeliveryState().getLastExportDeliveryStatus());

        }
    }

    private void processDeliveryShippingCost(final OrderDeliveryType deliveryType, final CustomerOrderDelivery delivery) {

        if (deliveryType.getShippingCost() != null) {

            delivery.setListPrice(deliveryType.getShippingCost().getListPrice());
            delivery.setPrice(deliveryType.getShippingCost().getPrice());
            delivery.setNetPrice(deliveryType.getShippingCost().getPriceNet());
            delivery.setGrossPrice(deliveryType.getShippingCost().getPriceGross());
            delivery.setTaxCode(deliveryType.getShippingCost().getTax().getCode());
            delivery.setTaxRate(deliveryType.getShippingCost().getTax().getRate());
            delivery.setTaxExclusiveOfPrice(deliveryType.getShippingCost().getTax().isExclusiveOfPrice());

            if (deliveryType.getShippingCost().getShippingPromotions() != null) {

                delivery.setPromoApplied(deliveryType.getShippingCost().getShippingPromotions().isApplied());
                if (deliveryType.getShippingCost().getShippingPromotions().getPromotions() != null) {
                    delivery.setAppliedPromo(processCodesCsv(deliveryType.getShippingCost().getShippingPromotions().getPromotions().getCode(), ','));
                }

            }

        }
    }

    private void processDeliveryShipping(final OrderDeliveryType deliveryType, final CustomerOrderDelivery delivery) {
        if (deliveryType.getShipping() != null) {

            delivery.setCarrierSla(this.carrierSlaService.findSingleByCriteria(" where e.guid = ?1", deliveryType.getShipping().getShippingMethod()));
            delivery.setRefNo(deliveryType.getShipping().getTrackingReference());
            delivery.setDeliveryRemarks(deliveryType.getShipping().getRemarks());
            delivery.setRequestedDeliveryDate(DateUtils.ldtParseSDT(deliveryType.getShipping().getRequestedDeliveryDate()));
            delivery.setDeliveryEstimatedMin(DateUtils.ldtParseSDT(deliveryType.getShipping().getDeliveryEstimatedMin()));
            delivery.setDeliveryEstimatedMax(DateUtils.ldtParseSDT(deliveryType.getShipping().getDeliveryEstimatedMax()));
            delivery.setDeliveryGuaranteed(DateUtils.ldtParseSDT(deliveryType.getShipping().getDeliveryGuaranteed()));
            delivery.setDeliveryConfirmed(DateUtils.ldtParseSDT(deliveryType.getShipping().getDeliveryConfirmed()));

        }
    }

    private void processOrderedDetails(final CustomerOrderType xmlType, final CustomerOrder customerOrder) {

        if (xmlType.getOrderItems() != null) {

            for (final OrderItemType item : xmlType.getOrderItems().getOrderItem()) {

                final CustomerOrderDet detail = this.customerOrderService.getGenericDao().getEntityFactory().getByIface(CustomerOrderDet.class);

                detail.setCustomerOrder(customerOrder);
                detail.setGuid(item.getGuid());
                detail.setProductSkuCode(item.getSku());
                detail.setProductName(item.getProductName());
                detail.setQty(item.getFulfilment().getOrderedQuantity());
                detail.setSupplierCode(item.getFulfilment().getSupplierCode());
                detail.setDeliveryEstimatedMin(DateUtils.ldtParseSDT(item.getFulfilment().getDeliveryEstimatedMin()));
                detail.setDeliveryEstimatedMax(DateUtils.ldtParseSDT(item.getFulfilment().getDeliveryEstimatedMax()));
                detail.setDeliveryGuaranteed(DateUtils.ldtParseSDT(item.getFulfilment().getDeliveryGuaranteed()));
                detail.setDeliveryRemarks(item.getFulfilment().getRemarks());

                // TODO: Cost price

                detail.setListPrice(item.getItemPrice().getListPrice());
                detail.setSalePrice(item.getItemPrice().getSalePrice());
                detail.setPrice(item.getItemPrice().getPrice());
                detail.setNetPrice(item.getItemPrice().getPriceNet());
                detail.setGrossPrice(item.getItemPrice().getPriceGross());
                detail.setTaxCode(item.getItemPrice().getTax().getCode());
                detail.setTaxRate(item.getItemPrice().getTax().getRate());
                detail.setTaxExclusiveOfPrice(item.getItemPrice().getTax().isExclusiveOfPrice());

                if (item.getItemPrice().getItemPromotions() != null) {

                    detail.setGift(item.getItemPrice().getItemPromotions().isGift());
                    detail.setFixedPrice(item.getItemPrice().getItemPromotions().isFixedPrice());
                    detail.setPromoApplied(item.getItemPrice().getItemPromotions().isApplied());

                    if (item.getItemPrice().getItemPromotions().getPromotions() != null) {
                        detail.setAppliedPromo(processCodesCsv(item.getItemPrice().getItemPromotions().getPromotions().getCode(), ','));
                    }

                }

                if (item.getB2B() != null) {
                    detail.setB2bRemarks(item.getB2B().getRemarks());
                }

                if (item.getCustomAttributes() != null) {

                    for (final CustomAttributeType ca : item.getCustomAttributes().getCustomAttribute()) {

                        detail.putValue(ca.getAttribute(), ca.getCustomValue(), getI18nForCustomAttribute(ca));

                    }

                }

                customerOrder.getOrderDetail().add(detail);

            }

        }
    }

    private void processOrderB2B(final CustomerOrderType xmlType, final CustomerOrder customerOrder) {
        if (xmlType.getB2B() != null) {
            customerOrder.setB2bRef(xmlType.getB2B().getReference());
            customerOrder.setB2bEmployeeId(xmlType.getB2B().getEmployeeId());
            customerOrder.setB2bChargeId(xmlType.getB2B().getChargeId());
            customerOrder.setB2bRequireApprove(xmlType.getB2B().isRequireApproval());
            customerOrder.setB2bApprovedBy(xmlType.getB2B().getApprovedBy());
            customerOrder.setB2bApprovedDate(DateUtils.ldtParseSDT(xmlType.getB2B().getApprovedDate()));
            customerOrder.setB2bRemarks(xmlType.getB2B().getRemarks());
        }
    }

    private void processOrderAmount(final CustomerOrderType xmlType, final CustomerOrder customerOrder) {

        if (xmlType.getAmountDue() == null) {
            return;
        }

        customerOrder.setCurrency(xmlType.getAmountDue().getCurrency());
        customerOrder.setListPrice(xmlType.getAmountDue().getListPrice());
        customerOrder.setPrice(xmlType.getAmountDue().getPrice());
        customerOrder.setNetPrice(xmlType.getAmountDue().getPriceNet());
        customerOrder.setGrossPrice(xmlType.getAmountDue().getPriceGross());

        if (xmlType.getAmountDue().getOrderPromotions() != null) {
            customerOrder.setPromoApplied(xmlType.getAmountDue().getOrderPromotions().isApplied());
            if (xmlType.getAmountDue().getOrderPromotions().getPromotions() != null) {
                customerOrder.setAppliedPromo(processCodesCsv(xmlType.getAmountDue().getOrderPromotions().getPromotions().getCode(), ','));
            }
            if (xmlType.getAmountDue().getOrderPromotions().getCoupons() != null) {
                for (final String couponCode : xmlType.getAmountDue().getOrderPromotions().getCoupons().getCode()) {
                    final PromotionCoupon coupon = promotionCouponService.findSingleByCriteria(" where e.code = ?1", couponCode);
                    final PromotionCouponUsage usage = promotionCouponService.getGenericDao().getEntityFactory().getByIface(PromotionCouponUsage.class);
                    usage.setCoupon(coupon);
                    usage.setCustomerEmail(xmlType.getContactDetails().getEmail());
                    usage.setCustomerOrder(customerOrder);
                    customerOrder.getCoupons().add(usage); // Usage is tracked by order state manager listener
                }
            }
        }
    }

    private void processContactDetails(final JobStatusListener statusListener, final CustomerOrderType xmlType, final CustomerOrder customerOrder, final Map<String, Integer>entityCount) {

        if (xmlType.getContactDetails() == null) {
            return;
        }

        if (StringUtils.isNotBlank(xmlType.getContactDetails().getCustomerCode())) {
            customerOrder.setCustomer(this.customerService.findSingleByCriteria(" where e.guid = ?1", xmlType.getContactDetails().getCustomerCode()));
        }
        customerOrder.setEmail(xmlType.getContactDetails().getEmail());
        customerOrder.setSalutation(xmlType.getContactDetails().getSalutation());
        customerOrder.setFirstname(xmlType.getContactDetails().getFirstname());
        customerOrder.setMiddlename(xmlType.getContactDetails().getMiddlename());
        customerOrder.setLastname(xmlType.getContactDetails().getLastname());
        if (xmlType.getContactDetails().getShippingAddress() != null) {
            customerOrder.setShippingAddress(xmlType.getContactDetails().getShippingAddress().getFormatted());
            customerOrder.setShippingAddressDetails(importStaticAddress(statusListener, xmlType.getContactDetails().getShippingAddress().getAddress(), entityCount));
        }
        if (xmlType.getContactDetails().getBillingAddress() != null) {
            customerOrder.setBillingAddress(xmlType.getContactDetails().getBillingAddress().getFormatted());
            customerOrder.setBillingAddressDetails(importStaticAddress(statusListener, xmlType.getContactDetails().getBillingAddress().getAddress(), entityCount));
        }
        customerOrder.setOrderMessage(xmlType.getContactDetails().getRemarks());
    }

    private I18NModel getI18nForCustomAttribute(final CustomAttributeType ca) {

        if (ca.getCustomDisplayValue() != null
                && CollectionUtils.isNotEmpty(ca.getCustomDisplayValue().getI18N())) {
            return processI18n(ca.getCustomDisplayValue(), null);
        }
        return null;
    }

    private Address importStaticAddress(final JobStatusListener statusListener, final AddressType xmlAddressType, final Map<String, Integer>entityCount) {
        if (xmlAddressType == null) {
            return null;
        }
        return addressXmlEntityImportHandler.handle(statusListener, null, (ImpExTuple) new XmlImportTupleImpl(xmlAddressType.getGuid(), xmlAddressType), null, null, entityCount);
    }

    @Override
    protected EntityImportModeType determineImportMode(final CustomerOrderType xmlType) {
        return xmlType.getImportMode() != null ? xmlType.getImportMode() : EntityImportModeType.MERGE;
    }

    @Override
    protected boolean isNew(final CustomerOrder domain) {
        return domain.getCustomerorderId() == 0L;
    }

    /**
     * Spring IoC.
     *
     * @param customerOrderService customer order service
     */
    public void setCustomerOrderService(final CustomerOrderService customerOrderService) {
        this.customerOrderService = customerOrderService;
    }

    /**
     * Spring IoC.
     *
     * @param shopService shop service
     */
    public void setShopService(final ShopService shopService) {
        this.shopService = shopService;
    }

    /**
     * Spring IoC.
     *
     * @param customerService customer service
     */
    public void setCustomerService(final CustomerService customerService) {
        this.customerService = customerService;
    }

    /**
     * Spring IoC.
     *
     * @param promotionCouponService coupon service
     */
    public void setPromotionCouponService(final PromotionCouponService promotionCouponService) {
        this.promotionCouponService = promotionCouponService;
    }

    /**
     * Spring IoC.
     *
     * @param carrierSlaService carrier SLA service
     */
    public void setCarrierSlaService(final CarrierSlaService carrierSlaService) {
        this.carrierSlaService = carrierSlaService;
    }


    /**
     * Spring IoC.
     *
     * @param addressXmlEntityImportHandler Address handler
     */
    public void setAddressXmlEntityImportHandler(final XmlEntityImportHandler<AddressType, Address> addressXmlEntityImportHandler) {
        this.addressXmlEntityImportHandler = addressXmlEntityImportHandler;
    }
}
