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
import org.yes.cart.icecat.transform.domain.Category
import org.yes.cart.icecat.transform.domain.CategoryFeatureGroup
import org.yes.cart.icecat.transform.domain.Feature
import org.yes.cart.icecat.transform.Util

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 5/9/12
 * Time: 7:29 PM
 */
class CategoryFeaturesListHandler extends DefaultHandler {

    final Map<String, Category> categoryMap;
    final String langFilter;

    final Map<String, CategoryFeatureGroup> groupMap = new HashMap<String, CategoryFeatureGroup>();

    boolean allowWork = false;
    Category category = null;
    CategoryFeatureGroup categoryFeatureGroup = null;
    boolean readyToGetCfgName = false;
    boolean readyToGetFeatureName = false;
    Feature feature = null;

    int counter = 0;



    CategoryFeaturesListHandler(Map<String, Category> categoryMap, String langFilter) {
        this.categoryMap = categoryMap
        this.langFilter = langFilter
    }

    void startElement(String uri, String localName, String qName, Attributes attributes) {

        if ("CategoryFeaturesList" == qName) {
            allowWork = true;
        }

        if (allowWork) {

            if ("Category" == qName) {


                if (categoryMap.containsKey(attributes.getValue("ID"))) {
                    category = categoryMap.get(attributes.getValue("ID"));
                    println("Start category " + category.id + " features");
                }
            }

            if ("CategoryFeatureGroup" == qName) {

                categoryFeatureGroup = new CategoryFeatureGroup();
                categoryFeatureGroup.ID = attributes.getValue("ID");
                categoryFeatureGroup.No = attributes.getValue("No");

            }

            if ("FeatureGroup" == qName) {
                readyToGetCfgName = true;
            }

            if ("Name" == qName) {
                if (langFilter == attributes.getValue("langid")) {
                    if (readyToGetCfgName) {
                        categoryFeatureGroup.Name = Util.maxLength(attributes.getValue("Value"), 255);
                        readyToGetCfgName = false;
                    }
                    if(readyToGetFeatureName) {
                        feature.Name = Util.maxLength(attributes.getValue("Value"), 255);
                        readyToGetFeatureName = false;
                    }
                }
            }

            if ("Feature" == qName) {

                feature = new Feature();
                feature.CategoryFeatureGroup_ID = attributes.getValue("CategoryFeatureGroup_ID");
                feature.CategoryFeature_ID = attributes.getValue("CategoryFeature_ID");
                feature.lClass = attributes.getValue("Class");
                feature.ID = attributes.getValue("ID");
                feature.LimitDirection = attributes.getValue("LimitDirection");
                feature.Mandatory = attributes.getValue("Mandatory");
                feature.No = attributes.getValue("No");
                feature.Searchable = attributes.getValue("Searchable");
                feature.Use_Dropdown_Input = attributes.getValue("Use_Dropdown_Input");

                readyToGetFeatureName = true;

            }

        }
    }

    void endElement(String ns, String localName, String qName) {

        if ("CategoryFeaturesList" == qName) {
            allowWork = false;
        }

        if (allowWork) {
            if ("Category" == qName) {
                category = null;
            }

            if ("CategoryFeatureGroup" == qName) {
                if (category != null) {
                    println("Adding feature group " + categoryFeatureGroup.ID + " to category " + category.id);
                    category.categoryFeatureGroup.add(categoryFeatureGroup);
                    if (!groupMap.containsKey(categoryFeatureGroup.ID)) {
                        groupMap.put(categoryFeatureGroup.ID, categoryFeatureGroup);
                    }
                    categoryFeatureGroup = null;

                }
            }

            if ("FeatureGroup" == qName) {
                readyToGetCfgName = false;
            }

            if ("Feature" == qName) {
                CategoryFeatureGroup cfg = groupMap.get(feature.CategoryFeatureGroup_ID);
                if (cfg != null) {
                    if (feature.Name == null) {
                        feature.Name = feature.ID;
                    }
                    println("Feature " + feature.ID + " belongs to category feature group " + feature.CategoryFeatureGroup_ID + "... adding");
                    cfg.featureList.add(feature);
                    counter++;
                }
                feature = null;
                readyToGetFeatureName = false;

            }

        }


    }

}

