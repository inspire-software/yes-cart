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

package org.yes.cart.domain.query.impl;

import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.springframework.util.CollectionUtils;
import org.yes.cart.domain.query.ProductSearchQueryBuilder;

import java.util.List;

/**
 * Analysed terms are all lower case - there is no point in using fuzzy to search against them
 * as it is case sensitive. Make sure that analysed field are "word.toLowerCase()" matches.
 *
 * Default lucene fuzzy is 0.5 - i.e. 50% match. Below settings are very project specific
 * and have to be fine-tuned.
 *
 * For attributes 0.65 - i.e. up to 3.5 letters wrong in a 10 letter word, more than this could be
 * damaging to search especially in Russian language. The worst example would
 * be fuzzy 0.5 on color, which is Russian has common ending for adjectives
 * in all colors
 *
 * In order to provide better matches we use the following strict boosts (phrase/words):
 * Name:            2.5/2.5
 * Display name:    4.0/3.0
 * Brand:           5.0/3.5  (brand should be more important than name)
 * CODE:           10.0/4.0  (ensure exact match)
 * Primary attr:   15.0/     (ensure exact match)
 * Attributes:      3.5/2.75 (make sure attributes have high significance but not over name)
 *
 * In order to provide better matches we use the following relaxed boosts:
 * Name:            2.5
 * Display name:    3.0
 * Brand:           3.5 (brand should be more important than name)
 * CODE:            4.0 (ensure exact match)
 * CODE Stems:      1.0
 * Attributes:      2.75 (make sure attributes have high significance but not over name)
 * Description:     0.5  (Descriptions can be noisy so suppress its significance, no fuzzy)
 *
 * User: denispavlov
 * Date: 16/11/2014
 * Time: 16:19
 */
public class KeywordProductSearchQueryBuilder extends AbstractSearchQueryBuilderImpl implements ProductSearchQueryBuilder {

    private int minWordLength = 2;

    /**
     * {@inheritDoc}
     */
    public Query createStrictQuery(final long shopId, final String parameter, final Object value) {

        if (!isEmptyValue(value)) {

            final List<String> words = SearchPhrazeUtil.splitForSearch(String.valueOf(value), minWordLength);
            if (CollectionUtils.isEmpty(words)) {
                return null;
            }

            final BooleanQuery aggregateQuery = new BooleanQuery();

            final String escapedSearchValue = escapeValue(value);
            final String escapedSearchValueLower = escapedSearchValue.toLowerCase();

            final BooleanQuery phrazeQuery = new BooleanQuery();

            phrazeQuery.add(createFuzzyQuery(PRODUCT_NAME_FIELD, escapedSearchValue, 0.95f, 2.5f), BooleanClause.Occur.SHOULD);
            phrazeQuery.add(createFuzzyQuery(PRODUCT_DISPLAYNAME_FIELD, escapedSearchValue, 0.9f, 4f), BooleanClause.Occur.SHOULD);
            phrazeQuery.add(createTermQuery(BRAND_FIELD, escapedSearchValueLower, 5f), BooleanClause.Occur.SHOULD);

            phrazeQuery.add(createFuzzyQuery(PRODUCT_CATEGORYNAME_FIELD, escapedSearchValue, 0.8f, 4.5f), BooleanClause.Occur.SHOULD);

            phrazeQuery.add(createTermQuery(PRODUCT_CODE_FIELD, escapedSearchValue, 10f), BooleanClause.Occur.SHOULD);
            phrazeQuery.add(createTermQuery(PRODUCT_MANUFACTURER_CODE_FIELD, escapedSearchValue, 10f), BooleanClause.Occur.SHOULD);
            phrazeQuery.add(createTermQuery(SKU_PRODUCT_CODE_FIELD, escapedSearchValue, 10f), BooleanClause.Occur.SHOULD);
            phrazeQuery.add(createTermQuery(SKU_PRODUCT_MANUFACTURER_CODE_FIELD, escapedSearchValue, 10f), BooleanClause.Occur.SHOULD);

            phrazeQuery.add(createTermQuery(ATTRIBUTE_VALUE_SEARCHPRIMARY_FIELD, escapedSearchValueLower, 15f), BooleanClause.Occur.SHOULD);
            phrazeQuery.add(createTermQuery(ATTRIBUTE_VALUE_SEARCHPHRASE_FIELD, escapedSearchValueLower, 3.5f), BooleanClause.Occur.SHOULD);

            aggregateQuery.add(phrazeQuery, BooleanClause.Occur.SHOULD);

            if (words.size() > 1 || !words.get(0).equals(escapedSearchValue)) {
                for (String word : words) {

                    final String escapedWord = escapeValue(word.toLowerCase());

                    final BooleanQuery wordQuery = new BooleanQuery();

//                    wordQuery.add(createFuzzyQuery(PRODUCT_NAME_FIELD, escapedWord, 0.6f, 2.5f), BooleanClause.Occur.SHOULD);
//                    wordQuery.add(createFuzzyQuery(PRODUCT_DISPLAYNAME_FIELD, escapedWord, 0.6f, 3f), BooleanClause.Occur.SHOULD);
                    wordQuery.add(createTermQuery(BRAND_FIELD, escapedWord, 5f), BooleanClause.Occur.SHOULD);

                    wordQuery.add(createTermQuery(PRODUCT_CATEGORYNAME_STEM_FIELD, escapedWord, 4.5f), BooleanClause.Occur.SHOULD);

                    wordQuery.add(createTermQuery(PRODUCT_CODE_FIELD, escapedWord, 4f), BooleanClause.Occur.SHOULD);
                    wordQuery.add(createTermQuery(PRODUCT_MANUFACTURER_CODE_FIELD, escapedWord, 4f), BooleanClause.Occur.SHOULD);
                    wordQuery.add(createTermQuery(SKU_PRODUCT_CODE_FIELD, escapedWord, 4f), BooleanClause.Occur.SHOULD);
                    wordQuery.add(createTermQuery(SKU_PRODUCT_MANUFACTURER_CODE_FIELD, escapedWord, 4f), BooleanClause.Occur.SHOULD);

                    wordQuery.add(createTermQuery(ATTRIBUTE_VALUE_SEARCHPHRASE_FIELD, escapedWord, 2.75f), BooleanClause.Occur.SHOULD);

                    aggregateQuery.add(wordQuery, BooleanClause.Occur.SHOULD);
                }
            }

            return aggregateQuery;

        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Query createRelaxedQuery(final long shopId, final String parameter, final Object value) {

        if (value != null) {

            final List<String> words = SearchPhrazeUtil.splitForSearch(String.valueOf(value), minWordLength);
            if (CollectionUtils.isEmpty(words)) {
                return null;
            }

            final BooleanQuery aggregateQuery = new BooleanQuery();

            for (String word : words) {

                final String escapedWord = escapeValue(word.toLowerCase()); // use lower case to increase chance of match

                final BooleanQuery wordQuery = new BooleanQuery();

                wordQuery.add(createFuzzyQuery(PRODUCT_NAME_FIELD, escapedWord, 0.85f, 2.5f), BooleanClause.Occur.SHOULD);
                wordQuery.add(createFuzzyQuery(PRODUCT_DISPLAYNAME_FIELD, escapedWord, 0.85f, 3f), BooleanClause.Occur.SHOULD);
                wordQuery.add(createFuzzyQuery(PRODUCT_DISPLAYNAME_STEM_FIELD, escapedWord, 0.85f, 2.5f), BooleanClause.Occur.SHOULD);
                wordQuery.add(createFuzzyQuery(BRAND_FIELD, escapedWord, 0.85f, 3.5f), BooleanClause.Occur.SHOULD);

                wordQuery.add(createFuzzyQuery(PRODUCT_CATEGORYNAME_FIELD, escapedWord, 0.8f, 3.5f), BooleanClause.Occur.SHOULD);
                wordQuery.add(createFuzzyQuery(PRODUCT_CATEGORYNAME_STEM_FIELD, escapedWord, 0.8f, 2.0f), BooleanClause.Occur.SHOULD);

                wordQuery.add(createFuzzyQuery(PRODUCT_CODE_FIELD, escapedWord, 0.7f, 4f), BooleanClause.Occur.SHOULD);
                wordQuery.add(createFuzzyQuery(PRODUCT_MANUFACTURER_CODE_FIELD, escapedWord, 0.7f, 4f), BooleanClause.Occur.SHOULD);
                wordQuery.add(createFuzzyQuery(SKU_PRODUCT_CODE_FIELD, escapedWord, 0.7f, 4f), BooleanClause.Occur.SHOULD);
                wordQuery.add(createFuzzyQuery(SKU_PRODUCT_MANUFACTURER_CODE_FIELD, escapedWord, 0.7f, 4f), BooleanClause.Occur.SHOULD);

                wordQuery.add(createFuzzyQuery(PRODUCT_CODE_STEM_FIELD, escapedWord, 0.75f, 1.0f), BooleanClause.Occur.SHOULD);
                wordQuery.add(createFuzzyQuery(PRODUCT_MANUFACTURER_CODE_STEM_FIELD, escapedWord, 0.75f, 1.0f), BooleanClause.Occur.SHOULD);
                wordQuery.add(createFuzzyQuery(SKU_PRODUCT_CODE_STEM_FIELD, escapedWord, 0.75f, 1.0f), BooleanClause.Occur.SHOULD);
                wordQuery.add(createFuzzyQuery(SKU_PRODUCT_MANUFACTURER_CODE_STEM_FIELD, escapedWord, 0.75f, 1.0f), BooleanClause.Occur.SHOULD);

                wordQuery.add(createFuzzyQuery(ATTRIBUTE_VALUE_SEARCH_FIELD, escapedWord, 0.65f, 2.75f), BooleanClause.Occur.SHOULD);

                wordQuery.add(createTermQuery(PRODUCT_DESCRIPTION_STEM_FIELD, escapedWord, 0.5f), BooleanClause.Occur.SHOULD);

                aggregateQuery.add(wordQuery, BooleanClause.Occur.SHOULD);
            }

            return aggregateQuery;

        }

        return null;
    }



    /**
     * Set minimum length of a searchable word.
     *
     * @param minWordLength min char length
     */
    public void setMinWordLength(final int minWordLength) {
        this.minWordLength = minWordLength;
    }


}
