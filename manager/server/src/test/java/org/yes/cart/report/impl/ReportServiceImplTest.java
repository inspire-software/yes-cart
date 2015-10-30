package org.yes.cart.report.impl;

import org.apache.commons.io.IOUtils;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.yes.cart.report.ReportDescriptor;
import org.yes.cart.report.ReportGenerator;
import org.yes.cart.service.domain.ContentService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.service.theme.ThemeService;
import org.yes.cart.util.ShopCodeContext;

import javax.servlet.ServletContext;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
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

    private final Mockery context = new JUnit4Mockery();

    private final List<ReportDescriptor> allReportToTestCreation = new ArrayList<ReportDescriptor>();

    private static final String ROOT_DIR = "src/test/resources/";

    @Before
    public void setUp() {


        ReportDescriptor reportDescriptor = new ReportDescriptor();
        reportDescriptor.setXslfoBase("xslfo/payments");
        reportDescriptor.setReportId("payments");

        allReportToTestCreation.add(reportDescriptor);

        reportDescriptor = new ReportDescriptor();
        reportDescriptor.setXslfoBase("xslfo/delivery");
        reportDescriptor.setReportId("reportDelivery");

        allReportToTestCreation.add(reportDescriptor);

        reportDescriptor = new ReportDescriptor();
        reportDescriptor.setXslfoBase("xslfo/available-stock");
        reportDescriptor.setReportId("reportAvailableStock");

        allReportToTestCreation.add(reportDescriptor);
    }

    @Test
    public void testGetReportAvailableStock() throws Exception {

        final ThemeService themeService = context.mock(ThemeService.class, "themeService");
        final ShopService shopService = context.mock(ShopService.class, "shopService");
        final ContentService contentService = context.mock(ContentService.class, "contentService");
        final ServletContext servletContext = context.mock(ServletContext.class, "servletContext");

        context.checking(new Expectations() {{
            allowing(themeService).getReportsTemplateChainByShopId(null);
            will(returnValue(Arrays.asList("default/reports/")));
            one(servletContext).getResourceAsStream("default/reports/fop-userconfig.xml");
            will(returnValue(new FileInputStream(ROOT_DIR + "default/reports/fop-userconfig.xml")));
            one(servletContext).getResourceAsStream("default/reports/xslfo/available-stock.xslfo");
            will(returnValue(new FileInputStream(ROOT_DIR + "default/reports/xslfo/available-stock.xslfo")));
            one(servletContext).getResourceAsStream("default/reports/yes-logo.png");
            will(returnValue(new FileInputStream(ROOT_DIR + "default/reports/yes-logo.png")));
            one(servletContext).getResourceAsStream("default/reports/fonts/times.xml");
            will(returnValue(new FileInputStream(ROOT_DIR + "default/reports/fonts/times.xml")));
            one(servletContext).getResourceAsStream("default/reports/fonts/times.ttf");
            will(returnValue(new FileInputStream(ROOT_DIR + "default/reports/fonts/times.ttf")));
            one(servletContext).getResourceAsStream("default/reports/fonts/timesbd.xml");
            will(returnValue(new FileInputStream(ROOT_DIR + "default/reports/fonts/timesbd.xml")));
            one(servletContext).getResourceAsStream("default/reports/fonts/timesbd.ttf");
            will(returnValue(new FileInputStream(ROOT_DIR + "default/reports/fonts/timesbd.ttf")));
        }});


        final AdminReportGeneratorImpl reportGenerator = new AdminReportGeneratorImpl(themeService, shopService, contentService) {

            @Override
            protected Source convertToSource(final ReportDescriptor descriptor, final Map<String, Object> parameters, final Object data, final String lang) {
                try {
                    return new StreamSource(
                            new InputStreamReader(new FileInputStream(ROOT_DIR + "default/reports/xslfo/available-stock-report.xml"), "UTF-8")
                    );
                } catch (Exception e) {
                    fail(e.getMessage());
                    return null;
                }
            }

        };
        reportGenerator.setServletContext(servletContext);

        final ReportServiceImpl reportService = new ReportServiceImpl(allReportToTestCreation, Collections.EMPTY_MAP, reportGenerator) {
            /** {@inheritDoc} */
            List<Object> getQueryResult(final String lang, final String reportId, final Map<String, Object> currentSelection) {
                return Collections.singletonList(new Object());
            }
        };


        byte[] report = reportService.downloadReport(null, "reportAvailableStock", null);

        context.assertIsSatisfied();

        assertNotNull(report);
        assertTrue(report.length > 30720); // more than 30K means it is a valid pdf

        final Writer out = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("target/reportAvailableStock.pdf"), Charset.forName("UTF-8")));
        IOUtils.copy(new ByteArrayInputStream(report), out);

        out.close();

    }



    @Test
    public void testGetReportPayments() throws Exception {

        final ThemeService themeService = context.mock(ThemeService.class, "themeService");
        final ShopService shopService = context.mock(ShopService.class, "shopService");
        final ContentService contentService = context.mock(ContentService.class, "contentService");
        final ServletContext servletContext = context.mock(ServletContext.class, "servletContext");

        context.checking(new Expectations() {{
            allowing(themeService).getReportsTemplateChainByShopId(null);
            will(returnValue(Arrays.asList("default/reports/")));
            one(servletContext).getResourceAsStream("default/reports/fop-userconfig.xml");
            will(returnValue(new FileInputStream(ROOT_DIR + "default/reports/fop-userconfig.xml")));
            one(servletContext).getResourceAsStream("default/reports/xslfo/payments.xslfo");
            will(returnValue(new FileInputStream(ROOT_DIR + "default/reports/xslfo/payments.xslfo")));
            one(servletContext).getResourceAsStream("default/reports/yes-logo.png");
            will(returnValue(new FileInputStream(ROOT_DIR + "default/reports/yes-logo.png")));
            one(servletContext).getResourceAsStream("default/reports/fonts/times.xml");
            will(returnValue(new FileInputStream(ROOT_DIR + "default/reports/fonts/times.xml")));
            one(servletContext).getResourceAsStream("default/reports/fonts/times.ttf");
            will(returnValue(new FileInputStream(ROOT_DIR + "default/reports/fonts/times.ttf")));
            one(servletContext).getResourceAsStream("default/reports/fonts/timesbd.xml");
            will(returnValue(new FileInputStream(ROOT_DIR + "default/reports/fonts/timesbd.xml")));
            one(servletContext).getResourceAsStream("default/reports/fonts/timesbd.ttf");
            will(returnValue(new FileInputStream(ROOT_DIR + "default/reports/fonts/timesbd.ttf")));
        }});


        final AdminReportGeneratorImpl reportGenerator = new AdminReportGeneratorImpl(themeService, shopService, contentService) {

            @Override
            protected Source convertToSource(final ReportDescriptor descriptor, final Map<String, Object> parameters, final Object data, final String lang) {
                try {
                    return new StreamSource(
                            new InputStreamReader(new FileInputStream(ROOT_DIR + "default/reports/xslfo/payment-report.xml"), "UTF-8")
                    );
                } catch (Exception e) {
                    fail(e.getMessage());
                    return null;
                }
            }

        };
        reportGenerator.setServletContext(servletContext);

        final ReportServiceImpl reportService = new ReportServiceImpl(allReportToTestCreation, Collections.EMPTY_MAP, reportGenerator) {
            /** {@inheritDoc} */
            List<Object> getQueryResult(final String lang, final String reportId, final Map<String, Object> currentSelection) {
                return Collections.singletonList(new Object());
            }
        };


        byte[] report = reportService.downloadReport(null, "payments", null);

        assertNotNull(report);
        assertTrue(report.length > 30720); // more than 30K means it is a valid pdf

        final Writer out = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("target/payments.pdf"), Charset.forName("UTF-8")));
        IOUtils.copy(new ByteArrayInputStream(report), out);

        out.close();
    }


    @Test
    public void testGetReportDelivery() throws Exception {

        final ThemeService themeService = context.mock(ThemeService.class, "themeService");
        final ShopService shopService = context.mock(ShopService.class, "shopService");
        final ContentService contentService = context.mock(ContentService.class, "contentService");
        final ServletContext servletContext = context.mock(ServletContext.class, "servletContext");

        context.checking(new Expectations() {{
            allowing(themeService).getReportsTemplateChainByShopId(null);
            will(returnValue(Arrays.asList("default/reports/")));
            one(servletContext).getResourceAsStream("default/reports/fop-userconfig.xml");
            will(returnValue(new FileInputStream(ROOT_DIR + "default/reports/fop-userconfig.xml")));
            one(servletContext).getResourceAsStream("default/reports/xslfo/delivery.xslfo");
            will(returnValue(new FileInputStream(ROOT_DIR + "default/reports/xslfo/delivery.xslfo")));
            one(servletContext).getResourceAsStream("default/reports/yes-logo.png");
            will(returnValue(new FileInputStream(ROOT_DIR + "default/reports/yes-logo.png")));
            one(servletContext).getResourceAsStream("default/reports/fonts/times.xml");
            will(returnValue(new FileInputStream(ROOT_DIR + "default/reports/fonts/times.xml")));
            one(servletContext).getResourceAsStream("default/reports/fonts/times.ttf");
            will(returnValue(new FileInputStream(ROOT_DIR + "default/reports/fonts/times.ttf")));
            one(servletContext).getResourceAsStream("default/reports/fonts/timesbd.xml");
            will(returnValue(new FileInputStream(ROOT_DIR + "default/reports/fonts/timesbd.xml")));
            one(servletContext).getResourceAsStream("default/reports/fonts/timesbd.ttf");
            will(returnValue(new FileInputStream(ROOT_DIR + "default/reports/fonts/timesbd.ttf")));
        }});


        final AdminReportGeneratorImpl reportGenerator = new AdminReportGeneratorImpl(themeService, shopService, contentService) {

            @Override
            protected Source convertToSource(final ReportDescriptor descriptor, final Map<String, Object> parameters, final Object data, final String lang) {
                try {
                    return new StreamSource(
                            new InputStreamReader(new FileInputStream(ROOT_DIR + "default/reports/xslfo/delivery-report.xml"), "UTF-8")
                    );
                } catch (Exception e) {
                    fail(e.getMessage());
                    return null;
                }
            }

        };
        reportGenerator.setServletContext(servletContext);

        final ReportServiceImpl reportService = new ReportServiceImpl(allReportToTestCreation, Collections.EMPTY_MAP, reportGenerator) {
            /** {@inheritDoc} */
            List<Object> getQueryResult(final String lang, final String reportId, final Map<String, Object> currentSelection) {
                return Collections.singletonList(new Object());
            }
        };


        byte[] report = reportService.downloadReport(null, "reportDelivery", null);

        assertNotNull(report);
        assertTrue(report.length > 30720); // more than 30K means it is a valid pdf

        final Writer out = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("target/reportDelivery.pdf"), Charset.forName("UTF-8")));
        IOUtils.copy(new ByteArrayInputStream(report), out);

        out.close();
    }




}
