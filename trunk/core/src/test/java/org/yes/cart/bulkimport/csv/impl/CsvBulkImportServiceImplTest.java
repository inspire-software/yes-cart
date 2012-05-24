package org.yes.cart.bulkimport.csv.impl;

import org.dbunit.AbstractDatabaseTester;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
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

    private static ApplicationContext sharedContext;

    /**
     * For bulk import to switch off lucene indexing
     * @return
     */
    /*protected synchronized ApplicationContext createContext2() {
        if (sharedContext == null) {
            sharedContext = new ClassPathXmlApplicationContext("testApplicationContext2.xml", "core-aspects.xml");
        }
        return sharedContext;
    } */

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
        allowedValue.add("HP Pavilion g6-1330eb");
        allowedValue.add("Lenovo ThinkPad Edge E530");
        allowedValue.add("Fujitsu LIFEBOOK T731");
        allowedValue.add("Samsung 3 Series 305E5A-A01");
        allowedValue.add("Acer TravelMate 5744-484G50MNKK");
        CsvFileReader csvFileReader = new CsvFileReaderImpl();

        csvFileReader.open(
                "src/test/resources/import/product_entity.csv",
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

            StringBuilder stringBuilder = new StringBuilder();
            Set<String> importedFilesSet = new HashSet<String>();

            BulkImportService bulkImportService = null;

            Date dt = new Date();
            bulkImportService = getBulkImportService("src/test/resources/import/brandimport.xml");
            bulkImportService.doImport(stringBuilder, importedFilesSet, null, "");
            System.out.println("brands " + (new Date().getTime() - dt.getTime()));

            dt = new Date();
            bulkImportService = getBulkImportService("src/test/resources/import/category.xml");
            bulkImportService.doImport(stringBuilder, importedFilesSet, null, "");
            System.out.println("category " + (new Date().getTime() - dt.getTime()));

            dt = new Date();
            bulkImportService = getBulkImportService("src/test/resources/import/availability.xml");
            bulkImportService.doImport(stringBuilder, importedFilesSet, null, "");
            System.out.println("availability " + (new Date().getTime() - dt.getTime()));

            dt = new Date();
            bulkImportService = getBulkImportService("src/test/resources/import/attribute.xml");
            bulkImportService.doImport(stringBuilder, importedFilesSet, null, "");
            System.out.println("attribute " + (new Date().getTime() - dt.getTime()));

            dt = new Date();
            bulkImportService = getBulkImportService("src/test/resources/import/attributeviewgroup.xml");
            bulkImportService.doImport(stringBuilder, importedFilesSet, null, "");
            System.out.println("attributeviewgroup.xml " + (new Date().getTime() - dt.getTime()));

            dt = new Date();
            bulkImportService = getBulkImportService("src/test/resources/import/product_type.xml");
            bulkImportService.doImport(stringBuilder, importedFilesSet, null, "");
            System.out.println("product_type.xml " + (new Date().getTime() - dt.getTime()));


            dt = new Date();
            bulkImportService = getBulkImportService("src/test/resources/import/productypeattributeviewgroup.xml");
            bulkImportService.doImport(stringBuilder, importedFilesSet, null, "");
            System.out.println("productypeattributeviewgroup.xml " + (new Date().getTime() - dt.getTime()));

            dt = new Date();
            bulkImportService = getBulkImportService("src/test/resources/import/producttypeattr.xml");
            bulkImportService.doImport(stringBuilder, importedFilesSet, null, "");
            System.out.println("producttypeattr.xml " + (new Date().getTime() - dt.getTime()));


            dt = new Date();
            bulkImportService = getBulkImportService("src/test/resources/import/product.xml");
            bulkImportService.doImport(stringBuilder, importedFilesSet, null, "");
            System.out.println("product.xml " + (new Date().getTime() - dt.getTime()));

            dt = new Date();
            bulkImportService = getBulkImportService("src/test/resources/import/product_sku.xml");
            bulkImportService.doImport(stringBuilder, importedFilesSet, null, "");
            System.out.println("attributeviewgroup.xml " + (new Date().getTime() - dt.getTime()));




            dt = new Date();
            bulkImportService = getBulkImportService("src/test/resources/import/warehouse.xml");
            bulkImportService.doImport(stringBuilder, importedFilesSet, null, "");
            System.out.println("warehouse.xml " + (new Date().getTime() - dt.getTime()));


            dt = new Date();
            bulkImportService = getBulkImportService("src/test/resources/import/skuquantity.xml");
            bulkImportService.doImport(stringBuilder, importedFilesSet, null, "");
            System.out.println("skuquantity.xml " + (new Date().getTime() - dt.getTime()));

            dt = new Date();
            bulkImportService = getBulkImportService("src/test/resources/import/skuprice.xml");
            bulkImportService.doImport(stringBuilder, importedFilesSet, null, "");
            System.out.println("skuprice.xml " + (new Date().getTime() - dt.getTime()));

            dt = new Date();
            bulkImportService = getBulkImportService("src/test/resources/import/product_category.xml");
            bulkImportService.doImport(stringBuilder, importedFilesSet, null, "");
            System.out.println("product_category.xml " + (new Date().getTime() - dt.getTime()));



            //assertTrue(stringBuilder.toString(), stringBuilder.toString().indexOf("ERROR") == -1);

        } catch (Exception e) {
            assertTrue(e.getMessage(), false);
        } finally {
            dumpDataBase("www", new String[]{"TATTRIBUTE", "TPRODUCTTYPE", "TPRODUCTTYPEATTR",
                    "TPRODUCT", "TSKU", "TPRODUCTATTRVALUE",
                    "TSKUWAREHOUSE", "TSKUPRICE", "TPRODUCTCATEGORY", "TCATEGORY",
                    "TATTRVIEWGROUP" ,
                    "TPRODTYPEATTRVIEWGROUP"
            });
        }

    }


    @Test
    public void testDoImportSimple() throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        Set<String> importedFilesSet = new HashSet<String>();
        BulkImportService bulkImportService = getBulkImportService("src/test/resources/import/brandimport.xml");
        bulkImportService.doImport(stringBuilder, importedFilesSet, null, "");
        try {
            //check the total qty of imported rows
            PreparedStatement pst = getConnection().getConnection().prepareStatement(
                    "select count(*) as cnt from TBRAND ");
            ResultSet rs = pst.executeQuery();
            rs.next();
            assertEquals(stringBuilder.toString(), 13, rs.getInt("cnt"));
            rs.close();
            pst.close();


        } catch (Exception e) {

            dumpDataBase(
                    "brand",
                    new String[]{"tbrand"}
            );
            
            e.printStackTrace();

            assertTrue(e.getMessage() + " " + stringBuilder.toString(), false);
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
