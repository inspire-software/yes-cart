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
public class KeywordProductSearchQueryBuilder extends AbstractKeywordSearchQueryBuilder implements ProductSearchQueryBuilder<Query> {

    public KeywordProductSearchQueryBuilder() {
        super(true);
    }

    @Override
    protected Query createKeywordQueryExact(final String term) {

        final String escapedTerm = escapeValue(term);

        final BooleanQuery.Builder query = new BooleanQuery.Builder();

        // product name weight (3)
        query.add(createTermQuery(PRODUCT_NAME_FIELD, escapedTerm, 3f), BooleanClause.Occur.SHOULD);
        query.add(createTermQuery(PRODUCT_DISPLAYNAME_FIELD, escapedTerm, 3f), BooleanClause.Occur.SHOULD);

        // product brand weight (5) must be higher than name (e.g. "hp notebook" must show hp brand first)
        query.add(createTermQuery(BRAND_FIELD, escapedTerm, 5f), BooleanClause.Occur.SHOULD);

        // category name weight (5~2) higher then name as names sometimes contains fuzzy terms (e.g. "notebook with usb")
        query.add(createFuzzyQuery(PRODUCT_CATEGORYNAME_FIELD, escapedTerm, 2, 5f), BooleanClause.Occur.SHOULD);

        // product type weight (7~2) higher then name and category for searching general type products (e.g. "usb stick")
        query.add(createFuzzyQuery(PRODUCT_TYPE_FIELD, escapedTerm, 2, 7f), BooleanClause.Occur.SHOULD);

        // product code matches (10) so that exact code match brings top result
        query.add(createTermQuery(PRODUCT_CODE_FIELD, escapedTerm,  10f), BooleanClause.Occur.SHOULD);
        query.add(createTermQuery(PRODUCT_MANUFACTURER_CODE_FIELD, escapedTerm, 10f), BooleanClause.Occur.SHOULD);
        query.add(createTermQuery(SKU_PRODUCT_CODE_FIELD, escapedTerm, 10f), BooleanClause.Occur.SHOULD);
        query.add(createTermQuery(SKU_PRODUCT_MANUFACTURER_CODE_FIELD, escapedTerm, 10f), BooleanClause.Occur.SHOULD);

        // attribute primary (10)
        query.add(createTermQuery(ATTRIBUTE_VALUE_SEARCHPRIMARY_FIELD, escapedTerm, 10f), BooleanClause.Occur.SHOULD);

        // attribute general (4/2~1)
        query.add(createTermQuery(ATTRIBUTE_VALUE_SEARCHPHRASE_FIELD, escapedTerm, 4f), BooleanClause.Occur.SHOULD);
        query.add(createFuzzyQuery(ATTRIBUTE_VALUE_SEARCHPHRASE_FIELD, escapedTerm, 1, 2f), BooleanClause.Occur.SHOULD);

        return query.build();

    }

    @Override
    protected Query createKeywordQueryTerm(final String term) {

        final String escapedTerm = escapeValue(term);

        final BooleanQuery.Builder query = new BooleanQuery.Builder();

        // product name weight (3~2/1~1)
        query.add(createFuzzyQuery(PRODUCT_NAME_FIELD, escapedTerm, 2, 3f), BooleanClause.Occur.SHOULD);
        query.add(createFuzzyQuery(PRODUCT_NAME_STEM_FIELD, escapedTerm, 1, 1f), BooleanClause.Occur.SHOULD);
        query.add(createFuzzyQuery(PRODUCT_DISPLAYNAME_FIELD, escapedTerm, 2, 3f), BooleanClause.Occur.SHOULD);
        query.add(createFuzzyQuery(PRODUCT_DISPLAYNAME_STEM_FIELD, escapedTerm, 1, 1f), BooleanClause.Occur.SHOULD);

        // product brand weight (5) must be higher than name (e.g. "hp notebook" must show hp brand first)
        query.add(createTermQuery(BRAND_FIELD, escapedTerm, 5f), BooleanClause.Occur.SHOULD);

        // category name weight (5~2/1.5~1) higher then name as names sometimes contains fuzzy terms (e.g. "notebook with usb")
        query.add(createFuzzyQuery(PRODUCT_CATEGORYNAME_FIELD, escapedTerm, 2, 5f), BooleanClause.Occur.SHOULD);
        query.add(createFuzzyQuery(PRODUCT_CATEGORYNAME_STEM_FIELD, escapedTerm, 1, 1.5f), BooleanClause.Occur.SHOULD);

        // product type weight (7~2/2~1) higher then name and category for searching general type products (e.g. "usb stick")
        query.add(createFuzzyQuery(PRODUCT_TYPE_FIELD, escapedTerm, 2, 7f), BooleanClause.Occur.SHOULD);
        query.add(createFuzzyQuery(PRODUCT_TYPE_STEM_FIELD, escapedTerm, 1, 2f), BooleanClause.Occur.SHOULD);

        // product code matches (10/7~1/2~1) so that exact code match brings top result
        query.add(createTermQuery(PRODUCT_CODE_FIELD, escapedTerm,  10f), BooleanClause.Occur.SHOULD);
        query.add(createFuzzyQuery(PRODUCT_CODE_FIELD, escapedTerm,  1,7f), BooleanClause.Occur.SHOULD);
        query.add(createFuzzyQuery(PRODUCT_CODE_STEM_FIELD, escapedTerm, 1, 2f), BooleanClause.Occur.SHOULD);
        query.add(createTermQuery(PRODUCT_MANUFACTURER_CODE_FIELD, escapedTerm, 10f), BooleanClause.Occur.SHOULD);
        query.add(createFuzzyQuery(PRODUCT_MANUFACTURER_CODE_FIELD, escapedTerm, 1, 7f), BooleanClause.Occur.SHOULD);
        query.add(createFuzzyQuery(PRODUCT_MANUFACTURER_CODE_STEM_FIELD, escapedTerm, 1, 2f), BooleanClause.Occur.SHOULD);
        query.add(createTermQuery(SKU_PRODUCT_CODE_FIELD, escapedTerm, 10f), BooleanClause.Occur.SHOULD);
        query.add(createFuzzyQuery(SKU_PRODUCT_CODE_FIELD, escapedTerm, 1,7f), BooleanClause.Occur.SHOULD);
        query.add(createFuzzyQuery(SKU_PRODUCT_CODE_STEM_FIELD, escapedTerm, 1, 2f), BooleanClause.Occur.SHOULD);
        query.add(createTermQuery(SKU_PRODUCT_MANUFACTURER_CODE_FIELD, escapedTerm, 10f), BooleanClause.Occur.SHOULD);
        query.add(createFuzzyQuery(SKU_PRODUCT_MANUFACTURER_CODE_FIELD, escapedTerm, 1, 7f), BooleanClause.Occur.SHOULD);
        query.add(createFuzzyQuery(SKU_PRODUCT_MANUFACTURER_CODE_STEM_FIELD, escapedTerm, 1,2f), BooleanClause.Occur.SHOULD);

        // attribute primary (10/7~1)
        query.add(createTermQuery(ATTRIBUTE_VALUE_SEARCHPRIMARY_FIELD, escapedTerm, 10f), BooleanClause.Occur.SHOULD);
        query.add(createFuzzyQuery(ATTRIBUTE_VALUE_SEARCHPRIMARY_FIELD, escapedTerm, 1, 7f), BooleanClause.Occur.SHOULD);

        // attribute general (4/2~2/1~1)
        query.add(createTermQuery(ATTRIBUTE_VALUE_SEARCHPHRASE_FIELD, escapedTerm, 4f), BooleanClause.Occur.SHOULD);
        query.add(createFuzzyQuery(ATTRIBUTE_VALUE_SEARCHPHRASE_FIELD, escapedTerm, 2, 2f), BooleanClause.Occur.SHOULD);
        query.add(createFuzzyQuery(ATTRIBUTE_VALUE_SEARCH_FIELD, escapedTerm, 1, 1f), BooleanClause.Occur.SHOULD);

        return query.build();

    }

    @Override
    protected Query createKeywordQueryStem(final String stem) {

        final String escapedStem = escapeValue(stem);

        final BooleanQuery.Builder query = new BooleanQuery.Builder();

        // product name weight (3/1~1)
        query.add(createTermQuery(PRODUCT_NAME_FIELD, escapedStem, 3f), BooleanClause.Occur.SHOULD);
        query.add(createFuzzyQuery(PRODUCT_NAME_STEM_FIELD, escapedStem, 1, 1f), BooleanClause.Occur.SHOULD);
        query.add(createTermQuery(PRODUCT_DISPLAYNAME_FIELD, escapedStem, 3f), BooleanClause.Occur.SHOULD);
        query.add(createFuzzyQuery(PRODUCT_DISPLAYNAME_STEM_FIELD, escapedStem, 1, 1f), BooleanClause.Occur.SHOULD);

        // product brand weight (5) must be higher than name (e.g. "hp notebook" must show hp brand first)
        query.add(createTermQuery(BRAND_FIELD, escapedStem, 5f), BooleanClause.Occur.SHOULD);

        // category name weight (5/1.5~1) higher then name as names sometimes contains fuzzy terms (e.g. "notebook with usb")
        query.add(createTermQuery(PRODUCT_CATEGORYNAME_FIELD, escapedStem, 5f), BooleanClause.Occur.SHOULD);
        query.add(createFuzzyQuery(PRODUCT_CATEGORYNAME_STEM_FIELD, escapedStem, 1, 1.5f), BooleanClause.Occur.SHOULD);

        // product type weight (7/2~1) higher then name and category for searching general type products (e.g. "usb stick")
        query.add(createTermQuery(PRODUCT_TYPE_FIELD, escapedStem, 7f), BooleanClause.Occur.SHOULD);
        query.add(createFuzzyQuery(PRODUCT_TYPE_STEM_FIELD, escapedStem, 1, 1f), BooleanClause.Occur.SHOULD);

        // product code matches (10/2~1) so that exact code match brings top result
        query.add(createTermQuery(PRODUCT_CODE_FIELD, escapedStem, 10f), BooleanClause.Occur.SHOULD);
        query.add(createFuzzyQuery(PRODUCT_CODE_STEM_FIELD, escapedStem, 1, 2f), BooleanClause.Occur.SHOULD);
        query.add(createTermQuery(PRODUCT_MANUFACTURER_CODE_FIELD, escapedStem, 10f), BooleanClause.Occur.SHOULD);
        query.add(createFuzzyQuery(PRODUCT_MANUFACTURER_CODE_STEM_FIELD, escapedStem, 1, 2f), BooleanClause.Occur.SHOULD);
        query.add(createTermQuery(SKU_PRODUCT_CODE_FIELD, escapedStem, 10f), BooleanClause.Occur.SHOULD);
        query.add(createFuzzyQuery(SKU_PRODUCT_CODE_STEM_FIELD, escapedStem, 1, 2f), BooleanClause.Occur.SHOULD);
        query.add(createTermQuery(SKU_PRODUCT_MANUFACTURER_CODE_FIELD, escapedStem, 10f), BooleanClause.Occur.SHOULD);
        query.add(createFuzzyQuery(SKU_PRODUCT_MANUFACTURER_CODE_STEM_FIELD, escapedStem, 1,2f), BooleanClause.Occur.SHOULD);

        // attribute primary (10/7~1)
        query.add(createTermQuery(ATTRIBUTE_VALUE_SEARCHPRIMARY_FIELD, escapedStem, 10f), BooleanClause.Occur.SHOULD);
        query.add(createFuzzyQuery(ATTRIBUTE_VALUE_SEARCHPRIMARY_FIELD, escapedStem, 1, 7f), BooleanClause.Occur.SHOULD);

        // attribute general (4/2~1/1)
        query.add(createTermQuery(ATTRIBUTE_VALUE_SEARCHPHRASE_FIELD, escapedStem, 4f), BooleanClause.Occur.SHOULD);
        query.add(createFuzzyQuery(ATTRIBUTE_VALUE_SEARCHPHRASE_FIELD, escapedStem, 1, 2f), BooleanClause.Occur.SHOULD);
        query.add(createTermQuery(ATTRIBUTE_VALUE_SEARCH_FIELD, escapedStem, 1.5f), BooleanClause.Occur.SHOULD);

        return query.build();

    }

    

}
