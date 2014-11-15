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

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.springframework.util.CollectionUtils;
import org.yes.cart.constants.Constants;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.query.LuceneQueryFactory;
import org.yes.cart.domain.query.PriceNavigation;
import org.yes.cart.domain.query.ProductSearchQueryBuilder;
import org.yes.cart.service.domain.AttributeService;
import org.yes.cart.service.domain.CategoryService;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.util.ShopCodeContext;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 08-May-2011
 * Time: 11:12:54
 */
public class LuceneQueryFactoryImpl implements LuceneQueryFactory {

    /**
     * Only this indexes allowed for attributive filtered navigation.
     */
    private final static String[] fields = {
            ProductSearchQueryBuilder.PRODUCT_NAME_FIELD,
            ProductSearchQueryBuilder.PRODUCT_DESCRIPTION_FIELD,
            ProductSearchQueryBuilder.BRAND_FIELD,
            ProductSearchQueryBuilder.ATTRIBUTE_CODE_FIELD,
            ProductSearchQueryBuilder.ATTRIBUTE_VALUE_FIELD,
            ProductSearchQueryBuilder.ATTRIBUTE_VALUE_SEARCH_FIELD,
            ProductSearchQueryBuilder.PRODUCT_CATEGORY_FIELD,
            ProductSearchQueryBuilder.PRODUCT_PRICE_CURRENCY,
            ProductSearchQueryBuilder.PRODUCT_PRICE_AMOUNT,
            ProductSearchQueryBuilder.PRODUCT_PRICE_SHOP,
            ProductSearchQueryBuilder.PRODUCT_ID_FIELD
    };


    private final PriceNavigation priceNavigation;
    private final AttributeService attributeService;
    private final ProductService productService;
    private final CategoryService categoryService;

    private final BrandSearchQueryBuilder brandSearchQueryBuilder = new BrandSearchQueryBuilder();
    private final TagSearchQueryBuilder tagSearchQueryBuilder = new TagSearchQueryBuilder();
    private final PriceSearchQueryBuilderImpl priceSearchQueryBuilder = new PriceSearchQueryBuilderImpl();
    private final AttributiveSearchQueryBuilderImpl attributeSearchQueryBuilder = new AttributiveSearchQueryBuilderImpl();
    private final GlobalSearchQueryBuilderImpl globalSearchQueryBuilder = new GlobalSearchQueryBuilderImpl();

    /**
     * Construct query builder factory.
     *
     * @param priceNavigation  price navigation service
     * @param attributeService attribute service to filter not allowed page parameters during filtered navigation
     * @param productService   product service
     * @param categoryService  category service
     */
    public LuceneQueryFactoryImpl(final PriceNavigation priceNavigation,
                                  final AttributeService attributeService,
                                  final ProductService productService,
                                  final CategoryService categoryService) {
        this.priceNavigation = priceNavigation;
        this.attributeService = attributeService;
        this.productService = productService;

        this.categoryService = categoryService;
    }


    private MultiFieldQueryParser getMultiFieldQueryParser(final boolean abatement) {

        // MultiFieldQueryParser is not thread safe so need new instance every time
        final MultiFieldQueryParser multiFieldQueryParser = new MultiFieldQueryParser(
                Version.LUCENE_31,
                fields,
                new AsIsAnalyzer(abatement));

        multiFieldQueryParser.setLowercaseExpandedTerms(abatement);

        return multiFieldQueryParser;
    }

    /**
     * Parse string into query
     *
     * @param queryString string representation of lucene query
     * @return query if string was successfully parsed.
     */
    private Query parseQuery(final String queryString, final boolean abatement) {
        try {
            return getMultiFieldQueryParser(abatement).parse(queryString);
        } catch (ParseException e) {
            ShopCodeContext.getLog(this).error(MessageFormat.format("Can not parse given query {0}", queryString), e);
        }
        return null;
    }

    private BooleanQuery join(final List<BooleanQuery> allQueries, final BooleanQuery currentQuery) {

        BooleanQuery booleanQuery = new BooleanQuery();

        if (allQueries.size() > 1) {
            for (final BooleanQuery allQuery : allQueries) {
                booleanQuery.add(allQuery, BooleanClause.Occur.MUST);
            }

            if (currentQuery != null) {
                booleanQuery.add(currentQuery, BooleanClause.Occur.MUST);
            }

        } else if (allQueries.size() == 1) {
            if (currentQuery != null) {
                booleanQuery.add(allQueries.get(0), BooleanClause.Occur.MUST);
                booleanQuery.add(currentQuery, BooleanClause.Occur.MUST);
            } else {
                booleanQuery = allQueries.get(0);
            }
        }

        return booleanQuery;

    }


    /**
     * Get the combined from query chain query.
     * The current query will be last in this chain.
     * Chain will be truncated if <code>currentQuery</code> already present in
     * list
     *
     * NOTE: the parameter queries may be mutated, please take care. If you need a copy of this
     *       you must clone it before passing to this method.
     *
     * @param allQueries   query chain as query
     * @param currentQuery optional current query
     * @return combined from chain query
     */
    public BooleanQuery getSnowBallQuery(final BooleanQuery allQueries, final Query currentQuery) {

        if (currentQuery == null || StringUtils.isBlank(currentQuery.toString())) {
            return allQueries;
        }
        BooleanQuery booleanQuery = new BooleanQuery();
        if (allQueries != null && StringUtils.isNotBlank(allQueries.toString())) {
            booleanQuery.add(allQueries, BooleanClause.Occur.MUST);
        }
        booleanQuery.add(currentQuery, BooleanClause.Occur.MUST);
        return booleanQuery;
    }


    /**
     * Get the queries chain.
     * <p/>
     * Note about search: Search can be performed on entire shop or in particular category.
     * Entire search if size of categories is one and first category is 0
     * otherwise need to use all sub categories, that belong to given category list.
     *
     * @param shopId            the current shop id
     * @param requestParameters web request parameters
     * @param categories        given category ids
     * @return ordered by cookie name list of cookies
     */
    public BooleanQuery getFilteredNavigationQueryChain(final Long shopId,
                                                        final List<Long> categories,
                                                        final Map<String, ?> requestParameters) {

        final Set<String> allowedAttributeCodes = attributeService.getAllNavigatableAttributeCodes();

        final List<BooleanQuery> queryChain = new ArrayList<BooleanQuery>();
        for (Map.Entry<String, ?> entry : requestParameters.entrySet()) {
            final String decodedKeyName = entry.getKey();
            if (isFilteredNavigationParameter(decodedKeyName, allowedAttributeCodes)) {
                Object value = entry.getValue();
                if (value != null) {
                    Object[] values = getValues(value);
                    for (Object val : values) {
                        BooleanQuery query;
                        if (ProductSearchQueryBuilder.BRAND_FIELD.equals(decodedKeyName)) {
                            query = createBrandChain(shopId, categories, val);
                        } else if (ProductSearchQueryBuilder.PRODUCT_TAG_FIELD.equals(decodedKeyName)) {
                            query = createTagChain(shopId, categories, val);
                        } else if (ProductSearchQueryBuilder.PRODUCT_PRICE.equals(decodedKeyName)) {
                            query = createPriceChain(shopId, categories, val);
                        } else if (ProductSearchQueryBuilder.QUERY.equals(decodedKeyName)) {
                            query = createSearchChain(categories, shopId, val, false);
                            final BooleanQuery booleanQueryToTest = join(queryChain, query);
                            // Take care here - we only need FT, so always put some projection value to prevent db access
                            if (productService.getProductQty(booleanQueryToTest) == 0) {
                                //create not very strict query with lowercase
                                query = createSearchChain(categories, shopId, val, true);
                            }
                        } else {
                            query = createAttributeChain(categories, shopId, decodedKeyName, val);
                        }
                        queryChain.add(query);

                    }
                }
            }
        }

        BooleanQuery out = null;
        if (!queryChain.isEmpty()) {
            out = join(queryChain, null);
        }
        final Logger log = ShopCodeContext.getLog(this);
        if (log.isDebugEnabled()) {
            log.debug("Constructed query {}", out);
        }
        return out;
    }

    private BooleanQuery createAttributeChain(final List<Long> categories, final long shopId, final String decodedKeyName, final Object val) {
        BooleanQuery query;
        //is it Single or Range value navigation, determination will be based on hyphen atm
        final String attrValue = String.valueOf(val);
        if (attrValue.indexOf(Constants.RANGE_NAVIGATION_DELIMITER) > -1) { // value range navigation
            final String[] attrValues = StringUtils.split(attrValue, Constants.RANGE_NAVIGATION_DELIMITER);
            final Pair<String, String> valueRange = new Pair<String, String>(
                    attrValues.length > 0 ? attrValues[0] : null,
                    attrValues.length > 1 ? attrValues[1] : null
            );
            query = attributeSearchQueryBuilder.createQuery(categories, shopId, decodedKeyName, valueRange);
        } else { //single attribute value navigation
            query = attributeSearchQueryBuilder.createQuery(categories, shopId, decodedKeyName, attrValue);
        }
        return query;
    }

    private BooleanQuery createSearchChain(final List<Long> categories, final long shopId, final Object val, final boolean abatement) {
        BooleanQuery query;
        if (CollectionUtils.isEmpty(categories)) {
            query = globalSearchQueryBuilder.createQuerySearchInShop(String.valueOf(val), shopId, abatement);
        } else {
            query = globalSearchQueryBuilder.createQuerySearchInCategories(String.valueOf(val), categories, abatement);
        }

        if (abatement) {
            final String queryToAbatement = query.toString();
            Query queryCandidate = parseQuery(queryToAbatement, abatement);
            if (queryCandidate instanceof BooleanQuery) {
                query = (BooleanQuery) queryCandidate;
            } else {
                query = new BooleanQuery();
                query.add(queryCandidate,BooleanClause.Occur.MUST);
            }
        }
        return query;
    }

    private BooleanQuery createPriceChain(final Long shopId, final List<Long> categories, final Object val) {
        final Pair<String, Pair<BigDecimal, BigDecimal>> priceRequestParams =
                priceNavigation.decomposePriceRequestParams(String.valueOf(val));
        return priceSearchQueryBuilder.createQuery(
                categories,
                shopId,
                priceRequestParams.getFirst(),
                priceRequestParams.getSecond().getFirst(),
                priceRequestParams.getSecond().getSecond()
        );
    }

    private BooleanQuery createBrandChain(final Long shopId, final List<Long> categories, final Object val) {
        return brandSearchQueryBuilder.createQuery(categories, shopId, String.valueOf(val));
    }


    private BooleanQuery createTagChain(final Long shopId, final List<Long> categories, final Object val) {
        if (TagSearchQueryBuilder.TAG_NEWARRIVAL.equals(val)) {

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

            return tagSearchQueryBuilder.createQuery(categories, shopId, beforeDays);
        }
        return tagSearchQueryBuilder.createQuery(categories, shopId, String.valueOf(val));
    }


    private Object[] getValues(final Object value) {
        if (value instanceof Object[]) {
            return (Object[]) value;
        } else if (value instanceof Collection) {
            return ((Collection) value).toArray();
        } else {
            return new Object[]{String.valueOf(value)};
        }
    }

    private boolean isFilteredNavigationParameter(final String parameterName,
                                                  final Collection<String> uniqueAttributeCodes) {
        return uniqueAttributeCodes.contains(parameterName);
    }


}
