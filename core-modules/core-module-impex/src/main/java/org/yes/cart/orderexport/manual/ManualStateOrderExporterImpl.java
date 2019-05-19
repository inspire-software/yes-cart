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

package org.yes.cart.orderexport.manual;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.config.ActiveConfiguration;
import org.yes.cart.config.ActiveConfigurationDetector;
import org.yes.cart.config.Configuration;
import org.yes.cart.config.ConfigurationContext;
import org.yes.cart.config.impl.ActiveConfigurationImpl;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.CustomerOrderDelivery;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.orderexport.OrderAutoExportProcessor;
import org.yes.cart.orderexport.OrderExporter;
import org.yes.cart.orderexport.impl.ExportResultImpl;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.utils.DateUtils;
import org.yes.cart.utils.log.Markers;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;

/**
 * This order exporter allows to transition the order to specific export eligibility and set
 * {@link CustomerOrder#isBlockExport()} to true if necessary to enable manual export flow for a specific
 * export eligibility.
 *
 * Uses SHOP[ORDER_EXPORTER_MANUAL_STATE_PROXY] attribute, which is of type Property and should contain
 * mapping of [Fulfilment Center Code].[Export eligibility]=NEXT|BLOCK or NEXT|UNBLOCK
 *
 * Example configuration for blocking export eligibility to EMAILNOTIFY:
 * INITPAID=EMAILNOTIFY|BLOCK
 *
 * Example configuration for supplier specific blocking change to EMAILNOTIFY, EMAILNOTIFYSEC (on delivery):
 * DELIVERY=XML|UNBLOCK
 *
 * User: denispavlov
 * Date: 20/04/2018
 * Time: 07:52
 */
public class ManualStateOrderExporterImpl implements OrderExporter, Configuration, ActiveConfigurationDetector {


    private static final Logger LOG = LoggerFactory.getLogger(ManualStateOrderExporterImpl.class);

    private String exporterId = "ManualStateOrderExporterImpl";

    private final ShopService shopService;

    private int priority = 10;

    private ConfigurationContext cfgContext;

    public ManualStateOrderExporterImpl(final ShopService shopService) {
        this.shopService = shopService;
    }

    /** {@inheritDoc} */
    @Override
    public boolean supports(final CustomerOrder customerOrder, final Collection<CustomerOrderDelivery> customerOrderDeliveries) {
        if (!customerOrder.isBlockExport() && CollectionUtils.isNotEmpty(customerOrderDeliveries)) {
            final Properties map = getTransitionMap(customerOrder);
            if (map.containsKey(customerOrder.getEligibleForExport())) {
                return true;
            }
        }
        return false;
    }


    private Properties getTransitionMap(final CustomerOrder customerOrder) {

        return getTransitionMap(customerOrder.getShop(), true);

    }

    private Properties getTransitionMap(final Shop orderShop, final boolean allowMaster) {

        final Properties props = new Properties();
        if (allowMaster) {
            loadProperties(props, orderShop.getMaster());
        }
        loadProperties(props, orderShop);
        return props;

    }

    private void loadProperties(final Properties props, final Shop shop) {
        if (shop != null) {
            final String config = shop.getAttributeValueByCode(AttributeNamesKeys.Shop.ORDER_EXPORTER_MANUAL_STATE_PROXY);
            if (StringUtils.isNotBlank(config)) {
                try {
                    final Properties cofigProps = new Properties();
                    cofigProps.load(new StringReader(config));
                    props.putAll(cofigProps);
                } catch (IOException e) {
                    LOG.warn(Markers.alert(), "Unable to load properties for manual state order exporter for {}", shop.getCode());
                    LOG.error(e.getMessage(), e);
                }
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public ExportResult export(final CustomerOrder customerOrder, final Collection<CustomerOrderDelivery> customerOrderDeliveries) {

        final Properties map = getTransitionMap(customerOrder);

        final Pair<String, Boolean> nextEligibleAndBlock = determineConfigurationByKey(map, customerOrder.getEligibleForExport());

        final Set<Long> exported = new HashSet<>();
        final Map<String, String> audit = new HashMap<>();
        final String timestamp = DateUtils.formatSDT();

        for (final CustomerOrderDelivery delivery : customerOrderDeliveries) {
            if (!delivery.isBlockExport() && delivery.getEligibleForExport() != null && !delivery.getDetail().isEmpty()) {

                final StringBuilder auditInfo = new StringBuilder();

                auditInfo
                        .append(nextEligibleAndBlock.getFirst() != null ? nextEligibleAndBlock.getFirst() : "[none]")
                        .append(',')
                        .append(nextEligibleAndBlock.getSecond() ? "BLOCK" : "UNBLOCK")
                        .append(' ').append(customerOrder.getOrderStatus());

                exported.add(delivery.getCustomerOrderDeliveryId());
                delivery.setEligibleForExport(nextEligibleAndBlock.getFirst());
                delivery.setBlockExport(nextEligibleAndBlock.getSecond());
                customerOrder.setEligibleForExport(nextEligibleAndBlock.getFirst());
                customerOrder.setBlockExport(nextEligibleAndBlock.getSecond());
                auditInfo.append(' ').append(delivery.getDeliveryNum());

                audit.put(getExporterId() + ": " + customerOrder.getEligibleForExport() + ": " + timestamp, auditInfo.toString());

            }
        }

        return new ExportResultImpl(exported, audit, nextEligibleAndBlock.getFirst(), nextEligibleAndBlock.getFirst());

    }

    private Pair<String, Boolean> determineConfigurationByKey(final Properties map, final String key) {
        final String transitionAndFlag = map.getProperty(key);
        final String[] config = StringUtils.split(transitionAndFlag, '|');
        final Pair<String, Boolean> nextEligibleAndBlock;
        if (config != null) {
            if (config.length > 1) {
                nextEligibleAndBlock = new Pair<>(config[0].trim(), "BLOCK".equalsIgnoreCase(config[1].trim()));
            } else {
                nextEligibleAndBlock = new Pair<>(config[0].trim(), true); // Block by default
            }
        } else {
            nextEligibleAndBlock = new Pair<>(null, false);
        }
        return nextEligibleAndBlock;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<ActiveConfiguration> getActive() {

        final List<ActiveConfiguration> active = new ArrayList<>();

        this.shopService.findAllIterator(shop -> {

            // check specific settings, so don't allow master
            final Properties map = getTransitionMap(shop, false);

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
