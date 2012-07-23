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

/**
 *
 * User: igora Igor Azarny
 * Date: 5/9/12
 * Time: 2:49 PM
 *
 */
class CategoryHandler extends DefaultHandler {

    boolean allowAddToCategoryList = false;

    List<Category> categoryList = new ArrayList<Category>();
    List<String> categoryIdFiler;
    String langFilter;

    Category category;


    String description;
    String keywords;
    String name;

    CategoryHandler(String categoryList, String langFilter) {

        this.langFilter = langFilter
        categoryIdFiler = Arrays.asList(categoryList.split(","));

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
                if (org.yes.cart.icecat.transform.Util.isNotBlank(attributes.getValue("Value"))) {
                    category.description = attributes.getValue("Value");
                }
            }
        } else if ("Name" == qName) {
            if ("1" == attributes.getValue("langid") || langFilter == attributes.getValue("langid")) {
                if (org.yes.cart.icecat.transform.Util.isNotBlank(attributes.getValue("Value"))) {
                    category.name = attributes.getValue("Value");
                }
            }
        } else if ("Keywords" == qName) {
            if ("1" == attributes.getValue("langid") || langFilter == attributes.getValue("langid")) {
                if (org.yes.cart.icecat.transform.Util.isNotBlank(attributes.getValue("Value"))) {
                    category.keywords = attributes.getValue("Value");
                }
            }
        } else if ("CategoriesList" == qName) {
            allowAddToCategoryList = true;
        }
    }


    void endElement(String ns, String localName, String qName) {

        if ("Category" == qName) {
            if (allowAddToCategoryList && categoryIdFiler.contains(category.id)) {
                categoryList.add(category);
            }
        } else if ("CategoriesList" == qName) {
            allowAddToCategoryList = false;

        }


    }


}
