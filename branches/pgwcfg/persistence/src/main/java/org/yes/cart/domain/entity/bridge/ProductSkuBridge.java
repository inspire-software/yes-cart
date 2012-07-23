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
import org.hibernate.search.bridge.StringBridge;
import org.yes.cart.domain.entity.ProductSku;

import java.util.Collection;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 16:13:01
 */
public class ProductSkuBridge implements FieldBridge {

    /**
     * {@inheritDoc}
     */
    public void set(final String name, final Object value, final Document document, final LuceneOptions luceneOptions) {

        if (value instanceof Collection) {

            final SkuPriceBridge skuPriceBridge = new SkuPriceBridge();

            final AttributeValueBridge attributeValueBridge = new AttributeValueBridge();

            for (Object obj : (Collection) value) {

                ProductSku sku = (ProductSku) obj;

                document.add(new Field(
                        "sku.code",
                        sku.getCode(),
                        luceneOptions.getStore(),
                        Field.Index.NOT_ANALYZED,
                        luceneOptions.getTermVector()
                ));

                document.add(new Field(
                        "sku.skuId",
                        String.valueOf(sku.getSkuId()),
                        luceneOptions.getStore(),
                        Field.Index.NOT_ANALYZED,
                        luceneOptions.getTermVector()
                ));


                skuPriceBridge.set("sku.skuPrice", sku.getSkuPrice(), document, luceneOptions);
                attributeValueBridge.set("attribute", sku.getAttribute(), document, luceneOptions);


            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public String objectToString(final Object productSkuObject) {
        return String.valueOf(((ProductSku) productSkuObject).getSkuId());
    }

}
