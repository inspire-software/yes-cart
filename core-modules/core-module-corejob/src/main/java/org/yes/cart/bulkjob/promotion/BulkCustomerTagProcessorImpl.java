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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.dao.ResultsIterator;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.CustomerShop;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.promotion.PromotionContext;
import org.yes.cart.promotion.PromotionContextFactory;
import org.yes.cart.service.async.JobStatusAware;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.async.impl.JobStatusListenerLoggerWrapperImpl;
import org.yes.cart.service.async.model.JobStatus;
import org.yes.cart.service.domain.CustomerService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.util.log.Markers;

/**
 * Runnable that scans all customers with respect to all Shops they are
 * assigned to and applies promotion tagging
 *
 * User: denispavlov
 * Date: 07/11/2013
 * Time: 09:18
 */
public class BulkCustomerTagProcessorImpl implements Runnable, JobStatusAware {

    private static final Logger LOG = LoggerFactory.getLogger(BulkCustomerTagProcessorImpl.class);

    private final ShopService shopService;
    private final CustomerService customerService;
    private final PromotionContextFactory promotionContextFactory;

    private int batchSize = 500;

    private final JobStatusListener listener = new JobStatusListenerLoggerWrapperImpl(LOG);

    public BulkCustomerTagProcessorImpl(final ShopService shopService,
                                        final CustomerService customerService,
                                        final PromotionContextFactory promotionContextFactory) {
        this.shopService = shopService;
        this.customerService = customerService;
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

        LOG.info("Processing tagging for customer");

        try {

            int count[] = new int[] { 0 };
            int updated[] = new int[] { 0 };

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
                        customerService.update(customer);
                        LOG.debug("Tags changed for customer {} with tags {} to {}",
                                customer.getEmail(), tagsBefore, customer.getTag());
                        updated[0]++;
                    } else {
                        LOG.debug("No tag change for customer {} with tags {}", customer.getEmail(), tagsBefore);
                    }

                    if (++count[0] % this.batchSize == 0) {
                        //flush a batch of updates and release memory:
                        customerService.getGenericDao().flush();
                        customerService.getGenericDao().clear();
                    }
                }

                listener.notifyPing("Processed " + count[0] + ", updated: " + updated[0]);

                return true; // all
            });

            LOG.info("Processed tagging for {} customers, updated {}", count, updated);

        } catch (Exception exp){
            LOG.error(Markers.alert(), "Processing tagging for customer error: " + exp.getMessage(), exp);
            throw new RuntimeException(exp);  // exception will make the transaction rollback anyway
        }

        LOG.info("Processing tagging for customer ... completed");

    }

    /**
     * Batch size for remote index update.
     *
     * @param batchSize batch size
     */
    public void setBatchSize(final int batchSize) {
        this.batchSize = batchSize;
    }


}
