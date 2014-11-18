/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.yes.cart.web.page.component;

import org.apache.lucene.search.Query;
import org.apache.wicket.Application;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.*;
import org.yes.cart.service.domain.*;
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.shoppingcart.ShoppingCartCommandFactory;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.page.component.price.PriceTierView;
import org.yes.cart.web.page.component.price.PriceView;
import org.yes.cart.web.page.component.product.*;
import org.yes.cart.web.page.component.social.AddAnyButton;
import org.yes.cart.web.support.constants.StorefrontServiceSpringKeys;
import org.yes.cart.web.support.constants.WebParametersKeys;
import org.yes.cart.web.support.entity.decorator.ObjectDecorator;
import org.yes.cart.web.support.service.AttributableImageService;
import org.yes.cart.web.util.WicketUtil;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * Central view to show product sku.
 * Supports products is multisku and default sku is
 * <p/>
 * Igor Azarny iazarny@yahoo.com
 * Date: 10-Sep-2011
 * Time: 11:11:08
 */
public class SkuCentralView extends AbstractCentralView {

    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    /**
     * Single item price panel
     */
    private final static String PRICE_VIEW = "priceView";
    /**
     * Price tiers if any
     */
    private final static String PRICE_TIERS_VIEW = "priceTiersView";
    /**
     * Add single item to cart
     */
    private final static String ADD_TO_CART_LINK = "addToCartLink";
    /**
     * Add single item to wish list
     */
    private final static String ADD_TO_WISHLIST_LINK = "addToWishListLink";
    /**
     * Add single item to wish list
     */
    private final static String LOGIN_LINK = "addToWishListLink";
    /**
     * Quantity picker
     */
    private final static String QTY_PICKER = "quantityPicker";
    /**
     * Add single item to cart label
     */
    private final static String ADD_TO_CART_LINK_LABEL = "addToCartLinkLabel";
    /**
     * Add single item to cart label
     */
    private final static String ADD_TO_WISHLIST_LINK_LABEL = "addToWishListLinkLabel";
    /**
     * AddToAny social button
     */
    private final static String SOCIAL_ADD_TO_ANY_BUTTON = "addToAnyButton";
    /**
     * Product sku code
     */
    private final static String SKU_CODE_LABEL = "skuCode";
    /**
     * Product name
     */
    private final static String PRODUCT_NAME_LABEL = "name";
    /**
     * Product description
     */
    private final static String PRODUCT_DESCRIPTION_LABEL = "description";
    /**
     * Product image panel
     */
    private final static String PRODUCT_IMAGE_VIEW = "imageView";
    /**
     * Product sku list
     */
    private final static String SKU_LIST_VIEW = "skuList";
    /**
     * View to show sku attributes
     */
    private final static String SKU_ATTR_VIEW = "skuAttrView";
    /**
     * Product accessories or cross sell
     */
    private final static String ACCESSORIES_VIEW = "accessoriesView";

    /**
     * Product accessories head container name
     */
    private final static String ACCESSORIES_HEAD_CONTAINER = "accessoriesHeadContainer";
    /**
     * Product accessories body container name
     */
    private final static String ACCESSORIES_BODY_CONTAINER = "accessoriesBodyContainer";
    /**
     * Product accessories head container name
     */
    private final static String ACCESSORIES_HEAD = "accessoriesHead";
    /**
     * Product accessories body container name
     */
    private final static String ACCESSORIES_BODY = "accessoriesBody";
    
   /**
     * Product accessories head container name
     */
    private final static String VOLUME_DISCOUNT_HEAD_CONTAINER = "volumeDiscountsHeadContainer";
    private final static String VOLUME_DISCOUNT_HEAD = "volumeDiscountsHead";
    private final static String VOLUME_DISCOUNT_BODY_CONTAINER = "priceTiersView";
    // ------------------------------------- MARKUP IDs END ---------------------------------- //

    @SpringBean(name = ServiceSpringKeys.PRODUCT_SERVICE)
    protected ProductService productService;

    @SpringBean(name = StorefrontServiceSpringKeys.PRODUCT_IMAGE_SERVICE)
    protected AttributableImageService attributableImageService;

    @SpringBean(name = ServiceSpringKeys.IMAGE_SERVICE)
    protected ImageService imageService;

    @SpringBean(name = ServiceSpringKeys.CATEGORY_SERVICE)
    protected CategoryService categoryService;

    @SpringBean(name = ServiceSpringKeys.PRICE_SERVICE)
    protected PriceService priceService;

    @SpringBean(name = ServiceSpringKeys.PRODUCT_ASSOCIATIONS_SERVICE)
    protected ProductAssociationService productAssociationService;

    @SpringBean(name = ServiceSpringKeys.PRODUCT_AVAILABILITY_STRATEGY)
    private ProductAvailabilityStrategy productAvailabilityStrategy;

    @SpringBean(name = ServiceSpringKeys.CART_COMMAND_FACTORY)
    private ShoppingCartCommandFactory shoppingCartCommandFactory;


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
    public SkuCentralView(final String id, final long categoryId, final Query booleanQuery) {
        super(id, categoryId, booleanQuery);
    }

    private void configureContext() {
        String productId = getPage().getPageParameters().get(WebParametersKeys.PRODUCT_ID).toString();
        String skuId = getPage().getPageParameters().get(WebParametersKeys.SKU_ID).toString();
        if (skuId != null) {
            isProduct = false;
            try {
                final Long skuPK = Long.valueOf(skuId);
                sku = productService.getSkuById(skuPK);
                product = productService.getProductById(sku.getProduct().getProductId(), true);
            } catch (Exception exp) {
                throw new RestartResponseException(Application.get().getHomePage());
            }
        } else if (productId != null) {
            isProduct = true;
            try {
                final Long prodPK = Long.valueOf(productId);
                product = productService.getProductById(prodPK, true);
                final ProductAvailabilityModel pam = productAvailabilityStrategy.getAvailabilityModel(ShopCodeContext.getShopId(), product);
                sku = getDefault(product, pam);
            } catch (Exception exp) {
                throw new RestartResponseException(Application.get().getHomePage());
            }
        } else {
            throw new RuntimeException("Product or Sku id expected");
        }

        shoppingCartCommandFactory.execute(ShoppingCartCommand.CMD_INTERNAL_VIEWSKU, ApplicationDirector.getShoppingCart(), new HashMap<String, Object>() {{
            put(ShoppingCartCommand.CMD_INTERNAL_VIEWSKU, product);
        }});

    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected void onBeforeRender() {

        configureContext();

        final String selectedLocale = getLocale().getLanguage();

        final ObjectDecorator decorator = getDecorator();

        add(new PriceView(PRICE_VIEW, new Model<SkuPrice>(getSkuPrice()), true, true));
        add(new SkuListView(SKU_LIST_VIEW, product.getSku(), sku, isProduct));
        add(new Label(SKU_CODE_LABEL, sku.getCode()));
        add(new Label(PRODUCT_NAME_LABEL, decorator.getName(selectedLocale)));
        add(new Label(PRODUCT_DESCRIPTION_LABEL, decorator.getDescription(selectedLocale)).setEscapeModelStrings(false));
        add(new AddAnyButton(SOCIAL_ADD_TO_ANY_BUTTON, product));

        final ProductAvailabilityModel pam = productAvailabilityStrategy.getAvailabilityModel(ShopCodeContext.getShopId(), sku);

        final Shop shop = ApplicationDirector.getCurrentShop();
        final AttrValue val = shop.getAttributeByCode(AttributeNamesKeys.Shop.CART_ADD_ENABLE_QTY_PICKER);
        final boolean qtyPickVisible = pam.isAvailable() && val != null && val.getVal() != null && Boolean.valueOf(val.getVal());

        add(new QuantityPickerPanel(QTY_PICKER, product.getProductId(), sku.getCode()).setVisible(qtyPickVisible));

        add(
                getWicketSupportFacade().links().newAddToCartLink(ADD_TO_CART_LINK, sku.getCode(), null, getPage().getPageParameters())
                        .add(new Label(ADD_TO_CART_LINK_LABEL, pam.isInStock() || pam.isPerpetual() ?
                                getLocalizer().getString("addToCart", this) :
                                getLocalizer().getString("preorderCart", this)))
                        .setVisible(pam.isAvailable())
        );

        add(
                getWicketSupportFacade().links().newAddToWishListLink(ADD_TO_WISHLIST_LINK, sku.getCode(), null, null, null, getPage().getPageParameters())
                        .add(new Label(ADD_TO_WISHLIST_LINK_LABEL, getLocalizer().getString("addToWishlist", this)))
        );

        add(
                new SkuAttributesView(SKU_ATTR_VIEW, sku, isProduct)
        );
        add(
                new ImageView(PRODUCT_IMAGE_VIEW, decorator)
        );


        final List<Long> associatedProducts = productAssociationService.getProductAssociationsIds(
                isProduct ? product.getProductId() : sku.getProduct().getProductId(),
                Association.ACCESSORIES
        );


        if (associatedProducts.isEmpty()  /*|| false is accessory*/) {

            add(
                    new Label(ACCESSORIES_HEAD_CONTAINER, "")
            ).add(
                    new Label(ACCESSORIES_BODY_CONTAINER, "")
            );

        } else {


            add(
                    new Fragment(ACCESSORIES_HEAD_CONTAINER, ACCESSORIES_HEAD, this)
            ).add(
                    new Fragment(ACCESSORIES_BODY_CONTAINER, ACCESSORIES_BODY, this)
                            .add(
                                    new ProductAssociationsView(ACCESSORIES_VIEW, Association.ACCESSORIES)
                            )
            );

        }


        if (isPriceTiersPresents()) {

            add(
                    new Fragment(VOLUME_DISCOUNT_HEAD_CONTAINER, VOLUME_DISCOUNT_HEAD, this)
            ).add(
                    new PriceTierView(VOLUME_DISCOUNT_BODY_CONTAINER, getSkuPrices())
            );

        } else {

            add(
                    new Label(VOLUME_DISCOUNT_HEAD_CONTAINER, "")
            ).add(
                    new Label(VOLUME_DISCOUNT_BODY_CONTAINER, "")
            );
        }

        final String wishListSkuAdded = getPage().getPageParameters().get(WebParametersKeys.WISHLIST_ITEM_ADDED).toString(null);
        final boolean wishListAdded = wishListSkuAdded != null;
        add(new Label("jsWishListItemAddedMsg", new StringResourceModel("wishListItemAdded", this, new Model<Serializable>(new Object[] {wishListSkuAdded})))
                .setVisible(wishListAdded));
        add(new Label("jsWishListItemAdded", "<script type=\"text/javascript\"> $(document).ready(function() {ctx.showModalWindow($('.jsWishListItemAddedMsg'));});</script>")
                .setVisible(wishListAdded).setEscapeModelStrings(false));

        super.onBeforeRender();

    }


    /**
     * Check is product has several prices in current shop currency.
     *
     * @return true in case if product has several prices in current shop currency.
     */
    private boolean isPriceTiersPresents() {
        boolean addPriceTier;
        final String currency = ApplicationDirector.getShoppingCart().getCurrencyCode();
        final List<SkuPrice> skuPricesInCurrentCurrency = new ArrayList<SkuPrice>();
        for (SkuPrice skuPrice : getSkuPrices()) {
            if (skuPrice.getCurrency().equals(currency)) {
                skuPricesInCurrentCurrency.add(skuPrice);
            }
        }
        addPriceTier = skuPricesInCurrentCurrency.size() > 1;
        return addPriceTier;
    }

    private ObjectDecorator getDecorator() {
        if (isProduct) {
            return getDecoratorFacade().decorate(product, WicketUtil.getHttpServletRequest().getContextPath(), true);
        }
        return getDecoratorFacade().decorate(sku, WicketUtil.getHttpServletRequest().getContextPath(), true);
    }

    /**
     * Get sku prices from default sku in case of product or from particular sku.
     *
     * @return collection of sku prices.
     */
    private Collection<SkuPrice> getSkuPrices() {
        /* We always preselect a SKU */
        return priceService.getAllCurrentPrices(
                product.getProductId(),
                sku.getCode(),
                ApplicationDirector.getCurrentShop(),
                ApplicationDirector.getShoppingCart().getCurrencyCode());
    }

    /*
    * Return first available sku rather than default to improve customer experience.
    */
    private ProductSku getDefault(final Product product, final ProductAvailabilityModel productPam) {
        if (productPam.isAvailable()) {
            if (product.isMultiSkuProduct()) {
                for (final ProductSku sku : product.getSku()) {
                    final ProductAvailabilityModel skuPam = productAvailabilityStrategy.getAvailabilityModel(ShopCodeContext.getShopId(), sku);
                    if (skuPam.isAvailable()) {
                        return sku;
                    }
                }
            }
        }
        // single SKU and N/A product just use default
        return product.getDefaultSku();
    }



    /**
     * Get product or his sku price.
     * In case of multisku product the minimal regular price from multiple sku was used for single item.
     *
     * @return {@link SkuPrice}
     */
    private SkuPrice getSkuPrice() {
        return priceService.getMinimalRegularPrice(
                null,
                sku.getCode(), /* We always preselect a SKU */
                ApplicationDirector.getCurrentShop(),
                ApplicationDirector.getShoppingCart().getCurrencyCode(),
                BigDecimal.ONE
        );
    }

    @Override
    protected Seoable getSeoObject() {
        return isProduct ? product : sku;
    }

}
