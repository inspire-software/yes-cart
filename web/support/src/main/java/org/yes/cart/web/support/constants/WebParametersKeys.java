package org.yes.cart.web.support.constants;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 2011-May-17
 * Time: 11:54:55 AM
 */
public interface WebParametersKeys {

    /**
     * Application director
     */
    String APPLICATION_DYNAMYC_CACHE = "appDynamycCache";

    /**
     * Currency switch request bean
     */
    String REQUEST_CURRENCY = "currency";

   /**
     * Language switch request bean.
     */
    String REQUEST_LANGUAGE = "language";

    /**
     * View resolver bean.
     */
    String REQUEST_VIEW_RESOLVER = "viewResolver";

    /**
     * Currency switch request bean
     */
    String REQUEST_TOP_LEVEL_CATEGORIES = "topLevelCategories";

    /**
     * Currency switch request bean
     */
    String REQUEST_SUB_CATEGORIES = "subCategories";

    /**
     * Product list.
     */
    String REQUEST_PRODUCT_LIST = "prodList";

    /**
     * View parameters holder bean.
     */
    String VIEW_PARAMETERS = "viewParams";


    /**
     * Session shopping cart.
     */
    String SESSION_SHOPPING_CART = "webShoppingCart";

    String CATEGORY_ID = "category";

    String PRODUCT_ID = "product";

    String SKU_ID = "sku";

    String CATEGORY_NAME = "categoryName";

    String QUERY = "query";

    String MAIN_PANEL_RENDERER = "renderer";

    String PAGE = "page";

    String SORT = "sorta";

    String SORT_REVERSE = "sortd";

    String ADDRESS_ID = "addrId";

    String ADDRESS_TYPE = "addrType";

    /**
     * Quantity of items per page.
     */
    String QUANTITY = "showItems";



    /**
     * Image height.
     */
    String HEIGHT = "h";
    /**
     * Image width.
     */
    String WIDTH = "w";
}
