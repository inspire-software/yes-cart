/*
 * Copyright 2013 Igor Azarnyi, Denys Pavlov
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
package org.yes.cart.domain.entity.bridge;


import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.hibernate.search.bridge.FieldBridge;
import org.hibernate.search.bridge.LuceneOptions;
import org.yes.cart.domain.query.ProductSearchQueryBuilder;

import java.util.Collection;

/**
 * User: igora Igor Azarny
 * Date: 6/09/13
 * Time: 9:23 AM
 */
public class ProductShopBridge implements FieldBridge {

    //this.getSku().iterator().next().getQuantityOnWarehouse().iterator().next().getWarehouse().getWarehouseShop()

    /**
     * {@inheritDoc}
     */
    public void set(final String name, final Object value, final Document document, final LuceneOptions luceneOptions) {


        if (value instanceof Collection) {

            for (Object obj : (Collection) value) {

                document.add(new Field(
                        ProductSearchQueryBuilder.PRODUCT_SHOP_FIELD,
                        String.valueOf(obj),
                        luceneOptions.getStore(),
                        Field.Index.NOT_ANALYZED,
                        luceneOptions.getTermVector()
                ));

            }

        }
    }
}
