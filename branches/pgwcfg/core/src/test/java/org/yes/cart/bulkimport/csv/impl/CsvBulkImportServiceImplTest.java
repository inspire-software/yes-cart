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

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.bulkimport.csv.CsvFileReader;
import org.yes.cart.bulkimport.service.BulkImportService;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 12/11/11
 * Time: 10:20 AM
 */
public class CsvBulkImportServiceImplTest extends BaseCoreDBTestCase {


    BulkImportService bulkImportService = null;


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

            StringBuilder stringBuilder = new StringBuilder();
            Set<String> importedFilesSet = new HashSet<String>();

            BulkImportService bulkImportService = null;

            long dt = System.currentTimeMillis();
            bulkImportService = getBulkImportService("src/test/resources/import/brandimport.xml");
            bulkImportService.doImport(stringBuilder, importedFilesSet, null, "");
            System.out.println("brands in " + (System.currentTimeMillis() - dt) + "millis");


            dt = System.currentTimeMillis();
            bulkImportService = getBulkImportService("src/test/resources/import/attribute.xml");
            bulkImportService.doImport(stringBuilder, importedFilesSet, null, "");
            System.out.println("attribute  in " + (System.currentTimeMillis() - dt) + "millis");

            /*dt = new Date();
            bulkImportService = getBulkImportService("src/test/resources/import/attributeviewgroup.xml");
            bulkImportService.doImport(stringBuilder, importedFilesSet, null, "");
            System.out.println("attributeviewgroup.xml " + (new Date().getTime() - dt.getTime())); */

            dt = System.currentTimeMillis();
            bulkImportService = getBulkImportService("src/test/resources/import/producttype.xml");
            bulkImportService.doImport(stringBuilder, importedFilesSet, null, "");
            System.out.println("producttype in " + (System.currentTimeMillis() - dt) + "millis");


            dt = System.currentTimeMillis();
            bulkImportService = getBulkImportService("src/test/resources/import/category.xml");
            bulkImportService.doImport(stringBuilder, importedFilesSet, null, "");
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
            bulkImportService.doImport(stringBuilder, importedFilesSet, null, "");
            System.out.println("shopcategory in " + (System.currentTimeMillis() - dt) + "millis");

            dt = System.currentTimeMillis();
            bulkImportService = getBulkImportService("src/test/resources/import/availability.xml");
            bulkImportService.doImport(stringBuilder, importedFilesSet, null, "");
            System.out.println("availability in " + (System.currentTimeMillis() - dt) + "millis");

            dt = System.currentTimeMillis();
            bulkImportService = getBulkImportService("src/test/resources/import/productypeattributeviewgroup.xml");
            bulkImportService.doImport(stringBuilder, importedFilesSet, null, "");
            System.out.println("productypeattributeviewgroup in " + (System.currentTimeMillis() - dt) + "millis");

             rs = getConnection().getConnection().createStatement().executeQuery ("select count(*) from TPRODTYPEATTRVIEWGROUP  ");
            rs.next();
            long cnt = rs.getLong(1);
            assertEquals("181 view group configured per all types" , 182, cnt );
            rs.close();




            dt = System.currentTimeMillis();
            bulkImportService = getBulkImportService("src/test/resources/import/producttypeattr.xml");
            bulkImportService.doImport(stringBuilder, importedFilesSet, null, "");
            System.out.println("producttypeattr in " + (System.currentTimeMillis() - dt) + "millis");


            dt = System.currentTimeMillis();
            bulkImportService = getBulkImportService("src/test/resources/import/product.xml");
            bulkImportService.doImport(stringBuilder, importedFilesSet, null, "");
            System.out.println("product in " + (System.currentTimeMillis() - dt) + "millis");

            dt = System.currentTimeMillis();
            bulkImportService = getBulkImportService("src/test/resources/import/productsku.xml");
            bulkImportService.doImport(stringBuilder, importedFilesSet, null, "");
            System.out.println("attributeviewgroup in " + (System.currentTimeMillis() - dt) + "millis");




            dt = System.currentTimeMillis();
            bulkImportService = getBulkImportService("src/test/resources/import/warehouse.xml");
            bulkImportService.doImport(stringBuilder, importedFilesSet, null, "");
            System.out.println("warehouse in " + (System.currentTimeMillis() - dt) + "millis");


            dt = System.currentTimeMillis();
            bulkImportService = getBulkImportService("src/test/resources/import/skuquantity.xml");
            bulkImportService.doImport(stringBuilder, importedFilesSet, null, "");
            System.out.println("skuquantity in " + (System.currentTimeMillis() - dt) + "millis");

            dt = System.currentTimeMillis();
            bulkImportService = getBulkImportService("src/test/resources/import/skuprice.xml");
            bulkImportService.doImport(stringBuilder, importedFilesSet, null, "");
            System.out.println("skuprice in " + (System.currentTimeMillis() - dt) + "millis");

            dt = System.currentTimeMillis();
            bulkImportService = getBulkImportService("src/test/resources/import/productcategory.xml");
            bulkImportService.doImport(stringBuilder, importedFilesSet, null, "");
            System.out.println("productcategory in in " + (System.currentTimeMillis() - dt) + "millis");

            //System.out.println(stringBuilder.toString());



            //assertTrue(stringBuilder.toString(), stringBuilder.toString().indexOf("ERROR") == -1);

        } catch (Exception e) {
            assertTrue(e.getMessage(), false);
        } finally {
            dumpDataBase("www", new String[]{"TATTRIBUTE", "TPRODUCTTYPE", "TPRODUCTTYPEATTR",
                    "TPRODUCT", "TSKU", "TPRODUCTATTRVALUE",
                    "TSKUWAREHOUSE", "TSKUPRICE", "TPRODUCTCATEGORY", "TCATEGORY",
                    "TPRODTYPEATTRVIEWGROUP" ,
                    "TSHOPCATEGORY"
            });
        }

    }


    @Test
    public void testDoImportWithForeignKeys() {
        StringBuilder stringBuilder = new StringBuilder();
        Set<String> importedFilesSet = new HashSet<String>();

        BulkImportService bulkImportService = getBulkImportService("src/test/resources/import/carrier.xml");
        bulkImportService.doImport(stringBuilder, importedFilesSet, null, "");

        bulkImportService = getBulkImportService("src/test/resources/import/carriersla.xml");
        bulkImportService.doImport(stringBuilder, importedFilesSet, null, "");

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
            assertEquals(stringBuilder.toString(), 4, rs.getInt("cnt"));
            rs.close();
            pst.close();

            //check that new valuki express sla not imported because fk constraint
            String errorReport = stringBuilder.toString();
            assertTrue(errorReport.indexOf("NEWVASUKIEXPRESS") > -1);


        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(e.getMessage(), false);
        }

    }

    @Test
    public void testDoImportWithSimpleSlaveFiled() {
        StringBuilder stringBuilder = new StringBuilder();
        Set<String> importedFilesSet = new HashSet<String>();

        BulkImportService bulkImportService = getBulkImportService("src/test/resources/import/shop.xml");
        bulkImportService.doImport(stringBuilder, importedFilesSet, null, "");

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
            assertTrue(e.getMessage() + " \n\n\n" + stringBuilder.toString(), false);

        }

    }
}
