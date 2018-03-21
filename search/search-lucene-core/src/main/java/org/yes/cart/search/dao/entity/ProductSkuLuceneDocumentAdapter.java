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

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.document.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.constants.Constants;
import org.yes.cart.domain.dto.ProductSkuSearchResultDTO;
import org.yes.cart.domain.dto.StoredAttributesDTO;
import org.yes.cart.domain.dto.impl.ProductSkuSearchResultDTOImpl;
import org.yes.cart.domain.dto.impl.StoredAttributesDTOImpl;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.i18n.impl.StringI18NModel;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.search.dao.LuceneDocumentAdapter;
import org.yes.cart.search.dao.support.NavigatableAttributesSupport;
import org.yes.cart.search.util.SearchUtil;
import org.yes.cart.util.DomainApiUtils;
import org.yes.cart.util.TimeContext;
import org.yes.cart.util.log.Markers;

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

    private NavigatableAttributesSupport attributesSupport;

    /**
     * {@inheritDoc}
     */
    @Override
    public Pair<Long, Document[]> toDocument(final ProductSku entity) {

        final LocalDateTime now = now();

        if (isProductInCategoryAndAvailableNow(entity, now)) {

            try {

                final Document document = new Document();

                final ProductSkuSearchResultDTO result = createBaseResultObject(entity);

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
                float boost = 1f;
                // 50 is lowest rank, anything lower increases boost, higher decreases boost
                if (entity.getRank() != 50) {
                    // 1pt == 0.01f boost
                    boost += 50f - ((float) entity.getRank()) / 100f;
                    if (boost < 0.25) {
                        boost = 0.25f; // does not make any sense to have it lower
                    }
                }
                addStoredField(document, "rank_boost", boost);

                // Created timestamp is used to determine ranges
                addInstantField(document, PRODUCT_CREATED_FIELD, entity.getCreatedTimestamp(), false);
                addSortField(document, PRODUCT_CREATED_SORT_FIELD, entity.getCreatedTimestamp(), false);

                // Add attributes
                addAttributeFields(document, entity, result);

                // save the whole search object instead of individual fields, fields are only for searching
                // must be last step so that we have fully modified object serialised to index
                addObjectDefaultField(document, result);

                return new Pair<>(entity.getSkuId(), new Document[]{document});

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
    protected ProductSkuSearchResultDTO createBaseResultObject(final ProductSku entity) {

        final ProductSkuSearchResultDTO baseResult = new ProductSkuSearchResultDTOImpl();
        baseResult.setId(entity.getSkuId());
        baseResult.setProductId(entity.getProduct().getProductId());
        baseResult.setCode(entity.getCode());
        baseResult.setManufacturerCode(entity.getManufacturerCode());
        baseResult.setName(entity.getName());
        baseResult.setDisplayName(entity.getDisplayName());
        final String image0 = entity.getAttributeValueByCode(AttributeNamesKeys.Product.PRODUCT_IMAGE_ATTR_NAME_PREFIX + "0");
        if (StringUtils.isBlank(image0)) {
            baseResult.setDefaultImage(Constants.NO_IMAGE);
        } else {
            baseResult.setDefaultImage(image0);
        }
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
    protected void addAttributeFields(final Document document, final ProductSku entity, final ProductSkuSearchResultDTO result) {

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
    public boolean isProductInCategoryAndAvailableNow(final ProductSku entity, final LocalDateTime now) {
        if (entity != null && entity.getProduct() != null) {
            if (entity.getProduct().getProductCategory().isEmpty()) {
                return false; // if it is not assigned to category, no way to determine the shop
            }

            final int availability = entity.getProduct().getAvailability();
            switch (availability) {
                case Product.AVAILABILITY_PREORDER:
                    // For preorders check only available to date since that is the whole point of preorders
                    return DomainApiUtils.isObjectAvailableNow(true, null, entity.getProduct().getAvailableto(), now);
                case Product.AVAILABILITY_STANDARD:
                case Product.AVAILABILITY_BACKORDER:
                case Product.AVAILABILITY_ALWAYS:
                case Product.AVAILABILITY_SHOWROOM:
                default:
                    // standard, showroom, always and backorder must be in product date range
                    return DomainApiUtils.isObjectAvailableNow(true, entity.getProduct().getAvailablefrom(), entity.getProduct().getAvailableto(), now);
            }

        }
        return false;
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
