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

    Map<String, ProductPointer> productMap = new HashMap<String, ProductPointer>();

    Category category;
    ProductPointer productPointer;

    long updateLimit;
    int maxProductsPerCat;

    String lang;

    int counter = 0;

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
            } else {
                if (productMap.containsKey(attributes.getValue("Product_ID"))) {
                    productPointer = productMap.get(attributes.getValue("Product_ID"));
                } else {
                    productPointer = new ProductPointer();
                    productPointer.inventory.put('Main', new BigDecimal(new Random().nextFloat() * 999).setScale(0, BigDecimal.ROUND_HALF_UP));
                    BigDecimal priceUSD = new BigDecimal(500f + new Random().nextFloat() * 3000).setScale(2, BigDecimal.ROUND_HALF_UP);
                    BigDecimal priceEUR = priceUSD.multiply(new BigDecimal("0.814232")).setScale(2, BigDecimal.ROUND_HALF_UP) // 10 Aug 2012
                    BigDecimal priceUAH = priceUSD.multiply(new BigDecimal("8.098000")).setScale(2, BigDecimal.ROUND_HALF_UP) // 10 Aug 2012
                    productPointer.prices.put('SHOP10', [
                            'USD' : priceUSD,
                            'EUR' : priceEUR,
                            'UAH' : priceUAH
                    ]);
                }
                productPointer.path.put(lang, attributes.getValue("path"));
                productPointer.Product_ID = attributes.getValue("Product_ID");
                productPointer.Product_ID_valid = validProductID(productPointer.Product_ID);
                productPointer.Updated= attributes.getValue("Updated");
                productPointer.Quality= attributes.getValue("Quality");
                productPointer.Supplier_id= attributes.getValue("Supplier_id")
                productPointer.Prod_ID = attributes.getValue("Prod_ID")
                productPointer.categories.put(attributes.getValue("Catid"), category);
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

    String validProductID(String productId) {
        return productId.replaceAll(":","-");
    }


    void endElement(String ns, String localName, String qName) {

        if ("file" == qName && productPointer != null) {

            println "Added product $productPointer.Product_ID ($productPointer.Product_ID_valid) to category $category.id" ;
            category.productPointer.add(productPointer);
            productMap.put(productPointer.Product_ID, productPointer);
            counter++;

        }

    }

}
