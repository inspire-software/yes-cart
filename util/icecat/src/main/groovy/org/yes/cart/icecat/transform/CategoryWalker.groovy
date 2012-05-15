package org.yes.cart.icecat.transform

import org.yes.cart.icecat.transform.xml.CategoryHandler
import org.yes.cart.icecat.transform.domain.Category
import javax.xml.parsers.SAXParserFactory
import org.xml.sax.InputSource
import org.yes.cart.icecat.transform.xml.ProductPointerHandler
import org.yes.cart.icecat.transform.xml.CategoryFeaturesListHandler
import org.yes.cart.icecat.transform.xml.ProductHandler
import org.yes.cart.icecat.transform.domain.Product

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

        String pictCacheFolder = createPictureCacheFolder();
        downloadProductPicturess(handler.categoryList, pictCacheFolder)

        //create folder for csv
        new File("$context.dataDirectory/export/freexml.int/csvresult/").mkdirs();



        StringBuilder csv = new StringBuilder();

        handler.categoryList.each { csv.append(it.toProductType())}
        new File("$context.dataDirectory/export/freexml.int/csvresult/category.csv") << csv.toString();

        csv = new StringBuilder();
        csv.append("name;attrname;mandatory;searchable;num\n")
        handler.categoryList.each { csv.append(it.toProductTypeAttr())}
        new File("$context.dataDirectory/export/freexml.int/csvresult/producttypeattr.csv") << csv.toString();

        csv = new StringBuilder();
        handler.categoryList.each { csv.append(it.toArrtViewGroup())}
        new File("$context.dataDirectory/export/freexml.int/csvresult/attributeviewgroup.csv") << csv.toString();

        csv = new StringBuilder();
        handler.categoryList.each { csv.append(it.toProductTypeAttrViewGroup())}
        new File("$context.dataDirectory/export/freexml.int/csvresult/productypeattributeviewgroup.csv") << csv.toString();

        new File("$context.dataDirectory/export/freexml.int/csvresult/brand.csv") << dumpBrands(handler.categoryList);

        new File("$context.dataDirectory/export/freexml.int/csvresult/product.csv") << dumpProducts(handler.categoryList);


    }


    private def dumpProducts(List<Category> categoryList) {
        StringBuilder builder = new StringBuilder();
        builder.append("name;category;producttype;brand;availability;skucode;qty;price;barcode;attributes;description\n");
        categoryList.each {
            Category cat = it;
            cat.product.each {
                builder.append(it.toString());
            }
        }
        return builder.toString();
    }

    private def dumpBrands(List<Category> categoryList) {
        Set<String> rez = new HashSet<String>();
        categoryList.each {
            Category cat = it;
            cat.product.each {
                rez.add(it.Supplier);
            }
        }
        StringBuilder builder = new StringBuilder();
        builder.append("brand;description\n");
        rez.each {
            builder.append(it)
            builder.append(";")
            builder.append(it)
            builder.append("\n")
        }
        return builder.toString();
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

    private def downloadProductPicturess(List<Category> categoryList, String cacheFolderName) {
        def authString = "$context.login:$context.pwd".getBytes().encodeBase64().toString()
        def cacheFolder = createPictureCacheFolder();
        categoryList.each {
            Category cat = it;
            it.product.each {
                char idx = 'a';

                String productName = it.Title.replace("_", "-").replace(" ", "-").replace("?", "-").replace(".", "-");
                String skuCode = it.Prod_id;

                downloadProductPicture(it.HighPic, authString, cacheFolder, idx++, productName, skuCode);
                it.productPicture.each {

                    downloadProductPicture(it, authString, cacheFolder, idx++, productName, skuCode);

                }

            }
        }
    }

    private def downloadProductPicture(String url, String authString, String cacheFolderName, char idx, String productName, String skuCode) {
        try {
            //preformat filename for import
            String productFile = cacheFolderName + productName + "_" + skuCode + "_" + idx + url.substring(url.lastIndexOf("."));


            if (!(new File(productFile).exists())) {
                try {
                    URLConnection conn = "$url".toURL().openConnection();
                    conn.setRequestProperty("Authorization", "Basic ${authString}")
                    InputStream input = conn.getInputStream()
                    def output = new BufferedOutputStream(new FileOutputStream(productFile));
                    output << input
                    input.close();
                    output.close();
                    println "Downloaded $url into $productFile"

                } catch (FileNotFoundException e) {
                    println "File $url not exists on remote server, skipped"
                }


            } else {
                println "Skipped $url"
            }

        } catch (Exception e) {
            println "cant download $url, because of $e.message"
        }
    }

    private def createPictureCacheFolder() {
        def cacheFolderName = "$context.dataDirectory/export/freexml.int/pictcache/";
        File cacheFolderFile = new File(cacheFolderName);
        if (!cacheFolderFile.exists()) {
            cacheFolderFile.mkdirs();
        }
        return cacheFolderName
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
