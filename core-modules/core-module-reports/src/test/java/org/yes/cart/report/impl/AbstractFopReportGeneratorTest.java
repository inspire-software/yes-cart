/*
 * Copyright 2009 Inspire-Software.com
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

package org.yes.cart.report.impl;

import org.apache.xmlgraphics.io.Resource;
import org.apache.xmlgraphics.io.ResourceResolver;
import org.apache.xmlgraphics.io.TempResourceResolver;
import org.junit.Test;
import org.yes.cart.report.ReportDescriptor;
import org.yes.cart.report.ReportDescriptorPDF;

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
                return new StreamSource(new File(BASE + ((ReportDescriptorPDF) descriptor).getLangXslfo(null)));
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

        final ReportDescriptorPDF availableStock = new ReportDescriptorPDF();
        availableStock.setReportId("available-stock");
        availableStock.setXslfoBase("stock-report/available-stock");
        availableStock.setVisible(true);

        final ByteArrayOutputStream baosAvailableStock = new ByteArrayOutputStream();

        generator.generateReport(availableStock, new HashMap<String, Object>() {{
            put("p1", "v1");
        }}, "available-stock-report.xml", "en", baosAvailableStock);

        assertTrue(baosAvailableStock.toByteArray().length > 0);

        try(FileOutputStream fos = new FileOutputStream("target/test-pdf-report.pdf")) {

            fos.write(baosAvailableStock.toByteArray());

        }

    }




    private static class TestResourceResolver implements ResourceResolver {
        @Override
        public Resource getResource(URI uri) throws IOException {
            return new Resource(new FileInputStream(BASE + uri.toString()));
        }

        @Override
        public OutputStream getOutputStream(URI uri) throws IOException {
            throw new IOException("Not supported");
        }
    }

    private static class TestTempResourceResolver implements TempResourceResolver {

        @Override
        public OutputStream getOutputStream(String id) throws IOException {
            throw new IOException("Not supported");
        }

        @Override
        public Resource getResource(final String id) throws IOException {
            throw new IOException("Not supported");
        }
    }




}