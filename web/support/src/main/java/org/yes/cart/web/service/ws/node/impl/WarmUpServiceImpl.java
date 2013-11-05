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

package org.yes.cart.web.service.ws.node.impl;

import org.yes.cart.domain.entity.Country;
import org.yes.cart.domain.entity.ProductType;
import org.yes.cart.domain.entity.Promotion;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.promotion.PromotionConditionParser;
import org.yes.cart.service.domain.*;
import org.yes.cart.service.misc.LanguageService;
import org.yes.cart.web.service.ws.node.WarmUpService;
import org.yes.cart.web.support.service.AddressBookFacade;

import java.util.List;

/**
 * User: denispavlov
 * Date: 13-10-17
 * Time: 7:01 PM
 */
public class WarmUpServiceImpl implements WarmUpService {

    private final LanguageService languageService;

    private final ProductTypeService productTypeService;
    private final ProductTypeAttrService productTypeAttrService;
    private final ProductService productService;

    private final ShopService shopService;

    private final AddressBookFacade addressBookFacade;

    private final PromotionService promotionService;
    private final PromotionConditionParser promotionConditionParser;

    public WarmUpServiceImpl(final LanguageService languageService,
                             final ProductTypeService productTypeService,
                             final ProductTypeAttrService productTypeAttrService,
                             final ProductService productService,
                             final ShopService shopService,
                             final AddressBookFacade addressBookFacade,
                             final PromotionService promotionService,
                             final PromotionConditionParser promotionConditionParser) {
        this.languageService = languageService;
        this.productTypeService = productTypeService;
        this.productTypeAttrService = productTypeAttrService;
        this.productService = productService;
        this.shopService = shopService;
        this.addressBookFacade = addressBookFacade;
        this.promotionService = promotionService;
        this.promotionConditionParser = promotionConditionParser;
    }

    /** {@inheritDoc} */
    @Override
    public void warmUp() {
        loadShopData();
        loadProductData();
        loadAddressData();
        loadPromotionConditions();
    }

    private void loadPromotionConditions() {

        final List<Promotion> promotions = promotionService.findAll();
        for (final Promotion promotion : promotions) {
            promotionConditionParser.parse(promotion); // parse all groovy conditions
        }

    }

    private void loadAddressData() {
        final List<Shop> shops = shopService.findAll();
        for (final Shop shop : shops) {
            // Preload all countries
            final List<Country> countries = addressBookFacade.findAllCountries(shop.getCode());
            for (final Country country : countries) {
                // Preload states by country
                addressBookFacade.findStatesByCountry(country.getCountryCode());
            }
        }

    }

    private void loadProductData() {
        final List<String> supportedLanguages = languageService.getSupportedLanguages();
        final List<ProductType> types = productTypeService.findAll();
        for (final ProductType type : types) {
            // This is used by SKU attribute view
            productTypeAttrService.getViewGroupsByProductTypeId(type.getProducttypeId());
            for (final String language : supportedLanguages) {
                // This is used for Filtered navigation by attributes
                productService.getDistinctAttributeValues(language, type.getProducttypeId());
            }
        }
    }

    private void loadShopData() {
        final List<Shop> shops = shopService.findAll();
        for (final Shop shop : shops) {
            // Used in SKU commands
            final Shop cachedShop = shopService.findById(shop.getShopId());
            // Used for navigation and determination of current category belonging to current shop
            shopService.getShopCategories(cachedShop.getShopId());
            shopService.getShopCategoriesIds(cachedShop.getShopId());
        }
    }
}
