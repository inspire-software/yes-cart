package org.yes.cart.web.page.component.product;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.entity.Product;
import org.yes.cart.domain.entity.SkuPrice;
import org.yes.cart.service.domain.CategoryService;
import org.yes.cart.service.domain.ImageService;
import org.yes.cart.service.domain.PriceService;
import org.yes.cart.shoppingcart.impl.AddSkuToCartEventCommandImpl;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.page.HomePage;
import org.yes.cart.web.page.component.BaseComponent;
import org.yes.cart.web.page.component.price.PriceView;
import org.yes.cart.web.support.constants.WebParametersKeys;
import org.yes.cart.web.support.constants.StorefrontServiceSpringKeys;
import org.yes.cart.web.support.entity.decorator.ProductDecorator;
import org.yes.cart.web.support.entity.decorator.impl.ProductDecoratorImpl;
import org.yes.cart.web.support.service.AttributableImageService;
import org.yes.cart.web.util.WicketUtil;

import java.math.BigDecimal;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 8/8/11
 * Time: 11:43 AM
 */
public class ProductInListView extends BaseComponent {

    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    private final static String PRODUCT_LINK_SKU = "productLinkSku";
    private final static String SKU_CODE_LABEL = "skuCode";
    private final static String PRODUCT_LINK_NAME = "productLinkName";
    private final static String NAME_LABEL = "name";
    private final static String DESCRIPTION_LABEL = "description";
    private final static String ADD_TO_CART_LINK = "addToCartLink";
    private final static String PRICE_VIEW = "priceView";
    private final static String PRODUCT_LINK_IMAGE = "productLinkImage";
    private final static String PRODUCT_IMAGE = "productDefaultImage";
    // ------------------------------------- MARKUP IDs END ------------------------------------ //

    private final ProductDecorator product;

    private final Category category;


    @SpringBean(name = StorefrontServiceSpringKeys.ATTRIBUTABLE_IMAGE_SERVICE)
    private AttributableImageService attributableImageService;

    @SpringBean(name = ServiceSpringKeys.CATEGORY_SERVICE)
    private CategoryService categoryService;

    @SpringBean(name = ServiceSpringKeys.PRICE_SERVICE)
    protected PriceService priceService;

    @SpringBean(name = ServiceSpringKeys.IMAGE_SERVICE)
    protected ImageService imageService;


    private final String [] defImgSize;


    /**
     * Construct product view, that show product in grid.
     *
     * @param id       view od
     * @param product  product model
     * @param category product in category, optional parameter
     * @param defImgSize image size in given category
     */
    public ProductInListView(final String id, final Product product, final Category category, final String [] defImgSize) {
        super(id);
        if (category == null) {
            this.category = categoryService.getRootCategory();
        } else {
            this.category = category;
        }
        this.product = ProductDecoratorImpl.createProductDecoratorImpl(
                imageService,
                attributableImageService,
                categoryService,
                product,
                WicketUtil.getHttpServletRequest().getContextPath(),
                false);
        this.defImgSize = defImgSize;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onBeforeRender() {

        final PageParameters linkToProductParameters = WicketUtil.getFilteredRequestParameters(getPage().getPageParameters());
        linkToProductParameters.set(WebParametersKeys.PRODUCT_ID, product.getId());


        final String width = defImgSize[0];
        final String height = defImgSize[1];

        add(
                new BookmarkablePageLink<HomePage>(PRODUCT_LINK_SKU, HomePage.class, linkToProductParameters).add(
                        new Label(SKU_CODE_LABEL, product.getCode())
                )
        );
        
        add (
            new Label(DESCRIPTION_LABEL, product.getDescription())
        );

        add(
                new BookmarkablePageLink<HomePage>(PRODUCT_LINK_NAME, HomePage.class, linkToProductParameters).add(
                        new Label(NAME_LABEL, product.getName())
                )
        );

        add(
                new BookmarkablePageLink<HomePage>(PRODUCT_LINK_IMAGE, HomePage.class, linkToProductParameters).add(
                        new ContextImage(PRODUCT_IMAGE, product.getDefaultImage(width, height))
                                .add(new AttributeModifier(HTML_WIDTH, width))
                                .add(new AttributeModifier(HTML_HEIGHT, height))
                )
        );

        final PageParameters addToCartParameters = WicketUtil.getFilteredRequestParameters(getPage().getPageParameters())
                .set(AddSkuToCartEventCommandImpl.CMD_KEY, product.getDefaultSku().getCode());

        add(
                new BookmarkablePageLink<HomePage>(ADD_TO_CART_LINK, HomePage.class, addToCartParameters)
        );

        add(
                new PriceView(PRICE_VIEW, new Model<SkuPrice>(getSkuPrice()), true)
        );


        super.onBeforeRender();
    }


    /**
     * Get product or his sku price.
     * In case of multisku product the minimal regular price from multiple sku was used for single item.
     *
     * @return {@link org.yes.cart.domain.entity.SkuPrice}
     */
    private SkuPrice getSkuPrice() {
        return priceService.getMinimalRegularPrice(
                product.getSku(),
                ApplicationDirector.getCurrentShop(),
                ApplicationDirector.getShoppingCart().getCurrencyCode(),
                BigDecimal.ONE
        );
    }

}
