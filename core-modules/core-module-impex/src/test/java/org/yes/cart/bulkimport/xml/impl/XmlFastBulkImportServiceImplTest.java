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

package org.yes.cart.bulkimport.xml.impl;

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
import org.yes.cart.bulkcommon.service.ImportService;
import org.yes.cart.bulkimport.model.ImportDescriptor;
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

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 05/11/2018
 * Time: 22:43
 */
public class XmlFastBulkImportServiceImplTest extends BaseCoreDBTestCase {

    ImportService bulkImportService = null;
    XStreamProvider<ImportDescriptor> xml = null;

    private final Mockery mockery = new JUnit4Mockery();

    @Override
    @Before
    public void setUp()  {

        if (bulkImportService == null) {
            bulkImportService = createContext().getBean("xmlFastBulkImportService", ImportService.class);
            xml = createContext().getBean("importXmlDescriptorXStreamProvider", XStreamProvider.class);
        }
        super.setUp();

    }

    @Override
    @After
    public void tearDown() throws Exception {
        bulkImportService = null;
        xml = null;
        super.tearDown();

    }

    private JobContext createContext(final String descriptorPath, final JobStatusListener listener, final Set<String> importFileSet) throws Exception {

        final ImportDescriptor descriptor = xml.fromXML(new FileInputStream(new File(descriptorPath)));
        descriptor.setImportDirectory(new File("src/test/resources/import/xml").getAbsolutePath());

        return new JobContextImpl(false, listener, new HashMap<String, Object>() {{
            put(JobContextKeys.IMPORT_DESCRIPTOR, descriptor);
            put(JobContextKeys.IMPORT_DESCRIPTOR_NAME, descriptorPath);
            put(JobContextKeys.IMPORT_FILE_SET, importFileSet);
        }});
    }

    @Test
    public void testDoImport() throws Exception {
        try {

            final JobStatusListener listener = mockery.mock(JobStatusListener.class, "listener");

            mockery.checking(new Expectations() {{
                // ONLY allow messages during import
                allowing(listener).notifyPing();
                allowing(listener).notifyPing(with(any(String.class)));
                allowing(listener).notifyMessage(with(any(String.class)));
            }});

            Set<String> importedFilesSet = new HashSet<>();

            ResultSet rs;
            long dt, change;


            rs = getConnection().getConnection().createStatement().executeQuery (
                    "select count(e.ETYPE_ID) as cnt from TETYPE e");
            rs.next();
            long cntEtypesBefore = rs.getLong("cnt");
            rs.close();

            dt = System.currentTimeMillis();
            bulkImportService.doImport(createContext("src/test/resources/import/xml/etypes.xml", listener, importedFilesSet));
            final long eTypes = System.currentTimeMillis() - dt;

            rs = getConnection().getConnection().createStatement().executeQuery (
                    "select count(e.ETYPE_ID) as cnt from TETYPE e");
            rs.next();
            long cntEtypes = rs.getLong("cnt");
            rs.close();
            assertEquals(1L + cntEtypesBefore, cntEtypes);

            change = cntEtypes;
            System.out.println(String.format("%5d", change) + " e-types in " + eTypes + "millis (~" + (eTypes / change) + " per item)");

            rs = getConnection().getConnection().createStatement().executeQuery (
                    "select count(g.ATTRIBUTEGROUP_ID) as cnt from TATTRIBUTEGROUP g");
            rs.next();
            long cntBeforeProductAttrGroup = rs.getLong("cnt");
            rs.close();

            dt = System.currentTimeMillis();
            bulkImportService.doImport(createContext("src/test/resources/import/xml/attributegroups.xml", listener, importedFilesSet));
            final long attrGroups = System.currentTimeMillis() - dt;

            rs = getConnection().getConnection().createStatement().executeQuery (
                    "select count(g.ATTRIBUTEGROUP_ID) as cnt from TATTRIBUTEGROUP g");
            rs.next();
            long cntProductAttrGroup = rs.getLong("cnt");
            rs.close();
            assertEquals(1L + cntBeforeProductAttrGroup, cntProductAttrGroup);

            change = cntProductAttrGroup;
            System.out.println(String.format("%5d", change) + " attribute groups in " + attrGroups + "millis (~" + (attrGroups / change) + " per item)");

            rs = getConnection().getConnection().createStatement().executeQuery (
                    "select ATTRIBUTEGROUP_ID, NAME, CODE from TATTRIBUTEGROUP where CODE = 'XML_NEW'");
            rs.next();
            assertFalse(rs.isAfterLast());
            String attrGroupName = rs.getString("NAME");
            String attrGroupCode = rs.getString("CODE");
            rs.close();
            assertEquals("New group", attrGroupName);
            assertEquals("XML_NEW", attrGroupCode);


            rs = getConnection().getConnection().createStatement().executeQuery (
                    "select count(a.ATTRIBUTE_ID) as cnt from TATTRIBUTE a, TATTRIBUTEGROUP g where a.ATTRIBUTEGROUP_ID = g.ATTRIBUTEGROUP_ID and g.CODE = 'PRODUCT'");
            rs.next();
            long cntBeforeProductAttr = rs.getLong("cnt");
            rs.close();

            dt = System.currentTimeMillis();
            bulkImportService.doImport(createContext("src/test/resources/import/xml/attributes.xml", listener, importedFilesSet));
            final long attrs = System.currentTimeMillis() - dt;

            rs = getConnection().getConnection().createStatement().executeQuery (
                    "select count(a.ATTRIBUTE_ID) as cnt from TATTRIBUTE a, TATTRIBUTEGROUP g where a.ATTRIBUTEGROUP_ID = g.ATTRIBUTEGROUP_ID and g.CODE = 'PRODUCT'");
            rs.next();
            long cntProductAttr = rs.getLong("cnt");
            rs.close();
            assertEquals(1336L + cntBeforeProductAttr, cntProductAttr);

            change = cntProductAttr - cntBeforeProductAttr;
            System.out.println(String.format("%5d", change) + " attributes in " + attrs + "millis (~" + (attrs / change) + " per item)");

            rs = getConnection().getConnection().createStatement().executeQuery (
                    "select ATTRIBUTEGROUP_ID, NAME, DISPLAYNAME, DESCRIPTION, MANDATORY, ALLOWDUPLICATE, ALLOWFAILOVER, RANK, ETYPE_ID from TATTRIBUTE where CODE = 'XML_8419'");
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
            assertEquals(1003L, attrGroupCodeFK);  // PRODUCT
            assertEquals(1000L, attrEtypeFK);      // String
            assertEquals("Trackball", attrName);
            assertTrue(attrDName.contains("en#~#Trackball#~#"));
            assertTrue(attrDName.contains("ru#~#Трэкбол#~#"));
            assertEquals("Trackball", attrDesc);
            assertEquals(500, attrRank);
            assertTrue(attrMandatory);
            assertFalse(attrAllowDuplicate);
            assertFalse(attrAllowFailover);


            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) as cnt from TPRODUCTTYPE ");
            rs.next();
            long cntBeforeProductType = rs.getLong("cnt");
            rs.close();

            dt = System.currentTimeMillis();
            bulkImportService.doImport(createContext("src/test/resources/import/xml/producttypes.xml", listener, importedFilesSet));
            final long prodTypes = System.currentTimeMillis() - dt;

            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) as cnt from TPRODUCTTYPE ");
            rs.next();
            long cntProductType = rs.getLong("cnt");
            rs.close();

            assertEquals(17L + cntBeforeProductType, cntProductType);  // 12 same as categories + 5 from initialdata.xml

            change = cntProductType - cntBeforeProductType;
            System.out.println(String.format("%5d", change) + " product types + groups + type attributes in " + prodTypes + "millis (~" + (prodTypes / change) + " per item)");

            rs = getConnection().getConnection().createStatement().executeQuery ("select PRODUCTTYPE_ID, DESCRIPTION from TPRODUCTTYPE where NAME = 'XML_mice'");
            rs.next();
            assertFalse(rs.isAfterLast());
            String ptypeDesc = rs.getString("DESCRIPTION");
            long ptypeId = rs.getLong("PRODUCTTYPE_ID");
            rs.close();
            assertEquals("XML_mice", ptypeDesc);

            rs = getConnection().getConnection().createStatement().executeQuery ("select PRODUCTTYPE_ID, ATTRCODELIST, NAME from TPRODTYPEATTRVIEWGROUP where GUID='XML_-2008597539'");
            rs.next();
            assertFalse(rs.isAfterLast());
            String ptypeGroupAttrsList = rs.getString("ATTRCODELIST");
            String ptypeGroupName = rs.getString("NAME");
            long ptypeGroupId = rs.getLong("PRODUCTTYPE_ID");
            rs.close();
            assertEquals(ptypeId, ptypeGroupId);
            assertEquals("XML_8549,XML_445,XML_1050,XML_898,XML_4614,XML_5826,XML_775,XML_7500,XML_5485,XML_1861,XML_5990,XML_8550,XML_3141,XML_751,XML_8426,XML_1766,XML_1448,XML_86,XML_6617,XML_771,XML_732,XML_1412,XML_789,XML_6298,XML_7501,XML_8547,XML_487,XML_5685,XML_2055,XML_8552,XML_8410,XML_7498,XML_1769,XML_2143,XML_8551,XML_1937,XML_8411,XML_1496,XML_1592,XML_2505,XML_1885,XML_7502,XML_7499,XML_2179,XML_8543,XML_6371,XML_1679,XML_1680,XML_8545,XML_1394,XML_5736,XML_474,XML_5991,XML_5709,XML_2539,XML_5184,XML_1501,XML_5456,XML_4799,XML_6305,XML_5501,XML_914,XML_83,XML_777,XML_4898,XML_8301,XML_4924,XML_5307,XML_1411,XML_5659,XML_1655", ptypeGroupAttrsList);
            assertEquals("XML_Technical details", ptypeGroupName);

            rs = getConnection().getConnection().createStatement().executeQuery ("select VISIBLE, RANK from TPRODUCTTYPEATTR where PRODUCTTYPE_ID=" + ptypeId + " and CODE='XML_5709'");
            rs.next();
            assertFalse(rs.isAfterLast());
            boolean ptypeAttrVisible = rs.getBoolean("VISIBLE");
            int ptypeAttrRank = rs.getInt("RANK");
            rs.close();
            assertTrue(ptypeAttrVisible);
            assertEquals(400, ptypeAttrRank);


            dt = System.currentTimeMillis();
            bulkImportService.doImport(createContext("src/test/resources/import/xml/category.xml", listener, importedFilesSet));
            final long cats = System.currentTimeMillis() - dt;

            change = 90;
            System.out.println(String.format("%5d", change) + " categories in " + cats + "millis (~" + (cats / change) + " per item)");

            rs = getConnection().getConnection().createStatement().executeQuery ("select CATEGORY_ID,PARENT_ID, PRODUCTTYPE_ID, DESCRIPTION, GUID, URI from TCATEGORY where NAME = 'XML_mice'");
            rs.next();
            assertFalse(rs.isAfterLast());
            String catDesc = rs.getString("DESCRIPTION");
            String catGuid = rs.getString("GUID");
            String catSeoUri = rs.getString("URI");
            long catParentId = rs.getLong("PARENT_ID");
            long catPtypeId = rs.getLong("PRODUCTTYPE_ID");
            long catMiceId = rs.getLong("CATEGORY_ID");
            rs.close();
            assertEquals("The mouse is the second most important way of communicating with a computer. " +
                    "Please be careful to choose the right type of connection when buying a mouse, there are three different types:- " +
                    "USB is the most modern. You can recognize it by the rectangular connector.- PS/2 connectors are round. " +
                    "This type of connection is fairly commonly used in PCs.- Bluetooth is another modern (wireless) " +
                    "connection method.", catDesc);
            assertEquals(100L, catParentId);
            assertEquals(ptypeId, catPtypeId);
            assertEquals("XML_195", catGuid);
            assertEquals("XML_mice", catSeoUri);

            rs = getConnection().getConnection().createStatement().executeQuery ("select CATEGORY_ID,PARENT_ID from TCATEGORY where NAME = 'XML_wireless mice'");
            rs.next();
            assertFalse(rs.isAfterLast());
            long catWirelessId = rs.getLong("CATEGORY_ID");
            long parentWirelessId = rs.getLong("PARENT_ID");
            rs.close();
            assertEquals(catMiceId, parentWirelessId);

            rs = getConnection().getConnection().createStatement().executeQuery ("select CATEGORY_ID,PARENT_ID from TCATEGORY where NAME = 'XML_optical mice'");
            rs.next();
            assertFalse(rs.isAfterLast());
            long parentOpticalId = rs.getLong("PARENT_ID");
            rs.close();
            assertEquals(catWirelessId, parentOpticalId);




            dt = System.currentTimeMillis();
            bulkImportService.doImport(createContext("src/test/resources/import/xml/content.xml", listener, importedFilesSet));
            final long cont = System.currentTimeMillis() - dt;

            change = 2;
            System.out.println(String.format("%5d", change) + " CMS content in " + cont + "millis (~" + (cont / change) + " per item)");

            rs = getConnection().getConnection().createStatement().executeQuery ("select CATEGORY_ID,PARENT_ID, PRODUCTTYPE_ID, DESCRIPTION, NAME, URI from TCATEGORY where GUID = 'SHOIP3-LVL1'");
            rs.next();
            assertFalse(rs.isAfterLast());
            String conDesc = rs.getString("DESCRIPTION");
            String conName = rs.getString("NAME");
            long conParentId = rs.getLong("PARENT_ID");
            long conId = rs.getLong("CATEGORY_ID");
            rs.close();
            assertEquals("Level 1 content", conDesc);
            assertTrue(conParentId > 0L);
            assertEquals("Level 1", conName);

            rs = getConnection().getConnection().createStatement().executeQuery ("select VAL from TCATEGORYATTRVALUE where CATEGORY_ID = '" + conId + "' and CODE like 'CONTENT_BODY_en_%' order by CODE");
            rs.next();
            assertFalse(rs.isAfterLast());
            assertTrue(rs.getString("VAL").startsWith("EN:\nLorem ipsum dolor sit amet"));
            rs.next();
            assertFalse(rs.isAfterLast());
            assertTrue(rs.getString("VAL").startsWith("m id est laborum.\n"));
            rs.close();




            dt = System.currentTimeMillis();
            bulkImportService.doImport(createContext("src/test/resources/import/xml/brands.xml", listener, importedFilesSet));
            final long brand = System.currentTimeMillis() - dt;

            change = 3;
            System.out.println(String.format("%5d", change) + " brands in " + brand + "millis (~" + (brand / change) + " per item)");

            rs = getConnection().getConnection().createStatement().executeQuery ("select BRAND_ID, DESCRIPTION, NAME from TBRAND where GUID = 'XML_002'");
            rs.next();
            assertFalse(rs.isAfterLast());
            long brandId = rs.getLong("BRAND_ID");
            String brandDesc = rs.getString("DESCRIPTION");
            String brandName = rs.getString("NAME");
            rs.close();
            assertEquals("xml 002 desc", brandDesc);
            assertEquals("XML 002", brandName);

            rs = getConnection().getConnection().createStatement().executeQuery ("select VAL from TBRANDATTRVALUE where BRAND_ID = '" + brandId + "' and CODE like 'BRAND_IMAGE0'");
            rs.next();
            assertFalse(rs.isAfterLast());
            assertEquals("xml_002_brand.jpeg", rs.getString("VAL"));
            rs.close();

            


            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TPRODUCT  ");
            rs.next();
            long cntBeforeProd = rs.getLong(1);
            rs.close();

            dt = System.currentTimeMillis();
            bulkImportService.doImport(createContext("src/test/resources/import/xml/products.xml", listener, importedFilesSet));
            final long prods = System.currentTimeMillis() - dt;

            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TPRODUCT  ");
            rs.next();
            long cntProd = rs.getLong(1);
            rs.close();

            change = cntProd - cntBeforeProd;
            System.out.println(String.format("%5d", change) + " products + attribute + SKU + categories + brands in " + prods + "millis (~" + (prods / change) + " per item)");

            assertEquals(115L + cntBeforeProd, cntProd);   // 60 new + 44 initialdata.xml

            rs = getConnection().getConnection().createStatement().executeQuery (
                    "select PRODUCT_ID, NAME, TAG from TPRODUCT where CODE = 'XML_BENDER-ua'");
            rs.next();
            assertFalse(rs.isAfterLast());
            long prodId = rs.getLong("PRODUCT_ID");
            String prodName = rs.getString("NAME");
            String prodTag = rs.getString("TAG");
            rs.close();
            assertEquals("Бендер Згинач Родріґес", prodName);
            assertEquals("sale newarrival specialpromo", prodTag);


            rs = getConnection().getConnection().createStatement().executeQuery (
                    "select CODE, PRODUCT_ID, NAME, SKU_ID, BARCODE from TSKU where PRODUCT_ID = " + prodId);
            rs.next();
            assertFalse(rs.isAfterLast());
            long prodSkuProdId = rs.getLong("PRODUCT_ID");
            long skuProdId = rs.getLong("SKU_ID");
            String prodSkuCode = rs.getString("CODE");
            String prodSkuName = rs.getString("NAME");
            String prodSkuBarcode = rs.getString("BARCODE");
            rs.close();
            assertEquals(prodId, prodSkuProdId);
            assertEquals("XML_BENDER-ua", prodSkuCode);
            assertEquals("001234567905", prodSkuBarcode);
            assertEquals("Бендер Згинач Родріґес", prodSkuName);

            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TPRODUCTATTRVALUE where PRODUCT_ID = " + prodId);
            rs.next();
            long cntProdValues = rs.getLong(1);
            rs.close();
            assertEquals(1L, cntProdValues);

            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TPRODUCTSKUATTRVALUE where SKU_ID = " + skuProdId);
            rs.next();
            long cntSkuValues = rs.getLong(1);
            rs.close();
            assertEquals(1L, cntSkuValues);

            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TPRODUCTASSOCIATION where PRODUCT_ID = " + prodId);
            rs.next();
            long cntProdAssocValues = rs.getLong(1);
            rs.close();
            assertEquals(2L, cntProdAssocValues);  // 1 cross + 1 new-assoc


            rs = getConnection().getConnection().createStatement().executeQuery (
                    "select VAL, DISPLAYVAL from TPRODUCTATTRVALUE where CODE = 'WEIGHT' and PRODUCT_ID = " + prodId);
            rs.next();
            assertFalse(rs.isAfterLast());
            String productAttrVal = rs.getString("VAL");
            rs.close();
            assertEquals("1.15", productAttrVal);

            rs = getConnection().getConnection().createStatement().executeQuery (
                    "select VAL, DISPLAYVAL from TPRODUCTSKUATTRVALUE where CODE = 'WEIGHT' and SKU_ID = " + skuProdId);
            rs.next();
            assertFalse(rs.isAfterLast());
            String skuAttrVal = rs.getString("VAL");
            rs.close();
            assertEquals("1.16", skuAttrVal);


            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TPRODUCTCATEGORY where PRODUCT_ID = " + prodId);
            rs.next();
            long cntProductCategory = rs.getLong(1);
            rs.close();
            assertEquals(1, cntProductCategory);

            dt = System.currentTimeMillis();
            bulkImportService.doImport(createContext("src/test/resources/import/xml/productscategories.xml", listener, importedFilesSet));
            final long prodsCats2 = System.currentTimeMillis() - dt;

            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TPRODUCTCATEGORY where PRODUCT_ID = " + prodId);
            rs.next();
            long cntProductCategory2 = rs.getLong(1);
            rs.close();
            assertEquals(2, cntProductCategory2);

            change = 4;
            System.out.println(String.format("%5d", change) + " product categories in " + prodsCats2 + "millis (~" + (prodsCats2 / change) + " per item)");


            dt = System.currentTimeMillis();
            bulkImportService.doImport(createContext("src/test/resources/import/xml/productslinks.xml", listener, importedFilesSet));
            final long prodsLinks2 = System.currentTimeMillis() - dt;

            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TPRODUCTASSOCIATION where PRODUCT_ID = " + prodId);
            rs.next();
            long cntProductAssociations2 = rs.getLong(1);
            rs.close();
            assertEquals(4, cntProductAssociations2);

            change = 4;
            System.out.println(String.format("%5d", change) + " product associations in " + prodsLinks2 + "millis (~" + (prodsLinks2 / change) + " per item)");


            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TSKUWAREHOUSE  ");
            rs.next();
            long cntBeforeInventory = rs.getLong(1);
            rs.close();

            dt = System.currentTimeMillis();
            bulkImportService.doImport(createContext("src/test/resources/import/xml/inventory.xml", listener, importedFilesSet));
            final long skuInv = System.currentTimeMillis() - dt;

            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TSKUWAREHOUSE  ");
            rs.next();
            long cntInventory = rs.getLong(1);
            rs.close();

            change = cntInventory - cntBeforeInventory;
            System.out.println(String.format("%5d", change) + " inventory records in " + skuInv + "millis (~" + (skuInv / change) + " per item)");

            assertEquals(100L + cntBeforeInventory, cntInventory);   // 60 new + 28 initialdata.xml



            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TSKUPRICE  ");
            rs.next();
            long cntBeforePrices = rs.getLong(1);
            rs.close();

            dt = System.currentTimeMillis();
            bulkImportService.doImport(createContext("src/test/resources/import/xml/pricelist.xml", listener, importedFilesSet));
            final long skuPrice = System.currentTimeMillis() - dt;

            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TSKUPRICE  ");
            rs.next();
            long cntPrices = rs.getLong(1);
            rs.close();

            change = cntPrices - cntBeforePrices;
            System.out.println(String.format("%5d", change) + " sku price records in " + skuPrice + "millis (~" + (skuPrice / change) + " per item)");

            assertEquals(241L + cntBeforePrices, cntPrices);   // 180 new + 53 initialdata.xml



            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TTAX  ");
            rs.next();
            long cntBeforeTax = rs.getLong(1);
            rs.close();

            dt = System.currentTimeMillis();
            bulkImportService.doImport(createContext("src/test/resources/import/xml/tax.xml", listener, importedFilesSet));
            final long taxes = System.currentTimeMillis() - dt;

            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TTAX  ");
            rs.next();
            long cntTaxes = rs.getLong(1);
            rs.close();

            change = cntTaxes - cntBeforeTax;
            System.out.println(String.format("%5d", change) + " tax entries in " + taxes + "millis (~" + (taxes / change) + " per item)");

            assertEquals(8L + cntBeforeTax, cntTaxes);   // 8 new


            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TTAXCONFIG  ");
            rs.next();
            long cntBeforeTaxConfig = rs.getLong(1);
            rs.close();

            dt = System.currentTimeMillis();
            bulkImportService.doImport(createContext("src/test/resources/import/xml/taxconfigs.xml", listener, importedFilesSet));
            final long taxConfigs = System.currentTimeMillis() - dt;

            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TTAXCONFIG  ");
            rs.next();
            long cntTaxConfigs = rs.getLong(1);
            rs.close();

            change = cntTaxConfigs - cntBeforeTaxConfig;
            System.out.println(String.format("%5d", change) + " tax configs in " + taxConfigs + "millis (~" + (taxConfigs / change) + " per item)");

            assertEquals(8L + cntBeforeTaxConfig, cntTaxConfigs);   // 8 new


            dt = System.currentTimeMillis();
            bulkImportService.doImport(createContext("src/test/resources/import/xml/system.xml", listener, importedFilesSet));
            final long sysConfigs = System.currentTimeMillis() - dt;

            change = 1;
            System.out.println(String.format("%5d", 1) + " system preference in " + taxConfigs + "millis (~" + (sysConfigs / change) + " per item)");


            rs = getConnection().getConnection().createStatement().executeQuery ("select VAL from TSYSTEMATTRVALUE where GUID = '1056_TSYSTEMATTRVALUE'");
            rs.next();
            final String newValue = rs.getString(1);
            rs.close();


            assertEquals("33500", newValue);




            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TDATAGROUP  ");
            rs.next();
            long cntBeforeDataGroups = rs.getLong(1);
            rs.close();

            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TDATADESCRIPTOR  ");
            rs.next();
            long cntBeforeDataDesc = rs.getLong(1);
            rs.close();

            dt = System.currentTimeMillis();
            bulkImportService.doImport(createContext("src/test/resources/import/xml/datagroups.xml", listener, importedFilesSet));
            final long dataGroups = System.currentTimeMillis() - dt;

            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TDATAGROUP  ");
            rs.next();
            long cntDataGroups = rs.getLong(1);
            rs.close();

            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TDATADESCRIPTOR  ");
            rs.next();
            long cntDataDescriptors = rs.getLong(1);
            rs.close();

            change = cntDataGroups - cntBeforeDataGroups;
            System.out.println(String.format("%5d", change) + " data groups in " + dataGroups + "millis (~" + (dataGroups / change) + " per item)");

            assertEquals(1L + cntBeforeDataGroups, cntDataGroups);
            assertEquals(1L + cntBeforeDataDesc, cntDataDescriptors); // new descriptor added via group






            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TCOUNTRY  ");
            rs.next();
            long cntBeforeCountry = rs.getLong(1);
            rs.close();

            dt = System.currentTimeMillis();
            bulkImportService.doImport(createContext("src/test/resources/import/xml/countries.xml", listener, importedFilesSet));
            final long countries = System.currentTimeMillis() - dt;

            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TCOUNTRY  ");
            rs.next();
            long cntCountries = rs.getLong(1);
            rs.close();

            change = cntCountries - cntBeforeCountry;
            System.out.println(String.format("%5d", change) + " countries in " + countries + "millis (~" + (countries / change) + " per item)");

            assertEquals(1L + cntBeforeCountry, cntCountries);   // 1 new

            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TSTATE  ");
            rs.next();
            long cntBeforeState = rs.getLong(1);
            rs.close();

            dt = System.currentTimeMillis();
            bulkImportService.doImport(createContext("src/test/resources/import/xml/countrystates.xml", listener, importedFilesSet));
            final long countryStates = System.currentTimeMillis() - dt;

            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TSTATE  ");
            rs.next();
            long cntCountryStates = rs.getLong(1);
            rs.close();

            change = cntCountryStates - cntBeforeState;
            System.out.println(String.format("%5d", change) + " country states in " + countryStates + "millis (~" + (countryStates / change) + " per item)");

            assertEquals(1L + cntBeforeState, cntCountryStates);   // 1 new




            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TWAREHOUSE  ");
            rs.next();
            long cntBeforeFc = rs.getLong(1);
            rs.close();

            dt = System.currentTimeMillis();
            bulkImportService.doImport(createContext("src/test/resources/import/xml/fulfilmentcentres.xml", listener, importedFilesSet));
            final long fcs = System.currentTimeMillis() - dt;

            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TWAREHOUSE  ");
            rs.next();
            long cntFcs = rs.getLong(1);
            rs.close();

            change = cntFcs - cntBeforeFc;
            System.out.println(String.format("%5d", change) + " fulfilment centres in " + fcs + "millis (~" + (fcs / change) + " per item)");

            assertEquals(1L + cntBeforeFc, cntFcs);   // 1 new





            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TCARRIER  ");
            rs.next();
            long cntBeforeShipProvider = rs.getLong(1);
            rs.close();

            dt = System.currentTimeMillis();
            bulkImportService.doImport(createContext("src/test/resources/import/xml/shippingproviders.xml", listener, importedFilesSet));
            final long sps = System.currentTimeMillis() - dt;

            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TCARRIER  ");
            rs.next();
            long cntShipProvider = rs.getLong(1);
            rs.close();

            change = cntShipProvider - cntBeforeShipProvider;
            System.out.println(String.format("%5d", change) + " shipping provider + methods in " + sps + "millis (~" + (sps / change) + " per item)");

            assertEquals(1L + cntBeforeShipProvider, cntShipProvider);   // 1 new

            rs = getConnection().getConnection().createStatement().executeQuery ("select CARRIER_ID, NAME, DESCRIPTION, DISPLAYNAME, WORLDWIDE from TCARRIER where GUID = 'XML_CARRIER'");
            rs.next();
            final Long carrierId = rs.getLong("CARRIER_ID");
            final String carrierName = rs.getString("NAME");
            final String carrierDisplayName = rs.getString("DISPLAYNAME");
            final Boolean carrierWorldwide = rs.getBoolean("WORLDWIDE");
            rs.close();

            assertEquals("XML carrier", carrierName);
            assertTrue(carrierDisplayName, carrierDisplayName.contains("XML carrier EN"));
            assertTrue(carrierWorldwide);

            rs = getConnection().getConnection().createStatement().executeQuery ("select CARRIER_ID, MAX_DAYS, MIN_DAYS, EXCLUDE_WEEK_DAYS, EXCLUDE_DATES, EXCLUDED_CT, SUPPORTED_PGS, SUPPORTED_FCS from TCARRIERSLA where GUID = 'XML_CARRIERSLA_1'");
            rs.next();
            final Long slaCarrierId = rs.getLong("CARRIER_ID");
            final int slaMax = rs.getInt("MAX_DAYS");
            final int slaMin = rs.getInt("MIN_DAYS");
            final String slaExclWeedDays = rs.getString("EXCLUDE_WEEK_DAYS");
            final String slaExclDates = rs.getString("EXCLUDE_DATES");
            final String slaExclCT = rs.getString("EXCLUDED_CT");
            final String slaSupPgs = rs.getString("SUPPORTED_PGS");
            final String slaSupFcs = rs.getString("SUPPORTED_FCS");
            rs.close();

            assertEquals(carrierId, slaCarrierId);
            assertEquals(3, slaMin);
            assertEquals(10, slaMax);
            assertEquals("6,7,1", slaExclWeedDays);
            assertEquals("2019-01-01,2019-02-01:2019-02-03", slaExclDates);
            assertEquals("B2B,B2E", slaExclCT);
            assertEquals("WAREHOUSE_1,WAREHOUSE_2", slaSupFcs);
            assertEquals("testPaymentGatewayLabel,courierPaymentGatewayLabel", slaSupPgs);





            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TPROMOTION  ");
            rs.next();
            long cntBeforePromotions = rs.getLong(1);
            rs.close();

            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TPROMOTIONCOUPON  ");
            rs.next();
            long cntBeforePromotionCoupons = rs.getLong(1);
            rs.close();

            dt = System.currentTimeMillis();
            bulkImportService.doImport(createContext("src/test/resources/import/xml/promotions.xml", listener, importedFilesSet));
            final long promos = System.currentTimeMillis() - dt;

            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TPROMOTION  ");
            rs.next();
            long cntPromotions = rs.getLong(1);
            rs.close();

            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TPROMOTIONCOUPON  ");
            rs.next();
            long cntPromotionCoupons = rs.getLong(1);
            rs.close();

            change = cntPromotions - cntBeforePromotions;
            System.out.println(String.format("%5d", change) + " promotions + coupons in " + promos + "millis (~" + (promos / change) + " per item)");

            assertEquals(1L + cntBeforePromotions, cntPromotions);   // 1 new

            rs = getConnection().getConnection().createStatement().executeQuery ("select PROMOTION_ID, NAME, ENABLED from TPROMOTION where CODE = 'XML_PROMO001'");
            rs.next();
            final Long promotionId = rs.getLong("PROMOTION_ID");
            final String promotionName = rs.getString("NAME");
            final boolean promotionEnabled = rs.getBoolean("ENABLED");
            rs.close();

            assertEquals("XML Promo 001", promotionName);
            assertTrue(promotionEnabled);

            rs = getConnection().getConnection().createStatement().executeQuery ("select PROMOTION_ID, USAGE_LIMIT, USAGE_LIMIT_PER_CUSTOMER  from TPROMOTIONCOUPON where CODE = 'XML_PROMO001-003'");
            rs.next();
            final Long couponPromotionId = rs.getLong("PROMOTION_ID");
            final int couponUsage = rs.getInt("USAGE_LIMIT");
            final int couponUsageCustomer = rs.getInt("USAGE_LIMIT_PER_CUSTOMER");
            rs.close();

            assertEquals(promotionId, couponPromotionId);
            assertEquals(10, couponUsageCustomer);
            assertEquals(100, couponUsage);




            mockery.assertIsSatisfied();

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        } finally {
            dumpDataBase("impex", "TATTRIBUTE", "TPRODUCTTYPE", "TPRODUCTTYPEATTR",
                    "TPRODUCT", "TSKU", "TPRODUCTATTRVALUE",
                    "TSKUWAREHOUSE", "TSKUPRICE", "TPRODUCTCATEGORY", "TCATEGORY", "TCATEGORYATTRVALUE",
                    "TPRODTYPEATTRVIEWGROUP",
                    "TSHOPCATEGORY", "TPROMOTION", "TPROMOTIONCOUPON", "TTAX", "TTAXCONFIG");
        }

    }

    private static class StringStartsWithMatcher extends TypeSafeMatcher<String> {
        private String prefix;

        public StringStartsWithMatcher(String prefix) {
            this.prefix = prefix;
        }

        @Override
        public boolean matchesSafely(String s) {
            return s.startsWith(prefix);
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("a string starting with ").appendValue(prefix);
        }
    }

    private static class StringNotStartsWithMatcher extends TypeSafeMatcher<String> {
        private String prefix;

        public StringNotStartsWithMatcher(String prefix) {
            this.prefix = prefix;
        }

        @Override
        public boolean matchesSafely(String s) {
            return !s.startsWith(prefix);
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("a string NOT starting with ").appendValue(prefix);
        }
    }

    private static class StringContainsMatcher extends TypeSafeMatcher<String> {
        private String text;

        public StringContainsMatcher(String text) {
            this.text = text;
        }

        @Override
        public boolean matchesSafely(String s) {
            return s.contains(text);
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("a string containing text: ").appendValue(text);
        }
    }

    private static class StringNotContainsMatcher extends TypeSafeMatcher<String> {
        private String text;

        public StringNotContainsMatcher(String text) {
            this.text = text;
        }

        @Override
        public boolean matchesSafely(String s) {
            return !s.contains(text);
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("a string NOT containing text: ").appendValue(text);
        }
    }

    @Factory
    public static Matcher<String> aStringStartingWith(String prefix) {
        return new XmlFastBulkImportServiceImplTest.StringStartsWithMatcher(prefix);
    }

    @Factory
    public static Matcher<String> aStringNotStartingWith(String prefix) {
        return new XmlFastBulkImportServiceImplTest.StringNotStartsWithMatcher(prefix);
    }

    @Factory
    public static Matcher<String> aStringContains(String text) {
        return new XmlFastBulkImportServiceImplTest.StringContainsMatcher(text);
    }

    @Factory
    public static Matcher<String> aStringNotContains(String text) {
        return new XmlFastBulkImportServiceImplTest.StringNotContainsMatcher(text);
    }

}