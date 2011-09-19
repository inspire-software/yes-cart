package org.yes.cart.web.page.component.product;

import org.yes.cart.domain.entity.Product;
import org.yes.cart.domain.entity.ShopCategory;
import org.yes.cart.service.domain.ShopCategoryService;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.util.WicketUtil;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 *
 * Display featured product in particular category or entire shop.
 *
 * Igor Azarny iazarny@yahoo.com
 * Date: 18-Sep-2011
 * Time: 11:03:57
 */
public class FeaturedProducts  extends AbstractProductList {


    private ShopCategoryService shopCategoryService;



    private List<Product> products = null;

    /**
     * Construct product list to show.
     *
     * @param id component id.
     */
    public FeaturedProducts(final String id) {
        super(id);
    }

    /** {@inheritDoc} */
    @Override
    public List<Product> getProductListToShow() {
        if (products == null) {
            final long categoryId = WicketUtil.getCategoryId();
            final Collection<ShopCategory> categories;
            if (categoryId == 0) {
                categories = ApplicationDirector.getCurrentShop().getShopCategory();
            } else {
                categories = Collections.singletonList(
                        shopCategoryService.findByShopCategory(
                                ApplicationDirector.getCurrentShop(),
                                categoryService.getById(categoryId))
                );
            }
            products = productService.getFeaturedProducts(
                    categories,
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
