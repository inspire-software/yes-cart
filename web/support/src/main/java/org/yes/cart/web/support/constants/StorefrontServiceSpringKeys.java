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
 * Date: 7/10/11
 * Time: 9:23 PM
 */
public interface StorefrontServiceSpringKeys {

    /**
     * Currency service.
     */
    public String CURRENCY_SYMBOL_SERVICE = "currencySymbolService";

    /**
     * Breadcrumbs builder
     */
    public String BREAD_CRUMBS_BUILDER = "breadCrumbsBuilder";

    /**
     * Filtered navigation support
     */
    public String FILTERNAV_SUPPORT_ATTRIBUTES = "attributeFilteredNavigationSupport";

    /**
     * Filtered navigation support
     */
    public String FILTERNAV_SUPPORT_BRANDS = "brandFilteredNavigationSupport";

    /**
     * Filtered navigation support
     */
    public String FILTERNAV_SUPPORT_PRICE = "priceFilteredNavigationSupport";

    /**
     * Service to resolve central view from request parameters.
     */
    public String CENTRAL_VIEW_RESOLVER = "centralViewResolver";

    /**
     * Service to resolve central view from request parameters.
     */
    public String AMOUNT_CALCULATION_STRATEGY = "amountCalculationStrategy";

     /**
      * Category image service .
      */
     public String CATEGORY_IMAGE_SERVICE = "categoryImageService";

     /**
      * Product image service .
      */
     public String PRODUCT_IMAGE_SERVICE = "productImageService";

     /**
      * Cart persister.
      */
     public String CART_PERSISTER = "shoppingCartPersister";

     /**
      * i18n web support
      */
     public String I18N_SUPPORT = "i18nSupport";

     /**
      * Decorator facade
      */
     public String DECORATOR_FACADE = "decoratorFacade";

    /**
      * Address book facade
      */
     public String ADDRESS_BOOK_FACADE = "addressBookFacade";

     /**
      * Customer service facade
      */
     public String CUSTOMER_SERVICE_FACADE = "customerServiceFacade";

     /**
      * Shipping service facade
      */
     public String SHIPPING_SERVICE_FACADE = "shippingServiceFacade";

     /**
      * Checkout service facade
      */
     public String CHECKOUT_SERVICE_FACADE = "checkoutServiceFacade";

     /**
      * Category service facade
      */
     public String CATEGORY_SERVICE_FACADE = "categoryServiceFacade";

     /**
      * Catalog service facade
      */
     public String CONTENT_SERVICE_FACADE = "contentServiceFacade";

     /**
      * Product service facade
      */
     public String PRODUCT_SERVICE_FACADE = "productServiceFacade";

}
