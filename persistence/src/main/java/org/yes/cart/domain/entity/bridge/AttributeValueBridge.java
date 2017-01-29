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
import org.yes.cart.domain.entityindexer.StoredAttributes;
import org.yes.cart.domain.entityindexer.impl.StoredAttributesImpl;
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.i18n.impl.StringI18NModel;
import org.yes.cart.domain.query.ProductSearchQueryBuilder;

import java.util.*;

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
            final Set<String> searchPrimaryAttrs = support.getAllSearchablePrimaryAttributeCodes();
            final Set<String> storeAttrs = support.getAllStorableAttributeCodes();

            StoredAttributes storedAttributes = null;

            for (Object obj : (Collection) value) {
                final AttrValue attrValue = (AttrValue) obj;

                if (attrValue.getAttribute() == null) {
                    continue; // skip invalid ones
                }

                final String code = attrValue.getAttribute().getCode();

                final boolean navigation = navAttrs.contains(code);
                final boolean search = navigation || searchAttrs.contains(code);
                final boolean searchPrimary = searchPrimaryAttrs.contains(code);

                // Only keep searcheable and navigatable attributes in index
                if (search) {
                    if (StringUtils.isNotBlank(attrValue.getVal())) {

                        if (searchPrimary) {

                            final List<String> searchValues = getSearchValue(attrValue);

                            // primary search should only exist in primary search exact match
                            for (final String searchValue : searchValues) {
                                final Field prime = new Field(
                                        ProductSearchQueryBuilder.ATTRIBUTE_VALUE_SEARCHPRIMARY_FIELD,
                                        searchValue,
                                        Field.Store.NO,
                                        Field.Index.NOT_ANALYZED,
                                        luceneOptions.getTermVector()
                                );
                                prime.setBoost(2f);
                                document.add(prime);
                            }

                        } else {

                            final List<String> searchValues = getSearchValue(attrValue);

                            for (final String searchValue : searchValues) {

                                // searchable and navigatable terms for global search tokenised
                                final Field searchTokens = new Field(
                                        ProductSearchQueryBuilder.ATTRIBUTE_VALUE_SEARCH_FIELD,
                                        searchValue,
                                        Field.Store.NO,
                                        Field.Index.ANALYZED,
                                        luceneOptions.getTermVector()
                                );
                                document.add(searchTokens);

                                // searchable and navigatable terms for global search full phrase
                                final Field searchPhrase = new Field(
                                        ProductSearchQueryBuilder.ATTRIBUTE_VALUE_SEARCHPHRASE_FIELD,
                                        searchValue,
                                        Field.Store.NO,
                                        Field.Index.NOT_ANALYZED,
                                        luceneOptions.getTermVector()
                                );
                                searchPhrase.setBoost(1.2f); // +20% for full phrases
                                document.add(searchPhrase);
                            }

                        }

                    }
                }

                if (navigation) {
                    // strict attribute navigation only for filtered navigation
                    document.add(new Field(
                            "facet_" + code,
                            cleanFacetValue(attrValue.getVal()),
                            Field.Store.NO,
                            Field.Index.NOT_ANALYZED,
                            luceneOptions.getTermVector()
                    ));
                }

                final boolean stored = storeAttrs.contains(code);

                if (stored) {
                    if (storedAttributes == null) {
                        storedAttributes = new StoredAttributesImpl();
                    }
                    storedAttributes.putValue(code, attrValue.getVal(), attrValue.getDisplayVal());
                }

            }

            if (storedAttributes != null && !storedAttributes.getAllValues().isEmpty()) {
                document.add(new Field(
                        ProductSearchQueryBuilder.ATTRIBUTE_VALUE_STORE_FIELD,
                        storedAttributes.toString(),
                        Field.Store.YES,
                        Field.Index.NOT_ANALYZED,
                        Field.TermVector.NO
                ));
            }

        }

    }

    private List<String> getSearchValue(final AttrValue attrValue) {
        final List<String> values = new ArrayList<String>();
        if (StringUtils.isNotBlank(attrValue.getDisplayVal())) {
            final I18NModel model = new StringI18NModel(attrValue.getDisplayVal());
            for (final String value : model.getAllValues().values()) {
                values.add(value.toLowerCase());
            }
        }
        values.add(attrValue.getVal().toLowerCase());
        return values;
    }

    private NavigatableAttributesSupport getNavigatableAttributesSupport() {
        return HibernateSearchBridgeStaticLocator.getNavigatableAttributesSupport();
    }

    private String cleanFacetValue(final String val) {
        return val.replace('/', ' '); // replace all forward slashes since they get decoded into paths
    }


}
