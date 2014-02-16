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
    List<String> langIdFilter;
    List<String> langFilter;
    int counter = 0;

    Category category;

    CategoryHandler(String allowedCategories, String langIdFilter, String langFilter) {

        this.langIdFilter = Arrays.asList(langIdFilter.split(","));
        this.langFilter = Arrays.asList(langFilter.split(","));
        categoryIdFiler = Arrays.asList(allowedCategories.split(","));

    }



    void startElement(String uri, String localName, String qName, Attributes attributes) {

        if (allowAddToCategoryList && "Category" == qName) {
            category = new Category();
            category.id = attributes.getValue("ID");
            if (categoryIdFiler.contains(category.id)) {
                category.lowPic = attributes.getValue("LowPic");
                category.score = attributes.getValue("Score");
                category.searchable = attributes.getValue("Searchable");
                category.thumbPic = attributes.getValue("ThumbPic");
                category.UNCATID = attributes.getValue("UNCATID");
                category.visible = attributes.getValue("Visible");
            }

        } else if ("ParentCategory" == qName) {

            category.parentCategoryid = attributes.getValue("ID");

        } else if ("CategoriesList" == qName) {

            allowAddToCategoryList = true;

        } else if ("CategoryFeaturesList" == qName) {

            allowAddToCategoryList = false;
            category = null;

        } else if (category != null) {

            if ("Description" == qName && categoryIdFiler.contains(category.id)) {
                Util.setLocalisedValue(category, 'description',
                        attributes.getValue("langid"), attributes.getValue("Value"), 4000, langIdFilter, langFilter);
            } else if ("Name" == qName && categoryIdFiler.contains(category.id)) {
                Util.setLocalisedValue(category, 'name',
                        attributes.getValue("langid"), attributes.getValue("Value"), 255, langIdFilter, langFilter);
            } else if ("Keywords" == qName && categoryIdFiler.contains(category.id)) {
                Util.setLocalisedValue(category, 'keywords',
                        attributes.getValue("langid"), attributes.getValue("Value"), 255, langIdFilter, langFilter);
            }
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
            category = null;
        } else if ("CategoriesList" == qName) {
            allowAddToCategoryList = false;

        }


    }


}
