package org.yes.cart.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.yes.cart.service.domain.ProductService;

import java.text.MessageFormat;
import java.util.Date;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class ReindexProductsJobBeanImpl extends QuartzJobBean {

    private static final Logger LOG = LoggerFactory.getLogger(ReindexProductsJobBeanImpl.class);

    private ProductService productService;

    /**
     * {@inheritDoc }
     */
    protected void executeInternal(final JobExecutionContext jobExecutionContext) throws JobExecutionException {
        LOG.info(MessageFormat.format("ReindexProductsJobDetailImpl start at {0}", new Date()));
/*
        setProductService(
                (ProductService)jobExecutionContext.getJobDetail().getJobDataMap().getByKey(ServiceSpringKeys.PRODUCT_SERVICE)
        );
*/
        productService.reindexProducts();
        LOG.info(MessageFormat.format("ReindexProductsJobDetailImpl   end at {0}", new Date()));
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
