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
class ProductTypeViewGroupCsvAdapter {

    Map<String, Category> categoryMap;

    public ProductTypeViewGroupCsvAdapter(final Map<String, Category> categoryMap) {
        this.categoryMap = categoryMap
    }

    public toCsvFile(String filename) {

        StringBuilder builder = new StringBuilder();
        builder.append("product type name;guid;name;EN;RU;attr code csl\n");

        categoryMap.values().each {
            for (CategoryFeatureGroup cfg : it.categoryFeatureGroup) {
                builder.append('"')
                builder.append(Util.escapeCSV(it.getNameFor(null))).append('";"')
                builder.append((cfg.getNameFor(null) + it.getNameFor(null)).hashCode()).append('";"')
                builder.append(Util.escapeCSV(cfg.getNameFor(null))).append('";"')
                builder.append(Util.escapeCSV(cfg.getNameFor('en'))).append('";"')
                builder.append(Util.escapeCSV(cfg.getNameFor('ru'))).append('";"')

                for(Feature feature : cfg.featureList) {
                    builder.append(feature.ID);
                    builder.append(',');
                }
                builder.append('"\n')
            }
        }
        new File(filename).write(builder.toString(), 'UTF-8');

    }

}
