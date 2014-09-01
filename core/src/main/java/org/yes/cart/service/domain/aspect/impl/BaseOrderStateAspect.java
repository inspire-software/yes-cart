package org.yes.cart.service.domain.aspect.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.core.task.TaskExecutor;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.CustomerOrderDelivery;
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.i18n.impl.FailoverStringI18NModel;
import org.yes.cart.domain.message.consumer.StandardMessageListener;
import org.yes.cart.service.order.OrderEvent;

import javax.servlet.ServletContext;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * Base class for order aspects.
 *
 * User: iazarny@yahoo.com
 * Date: 10/20/12
 * Time: 11:07 AM
 */
public abstract class BaseOrderStateAspect extends  BaseNotificationAspect  {

    private ServletContext servletContext;

    /**
     * Construct base notification aspect class.
     *
     * @param taskExecutor to use
     */
    public BaseOrderStateAspect(final TaskExecutor taskExecutor) {
        super(taskExecutor);
    }

    /**
     * Create email and sent it.
     *
     * @param orderEvent        given order event
     * @param emailTemplateName optional email template name
     * @param emailsAddresses   set of email addresses
     * @param params            additional params
     */
    protected void fillNotificationParameters(final OrderEvent orderEvent, final String emailTemplateName, final Map<String,Object> params, final String... emailsAddresses) {

        if (StringUtils.isNotBlank(emailTemplateName)) {

            final CustomerOrder customerOrder = orderEvent.getCustomerOrder();

            for (String emailAddr : emailsAddresses) {

                final HashMap<String, Object> map = new HashMap<String, Object>();

                if (params != null) {

                    map.putAll(params);
                }

                map.put(StandardMessageListener.SHOP_CODE, customerOrder.getShop().getCode());
                map.put(StandardMessageListener.CUSTOMER_EMAIL, emailAddr);
                map.put(StandardMessageListener.RESULT, true);
                map.put(StandardMessageListener.ROOT, customerOrder);
                // TODO: YC-416 Unify all mail templates into a separate module
                map.put(StandardMessageListener.TEMPLATE_FOLDER, servletContext.getRealPath(customerOrder.getShop().getMailFolder()) + File.separator);
                map.put(StandardMessageListener.SHOP, customerOrder.getShop());
                map.put(StandardMessageListener.CUSTOMER, customerOrder.getCustomer());
                map.put(StandardMessageListener.TEMPLATE_NAME, emailTemplateName);

                if (orderEvent.getCustomerOrderDelivery() != null) {
                    final CustomerOrderDelivery delivery = orderEvent.getCustomerOrderDelivery();
                    final I18NModel carrierName = new FailoverStringI18NModel(
                            delivery.getCarrierSla().getCarrier().getDisplayName(),
                            delivery.getCarrierSla().getCarrier().getName());
                    map.put(StandardMessageListener.DELIVERY_CARRIER, carrierName.getValue(customerOrder.getLocale()));
                    final I18NModel carrierSlaName = new FailoverStringI18NModel(
                            delivery.getCarrierSla().getDisplayName(),
                            delivery.getCarrierSla().getName());
                    map.put(StandardMessageListener.DELIVERY_CARRIER_SLA, carrierSlaName.getValue(customerOrder.getLocale()));
                    map.put(StandardMessageListener.DELIVERY_NUM, delivery.getDeliveryNum());
                    map.put(StandardMessageListener.DELIVERY_EXTERNAL_NUM, delivery.getRefNo());
                }

                sendNotification(map);

            }



        }


    }
        /**
        * Create email and sent it.
        *
        * @param orderEvent       given order event
        * @param emailTempateName optional email tempate name
        * @param emailsAddresses  set of email addresses
        */
    protected void fillNotificationParameters(final OrderEvent orderEvent, final String emailTempateName, final String... emailsAddresses) {

        fillNotificationParameters( orderEvent, emailTempateName, null, emailsAddresses);

    }



    /** {@inheritDoc} */
    public void setServletContext(final ServletContext servletContext) {
        this.servletContext = servletContext;
    }

}
