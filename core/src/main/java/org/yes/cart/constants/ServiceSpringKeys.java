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

package org.yes.cart.constants;

import org.yes.cart.service.media.impl.BrandMediaFileNameStrategyImpl;
import org.yes.cart.service.media.impl.CategoryMediaFileNameStrategyImpl;
import org.yes.cart.service.media.impl.ProductMediaFileNameStrategyImpl;
import org.yes.cart.service.media.impl.ShopMediaFileNameStrategyImpl;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
@SuppressWarnings("PMD.AvoidConstantsInterface")
public interface ServiceSpringKeys {

    /* ######### DOMAIN ####################################### */

    /**
     * {@link org.yes.cart.service.domain.CategoryService} bean key.
     */
    String CATEGORY_SERVICE = "categoryService";
    /**
     * {@link org.yes.cart.service.domain.ContentService} bean key.
     */
    String CONTENT_SERVICE = "contentService";
    /**
     * {@link org.yes.cart.service.domain.ShopService} bean key.
     */
    String SHOP_SERVICE = "shopService";
    /**
     * {@link org.yes.cart.service.domain.ShoppingCartStateService} bean key.
     */
    String SHOPPING_CART_STATE_SERVICE = "shoppingCartStateService";

    /**
     * {@link org.yes.cart.service.domain.ShopUrlService} bean key.
     */
    String SHOP_URL_SERVICE = "shopUrlService";
    /**
     * {@link org.yes.cart.service.domain.SystemService} bean key.
     */
    String SYSTEM_SERVICE = "systemService";
    /**
     * {@link org.yes.cart.service.domain.ProductService} bean key.
     */
    String PRODUCT_SERVICE = "productService";
    /**
     * {@link org.yes.cart.service.domain.ProductSkuService} bean key.
     */
    String PRODUCT_SKU_SERVICE = "productSkuService";
    /**
     * {@link org.yes.cart.service.domain.AddressService} bean key.
     */
    String ADDRESS_SERVICE = "addressService";
    /**
     * {@link org.yes.cart.service.domain.CustomerWishListService} bean key.
     */
    String CUSTOMER_WISH_LIST_SERVICE = "customerWishListService";

    /**
     * {@link org.yes.cart.service.domain.AttributeService} bean key.
     */
    String ATTRIBUTE_SERVICE = "attributeService";

    /* ######### IMAGES BEGIN ####################################### */
    /**
     * {@link org.yes.cart.service.domain.ImageService} bean key.
     */
    String IMAGE_SERVICE = "imageService";

    /**
     * {@link BrandMediaFileNameStrategyImpl} bean key.
     */
    String BRAND_IMAGE_NAME_STRATEGY = "brandImageNameStrategy";

    /**
     * {@link ShopMediaFileNameStrategyImpl} bean key.
     */
    String SHOP_IMAGE_NAME_STRATEGY = "shopImageNameStrategy";

    /**
     * {@link CategoryMediaFileNameStrategyImpl} bean key.
     */
    String CATEGORY_IMAGE_NAME_STRATEGY = "categoryImageNameStrategy";

    /**
     * {@link ProductMediaFileNameStrategyImpl} bean key.
     */
    String PRODUCT_IMAGE_NAME_STRATEGY = "productImageNameStrategy";

    /* ######### IMAGES END ####################################### */

    /**
     * {@link org.yes.cart.service.domain.ProductAssociationService} bean key.
     */
    String PRODUCT_ASSOCIATIONS_SERVICE = "productAssociationService";

    /**
     * {@link org.yes.cart.service.domain.PriceService} bean key.
     */
    String PRICE_SERVICE = "priceService";


    String USER_MANAGMENT_SERVICE = "userManagementService";

    /**
     * {@link org.yes.cart.service.domain.AttributeGroupService} bean key.
     */
    String ATTRIBUTE_GROUP_SERVICE = "attributeGroupService";

    /**
     * {@link org.yes.cart.service.domain.EtypeService} bean key.
     */
    String ETYPE_SERVICE = "etypeService";

    /**
     * {@link org.yes.cart.service.domain.ShopCategoryService} bean key.
     */
    String SHOP_CATEGORY_SERVICE = "shopCategoryService";

   /**
     * {@link org.yes.cart.service.domain.ShopTopSellerService} bean key.
     */
    String SHOP_TOP_SELLER_SERVICE = "shopTopSellerService";

    /**
     * {@link org.yes.cart.service.domain.BrandService} bean key.
     */
    String BRAND_SERVICE = "brandService";

    /**
     * {@link org.yes.cart.service.domain.WarehouseService} bean key.
     */
    String WAREHOUSE_SERVICE = "warehouseService";

    /**
     * {@link org.yes.cart.service.domain.ProductTypeService} bean key.
     */
    String PRODUCT_TYPE_SERVICE = "productTypeService";

    /**
     * {@link org.yes.cart.service.domain.ProductTypeAttrService} bean key.
     */
    String PRODUCT_TYPE_ATTR_SERVICE = "productTypeAttrService";

    /**
     * {@link org.yes.cart.service.domain.ProductCategoryService} bean key.
     */
    String PRODUCT_CATEGORY_SERVICE = "productCategoryService";

    /**
     * {@link org.yes.cart.service.domain.CustomerOrderService} bean key.
     */
    String CUSTOMER_ORDER_SERVICE = "customerOrderService";

    /**
     * {@link org.yes.cart.service.domain.CustomerService} bean key.
     */
    String CUSTOMER_SERVICE = "customerService";

    /**
     * {@link org.yes.cart.service.domain.CountryService} bean key.
     */
    String COUNTRY_SERVICE = "countryService";

    /**
     * {@link org.yes.cart.service.domain.StateService} bean key.
     */
    String STATE_SERVICE = "stateService";

    /**
     * {@link org.yes.cart.service.domain.CarrierService} bean key.
     */
    String CARRIER_SERVICE = "carrierService";

    /**
     * {@link org.yes.cart.service.domain.CarrierSlaService} bean key.
     */
    String CARRIER_SLA_SERVICE = "carrierSlaService";

    /**
     * {@link org.yes.cart.service.domain.SkuWarehouseService} bean key.
     */
    String SKU_WAREHOUSE_SERVICE = "skuWarehouseService";

    /**
     * {@link org.yes.cart.service.domain.AssociationService} bean key.
     */
    String ASSOCIATION_SERVICE = "associationService";

    /**
     * {@link org.yes.cart.service.domain.ManagerService} bean key.
     */
    String MANAGER_SERVICE = "managerService";





    /* ######### UTILS ####################################### */

    /**
     * {@link org.yes.cart.domain.query.impl.PriceNavigationImpl} bean key.
     */
    String PRICE_NAVIGATION = "priceNavigation";

    /**
     * {@link org.yes.cart.domain.query.impl.LuceneQueryFactoryImpl} bean key.
     */
    String LUCENE_QUERY_FACTORY = "luceneQueryFactory";

    /**
     * {@link org.yes.cart.utils.impl.ExtendedConversionService} bean key
     */
    String CONVERSION_SERVICE = "extendedConversionService";


    /* ######### WEB SUPPORT ####################################### */


    /**
     * {@link org.yes.cart.service.mail.MailComposer}
     */
    String MAIL_COMPOSER_SERVICE = "mailComposer";


    /**
     * Shopping cart command factory.
     */
    String CART_COMMAND_FACTORY = "shoppingCartCommandFactory";

    /**
     * Order assembler.
     */
    String ORDER_ASSEMBLER = "orderAssembler";

    /**
     * Delivery assembler.
     */
    String DELIVERY_ASSEMBLER = "deliveryAssembler";

    /**
     * Order assembler.
     */
    String ORDER_DISASSEMBLER = "orderDisassembler";

    /**
     * Order number generator .
     */
    String ORDER_NUMBER_GENERATOR = "orderNumberGenerator";

    /**
     * Service locator.
     */
    String SERVICE_LOCATOR = "serviceLocator";

    /**
     * Payment modules manager.
     */
    String PAYMENT_MODULES_MANAGER = "paymentModulesManager";

    /**
     * Payment process facade. Used of payment filter.
     */
    String PAYMENT_PROCESS_FACADE = "paymentProcessFacade";

    /**
     * Payment processor factory.
     */
    String PAYMENT_PROCESSOR_FACTORY = "paymentProcessorFactory";

    /**
     * Prototype payment processor.
     */
    String PAYMENT_CALLBACK_HANDLER = "paymentCallBackHandlerFacade";

    /**
     * Prototype payment processor.
     */
    String ORDER_PAYMENT_SERICE = "customerOrderPaymentService";

    /**
     * Order and delivery state manager.
     */
    String ORDER_STATE_MANAGER = "orderStateManager";


    /**
     * Plural form service bean id.
     */
    String PLURAL_FORM_SERVICE = "pluralFormService";


    /**
     * Password generator.
     */
    String PASSPHRAZE_GENERATOR = "passPhraseGenerator";

   /**
     * Hash creator.
     */
    String HASH_HELPER = "passwordHashHelper";

    /**
     * Language service.
     */
    String LANGUAGE_SERVICE = "languageService";
    /**
     * Strategy for determining if product is purchasable
     */
    String PRODUCT_AVAILABILITY_STRATEGY = "productAvailabilityStrategy";
    /**
      * Strategy for determining valid orderable quantity of product
      */
    String PRODUCT_QUANTITY_STRATEGY = "productQuantityStrategy";
}
