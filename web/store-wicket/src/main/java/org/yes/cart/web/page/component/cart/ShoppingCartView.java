/*
 * Copyright 2009 Inspire-Software.com
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
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.value.ValueMap;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.entity.PriceModel;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.shoppingcart.*;
import org.yes.cart.utils.MoneyUtils;
import org.yes.cart.web.page.CheckoutPage;
import org.yes.cart.web.page.component.BaseComponent;
import org.yes.cart.web.page.component.price.PriceView;
import org.yes.cart.web.support.constants.StorefrontServiceSpringKeys;
import org.yes.cart.web.support.service.CheckoutServiceFacade;
import org.yes.cart.web.support.service.ContentServiceFacade;
import org.yes.cart.web.support.service.ProductServiceFacade;

import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 10/8/11
 * Time: 1:18 PM
 */
public class ShoppingCartView extends BaseComponent {


    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    private static final String SUB_TOTAL_VIEW = "subTotalView";
    private static final String ITEMS_LIST = "itemsList";
    private static final String SAVED_LIST = "savedList";
    private static final String CART_FORM = "cartForm";

    private static final String COUPON_INCLUDE = "couponsInclude";
    private static final String COUPON_LIST = "couponsList";
    private static final String COUPON_CODE_INPUT = "couponInput";
    private static final String COUPON_CODE_ADD_BTN = "addCouponBtn";

    private final static String SUBTOTAL_INCLUDE = "subTotalInclude";
    private final static String CHECKOUT_LINK = "checkoutLink";

    private final static String ORDERMSG_INCLUDE = "orderMessageInclude";
    private static final String ORDERMSG_INPUT = "orderMessageInput";
    private static final String ORDERMSG_BTN = "orderMessageBtn";

    private static final String DELIVERY_COST_LABEL = "deliveryCost";
    private static final String TAX_LABEL = "tax";
    private static final String TOTAL_LABEL = "total";
    private static final String GRAND_TOTAL_LABEL = "grandTotal";
    private static final String DELIVERY_LIST = "deliveryList";
    private static final String NEXT_LINK = "nextLink";

    private static final String MULTIPLE_DELIVERY_CHECKBOX = "multipleDelivery";
    private static final String MULTIPLE_DELIVERY_LABEL = "multipleDeliveryLabel";
    // ------------------------------------- MARKUP IDs END ---------------------------------- //


    @SpringBean(name = StorefrontServiceSpringKeys.CONTENT_SERVICE_FACADE)
    private ContentServiceFacade contentServiceFacade;

    @SpringBean(name = StorefrontServiceSpringKeys.PRODUCT_SERVICE_FACADE)
    private ProductServiceFacade productServiceFacade;

    @SpringBean(name = StorefrontServiceSpringKeys.CHECKOUT_SERVICE_FACADE)
    private CheckoutServiceFacade checkoutServiceFacade;

    /**
     * Construct shopping cart view.
     *
     * @param id component id
     */
    public ShoppingCartView(final String id) {
        super(id);

        final Shop shop = getCurrentShop();

        final boolean allowCoupons = shop.isAttributeValueByCodeTrue(AttributeNamesKeys.Shop.CART_UPDATE_ENABLE_COUPONS);

        final boolean allowMessages = shop.isAttributeValueByCodeTrue(AttributeNamesKeys.Shop.CART_UPDATE_ENABLE_ORDER_MSG);

        final ShoppingCart cart = getCurrentCart();

        final CartValidityModel validation = checkoutServiceFacade.validateCart(cart);

        final PriceModel model = productServiceFacade.getCartItemsTotal(cart);
        final Total total = cart.getTotal();

        final Form cartForm = new StatelessForm(CART_FORM);

        cartForm.addOrReplace(new ShoppingCartItemsList(ITEMS_LIST, cart.getCartItemList()).setVisible(cart.getCartItemsCount() > 0));
        cartForm.addOrReplace(new ShoppingCartSavedItemsList(SAVED_LIST, new Model<>(cart.getCustomerEmail())));

        cartForm.addOrReplace(
                new PriceView(
                        SUB_TOTAL_VIEW,
                        model,
                        total.getAppliedOrderPromo(), true, true,
                        model.isTaxInfoEnabled(), model.isTaxInfoShowAmount())
        );

        final ShoppingCartCouponsList couponsList = new ShoppingCartCouponsList(COUPON_LIST, cart.getCoupons(), cart.getAppliedCoupons());
        couponsList.setVisible(allowCoupons);
        cartForm.addOrReplace(couponsList);

        final boolean disabledCheckout = validation.isCheckoutBlocked();

        // TOTALS
        final boolean cartIsNotEmpty = cart.getCartItemsCount() > 0 && MoneyUtils.isPositive(total.getTotalAmount());
        cartForm.addOrReplace(new Label(SUBTOTAL_INCLUDE, "").setVisible(cartIsNotEmpty));

        cartForm.addOrReplace(new BookmarkablePageLink<CheckoutPage>(CHECKOUT_LINK, CheckoutPage.class).setVisible(cartIsNotEmpty && !disabledCheckout));

        // COUPONS
        cartForm.addOrReplace(new Label(COUPON_INCLUDE, "").setVisible(allowCoupons));

        final TextField<String> couponCode = new TextField<>(COUPON_CODE_INPUT, new Model<>());
        couponCode.setVisible(allowCoupons);
        cartForm.addOrReplace(couponCode);

        final Button couponBtn = new Button(COUPON_CODE_ADD_BTN) {
            @Override
            public void onSubmit() {

                final String code = couponCode.getModelObject();

                if (StringUtils.isNotBlank(code)) {

                    setResponsePage(
                            getPage().getPageClass(),
                            new PageParameters()
                                    .add(ShoppingCartCommand.CMD_ADDCOUPON, code)
                    );

                } else {
                    error(getLocalizer().getString("noEmptyCoupons", this, "Need non empty coupon code"));
                }
            }
        };
        couponBtn.setVisible(allowCoupons);
        cartForm.addOrReplace(couponBtn);

        // MESSAGING

        cartForm.addOrReplace(new Label(ORDERMSG_INCLUDE, "").setEscapeModelStrings(false).setVisible(allowMessages));

        final TextField<String> messageInput = new TextField<>(ORDERMSG_INPUT, new Model<>());
        messageInput.setVisible(allowMessages);
        cartForm.addOrReplace(messageInput);

        final Button messageBtn = new Button(ORDERMSG_BTN) {
            @Override
            public void onSubmit() {

                final String msg = messageInput.getModelObject();

                if (StringUtils.isNotBlank(msg)) {

                    setResponsePage(
                            getPage().getPageClass(),
                            new PageParameters()
                                    .add(ShoppingCartCommand.CMD_SETORDERMSG, msg)
                    );

                }
            }
        };
        messageBtn.setVisible(allowMessages);
        cartForm.addOrReplace(messageBtn);


        addOrReplace(cartForm);

    }

    private void displayCartValidationMessages() {

        final ShoppingCart cart = getCurrentCart();

        final CartValidityModel validation = checkoutServiceFacade.validateCart(cart);

        if (validation.getMessages() != null) {
            for (final CartValidityModelMessage message : validation.getMessages()) {
                try {
                    if (CartValidityModelMessage.MessageType.ERROR == message.getMessageType()) {
                        error(getLocalizer().getString(message.getMessageKey(), this,
                                new Model<>(new ValueMap(message.getMessageArgs()))));
                    } else if (CartValidityModelMessage.MessageType.INFO == message.getMessageType()) {
                        info(getLocalizer().getString(message.getMessageKey(), this,
                                new Model<>(new ValueMap(message.getMessageArgs()))));
                    } else if (CartValidityModelMessage.MessageType.SUCCESS == message.getMessageType()) {
                        success(getLocalizer().getString(message.getMessageKey(), this,
                                new Model<>(new ValueMap(message.getMessageArgs()))));
                    } else {
                        warn(getLocalizer().getString(message.getMessageKey(), this,
                                new Model<>(new ValueMap(message.getMessageArgs()))));
                    }
                } catch (MissingResourceException exp) {
                    warn(message.getMessageKey());
                }
            }
        }


    }

    @Override
    protected void onBeforeRender() {


        final Shop shop = getCurrentShop();
        final ShoppingCart cart = getCurrentCart();
        final long shopId = shop.getShopId();
        final String lang = getLocale().getLanguage();

        final Map<String, Object> dynaCtx = new HashMap<>();
        dynaCtx.put("shop", shop);
        dynaCtx.put("shoppingCart", cart);

        final Component form = get(CART_FORM);

        final boolean cartIsNotEmpty = cart.getCartItemsCount() > 0;
        String subTotalInclude = getContentInclude(shopId, "shopping_cart_checkout_include", lang, dynaCtx);
        form.get(SUBTOTAL_INCLUDE).replaceWith(new Label(SUBTOTAL_INCLUDE, subTotalInclude).setVisible(cartIsNotEmpty).setEscapeModelStrings(false));

        final boolean allowCoupons = shop.isAttributeValueByCodeTrue(AttributeNamesKeys.Shop.CART_UPDATE_ENABLE_COUPONS);
        String couponsInclude = getContentInclude(shopId, "shopping_cart_coupons_include", lang, dynaCtx);
        form.get(COUPON_INCLUDE).replaceWith(new Label(COUPON_INCLUDE, couponsInclude).setEscapeModelStrings(false).setVisible(allowCoupons));

        final boolean allowMessages = shop.isAttributeValueByCodeTrue(AttributeNamesKeys.Shop.CART_UPDATE_ENABLE_ORDER_MSG);
        String messageInclude = getContentInclude(shopId, "shopping_cart_message_include", lang, dynaCtx);
        form.get(ORDERMSG_INCLUDE).replaceWith(new Label(ORDERMSG_INCLUDE, messageInclude).setEscapeModelStrings(false).setVisible(allowMessages));

        displayCartValidationMessages();

        super.onBeforeRender();
    }

    private String getContentInclude(long shopId, String contentUri, String lang, Map<String, Object> dynaCtx) {
        String content = contentServiceFacade.getDynamicContentBody(
                contentUri, shopId, lang, dynaCtx);
        if (content == null) {
            content = "";
        }
        return content;
    }


}
