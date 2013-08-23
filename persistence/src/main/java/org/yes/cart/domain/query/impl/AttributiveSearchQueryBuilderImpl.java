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
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermRangeQuery;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.query.ProductSearchQueryBuilder;

import java.util.List;
import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 16:13:01
 * <p/>
 * <p/>
 * Use in case of if particular category has a particular product type.
 */
public class AttributiveSearchQueryBuilderImpl extends ProductsInCategoryQueryBuilderImpl
        implements ProductSearchQueryBuilder {

    /**
     * Creates a query to getByKey the products with specified attribute name and values
     * in particular categories
     *
     * @param categories     given set of category ids.
     * @param attributeName  attribute name
     * @param attributeValue value of attribute
     * @return boolean query
     */
    public BooleanQuery createQuery(
            final List<Long> categories,
            final String attributeName,
            final String attributeValue) {

        return addKeyValue(
                super.createQuery(categories),
                attributeName,
                attributeValue);

    }

    /**
     * Creates a query to getByKey the products with specified attribute name and values
     * in particular categories
     *
     * @param categories          given set of category ids.
     * @param attributeName       attribute name
     * @param attributeValueRange inclusive range of values. hi and lo
     * @return boolean query
     */
    public BooleanQuery createQuery(
            final List<Long> categories,
            final String attributeName,
            final Pair<String, String> attributeValueRange) {

        final BooleanQuery query = super.createQuery(categories);

        return addKeyRangeValues(
                query,
                attributeName,
                attributeValueRange.getFirst(),
                attributeValueRange.getSecond());


        /*final BooleanQuery query = new BooleanQuery();
        for (Long category : categories) {
            BooleanQuery booleanQuery = new BooleanQuery();
            booleanQuery.add(
                    new TermQuery(new Term(PRODUCT_CATEGORY_FIELD, category.toString())),
                    BooleanClause.Occur.MUST
            );
            addKeyRangeValues(
                    booleanQuery,
                    attributeName,
                    attributeValueRange.getFirst(),
                    attributeValueRange.getSecond());
            query.add(booleanQuery, BooleanClause.Occur.SHOULD);

        }
        return query; */
    }

    /**
     * Creates a range query to getByKey the products with specified attribute name and range value
     * in particular categories
     *
     * @param categories          given set of category ids.
     * @param attributeName       attribute name
     * @param attributeValueRange value range for given attribute
     * @return boolean query
     */
    public BooleanQuery createQueryWithRangeValues(
            final List<Long> categories,
            final String attributeName,
            final Pair<String, String> attributeValueRange
            /*final Map<String, Pair<String, String>> attributeRangeMap*/) {

        final BooleanQuery categoriesQueries = super.createQuery(categories);

        return addKeyRangeValues(
                categoriesQueries,
                attributeName,
                attributeValueRange.getFirst(),
                attributeValueRange.getSecond());


        /*final BooleanQuery query = new BooleanQuery();
      for (Long category : categories) {
          BooleanQuery booleanQuery = new BooleanQuery();
          booleanQuery.add(
                  new TermQuery(new Term(PRODUCT_CATEGORY_FIELD, category.toString())),
                  BooleanClause.Occur.MUST
          );
          for (Map.Entry<String, Pair<String, String>> entry : attributeRangeMap.entrySet()) {
              addKeyRangeValues(
                      booleanQuery,
                      entry.getKey(),
                      entry.getValue().getFirst(),
                      entry.getValue().getSecond());
          }
          query.add(booleanQuery, BooleanClause.Occur.SHOULD);

      }

      return query; */
    }


    /**
     * Creates a query to getByKey the products with specified attribute name and values
     * in particular categories
     *
     * @param categories given set of category ids.
     * @param attribute  map of attribute name and value
     * @return boolean query
     */
    /*public BooleanQuery createQuery(
            final List<Long> categories,
            final Map<String, String> attribute) {
        BooleanQuery query = super.createQuery(categories);
        for (Map.Entry<String, String> entry : attribute.entrySet()) {
            addKeyValue(query, entry.getKey(), entry.getValue());
        }
        return query;
    }   */


    /**
     * Creates a query to getByKey the products with specified attribute name and values
     * in particular category
     *
     * @param categoryId     given category id
     * @param attributeName  attribute name
     * @param attributeValue value of attribute
     * @return boolean query
     */
    public BooleanQuery createQuery(
            final Long categoryId,
            final String attributeName,
            final String attributeValue) {

        return addKeyValue(super.createQuery(categoryId), attributeName, attributeValue);

    }

    /**
     * Creates a query to getByKey the products with specified attribute name and values
     * in particular category
     *
     * @param categoryId          given category id
     * @param attributeName       attribute name
     * @param attributeValueRange range values (hi and lo) of attribute
     * @return boolean query
     */
    public BooleanQuery createQuery(
            final Long categoryId,
            final String attributeName,
            final Pair<String, String> attributeValueRange) {
        BooleanQuery query = super.createQuery(categoryId);
        addKeyRangeValues(query,
                attributeName,
                attributeValueRange.getFirst(),
                attributeValueRange.getSecond());
        return query;
    }


    private BooleanQuery addKeyValue(final BooleanQuery query, final String attributeName, final String attributeValue) {

        final BooleanQuery aggregatedQuery = new BooleanQuery();


        final BooleanQuery productAttrNames = new BooleanQuery();
        productAttrNames.add(
                new TermQuery(new Term(ATTRIBUTE_CODE_FIELD, attributeName)),
                BooleanClause.Occur.SHOULD
        );
        productAttrNames.add(
                new TermQuery(new Term(SKU_ATTRIBUTE_CODE_FIELD, attributeName)),
                BooleanClause.Occur.SHOULD
        );


        final BooleanQuery productAttrVal = new BooleanQuery();
        productAttrVal.add(
                new TermQuery(new Term(ATTRIBUTE_VALUE_FIELD, attributeName + attributeValue)),
                BooleanClause.Occur.SHOULD
        );
        productAttrVal.add(
                new TermQuery(new Term(SKU_ATTRIBUTE_VALUE_FIELD, attributeName + attributeValue)),
                BooleanClause.Occur.SHOULD
        );

        if (!query.clauses().isEmpty()) {
            aggregatedQuery.add(query, BooleanClause.Occur.MUST);
        }

        aggregatedQuery.add(productAttrNames, BooleanClause.Occur.MUST);
        aggregatedQuery.add(productAttrVal, BooleanClause.Occur.MUST);

        return aggregatedQuery;


    }

    private BooleanQuery addKeyRangeValues(final BooleanQuery query,
                                           final String attributeName,
                                           final String attributeValueLo,
                                           final String attributeValueHi) {

        final BooleanQuery rez = new BooleanQuery();

        final BooleanQuery productAttrNames = new BooleanQuery();
        productAttrNames.add(
                new TermQuery(new Term(ATTRIBUTE_CODE_FIELD, attributeName)),
                BooleanClause.Occur.SHOULD
        );
        productAttrNames.add(
                new TermQuery(new Term(SKU_ATTRIBUTE_CODE_FIELD, attributeName)),
                BooleanClause.Occur.SHOULD
        );


        final BooleanQuery productAttrValueRange = new BooleanQuery();
        productAttrValueRange.add(
                new TermRangeQuery(
                        ATTRIBUTE_VALUE_FIELD,
                        attributeName + attributeValueLo,
                        attributeName + attributeValueHi,
                        true, true), // inclusive search
                BooleanClause.Occur.SHOULD
        );
        productAttrValueRange.add(
                new TermRangeQuery(
                        SKU_ATTRIBUTE_VALUE_FIELD,
                        attributeName + attributeValueLo,
                        attributeName + attributeValueHi,
                        true, true), // inclusive search
                BooleanClause.Occur.SHOULD
        );


        rez.add(query, BooleanClause.Occur.MUST);
        rez.add(productAttrNames, BooleanClause.Occur.MUST);
        rez.add(productAttrValueRange, BooleanClause.Occur.MUST);

        return rez;


    }


}
