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

package org.yes.cart.service.payment.impl;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.entity.AttrValueShop;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.payment.PaymentGateway;
import org.yes.cart.payment.PaymentModule;
import org.yes.cart.payment.persistence.entity.PaymentGatewayDescriptor;
import org.yes.cart.payment.service.PaymentGatewayConfigurationVisitor;
import org.yes.cart.payment.service.impl.PaymentGatewayConfigurationVisitorImpl;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.service.domain.SystemService;
import org.yes.cart.service.locator.ServiceLocator;
import org.yes.cart.service.payment.PaymentModulesManager;
import org.yes.cart.util.ShopCodeContext;

import java.util.*;


/**
 * Payment modules manager implementation. Use service locator to work with modules.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class PaymentModulesManagerImpl implements PaymentModulesManager {

    private static final Logger LOG = LoggerFactory.getLogger(PaymentModulesManagerImpl.class);

    private final ServiceLocator serviceLocator;

    private final SystemService systemService;
    private final ShopService shopService;

    private final Map<String, PaymentModule> paymentModulesMap = new HashMap<String, PaymentModule>();

    /**
     * Construct PG module manager.
     *
     * @param serviceLocator service locator.
     * @param systemService to get the payment modules URLs
     * @param shopService to get shop enabled modules
     */
    public PaymentModulesManagerImpl(final ServiceLocator serviceLocator,
                                     final SystemService systemService,
                                     final ShopService shopService) {
        this.serviceLocator = serviceLocator;
        this.systemService = systemService;
        this.shopService = shopService;
    }

    /**
     * {@inheritDoc}
     */
    public Collection<PaymentModule> getPaymentModules() {
        return Collections.unmodifiableCollection(paymentModulesMap.values());
    }

    /**
     * {@inheritDoc}
     */
    public Collection<PaymentGatewayDescriptor> getPaymentGatewaysDescriptors(final String paymentModuleLabel) {
        return paymentModulesMap.get(paymentModuleLabel).getPaymentGateways();
    }

    /**
     * {@inheritDoc}
     */
    public void allowPaymentGateway(final String label) {

        final String[] allowed = StringUtils.split(systemService.getAttributeValue(AttributeNamesKeys.System.SYSTEM_ACTIVE_PAYMENT_GATEWAYS_LABEL), ',');

        if (allowed == null || allowed.length == 0) {       //not yet allowed

            systemService.updateAttributeValue(
                    AttributeNamesKeys.System.SYSTEM_ACTIVE_PAYMENT_GATEWAYS_LABEL,
                    label
            );

        } else  if (!Arrays.asList(allowed).contains(label)) {       //not yet allowed


            final List<String> updated = new ArrayList<String>(Arrays.asList(allowed));
            updated.add(label);

            systemService.updateAttributeValue(
                    AttributeNamesKeys.System.SYSTEM_ACTIVE_PAYMENT_GATEWAYS_LABEL,
                    StringUtils.join(updated, ',')
            );

        }

    }

    /**
     * {@inheritDoc}
     */
    public void allowPaymentGatewayForShop(final String label, final String shopCode) {

        final Shop shop = shopService.getShopByCode(shopCode);

        if (shop != null) {

            final AttrValueShop av = shop.getAttributeByCode(AttributeNamesKeys.Shop.SHOP_ACTIVE_PAYMENT_GATEWAYS_LABEL);

            if (av != null) {

                final String[] allowed = StringUtils.split(av.getVal(), ',');
                if (allowed == null || allowed.length == 0) {       //not yet allowed

                    shopService.updateAttributeValue(
                            shop.getShopId(),
                            AttributeNamesKeys.Shop.SHOP_ACTIVE_PAYMENT_GATEWAYS_LABEL,
                            label);

                } else  if (!Arrays.asList(allowed).contains(label)) {       //not yet allowed


                    final List<String> updated = new ArrayList<String>(Arrays.asList(allowed));
                    updated.add(label);

                    shopService.updateAttributeValue(
                            shop.getShopId(),
                            AttributeNamesKeys.Shop.SHOP_ACTIVE_PAYMENT_GATEWAYS_LABEL,
                            StringUtils.join(updated, ','));

                }

            } else {

                shopService.updateAttributeValue(
                        shop.getShopId(),
                        AttributeNamesKeys.Shop.SHOP_ACTIVE_PAYMENT_GATEWAYS_LABEL,
                        label);

            }

        }

    }

    /**
     * {@inheritDoc}
     */
    public void disallowPaymentGateway(final String label) {

        final String[] allowed = StringUtils.split(systemService.getAttributeValue(AttributeNamesKeys.System.SYSTEM_ACTIVE_PAYMENT_GATEWAYS_LABEL), ',');

        if (allowed != null && Arrays.asList(allowed).contains(label)) {       //need to remove

            final List<String> updated = new ArrayList<String>(Arrays.asList(allowed));
            updated.remove(label);

            systemService.updateAttributeValue(
                    AttributeNamesKeys.System.SYSTEM_ACTIVE_PAYMENT_GATEWAYS_LABEL,
                    StringUtils.join(updated, ',')
            );

        }

    }

    /**
     * {@inheritDoc}
     */
    public void disallowPaymentGatewayForShop(final String label, final String shopCode) {


        final Shop shop = shopService.getShopByCode(shopCode);
        if (shop != null) {

            final AttrValueShop av = shop.getAttributeByCode(AttributeNamesKeys.Shop.SHOP_ACTIVE_PAYMENT_GATEWAYS_LABEL);

            if (av != null) {

                final String[] allowed = StringUtils.split(av.getVal(), ',');
                if (allowed != null && Arrays.asList(allowed).contains(label)) {       //need to remove

                    final List<String> updated = new ArrayList<String>(Arrays.asList(allowed));
                    updated.remove(label);

                    shopService.updateAttributeValue(
                            shop.getShopId(),
                            AttributeNamesKeys.Shop.SHOP_ACTIVE_PAYMENT_GATEWAYS_LABEL,
                            StringUtils.join(updated, ','));

                }

            }

        }

    }

    /**
     * {@inheritDoc}
     */
    public List<PaymentGatewayDescriptor> getPaymentGatewaysDescriptors(final boolean includeDisabled, final String shopCode) {
        final List<PaymentGatewayDescriptor> paymentGatewayDescriptors = new ArrayList<PaymentGatewayDescriptor>();

        for (final Map.Entry<String, PaymentModule> moduleEntry : paymentModulesMap.entrySet()) {
            paymentGatewayDescriptors.addAll(moduleEntry.getValue().getPaymentGateways());
        }

        final boolean systemEnabledOnly = !includeDisabled || (shopCode != null && !"DEFAULT".equals(shopCode));
        final boolean shopEnabledOnly = systemEnabledOnly && !includeDisabled;

        if (systemEnabledOnly) {

            final String av = systemService.getAttributeValue(AttributeNamesKeys.System.SYSTEM_ACTIVE_PAYMENT_GATEWAYS_LABEL);
            final String[] allowed = StringUtils.split(av, ',');
            filterOutDisabledPaymentGateways(paymentGatewayDescriptors, allowed);

        }

        if (shopEnabledOnly) {

            final Shop shop = shopService.getShopByCode(shopCode);

            if (shop != null) {

                final AttrValueShop av = shop.getAttributeByCode(AttributeNamesKeys.Shop.SHOP_ACTIVE_PAYMENT_GATEWAYS_LABEL);

                if (av != null) {

                    final String[] allowed = StringUtils.split(av.getVal(), ',');
                    filterOutDisabledPaymentGateways(paymentGatewayDescriptors, allowed);

                } else {

                    filterOutDisabledPaymentGateways(paymentGatewayDescriptors, null);

                }
            }

        }

        Collections.sort(
                paymentGatewayDescriptors,
                new Comparator<PaymentGatewayDescriptor>() {
                    public int compare(final PaymentGatewayDescriptor pgd1, final PaymentGatewayDescriptor pgd2) {
                        return pgd1.getLabel().compareToIgnoreCase(pgd2.getLabel());
                    }
                }
        );
        return paymentGatewayDescriptors;
    }

    private void filterOutDisabledPaymentGateways(final List<PaymentGatewayDescriptor> paymentGatewayDescriptors, final String[] allowed) {

        if (allowed != null && allowed.length > 0) {

            final List<String> enabled = Arrays.asList(allowed);

            final List<PaymentGatewayDescriptor> allowedDescr = new ArrayList<PaymentGatewayDescriptor>();
            for (final PaymentGatewayDescriptor descriptor : paymentGatewayDescriptors) {
                if (enabled.contains(descriptor.getLabel())) {
                    allowedDescr.add(descriptor);
                }
            }
            paymentGatewayDescriptors.retainAll(allowedDescr);

        } else {

            paymentGatewayDescriptors.clear();

        }
    }

    /**
     * {@inheritDoc}
     */
    public PaymentGateway getPaymentGateway(final String paymentGatewayLabel, final String shopCode) {

        for (final Map.Entry<String, PaymentModule> moduleEntry : paymentModulesMap.entrySet()) {
            for (final PaymentGatewayDescriptor pgDescriptor : moduleEntry.getValue().getPaymentGateways()) {
                if (pgDescriptor.getLabel().equals(paymentGatewayLabel)) {

                    final PaymentGatewayConfigurationVisitor visitor = new PaymentGatewayConfigurationVisitorImpl(
                            (Map) Collections.singletonMap("shopCode", shopCode)
                    );

                    final PaymentGateway pg = serviceLocator.getServiceInstance(
                            pgDescriptor.getUrl(),
                            PaymentGateway.class,
                            pgDescriptor.getLogin(),
                            pgDescriptor.getPassword()
                    );

                    visitor.visit(pg);

                    return pg;

                }
            }
        }

        LOG.error("Payment gateway {} not found", paymentGatewayLabel);

        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void register(final PaymentModule paymentModule) {

        final String label = paymentModule.getPaymentModuleDescriptor().getLabel();
        final PaymentModule existing = paymentModulesMap.get(label);
        if (existing == null) {

            paymentModulesMap.put(label, paymentModule);

            LOG.info("Registering payment module {}", label);

        } else if (!existing.equals(paymentModule)) {
            throw new RuntimeException("Duplicate binding detected for payment module with label: " + label);
        }

    }
}
