package org.yes.cart.web.page.component.customer.wishlist;

import org.apache.wicket.markup.html.basic.Label;
import org.yes.cart.domain.entity.CustomerWishList;
import org.yes.cart.web.page.component.BaseComponent;
import org.yes.cart.web.support.constants.WebParametersKeys;
import org.yes.cart.web.util.WicketUtil;

import java.util.Collections;

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
        add(new Label("jsWishListItemAddedMsg",
                WicketUtil.createStringResourceModel(this, "wishListItemAdded" + wishListType,
                        Collections.<String, Object>singletonMap("sku", wishListSkuAdded))));
        add(new Label("jsWishListItemAdded", "<script type=\"text/javascript\"> $(document).ready(function() {ctx.showModalWindow($('.jsWishListItemAddedMsg'));});</script>")
                .setEscapeModelStrings(false));

        setVisible(wishListAdded);

        super.onBeforeRender();
    }
}
