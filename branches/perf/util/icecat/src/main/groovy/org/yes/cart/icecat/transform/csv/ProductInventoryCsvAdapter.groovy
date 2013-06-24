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
class ProductInventoryCsvAdapter {

    Map<String, ProductPointer> productMap;

    ProductInventoryCsvAdapter(final Map<String, ProductPointer> productMap) {
        this.productMap = productMap
    }

    public toCsvFile(String filename) {

        StringBuilder builder = new StringBuilder();
        builder.append("sku code;model;warehouse code;qty in stock\n");

        productMap.values().each {
            def pp = it;
            it.inventory.each {
                builder.append('"')
                builder.append(pp.Prod_ID).append('";"')
                builder.append(Util.escapeCSV(pp.Model_Name)).append('";"')
                builder.append(it.key).append('";')
                builder.append(it.value).append('\n')
            }
        }
        new File(filename).write(builder.toString(), 'UTF-8');

    }
}
