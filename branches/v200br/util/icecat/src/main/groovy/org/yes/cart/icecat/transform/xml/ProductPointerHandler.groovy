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





package org.yes.cart.icecat.transform.xml

import org.xml.sax.Attributes
import org.xml.sax.helpers.DefaultHandler
import org.yes.cart.icecat.transform.Util
import org.yes.cart.icecat.transform.domain.Category
import org.yes.cart.icecat.transform.domain.ProductPointer

/**
 * 
 * User: igora Igor Azarny
 * Date: 5/8/12
 * Time: 4:22 PM
 * 
 */
class ProductPointerHandler extends DefaultHandler {

    Map<String, Category> categoryMap;

    Map<String, ProductPointer> productMap = new TreeMap<String, ProductPointer>();

    Category category;
    ProductPointer productPointer;

    long updateLimit;
    int maxProductsPerCat;

    List<String> allLangs;
    String lang;

    int counter = 0;

    Random randAv = new Random();
    Random randFe = new Random();
    Random randInv = new Random();
    Random randPri = new Random();
    Random randSale = new Random();
    Random randNew = new Random();

    ProductPointerHandler(Map<String, Category> categoryMap, long updateLimit, int maxProductsPerCat) {
        this.categoryMap = categoryMap;
        this.updateLimit = updateLimit;
        this.maxProductsPerCat = maxProductsPerCat;
    }


    void startElement(String uri, String localName, String qName, Attributes attributes) {
        if ("file" == qName && attributes.getValue("Date_Added").toLong() > updateLimit /* old products */) {
            category = categoryMap.get(attributes.getValue("Catid"));
            if (category == null || (!productMap.containsKey(attributes.getValue("Product_ID")) && category.productPointer.size() > maxProductsPerCat) /* limit reached */) {
                productPointer = null;
                if (category != null) {
                    println "Limit reached for category $category.id" ;
                }
            } else {
                if (productMap.containsKey(attributes.getValue("Product_ID"))) {
                    productPointer = productMap.get(attributes.getValue("Product_ID"));
                    println "Reusing pointer $productPointer.Product_ID for category $category.id" ;
                } else {
                    productPointer = new ProductPointer();

                    int chanceAv = randAv.nextInt(100);
                    if (chanceAv < 90) {
                        productPointer.Availability = ProductPointer.AVAILABILITY_STANDARD;
                    } else if (chanceAv > 90 && chanceAv < 95) {
                        productPointer.Availability = ProductPointer.AVAILABILITY_PREORDER;
                        productPointer.Tags = "preorder";
                    } else {
                        productPointer.Availability = ProductPointer.AVAILABILITY_BACKORDER;
                        productPointer.Tags = "backorder";
                    }

                    int chanceFe = randFe.nextInt(100);
                    productPointer.Featured = chanceFe < 90 ? "0" : "1";

                    if (productPointer.Availability == ProductPointer.AVAILABILITY_STANDARD) {
                        productPointer.inventory.put('Main', new BigDecimal(randInv.nextFloat() * 999).setScale(0, BigDecimal.ROUND_HALF_UP));
                    } else {
                        productPointer.inventory.put('Main', new BigDecimal(0));
                    }

                    int chanceSale = randSale.nextInt(100);
                    if (chanceSale > 90) {
                        if (chanceSale > 95) {
                            productPointer.SpecialOffer = true;
                            productPointer.Tags = (productPointer.Tags + " specialoffer").trim();
                        } else {
                            productPointer.Sale = true;
                            productPointer.Tags = (productPointer.Tags + " sale").trim();
                        }
                    }
                    BigDecimal priceUSD = new BigDecimal(500f + randPri.nextFloat() * 3000).setScale(2, BigDecimal.ROUND_HALF_UP);
                    BigDecimal priceEUR = priceUSD.multiply(new BigDecimal("0.74772")).setScale(2, BigDecimal.ROUND_HALF_UP) // 14 Aug 2014
                    BigDecimal priceUAH = priceUSD.multiply(new BigDecimal("13.11550")).setScale(2, BigDecimal.ROUND_HALF_UP) // 14 Aug 2014
                    productPointer.prices.put('SHOP10', [
                            'USD' : priceUSD,
                            'EUR' : priceEUR,
                            'UAH' : priceUAH
                    ]);

                    int chanceNew = randNew.nextInt(100);
                    if (chanceNew > 95) {
                        productPointer.NewArrival = true;
                        productPointer.Tags = (productPointer.Tags + " newarrival").trim();
                    }

                    productPointer.Product_ID = attributes.getValue("Product_ID");
                    productPointer.Product_ID_valid = Util.normalize(productPointer.Product_ID);
                    productMap.put(productPointer.Product_ID, productPointer);
                    counter++;

                    println "Creating pointer $productPointer.Product_ID for category $category.id" ;
                }
                String path = attributes.getValue("path");
                productPointer.path.put(lang, path);
                println "Adding path for $productPointer.Product_ID in $lang to $path";

                String Catid = attributes.getValue("Catid");
                if (!productPointer.categories.containsKey(Catid)) {
                    productPointer.categories.put(Catid, category);
                    category.productPointer.add(productPointer);
                    println "Adding product $productPointer.Product_ID ($productPointer.Product_ID_valid) to category $category.id" ;
                }

                productPointer.Updated= attributes.getValue("Updated");
                productPointer.Quality= attributes.getValue("Quality");
                productPointer.Supplier_id= attributes.getValue("Supplier_id")
                productPointer.Prod_ID = attributes.getValue("Prod_ID")
                productPointer.On_Market = attributes.getValue("On_Market")
                productPointer.Model_Name = Util.maxLength(attributes.getValue("Model_Name"), 255);
                productPointer.Product_View= attributes.getValue("Product_View");
                productPointer.HighPic = attributes.getValue("HighPic")
                productPointer.HighPicHeight = attributes.getValue("HighPicHeight")
                productPointer.HighPicSize = attributes.getValue("HighPicSize")
                productPointer.HighPicWidth = attributes.getValue("HighPicWidth")
                productPointer.Date_Added= attributes.getValue("Date_Added");

            }

        }
    }


    void endElement(String ns, String localName, String qName) {

    }

}
