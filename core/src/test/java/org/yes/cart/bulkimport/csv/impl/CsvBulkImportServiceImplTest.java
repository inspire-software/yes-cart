/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
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

package org.yes.cart.bulkimport.csv.impl;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.bulkimport.csv.CsvFileReader;
import org.yes.cart.bulkimport.service.BulkImportService;
import org.yes.cart.bulkimport.service.BulkImportStatusListener;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 12/11/11
 * Time: 10:20 AM
 */
public class CsvBulkImportServiceImplTest extends BaseCoreDBTestCase {


    BulkImportService bulkImportService = null;

    private final Mockery mockery = new JUnit4Mockery();


    @Before
    public void setUp() throws Exception {

        if (bulkImportService == null) {
            bulkImportService = (BulkImportService) createContext().getBean("bulkImportServiceImpl");
        }


    }

    @After
    public void tearDown() throws Exception {
        bulkImportService = null;
        super.tearDown();

    }


    private BulkImportService getBulkImportService(final String pathToDescriptor) {

        bulkImportService.setPathToImportDescriptor(pathToDescriptor);
        return bulkImportService;

    }


    @Test
    public void csvFileReaderTest() throws Exception {

        ArrayList<String> allowedValue =  new ArrayList<String>();
        allowedValue.add("HP ProBook 6560b");
        allowedValue.add("HP EliteBook Ноутбук HP EliteBook 2560p");
        allowedValue.add("HP Pavilion dv7-6b80eb");
        allowedValue.add("Toshiba Qosmio X770-13G");
        CsvFileReader csvFileReader = new CsvFileReaderImpl();

        csvFileReader.open(
                "src/test/resources/import/productentity.csv",
                ';',
                '"',
                "UTF-8",
                true);

        String[] line;
        while ((line = csvFileReader.readLine()) != null) {
            //doImport(errorReport, line, cvsImportDescriptor, pkColumn, null);
            allowedValue.remove(line[0]) ;
        }

        assertTrue(allowedValue.isEmpty());


    }

    @Test
    public void testDoProductImportWithSimpleSlaveFiled() throws Exception {
        try {

            getConnection().getConnection().createStatement().execute("CREATE index asdsda on TPRODUCTATTRVALUE(code)") ;

            final BulkImportStatusListener listener = mockery.mock(BulkImportStatusListener.class, "listener");

            mockery.checking(new Expectations() {{
                allowing(listener).notifyMessage(with(any(String.class)));
                one(listener).notifyError(with(aStringStartingWith("unexpected error src/test/resources/import/availability.xml (No such file or directory)")));
                // data truncation in groups
                one(listener).notifyError(with(aStringContains("right truncation")));
                one(listener).notifyError(with(aStringStartingWith("Tuple: CsvImportTupleImpl{sid=productypeattributeviewgroup.csv:31")));
                one(listener).notifyError(with(aStringContains("right truncation")));
                one(listener).notifyError(with(aStringStartingWith("Tuple: CsvImportTupleImpl{sid=productypeattributeviewgroup.csv:110")));
                // data truncation in products
                one(listener).notifyError(with(aStringContains("right truncation")));
                one(listener).notifyError(with(aStringStartingWith("Tuple: CsvImportTupleImpl{sid=productentity.csv:4")));
                one(listener).notifyError(with(aStringContains("right truncation")));
                one(listener).notifyError(with(aStringStartingWith("Tuple: CsvImportTupleImpl{sid=productentity.csv:14")));
                one(listener).notifyError(with(aStringContains("right truncation")));
                one(listener).notifyError(with(aStringStartingWith("Tuple: CsvImportTupleImpl{sid=productentity.csv:101")));
                // inventory import crashes due to truncation in product
                one(listener).notifyError(with(aStringContains("additional info Property name sku type")));
                one(listener).notifyError(with(aStringStartingWith("Tuple: CsvImportTupleImpl{sid=productentity.csv:4")));
                one(listener).notifyError(with(aStringContains("additional info Property name sku type")));
                one(listener).notifyError(with(aStringStartingWith("Tuple: CsvImportTupleImpl{sid=productentity.csv:14")));
                one(listener).notifyError(with(aStringContains("additional info Property name sku type")));
                one(listener).notifyError(with(aStringStartingWith("Tuple: CsvImportTupleImpl{sid=productentity.csv:101")));
                one(listener).notifyError(with(aStringContains("fillEntityForeignKeys")));
                one(listener).notifyError(with(aStringStartingWith("Tuple: CsvImportTupleImpl{sid=productentity.csv:4")));
                one(listener).notifyError(with(aStringContains("fillEntityForeignKeys")));
                one(listener).notifyError(with(aStringStartingWith("Tuple: CsvImportTupleImpl{sid=productentity.csv:14")));
                one(listener).notifyError(with(aStringContains("fillEntityForeignKeys")));
                one(listener).notifyError(with(aStringStartingWith("Tuple: CsvImportTupleImpl{sid=productentity.csv:101")));
            }});

            Set<String> importedFilesSet = new HashSet<String>();

            BulkImportService bulkImportService = null;

            long dt = System.currentTimeMillis();
            bulkImportService = getBulkImportService("src/test/resources/import/brandimport.xml");
            bulkImportService.doImport(listener, importedFilesSet, null, "");
            System.out.println("brands in " + (System.currentTimeMillis() - dt) + "millis");


            dt = System.currentTimeMillis();
            bulkImportService = getBulkImportService("src/test/resources/import/attribute.xml");
            bulkImportService.doImport(listener, importedFilesSet, null, "");
            System.out.println("attribute  in " + (System.currentTimeMillis() - dt) + "millis");

            /*dt = new Date();
            bulkImportService = getBulkImportService("src/test/resources/import/attributeviewgroup.xml");
            bulkImportService.doImport(stringBuilder, importedFilesSet, null, "");
            System.out.println("attributeviewgroup.xml " + (new Date().getTime() - dt.getTime())); */

            dt = System.currentTimeMillis();
            bulkImportService = getBulkImportService("src/test/resources/import/producttype.xml");
            bulkImportService.doImport(listener, importedFilesSet, null, "");
            System.out.println("producttype in " + (System.currentTimeMillis() - dt) + "millis");


            dt = System.currentTimeMillis();
            bulkImportService = getBulkImportService("src/test/resources/import/category.xml");
            bulkImportService.doImport(listener, importedFilesSet, null, "");
            System.out.println("category in " + (System.currentTimeMillis() - dt) + "millis");


            ResultSet rs = getConnection().getConnection().createStatement().executeQuery ("select * from TPRODUCTTYPE where name = 'ноутбуки'   ");
            rs.next();
            long prodtype_id = rs.getLong("PRODUCTTYPE_ID");
            rs.close();

            rs = getConnection().getConnection().createStatement().executeQuery("select * from TCATEGORY where PRODUCTTYPE_ID =   " + prodtype_id)  ;
            rs.next(); // the product type was imported
            rs.close();

           dt = System.currentTimeMillis();
            bulkImportService = getBulkImportService("src/test/resources/import/shopcategory.xml");
            bulkImportService.doImport(listener, importedFilesSet, null, "");
            System.out.println("shopcategory in " + (System.currentTimeMillis() - dt) + "millis");

            dt = System.currentTimeMillis();
            bulkImportService = getBulkImportService("src/test/resources/import/availability.xml");
            bulkImportService.doImport(listener, importedFilesSet, null, "");
            System.out.println("availability in " + (System.currentTimeMillis() - dt) + "millis");

            dt = System.currentTimeMillis();
            bulkImportService = getBulkImportService("src/test/resources/import/productypeattributeviewgroup.xml");
            bulkImportService.doImport(listener, importedFilesSet, null, "");
            System.out.println("productypeattributeviewgroup in " + (System.currentTimeMillis() - dt) + "millis");

             rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TPRODTYPEATTRVIEWGROUP  ");
            rs.next();
            long cnt = rs.getLong(1);
            assertEquals("181 view group configured per all types" , 182, cnt );
            rs.close();




            dt = System.currentTimeMillis();
            bulkImportService = getBulkImportService("src/test/resources/import/producttypeattr.xml");
            bulkImportService.doImport(listener, importedFilesSet, null, "");
            System.out.println("producttypeattr in " + (System.currentTimeMillis() - dt) + "millis");


            dt = System.currentTimeMillis();
            bulkImportService = getBulkImportService("src/test/resources/import/product.xml");
            bulkImportService.doImport(listener, importedFilesSet, null, "");
            System.out.println("product in " + (System.currentTimeMillis() - dt) + "millis");

            dt = System.currentTimeMillis();
            bulkImportService = getBulkImportService("src/test/resources/import/productsku.xml");
            bulkImportService.doImport(listener, importedFilesSet, null, "");
            System.out.println("attributeviewgroup in " + (System.currentTimeMillis() - dt) + "millis");




            dt = System.currentTimeMillis();
            bulkImportService = getBulkImportService("src/test/resources/import/warehouse.xml");
            bulkImportService.doImport(listener, importedFilesSet, null, "");
            System.out.println("warehouse in " + (System.currentTimeMillis() - dt) + "millis");


            dt = System.currentTimeMillis();
            bulkImportService = getBulkImportService("src/test/resources/import/skuquantity.xml");
            bulkImportService.doImport(listener, importedFilesSet, null, "");
            System.out.println("skuquantity in " + (System.currentTimeMillis() - dt) + "millis");

            dt = System.currentTimeMillis();
            bulkImportService = getBulkImportService("src/test/resources/import/skuprice.xml");
            bulkImportService.doImport(listener, importedFilesSet, null, "");
            System.out.println("skuprice in " + (System.currentTimeMillis() - dt) + "millis");

            dt = System.currentTimeMillis();
            bulkImportService = getBulkImportService("src/test/resources/import/productcategory.xml");
            bulkImportService.doImport(listener, importedFilesSet, null, "");
            System.out.println("productcategory in in " + (System.currentTimeMillis() - dt) + "millis");

            //System.out.println(stringBuilder.toString());



            //assertTrue(stringBuilder.toString(), stringBuilder.toString().indexOf("ERROR") == -1);
            mockery.assertIsSatisfied();

        } catch (Exception e) {
            fail(e.getMessage());
        } finally {
            dumpDataBase("www", new String[]{"TATTRIBUTE", "TPRODUCTTYPE", "TPRODUCTTYPEATTR",
                    "TPRODUCT", "TSKU", "TPRODUCTATTRVALUE",
                    "TSKUWAREHOUSE", "TSKUPRICE", "TPRODUCTCATEGORY", "TCATEGORY",
                    "TPRODTYPEATTRVIEWGROUP" ,
                    "TSHOPCATEGORY"
            });
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


    @Test
    public void testDoImportWithForeignKeys() {


        final BulkImportStatusListener listenerCarrier = mockery.mock(BulkImportStatusListener.class, "listenerCarrier");

        mockery.checking(new Expectations() {{
            //allowing(listenerCarrier).getJobToken(); will(returnValue("1"));
            //allowing(listener).notifyWarning(with(any(String.class)));
            allowing(listenerCarrier).notifyMessage(with(any(String.class)));
            //allowing(listener).notifyError(with(any(String.class)));
            //one(listenerCarrier).notifyCompleted(ImportService.BulkImportResult.OK);
        }});

        final BulkImportStatusListener listenerCarrierSla = mockery.mock(BulkImportStatusListener.class, "listenerCarrierSla");

        mockery.checking(new Expectations() {{
            //allowing(listenerCarrierSla).getJobToken(); will(returnValue("1"));
            //allowing(listener).notifyWarning(with(any(String.class)));
            allowing(listenerCarrierSla).notifyMessage(with(any(String.class)));
            one(listenerCarrierSla).notifyError(with(aStringStartingWith("during import row : NEW_V 1 day;New Vasuki express 1 day delivery;5.58;UAH;NEWVASUKIEXPRESS;F;")));
            one(listenerCarrierSla).notifyError(with(aStringStartingWith("Tuple: CsvImportTupleImpl{sid=carriersla.csv:1")));
            //one(listenerCarrierSla).notifyCompleted(ImportService.BulkImportResult.OK);
        }});

        Set<String> importedFilesSet = new HashSet<String>();

        BulkImportService bulkImportService = getBulkImportService("src/test/resources/import/carrier.xml");
        bulkImportService.doImport(listenerCarrier, importedFilesSet, null, "");

        bulkImportService = getBulkImportService("src/test/resources/import/carriersla.xml");
        bulkImportService.doImport(listenerCarrierSla, importedFilesSet, null, "");

        try {
            dumpDataBase(
                    "carrier",
                    new String[]{"tcarrier", "tcarriersla"}
            );
        } catch (Exception e1) {
            e1.printStackTrace();
            assertTrue(e1.getMessage(), false);
        }


        try {


            // Two carries are imported
            PreparedStatement pst = getConnection().getConnection().prepareStatement(
                    "select count(*) as cnt from tcarrier where name in ('UPS','TNT')");
            ResultSet rs = pst.executeQuery();
            rs.next();
            assertEquals(2, rs.getInt("cnt"));
            rs.close();
            pst.close();

            //sla for UPS only, not for  new vasuki carrier
            pst = getConnection().getConnection().prepareStatement(
                    "select count(*) as cnt from tcarriersla");
            rs = pst.executeQuery();
            rs.next();
            assertEquals(4, rs.getInt("cnt"));
            rs.close();
            pst.close();

        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(e.getMessage(), false);
        }
        mockery.assertIsSatisfied();

    }

    @Test
    public void testDoImportWithSimpleSlaveFiled() {

        final BulkImportStatusListener listener = mockery.mock(BulkImportStatusListener.class, "listener");

        mockery.checking(new Expectations() {{
            allowing(listener).notifyMessage(with(any(String.class)));
        }});


        Set<String> importedFilesSet = new HashSet<String>();

        BulkImportService bulkImportService = getBulkImportService("src/test/resources/import/shop.xml");
        bulkImportService.doImport(listener, importedFilesSet, null, "");

        try {


            PreparedStatement pst = getConnection().getConnection().prepareStatement(
                    "select shop_id  from tshop where code='zzz'");
            ResultSet rs = pst.executeQuery();
            rs.next();
            int shop_id = rs.getInt("shop_id");
            rs.close();
            pst.close();

            //check that shop with code zzz has 3 urls, but 4 was in subimport. one from sub import was updated
            pst = getConnection().getConnection().prepareStatement(
                    "select count(*) as cnt from tshopurl where shop_id = ?");
            pst.setInt(1, shop_id);
            rs = pst.executeQuery();
            rs.next();
            assertEquals(3, rs.getInt("cnt"));
            rs.close();
            pst.close();


        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());

        }

        mockery.assertIsSatisfied();

    }
}
