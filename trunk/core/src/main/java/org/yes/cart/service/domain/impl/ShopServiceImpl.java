package org.yes.cart.service.domain.impl;

import org.apache.commons.lang.StringUtils;
import org.yes.cart.cache.Cacheable;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.*;
import org.yes.cart.service.domain.AttributeService;
import org.yes.cart.service.domain.CategoryService;
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


    /**
     * Construct shop service.
     * @param shopDao shop doa.
     * @param categoryService {@link CategoryService}
     * @param attributeService attribute service
     */
    public ShopServiceImpl(final GenericDAO<Shop, Long> shopDao,
                           final CategoryService categoryService,
                           final AttributeService attributeService) {
        super(shopDao);
        this.shopDao = shopDao;
        this.categoryService = categoryService;
        this.attributeService = attributeService;
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
    public Shop getShopByOrderNum(final String orderNum) {
        return shopDao.findSingleByNamedQuery("SHOP.BY.ORDER.NUM", orderNum);
    }




    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "shopServiceImplMethodCache")
    public Shop getShopByDomainName(final String serverName) {
        return shopDao.findSingleByNamedQuery("SHOP.BY.URL", serverName);
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "shopServiceImplMethodCache")
    public Set<Category> getShopCategories(final Shop shop) {
        Set<Category> result = new HashSet<Category>();
        for (ShopCategory category : shop.getShopCategory()) {
            result.addAll(
                    categoryService.getChildCategoriesRecursive(category.getCategory().getCategoryId())
            );
        }
        return result;
    }

    /** {@inheritDoc} */
    public Collection<String> getAllSupportedCurrenciesByShops() {
        final List<Shop> shops = shopDao.findAll();
        final Set<String> currencies = new TreeSet<String>();

        for(Shop shop : shops) {
            final String shopCurrencies = shop.getSupportedCurrensies();
            if (StringUtils.isNotBlank(shopCurrencies)) {
                currencies.addAll(Arrays.asList(shopCurrencies.split(",")));
            }
        }

        return currencies;
    }


    /**
     * Set attribute value. New attribute value will be created, if attribute has not value for given shop.
     * TODO make sence to move it into abstract generic
     * @param shopId shop id
     * @param attributeKey attribute key
     * @param attributeValue attribute value.
     */
    public void updateAttributeValue(final long shopId, final String attributeKey, final String attributeValue) {
        final Shop shop = shopDao.findById(shopId);
        if (shop != null) {
            AttrValueShop attrValueShop = shop.getAttributeByCode(attributeKey);
            if (attrValueShop != null) {
                attrValueShop.setVal(attributeValue);
            } else {
                final Attribute attribute = attributeService.findByAttributeCode(attributeKey);
                attrValueShop = getGenericDao().getEntityFactory().getByIface(AttrValueShop.class);
                attrValueShop.setVal(attributeValue);
                attrValueShop.setAttribute(attribute);
                attrValueShop.setShop(shop);
                shop.getAttribute().add(attrValueShop);
            }
            shopDao.update(shop);
        }
    }


}
