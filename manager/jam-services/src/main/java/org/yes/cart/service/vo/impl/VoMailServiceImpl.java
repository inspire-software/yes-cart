/*
 * Copyright 2009 Inspire-Software.com
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

package org.yes.cart.service.vo.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.access.AccessDeniedException;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.dto.ShopDTO;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.message.consumer.StandardMessageListener;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.payment.PaymentGateway;
import org.yes.cart.payment.dto.Payment;
import org.yes.cart.payment.persistence.entity.CustomerOrderPayment;
import org.yes.cart.payment.service.CustomerOrderPaymentService;
import org.yes.cart.service.domain.*;
import org.yes.cart.service.federation.FederationFacade;
import org.yes.cart.service.mail.MailComposer;
import org.yes.cart.service.mail.impl.MailUtils;
import org.yes.cart.service.payment.PaymentModulesManager;
import org.yes.cart.service.theme.ThemeService;
import org.yes.cart.service.vo.VoMailService;
import org.yes.cart.utils.MoneyUtils;

import java.math.BigDecimal;
import java.util.*;

/**
 * User: denispavlov
 * Date: 07/09/2016
 * Time: 18:28
 */
public class VoMailServiceImpl implements VoMailService {

    private final ShopService shopService;
    private final CustomerOrderService customerOrderService;
    private final CustomerWishListService customerWishListService;
    private final ProductSkuService productSkuService;
    private final CustomerOrderPaymentService customerOrderPaymentService;
    private final PaymentModulesManager paymentModulesManager;
    private final MailService mailService;
    private final MailComposer mailComposer;
    private final ThemeService themeService;

    private final FederationFacade federationFacade;

    public VoMailServiceImpl(final ShopService shopService,
                             final CustomerOrderService customerOrderService,
                             final CustomerWishListService customerWishListService,
                             final ProductSkuService productSkuService,
                             final CustomerOrderPaymentService customerOrderPaymentService,
                             final PaymentModulesManager paymentModulesManager,
                             final MailService mailService,
                             final MailComposer mailComposer,
                             final ThemeService themeService,
                             final FederationFacade federationFacade) {
        this.shopService = shopService;
        this.customerOrderService = customerOrderService;
        this.customerWishListService = customerWishListService;
        this.productSkuService = productSkuService;
        this.customerOrderPaymentService = customerOrderPaymentService;
        this.paymentModulesManager = paymentModulesManager;
        this.mailService = mailService;
        this.mailComposer = mailComposer;
        this.federationFacade = federationFacade;
        this.themeService = themeService;
    }

    @Override
    public String getShopMail(final long shopId, final String template, final String order, final String delivery, final String customer, final String format) throws Exception {

        if (!federationFacade.isManageable(shopId, ShopDTO.class)) {
            throw new AccessDeniedException("Access is denied");
        }

        CustomerOrder customerOrder = null;
        CustomerOrderDelivery customerOrderDelivery = null;
        Customer customerAccount = null;

        if (StringUtils.isNotBlank(order))  {
            customerOrder = customerOrderService.findByReference(order);
        }

        if (StringUtils.isNotBlank(customer)) {
            customerOrder = customerOrderService.findByReference(order);
            if (customerOrder != null) {
                customerAccount = customerOrder.getCustomer();
            }
        }

        if (customerOrder == null || (customerOrder.getShop().getShopId() != shopId &&
                (customerOrder.getShop().getMaster() == null || customerOrder.getShop().getMaster().getShopId() != shopId))) {
            throw new AccessDeniedException("Access is denied");
        }


        if (StringUtils.isNotBlank(delivery) && customerOrder.getDelivery() != null) {
            for (final CustomerOrderDelivery orderDelivery : customerOrder.getDelivery()) {
                if (delivery.equals(orderDelivery.getDeliveryNum())) {
                    customerOrderDelivery = orderDelivery;
                    break;
                }
            }
        }

        if (customerOrderDelivery == null && customerOrder.getDelivery() != null &&
                (template.contains("delivery") || template.contains("shipping") ||
                        template.contains("cant-allocate") || template.contains("shipment-complete"))) {
            for (final CustomerOrderDelivery orderDelivery : customerOrder.getDelivery()) {
                customerOrderDelivery = orderDelivery;
                break;
            }
        }

        final HashMap<String, Object> emailModel = new HashMap<>();
        final Shop configShop = customerOrder.getShop().getMaster() != null ? customerOrder.getShop().getMaster() : customerOrder.getShop();

        if (template.contains("payment") || template.contains("shipment-complete")) {

            final PaymentGateway gateway = paymentModulesManager.getPaymentGateway(customerOrder.getPgLabel(), configShop.getCode());
            if (gateway == null) {
                return "Cannot send payment email because gateway " + customerOrder.getPgLabel() + " is not resolved for " + configShop.getCode() + ", could it be disabled?";
            }

            List<CustomerOrderPayment> payments = null;

            if (template.contains("adm-")) {
                payments = customerOrderPaymentService.findPayments(customerOrder.getOrdernum(), null, (String) null, (String) null);
            }

            if (template.contains("shipment-complete")) {
                MailUtils.appendMethodParamaters(
                        emailModel,
                        new Object[]{ customerOrder, customerOrderDelivery.getDeliveryNum(), Collections.emptyMap() }
                );
            } else {
                MailUtils.appendMethodParamaters(
                        emailModel,
                        new Object[]{ customerOrder, Collections.emptyMap() }
                );
            }

            MailUtils.appendPaymentEmailParameters(
                    emailModel,
                    themeService.getMailTemplateChainByShopId(configShop.getShopId()),
                    template,
                    "test@example.com", customerOrder,
                    gateway,
                    Payment.PAYMENT_STATUS_OK,
                    payments);

            enrichMapWithProducts(emailModel, customerOrder);

            final Mail mail = mailService.getGenericDao().getEntityFactory().getByIface(Mail.class);

            String fromEmail = "test@example.com";

            mailComposer.composeMessage(
                    mail,
                    (String) emailModel.get(StandardMessageListener.SHOP_CODE),
                    (String) emailModel.get(StandardMessageListener.LOCALE),
                    (List<String>) emailModel.get(StandardMessageListener.TEMPLATE_FOLDER),
                    (String) emailModel.get(StandardMessageListener.TEMPLATE_NAME),
                    fromEmail,
                    (String) emailModel.get(StandardMessageListener.CUSTOMER_EMAIL),  //email recipient - to
                    null,
                    null,
                    emailModel);

            if ("txt".equalsIgnoreCase(format)) {
                return mailComposer.convertMessageToTXT(mail);
            }
            return mailComposer.convertMessageToHTML(mail);

        } else if (template.contains("customer") || template.contains("contactform") || template.contains("newsletter")
                || template.contains("managedlist") || template.contains("in-stock-product")) {

            final Shop shop = shopService.findById(shopId);

            emailModel.put("email", "test@example.com");
            emailModel.put("password", "PASSWORD");
            emailModel.put("authToken", null);
            emailModel.put("salutation", "Mr");
            emailModel.put("firstName", "Bob");
            emailModel.put("lastName", "Doe");
            emailModel.put("middleName", "J.");
            emailModel.put("shopUrl", Collections.singleton(shop.getDefaultShopUrl()));
            emailModel.put("shopSecureUrl", Collections.singleton(shop.getDefaultShopSecureUrl()));
            emailModel.put("shopPreferredUrl", Collections.singleton(shop.getDefaultShopPreferredUrl()));
            emailModel.put("shopName", shop.getName());


            final Map<String, Object> additionalData = new HashMap<>();
            additionalData.put("name", "Bob J. Doe");
            additionalData.put("phone", "+00 0000 000 000");
            additionalData.put("email", "test@example.com");
            additionalData.put("subject", "Test Email");
            additionalData.put("body", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");
            if (customerAccount != null) {
                additionalData.put("customerType", customerAccount.getCustomerType());
                additionalData.put("pricingPolicy", customerAccount.getPricingPolicy());
                additionalData.put("tag", customerAccount.getTag());
            } else {
                additionalData.put("customerType", "B2G");
                additionalData.put("pricingPolicy", null);
                additionalData.put("tag", null);
            }
            additionalData.put("newPerson", Boolean.TRUE);
            additionalData.put("requireApproval", Boolean.TRUE);
            additionalData.put("requireNotification", Boolean.TRUE);
            additionalData.put("requireNotificationEmails", "test@example.com");
            additionalData.put("callCentrePasswordReset", Boolean.TRUE);

            if (template.contains("managedlist")) {
                additionalData.put("managedListName", "Managed List 001");
                additionalData.put("managedListManagerEmail", "manager@example.com");
                additionalData.put("managedListCustomerEmail", "test@example.com");
                additionalData.put("managedListCustomerName", "Bob J. Doe");
                additionalData.put("rejectReason", "Rejected due to reason X");
                additionalData.put("managedListUri", "managedlist?list=Managed%20List%20001");
                additionalData.put("appliedCoupons", Arrays.asList("COUPON0001", "COUPON0002", "COUPON0003", "COUPON0004", "COUPON0005"));
            }

            if (template.contains("in-stock-product") && customerOrder.getOrderDetail().size() > 0) {

                final CustomerWishList wl = customerWishListService.getGenericDao().getEntityFactory().getByIface(CustomerWishList.class);
                wl.setRegularPriceCurrencyWhenAdded(customerOrder.getCurrency());
                wl.setRegularPriceWhenAdded(MoneyUtils.ONE);
                wl.setQuantity(BigDecimal.TEN);
                wl.setCustomer(customerAccount);
                wl.setVisibility(CustomerWishList.PRIVATE);
                wl.setWlType(CustomerWishList.REMIND_WHEN_WILL_BE_AVAILABLE);
                wl.setSupplierCode("S001");
                wl.setSkuCode("TEST-SKU");
                wl.setTag("Notify me");

                final ProductSku sku = productSkuService.getProductSkuBySkuCode(customerOrder.getOrderDetail().iterator().next().getProductSkuCode());

                final List<Pair<CustomerWishList, ProductSku>> notifications = new ArrayList<>();
                notifications.add(Pair.of(wl, sku));
                notifications.add(Pair.of(wl, sku));
                notifications.add(Pair.of(wl, sku));

                emailModel.put("root", notifications);
            }

            emailModel.put("additionalData", additionalData);

            final Mail mail = mailService.getGenericDao().getEntityFactory().getByIface(Mail.class);

            mailComposer.composeMessage(
                    mail,
                    shop.getCode(),
                    customerOrder.getLocale(),
                    themeService.getMailTemplateChainByShopId(configShop.getShopId()),
                    template,
                    "test@example.com",
                    "test@example.com",
                    null,
                    null,
                    emailModel);

            if ("txt".equalsIgnoreCase(format)) {
                return mailComposer.convertMessageToTXT(mail);
            }
            return mailComposer.convertMessageToHTML(mail);

        } else { // Order related

            Map<String, Object> params = null;
            for (final CustomerOrderDet det : customerOrder.getOrderDetail()) {
                final ProductSku sku = productSkuService.getProductSkuBySkuCode(det.getProductSkuCode());
                if (sku != null) {
                    params = Collections.singletonMap("sku", sku);
                    break;
                }
            }

            MailUtils.appendOrderEmailParameters(
                    emailModel,
                    themeService.getMailTemplateChainByShopId(configShop.getShopId()),
                    template,
                    "test@example.com",
                    customerOrder,
                    customerOrderDelivery,
                    params
            );

            emailModel.put(StandardMessageListener.SUP_DELIVERIES, customerOrder.getDelivery());

            emailModel.put(AttributeNamesKeys.CustomerOrder.ORDER_TRANSITION_USER, "TransitionUserID");
            emailModel.put(AttributeNamesKeys.CustomerOrder.ORDER_TRANSITION_AUDIT_MESSAGE, "Transition internal remarks");
            emailModel.put(AttributeNamesKeys.CustomerOrder.ORDER_TRANSITION_CLIENT_MESSAGE, "Transition client message");

            enrichMapWithProducts(emailModel, customerOrder);

            final Mail mail = mailService.getGenericDao().getEntityFactory().getByIface(Mail.class);

            String fromEmail = "test@example.com";

            mailComposer.composeMessage(
                    mail,
                    (String) emailModel.get(StandardMessageListener.SHOP_CODE),
                    (String) emailModel.get(StandardMessageListener.LOCALE),
                    (List<String>) emailModel.get(StandardMessageListener.TEMPLATE_FOLDER),
                    (String) emailModel.get(StandardMessageListener.TEMPLATE_NAME),
                    fromEmail,
                    (String) emailModel.get(StandardMessageListener.CUSTOMER_EMAIL),  //email recipient - to
                    null,
                    null,
                    emailModel);

            if ("txt".equalsIgnoreCase(format)) {
                return mailComposer.convertMessageToTXT(mail);
            }
            return mailComposer.convertMessageToHTML(mail);

        }

    }


    /**
     * Enrich given map with product date.
     *
     * @param map given map to enrich
     * @param customerOrder customer order
     */
    private void enrichMapWithProducts(final Map<String, Object> map, final CustomerOrder customerOrder) {

        final Map<String, ProductSku> products = new HashMap<>();

        for (final CustomerOrderDet orderDet : customerOrder.getOrderDetail()) {

            final ProductSku sku = productSkuService.getProductSkuBySkuCode(orderDet.getProductSkuCode());
            if (sku != null) {
                products.put(sku.getCode(), sku);
            }

        }

        map.put(StandardMessageListener.PRODUCTS, products);
    }


}
