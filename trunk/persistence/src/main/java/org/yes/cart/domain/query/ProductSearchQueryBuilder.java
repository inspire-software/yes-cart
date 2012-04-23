package org.yes.cart.domain.query;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 16:13:01
 */
public interface ProductSearchQueryBuilder {

    //can be used in sort order
    public final static String PRODUCT_NAME_FIELD = "name";
    public final static String PRODUCT_CODE_FIELD = "code";
    public final static String PRODUCT_TAG_FIELD = "tag";
    public final static String SKU_PRODUCT_CODE_FIELD = "sku.code";
    public final static String PRODUCT_NAME_SORT_FIELD = "name_sort";
    public final static String PRODUCT_DESCIPTION_FIELD = "description";
    public final static String BRAND_FIELD = "brand";
    public final static String ATTRIBUTE_CODE_FIELD = "attribute.attribute";
    public final static String ATTRIBUTE_VALUE_FIELD = "attribute.val";
    public final static String ATTRIBUTE_VALUE_SEARCH_FIELD = "attribute.attrvalsearch";

    public final static String SKU_ATTRIBUTE_CODE_FIELD = "sku.attribute.attribute";
    public final static String SKU_ATTRIBUTE_VALUE_FIELD = "sku.attribute.val";
    public final static String SKU_ATTRIBUTE_VALUE_SEARCH_FIELD = "sku.attribute.attrvalsearch";


    public final static String PRODUCT_CATEGORY_FIELD = "productCategory.category";
    public final static String PRODUCT_ID_FIELD = "productId";
    public final static String SKU_ID_FIELD = "sku.skuId"; //////////////////////////////////////////////

    //default sort order
    public final static String PRODUCT_CATEGORY_RANK_FIELD = "productCategory.rank";

    public final static String PRODUCT_PRICE_CURRENCY = "sku.skuPrice.currency";
    
    //can be used in sort order
    public final static String PRODUCT_PRICE_AMOUNT = "sku.skuPrice.regularPrice";
    public final static String PRODUCT_PRICE_SHOP = "sku.skuPrice.shop";

    //aggregated index in SHOPID_CURRENCY_PRICE format
    public final static String PRODUCT_SKU_PRICE = "sku.skuPrice";

    // not really a field, but used in query type determination
    public final static String PRODUCT_PRICE = "price";

    // not really a field, but used for global search
    public final static String QUERY = "query";

}
