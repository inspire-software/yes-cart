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

package org.yes.cart.web.page.component.cart;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.constants.Constants;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.domain.CategoryService;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.service.domain.ProductSkuService;
import org.yes.cart.shoppingcart.CartItem;
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.web.page.AbstractWebPage;
import org.yes.cart.web.page.ShoppingCartPage;
import org.yes.cart.web.page.component.BaseComponent;
import org.yes.cart.web.page.component.price.PriceView;
import org.yes.cart.web.support.constants.StorefrontServiceSpringKeys;
import org.yes.cart.web.support.entity.decorator.DecoratorFacade;
import org.yes.cart.web.support.entity.decorator.ProductSkuDecorator;
import org.yes.cart.web.support.i18n.I18NWebSupport;
import org.yes.cart.web.util.WicketUtil;

import java.math.BigDecimal;
import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 10/9/11
 * Time: 11:30 AM
 */
public class ShoppingCartItemsList extends ListView<CartItem> {


    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //


    private static final String PRICE_PANEL = "pricePanel";
    private static final String SKU_NUM_LABEL = "skunum";
    private static final String ADD_ONE_LINK = "addOneLink";
    private static final String REMOVE_ONE_LINK = "removeOneLink";
    private static final String REMOVE_ALL_LINK = "removeAllLink";
    private static final String PRODUCT_LINK = "productLink";
    private static final String PRODUCT_NAME_LABEL = "name";
    private static final String DEFAULT_IMAGE = "defaultImage";
    private static final String QUANTITY_TEXT = "quantity";


    private static final String LINE_TOTAL_VIEW = "lineAmountView";
    private final static String PRICE_VIEW = "skuPriceView";

    private static final String QUANTITY_ADJUST_BUTTON = "quantityButton";

    // ------------------------------------- MARKUP IDs END ---------------------------------- //

    @SpringBean(name = ServiceSpringKeys.PRODUCT_SKU_SERVICE)
    private ProductSkuService productSkuService;

    @SpringBean(name = ServiceSpringKeys.PRODUCT_SERVICE)
    protected ProductService productService;

    @SpringBean(name = ServiceSpringKeys.CATEGORY_SERVICE)
    protected CategoryService categoryService;


    private final Category rootCategory;

    @SpringBean(name = StorefrontServiceSpringKeys.I18N_SUPPORT)
    private I18NWebSupport i18NWebSupport;

    @SpringBean(name = StorefrontServiceSpringKeys.DECORATOR_FACADE)
    private DecoratorFacade decoratorFacade;


    /**
     * Construct list of product in shopping cart.
     *
     * @param id        component id
     * @param cartItems cart items
     */
    public ShoppingCartItemsList(final String id, final List<? extends CartItem> cartItems) {
        super(id, cartItems);
        rootCategory = categoryService.getRootCategory();

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void populateItem(final ListItem<CartItem> cartItemListItem) {

        final CartItem cartItem = cartItemListItem.getModelObject();

        final String skuCode = cartItem.getProductSkuCode();

        final ProductSku sku = productSkuService.getProductSkuBySkuCode(skuCode);

        final ProductSkuDecorator productSkuDecorator = decoratorFacade.decorate(
                sku,
                WicketUtil.getHttpServletRequest().getContextPath(),
                i18NWebSupport);


        cartItemListItem.add(
                createAddOneSkuLink(skuCode).setVisible(!cartItem.isGift())
        ).add(
                createRemoveAllSkuLink(skuCode).setVisible(!cartItem.isGift())
        ).add(
                createRemoveOneSkuLink(skuCode).setVisible(!cartItem.isGift())
        ).add(
                new Label(SKU_NUM_LABEL, skuCode)
        ).add(
                getProductLink(productSkuDecorator)
        ).add(
                new PriceView(PRICE_VIEW, new Pair<BigDecimal, BigDecimal>(cartItem.getListPrice(), cartItem.getPrice()), null, cartItem.getAppliedPromo(), false, true)
        ).add(
                new PriceView(LINE_TOTAL_VIEW, new Pair<BigDecimal, BigDecimal>(
                        cartItem.getPrice().multiply(cartItem.getQty()), null), null, null, false, false)
        );


        final TextField<BigDecimal> quantity = new TextField<BigDecimal>(QUANTITY_TEXT,
                new Model<BigDecimal>(cartItem.getQty()));

        cartItemListItem.add(
                quantity.setVisible(!cartItem.isGift())
        ).add(
                createAddSeveralSkuButton(skuCode, quantity).setVisible(!cartItem.isGift())
        );


        final String [] size = productSkuDecorator.getThumbnailImageSize(rootCategory);

        final String width = size[0];
        final String height = size[1];

        final String defaultImageRelativePath = productSkuDecorator.getImage(
                width, height,
                productSkuDecorator.getDefaultImageAttributeName());

        cartItemListItem.add(new ContextImage(DEFAULT_IMAGE, defaultImageRelativePath)
                .add(
                        new AttributeModifier(BaseComponent.HTML_WIDTH, width),
                        new AttributeModifier(BaseComponent.HTML_HEIGHT, height)
                )
        );


    }


    /**
     * Adjust quantity.
     *
     * @param productSkuCode sku code
     * @param qtyField       quantity input box
     * @return BookmarkablePageLink for remove one sku from cart command
     */
    private Button createAddSeveralSkuButton(final String productSkuCode, final TextField<BigDecimal> qtyField) {
        final Button adjustQuantityButton = new Button(QUANTITY_ADJUST_BUTTON) {
            @Override
            public void onSubmit() {
                String qty = qtyField.getInput();
                if (StringUtils.isNotBlank(qty)) {
                    qty = qty.replace(',', '.');
                }
                final String skuCode  = productSkuCode;
                /*try {
                    skuCode = URLEncoder.encode(productSkuCode, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    skuCode = productSkuCode;
                }*/


                //TODOv2 add flag for product is quantity with float point enabled for this product or not
                //ATM this is CPOINT
                if (NumberUtils.isNumber(qty) /*&& NumberUtils.toInt(qty) >= 1*/) {
                    qtyField.setConvertedInput(new BigDecimal(qty).setScale(Constants.DEFAULT_SCALE, BigDecimal.ROUND_HALF_UP));
                    setResponsePage(
                            ShoppingCartPage.class,
                            new PageParameters()
                                    .add(ShoppingCartCommand.CMD_SETQTYSKU, skuCode)
                                    .add(ShoppingCartCommand.CMD_SETQTYSKU_P_QTY, qty)
                    );


                } else {
                    qtyField.setConvertedInput(BigDecimal.ONE.setScale(Constants.DEFAULT_SCALE));
                    error(getLocalizer().getString("nonzerodigits", this, "Need positive integer value"));
                }
            }
        };
        adjustQuantityButton.setDefaultFormProcessing(true);
        return adjustQuantityButton;
    }

    /**
     * Get link to show product with selected in cart product sku.
     *
     * @param productSku product    sku
     * @return link to show product and selected SKU
     */
    private Link getProductLink(final ProductSkuDecorator productSku) {
        final Link productLink = ((AbstractWebPage) getPage()).getWicketSupportFacade().links()
                .newProductSkuLink(PRODUCT_LINK, productSku.getId());
        productLink.add(new Label(PRODUCT_NAME_LABEL, productSku.getName(getLocale().getLanguage())).setEscapeModelStrings(false));
        return productLink;
    }


    /**
     * Create BookmarkablePageLink for add one sku to cart command.
     *
     * @param skuCode sku code
     * @return BookmarkablePageLink for add one sku to cart command
     */
    private BookmarkablePageLink createAddOneSkuLink(final String skuCode) {
        final PageParameters paramsMap = new PageParameters();
        paramsMap.set(ShoppingCartCommand.CMD_ADDTOCART, skuCode);
        return new BookmarkablePageLink<ShoppingCartPage>(
                ADD_ONE_LINK,
                ShoppingCartPage.class,
                paramsMap
        );
    }

    /**
     * Create BookmarkablePageLink for remove one sku from cart command.
     *
     * @param skuCode sku code
     * @return BookmarkablePageLink for remove one sku from cart command
     */
    private BookmarkablePageLink createRemoveOneSkuLink(final String skuCode) {
        final PageParameters paramsMap = new PageParameters();
        paramsMap.set(ShoppingCartCommand.CMD_REMOVEONESKU, skuCode);
        return new BookmarkablePageLink<ShoppingCartPage>(
                REMOVE_ONE_LINK,
                ShoppingCartPage.class,
                paramsMap
        );
    }


    /**
     * Create BookmarkablePageLink for remove one sku from cart command.
     *
     * @param skuCode sku code
     * @return BookmarkablePageLink for remove one sku from cart command
     */
    private BookmarkablePageLink createRemoveAllSkuLink(final String skuCode) {
        final PageParameters paramsMap = new PageParameters();
        paramsMap.set(ShoppingCartCommand.CMD_REMOVEALLSKU, skuCode);
        return new BookmarkablePageLink<ShoppingCartPage>(
                REMOVE_ALL_LINK,
                ShoppingCartPage.class,
                paramsMap
        );
    }


}
