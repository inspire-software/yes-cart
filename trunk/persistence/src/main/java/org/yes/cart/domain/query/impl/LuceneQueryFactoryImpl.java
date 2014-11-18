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

import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.yes.cart.domain.query.LuceneQueryFactory;
import org.yes.cart.domain.query.ProductSearchQueryBuilder;
import org.yes.cart.domain.query.SearchQueryBuilder;
import org.yes.cart.service.domain.AttributeService;
import org.yes.cart.service.domain.CategoryService;
import org.yes.cart.service.domain.ProductService;

import java.util.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 08-May-2011
 * Time: 11:12:54
 */
public class LuceneQueryFactoryImpl implements LuceneQueryFactory {

    private static final Logger LOG = LoggerFactory.getLogger("FTQ");

    private final AttributeService attributeService;
    private final ProductService productService;
    private final CategoryService categoryService;

    private final Map<String, SearchQueryBuilder> builders;
    private final Set<String> useQueryRelaxation;

    private final SearchQueryBuilder categoryBuilder;
    private final SearchQueryBuilder shopBuilder;
    private final SearchQueryBuilder attributeBuilder;
    private final SearchQueryBuilder tagBuilder;

    /**
     * Construct query builder factory.
     *
     * @param attributeService attribute service to filter not allowed page parameters during filtered navigation
     * @param productService   product service
     * @param categoryService  category service
     * @param builders         map of builders to provide parts of navigation query
     * @param useQueryRelaxation set of parameters names that are allowed to be relaxed to avoid no search results
     */
    public LuceneQueryFactoryImpl(final AttributeService attributeService,
                                  final ProductService productService,
                                  final CategoryService categoryService,
                                  final Map<String, SearchQueryBuilder> builders,
                                  final Set<String> useQueryRelaxation) {

        this.attributeService = attributeService;
        this.productService = productService;
        this.categoryService = categoryService;

        this.builders = builders;
        this.categoryBuilder = builders.get(ProductSearchQueryBuilder.PRODUCT_CATEGORY_FIELD);
        this.shopBuilder = builders.get(ProductSearchQueryBuilder.PRODUCT_SHOP_FIELD);
        this.attributeBuilder = builders.get(ProductSearchQueryBuilder.ATTRIBUTE_CODE_FIELD);
        this.tagBuilder = builders.get(ProductSearchQueryBuilder.PRODUCT_TAG_FIELD);

        this.useQueryRelaxation = useQueryRelaxation;
    }

    private Query join(final List<Query> allQueries) {

        if (CollectionUtils.isEmpty(allQueries)) {
            return null;
        }

        BooleanQuery booleanQuery = new BooleanQuery();

        for (final Query query : allQueries) {
            booleanQuery.add(query, BooleanClause.Occur.MUST);
        }

        return booleanQuery;

    }

    /**
     * {@inheritDoc}
     */
    public Query getSnowBallQuery(final Query allQueries, final long shopId, final String param, final Object value) {

        final BooleanQuery snowball = new BooleanQuery();

        if (value != null) {

            snowball.add(allQueries, BooleanClause.Occur.MUST);

            SearchQueryBuilder builder = builders.get(param);
            if (builder == null) {
                builder = attributeBuilder; // use attribute builder by default
            }

            final Query strictQuery = builder.createStrictQuery(shopId, param, value);
            if (strictQuery != null) {
                snowball.add(strictQuery, BooleanClause.Occur.MUST);
            }

        }

        return snowball;
    }

    /**
     * {@inheritDoc}
     */
    public Query getFilteredNavigationQueryChain(final Long shopId,
                                                 final List<Long> categories,
                                                 final Map<String, List> requestParameters) {

        final Set<String> allowedAttributeCodes = attributeService.getAllNavigatableAttributeCodes();
        final Set<String> allowedBuilderCodes = builders.keySet();

        final List<Query> queryChainStrict = new ArrayList<Query>();
        final List<Query> queryChainRelaxed = new ArrayList<Query>();

        final Query cats = categoryBuilder.createStrictQuery(shopId, ProductSearchQueryBuilder.PRODUCT_CATEGORY_FIELD, categories);
        if (cats != null) {
            // Every category belongs to a store, so no need to add store query too
            queryChainStrict.add(cats);
            queryChainRelaxed.add(cats);
        } else {
            // If we have no category criteria need to ensure we only view products that belong to current store
            final Query store = shopBuilder.createStrictQuery(shopId, ProductSearchQueryBuilder.PRODUCT_SHOP_FIELD, shopId);
            queryChainStrict.add(store);
            queryChainRelaxed.add(store);
        }

        if (requestParameters != null) {
            for (Map.Entry<String, List> entry : requestParameters.entrySet()) {
                final String decodedKeyName = entry.getKey();
                if (allowedAttributeCodes.contains(decodedKeyName) || allowedBuilderCodes.contains(decodedKeyName)) {
                    final List value = entry.getValue();
                    if (value != null) {

                        SearchQueryBuilder builder = builders.get(decodedKeyName);
                        if (builder == null) {
                            builder = attributeBuilder; // use attribute builder by default
                        }

                        final boolean tag = builder == tagBuilder;

                        for (final Object valueItem : value) {

                            final Object searchValue;
                            if (tag && ProductSearchQueryBuilder.TAG_NEWARRIVAL.equals(valueItem)) {
                                searchValue = earliestNewArrivalDate(shopId, categories);
                            } else {
                                searchValue = valueItem;
                            }

                            final Query strictQuery = builder.createStrictQuery(shopId, decodedKeyName, searchValue);
                            if (strictQuery == null) {
                                continue; // no valid criteria
                            }
                            queryChainStrict.add(strictQuery);

                            if (useQueryRelaxation.contains(decodedKeyName)) {
                                final Query relaxedQuery = builder.createRelaxedQuery(shopId, decodedKeyName, searchValue);
                                if (relaxedQuery == null) {
                                    continue; // no valid criteria
                                }
                                queryChainRelaxed.add(relaxedQuery);
                            } else {
                                queryChainRelaxed.add(strictQuery);
                            }

                        }

                    }
                }
            }
        }

        Query out = join(queryChainStrict);
        if (productService.getProductQty(out) == 0) {
            // use relaxation for all elements of query if strict query yields no results
            out = join(queryChainRelaxed);
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Constructed nav query: {}", out);
        }

        return out;
    }

    private Date earliestNewArrivalDate(final Long shopId, final List<Long> categories) {
        Date beforeDays = new Date();
        if (CollectionUtils.isEmpty(categories)) {
            beforeDays = categoryService.getCategoryNewArrivalDate(0L, shopId);
        } else {
            for (final Long categoryId : categories) {
                Date catBeforeDays = categoryService.getCategoryNewArrivalDate(categoryId, shopId);
                if (catBeforeDays.before(beforeDays)) {
                    beforeDays = catBeforeDays; // get the earliest
                }
            }
        }
        return beforeDays;
    }

}
