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





package org.yes.cart.icecat.transform

import org.xml.sax.InputSource
import org.yes.cart.icecat.transform.domain.Category
import org.yes.cart.icecat.transform.xml.CategoryFeaturesListHandler
import org.yes.cart.icecat.transform.xml.CategoryHandler
import org.yes.cart.icecat.transform.xml.ProductHandler
import org.yes.cart.icecat.transform.xml.ProductPointerHandler

import javax.xml.parsers.SAXParserFactory

import org.yes.cart.icecat.transform.csv.*
import org.yes.cart.icecat.transform.domain.ProductPointer
import org.yes.cart.icecat.transform.domain.Feature

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

        println("Parsing categories...")
        def FileInputStream refs = new FileInputStream("$context.dataDirectory/export/freexml.int/refs.xml");
        def handler = new CategoryHandler(context.categories, context.langId, context.langNames)
        def reader = SAXParserFactory.newInstance().newSAXParser().XMLReader
        reader.setContentHandler(handler)
        reader.parse(new InputSource(refs))
        refs.close();
        println("Added " + handler.counter + " categories...")

        println("Parsing categories features...")
        refs = new FileInputStream("$context.dataDirectory/export/freexml.int/refs.xml");
        def categoryFeaturelistHandler = new CategoryFeaturesListHandler(handler.categoryMap,
                context.langId, context.langNames)
        reader = SAXParserFactory.newInstance().newSAXParser().XMLReader
        reader.setContentHandler(categoryFeaturelistHandler)
        reader.parse(new InputSource(refs))
        refs.close();
        println("Added " + categoryFeaturelistHandler.counter + " features to categories...")

        println("Parsing product indexes...")
        def productPointerHandler = new ProductPointerHandler(handler.categoryMap, context.mindata, context.productsPerCategoryLimit);
        def productReadeReader = SAXParserFactory.newInstance().newSAXParser().XMLReader

        def i = 0;
        for (String dir : Arrays.asList(context.productDir.split(','))) {
            println("index file: $context.dataDirectory/export/freexml.int/$dir/index.html")
            def indexes = new FileInputStream("$context.dataDirectory/export/freexml.int/$dir/index.html");
            productPointerHandler.lang = context.langNames.split(',')[i];
            productReadeReader.setContentHandler(productPointerHandler)
            productReadeReader.parse(new InputSource(indexes))
            i++
        }

        println("Added " + productPointerHandler.productMap.size() + " products to categories...")

        println("Download product data...")
        //check the cache and download product's xml if need
        downloadProducts(productPointerHandler.productMap,
                Arrays.asList(context.productDir.split(',')),
                Arrays.asList(context.langNames.split(',')))
        parseProducts(handler.categoryMap, categoryFeaturelistHandler.featureMap, productPointerHandler.productMap,
                Arrays.asList(context.productDir.split(',')),
                Arrays.asList(context.langNames.split(',')),
                Arrays.asList(context.langId.split(',')))

        downloadProductPictures(productPointerHandler.productMap,
                Arrays.asList(context.productDir.split(',')),
                Arrays.asList(context.langNames.split(',')),
                Arrays.asList(context.langId.split(',')))
        println("Added products data to categories...")

        println("Generating CSV files...")

        def rootDir = "$context.dataDirectory/export/freexml.int/csvresult/$context.productDir";

        //create folder for csv
        new File(rootDir).mkdirs();

        println("Generating producttypenames.csv")
        new ProductTypeCsvAdapter(handler.categoryMap).toCsvFile("$rootDir/producttypenames.csv");

        println("Generating attributenames.csv")
        new AttributeCsvAdapter(categoryFeaturelistHandler.featureMap).toCsvFile("$rootDir/attributenames.csv");

        println("Generating productypeattributeviewgroupnames.csv")
        new ProductTypeViewGroupCsvAdapter(handler.categoryMap).toCsvFile("$rootDir/productypeattributeviewgroupnames.csv");

        println("Generating producttypeattrnames.csv")
        new ProductTypeAttributeCsvAdapter(handler.categoryMap).toCsvFile("$rootDir/producttypeattrnames.csv");

        println("Generating categorynames.csv")
        new CategoryCsvAdapter(handler.categoryMap).toCsvFile("$rootDir/categorynames.csv");

        println("Generating productcategorynames.csv")
        new ProductCategoryCsvAdapter(productPointerHandler.productMap).toCsvFile("$rootDir/productcategorynames.csv");

        println("Generating brandnames.csv")
        new BrandCsvAdapter(handler.categoryMap).toCsvFile("$rootDir/brandnames.csv");

        println("Generating skuinventory.csv")
        new ProductInventoryCsvAdapter(productPointerHandler.productMap).toCsvFile("$rootDir/skuinventory.csv");

        println("Generating skuprices.csv")
        new ProductPricesCsvAdapter(productPointerHandler.productMap).toCsvFile("$rootDir/skuprices.csv");

        println("Generating productnames.csv")
        new ProductsCsvAdapter(productPointerHandler.productMap, context.langNames.split(',')[0]).toCsvFile("$rootDir/productnames.csv");

        println("Generating productsattributes.csv")
        new ProductsAttributesCsvAdapter(productPointerHandler.productMap, context.langNames.split(',')[0]).toCsvFile("$rootDir/productsattributes.csv");

        // Main warehouse is part of initial data
        // new File("$rootDir/warehouse.csv").write("Ware house code;name;description\nMain;Main warehouse;Main warehouse", 'UTF-8');

        println("All done...")

    }


    private def dumpProducts(Map<String, Category> categoryMap) {
        StringBuilder builder = new StringBuilder();
        builder.append("name;category;producttype;brand;availability;skucode;qty;price;barcode;attributes;description\n");
        categoryMap.values().each {
            Category cat = it;
            cat.product.each {
                builder.append(it.toString());
            }
        }
        return builder.toString();
    }


    private def parseProducts(Map<String, Category> categoryMap,
                              Map<String, Feature> featureMap,
                              Map<String, ProductPointer> productMap,
                              List<String> dirs, List<String> langs, List<String> langsids) {
        productMap.values().each {
            for (int i = 0; i < dirs.size(); i++) {
                String cacheFolderName = createCacheFolder(dirs[i])
                String lang = langs.get(i);
                String langId = langsids.get(i);
                String path = it.path.get(lang);
                String productFile = cacheFolderName + path.substring(1 + path.lastIndexOf("/"));

                def FileInputStream prodis = new FileInputStream(productFile);
                def handler = new ProductHandler(categoryMap, featureMap, it, lang, langId);
                def reader = SAXParserFactory.newInstance().newSAXParser().XMLReader
                reader.setContentHandler(handler)
                reader.parse(new InputSource(prodis))
                prodis.close();
            }
        }
    }

    private def downloadProductPictures(Map<String, ProductPointer> productMap,
                                        List<String> dirs, List<String> langs, List<String> langsids) {
        def authString = "$context.login:$context.pwd".getBytes().encodeBase64().toString()
        def cacheFolder = createPictureCacheFolder();
        productMap.values().each {
            char idx = 'a';

            println "Scanning pictures for: $it.Prod_ID with title $it.product.Title"
            // images are the same, so it really is only upto file name in one language
            String productName = it.product.Title.get(langs.get(0)).replace("_", "-").replace(" ", "-").replace("?", "-").replace(".", "-");
            String skuCode = it.product.Prod_id;

            downloadProductPicture(it.product.HighPic, authString, cacheFolder, idx++, productName, skuCode);
            it.product.productPicture.each {

                //limit to 3 pictures only, because of import size
                if (idx != 'd') {
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
        def cacheFolderName = "$context.dataDirectory/export/freexml.int/pictcache/$context.productDir/";
        File cacheFolderFile = new File(cacheFolderName);
        if (!cacheFolderFile.exists()) {
            cacheFolderFile.mkdirs();
        }
        return cacheFolderName
    }




    private def downloadProducts(Map<String, ProductPointer> productMap,
                                 List<String> dirs, List<String> langs) {
        def authString = "$context.login:$context.pwd".getBytes().encodeBase64().toString()
        productMap.values().each {
            for (int i = 0; i < dirs.size(); i++) {
                String cacheFolderName = createCacheFolder(dirs[i])
                String lang = langs.get(i);
                String path = it.path.get(lang);
                String productFile = cacheFolderName + path.substring(1 + path.lastIndexOf("/"));
                println("file: $productFile")
                downloadSingleProduct(lang, productFile, authString, it)
            }
        }
    }


    private downloadSingleProduct(String lang, String productFile, authString, productPointer) {
        if (!(new File(productFile).exists())) {
            def path = productPointer.path.get(lang);
            def conn = "$context.url$path".toURL().openConnection();
            conn.setRequestProperty("Authorization", "Basic ${authString}")
            new File(productFile) << conn.getInputStream().text
            println "Downloaded $productPointer.Model_Name"


        }
    }

    private def createCacheFolder(String dir) {
        def cacheFolderName = "$context.dataDirectory/export/freexml.int/xmlcache/$dir/";
        File cacheFolderFile = new File(cacheFolderName);
        if (!cacheFolderFile.exists()) {
            cacheFolderFile.mkdirs();
        }
        return cacheFolderName
    }


}
