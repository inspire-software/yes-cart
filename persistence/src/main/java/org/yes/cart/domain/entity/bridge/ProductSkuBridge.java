/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
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
import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.domain.entity.Rankable;
import org.yes.cart.domain.misc.RankableComparatorImpl;

import java.util.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 16:13:01
 */
public class ProductSkuBridge implements FieldBridge {

    private static final Comparator<Rankable> BY_RANK = new RankableComparatorImpl();

    private final SkuPriceBridge skuPriceBridge = new SkuPriceBridge();
    private final AttributeValueBridge attributeValueBridge = new AttributeValueBridge();

    /**
     * {@inheritDoc}
     */
    public void set(final String name, final Object value, final Document document, final LuceneOptions luceneOptions) {

        if (value instanceof Collection) {

            final Collection<ProductSku> skus = (Collection<ProductSku>) value;

            document.add(new Field(
                    "multisku",
                    skus.size() > 1 ? "true" : "false",
                    Field.Store.YES,
                    Field.Index.NOT_ANALYZED,
                    luceneOptions.getTermVector()
            ));

            final List<ProductSku> ordered = new ArrayList<ProductSku>(skus);
            Collections.sort(ordered, BY_RANK);

            for (final ProductSku sku : ordered) {

                document.add(new Field(
                        "sku.code",
                        sku.getCode(),
                        Field.Store.NO,
                        Field.Index.NOT_ANALYZED,
                        luceneOptions.getTermVector()
                ));

                document.add(new Field(
                        "sku.code_stem",
                        sku.getCode(),
                        Field.Store.NO,
                        Field.Index.ANALYZED,
                        luceneOptions.getTermVector()
                ));

                document.add(new Field(
                        "sku.skuId",
                        String.valueOf(sku.getSkuId()),
                        Field.Store.NO,
                        Field.Index.NOT_ANALYZED,
                        luceneOptions.getTermVector()
                ));


                skuPriceBridge.set("", sku, document, luceneOptions);
                attributeValueBridge.set("attribute", sku.getAttributes(), document, luceneOptions);

            }
        }
    }

    /**
     * Object to string conversion.
     *
     * @param productSkuObject product SKU
     *
     * @return string PK
     */
    public String objectToString(final Object productSkuObject) {
        return String.valueOf(((ProductSku) productSkuObject).getSkuId());
    }

}
