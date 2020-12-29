/*
 * Copyright 2009 Inspire-Software.com
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
import org.apache.commons.lang.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.yes.cart.constants.Constants;
import org.yes.cart.domain.dto.ProductSearchResultDTO;
import org.yes.cart.domain.dto.ProductSearchResultPageDTO;
import org.yes.cart.domain.dto.ProductSkuSearchResultDTO;
import org.yes.cart.domain.dto.ProductSkuSearchResultPageDTO;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.entity.impl.PriceModelImpl;
import org.yes.cart.domain.entity.impl.PromotionModelImpl;
import org.yes.cart.domain.i18n.impl.FailoverStringI18NModel;
import org.yes.cart.domain.i18n.impl.StringI18NModel;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.search.SearchQueryFactory;
import org.yes.cart.search.dto.NavigationContext;
import org.yes.cart.search.query.ProductSearchQueryBuilder;
import org.yes.cart.service.domain.*;
import org.yes.cart.shoppingcart.*;
import org.yes.cart.utils.MoneyUtils;
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
    private final SearchQueryFactory searchQueryFactory;
    private final ProductAvailabilityStrategy productAvailabilityStrategy;
    private final ProductQuantityStrategy productQuantityStrategy;
    private final PricingPolicyProvider pricingPolicyProvider;
    private final PriceResolver priceResolver;
    private final ShoppingCartCalculator shoppingCartCalculator;
    private final PromotionService promotionService;
    private final CategoryServiceFacade categoryServiceFacade;
    private final ShopService shopService;
    private final BrandService brandService;

    public ProductServiceFacadeImpl(final ProductService productService,
                                    final ProductSkuService productSkuService,
                                    final ProductAssociationService productAssociationService,
                                    final SearchQueryFactory searchQueryFactory,
                                    final ProductAvailabilityStrategy productAvailabilityStrategy,
                                    final ProductQuantityStrategy productQuantityStrategy,
                                    final PricingPolicyProvider pricingPolicyProvider,
                                    final PriceResolver priceResolver,
                                    final ShoppingCartCalculator shoppingCartCalculator,
                                    final PromotionService promotionService,
                                    final CategoryServiceFacade categoryServiceFacade,
                                    final ShopService shopService,
                                    final BrandService brandService) {
        this.productService = productService;
        this.productSkuService = productSkuService;
        this.productAssociationService = productAssociationService;
        this.searchQueryFactory = searchQueryFactory;
        this.productAvailabilityStrategy = productAvailabilityStrategy;
        this.productQuantityStrategy = productQuantityStrategy;
        this.pricingPolicyProvider = pricingPolicyProvider;
        this.priceResolver = priceResolver;
        this.shoppingCartCalculator = shoppingCartCalculator;
        this.categoryServiceFacade = categoryServiceFacade;
        this.promotionService = promotionService;
        this.shopService = shopService;
        this.brandService = brandService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProductSku getSkuById(final Long skuId) {

        return productService.getSkuById(skuId, true);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Product getProductById(final Long productId) {

        return productService.getProductById(productId, true);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProductSku getProductSkuBySkuCode(final String skuCode) {

        return productSkuService.getProductSkuBySkuCode(skuCode);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProductAttributesModel getProductAttributes(final long productId,
                                                       final long skuId,
                                                       final long productTypeId) {
        return productService.getProductAttributes(productId, skuId, productTypeId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProductCompareModel getCompareAttributes(final List<Long> productId,
                                                    final List<Long> skuId) {

        return productService.getCompareAttributes(productId, skuId);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable(value = "productService-productAssociations")
    public List<ProductSearchResultDTO> getProductAssociations(final long productId,
                                                               final long shopId,
                                                               final long customerShopId,
                                                               final String associationType) {

        final List<String> productIds = productAssociationService.getProductAssociationsProductCodes(
                productId,
                associationType
        );

        if (productIds != null && !productIds.isEmpty()) {


            final List<String> search = new ArrayList<>(productIds.size() > 50 ? productIds.subList(0, 50) : productIds);

            final NavigationContext assoc = searchQueryFactory.getFilteredNavigationQueryChain(shopId, customerShopId, null, null,
                    false, Collections.singletonMap(ProductSearchQueryBuilder.PRODUCT_CODE_FIELD,
                            search));

            return Collections.unmodifiableList(getListProducts(
                    assoc, 0, search.size(), null, false).getResults());
        }

        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable(value = "productService-featuredProducts")
    public List<ProductSearchResultDTO> getFeaturedProducts(final long categoryId,
                                                            final long shopId,
                                                            final long customerShopId) {

        final List<Long> categories;
        if (categoryId == 0) {
            categories = null;
        } else {
            categories = Collections.singletonList(categoryId);
        }

        final int limit = categoryServiceFacade.getFeaturedListSizeConfig(categoryId, customerShopId);

        final NavigationContext featured = searchQueryFactory.getFilteredNavigationQueryChain(shopId, customerShopId, null, categories,
                false, Collections.singletonMap(ProductSearchQueryBuilder.PRODUCT_FEATURED_FIELD,
                        Collections.singletonList("true")));

        return Collections.unmodifiableList(getListProducts(
                featured, 0, limit, null, false).getResults());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable(value = "productService-newProducts")
    public List<ProductSearchResultDTO> getNewProducts(final long categoryId,
                                                       final long shopId,
                                                       final long customerShopId) {

        final List<Long> newArrivalCats;
        if (categoryId > 0L) {
            newArrivalCats = Collections.singletonList(categoryId);
        } else {
            newArrivalCats = null;
        }

        final int limit = categoryServiceFacade.getNewArrivalListSizeConfig(categoryId, customerShopId);

        final NavigationContext newarrival = searchQueryFactory.getFilteredNavigationQueryChain(shopId, customerShopId, null, newArrivalCats,
                false, Collections.singletonMap(ProductSearchQueryBuilder.PRODUCT_TAG_FIELD,
                        Collections.singletonList(ProductSearchQueryBuilder.TAG_NEWARRIVAL)));

        return Collections.unmodifiableList(getListProducts(
                newarrival, 0, limit, null, true).getResults());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable(value = "productService-taggedProducts")
    public List<ProductSearchResultDTO> getTaggedProducts(final long categoryId,
                                                          final long shopId,
                                                          final long customerShopId,
                                                          final String tag) {

        final List<Long> categories;
        if (categoryId == 0) {
            categories = null;
        } else {
            categories = Collections.singletonList(categoryId);
        }

        final int limit = categoryServiceFacade.getFeaturedListSizeConfig(categoryId, customerShopId);

        final NavigationContext tagged = searchQueryFactory.getFilteredNavigationQueryChain(shopId, customerShopId, null, categories,
                false, Collections.singletonMap(ProductSearchQueryBuilder.PRODUCT_TAG_FIELD,
                        Collections.singletonList(tag)));

        return Collections.unmodifiableList(getListProducts(
                tagged, 0, limit, null, false).getResults());
    }

    @Override
    public List<ProductSearchResultDTO> getListProductSKUs(final List<String> SKUs,
                                                           final long categoryId,
                                                           final long shopId,
                                                           final long customerShopId) {
        if (CollectionUtils.isNotEmpty(SKUs)) {

            final List<String> SKUsForCategory = new ArrayList<>(SKUs);

            final NavigationContext recent = searchQueryFactory.getFilteredNavigationQueryChain(shopId, customerShopId, null, null,
                    false, Collections.singletonMap(ProductSearchQueryBuilder.SKU_PRODUCT_CODE_FIELD,
                            SKUsForCategory));

            return Collections.unmodifiableList(getListProducts(
                    recent, 0, -1, null, false).getResults());

        }

        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ProductSearchResultDTO> getListProducts(final List<String> productIds,
                                                        final long categoryId,
                                                        final long shopId,
                                                        final long customerShopId) {
        if (CollectionUtils.isNotEmpty(productIds)) {

            final List<String> productIdsForCategory = new ArrayList<>(productIds);

            final NavigationContext recent = searchQueryFactory.getFilteredNavigationQueryChain(shopId, customerShopId, null, null,
                    false, Collections.singletonMap(ProductSearchQueryBuilder.PRODUCT_ID_FIELD,
                            productIdsForCategory));

            return Collections.unmodifiableList(getListProducts(
                    recent, 0, -1, null, false).getResults());

        }

        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProductSearchResultPageDTO getListProducts(final NavigationContext context,
                                                      final int firstResult,
                                                      final int maxResults,
                                                      final String sortFieldName,
                                                      final boolean descendingSort) {

        final ProductSearchResultPageDTO result = productService.getProductSearchResultDTOByQuery(
                context, firstResult, maxResults, sortFieldName, descendingSort
        ).copy(); // MUST BE COPY for each search as we are setting relevant SKU list

        if (!result.getResults().isEmpty()) {

            final NavigationContext skuContext = searchQueryFactory.getSkuSnowBallQuery(context, result.getResults());

            final ProductSkuSearchResultPageDTO skus = productSkuService.getProductSkuSearchResultDTOByQuery(
                    skuContext
            ).copy(); // MUST BE COPY
            // Need list of skus to maintain priority order
            final Map<Long, List<ProductSkuSearchResultDTO>> skuMap = new HashMap<>();
            for (ProductSkuSearchResultDTO sku : skus.getResults()) {
                List<ProductSkuSearchResultDTO> skuForProduct = skuMap.computeIfAbsent(sku.getProductId(), k -> new ArrayList<>());
                skuForProduct.add(sku);
            }
            for (final ProductSearchResultDTO product : result.getResults()) {
                final List<ProductSkuSearchResultDTO> relevantSKUs = skuMap.get(product.getId());
                final List<ProductSkuSearchResultDTO> sortedSKUs = new ArrayList<>();
                if (relevantSKUs != null) {
                    for (final ProductSkuSearchResultDTO relevantSKU : relevantSKUs) {
                        if (relevantSKU.getFulfilmentCentreCode().equals(product.getFulfilmentCentreCode())) {
                            final ProductSkuSearchResultDTO baseSKU = product.getBaseSku(relevantSKU.getId());
                            if (baseSKU != null) {
                                sortedSKUs.add(baseSKU);
                            }
                        }
                    }
                } // else this is most likely SKU reindexing not been done
                product.setSearchSkus(sortedSKUs);
            }

        }
        return result;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProductAvailabilityModel getProductAvailability(final ProductSearchResultDTO product,
                                                           final long customerShopId) {

        return productAvailabilityStrategy.getAvailabilityModel(customerShopId, product);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProductAvailabilityModel getProductAvailability(final ProductSkuSearchResultDTO sku,
                                                           final long customerShopId) {

        return productAvailabilityStrategy.getAvailabilityModel(customerShopId, sku);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProductAvailabilityModel getProductAvailability(final Product product,
                                                           final long customerShopId,
                                                           final String supplier) {

        return productAvailabilityStrategy.getAvailabilityModel(customerShopId, product, supplier);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProductAvailabilityModel getProductAvailability(final ProductSku product,
                                                           final long customerShopId,
                                                           final String supplier) {

        return productAvailabilityStrategy.getAvailabilityModel(customerShopId, product, supplier);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProductAvailabilityModel getProductAvailability(final String skuCode,
                                                           final long customerShopId,
                                                           final String supplier) {

        return productAvailabilityStrategy.getAvailabilityModel(customerShopId, skuCode, supplier);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QuantityModel getProductQuantity(final BigDecimal cartQty,
                                            final Product product,
                                            final long customerShopId,
                                            final String supplier) {

        return productQuantityStrategy.getQuantityModel(customerShopId, cartQty, product, supplier);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QuantityModel getProductQuantity(final BigDecimal cartQty,
                                            final ProductSku product,
                                            final long customerShopId,
                                            final String supplier) {

        return productQuantityStrategy.getQuantityModel(customerShopId, cartQty, product, supplier);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QuantityModel getProductQuantity(final BigDecimal cartQty,
                                            final ProductSearchResultDTO product,
                                            final long customerShopId) {

        return productQuantityStrategy.getQuantityModel(customerShopId, cartQty, product);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QuantityModel getProductQuantity(final BigDecimal cartQty,
                                            final ProductSkuSearchResultDTO sku,
                                            final long customerShopId) {

        return productQuantityStrategy.getQuantityModel(customerShopId, cartQty, sku);

    }

    @Override
    public QuantityModel getProductQuantity(final BigDecimal cartQty,
                                            final String sku,
                                            final String supplier,
                                            final long customerShopId) {

        return productQuantityStrategy.getQuantityModel(customerShopId, cartQty, sku, supplier);
        
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QuantityModel getProductQuantity(final BigDecimal cartQty,
                                            final String sku,
                                            final String supplier,
                                            final BigDecimal min,
                                            final BigDecimal max,
                                            final BigDecimal step,
                                            final long customerShopId) {

        return productQuantityStrategy.getQuantityModel(customerShopId, cartQty, sku, min, max, step, supplier);

    }

    private boolean isPriceValid(final SkuPrice price) {
        return price != null && price.getRegularPrice() != null && !price.isPriceUponRequest();
    }

    /**
     * We resolve prices from current customer shop first. In simple setup this would be the same as the master.
     * In case current and master differs we are in B2B mode, so we check if we are not in strict profile and
     * attempt to resolve price from master.
     *
     * @param cart      cart
     * @param productId productId (in case no specific sku is selected)
     * @param sku       sku to resolve price for
     * @param qty       quantity
     * @param supplier  supplier
     *
     * @return resolved SKU price
     */
    protected SkuPrice resolveMinimalPrice(final ShoppingCart cart,
                                           final Long productId,
                                           final String sku,
                                           final BigDecimal qty,
                                           final String supplier) {

        final long customerShopId = cart.getShoppingContext().getCustomerShopId();
        final long masterShopId = cart.getShoppingContext().getShopId();
        // Fallback only if we have a B2B non-strict mode
        final Long fallbackShopId = masterShopId == customerShopId || shopService.getById(customerShopId).isB2BStrictPriceActive() ? null : masterShopId;
        final String shopCode = cart.getShoppingContext().getShopCode();
        final String currency = cart.getCurrencyCode();

        // Policy is setup on master
        final PricingPolicyProvider.PricingPolicy policy = pricingPolicyProvider.determinePricingPolicy(
                shopCode, currency, cart.getCustomerLogin(),
                cart.getShoppingContext().getCountryCode(),
                cart.getShoppingContext().getStateCode()
        );

        return priceResolver.getMinimalPrice(
                productId,
                sku,
                customerShopId,
                fallbackShopId,
                currency,
                qty,
                false,
                policy.getID(),
                supplier
        );

    }


    /**
     * We resolve prices from current customer shop first. In simple setup this would be the same as the master.
     * In case current and master differs we are in B2B mode, so we check if we are not in strict profile and
     * attempt to resolve price from master.
     *
     * @param cart      cart
     * @param productId productId (in case no specific sku is selected)
     * @param sku       sku to resolve price for
     * @param supplier  supplier
     *
     * @return resolved SKU price
     */
    protected Collection<SkuPrice> resolvePrices(final ShoppingCart cart,
                                                 final Long productId,
                                                 final String sku,
                                                 final String supplier) {

        final long customerShopId = cart.getShoppingContext().getCustomerShopId();
        final long masterShopId = cart.getShoppingContext().getShopId();
        // Fallback only if we have a B2B non-strict mode
        final Long fallbackShopId = masterShopId == customerShopId || shopService.getById(customerShopId).isB2BStrictPriceActive() ? null : masterShopId;
        final String shopCode = cart.getShoppingContext().getShopCode();
        final String currency = cart.getCurrencyCode();

        // Policy is setup on master
        final PricingPolicyProvider.PricingPolicy policy = pricingPolicyProvider.determinePricingPolicy(
                shopCode, currency, cart.getCustomerLogin(),
                cart.getShoppingContext().getCountryCode(),
                cart.getShoppingContext().getStateCode()
        );

        return priceResolver.getAllCurrentPrices(
                productId,
                sku,
                customerShopId,
                fallbackShopId,
                currency,
                policy.getID(),
                supplier
        );

    }

    /**
     * Model that does not show any details, NULL object.
     *
     * @param currency currency
     *
     * @return null price model
     */
    protected PriceModelImpl getNullProductPriceModel(final String currency) {
        return new PriceModelImpl(
                null,
                currency,
                null,
                false,
                false,
                null,
                null
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pair<PriceModel, CustomerWishList.PriceChange> getSkuPrice(final ShoppingCart cart,
                                                                      final CustomerWishList item) {

        if (cart.getShoppingContext().isHidePrices()) {
            return new Pair<>(
                    getNullProductPriceModel(cart.getCurrencyCode()),
                    new CustomerWishList.PriceChange(CustomerWishList.PriceChangeType.NOCHANGE, null)
            );
        }

        final String sku = item.getSkuCode();
        final String supplier = item.getSupplierCode();
        final BigDecimal qty = item.getQuantity();

        final SkuPrice priceNow = resolveMinimalPrice(cart, null, sku, qty, supplier);

        final String addedPriceCurr = item.getRegularPriceCurrencyWhenAdded();
        final Pair<BigDecimal, BigDecimal> price;
        final CustomerWishList.PriceChange priceInfo;
        if (isPriceValid(priceNow)) {
            if (cart.getCurrencyCode().equals(addedPriceCurr)) {
                final BigDecimal addedPrice = item.getRegularPriceWhenAdded();
                final Pair<BigDecimal, BigDecimal> listAndSale = priceNow.getSalePriceForCalculation();
                final BigDecimal saleNow = MoneyUtils.secondOrFirst(listAndSale);
                if (MoneyUtils.isFirstEqualToSecond(addedPrice, saleNow)) {
                    // no change
                    if (MoneyUtils.isFirstBiggerThanSecond(listAndSale.getFirst(), addedPrice)) {
                        price = new Pair<>(listAndSale.getFirst(), addedPrice);
                        priceInfo = new CustomerWishList.PriceChange(
                                CustomerWishList.PriceChangeType.ONSALE,
                                MoneyUtils.getDiscountDisplayValue(listAndSale.getFirst(), addedPrice)
                        );
                    } else {
                        // not on sale
                        price = new Pair<>(addedPrice, null);
                        priceInfo = new CustomerWishList.PriceChange(CustomerWishList.PriceChangeType.NOCHANGE, null);
                    }
                } else if (MoneyUtils.isFirstBiggerThanSecond(addedPrice, saleNow)) {
                    // price dropped since added
                    price = new Pair<>(addedPrice, saleNow);
                    priceInfo = new CustomerWishList.PriceChange(
                            CustomerWishList.PriceChangeType.DECREASED,
                            MoneyUtils.getDiscountDisplayValue(addedPrice, saleNow)
                    );
                } else if (MoneyUtils.isPositive(addedPrice)) {
                    // price gone up
                    price = listAndSale;
                    priceInfo = new CustomerWishList.PriceChange(
                            CustomerWishList.PriceChangeType.INCRSEASED,
                            MoneyUtils.getDiscountDisplayValue(addedPrice, saleNow).negate()
                    );
                } else {
                    // invalid config - old price is zero or negative
                    price = listAndSale;
                    priceInfo = new CustomerWishList.PriceChange(CustomerWishList.PriceChangeType.NOCHANGE, null);
                }
            } else {
                // no comparative price - different currency
                price = priceNow.getSalePriceForCalculation();
                priceInfo = new CustomerWishList.PriceChange(CustomerWishList.PriceChangeType.DIFFCURRENCY, null);
            }
        } else {
            // Item is not priced now so it cannot be bought
            price = new Pair<>(null, null);
            priceInfo = new CustomerWishList.PriceChange(CustomerWishList.PriceChangeType.OFFLINE, null);
        }

        final PriceModel model = getSkuPrice(
                cart,
                sku,
                BigDecimal.ONE,
                priceNow.isPriceUponRequest(),
                priceNow.isPriceOnOffer(),
                price.getFirst(),
                price.getSecond(),
                supplier
        );

        return new Pair<>(model, priceInfo);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PriceModel getSkuPrice(final ShoppingCart cart,
                                  final Long productId,
                                  final String skuCode,
                                  final BigDecimal quantity,
                                  final String supplier) {

        final String currency = cart.getCurrencyCode();

        if (!cart.getShoppingContext().isHidePrices()) {
            final SkuPrice resolved = resolveMinimalPrice(cart, productId, skuCode, quantity, supplier);

            if (resolved != null) {

                final Pair<BigDecimal, BigDecimal> listAndSale = resolved.getSalePriceForCalculation();

                return getSkuPrice(
                        cart,
                        resolved.getSkuCode(),
                        resolved.getQuantity(),
                        resolved.isPriceUponRequest(),
                        resolved.isPriceOnOffer(),
                        listAndSale.getFirst(),
                        listAndSale.getSecond(),
                        supplier
                );

            }
        }
        return getNullProductPriceModel(currency);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PriceModel getSkuPrice(final ShoppingCart cart,
                                  final String ref,
                                  final BigDecimal quantity,
                                  final BigDecimal listPrice,
                                  final BigDecimal salePrice,
                                  final String supplier) {
        return getSkuPrice(
                cart,
                ref,
                quantity,
                false,
                false,
                listPrice,
                salePrice,
                supplier
        );
    }

    protected PriceModel getSkuPrice(final ShoppingCart cart,
                                     final String ref,
                                     final BigDecimal quantity,
                                     final boolean priceUponRequest,
                                     final boolean priceOnOffer,
                                     final BigDecimal listPrice,
                                     final BigDecimal salePrice,
                                     final String supplier) {

        final String currency = cart.getCurrencyCode();

        if (cart.getShoppingContext().isHidePrices()) {
            return getNullProductPriceModel(currency);
        }

        final boolean showTax = cart.getShoppingContext().isTaxInfoEnabled();
        final boolean showTaxNet = showTax && cart.getShoppingContext().isTaxInfoUseNet();
        final boolean showTaxAmount = showTax && cart.getShoppingContext().isTaxInfoShowAmount();

        final BigDecimal sale = salePrice;
        final BigDecimal list = listPrice;

        if (showTax) {
            // prices with tax
            if (sale != null) {
                // if sale price exists use it as primary target as this one will be shown
                ShoppingCartCalculator.PriceModel saleModel = shoppingCartCalculator.calculatePrice(cart, supplier, ref, sale);

                final BigDecimal saleAdjusted, listAdjusted;

                if (showTaxNet) {
                    saleAdjusted = saleModel.getNetPrice();
                    // recalculate list price so that discounts are correct
                    listAdjusted = list != null ? MoneyUtils.getNetAmount(list, saleModel.getTaxRate(), !saleModel.isTaxExclusive()) : null;
                } else {
                    saleAdjusted = saleModel.getGrossPrice();
                    // recalculate list price so that discounts are correct
                    listAdjusted = list != null ? MoneyUtils.getGrossAmount(list, saleModel.getTaxRate(), !saleModel.isTaxExclusive()) : null;
                }

                return new PriceModelImpl(
                        ref,
                        currency,
                        quantity,
                        priceUponRequest,
                        priceOnOffer,
                        listAdjusted, saleAdjusted,
                        showTax, showTaxNet, showTaxAmount,
                        saleModel.getTaxCode(),
                        saleModel.getTaxRate(),
                        saleModel.isTaxExclusive(),
                        saleModel.getTaxAmount()
                );

            } else if (list != null) {
                // use list price to calculate taxes
                ShoppingCartCalculator.PriceModel listModel = shoppingCartCalculator.calculatePrice(cart, supplier, ref, list);

                final BigDecimal listAdjusted = showTaxNet ? listModel.getNetPrice() : listModel.getGrossPrice();

                return new PriceModelImpl(
                        ref,
                        currency,
                        quantity,
                        priceUponRequest,
                        priceOnOffer,
                        listAdjusted, null,
                        showTax, showTaxNet, showTaxAmount,
                        listModel.getTaxCode(),
                        listModel.getTaxRate(),
                        listModel.isTaxExclusive(),
                        listModel.getTaxAmount()
                );

            }

        }
        // standard "as is" prices
        return new PriceModelImpl(
                ref,
                currency,
                quantity,
                priceUponRequest,
                priceOnOffer,
                list, sale
        );

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PriceModel getSkuPrice(final ShoppingCart cart,
                                  final CartItem item,
                                  final boolean total) {

        if (cart.getShoppingContext().isHidePrices()) {
            return getNullProductPriceModel(cart.getCurrencyCode());
        }

        final boolean enabled = cart.getShoppingContext().isTaxInfoEnabled();
        return getSkuPrice(
                cart.getCurrencyCode(),
                enabled,
                enabled && cart.getShoppingContext().isTaxInfoUseNet(),
                enabled && cart.getShoppingContext().isTaxInfoShowAmount(),
                item, total, false);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PriceModel getSkuPrice(final String currency,
                                  final boolean showTax,
                                  final boolean showTaxNet,
                                  final boolean showTaxAmount,
                                  final CartItem item,
                                  final boolean total,
                                  final boolean hide) {

        if (hide) {
            return getNullProductPriceModel(currency);
        }

        if (showTax) {
            if (item.getSalePrice() != null && MoneyUtils.isFirstBiggerThanSecond(item.getListPrice(), item.getPrice())) {

                if (total) {

                    // if sale price exists use it as primary target as this one will be shown
                    final BigDecimal saleAdjusted, listAdjusted;

                    if (showTaxNet) {
                        saleAdjusted = item.getNetPrice().multiply(item.getQty()).setScale(Constants.MONEY_SCALE, BigDecimal.ROUND_HALF_UP);
                        // recalculate list price so that discounts are correct
                        listAdjusted = item.getListPrice() != null ?
                                MoneyUtils.getMoney(item.getListPrice(), item.getTaxRate(), !item.isTaxExclusiveOfPrice()).getNet().multiply(item.getQty()).setScale(Constants.MONEY_SCALE, BigDecimal.ROUND_HALF_UP) : null;
                    } else {
                        saleAdjusted = item.getGrossPrice().multiply(item.getQty()).setScale(Constants.MONEY_SCALE, BigDecimal.ROUND_HALF_UP);
                        // recalculate list price so that discounts are correct
                        listAdjusted = item.getListPrice() != null ?
                                MoneyUtils.getMoney(item.getListPrice(), item.getTaxRate(), !item.isTaxExclusiveOfPrice()).getGross().multiply(item.getQty()).setScale(Constants.MONEY_SCALE, BigDecimal.ROUND_HALF_UP) : null;
                    }

                    return new PriceModelImpl(
                            item.getProductSkuCode(),
                            currency,
                            item.getQty(),
                            false,
                            false,
                            listAdjusted, saleAdjusted,
                            showTax, showTaxNet, showTaxAmount,
                            item.getTaxCode(),
                            item.getTaxRate(),
                            item.isTaxExclusiveOfPrice(),
                            item.getGrossPrice().subtract(item.getNetPrice()).multiply(item.getQty()).setScale(Constants.MONEY_SCALE, BigDecimal.ROUND_HALF_UP)
                    );

                }

                final BigDecimal list = item.getListPrice();

                // if sale price exists use it as primary target as this one will be shown
                final BigDecimal saleAdjusted, listAdjusted;

                if (showTaxNet) {
                    saleAdjusted = item.getNetPrice();
                    // recalculate list price so that discounts are correct
                    listAdjusted = list != null ? MoneyUtils.getNetAmount(list, item.getTaxRate(), !item.isTaxExclusiveOfPrice()) : null;
                } else {
                    saleAdjusted = item.getGrossPrice();
                    // recalculate list price so that discounts are correct
                    listAdjusted = list != null ? MoneyUtils.getGrossAmount(list, item.getTaxRate(), !item.isTaxExclusiveOfPrice()) : null;
                }

                return new PriceModelImpl(
                        item.getProductSkuCode(),
                        currency,
                        item.getQty(),
                        false,
                        false,
                        listAdjusted, saleAdjusted,
                        showTax, showTaxNet, showTaxAmount,
                        item.getTaxCode(),
                        item.getTaxRate(),
                        item.isTaxExclusiveOfPrice(),
                        item.getGrossPrice().subtract(item.getNetPrice())
                );

            } else if (item.getListPrice() != null) {

                if (total) {

                    // use list price to calculate taxes
                    final BigDecimal listAdjusted = showTaxNet ? item.getNetPrice().multiply(item.getQty()) : item.getGrossPrice().multiply(item.getQty());

                    return new PriceModelImpl(
                            item.getProductSkuCode(),
                            currency,
                            item.getQty(),
                            false,
                            false,
                            listAdjusted.setScale(Constants.MONEY_SCALE, BigDecimal.ROUND_HALF_UP), null,
                            showTax, showTaxNet, showTaxAmount,
                            item.getTaxCode(),
                            item.getTaxRate(),
                            item.isTaxExclusiveOfPrice(),
                            item.getGrossPrice().subtract(item.getNetPrice()).multiply(item.getQty()).setScale(Constants.MONEY_SCALE, BigDecimal.ROUND_HALF_UP)
                    );

                }

                // use list price to calculate taxes
                final BigDecimal listAdjusted = showTaxNet ? item.getNetPrice() : item.getGrossPrice();

                return new PriceModelImpl(
                        item.getProductSkuCode(),
                        currency,
                        item.getQty(),
                        false,
                        false,
                        listAdjusted, null,
                        showTax, showTaxNet, showTaxAmount,
                        item.getTaxCode(),
                        item.getTaxRate(),
                        item.isTaxExclusiveOfPrice(),
                        item.getGrossPrice().subtract(item.getNetPrice())
                );

            }
        }

        final BigDecimal sale = total ?
                (item.getPrice() != null && !MoneyUtils.isFirstEqualToSecond(item.getPrice(), item.getListPrice()) ?
                        item.getPrice().multiply(item.getQty()).setScale(Constants.MONEY_SCALE, BigDecimal.ROUND_HALF_UP) :
                        null) :
                (item.getPrice() != null && !MoneyUtils.isFirstEqualToSecond(item.getPrice(), item.getListPrice()) ?
                        item.getPrice() :
                        null);
        final BigDecimal list = total ?
                (item.getListPrice() != null ?
                        item.getListPrice().multiply(item.getQty()).setScale(Constants.MONEY_SCALE, BigDecimal.ROUND_HALF_UP) :
                        null) :
                item.getListPrice();

        // standard "as is" prices
        return new PriceModelImpl(
                item.getProductSkuCode(),
                currency,
                item.getQty(),
                false,
                false,
                list, sale
        );


    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<PriceModel> getSkuPrices(final ShoppingCart cart,
                                         final Long productId,
                                         final String skuCode,
                                         final String supplier) {

        if (!cart.getShoppingContext().isHidePrices()) {
            final Collection<SkuPrice> prices = resolvePrices(cart, productId, skuCode, supplier);

            if (CollectionUtils.isNotEmpty(prices)) {

                final List<PriceModel> models = new ArrayList<>(prices.size());
                for (final SkuPrice price : prices) {

                    final Pair<BigDecimal, BigDecimal> listAndSale = price.getSalePriceForCalculation();

                    models.add(getSkuPrice(
                            cart,
                            price.getSkuCode(),
                            price.getQuantity(),
                            price.isPriceUponRequest(),
                            price.isPriceOnOffer(),
                            listAndSale.getFirst(),
                            listAndSale.getSecond(),
                            supplier
                    ));

                }
                return models;

            }
        }
        return Collections.emptyList();
    }

    static final String CART_ITEMS_TOTAL_REF = "cart-items-total";

    /**
     * {@inheritDoc}
     */
    @Override
    public PriceModel getCartItemsTotal(final ShoppingCart cart) {

        final String currency = cart.getCurrencyCode();

        if (cart.getShoppingContext().isHidePrices()) {
            return getNullProductPriceModel(currency);
        }

        final BigDecimal list = cart.getTotal().getListSubTotal();
        final BigDecimal sale = cart.getTotal().getSubTotal();

        final boolean showTax = cart.getShoppingContext().isTaxInfoEnabled();
        final boolean showTaxNet = showTax && cart.getShoppingContext().isTaxInfoUseNet();
        final boolean showTaxAmount = showTax && cart.getShoppingContext().isTaxInfoShowAmount();

        if (showTax) {

            final BigDecimal totalInclTax = cart.getTotal().getSubTotalAmount();

            if (MoneyUtils.isPositive(totalInclTax)) {
                final BigDecimal totalTax = cart.getTotal().getSubTotalTax();
                final BigDecimal net = totalInclTax.subtract(totalTax);
                final BigDecimal gross = totalInclTax;

                final BigDecimal totalAdjusted = showTaxNet ? net : gross;

                final Set<String> taxes = new TreeSet<>(); // sorts and de-duplicate
                final Set<BigDecimal> rates = new TreeSet<>();
                for (final CartItem item : cart.getCartItemList()) {
                    if (StringUtils.isNotBlank(item.getTaxCode())) {
                        taxes.add(item.getTaxCode());
                    }
                    rates.add(item.getTaxRate());
                }

                final BigDecimal taxRate;
                if (MoneyUtils.isPositive(totalTax) && rates.size() > 1) {
                    // mixed rates in cart we use average with round up so that tax is not reduced by rounding
                    taxRate = totalTax.multiply(MoneyUtils.HUNDRED)
                            .divide(net, 1, BigDecimal.ROUND_HALF_UP)
                                .setScale(Constants.MONEY_SCALE, BigDecimal.ROUND_HALF_UP);
                } else {
                    // single rate for all items, use it to prevent rounding errors
                    taxRate = rates.iterator().next();
                }

                final String tax = StringUtils.join(taxes, ',');
                final boolean exclusiveTax = totalInclTax.compareTo(sale) > 0;

                if (MoneyUtils.isFirstBiggerThanSecond(list, sale)) {
                    // if we have discounts

                    final MoneyUtils.Money listMoney = MoneyUtils.getMoney(list, taxRate, !exclusiveTax);
                    final BigDecimal listAdjusted = showTaxNet ? listMoney.getNet() : listMoney.getGross();

                    return new PriceModelImpl(
                            CART_ITEMS_TOTAL_REF,
                            currency,
                            BigDecimal.ONE,
                            false,
                            false,
                            listAdjusted, totalAdjusted,
                            showTax, showTaxNet, showTaxAmount,
                            tax,
                            taxRate,
                            exclusiveTax,
                            totalTax
                    );

                }
                // no discounts
                return new PriceModelImpl(
                        CART_ITEMS_TOTAL_REF,
                        currency,
                        BigDecimal.ONE,
                        false,
                        false,
                        totalAdjusted, null,
                        showTax, showTaxNet, showTaxAmount,
                        tax,
                        taxRate,
                        exclusiveTax,
                        totalTax
                );

            }
        }

        // standard "as is" prices

        if (MoneyUtils.isPositive(sale)
                && MoneyUtils.isFirstBiggerThanSecond(list, sale)) {
            // if we have discounts
            return new PriceModelImpl(
                    CART_ITEMS_TOTAL_REF,
                    currency,
                    BigDecimal.ONE,
                    false,
                    false,
                    list, sale
            );

        }
        // no discounts
        return new PriceModelImpl(
                CART_ITEMS_TOTAL_REF,
                currency,
                BigDecimal.ONE,
                false,
                false,
                sale, null
        );

    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, PromotionModel> getPromotionModel(final String appliedPromo) {
        return getPromotionModel(appliedPromo, false);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, PromotionModel> getPromotionModel(final String appliedPromo, final boolean includeOffline) {

        if (StringUtils.isBlank(appliedPromo)) {
            return Collections.emptyMap();
        }

        final String[] promoCodes = StringUtils.split(appliedPromo, ',');
        final Map<String, PromotionModel> result = new LinkedHashMap<>(promoCodes.length * 2);

        for (final String code : promoCodes) {

            final int pos = code.indexOf(':');
            if (pos == -1) {
                // simple promo
                if ("#OFFLINE#".equals(code)) {
                    if (includeOffline) {
                        result.put(code, new PromotionModelImpl(
                                code,
                                null,
                                null,
                                null,
                                null,
                                new StringI18NModel("#OFFLINE#"),
                                new StringI18NModel(),
                                null,
                                null
                        ));
                    }
                } else {

                    final Promotion promotion = promotionService.findPromotionByCode(code, true);
                    if (promotion != null) {

                        final Promotion data = promotion;

                        result.put(code, new PromotionModelImpl(
                                code,
                                null,
                                data.getPromoType(),
                                data.getPromoAction(),
                                data.getPromoActionContext(),
                                new FailoverStringI18NModel(data.getDisplayName(), data.getName()),
                                new FailoverStringI18NModel(data.getDisplayDescription(), data.getDescription()),
                                data.getEnabledFrom(),
                                data.getEnabledTo()
                        ));

                    }
                }
            } else {
                // coupon triggered

                final String realCode = code.substring(0, pos);
                final String coupon = code.substring(pos + 1);

                final Promotion promotion = promotionService.findPromotionByCode(realCode, true);
                if (promotion != null) {

                    final Promotion data = promotion;

                    result.put(code, new PromotionModelImpl(
                            realCode,
                            coupon,
                            data.getPromoType(),
                            data.getPromoAction(),
                            data.getPromoActionContext(),
                            new FailoverStringI18NModel(data.getDisplayName(), data.getName()),
                            new FailoverStringI18NModel(data.getDisplayDescription(), data.getDescription()),
                            data.getEnabledFrom(),
                            data.getEnabledTo()
                    ));

                }

            }

        }

        return result;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Brand getBrandById(final long brandId) {
        return brandService.getById(brandId);
    }
}
