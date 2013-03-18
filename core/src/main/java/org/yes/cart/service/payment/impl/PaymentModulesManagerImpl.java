/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
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

package org.yes.cart.service.payment.impl;

import org.apache.commons.lang.StringUtils;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.payment.PaymentGateway;
import org.yes.cart.payment.PaymentModule;
import org.yes.cart.payment.persistence.entity.PaymentGatewayDescriptor;
import org.yes.cart.service.domain.SystemService;
import org.yes.cart.service.locator.ServiceLocator;
import org.yes.cart.service.payment.PaymentModulesManager;
import org.yes.cart.util.ShopCodeContext;

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
                    ShopCodeContext.getLog().info("Loading payment module from url {}", url);
                    final PaymentModule paymentModule = serviceLocator.getServiceInstance(url, PaymentModule.class, null, null); //passwd & login not need set of payment gateways
                    paymentModulesMap.put(
                            paymentModule.getPaymentModuleDescriptor().getLabel(),
                            paymentModule
                    );
                } catch (Throwable e) {
                    ShopCodeContext.getLog().error(
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
    public void allowPaymentGateway(final String label) {

        String allowed = systemService.getAttributeValue(AttributeNamesKeys.System.SYSTEM_ACTIVE_PAYMENT_GATEWAYS_LABEL);

        if (StringUtils.isBlank(allowed)) {       //not yet allowed

            systemService.updateAttributeValue(
                    AttributeNamesKeys.System.SYSTEM_ACTIVE_PAYMENT_GATEWAYS_LABEL,
                    label
            );

        } else  if (!allowed.contains(label)) {       //not yet allowed

            if (allowed.endsWith(","))  {
                allowed += label;
            } else {
                allowed += ',' + label;
            }

            systemService.updateAttributeValue(
                    AttributeNamesKeys.System.SYSTEM_ACTIVE_PAYMENT_GATEWAYS_LABEL,
                    allowed
            );

        }


    }

    /**
     * {@inheritDoc}
     */
    public void disallowPaymentGateway(final String label) {

        String allowed = systemService.getAttributeValue(AttributeNamesKeys.System.SYSTEM_ACTIVE_PAYMENT_GATEWAYS_LABEL);

        if (allowed.contains(label)) {       //need to remove

            allowed = StringUtils.remove(allowed, label).replace(",,",",");

            if (allowed.endsWith(",")) {

                allowed = StringUtils.chop(allowed);

            }

            if (allowed.startsWith(",")) {

                allowed = allowed.substring(1);

            }

            systemService.updateAttributeValue(
                    AttributeNamesKeys.System.SYSTEM_ACTIVE_PAYMENT_GATEWAYS_LABEL,
                    allowed
            );

        }

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
            final String allowed = systemService.getAttributeValue(AttributeNamesKeys.System.SYSTEM_ACTIVE_PAYMENT_GATEWAYS_LABEL);
            if (StringUtils.isNotBlank(allowed)) {
                final List<PaymentGatewayDescriptor> allowedDescr = new ArrayList<PaymentGatewayDescriptor>();
                for (PaymentGatewayDescriptor descriptor : paymentGatewayDescriptors) {
                    if (allowed.contains(descriptor.getLabel())) {
                        allowedDescr.add(descriptor);
                    }
                }
                paymentGatewayDescriptors.retainAll(allowedDescr);
            } else {
                paymentGatewayDescriptors.clear();

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
        ShopCodeContext.getLog().error("Payment gateway {} not found", paymentGatewayLabel);

        return null;
    }
}
