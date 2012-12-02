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

package org.yes.cart.domain.entity.bridge;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.hibernate.search.bridge.FieldBridge;
import org.hibernate.search.bridge.LuceneOptions;
import org.yes.cart.domain.entity.ProductCategory;

import java.util.Collection;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 2/4/12
 * Time: 1:33 PM
 */
public class ProductCategoryBridge  implements FieldBridge {

    /** {@inheritDoc} */
    public void set(final String name, final Object value, final Document document, final LuceneOptions luceneOptions) {

         if (value instanceof Collection) {

            for (Object obj : (Collection) value) {

                ProductCategory productCategory = (ProductCategory) obj;

                document.add(new Field(
                        "productCategory.productCategoryId",
                        String.valueOf(productCategory.getProductCategoryId()),
                        luceneOptions.getStore(),
                        Field.Index.NOT_ANALYZED,
                        luceneOptions.getTermVector()
                ));

                document.add(new Field(
                        "productCategory.category",
                        String.valueOf(productCategory.getCategory().getCategoryId()),
                        luceneOptions.getStore(),
                        Field.Index.NOT_ANALYZED,
                        luceneOptions.getTermVector()
                ));

                document.add(new Field(
                        "productCategory.rank",
                        String.valueOf(productCategory.getRank()),
                        luceneOptions.getStore(),
                        Field.Index.NOT_ANALYZED,
                        luceneOptions.getTermVector()
                ));

            }

         }

    }

}
