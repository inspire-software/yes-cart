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

package org.yes.cart.service.domain.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.*;
import org.yes.cart.service.domain.AttributeService;
import org.yes.cart.service.domain.CategoryService;
import org.yes.cart.service.domain.ContentService;
import org.yes.cart.service.domain.ShopService;

import java.util.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class ShopServiceImpl extends BaseGenericServiceImpl<Shop> implements ShopService {

    private final GenericDAO<Shop, Long> shopDao;

    private final AttributeService attributeService;

    private final CategoryService categoryService;
    private final ContentService contentService;


    /**
     * Construct shop service.
     * @param shopDao shop doa.
     * @param categoryService {@link org.yes.cart.service.domain.CategoryService}
     * @param attributeService attribute service
     * @param contentService {@link org.yes.cart.service.domain.ContentService}
     */
    public ShopServiceImpl(final GenericDAO<Shop, Long> shopDao,
                           final CategoryService categoryService,
                           final ContentService contentService,
                           final AttributeService attributeService) {
        super(shopDao);
        this.shopDao = shopDao;
        this.categoryService = categoryService;
        this.attributeService = attributeService;
        this.contentService = contentService;
    }

    /**
     * {@inheritDoc}
     */
    public Shop getShopByOrderGuid(final String orderGuid) {
        return shopDao.findSingleByNamedQuery("SHOP.BY.ORDER.GUID", orderGuid);
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "shopService-shopByCode")
    public Shop getShopByCode(final String shopCode) {
        return shopDao.findSingleByNamedQuery("SHOP.BY.CODE", shopCode);
    }

    /**
     * {@inheritDoc}
     */
    public Shop getShopByOrderNum(final String orderNum) {
        return shopDao.findSingleByNamedQuery("SHOP.BY.ORDER.NUM", orderNum);
    }


    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "shopService-shopById")
    public Shop getById(final long shopId) {
        return shopDao.findById(shopId);
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "shopService-allShops")
    public List<Shop> getAll() {
        return super.findAll();
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "shopService-shopByDomainName")
    public Shop getShopByDomainName(final String serverName) {
        return shopDao.findSingleByNamedQuery("SHOP.BY.URL", serverName);
    }

    private Set<Category> getShopCategories(final long shopId) {
        Set<Category> result = new HashSet<Category>();
        for (ShopCategory category : shopDao.findById(shopId).getShopCategory()) {
            result.addAll(
                    categoryService.getChildCategoriesRecursive(category.getCategory().getCategoryId())
            );
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "shopService-shopCategoriesIds"/*, key ="shop.getShopId()"*/)
    public Set<Long> getShopCategoriesIds(final long shopId) {
        return transform(getShopCategories(shopId));
    }


    private Set<Category> getShopContent(final long shopId) {
        Set<Category> result = new HashSet<Category>();
        final Category root = contentService.getRootContent(shopId);
        if (root != null) {
            result.addAll(
                    contentService.getChildContentRecursive(root.getCategoryId())
            );
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "shopService-shopContentIds"/*, key ="shop.getShopId()"*/)
    public Set<Long> getShopContentIds(final long shopId) {
        return transform(getShopContent(shopId));
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "shopService-shopAllCategoriesIds"/*, key ="shop.getShopId()"*/)
    public Set<Long> getShopAllCategoriesIds(final long shopId) {
        final Set<Long> all = new HashSet<Long>();
        all.addAll(getShopCategoriesIds(shopId));
        all.addAll(getShopContentIds(shopId));
        final Category root = contentService.getRootContent(shopId);
        if (root != null) {
            all.add(root.getCategoryId());
        }
        return all;
    }

    public Set<Long> transform(final Collection<Category> categories) {
        final Set<Long> result = new LinkedHashSet<Long>(categories.size());
        for (Category category : categories) {
            result.add(category.getCategoryId());
        }
        return result;
    }


    /** {@inheritDoc} */
    public Collection<String> findAllSupportedCurrenciesByShops() {
        final List<Shop> shops = shopDao.findAll();
        final Set<String> currencies = new TreeSet<String>();

        for(Shop shop : shops) {
            final String shopCurrencies = shop.getSupportedCurrencies();
            if (StringUtils.isNotBlank(shopCurrencies)) {
                currencies.addAll(Arrays.asList(shopCurrencies.split(",")));
            }
        }

        return currencies;
    }




    /**
     * Set attribute value. New attribute value will be created, if attribute has not value for given shop.
     * TODO makes sense to move it into abstract generic
     * @param shopId shop id
     * @param attributeKey attribute key
     * @param attributeValue attribute value.
     */
    @CacheEvict(value ={
            "shopService-shopByCode",
            "shopService-shopById",
            "shopService-shopByDomainName",
            "shopService-allShops",
            "shopService-shopWarehouses",
            "shopService-shopWarehousesIds"
    }, allEntries = true)
    public void updateAttributeValue(final long shopId, final String attributeKey, final String attributeValue) {
        final Shop shop = shopDao.findById(shopId);
        if (shop != null) {
            AttrValueShop attrValueShop = shop.getAttributeByCode(attributeKey);
            if (attrValueShop == null) {
                final Attribute attribute = attributeService.findByAttributeCode(attributeKey);
                attrValueShop = getGenericDao().getEntityFactory().getByIface(AttrValueShop.class);
                attrValueShop.setVal(attributeValue);
                attrValueShop.setAttribute(attribute);
                attrValueShop.setShop(shop);
                shop.getAttributes().add(attrValueShop);
            } else {
                attrValueShop.setVal(attributeValue);
            }
            shopDao.update(shop);
        }
    }

    /** {@inheritDoc} */
    @CacheEvict(value ={
            "shopService-shopCategoriesIds",
            "shopService-shopContentIds",
            "shopService-shopAllCategoriesIds"
    }, allEntries = true)
    public Shop create(final Shop instance) {
        final Shop shop = super.create(instance);
        final Category category = contentService.getRootContent(shop.getShopId());
        if (category == null) {
            contentService.createRootContent(shop.getShopId());
        }
        return shop;
    }

    /** {@inheritDoc} */
    @CacheEvict(value ={
            "shopService-shopByCode",
            "shopService-shopById",
            "shopService-shopByDomainName",
            "shopService-allShops",
            "shopService-shopWarehouses",
            "shopService-shopWarehousesIds",
            "themeService-themeChainByShopId",
            "themeService-markupChainByShopId",
            "themeService-mailTemplateChainByShopId"
    }, allEntries = true)
    public Shop update(Shop instance) {
        return super.update(instance);
    }

    /** {@inheritDoc} */
    @CacheEvict(value ={
            "shopService-shopByCode",
            "shopService-shopById",
            "shopService-shopByDomainName",
            "shopService-allShops",
            "shopService-shopCategoriesIds",
            "shopService-shopContentIds",
            "shopService-shopAllCategoriesIds",
            "shopService-shopWarehouses",
            "shopService-shopWarehousesIds",
            "themeService-themeChainByShopId",
            "themeService-markupChainByShopId",
            "themeService-mailTemplateChainByShopId"
    }, allEntries = true)
    public void delete(Shop instance) {
        super.delete(instance);
    }
}
