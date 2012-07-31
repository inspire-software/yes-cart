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
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.yes.cart.bulkimport.service.ImportDirectorService;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.service.domain.SystemService;
import org.yes.cart.util.ShopCodeContext;

import java.util.HashSet;
import java.util.Set;

/**
 * This stateful job perform import and products indexing, in case if import was fired.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 * @deprecated  ImportDirectorService now has capability to do async imports
 */
@Deprecated
public class ImportJobBeanImpl extends QuartzJobBean {

    private static final Logger LOG = LoggerFactory.getLogger(ShopCodeContext.getShopCode());

    private ImportDirectorService importDirector;

    private SystemService systemService;

    private ProductService productService;

    /**
     * {@inheritDoc}
     */
    protected void executeInternal(final JobExecutionContext jobExecutionContext) throws JobExecutionException {

        importDirector.doImport(false);
        productService.clearEmptyAttributes();
        productService.reindexProducts();

    }

    /**
     * IoC. Set product service to use.
     * @param productService product service to use
     */
    public void setProductService(final ProductService productService) {
        this.productService = productService;
    }

    /**
     * IoC. Set system service.
     * @param systemService system service.
     */
    public void setSystemService(final SystemService systemService) {
        this.systemService = systemService;
    }

    /**
     * IoC. Set the import director service to use.
     *
     * @param importDirector service to use.
     */
    public void setImportDirector(final ImportDirectorService importDirector) {
        this.importDirector = importDirector;
    }
}
