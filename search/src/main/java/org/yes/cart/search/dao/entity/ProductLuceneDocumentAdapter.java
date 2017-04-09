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
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.constants.Constants;
import org.yes.cart.domain.dto.ProductSearchResultDTO;
import org.yes.cart.domain.dto.impl.ProductSearchResultDTOImpl;
import org.yes.cart.domain.entity.Product;
import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.domain.entity.impl.StoredAttributesImpl;
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.i18n.impl.StringI18NModel;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.search.dao.LuceneDocumentAdapter;
import org.yes.cart.util.DomainApiUtils;

import java.util.Date;

import static org.yes.cart.search.dao.entity.LuceneDocumentAdapterUtils.*;

/**
 * User: denispavlov
 * Date: 07/04/2017
 * Time: 14:21
 */
public class ProductLuceneDocumentAdapter implements LuceneDocumentAdapter<Product, Long> {

    /**
     * {@inheritDoc}
     */
    @Override
    public Pair<Long, Document[]> toDocument(final Product entity) {
        if (isIncludeInLuceneIndex(entity, false)) {

            final ProductSearchResultDTO base = createBaseResultObject(entity);

            final Document document = new Document();

            // TODO: save the whole search object instead of individual fields, fields are only for searching
            addObjectDefaultField(document, base);

            addPkField(document, Product.class, String.valueOf(entity.getProductId()));

            addSimpleField(document, "code", entity.getCode());
            addSortField(document, "code_sort", entity.getCode());
            addStemField(document, "code_stem", entity.getCode());

            addSimpleField(document, "manufacturerCode", entity.getManufacturerCode());
            addSortField(document, "manufacturerCode_sort", entity.getManufacturerCode());
            addStemFields(document, "manufacturerCode_stem", entity.getManufacturerCode(), entity.getManufacturerPartCode(), entity.getSupplierCode());

//            addSortableDateField(document, "availablefrom", "availablefrom_sort", "availablefrom_range", entity.getAvailablefrom(), true);
//            addSortableDateField(document, "availableto", "availableto_sort", "availableto_range", entity.getAvailableto(), false);

            addSimpleField(document, "name", entity.getName());
            addSortField(document, "name_sort", entity.getName());
            addStemField(document, "name_stem", entity.getName());

            final String displayNameAsIs = entity.getDisplayName();
            addSimpleField(document, "displayNameAsIs", displayNameAsIs);
            final I18NModel displayName = new StringI18NModel(displayNameAsIs);
            addStemFields(document, "displayName_stem", displayName);
            addSimpleFields(document, "displayName", displayName);
            addSortFields(document, "displayName_sort", displayName);

            addSimpleField(document, "description", entity.getDescription());
            final String descAsIs = entity.getDescriptionAsIs();
            addSimpleField(document, "descriptionAsIs", descAsIs);
//            final I18NModel desc = new StringI18NModel(descAsIs);
//            addStemFields(document, "description_stem", desc);

            addSimpleField(document, "tag", entity.getTag());
            addSimpleField(document, "tag", entity.getTag());


        }
        return entity != null ? new Pair<Long, Document[]>(entity.getProductId(), null) : null;
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

}
