package org.yes.cart.icecat.transform

import org.yes.cart.icecat.transform.xml.CategoryHandler
import org.yes.cart.icecat.transform.domain.Category
import javax.xml.parsers.SAXParserFactory
import org.xml.sax.InputSource
import org.yes.cart.icecat.transform.xml.ProductPointerHandler
import org.yes.cart.icecat.transform.xml.CategoryFeaturesListHandler
import org.yes.cart.icecat.transform.xml.ProductHandler

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 5/7/12
 * Time: 10:11 PM
 */
class CategoryWalker {

    Context context;

    CategoryWalker(Context context) {

        this.context = context;

    }


    void collectData() {

        def FileInputStream refs = new FileInputStream("$context.dataDirectory/export/freexml.int/refs.xml");
        def handler = new CategoryHandler(context.categories, context.langId.toString())
        def reader = SAXParserFactory.newInstance().newSAXParser().XMLReader
        reader.setContentHandler(handler)
        reader.parse(new InputSource(refs))
        refs.close();


        refs = new FileInputStream("$context.dataDirectory/export/freexml.int/refs.xml");
        def categoryFeaturelistHandler = new CategoryFeaturesListHandler(
                handler.categoryList,
                handler.categoryIdFiler,
                context.langId.toString())
        reader = SAXParserFactory.newInstance().newSAXParser().XMLReader
        reader.setContentHandler(categoryFeaturelistHandler)
        reader.parse(new InputSource(refs))
        refs.close();


        def indexis = new FileInputStream("$context.dataDirectory/export/freexml.int/$context.productDir/index.html");
        def productPointerHandler = new ProductPointerHandler(handler.categoryList);
        def productReadeReader = SAXParserFactory.newInstance().newSAXParser().XMLReader
        productReadeReader.setContentHandler(productPointerHandler)
        productReadeReader.parse(new InputSource(indexis))

        //check the cache and download product's xml if need
        String cacheFolderName = createCacheFolder()
        downloadProducts(handler.categoryList, cacheFolderName)
        parseProducts(handler.categoryList, cacheFolderName)


    }


    private def parseProducts(List<Category> categoryList, String cacheFolderName) {
        categoryList.each {
            Category cat = it;
            it.productPointer.each {

                if (it.Date_Added.toLong() > context.mindata) {
                    String productFile = cacheFolderName + it.path.substring(1 + it.path.lastIndexOf("/"));
                    def FileInputStream prodis = new FileInputStream(productFile);
                    def handler = new ProductHandler(categoryList)
                    def reader = SAXParserFactory.newInstance().newSAXParser().XMLReader
                    reader.setContentHandler(handler)
                    reader.parse(new InputSource(prodis))
                    prodis.close();

                }


            }
        }
    }


    private def downloadProducts(List<Category> categoryList, String cacheFolderName) {
        def authString = "$context.login:$context.pwd".getBytes().encodeBase64().toString()
        categoryList.each {
            Category cat = it;
            it.productPointer.each {

                if (it.Date_Added.toLong() > context.mindata) {
                    String productFile = cacheFolderName + it.path.substring(1 + it.path.lastIndexOf("/"));
                    if (!(new File(productFile).exists())) {

                        def conn = "$context.url$it.path".toURL().openConnection();
                        conn.setRequestProperty("Authorization", "Basic ${authString}")
                        new File(productFile) << conn.getInputStream().text
                        println "Downloaded $it.Model_Name"
                    } else {
                        println "Skipped $it.Model_Name"
                    }
                }


            }
        }
    }

    private def createCacheFolder() {
        def cacheFolderName = "$context.dataDirectory/export/freexml.int/xmlcache/$context.productDir/";
        File cacheFolderFile = new File(cacheFolderName);
        if (!cacheFolderFile.exists()) {
            cacheFolderFile.mkdirs();
        }
        return cacheFolderName
    }


}
