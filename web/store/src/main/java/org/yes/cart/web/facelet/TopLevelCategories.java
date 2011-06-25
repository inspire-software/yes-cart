package org.yes.cart.web.facelet;

import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.service.domain.CategoryService;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.constants.ManagedBeanELNames;
import org.yes.cart.web.support.constants.WebParametersKeys;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import java.util.List;

/**
 * Represent top level categories container for current shop.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 6/25/11
 * Time: 3:15 PM
 */
@ManagedBean(name = WebParametersKeys.REQUEST_TOP_LEVEL_CATEGORIES)
@RequestScoped
public class TopLevelCategories {

    @ManagedProperty(ManagedBeanELNames.EL_APPLICATION_DYNAMYC_CACHE)
    private ApplicationDirector applicationDirector;

    @ManagedProperty(ManagedBeanELNames.EL_SESSION_SHOPPING_CART)
    private ShoppingCart shoppingCart;

    @ManagedProperty(ManagedBeanELNames.EL_CATEGORY_SERVICE)
    protected CategoryService categoryService;

    /**
     * Get the top level categories for current shop.
     * @return  list of top level categories.
     */
    public List<Category> getCategoies() {
        final Shop shop = applicationDirector.getShopById(shoppingCart.getShoppingContext().getShopId());
        return categoryService.getTopLevelCategories(shop);
    }

    /**
     * Set application director.
     * @param applicationDirector  app director to use.
     */
    public void setApplicationDirector(ApplicationDirector applicationDirector) {
        this.applicationDirector = applicationDirector;
    }

    /**
     * Set shopping cart.
     * @param shoppingCart   current shopping cart
     */
    public void setShoppingCart(ShoppingCart shoppingCart) {
        this.shoppingCart = shoppingCart;
    }

    /**
     * Set category service to use.
     * @param categoryService category  service to use.
     */
    public void setCategoryService(CategoryService categoryService) {
        this.categoryService = categoryService;
    }
}
