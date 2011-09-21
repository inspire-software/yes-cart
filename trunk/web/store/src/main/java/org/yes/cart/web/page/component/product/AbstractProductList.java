package org.yes.cart.web.page.component.product;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.entity.Product;
import org.yes.cart.service.domain.CategoryService;
import org.yes.cart.service.domain.ProductAssociationService;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.service.domain.ProductSkuService;
import org.yes.cart.web.page.HomePage;
import org.yes.cart.web.page.component.BaseComponent;
import org.yes.cart.web.support.constants.WebParametersKeys;
import org.yes.cart.web.support.constants.StorefrontServiceSpringKeys;
import org.yes.cart.web.support.entity.decorator.ProductDecorator;
import org.yes.cart.web.support.entity.decorator.impl.ProductDecoratorImpl;
import org.yes.cart.web.support.service.ProductImageService;
import org.yes.cart.web.util.WicketUtil;

import java.util.List;

/**
 * Abstract class to present products list.
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 9/18/11
 * Time: 11:06 AM
 */
public abstract class AbstractProductList extends BaseComponent {


    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    protected final static String NAME = "name";
    protected final static String PRODUCT_LIST = "products";
    protected final static String PRODUCT_NAME_LINK = "productLinkName";
    protected final static String PRODUCT_LINK_IMAGE = "productLinkImage";
    protected final static String PRODUCT_IMAGE = "productDefaultImage";
    // ------------------------------------- MARKUP IDs END   ---------------------------------- //

    @SpringBean(name = ServiceSpringKeys.PRODUCT_ASSOCIATIONS_SERVICE)
    protected ProductAssociationService productAssociationService;

    @SpringBean(name = ServiceSpringKeys.PRODUCT_SKU_SERVICE)
    protected ProductSkuService productSkuService;

    @SpringBean(name = ServiceSpringKeys.PRODUCT_SERVICE)
    protected ProductService productService;

    @SpringBean(name = StorefrontServiceSpringKeys.PRODUCT_IMAGE_SERVICE)
    protected ProductImageService productImageService;

    @SpringBean(name = ServiceSpringKeys.CATEGORY_SERVICE)
    protected CategoryService categoryService;


    /**
     * Construct product list to show.
     * @param id component id.
     */
    public AbstractProductList(final String id) {
        super(id);
    }


    /**
     * Get the value map with parameter to show the product.
     *
     * @param prod given product product
     * @return value map with parameter to show the product
     */
    protected PageParameters getProductPageParameters(final Product prod) {
        return WicketUtil.getFilteredRequestParameters(
                getPage().getPageParameters())
                .set(WebParametersKeys.PRODUCT_ID, String.valueOf(prod.getProductId()));

    }

    /**
     * Get list of product to show.
     *
     * @return list of products to show.
     */
    public abstract List<Product> getProductListToShow();


    /**
     * {@inheritDoc}
     */
    @Override
    protected void onBeforeRender() {
        add(
                new ListView<Product>(PRODUCT_LIST, getProductListToShow()) {
                    protected void populateItem(ListItem<Product> listItem) {

                        final Product prod =  listItem.getModelObject();
                        final ProductDecorator productDecorator = new ProductDecoratorImpl(
                                productImageService,
                                categoryService,
                                prod,
                                WicketUtil.getHttpServletRequest().getContextPath());
                        final Category category = categoryService.getRootCategory();
                        final String width = productDecorator.getProductImageWidth(category);   //TODo size
                        final String height = productDecorator.getProductImageHeight(category);
                        final PageParameters pageParameters = getProductPageParameters(prod);


                        listItem.add(
                                new BookmarkablePageLink<HomePage>(PRODUCT_LINK_IMAGE, HomePage.class, pageParameters).add(
                                        new ContextImage(PRODUCT_IMAGE, productDecorator.getProductImage(width, height))
                                                .add(new AttributeModifier("width", width))
                                                .add(new AttributeModifier("height", height))
                                                .add(new AttributeModifier("title", prod.getDescription()))
                                                .add(new AttributeModifier("alt", prod.getDescription()))
                                )
                        );
                        listItem.add(
                                new BookmarkablePageLink<HomePage>(PRODUCT_NAME_LINK, HomePage.class, pageParameters)
                                        .add(
                                                new Label(NAME, prod.getName()).setEscapeModelStrings(false)
                                        )
                        );
                    }

                }
        );

        super.onBeforeRender();
    }




    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isVisible() {
        return super.isVisible() && !getProductListToShow().isEmpty();
    }



}
