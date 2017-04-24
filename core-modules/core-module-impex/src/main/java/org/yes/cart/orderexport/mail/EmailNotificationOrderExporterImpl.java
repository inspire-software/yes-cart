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
import org.yes.cart.service.domain.CustomerService;
import org.yes.cart.service.domain.MailService;
import org.yes.cart.service.domain.ProductSkuService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.service.mail.MailComposer;
import org.yes.cart.service.theme.ThemeService;

import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * User: denispavlov
 * Date: 20/02/2017
 * Time: 13:47
 */
public class EmailNotificationOrderExporterImpl implements OrderExporter {

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

    private final ThreadLocal<SimpleDateFormat> formatter = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };

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

        final Set<String> supplierCodes = new HashSet<String>();
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

        final Shop shop = customerOrder.getShop().getMaster() != null ? customerOrder.getShop().getMaster() : customerOrder.getShop();

        final String suppliersMap = shop.getAttributeValueByCode(AttributeNamesKeys.Shop.ORDER_EXPORTER_MAIL_SUPPORTED_SUPPLIERS);
        final Properties props = new Properties();
        if (StringUtils.isNotBlank(suppliersMap)) {
            try {
                props.load(new StringReader(suppliersMap));
            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
            }
        }
        return props;
    }

    /** {@inheritDoc} */
    @Override
    public ExportResult export(final CustomerOrder customerOrder, final Collection<CustomerOrderDelivery> customerOrderDeliveries) throws Exception {

        final Properties map = getSupplierNotificationsMap(customerOrder);

        final Map<String, List<CustomerOrderDelivery>> deliveriesBySupplierCode = new HashMap<String, List<CustomerOrderDelivery>>();
        final List<CustomerOrderDelivery> exportCandidates = new ArrayList<CustomerOrderDelivery>(customerOrderDeliveries);

        for (final CustomerOrderDelivery delivery : exportCandidates) {
            if (!delivery.isBlockExport() && delivery.getEligibleForExport() != null && !delivery.getDetail().isEmpty()) {
                final String supplierCode = delivery.getDetail().iterator().next().getSupplierCode();
                final String key = supplierCode.concat(".").concat(delivery.getEligibleForExport());
                if (map.containsKey(key)) {
                    List<CustomerOrderDelivery> dels = deliveriesBySupplierCode.get(supplierCode);
                    if (dels == null) {
                        dels = new ArrayList<CustomerOrderDelivery>();
                        deliveriesBySupplierCode.put(key, dels);
                    }
                    dels.add(delivery);
                }
            }
        }

        final Set<Long> exported = new HashSet<Long>();
        final Map<String, String> audit = new HashMap<String, String>();
        final String timestamp = formatter.get().format(new Date());

        for (final Map.Entry<String, List<CustomerOrderDelivery>> entry : deliveriesBySupplierCode.entrySet()) {

            final StringBuilder auditInfo = new StringBuilder();

            final String notify = map.getProperty(entry.getKey());

            auditInfo.append(notify).append(' ').append(customerOrder.getOrderStatus());

            fillNotificationParameters(
                    customerOrder, entry.getValue(),
                    supplierTemplates.get(customerOrder.getOrderStatus()),
                    Collections.<String, Object>singletonMap("supplierTemplateKey", entry.getKey()),
                    StringUtils.split(notify, ','));

            for (final CustomerOrderDelivery delivery : entry.getValue()) {
                exported.add(delivery.getCustomerOrderDeliveryId());
                auditInfo.append(' ').append(delivery.getDeliveryNum());
            }

            audit.put(getExporterId() + ": " + entry.getKey() + ": " + timestamp, auditInfo.toString());

        }

        return new ExportResult() {
            @Override
            public Set<Long> getExportedDeliveryIds() {
                return exported;
            }

            @Override
            public Map<String, String> getOrderAuditParams() {
                return audit;
            }

            @Override
            public String getNextExportEligibilityForOrder() {
                return null; // Nothing to follow
            }

            @Override
            public Map<Long, String> getNextExportEligibilityForDelivery() {
                return Collections.emptyMap(); // Nothing to follow
            }
        };

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

            final CustomerOrder customerOrder = order;
            final Shop shop = customerOrder.getShop().getMaster() != null ? customerOrder.getShop().getMaster() : customerOrder.getShop();

            for (String emailAddr : emailsAddresses) {

                final HashMap<String, Object> map = new HashMap<String, Object>();

                if (params != null) {

                    map.putAll(params);
                }

                map.put(StandardMessageListener.SHOP_CODE, shop.getCode());
                map.put(StandardMessageListener.CUSTOMER_EMAIL, emailAddr);
                map.put(StandardMessageListener.RESULT, true);
                map.put(StandardMessageListener.ROOT, customerOrder);
                map.put(StandardMessageListener.TEMPLATE_FOLDER, themeService.getMailTemplateChainByShopId(shop.getShopId()));
                map.put(StandardMessageListener.SHOP, shop);
                map.put(StandardMessageListener.CUSTOMER, customerOrder.getCustomer());
                map.put(StandardMessageListener.SHIPPING_ADDRESS, customerOrder.getShippingAddressDetails());
                map.put(StandardMessageListener.BILLING_ADDRESS, customerOrder.getBillingAddressDetails());
                map.put(StandardMessageListener.TEMPLATE_NAME, emailTemplateName);
                map.put(StandardMessageListener.LOCALE, customerOrder.getLocale());

                final Map<String, String> carrier = new HashMap<String, String>();
                final Map<String, String> carrierSla = new HashMap<String, String>();
                for (final CustomerOrderDelivery delivery : deliveries) {

                    final I18NModel carrierName = new FailoverStringI18NModel(
                            delivery.getCarrierSla().getCarrier().getDisplayName(),
                            delivery.getCarrierSla().getCarrier().getName());
                    carrier.put(delivery.getDeliveryNum(), carrierName.getValue(customerOrder.getLocale()));
                    final I18NModel carrierSlaName = new FailoverStringI18NModel(
                            delivery.getCarrierSla().getDisplayName(),
                            delivery.getCarrierSla().getName());
                    carrierSla.put(delivery.getDeliveryNum(), carrierSlaName.getValue(customerOrder.getLocale()));
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
}
