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

import org.xml.sax.helpers.DefaultHandler
import org.xml.sax.Attributes
import org.yes.cart.icecat.transform.domain.Category;
import org.yes.cart.icecat.transform.Util;

/**
 *
 * User: igora Igor Azarny
 * Date: 5/9/12
 * Time: 2:49 PM
 *
 */
class CategoryHandler extends DefaultHandler {

    boolean allowAddToCategoryList = false;

    Map<String, Category> categoryMap = new HashMap<String, Category>();
    List<String> categoryIdFiler;
    String langFilter;
    int counter = 0;

    Category category;

    CategoryHandler(String allowedCategories, String langFilter) {

        this.langFilter = langFilter
        categoryIdFiler = Arrays.asList(allowedCategories.split(","));

    }



    void startElement(String uri, String localName, String qName, Attributes attributes) {

        if ("Category" == qName) {
            category = new Category();
            category.id = attributes.getValue("ID");
            category.lowPic = attributes.getValue("LowPic");
            category.score = attributes.getValue("Score");
            category.searchable = attributes.getValue("Searchable");
            category.thumbPic = attributes.getValue("ThumbPic");
            category.UNCATID = attributes.getValue("UNCATID");
            category.visible = attributes.getValue("Visible");
        } else if ("Description" == qName) {
            if ("1" == attributes.getValue("langid") || langFilter == attributes.getValue("langid")) {
                if (Util.isNotBlank(attributes.getValue("Value"))) {
                    category.description = Util.maxLength(attributes.getValue("Value"), 4000);
                }
            }
        } else if ("Name" == qName) {
            if ("1" == attributes.getValue("langid") || langFilter == attributes.getValue("langid")) {
                if (Util.isNotBlank(attributes.getValue("Value"))) {
                    category.name = Util.maxLength(attributes.getValue("Value"), 255);
                }
            }
        } else if ("Keywords" == qName) {
            if ("1" == attributes.getValue("langid") || langFilter == attributes.getValue("langid")) {
                if (Util.isNotBlank(attributes.getValue("Value"))) {
                    category.keywords = Util.maxLength(attributes.getValue("Value"), 255);
                }
            }
        } else if ("CategoriesList" == qName) {
            allowAddToCategoryList = true;
        }
    }


    void endElement(String ns, String localName, String qName) {

        if ("Category" == qName) {

            if (allowAddToCategoryList && categoryIdFiler.contains(category.id)) {
                println("Category " + category.id + " ... added")
                if (category.name == null) {
                    category.name = category.id;
                }
                categoryMap.put(category.id, category);
                counter++;
            }
        } else if ("CategoriesList" == qName) {
            allowAddToCategoryList = false;

        }


    }


}
