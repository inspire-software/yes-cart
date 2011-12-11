package org.yes.cart.bulkimport.csv.impl;

import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.bulkimport.service.BulkImportService;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

    private BulkImportService getBulkImportService(final String pathToDescriptor) {
        BulkImportService bulkImportService = (BulkImportService) createContext().getBean("bulkImportServiceImpl");
        bulkImportService.setPathToImportDescriptor(pathToDescriptor);
        return bulkImportService;

    }

    @Test
    public void testDoProductImportWithSimpleSlaveFiled() {
        try {

            //Каблучка

            StringBuilder stringBuilder = new StringBuilder();
            Set<String> importedFilesSet = new HashSet<String>();

            BulkImportService bulkImportService = null;

            bulkImportService = getBulkImportService("src/test/resources/import/brandimport.xml");
            bulkImportService.doImport(stringBuilder, importedFilesSet, null, "");

            bulkImportService = getBulkImportService("src/test/resources/import/availability.xml");
            bulkImportService.doImport(stringBuilder, importedFilesSet, null, "");


            bulkImportService = getBulkImportService("src/test/resources/import/product_type.xml");
            bulkImportService.doImport(stringBuilder, importedFilesSet, null, "");


            bulkImportService = getBulkImportService("src/test/resources/import/product.xml");
            bulkImportService.doImport(stringBuilder, importedFilesSet, null, "");

            bulkImportService = getBulkImportService("src/test/resources/import/product_sku.xml");
            bulkImportService.doImport(stringBuilder, importedFilesSet, null, "");

            bulkImportService = getBulkImportService("src/test/resources/import/warehouse.xml");
            bulkImportService.doImport(stringBuilder, importedFilesSet, null, "");

            bulkImportService = getBulkImportService("src/test/resources/import/skuquantity.xml");
            bulkImportService.doImport(stringBuilder, importedFilesSet, null, "");

            bulkImportService = getBulkImportService("src/test/resources/import/skuprice.xml");
            bulkImportService.doImport(stringBuilder, importedFilesSet, null, "");

            bulkImportService = getBulkImportService("src/test/resources/import/product_category.xml");
            bulkImportService.doImport(stringBuilder, importedFilesSet, null, "");

            dumpDataBase(
                    "product",
                    new String[]{"tproduct", "tsku", "tattribute",
                            "tproductattrvalue", "tproductskuattrvalue", "tproducttype",
                            "tbrand", "twarehouse", "tskuwarehouse",
                            "tskuprice", "tproductcategory"}
            );

            assertTrue(stringBuilder.toString(), stringBuilder.toString().indexOf("ERROR") == -1);

        } catch (Exception e) {
            assertTrue(e.getMessage(), false);
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
                    "select count(*) as cnt from tbrand where name like 'imported brand name%'");
            ResultSet rs = pst.executeQuery();
            rs.next();
            assertEquals( stringBuilder.toString(), 7, rs.getInt("cnt"));
            rs.close();
            pst.close();

            //check that duplicate was updated duiring impor
            pst = getConnection().getConnection().prepareStatement(
                    "select description from tbrand where name = 'imported brand name2'");
            rs = pst.executeQuery();
            rs.next();
            assertEquals("imported brand description two", rs.getString("description"));
            rs.close();
            pst.close();



        } catch (Exception e) {

            dumpDataBase(
                    "brand",
                    new String[]{"tbrand"}
            );

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
            //nothing
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
