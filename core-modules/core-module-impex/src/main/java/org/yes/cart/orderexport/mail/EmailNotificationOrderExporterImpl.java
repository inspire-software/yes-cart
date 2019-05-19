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

package org.yes.cart.orderexport.mail;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;
import org.yes.cart.config.ActiveConfiguration;
import org.yes.cart.config.ActiveConfigurationDetector;
import org.yes.cart.config.Configuration;
import org.yes.cart.config.ConfigurationContext;
import org.yes.cart.config.impl.ActiveConfigurationImpl;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.CustomerOrderDelivery;
import org.yes.cart.domain.entity.CustomerOrderDeliveryDet;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.i18n.impl.FailoverStringI18NModel;
import org.yes.cart.domain.message.consumer.StandardMessageListener;
import org.yes.cart.orderexport.OrderAutoExportProcessor;
import org.yes.cart.orderexport.OrderExporter;
import org.yes.cart.orderexport.impl.ExportResultImpl;
import org.yes.cart.service.domain.CustomerService;
import org.yes.cart.service.domain.MailService;
import org.yes.cart.service.domain.ProductSkuService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.service.mail.MailComposer;
import org.yes.cart.service.theme.ThemeService;
import org.yes.cart.utils.DateUtils;
import org.yes.cart.utils.log.Markers;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;

/**
 * Allows to export the order as an email summary containing delivery for specific fulfilment centre.
 *
 * Uses SHOP[ORDER_EXPORTER_MAIL_SUPPORTED_SUPPLIERS] attribute, which is of type Property and should contain
 * mapping of [Fulfilment Center Code].[Export eligibility]=[CSV of emails to send the email to]
 *
 * Example configuration:
 * Main.INITPAID=neworder@mainff.com
 * Secondary.INITPAID=specialorder@specialff.com,viporders@specialff.com
 *
 * Default configuration is that exporter will only trigger emails for orders in state: "os.in.progress",
 * "os.partially.shipped" and "os.completed". And it uses "sup-order-new" email template. This accounts for
 * standard configuration of the OrderAutoExportListenerImpl mappings in order state machine.
 *
 * User: denispavlov
 * Date: 20/02/2017
 * Time: 13:47
 */
public class EmailNotificationOrderExporterImpl implements OrderExporter, Configuration, ActiveConfigurationDetector {

    private static final Logger LOG = LoggerFactory.getLogger(EmailNotificationOrderExporterImpl.class);

    private final TaskExecutor taskExecutor;
    private final ThemeService themeService;
    private final MailService mailService;

    private final MailComposer mailComposer;

    private final CustomerService customerService;

    private final ShopService shopService;

    private final ProductSkuService productSkuService;

    private final Map<String, String> supplierTemplates;

    private String exporterId = "EmailNotificationOrderExporterImpl";

    private int priority = 100;

    private ConfigurationContext cfgContext;

    public EmailNotificationOrderExporterImpl(final TaskExecutor taskExecutor,
                                              final ThemeService themeService,
                                              final MailService mailService,
                                              final MailComposer mailComposer,
                                              final CustomerService customerService,
                                              final ShopService shopService,
                                              final ProductSkuService productSkuService,
                                              final Map<String, String> supplierTemplates) {
        this.taskExecutor = taskExecutor;
        this.themeService = themeService;
        this.mailService = mailService;
        this.mailComposer = mailComposer;
        this.customerService = customerService;
        this.shopService = shopService;
        this.productSkuService = productSkuService;
        this.supplierTemplates = supplierTemplates;
    }

    /** {@inheritDoc} */
    @Override
    public boolean supports(final CustomerOrder customerOrder, final Collection<CustomerOrderDelivery> customerOrderDeliveries) {
        if (!customerOrder.isBlockExport() && CollectionUtils.isNotEmpty(customerOrderDeliveries)) {
            final Properties map = getSupplierNotificationsMap(customerOrder);
            for (final CustomerOrderDelivery delivery : customerOrderDeliveries) {
                final Set<String> suppliers = getSupplierCodesAndExportType(customerOrder, delivery);
                for (final String supplier : suppliers) {
                    if (map.containsKey(supplier)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Returns supplier code and export eligibility type.
     *
     * E.g. if supplier code is "Main" and eligible for export value is "INITPAID" then key is "Main.INITPAID"
     *
     * @param customerOrder customer order
     * @param customerOrderDelivery delivery
     *
     * @return collection of code and types
     */
    protected Set<String> getSupplierCodesAndExportType(final CustomerOrder customerOrder, final CustomerOrderDelivery customerOrderDelivery) {

        final Collection<CustomerOrderDelivery> deliveries = customerOrderDelivery != null ?
                Collections.singleton(customerOrderDelivery) : customerOrder.getDelivery();

        final Set<String> supplierCodes = new HashSet<>();
        if (deliveries != null) {
            for (final CustomerOrderDelivery delivery : deliveries) {
                if (delivery.getEligibleForExport() != null && delivery.getDetail() != null) {
                    for (final CustomerOrderDeliveryDet detail : delivery.getDetail()) {

                        supplierCodes.add(detail.getSupplierCode().concat(".").concat(delivery.getEligibleForExport()));

                    }
                }
            }
        }

        return supplierCodes;


    }

    private Properties getSupplierNotificationsMap(final CustomerOrder customerOrder) {

        return getSupplierNotificationsMap(customerOrder.getShop(), true);

    }

    private Properties getSupplierNotificationsMap(final Shop orderShop, final boolean allowMaster) {

        final Properties props = new Properties();
        if (allowMaster) {
            loadProperties(props, orderShop.getMaster());
        }
        loadProperties(props, orderShop);
        return props;

    }

    private void loadProperties(final Properties props, final Shop shop) {
        if (shop != null) {
            final String config = shop.getAttributeValueByCode(AttributeNamesKeys.Shop.ORDER_EXPORTER_MAIL_SUPPORTED_SUPPLIERS);
            if (StringUtils.isNotBlank(config)) {
                try {
                    final Properties cofigProps = new Properties();
                    cofigProps.load(new StringReader(config));
                    props.putAll(cofigProps);
                } catch (IOException e) {
                    LOG.warn(Markers.alert(), "Unable to load properties for mail order exporter for {}", shop.getCode());
                    LOG.error(e.getMessage(), e);
                }
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public ExportResult export(final CustomerOrder customerOrder, final Collection<CustomerOrderDelivery> customerOrderDeliveries) throws Exception {

        final Properties map = getSupplierNotificationsMap(customerOrder);

        final Map<String, List<CustomerOrderDelivery>> deliveriesBySupplierCode = new HashMap<>();
        final List<CustomerOrderDelivery> exportCandidates = new ArrayList<>(customerOrderDeliveries);

        for (final CustomerOrderDelivery delivery : exportCandidates) {
            if (!delivery.isBlockExport() && delivery.getEligibleForExport() != null && !delivery.getDetail().isEmpty()) {
                final String supplierCode = delivery.getDetail().iterator().next().getSupplierCode();
                final String key = supplierCode.concat(".").concat(delivery.getEligibleForExport());
                if (map.containsKey(key)) {
                    List<CustomerOrderDelivery> dels = deliveriesBySupplierCode.get(supplierCode);
                    if (dels == null) {
                        dels = new ArrayList<>();
                        deliveriesBySupplierCode.put(key, dels);
                    }
                    dels.add(delivery);
                }
            }
        }

        final Set<Long> exported = new HashSet<>();
        final Map<String, String> audit = new HashMap<>();
        final String timestamp = DateUtils.formatSDT();

        for (final Map.Entry<String, List<CustomerOrderDelivery>> entry : deliveriesBySupplierCode.entrySet()) {

            final StringBuilder auditInfo = new StringBuilder();

            final String notify = map.getProperty(entry.getKey());

            auditInfo.append(notify).append(' ').append(customerOrder.getOrderStatus());

            fillNotificationParameters(
                    customerOrder, entry.getValue(),
                    supplierTemplates.get(customerOrder.getOrderStatus()),
                    Collections.singletonMap("supplierTemplateKey", entry.getKey()),
                    StringUtils.split(notify, ','));

            for (final CustomerOrderDelivery delivery : entry.getValue()) {
                exported.add(delivery.getCustomerOrderDeliveryId());
                auditInfo.append(' ').append(delivery.getDeliveryNum());
            }

            audit.put(getExporterId() + ": " + entry.getKey() + ": " + timestamp, auditInfo.toString());

        }

        return new ExportResultImpl(exported, audit);

    }


    /**
     * Create email and sent it.
     *
     * @param order             given order
     * @param deliveries        deliveries to be exported
     * @param emailTemplateName optional email template name
     * @param emailsAddresses   set of email addresses
     * @param params            additional params
     */
    protected void fillNotificationParameters(final CustomerOrder order,
                                              final List<CustomerOrderDelivery> deliveries,
                                              final String emailTemplateName,
                                              final Map<String,Object> params,
                                              final String... emailsAddresses) {

        if (StringUtils.isNotBlank(emailTemplateName)) {

            final Shop shop = order.getShop().getMaster() != null ? order.getShop().getMaster() : order.getShop();

            for (String emailAddr : emailsAddresses) {

                final HashMap<String, Object> map = new HashMap<>();

                if (params != null) {

                    map.putAll(params);
                }

                map.put(StandardMessageListener.SHOP_CODE, shop.getCode());
                map.put(StandardMessageListener.CUSTOMER_EMAIL, emailAddr);
                map.put(StandardMessageListener.RESULT, true);
                map.put(StandardMessageListener.ROOT, order);
                map.put(StandardMessageListener.TEMPLATE_FOLDER, themeService.getMailTemplateChainByShopId(shop.getShopId()));
                map.put(StandardMessageListener.SHOP, shop);
                map.put(StandardMessageListener.CUSTOMER, order.getCustomer());
                map.put(StandardMessageListener.SHIPPING_ADDRESS, order.getShippingAddressDetails());
                map.put(StandardMessageListener.BILLING_ADDRESS, order.getBillingAddressDetails());
                map.put(StandardMessageListener.TEMPLATE_NAME, emailTemplateName);
                map.put(StandardMessageListener.LOCALE, order.getLocale());

                final Map<String, String> carrier = new HashMap<>();
                final Map<String, String> carrierSla = new HashMap<>();
                for (final CustomerOrderDelivery delivery : deliveries) {

                    final I18NModel carrierName = new FailoverStringI18NModel(
                            delivery.getCarrierSla().getCarrier().getDisplayName(),
                            delivery.getCarrierSla().getCarrier().getName());
                    carrier.put(delivery.getDeliveryNum(), carrierName.getValue(order.getLocale()));
                    final I18NModel carrierSlaName = new FailoverStringI18NModel(
                            delivery.getCarrierSla().getDisplayName(),
                            delivery.getCarrierSla().getName());
                    carrierSla.put(delivery.getDeliveryNum(), carrierSlaName.getValue(order.getLocale()));
                }
                map.put(StandardMessageListener.DELIVERY_CARRIER, carrier);
                map.put(StandardMessageListener.DELIVERY_CARRIER_SLA, carrierSla);
                map.put(StandardMessageListener.SUP_DELIVERIES, deliveries);

                sendNotification(map);

            }



        }


    }


    private void sendNotification(final Object serializableMessage) {

        taskExecutor.execute(new StandardMessageListener(
                mailService,
                mailComposer,
                customerService,
                productSkuService,
                shopService,
                serializableMessage));

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ActiveConfiguration> getActive() {

        final List<ActiveConfiguration> active = new ArrayList<>();

        this.shopService.findAllIterator(shop -> {

            // check specific settings, so don't allow master
            final Properties map = getSupplierNotificationsMap(shop, false);

            for (final String key : map.stringPropertyNames()) {

                active.add(
                        new ActiveConfigurationImpl(
                                this.cfgContext.getName(),
                                this.cfgContext.getCfgInterface(),
                                shop.getCode().concat(".").concat(key)
                        )
                );

            }

            return true; // read all
        });

        return active;
    }

    /**
     * Spring IoC.
     *
     * @param autoExportListener listener
     */
    public void setOrderAutoExportListener(final OrderAutoExportProcessor autoExportListener) {
        autoExportListener.registerExporter(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getExporterId() {
        return exporterId;
    }

    /**
     * Exporter ID.
     *
     * @param exporterId ID
     */
    public void setExporterId(final String exporterId) {
        this.exporterId = exporterId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getPriority() {
        return priority;
    }

    /**
     * Set priority as natural order.
     *
     * @param priority priority
     */
    public void setPriority(final int priority) {
        this.priority = priority;
    }

    /** {@inheritDoc} */
    @Override
    public ConfigurationContext getCfgContext() {
        return cfgContext;
    }

    public void setCfgContext(final ConfigurationContext cfgContext) {
        this.cfgContext = cfgContext;
    }
}
