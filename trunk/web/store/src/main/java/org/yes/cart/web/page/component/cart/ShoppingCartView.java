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
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.shoppingcart.Total;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.page.component.BaseComponent;
import org.yes.cart.web.page.component.price.PriceView;

import java.math.BigDecimal;

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
    private static final String COUPON_LIST = "couponsList";
    private static final String COUPON_CODE_INPUT = "couponInput";
    private static final String COUPON_CODE_ADD_BTN = "addCouponBtn";

    private static final String DELIVERY_COST_LABEL = "deliveryCost";
    private static final String TAX_LABEL = "tax";
    private static final String TOTAL_LABEL = "total";
    private static final String GRAND_TOTAL_LABEL = "grandTotal";
    private static final String DELIVERY_LIST = "deliveryList";
    private static final String NEXT_LINK = "nextLink";


    private static final String MULTIPLE_DELIVERY_CHECKBOX = "multipleDelivery";
    private static final String MULTIPLE_DELIVERY_LABEL = "multipleDeliveryLabel";
    // ------------------------------------- MARKUP IDs END ---------------------------------- //


    /**
     * Construct shopping cart view.
     *
     * @param id component id
     */
    public ShoppingCartView(final String id) {
        super(id);


        final ShoppingCart cart = ApplicationDirector.getShoppingCart();
        final Total total = cart.getTotal();

        final Form cartForm = new Form(CART_FORM);

        cartForm.addOrReplace(new ShoppingCartItemsList(ITEMS_LIST, cart.getCartItemList()));
        cartForm.addOrReplace(new FeedbackPanel(FEEDBACK));
        cartForm.addOrReplace(
                new PriceView(
                        SUB_TOTAL_VIEW,
                        new Pair<BigDecimal, BigDecimal>(total.getListSubTotal(), total.getSubTotal()),
                        cart.getCurrencyCode(),
                        total.getAppliedOrderPromo(), true, true)
        );

        cartForm.addOrReplace(new ShoppingCartCouponsList(COUPON_LIST, cart.getCoupons(), cart.getAppliedCoupons()));

        final TextField<String> couponCode = new TextField<String>(COUPON_CODE_INPUT, new Model<String>());
        cartForm.addOrReplace(couponCode);

        cartForm.addOrReplace(new Button(COUPON_CODE_ADD_BTN) {
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
                    error(getLocalizer().getString("noemptycoupons", this, "Need non empty coupon code"));
                }
            }
        });


        addOrReplace(cartForm);

    }


}
