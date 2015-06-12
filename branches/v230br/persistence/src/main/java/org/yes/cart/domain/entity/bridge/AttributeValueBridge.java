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

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.hibernate.search.bridge.FieldBridge;
import org.hibernate.search.bridge.LuceneOptions;
import org.yes.cart.domain.entity.AttrValue;
import org.yes.cart.domain.entity.bridge.support.NavigatableAttributesSupport;
import org.yes.cart.domain.i18n.impl.StringI18NModel;
import org.yes.cart.domain.query.ProductSearchQueryBuilder;

import java.util.Collection;
import java.util.Set;

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

            final NavigatableAttributesSupport support = getNavigatableAttributesSupport();
            final Set<String> navAttrs = support.getAllNavigatableAttributeCodes();
            final Set<String> searchAttrs = support.getAllSearchableAttributeCodes();

            for (Object obj : (Collection) value) {
                final AttrValue attrValue = (AttrValue) obj;

                if (attrValue.getAttribute() == null) {
                    continue; // skip invalid ones
                }

                final boolean navigation = navAttrs.contains(attrValue.getAttribute().getCode());
                final boolean search = searchAttrs.contains(attrValue.getAttribute().getCode());

                // Only keep navigatable attributes in index
                if (search || navigation) {

                    if (StringUtils.isNotBlank(attrValue.getVal())) {
                        // searchable and navigatable terms for global search tokenised
                        document.add(new Field(
                                ProductSearchQueryBuilder.ATTRIBUTE_VALUE_SEARCH_FIELD,
                                getSearchValue(attrValue),
                                Field.Store.NO,
                                Field.Index.ANALYZED,
                                luceneOptions.getTermVector()
                        ));

                        // searchable and navigatable terms for global search full phrase
                        document.add(new Field(
                                ProductSearchQueryBuilder.ATTRIBUTE_VALUE_SEARCH_FIELD,
                                getSearchValue(attrValue),
                                Field.Store.NO,
                                Field.Index.NOT_ANALYZED,
                                luceneOptions.getTermVector()
                        ));

                    }

                    if (navigation) {
                        // strict attribute navigation only for filtered navigation
                        document.add(new Field(
                                "facet_" + attrValue.getAttribute().getCode(),
                                attrValue.getVal(),
                                Field.Store.NO,
                                Field.Index.NOT_ANALYZED,
                                luceneOptions.getTermVector()
                        ));
                    }

                }

            }
        }

    }

    private String getSearchValue(final AttrValue attrValue) {
        if (StringUtils.isNotBlank(attrValue.getDisplayVal())) {
            return attrValue.getDisplayVal().replace(StringI18NModel.SEPARATOR, " ").concat(" ").concat(attrValue.getVal());
        }
        return attrValue.getVal();
    }

    private NavigatableAttributesSupport getNavigatableAttributesSupport() {
        return HibernateSearchBridgeStaticLocator.getNavigatableAttributesSupport();
    }


}
