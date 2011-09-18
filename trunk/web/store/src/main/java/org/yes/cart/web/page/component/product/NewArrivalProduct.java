package org.yes.cart.web.page.component.product;

import org.yes.cart.domain.entity.Product;
import org.yes.cart.domain.entity.ProductAssociation;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.support.constants.WebParametersKeys;

import java.util.List;

/**
 * Show new arrival. Current category will be selected to pick up
 * new arrivals.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 9/18/11
 * Time: 11:45 AM
 */
public class NewArrivalProduct extends AbstractProductList {



    private List<Product> products = null;

    /**
     * Construct product list to show.
     *
     * @param id component id.
     */
    public NewArrivalProduct(final String id) {
        super(id);
    }

    @Override
    public List<Product> getProductListToShow() {
        if (products == null) {
            products = productService.getNewArrivalsProductInCategory(getCategoryId(), getNewProductsLimit());
            if (products.isEmpty()) {
                products = productService.getFeaturedProducts(ApplicationDirector.getCurrentShop().getShopCategory()); //TODO pass limit
            }
        }
        return products;
    }

    /**
     * Get quantity limit.
     *
     * @return quantity limit
     */
    public int getNewProductsLimit() {
        return 5;   //TODO from configuration
    }

    /**
     * Get product id from page parameters or from sku.
     *
     * @return product id
     */
    protected long getCategoryId() {
        String categoryId = getPage().getPageParameters().get(WebParametersKeys.CATEGORY_ID).toString();
        if (categoryId == null) {
            return categoryService.getRootCategory().getCategoryId();
        } else {
            return Long.valueOf(categoryId);
        }
    }
}
