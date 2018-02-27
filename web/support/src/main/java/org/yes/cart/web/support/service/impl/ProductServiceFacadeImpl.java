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

package org.yes.cart.web.support.service.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.yes.cart.constants.Constants;
import org.yes.cart.domain.dto.ProductSearchResultDTO;
import org.yes.cart.domain.dto.ProductSearchResultPageDTO;
import org.yes.cart.domain.dto.ProductSkuSearchResultDTO;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.entity.impl.PriceModelImpl;
import org.yes.cart.domain.entity.impl.PromotionModelImpl;
import org.yes.cart.domain.i18n.impl.FailoverStringI18NModel;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.search.SearchQueryFactory;
import org.yes.cart.search.dto.NavigationContext;
import org.yes.cart.search.query.ProductSearchQueryBuilder;
import org.yes.cart.service.domain.*;
import org.yes.cart.shoppingcart.*;
import org.yes.cart.util.MoneyUtils;
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
    public Map<Pair<String, String>, Map<Pair<String, String>, Map<String, List<Pair<String, String>>>>> getCompareAttributes(final String locale,
                                                                                                                              final List<Long> productId,
                                                                                                                              final List<Long> skuId) {

        return productService.getCompareAttributes(locale, productId, skuId);

    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "productService-productAssociationsIds")
    public List<ProductSearchResultDTO> getProductAssociations(final long productId,
                                                               final long shopId,
                                                               final long customerShopId,
                                                               final String associationType) {

        final List<String> productIds = productAssociationService.getProductAssociationsProductCodes(
                productId,
                associationType
        );

        if (productIds != null && !productIds.isEmpty()) {


            final List<String> search = productIds.size() > 50 ? productIds.subList(0, 50) : productIds;

            final NavigationContext assoc = searchQueryFactory.getFilteredNavigationQueryChain(shopId, customerShopId, null, null,
                    false, Collections.singletonMap(ProductSearchQueryBuilder.PRODUCT_CODE_FIELD,
                            search));

            return Collections.unmodifiableList(productService.getProductSearchResultDTOByQuery(
                    assoc, 0, search.size(), null, false).getResults());
        }

        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
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
                        (List) Collections.singletonList("true")));

        return Collections.unmodifiableList(productService.getProductSearchResultDTOByQuery(
                featured, 0, limit, null, false).getResults());
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "productService-newProducts")
    public List<ProductSearchResultDTO> getNewProducts(final long categoryId,
                                                       final long shopId,
                                                       final long customerShopId) {

        final int limit = categoryServiceFacade.getNewArrivalListSizeConfig(categoryId, customerShopId);

        final List<Long> newArrivalCats;
        if (categoryId > 0L) {
            newArrivalCats = Collections.singletonList(categoryId);
        } else {
            newArrivalCats = null;
        }
        final NavigationContext newarrival = searchQueryFactory.getFilteredNavigationQueryChain(shopId, customerShopId, null, newArrivalCats,
                false, Collections.singletonMap(ProductSearchQueryBuilder.PRODUCT_TAG_FIELD,
                        (List) Collections.singletonList(ProductSearchQueryBuilder.TAG_NEWARRIVAL)));

        return Collections.unmodifiableList(productService.getProductSearchResultDTOByQuery(
                newarrival, 0, limit, null, true).getResults());
    }

    /**
     * {@inheritDoc}
     */
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
                        (List) Collections.singletonList(tag)));

        return Collections.unmodifiableList(productService.getProductSearchResultDTOByQuery(
                tagged, 0, limit, null, false).getResults());
    }

    /**
     * {@inheritDoc}
     */
    public List<ProductSearchResultDTO> getListProducts(final List<String> productIds,
                                                        final long categoryId,
                                                        final long shopId,
                                                        final long customerShopId) {
        if (CollectionUtils.isNotEmpty(productIds)) {

            List<String> productIdsForCategory = productIds;
            int limit = categoryServiceFacade.getNewArrivalListSizeConfig(categoryId, customerShopId);
            if (limit > productIds.size() || categoryId < 0L) {
                limit = productIds.size();
            } else {
                productIdsForCategory = productIds.subList(productIds.size() - limit, productIds.size());
            }

            final NavigationContext recent = searchQueryFactory.getFilteredNavigationQueryChain(shopId, customerShopId, null, null,
                    false, Collections.singletonMap(ProductSearchQueryBuilder.PRODUCT_ID_FIELD,
                            productIdsForCategory));

            return Collections.unmodifiableList(productService.getProductSearchResultDTOByQuery(
                    recent, 0, limit, null, false).getResults());

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
                context, firstResult, maxResults, sortFieldName, descendingSort
        ).copy(); // MUST BE COPY for each search as we are setting relevant SKU list

        if (!result.getResults().isEmpty()) {

            final NavigationContext skuContext = searchQueryFactory.getSkuSnowBallQuery(context, result.getResults());

            final List<ProductSkuSearchResultDTO> skus = productSkuService.getProductSkuSearchResultDTOByQuery(
                    skuContext
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
    public ProductAvailabilityModel getProductAvailability(final ProductSearchResultDTO product, final long customerShopId) {

        return productAvailabilityStrategy.getAvailabilityModel(customerShopId, product);

    }

    /**
     * {@inheritDoc}
     */
    public ProductAvailabilityModel getProductAvailability(final Product product, final long customerShopId) {

        return productAvailabilityStrategy.getAvailabilityModel(customerShopId, product);

    }

    /**
     * {@inheritDoc}
     */
    public ProductAvailabilityModel getProductAvailability(final ProductSku product, final long customerShopId) {

        return productAvailabilityStrategy.getAvailabilityModel(customerShopId, product);

    }

    /**
     * {@inheritDoc}
     */
    public ProductAvailabilityModel getProductAvailability(final String skuCode, final long customerShopId) {

        return productAvailabilityStrategy.getAvailabilityModel(customerShopId, skuCode);

    }

    /**
     * {@inheritDoc}
     */
    public QuantityModel getProductQuantity(final BigDecimal cartQty, final Product product) {

        return productQuantityStrategy.getQuantityModel(cartQty, product);

    }

    /**
     * {@inheritDoc}
     */
    public QuantityModel getProductQuantity(final BigDecimal cartQty, final ProductSku product) {

        return productQuantityStrategy.getQuantityModel(cartQty, product);

    }

    /**
     * {@inheritDoc}
     */
    public QuantityModel getProductQuantity(final BigDecimal cartQty, final ProductSearchResultDTO product) {

        return productQuantityStrategy.getQuantityModel(cartQty, product);

    }

    /**
     * {@inheritDoc}
     */
    public QuantityModel getProductQuantity(final BigDecimal cartQty, final BigDecimal min, final BigDecimal max, final BigDecimal step) {

        return productQuantityStrategy.getQuantityModel(cartQty, min, max, step);

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
     *
     * @return resolved SKU price
     */
    protected SkuPrice resolveMinimalPrice(final ShoppingCart cart,
                                           final Long productId,
                                           final String sku,
                                           final BigDecimal qty) {

        final long customerShopId = cart.getShoppingContext().getCustomerShopId();
        final long masterShopId = cart.getShoppingContext().getShopId();
        // Fallback only if we have a B2B non-strict mode
        final Long fallbackShopId = masterShopId == customerShopId || shopService.getById(customerShopId).isB2BStrictPriceActive() ? null : masterShopId;
        final String shopCode = cart.getShoppingContext().getShopCode();
        final String currency = cart.getCurrencyCode();

        // Policy is setup on master
        final PricingPolicyProvider.PricingPolicy policy = pricingPolicyProvider.determinePricingPolicy(
                shopCode, currency, cart.getCustomerEmail(),
                cart.getShoppingContext().getCountryCode(),
                cart.getShoppingContext().getStateCode()
        );

        return priceResolver.getMinimalPrice(productId, sku, customerShopId, fallbackShopId, currency, qty, false, policy.getID());

    }


    /**
     * We resolve prices from current customer shop first. In simple setup this would be the same as the master.
     * In case current and master differs we are in B2B mode, so we check if we are not in strict profile and
     * attempt to resolve price from master.
     *
     * @param cart      cart
     * @param productId productId (in case no specific sku is selected)
     * @param sku       sku to resolve price for
     *
     * @return resolved SKU price
     */
    protected Collection<SkuPrice> resolvePrices(final ShoppingCart cart,
                                                 final Long productId,
                                                 final String sku) {

        final long customerShopId = cart.getShoppingContext().getCustomerShopId();
        final long masterShopId = cart.getShoppingContext().getShopId();
        // Fallback only if we have a B2B non-strict mode
        final Long fallbackShopId = masterShopId == customerShopId || shopService.getById(customerShopId).isB2BStrictPriceActive() ? null : masterShopId;
        final String shopCode = cart.getShoppingContext().getShopCode();
        final String currency = cart.getCurrencyCode();

        // Policy is setup on master
        final PricingPolicyProvider.PricingPolicy policy = pricingPolicyProvider.determinePricingPolicy(
                shopCode, currency, cart.getCustomerEmail(),
                cart.getShoppingContext().getCountryCode(),
                cart.getShoppingContext().getStateCode()
        );

        return priceResolver.getAllCurrentPrices(productId, sku, customerShopId, fallbackShopId, currency, policy.getID());

    }

    /**
     * Model that does not show any details, NULL object.
     *
     * @param currency currency
     *
     * @return null price model
     */
    protected PriceModelImpl getNullProductPriceModel(final String currency) {
        return new PriceModelImpl(null, currency, null, false, null, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pair<PriceModel, CustomerWishList.PriceChange> getSkuPrice(final ShoppingCart cart,
                                                                      final CustomerWishList item) {

        if (cart.getShoppingContext().isHidePrices()) {
            return new Pair<PriceModel, CustomerWishList.PriceChange>(
                    getNullProductPriceModel(cart.getCurrencyCode()),
                    new CustomerWishList.PriceChange(CustomerWishList.PriceChangeType.NOCHANGE, null)
            );
        }

        final String sku = item.getSkus().getCode();
        final BigDecimal qty = item.getQuantity();

        final SkuPrice priceNow = resolveMinimalPrice(cart, null, sku, qty);

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
                        price = new Pair<BigDecimal, BigDecimal>(listAndSale.getFirst(), addedPrice);
                        priceInfo = new CustomerWishList.PriceChange(
                                CustomerWishList.PriceChangeType.ONSALE,
                                MoneyUtils.getDiscountDisplayValue(listAndSale.getFirst(), addedPrice)
                        );
                    } else {
                        // not on sale
                        price = new Pair<BigDecimal, BigDecimal>(addedPrice, null);
                        priceInfo = new CustomerWishList.PriceChange(CustomerWishList.PriceChangeType.NOCHANGE, null);
                    }
                } else if (MoneyUtils.isFirstBiggerThanSecond(addedPrice, saleNow)) {
                    // price dropped since added
                    price = new Pair<BigDecimal, BigDecimal>(addedPrice, saleNow);
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
            price = new Pair<BigDecimal, BigDecimal>(null, null);
            priceInfo = new CustomerWishList.PriceChange(CustomerWishList.PriceChangeType.OFFLINE, null);
        }

        final PriceModel model = getSkuPrice(cart, sku, BigDecimal.ONE, priceNow.isPriceUponRequest(), price.getFirst(), price.getSecond());

        return new Pair<PriceModel, CustomerWishList.PriceChange>(model, priceInfo);

    }

    /**
     * {@inheritDoc}
     */
    public PriceModel getSkuPrice(final ShoppingCart cart,
                                  final Long productId,
                                  final String skuCode,
                                  final BigDecimal quantity) {

        final String currency = cart.getCurrencyCode();

        if (!cart.getShoppingContext().isHidePrices()) {
            final SkuPrice resolved = resolveMinimalPrice(cart, productId, skuCode, quantity);

            if (resolved != null) {

                final Pair<BigDecimal, BigDecimal> listAndSale = resolved.getSalePriceForCalculation();

                return getSkuPrice(
                        cart,
                        resolved.getSkuCode(),
                        resolved.getQuantity(),
                        resolved.isPriceUponRequest(),
                        listAndSale.getFirst(),
                        listAndSale.getSecond()
                );

            }
        }
        return getNullProductPriceModel(currency);
    }

    /**
     * {@inheritDoc}
     */
    public PriceModel getSkuPrice(final ShoppingCart cart,
                                  final String ref,
                                  final BigDecimal quantity,
                                  final BigDecimal listPrice,
                                  final BigDecimal salePrice) {
        return getSkuPrice(cart, ref, quantity, false, listPrice, salePrice);
    }

    protected PriceModel getSkuPrice(final ShoppingCart cart,
                                     final String ref,
                                     final BigDecimal quantity,
                                     final boolean priceUponRequest,
                                     final BigDecimal listPrice,
                                     final BigDecimal salePrice) {

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
                ShoppingCartCalculator.PriceModel saleModel = shoppingCartCalculator.calculatePrice(cart, ref, sale);

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
                        listAdjusted, saleAdjusted,
                        showTax, showTaxNet, showTaxAmount,
                        saleModel.getTaxCode(),
                        saleModel.getTaxRate(),
                        saleModel.isTaxExclusive(),
                        saleModel.getTaxAmount()
                );

            } else if (list != null) {
                // use list price to calculate taxes
                ShoppingCartCalculator.PriceModel listModel = shoppingCartCalculator.calculatePrice(cart, ref, list);

                final BigDecimal listAdjusted = showTaxNet ? listModel.getNetPrice() : listModel.getGrossPrice();

                return new PriceModelImpl(
                        ref,
                        currency,
                        quantity,
                        priceUponRequest,
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
                list, sale
        );

    }

    /**
     * {@inheritDoc}
     */
    public PriceModel getSkuPrice(final ShoppingCart cart, final CartItem item, boolean total) {

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
                list, sale
        );


    }

    /**
     * {@inheritDoc}
     */
    public List<PriceModel> getSkuPrices(final ShoppingCart cart,
                                         final Long productId,
                                         final String skuCode) {

        if (!cart.getShoppingContext().isHidePrices()) {
            final Collection<SkuPrice> prices = resolvePrices(cart, productId, skuCode);

            if (CollectionUtils.isNotEmpty(prices)) {

                final List<PriceModel> models = new ArrayList<PriceModel>(prices.size());
                for (final SkuPrice price : prices) {

                    final Pair<BigDecimal, BigDecimal> listAndSale = price.getSalePriceForCalculation();

                    models.add(getSkuPrice(
                            cart,
                            price.getSkuCode(),
                            price.getQuantity(),
                            price.isPriceUponRequest(),
                            listAndSale.getFirst(),
                            listAndSale.getSecond()
                    ));

                }
                return models;

            }
        }
        return Collections.emptyList();
    }

    static final String CART_ITEMS_TOTAL_REF = "yc-cart-items-total";

    /**
     * {@inheritDoc}
     */
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

                final Set<String> taxes = new TreeSet<String>(); // sorts and de-dup's
                final Set<BigDecimal> rates = new TreeSet<BigDecimal>();
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
                    list, sale
            );

        }
        // no discounts
        return new PriceModelImpl(
                CART_ITEMS_TOTAL_REF,
                currency,
                BigDecimal.ONE,
                false,
                sale, null
        );

    }


    /**
     * {@inheritDoc}
     */
    public Map<String, PromotionModel> getPromotionModel(final String appliedPromo) {

        if (StringUtils.isBlank(appliedPromo)) {
            return Collections.emptyMap();
        }

        final String[] promoCodes = StringUtils.split(appliedPromo, ',');
        final Map<String, PromotionModel> result = new LinkedHashMap<String, PromotionModel>(promoCodes.length * 2);

        for (final String code : promoCodes) {

            final int pos = code.indexOf(':');
            if (pos == -1) {
                // simple promo
                final List<Promotion> promotions = promotionService.findByParameters(code, null, null, null, null, null, Boolean.TRUE);
                if (promotions.isEmpty()) {
                    continue;
                }

                final Promotion data = promotions.get(0);

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
            } else {
                // coupon triggered

                final String realCode = code.substring(0, pos);
                final String coupon = code.substring(pos + 1);

                final List<Promotion> promotions = promotionService.findByParameters(realCode, null, null, null, null, null, Boolean.TRUE);
                if (promotions.isEmpty()) {
                    continue;
                }

                final Promotion data = promotions.get(0);

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

        return result;
    }


    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "productService-brandById")
    public Brand getBrandById(final long brandId) {
        return brandService.findById(brandId);
    }
}
