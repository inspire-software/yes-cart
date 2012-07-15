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

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 5/9/12
 * Time: 7:29 PM
 */
class CategoryFeaturesListHandler extends DefaultHandler {

    final List<Category> categoryList;
    final List<String> categoryIdFiler;
    final String langFilter;



    boolean allowWork = false;
    Category category = null;
    CategoryFeatureGroup categoryFeatureGroup = null;
    boolean readyToGetCfgName = false;
    boolean readyToGetFeatureName = false;
    Feature feature = null;





    CategoryFeaturesListHandler(List<Category> categoryList, List<String> categoryIdFiler, String langFilter) {
        this.categoryList = categoryList
        this.categoryIdFiler = categoryIdFiler
        this.langFilter = langFilter
    }

    void startElement(String uri, String localName, String qName, Attributes attributes) {

        if ("CategoryFeaturesList" == qName) {
            allowWork = true;
        }

        if (allowWork) {

            if ("Category" == qName) {


                if (categoryIdFiler.contains(attributes.getValue("ID"))) {
                    category = locateCategory(attributes.getValue("ID"));
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
                        categoryFeatureGroup.Name = attributes.getValue("Value");
                        readyToGetCfgName = false;
                    }
                    if(readyToGetFeatureName) {
                        feature.Name = attributes.getValue("Value");
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
                if(category != null) {
                    println category;
                }
                category = null;
            }

            if ("CategoryFeatureGroup" == qName) {
                if (category != null) {
                    category.categoryFeatureGroup.add(categoryFeatureGroup);
                    categoryFeatureGroup = null;

                }
            }

            if ("Feature" == qName) {
                CategoryFeatureGroup cfg = locateCategoryFeatureGroup(feature.categoryFeatureGroup_ID);
                if (cfg != null) {
                    cfg.featureList.add(feature);
                }
                feature = null;

            }

        }


    }


    Category locateCategory(String categId) {
        for (Category cat: categoryList) {
            if (categId == cat.id) {
                return cat;
            }
        }
        return null;

    }

    CategoryFeatureGroup locateCategoryFeatureGroup(String categoryFeatureGroupID) {

        for (Category cat: categoryList) {
            for (CategoryFeatureGroup c: cat.categoryFeatureGroup) {
                if (c.ID.equals(categoryFeatureGroupID)) {
                    return c;
                }
            }

        }

        return null;
    }


}

