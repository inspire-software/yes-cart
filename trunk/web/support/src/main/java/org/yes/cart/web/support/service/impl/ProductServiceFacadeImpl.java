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

package org.yes.cart.web.support.service.impl;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.cache.annotation.Cacheable;
import org.yes.cart.domain.dto.ProductSearchResultDTO;
import org.yes.cart.domain.dto.ProductSearchResultPageDTO;
import org.yes.cart.domain.dto.ProductSkuSearchResultDTO;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.query.LuceneQueryFactory;
import org.yes.cart.domain.query.NavigationContext;
import org.yes.cart.domain.query.ProductSearchQueryBuilder;
import org.yes.cart.service.domain.*;
import org.yes.cart.web.support.service.CategoryServiceFacade;
import org.yes.cart.web.support.service.ProductServiceFacade;

import java.math.BigDecimal;
import java.util.*;

/**
 * User: denispavlov
 * Date: 20/11/2014
 * Time: 19:59
 */
public class ProductServiceFacadeImpl implements ProductServiceFacade {

    private final ProductService productService;
    private final ProductSkuService productSkuService;
    private final ProductAssociationService productAssociationService;
    private final LuceneQueryFactory luceneQueryFactory;
    private final ProductAvailabilityStrategy productAvailabilityStrategy;
    private final ProductQuantityStrategy productQuantityStrategy;
    private final PriceService priceService;
    private final CategoryServiceFacade categoryServiceFacade;

    public ProductServiceFacadeImpl(final ProductService productService,
                                    final ProductSkuService productSkuService,
                                    final ProductAssociationService productAssociationService,
                                    final LuceneQueryFactory luceneQueryFactory,
                                    final ProductAvailabilityStrategy productAvailabilityStrategy,
                                    final ProductQuantityStrategy productQuantityStrategy,
                                    final PriceService priceService,
                                    final CategoryServiceFacade categoryServiceFacade) {
        this.productService = productService;
        this.productSkuService = productSkuService;
        this.productAssociationService = productAssociationService;
        this.luceneQueryFactory = luceneQueryFactory;
        this.productAvailabilityStrategy = productAvailabilityStrategy;
        this.productQuantityStrategy = productQuantityStrategy;
        this.priceService = priceService;
        this.categoryServiceFacade = categoryServiceFacade;
    }

    /**
     * {@inheritDoc}
     */
    public ProductSku getSkuById(final Long skuId) {

        return productService.getSkuById(skuId, true);

    }

    /**
     * {@inheritDoc}
     */
    public Product getProductById(final Long productId) {

        return productService.getProductById(productId, true);

    }

    /**
     * {@inheritDoc}
     */
    public ProductSku getProductSkuBySkuCode(final String skuCode) {

        return productSkuService.getProductSkuBySkuCode(skuCode);

    }

    /**
     * {@inheritDoc}
     */
    public Map<Pair<String, String>, Map<Pair<String, String>, List<Pair<String, String>>>> getProductAttributes(final String locale,
                                                                                                                 final long productId,
                                                                                                                 final long skuId,
                                                                                                                 final long productTypeId) {
        return productService.getProductAttributes(locale, productId, skuId, productTypeId);
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "productService-productAssociationsIds")
    public List<ProductSearchResultDTO> getProductAssociations(final long productId,
                                                               final long shopId,
                                                               final String associationType) {

        final List<Long> productIds = productAssociationService.getProductAssociationsIds(
                productId,
                associationType
        );

        if (productIds != null && !productIds.isEmpty()) {

            final NavigationContext assoc = luceneQueryFactory.getFilteredNavigationQueryChain(shopId, null,
                    Collections.singletonMap(ProductSearchQueryBuilder.PRODUCT_ID_FIELD,
                            (List) Arrays.asList(productIds)));

            return Collections.unmodifiableList(productService.getProductSearchResultDTOByQuery(
                    assoc.getProductQuery(), 0, productIds.size(), null, false).getResults());
        }

        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "productService-featuredProducts")
    public List<ProductSearchResultDTO> getFeaturedProducts(final long categoryId, final long shopId) {

        final List<Long> categories;
        if (categoryId == 0) {
            categories = null;
        } else {
            categories = Collections.singletonList(categoryId);
        }

        final int limit = categoryServiceFacade.getFeaturedListSizeConfig(categoryId, shopId);

        final NavigationContext featured = luceneQueryFactory.getFilteredNavigationQueryChain(shopId, categories,
                Collections.singletonMap(ProductSearchQueryBuilder.PRODUCT_FEATURED_FIELD,
                        (List) Arrays.asList("true")));

        return Collections.unmodifiableList(productService.getProductSearchResultDTOByQuery(
                featured.getProductQuery(), 0, limit, null, false).getResults());
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "productService-newProducts")
    public List<ProductSearchResultDTO> getNewProducts(final long categoryId, final long shopId) {

        final int limit = categoryServiceFacade.getNewArrivalListSizeConfig(categoryId, shopId);

        final List<Long> newArrivalCats;
        if (categoryId > 0L) {
            newArrivalCats = Collections.singletonList(categoryId);
        } else {
            newArrivalCats = null;
        }
        final NavigationContext newarrival = luceneQueryFactory.getFilteredNavigationQueryChain(shopId, newArrivalCats,
                Collections.singletonMap(ProductSearchQueryBuilder.PRODUCT_TAG_FIELD,
                        (List) Arrays.asList(ProductSearchQueryBuilder.TAG_NEWARRIVAL)));

        return Collections.unmodifiableList(productService.getProductSearchResultDTOByQuery(
                newarrival.getProductQuery(), 0, limit, null, true).getResults());
    }

    /**
     * {@inheritDoc}
     */
    public List<ProductSearchResultDTO> getListProducts(final List<String> productIds,
                                                        final long categoryId,
                                                        final long shopId) {
        if (CollectionUtils.isNotEmpty(productIds)) {

            List<String> productIdsForCategory = productIds;
            int limit = categoryServiceFacade.getNewArrivalListSizeConfig(categoryId, shopId);
            if (limit > productIds.size() || categoryId < 0L) {
                limit = productIds.size();
            } else {
                productIdsForCategory = productIds.subList(productIds.size() - limit, productIds.size());
            }

            final NavigationContext recent = luceneQueryFactory.getFilteredNavigationQueryChain(shopId, null,
                    Collections.singletonMap(ProductSearchQueryBuilder.PRODUCT_ID_FIELD,
                            (List) Arrays.asList(productIdsForCategory)));

            return Collections.unmodifiableList(productService.getProductSearchResultDTOByQuery(
                    recent.getProductQuery(), 0, limit, null, false).getResults());

        }

        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    public ProductSearchResultPageDTO getListProducts(final NavigationContext context,
                                                      final int firstResult,
                                                      final int maxResults,
                                                      final String sortFieldName,
                                                      final boolean descendingSort) {

        final ProductSearchResultPageDTO result = productService.getProductSearchResultDTOByQuery(
                context.getProductQuery(), firstResult, maxResults, sortFieldName, descendingSort
        ).copy(); // MUST BE COPY for each search as we are setting relevant SKU list

        if (!result.getResults().isEmpty()) {

            final NavigationContext skuContext = luceneQueryFactory.getSkuSnowBallQuery(context, result.getResults());

            final List<ProductSkuSearchResultDTO> skus = productSkuService.getProductSkuSearchResultDTOByQuery(
                    skuContext.getProductSkuQuery()
            );
            // Need list of skus to maintain priority order
            final Map<Long, List<ProductSkuSearchResultDTO>> skuMap = new HashMap<Long, List<ProductSkuSearchResultDTO>>();
            for (ProductSkuSearchResultDTO sku : skus) {
                List<ProductSkuSearchResultDTO> skuForProduct = skuMap.get(sku.getProductId());
                if (skuForProduct == null) {
                    skuForProduct = new ArrayList<ProductSkuSearchResultDTO>();
                    skuMap.put(sku.getProductId(), skuForProduct);
                }
                skuForProduct.add(sku);
            }
            for (final ProductSearchResultDTO product : result.getResults()) {
                product.setSkus(skuMap.get(product.getId()));
            }
        }
        return result;

    }

    /**
     * {@inheritDoc}
     */
    public ProductAvailabilityModel getProductAvailability(final ProductSearchResultDTO product, final long shopId) {

        return productAvailabilityStrategy.getAvailabilityModel(shopId, product);

    }

    /**
     * {@inheritDoc}
     */
    public ProductAvailabilityModel getProductAvailability(final Product product, final long shopId) {

        return productAvailabilityStrategy.getAvailabilityModel(shopId, product);

    }

    /**
     * {@inheritDoc}
     */
    public ProductAvailabilityModel getProductAvailability(final ProductSku product, final long shopId) {

        return productAvailabilityStrategy.getAvailabilityModel(shopId, product);

    }

    /**
     * {@inheritDoc}
     */
    public ProductQuantityModel getProductQuantity(final BigDecimal cartQty, final Product product) {

        return productQuantityStrategy.getQuantityModel(cartQty, product);

    }

    /**
     * {@inheritDoc}
     */
    public ProductQuantityModel getProductQuantity(final BigDecimal cartQty, final ProductSku product) {

        return productQuantityStrategy.getQuantityModel(cartQty, product);

    }

    /**
     * {@inheritDoc}
     */
    public ProductQuantityModel getProductQuantity(final BigDecimal cartQty, final ProductSearchResultDTO product) {

        return productQuantityStrategy.getQuantityModel(cartQty, product);

    }

    /**
     * {@inheritDoc}
     */
    public SkuPrice getSkuPrice(final Long productId,
                                final String skuCode,
                                final BigDecimal quantity,
                                final String currency,
                                final long shopId) {
        return priceService.getMinimalPrice(productId, skuCode, shopId, currency, quantity);
    }

    /**
     * {@inheritDoc}
     */
    public Collection<SkuPrice> getSkuPrices(final Long productId,
                                             final String skuCode,
                                             final String currency,
                                             final long shopId) {
        return priceService.getAllCurrentPrices(productId, skuCode, shopId, currency);
    }
}
