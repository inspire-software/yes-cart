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

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.web.service.wicketsupport.WicketSupportFacade;
import org.yes.cart.web.support.constants.WicketServiceSpringKeys;

import java.io.Serializable;
import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 10/9/11
 * Time: 11:30 AM
 */
public class ShoppingCartCouponsList extends ListView<String> {


    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //


    private static final String LIST_ITEM_TEXT = "couponsListItem";
    private static final String REMOVE_COUPON = "couponsListItemRemoveLink";


    // ------------------------------------- MARKUP IDs END ---------------------------------- //


    @SpringBean(name = WicketServiceSpringKeys.WICKET_SUPPORT_FACADE)
    private WicketSupportFacade wicketSupportFacade;

    private List<String> appliedCoupons;

    /**
     * Construct list of product in shopping cart.
     *
     * @param id        component id
     * @param coupons   coupons
     */
    public ShoppingCartCouponsList(final String id, final List<String> coupons, final List<String> appliedCoupons) {
        super(id, coupons);
        this.appliedCoupons = appliedCoupons;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void populateItem(final ListItem<String> couponListItem) {

        final String couponCode = couponListItem.getModelObject();

        final String messageKey;
        if (appliedCoupons.contains(couponCode)) {
            messageKey = "appliedPromoCode";
        } else {
            messageKey = "pendingPromoCode";
        }

        couponListItem.add(new Label(LIST_ITEM_TEXT,
                getLocalizer().getString(messageKey, this.getParent(),
                        new Model<Serializable>(new Object[] { couponCode }))));

        couponListItem.add(wicketSupportFacade.links()
                .newRemoveCouponLink(REMOVE_COUPON, couponCode,
                        (Class) getPage().getClass(), getPage().getPageParameters()));

    }

}
