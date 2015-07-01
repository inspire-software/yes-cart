/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
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





package org.yes.cart.icecat.transform.csv

import org.yes.cart.icecat.transform.domain.CategoryFeatureGroup
import org.yes.cart.icecat.transform.domain.Feature
import org.yes.cart.icecat.transform.xml.CategoryHandler
import org.yes.cart.icecat.transform.domain.Category
import org.yes.cart.icecat.transform.Util

/**
 * User: denispavlov
 * Date: 12-08-09
 * Time: 8:03 AM
 */
class ProductTypeAttributeCsvAdapter {

    Map<String, Category> categoryMap;

    def navigable = [
            "7861",  // "7861";"Internal RAM"
            "1120",  // "1120";"Optical drive type"
            "2196",  // "2196";"Processor family"
            "7",     // "7";"Hard drive capacity"
            "909",   // "909";"Battery performance" value in mAh
            "944",   // "944";"Display diagonal"
            "1585",  // "1585";"Display resolution"
            "427",   // "427";"Internal memory type"
            "11381", // "11381";"Internal memory"
            "436",   // "436";"Chipset"
            "9018",  // "9018";"Discrete graphics adapter model"
            "1766",  // "1766";"Color of product"
            "771",   // "771";"Form factor"    (for Mice e.g. "Right-hand")
            "9215",  // "9215";"Scroll type"   (for Mice e.g. "Wheel")
            "8480"   // "8480";"Recommended usage"   (for Keyboards e.g. "Gaming")
    ];

    def rangeMap = [
            "909"   : "<range-list><ranges><range><from>0</from><to>3000</to></range><range><from>3000</from><to>5000</to></range><range><from>5000</from><to>6000</to></range><range><from>6000</from><to>10000</to></range></ranges></range-list>",
            "11381" : "<range-list><ranges><range><from>0</from><to>2</to></range><range><from>2</from><to>4</to></range><range><from>4</from><to>8</to></range><range><from>8</from><to>16</to></range><range><from>16</from><to>32</to></range><range><from>32</from><to>64</to></range><range><from>64</from><to>512</to></range></ranges></range-list>",
    ];

    public ProductTypeAttributeCsvAdapter(final Map<String, Category> categoryMap) {
        this.categoryMap = categoryMap
    }

    public toCsvFile(String filename) {

        Map<String, Feature> features = new TreeMap<String, Feature>();
        Map<String, List<String>> featureTypes = new HashMap();

        categoryMap.values().each {
            for (CategoryFeatureGroup cfg : it.categoryFeatureGroup) {
                for(Feature feature : cfg.featureList) {
                    features.put(feature.ID, feature);
                    List<String> types = featureTypes.get(feature.ID);
                    if (types == null) {
                        types = new ArrayList<String>();
                        featureTypes.put(feature.ID, types);
                    }
                    types.add(it.getNameFor('en'));
                }
            }
        }


        StringBuilder builder = new StringBuilder();
        builder.append("guid;product type;attribute code;attribute name;filter nav;nav type;range nav;\n");
        features.values().each {

            Feature feature = it;
            List<String> types = featureTypes.get(feature.ID);

            types.each {

                builder.append('"')
                builder.append(feature.ID).append('-').append(Util.escapeCSV(it)).append('";"')
                builder.append(Util.escapeCSV(it)).append('";"')
                builder.append(feature.ID).append('";"')
                builder.append(Util.escapeCSV(feature.getNameFor('en'))).append('";')
                builder.append(navigable.contains(feature.ID) ? "true" : "false").append(";")
                if (rangeMap.containsKey(feature.ID)) {
                    builder.append('"R";"').append(rangeMap.get(feature.ID)).append('"\n')
                } else {
                    builder.append('"S";;\n')
                }

            }
        }
        new File(filename).write(builder.toString(), 'UTF-8');

    }

}
