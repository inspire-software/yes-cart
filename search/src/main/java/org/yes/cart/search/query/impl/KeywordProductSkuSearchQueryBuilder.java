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

package org.yes.cart.search.query.impl;

import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.yes.cart.search.query.ProductSearchQueryBuilder;

/**
 * Analysed terms are all lower case - there is no point in using fuzzy to search against them
 * as it is case sensitive. Make sure that analysed field are "word.toLowerCase()" matches.
 *
 * User: denispavlov
 * Date: 16/11/2014
 * Time: 16:19
 */
public class KeywordProductSkuSearchQueryBuilder extends AbstractKeywordSearchQueryBuilder implements ProductSearchQueryBuilder<Query> {

    public KeywordProductSkuSearchQueryBuilder() {
        super(false);
    }

    private Query createKeywordQueryMaxMatch(final String term) {

        final String escapedTerm = escapeValue(term);

        final BooleanQuery.Builder query = new BooleanQuery.Builder();

        // product name weight (3~2/1~1)
        query.add(createFuzzyQuery(PRODUCT_NAME_FIELD, escapedTerm, 2, 3f), BooleanClause.Occur.SHOULD);
        query.add(createFuzzyQuery(PRODUCT_NAME_STEM_FIELD, escapedTerm, 1, 1f), BooleanClause.Occur.SHOULD);
        query.add(createFuzzyQuery(PRODUCT_DISPLAYNAME_FIELD, escapedTerm, 2, 3f), BooleanClause.Occur.SHOULD);
        query.add(createFuzzyQuery(PRODUCT_DISPLAYNAME_STEM_FIELD, escapedTerm, 1, 1f), BooleanClause.Occur.SHOULD);

        // product code matches (5/4~1) so that exact code match brings top result
        query.add(createFuzzyQuery(SKU_PRODUCT_CODE_FIELD_SEARCH, escapedTerm, 1,5f), BooleanClause.Occur.SHOULD);
        query.add(createFuzzyQuery(SKU_PRODUCT_CODE_STEM_FIELD, escapedTerm, 1, 2f), BooleanClause.Occur.SHOULD);
        query.add(createFuzzyQuery(SKU_PRODUCT_MANUFACTURER_CODE_FIELD_SEARCH, escapedTerm, 1, 5f), BooleanClause.Occur.SHOULD);
        query.add(createFuzzyQuery(SKU_PRODUCT_MANUFACTURER_CODE_STEM_FIELD, escapedTerm, 1,2f), BooleanClause.Occur.SHOULD);

        // attribute primary (4~1)
        query.add(createFuzzyQuery(ATTRIBUTE_VALUE_SEARCHPRIMARY_FIELD, escapedTerm, 1, 5f), BooleanClause.Occur.SHOULD);

        // attribute general (5~2/5~1)
        query.add(createFuzzyQuery(ATTRIBUTE_VALUE_SEARCHPHRASE_FIELD, escapedTerm, 2, 5f), BooleanClause.Occur.SHOULD);
        query.add(createFuzzyQuery(ATTRIBUTE_VALUE_SEARCH_FIELD, escapedTerm, 1, 5f), BooleanClause.Occur.SHOULD);

        return query.build();

    }

    @Override
    protected Query createKeywordQueryExact(final String term) {

        return createKeywordQueryMaxMatch(term);

    }

    @Override
    protected Query createKeywordQueryTerm(final String term) {

        return createKeywordQueryMaxMatch(term);

    }

    @Override
    protected Query createKeywordQueryStem(final String stem) {

        return createKeywordQueryMaxMatch(stem);

    }



}
