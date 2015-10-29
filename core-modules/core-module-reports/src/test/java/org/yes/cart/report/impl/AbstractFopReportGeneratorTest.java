package org.yes.cart.report.impl;

import org.apache.xmlgraphics.io.Resource;
import org.apache.xmlgraphics.io.ResourceResolver;
import org.apache.xmlgraphics.io.TempResourceResolver;
import org.junit.Test;
import org.yes.cart.report.ReportDescriptor;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 23/10/2015
 * Time: 16:44
 */
public class AbstractFopReportGeneratorTest {

    private static final String BASE = "src/test/resources/default/reports/";

    @Test
    public void testGeneratePdfReport() throws Exception {

        final AbstractFopReportGenerator generator = new AbstractFopReportGenerator() {

            @Override
            protected InputStream getFopUserConfigInputStream(final ReportDescriptor descriptor,
                                                              final Map<String, Object> parameters,
                                                              final Object data,
                                                              final String lang) {
                assertEquals("v1", parameters.get("p1"));
                assertEquals("en", lang);
                try {
                    return new FileInputStream(new File(BASE + "fop-userconfig.xml"));
                } catch (FileNotFoundException e) {
                    fail("Unable to read " + BASE + "fop-userconfig.xml");
                    return null;
                }
            }

            @Override
            protected Source getXsltFile(final ReportDescriptor descriptor,
                                         final Map<String, Object> parameters,
                                         final Object data,
                                         final String lang) {
                assertEquals("v1", parameters.get("p1"));
                assertEquals("en", lang);
                return new StreamSource(new File(BASE + descriptor.getLangXslfo(null)));
            }

            @Override
            protected Source convertToSource(final ReportDescriptor descriptor,
                                             final Map<String, Object> parameters,
                                             final Object data,
                                             final String lang) {
                try {
                    return new StreamSource(
                            new InputStreamReader(new FileInputStream("src/test/resources/testdata/" + data), "UTF-8")
                    );
                } catch (Exception e) {
                    fail(e.getMessage());
                    return null;
                }
            }

            @Override
            protected TempResourceResolver getTempResourceResolver(final ReportDescriptor descriptor,
                                                                   final Map<String, Object> parameters,
                                                                   final Object data,
                                                                   final String lang) {
                return new TestTempResourceResolver();
            }

            @Override
            protected ResourceResolver getResourceResolver(final ReportDescriptor descriptor,
                                                           final Map<String, Object> parameters,
                                                           final Object data,
                                                           final String lang) {
                return new TestResourceResolver();
            }
        };

        final ReportDescriptor availableStock = new ReportDescriptor();
        availableStock.setReportId("available-stock");
        availableStock.setXslfoBase("stock-report/available-stock");
        availableStock.setVisible(true);

        final ByteArrayOutputStream baosAvailableStock = new ByteArrayOutputStream();

        generator.generateReport(availableStock, new HashMap<String, Object>() {{
            put("p1", "v1");
        }}, "available-stock-report.xml", "en", baosAvailableStock);

        assertTrue(baosAvailableStock.toByteArray().length > 0);

    }




    private static class TestResourceResolver implements ResourceResolver {
        public Resource getResource(URI uri) throws IOException {
            return new Resource(new FileInputStream(BASE + uri.toString()));
        }

        public OutputStream getOutputStream(URI uri) throws IOException {
            throw new IOException("Not supported");
        }
    }

    private static class TestTempResourceResolver implements TempResourceResolver {

        public OutputStream getOutputStream(String id) throws IOException {
            throw new IOException("Not supported");
        }

        public Resource getResource(final String id) throws IOException {
            throw new IOException("Not supported");
        }
    }




}