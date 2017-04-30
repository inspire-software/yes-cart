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

package org.yes.cart.search.dao.entity;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.document.Document;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.constants.Constants;
import org.yes.cart.domain.dto.ProductSearchResultDTO;
import org.yes.cart.domain.dto.impl.ProductSearchResultDTOImpl;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.entity.impl.StoredAttributesImpl;
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.i18n.impl.StringI18NModel;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.search.dao.LuceneDocumentAdapter;
import org.yes.cart.search.dao.support.*;
import org.yes.cart.search.query.ProductSearchQueryBuilder;
import org.yes.cart.search.query.impl.SearchUtil;
import org.yes.cart.util.DomainApiUtils;
import org.yes.cart.util.MoneyUtils;

import java.lang.System;
import java.math.BigDecimal;
import java.util.*;

import static org.yes.cart.search.dao.entity.LuceneDocumentAdapterUtils.*;

/**
 * User: denispavlov
 * Date: 07/04/2017
 * Time: 14:21
 */
public class ProductLuceneDocumentAdapter implements LuceneDocumentAdapter<Product, Long> {

    private static final String NO_FC_CODE = "";

    private ShopWarehouseRelationshipSupport shopWarehouseSupport;
    private SkuWarehouseRelationshipSupport skuWarehouseSupport;
    private ShopCategoryRelationshipSupport shopCategorySupport;
    private SkuPriceRelationshipSupport skuPriceSupport;
    private NavigatableAttributesSupport attributesSupport;

    /**
     * Product search result is bound to supplier (fulfilment centre).
     * Therefore for every supplier there will be a separate product search result
     * linking back to same product ID.
     *
     * This means that {@link ProductSearchResultDTO#getQtyOnWarehouse(long)} will always
     * be specific to single supplier
     */
    @Override
    public Pair<Long, Document[]> toDocument(final Product entity) {
        if (isIncludeInLuceneIndex(entity, false)) {

            final long now = now();
            final Map<String, ProductSearchResultDTO> resultsByFc = new HashMap<String, ProductSearchResultDTO>();
            final Map<ProductSearchResultDTO, Set<Long>> availableIn = new HashMap<ProductSearchResultDTO, Set<Long>>();

            populateResultsByFc(entity, now, resultsByFc, availableIn);

            final Document[] documents = new Document[resultsByFc.size()];
            int count = 0;

            for (final Map.Entry<String, ProductSearchResultDTO> resultByFc : resultsByFc.entrySet()) {

                final ProductSearchResultDTO result = resultByFc.getValue();
                final Set<Long> available = availableIn.get(result);

                if (CollectionUtils.isEmpty(available)) {
                    continue; // If the product is not available in any shop it should not be in the index
                }

                final Document document = new Document();

                // TODO: save the whole search object instead of individual fields, fields are only for searching
                addObjectDefaultField(document, result);

                addPkField(document, Product.class, String.valueOf(entity.getProductId()));

                addSimpleField(document, "productId", String.valueOf(entity.getProductId()));

                addSimpleField(document, "code", entity.getCode());
                addSortField(document, "code_sort", entity.getCode());
                addStemField(document, "code_stem", entity.getCode());

                addSimpleField(document, "multisku", String.valueOf(entity.isMultiSkuProduct()));

                for (final ProductSku sku : entity.getSku()) {
                    addSimpleField(document, "sku.code", sku.getCode());
                    addSimpleField(document, "sku.skuId", String.valueOf(sku.getSkuId()));
                    addStemFields(document, "sku.code_stem", sku.getCode(), sku.getManufacturerCode(), sku.getManufacturerPartCode(), sku.getSupplierCode());
                }

                addSimpleField(document, "manufacturerCode", entity.getManufacturerCode());
                addSortField(document, "manufacturerCode_sort", entity.getManufacturerCode());
                addStemFields(document, "manufacturerCode_stem", entity.getManufacturerCode(), entity.getManufacturerPartCode(), entity.getSupplierCode());

                //            addSortableDateField(document, "availablefrom", "availablefrom_sort", "availablefrom_range", entity.getAvailablefrom(), true);
                //            addSortableDateField(document, "availableto", "availableto_sort", "availableto_range", entity.getAvailableto(), false);

                addSimpleField(document, "name", entity.getName());
                addSortField(document, "name_sort", entity.getName());
                addStemField(document, "name_stem", entity.getName());

                final String displayNameAsIs = entity.getDisplayName();
                final I18NModel displayName = new StringI18NModel(displayNameAsIs);
                addStemFields(document, "displayName_stem", displayName);
                addSimpleFields(document, "displayName", displayName);
                addSortFields(document, "displayName_sort", displayName);

                // Description is a bad field to index as it contain a lot of information, most of which is irrelevant
//                addSimpleField(document, "description", entity.getDescription());
//                final String descAsIs = entity.getDescriptionAsIs();
//                addSimpleField(document, "descriptionAsIs", descAsIs);
//                final I18NModel desc = new StringI18NModel(descAsIs);
//                addStemFields(document, "description_stem", desc);

                addSimpleField(document, "tag", entity.getTag());

                addSimpleField(document, "brand", entity.getBrand().getName());
                addStemField(document, "brand_stem", entity.getBrand().getName());

                addSimpleField(document, "featured", entity.getFeatured() != null && entity.getFeatured() ? "true" : "false");
                // Created timestamp is used to determine ranges
                addDateField(document, "createdTimestamp", entity.getCreatedTimestamp(), false);

                // Inventory flag
                addInventoryFields(document, result, available, now);

                // Add prices
                addPriceFields(document, entity, now);

                // Add categories
                addCategoryFields(document, entity, available, now);

                // Add attributes
                addAttributeFields(document, entity, result);

                documents[count++] = document;

            }

            return new Pair<Long, Document[]>(entity.getProductId(), documents);

        }
        return entity != null ? new Pair<Long, Document[]>(entity.getProductId(), null) : null;
    }

    /**
     * Add attribute fields.
     *
     * @param document index document
     * @param entity   entity
     * @param result   result to store attributes in
     */
    protected void addAttributeFields(final Document document, final Product entity, final ProductSearchResultDTO result) {

        final Set<String> navAttrs = attributesSupport.getAllNavigatableAttributeCodes();
        final Set<String> searchAttrs = attributesSupport.getAllSearchableAttributeCodes();
        final Set<String> searchPrimaryAttrs = attributesSupport.getAllSearchablePrimaryAttributeCodes();
        final Set<String> storeAttrs = attributesSupport.getAllStorableAttributeCodes();

        StoredAttributes storedAttributes = result.getAttributes();

        final List<AttrValue> attributes = new ArrayList<AttrValue>(entity.getAttributes());
        for (final ProductSku sku : entity.getSku()) {
            attributes.addAll(sku.getAttributes());
        }

        for (final AttrValue attrValue : attributes) {

            if (attrValue.getAttribute() == null) {
                continue; // skip invalid ones
            }

            final String code = attrValue.getAttribute().getCode();

            final boolean navigation = navAttrs.contains(code);
            final boolean search = navigation || searchAttrs.contains(code);
            final boolean searchPrimary = searchPrimaryAttrs.contains(code);

            // Only keep searcheable and navigatable attributes in index
            if (search) {
                if (StringUtils.isNotBlank(attrValue.getVal())) {

                    if (searchPrimary) {

                        final List<String> searchValues = getSearchValue(attrValue);

                        // primary search should only exist in primary search exact match
                        for (final String searchValue : searchValues) {

                            addSimpleField(document, ProductSearchQueryBuilder.ATTRIBUTE_VALUE_SEARCHPRIMARY_FIELD, searchValue);

                        }

                    } else {

                        final List<String> searchValues = getSearchValue(attrValue);

                        for (final String searchValue : searchValues) {

                            // searchable and navigatable terms for global search tokenised
                            addStemField(document, ProductSearchQueryBuilder.ATTRIBUTE_VALUE_SEARCH_FIELD, searchValue);

                            // searchable and navigatable terms for global search full phrase
                            addSimpleField(document, ProductSearchQueryBuilder.ATTRIBUTE_VALUE_SEARCHPHRASE_FIELD, searchValue);

                        }

                    }

                }
            }

            if (navigation) {
                // strict attribute navigation only for filtered navigation
                addFacetField(document, "facet_" + code, cleanFacetValue(attrValue.getVal()));
            }

            final boolean stored = storeAttrs.contains(code);

            // Only keep product level stored fields, so they are not overwritten
            if (stored && attrValue instanceof AttrValueProduct) {
                storedAttributes.putValue(code, attrValue.getVal(), attrValue.getDisplayVal());
            }

        }

    }

    private String cleanFacetValue(final String val) {
        return val.replace('/', ' '); // replace all forward slashes since they get decoded into paths
    }

    private List<String> getSearchValue(final AttrValue attrValue) {
        final List<String> values = new ArrayList<String>();
        if (StringUtils.isNotBlank(attrValue.getDisplayVal())) {
            final I18NModel model = new StringI18NModel(attrValue.getDisplayVal());
            for (final String value : model.getAllValues().values()) {
                values.add(value.toLowerCase());
            }
        }
        values.add(attrValue.getVal().toLowerCase());
        return values;
    }


    /**
     * Prices are per shop so we retrieve them once and then use for all supplier specific results.
     *
     * @param entity       product
     * @return prices for all SKUs of this product
     */
    protected List<SkuPrice> determineAllActivePrices(final Product entity) {

        final List<SkuPrice> all = new ArrayList<SkuPrice>();

        for (final ProductSku sku : entity.getSku()) {

            all.addAll(skuPriceSupport.getSkuPrices(sku.getCode()));

        }

        return all;
    }

    /**
     * Determine lowest price and set it as facet field.
     *
     * @param document    index document
     * @param entity      product
     * @param now         time now to filter out inactive price records
     */
    protected void addPriceFields(final Document document, final Product entity, final long now) {

        final boolean isShowRoom = entity.getAvailability() == Product.AVAILABILITY_SHOWROOM;

        final List<SkuPrice> allPrices = determineAllActivePrices(entity);

        Set<Long> availableIn = null;
        if (!allPrices.isEmpty()) {

            final Map<Long, Map<String, SkuPrice>> lowestQuantityPrice = new HashMap<Long, Map<String, SkuPrice>>();
            for (Object obj : (Collection) allPrices) {
                SkuPrice skuPrice = (SkuPrice) obj;

                if (!DomainApiUtils.isObjectAvailableNow(true, skuPrice.getSalefrom(), skuPrice.getSaleto(), now)) {
                    continue; // This price is not active
                }

                final Map<String, SkuPrice> lowestQuantityPriceByShop = lowestQuantityPrice.get(skuPrice.getShop().getShopId());
                if (lowestQuantityPriceByShop == null) {
                    // if we do not have a "byShop" this is the new lowest price
                    final Map<String, SkuPrice> newLowestQuantity = new HashMap<String, SkuPrice>();
                    newLowestQuantity.put(skuPrice.getCurrency(), skuPrice);
                    lowestQuantityPrice.put(skuPrice.getShop().getShopId(), newLowestQuantity);
                } else {
                    final SkuPrice oldLowestQuantity = lowestQuantityPriceByShop.get(skuPrice.getCurrency());
                    if (oldLowestQuantity == null) {
                        // if we do not have the lowest for this shop for this currency just add it
                        lowestQuantityPriceByShop.put(skuPrice.getCurrency(), skuPrice);
                    } else {
                        final int compare = oldLowestQuantity.getQuantity().compareTo(skuPrice.getQuantity());
                        if (compare > 0 || (compare == 0 && MoneyUtils.isFirstBiggerThanSecond(
                                MoneyUtils.minPositive(oldLowestQuantity.getRegularPrice(), oldLowestQuantity.getSalePrice()),
                                MoneyUtils.minPositive(skuPrice.getRegularPrice(), skuPrice.getSalePrice())
                        ))) {
                            // if this sku price has lower quantity then this is probably better starting price
                            // if the quantity is the same lower price is more appealing to show
                            lowestQuantityPriceByShop.put(skuPrice.getCurrency(), skuPrice);
                        }
                    }
                }
            }

            if (!lowestQuantityPrice.isEmpty()) {
                availableIn = new HashSet<Long>();
                for (final Map.Entry<Long, Map<String, SkuPrice>> shop : lowestQuantityPrice.entrySet()) {
                    for (final Map.Entry<String, SkuPrice> currency : shop.getValue().entrySet()) {

                        final BigDecimal price = MoneyUtils.minPositive(currency.getValue().getRegularPrice(), currency.getValue().getSalePrice());
                        // Pad 2 decimal places 99.99 => 9999
                        final long longPrice = SearchUtil.priceToLong(price);
                        final String facetName = SearchUtil.priceFacetName(shop.getKey(), currency.getKey());

                        addFacetField(document, facetName, longPrice);
                        addNumericField(document, facetName + "_range", longPrice, false);
                        addSortField(document, facetName + "_sort", longPrice, false);

                        final Set<Shop> subs = skuPriceSupport.getAllShopsAndSubs().get(shop.getKey());

                        if (CollectionUtils.isNotEmpty(subs)) {

                            for (final Shop subShop : subs) {

                                if (!subShop.isB2BStrictPriceActive()) {

                                    final String subFacetName = SearchUtil.priceFacetName(subShop.getShopId(), currency.getKey());

                                    addFacetField(document, subFacetName, longPrice);
                                    addNumericField(document, subFacetName + "_range", longPrice, false);
                                    addSortField(document, subFacetName + "_sort", longPrice, false);

                                    // Note that price is inherited for this sub shop
                                    availableIn.add(subShop.getShopId());

                                }
                            }

                        }

                    }

                    // Note that price is set for this shop
                    availableIn.add(shop.getKey());

                }
            }

            if (isShowRoom) {
                for (final Shop shop : skuPriceSupport.getAll()) {
                    // Fill in PK's for all shops as showroom products are visible regardless of price.
                    addNumericField(document, ProductSearchQueryBuilder.PRODUCT_SHOP_HASPRICE_FIELD, shop.getShopId(), false);
                }
            } else if (CollectionUtils.isNotEmpty(availableIn)) {

                for (final Long shopId : availableIn) {
                    // Fill in PK's for all shops that have price entries.
                    addNumericField(document, ProductSearchQueryBuilder.PRODUCT_SHOP_HASPRICE_FIELD, shopId, false);
                }

            }
        }
    }

    private void addCategoryFields(final Document document, final Product entity, final Set<Long> available, final long now) {

        final Set<Long> availableCategories = new HashSet<Long>();

        for (final ProductCategory productCategory : entity.getProductCategory()) {

            if (isCategoryAvailable(productCategory.getCategory().getCategoryId(), now)) {

                final Category category = shopCategorySupport.getCategoryById(productCategory.getCategory().getCategoryId());

                addNumericField(document, ProductSearchQueryBuilder.PRODUCT_CATEGORY_FIELD, category.getCategoryId(), false);
                addCategoryNameFields(document, category);

                addCategoryParentIdsFields(document, category);

                availableCategories.add(productCategory.getCategory().getCategoryId());

            }

        }

        if (CollectionUtils.isNotEmpty(availableCategories)) {

            // Global search is done on this field. There is a disparity in the sence that after assigning category to
            // shop the product will become reachable in category search but will still be unavailable in global searches
            // since it is done on shop field below. So full reindexing is required to synchronise fully

            final List<Shop> shops = shopCategorySupport.getAll();

            for (final Long availableCategory : availableCategories) {

                for (final Shop shop : shops) {

                    if (shopCategorySupport.getShopCategoriesIds(shop.getShopId()).contains(availableCategory)) {

                        addNumericField(document, ProductSearchQueryBuilder.PRODUCT_SHOP_FIELD, shop.getShopId(), false);

                    }

                }

            }
        }

    }

    /**
     * This check should be done only at the lowest level. Catalog is independent of the shops and hence criteria for
     * category being unavailable is global through settigs of availability dates.
     *
     * Note that we do not consider option to check if this category is reachable by a shop since we want to allow
     * shop category assignment without the need for re-indexing afterwards.
     *
     * @param currentId         category PK to check
     * @param now               time now to check availability
     *
     * @return true if this category is available, false otherwise
     */
    protected boolean isCategoryAvailable(final Long currentId, final long now) {

        if (currentId != null) {

            final Category category = shopCategorySupport.getCategoryById(currentId);
            if (DomainApiUtils.isObjectAvailableNow(true, category.getAvailablefrom(), category.getAvailableto(), now)) {

                final Set<Long> parentIds = shopCategorySupport.getCategoryParentsIds(category.getCategoryId());
                if (parentIds.isEmpty()) {
                    // reached top and all are available
                    return true;
                }

                // Else if has parents check that at least one is available
                for (final Long parentId : parentIds) {
                    if (isCategoryAvailable(parentId, now)) {
                        return true;
                    }
                }

            }

        }

        return false;

    }

    /**
     * Add category name search terms.
     *
     * @param document  index document
     * @param category  category
     */
    protected void addCategoryNameFields(final Document document, final Category category) {

        addNumericField(document, ProductSearchQueryBuilder.PRODUCT_CATEGORY_FIELD, category.getCategoryId(), false);
        addSimpleField(document, ProductSearchQueryBuilder.PRODUCT_CATEGORYNAME_FIELD, category.getName());
        addStemField(document, ProductSearchQueryBuilder.PRODUCT_CATEGORYNAME_STEM_FIELD, category.getName());
        final I18NModel displayName = new StringI18NModel(category.getDisplayName());
        addSimpleFields(document, ProductSearchQueryBuilder.PRODUCT_CATEGORYNAME_FIELD, displayName);
        addStemFields(document, ProductSearchQueryBuilder.PRODUCT_CATEGORYNAME_STEM_FIELD, displayName);

    }

    /**
     * Recursion function to traverse up the category hierarchy (including category links) and fill in category id's in which
     * current product can be found.
     *
     * Note that this function goe right up to the "root" since catalogs are independent of shops and hence any shop can link
     * to category at any level of the hierarchy thus we cannot distinguish between what level is specific to specific shop.
     *
     * This is not a bug but a feature since catalog is viewed as taxonomy tool and not a container tree for storing products.
     *
     * @param document       index document
     * @param category       current category
     */
    protected void addCategoryParentIdsFields(final Document document, final Category category) {

        addNumericField(document, ProductSearchQueryBuilder.PRODUCT_CATEGORY_INC_PARENTS_FIELD, category.getCategoryId(), false);

        final Set<Long> parentIds = shopCategorySupport.getCategoryParentsIds(category.getCategoryId());

        for (final Long parentId : parentIds) {

            final Category parent = shopCategorySupport.getCategoryById(parentId);

            addCategoryNameFields(document, parent);
            addCategoryParentIdsFields(document, parent);

        }

    }


    /**
     * Populate inventory related flags so that we can restrict result hits which should be available in shop by inventory.
     *
     * @param document   index document
     * @param result     current supplier specific result hit
     * @param available  shop PKs where this result is available
     * @param now        time now (used for pre-ordered items)
     */
    protected void addInventoryFields(final Document document, final ProductSearchResultDTO result, final Set<Long> available, final long now) {

        for (final Long shop : available) {
            addSimpleField(document, ProductSearchQueryBuilder.PRODUCT_SHOP_INSTOCK_FIELD, String.valueOf(shop));
            if (result.getAvailability() == Product.AVAILABILITY_ALWAYS) {
                addNumericField(document, ProductSearchQueryBuilder.PRODUCT_SHOP_INSTOCK_FIELD, 1L, false);
            } else if (result.getAvailability() == Product.AVAILABILITY_PREORDER &&
                    DomainApiUtils.isObjectAvailableNow(true, result.getAvailablefrom(), null, now)) {
                addNumericField(document, ProductSearchQueryBuilder.PRODUCT_SHOP_INSTOCK_FIELD, 1L, false);
            } else { // Standard stock
                final Map<String, BigDecimal> qty = result.getQtyOnWarehouse(shop);
                boolean hasStock = false;
                for (final BigDecimal singleSku : qty.values()) {
                    if (MoneyUtils.isFirstBiggerThanSecond(singleSku, BigDecimal.ZERO)) {
                        hasStock = true;
                        break;
                    }
                }
                addNumericField(document, ProductSearchQueryBuilder.PRODUCT_SHOP_INSTOCK_FIELD, hasStock ? 1L : 0L, false);
            }
        }

    }

    /**
     * Generate result hit result for product for each supplier.
     *
     * @param entity      product entity
     * @param now         time now (used for pre-ordered items)
     * @param resultsByFc hit results by supplier code
     * @param availableIn shop PK for which given product is available (determined by supplier link to shop)
     */
    protected void populateResultsByFc(final Product entity, final long now, final Map<String, ProductSearchResultDTO> resultsByFc, final Map<ProductSearchResultDTO, Set<Long>> availableIn) {

        final ProductSearchResultDTO base = createBaseResultObject(entity);

        final List<Shop> shops = shopWarehouseSupport.getAll();
        final Map<Long, Set<Long>> shopsByFulfilment = shopWarehouseSupport.getShopsByFulfilmentMap();

        final Map<ProductSearchResultDTO, Map<Long, Map<String, BigDecimal>>> qtyOnWarehouse = new HashMap<ProductSearchResultDTO, Map<Long, Map<String, BigDecimal>>>();

        // Stock and prices are per SKU so need to determine if we have valid ones
        for (final ProductSku sku : entity.getSku()) {

            final boolean preorderInSale = sku.getProduct().getAvailability() == Product.AVAILABILITY_PREORDER &&
                    DomainApiUtils.isObjectAvailableNow(true, sku.getProduct().getAvailablefrom(), null, now);

            if (sku.getProduct().getAvailability() != Product.AVAILABILITY_ALWAYS) {
                final List<SkuWarehouse> inventory = skuWarehouseSupport.getQuantityOnWarehouse(sku.getCode());

                if (CollectionUtils.isNotEmpty(inventory)) {
                    for (final SkuWarehouse stock : inventory) {

                        final BigDecimal ats = stock.getAvailableToSell();
                        // if standard with zero stock then not available
                        if ((sku.getProduct().getAvailability() != Product.AVAILABILITY_STANDARD && !preorderInSale)
                                || MoneyUtils.isFirstBiggerThanSecond(ats, BigDecimal.ZERO)) {
                            final long ff = stock.getWarehouse().getWarehouseId();
                            final Set<Long> shs = shopsByFulfilment.get(ff);
                            if (shs != null) {

                                final String code = skuWarehouseSupport.getWarehouseCode(stock);
                                if (code != null) {

                                    ProductSearchResultDTO withFc = resultsByFc.get(code);
                                    if (withFc == null) {
                                        withFc = base.copy();
                                        withFc.setFulfilmentCentreCode(code);
                                        final Map<Long, Map<String, BigDecimal>> qty = new HashMap<Long, Map<String, BigDecimal>>();
                                        withFc.setQtyOnWarehouse(qty);
                                        qtyOnWarehouse.put(withFc, qty);
                                        resultsByFc.put(code, withFc);
                                        availableIn.put(withFc, new HashSet<Long>());
                                    }
                                    final Set<Long> withFcAvailableIn = availableIn.get(withFc);

                                    final BigDecimal atsAdd = MoneyUtils.isFirstBiggerThanSecond(ats, BigDecimal.ZERO) ? ats : BigDecimal.ZERO;

                                    for (final Long sh : shs) {

                                        withFcAvailableIn.add(sh);
                                        Map<Long, Map<String, BigDecimal>> qtyByShop = qtyOnWarehouse.get(withFc);
                                        Map<String, BigDecimal> qtyBySku = qtyByShop.get(sh);
                                        if (qtyBySku == null) {
                                            qtyBySku = new HashMap<String, BigDecimal>();
                                            qtyByShop.put(sh, qtyBySku);
                                        }

                                        qtyBySku.put(sku.getCode(), atsAdd);

                                    }

                                }

                            }
                        }

                    }

                }
            } else {

                ProductSearchResultDTO noFc = resultsByFc.get(NO_FC_CODE);
                if (noFc == null) {
                    noFc = base.copy();
                    noFc.setFulfilmentCentreCode(NO_FC_CODE);
                    final Map<Long, Map<String, BigDecimal>> qty = new HashMap<Long, Map<String, BigDecimal>>();
                    noFc.setQtyOnWarehouse(qty);
                    qtyOnWarehouse.put(noFc, qty);
                    resultsByFc.put(NO_FC_CODE, noFc);
                    availableIn.put(noFc, new HashSet<Long>());
                }
                final Set<Long> noFcAvailableIn = availableIn.get(noFc);

                for (final Shop shop : shops) {

                    // Available as perpetual (preorder/backorder must have stock)
                    noFcAvailableIn.add(shop.getShopId());
                    Map<Long, Map<String, BigDecimal>> qtyByShop = qtyOnWarehouse.get(noFc);
                    Map<String, BigDecimal> qtyBySku = qtyByShop.get(shop.getShopId());
                    if (qtyBySku == null) {
                        qtyBySku = new HashMap<String, BigDecimal>();
                        qtyByShop.put(shop.getShopId(), qtyBySku);
                    }

                    qtyBySku.put(sku.getCode(), BigDecimal.ONE);

                }

            }

        }

    }

    long now() {
        return System.currentTimeMillis();
    }

    /**
     * Each product can be supplied by several fulfilment centres (warehouses) and therefore should be
     * treated as unique per warehouse result. (Potentially we can integrate supplier specific pricelists
     * later on).
     *
     * @param entity entity
     *
     * @return basic search result object containing common information
     */
    protected ProductSearchResultDTO createBaseResultObject(final Product entity) {

        final ProductSearchResultDTO baseResult = new ProductSearchResultDTOImpl();
        baseResult.setId(entity.getProductId());
        baseResult.setCode(entity.getCode());
        baseResult.setManufacturerCode(entity.getManufacturerCode());
        baseResult.setMultisku(entity.isMultiSkuProduct());
        final ProductSku sku = entity.getDefaultSku();
        baseResult.setDefaultSkuCode(sku != null ? sku.getCode() : null);
        baseResult.setName(entity.getName());
        baseResult.setDisplayName(entity.getDisplayName());
        baseResult.setDescription(entity.getDescription());
        baseResult.setDisplayDescription(entity.getDescriptionAsIs());
        baseResult.setTag(entity.getTag());
        baseResult.setBrand(entity.getBrand().getName());
        baseResult.setAvailablefrom(entity.getAvailablefrom());
        baseResult.setAvailableto(entity.getAvailableto());
        baseResult.setAvailability(entity.getAvailability());
        final String image0 = entity.getAttributeValueByCode(AttributeNamesKeys.Product.PRODUCT_IMAGE_ATTR_NAME_PREFIX + "0");
        if (StringUtils.isBlank(image0)) {
            baseResult.setDefaultImage(Constants.NO_IMAGE);
        } else {
            baseResult.setDefaultImage(image0);
        }
        baseResult.setFeatured(entity.getFeatured());
        baseResult.setMinOrderQuantity(entity.getMinOrderQuantity());
        baseResult.setMaxOrderQuantity(entity.getMaxOrderQuantity());
        baseResult.setStepOrderQuantity(entity.getStepOrderQuantity());
        baseResult.setCreatedTimestamp(entity.getCreatedTimestamp());
        baseResult.setUpdatedTimestamp(entity.getUpdatedTimestamp());
        baseResult.setAttributes(new StoredAttributesImpl()); // TODO: add attributes
        baseResult.setQtyOnWarehouse(null); // TODO: this will be now per warehouse! (i.e. result per supplier)

        return baseResult;
    }


    /**
     * Check is product need to be in index.
     * Product will be added to index:
     * in case if product available for pre/back order;
     * or always available (for example digital products);
     * or has quantity of sku more than 0  on any stock (in any shop).
     *
     *
     * @param entity entity to check
     * @param checkInventory check inventory (performs select that causes flush)
     *
     * @return true if entity need to be in lucene index.
     */
    public boolean isIncludeInLuceneIndex(final Product entity, final boolean checkInventory) {
        if (entity != null) {
            if (entity.getProductCategory().isEmpty()) {
                return false; // if it is not assigned to category, no way to determine the shop
            }
            if (entity.getSku().isEmpty()) {
                return false; // if there are no SKU then it makes no sense to have it in index
            }

            final int availability = entity.getAvailability();
            switch (availability) {
                case Product.AVAILABILITY_ALWAYS:
                case Product.AVAILABILITY_BACKORDER:
                case Product.AVAILABILITY_SHOWROOM:
                    // showroom, always and backorder must be in product date range
                    return DomainApiUtils.isObjectAvailableNow(true, entity.getAvailablefrom(), entity.getAvailableto(), new Date());
                case Product.AVAILABILITY_PREORDER:
                    // For preorders check only available to date since that is the whole point of preorders
                    return DomainApiUtils.isObjectAvailableNow(true, null, entity.getAvailableto(), new Date());
                case Product.AVAILABILITY_STANDARD:
                default:
                    /*
                     * For standard check only available date.
                     *
                     * Checking inventory in a multistore environment does not work as even if one shop has this product it will
                     * show up in all stored. Instead an inventoty flag field needs to be used. This also gives option for
                     * implementing "only in stock / all products" navigation feature.
                     *
                     */
                    return DomainApiUtils.isObjectAvailableNow(true, entity.getAvailablefrom(), entity.getAvailableto(), new Date());


            }

        }
        return false;
    }

    /**
     * Spring IoC.
     *
     * @param shopWarehouseSupport support
     */
    public void setShopWarehouseSupport(final ShopWarehouseRelationshipSupport shopWarehouseSupport) {
        this.shopWarehouseSupport = shopWarehouseSupport;
    }

    /**
     * Spring IoC.
     *
     * @param skuWarehouseSupport support
     */
    public void setSkuWarehouseSupport(final SkuWarehouseRelationshipSupport skuWarehouseSupport) {
        this.skuWarehouseSupport = skuWarehouseSupport;
    }

    /**
     * Spring IoC.
     *
     * @param shopCategorySupport support
     */
    public void setShopCategorySupport(final ShopCategoryRelationshipSupport shopCategorySupport) {
        this.shopCategorySupport = shopCategorySupport;
    }

    /**
     * Spring IoC.
     *
     * @param skuPriceSupport support
     */
    public void setSkuPriceSupport(final SkuPriceRelationshipSupport skuPriceSupport) {
        this.skuPriceSupport = skuPriceSupport;
    }

    /**
     * Spring IoC.
     *
     * @param attributesSupport support
     */
    public void setAttributesSupport(final NavigatableAttributesSupport attributesSupport) {
        this.attributesSupport = attributesSupport;
    }
}
