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

import org.yes.cart.icecat.transform.Util
import org.yes.cart.icecat.transform.domain.ProductPointer

/**
 * User: denispavlov
 * Date: 12-08-09
 * Time: 10:15 PM
 */
class ProductsSkuCsvAdapter {

    Map<String, ProductPointer> productMap;
    String defLang;

    Random rand = new Random();

    ProductsSkuCsvAdapter(final Map<String, ProductPointer> productMap, final String defLang) {
        this.productMap = productMap
        this.defLang = defLang
    }

    public toCsvFile(String filename) {

        StringBuilder builder = new StringBuilder();
        builder.append("product guid;SKU code;model;brand;product type;barcode;name;EN;RU;UK;description;EN;RU;UK;availability;featured;tag\n");

        productMap.values().each {
            try {
                if (it.product != null) {
                    StringBuilder builderTmp = new StringBuilder();
                    builderTmp.append('"')
                    builderTmp.append(it.Product_ID_valid).append('";"')
                    builderTmp.append(Util.escapeCSV(it.Prod_ID)).append('";"') // SKU
                    builderTmp.append(Util.escapeCSV(it.Model_Name)).append('";')
                    builderTmp.append(';') // Brand
                    builderTmp.append(';') // Type is same as prime category
                    builderTmp.append(';"')
                    builderTmp.append(Util.escapeCSV(it.product.getNameFor('en'))).append('";"')
                    builderTmp.append(Util.escapeCSV(it.product.getNameFor('en'))).append('";"')
                    builderTmp.append(Util.escapeCSV(it.product.getNameFor('ru'))).append('";"')
                    builderTmp.append(Util.escapeCSV(it.product.getNameFor('uk'))).append('";')
                    builderTmp.append(';')
                    builderTmp.append(';')
                    builderTmp.append(';')
                    builderTmp.append(';')
                    builderTmp.append(';')
                    builderTmp.append(';')
                    builderTmp.append('\n')
                    builder.append(builderTmp.toString());
                }


            }   catch (Exception e){
                e.printStackTrace();

            }

        }
        new File(filename).write(builder.toString(), 'UTF-8');

    }

}
