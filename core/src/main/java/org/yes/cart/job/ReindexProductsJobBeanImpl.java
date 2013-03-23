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

package org.yes.cart.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.util.ShopCodeContext;

import java.util.Date;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class ReindexProductsJobBeanImpl extends QuartzJobBean {

    private ProductService productService;

    /**
     * {@inheritDoc }
     */
    protected void executeInternal(final JobExecutionContext jobExecutionContext) throws JobExecutionException {
        final Logger log = ShopCodeContext.getLog(this);
        log.info("ReindexProductsJobDetailImpl start at {}", new Date());
/*
        setProductService(
                (ProductService)jobExecutionContext.getJobDetail().getJobDataMap().getByKey(ServiceSpringKeys.PRODUCT_SERVICE)
        );
*/
        productService.reindexProducts();
        log.info("ReindexProductsJobDetailImpl   end at {}", new Date());
    }

    /**
     * IoC Authowire.
     * Set {@link org.yes.cart.service.domain.ProductService} to use
     *
     * @param productService service to use.
     */
    public void setProductService(final ProductService productService) {
        this.productService = productService;
    }
}
