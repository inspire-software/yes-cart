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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.constants.Constants;
import org.yes.cart.domain.dto.CategoryRelationDTO;
import org.yes.cart.domain.dto.ProductSearchResultDTO;
import org.yes.cart.domain.dto.StoredAttributesDTO;
import org.yes.cart.domain.dto.impl.ProductSearchResultDTOImpl;
import org.yes.cart.domain.dto.impl.StoredAttributesDTOImpl;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.i18n.impl.StringI18NModel;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.search.dao.LuceneDocumentAdapter;
import org.yes.cart.search.dao.support.*;
import org.yes.cart.search.query.impl.SearchUtil;
import org.yes.cart.util.DomainApiUtils;
import org.yes.cart.util.MoneyUtils;
import org.yes.cart.util.TimeContext;
import org.yes.cart.util.log.Markers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.yes.cart.search.dao.entity.LuceneDocumentAdapterUtils.*;
import static org.yes.cart.search.query.ProductSearchQueryBuilder.*;
/**
 * User: denispavlov
 * Date: 07/04/2017
 * Time: 14:21
 */
public class ProductLuceneDocumentAdapter implements LuceneDocumentAdapter<Product, Long> {

    private static final Logger LOGFTQ = LoggerFactory.getLogger("FTQ");

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

        final LocalDateTime now = now();

        if (isProductInCategoryHasSkuAndAvailableNow(entity, now)) {

            try {

                final Map<String, ProductSearchResultDTO> resultsByFc = new HashMap<>();
                final Map<ProductSearchResultDTO, Set<Long>> availableIn = new HashMap<>();

                populateResultsByFc(entity, now, resultsByFc, availableIn);

                final List<SkuPrice> allPrices = determineAllActivePrices(entity);

                final Document[] documents = new Document[resultsByFc.size()];
                int count = 0;

                for (final Map.Entry<String, ProductSearchResultDTO> resultByFc : resultsByFc.entrySet()) {

                    final ProductSearchResultDTO result = resultByFc.getValue();
                    final Set<Long> available = availableIn.get(result);

                    if (CollectionUtils.isEmpty(available)) {
                        continue; // If the product is not available in any shop it should not be in the index
                    }

                    final Document document = new Document();

                    addPkField(document, Product.class, String.valueOf(entity.getProductId()));

                    addSimpleField(document, PRODUCT_ID_FIELD, String.valueOf(entity.getProductId()));

                    addSimpleField(document, PRODUCT_CODE_FIELD, entity.getCode());
                    addSearchField(document, PRODUCT_CODE_FIELD_SEARCH, entity.getCode());
                    addSortField(document, PRODUCT_CODE_SORT_FIELD, entity.getCode());
                    addStemField(document, PRODUCT_CODE_STEM_FIELD, entity.getCode());

                    addSimpleField(document, PRODUCT_MULTISKU, String.valueOf(entity.isMultiSkuProduct()));

                    for (final ProductSku sku : entity.getSku()) {
                        addSimpleField(document, SKU_PRODUCT_CODE_FIELD, sku.getCode());
                        addSearchField(document, SKU_PRODUCT_CODE_FIELD_SEARCH, sku.getCode());
                        addSimpleField(document, SKU_ID_FIELD, String.valueOf(sku.getSkuId()));
                        addStemFields(document, SKU_PRODUCT_CODE_STEM_FIELD, sku.getCode(), sku.getManufacturerCode(), sku.getManufacturerPartCode(), sku.getSupplierCode());
                        addSearchField(document, PRODUCT_NAME_FIELD, sku.getName());
                        addStemFields(document, PRODUCT_NAME_STEM_FIELD, sku.getName(), sku.getSeo().getTitle(), sku.getSeo().getMetakeywords());
                        final I18NModel displayName = new StringI18NModel(sku.getDisplayName());
                        addStemFields(document, PRODUCT_DISPLAYNAME_STEM_FIELD, displayName);
                        addStemFields(document, PRODUCT_DISPLAYNAME_STEM_FIELD, new StringI18NModel(sku.getSeo().getDisplayTitle()));
                        addStemFields(document, PRODUCT_DISPLAYNAME_STEM_FIELD, new StringI18NModel(sku.getSeo().getDisplayMetakeywords()));
                        addSearchFields(document, PRODUCT_DISPLAYNAME_FIELD, displayName);
                    }
                    addSortField(document, SKU_PRODUCT_CODE_SORT_FIELD, entity.getDefaultSku().getCode());

                    addSimpleField(document, PRODUCT_MANUFACTURER_CODE_FIELD, entity.getManufacturerCode());
                    addSearchField(document, PRODUCT_MANUFACTURER_CODE_FIELD_SEARCH, entity.getManufacturerCode());
                    addSortField(document, PRODUCT_MANUFACTURER_CODE_SORT_FIELD, entity.getManufacturerCode());
                    addStemFields(document, PRODUCT_MANUFACTURER_CODE_STEM_FIELD, entity.getManufacturerCode(), entity.getManufacturerPartCode(), entity.getSupplierCode());

                    //            addSortableDateField(document, "availablefrom", "availablefrom_sort", "availablefrom_range", entity.getAvailablefrom(), true);
                    //            addSortableDateField(document, "availableto", "availableto_sort", "availableto_range", entity.getAvailableto(), false);

                    addSearchField(document, PRODUCT_NAME_FIELD, entity.getName());
                    addSortField(document, PRODUCT_NAME_SORT_FIELD, entity.getName());
                    addStemFields(document, PRODUCT_NAME_STEM_FIELD, entity.getName(), entity.getSeo().getTitle(), entity.getSeo().getMetakeywords());

                    final I18NModel displayName = new StringI18NModel(entity.getDisplayName());
                    addStemFields(document, PRODUCT_DISPLAYNAME_STEM_FIELD, displayName);
                    addStemFields(document, PRODUCT_DISPLAYNAME_STEM_FIELD, new StringI18NModel(entity.getSeo().getDisplayTitle()));
                    addStemFields(document, PRODUCT_DISPLAYNAME_STEM_FIELD, new StringI18NModel(entity.getSeo().getDisplayMetakeywords()));
                    addSearchFields(document, PRODUCT_DISPLAYNAME_FIELD, displayName);
                    addSortFields(document, PRODUCT_DISPLAYNAME_SORT_FIELD, displayName);

                    final I18NModel displayType = new StringI18NModel(entity.getProducttype().getDisplayName());
                    final String productType = cleanFacetValue(entity.getProducttype().getName());
                    addSearchField(document, PRODUCT_TYPE_FIELD_SEARCH, productType);
                    addSearchFields(document, PRODUCT_TYPE_FIELD_SEARCH, displayType);
                    addSimpleField(document, PRODUCT_TYPE_FIELD, productType);
                    addFacetField(document, "facet_productType", productType, entity.getProducttype().getDisplayName());
                    addStemField(document, PRODUCT_TYPE_STEM_FIELD, productType);
                    addStemFields(document, PRODUCT_TYPE_STEM_FIELD, displayType);

                    // Description is a bad field to index as it contain a lot of information, most of which is irrelevant
//                    addSimpleField(document, "description", entity.getDescription());
//                    final String descAsIs = entity.getDescriptionAsIs();
//                    addSimpleField(document, "descriptionAsIs", descAsIs);
//                    final I18NModel desc = new StringI18NModel(descAsIs);
//                    addStemFields(document, "description_stem", desc);

                    if (entity.getTag() != null) {
                        for (final String tag : StringUtils.split(entity.getTag(), ' ')) {
                            addSimpleField(document, PRODUCT_TAG_FIELD, tag);
                            addFacetField(document, "facet_tag", tag);
                        }
                    }

                    final String brand = cleanFacetValue(entity.getBrand().getName());
                    addSearchField(document, BRAND_FIELD_SEARCH, brand);
                    addSimpleField(document, BRAND_FIELD, brand);
                    addFacetField(document, "facet_brand", brand);
                    addStemField(document, BRAND_STEM_FIELD, brand);
                    addSortField(document, BRAND_SORT_FIELD, brand);

                    addSimpleField(document, PRODUCT_FEATURED_FIELD, entity.getFeatured() != null && entity.getFeatured() ? "true" : "false");
                    addStoredField(document, "featured_boost", entity.getFeatured() != null && entity.getFeatured() ? 1.25f : 1.0f);
                    // Created timestamp is used to determine ranges
                    addInstantField(document, PRODUCT_CREATED_FIELD, entity.getCreatedTimestamp(), false);
                    addSortField(document, PRODUCT_CREATED_SORT_FIELD, entity.getCreatedTimestamp(), false);

                    // Inventory flag
                    addInventoryFields(document, result, available, now);

                    // Add prices
                    addPriceFields(document, entity, allPrices, result, available, now);

                    // Add categories
                    addCategoryFields(document, entity, available, now);

                    // Add attributes
                    addAttributeFields(document, entity, result);

                    // save the whole search object instead of individual fields, fields are only for searching
                    // must be last step so that we have fully modified object serialised to index
                    addObjectDefaultField(document, result);

                    documents[count++] = document;

                }

                return new Pair<>(entity.getProductId(), documents);
            } catch (Exception exp) {
                LOGFTQ.error(Markers.alert(),
                        "Unable to adapt product {} to index document", entity.getCode());
                LOGFTQ.error(
                        "Unable to adapt product to index document, code: " + entity.getCode()
                                + ", cause: " + exp.getMessage(), exp);
            }

        }
        return entity != null ? new Pair<>(entity.getProductId(), null) : null;
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

        StoredAttributesDTO storedAttributes = result.getAttributes();

        final List<AttrValue> attributes = new ArrayList<>(entity.getAttributes());
        for (final ProductSku sku : entity.getSku()) {
            attributes.addAll(sku.getAttributes());
        }

        final Map<String, String> sortFields = new HashMap<>();
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

                        final I18NModel displayValue = StringUtils.isNotBlank(attrValue.getDisplayVal()) ? new StringI18NModel(attrValue.getDisplayVal()) : null;

                        // searchable and navigatable terms for global search tokenised
                        addStemField(document, ATTRIBUTE_VALUE_SEARCH_FIELD, searchValue);

                        // searchable and navigatable terms for global search full phrase
                        addSearchField(document, ATTRIBUTE_VALUE_SEARCHPHRASE_FIELD, searchValue);
                        addSearchFields(document, ATTRIBUTE_VALUE_SEARCHPHRASE_FIELD, displayValue);

                        if (isEnabledFlagAttributeValue(searchValue)) {

                            final Attribute attribute = attributesSupport.getByAttributeCode(attrValue.getAttributeCode());
                            if (attribute != null) {

                                final I18NModel attrName = StringUtils.isNotBlank(attribute.getDisplayName()) ? new StringI18NModel(attribute.getDisplayName()) : null;

                                addSearchField(document, ATTRIBUTE_VALUE_SEARCHPHRASE_FIELD, attribute.getName());
                                addSearchFields(document, ATTRIBUTE_VALUE_SEARCHPHRASE_FIELD, attrName);
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
                    // Choose the lowest value for sorting
                    if (!sortFields.containsKey(code) || navVal.compareTo(sortFields.get(code)) < 0){
                        sortFields.put(code, navVal);
                    }
                    final Long decNavVal = SearchUtil.valToLong(navVal, 3);
                    // If this is a decimal value choose the lowest value for range navigation
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

        for (final Map.Entry<String, String> sort : sortFields.entrySet()) {
            addSortField(document, sort.getKey() + "_sort", sort.getValue());
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
     * @param entity       product
     * @return prices for all SKUs of this product
     */
    protected List<SkuPrice> determineAllActivePrices(final Product entity) {

        final List<SkuPrice> all = new ArrayList<>();

        for (final ProductSku sku : entity.getSku()) {

            all.addAll(skuPriceSupport.getSkuPrices(sku.getCode()));

        }

        return all;
    }

    /**
     * Determine lowest price and set it as facet field.
     * @param document    index document
     * @param entity      product
     * @param allPrices   all prices for all SKU in all shops
     * @param result      search result
     * @param available   shop PK's
     * @param now         time now to filter out inactive price records
     */
    protected void addPriceFields(final Document document, final Product entity, final List<SkuPrice> allPrices, final ProductSearchResultDTO result, final Set<Long> available, final LocalDateTime now) {

        final boolean isShowRoom = entity.getAvailability() == Product.AVAILABILITY_SHOWROOM;

        Set<Long> availableIn = null;
        if (!allPrices.isEmpty()) {

            final Map<Long, Map<String, SkuPrice>> lowestQuantityPrice = new HashMap<>();
            for (final SkuPrice skuPrice : allPrices) {

                if (!available.contains(skuPrice.getShop().getShopId())) {
                    continue; // This product is not in stock in this shop
                }

                final Map<String, BigDecimal> stock = result.getQtyOnWarehouse(skuPrice.getShop().getShopId());
                if (stock != null && !stock.containsKey(skuPrice.getSkuCode())) {
                    continue; // This SKU is available in this shop
                }

                if (!DomainApiUtils.isObjectAvailableNow(true, skuPrice.getSalefrom(), skuPrice.getSaleto(), now)) {
                    continue; // This price is not active
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

                        final BigDecimal price = currency.getValue().isPriceUponRequest() ?
                                BigDecimal.ZERO : MoneyUtils.minPositive(currency.getValue().getSalePriceForCalculation());
                        // Pad 2 decimal places 99.99 => 9999
                        final long longPrice = SearchUtil.priceToLong(price);
                        final String facetName = SearchUtil.priceFacetName(shop.getKey(), currency.getKey());

                        if (LOGFTQ.isTraceEnabled()) {
                            addStoredField(document, facetName + "_debug", String.valueOf(longPrice));
                        }
                        addFacetField(document, facetName, longPrice);
                        addNumericField(document, facetName + "_range", longPrice, false);
                        addSortField(document, facetName + "_sort", longPrice, false);

                        final Set<Shop> subs = skuPriceSupport.getAllShopsAndSubs().get(shop.getKey());

                        if (CollectionUtils.isNotEmpty(subs)) {

                            for (final Shop subShop : subs) {

                                if (!subShop.isB2BStrictPriceActive() && !lowestQuantityPrice.containsKey(subShop.getShopId())) {

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
                    if (LOGFTQ.isTraceEnabled()) {
                        addStoredField(document, PRODUCT_SHOP_HASPRICE_FIELD + "_debug", String.valueOf(shop.getShopId()));
                    }
                    addNumericField(document, PRODUCT_SHOP_HASPRICE_FIELD, shop.getShopId(), false);
                }
            } else if (CollectionUtils.isNotEmpty(availableIn)) {

                for (final Long shopId : availableIn) {
                    // Fill in PK's for all shops that have price entries.
                    if (LOGFTQ.isTraceEnabled()) {
                        addStoredField(document, PRODUCT_SHOP_HASPRICE_FIELD + "_debug", shopId.toString());
                    }
                    addNumericField(document, PRODUCT_SHOP_HASPRICE_FIELD, shopId, false);
                }

            }
        } else if (isShowRoom) {
            for (final Shop shop : skuPriceSupport.getAll()) {
                // Fill in PK's for all shops as showroom products are visible regardless of price.
                if (LOGFTQ.isTraceEnabled()) {
                    addStoredField(document, PRODUCT_SHOP_HASPRICE_FIELD + "_debug", String.valueOf(shop.getShopId()));
                }
                addNumericField(document, PRODUCT_SHOP_HASPRICE_FIELD, shop.getShopId(), false);
            }
        }
    }

    private void addCategoryFields(final Document document, final Product entity, final Set<Long> available, final LocalDateTime now) {

        final Set<Long> availableCategories = new HashSet<>();

        int rank = 0;
        int count = 0;

        for (final ProductCategory productCategory : entity.getProductCategory()) {

            if (isCategoryAvailable(productCategory.getCategory().getCategoryId(), now)) {

                final CategoryRelationDTO category =
                        shopCategorySupport.getCategoryRelationById(productCategory.getCategory().getCategoryId());

                if (LOGFTQ.isTraceEnabled()) {
                    addStoredField(document, PRODUCT_CATEGORY_FIELD + "_debug", String.valueOf(category.getCategoryId()));
                }
                addNumericField(document, PRODUCT_CATEGORY_FIELD, category.getCategoryId(), false);
                addCategoryNameFields(document, category);

                addCategoryParentIdsFields(document, category);

                availableCategories.add(productCategory.getCategory().getCategoryId());

                rank += productCategory.getRank();
                count++;

            }

        }
        float boost = 1f;
        if (count > 0) {
            // 500 is base rank, anything lower increases boost, higher decreases boost
            float score = (500f - ((float) rank / (float) count));
            if (score != 0f) {
                // 1pt == 0.001f boost
                boost += score / 1000f;
            }
            if (boost < 0.25) {
                boost = 0.25f; // does not make any sense to have it lower
            }
        }

        addStoredField(document, PRODUCT_CATEGORY_FIELD + "_boost", boost);

        if (CollectionUtils.isNotEmpty(availableCategories)) {

            // Global search is done on this field. There is a disparity in the sense that after assigning category to
            // shop the product will become reachable in category search but will still be unavailable in global searches
            // since it is done on shop field below. So full reindexing is required to synchronise fully

            final List<Shop> shops = shopCategorySupport.getAll();

            for (final Long availableCategory : availableCategories) {

                for (final Shop shop : shops) {

                    if (shopCategorySupport.getShopCategoriesIds(shop.getShopId()).contains(availableCategory)) {

                        if (LOGFTQ.isTraceEnabled()) {
                            addStoredField(document, PRODUCT_SHOP_FIELD + "_debug", String.valueOf(shop.getShopId()));
                        }
                        addNumericField(document, PRODUCT_SHOP_FIELD, shop.getShopId(), false);

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
    protected boolean isCategoryAvailable(final Long currentId, final LocalDateTime now) {

        if (currentId != null) {

            final CategoryRelationDTO category = shopCategorySupport.getCategoryRelationById(currentId);
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
    protected void addCategoryNameFields(final Document document, final CategoryRelationDTO category) {

        addSearchField(document, PRODUCT_CATEGORYNAME_FIELD, category.getName());
        addStemField(document, PRODUCT_CATEGORYNAME_STEM_FIELD, category.getName());
        final I18NModel displayName = new StringI18NModel(category.getDisplayName());
        addSearchFields(document, PRODUCT_CATEGORYNAME_FIELD, displayName);
        addStemFields(document, PRODUCT_CATEGORYNAME_STEM_FIELD, displayName);

    }

    /**
     * Recursion function to traverse up the category hierarchy (including category links) and fill in category id's in which
     * current product can be found.
     *
     * Note that this function goes right up to the "root" since catalogs are independent of shops and hence any shop can link
     * to category at any level of the hierarchy thus we cannot distinguish between what level is specific to specific shop.
     *
     * This is not a bug but a feature since catalog is viewed as taxonomy tool and not a container tree for storing products.
     *
     * @param document       index document
     * @param category       current category
     */
    protected void addCategoryParentIdsFields(final Document document, final CategoryRelationDTO category) {

        if (LOGFTQ.isTraceEnabled()) {
            addStoredField(document, PRODUCT_CATEGORY_INC_PARENTS_FIELD + "_debug", String.valueOf(category.getCategoryId()));
        }
        addNumericField(document, PRODUCT_CATEGORY_INC_PARENTS_FIELD, category.getCategoryId(), false);

        final Set<Long> parentIds = shopCategorySupport.getCategoryParentsIds(category.getCategoryId());

        for (final Long parentId : parentIds) {

            final CategoryRelationDTO parent = shopCategorySupport.getCategoryRelationById(parentId);

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
    protected void addInventoryFields(final Document document, final ProductSearchResultDTO result, final Set<Long> available, final LocalDateTime now) {

        for (final Long shop : available) {
            if (LOGFTQ.isTraceEnabled()) {
                addStoredField(document, PRODUCT_SHOP_INSTOCK_FIELD + "_debug", shop.toString());
            }
            addNumericField(document, PRODUCT_SHOP_INSTOCK_FIELD, shop, false);
            if (result.getAvailability() == Product.AVAILABILITY_ALWAYS) {
                // Always = -5% boost (stocked items must be first)
                addStoredField(document, PRODUCT_SHOP_INSTOCK_FIELD + "_boost", 0.95f);
                addSortField(document, PRODUCT_AVAILABILITY_SORT_FIELD + shop.toString(), "095");
                addNumericField(document, PRODUCT_SHOP_INSTOCK_FLAG_FIELD + "1", shop, false);
                addSortField(document, PRODUCT_SHOP_INSTOCK_FLAG_SORT_FIELD + shop.toString(), "1");
            } else if (result.getAvailability() == Product.AVAILABILITY_PREORDER &&
                    DomainApiUtils.isObjectAvailableNow(true, result.getAvailablefrom(), null, now)) {
                // Preorder is 1.25f = 25% boost
                addStoredField(document, PRODUCT_SHOP_INSTOCK_FIELD + "_boost", 1.25f);
                addSortField(document, PRODUCT_AVAILABILITY_SORT_FIELD + shop.toString(), "125");
                addNumericField(document, PRODUCT_SHOP_INSTOCK_FLAG_FIELD + "1", shop, false);
                addSortField(document, PRODUCT_SHOP_INSTOCK_FLAG_SORT_FIELD + shop.toString(), "1");
            } else { // Standard stock
                final Map<String, BigDecimal> qty = result.getQtyOnWarehouse(shop);
                boolean hasStock = false;
                for (final BigDecimal singleSku : qty.values()) {
                    if (MoneyUtils.isPositive(singleSku)) {
                        hasStock = true;
                        break;
                    }
                }
                // Standard + Backorder in stock = no boost, out of stock = -10% boost
                addStoredField(document, PRODUCT_SHOP_INSTOCK_FIELD + "_boost", hasStock ? 1.0f : 0.9f);
                addSortField(document, PRODUCT_AVAILABILITY_SORT_FIELD + shop.toString(), hasStock ? "100" : "090");
                addNumericField(document, PRODUCT_SHOP_INSTOCK_FLAG_FIELD + (hasStock ? "1" : "0"), shop, false);
                addSortField(document, PRODUCT_SHOP_INSTOCK_FLAG_SORT_FIELD + shop.toString(), hasStock ? "1" : "0");
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
    protected void populateResultsByFc(final Product entity, final LocalDateTime now, final Map<String, ProductSearchResultDTO> resultsByFc, final Map<ProductSearchResultDTO, Set<Long>> availableIn) {

        final ProductSearchResultDTO base = createBaseResultObject(entity);

        final List<Shop> shops = shopWarehouseSupport.getAll();
        final Map<Long, Set<Long>> shopsByFulfilment = shopWarehouseSupport.getShopsByFulfilmentMap();

        final Map<ProductSearchResultDTO, Map<Long, Map<String, BigDecimal>>> qtyOnWarehouse = new HashMap<>();

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
                                || MoneyUtils.isPositive(ats)) {
                            final long ff = stock.getWarehouse().getWarehouseId();
                            final Set<Long> shs = shopsByFulfilment.get(ff);
                            if (shs != null) {

                                final String code = skuWarehouseSupport.getWarehouseCode(stock);
                                if (code != null) {

                                    ProductSearchResultDTO withFc = resultsByFc.get(code);
                                    if (withFc == null) {
                                        withFc = base.copy();
                                        withFc.setFulfilmentCentreCode(code);
                                        final Map<Long, Map<String, BigDecimal>> qty = new HashMap<>();
                                        withFc.setQtyOnWarehouse(qty);
                                        qtyOnWarehouse.put(withFc, qty);
                                        resultsByFc.put(code, withFc);
                                        availableIn.put(withFc, new HashSet<>());
                                    }
                                    final Set<Long> withFcAvailableIn = availableIn.get(withFc);

                                    final BigDecimal atsAdd = MoneyUtils.isPositive(ats) ? ats : BigDecimal.ZERO;

                                    for (final Long sh : shs) {

                                        withFcAvailableIn.add(sh);
                                        final Map<Long, Map<String, BigDecimal>> qtyByShop = qtyOnWarehouse.get(withFc);
                                        final Map<String, BigDecimal> qtyBySku = qtyByShop.computeIfAbsent(sh, k -> new HashMap<>());
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
                    final Map<Long, Map<String, BigDecimal>> qty = new HashMap<>();
                    noFc.setQtyOnWarehouse(qty);
                    qtyOnWarehouse.put(noFc, qty);
                    resultsByFc.put(NO_FC_CODE, noFc);
                    availableIn.put(noFc, new HashSet<>());
                }
                final Set<Long> noFcAvailableIn = availableIn.get(noFc);

                for (final Shop shop : shops) {

                    // Available as perpetual (preorder/backorder must have stock)
                    noFcAvailableIn.add(shop.getShopId());
                    final Map<Long, Map<String, BigDecimal>> qtyByShop = qtyOnWarehouse.get(noFc);
                    final Map<String, BigDecimal> qtyBySku = qtyByShop.computeIfAbsent(shop.getShopId(), k -> new HashMap<>());
                    qtyBySku.put(sku.getCode(), BigDecimal.ONE);

                }

            }

        }

    }

    LocalDateTime now() {
        return TimeContext.getLocalDateTime();
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
        baseResult.setType(entity.getProducttype().getName());
        baseResult.setService(entity.getProducttype().isService());
        baseResult.setEnsemble(entity.getProducttype().isEnsemble());
        baseResult.setDigital(entity.getProducttype().isDigital());
        baseResult.setShippable(entity.getProducttype().isShippable());
        baseResult.setDownloadable(entity.getProducttype().isDownloadable());
        baseResult.setDisplayType(entity.getProducttype().getDisplayName());
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
        baseResult.setAttributes(new StoredAttributesDTOImpl());
        baseResult.setQtyOnWarehouse(null); // this will be now per warehouse (i.e. result per supplier)

        return baseResult;
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
    public boolean isProductInCategoryHasSkuAndAvailableNow(final Product entity, final LocalDateTime now) {
        if (entity != null) {
            if (entity.getProductCategory().isEmpty()) {
                return false; // if it is not assigned to category, no way to determine the shop
            }
            if (entity.getSku().isEmpty()) {
                return false; // if there are no SKU then it makes no sense to have it in index
            }

            final int availability = entity.getAvailability();
            switch (availability) {
                case Product.AVAILABILITY_PREORDER:
                    // For preorders check only available to date since that is the whole point of preorders
                    return DomainApiUtils.isObjectAvailableNow(true, null, entity.getAvailableto(), now);
                case Product.AVAILABILITY_STANDARD:
                case Product.AVAILABILITY_BACKORDER:
                case Product.AVAILABILITY_ALWAYS:
                case Product.AVAILABILITY_SHOWROOM:
                default:
                    // standard, showroom, always and backorder must be in product date range
                    return DomainApiUtils.isObjectAvailableNow(true, entity.getAvailablefrom(), entity.getAvailableto(), now);
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
