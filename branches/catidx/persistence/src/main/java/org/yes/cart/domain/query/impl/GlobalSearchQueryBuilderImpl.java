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

package org.yes.cart.domain.query.impl;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.TermQuery;
import org.yes.cart.domain.query.ProductSearchQueryBuilder;

import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 08-May-2011
 * Time: 11:12:54
 * This query builder used for perform search on the whole shop.
 */
public class GlobalSearchQueryBuilderImpl implements ProductSearchQueryBuilder {

    public BooleanQuery createQuerySearchInShop(
            final String searchPhraze,
            final Long shopId) throws IllegalArgumentException {

        final BooleanQuery currentQuery = new BooleanQuery();

        currentQuery.add(new TermQuery(new Term(PRODUCT_SHOP_FIELD, shopId.toString())),
                BooleanClause.Occur.MUST);

        final List<String> words = SearchPhrazeUtil.splitForSearch(searchPhraze);

        enrichQueryWithSearchWords(words, currentQuery);

        return currentQuery;

    }

    public BooleanQuery createQuerySearchInCategories(
            final String searchPhraze,
            final List<Long> categories) throws IllegalArgumentException {

        final BooleanQuery query = new BooleanQuery();

        if (searchPhraze != null) {

            final List<String> words = SearchPhrazeUtil.splitForSearch(searchPhraze);

            if (categories != null) {

                for (Long category : categories) {

                    final BooleanQuery currentQuery = createQuery(words, category);

                    query.add(currentQuery, BooleanClause.Occur.SHOULD);

                }

            }

        }

        return query;
    }

    public BooleanQuery createQuerySearchInCategory(final String searchPhraze, final Long category) {
        return createQuery(SearchPhrazeUtil.splitForSearch(searchPhraze), category);
    }

    private BooleanQuery createQuery(final List<String> words, final Long category) {
        BooleanQuery currentQuery = new BooleanQuery();

        if (category != null) {
            currentQuery.add(new TermQuery(new Term(PRODUCT_CATEGORY_FIELD, category.toString())),
                    BooleanClause.Occur.MUST);
        }

        enrichQueryWithSearchWords(words, currentQuery);

        return currentQuery;
    }

    private void enrichQueryWithSearchWords(final List<String> words, final BooleanQuery currentQuery) {
        for (String word : words) {
            BooleanQuery termQuery = new BooleanQuery();
            termQuery.add(new FuzzyQuery(new Term(PRODUCT_NAME_FIELD, word)), BooleanClause.Occur.SHOULD);
            termQuery.add(new FuzzyQuery(new Term(PRODUCT_DISPLAYNAME_FIELD, word)), BooleanClause.Occur.SHOULD);
            termQuery.add(new FuzzyQuery(new Term(PRODUCT_DESCIPTION_FIELD, word)), BooleanClause.Occur.SHOULD);
            termQuery.add(new FuzzyQuery(new Term(BRAND_FIELD, word)), BooleanClause.Occur.SHOULD);
            termQuery.add(new FuzzyQuery(new Term(ATTRIBUTE_VALUE_SEARCH_FIELD, word)), BooleanClause.Occur.SHOULD);
            termQuery.add(new FuzzyQuery(new Term(SKU_ATTRIBUTE_VALUE_SEARCH_FIELD, word)), BooleanClause.Occur.SHOULD);
            termQuery.add(new FuzzyQuery(new Term(PRODUCT_CODE_FIELD, word), 0.9f), BooleanClause.Occur.SHOULD);
            termQuery.add(new FuzzyQuery(new Term(SKU_PRODUCT_CODE_FIELD, word), 0.9f), BooleanClause.Occur.SHOULD);
            currentQuery.add(termQuery, BooleanClause.Occur.MUST);
        }
    }

}
