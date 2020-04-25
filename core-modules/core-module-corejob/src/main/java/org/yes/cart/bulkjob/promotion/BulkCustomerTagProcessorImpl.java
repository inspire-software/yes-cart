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

package org.yes.cart.bulkjob.promotion;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.CustomerShop;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.promotion.PromotionContext;
import org.yes.cart.promotion.PromotionContextFactory;
import org.yes.cart.service.async.JobStatusAware;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.async.impl.JobStatusListenerLoggerWrapperImpl;
import org.yes.cart.service.async.model.JobStatus;
import org.yes.cart.service.domain.CustomerService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.service.domain.SystemService;
import org.yes.cart.utils.log.Markers;

import java.util.ArrayList;
import java.util.List;

/**
 * Runnable that scans all customers with respect to all Shops they are
 * assigned to and applies promotion tagging
 *
 * User: denispavlov
 * Date: 07/11/2013
 * Time: 09:18
 */
public class BulkCustomerTagProcessorImpl implements BulkCustomerTagProcessorInternal, JobStatusAware {

    private static final Logger LOG = LoggerFactory.getLogger(BulkCustomerTagProcessorImpl.class);

    private static final String PROCESS_COUNTER = "Customers";
    private static final String UPDATED_COUNTER = "Updated";

    private final ShopService shopService;
    private final CustomerService customerService;
    private final SystemService systemService;
    private final PromotionContextFactory promotionContextFactory;

    private int batchSize = 500;

    private final JobStatusListener listener = new JobStatusListenerLoggerWrapperImpl(LOG, "Customer tagging", true);

    public BulkCustomerTagProcessorImpl(final ShopService shopService,
                                        final CustomerService customerService,
                                        final SystemService systemService,
                                        final PromotionContextFactory promotionContextFactory) {
        this.shopService = shopService;
        this.customerService = customerService;
        this.systemService = systemService;
        this.promotionContextFactory = promotionContextFactory;
    }

    /** {@inheritDoc} */
    @Override
    public JobStatus getStatus(final String token) {
        return listener.getLatestStatus();
    }

    /** {@inheritDoc} */
    @Override
    public void run() {

        try {

            final int batchSize = determineBatchSize();
            final List<Pair<Customer, String>> batch = new ArrayList<>();

            customerService.findAllIterator(customer -> {

                final String tagsBefore = customer.getTag();

                LOG.debug("Processing tagging for customer {} with tags {}", customer.getEmail(), tagsBefore);

                if (!customer.isGuest()) { // only registered accounts

                    for (final CustomerShop customerShop : customer.getShops()) {

                        final Shop shop = shopService.getById(customerShop.getShop().getShopId());
                        for (final String currency : shop.getSupportedCurrenciesAsList()) {

                            final PromotionContext promotionContext =
                                    promotionContextFactory.getInstance(shop.getCode(), currency);

                            promotionContext.applyCustomerPromo(customer, null);

                        }

                    }

                    if (!StringUtils.equals(tagsBefore, customer.getTag())) {

                        if (batch.size() + 1 >= batchSize) {
                            // Save batch
                            self().updateCustomers(batch);
                            listener.count(UPDATED_COUNTER, batch.size());
                            batch.clear();
                            // release memory from HS
                            customerService.getGenericDao().clear();
                        }
                        batch.add(new Pair<>(customer, customer.getTag()));
                        
                    }

                    listener.count(PROCESS_COUNTER);

                    listener.notifyPing("Processed {}, updated: {}", listener.getCount(PROCESS_COUNTER), listener.getCount(UPDATED_COUNTER));

                }

                return true; // all
            });

            if (batch.size() > 0) {
                // Save last batch
                self().updateCustomers(batch);
                listener.count(UPDATED_COUNTER, batch.size());
            }

        } catch (Exception exp){
            LOG.error(Markers.alert(), "Processing tagging for customer error: " + exp.getMessage(), exp);
            throw new RuntimeException(exp);  // exception will make the transaction rollback anyway
        }

        listener.notifyCompleted();
        listener.reset();

    }

    @Override
    public void updateCustomers(final List<Pair<Customer, String>> customers) {
        for (final Pair<Customer, String> customerAndTag : customers) {
            final Customer customer = customerService.findById(customerAndTag.getFirst().getCustomerId());
            final String tagsBefore = customer.getTag();
            customer.setTag(customerAndTag.getSecond());
            customerService.update(customer);
            LOG.debug("Tags changed for customer {} with tags {} to {}", customer.getEmail(), tagsBefore, customer.getTag());
        }
    }

    private int determineBatchSize() {

        final String av = systemService.getAttributeValue(AttributeNamesKeys.System.JOB_CUSTOMER_TAG_BATCH_SIZE);

        if (av != null && StringUtils.isNotBlank(av)) {
            int batch = NumberUtils.toInt(av);
            if (batch > 0) {
                return batch;
            }
        }
        return this.batchSize;

    }

    /**
     * Batch size for remote index update.
     *
     * @param batchSize batch size
     */
    public void setBatchSize(final int batchSize) {
        this.batchSize = batchSize;
    }



    private BulkCustomerTagProcessorInternal self;

    private BulkCustomerTagProcessorInternal self() {
        if (self == null) {
            self = getSelf();
        }
        return self;
    }

    public BulkCustomerTagProcessorInternal getSelf() {
        return null;
    }


}
