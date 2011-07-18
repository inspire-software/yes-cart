package org.yes.cart.service.payment.impl;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.payment.PaymentGateway;
import org.yes.cart.payment.PaymentModule;
import org.yes.cart.payment.persistence.entity.PaymentGatewayDescriptor;
import org.yes.cart.service.domain.SystemService;
import org.yes.cart.service.locator.ServiceLocator;
import org.yes.cart.service.payment.PaymentModulesManager;

import java.text.MessageFormat;
import java.util.*;


/**
 * Payment modules manager implementation. Use service locator to work with modules.
 * <p/>
 * TODO add reinit method available for JMX
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class PaymentModulesManagerImpl implements PaymentModulesManager {

    private static final Logger LOG = LoggerFactory.getLogger(PaymentModulesManagerImpl.class);

    private final ServiceLocator serviceLocator;

    private final SystemService systemService;

    private List<String> modulesUrl = null;

    private Map<String, PaymentModule> paymentModulesMap;

    /**
     * Construct PG module smanager.
     *
     * @param serviceLocator service locator.
     * @param systemService to get the payment modules URLs
     */
    public PaymentModulesManagerImpl(
            final ServiceLocator serviceLocator,
            final SystemService systemService) {
        this.serviceLocator = serviceLocator;
        this.systemService = systemService;
    }


    /**
     * @return list of string treated as list or payment modules URLs, not as spring bean names. Example
     *  https://doma.com:1234/module1,https://othedomain.com/module2, values are posible. URL without protocol will be treated as spring bean name
     */
    private List<String> getModulesUrl() {
        if (modulesUrl == null) {
            modulesUrl = new ArrayList<String>();
            String urls = systemService.getAttributeValue(AttributeNamesKeys.System.SYSTEM_PAYMENT_MODULES_URLS);
            if (StringUtils.isNotBlank(urls)) {
                modulesUrl.addAll(
                        Arrays.asList(
                                urls.split(",")
                        )
                );
            }
        }
        return modulesUrl;
    }

    private synchronized Map<String, PaymentModule> getPaymentModulesMap() {
        if (paymentModulesMap == null) {
            paymentModulesMap = new HashMap<String, PaymentModule>();
            for (String url : getModulesUrl()) {
                try {
                    LOG.info(MessageFormat.format("Loading payment module from url {0}", url));
                    final PaymentModule paymentModule = serviceLocator.getServiceInstance(url, PaymentModule.class, null, null); //passwd & login not need set of payment gateways
                    paymentModulesMap.put(
                            paymentModule.getPaymentModuleDescriptor().getLabel(),
                            paymentModule
                    );
                } catch (Throwable e) {
                    LOG.error(
                            MessageFormat.format(
                                    "Cannot load payment module with url {0} error message is {1}. See trace for more details",
                                    url,
                                    e.getMessage()
                            ), e
                    );
                }
            }
        }
        return paymentModulesMap;
    }

    /**
     * {@inheritDoc}
     */
    public Collection<PaymentModule> getPaymentModules() {
        return getPaymentModulesMap().values();
    }

    /**
     * {@inheritDoc}
     */
    public Collection<PaymentGatewayDescriptor> getPaymentGatewaysDescriptors(final String paymentModuleLabel) {
        return getPaymentModulesMap().get(paymentModuleLabel).getPaymentGateways();
    }


    /**
     * {@inheritDoc}
     */
    public List<PaymentGatewayDescriptor> getPaymentGatewaysDescriptors(final boolean allModules) {
        final List<PaymentGatewayDescriptor> paymentGatewayDescriptors = new ArrayList<PaymentGatewayDescriptor>();

        for (Map.Entry<String, PaymentModule> moduleEntry : getPaymentModulesMap().entrySet()) {
            paymentGatewayDescriptors.addAll(
                    getPaymentModulesMap().get(moduleEntry.getKey()).getPaymentGateways()
            );
        }
        if (!allModules) {
            final String allowed = systemService.getAttributeValue(AttributeNamesKeys.System.SYSTEM_ACTIVE_PAYMENT_GATEWAYS_LABELS);
            if (StringUtils.isNotBlank(allowed)) {
                //final String [] labels =  allowed.split(",");
                final List<PaymentGatewayDescriptor> allowedDescr = new ArrayList<PaymentGatewayDescriptor>();
                for (PaymentGatewayDescriptor descriptor : paymentGatewayDescriptors) {
                    if (allowed.indexOf(descriptor.getLabel()) > -1) {
                        allowedDescr.add(descriptor);

                    }
                }
                paymentGatewayDescriptors.retainAll(allowedDescr);
            }

        }
        Collections.sort(
                paymentGatewayDescriptors,
                new Comparator<PaymentGatewayDescriptor>() {
                    public int compare(final PaymentGatewayDescriptor pgd1, final PaymentGatewayDescriptor pgd2) {
                        return (pgd1.getPriority() < pgd2.getPriority() ? -1 : (pgd1.getPriority() == pgd2.getPriority() ? 0 : 1));
                    }
                }
        );
        return paymentGatewayDescriptors;
    }

    /**
     * {@inheritDoc}
     */
    public PaymentGateway getPaymentGateway(final String paymentGatewayLabel) {
        //TODO refactor need map label - descriptor

        for (PaymentGatewayDescriptor pgDescriptor : getPaymentGatewaysDescriptors(true)) {
            if (pgDescriptor.getLabel().equals(paymentGatewayLabel)) {
                return serviceLocator.getServiceInstance(
                        pgDescriptor.getUrl(),
                        PaymentGateway.class,
                        pgDescriptor.getLogin(),
                        pgDescriptor.getPassword()
                );
            }
        }
        LOG.error(MessageFormat.format("Payment gateway {0} not found",  paymentGatewayLabel));

        return null;
    }
}
