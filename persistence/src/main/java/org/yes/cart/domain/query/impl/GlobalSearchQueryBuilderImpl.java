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
            final Long shopId,
            final boolean abatement) throws IllegalArgumentException {

        final BooleanQuery currentQuery = new BooleanQuery();

        currentQuery.add(new TermQuery(new Term(PRODUCT_SHOP_FIELD, shopId.toString())),
                BooleanClause.Occur.MUST);

        final List<String> words = SearchPhrazeUtil.splitForSearch(searchPhraze);

        enrichQueryWithSearchWords(words, currentQuery, abatement);

        return currentQuery;

    }

    public BooleanQuery createQuerySearchInCategories(
            final String searchPhraze,
            final List<Long> categories,
            final boolean abatement) throws IllegalArgumentException {

        final BooleanQuery query = new BooleanQuery();

        if (searchPhraze != null) {

            final List<String> words = SearchPhrazeUtil.splitForSearch(searchPhraze);

            if (categories != null) {

                for (Long category : categories) {

                    final BooleanQuery currentQuery = createQuery(words, category, abatement);

                    query.add(currentQuery, BooleanClause.Occur.SHOULD);

                }

            }

        }

        return query;
    }

    public BooleanQuery createQuerySearchInCategory(final String searchPhraze, final Long category, final boolean abatement) {
        return createQuery(SearchPhrazeUtil.splitForSearch(searchPhraze), category, abatement);
    }

    private BooleanQuery createQuery(final List<String> words, final Long category, final boolean stem) {
        BooleanQuery currentQuery = new BooleanQuery();

        if (category != null) {
            currentQuery.add(new TermQuery(new Term(PRODUCT_CATEGORY_FIELD, category.toString())),
                    BooleanClause.Occur.MUST);
        }

        enrichQueryWithSearchWords(words, currentQuery, stem);

        return currentQuery;
    }

    private void enrichQueryWithSearchWords(final List<String> words, final BooleanQuery currentQuery, final boolean abatement) {
        for (String word : words) {
            BooleanQuery termQuery = new BooleanQuery();
            /*
               Analysed terms are all lower case - there is no point in using fuzzy to search against them
               as it is case sensitive. Make sure that analysed field are "word.toLowerCase()" matches.

               Default lucene fuzzy is 0.5 - i.e. 50% match. Below settings are very project specific
               and have to be fine-tuned.

               For attributes 0.65 - i.e. up to 3.5 letters wrong in a 10 letter word, more than this could be
                                     damaging to search especially in Russian language. The worst example would
                                     be fuzzy 0.5 on color, which is Russian has common ending for adjectives
                                     in all colors

               In order to provide better matches we use the following boosts:
               Name:            2.5
               Display name:    3.0
               Brand:           3.0
               CODE:            4.0
               Attributes:      1.0
               CODE Stems:      1.0
               Description:     0.4 (Descriptions can be noisy so suppress its significance)
             */

            if (abatement) {

                final FuzzyQuery name = new FuzzyQuery(new Term(PRODUCT_NAME_FIELD, word));
                name.setBoost(2.5f);
                termQuery.add(name, BooleanClause.Occur.SHOULD);
                final FuzzyQuery dname = new FuzzyQuery(new Term(PRODUCT_DISPLAYNAME_FIELD, word));
                dname.setBoost(3f);
                termQuery.add(dname, BooleanClause.Occur.SHOULD);
                final FuzzyQuery brand = new FuzzyQuery(new Term(BRAND_FIELD, word.toLowerCase()), 0.7f);
                brand.setBoost(3f);
                termQuery.add(brand, BooleanClause.Occur.SHOULD);
                final FuzzyQuery pcode = new FuzzyQuery(new Term(PRODUCT_CODE_FIELD, word), 0.7f);
                pcode.setBoost(4f);
                termQuery.add(pcode, BooleanClause.Occur.SHOULD);
                final FuzzyQuery scode = new FuzzyQuery(new Term(SKU_PRODUCT_CODE_FIELD, word), 0.7f);
                scode.setBoost(4f);
                termQuery.add(scode, BooleanClause.Occur.SHOULD);

                termQuery.add(new FuzzyQuery(new Term(ATTRIBUTE_VALUE_SEARCH_FIELD, word), 0.65f), BooleanClause.Occur.SHOULD);
                termQuery.add(new FuzzyQuery(new Term(SKU_ATTRIBUTE_VALUE_SEARCH_FIELD, word), 0.65f), BooleanClause.Occur.SHOULD);

                termQuery.add(new FuzzyQuery(new Term(PRODUCT_CODE_STEM_FIELD, word.toLowerCase()), 0.75f), BooleanClause.Occur.SHOULD);
                termQuery.add(new FuzzyQuery(new Term(SKU_PRODUCT_CODE_STEM_FIELD, word.toLowerCase()), 0.75f), BooleanClause.Occur.SHOULD);

                final FuzzyQuery desc = new FuzzyQuery(new Term(PRODUCT_DESCRIPTION_FIELD, word.toLowerCase()), 0.85f);
                desc.setBoost(0.4f);
                termQuery.add(desc, BooleanClause.Occur.SHOULD);

            } else {

                final FuzzyQuery name = new FuzzyQuery(new Term(PRODUCT_NAME_FIELD, word), 0.6f);
                name.setBoost(2.5f);
                termQuery.add(name, BooleanClause.Occur.SHOULD);
                final FuzzyQuery dname = new FuzzyQuery(new Term(PRODUCT_DISPLAYNAME_FIELD, word), 0.6f);
                dname.setBoost(3f);
                termQuery.add(dname, BooleanClause.Occur.SHOULD);
                final FuzzyQuery brand = new FuzzyQuery(new Term(BRAND_FIELD, word.toLowerCase()), 0.8f);
                brand.setBoost(3f);
                termQuery.add(brand, BooleanClause.Occur.SHOULD);

                final FuzzyQuery pcode = new FuzzyQuery(new Term(PRODUCT_CODE_FIELD, word), 0.8f);
                pcode.setBoost(4f);
                termQuery.add(pcode, BooleanClause.Occur.SHOULD);
                final FuzzyQuery scode = new FuzzyQuery(new Term(SKU_PRODUCT_CODE_FIELD, word), 0.8f);
                scode.setBoost(4f);
                termQuery.add(scode, BooleanClause.Occur.SHOULD);

                termQuery.add(new FuzzyQuery(new Term(ATTRIBUTE_VALUE_SEARCH_FIELD, word), 0.65f), BooleanClause.Occur.SHOULD);
                termQuery.add(new FuzzyQuery(new Term(SKU_ATTRIBUTE_VALUE_SEARCH_FIELD, word), 0.65f), BooleanClause.Occur.SHOULD);

            }

            currentQuery.add(termQuery, BooleanClause.Occur.MUST);
        }
    }

}
