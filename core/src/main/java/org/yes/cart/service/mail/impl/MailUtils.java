package org.yes.cart.service.mail.impl;

import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.CustomerOrderDelivery;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.i18n.impl.FailoverStringI18NModel;
import org.yes.cart.domain.message.consumer.StandardMessageListener;
import org.yes.cart.payment.PaymentGateway;
import org.yes.cart.payment.dto.PaymentGatewayFeature;
import org.yes.cart.payment.persistence.entity.CustomerOrderPayment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 08/06/2017
 * Time: 09:46
 */
public final class MailUtils {

    private MailUtils() {
        // no instance
    }

    /**
     * Append method parameters to the model.
     *
     * @param mailModel     mail model
     * @param args          method arguments
     */
    public static void appendMethodParamaters(final HashMap<String, Object> mailModel,
                                              final Object[] args) {
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                mailModel.put(StandardMessageListener.PARAM_PREFIX + i, args[i]);
            }
        }

    }

    /**
     * Append all payment template specific parameters to mailModel.
     *
     * @param mailModel      mail model
     * @param themeChain     theme chain to use
     * @param template       template to use
     * @param mailTo         email recipient
     * @param order          customer order
     * @param gateway        payment gateway for given order
     * @param paymentResult  payment result
     * @param payments       all payments for this order
     */
    public static void appendPaymentEmailParameters(final HashMap<String, Object> mailModel,
                                                    final List<String> themeChain,
                                                    final String template,
                                                    final String mailTo,
                                                    final CustomerOrder order,
                                                    final PaymentGateway gateway,
                                                    final String paymentResult,
                                                    final List<CustomerOrderPayment> payments) {

        final Shop orderShop = order.getShop();
        final Shop shop = orderShop.getMaster() != null ? orderShop.getMaster() : orderShop;

        mailModel.put(StandardMessageListener.TEMPLATE_FOLDER, themeChain);
        mailModel.put(StandardMessageListener.TEMPLATE_NAME, template);
        mailModel.put(StandardMessageListener.CUSTOMER_EMAIL, mailTo);

        mailModel.put(StandardMessageListener.ROOT, order);
        mailModel.put(StandardMessageListener.RESULT, paymentResult);

        mailModel.put(StandardMessageListener.SHOP_CODE, shop.getCode());
        mailModel.put(StandardMessageListener.SHOP, shop);
        mailModel.put(StandardMessageListener.CUSTOMER_SHOP_CODE, orderShop.getCode());
        mailModel.put(StandardMessageListener.CUSTOMER_SHOP, orderShop);

        mailModel.put(StandardMessageListener.CUSTOMER, order.getCustomer());
        mailModel.put(StandardMessageListener.SHIPPING_ADDRESS, order.getShippingAddressDetails());
        mailModel.put(StandardMessageListener.BILLING_ADDRESS, order.getBillingAddressDetails());
        mailModel.put(StandardMessageListener.LOCALE, order.getLocale());

        final PaymentGatewayFeature feature = gateway.getPaymentGatewayFeatures();
        mailModel.put(StandardMessageListener.PAYMENT_GATEWAY_FEATURE, feature);

        final Map<String, String> carrier = new HashMap<>();
        final Map<String, String> carrierSla = new HashMap<>();
        for (final CustomerOrderDelivery delivery : order.getDelivery()) {

            final I18NModel carrierName = new FailoverStringI18NModel(
                    delivery.getCarrierSla().getCarrier().getDisplayName(),
                    delivery.getCarrierSla().getCarrier().getName());
            carrier.put(delivery.getDeliveryNum(), carrierName.getValue(order.getLocale()));
            final I18NModel carrierSlaName = new FailoverStringI18NModel(
                    delivery.getCarrierSla().getDisplayName(),
                    delivery.getCarrierSla().getName());
            carrierSla.put(delivery.getDeliveryNum(), carrierSlaName.getValue(order.getLocale()));
        }
        mailModel.put(StandardMessageListener.DELIVERY_CARRIER, carrier);
        mailModel.put(StandardMessageListener.DELIVERY_CARRIER_SLA, carrierSla);

        mailModel.put(StandardMessageListener.PAYMENTS, payments);

    }


    /**
     * Create email and sent it.
     *
     * @param mailModel      mail model
     * @param themeChain     theme chain to use
     * @param template       template to use
     * @param mailTo         email recipient
     * @param order          customer order
     * @param delivery       optional customer delivery
     * @param params         additional params
     */
    public static void appendOrderEmailParameters(final HashMap<String, Object> mailModel,
                                                  final List<String> themeChain,
                                                  final String template,
                                                  final String mailTo,
                                                  final CustomerOrder order,
                                                  final CustomerOrderDelivery delivery,
                                                  final Map<String,Object> params) {

        final Shop orderShop = order.getShop();
        final Shop shop = orderShop.getMaster() != null ? orderShop.getMaster() : orderShop;

        if (params != null) {

            mailModel.putAll(params);
        }

        mailModel.put(StandardMessageListener.TEMPLATE_FOLDER, themeChain);
        mailModel.put(StandardMessageListener.TEMPLATE_NAME, template);
        mailModel.put(StandardMessageListener.CUSTOMER_EMAIL, mailTo);

        mailModel.put(StandardMessageListener.ROOT, order);
        mailModel.put(StandardMessageListener.RESULT, true);

        mailModel.put(StandardMessageListener.SHOP_CODE, shop.getCode());
        mailModel.put(StandardMessageListener.SHOP, shop);
        mailModel.put(StandardMessageListener.CUSTOMER_SHOP_CODE, orderShop.getCode());
        mailModel.put(StandardMessageListener.CUSTOMER_SHOP, orderShop);

        mailModel.put(StandardMessageListener.CUSTOMER, order.getCustomer());
        mailModel.put(StandardMessageListener.SHIPPING_ADDRESS, order.getShippingAddressDetails());
        mailModel.put(StandardMessageListener.BILLING_ADDRESS, order.getBillingAddressDetails());
        mailModel.put(StandardMessageListener.LOCALE, order.getLocale());

        if (delivery != null) {
            mailModel.put(StandardMessageListener.DELIVERY, delivery);
            final I18NModel carrierName = new FailoverStringI18NModel(
                    delivery.getCarrierSla().getCarrier().getDisplayName(),
                    delivery.getCarrierSla().getCarrier().getName());
            mailModel.put(StandardMessageListener.DELIVERY_CARRIER, carrierName.getValue(order.getLocale()));
            final I18NModel carrierSlaName = new FailoverStringI18NModel(
                    delivery.getCarrierSla().getDisplayName(),
                    delivery.getCarrierSla().getName());
            mailModel.put(StandardMessageListener.DELIVERY_CARRIER_SLA, carrierSlaName.getValue(order.getLocale()));
            mailModel.put(StandardMessageListener.DELIVERY_NUM, delivery.getDeliveryNum());
            mailModel.put(StandardMessageListener.DELIVERY_EXTERNAL_NUM, delivery.getRefNo());
        } else {
            final Map<String, String> carrier = new HashMap<>();
            final Map<String, String> carrierSla = new HashMap<>();
            for (final CustomerOrderDelivery orderDelivery : order.getDelivery()) {

                final I18NModel carrierName = new FailoverStringI18NModel(
                        orderDelivery.getCarrierSla().getCarrier().getDisplayName(),
                        orderDelivery.getCarrierSla().getCarrier().getName());
                carrier.put(orderDelivery.getDeliveryNum(), carrierName.getValue(order.getLocale()));
                final I18NModel carrierSlaName = new FailoverStringI18NModel(
                        orderDelivery.getCarrierSla().getDisplayName(),
                        orderDelivery.getCarrierSla().getName());
                carrierSla.put(orderDelivery.getDeliveryNum(), carrierSlaName.getValue(order.getLocale()));
            }
            mailModel.put(StandardMessageListener.DELIVERY_CARRIER, carrier);
            mailModel.put(StandardMessageListener.DELIVERY_CARRIER_SLA, carrierSla);
        }

    }



}
