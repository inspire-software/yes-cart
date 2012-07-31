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

import org.yes.cart.icecat.transform.domain.ProductPointer
import org.yes.cart.icecat.transform.domain.Category
import org.xml.sax.helpers.DefaultHandler
import org.xml.sax.Attributes
import org.yes.cart.icecat.transform.Util

/**
 * 
 * User: igora Igor Azarny
 * Date: 5/8/12
 * Time: 4:22 PM
 * 
 */
class ProductPointerHandler extends DefaultHandler {

    Map<String, Category> categoryMap;

    Category category;
    ProductPointer productPointer;

    long updateLimit;
    int maxProductsPerCat;

    int counter = 0;

    ProductPointerHandler(Map<String, Category> categoryMap, long updateLimit, int maxProductsPerCat) {
        this.categoryMap = categoryMap;
        this.updateLimit = updateLimit;
        this.maxProductsPerCat = maxProductsPerCat;
    }


    void startElement(String uri, String localName, String qName, Attributes attributes) {
        if ("file" == qName) {
            category = categoryMap.get(attributes.getValue("Catid"));
            if (category == null) {
                productPointer = null;
            } else {
                productPointer = new ProductPointer()
                productPointer.path   = attributes.getValue("path");
                productPointer.Product_ID = Util.maxLength(attributes.getValue("Product_ID"), 255);
                productPointer.Updated= attributes.getValue("Updated");
                productPointer.Quality= attributes.getValue("Quality");
                productPointer.Supplier_id= attributes.getValue("Supplier_id")
                productPointer.Prod_ID = attributes.getValue("Prod_ID")
                productPointer.Catid = attributes.getValue("Catid");
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

        if ("file" == qName && category != null && productPointer.Date_Added.toLong() > updateLimit) {

            if (category.productPointer.size() > maxProductsPerCat) {
                return; // limit reached
            }
            println("Added product " + productPointer.Product_ID + " to category " + category.id);
            category.productPointer.add(productPointer);
            counter++;

        }

    }

}
