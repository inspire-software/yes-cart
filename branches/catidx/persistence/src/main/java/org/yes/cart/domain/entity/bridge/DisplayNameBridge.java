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
import org.hibernate.search.bridge.FieldBridge;
import org.hibernate.search.bridge.LuceneOptions;
import org.hibernate.search.bridge.TwoWayFieldBridge;
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.i18n.impl.StringI18NModel;
import org.yes.cart.domain.query.ProductSearchQueryBuilder;

/**
 * User: denispavlov
 * Date: 12-08-15
 * Time: 2:05 PM
 */
public class DisplayNameBridge implements TwoWayFieldBridge/*FieldBridge*/ {

    /** {@inheritDoc} */
    public void set(final String name, final Object value, final Document document, final LuceneOptions luceneOptions) {

        if (value instanceof String) {
            if (ProductSearchQueryBuilder.PRODUCT_DISPLAYNAME_FIELD.equals(name)) {
                final I18NModel model = new StringI18NModel((String) value);
                for (String displayName : model.getAllValues().values()) {
                    // add all names to index
                    luceneOptions.addFieldToDocument(name, displayName, document);
                }
            } else if (ProductSearchQueryBuilder.PRODUCT_DISPLAYNAME_ASIS_FIELD.equals(name)) {
                luceneOptions.addFieldToDocument(name, (String) value, document);
            }
        }
    }

    /** {@inheritDoc} */
    public Object get(final String name, final Document document) {
        return document.get(name);
    }

    /** {@inheritDoc} */
    public String objectToString(final Object object) {
        return (String) object;
    }
}
