package org.yes.cart.web.page.component;

import org.apache.lucene.search.BooleanQuery;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.model.Model;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.service.domain.CategoryService;
import org.yes.cart.service.domain.ProductSkuService;
import org.yes.cart.service.domain.PriceService;
import org.yes.cart.web.support.constants.StorefrontServiceSpringKeys;
import org.yes.cart.web.support.constants.WebParametersKeys;
import org.yes.cart.web.support.service.ProductImageService;
import org.yes.cart.web.util.WicketUtil;
import org.yes.cart.web.page.HomePage;
import org.yes.cart.web.page.component.price.PriceView;
import org.yes.cart.web.page.component.price.PriceTierView;
import org.yes.cart.web.page.component.product.SkuListView;
import org.yes.cart.web.page.component.product.SkuAttributesView;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.domain.entity.Product;
import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.domain.entity.SkuPrice;
import org.yes.cart.shoppingcart.impl.AddSkuToCartEventCommandImpl;

import java.util.Collection;
import java.util.Collections;
import java.math.BigDecimal;

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
    private final static String PRICE_VIEW = "priceView";
    /** Price tiers if any */
    private final static String PRICE_TIERS_VIEW = "priceTiersView";
    /** Add single item to cart */
    private final static String ADD_TO_CART_LINK = "addToCartLink";
    /** Product sku code */
    private final static String SKU_CODE_LABEL = "skuCode";
    /** Product name */
    private final static String PRODUCT_NAME_LABEL = "name";
    /** Product description */
    private final static String PRODUCT_DESCRIPTION_LABEL = "description";
    /** Product image panel */
    private final static String PRODUCT_IMAGE_VIEW = "imageView";
    /** Product sku list */
    private final static String SKU_LIST_VIEW = "skuList";
    /** View to show sku attributes */
    private final static String SKU_ATTR_VIEW = "skuAttrView";
    // ------------------------------------- MARKUP IDs END ---------------------------------- //

    @SpringBean(name = ServiceSpringKeys.PRODUCT_SERVICE)
    protected ProductService productService;

    @SpringBean(name = ServiceSpringKeys.PRODUCT_SKU_SERVICE)
    protected ProductSkuService productSkuService;

    @SpringBean(name = StorefrontServiceSpringKeys.PRODUCT_IMAGE_SERVICE)
    protected ProductImageService productImageService;

    @SpringBean(name = ServiceSpringKeys.CATEGORY_SERVICE)
    protected CategoryService categoryService;

    @SpringBean(name = ServiceSpringKeys.PRICE_SERVICE)
    protected PriceService priceService;

    private boolean isProduct;
    private Product product;
    private ProductSku sku;


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

    private void configureContext() {
        String productId = getPage().getPageParameters().get(WebParametersKeys.PRODUCT_ID).toString();
        String skuId = getPage().getPageParameters().get(WebParametersKeys.SKU_ID).toString();
        if (skuId != null) {
            isProduct = false;
            sku = productSkuService.getById(Long.valueOf(skuId));
            product = sku.getProduct();
        } else if (productId != null) {
            isProduct = true;
            product = productService.getProductById(Long.valueOf(productId));
            sku = product.getDefaultSku();
        } else {
            throw new RuntimeException("Product or Sku id expected");
        }
        
    }



    /**
     * {@inheritDoc}
     */
    @Override
    protected void onBeforeRender() {

        configureContext();

        add(
                new PriceView(PRICE_VIEW, new Model<SkuPrice>(getSkuPrice()), true)
        );
        add(
                new PriceTierView(PRICE_TIERS_VIEW, getSkuPrices())
        );
        add(
                new SkuListView(SKU_LIST_VIEW, product.getSku(), sku, isProduct)
        );
        add(
                new Label(SKU_CODE_LABEL, sku.getCode())
        );
        add(
                new Label(PRODUCT_NAME_LABEL, isProduct?product.getName():sku.getName())
        );
        add(
                new Label(PRODUCT_DESCRIPTION_LABEL, isProduct?product.getDescription():sku.getDescription())
        );
        
        final PageParameters addToCartParameters = WicketUtil.getFilteredRequestParameters(getPage().getPageParameters())
            .set(AddSkuToCartEventCommandImpl.CMD_KEY, sku.getCode());

        add(
                new BookmarkablePageLink<HomePage>(ADD_TO_CART_LINK, HomePage.class, addToCartParameters)
        );

        add(
                new SkuAttributesView(SKU_ATTR_VIEW, sku, isProduct)
        );

        add(
                new Label(PRODUCT_IMAGE_VIEW, "TODO image view")
        );



        super.onBeforeRender();

    }

    /**
     * Get sku prices from default sku in case of product or from particular sku.
     * @return collection of sku prices.
     */
    private Collection<SkuPrice> getSkuPrices() {
        if (isProduct) {
            return product.getDefaultSku().getSkuPrice();
        }
        return sku.getSkuPrice();
    }


    /**
     * Get product or his sku price.
     * In case of multisku product the minimal regular price from multiple sku was used for single item.
     * @return {@link SkuPrice}
     */
    private SkuPrice getSkuPrice() {
        final Collection<ProductSku> productSkus;
        if (isProduct) {
            productSkus = product.getSku();
        } else {
            productSkus = Collections.singletonList(sku);
        }
        return priceService.getMinimalRegularPrice(
                productSkus,
                ApplicationDirector.getCurrentShop(),
                ApplicationDirector.getShoppingCart().getCurrencyCode(),
                BigDecimal.ONE
                );
    }

}
