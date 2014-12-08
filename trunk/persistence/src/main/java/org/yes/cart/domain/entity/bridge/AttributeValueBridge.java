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

            for (Object obj : (Collection) value) {
                final AttrValue attrValue = (AttrValue) obj;

                // Only keep navigatable attributes in index
                if (attrValue.getAttribute() != null && navAttrs.contains(attrValue.getAttribute().getCode())) {

                    if (isValidValue(attrValue)) {
                        document.add(new Field(
                                ProductSearchQueryBuilder.ATTRIBUTE_VALUE_FIELD,
                                (attrValue.getAttribute() == null ? "" : attrValue.getAttribute().getCode()) + attrValue.getVal(),
                                luceneOptions.getStore(),
                                Field.Index.NOT_ANALYZED,
                                luceneOptions.getTermVector()
                        ));
                        document.add(new Field(
                                ProductSearchQueryBuilder.ATTRIBUTE_VALUE_SEARCH_FIELD,
                                getSearchValue(attrValue),
                                luceneOptions.getStore(),
                                Field.Index.ANALYZED,
                                luceneOptions.getTermVector()
                        ));

                    }

                    document.add(new Field(
                            ProductSearchQueryBuilder.ATTRIBUTE_CODE_FIELD,
                            attrValue.getAttribute().getCode(),
                            luceneOptions.getStore(),
                            Field.Index.NOT_ANALYZED,
                            luceneOptions.getTermVector()
                    ));

                    document.add(new Field(
                            "facet_" + attrValue.getAttribute().getCode(),
                            attrValue.getVal(),
                            luceneOptions.getStore(),
                            Field.Index.NOT_ANALYZED,
                            luceneOptions.getTermVector()
                    ));

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

    /**
     * Is value may be in index ans searchable.
     * @param attrValue given attribute value
     * @return true if value must be in index.
     */
    private boolean isValidValue(final AttrValue attrValue) {
        return StringUtils.isNotBlank(attrValue.getVal())
                &&
                !attrValue.getVal().contains("±")  //ice cat hack
                &&
                !attrValue.getVal().contains("+")  //ice cat hack
                &&
                !attrValue.getVal().contains("/")  //ice cat hack
                ;
    }

    private NavigatableAttributesSupport getNavigatableAttributesSupport() {
        return HibernateSearchBridgeStaticLocator.getNavigatableAttributesSupport();
    }


}
