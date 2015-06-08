package org.yes.cart.web.page.component.customer.wishlist;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.yes.cart.domain.entity.CustomerWishList;
import org.yes.cart.web.page.component.BaseComponent;
import org.yes.cart.web.support.constants.WebParametersKeys;

import java.io.Serializable;

/**
 * User: Denis Lozenko
 * Date: 3/10/15
 * Time: 1:51 PM
 */
public class WishListNotification extends BaseComponent {

    public WishListNotification(final String id) {
        super(id);
    }

    @Override
    protected void onBeforeRender() {

        final String wishListSkuAdded = getPage().getPageParameters().get(WebParametersKeys.WISHLIST_ITEM_ADDED).toString(null);
        final String wishListType = getPage().getPageParameters().get(WebParametersKeys.WISHLIST_ITEM_TYPE).toString(CustomerWishList.SIMPLE_WISH_ITEM);
        final boolean wishListAdded = wishListSkuAdded != null;
        add(new Label("jsWishListItemAddedMsg", new StringResourceModel("wishListItemAdded" + wishListType, this, new Model<Serializable>(new Object[] {wishListSkuAdded}))));
        add(new Label("jsWishListItemAdded", "<script type=\"text/javascript\"> $(document).ready(function() {ctx.showModalWindow($('.jsWishListItemAddedMsg'));});</script>")
                .setEscapeModelStrings(false));

        setVisible(wishListAdded);

        super.onBeforeRender();
    }
}
