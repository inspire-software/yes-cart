package org.yes.cart.service.domain.impl;

import org.apache.commons.lang.StringUtils;
import org.yes.cart.cache.Cacheable;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.constants.Constants;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.entity.ShopCategory;
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
     * @param categoryDao category dao to use
     * @param shopCategoryDao shop category dao to use
     * @param shopDao shop dao
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
     * @param shop given shop
     * @return ordered by rank list of assigned top level categories
     */
    @Cacheable(value = "categoryServiceImplMethodCache")
    public List<Category> getTopLevelCategories(final Shop shop) {
        return categoryDao.findByNamedQuery("TOPCATEGORIES.BY.SHOPID", shop.getShopId(), new Date());
    }

    /**
     * {@inheritDoc}
     */
    public List<Category> getAllByShopId(final long shopId) {
        return categoryDao.findByNamedQuery("ALL.TOPCATEGORIES.BY.SHOPID", shopId);        
    }


    /**
     * {@inheritDoc}
     */
    public ShopCategory assignToShop(final long categoryId, final long shopId) {
        final ShopCategory shopCategory = shopCategoryDao.getEntityFactory().getByIface(ShopCategory.class);
        shopCategory.setCategory(categoryDao.findById(categoryId));
        shopCategory.setShop(shopDao.findById(shopId));
        return shopCategoryDao.create(shopCategory);
    }

    /**
     * {@inheritDoc}
     */
    public void unassignFromShop(long categoryId, long shopId) {
        ShopCategory shopCategory = shopCategoryDao.findSingleByNamedQuery(
                "SHOP.CATEGORY",
                categoryId,
                shopId);
        shopCategoryDao.delete(shopCategory);

    }


    /**
     * {@inheritDoc}
     */
    public Category getRootCategory() {
        return categoryDao.findSingleByNamedQuery("ROOTCATEORY");        
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "categoryServiceImplMethodCache")
    public String getCategoryTemplateVariation(final Category category) {
        String variation = null;
        if (StringUtils.isBlank(category.getUitemplate())) {
            if (category.getParentId() != category.getCategoryId()) {
                Category parentCategory =
                        categoryDao.findById(category.getParentId());
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
    @Cacheable(value = "categoryServiceImplMethodCache")
    public List<String> getItemsPerPage(final long categoryId) {
        return getItemsPerPage(
                categoryDao.findById(categoryId)
        );
    }


    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "categoryServiceImplMethodCache")
    public List<String> getItemsPerPage(final Category category) {
        String attributeName = AttributeNamesKeys.CATEGORY_ITEMS_PER_PAGE;
        final String val = getCategoryAttributeRecursive(category, attributeName);
        if (val == null) {
            return  Constants.DEFAULT_ITEMS_ON_PAGE;
        } else {
            return  Arrays.asList(val.split(","));
        }
    }

    /**
     * Get the value of given attribute. If value not present in given category
     * failover to parent category will be used.
     * @param category given category
     * @param attributeName attribute name
     * @param defaultValue default value will be returned if value not found in hierarcht
     * @return value of given attibute name or defaultValue if value not found in category hierarchy
     */
    public String getCategoryAttributeRecursive(final Category category,  final String attributeName, final String defaultValue) {
        final String value = getCategoryAttributeRecursive(category, attributeName);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    /**
     * Get the value of given attribute. If value not present in given category failover to parent category will be used.
     * @param category given category
     * @param attributeName attribute name
     * @return value of given attibute name or null if value not found in category hierarchy
     */
    @Cacheable(value = "categoryServiceImplMethodCache")
    public String getCategoryAttributeRecursive(final Category category, final String attributeName) {
        if (category.getAttributeByCode(attributeName) == null
                ||
                StringUtils.isBlank(category.getAttributeByCode(attributeName).getVal())) {
            if (category.getCategoryId() == category.getParentId()) {
                return null; //root of hierarchy
            }
            final Category parentCategory =
                    categoryDao.findById(category.getParentId());
            return getCategoryAttributeRecursive(parentCategory, attributeName);
        } else {
            return category.getAttributeByCode(attributeName).getVal();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "categoryServiceImplMethodCache")
    public String getCategoryTemplateVariation(final long categoryId) {
        return getCategoryTemplateVariation(
                categoryDao.findById(categoryId)
        );
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "categoryServiceImplMethodCache")
    public int getProductQuantity(final long categoryId, final boolean includeChild) {
        int qty = 0;
        Category category = categoryDao.findById(categoryId);
        if (category != null) {
            qty = category.getProductCategory().size();
            if (includeChild) {
                List<Category> childs = getChildCategories(categoryId);
                for (Category childCategory : childs) {
                    qty += getProductQuantity(childCategory.getCategoryId(), includeChild);
                }

            }
        }
        return qty;
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "categoryServiceImplMethodCache")
    public List<Category> getChildCategories(final long categoryId) {
        return getChildCategoriesWithAvailability(categoryId, true);
    }

    /**
     * {@inheritDoc}
     */
    public List<Category> getChildCategoriesWithAvailability(long categoryId, boolean withAvailability) {
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
    @Cacheable(value = "categoryServiceImplMethodCache")
    public Set<Category> getChildCategoriesRecursive(final long categoryId) {
        Set<Category> result = new HashSet<Category>();
        List<Category> categories = getChildCategories(categoryId);
        result.addAll(categories);
        for (Category category : categories) {
            if (category.getCategoryId() != category.getParentId()) {
                result.addAll(getChildCategoriesRecursive(category.getCategoryId()));
            }
        }
        result.add(getById(categoryId));
        return result;
    }

    /** {@inheritDoc} Just to cache*/
    @Cacheable(value = "categoryServiceImplMethodCache")
    public Category getById(final long pk) {
        return getGenericDao().findById(pk);
    }

    /**
     * {@inheritDoc}
     */
    public List<Long> transform(final Collection<Category> categories) {
        List<Long> result = new ArrayList<Long>(categories.size());
        for (Category category : categories) {
            result.add(category.getCategoryId());
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "categoryServiceImplMethodCache")
    public Long getCategoryIdBySeoUri(final String seoUri) {
        List<Category> list = categoryDao.findByNamedQuery("CATEGORY.BY.SEO.URI", seoUri);
        if (list != null && !list.isEmpty()) {
            return list.get(0).getCategoryId();
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public List<Category> getByProductId(long productId) {
        return categoryDao.findByNamedQuery(
                    "CATEGORIES.BY.PRODUCTID",
                    productId
            );
    }

}
