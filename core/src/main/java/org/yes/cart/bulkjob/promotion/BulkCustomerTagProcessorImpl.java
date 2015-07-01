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
import org.yes.cart.dao.ResultsIterator;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.CustomerShop;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.promotion.PromotionContext;
import org.yes.cart.promotion.PromotionContextFactory;
import org.yes.cart.service.domain.CustomerService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.util.ShopCodeContext;

/**
 * Runnable that scans all customers with respect to all Shops they are
 * assigned to and applies promotion tagging
 *
 * User: denispavlov
 * Date: 07/11/2013
 * Time: 09:18
 */
public class BulkCustomerTagProcessorImpl implements Runnable {

    private final ShopService shopService;
    private final CustomerService customerService;
    private final PromotionContextFactory promotionContextFactory;

    private int batchSize = 20;

    public BulkCustomerTagProcessorImpl(final ShopService shopService,
                                        final CustomerService customerService,
                                        final PromotionContextFactory promotionContextFactory) {
        this.shopService = shopService;
        this.customerService = customerService;
        this.promotionContextFactory = promotionContextFactory;
    }

    /** {@inheritDoc} */
    @Override
    public void run() {

        final Logger log = ShopCodeContext.getLog(this);

        final long start = System.currentTimeMillis();

        log.info("Processing tagging for customer");

        final ResultsIterator<Customer> customerIterator = customerService.getGenericDao().findAllIterator();

        try {

            int count = 0;
            int updated = 0;

            while (customerIterator.hasNext())  {

                final Customer customer = customerIterator.next();
                final String tagsBefore = customer.getTag();

                log.debug("Processing tagging for customer {} with tags {}", customer.getEmail(), tagsBefore);

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
                    log.debug("Tags changed for customer {} with tags {} to {}",
                            new Object[] { customer.getEmail(), tagsBefore, customer.getTag() });
                    updated++;
                } else {
                    log.debug("No tag change for customer {} with tags {}", customer.getEmail(), tagsBefore);
                }

                if (++count % this.batchSize == 0 ) {
                    //flush a batch of updates and release memory:
                    customerService.getGenericDao().flush();
                    customerService.getGenericDao().clear();
                }

            }

            log.info("Processed tagging for {} customers, updated {}", count, updated);

        } catch (Exception exp){
            log.error("Processing tagging for customer exception " + exp.getMessage(), exp);
            throw new RuntimeException(exp);  // exception will make the transaction rollback anyway
        } finally {
            try {
                customerIterator.close();
            } catch (Exception exp) {
                log.error("Processing tagging for customer exception, error closing iterator: " + exp.getMessage(), exp);
            }
        }

        final long finish = System.currentTimeMillis();

        final long ms = (finish - start);

        log.info("Processing tagging for customer ... completed in {}s", (ms > 0 ? ms / 1000 : 0));

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
