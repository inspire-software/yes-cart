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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.yes.cart.domain.dto.ProductSearchResultDTO;
import org.yes.cart.domain.query.LuceneQueryFactory;
import org.yes.cart.domain.query.ProductSearchQueryBuilder;
import org.yes.cart.domain.query.SearchQueryBuilder;
import org.yes.cart.domain.query.ShopSearchSupportService;
import org.yes.cart.domain.queryobject.NavigationContext;
import org.yes.cart.domain.queryobject.impl.NavigationContextImpl;
import org.yes.cart.service.domain.AttributeService;
import org.yes.cart.service.domain.ProductService;

import java.util.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 08-May-2011
 * Time: 11:12:54
 */
public class LuceneQueryFactoryImpl implements LuceneQueryFactory {

    private final Logger LOG = LoggerFactory.getLogger("FTQ");

    private final AttributeService attributeService;
    private final ProductService productService;
    private final ShopSearchSupportService shopSearchSupportService;

    private final Map<String, SearchQueryBuilder> productBuilders;
    private final Map<String, SearchQueryBuilder> skuBuilders;
    private final Set<String> useQueryRelaxation;

    private final SearchQueryBuilder productCategoryBuilder;
    private final SearchQueryBuilder productCategoryIncludingParentsBuilder;
    private final SearchQueryBuilder productShopBuilder;
    private final SearchQueryBuilder productShopStockBuilder;
    private final SearchQueryBuilder productShopPriceBuilder;
    private final SearchQueryBuilder productAttributeBuilder;
    private final SearchQueryBuilder productTagBuilder;
    private final SearchQueryBuilder skuAttributeBuilder;

    /**
     * Construct query builder factory.
     * @param attributeService attribute service to filter not allowed page parameters during filtered navigation
     * @param productService   product service
     * @param shopSearchSupportService shop search support
     * @param productBuilders  map of builders to provide parts of navigation query
     * @param skuBuilders      map of builders to provide parts of navigation query
     * @param useQueryRelaxation set of parameters names that are allowed to be relaxed to avoid no search results
     */
    public LuceneQueryFactoryImpl(final AttributeService attributeService,
                                  final ProductService productService,
                                  final ShopSearchSupportService shopSearchSupportService,
                                  final Map<String, SearchQueryBuilder> productBuilders,
                                  final Map<String, SearchQueryBuilder> skuBuilders,
                                  final Set<String> useQueryRelaxation) {

        this.attributeService = attributeService;
        this.productService = productService;
        this.shopSearchSupportService = shopSearchSupportService;

        this.productBuilders = productBuilders;
        this.skuBuilders = skuBuilders;
        this.productCategoryBuilder = productBuilders.get(ProductSearchQueryBuilder.PRODUCT_CATEGORY_FIELD);
        this.productCategoryIncludingParentsBuilder = productBuilders.get(ProductSearchQueryBuilder.PRODUCT_CATEGORY_INC_PARENTS_FIELD);
        this.productShopBuilder = productBuilders.get(ProductSearchQueryBuilder.PRODUCT_SHOP_FIELD);
        this.productShopStockBuilder = productBuilders.get(ProductSearchQueryBuilder.PRODUCT_SHOP_INSTOCK_FIELD);
        this.productShopPriceBuilder = productBuilders.get(ProductSearchQueryBuilder.PRODUCT_SHOP_HASPRICE_FIELD);
        this.productAttributeBuilder = productBuilders.get(ProductSearchQueryBuilder.ATTRIBUTE_CODE_FIELD);
        this.productTagBuilder = productBuilders.get(ProductSearchQueryBuilder.PRODUCT_TAG_FIELD);
        this.skuAttributeBuilder = skuBuilders.get(ProductSearchQueryBuilder.ATTRIBUTE_CODE_FIELD);

        this.useQueryRelaxation = useQueryRelaxation;
    }

    private Query join(final List<Query> allQueries, BooleanClause.Occur with) {

        if (CollectionUtils.isEmpty(allQueries)) {
            return null;
        }

        BooleanQuery booleanQuery = new BooleanQuery();

        for (final Query query : allQueries) {
            booleanQuery.add(query, with);
        }

        return booleanQuery;

    }

    /**
     * {@inheritDoc}
     */
    public NavigationContext getProductSnowBallQuery(final NavigationContext navigationContext,
                                                     final String param,
                                                     final Object value) {

        final BooleanQuery snowball = new BooleanQuery();

        final Map<String, List<String>> navigationParameters = new HashMap<String, List<String>>(navigationContext.getFilterParameters());

        if (value != null) {

            snowball.add(navigationContext.getProductQuery(), BooleanClause.Occur.MUST);

            SearchQueryBuilder builder = productBuilders.get(param);
            if (builder == null) {
                builder = productAttributeBuilder; // use attribute builder by default
            }

            final Query strictQuery = builder.createStrictQuery(navigationContext.getShopId(), param, value);
            if (strictQuery != null) {
                snowball.add(strictQuery, BooleanClause.Occur.MUST);

                List<String> paramValues = navigationParameters.get(param);
                if (paramValues == null) {
                    paramValues = new ArrayList<String>(2);
                    navigationParameters.put(param, paramValues);
                }
                paramValues.add(String.valueOf(value)); // record original value as string

            }

        }

        return new NavigationContextImpl(
                    navigationContext.getShopId(),
                    navigationContext.getCategories(),
                    navigationContext.isIncludeSubCategories(),
                    navigationParameters,
                    snowball,
                    navigationContext.getProductSkuQuery()
        );
    }

    /**
     * {@inheritDoc}
     */
    public NavigationContext getSkuSnowBallQuery(final NavigationContext navigationContext,
                                                 final List<ProductSearchResultDTO> products) {

        final BooleanQuery snowball = new BooleanQuery();

        final Map<String, List<String>> navigationParameters = new HashMap<String, List<String>>(navigationContext.getFilterParameters());

        if (!CollectionUtils.isEmpty(products)) {

            if (navigationContext.getProductSkuQuery() != null) {
                snowball.add(navigationContext.getProductSkuQuery(), BooleanClause.Occur.SHOULD);
            }

            SearchQueryBuilder builder = skuBuilders.get(ProductSearchQueryBuilder.PRODUCT_ID_FIELD);
            final List<Long> productIds = new ArrayList<Long>();
            for (final ProductSearchResultDTO product : products) {
                productIds.add(product.getId());
            }

            final Query strictQuery = builder.createStrictQuery(
                    navigationContext.getShopId(), ProductSearchQueryBuilder.PRODUCT_ID_FIELD, productIds);
            if (strictQuery != null) {
                snowball.add(strictQuery, BooleanClause.Occur.MUST);
            }

        }

        return new NavigationContextImpl(
                navigationContext.getShopId(),
                navigationContext.getCategories(),
                navigationContext.isIncludeSubCategories(),
                navigationParameters,
                navigationContext.getProductQuery(),
                snowball
        );
    }

    /**
     * {@inheritDoc}
     */
    public NavigationContext getFilteredNavigationQueryChain(final long shopId,
                                                             final List<Long> categories,
                                                             final boolean includeSubCategories,
                                                             final Map<String, List> requestParameters) {

        final Set<String> allowedAttributeCodes = attributeService.getAllNavigatableAttributeCodes();
        final Set<String> allowedBuilderCodes = productBuilders.keySet();

        final List<Query> productQueryChainStrict = new ArrayList<Query>();
        final List<Query> productQueryChainRelaxed = new ArrayList<Query>();
        final List<Query> skuQueryChainStrict = new ArrayList<Query>();
        final List<Query> skuQueryChainRelaxed = new ArrayList<Query>();

        final Map<String, List<String>> navigationParameters = new HashMap<String, List<String>>();
        if (requestParameters != null) {
            for (Map.Entry<String, List> entry : requestParameters.entrySet()) {
                final String decodedKeyName = entry.getKey();
                if (allowedAttributeCodes.contains(decodedKeyName) || allowedBuilderCodes.contains(decodedKeyName)) {
                    final List value = entry.getValue();
                    if (value != null) {

                        SearchQueryBuilder prodBuilder = productBuilders.get(decodedKeyName);
                        if (prodBuilder == null) {
                            prodBuilder = productAttributeBuilder; // use attribute builder by default
                        }
                        SearchQueryBuilder skuBuilder = skuBuilders.get(decodedKeyName);
                        if (skuBuilder == null) {
                            skuBuilder = skuAttributeBuilder; // use attribute builder by default
                        }

                        final boolean tag = prodBuilder == productTagBuilder;

                        final List<Query> productQueryChainStrictClauses = new ArrayList<Query>();
                        final List<Query> productQueryChainRelaxedClauses = new ArrayList<Query>();
                        final List<Query> skuQueryChainStrictClauses = new ArrayList<Query>();
                        final List<Query> skuQueryChainRelaxedClauses = new ArrayList<Query>();

                        for (final Object valueItem : value) {

                            final Object searchValue;
                            if (tag && ProductSearchQueryBuilder.TAG_NEWARRIVAL.equals(valueItem)) {
                                searchValue = earliestNewArrivalDate(shopId, categories);
                            } else {
                                searchValue = valueItem;
                            }

                            final Query strictProdQuery = prodBuilder.createStrictQuery(shopId, decodedKeyName, searchValue);
                            if (strictProdQuery == null) {
                                continue; // no valid criteria
                            }

                            List<String> paramValues = navigationParameters.get(decodedKeyName);
                            if (paramValues == null) {
                                paramValues = new ArrayList<String>(); // must be list to preserve order, as it influences the priority
                                navigationParameters.put(decodedKeyName, paramValues);
                            }
                            paramValues.add(String.valueOf(valueItem)); // record original value as string

                            productQueryChainStrictClauses.add(strictProdQuery);

                            if (useQueryRelaxation.contains(decodedKeyName)) {
                                final Query relaxedProdQuery = prodBuilder.createRelaxedQuery(shopId, decodedKeyName, searchValue);
                                if (relaxedProdQuery == null) {
                                    continue; // no valid criteria
                                }
                                productQueryChainRelaxedClauses.add(relaxedProdQuery);
                            } else {
                                productQueryChainRelaxedClauses.add(strictProdQuery);
                            }

                            final Query strictSkuQuery = skuBuilder.createStrictQuery(shopId, decodedKeyName, searchValue);
                            if (strictSkuQuery == null) {
                                continue; // no valid criteria
                            }
                            skuQueryChainStrictClauses.add(strictSkuQuery);

                            if (useQueryRelaxation.contains(decodedKeyName)) {
                                final Query relaxedSkuQuery = skuBuilder.createRelaxedQuery(shopId, decodedKeyName, searchValue);
                                if (relaxedSkuQuery == null) {
                                    continue; // no valid criteria
                                }
                                skuQueryChainRelaxedClauses.add(relaxedSkuQuery);
                            } else {
                                skuQueryChainRelaxedClauses.add(strictSkuQuery);
                            }

                        }

                        if (productQueryChainStrictClauses.size() == 1) {
                            productQueryChainStrict.add(productQueryChainStrictClauses.get(0));
                        } else if (productQueryChainStrictClauses.size() > 1) {
                            // Multivalues are OR'ed
                            productQueryChainStrict.add(join(productQueryChainStrictClauses, BooleanClause.Occur.SHOULD));
                        }

                        if (productQueryChainRelaxedClauses.size() == 1) {
                            productQueryChainRelaxed.add(productQueryChainRelaxedClauses.get(0));
                        } else if (productQueryChainRelaxedClauses.size() > 1) {
                            // Multivalues are OR'ed
                            productQueryChainRelaxed.add(join(productQueryChainRelaxedClauses, BooleanClause.Occur.SHOULD));
                        }

                        if (skuQueryChainStrictClauses.size() == 1) {
                            skuQueryChainStrict.add(skuQueryChainStrictClauses.get(0));
                        } else if (skuQueryChainStrictClauses.size() > 1) {
                            // Multivalues are OR'ed
                            skuQueryChainStrict.add(join(skuQueryChainStrictClauses, BooleanClause.Occur.SHOULD));
                        }

                        if (skuQueryChainRelaxedClauses.size() == 1) {
                            skuQueryChainRelaxed.add(skuQueryChainRelaxedClauses.get(0));
                        } else if (skuQueryChainRelaxedClauses.size() > 1) {
                            // Multivalues are OR'ed
                            skuQueryChainRelaxed.add(join(skuQueryChainRelaxedClauses, BooleanClause.Occur.SHOULD));
                        }

                    }
                }
            }
        }

        // Mandatory fields are last for better scoring
        final Query cats;
        if (includeSubCategories) {
            cats = productCategoryIncludingParentsBuilder.createStrictQuery(shopId, ProductSearchQueryBuilder.PRODUCT_CATEGORY_INC_PARENTS_FIELD, categories);
        } else {
            cats = productCategoryBuilder.createStrictQuery(shopId, ProductSearchQueryBuilder.PRODUCT_CATEGORY_FIELD, categories);
        }
        if (cats != null) {
            // Every category belongs to a store, so no need to add store query too
            productQueryChainStrict.add(cats);
            productQueryChainRelaxed.add(cats);
        } else {
            // If we have no category criteria need to ensure we only view products that belong to current store
            final Query store = productShopBuilder.createStrictQuery(shopId, ProductSearchQueryBuilder.PRODUCT_SHOP_FIELD, shopId);
            productQueryChainStrict.add(store);
            productQueryChainRelaxed.add(store);
        }

        // Enforce in stock products
        final  Query inStock = productShopStockBuilder.createStrictQuery(shopId, ProductSearchQueryBuilder.PRODUCT_SHOP_INSTOCK_FIELD, shopId);
        if (inStock != null) {
            productQueryChainStrict.add(inStock);
            productQueryChainRelaxed.add(inStock);
        }

        // Enforce products with price
        final  Query hasPrice = productShopPriceBuilder.createStrictQuery(shopId, ProductSearchQueryBuilder.PRODUCT_SHOP_HASPRICE_FIELD, shopId);
        if (hasPrice != null) {
            productQueryChainStrict.add(hasPrice);
            productQueryChainRelaxed.add(hasPrice);
        }

        Query prod = join(productQueryChainStrict, BooleanClause.Occur.MUST);
        Query sku;
        if (productService.getProductQty(prod) == 0) {
            // use relaxation for all elements of query if strict query yields no results
            prod = join(productQueryChainRelaxed, BooleanClause.Occur.MUST);
            sku = join(skuQueryChainRelaxed, BooleanClause.Occur.SHOULD);
        } else {
            sku = join(skuQueryChainStrict, BooleanClause.Occur.SHOULD);
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Constructed nav prod query: {}", prod);
            LOG.debug("Constructed nav sku  query: {}", sku );
        }

        return new NavigationContextImpl(shopId, categories, includeSubCategories, navigationParameters, prod, sku);
    }

    private Date earliestNewArrivalDate(final Long shopId, final List<Long> categories) {

        Date beforeDays = new Date();
        if (CollectionUtils.isEmpty(categories)) {

            beforeDays = shopSearchSupportService.getCategoryNewArrivalDate(0L, shopId);

        } else {
            for (final Long categoryId : categories) {
                Date catBeforeDays = shopSearchSupportService.getCategoryNewArrivalDate(categoryId, shopId);
                if (catBeforeDays.before(beforeDays)) {
                    beforeDays = catBeforeDays; // get the earliest
                }
            }
        }
        return beforeDays;
    }

}
