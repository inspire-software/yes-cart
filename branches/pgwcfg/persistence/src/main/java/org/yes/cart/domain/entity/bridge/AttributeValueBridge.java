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

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;


import org.hibernate.search.bridge.FieldBridge;
import org.hibernate.search.bridge.LuceneOptions;
import org.hibernate.search.bridge.StringBridge;
import org.yes.cart.domain.entity.AttrValue;
import org.yes.cart.domain.entity.AttrValueProductSku;
import org.yes.cart.domain.query.ProductSearchQueryBuilder;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 16:13:01
 */
public class AttributeValueBridge implements FieldBridge {


    /**
     * {@inheritDoc}
     */
    public void set(final String name, final Object value, final Document document, final LuceneOptions luceneOptions) {

        if (value instanceof Collection) {
            for (Object obj : (Collection) value) {
                final AttrValue attrValue = (AttrValue) obj;
                final String prefix = (obj instanceof AttrValueProductSku) ? "sku." : "";

                if (StringUtils.isNotBlank(attrValue.getVal())) {
                    document.add(new Field(
                            prefix + ProductSearchQueryBuilder.ATTRIBUTE_VALUE_FIELD,
                            (attrValue.getAttribute() == null ? "" : attrValue.getAttribute().getCode()) + attrValue.getVal(),
                            luceneOptions.getStore(),
                            Field.Index.NOT_ANALYZED,
                            luceneOptions.getTermVector()
                    ));
                    document.add(new Field(
                            prefix + ProductSearchQueryBuilder.ATTRIBUTE_VALUE_SEARCH_FIELD,
                            attrValue.getVal(),
                            luceneOptions.getStore(),
                            Field.Index.ANALYZED,
                            luceneOptions.getTermVector()
                    ));

                }

                if (attrValue.getAttribute() != null && StringUtils.isNotBlank(attrValue.getAttribute().getCode())) {
                    document.add(new Field(
                            prefix + ProductSearchQueryBuilder.ATTRIBUTE_CODE_FIELD,
                            attrValue.getAttribute().getCode(),
                            luceneOptions.getStore(),
                            Field.Index.NOT_ANALYZED,
                            luceneOptions.getTermVector()
                    ));
                }

            }
        }

    }


}
