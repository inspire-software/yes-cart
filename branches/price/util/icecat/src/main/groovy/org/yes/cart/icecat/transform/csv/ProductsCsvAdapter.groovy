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
class ProductsCsvAdapter {

    Map<String, ProductPointer> productMap;
    String defLang;

    ProductsCsvAdapter(final Map<String, ProductPointer> productMap, final String defLang) {
        this.productMap = productMap
        this.defLang = defLang
    }

    public toCsvFile(String filename) {

        StringBuilder builder = new StringBuilder();
        builder.append("product guid;SKU code;model;brand;product type;barcode;name;EN;RU;description;EN;RU\n");

        productMap.values().each {
            builder.append('"')
            builder.append(it.Product_ID).append('";"')
            builder.append(Util.escapeCSV(it.Prod_ID)).append('";"') // SKU
            builder.append(Util.escapeCSV(it.Model_Name)).append('";"')
            builder.append(Util.escapeCSV(it.product.Supplier)).append('";"') // Brand
            builder.append(Util.escapeCSV(it.categories.values().iterator().next().getNameFor(null))).append('";"') // Type is same as prime category
            builder.append(it.product.EANCode == null ? '' : Util.escapeCSV(it.product.EANCode)).append('";"')
            builder.append(Util.escapeCSV(it.product.getNameFor(this.defLang))).append('";"')
            builder.append(Util.escapeCSV(it.product.getNameFor('en'))).append('";"')
            builder.append(Util.escapeCSV(it.product.getNameFor('ru'))).append('";"')
            builder.append(Util.escapeCSV(it.product.getLongSummaryDescriptionFor(this.defLang))).append('";"')
            builder.append(Util.escapeCSV(it.product.getLongSummaryDescriptionFor('en'))).append('";"')
            builder.append(Util.escapeCSV(it.product.getLongSummaryDescriptionFor('ru'))).append('"\n')
        }
        new File(filename).write(builder.toString(), 'UTF-8');

    }
}
