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
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.constants.Constants;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.AttrValue;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.entity.ShopCategory;
import org.yes.cart.domain.i18n.impl.FailoverStringI18NModel;
import org.yes.cart.service.domain.CategoryService;

import java.util.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class CategoryServiceImpl extends BaseGenericServiceImpl<Category> implements CategoryService {

    private final GenericDAO<Category, Long> categoryDao;

    private final GenericDAO<ShopCategory, Long> shopCategoryDao;

    private final GenericDAO<Shop, Long> shopDao;

    /**
     * Construct service to manage categories
     *
     * @param categoryDao     category dao to use
     * @param shopCategoryDao shop category dao to use
     * @param shopDao         shop dao
     */
    public CategoryServiceImpl(
            final GenericDAO<Category, Long> categoryDao,
            final GenericDAO<ShopCategory, Long> shopCategoryDao,
            final GenericDAO<Shop, Long> shopDao) {
        super(categoryDao);
        this.categoryDao = categoryDao;
        this.shopCategoryDao = shopCategoryDao;
        this.shopDao = shopDao;
    }

    /**
     * Get the top level categories assigned to shop.
     *
     * @param shopId given shop
     * @return ordered by rank list of assigned top level categories
     */
    @Cacheable(value = "categoryService-topLevelCategories"/*, key="shop.shopId"*/)
    public List<Category> getTopLevelCategories(final Long shopId) {
        return categoryDao.findByNamedQuery("TOPCATEGORIES.BY.SHOPID", shopId, new Date());
    }

    /**
     * {@inheritDoc}
     */
    public List<Category> findAllByShopId(final long shopId) {
        return categoryDao.findByNamedQuery("ALL.TOPCATEGORIES.BY.SHOPID", shopId);
    }


    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = {
            "categoryService-topLevelCategories",
            "shopService-shopByCode",
            "shopService-shopById",
            "shopService-shopByDomainName",
            "shopService-allShops",
            "shopService-shopCategories",
            "shopService-shopCategoriesIds"
    }, allEntries = true)
    public ShopCategory assignToShop(final long categoryId, final long shopId) {
        final ShopCategory shopCategory = shopCategoryDao.getEntityFactory().getByIface(ShopCategory.class);
        shopCategory.setCategory(categoryDao.findById(categoryId));
        shopCategory.setShop(shopDao.findById(shopId));
        return shopCategoryDao.create(shopCategory);
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = {
            "categoryService-topLevelCategories",
            "shopService-shopByCode",
            "shopService-shopById",
            "shopService-shopByDomainName",
            "shopService-allShops",
            "shopService-shopCategories",
            "shopService-shopCategoriesIds"
    }, allEntries = true)
    public void unassignFromShop(final long categoryId, final long shopId) {
        ShopCategory shopCategory = shopCategoryDao.findSingleByNamedQuery(
                "SHOP.CATEGORY",
                categoryId,
                shopId);
        shopCategoryDao.delete(shopCategory);

    }


    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "categoryService-rootCategory")
    public Category getRootCategory() {
        return categoryDao.findSingleByNamedQuery("ROOTCATEORY");
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "categoryService-categoryTemplateVariation"/*, key="category.categoryId"*/)
    public String getCategoryTemplateVariation(final Category category) {
        String variation = null;
        if (StringUtils.isBlank(category.getUitemplate())) {
            if (!category.isRoot()) {
                Category parentCategory =
                        proxy().findById(category.getParentId());
                variation = getCategoryTemplateVariation(parentCategory);
            }
        } else {
            variation = category.getUitemplate();
        }
        return variation;
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "categoryService-categoryTemplate")
    public String getCategoryTemplate(final long categoryId) {
        List<Object> count = categoryDao.findQueryObjectByNamedQuery("TEMPLATE.BY.CATEGORY.ID", categoryId);
        if (count != null && count.size() == 1) {
            final String template = (String) count.get(0);
            if (!StringUtils.isBlank(template)) {
                return template;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "categoryService-itemsPerPage"/*, key="category.getCategoryId()"*/)
    public List<String> getItemsPerPage(final Category category) {
        final List<String> rez;
        if (category == null) {
            rez = Constants.DEFAULT_ITEMS_ON_PAGE;
        } else {
            final String val = proxy().getCategoryAttributeRecursive(null, category, AttributeNamesKeys.Category.CATEGORY_ITEMS_PER_PAGE, null);
            if (val == null) {
                rez = Constants.DEFAULT_ITEMS_ON_PAGE;
            } else {
                rez = Arrays.asList(val.split(","));
            }
        }
        return rez;
    }

    /**
     * Get the value of given attribute. If value not present in given category
     * failover to parent category will be used.
     *
     *
     * @param locale        locale for localisable value (or null for raw)
     * @param category      given category
     * @param attributeName attribute name
     * @param defaultValue  default value will be returned if value not found in hierarchy
     * @return value of given attribute name or defaultValue if value not found in category hierarchy
     */
    @Cacheable(value = "categoryService-categoryAttributeRecursive")
    public String getCategoryAttributeRecursive(final String locale, final Category category, final String attributeName, final String defaultValue) {
        final String value = getCategoryAttributeRecursive(locale, category, attributeName);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    /**
     * Get the values of given attributes. If value not present in given category
     * failover to parent category will be used.  In case if attribute value for first
     * attribute will be found, the rest values also will be collected form the same category.
     *
     *
     * @param locale           locale for localisable value (or null for raw)
     * @param incategory       given category
     * @param attributeNames set of attributes, to collect values.
     * @return value of given attribute name or defaultValue if value not found in category hierarchy
     */
    @Cacheable(value = "categoryService-categoryAttributesRecursive")
    public String[] getCategoryAttributeRecursive(final String locale, final Category incategory, final String[] attributeNames) {
        final String[] rez;
        final Category category;
        
        if (incategory == null) {
            category = getRootCategory();
        } else {
            category = incategory;
        }

        final AttrValue attrValue = category.getAttributeByCode(attributeNames[0]);
        if (attrValue == null
                ||
                StringUtils.isBlank(attrValue.getVal())) {
            if (category.isRoot()) {
                rez = null; //root of hierarchy
            } else {
                final Category parentCategory =
                        proxy().findById(category.getParentId());
                rez = proxy().getCategoryAttributeRecursive(null, parentCategory, attributeNames);
            }
        } else {
            rez = new String[attributeNames.length];
            int idx = 0;
            for (String attrName : attributeNames) {
                final AttrValue av = category.getAttributeByCode(attrName);
                if (av != null) {
                    rez[idx] = av.getVal();
                } else {
                    rez[idx] = null;

                }
                idx ++;


            }
        }

        return rez;
    }

    /**
     * Get the value of given attribute. If value not present in given category failover to parent category will be used.
     *
     * @param locale        locale for localisable value (or null for raw)
     * @param category      given category
     * @param attributeName attribute name
     * @return value of given attribute name or null if value not found in category hierarchy
     */
    private String getCategoryAttributeRecursive(final String locale, final Category category, final String attributeName) {

        if (category == null || attributeName == null) {
            return null;
        }

        final AttrValue attrValue = category.getAttributeByCode(attributeName);
        if (attrValue != null) {
            final String val;
            if (locale == null) {
                val = attrValue.getVal();
            } else {
                val = new FailoverStringI18NModel(attrValue.getDisplayVal(), attrValue.getVal()).getValue(locale);
            }
            if (!StringUtils.isBlank(val)) {
                return val;
            }
        }

        if (category.isRoot()) {
            return null; //root of hierarchy
        }
        final Category parentCategory =
                proxy().findById(category.getParentId());
        return getCategoryAttributeRecursive(locale, parentCategory, attributeName);
    }


    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "categoryService-productQuantity")
    public int getProductQuantity(final long categoryId, final boolean includeChildren) {
        int qty = 0;
        List<Object> count = categoryDao.findQueryObjectByNamedQuery("CATEGORY.PRODUCT.COUNT", categoryId);
        if (count != null && count.size() == 1) {
            qty = ((Number) count.get(0)).intValue();
            if (includeChildren) {
                List<Category> childs = proxy().getChildCategories(categoryId);
                for (Category childCategory : childs) {
                    qty += proxy().getProductQuantity(childCategory.getCategoryId(), includeChildren);
                }

            }
        }
        return qty;
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "categoryService-categoryHasProducts")
    public boolean isCategoryHasProducts(final long categoryId, final boolean includeChildren) {
        List<Object> count = categoryDao.findQueryObjectByNamedQuery("CATEGORY.PRODUCT.COUNT", categoryId);
        if (count != null && count.size() == 1) {
            int qty = ((Number) count.get(0)).intValue();
            if (qty > 0) {
                return true;
            }
            if (includeChildren) {
                List<Category> childs = proxy().getChildCategories(categoryId);
                for (Category childCategory : childs) {
                    final boolean childHasProducts = proxy().isCategoryHasProducts(childCategory.getCategoryId(), includeChildren);
                    if (childHasProducts) {
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
    @Cacheable(value = "categoryService-categoryHasChildren")
    public boolean isCategoryHasChildren(final long categoryId, final boolean includeChildren) {
        List<Object> count = categoryDao.findQueryObjectByNamedQuery("CATEGORY.SUBCATEGORY.COUNT", categoryId);
        if (count != null && count.size() == 1) {
            int qty = ((Number) count.get(0)).intValue();
            if (qty > 0) {
                return true;
            }
            if (includeChildren) {
                List<Category> childs = proxy().getChildCategories(categoryId);
                for (Category childCategory : childs) {
                    final boolean childHasProducts = proxy().isCategoryHasChildren(childCategory.getCategoryId(), includeChildren);
                    if (childHasProducts) {
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
    @Cacheable(value = "categoryService-childCategories")
    public List<Category> getChildCategories(final long categoryId) {
        return findChildCategoriesWithAvailability(categoryId, true);
    }

    /**
     * {@inheritDoc}
     */
    public List<Category> findChildCategoriesWithAvailability(final long categoryId, final boolean withAvailability) {
        if (withAvailability) {
            return categoryDao.findByNamedQuery(
                    "CATEGORIES.BY.PARENTID",
                    categoryId,
                    new Date()
            );
        } else {
            return categoryDao.findByNamedQuery(
                    "CATEGORIES.BY.PARENTID.WITHOUT.DATE.FILTERING",
                    categoryId
            );
        }
    }


    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "categoryService-childCategoriesRecursive")
    public Set<Category> getChildCategoriesRecursive(final long categoryId) {
        final Category thisCat = proxy().findById(categoryId);
        if (thisCat != null) {
            final Set<Category> all = new HashSet<Category>();
            all.add(thisCat);
            loadChildCategoriesRecursiveInternal(all, thisCat);
            return all;
        }
        return Collections.emptySet();
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "categoryService-childCategoriesRecursiveIds")
    public Set<Long> getChildCategoriesRecursiveIds(final long categoryId) {
        final Set<Category> cats = getChildCategoriesRecursive(categoryId);
        if (cats.isEmpty()) {
            return Collections.emptySet();
        }
        return transform(cats);
    }

    private void loadChildCategoriesRecursiveInternal(final Set<Category> result, final Category category) {
        List<Category> categories = proxy().getChildCategories(category.getCategoryId());
        result.addAll(categories);
        for (Category subCategory : categories) {
            loadChildCategoriesRecursiveInternal(result, subCategory);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "categoryService-categoryHasSubcategory")
    public boolean isCategoryHasSubcategory(final long topCategoryId, final long subCategoryId) {
        final Category start = proxy().findById(subCategoryId);
        if (start != null) {
            if (subCategoryId == topCategoryId) {
                return true;
            } else {
                final List<Category> list = new ArrayList<Category>();
                list.add(start);
                addParent(list, topCategoryId);
                return list.get(list.size() - 1).getCategoryId() == topCategoryId;
            }
        }
        return false;
    }

    private void addParent(final List<Category> categoryChain, final long categoryIdStopAt) {
        final Category cat = categoryChain.get(categoryChain.size() - 1);
        if (!cat.isRoot()) {
            final Category parent = findById(cat.getParentId());
            if (parent != null) {
                categoryChain.add(parent);
                if (parent.getCategoryId() != categoryIdStopAt) {
                    addParent(categoryChain, categoryIdStopAt);
                }
            }
        }
    }

    /**
     * {@inheritDoc} Just to cache
     */
    @Cacheable(value = "categoryService-byId")
    public Category getById(final long pk) {
        return getGenericDao().findById(pk);
    }

    /**
     * {@inheritDoc}
     */
    public Set<Long> transform(final Collection<Category> categories) {
        final Set<Long> result = new LinkedHashSet<Long>(categories.size());
        for (Category category : categories) {
            result.add(category.getCategoryId());
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public Long findCategoryIdBySeoUri(final String seoUri) {
        List<Object> list = categoryDao.findQueryObjectByNamedQuery("CATEGORY.ID.BY.SEO.URI", seoUri);
        if (list != null && !list.isEmpty()) {
            final Object id = list.get(0);
            if (id instanceof Long) {
                return (Long) id;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String findSeoUriByCategoryId(final Long categoryId) {
        List<Object> list = categoryDao.findQueryObjectByNamedQuery("SEO.URI.BY.CATEGORY.ID", categoryId);
        if (list != null && !list.isEmpty()) {
            final Object uri = list.get(0);
            if (uri instanceof String) {
                return (String) uri;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public List<Category> findByProductId(final long productId) {
        return categoryDao.findByNamedQuery(
                "CATEGORIES.BY.PRODUCTID",
                productId
        );
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = {
            "categoryService-topLevelCategories",
            "categoryService-rootCategory",
            "categoryService-categoryHasChildren",
            "categoryService-childCategories",
            "categoryService-childCategoriesRecursive",
            "categoryService-childCategoriesRecursiveIds",
            "categoryService-categoryHasSubcategory",
            "categoryService-byId"
    }, allEntries = true)
    public Category create(Category instance) {
        return super.create(instance);
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = {
            "categoryService-topLevelCategories",
            "categoryService-rootCategory",
            "categoryService-categoryTemplateVariation",
            "categoryService-categoryTemplate",
            "categoryService-itemsPerPage",
            "categoryService-categoryAttributeRecursive",
            "categoryService-categoryAttributesRecursive",
            "categoryService-productQuantity",
            "categoryService-categoryHasProducts",
            "categoryService-categoryHasChildren",
            "categoryService-childCategories",
            "categoryService-childCategoriesRecursive",
            "categoryService-childCategoriesRecursiveIds",
            "categoryService-categoryHasSubcategory",
            "categoryService-byId"
    }, allEntries = true)
    public Category update(Category instance) {
        return super.update(instance);
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = {
            "categoryService-topLevelCategories",
            "categoryService-rootCategory",
            "categoryService-categoryTemplateVariation",
            "categoryService-categoryTemplate",
            "categoryService-itemsPerPage",
            "categoryService-categoryAttributeRecursive",
            "categoryService-categoryAttributesRecursive",
            "categoryService-productQuantity",
            "categoryService-categoryHasProducts",
            "categoryService-categoryHasChildren",
            "categoryService-childCategories",
            "categoryService-childCategoriesRecursive",
            "categoryService-childCategoriesRecursiveIds",
            "categoryService-categoryHasSubcategory",
            "categoryService-byId"
    }, allEntries = true)
    public void delete(Category instance) {
        super.delete(instance);
    }


    private CategoryService proxy;

    private CategoryService proxy() {
        if (proxy == null) {
            proxy = getSelf();
        }
        return proxy;
    }

    /**
     * @return self proxy to reuse AOP caching
     */
    public CategoryService getSelf() {
        // Spring lookup method to get self proxy
        return null;
    }

}
