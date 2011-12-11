package org.yes.cart.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.yes.cart.bulkimport.service.ImportDirectorService;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.service.domain.SystemService;

import java.util.HashSet;
import java.util.Set;

/**
 * This stateful job perform import and products indexing, in case if import was fired.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class ImportJobBeanImpl extends QuartzJobBean {

    private static final Logger LOG = LoggerFactory.getLogger(ImportJobBeanImpl.class);

    private ImportDirectorService importDirector;

    private SystemService systemService;

    private ProductService productService;

    /**
     * {@inheritDoc}
     */
    protected void executeInternal(final JobExecutionContext jobExecutionContext) throws JobExecutionException {

        importDirector.setPathToImportDescriptors(systemService.getImportDescritorsDirectory());
        importDirector.setPathToImportImagesFolder(systemService.getImportDirectory());
        importDirector.setPathToArchiveFolder(systemService.getImportArchiveDirectory());
        importDirector.doImport();
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
