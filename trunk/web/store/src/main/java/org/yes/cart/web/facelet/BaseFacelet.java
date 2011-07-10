package org.yes.cart.web.facelet;

import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.query.LuceneQueryFactory;
import org.yes.cart.service.domain.CategoryService;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.constants.ManagedBeanELNames;
import org.yes.cart.web.support.constants.WebParametersKeys;
import org.yes.cart.web.support.service.CategoryImageService;
import org.yes.cart.web.support.service.CentralViewResolver;
import org.yes.cart.web.support.service.CurrencySymbolService;

import javax.faces.application.Application;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/9/11
 * Time: 8:25 AM
 */
public class BaseFacelet {

    private ShoppingCart shoppingCart;

    private CategoryService categoryService;

    private LuceneQueryFactory luceneQueryFactory;

    private ShopService shopService;

    private CentralViewResolver centralViewResolver;

    private ProductService productService;

    private CategoryImageService categoryImageService;

    private ApplicationDirector applicationDirector;


    /**
     * Get query builder factory.
     *
     * @return {@link LuceneQueryFactory}
     */
    public LuceneQueryFactory getLuceneQueryFactory() {
        if (luceneQueryFactory == null) {
            luceneQueryFactory = ApplicationDirector.getApplicationContext().getBean(
                    ServiceSpringKeys.LUCENE_QUERY_FACTORY, LuceneQueryFactory.class);
        }
        return luceneQueryFactory;
    }


    /**
     * Get category service.
     *
     * @return {@link CategoryService}
     */
    public CategoryService getCategoryService() {
        if (categoryService == null) {
            categoryService = ApplicationDirector.getApplicationContext().getBean(
                    ServiceSpringKeys.CATEGORY_SERVICE, CategoryService.class);
        }
        return categoryService;
    }

    /**
     * Get shop service.
     *
     * @return shop service.
     */
    public ShopService getShopService() {
        if (shopService == null) {
            shopService = ApplicationDirector.getApplicationContext().getBean(
                    ServiceSpringKeys.SHOP_SERVICE, ShopService.class);
        }
        return shopService;
    }


    /**
     * Get central view resolver.
     *
     * @return resolver.
     */
    public CentralViewResolver getCentralViewResolver() {
        if (centralViewResolver == null) {
            centralViewResolver = ApplicationDirector.getApplicationContext().getBean(
                    "centralViewResolver", CentralViewResolver.class);    // todo constant

        }
        return centralViewResolver;
    }


    /**
     * Get product service.
     *
     * @return product service.
     */
    public ProductService getProductService() {
        if (productService == null) {
            productService = ApplicationDirector.getApplicationContext().getBean(
                    ServiceSpringKeys.PRODUCT_SERVICE, ProductService.class);
        }
        return productService;
    }


    /**
     * Get the {@link ShoppingCart}.
     *
     * @return shopping cart.
     */
    public ShoppingCart getShoppingCart() {
        if (shoppingCart == null) {
            final FacesContext facesContext = FacesContext.getCurrentInstance();
            final Application application = facesContext.getApplication();
            shoppingCart = (ShoppingCart) application.getVariableResolver().resolveVariable(
                    facesContext, WebParametersKeys.SESSION_SHOPPING_CART);
        }
        return shoppingCart;
    }


    /**
     * Get category image service.
     * @return {@link CategoryImageService}
     */
    public CategoryImageService getCategoryImageService() {
        if(categoryImageService == null) {
            categoryImageService = ApplicationDirector.getApplicationContext().getBean(
                    "categoryImageService", CategoryImageService.class);
        }
        return categoryImageService;
    }

    /**
     * Get {@link ApplicationDirector} .
     * @return {@link ApplicationDirector}
     */
    public ApplicationDirector getApplicationDirector() {
        if (applicationDirector == null) {
            final FacesContext facesContext = FacesContext.getCurrentInstance();
            final Application application = facesContext.getApplication();
            applicationDirector = (ApplicationDirector) application.getVariableResolver().resolveVariable(
                    facesContext, WebParametersKeys.APPLICATION_DYNAMYC_CACHE);
        }
        return applicationDirector;
    }

}
