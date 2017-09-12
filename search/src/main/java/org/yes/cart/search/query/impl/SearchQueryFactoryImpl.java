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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.yes.cart.domain.dto.ProductSearchResultDTO;
import org.yes.cart.search.SearchQueryFactory;
import org.yes.cart.search.dto.NavigationContext;
import org.yes.cart.search.dto.impl.LuceneNavigationContextImpl;
import org.yes.cart.search.query.ProductSearchQueryBuilder;
import org.yes.cart.search.query.SearchQueryBuilder;
import org.yes.cart.service.domain.AttributeService;
import org.yes.cart.service.domain.ProductService;

import java.util.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 08-May-2011
 * Time: 11:12:54
 */
public class SearchQueryFactoryImpl implements SearchQueryFactory<Query> {

    private final Logger LOG = LoggerFactory.getLogger("FTQ");

    private final AttributeService attributeService;
    private final ProductService productService;

    private final Map<String, SearchQueryBuilder<Query>> productBuilders;
    private final Map<String, SearchQueryBuilder<Query>> skuBuilders;

    private final SearchQueryBuilder<Query> productCategoryBuilder;
    private final SearchQueryBuilder<Query> productCategoryIncludingParentsBuilder;
    private final SearchQueryBuilder<Query> productShopBuilder;
    private final SearchQueryBuilder<Query> productShopStockBuilder;
    private final SearchQueryBuilder<Query> productShopPriceBuilder;
    private final SearchQueryBuilder<Query> productAttributeBuilder;
    private final SearchQueryBuilder<Query> skuAttributeBuilder;

    /**
     * Construct query builder factory.
     *
     * @param attributeService attribute service to filter not allowed page parameters during filtered navigation
     * @param productService   product service
     * @param productBuilders  map of builders to provide parts of navigation query
     * @param skuBuilders      map of builders to provide parts of navigation query
     */
    public SearchQueryFactoryImpl(final AttributeService attributeService,
                                  final ProductService productService,
                                  final Map<String, SearchQueryBuilder<Query>> productBuilders,
                                  final Map<String, SearchQueryBuilder<Query>> skuBuilders) {

        this.attributeService = attributeService;
        this.productService = productService;

        this.productBuilders = productBuilders;
        this.skuBuilders = skuBuilders;
        this.productCategoryBuilder = productBuilders.get(ProductSearchQueryBuilder.PRODUCT_CATEGORY_FIELD);
        this.productCategoryIncludingParentsBuilder = productBuilders.get(ProductSearchQueryBuilder.PRODUCT_CATEGORY_INC_PARENTS_FIELD);
        this.productShopBuilder = productBuilders.get(ProductSearchQueryBuilder.PRODUCT_SHOP_FIELD);
        this.productShopStockBuilder = productBuilders.get(ProductSearchQueryBuilder.PRODUCT_SHOP_INSTOCK_FIELD);
        this.productShopPriceBuilder = productBuilders.get(ProductSearchQueryBuilder.PRODUCT_SHOP_HASPRICE_FIELD);
        this.productAttributeBuilder = productBuilders.get(ProductSearchQueryBuilder.ATTRIBUTE_CODE_FIELD);
        this.skuAttributeBuilder = skuBuilders.get(ProductSearchQueryBuilder.ATTRIBUTE_CODE_FIELD);
    }

    private Query join(final List<Query> allQueries, BooleanClause.Occur with) {

        return join(allQueries, null, with);

    }

    private Query join(final List<Query> allQueries, final Query extraQuery, BooleanClause.Occur with) {

        if (CollectionUtils.isEmpty(allQueries)) {
            if (extraQuery != null) {
                return extraQuery;
            }
            return null;
        }

        final BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();

        for (final Query query : allQueries) {
            booleanQuery.add(query, with);
        }

        if (extraQuery != null) {
            booleanQuery.add(extraQuery, with);
        }

        return booleanQuery.build();

    }

    private static final Set<String> PRODUCT_BOOST_FIELDS = new HashSet<>(
            Arrays.asList(
                    ProductSearchQueryBuilder.PRODUCT_CATEGORY_FIELD + "_boost",
                    ProductSearchQueryBuilder.PRODUCT_SHOP_INSTOCK_FIELD + "_boost",
                    "featured_boost"
            )
    );

    private Query productBoost(final Query query) {

        if (query == null) {
            return null;
        }

        return new DocumentBoostFieldsScoreQuery(query, PRODUCT_BOOST_FIELDS);

    }

    private static final Set<String> SKU_BOOST_FIELDS = new HashSet<>(
            Arrays.asList(
                    "rank_boost"
            )
    );

    private Query skuBoost(final Query query) {

        if (query == null) {
            return null;
        }

        return new DocumentBoostFieldsScoreQuery(query, SKU_BOOST_FIELDS);

    }

    /**
     * {@inheritDoc}
     */
    public NavigationContext<Query> getProductSnowBallQuery(final NavigationContext<Query> navigationContext,
                                                            final String param,
                                                            final Object value) {

        final BooleanQuery.Builder snowball = new BooleanQuery.Builder();

        final Map<String, List<String>> navigationParameters = new HashMap<String, List<String>>(navigationContext.getFilterParameters());

        if (value != null) {

            snowball.add(navigationContext.getProductQuery(), BooleanClause.Occur.MUST);

            SearchQueryBuilder<Query> builder = productBuilders.get(param);
            if (builder == null) {
                builder = productAttributeBuilder; // use attribute builder by default
            }

            final List<Query> chain = builder.createQueryChain(navigationContext, param, value);
            if (!CollectionUtils.isEmpty(chain)) {
                snowball.add(chain.get(0), BooleanClause.Occur.MUST);

                List<String> paramValues = navigationParameters.get(param);
                if (paramValues == null) {
                    paramValues = new ArrayList<String>(2);
                    navigationParameters.put(param, paramValues);
                }
                paramValues.add(String.valueOf(value)); // record original value as string

            }

        }

        return new LuceneNavigationContextImpl(
                    navigationContext.getShopId(),
                    navigationContext.getCustomerShopId(),
                    navigationContext.getCustomerLanguage(),
                    navigationContext.getCategories(),
                    navigationContext.isIncludeSubCategories(),
                    navigationParameters,
                    snowball.build(),
                    navigationContext.getProductSkuQuery()
        );
    }

    /**
     * {@inheritDoc}
     */
    public NavigationContext<Query> getSkuSnowBallQuery(final NavigationContext<Query> navigationContext,
                                                        final List<ProductSearchResultDTO> products) {

        final BooleanQuery.Builder snowball = new BooleanQuery.Builder();

        final Map<String, List<String>> navigationParameters = new HashMap<String, List<String>>(navigationContext.getFilterParameters());

        if (!CollectionUtils.isEmpty(products)) {

            if (navigationContext.getProductSkuQuery() != null) {
                snowball.add(navigationContext.getProductSkuQuery(), BooleanClause.Occur.SHOULD);
            }

            SearchQueryBuilder<Query> builder = skuBuilders.get(ProductSearchQueryBuilder.PRODUCT_ID_FIELD);
            final List<Long> productIds = new ArrayList<Long>();
            for (final ProductSearchResultDTO product : products) {
                productIds.add(product.getId());
            }

            final List<Query> chain = builder.createQueryChain(navigationContext, ProductSearchQueryBuilder.PRODUCT_ID_FIELD, productIds);
            if (!CollectionUtils.isEmpty(chain)) {
                snowball.add(chain.get(0), BooleanClause.Occur.MUST);
            }

        }

        return new LuceneNavigationContextImpl(
                navigationContext.getShopId(),
                navigationContext.getCustomerShopId(),
                navigationContext.getCustomerLanguage(),
                navigationContext.getCategories(),
                navigationContext.isIncludeSubCategories(),
                navigationParameters,
                navigationContext.getProductQuery(),
                skuBoost(snowball.build())
        );
    }

    /**
     * {@inheritDoc}
     */
    public NavigationContext<Query> getFilteredNavigationQueryChain(final long shopId,
                                                                    final long customerShopId,
                                                                    final String customerLanguage,
                                                                    final List<Long> categories,
                                                                    final boolean includeSubCategories,
                                                                    final Map<String, List> requestParameters) {

        final NavigationContext<Query> temp = new LuceneNavigationContextImpl(shopId, customerShopId, customerLanguage, categories, includeSubCategories, Collections.EMPTY_MAP, null, null);

        final Set<String> allowedAttributeCodes = attributeService.getAllNavigatableAttributeCodes();
        final Set<String> allowedBuilderCodes = productBuilders.keySet();

        final List<Query> productQueryStrictClauses = new ArrayList<Query>();
        final List<Query> productQueryRelaxedClause = new ArrayList<Query>();
        final List<Query> skuQueryStrictClauses = new ArrayList<Query>();
        final List<Query> skuQueryRelaxedClause = new ArrayList<Query>();

        final Map<String, List<String>> navigationParameters = new HashMap<String, List<String>>();
        if (requestParameters != null) {
            for (Map.Entry<String, List> entry : requestParameters.entrySet()) {
                final String decodedKeyName = entry.getKey();
                if (allowedAttributeCodes.contains(decodedKeyName) || allowedBuilderCodes.contains(decodedKeyName)) {
                    final List value = entry.getValue();
                    if (value != null) {

                        SearchQueryBuilder<Query> prodBuilder = productBuilders.get(decodedKeyName);
                        if (prodBuilder == null) {
                            prodBuilder = productAttributeBuilder; // use attribute builder by default
                        }

                        final List<Query> prodChain = prodBuilder.createQueryChain(temp, decodedKeyName, value);
                        if (prodChain == null) {
                            continue; // no valid criteria
                        }

                        boolean useRelaxed = false;
                        if (prodChain.size() > 1) {
                            if (productQueryRelaxedClause.size() > 0) {
                                LOG.warn("Only one relaxation clause is supported. One strictest clause for {} will be used", decodedKeyName);
                                productQueryStrictClauses.add(prodChain.get(0));
                            } else {
                                productQueryRelaxedClause.addAll(prodChain);
                                useRelaxed = true;
                            }
                        } else {
                            productQueryStrictClauses.add(prodChain.get(0));
                        }

                        navigationParameters.put(decodedKeyName, value); // record param if it produces criteria

                        SearchQueryBuilder<Query> skuBuilder = skuBuilders.get(decodedKeyName);
                        if (skuBuilder == null) {
                            skuBuilder = skuAttributeBuilder; // use attribute builder by default
                        }

                        final List<Query> skuChain = skuBuilder.createQueryChain(temp, decodedKeyName, value);
                        if (skuChain == null) {
                            continue; // no valid criteria
                        }

                        if (useRelaxed) {
                            skuQueryRelaxedClause.addAll(skuChain);
                        } else {
                            skuQueryStrictClauses.add(skuChain.get(0));
                        }

                    }
                }
            }
        }

        // Mandatory fields are last for better scoring
        final List<Query> cats;
        if (includeSubCategories) {
            cats = productCategoryIncludingParentsBuilder.createQueryChain(temp, ProductSearchQueryBuilder.PRODUCT_CATEGORY_INC_PARENTS_FIELD, categories);
        } else {
            cats = productCategoryBuilder.createQueryChain(temp, ProductSearchQueryBuilder.PRODUCT_CATEGORY_FIELD, categories);
        }
        if (!CollectionUtils.isEmpty(cats)) {
            // Every category belongs to a store, so no need to add store query too
            productQueryStrictClauses.add(cats.get(0));
        } else {
            // If we have no category criteria need to ensure we only view products that belong to current store
            final List<Query> store = productShopBuilder.createQueryChain(temp, ProductSearchQueryBuilder.PRODUCT_SHOP_FIELD, customerShopId);
            productQueryStrictClauses.add(store.get(0));
        }

        // Enforce in stock products
        final  List<Query> inStock = productShopStockBuilder.createQueryChain(temp, ProductSearchQueryBuilder.PRODUCT_SHOP_INSTOCK_FIELD, customerShopId);
        if (inStock != null) {
            productQueryStrictClauses.add(inStock.get(0));
        }

        // Enforce products with price
        final  List<Query> hasPrice = productShopPriceBuilder.createQueryChain(temp, ProductSearchQueryBuilder.PRODUCT_SHOP_HASPRICE_FIELD, customerShopId);
        if (hasPrice != null) {
            productQueryStrictClauses.add(hasPrice.get(0));
        }

        Query prod = null;
        Query sku = null;

        if (CollectionUtils.isEmpty(productQueryRelaxedClause)) {
            // strict matches only (e.g. filternav, tags)
            prod = productBoost(join(productQueryStrictClauses, BooleanClause.Occur.MUST));
            sku = join(skuQueryStrictClauses, BooleanClause.Occur.SHOULD); // boost in snowball

        } else {
            // keyword search

            int strictnessLevel = 0;
            for (int level = 0; level < productQueryRelaxedClause.size(); level++) {

                final Query extra = productQueryRelaxedClause.get(level);

                if (extra != null) {

                    strictnessLevel = level;
                    prod = join(productQueryStrictClauses, extra, BooleanClause.Occur.MUST);
                    final NavigationContext<Query> testProd = new LuceneNavigationContextImpl(shopId, customerShopId, customerLanguage, categories, includeSubCategories, navigationParameters, prod, null);

                    if (productService.getProductQty(testProd) > 0) {

                        prod = productBoost(prod);
                        break;

                    }
                }

            }

            final Query skuQueryWithSameLevel = skuQueryRelaxedClause.size() > strictnessLevel ? skuQueryRelaxedClause.get(strictnessLevel) : skuQueryRelaxedClause.get(skuQueryRelaxedClause.size() - 1);
            if (skuQueryStrictClauses.size() > 0) {
                sku = join(skuQueryStrictClauses, skuQueryWithSameLevel, BooleanClause.Occur.SHOULD);
            } else {
                sku = skuQueryWithSameLevel;
            }

        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Constructed nav queries: \nprod: {}\nsku:  {}", prod, sku);
        }

        return new LuceneNavigationContextImpl(shopId, customerShopId, customerLanguage, categories, includeSubCategories, navigationParameters, prod, sku);
    }

}
