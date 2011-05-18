package org.yes.cart.web.support.shoppingcart.impl;

import org.apache.lucene.search.BooleanQuery;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.web.support.shoppingcart.RequestRuntimeContainer;
import org.yes.cart.web.support.shoppingcart.VisitableShoppingCart;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Request runtime container is a global object that acts as container for
 * all objects that need to be accessible during request, such as current shop,
 * currency, shopping cart etc.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 2011-May-17
 * Time: 11:51:33 AM
 */
public class RequestRuntimeContainerImpl implements RequestRuntimeContainer {

    private Shop shop;

    private String defaultContextPath;

    private VisitableShoppingCart shoppingCart;

    private List<Long> allShopCategories;

    private Map pageParameters;

    private long currentCategoryId;

    private List<Long> currentCategories;

    private BooleanQuery apppliedQuery;

    private String resolvedIp;

    private Date currentDate;


    /**
     * Default constructor.
     */
    public RequestRuntimeContainerImpl() {
        // do nothing
    }

    /**
     * Get page parameters.
     *
     * @return page parameters.
     */
    public Map getPageParameters() {
        return pageParameters;
    }

    /**
     * Set page parameters.
     *
     * @param pageParameters paga parameters.
     */
    public void setPageParameters(final Map pageParameters) {
        if (pageParameters == null) {
            this.pageParameters = new LinkedHashMap();
        } else {
            this.pageParameters = pageParameters;
        }
    }

    /**
     * Is shopper authorized.
     *
     * @return true if authorized
     */
    public boolean isAuthenticated() {

        // return shoppingCart.getAuthentication() != null; TODO impl
        return false;

    }

    /**
     * {@inheritDoc}
     */
    public boolean isPricesVisible() {
        return !shop.isB2ProfileActive() || isAuthenticated();
    }


    /**
     * IoC constructor.
     *
     * @param cart default cart instance.
     */
    public RequestRuntimeContainerImpl(final VisitableShoppingCart cart) {
        this.shoppingCart = cart;
    }

    /**
     * {@inheritDoc}
     */
    public String getResolvedIp() {
        return resolvedIp;
    }

    /**
     * {@inheritDoc}
     */
    public void setResolvedIp(final String resolvedIp) {
        this.resolvedIp = resolvedIp;
    }

    /**
     * {@inheritDoc}
     */
    public Date getCurrentDate() {
        if (currentDate == null) {
            currentDate = new Date();
        }
        return currentDate;
    }

    /**
     * {@inheritDoc}
     */
    public void setCurrentDate(final Date currentDate) {
        this.currentDate = currentDate;
    }

    /**
     * {@inheritDoc}
     */
    public Shop getShop() {
        return shop;
    }

    /**
     * {@inheritDoc}
     */
    public void setShop(final Shop shop) {
        this.shop = shop;
    }

    /**
     * {@inheritDoc}
     */
    public String getDefaultContextPath() {
        return defaultContextPath;
    }

    /**
     * {@inheritDoc}
     */
    public void setDefaultContextPath(final String defaultContextPath) {
        this.defaultContextPath = defaultContextPath;
    }

    /**
     * {@inheritDoc}
     */
    public List<Long> getAllShopCategories() {
        return allShopCategories;
    }

    /**
     * {@inheritDoc}
     */
    public void setAllShopCategories(final List<Long> allShopCategories) {
        this.allShopCategories = allShopCategories;
    }

    /**
     * {@inheritDoc}
     */
    public void setShoppingCart(final VisitableShoppingCart cart) {
        this.shoppingCart = cart;
    }

    /**
     * {@inheritDoc}
     */
    public VisitableShoppingCart getShoppingCart() {
        return shoppingCart;
    }

    /**
     * {@inheritDoc}
     */
    public long getCurrentCategoryId() {
        return currentCategoryId;
    }

    /**
     * {@inheritDoc}
     */
    public void setCurrentCategoryId(final long currentCategoryId) {
        this.currentCategoryId = currentCategoryId;
    }

    /**
     * {@inheritDoc}
     */
    public List<Long> getCurrentCategories() {
        return currentCategories;
    }

    /**
     * {@inheritDoc}
     */
    public void setCurrentCategories(final List<Long> currentCategories) {
        this.currentCategories = currentCategories;
    }

    /**
     * {@inheritDoc}
     */
    public BooleanQuery getApppliedQuery() {
        return apppliedQuery;
    }

    /**
     * {@inheritDoc}
     */
    public void setApppliedQuery(final BooleanQuery apppliedQuery) {
        this.apppliedQuery = apppliedQuery;
    }
}
