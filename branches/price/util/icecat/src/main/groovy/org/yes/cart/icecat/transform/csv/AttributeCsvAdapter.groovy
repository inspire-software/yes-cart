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

import org.yes.cart.icecat.transform.domain.Feature
import org.yes.cart.icecat.transform.Util

/**
 * User: denispavlov
 * Date: 12-08-09
 * Time: 8:03 AM
 */
class AttributeCsvAdapter {

    Map<String, Feature> featureMap;

    public AttributeCsvAdapter(final Map<String, Feature> featureMap) {
        this.featureMap = featureMap
    }

    public toCsvFile(String filename) {

        StringBuilder builder = new StringBuilder();
        builder.append("group code;code;name;EN;RU;mandatory;searchable\n");
        featureMap.values().each {
            builder.append('"PRODUCT";"')
            builder.append(it.ID).append('";"')
            builder.append(Util.escapeCSV(it.getNameFor(null))).append('";"')
            builder.append(Util.escapeCSV(it.getNameFor('en'))).append('";"')
            builder.append(Util.escapeCSV(it.getNameFor('ru'))).append('";')
            builder.append(it.Mandatory != null && it.Mandatory ? "true" : "false").append(";")
            builder.append(it.Searchable != null && it.Searchable ? "true" : "false").append("\n")
        }
        new File(filename).write(builder.toString(), 'UTF-8');

    }

}
