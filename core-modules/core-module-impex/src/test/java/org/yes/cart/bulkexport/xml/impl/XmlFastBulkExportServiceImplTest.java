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
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.bulkcommon.service.ExportService;
import org.yes.cart.bulkexport.model.ExportDescriptor;
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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * User: denispavlov
 * Date: 26/10/2018
 * Time: 08:19
 */
public class XmlFastBulkExportServiceImplTest extends BaseCoreDBTestCase {

    ExportService bulkExportService = null;
    XStreamProvider<ExportDescriptor> xml = null;

    private final Mockery mockery = new JUnit4Mockery();

    @Override
    @Before
    public void setUp()  {

        if (bulkExportService == null) {
            bulkExportService = createContext().getBean("xmlFastBulkExportService", ExportService.class);
            xml = createContext().getBean("exportXmlDescriptorXStreamProvider", XStreamProvider.class);
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
                allowing(listener).notifyPing(with(any(String.class)), with(any(Object[].class)));
                allowing(listener).notifyMessage(with(any(String.class)), with(any(Object[].class)));
            }});

            Set<String> importedFilesSet = new HashSet<>();

            ResultSet rs;
            String fileToExport;
            String content;
            File xml;
            long dt;

            rs = getConnection().getConnection().createStatement().executeQuery (
                    "select count(e.ETYPE_ID) as cnt from TETYPE e");
            rs.next();
            long cntEtypeCnt = rs.getLong("cnt");
            rs.close();

            dt = System.currentTimeMillis();
            fileToExport = "target/etypes-export-" + UUID.randomUUID().toString() + ".xml";
            bulkExportService.doExport(createContext("src/test/resources/export/xml/etypes.xml", listener, fileToExport));
            final long etypes = System.currentTimeMillis() - dt;
            System.out.println(String.format("%5d", cntEtypeCnt) + " e-types in " + etypes + "millis (~" + (etypes / cntEtypeCnt) + " per item)");

            xml = new File(fileToExport);
            content = FileUtils.readFileToString(xml, "UTF-8");
            assertTrue(content.contains("java-type=\"java.lang.String\""));

            validateXmlFile(xml);

            rs = getConnection().getConnection().createStatement().executeQuery (
                    "select count(g.ATTRIBUTEGROUP_ID) as cnt from TATTRIBUTEGROUP g");
            rs.next();
            long cntProductAttrGroup = rs.getLong("cnt");
            rs.close();

            dt = System.currentTimeMillis();
            fileToExport = "target/attributegroups-export-" + UUID.randomUUID().toString() + ".xml";
            bulkExportService.doExport(createContext("src/test/resources/export/xml/attributegroups.xml", listener, fileToExport));
            final long attrsGroups = System.currentTimeMillis() - dt;
            System.out.println(String.format("%5d", cntProductAttrGroup) + " attribute groups in " + attrsGroups + "millis (~" + (attrsGroups / cntProductAttrGroup) + " per item)");

            xml = new File(fileToExport);
            content = FileUtils.readFileToString(xml, "UTF-8");
            assertTrue(content.contains("code=\"PRODUCT\""));

            validateXmlFile(xml);


            rs = getConnection().getConnection().createStatement().executeQuery (
                    "select count(a.ATTRIBUTE_ID) as cnt from TATTRIBUTE a, TATTRIBUTEGROUP g where a.ATTRIBUTEGROUP_ID = g.ATTRIBUTEGROUP_ID and g.CODE = 'PRODUCT'");
            rs.next();
            long cntProductAttr = rs.getLong("cnt");
            rs.close();

            dt = System.currentTimeMillis();
            fileToExport = "target/attributes-export-" + UUID.randomUUID().toString() + ".xml";
            bulkExportService.doExport(createContext("src/test/resources/export/xml/attributes.xml", listener, fileToExport));
            final long attrs = System.currentTimeMillis() - dt;
            System.out.println(String.format("%5d", cntProductAttr) + " attributes in " + attrs + "millis (~" + (attrs / cntProductAttr) + " per item)");

            xml = new File(fileToExport);
            content = FileUtils.readFileToString(xml, "UTF-8");
            assertTrue(content.contains("group=\"PRODUCT\" etype=\"String\" rank=\"500\">"));

            validateXmlFile(xml);



            rs = getConnection().getConnection().createStatement().executeQuery (
                    "select count(b.BRAND_ID) as cnt from TBRAND b");
            rs.next();
            long cntBrandCnt = rs.getLong("cnt");
            rs.close();

            dt = System.currentTimeMillis();
            fileToExport = "target/brands-export-" + UUID.randomUUID().toString() + ".xml";
            bulkExportService.doExport(createContext("src/test/resources/export/xml/brands.xml", listener, fileToExport));
            final long brands = System.currentTimeMillis() - dt;
            System.out.println(String.format("%5d", cntBrandCnt) + " brands in " + brands + "millis (~" + (brands / cntBrandCnt) + " per item)");

            xml = new File(fileToExport);
            content = FileUtils.readFileToString(xml, "UTF-8");
            assertTrue(content.contains("<name><![CDATA[FutureRobots]]></name>"));

            validateXmlFile(xml);



            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TPRODUCT  ");
            rs.next();
            long cntProd = rs.getLong(1);
            rs.close();

            dt = System.currentTimeMillis();
            fileToExport = "target/products-export-" + UUID.randomUUID().toString() + ".xml";
            bulkExportService.doExport(createContext("src/test/resources/export/xml/products.xml", listener, fileToExport));
            final long prods = System.currentTimeMillis() - dt;
            System.out.println(String.format("%5d", cntProd) + " products in " + prods + "millis (~" + (prods / cntProd) + " per item)");


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
            fileToExport = "target/categories-export-" + UUID.randomUUID().toString() + ".xml";
            bulkExportService.doExport(createContext("src/test/resources/export/xml/categories.xml", listener, fileToExport));
            final long cats = System.currentTimeMillis() - dt;
            System.out.println(String.format("%5d", cntCat) + " categories in " + cats + "millis (~" + (cats / cntCat) + " per item)");


            xml = new File(fileToExport);
            content = FileUtils.readFileToString(xml, "UTF-8");
            assertTrue(content.contains("<category id=\""));
            assertTrue(content.contains(" guid=\"112\" rank=\"60\""));
            assertTrue(content.contains("<name><![CDATA[KnickKnacks]]></name>"));
            assertTrue(content.contains("<custom-value><![CDATA[10,20,50]]></custom-value>"));

            validateXmlFile(xml);

            dt = System.currentTimeMillis();
            fileToExport = "target/categorytree-export-" + UUID.randomUUID().toString() + ".xml";
            bulkExportService.doExport(createContext("src/test/resources/export/xml/categorytree.xml", listener, fileToExport));
            final long catsTree = System.currentTimeMillis() - dt;
            System.out.println(String.format("%5d", cntCat) + " category tree in " + catsTree + "millis (~" + (catsTree / cntCat) + " per item)");


            xml = new File(fileToExport);
            content = FileUtils.readFileToString(xml, "UTF-8");
            assertTrue(content.contains("<category id=\""));
            assertTrue(content.contains(" guid=\"101\" rank=\"10\""));
            assertTrue(content.contains("<description><![CDATA[Flying Machines]]></description>"));
            assertTrue(content.contains("<custom-value><![CDATA[10,20,50]]></custom-value>"));

            validateXmlFile(xml);


            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TCATEGORY  ");
            rs.next();
            long cntContent1 = rs.getLong(1);
            rs.close();

            dt = System.currentTimeMillis();
            fileToExport = "target/content_cms1-export-" + UUID.randomUUID().toString() + ".xml";
            bulkExportService.doExport(createContext("src/test/resources/export/xml/content_cms1.xml", listener, fileToExport));
            final long cms1 = System.currentTimeMillis() - dt;
            System.out.println(String.format("%5d", cntContent1) + " content/1 in " + cms1 + "millis (~" + (cms1 / cntContent1) + " per item)");


            xml = new File(fileToExport);
            content = FileUtils.readFileToString(xml, "UTF-8");
            assertTrue(content.contains("<content id=\""));
            assertTrue(content.contains(" guid=\"112\" rank=\"60\""));
            assertTrue(content.contains("<name><![CDATA[KnickKnacks]]></name>"));
            assertTrue(content.contains("<custom-value><![CDATA[10,20,50]]></custom-value>"));

            validateXmlFile(xml);



            dt = System.currentTimeMillis();
            fileToExport = "target/shopcontent_cms1-export-" + UUID.randomUUID().toString() + ".xml";
            bulkExportService.doExport(createContext("src/test/resources/export/xml/shopcontent_cms1.xml", listener, fileToExport));
            final long scms1 = System.currentTimeMillis() - dt;
            System.out.println(String.format("%5d", cntContent1) + " shop content/1 in " + scms1 + "millis (~" + (scms1 / cntContent1) + " per item)");


            xml = new File(fileToExport);
            content = FileUtils.readFileToString(xml, "UTF-8");
            assertTrue(content.contains("<content id=\""));
            assertTrue(content.contains(" guid=\"SHOIP1\" rank=\"0\" shop=\"SHOIP1\""));
            assertTrue(content.contains("<name><![CDATA[SHOIP1]]></name>"));
            assertTrue(content.contains("<uri><![CDATA[SHOIP1_mail_customer-registered.html]]></uri>"));

            validateXmlFile(xml);


            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TCONTENT  ");
            rs.next();
            long cntContent3 = rs.getLong(1);
            rs.close();

            dt = System.currentTimeMillis();
            fileToExport = "target/content_cms3-export-" + UUID.randomUUID().toString() + ".xml";
            bulkExportService.doExport(createContext("src/test/resources/export/xml/content_cms3.xml", listener, fileToExport));
            final long cms3 = System.currentTimeMillis() - dt;
            System.out.println(String.format("%5d", cntContent3) + " content/3 in " + cms3 + "millis (~" + (cms3 / cntContent3) + " per item)");


            xml = new File(fileToExport);
            content = FileUtils.readFileToString(xml, "UTF-8");
            assertTrue(content.contains("<content id=\""));
            assertTrue(content.contains(" guid=\"SHOIP1_email_cr.html\" rank=\"0\" shop=\"SHOIP1\""));
            assertTrue(content.contains("<uri><![CDATA[SHOIP1_mail_customer-registered.html]]></uri>"));
            assertTrue(content.contains("<custom-value><![CDATA[6,12,24]]></custom-value>"));

            validateXmlFile(xml);


            dt = System.currentTimeMillis();
            fileToExport = "target/shopcontent_cms3-export-" + UUID.randomUUID().toString() + ".xml";
            bulkExportService.doExport(createContext("src/test/resources/export/xml/shopcontent_cms3.xml", listener, fileToExport));
            final long scms3 = System.currentTimeMillis() - dt;
            System.out.println(String.format("%5d", cntContent3) + " content/3 in " + scms3 + "millis (~" + (scms3 / cntContent3) + " per item)");


            xml = new File(fileToExport);
            content = FileUtils.readFileToString(xml, "UTF-8");
            assertTrue(content.contains(" guid=\"SHOIP1\" rank=\"0\" shop=\"SHOIP1\""));
            assertTrue(content.contains("<name><![CDATA[SHOIP1]]></name>"));
            assertTrue(content.contains("<uri><![CDATA[SHOIP1_mail_customer-registered.html]]></uri>"));

            validateXmlFile(xml);



            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TSKUWAREHOUSE  ");
            rs.next();
            long cntSW = rs.getLong(1);
            rs.close();

            dt = System.currentTimeMillis();
            fileToExport = "target/inventory-export-" + UUID.randomUUID().toString() + ".xml";
            bulkExportService.doExport(createContext("src/test/resources/export/xml/inventory.xml", listener, fileToExport));
            final long inv = System.currentTimeMillis() - dt;
            System.out.println(String.format("%5d", cntSW) + " inventory in " + inv + "millis (~" + (inv / cntSW) + " per item)");


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
            System.out.println(String.format("%5d", cntSP) + " prices in " + price + "millis (~" + (price / cntSP) + " per item)");


            xml = new File(fileToExport);
            content = FileUtils.readFileToString(xml, "UTF-8");
            assertTrue(content.contains("sku=\"SOBOT-BEER\" shop=\"SHOIP1\" currency=\"EUR\" quantity=\"1.00\""));
            assertTrue(content.contains("<list-price>150.85</list-price>"));

            validateXmlFile(xml);


            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TTAX  ");
            rs.next();
            long cntTax = rs.getLong(1);
            rs.close();

            dt = System.currentTimeMillis();
            fileToExport = "target/taxes-export-" + UUID.randomUUID().toString() + ".xml";
            bulkExportService.doExport(createContext("src/test/resources/export/xml/taxes.xml", listener, fileToExport));
            final long tax = System.currentTimeMillis() - dt;
            System.out.println(String.format("%5d", tax) + " taxes in " + tax + "millis (~" + (tax / cntTax) + " per item)");


            xml = new File(fileToExport);
            content = FileUtils.readFileToString(xml, "UTF-8");
            assertTrue(content.contains("code=\"VAT\" shop=\"SHOIP1\" currency=\"EUR\""));
            assertTrue(content.contains("<rate gross=\"false\">20.00</rate>"));

            validateXmlFile(xml);


            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TTAXCONFIG  ");
            rs.next();
            long cntTaxCfg = rs.getLong(1);
            rs.close();

            dt = System.currentTimeMillis();
            fileToExport = "target/taxconfigs-export-" + UUID.randomUUID().toString() + ".xml";
            bulkExportService.doExport(createContext("src/test/resources/export/xml/taxconfigs.xml", listener, fileToExport));
            final long taxCfg = System.currentTimeMillis() - dt;
            System.out.println(String.format("%5d", cntTaxCfg) + " tax configs in " + taxCfg + "millis (~" + (taxCfg / cntTaxCfg) + " per item)");


            xml = new File(fileToExport);
            content = FileUtils.readFileToString(xml, "UTF-8");
            assertTrue(content.contains(" guid=\"SHOIP1_EUR_"));
            assertTrue(content.contains("<tax-region iso-3166-1-alpha2=\"US\" region-code=\"US-US\"/>"));

            validateXmlFile(xml);


            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TPRODUCTTYPE  ");
            rs.next();
            long cntPtype = rs.getLong(1);
            rs.close();

            dt = System.currentTimeMillis();
            fileToExport = "target/producttypes-export-" + UUID.randomUUID().toString() + ".xml";
            bulkExportService.doExport(createContext("src/test/resources/export/xml/producttypes.xml", listener, fileToExport));
            final long pTypes = System.currentTimeMillis() - dt;
            System.out.println(String.format("%5d", cntPtype) + " product types in " + pTypes + "millis (~" + (pTypes / cntPtype) + " per item)");


            xml = new File(fileToExport);
            content = FileUtils.readFileToString(xml, "UTF-8");
            assertTrue(content.contains("<name>Power supply</name>"));
            assertTrue(content.contains("<description><![CDATA[Robots]]></description>"));
            assertTrue(content.contains("<navigation type=\"R\">"));
            assertTrue(content.contains("<range-list>"));
            assertTrue(content.contains("<from>0.10</from>"));

            validateXmlFile(xml);


            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TSYSTEMATTRVALUE  ");
            rs.next();
            long cntSysPrefs = rs.getLong(1);
            rs.close();

            dt = System.currentTimeMillis();
            fileToExport = "target/systempreferences_export-" + UUID.randomUUID().toString() + ".xml";
            bulkExportService.doExport(createContext("src/test/resources/export/xml/systempreferences.xml", listener, fileToExport));
            final long sysPrefs = System.currentTimeMillis() - dt;
            System.out.println(String.format("%5d", cntSysPrefs) + " system preferences in " + sysPrefs + "millis (~" + (sysPrefs / cntSysPrefs) + " per item)");


            xml = new File(fileToExport);
            content = FileUtils.readFileToString(xml, "UTF-8");
            assertTrue(content.contains("<name>YesCart e-commerce system</name>"));
            assertTrue(content.contains("guid=\"1056_TSYSTEMATTRVALUE\" attribute=\"IMPORT_JOB_TIMEOUT_MS\""));

            validateXmlFile(xml);



            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TDATAGROUP  ");
            rs.next();
            long cntDataGrp = rs.getLong(1);
            rs.close();

            dt = System.currentTimeMillis();
            fileToExport = "target/datagroups_export-" + UUID.randomUUID().toString() + ".xml";
            bulkExportService.doExport(createContext("src/test/resources/export/xml/datagroups.xml", listener, fileToExport));
            final long dataGrps = System.currentTimeMillis() - dt;
            System.out.println(String.format("%5d", cntDataGrp) + " data groups in " + dataGrps + "millis (~" + (dataGrps / cntDataGrp) + " per item)");


            xml = new File(fileToExport);
            content = FileUtils.readFileToString(xml, "UTF-8");
            assertTrue(content.contains("data-descriptor id=\"1000\" name=\"customer/productandcategorynamesimport.xml\""));
            assertTrue(content.contains("<value><![CDATA[customer/productskuimport.xml]]></value>"));

            validateXmlFile(xml);




            rs = getConnection().getConnection().createStatement().executeQuery (
                    "select count(c.COUNTRY_ID) as cnt from TCOUNTRY c");
            rs.next();
            long cntCountryCnt = rs.getLong("cnt");
            rs.close();

            dt = System.currentTimeMillis();
            fileToExport = "target/countries-export-" + UUID.randomUUID().toString() + ".xml";
            bulkExportService.doExport(createContext("src/test/resources/export/xml/countries.xml", listener, fileToExport));
            final long countries = System.currentTimeMillis() - dt;
            System.out.println(String.format("%5d", cntCountryCnt) + " countries in " + countries + "millis (~" + (countries / cntCountryCnt) + " per item)");

            xml = new File(fileToExport);
            content = FileUtils.readFileToString(xml, "UTF-8");
            assertTrue(content.contains("iso-3166-1-alpha2=\"AU\" iso-3166-1-numeric=\"36\" name=\"Australia\""));

            validateXmlFile(xml);

            rs = getConnection().getConnection().createStatement().executeQuery (
                    "select count(c.STATE_ID) as cnt from TSTATE c");
            rs.next();
            long cntCountryStateCnt = rs.getLong("cnt");
            rs.close();

            dt = System.currentTimeMillis();
            fileToExport = "target/countrystates-export-" + UUID.randomUUID().toString() + ".xml";
            bulkExportService.doExport(createContext("src/test/resources/export/xml/countrystates.xml", listener, fileToExport));
            final long countryStates = System.currentTimeMillis() - dt;
            System.out.println(String.format("%5d", cntCountryStateCnt) + " country states in " + countryStates + "millis (~" + (countryStates / cntCountryStateCnt) + " per item)");

            xml = new File(fileToExport);
            content = FileUtils.readFileToString(xml, "UTF-8");
            assertTrue(content.contains("iso-3166-1-alpha2=\"AU\" region-code=\"AU-AU\" name=\"Australia\""));

            validateXmlFile(xml);




            rs = getConnection().getConnection().createStatement().executeQuery (
                    "select count(w.WAREHOUSE_ID) as cnt from TWAREHOUSE w");
            rs.next();
            long cntFcCnt = rs.getLong("cnt");
            rs.close();

            dt = System.currentTimeMillis();
            fileToExport = "target/fulfilmentcentres-export-" + UUID.randomUUID().toString() + ".xml";
            bulkExportService.doExport(createContext("src/test/resources/export/xml/fulfilmentcentres.xml", listener, fileToExport));
            final long fcs = System.currentTimeMillis() - dt;
            System.out.println(String.format("%5d", cntFcCnt) + " fulfilment centres in " + fcs + "millis (~" + (fcs / cntFcCnt) + " per item)");

            xml = new File(fileToExport);
            content = FileUtils.readFileToString(xml, "UTF-8");
            assertTrue(content.contains("<name><![CDATA[First warehouse]]></name>"));

            validateXmlFile(xml);




            rs = getConnection().getConnection().createStatement().executeQuery (
                    "select count(c.CARRIER_ID) as cnt from TCARRIER c");
            rs.next();
            long cntSpCnt = rs.getLong("cnt");
            rs.close();

            dt = System.currentTimeMillis();
            fileToExport = "target/shippingproviders-export-" + UUID.randomUUID().toString() + ".xml";
            bulkExportService.doExport(createContext("src/test/resources/export/xml/shippingproviders.xml", listener, fileToExport));
            final long sps = System.currentTimeMillis() - dt;
            System.out.println(String.format("%5d", cntSpCnt) + " shipping providers + methods in " + sps + "millis (~" + (sps / cntSpCnt) + " per item)");

            xml = new File(fileToExport);
            content = FileUtils.readFileToString(xml, "UTF-8");
            assertTrue(content.contains("<name><![CDATA[Test carrier 1]]></name>"));
            assertTrue(content.contains("<configuration type=\"F\" guaranteed-delivery=\"false\" named-day-delivery=\"false\" billing-address-not-required=\"false\" delivery-address-not-required=\"false\"/>"));

            validateXmlFile(xml);


            rs = getConnection().getConnection().createStatement().executeQuery (
                    "select count(c.PROMOTION_ID) as cnt from TPROMOTION c");
            rs.next();
            long cntPromoCnt = rs.getLong("cnt");
            rs.close();

            dt = System.currentTimeMillis();
            fileToExport = "target/promotions-export-" + UUID.randomUUID().toString() + ".xml";
            bulkExportService.doExport(createContext("src/test/resources/export/xml/promotions.xml", listener, fileToExport));
            final long promos = System.currentTimeMillis() - dt;
            System.out.println(String.format("%5d", cntPromoCnt) + " promotions + coupons in " + promos + "millis (~" + (promos / cntPromoCnt) + " per item)");

            xml = new File(fileToExport);
            content = FileUtils.readFileToString(xml, "UTF-8");
            assertTrue(content.contains("<name><![CDATA[Promo 001]]></name>"));
            assertTrue(content.contains("<configuration action=\"F\" type=\"I\" coupon-triggered=\"true\" can-be-combined=\"true\"><![CDATA[true]]></configuration>"));
            assertTrue(content.contains("guid=\"PROMO001-001\" code=\"PROMO001-001\" promotion=\"PROMO001\" usage-count=\"0\">"));
            assertTrue(content.contains("<configuration max-limit=\"100\" customer-limit=\"1\"/>"));

            validateXmlFile(xml);



            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TMANAGER  ");
            rs.next();
            long cntMgr = rs.getLong(1);
            rs.close();

            dt = System.currentTimeMillis();
            fileToExport = "target/organisationusers-export-" + UUID.randomUUID().toString() + ".xml";
            bulkExportService.doExport(createContext("src/test/resources/export/xml/organisationusers.xml", listener, fileToExport));
            final long mgr = System.currentTimeMillis() - dt;
            System.out.println(String.format("%5d", cntMgr) + " managers in " + mgr + "millis (~" + (mgr / cntMgr) + " per item)");


            xml = new File(fileToExport);
            content = FileUtils.readFileToString(xml, "UTF-8");
            assertTrue(content.contains("organisation-user id=\"10001\" guid=\"10001\""));
            assertTrue(content.contains("<company-name-1>ABC</company-name-1>"));

            validateXmlFile(xml);



            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TADDRESS  ");
            rs.next();
            long cntAddr = rs.getLong(1);
            rs.close();

            dt = System.currentTimeMillis();
            fileToExport = "target/addresses-export-" + UUID.randomUUID().toString() + ".xml";
            bulkExportService.doExport(createContext("src/test/resources/export/xml/addresses.xml", listener, fileToExport));
            final long addr = System.currentTimeMillis() - dt;
            System.out.println(String.format("%5d", cntAddr) + " addresses in " + addr + "millis (~" + (addr / cntAddr) + " per item)");


            xml = new File(fileToExport);
            content = FileUtils.readFileToString(xml, "UTF-8");
            assertTrue(content.contains("<address id=\"10101\" guid=\"10101\" address-type=\"S\" default-address=\"true\" customer-code=\"10001\" customer-email=\"reg@test.com\">"));
            assertTrue(content.contains("<address id=\"20101\" guid=\"20101\" address-type=\"B\" default-address=\"true\" shop-code=\"SHOIP\">"));

            validateXmlFile(xml);


            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TCUSTOMER  ");
            rs.next();
            long cntCust = rs.getLong(1);
            rs.close();

            dt = System.currentTimeMillis();
            fileToExport = "target/customers-export-" + UUID.randomUUID().toString() + ".xml";
            bulkExportService.doExport(createContext("src/test/resources/export/xml/customers.xml", listener, fileToExport));
            final long cust = System.currentTimeMillis() - dt;
            System.out.println(String.format("%5d", cntCust) + " customers in " + cust + "millis (~" + (cust / cntCust) + " per item)");


            xml = new File(fileToExport);
            content = FileUtils.readFileToString(xml, "UTF-8");
            assertTrue(content.contains("<customer id=\"10001\" guid=\"10001\">"));
            assertTrue(content.contains("<shop code=\"SHOIP1\" enabled=\"true\"/>"));
            assertTrue(content.contains("<shop code=\"SHOIP2\" enabled=\"false\"/>"));
            assertTrue(content.contains("<firstname>John</firstname>"));
            assertTrue(content.contains("<pricing-policy>TEST</pricing-policy>"));
            assertTrue(content.contains("<address id=\"10101\" guid=\"10101\" address-type=\"S\" default-address=\"true\" customer-code=\"10001\" customer-email=\"reg@test.com\">"));
            assertTrue(content.contains("<wishlist-item id=\"10101\" guid=\"10101\" wishlist-type=\"W\" visibility=\"P\" sku-code=\"BACKORDER-BACK-TO-FLOW1\">"));
            assertTrue(content.contains("<price currency=\"GBP\" quantity=\"2.00\">"));
            assertTrue(content.contains("<list-price>9.99</list-price>"));

            validateXmlFile(xml);





            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TSHOP  ");
            rs.next();
            long cntShop = rs.getLong(1);
            rs.close();

            dt = System.currentTimeMillis();
            fileToExport = "target/shops-export-" + UUID.randomUUID().toString() + ".xml";
            bulkExportService.doExport(createContext("src/test/resources/export/xml/shops.xml", listener, fileToExport));
            final long shop = System.currentTimeMillis() - dt;
            System.out.println(String.format("%5d", cntShop) + " shops in " + shop + "millis (~" + (shop / cntShop) + " per item)");


            xml = new File(fileToExport);
            content = FileUtils.readFileToString(xml, "UTF-8");
            assertTrue(content.contains("<shop id=\"10\" guid=\"SHOIP1\" code=\"SHOIP1\">"));
            assertTrue(content.contains("<description><![CDATA[Gadget universe shop]]></description>"));
            assertTrue(content.contains("<shop-url primary=\"false\" domain=\"www.gadget.yescart.org\"/>"));
            assertTrue(content.contains("<shop-category id=\"140\" guid=\"140\" rank=\"5\"/>"));
            assertTrue(content.contains("<shop-carrier id=\"1\" guid=\"1_CARRIER\" disabled=\"false\"/>"));
            assertTrue(content.contains("<shop-fulfilment-centre id=\"1\" guid=\"WAREHOUSE_1\" disabled=\"false\"/>"));
            assertTrue(content.contains("<address id=\"20101\" guid=\"20101\" address-type=\"B\" default-address=\"true\" shop-code=\"SHOIP\">"));
            assertTrue(content.contains("<custom-attribute id=\"10\" guid=\"10_CURRENCY\" attribute=\"CURRENCY\">"));

            validateXmlFile(xml);



            rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TCUSTOMERORDER  ");
            rs.next();
            long cntOrders = rs.getLong(1);
            rs.close();

            dt = System.currentTimeMillis();
            fileToExport = "target/customerorders-export-" + UUID.randomUUID().toString() + ".xml";
            bulkExportService.doExport(createContext("src/test/resources/export/xml/customerorders.xml", listener, fileToExport));
            final long orders = System.currentTimeMillis() - dt;
            System.out.println(String.format("%5d", cntOrders) + " customer orders in " + orders + "millis (~" + (orders / cntOrders) + " per item)");


            xml = new File(fileToExport);
            content = FileUtils.readFileToString(xml, "UTF-8");
            assertTrue(content.contains("<customer-order id=\"10001\" guid=\"5ef4a3eb-d0b4-417e-8ff6-7624a8667fa5\" order-number=\"190323063746-1\" shop-code=\"SHOIP1\">"));
            assertTrue(content.contains("<payment-gateway>testPaymentGatewayLabel</payment-gateway>"));
            assertTrue(content.contains("<status>os.in.progress</status>"));
            assertTrue(content.contains("<email>reg@test.com</email>"));
            assertTrue(content.contains("<formatted><![CDATA[line1 shipping addr  Vancouver CA ,  John  Doe, 555-55-51  ]]></formatted>"));
            assertTrue(content.contains("<city>Vancouver</city>"));
            assertTrue(content.contains("<price>983.94</price>"));
            assertTrue(content.contains("<code>PROMO001</code>"));
            assertTrue(content.contains("<code>PROMO001-001</code>"));
            assertTrue(content.contains("<reference>B2B00001</reference>"));
            assertTrue(content.contains("<custom-attribute attribute=\"310\">"));
            assertTrue(content.contains("<custom-value><![CDATA[3100133903]]></custom-value>"));
            assertTrue(content.contains("<i18n lang=\"xx\"><![CDATA[AUDITEXPORT]]></i18n>"));

            validateXmlFile(xml);





            mockery.assertIsSatisfied();

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }


    @Test
    public void testDoExportSite() throws Exception {
        try {

            final JobStatusListener listener = mockery.mock(JobStatusListener.class, "listener");

            mockery.checking(new Expectations() {{
                // ONLY allow messages during import
                allowing(listener).notifyPing();
                allowing(listener).notifyPing(with(any(String.class)), with(any(Object[].class)));
                allowing(listener).notifyMessage(with(any(String.class)), with(any(Object[].class)));
            }});

            long dt = System.currentTimeMillis();
            String fileToExport = "target/site-export-" + UUID.randomUUID().toString() + ".zip";
            bulkExportService.doExport(createContext("src/test/resources/export/xml/site.xml", listener, fileToExport));
            final long shop = System.currentTimeMillis() - dt;
            System.out.println(String.format("%5d", 1) + " shop in " + shop + "millis (~" + (shop) + " per item)");



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