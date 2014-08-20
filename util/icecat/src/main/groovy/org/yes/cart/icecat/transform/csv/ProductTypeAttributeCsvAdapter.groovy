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

    def navigableMap = [
            "83"    : 120,     // 83;Display;Display;Дисплей
            "944"   : 100,     // 944;Display diagonal;Display diagonal;Диагональ экрана
            "1585"  : 110,     // 1585;Display resolution;Display resolution;Разрешение экрана;
            "14"    : 200,     // 14;Weight;Weight;Вес;
            "909"   : 250,     // 909;Battery performance;Battery performance;Емкость батареи;
            "7"     :  60,     // 7;Hard drive capacity;Hard drive capacity;Емкость жесткого диска;
            "6"     :  50,     // 6;Internal memory;Internal memory;Оперативная память
            "2196"  :  10,     // 2196;Processor family;Processor family;Семейство процессоров;
            "5"     :  20,     // 5;Processor clock speed;Processor clock speed;Тактовая частота процессора;
            "1120"  : 300,     // 1120;Optical drive type;Optical drive type;Тип оптического привода;
            "46"    : 400      // 46;Color;Color;Цвет;
    ];

    def rangeMap = [
            "14" : "<range-list><ranges><range><from>1200</from><to>1500</to></range><range><from>1500</from><to>1800</to></range><range><from>1800</from><to>2000</to></range><range><from>2000</from><to>2500</to></range><range><from>2500</from><to>3000</to></range><range><from>3000</from><to>3500</to></range><range><from>3500</from><to>4000</to></range><range><from>4000</from><to>5000</to></range></ranges></range-list>"
    ];

    public ProductTypeAttributeCsvAdapter(final Map<String, Category> categoryMap) {
        this.categoryMap = categoryMap
    }

    public toCsvFile(String filename) {

        Map<String, Feature> features = new HashMap();
        Map<String, Feature> featureTypes = new HashMap();

        categoryMap.values().each {
            for (CategoryFeatureGroup cfg : it.categoryFeatureGroup) {
                for(Feature feature : cfg.featureList) {
                    features.put(feature.ID, feature);
                    featureTypes.put(feature, it.getNameFor('en'));
                }
            }
        }


        StringBuilder builder = new StringBuilder();
        builder.append("guid;product type;attribute code;attribute name;filter nav;nav type;range nav;\n");
        features.values().each {
            builder.append('"')
            builder.append(it.ID).append('";"')
            builder.append(Util.escapeCSV(featureTypes.get(it))).append('";"')
            builder.append(it.ID).append('";"')
            builder.append(Util.escapeCSV(it.getNameFor('en'))).append('";')
            builder.append(navigableMap.containsKey(it.ID) ? "true" : "false").append(";")
            if (rangeMap.containsKey(it.ID)) {
                builder.append('"R";"').append(rangeMap.get(it.ID)).append('"\n')
            } else {
                builder.append('"S";;\n')
            }
        }
        new File(filename).write(builder.toString(), 'UTF-8');

    }

}
