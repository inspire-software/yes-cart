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

package org.yes.cart.bulkjob.product;

import org.slf4j.Logger;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.util.ShopCodeContext;

import java.util.Date;
import java.util.List;

/**
 * Since we need to remove discontinued items from index we need a cron job to
 * select products whose availableTo date is before now.
 *
 * For these products we force reindexing that removed them from the index.
 *
 * User: denispavlov
 * Date: 13/11/2013
 * Time: 15:30
 */
public class ProductsPassedAvailbilityDateIndexProcessorImpl implements Runnable {

    private final ProductService productService;

    public ProductsPassedAvailbilityDateIndexProcessorImpl(final ProductService productService) {
        this.productService = productService;
    }


    /** {@inheritDoc} */
    @Override
    public void run() {

        final Logger log = ShopCodeContext.getLog(this);

        final List<Object> discontinued = productService.getGenericDao().findQueryObjectByNamedQuery("DISCONTINUED.PRODUCTS.AFTER.DATE", now());

        for (final Object pk : discontinued) {
            log.info("Reindexing discontinued product {}", pk);
            productService.reindexProduct((Long) pk);
        }

    }

    protected Date now() {
        return new Date();
    }
}
