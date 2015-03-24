package org.yes.cart.report.impl;

import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/1/12
 * Time: 11:59 AM
 */
public class ReportServiceImplTest  {

    private final List<ReportDescriptor> allReportToTestCreation = new ArrayList<ReportDescriptor>();

    private static final String ROOT_DIR = "src/test/resources/";

    @Before
    public void setUp() {


        ReportDescriptor reportDescriptor = new ReportDescriptor();
        reportDescriptor.setXslfoBase(ROOT_DIR + "xslfo/payments");
        reportDescriptor.setReportId("payments");

        allReportToTestCreation.add(reportDescriptor);

        reportDescriptor = new ReportDescriptor();
        reportDescriptor.setXslfoBase(ROOT_DIR + "xslfo/delivery");
        reportDescriptor.setReportId("reportDelivery");

        allReportToTestCreation.add(reportDescriptor);

        reportDescriptor = new ReportDescriptor();
        reportDescriptor.setXslfoBase(ROOT_DIR + "xslfo/available-stock");
        reportDescriptor.setReportId("reportAvailableStock");

        allReportToTestCreation.add(reportDescriptor);
    }

    @Test
    public void testGetReportAvailableStock() throws Exception {
        ReportServiceImpl reportService = new ReportServiceImpl(allReportToTestCreation, null, null) {
            /** {@inheritDoc} */
            List<Object> getQueryResult(final String lang, final String reportId, final Map<String, Object> currentSelection) {
                return Collections.EMPTY_LIST;
            }
            /** {@inheritDoc} */
            byte[] getXml(final List<Object> rez) {

                try {
                    return new Scanner(new File(ROOT_DIR + "xslfo/available-stock-report.xml")).useDelimiter("\\Z").next().getBytes(Charset.forName("UTF-8"));
                } catch (FileNotFoundException e) {
                    fail(e.getMessage());
                    return null;
                }

            }
        };


        byte[] report = reportService.downloadReport(null, "reportAvailableStock", null);

        assertNotNull(report);
        assertTrue(report.length > 30720); // more than 30K means it is a valid pdf

    }



    @Test
    public void testGetReportPayments() throws Exception {
        ReportServiceImpl reportService = new ReportServiceImpl(allReportToTestCreation, null, null) {
            /** {@inheritDoc} */
            List<Object> getQueryResult(final String lang, final String reportId, final Map<String, Object> currentSelection) {
                return Collections.EMPTY_LIST;
            }
            /** {@inheritDoc} */
            byte[] getXml(final List<Object> rez) {

                try {
                    return new Scanner(new File(ROOT_DIR + "xslfo/payment-report.xml")).useDelimiter("\\Z").next().getBytes(Charset.forName("UTF-8"));
                } catch (FileNotFoundException e) {
                    fail(e.getMessage());
                    return null;
                }

            }
        };


        byte[] report = reportService.downloadReport(null, "payments", null);

        assertNotNull(report);
        assertTrue(report.length > 30720); // more than 30K means it is a valid pdf

    }


    @Test
    public void testGetReportDelivery() throws Exception {
        ReportServiceImpl reportService = new ReportServiceImpl(allReportToTestCreation, null, null) {
            /** {@inheritDoc} */
            List<Object> getQueryResult(final String lang, final String reportId, final Map<String, Object> currentSelection) {
                return Collections.EMPTY_LIST;
            }
            /** {@inheritDoc} */
            byte[] getXml(final List<Object> rez) {

                try {
                    return new Scanner(new File(ROOT_DIR + "xslfo/delivery-report.xml")).useDelimiter("\\Z").next().getBytes(Charset.forName("UTF-8"));
                } catch (FileNotFoundException e) {
                    fail(e.getMessage());
                    return null;
                }

            }
        };


        byte[] report = reportService.downloadReport(null, "reportDelivery", null);

        assertNotNull(report);
        assertTrue(report.length > 30720); // more than 30K means it is a valid pdf

    }




}
