/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
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
    String REQUEST_SHOPPING_CART = "shoppingCart";

    /**
     * Parameter set by the encoder to denote which page we are on.
     * I.e. "content", "category", "product" or "sku".
     */
    String PAGE_TYPE = "pagetype";

    /**
     * Key for content rendering
     */
    String CONTENT_ID = "content";

    /**
     * Key for category rendering
     */
    String CATEGORY_ID = "category";

    /**
     * Central View resolver config to allow recursive product lookup instead of category view.
     */
    String CATEGORY_PRODUCTS_RECURSIVE = "categoryProductsRecursive";

    /**
     * Key for product rendering
     */
    String PRODUCT_ID = "product";

    /**
     * Key for specific SKU rendering
     */
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
     * Label, that shows return address after address editing
     */
    String ADDRESS_FORM_RETURN_LABEL= "returnTo";

    /**
     * Quantity of items per page.
     */
    String QUANTITY = "items";



    /**
     * Image height.
     */
    String HEIGHT = "h";
    /**
     * Image width.
     */
    String WIDTH = "w";
}
