package org.yes.cart.payment.impl;


import com.google.checkout.sdk.domain.Address;
import com.google.checkout.sdk.domain.*;
import com.google.checkout.sdk.notifications.BaseNotificationDispatcher;
import com.google.checkout.sdk.notifications.Notification;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.Restrictions;
import org.springframework.context.ApplicationContext;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.entity.*;
import org.yes.cart.payment.persistence.entity.GoogleNotificationHistory;
import org.yes.cart.payment.persistence.entity.impl.GoogleNotificationHistoryEntity;
import org.yes.cart.payment.persistence.service.PaymentModuleGenericDAO;
import org.yes.cart.service.domain.*;
import org.yes.cart.service.order.OrderAssembler;
import org.yes.cart.util.ShopCodeContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.List;

/**
 * Google checkout notification dispatcher.
 * <p/>
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 16/01/12
 * Time: 5:13 PM
 */
public class GoogleNotificationDispatcherImpl extends BaseNotificationDispatcher {

    private final ApplicationContext applicationContext;

    private CustomerOrderService customerOrderService;

    private CustomerService customerService;

    private AttributeService attributeService;

    private OrderAssembler orderAssembler;

    private CarrierSlaService carrierSlaService;

    private PaymentModuleGenericDAO<GoogleNotificationHistory, Long> googleNotificationPaymentDao;


    /**
     * Construct dispatcher.
     *
     * @param request            request
     * @param response           responce.
     * @param applicationContext spring app context
     */
    public GoogleNotificationDispatcherImpl(final ApplicationContext applicationContext, final HttpServletRequest request, final HttpServletResponse response) {
        super(request, response);
        this.applicationContext = applicationContext;
    }

    /**
     * The onNewOrderNotification called by GC when customer confirm order.
     * Amount is authorized by google.
     * <p/>
     * Here we
     * 1. Collect information about customer, create new one if it not existst.
     * 2. Wire billing and shipping addresses to order.
     * 3. Update shipping information.
     * <p/>
     * {@inheritDoc}
     */
    protected void onNewOrderNotification(final OrderSummary orderSummary,
                                          final NewOrderNotification notification) throws Exception {
        ShopCodeContext.getLog(this).info("BaseNotificationDispatcher#onNewOrderNotification {} ", notification);

        final String orderGuid = getOrderGuid(orderSummary);
        final CustomerOrder customerOrder = getCustomerOrderService().findByGuid(orderGuid);
        final Customer customer = createrCustomer(notification, customerOrder.getShop());

        if (customerOrder.getCustomer() == null) {
            customerOrder.setCustomer(customer);
            getCustomerOrderService().update(customerOrder);
        }

        enrichWithAddresses(customer, notification);

        enrichWithMessage(customerOrder, notification);

        customerOrder.setBillingAddress(getOrderAssembler().formatAddress(adaptGoogleAddress(notification.getBuyerBillingAddress())));

        customerOrder.setShippingAddress(getOrderAssembler().formatAddress(adaptGoogleAddress(notification.getBuyerShippingAddress())));


        final CustomerOrderDelivery customerOrderDelivery = customerOrder.getDelivery().iterator().next();
        final CarrierSla carrierSla = getCarrierSlaService().finaByName(
                orderSummary.getOrderAdjustment().getShipping().getFlatRateShippingAdjustment().getShippingName());

        if (carrierSla != null) {
            // TODO: YC-303 Rework ShopDiscount and ShopDiscountRule domain objects
            customerOrderDelivery.setCarrierSla(carrierSla); // only one delivery, so just set sla
            if (carrierSla.getPrice() != null) {
                customerOrderDelivery.setPrice(carrierSla.getPrice());
            } else {
                customerOrderDelivery.setPrice(BigDecimal.ZERO);
            }
        }

        customerOrder.setOrdernum(orderSummary.getGoogleOrderNumber()); //switch to google order number instead of internal


        getCustomerOrderService().update(customerOrder);
        //TODO: YC-147 perform auth transition

    }


    /**
     * The onAuthAmountNotification method is called when you receive notice that an order is ready to be
     * shipped and charged; in the above code, we print out the order number and buyer's name.
     * <p/>
     * So the GC make card authorization before on new order notification.
     * onAuthAmountNotification called on fund capture.
     *
     * @param orderSummary {@link OrderSummary}
     * @param notification {@link AuthorizationAmountNotification}
     */
    @Override
    public void onAuthorizationAmountNotification(final OrderSummary orderSummary, final AuthorizationAmountNotification notification) {

        ShopCodeContext.getLog(this).info("BaseNotificationDispatcher#onAuthorizationAmountNotification {}", notification);

    }

    @Override
    protected void onOrderStateChangeNotification(
            final OrderSummary orderSummary,
            final OrderStateChangeNotification notification) throws Exception {
        ShopCodeContext.getLog(this).info("#onOrderStateChangeNotification order summary is : {} notification is {} ", orderSummary, notification);

    }

    @Override
    public boolean hasAlreadyHandled(final String serialNumber,
                                     final OrderSummary orderSummary,
                                     final Notification notification) {
        ShopCodeContext.getLog(this).info("BaseNotificationDispatcher#hasAlreadyHandled {} {} ", serialNumber, notification);
        return getPaymentModuleGenericDAO().findSingleByCriteria(Restrictions.eq("serialNumber", serialNumber)) != null;
    }

    @Override
    protected void rememberSerialNumber(final String serialNumber,
                                        final OrderSummary orderSummary, final Notification notification) {

        ShopCodeContext.getLog(this).info("BaseNotificationDispatcher#rememberSerialNumber {} {} ", serialNumber, notification);

        final GoogleNotificationHistory entity = new GoogleNotificationHistoryEntity();
        entity.setSerialNumber(serialNumber);
        entity.setNotification(notification.toString());
        getPaymentModuleGenericDAO().create(entity);

    }

    /**
     * Enrich with message.
     *
     * @param customerOrder order to enrich.
     * @param notification  notification.
     */
    private void enrichWithMessage(CustomerOrder customerOrder, NewOrderNotification notification) {
        if (notification.getShoppingCart().getBuyerMessages() != null
                && notification.getShoppingCart().getBuyerMessages().getAllBuyerMessages() != null) {
            final StringBuilder stringBuilder = new StringBuilder();
            for (Object str : notification.getShoppingCart().getBuyerMessages().getAllBuyerMessages()) {
                stringBuilder.append(str);
                stringBuilder.append('\n');
            }
            customerOrder.setOrderMessage(stringBuilder.toString());
        }
    }


    /**
     * Enrich customer with addresses in case if customer has not any addreses.
     *
     * @param customer     customer.
     * @param notification notification to get addrtesses from .
     */
    private void enrichWithAddresses(final Customer customer, final NewOrderNotification notification) {

        if (!customer.getAddress().isEmpty()) {

            final org.yes.cart.domain.entity.Address billingAddress = adaptGoogleAddress(notification.getBuyerBillingAddress());
            if (billingAddress != null) {
                billingAddress.setDefaultAddress(true);
                billingAddress.setAddressType(org.yes.cart.domain.entity.Address.ADDR_TYPE_BILLING);
            }

            final org.yes.cart.domain.entity.Address shippingAddress = adaptGoogleAddress(notification.getBuyerShippingAddress());
            if (shippingAddress != null) {
                shippingAddress.setAddressType(org.yes.cart.domain.entity.Address.ADDR_TYPE_SHIPING);
            }

            getCustomerService().update(customer);

        }
    }

    /**
     * Adapt google address to out address.
     *
     * @param address address to adapt
     * @return address form our domain.
     */
    private org.yes.cart.domain.entity.Address adaptGoogleAddress(final Address address) {
        if (address != null) {
            org.yes.cart.domain.entity.Address rez =
                    getCustomerService().getGenericDao().getEntityFactory().getByIface(org.yes.cart.domain.entity.Address.class);
            rez.setAddrline1(address.getAddress1());
            rez.setAddrline2(address.getAddress2());
            rez.setCountryCode(address.getCountryCode());
            rez.setCity(address.getCity());
            rez.setStateCode(address.getRegion());
            rez.setFirstname(address.getStructuredName() == null ? "" : address.getStructuredName().getFirstName());
            rez.setLastname(address.getStructuredName() == null ? "" : address.getStructuredName().getLastName());
            rez.setPhoneList(address.getPhone());
            rez.setPostcode(address.getPostalCode());
            return rez;
        }
        return null;
    }


    /**
     * Get order guid from {@link OrderSummary} .
     *
     * @param orderSummary given {@link OrderSummary} .
     * @return order guid if it present in order summary, null otherwise
     */
    String getOrderGuid(final OrderSummary orderSummary) {
        List merchantData = orderSummary.getShoppingCart().getMerchantPrivateData().getContent();
        if (merchantData != null && !merchantData.isEmpty()) {
            return (String) merchantData.get(0);
        }
        return null;
    }


    /**
     * Create customer if it not exists or just find by email.
     *
     * @param notification google notification about new order.
     * @param shop         given shop
     * @return created customer
     */
    private Customer createrCustomer(final NewOrderNotification notification, final Shop shop) {

        final Address gAddress = (Address) ObjectUtils.defaultIfNull(
                notification.getBuyerBillingAddress(), notification.getBuyerShippingAddress());

        final String email = StringUtils.defaultIfEmpty(
                gAddress.getEmail(),
                String.valueOf(notification.getBuyerId())
        );

        Customer customer = getCustomerService().findCustomer(email);

        if (customer == null) {

            customer = getCustomerService().getGenericDao().getEntityFactory().getByIface(Customer.class);

            customer.setEmail(email);

            customer.setFirstname(
                    gAddress.getStructuredName().getFirstName()
            );

            customer.setLastname(
                    gAddress.getStructuredName().getLastName()
            );

            customer.setPassword(getPassPhrazeGenerator().getNextPassPhrase());

            getCustomerService().addAttribute(customer, AttributeNamesKeys.CUSTOMER_PHONE, gAddress.getPhone());

            if (notification.getBuyerMarketingPreferences() != null) {
                getCustomerService().addAttribute(customer, AttributeNamesKeys.MARKETING_OPT_IN, Boolean.toString(notification.getBuyerMarketingPreferences().isEmailAllowed()));
            }


            return getCustomerService().create(customer, shop);
        }

        return customer;


    }

    /**
     * Get passworg generator.
     * @return {@link PassPhrazeGenerator}
     */
    private PassPhrazeGenerator getPassPhrazeGenerator() {
        return  applicationContext.getBean("passPhraseGenerator", PassPhrazeGenerator.class);
    }



    /**
     * Get attribute service from application context.
     *
     * @return {@link AttributeService}
     */
    private AttributeService getAttributeService() {
        if (attributeService == null) {
            attributeService = applicationContext.getBean("attributeService", AttributeService.class);
        }
        return attributeService;
    }

    /**
     * Get customer order service.
     *
     * @return {@link org.yes.cart.service.domain.CustomerOrderService}
     */
    private CustomerOrderService getCustomerOrderService() {
        if (customerOrderService == null) {
            customerOrderService = applicationContext.getBean("customerOrderService", CustomerOrderService.class);
        }
        return customerOrderService;
    }

    /**
     * Get customer service.
     *
     * @return {@link CustomerService}
     */
    private CustomerService getCustomerService() {
        if (customerService == null) {
            customerService = applicationContext.getBean("customerService", CustomerService.class);
        }
        return customerService;
    }

    /**
     * Get the order assempbler
     *
     * @return {@link OrderAssembler}
     */
    private OrderAssembler getOrderAssembler() {
        if (orderAssembler == null) {
            orderAssembler = applicationContext.getBean("orderAssembler", OrderAssembler.class);
        }
        return orderAssembler;
    }

    /**
     * Get carrier sla service.
     *
     * @return {@link CarrierSlaService}
     */
    private CarrierSlaService getCarrierSlaService() {
        if (carrierSlaService == null) {
            carrierSlaService = applicationContext.getBean("carrierSlaService", CarrierSlaService.class);
        }
        return carrierSlaService;
    }


    /**
     * Get notificatin dao.
     *
     * @return notification dao.
     */
    @SuppressWarnings("unchecked")
    private PaymentModuleGenericDAO<GoogleNotificationHistory, Long> getPaymentModuleGenericDAO() {
        if (googleNotificationPaymentDao == null) {
            googleNotificationPaymentDao = applicationContext.getBean("googleNotificationPaymentDao", PaymentModuleGenericDAO.class);
        }
        return googleNotificationPaymentDao;
    }
}
