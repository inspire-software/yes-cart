/*
 * Copyright 2009 Inspire-Software.com
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

package org.yes.cart.promotion.impl;

import org.apache.commons.lang.StringUtils;
import org.yes.cart.domain.entity.*;
import org.yes.cart.promotion.PromotionConditionSupport;
import org.yes.cart.service.domain.*;

import java.util.List;

/**
 * User: denispavlov
 * Date: 28/02/2018
 * Time: 21:45
 */
public class PromotionConditionSupportImpl implements PromotionConditionSupport {

    private final ProductService productService;
    private final BrandService brandService;
    private final ShopService shopService;
    private final CategoryService categoryService;
    private final ProductCategoryService productCategoryService;

    public PromotionConditionSupportImpl(final ProductService productService,
                                         final BrandService brandService,
                                         final ShopService shopService,
                                         final CategoryService categoryService,
                                         final ProductCategoryService productCategoryService) {
        this.productService = productService;
        this.brandService = brandService;
        this.shopService = shopService;
        this.categoryService = categoryService;
        this.productCategoryService = productCategoryService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Product getProductBySkuCode(final String sku) {
        if (StringUtils.isNotEmpty(sku)) {
            final Product product = productService.getProductBySkuCode(sku);
            if (product != null) {
                return productService.getProductById(product.getProductId(), true);
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProductSku getProductSkuByCode(final String sku) {
        if (StringUtils.isNotEmpty(sku)) {
            return productService.getProductSkuByCode(sku);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Brand getProductBrand(final String sku) {
        if (StringUtils.isNotEmpty(sku)) {
            final Product product = productService.getProductBySkuCode(sku);
            if (product != null) {
                return brandService.getById(product.getBrand().getBrandId());
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasProductAttribute(final String sku, final String attribute) {
        if (StringUtils.isNotEmpty(sku) && StringUtils.isNotEmpty(attribute)) {
            final ProductSku productSku = getProductSkuByCode(sku);
            if (productSku != null) {
                if (StringUtils.isNotEmpty(productSku.getAttributeValueByCode(attribute))) {
                    return true;
                }
                final Product product = getProductBySkuCode(sku);
                if (product != null) {
                    return StringUtils.isNotEmpty(product.getAttributeValueByCode(attribute));
                }
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getProductAttribute(final String sku, final String attribute) {
        if (StringUtils.isNotEmpty(sku) && StringUtils.isNotEmpty(attribute)) {
            final ProductSku productSku = getProductSkuByCode(sku);
            if (productSku != null) {
                final String avSku = productSku.getAttributeValueByCode(attribute);
                if (StringUtils.isNotEmpty(avSku)) {
                    return avSku;
                }
                final Product product = getProductBySkuCode(sku);
                if (product != null) {
                    return product.getAttributeValueByCode(attribute);
                }
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isProductOfBrand(final String sku, final String... brandNames) {
        if (StringUtils.isNotEmpty(sku)) {
            final Brand brand = getProductBrand(sku);
            if (brand != null) {
                for (final String brandName : brandNames) {
                    if (brandName.equalsIgnoreCase(brand.getName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isProductInCategory(final String sku, final Long shopId, final String... categoryGUIDs) {
        if (StringUtils.isNotEmpty(sku) && shopId != null) {
            final Product product = productService.getProductBySkuCode(sku);
            if (product != null) {
                final List<Long> pcats = productCategoryService.getByProductId(product.getProductId());
                for (final Long categoryId : pcats) {
                    if (isCategoryOneOf(categoryId, shopId, categoryGUIDs)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isCategoryOneOf(final long catId, final long shopId, final String... categoryGUIDs) {
        if (shopService.getShopCategoriesIds(shopId).contains(catId)) {
            final Category category = this.categoryService.getById(catId);
            for (final String catGUID : categoryGUIDs) {
                if (catGUID.equals(category.getGuid())) {
                    return true;
                }
            }
            final Long parentId = shopService.getShopCategoryParentId(shopId, category.getCategoryId(), true);
            if (parentId != null) {
                return isCategoryOneOf(parentId, shopId, categoryGUIDs);
            }
        }
        return false;
    }
}
