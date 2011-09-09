package org.yes.cart.web.page.component;

import org.apache.lucene.search.BooleanQuery;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.service.domain.CategoryService;
import org.yes.cart.service.domain.ProductSkuService;
import org.yes.cart.web.support.constants.WebServiceSpringKey;
import org.yes.cart.web.support.service.ProductImageService;

/**
 *
 * Central view to show product sku.
 * Supports products is multisku and default sku is 
 *
 * Igor Azarny iazarny@yahoo.com
 * Date: 10-Sep-2011
 * Time: 11:11:08
 */
public class SkuCentralView extends AbstractCentralView {

    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    /** Single item price panel*/
    private final static String PRICE_PANEL = "pricePanel";
    /** Price tiers if any */
    private final static String PRICE_TIERS_PANEL = "priceTiersPanel";
    /** Add single item to cart */
    private final static String ADD_TO_CART_LINK = "addToCartLink";

    /** Product sku code */
    private final static String SKU_CODE = "skuCode";

    /** Product name */
    private final static String PRODUCT_NAME = "name";

    /** Product description */
    private final static String PRODUCT_DESCRIPTION = "description";
    
    // ------------------------------------- MARKUP IDs END ---------------------------------- //

    @SpringBean(name = ServiceSpringKeys.PRODUCT_SERVICE)
    protected ProductService productService;

    @SpringBean(name = ServiceSpringKeys.PRODUCT_SKU_SERVICE)
    protected ProductSkuService productSkuService;

    @SpringBean(name = WebServiceSpringKey.PRODUCT_IMAGE_SERVICE)
    protected ProductImageService productImageService;

    @SpringBean(name = ServiceSpringKeys.CATEGORY_SERVICE)
    protected CategoryService categoryService;


    /**
     * Construct panel.
     *
     * @param id           panel id
     * @param categoryId   current category id.
     * @param booleanQuery boolean query.
     */
    public SkuCentralView(final String id, final long categoryId, final BooleanQuery booleanQuery) {
        super(id, categoryId, booleanQuery);
    }
    
}
