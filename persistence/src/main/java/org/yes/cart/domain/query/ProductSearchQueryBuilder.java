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

package org.yes.cart.domain.query;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 16:13:01
 */
public interface ProductSearchQueryBuilder extends SearchQueryBuilder {

    //can be used in sort order
    String PRODUCT_NAME_FIELD = "name";
    String PRODUCT_DISPLAYNAME_FIELD = "displayName";
    String PRODUCT_DISPLAYNAME_STEM_FIELD = "displayName_stem";
    String PRODUCT_DISPLAYNAME_ASIS_FIELD = "displayNameAsIs"; //for projections only
    String PRODUCT_CATEGORYNAME_FIELD = "categoryName";
    String PRODUCT_CATEGORYNAME_STEM_FIELD = "categoryName_stem";
    String PRODUCT_CODE_FIELD = "code";
    String PRODUCT_MANUFACTURER_CODE_FIELD = "manufacturerCode";
    String PRODUCT_DEFAULT_SKU_CODE_FIELD = "defaultSku";
    String PRODUCT_CODE_STEM_FIELD = "code_stem";
    String PRODUCT_MANUFACTURER_CODE_STEM_FIELD = "manufacturerCode_stem";
    String PRODUCT_MULTISKU = "multisku";

    String PRODUCT_CREATED_FIELD = "createdTimestamp"; //for projections only
    String PRODUCT_UPDATED_FIELD = "updatedTimestamp"; //for projections only
    String PRODUCT_FEATURED_FIELD = "featured"; //for projections only
    String PRODUCT_AVAILABILITY_FROM_FIELD = "availablefrom"; //for projections only
    String PRODUCT_AVAILABILITY_TO_FIELD = "availableto"; //for projections only
    String PRODUCT_AVAILABILITY_FIELD = "availability"; //for projections only
    String PRODUCT_QTY_FIELD = "qtyOnWarehouse"; //for projections only
    String PRODUCT_DEFAULTIMAGE_FIELD = "defaultImage"; //for projections only
    String PRODUCT_DESCRIPTION_ASIS_FIELD = "descriptionAsIs"; //for projections only
    String PRODUCT_MIN_QTY_FIELD = "minOrderQuantity"; //for projections only
    String PRODUCT_MAX_QTY_FIELD = "maxOrderQuantity"; //for projections only
    String PRODUCT_STEP_QTY_FIELD = "stepOrderQuantity"; //for projections only

    String PRODUCT_TAG_FIELD = "tag";
    String SKU_PRODUCT_CODE_FIELD = "sku.code";
    String SKU_PRODUCT_CODE_STEM_FIELD = "sku.code_stem";
    String SKU_PRODUCT_MANUFACTURER_CODE_FIELD = "sku.manufacturerCode";
    String SKU_PRODUCT_MANUFACTURER_CODE_STEM_FIELD = "sku.manufacturerCode_stem";
    String PRODUCT_NAME_SORT_FIELD = "name_sort";
    String PRODUCT_DISPLAYNAME_SORT_FIELD = "displayName_sort";
    String PRODUCT_DESCRIPTION_FIELD = "description";
    String PRODUCT_DESCRIPTION_STEM_FIELD = "description_stem";
    String BRAND_FIELD = "brand";
    String BRAND_NAME_FIELD = "brandName";
    String ATTRIBUTE_CODE_FIELD = "attribute.attribute";
    String ATTRIBUTE_VALUE_FIELD = "attribute.val";
    String ATTRIBUTE_VALUE_SEARCH_FIELD = "attribute.attrvalsearch";
    String ATTRIBUTE_VALUE_SEARCHPHRASE_FIELD = "attribute.attrvalsearchphrase";
    String ATTRIBUTE_VALUE_SEARCHPRIMARY_FIELD = "attribute.attrvalsearchprimary";
    String ATTRIBUTE_VALUE_STORE_FIELD = "attribute.attrvalstore";

    String PRODUCT_CATEGORY_FIELD = "productCategory.category";
    String PRODUCT_CATEGORY_INC_PARENTS_FIELD = "productCategory.category.inc.parents";
    String PRODUCT_SHOP_FIELD = "productShopId";
    String PRODUCT_SHOP_INSTOCK_FIELD = "productInStockShopId";
    String PRODUCT_SHOP_HASPRICE_FIELD = "productHasPriceShopId";
    String PRODUCT_ID_FIELD = "productId";
    String SKU_ID_FIELD = "sku.skuId"; //////////////////////////////////////////////

    // not really a field, but used in query type determination
    String PRODUCT_PRICE = "price";

    // not really a field, but used for global search
    String QUERY = "query";


    String TAG_NEWARRIVAL = "newarrival";

    //--------------------------------------- categories -----------------------------------

    String CATEGORY_NAME_FIELD = "name";
    String CATEGORY_DISPLAYNAME_FIELD = "displayName";
    String CATEGORY_DESCRIPTION_FIELD = "description";
    String CATEGORY_SEO_URI_FIELD = "seo.uri";
    String CATEGORY_SEO_TITLE_FIELD = "seo.title";
    String CATEGORY_SEO_METAKEYWORDS_FIELD = "seo.metakeywords";
    String CATEGORY_SEO_METADESCRIPTION_FIELD = "seo.metadescription";

}
