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
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.entity.AttrValue;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.shoppingcart.Total;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.page.CheckoutPage;
import org.yes.cart.web.page.component.BaseComponent;
import org.yes.cart.web.page.component.price.PriceView;
import org.yes.cart.web.support.constants.StorefrontServiceSpringKeys;
import org.yes.cart.web.support.service.ContentServiceFacade;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 10/8/11
 * Time: 1:18 PM
 */
public class ShoppingCartView extends BaseComponent {


    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    private static final String SUB_TOTAL_VIEW = "subTotalView";
    private static final String ITEMS_LIST = "itemsList";
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

    /**
     * Construct shopping cart view.
     *
     * @param id component id
     */
    public ShoppingCartView(final String id) {
        super(id);

        final Shop shop = ApplicationDirector.getCurrentShop();
        final long shopId = shop.getShopId();
        final String lang = getLocale().getLanguage();

        final AttrValue valCoupons = shop.getAttributeByCode(AttributeNamesKeys.Shop.CART_UPDATE_ENABLE_COUPONS);
        final boolean allowCoupons = valCoupons != null && valCoupons.getVal() != null && Boolean.valueOf(valCoupons.getVal());

        final AttrValue valMessage = shop.getAttributeByCode(AttributeNamesKeys.Shop.CART_UPDATE_ENABLE_ORDER_MSG);
        final boolean allowMessages = valMessage != null && valMessage.getVal() != null && Boolean.valueOf(valMessage.getVal());

        final ShoppingCart cart = ApplicationDirector.getShoppingCart();
        final Total total = cart.getTotal();

        final Map<String, Object> dynaCtx = new HashMap<String, Object>();
        dynaCtx.put("shop", shop);
        dynaCtx.put("shoppingCart", cart);


        final Form cartForm = new StatelessForm(CART_FORM);

        cartForm.addOrReplace(new ShoppingCartItemsList(ITEMS_LIST, cart.getCartItemList()));
        cartForm.addOrReplace(new FeedbackPanel(FEEDBACK));
        cartForm.addOrReplace(
                new PriceView(
                        SUB_TOTAL_VIEW,
                        new Pair<BigDecimal, BigDecimal>(total.getListSubTotal(), total.getSubTotal()),
                        cart.getCurrencyCode(),
                        total.getAppliedOrderPromo(), true, true)
        );

        final ShoppingCartCouponsList couponsList = new ShoppingCartCouponsList(COUPON_LIST, cart.getCoupons(), cart.getAppliedCoupons());
        couponsList.setVisible(allowCoupons);
        cartForm.addOrReplace(couponsList);

        // TOTALS

        String subTotalInclude = getContentInclude(shopId, "shopping_cart_checkout_include", lang, dynaCtx);
        cartForm.addOrReplace(new Label(SUBTOTAL_INCLUDE, subTotalInclude).setEscapeModelStrings(false));

        cartForm.addOrReplace(new BookmarkablePageLink<CheckoutPage>(CHECKOUT_LINK, CheckoutPage.class).setVisible(!ApplicationDirector.getShoppingCart().getCartItemList().isEmpty()));

        // COUPONS

        String couponsInclude = getContentInclude(shopId, "shopping_cart_coupons_include", lang, dynaCtx);
        cartForm.addOrReplace(new Label(COUPON_INCLUDE, couponsInclude).setEscapeModelStrings(false).setVisible(allowCoupons));

        final TextField<String> couponCode = new TextField<String>(COUPON_CODE_INPUT, new Model<String>());
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

        String messageInclude = getContentInclude(shopId, "shopping_cart_message_include", lang, dynaCtx);
        cartForm.addOrReplace(new Label(ORDERMSG_INCLUDE, messageInclude).setEscapeModelStrings(false).setVisible(allowMessages));

        final TextField<String> messageInput = new TextField<String>(ORDERMSG_INPUT, new Model<String>());
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
        messageBtn.setVisible(allowCoupons);
        cartForm.addOrReplace(messageBtn);


        addOrReplace(cartForm);

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
