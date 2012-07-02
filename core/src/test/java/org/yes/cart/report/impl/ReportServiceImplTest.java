package org.yes.cart.report.impl;

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.dao.EntityFactory;
import org.yes.cart.dao.GenericDAO;

import static org.junit.Assert.assertTrue;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/1/12
 * Time: 11:59 AM
 */
public class ReportServiceImplTest   extends BaseCoreDBTestCase {

    private GenericDAO<Object, Long> genericDAO;



    @Before
    public void setUp() throws Exception {
        genericDAO = (GenericDAO) ctx().getBean("genericDao");

    }



    @Test
    public void testGetReport() throws Exception {

        ReportDescriptor reportDescriptor = new ReportDescriptor();

        reportDescriptor.setHsqlQuery("select s from ShopEntity s");

        reportDescriptor.setXslfo("src/test/resources/xslfo/shop.xslfo");

        ReportServiceImpl reportService = new ReportServiceImpl(genericDAO);

        assertTrue(reportService.getReport(reportDescriptor, "shop.pdf"));

    }

}
