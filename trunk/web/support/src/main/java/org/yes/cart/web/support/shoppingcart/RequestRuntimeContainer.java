package org.yes.cart.web.support.shoppingcart;

import org.apache.lucene.search.BooleanQuery;
import org.yes.cart.domain.entity.Shop;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * * Request runtime container is a global object that acts as service locator for
 * all objects that need to be accessible during request, such as current shop,
 * currency, shopping cart etc.
 * <p/>
 * User: dogma
 * Date: 2011-May-17
 * Time: 10:30:26 PM
 */
public interface RequestRuntimeContainer extends Serializable {

    /**
     * @return currently active shop
     */
    Shop getShop();

    /**
     * @param shop currently active shop
     */
    void setShop(Shop shop);

    /**
     * @return default context path for resources
     */
    String getDefaultContextPath();

    /**
     * @param defaultContextPath default context path for resources
     */
    void setDefaultContextPath(String defaultContextPath);

    /**
     * @return all shop categories
     */
    List<Long> getAllShopCategories();

    /**
     * @param allShopCategories all shop categories
     */
    void setAllShopCategories(List<Long> allShopCategories);

    /**
     * @param cart current shopping cart
     */
    void setShoppingCart(VisitableShoppingCart cart);

    /**
     * @return current shopping cart
     */
    VisitableShoppingCart getShoppingCart();


    /**
     * Get the page parameters, that more suitable to use, that request parameters (Map<String,String[])
     *
     * @return {@link Map} instance for current request.
     */
    Map getPageParameters();


    /**
     * Set page parameters.
     *
     * @param pageParameters paga parameters.
     */
    void setPageParameters(Map pageParameters);


    /**
     * Get current category.
     *
     * @return id of current category.
     */
    public long getCurrentCategoryId();

    /**
     * Set current category id.
     *
     * @param currentCategoryId category id.
     */
    public void setCurrentCategoryId(final long currentCategoryId);

    /**
     * Get list of sub categories id with current category id, that belong to current category.
     *
     * @return list of sub category IDs.
     */
    public List<Long> getCurrentCategories();

    /**
     * Set list of sub categories
     *
     * @param currentCategories list of sub categories
     */
    public void setCurrentCategories(final List<Long> currentCategories);

    /**
     * Get applied navigation query.
     *
     * @return applied navigation query.
     */
    public BooleanQuery getApppliedQuery();

    /**
     * Set applied navigation query.
     *
     * @param apppliedQuery applied navigation query.
     */
    public void setApppliedQuery(final BooleanQuery apppliedQuery);


    /**
     * Get Real client ip. TODO add filter
     *
     * @return client ip.
     */
    String getResolvedIp();

    /**
     * Set client ip.
     *
     * @param resolvedIp client ip.
     */
    void setResolvedIp(String resolvedIp);

    /**
     * Get current date. In case of staging server value
     * can be setted by filter to implement time machine feature.
     *
     * @return current date.
     */
    Date getCurrentDate();

    /**
     * Set current date.   TODO add filter
     *
     * @param currentDate urrent date.
     */
    void setCurrentDate(Date currentDate);


    /**
     * Is shopper authorized.
     *
     * @return true if authorized
     */
    boolean isAuthenticated();


    /**
     * Helper method to determinate is prices visible. See b2b profile settings
     *
     * @return true in case if prices visible.
     */
    boolean isPricesVisible();


}
