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

/**
 * User: denispavlov
 * Date: 02/06/2015
 * Time: 20:23
 */
@SuppressWarnings("PMD.AvoidConstantsInterface")
public interface DtoServiceSpringKeys {
    /**
     * {@link org.yes.cart.domain.dto.factory.DtoFactory} bean key.
     */
    String DTO_FACTORY = "dtoInterfaceToClassFactory";
    /**
     * {@link org.yes.cart.service.dto.DtoAttributeService}
     */
    String DTO_ATTRIBUTE_SERVICE = "dtoAttributeService";
    /**
     * {@link org.yes.cart.service.dto.DtoAttributeGroupService}
     */
    String DTO_ATTRIBUTE_GROUP_SERVICE = "dtoAttributeGroupService";
    /**
     * {@link org.yes.cart.service.dto.DtoEtypeService}
     */
    String DTO_ETYPE_SERVICE = "dtoEtypeService";
    /**
     * {@link org.yes.cart.service.dto.DtoBrandService}
     */
    String DTO_BRAND_SERVICE = "dtoBrandService";
    /**
     * {@link org.yes.cart.service.dto.DtoWarehouseService}
     */
    String DTO_WAREHOUSE_SERVICE = "dtoWarehouseService";
    /**
     * {@link org.yes.cart.service.dto.DtoCategoryService}
     */
    String DTO_CATEGORY_SERVICE = "dtoCategoryService";
    /**
     * {@link org.yes.cart.service.dto.DtoContentService}
     */
    String DTO_CONTENT_SERVICE = "dtoContentService";
    /**
     * {@link org.yes.cart.service.dto.DtoProductService}
     */
    String DTO_PRODUCT_SERVICE = "dtoProductService";
    /**
     * {@link org.yes.cart.service.dto.DtoProductService}
     */
    String DTO_PRODUCT_SKU_SERVICE = "dtoProductSkuService";
    /**
     * {@link org.yes.cart.service.dto.DtoProductTypeService}
     */
    String DTO_PRODUCT_TYPE_SERVICE = "dtoProductTypeService";
    /**
     * {@link org.yes.cart.service.dto.DtoProductTypeAttrService}
     */
    String DTO_PRODUCT_TYPE_ATTR_SERVICE = "dtoProductTypeAttrService";
    /**
     * {@link org.yes.cart.service.dto.DtoImageService}
     */
    String DTO_IMAGE_SERVICE = "dtoImageService";
    /**
     * {@link org.yes.cart.service.dto.DtoAssociationService}
     */
    String DTO_ASSOCIATION_SERVICE = "dtoAssociationService";
    /**
     * {@link org.yes.cart.service.dto.DtoProductAssociationService}
     */
    String DTO_PRODUCT_ASSOCIATION_SERVICE = "dtoProductAssociationService";
    /**
     * {@link org.yes.cart.service.dto.DtoCustomerService}
     */
    String DTO_CUSTOMER_SERVICE = "dtoCustomerService";
    /**
     * {@link org.yes.cart.service.dto.DtoCarrierService} bean key.
     */
    String DTO_CARRIER_SERVICE = "dtoCarrierService";
    /**
     * {@link org.yes.cart.service.dto.DtoCarrierSlaService} bean key.
     */
    String DTO_CARRIER_SLA_SERVICE = "dtoCarrierSlaService";
    /**
     * {@link org.yes.cart.service.dto.DtoCustomerOrderService} bean key.
     */
    String DTO_CUSTOMER_ORDER_SERVICE = "dtoCustomerOrderService";
    /**
     * {@link org.yes.cart.service.dto.DtoShopService} bean key.
     */
    String DTO_SHOP_SERVICE = "dtoShopService";
    /**
     * Product type view group
     */
    String DTO_PRODUCT_TYPE_AV_SERVICE = "dtoProdTypeAttributeViewGroupService";
    /**
     * {@link org.yes.cart.service.dto.DtoPromotionService}
     */
    String DTO_PROMOTION_SERVICE = "dtoPromotionService";
    /**
     * {@link org.yes.cart.service.dto.DtoPromotionCouponService}
     */
    String DTO_PROMOTION_COUPON_SERVICE = "dtoPromotionCouponService";
    /**
     * {@link org.yes.cart.service.dto.DtoTaxService}
     */
    String DTO_TAX_SERVICE = "dtoTaxService";
    /**
     * {@link org.yes.cart.service.dto.DtoTaxConfigService}
     */
    String DTO_TAX_CONFIG_SERVICE = "dtoTaxConfigService";
    /**
     * {@link org.yes.cart.service.dto.DtoShoppingCartService} bean key.
     */
    String DTO_SHOPPINGCART_SERVICE = "dtoShoppingCartService";

}
