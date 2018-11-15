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

package org.yes.cart.bulkexport.xml.impl;

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
import org.junit.Ignore;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.bulkcommon.service.ExportService;
import org.yes.cart.bulkcommon.service.ImportService;
import org.yes.cart.bulkexport.model.ExportDescriptor;
import org.yes.cart.bulkimport.model.ImportDescriptor;
import org.yes.cart.domain.entity.ProductTypeAttr;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.async.model.JobContext;
import org.yes.cart.service.async.model.JobContextKeys;
import org.yes.cart.service.async.model.impl.JobContextImpl;
import org.yes.cart.stream.xml.XStreamProvider;

import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.FileInputStream;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * User: denispavlov
 * Date: 26/10/2018
 * Time: 08:19
 */
@Ignore
public class XmlFastBulkExportServiceImpl2Test extends BaseCoreDBTestCase {

    ExportService bulkExportService = null;
    XStreamProvider<ExportDescriptor> xml = null;

    ImportService bulkImportService = null;
    XStreamProvider<ImportDescriptor> xml2 = null;


    private final Mockery mockery = new JUnit4Mockery();

    @Override
    @Before
    public void setUp()  {

        if (bulkExportService == null) {
            bulkExportService = createContext().getBean("xmlFastBulkExportService", ExportService.class);
            xml = createContext().getBean("exportXmlDescriptorXStreamProvider", XStreamProvider.class);
            bulkImportService = createContext().getBean("csvBulkImportService", ImportService.class);
            xml2 = createContext().getBean("importCsvDescriptorXStreamProvider", XStreamProvider.class);

        }
        super.setUp();

    }


    @Override
    @After
    public void tearDown() throws Exception {
        bulkExportService = null;
        xml = null;
        super.tearDown();

    }


    private JobContext createContext2(final String descriptorPath, final JobStatusListener listener, final Set<String> importFileSet) throws Exception {

        final ImportDescriptor descriptor = xml2.fromXML(new FileInputStream(new File(descriptorPath)));
        descriptor.setImportDirectory(new File("src/test/resources/import/csv").getAbsolutePath());

        return new JobContextImpl(false, listener, new HashMap<String, Object>() {{
            put(JobContextKeys.IMPORT_DESCRIPTOR, descriptor);
            put(JobContextKeys.IMPORT_DESCRIPTOR_NAME, descriptorPath);
            put(JobContextKeys.IMPORT_FILE_SET, importFileSet);
        }});
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

            final JobStatusListener listener = mockery.mock(JobStatusListener.class, "listener2");

            mockery.checking(new Expectations() {{
                // ONLY allow messages during import
                allowing(listener).notifyPing();
                allowing(listener).notifyPing(with(any(String.class)));
                allowing(listener).notifyMessage(with(any(String.class)));
            }});

            Set<String> importedFilesSet = new HashSet<>();

            ResultSet rs;


            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) as cnt from TBRAND");
            rs.next();
            long cntBeforeBrands = rs.getLong("cnt");
            rs.close();

            long dt = System.currentTimeMillis();
            bulkImportService.doImport(createContext2("src/test/resources/import/csv/brandnames.xml", listener, importedFilesSet));

            final long brandMillis = System.currentTimeMillis() - dt;
            System.out.println("  12 brands in " + brandMillis + "millis (~" + (brandMillis / 12) + " per item)");

            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) as cnt from TBRAND");
            rs.next();
            long cntBrands = rs.getLong("cnt");
            rs.close();
            assertEquals(11L + cntBeforeBrands, cntBrands);  // 12 new + 7 Initial Data (but Samsung is duplicate)

            rs = getConnection().getConnection().createStatement().executeQuery ("select DESCRIPTION from TBRAND where NAME = 'Ergotron'");
            rs.next();
            assertFalse(rs.isAfterLast());
            String brandDescription = rs.getString("DESCRIPTION");
            rs.close();
            assertEquals("Ergotron", brandDescription);


            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) as cnt from TATTRIBUTEGROUP");
            rs.next();
            long cntBeforeAttrGroups = rs.getLong("cnt");
            rs.close();

            bulkImportService.doImport(createContext2("src/test/resources/import/csv/attributegroupnames.xml", listener, importedFilesSet));
            final long attrGroups = System.currentTimeMillis() - dt;
            System.out.println("   3 attribute groups in " + attrGroups + "millis (~" + (attrGroups / 3) + " per item)");

            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) as cnt from TATTRIBUTEGROUP");
            rs.next();
            long cntAttrGroups = rs.getLong("cnt");
            rs.close();
            assertEquals(3L + cntBeforeAttrGroups, cntAttrGroups);  // 3 new ones +  6 OOTB

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

            rs = getConnection().getConnection().createStatement().executeQuery (
                    "select count(a.ATTRIBUTE_ID) as cnt from TATTRIBUTE a, TATTRIBUTEGROUP g where a.ATTRIBUTEGROUP_ID = g.ATTRIBUTEGROUP_ID and g.CODE = 'PRODUCT'");
            rs.next();
            long cntBeforeProductAttr = rs.getLong("cnt");
            rs.close();

            dt = System.currentTimeMillis();
            bulkImportService.doImport(createContext2("src/test/resources/import/csv/attributenames.xml", listener, importedFilesSet));
            final long attrs = System.currentTimeMillis() - dt;
            System.out.println("1312 attributes  in " + attrs + "millis (~" + (attrs / 1312) + " per item)");

            rs = getConnection().getConnection().createStatement().executeQuery (
                    "select count(a.ATTRIBUTE_ID) as cnt from TATTRIBUTE a, TATTRIBUTEGROUP g where a.ATTRIBUTEGROUP_ID = g.ATTRIBUTEGROUP_ID and g.CODE = 'PRODUCT'");
            rs.next();
            long cntProductAttr = rs.getLong("cnt");
            rs.close();
            assertEquals(1312L + cntBeforeProductAttr, cntProductAttr);

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
            assertEquals(1003L, attrGroupCodeFK);  // PRODUCT
            assertEquals(1000L, attrEtypeFK);      // String
            assertEquals("Keys", attrName);
            assertTrue(attrDName.contains("en#~#Keys#~#"));
            assertTrue(attrDName.contains("ru#~#Клавиши#~#"));
            assertEquals("Keys", attrDesc);
            assertEquals(500, attrRank);
            assertTrue(attrMandatory);
            assertFalse(attrAllowDuplicate);
            assertFalse(attrAllowFailover);


            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) as cnt from TPRODUCTTYPE ");
            rs.next();
            long cntBeforeProductType = rs.getLong("cnt");
            rs.close();

            dt = System.currentTimeMillis();
            bulkImportService.doImport(createContext2("src/test/resources/import/csv/producttypenames.xml", listener, importedFilesSet));
            final long prodTypes = System.currentTimeMillis() - dt;
            System.out.println("  12 product types in " + prodTypes + "millis (~" + (prodTypes / 12) + " per item)");

            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) as cnt from TPRODUCTTYPE ");
            rs.next();
            long cntProductType = rs.getLong("cnt");
            rs.close();

            assertEquals(12L + cntBeforeProductType, cntProductType);  // 12 same as categories + 5 from initialdata.xml

            rs = getConnection().getConnection().createStatement().executeQuery ("select PRODUCTTYPE_ID, DESCRIPTION from TPRODUCTTYPE where NAME = 'mice'");
            rs.next();
            assertFalse(rs.isAfterLast());
            String ptypeDesc = rs.getString("DESCRIPTION");
            long ptypeId = rs.getLong("PRODUCTTYPE_ID");
            rs.close();
            assertEquals("mice", ptypeDesc);


            dt = System.currentTimeMillis();
            bulkImportService.doImport(createContext2("src/test/resources/import/csv/categorynames.xml", listener, importedFilesSet));
            final long cats = System.currentTimeMillis() - dt;
            System.out.println("  14 categories in " + cats + "millis (~" + (cats / 14) + " per item)");

            rs = getConnection().getConnection().createStatement().executeQuery (
                    "select count(*) as cnt from TCATEGORY c where c.GUID in ('151','1296','942','803','788','195','19501','19502','194','197','943','196','191','192') ");
            rs.next();
            long cntCats = rs.getLong("cnt");
            rs.close();
            assertEquals(14L, cntCats);  // 14 categories

            rs = getConnection().getConnection().createStatement().executeQuery ("select CATEGORY_ID,PARENT_ID, PRODUCTTYPE_ID, DESCRIPTION, GUID, URI from TCATEGORY where NAME = 'mice'");
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
            assertEquals("195", catGuid);
            assertEquals("mice", catSeoUri);

            rs = getConnection().getConnection().createStatement().executeQuery ("select CATEGORY_ID,PARENT_ID from TCATEGORY where NAME = 'wireless mice'");
            rs.next();
            assertFalse(rs.isAfterLast());
            long catWirelessId = rs.getLong("CATEGORY_ID");
            long parentWirelessId = rs.getLong("PARENT_ID");
            rs.close();
            assertEquals(catMiceId, parentWirelessId);

            rs = getConnection().getConnection().createStatement().executeQuery ("select CATEGORY_ID,PARENT_ID from TCATEGORY where NAME = 'optical mice'");
            rs.next();
            assertFalse(rs.isAfterLast());
            long parentOpticalId = rs.getLong("PARENT_ID");
            rs.close();
            assertEquals(catWirelessId, parentOpticalId);

            dt = System.currentTimeMillis();
            bulkImportService.doImport(createContext2("src/test/resources/import/csv/contentnames.xml", listener, importedFilesSet));
            final long cont = System.currentTimeMillis() - dt;
            System.out.println("   2 content in " + cont + "millis (~" + (cont / 2) + " per item)");

            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) as cnt from TCATEGORY c where c.GUID in ('LONG-CONTENT','SHORT-CONTENT') ");
            rs.next();
            long cntCont = rs.getLong("cnt");
            rs.close();
            assertEquals(2L, cntCont);  // 2 content

            rs = getConnection().getConnection().createStatement().executeQuery (
                    "select c.PARENT_ID, c.GUID, v.VAL from TCATEGORY c, TCATEGORYATTRVALUE v where c.GUID = 'LONG-CONTENT' and c.CATEGORY_ID = v.CATEGORY_ID and v.CODE in ('CONTENT_BODY_en_1', 'CONTENT_BODY_en_2') order by v.CODE asc");
            rs.next();
            assertFalse(rs.isAfterLast());
            String cVal1 = rs.getString("VAL");
            String conGuid1 = rs.getString("GUID");
            long conParentId1 = rs.getLong("PARENT_ID");
            rs.next();
            assertFalse(rs.isAfterLast());
            String cVal2 = rs.getString("VAL");
            String conGuid2 = rs.getString("GUID");
            long conParentId2 = rs.getLong("PARENT_ID");
            rs.close();

            assertEquals(0L, conParentId1);
            assertEquals("LONG-CONTENT", conGuid1);
            assertEquals(0L, conParentId2);
            assertEquals("LONG-CONTENT", conGuid2);
            assertEquals(
                    "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam volutpat est ac ultrices tincidunt. Donec sodales nec libero suscipit cursus. Duis vel pulvinar purus, eu adipiscing justo. Donec porttitor aliquet lacinia. Quisque pellentesque venenatis nunc eu dignissim. Quisque eros leo, pellentesque facilisis iaculis eu, rutrum posuere arcu. Pellentesque blandit nunc at mauris semper, eu tempor felis pellentesque. Maecenas sodales nunc id rhoncus vestibulum. Quisque at est eros. Vestibulum euismod sapien urna, sit amet luctus orci fermentum id. Fusce nec lacus vel eros fringilla commodo non eu purus.\n" +
                            "\n" +
                            "Morbi convallis dui sed justo elementum, non blandit velit lobortis. Mauris venenatis dignissim odio in iaculis. Aliquam ac felis accumsan, blandit lectus nec, fringilla nisi. Cras blandit ut mauris sit amet placerat. Donec quis sollicitudin massa. Donec ut luctus diam. Nullam viverra iaculis neque.\n" +
                            "\n" +
                            "Duis rutrum, diam ac vulputate tincidunt, augue nisl vehicula turpis, id luctus lacus dui ut massa. Donec convallis odio interdum turpis eleifend, sed iaculis tortor sodales. Vivamus quis massa a turpis dapibus condimentum eget vitae odio. Aliquam lobortis orci arcu, at fringilla sapien consequat quis. Nam at turpis eget lacus sodales accumsan sit amet ut libero. Nam in faucibus magna. Nunc nisi purus, vehicula ut porttitor a, malesuada id tortor.\n" +
                            "\n" +
                            "Proin malesuada urna a arcu egestas luctus. Aliquam volutpat, sapien at tempus luctus, metus odio viverra turpis, ut tincidunt velit nisi non tortor. Nulla pretium volutpat scelerisque. Suspendisse potenti. Proin eu facilisis urna, aliquet pellentesque orci. Nam fringilla fringilla justo, eget varius est fringilla quis. Curabitur suscipit odio in lectus faucibus sodales at sit amet massa. Proin dictum turpis sapien, id tristique turpis sollicitudin sit amet. Maecenas eget leo sem. Praesent in risus a mauris tincidunt varius eu id mi.\n" +
                            "\n" +
                            "Nulla aliquam sollicitudin rutrum. Mauris et diam id sapien mollis venenatis. Phasellus blandit volutpat imperdiet. Integer vel malesuada dolor. Pellentesque aliquam in nunc vitae consectetur. Quisque viverra venenatis ultrices. Nam sit amet consectetur nisl. Aliquam molestie tortor vitae pretium convallis. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus.\n" +
                            "\n" +
                            "Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Aliquam erat volutpat. Nulla facilisi. Ut viverra malesuada luctus. Morbi iaculis dolor sit amet euismod ultrices. Praesent ullamcorper lorem non arcu molestie porta. Maecenas dictum augue non quam consequat, eget molestie diam molestie. Sed ac est ac dolor aliquet posuere a quis nisi. Sed eget ante sit amet orci egestas lobortis. Ut mollis varius molestie. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Duis nec urna volutpat, ultrices justo sit amet, tincidunt erat.\n" +
                            "\n" +
                            "Curabitur egestas sodales ligula quis feugiat. Nullam elit nisl, fringilla non sem non, viverra fermentum justo. Sed mi urna, dictum et lobortis vel, egestas a velit. Nam vel pretium augue. Praesent scelerisque vulputate massa, non mattis augue molestie id. Curabitur quis pellentesque orci. Pellentesque vehicula enim in diam tempor scelerisque.\n" +
                            "\n" +
                            "Fusce ante nisl, elementum vel magna sit amet, lacinia facilisis libero. Proin lacinia massa nec turpis placerat, at volutpat dolor fermentum. Fusce sed interdum eros. Proin dapibus sagittis tellus convallis bibendum. Maecenas nec tortor turpis. Mauris porttitor neque accumsan, iaculis urna ut, auctor purus. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Nulla magna nunc, scelerisque sollicitudin tincidunt ut, mollis at sapien. Maecenas ornare vehicula eros, ut tincidunt purus lacinia non. Nulla sapien ante, porta a lorem sed, porta pretium libero. Proin rhoncus, quam eget lobortis placerat, nibh elit imperdiet augue, a sollicitudin purus nisl ornare libero. Praesent malesuada purus a facilisis tempus. Sed vel lacus vitae lacus euismod commodo ac at ante. Donec at turpis sit amet nulla ultricies lobortis et sed libero. Phasellus ac magna interdum, dapibus dui sit amet, vestibulum diam. Vivamus sed eros id dui condimentum consectetur.\n" +
                            "\n" +
                            "Curabitur rutrum sem vitae purus egestas volutpat. Phasellus egestas neque vitae velit varius accumsan. Suspendisse posuere tristique ultrices. Vivamus eu odio eu turpis pharetra lacinia. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Vestibulum nibh nisl, egestas et sodales eget, semper in est. Mauris mollis, enim ac sagittis sollicitudin, nulla sapien bibendum nisi, ac eleifend mi massa lobortis lorem. Aliquam elit libero, luctus sit amet facilisis eu, blandit tristique orci. Mauris auctor placerat risus convallis gravida. Quisque accumsan auctor lorem vel venenatis. Morbi eu dolor felis. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. In porttitor iaculis nulla a posuere.\n" +
                            "\n" +
                            "Maecenas condimentum non lacus id pretium. Nunc mi neque, euismod vitae scelerisque eget, dictum et leo. Proin vitae gravida metus. Nulla sodales at mi ac sodales. Donec ante ligula, aliquam quis est sit amet, fermentum malesuada odio. Morbi risus orci, bibendum eu pulvinar ut, pellentesque tempor urna. Pellentesque eu mi placerat, scelerisque mauris quis, commodo quam.", cVal1 + cVal2);



            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) as cnt from TSHOPCATEGORY c where c.SHOP_ID = '10' ");
            rs.next();
            long cntBeforeShop10Cats = rs.getLong("cnt");
            rs.close();

            dt = System.currentTimeMillis();
            bulkImportService.doImport(createContext2("src/test/resources/import/csv/shopcategory.xml", listener, importedFilesSet));
            final long shopCats = System.currentTimeMillis() - dt;
            System.out.println("  12 shop categories in " + shopCats + "millis (~" + (shopCats / 12) + " per item)");

            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) as cnt from TSHOPCATEGORY c where c.SHOP_ID = '10' ");
            rs.next();
            long cntShop10Cats = rs.getLong("cnt");
            rs.close();
            assertEquals(12L + cntBeforeShop10Cats, cntShop10Cats);  // 12 categories + 11 from initialdata.xml


            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TPRODTYPEATTRVIEWGROUP  ");
            rs.next();
            long cntBeforeProdTypeGroup = rs.getLong(1);
            rs.close();

            dt = System.currentTimeMillis();
            bulkImportService.doImport(createContext2("src/test/resources/import/csv/productypeattributeviewgroupnames.xml", listener, importedFilesSet));

            final long prodTypeAttrGroups = System.currentTimeMillis() - dt;
            System.out.println(" 179 product type attribute view groups in " + prodTypeAttrGroups + "millis (~" + (prodTypeAttrGroups / 179) + " per item)");

            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TPRODTYPEATTRVIEWGROUP  ");
            rs.next();
            long cntProdTypeGroup = rs.getLong(1);
            rs.close();
            assertEquals(179 + cntBeforeProdTypeGroup, cntProdTypeGroup);

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



            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TPRODUCTTYPEATTR  ");
            rs.next();
            long cntBeforeProdTypeAttrs = rs.getLong(1);
            rs.close();

            dt = System.currentTimeMillis();
            bulkImportService.doImport(createContext2("src/test/resources/import/csv/producttypeattrnames.xml", listener, importedFilesSet));
            final long prodTypeAttr = System.currentTimeMillis() - dt;
            System.out.println("1312 product type attributes in " + prodTypeAttr + "millis (~" + (prodTypeAttr / 1312) + " per item)");

            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TPRODUCTTYPEATTR  ");
            rs.next();
            long cntProdTypeAttrs = rs.getLong(1);
            rs.close();
            assertEquals(1312L + cntBeforeProdTypeAttrs, cntProdTypeAttrs);   // 1312 new + 3 from initialdata.xml

            rs = getConnection().getConnection().createStatement().executeQuery (
                    "select CODE, PRODUCTTYPE_ID, NAV_TYPE, RANGE_NAV from TPRODUCTTYPEATTR where GUID = '1496'");
            rs.next();
            assertFalse(rs.isAfterLast());
            String prodAttrCode = rs.getString("CODE");
            long prodAttrPtypeId = rs.getLong("PRODUCTTYPE_ID");
            String prodAttrNavType = rs.getString("NAV_TYPE");
            String prodAttrNavRange = rs.getString("RANGE_NAV");
            rs.close();
            assertEquals("1496", prodAttrCode);
            assertEquals(ptypeId, prodAttrPtypeId);
            assertEquals(ProductTypeAttr.NAVIGATION_TYPE_SINGLE, prodAttrNavType);
            assertEquals("", prodAttrNavRange);



            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TPRODUCT  ");
            rs.next();
            long cntBeforeProd = rs.getLong(1);
            rs.close();

            dt = System.currentTimeMillis();
            bulkImportService.doImport(createContext2("src/test/resources/import/csv/productnames.xml", listener, importedFilesSet));
            final long prods = System.currentTimeMillis() - dt;
            System.out.println("  60 products in " + prods + "millis (~" + (prods / 60) + " per item)");

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
            bulkImportService.doImport(createContext2("src/test/resources/import/csv/productsku.xml", listener, importedFilesSet));
            final long skus = System.currentTimeMillis() - dt;
            System.out.println("  60 product sku's in " + skus + "millis (~" + (skus / 60) + " per item)");

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
            bulkImportService.doImport(createContext2("src/test/resources/import/csv/productsattributes.xml", listener, importedFilesSet));
            final long prodAttrs = System.currentTimeMillis() - dt;
            System.out.println("3286 products' attributes in " + prodAttrs + "millis (~" + (prodAttrs / 3286) + " per item)");

            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TPRODUCTATTRVALUE  ");
            rs.next();
            long cntProdValues = rs.getLong(1);
            rs.close();
            assertEquals(3286L + cntBeforeProdValues, cntProdValues);


            rs = getConnection().getConnection().createStatement().executeQuery (
                    "select VAL, DISPLAYVAL from TPRODUCTATTRVALUE where CODE = '3834' and PRODUCT_ID = " + prodId + "");
            rs.next();
            assertFalse(rs.isAfterLast());
            String productAttrVal = rs.getString("VAL");
            String productAttrDVal = rs.getString("DISPLAYVAL");
            rs.close();
            assertEquals("2", productAttrVal);
            assertEquals("en#~#2 year(s)#~#ru#~#2 лет", productAttrDVal);



            dt = System.currentTimeMillis();
            bulkImportService.doImport(createContext2("src/test/resources/import/csv/productsattributes.delete.native.xml", listener, importedFilesSet));
            final long prodAttrsDelNative = System.currentTimeMillis() - dt;
            System.out.println(" -20 products' attributes in " + prodAttrsDelNative + "millis (~" + (prodAttrsDelNative / 20) + " per item) native");

            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TPRODUCTATTRVALUE  ");
            rs.next();
            long cntProdValuesDelNative = rs.getLong(1);
            rs.close();
            assertEquals(3286L - 20L + cntBeforeProdValues, cntProdValuesDelNative);


            dt = System.currentTimeMillis();
            bulkImportService.doImport(createContext2("src/test/resources/import/csv/productsattributes.delete.hbm.xml", listener, importedFilesSet));
            final long prodAttrsDelHbm = System.currentTimeMillis() - dt;
            System.out.println(" -20 products' attributes in " + prodAttrsDelHbm + "millis (~" + (prodAttrsDelHbm / 20) + " per item) hbm");

            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TPRODUCTATTRVALUE  ");
            rs.next();
            long cntProdValuesDelHbm = rs.getLong(1);
            rs.close();
            assertEquals(3286L - 40L + cntBeforeProdValues, cntProdValuesDelHbm);


            dt = System.currentTimeMillis();
            bulkImportService.doImport(createContext2("src/test/resources/import/csv/productsattributes.delete.native.all.xml", listener, importedFilesSet));
            final long prodAttrsDelNativeAll = System.currentTimeMillis() - dt;
            System.out.println(" -25 products' attributes in " + prodAttrsDelNativeAll + "millis (~" + (prodAttrsDelNativeAll / 25) + " per item) native all");

            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TPRODUCTATTRVALUE  ");
            rs.next();
            long cntProdValuesDelNativeAll = rs.getLong(1);
            rs.close();
            assertEquals(3286L - 65L + cntBeforeProdValues, cntProdValuesDelNativeAll);




            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TWAREHOUSE  ");
            rs.next();
            long cntBeforeWarehouse = rs.getLong(1);
            rs.close();

            dt = System.currentTimeMillis();
            bulkImportService.doImport(createContext2("src/test/resources/import/csv/warehouse.xml", listener, importedFilesSet));
            System.out.println("   1 warehouse in " + (System.currentTimeMillis() - dt) + "millis");

            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TWAREHOUSE  ");
            rs.next();
            long cntWarehouse = rs.getLong(1);
            rs.close();
            assertEquals(1L + cntBeforeWarehouse, cntWarehouse);   // 1 new + 3 initialdata.xml


            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TSKUWAREHOUSE  ");
            rs.next();
            long cntBeforeInventory = rs.getLong(1);
            rs.close();

            dt = System.currentTimeMillis();
            bulkImportService.doImport(createContext2("src/test/resources/import/csv/skuinventory.xml", listener, importedFilesSet));
            final long skuInv = System.currentTimeMillis() - dt;
            System.out.println("  60 sku inventory records in " + skuInv + "millis (~" + (skuInv / 60) + " per item)");

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
            bulkImportService.doImport(createContext2("src/test/resources/import/csv/skuprices.xml", listener, importedFilesSet));
            final long skuPrice = System.currentTimeMillis() - dt;
            System.out.println(" 180 sku price records in " + skuPrice + "millis (~" + (skuPrice / 180) + " per item)");

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
            bulkImportService.doImport(createContext2("src/test/resources/import/csv/productcategorynames.xml", listener, importedFilesSet));
            final long prodCats = System.currentTimeMillis() - dt;
            System.out.println("  60 product categories in " + prodCats + "millis (~" + (prodCats / 60) + " per item)");

            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TPRODUCTCATEGORY  ");
            rs.next();
            long cntProductCategory = rs.getLong(1);
            rs.close();
            assertEquals(60L + cntBeforeProductCategory, cntProductCategory);   // 60 new + 27 initialdata.xml


            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TPROMOTION  ");
            rs.next();
            long cntBeforePromotion = rs.getLong(1);
            rs.close();

            dt = System.currentTimeMillis();
            bulkImportService.doImport(createContext2("src/test/resources/import/csv/promotionnames.xml", listener, importedFilesSet));
            final long promos = System.currentTimeMillis() - dt;
            System.out.println("   9 promotions in " + promos + "millis (~" + (promos / 9) + " per item)");

            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TPROMOTION  ");
            rs.next();
            long cntPromotions = rs.getLong(1);
            rs.close();
            assertEquals(9L + cntBeforePromotion, cntPromotions);   // 9 new

            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TPROMOTIONCOUPON  ");
            rs.next();
            long cntBeforePromotionCoupons = rs.getLong(1);
            rs.close();

            dt = System.currentTimeMillis();
            bulkImportService.doImport(createContext2("src/test/resources/import/csv/promotioncouponnames.xml", listener, importedFilesSet));
            final long promocoupons = System.currentTimeMillis() - dt;
            System.out.println("   3 promotion coupons in " + promocoupons + "millis (~" + (promocoupons / 3) + " per item)");

            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TPROMOTIONCOUPON  ");
            rs.next();
            long cntPromotionCoupons = rs.getLong(1);
            rs.close();
            assertEquals(3L + cntBeforePromotionCoupons, cntPromotionCoupons);   // 3 new



            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TTAX  ");
            rs.next();
            long cntBeforeTax = rs.getLong(1);
            rs.close();

            dt = System.currentTimeMillis();
            bulkImportService.doImport(createContext2("src/test/resources/import/csv/taxnames.xml", listener, importedFilesSet));
            final long taxes = System.currentTimeMillis() - dt;
            System.out.println("   2 tax entries in " + taxes + "millis (~" + (taxes / 2) + " per item)");

            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TTAX  ");
            rs.next();
            long cntTaxes = rs.getLong(1);
            rs.close();
            assertEquals(2L + cntBeforeTax, cntTaxes);   // 2 new


            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TTAXCONFIG  ");
            rs.next();
            long cntBeforeTaxConfig = rs.getLong(1);
            rs.close();

            dt = System.currentTimeMillis();
            bulkImportService.doImport(createContext2("src/test/resources/import/csv/taxconfignames.xml", listener, importedFilesSet));
            final long taxConfigs = System.currentTimeMillis() - dt;
            System.out.println("   5 tax configs in " + taxConfigs + "millis (~" + (taxConfigs / 5) + " per item)");

            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TTAXCONFIG  ");
            rs.next();
            long cntTaxConfigs = rs.getLong(1);
            rs.close();
            assertEquals(5L + cntBeforeTaxConfig, cntTaxConfigs);   // 5 new


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


            rs = getConnection().getConnection().createStatement().executeQuery (
                    "select count(a.ATTRIBUTE_ID) as cnt from TATTRIBUTE a, TATTRIBUTEGROUP g where a.ATTRIBUTEGROUP_ID = g.ATTRIBUTEGROUP_ID and g.CODE = 'PRODUCT'");
            rs.next();
            long cntProductAttr = rs.getLong("cnt");
            rs.close();

            long dt = System.currentTimeMillis();
            String fileToExport = "target/attributenames-export-" + UUID.randomUUID().toString() + ".xml";
            bulkExportService.doExport(createContext("src/test/resources/export/xml/attributenames.xml", listener, fileToExport));
            final long attrs = System.currentTimeMillis() - dt;
            System.out.println(cntProductAttr + " attributes  in " + attrs + "millis (~" + (attrs / cntProductAttr) + " per item)");

            File xml = new File(fileToExport);
            String content = FileUtils.readFileToString(xml, "UTF-8");
            assertTrue(content.contains("group=\"PRODUCT\" etype=\"String\" rank=\"500\">"));

            validateXmlFile(xml);

            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TPRODUCT  ");
            rs.next();
            long cntProd = rs.getLong(1);
            rs.close();

            dt = System.currentTimeMillis();
            fileToExport = "target/productnames-export-" + UUID.randomUUID().toString() + ".xml";
            bulkExportService.doExport(createContext("src/test/resources/export/xml/productnames.xml", listener, fileToExport));
            final long prods = System.currentTimeMillis() - dt;
            System.out.println(cntProd + " products in " + prods + "millis (~" + (prods / cntProd) + " per item)");


            xml = new File(fileToExport);
            content = FileUtils.readFileToString(xml, "UTF-8");
            assertTrue(content.contains("<product id=\""));
            assertTrue(content.contains("guid=\"SOBOT\" code=\"SOBOT\">"));
            assertTrue(content.contains("<name><![CDATA[Bender Bending Rodriguez]]></name>"));
            assertTrue(content.contains("<custom-value><![CDATA[sobot-picture.jpeg]]></custom-value>"));
            assertTrue(content.contains("<sku id=\""));
            assertTrue(content.contains("guid=\"SOBOT-BEER\" code=\"SOBOT-BEER\" product-code=\"SOBOT\" rank=\"1\""));

            validateXmlFile(xml);

            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TCATEGORY  ");
            rs.next();
            long cntCat = rs.getLong(1);
            rs.close();

            dt = System.currentTimeMillis();
            fileToExport = "target/categorynames-export-" + UUID.randomUUID().toString() + ".xml";
            bulkExportService.doExport(createContext("src/test/resources/export/xml/categorynames.xml", listener, fileToExport));
            final long cats = System.currentTimeMillis() - dt;
            System.out.println(cntCat + " categories in " + cats + "millis (~" + (cats / cntCat) + " per item)");


            xml = new File(fileToExport);
            content = FileUtils.readFileToString(xml, "UTF-8");
            assertTrue(content.contains("<category id=\""));
            assertTrue(content.contains(" guid=\"112\" rank=\"60\""));
            assertTrue(content.contains("<name><![CDATA[KnickKnacks]]></name>"));
            assertTrue(content.contains("<custom-value><![CDATA[10,20,50]]></custom-value>"));

            validateXmlFile(xml);

            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TSKUWAREHOUSE  ");
            rs.next();
            long cntSW = rs.getLong(1);
            rs.close();

            dt = System.currentTimeMillis();
            fileToExport = "target/inventory-export-" + UUID.randomUUID().toString() + ".xml";
            bulkExportService.doExport(createContext("src/test/resources/export/xml/inventory.xml", listener, fileToExport));
            final long inv = System.currentTimeMillis() - dt;
            System.out.println(cntSW + " inventory in " + inv + "millis (~" + (inv / cntSW) + " per item)");


            xml = new File(fileToExport);
            content = FileUtils.readFileToString(xml, "UTF-8");
            assertTrue(content.contains("<stock id=\""));
            assertTrue(content.contains(" sku=\"SOBOT-BEER\" warehouse=\"WAREHOUSE_1\">"));

            validateXmlFile(xml);



            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TSKUPRICE  ");
            rs.next();
            long cntSP = rs.getLong(1);
            rs.close();

            dt = System.currentTimeMillis();
            fileToExport = "target/pricelist-export-" + UUID.randomUUID().toString() + ".xml";
            bulkExportService.doExport(createContext("src/test/resources/export/xml/pricelists.xml", listener, fileToExport));
            final long price = System.currentTimeMillis() - dt;
            System.out.println(cntSP + " prices in " + price + "millis (~" + (price / cntSP) + " per item)");


            xml = new File(fileToExport);
            content = FileUtils.readFileToString(xml, "UTF-8");
            assertTrue(content.contains("sku=\"SOBOT-BEER\" shop=\"SHOIP1\" currency=\"EUR\" quantity=\"1.00\""));
            assertTrue(content.contains("<list-price>150.85</list-price>"));

            validateXmlFile(xml);



            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TPRODUCTTYPE  ");
            rs.next();
            long cntPtype = rs.getLong(1);
            rs.close();

            dt = System.currentTimeMillis();
            fileToExport = "target/producttypes-export-" + UUID.randomUUID().toString() + ".xml";
            bulkExportService.doExport(createContext("src/test/resources/export/xml/producttypenames.xml", listener, fileToExport));
            final long pTypes = System.currentTimeMillis() - dt;
            System.out.println(String.format("%5d", pTypes) + " product types in " + pTypes + "millis (~" + (pTypes / cntPtype) + " per item)");


            xml = new File(fileToExport);
            content = FileUtils.readFileToString(xml, "UTF-8");
            assertTrue(content.contains(" guid=\"SHOIP1_EUR_"));
            assertTrue(content.contains("<country>UA</country>"));

            validateXmlFile(xml);


            mockery.assertIsSatisfied();

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    private void validateXmlFile(final File xml) throws Exception {

        final String schemaLang = "http://www.w3.org/2001/XMLSchema";

        final SchemaFactory factory = SchemaFactory.newInstance(schemaLang);

        final Schema schema = factory.newSchema(new StreamSource(new FileInputStream(new File("src/main/resources/META-INF/schema/impex.xsd"))));
        final Validator validator = schema.newValidator();

        validator.validate(new StreamSource(new FileInputStream(xml)));
        
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

    @Factory
    public static Matcher<String> aStringStartingWith(String prefix) {
        return new StringStartsWithMatcher(prefix);
    }

    @Factory
    public static Matcher<String> aStringContains(String text) {
        return new StringContainsMatcher(text);
    }

}