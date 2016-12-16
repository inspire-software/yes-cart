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

package org.yes.cart.cluster.service.impl;

import org.yes.cart.cluster.node.Message;
import org.yes.cart.cluster.node.MessageListener;
import org.yes.cart.cluster.node.NodeService;
import org.yes.cart.domain.entity.Country;
import org.yes.cart.domain.entity.ProductType;
import org.yes.cart.domain.entity.Promotion;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.promotion.PromotionConditionParser;
import org.yes.cart.service.domain.*;
import org.yes.cart.service.misc.LanguageService;
import org.yes.cart.cluster.service.WarmUpService;
import org.yes.cart.web.support.service.AddressBookFacade;

import java.io.Serializable;
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
    private final AttributeService attributeService;

    private final ShopService shopService;

    private final AddressBookFacade addressBookFacade;

    private final PromotionService promotionService;
    private final PromotionConditionParser promotionConditionParser;

    public WarmUpServiceImpl(final LanguageService languageService,
                             final ProductTypeService productTypeService,
                             final ProductTypeAttrService productTypeAttrService,
                             final ProductService productService,
                             final AttributeService attributeService,
                             final ShopService shopService,
                             final AddressBookFacade addressBookFacade,
                             final PromotionService promotionService,
                             final PromotionConditionParser promotionConditionParser) {
        this.languageService = languageService;
        this.productTypeService = productTypeService;
        this.productTypeAttrService = productTypeAttrService;
        this.productService = productService;
        this.attributeService = attributeService;
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
            // Preload all shipping countries
            final List<Country> countriesShip = addressBookFacade.getAllCountries(shop.getCode(), "S");
            for (final Country country : countriesShip) {
                // Preload states by country
                addressBookFacade.getStatesByCountry(country.getCountryCode());
            }
            // Preload all billing countries
            final List<Country> countriesBill = addressBookFacade.getAllCountries(shop.getCode(), "B");
            for (final Country country : countriesBill) {
                // Preload states by country
                addressBookFacade.getStatesByCountry(country.getCountryCode());
            }
        }

    }

    private void loadProductData() {

        // Load all attribute data
        attributeService.getAllNavigatableAttributeCodes();
        attributeService.getAllSearchableAttributeCodes();
        attributeService.getAllSearchablePrimaryAttributeCodes();
        attributeService.getAllStorableAttributeCodes();
        attributeService.getAllAttributeCodes();
        attributeService.getAllAttributeNames();

        final List<String> supportedLanguages = languageService.getSupportedLanguages();
        final List<ProductType> navigatableTypes = productTypeService.findAllAssignedToCategories();
        for (final ProductType type : navigatableTypes) {
            for (final String language : supportedLanguages) {
                // This is used for Filtered navigation by attributes
                productService.getDistinctAttributeValues(language, type.getProducttypeId());
            }
        }
    }

    private void loadShopData() {
        final List<Shop> shops = shopService.getAll();
        for (final Shop shop : shops) {
            // Used in SKU commands
            final Shop cachedShop = shopService.getById(shop.getShopId());
            // Used for navigation and determination of current category belonging to current shop
            shopService.getShopCategoriesIds(cachedShop.getShopId());
            // Used for navigation and determination of current content belonging to current shop
            shopService.getShopContentIds(cachedShop.getShopId());
            // Used for navigation and determination of current category or content belonging to current shop
            shopService.getShopAllCategoriesIds(cachedShop.getShopId());
        }
    }

    /**
     * Spring IoC.
     *
     * @param nodeService node service
     */
    public void setNodeService(final NodeService nodeService) {

        nodeService.subscribe("WarmUpService.warmUp", new MessageListener() {
            @Override
            public Serializable onMessageReceived(final Message message) {
                WarmUpServiceImpl.this.warmUp();
                return "OK";
            }
        });

    }

}