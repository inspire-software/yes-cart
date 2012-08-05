package org.yes.cart.report.impl;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.*;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

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

    private String fileName;

    @Before
    public void setUp() {


        ReportDescriptor reportDescriptor = new ReportDescriptor();
        reportDescriptor.setHsqlQuery("customerOrderPaymentService.getData.paymentReport");
        reportDescriptor.getLangXslfo().add(new ReportPair("en", ROOT_DIR + "xslfo/payments.xslfo"));
        reportDescriptor.setReportId("payments");

        allReportToTestCreation.add(reportDescriptor);

        reportDescriptor = new ReportDescriptor();
        reportDescriptor.setHsqlQuery("select s from ShopEntity s");
        reportDescriptor.getLangXslfo().add(new ReportPair("en", ROOT_DIR + "xslfo/shop.xslfo"));
        reportDescriptor.setReportId("shopTestReport");

        allReportToTestCreation.add(reportDescriptor);

        reportDescriptor = new ReportDescriptor();
        reportDescriptor.setHsqlQuery("select  o.sku.code,  o.sku.name, o.sku.barCode, o.reserved, o.quantity  from SkuWarehouseEntity o\n" +
                "                                          where o.warehouse.code = ?1\n" +
                "                                          order by o.sku.code");
        reportDescriptor.getLangXslfo().add(new ReportPair("en", ROOT_DIR + "xslfo/available-stock.xslfo"));
        reportDescriptor.setReportId("reportAvailableStock");

        allReportToTestCreation.add(reportDescriptor);
    }


    @Test
    public void testGetReportShop() throws Exception {

        ReportServiceImpl reportService = new ReportServiceImpl(null, allReportToTestCreation, null) {

            /** {@inheritDoc} */
            List<Object> getQueryResult(final String query, final Object... params) {
                return Collections.EMPTY_LIST;

            }

            /** {@inheritDoc} */
            File getXml(final List<Object> rez) {

                try {
                    System.out.println(new File(".").getAbsolutePath());
                    BufferedReader in = new BufferedReader(new FileReader(ROOT_DIR + "xslfo/shop-report.xml"));
                    File file = File.createTempFile("testyes", "cart");
                    BufferedWriter out = new BufferedWriter(new FileWriter(file));
                    String line;
                    while ((line = in.readLine()) != null) {
                        out.write(line);
                    }
                    out.close();
                    in.close();
                    return file;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                fail("Unable to create XML file");
                return null;

            }
        };

        assertTrue(reportService.createReport("en", "shopTestReport", "shopTestReport.pdf"));

        final File pdf = new File("shopTestReport.pdf");
        assertTrue(pdf.exists());
        assertTrue(pdf.length() > 30720); // more than 30K means it is a valid pdf
        assertTrue(pdf.delete());

    }


    @Test
    public void testGetReportAvailableStock() throws Exception {
        ReportServiceImpl reportService = new ReportServiceImpl(null, allReportToTestCreation, null) {
            /** {@inheritDoc} */
            List<Object> getQueryResult(final String query, final Object... params) {
                return Collections.EMPTY_LIST;
            }
            /** {@inheritDoc} */
            File getXml(final List<Object> rez) {
                try {
                    System.out.println(new File(".").getAbsolutePath());
                    BufferedReader in = new BufferedReader(new FileReader(ROOT_DIR + "xslfo/available-stock-report.xml"));
                    File file = File.createTempFile("testyes", "cart");
                    BufferedWriter out = new BufferedWriter(new FileWriter(file));
                    String line;
                    while ((line = in.readLine()) != null) {
                        out.write(line);
                    }
                    out.close();
                    in.close();
                    return file;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                fail("Unable to create XML file");
                return null;
            }
        };

        assertTrue(reportService.createReport("en", "reportAvailableStock", "reportAvailableStock.pdf"));
        final File pdf = new File("reportAvailableStock.pdf");
        assertTrue(pdf.exists());
        assertTrue(pdf.length() > 30720); // more than 30K means it is a valid pdf
        assertTrue(pdf.delete());
    }

    @Test
    public void testGetReportPayments() throws Exception {
        ReportServiceImpl reportService = new ReportServiceImpl(null, allReportToTestCreation, null) {
            /** {@inheritDoc} */
            List<Object> getQueryResult(final String query, final Object... params) {
                return Collections.EMPTY_LIST;
            }
            /** {@inheritDoc} */
            File getXml(final List<Object> rez) {
                try {
                    System.out.println(new File(".").getAbsolutePath());
                    BufferedReader in = new BufferedReader(new FileReader(ROOT_DIR + "xslfo/payment-report.xml"));
                    File file = File.createTempFile("testyes", "cart");
                    BufferedWriter out = new BufferedWriter(new FileWriter(file));
                    String line;
                    while ((line = in.readLine()) != null) {
                        out.write(line);
                    }
                    out.close();
                    in.close();
                    return file;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                fail("Unable to create XML file");
                return null;
            }
        };

        assertTrue(reportService.createReport("en", "payments", "payments.pdf"));
        final File pdf = new File("payments.pdf");
        assertTrue(pdf.exists());
        assertTrue(pdf.length() > 30720); // more than 30K means it is a valid pdf
        assertTrue(pdf.delete());
    }

}
