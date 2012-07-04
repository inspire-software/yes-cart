package org.yes.cart.report.impl;

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.dao.EntityFactory;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.misc.Pair;

import java.util.Collections;

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

        final ReportDescriptor reportDescriptor = new ReportDescriptor();
        reportDescriptor.setHsqlQuery("select s from ShopEntity s");
        reportDescriptor.getLangXslfo().add(new Pair<String, String>("en", "src/test/resources/xslfo/shop.xslfo"));
        reportDescriptor.setReportId("testReport");

        ReportServiceImpl reportService = new ReportServiceImpl(genericDAO, Collections.singletonList(reportDescriptor), null);

        assertTrue(reportService.getReport("en", "testReport", "shop.pdf"));

    }

}
