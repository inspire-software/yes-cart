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
     * Language service.
     */
    public String LANGUAGE_SERVICE = "languageService";

    /**
     * Service to resolve central view from request parameters.
     */
    public String CENTRAL_VIEW_RESOLVER = "centralViewResolver";

    /**
     * Service to resolve theme visitors.
     */
    public String COMPONENT_THEME_VISITOR_FACTORY = "componentThemeVisitorFactory";

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
     public String ATTRIBUTABLE_IMAGE_SERVICE = "attributableImageService";

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

}
