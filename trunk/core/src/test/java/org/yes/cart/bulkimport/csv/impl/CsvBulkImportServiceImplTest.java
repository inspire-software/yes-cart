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
import org.yes.cart.service.async.JobStatusListener;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static junit.framework.Assert.*;

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
        allowedValue.add("ION");
        allowedValue.add("Brother");
        allowedValue.add("Logitech");
        allowedValue.add("Xerox");
        allowedValue.add("Trust");
        allowedValue.add("Ergotron");
        allowedValue.add("Roline");
        allowedValue.add("Targus");
        allowedValue.add("HP");
        allowedValue.add("Conceptronic");
        allowedValue.add("Toshiba");
        allowedValue.add("Samsung");

        CsvFileReader csvFileReader = new CsvFileReaderImpl();

        csvFileReader.open(
                "src/test/resources/import/brandnames.csv",
                ';',
                '"',
                "UTF-8",
                true);

        String[] line;
        while ((line = csvFileReader.readLine()) != null) {
            assertNotNull(line);
            assertEquals(2, line.length);
            assertEquals(line[0], line[1]);
            allowedValue.remove(line[0]) ;
        }

        assertTrue(allowedValue.isEmpty());

    }

    @Test
    public void testDoProductImportWithSimpleSlaveFiled() throws Exception {
        try {

            getConnection().getConnection().createStatement().execute("CREATE index asdsda on TPRODUCTATTRVALUE(code)") ;

            final JobStatusListener listener = mockery.mock(JobStatusListener.class, "listener");

            mockery.checking(new Expectations() {{
                // ONLY allow messages during import
                allowing(listener).notifyPing();
                allowing(listener).notifyMessage(with(any(String.class)));
            }});

            Set<String> importedFilesSet = new HashSet<String>();

            BulkImportService bulkImportService;
            ResultSet rs;

            long dt = System.currentTimeMillis();
            bulkImportService = getBulkImportService("src/test/resources/import/brandnames.xml");
            bulkImportService.doImport(listener, importedFilesSet, null, "");
            System.out.println("brands in " + (System.currentTimeMillis() - dt) + "millis");

            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) as cnt from TBRAND");
            rs.next();
            long cntBrands = rs.getLong("cnt");
            rs.close();
            assertEquals(17L, cntBrands);  // 12 new + 6 Initial Data (but Samsung is duplicate)

            rs = getConnection().getConnection().createStatement().executeQuery ("select DESCRIPTION from TBRAND where NAME = 'Ergotron'");
            rs.next();
            assertFalse(rs.isAfterLast());
            String brandDescription = rs.getString("DESCRIPTION");
            rs.close();
            assertEquals("Ergotron", brandDescription);


            bulkImportService = getBulkImportService("src/test/resources/import/attributegroupnames.xml");
            bulkImportService.doImport(listener, importedFilesSet, null, "");
            System.out.println("attribute groups in " + (System.currentTimeMillis() - dt) + "millis");

            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) as cnt from TATTRIBUTEGROUP");
            rs.next();
            long cntAttrGroups = rs.getLong("cnt");
            rs.close();
            assertEquals(10L, cntAttrGroups);  // 3 new ones +  7 OOTB

            rs = getConnection().getConnection().createStatement().executeQuery ("select CODE, NAME, DESCRIPTION from TATTRIBUTEGROUP where GUID = '10000001'");
            rs.next();
            assertFalse(rs.isAfterLast());
            String attrGroupCode = rs.getString("CODE");
            String attrGroupName = rs.getString("NAME");
            String attrGroupDesc = rs.getString("DESCRIPTION");
            rs.close();
            assertEquals("NEW1", attrGroupCode);
            assertEquals("New Group 1", attrGroupName);
            assertEquals("New Group 1 desc", attrGroupDesc);



            dt = System.currentTimeMillis();
            bulkImportService = getBulkImportService("src/test/resources/import/attributenames.xml");
            bulkImportService.doImport(listener, importedFilesSet, null, "");
            System.out.println("attribute  in " + (System.currentTimeMillis() - dt) + "millis");

            rs = getConnection().getConnection().createStatement().executeQuery (
                    "select count(a.ATTRIBUTE_ID) as cnt from TATTRIBUTE a, TATTRIBUTEGROUP g where a.ATTRIBUTEGROUP_ID = g.ATTRIBUTEGROUP_ID and g.CODE = 'PRODUCT'");
            rs.next();
            long cntProductAttr = rs.getLong("cnt");
            rs.close();
            assertEquals(1332L, cntProductAttr);  // 1312 new ones +  20 OOTB

            rs = getConnection().getConnection().createStatement().executeQuery (
                    "select ATTRIBUTEGROUP_ID, NAME, DISPLAYNAME, DESCRIPTION, MANDATORY, ALLOWDUPLICATE, ALLOWFAILOVER, RANK, ETYPE_ID from TATTRIBUTE where CODE = '1411'");
            rs.next();
            assertFalse(rs.isAfterLast());
            long attrGroupCodeFK = rs.getLong("ATTRIBUTEGROUP_ID");
            long attrEtypeFK = rs.getLong("ETYPE_ID");
            String attrName = rs.getString("NAME");
            String attrDName = rs.getString("DISPLAYNAME");
            String attrDesc = rs.getString("DESCRIPTION");
            boolean attrMandatory = rs.getBoolean("MANDATORY");
            boolean attrAllowDuplicate = rs.getBoolean("ALLOWDUPLICATE");
            boolean attrAllowFailover = rs.getBoolean("ALLOWFAILOVER");
            int attrRank = rs.getInt("RANK");
            rs.close();
            assertEquals(2003L, attrGroupCodeFK);  // PRODUCT
            assertEquals(1000L, attrEtypeFK);      // String
            assertEquals("Keys", attrName);
            assertEquals("en#~#Keys#~#ru#~#Клавиши#~#", attrDName);
            assertEquals("Keys", attrDesc);
            assertEquals(500, attrRank);
            assertTrue(attrMandatory);
            assertFalse(attrAllowDuplicate);
            assertFalse(attrAllowFailover);



            dt = System.currentTimeMillis();
            bulkImportService = getBulkImportService("src/test/resources/import/producttypenames.xml");
            bulkImportService.doImport(listener, importedFilesSet, null, "");
            System.out.println("producttype in " + (System.currentTimeMillis() - dt) + "millis");

            rs = getConnection().getConnection().createStatement().executeQuery (
                    "select count(*) as cnt from TPRODUCTTYPE ");
            rs.next();
            long cntProductType = rs.getLong("cnt");
            rs.close();
            assertEquals(16L, cntProductType);  // 12 same as categories + 4 from initialdata.xml

            rs = getConnection().getConnection().createStatement().executeQuery (
                    "select PRODUCTTYPE_ID, DESCRIPTION from TPRODUCTTYPE where NAME = 'mice'");
            rs.next();
            assertFalse(rs.isAfterLast());
            String ptypeDesc = rs.getString("DESCRIPTION");
            long ptypeId = rs.getLong("PRODUCTTYPE_ID");
            rs.close();
            assertEquals("mice", ptypeDesc);


            dt = System.currentTimeMillis();
            bulkImportService = getBulkImportService("src/test/resources/import/categorynames.xml");
            bulkImportService.doImport(listener, importedFilesSet, null, "");
            System.out.println("category in " + (System.currentTimeMillis() - dt) + "millis");

            rs = getConnection().getConnection().createStatement().executeQuery (
                    "select count(*) as cnt from TCATEGORY c where c.GUID in ('151','1296','942','803','788','195','194','197','943','196','191','192') ");
            rs.next();
            long cntCats = rs.getLong("cnt");
            rs.close();
            assertEquals(12L, cntCats);  // 12 categories

            rs = getConnection().getConnection().createStatement().executeQuery (
                    "select PARENT_ID, PRODUCTTYPE_ID, DESCRIPTION, GUID from TCATEGORY where NAME = 'mice'");
            rs.next();
            assertFalse(rs.isAfterLast());
            String catDesc = rs.getString("DESCRIPTION");
            String catGuid = rs.getString("GUID");
            long catParentId = rs.getLong("PARENT_ID");
            long catPtypeId = rs.getLong("PRODUCTTYPE_ID");
            rs.close();
            assertEquals("The mouse is the second most important way of communicating with a computer. " +
                    "Please be careful to choose the right type of connection when buying a mouse, there are three different types:- " +
                    "USB is the most modern. You can recognize it by the rectangular connector.- PS/2 connectors are round. " +
                    "This type of connection is fairly commonly used in PCs.- Bluetooth is another modern (wireless) " +
                    "connection method.", catDesc);
            assertEquals(100L, catParentId);
            assertEquals(ptypeId, catPtypeId);
            assertEquals("195", catGuid);



            dt = System.currentTimeMillis();
            bulkImportService = getBulkImportService("src/test/resources/import/shopcategory.xml");
            bulkImportService.doImport(listener, importedFilesSet, null, "");
            System.out.println("shopcategory in " + (System.currentTimeMillis() - dt) + "millis");

            rs = getConnection().getConnection().createStatement().executeQuery (
                    "select count(*) as cnt from TSHOPCATEGORY c where c.SHOP_ID = '10' ");
            rs.next();
            long cntShop10Cats = rs.getLong("cnt");
            rs.close();
            assertEquals(23L, cntShop10Cats);  // 12 categories + 11 from initialdata.xml


            dt = System.currentTimeMillis();
            bulkImportService = getBulkImportService("src/test/resources/import/productypeattributeviewgroupnames.xml");
            bulkImportService.doImport(listener, importedFilesSet, null, "");
            System.out.println("productypeattributeviewgroup in " + (System.currentTimeMillis() - dt) + "millis");

            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TPRODTYPEATTRVIEWGROUP  ");
            rs.next();
            long cntProdTypeGroup = rs.getLong(1);
            rs.close();
            assertEquals(182, cntProdTypeGroup);

            rs = getConnection().getConnection().createStatement().executeQuery (
                    "select PRODUCTTYPE_ID, ATTRCODELIST, NAME from TPRODTYPEATTRVIEWGROUP where GUID = '1128526143'");
            rs.next();
            assertFalse(rs.isAfterLast());
            String prodTgAttrCodeList = rs.getString("ATTRCODELIST");
            String prodTgName = rs.getString("NAME");
            long prodTgPtypeId = rs.getLong("PRODUCTTYPE_ID");
            rs.close();
            assertEquals("67,5,66,68,5170,", prodTgAttrCodeList);
            assertEquals("System requirements", prodTgName);
            assertEquals(ptypeId, prodTgPtypeId);




            dt = System.currentTimeMillis();
            bulkImportService = getBulkImportService("src/test/resources/import/producttypeattrnames.xml");
            bulkImportService.doImport(listener, importedFilesSet, null, "");
            System.out.println("producttypeattr in " + (System.currentTimeMillis() - dt) + "millis");

            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TPRODUCTTYPEATTR  ");
            rs.next();
            long cntProdTypeAttrs = rs.getLong(1);
            rs.close();
            assertEquals(1315L, cntProdTypeAttrs);   // 1312 new + 3 from initialdata.xml

            rs = getConnection().getConnection().createStatement().executeQuery (
                    "select CODE, PRODUCTTYPE_ID, NAV, NAV_TYPE, RANGE_NAV from TPRODUCTTYPEATTR where GUID = '1496'");
            rs.next();
            assertFalse(rs.isAfterLast());
            String prodAttrCode = rs.getString("CODE");
            long prodAttrPtypeId = rs.getLong("PRODUCTTYPE_ID");
            boolean prodAttrNav = rs.getBoolean("NAV");
            String prodAttrNavType = rs.getString("NAV_TYPE");
            String prodAttrNavRange = rs.getString("RANGE_NAV");
            rs.close();
            assertEquals("1496", prodAttrCode);
            assertEquals(ptypeId, prodAttrPtypeId);
            assertFalse(prodAttrNav);
            assertEquals("S", prodAttrNavType);
            assertEquals("", prodAttrNavRange);



            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TPRODUCT  ");
            rs.next();
            long cntBeforeProd = rs.getLong(1);
            rs.close();

            dt = System.currentTimeMillis();
            bulkImportService = getBulkImportService("src/test/resources/import/productnames.xml");
            bulkImportService.doImport(listener, importedFilesSet, null, "");
            System.out.println("product in " + (System.currentTimeMillis() - dt) + "millis");

            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TPRODUCT  ");
            rs.next();
            long cntProd = rs.getLong(1);
            rs.close();
            assertEquals(60L + cntBeforeProd, cntProd);   // 60 new + 44 initialdata.xml

            rs = getConnection().getConnection().createStatement().executeQuery (
                    "select PRODUCT_ID, NAME, DESCRIPTION from TPRODUCT where GUID = '11042544'");
            rs.next();
            assertFalse(rs.isAfterLast());
            long prodId = rs.getLong("PRODUCT_ID");
            String prodName = rs.getString("NAME");
            String prodDesc = rs.getString("DESCRIPTION");
            rs.close();
            assertEquals("Workcentre 6015, 2 Year Extended On-Site Service", prodName);
            assertEquals("Xerox Workcentre 6015, 2 Year Extended On-Site Service. Package weight: 160 g", prodDesc);


            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TSKU  ");
            rs.next();
            long cntBeforeProdSku = rs.getLong(1);
            rs.close();

            dt = System.currentTimeMillis();
            bulkImportService = getBulkImportService("src/test/resources/import/productsku.xml");
            bulkImportService.doImport(listener, importedFilesSet, null, "");
            System.out.println("productsku in " + (System.currentTimeMillis() - dt) + "millis");

            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TSKU  ");
            rs.next();
            long cntProdSku = rs.getLong(1);
            rs.close();
            assertEquals(60L + cntBeforeProdSku, cntProdSku);   // 60 new + 35 initialdata.xml

            rs = getConnection().getConnection().createStatement().executeQuery (
                    "select CODE, PRODUCT_ID, NAME, DESCRIPTION, BARCODE from TSKU where GUID = '6015ES3'");
            rs.next();
            assertFalse(rs.isAfterLast());
            long prodSkuProdId = rs.getLong("PRODUCT_ID");
            String prodSkuCode = rs.getString("CODE");
            String prodSkuName = rs.getString("NAME");
            String prodSkuDesc = rs.getString("DESCRIPTION");
            String prodSkuBarcode = rs.getString("BARCODE");
            rs.close();
            assertEquals(prodId, prodSkuProdId);
            assertEquals("6015ES3", prodSkuCode);
            assertEquals("0095205861457", prodSkuBarcode);
            assertEquals("Workcentre 6015, 2 Year Extended On-Site Service", prodSkuName);
            assertEquals("Xerox Workcentre 6015, 2 Year Extended On-Site Service. Package weight: 160 g", prodSkuDesc);



            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TPRODUCTATTRVALUE  ");
            rs.next();
            long cntBeforeProdValues = rs.getLong(1);
            rs.close();

            dt = System.currentTimeMillis();
            bulkImportService = getBulkImportService("src/test/resources/import/productsattributes.xml");
            bulkImportService.doImport(listener, importedFilesSet, null, "");
            System.out.println("products attributes in " + (System.currentTimeMillis() - dt) + "millis");

            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TPRODUCTATTRVALUE  ");
            rs.next();
            long cntProdValues = rs.getLong(1);
            rs.close();
            assertEquals(6046L + cntBeforeProdValues, cntProdValues);   // 6046 new + 14 initialdata.xml


            rs = getConnection().getConnection().createStatement().executeQuery (
                    "select VAL, DISPLAYVAL from TPRODUCTATTRVALUE where CODE = '3834' and PRODUCT_ID = " + prodId + "");
            rs.next();
            assertFalse(rs.isAfterLast());
            String productAttrVal = rs.getString("VAL");
            String productAttrDVal = rs.getString("DISPLAYVAL");
            rs.close();
            assertEquals("2", productAttrVal);
            assertEquals("en#~#2 year(s)#~#ru#~#2", productAttrDVal);



            dt = System.currentTimeMillis();
            bulkImportService = getBulkImportService("src/test/resources/import/warehouse.xml");
            bulkImportService.doImport(listener, importedFilesSet, null, "");
            System.out.println("warehouse in " + (System.currentTimeMillis() - dt) + "millis");

            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TWAREHOUSE  ");
            rs.next();
            long cntWarehouse = rs.getLong(1);
            rs.close();
            assertEquals(4L, cntWarehouse);   // 1 new + 3 initialdata.xml

            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TSKUWAREHOUSE  ");
            rs.next();
            long cntBeforeInventory = rs.getLong(1);
            rs.close();

            dt = System.currentTimeMillis();
            bulkImportService = getBulkImportService("src/test/resources/import/skuinventory.xml");
            bulkImportService.doImport(listener, importedFilesSet, null, "");
            System.out.println("skuquantity in " + (System.currentTimeMillis() - dt) + "millis");

            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TSKUWAREHOUSE  ");
            rs.next();
            long cntInventory = rs.getLong(1);
            rs.close();
            assertEquals(60L + cntBeforeInventory, cntInventory);   // 60 new + 28 initialdata.xml


            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TSKUPRICE  ");
            rs.next();
            long cntBeforePrices = rs.getLong(1);
            rs.close();

            dt = System.currentTimeMillis();
            bulkImportService = getBulkImportService("src/test/resources/import/skuprices.xml");
            bulkImportService.doImport(listener, importedFilesSet, null, "");
            System.out.println("skuprice in " + (System.currentTimeMillis() - dt) + "millis");

            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TSKUPRICE  ");
            rs.next();
            long cntPrices = rs.getLong(1);
            rs.close();
            assertEquals(180L + cntBeforePrices, cntPrices);   // 180 new + 53 initialdata.xml


            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TPRODUCTCATEGORY  ");
            rs.next();
            long cntBeforeProductCategory = rs.getLong(1);
            rs.close();

            dt = System.currentTimeMillis();
            bulkImportService = getBulkImportService("src/test/resources/import/productcategorynames.xml");
            bulkImportService.doImport(listener, importedFilesSet, null, "");
            System.out.println("productcategory in in " + (System.currentTimeMillis() - dt) + "millis");

            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TPRODUCTCATEGORY  ");
            rs.next();
            long cntProductCategory = rs.getLong(1);
            rs.close();
            assertEquals(60L + cntBeforeProductCategory, cntProductCategory);   // 60 new + 27 initialdata.xml

            mockery.assertIsSatisfied();

        } catch (Exception e) {
            e.printStackTrace();
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


        final JobStatusListener listenerCarrier = mockery.mock(JobStatusListener.class, "listenerCarrier");

        mockery.checking(new Expectations() {{
            allowing(listenerCarrier).notifyPing();
            allowing(listenerCarrier).notifyMessage(with(any(String.class)));
        }});

        final JobStatusListener listenerCarrierSla = mockery.mock(JobStatusListener.class, "listenerCarrierSla");

        mockery.checking(new Expectations() {{
            allowing(listenerCarrierSla).notifyPing();
            allowing(listenerCarrierSla).notifyMessage(with(any(String.class)));
            one(listenerCarrierSla).notifyError(with(aStringStartingWith("during import row : CsvImportTupleImpl{sid=carrierslanames.csv:1, line=[NEW_V 1 day,New Vasuki express 1")));
        }});

        Set<String> importedFilesSet = new HashSet<String>();

        BulkImportService bulkImportService = getBulkImportService("src/test/resources/import/carriernames.xml");
        bulkImportService.doImport(listenerCarrier, importedFilesSet, null, "");

        bulkImportService = getBulkImportService("src/test/resources/import/carrierslanames.xml");
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
            ResultSet rs = null;

            // Two carries are imported
            rs = getConnection().getConnection().createStatement().executeQuery(
                    "select count(*) as cnt from TCARRIER where name in ('UPS','TNT')");
            rs.next();
            int cntNewCarries = rs.getInt("cnt");
            rs.close();

            assertEquals(2, cntNewCarries);

            //sla for UPS only + 3 from initialdata.xml, not for  new vasuki carrier
            rs = getConnection().getConnection().createStatement().executeQuery(
                    "select count(*) as cnt from TCARRIERSLA");
            rs.next();
            int cntCarriesSlas = rs.getInt("cnt");
            rs.close();
            assertEquals(4, cntCarriesSlas);


        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(e.getMessage(), false);
        }
        mockery.assertIsSatisfied();

    }

    @Test
    public void testDoImportWithSimpleSlaveFiled() {

        final JobStatusListener listener = mockery.mock(JobStatusListener.class, "listener");

        mockery.checking(new Expectations() {{
            allowing(listener).notifyPing();
            allowing(listener).notifyMessage(with(any(String.class)));
        }});


        Set<String> importedFilesSet = new HashSet<String>();

        BulkImportService bulkImportService = getBulkImportService("src/test/resources/import/shop.xml");
        bulkImportService.doImport(listener, importedFilesSet, null, "");

        try {
            ResultSet rs;

            rs = getConnection().getConnection().createStatement().executeQuery(
                    "select shop_id  from TSHOP where code='zzz'");
            rs.next();
            int shop_id = rs.getInt("shop_id");
            rs.close();
            assertTrue(shop_id > 0);

            //check that shop with code zzz has 3 urls, but 4 was in subimport. one from sub import was a duplicate
            rs = getConnection().getConnection().createStatement().executeQuery(
                    "select count(*) as cnt from TSHOPURL where shop_id = " + shop_id);
            rs.next();
            int cntShopUrls = rs.getInt("cnt");
            rs.close();

            assertEquals(3, cntShopUrls);


        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());

        }

        mockery.assertIsSatisfied();

    }
}
