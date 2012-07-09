package org.yes.cart.web.page.component.product;

import org.yes.cart.domain.entity.Product;
import org.yes.cart.web.util.WicketUtil;

import java.util.List;

/**
 * Show new arrival. Current category will be selected to pick up
 * new arrivals.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 9/18/11
 * Time: 11:45 AM
 */
public class NewArrivalProducts extends AbstractProductList {


    private List<Product> products = null;

    /**
     * Construct product list to show.
     *
     * @param id component id.
     */
    public NewArrivalProducts(final String id) {
        super(id, true);
    }

    /** {@inheritDoc} */
    @Override
    public List<Product> getProductListToShow() {
        if (products == null) {
            products = productService.getNewArrivalsProductInCategory(
                    WicketUtil.getCategoryId(getPage().getPageParameters()),
                    getProductsLimit());
        }
        return products;
    }

    /**
     * Get quantity limit.
     *
     * @return quantity limit
     */
    public int getProductsLimit() {
        return 5;   //TODO from configuration
    }

}
