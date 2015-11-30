/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
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

package org.yes.cart.bulkexport.csv.impl;

import org.apache.commons.io.FileUtils;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.bulkcommon.service.ExportService;
import org.yes.cart.bulkexport.model.ExportDescriptor;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.async.model.JobContext;
import org.yes.cart.service.async.model.JobContextKeys;
import org.yes.cart.service.async.model.impl.JobContextImpl;
import org.yes.cart.stream.xml.XStreamProvider;

import java.io.File;
import java.io.FileInputStream;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

/**
 * User: denispavlov
 * Date: 26/11/2015
 * Time: 17:54
 */
public class CsvBulkExportServiceImplTest extends BaseCoreDBTestCase {

    ExportService bulkExportService = null;
    XStreamProvider<ExportDescriptor> xml = null;

    private final Mockery mockery = new JUnit4Mockery();

    @Before
    public void setUp()  {

        if (bulkExportService == null) {
            bulkExportService = createContext().getBean("csvBulkExportService", ExportService.class);
            xml = createContext().getBean("exportDescriptorXStreamProvider", XStreamProvider.class);
        }
        super.setUp();

    }


    @After
    public void tearDown() throws Exception {
        bulkExportService = null;
        xml = null;
        super.tearDown();

    }


    private JobContext createContext(final String descriptorPath, final JobStatusListener listener, final String fileToExport) throws Exception {

        final ExportDescriptor descriptor = xml.fromXML(new FileInputStream(new File(descriptorPath)));

        return new JobContextImpl(false, listener, new HashMap<String, Object>() {{
            put(JobContextKeys.EXPORT_DESCRIPTOR, descriptor);
            put(JobContextKeys.EXPORT_DESCRIPTOR_NAME, descriptorPath);
            put(JobContextKeys.EXPORT_FILE, fileToExport);
        }});
    }


    @Test
    public void testDoExport() throws Exception {
        try {

            final JobStatusListener listener = mockery.mock(JobStatusListener.class, "listener");

            mockery.checking(new Expectations() {{
                // ONLY allow messages during import
                allowing(listener).notifyPing();
                allowing(listener).notifyPing(with(any(String.class)));
                allowing(listener).notifyMessage(with(any(String.class)));
            }});

            Set<String> importedFilesSet = new HashSet<String>();

            ResultSet rs;


            rs = getConnection().getConnection().createStatement().executeQuery (
                    "select count(a.ATTRIBUTE_ID) as cnt from TATTRIBUTE a, TATTRIBUTEGROUP g where a.ATTRIBUTEGROUP_ID = g.ATTRIBUTEGROUP_ID and g.CODE = 'PRODUCT'");
            rs.next();
            long cntProductAttr = rs.getLong("cnt");
            rs.close();

            long dt = System.currentTimeMillis();
            String fileToExport = "target/attributenames-export-" + UUID.randomUUID().toString() + ".csv";
            bulkExportService.doExport(createContext("src/test/resources/export/attributenames.xml", listener, fileToExport));
            final long attrs = System.currentTimeMillis() - dt;
            System.out.println(cntProductAttr + " attributes  in " + attrs + "millis (~" + (attrs / cntProductAttr) + " per item)");

            String content = FileUtils.readFileToString(new File(fileToExport), "UTF-8");
            assertTrue(content.contains("\"PRODUCT\";\"COLOR\";\"Color\";;;;\"false\";\"false\";\"Failover is: false\";\"500\";\"java.lang.String\""));


            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TPRODUCT  ");
            rs.next();
            long cntProd = rs.getLong(1);
            rs.close();

            dt = System.currentTimeMillis();
            fileToExport = "target/productnames-export-" + UUID.randomUUID().toString() + ".csv";
            bulkExportService.doExport(createContext("src/test/resources/export/productnames.xml", listener, fileToExport));
            final long prods = System.currentTimeMillis() - dt;
            System.out.println(cntProd + " products in " + prods + "millis (~" + (prods / cntProd) + " per item)");


            content = FileUtils.readFileToString(new File(fileToExport), "UTF-8");
            assertTrue(content.contains("\"BENDER\";\"BENDER\";\"Bender Bending Rodriguez\";\"Бендер Згибатель Родригес\";\"Brand is: FutureRobots\""));
            assertTrue(content.contains("#IMAGE0#\t#sobot-picture.jpeg#"));
            assertTrue(content.contains(";\"sobot-picture.jpeg\";\"#101#\t#Big Boys Gadgets#\""));
            assertTrue(content.contains(";\"sobot-picture.jpeg\";;")); // AVC
            assertTrue(content.contains(";;\"12.11\";\"11.10\"")); // PRICES

            mockery.assertIsSatisfied();

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    private static class StringStartsWithMatcher extends TypeSafeMatcher<String> {
        private String prefix;

        public StringStartsWithMatcher(String prefix) {
            this.prefix = prefix;
        }

        public boolean matchesSafely(String s) {
            return s.startsWith(prefix);
        }

        public void describeTo(Description description) {
            description.appendText("a string starting with ").appendValue(prefix);
        }
    }

    private static class StringContainsMatcher extends TypeSafeMatcher<String> {
        private String text;

        public StringContainsMatcher(String text) {
            this.text = text;
        }

        public boolean matchesSafely(String s) {
            return s.contains(text);
        }

        public void describeTo(Description description) {
            description.appendText("a string containing text: ").appendValue(text);
        }
    }

    @Factory
    public static Matcher<String> aStringStartingWith(String prefix) {
        return new StringStartsWithMatcher(prefix);
    }

    @Factory
    public static Matcher<String> aStringContains(String text) {
        return new StringContainsMatcher(text);
    }

}