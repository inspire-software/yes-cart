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

package org.yes.cart.search.dao.entity;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.document.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.constants.Constants;
import org.yes.cart.domain.dto.StoredAttributesDTO;
import org.yes.cart.domain.dto.impl.ProductSkuSearchResultDTOImpl;
import org.yes.cart.domain.dto.impl.StoredAttributesDTOImpl;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.i18n.impl.StringI18NModel;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.search.dao.LuceneDocumentAdapter;
import org.yes.cart.search.dao.support.NavigatableAttributesSupport;
import org.yes.cart.search.dao.support.ShopWarehouseRelationshipSupport;
import org.yes.cart.search.dao.support.SkuPriceRelationshipSupport;
import org.yes.cart.search.dao.support.SkuWarehouseRelationshipSupport;
import org.yes.cart.search.utils.SearchUtil;
import org.yes.cart.utils.MoneyUtils;
import org.yes.cart.utils.TimeContext;
import org.yes.cart.utils.log.Markers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.yes.cart.search.dao.entity.LuceneDocumentAdapterUtils.*;
import static org.yes.cart.search.query.ProductSearchQueryBuilder.*;

/**
 * User: denispavlov
 * Date: 07/04/2017
 * Time: 14:22
 */
public class ProductSkuLuceneDocumentAdapter implements LuceneDocumentAdapter<ProductSku, Long> {

    private static final Logger LOGFTQ = LoggerFactory.getLogger("FTQ");

    private ShopWarehouseRelationshipSupport shopWarehouseSupport;
    private SkuWarehouseRelationshipSupport skuWarehouseSupport;
    private SkuPriceRelationshipSupport skuPriceSupport;
    private NavigatableAttributesSupport attributesSupport;

    /**
     * {@inheritDoc}
     */
    @Override
    public Pair<Long, Document[]> toDocument(final ProductSku entity) {

        final LocalDateTime now = now();

        if (isProductInCategory(entity)) {

            try {

                final Map<String, ProductSkuSearchResultDTOImpl> resultsByFc = new HashMap<>();
                final Map<ProductSkuSearchResultDTOImpl, Set<Long>> availableIn = new HashMap<>();

                populateResultsByFc(entity, now, resultsByFc, availableIn);

                final List<SkuPrice> allPrices = determineAllActivePrices(entity);

                final Document[] documents = new Document[resultsByFc.size()];
                int count = 0;

                for (final Map.Entry<String, ProductSkuSearchResultDTOImpl> resultByFc : resultsByFc.entrySet()) {

                    final ProductSkuSearchResultDTOImpl result = resultByFc.getValue();
                    final Set<Long> available = availableIn.get(result);

                    if (CollectionUtils.isEmpty(available)) {
                        continue; // If the product is not available in any shop it should not be in the index
                    }

                    final Document document = new Document();

                    addPkField(document, ProductSku.class, String.valueOf(entity.getSkuId()));

                    addSimpleField(document, "skuId", String.valueOf(entity.getSkuId()));
                    addSimpleField(document, SKU_ID_FIELD, String.valueOf(entity.getSkuId()));
                    addSimpleField(document, PRODUCT_ID_FIELD, String.valueOf(entity.getProduct().getProductId()));

                    addSimpleField(document, SKU_PRODUCT_CODE_FIELD, entity.getCode());
                    addSearchField(document, SKU_PRODUCT_CODE_FIELD_SEARCH, entity.getCode());
                    addSimpleField(document, SKU_PRODUCT_CODE_FIELD, entity.getBarCode());
                    addSearchField(document, SKU_PRODUCT_CODE_FIELD_SEARCH, entity.getBarCode());
                    addSortField(document, SKU_PRODUCT_CODE_SORT_FIELD, entity.getCode());
                    addStemFields(document, SKU_PRODUCT_CODE_STEM_FIELD, entity.getCode(), entity.getProduct().getCode());

                    addSimpleField(document, SKU_PRODUCT_MANUFACTURER_CODE_FIELD, entity.getManufacturerCode());
                    addSearchField(document, SKU_PRODUCT_MANUFACTURER_CODE_FIELD_SEARCH, entity.getManufacturerCode());
                    addSortField(document, SKU_PRODUCT_MANUFACTURER_CODE_SORT_FIELD, entity.getManufacturerCode());
                    addStemFields(document, SKU_PRODUCT_MANUFACTURER_CODE_STEM_FIELD, entity.getManufacturerCode(), entity.getManufacturerPartCode(), entity.getSupplierCode());

                    addSimpleField(document, PRODUCT_NOT_SOLD_SEPARATELY, String.valueOf(entity.getProduct().getNotSoldSeparately()));

                    addSearchField(document, PRODUCT_NAME_FIELD, entity.getName());
                    addSortField(document, PRODUCT_NAME_SORT_FIELD, entity.getName());
                    addStemFields(document, PRODUCT_NAME_STEM_FIELD, entity.getName(), entity.getSeo().getTitle(), entity.getSeo().getMetakeywords());

                    final I18NModel displayName = new StringI18NModel(entity.getDisplayName());
                    addStemFields(document, PRODUCT_DISPLAYNAME_STEM_FIELD, displayName);
                    addStemFields(document, PRODUCT_DISPLAYNAME_STEM_FIELD, new StringI18NModel(entity.getSeo().getDisplayTitle()));
                    addStemFields(document, PRODUCT_DISPLAYNAME_STEM_FIELD, new StringI18NModel(entity.getSeo().getDisplayMetakeywords()));
                    addSearchFields(document, PRODUCT_DISPLAYNAME_FIELD, displayName);
                    addSortFields(document, PRODUCT_DISPLAYNAME_SORT_FIELD, displayName);

                    addNumericField(document, "rank", (long) entity.getRank(), false);

                    final double boost = LuceneDocumentAdapterUtils.determineBoots(entity.getRank(), 0d, 50d, 0.25d, 2d);

                    if (result.getTag() != null) {
                        for (final String tag : StringUtils.split(result.getTag(), ' ')) {
                            addSimpleField(document, PRODUCT_TAG_FIELD, tag);
                            addFacetField(document, "facet_tag", tag);
                        }
                    }

                    final Boolean atLeastOneFeatured = result.isFeatured();
                    addSimpleField(document, PRODUCT_FEATURED_FIELD, atLeastOneFeatured != null && atLeastOneFeatured ? "true" : "false");
                    if (LOGFTQ.isTraceEnabled()) {
                        addStoredField(document, "featured_boost_debug", atLeastOneFeatured != null && atLeastOneFeatured ? 1.25d : 1.0d);
                    }
                    addBoostField(document, "featured_boost", atLeastOneFeatured != null && atLeastOneFeatured ? 1.25d : 1.0d);

                    if (LOGFTQ.isTraceEnabled()) {
                        addStoredField(document, "rank_boost_debug", boost);
                    }
                    addBoostField(document, "rank_boost", boost);

                    // Created timestamp is used to determine ranges
                    addInstantField(document, PRODUCT_CREATED_FIELD, result.getCreatedTimestamp(), false);
                    addSortField(document, PRODUCT_CREATED_SORT_FIELD, result.getCreatedTimestamp(), false);

                    // Inventory flag
                    addInventoryFields(document, result, available, now);

                    // Add prices
                    addPriceFields(document, entity, allPrices, result, available, now);

                    // Add attributes
                    addAttributeFields(document, entity, result);

                    // save the whole search object instead of individual fields, fields are only for searching
                    // must be last step so that we have fully modified object serialised to index
                    addObjectDefaultField(document, result);

                    documents[count++] = document;

                }

                return new Pair<>(entity.getSkuId(), documents);
            } catch (Exception exp) {
                LOGFTQ.error(Markers.alert(),
                        "Unable to adapt product {} to index document", entity.getCode());
                LOGFTQ.error(
                        "Unable to adapt product to index document, code: " + entity.getCode()
                                + ", cause: " + exp.getMessage(), exp);
            }

        }
        return entity != null ? new Pair<>(entity.getSkuId(), null) : null;
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
    protected ProductSkuSearchResultDTOImpl createBaseResultObject(final ProductSku entity) {

        final ProductSkuSearchResultDTOImpl baseResult = new ProductSkuSearchResultDTOImpl();
        baseResult.setId(entity.getSkuId());
        baseResult.setProductId(entity.getProduct().getProductId());
        baseResult.setCode(entity.getCode());
        baseResult.setManufacturerCode(entity.getManufacturerCode());
        baseResult.setName(entity.getName());
        baseResult.setDisplayName(new StringI18NModel(entity.getDisplayName()));
        baseResult.setDescription(entity.getDescription());
        baseResult.setDisplayDescription(new StringI18NModel(entity.getDisplayDescription()));
        final String image0 = entity.getAttributeValueByCode(AttributeNamesKeys.Product.PRODUCT_IMAGE_ATTR_NAME_PREFIX + "0");
        if (StringUtils.isBlank(image0)) {
            baseResult.setDefaultImage(Constants.NO_IMAGE);
        } else {
            baseResult.setDefaultImage(image0);
        }
        baseResult.setCreatedTimestamp(entity.getCreatedTimestamp());
        baseResult.setUpdatedTimestamp(entity.getUpdatedTimestamp());
        baseResult.setAttributes(new StoredAttributesDTOImpl());

        return baseResult;
    }


    /**
     * Add attribute fields.
     *
     * @param document index document
     * @param entity   entity
     * @param result   result to store attributes in
     */
    protected void addAttributeFields(final Document document, final ProductSku entity, final ProductSkuSearchResultDTOImpl result) {

        final Set<String> navAttrs = attributesSupport.getAllNavigatableAttributeCodes();
        final Set<String> searchAttrs = attributesSupport.getAllSearchableAttributeCodes();
        final Set<String> searchPrimaryAttrs = attributesSupport.getAllSearchablePrimaryAttributeCodes();
        final Set<String> storeAttrs = attributesSupport.getAllStorableAttributeCodes();

        StoredAttributesDTO storedAttributes = result.getAttributes();

        final List<AttrValue> attributes = new ArrayList<>(entity.getAttributes());

        final Map<String, Long> numRangeFields = new HashMap<>();

        for (final AttrValue attrValue : attributes) {

            if (attrValue.getAttributeCode() == null) {
                continue; // skip invalid ones
            }

            final String code = attrValue.getAttributeCode();

            final boolean navigation = navAttrs.contains(code);
            final boolean search = navigation || searchAttrs.contains(code);
            final boolean searchPrimary = searchPrimaryAttrs.contains(code);

            // Only keep searcheable and navigatable attributes in index
            if (search) {
                if (StringUtils.isNotBlank(attrValue.getVal())) {

                    final String searchValue = cleanFacetValue(attrValue.getVal());

                    if (searchPrimary) {

                        addSearchField(document, ATTRIBUTE_VALUE_SEARCHPRIMARY_FIELD, searchValue);

                    } else {

                        // searchable and navigatable terms for global search tokenised
                        addStemField(document, ATTRIBUTE_VALUE_SEARCH_FIELD, searchValue);

                        // searchable and navigatable terms for global search full phrase
                        addSearchField(document, ATTRIBUTE_VALUE_SEARCHPHRASE_FIELD, searchValue);
                        addSearchFields(document, ATTRIBUTE_VALUE_SEARCHPHRASE_FIELD, attrValue.getDisplayVal());

                        if (isEnabledFlagAttributeValue(searchValue)) {

                            final Attribute attribute = attributesSupport.getByAttributeCode(attrValue.getAttributeCode());
                            if (attribute != null) {

                                addSearchField(document, ATTRIBUTE_VALUE_SEARCHPHRASE_FIELD, attribute.getName());
                                addSearchFields(document, ATTRIBUTE_VALUE_SEARCHPHRASE_FIELD, attribute.getDisplayName());
                                
                            }
                        }

                    }

                }
            }

            if (navigation) {
                // strict attribute navigation only for filtered navigation
                final String navVal = cleanFacetValue(attrValue.getVal());
                if (StringUtils.isNotBlank(navVal)) {

                    /*
                        Attribute values can be used by many different product types. However we cannot enforce usage of
                        product types in determination of distinct values since we want to use product type definitions
                        in polymorphic fashion.

                        For example Category can define pseudo type PC which has attribute PROCESSOR. However we may want to
                        refine PC into Notebook product type. Notebooks may also reside in this category. Therefore when we
                        access filtered navigation for Category PC we want distinct values of PROCESSOR for both
                        PCs and Notebooks.

                        Therefore distinct grouping must only be done on Attribute.CODE.

                        However a caution must be taken here because this means that values for attribute must be consistent
                        across all product types, otherwise there is no guarantee on what displayable name will appear in
                        filtered navigation.
                     */

                    addFacetField(document, "facet_" + code, navVal, attrValue.getDisplayVal());
                    addSimpleField(document, code, navVal);
                    final Long decNavVal = SearchUtil.valToLong(navVal, 3);
                    // If this is a decimal value choose the lowers value for range navigation
                    if (decNavVal != null && (!numRangeFields.containsKey(code) || decNavVal.compareTo(numRangeFields.get(code)) < 0)) {
                        numRangeFields.put(code, decNavVal);
                    }
                }
            }

            final boolean stored = storeAttrs.contains(code);

            // Only keep product level stored fields, so they are not overwritten
            if (stored && attrValue instanceof AttrValueProduct) {
                storedAttributes.putValue(code, attrValue.getVal(), attrValue.getDisplayVal());
            }

        }


        for (final Map.Entry<String, Long> numRange : numRangeFields.entrySet()) {
            addFacetField(document, "facetr_" + numRange.getKey(), numRange.getValue());
            addNumericField(document, numRange.getKey() + "_range", numRange.getValue(), false);
        }


    }

    /*
        Sometimes values are yes/no flags and some are meaningless without the attribute name
        so we add attribute name to search values in case someone searches by features
        (e.g. "notebook with optical drive", where attribute name "optical drive" and value is Y/N)

         // TODO: improve
     */
    protected boolean isEnabledFlagAttributeValue(final String searchValue) {
        return !"N".equalsIgnoreCase(searchValue);
    }

    private String cleanFacetValue(final String val) {
        return val != null ? val.replace('/', ' ') : null; // replace all forward slashes since they get decoded into paths
    }


    /**
     * Prices are per shop so we retrieve them once and then use for all supplier specific results.
     *
     * @param entity       SKU
     * @return prices for given SKU
     */
    protected List<SkuPrice> determineAllActivePrices(final ProductSku entity) {

        return new ArrayList<>(skuPriceSupport.getSkuPrices(entity.getCode()));

    }

    /**
     * Determine lowest price and set it as facet field.
     *
     * Sets:
     * PRODUCT_SHOP_HASOFFER_FIELD + currency: shopID, used by has offer searches
     * PRODUCT_SHOP_HASPRICE_FIELD + currency: shopID, used as one of core criteria for search in shop
     *
     * @param document    index document
     * @param entity      product
     * @param allPrices   all prices for all SKU in all shops
     * @param result      search result
     * @param available   shop PK's
     * @param now         time now to filter out inactive price records
     */
    protected void addPriceFields(final Document document, final ProductSku entity, final List<SkuPrice> allPrices, final ProductSkuSearchResultDTOImpl result, final Set<Long> available, final LocalDateTime now) {

        final boolean isShowRoom = result.getAvailability() == SkuWarehouse.AVAILABILITY_SHOWROOM;

        Set<Long> availableInShowroom = null;
        if (isShowRoom) {
            availableInShowroom = new TreeSet<>();

            for (final Long shopId : available) {

                availableInShowroom.add(shopId);

                final Set<Shop> subs = skuPriceSupport.getAllShopsAndSubs().get(shopId);

                if (CollectionUtils.isNotEmpty(subs)) {
                    for (final Shop subShop : subs) {
                        availableInShowroom.add(subShop.getShopId());
                    }
                }
            }
        }

        Set<Long> availableIn = null;

        if (!allPrices.isEmpty()) {

            final Map<Long, Map<String, SkuPrice>> lowestQuantityPrice = new HashMap<>();
            for (final SkuPrice skuPrice : allPrices) {

                if (!skuPrice.isAvailable(now)) {
                    continue; // This price is not active
                }

                if (!available.contains(skuPrice.getShop().getShopId())) {
                    continue; // This product is not in stock in this shop
                }

                if (StringUtils.isNotEmpty(skuPrice.getSupplier()) && !result.getFulfilmentCentreCode().equals(skuPrice.getSupplier())) {
                    continue; // Skip supplier specific prices which are not for this supplier
                }

                final Map<String, SkuPrice> lowestQuantityPriceByShop = lowestQuantityPrice.get(skuPrice.getShop().getShopId());
                if (lowestQuantityPriceByShop == null) {
                    // if we do not have a "byShop" this is the new lowest price
                    final Map<String, SkuPrice> newLowestQuantity = new HashMap<>();
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
                                MoneyUtils.minPositive(oldLowestQuantity.getSalePriceForCalculation()),
                                MoneyUtils.minPositive(skuPrice.getSalePriceForCalculation())
                        ))) {
                            // if this sku price has lower quantity then this is probably better starting price
                            // if the quantity is the same lower price is more appealing to show
                            lowestQuantityPriceByShop.put(skuPrice.getCurrency(), skuPrice);
                        }
                    }
                }
            }

            if (!lowestQuantityPrice.isEmpty()) {
                availableIn = new HashSet<>();
                for (final Map.Entry<Long, Map<String, SkuPrice>> shop : lowestQuantityPrice.entrySet()) {
                    for (final Map.Entry<String, SkuPrice> currency : shop.getValue().entrySet()) {

                        final Pair<BigDecimal, BigDecimal> listAndSale = currency.getValue().getSalePriceForCalculation();
                        final BigDecimal price = currency.getValue().isPriceUponRequest() ?
                                BigDecimal.ZERO : MoneyUtils.minPositive(currency.getValue().getSalePriceForCalculation());
                        // Pad 2 decimal places 99.99 => 9999
                        final long longPrice = SearchUtil.priceToLong(price);
                        final String facetName = SearchUtil.priceFacetName(shop.getKey(), currency.getKey());
                        final boolean hasOffer = !currency.getValue().isPriceUponRequest()
                                && (currency.getValue().isPriceOnOffer() || MoneyUtils.isPositive(listAndSale.getSecond()));

                        if (LOGFTQ.isTraceEnabled()) {
                            addStoredField(document, facetName + "_debug", String.valueOf(longPrice));
                        }
                        addFacetField(document, facetName, longPrice);
                        addNumericField(document, facetName + "_range", longPrice, false);
                        addSortField(document, facetName + "_sort", longPrice, false);
                        if (hasOffer) {
                            addNumericField(document, PRODUCT_SHOP_HASOFFER_FIELD + currency.getKey(), shop.getKey(), false);
                        }

                        final Set<Shop> subs = skuPriceSupport.getAllShopsAndSubs().get(shop.getKey());

                        if (CollectionUtils.isNotEmpty(subs)) {

                            for (final Shop subShop : subs) {

                                if (!subShop.isB2BStrictPriceActive() && !lowestQuantityPrice.containsKey(subShop.getShopId())) {

                                    final String subFacetName = SearchUtil.priceFacetName(subShop.getShopId(), currency.getKey());

                                    addFacetField(document, subFacetName, longPrice);
                                    addNumericField(document, subFacetName + "_range", longPrice, false);
                                    addSortField(document, subFacetName + "_sort", longPrice, false);
                                    if (hasOffer) {
                                        addNumericField(document, PRODUCT_SHOP_HASOFFER_FIELD + currency.getKey(), subShop.getShopId(), false);
                                    }

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
                for (final Long shopId : availableInShowroom) {
                    // Fill in PK's for all shops as showroom products are visible regardless of price.
                    if (LOGFTQ.isTraceEnabled()) {
                        addStoredField(document, PRODUCT_SHOP_HASPRICE_FIELD + "_debug", String.valueOf(shopId));
                    }
                    addNumericField(document, PRODUCT_SHOP_HASPRICE_FIELD, shopId, false);
                }
            } else if (CollectionUtils.isNotEmpty(availableIn)) {

                for (final Long shopId : availableIn) {
                    // Fill in PK's for all shops that have price entries.
                    if (LOGFTQ.isTraceEnabled()) {
                        addStoredField(document, PRODUCT_SHOP_HASPRICE_FIELD + "_debug", String.valueOf(shopId));
                    }
                    addNumericField(document, PRODUCT_SHOP_HASPRICE_FIELD, shopId, false);
                }

            }
        } else if (isShowRoom) {
            for (final Long shopId : availableInShowroom) {
                // Fill in PK's for all shops as showroom products are visible regardless of price.
                if (LOGFTQ.isTraceEnabled()) {
                    addStoredField(document, PRODUCT_SHOP_HASPRICE_FIELD + "_debug", String.valueOf(shopId));
                }
                addNumericField(document, PRODUCT_SHOP_HASPRICE_FIELD, shopId, false);
            }
        }
    }


    /**
     * Populate inventory related flags so that we can restrict result hits which should be available in shop by inventory.
     *
     * Sets:
     * PRODUCT_SHOP_INSTOCK_FIELD: shopID, used as core criteria for searches
     * PRODUCT_SHOP_INSTOCK_FLAG_FIELD[1|0]: shopID, used for seraching for "in stock" VS "out of stock"
     *
     * @param document   index document
     * @param result     current supplier specific result hit
     * @param available  shop PKs where this result is available
     * @param now        time now (used for pre-ordered items)
     */
    protected void addInventoryFields(final Document document, final ProductSkuSearchResultDTOImpl result, final Set<Long> available, final LocalDateTime now) {

        double boost = 1.0d;
        for (final Long shop : available) {
            if (LOGFTQ.isTraceEnabled()) {
                addStoredField(document, PRODUCT_SHOP_INSTOCK_FIELD + "_debug", shop.toString());
            }
            addNumericField(document, PRODUCT_SHOP_INSTOCK_FIELD, shop, false);
            if (result.getAvailability() == SkuWarehouse.AVAILABILITY_ALWAYS) {
                boost = 0.95d; // Always = -5% boost (stocked items must be first)
                addSortField(document, PRODUCT_AVAILABILITY_SORT_FIELD + shop.toString(), "095");
                addNumericField(document, PRODUCT_SHOP_INSTOCK_FLAG_FIELD + "1", shop, false);
                addSortField(document, PRODUCT_SHOP_INSTOCK_FLAG_SORT_FIELD + shop.toString(), "1");
            } else if (result.getReleaseDate() != null && result.getReleaseDate().isAfter(now)) {
                boost = 1.25d; // Preorder is 1.25f = 25% boost
                addSortField(document, PRODUCT_AVAILABILITY_SORT_FIELD + shop.toString(), "125");
                addNumericField(document, PRODUCT_SHOP_INSTOCK_FLAG_FIELD + "1", shop, false);
                addSortField(document, PRODUCT_SHOP_INSTOCK_FLAG_SORT_FIELD + shop.toString(), "1");
            } else { // Standard stock
                final BigDecimal qty = result.getQtyOnWarehouse(shop);
                final boolean hasStock = MoneyUtils.isPositive(qty);
                boost = hasStock ? 1.0d : 0.9d; // Standard + Backorder in stock = no boost, out of stock = -10% boost
                addSortField(document, PRODUCT_AVAILABILITY_SORT_FIELD + shop.toString(), hasStock ? "100" : "090");
                addNumericField(document, PRODUCT_SHOP_INSTOCK_FLAG_FIELD + (hasStock ? "1" : "0"), shop, false);
                addSortField(document, PRODUCT_SHOP_INSTOCK_FLAG_SORT_FIELD + shop.toString(), hasStock ? "1" : "0");
            }
        }
        if (LOGFTQ.isTraceEnabled()) {
            addStoredField(document, PRODUCT_SHOP_INSTOCK_FIELD + "_boost_debug", boost);
        }
        addBoostField(document, PRODUCT_SHOP_INSTOCK_FIELD + "_boost", boost);

    }

    /**
     * Generate result hit for product for each supplier.
     *
     * Only products that have stock records will be available in shop that have corresponding suppliers assigned
     * to them. Further filter is applied that only products that are available to sell will be cosidered for indexing.
     *
     * @param entity      product entity
     * @param now         time now (used for pre-ordered items)
     * @param resultsByFc hit results by supplier code
     * @param availableIn shop PK for which given product is available (determined by supplier link to shop)
     */
    protected void populateResultsByFc(final ProductSku entity,
                                       final LocalDateTime now,
                                       final Map<String, ProductSkuSearchResultDTOImpl> resultsByFc,
                                       final Map<ProductSkuSearchResultDTOImpl, Set<Long>> availableIn) {

        final ProductSkuSearchResultDTOImpl base = createBaseResultObject(entity);

        final Map<Long, Set<Long>> shopsByFulfilment = shopWarehouseSupport.getShopsByFulfilmentMap();

        // Stock and prices are per SKU so need to determine if we have valid ones
        final ProductSku sku = entity;

        final List<SkuWarehouse> inventory = skuWarehouseSupport.getQuantityOnWarehouse(sku.getCode());

        if (CollectionUtils.isNotEmpty(inventory)) {
            for (final SkuWarehouse stock : inventory) {

                if (isInventoryAvailableNow(stock, now)) {

                    final boolean availableToSell = stock.isAvailableToSell(true);
                    final boolean showroom = stock.getAvailability() == SkuWarehouse.AVAILABILITY_SHOWROOM;
                    final BigDecimal ats = stock.getAvailableToSell();

                    // if standard with zero stock then not available
                    if (availableToSell || showroom) {
                        final long ff = stock.getWarehouse().getWarehouseId();
                        final Set<Long> shs = shopsByFulfilment.get(ff);
                        if (shs != null) {

                            final String code = skuWarehouseSupport.getWarehouseCode(stock);
                            if (code != null) {

                                ProductSkuSearchResultDTOImpl withFc = resultsByFc.get(code);
                                if (withFc == null) {

                                    withFc = base.copy();
                                    withFc.setFulfilmentCentreCode(code);
                                    withFc.setCreatedTimestamp(stock.getCreatedTimestamp());
                                    withFc.setUpdatedTimestamp(stock.getUpdatedTimestamp());
                                    if (stock.getTag() != null) {
                                        final Set<String> uniqueTags = new TreeSet<>();
                                        if (entity.getTag() != null) {
                                            Collections.addAll(uniqueTags, StringUtils.split(entity.getTag(), ' '));
                                        }
                                        if (stock.getTag() != null) {
                                            Collections.addAll(uniqueTags, StringUtils.split(stock.getTag(), ' '));
                                        }
                                        withFc.setTag(StringUtils.join(uniqueTags, ' '));
                                    }

                                    resultsByFc.put(code, withFc);
                                    availableIn.put(withFc, new HashSet<>());
                                }
                                final Set<Long> withFcAvailableIn = availableIn.get(withFc);

                                final BigDecimal atsAdd = MoneyUtils.isPositive(ats) ? ats : BigDecimal.ZERO;

                                final Map<Long, BigDecimal> skuQtyByShopId = new HashMap<>();
                                for (final Long sh : shs) {

                                    withFcAvailableIn.add(sh);
                                    skuQtyByShopId.put(sh, atsAdd);

                                }
                                withFc.setQtyOnWarehouse(skuQtyByShopId);
                                withFc.setFeatured(stock.getFeatured() != null && stock.getFeatured());
                                withFc.setAvailability(stock.getAvailability());
                                withFc.setAvailablefrom(stock.getAvailablefrom());
                                withFc.setAvailableto(stock.getAvailableto());
                                withFc.setReleaseDate(stock.getReleaseDate());
                                withFc.setRestockDate(stock.getRestockDate());
                                withFc.setRestockNotes(new StringI18NModel(stock.getRestockNote()));
                                withFc.setMinOrderQuantity(stock.getMinOrderQuantity());
                                withFc.setMinOrderQuantity(stock.getMaxOrderQuantity());
                                withFc.setStepOrderQuantity(stock.getStepOrderQuantity());

                            }

                        }
                    }

                }

            }

        }

    }

    LocalDateTime now() {
        return TimeContext.getLocalDateTime();
    }

    /**
     * Check is product need to be in index.
     * Product will be added to index:
     * in case if product available for pre/back order;
     * or always available (for example digital products);
     *
     *
     * @param entity entity to check
     *
     * @return true if entity need to be in lucene index.
     */
    public boolean isProductInCategory(final ProductSku entity) {
        if (entity != null && entity.getProduct() != null) {
            if (entity.getProduct().getProductCategory().isEmpty()) {
                return false; // if it is not assigned to category, no way to determine the shop
            }

            return true;
        }
        return false;
    }

    /**
     * Check is product need to be in index.
     * Product will be added to index:
     * in case if product available for pre/back order;
     * or always available (for example digital products);
     *
     *
     * @param entity entity to check
     * @param now    time now
     *
     * @return true if entity need to be in lucene index.
     */
    public boolean isInventoryAvailableNow(final SkuWarehouse entity, final LocalDateTime now) {
        return entity != null && entity.isAvailable(now);
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
