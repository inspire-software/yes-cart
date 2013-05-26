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

import org.yes.cart.icecat.transform.domain.ProductPointer
import org.yes.cart.icecat.transform.Util

/**
 * User: denispavlov
 * Date: 12-08-09
 * Time: 10:15 PM
 */
class ProductsAttributesCsvAdapter {

    Map<String, ProductPointer> productMap;
    String defLang;

    ProductsAttributesCsvAdapter(final Map<String, ProductPointer> productMap, final String defLang) {
        this.productMap = productMap
        this.defLang = defLang
    }

    public toCsvFile(String filename) {

        StringBuilder builder = new StringBuilder();
        builder.append("product guid;SKU code;model;attribute code;attribute name;value;display value EN; display value RU\n");

        productMap.values().each {
            def pp = it;
            pp.product.productFeatures.values().each {
                builder.append('"')
                builder.append(pp.Product_ID_valid).append('";"')
                builder.append(Util.escapeCSV(pp.Prod_ID)).append('";"') // SKU
                builder.append(Util.escapeCSV(pp.Model_Name)).append('";"')
                builder.append(it.feature.ID).append('";"') // code (same for product attr code)
                if (it.Value != null && it.Value != '') {
                    builder.append(Util.escapeCSV(it.Value)).append('";"')
                } else {
                    builder.append(Util.escapeCSV(it.getPresentationValueFor(defLang))).append('";"')
                }
                builder.append(Util.escapeCSV(it.getPresentationValueFor('en'))).append('";"')
                builder.append(Util.escapeCSV(it.getPresentationValueFor('ru'))).append('"\n')

            }
        }
        new File(filename).write(builder.toString(), 'UTF-8');

    }
}
