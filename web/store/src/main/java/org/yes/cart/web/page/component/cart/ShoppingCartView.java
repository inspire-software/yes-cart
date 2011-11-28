package org.yes.cart.web.page.component.cart;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.yes.cart.domain.misc.Pair;
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
    }

    @Override
    protected void onBeforeRender() {


        add(
                new Form(CART_FORM).add(
                        new ShoppingCartItemsList(ITEMS_LIST, ApplicationDirector.getShoppingCart().getCartItemList())
                ).add(
                        new FeedbackPanel(FEEDBACK)
                ).add(
                        new PriceView(
                                SUB_TOTAL_VIEW,
                                new Pair<BigDecimal, BigDecimal>(
                                        ApplicationDirector.getShoppingCart().getCartSubTotal(),
                                        null),
                                ApplicationDirector.getShoppingCart().getCurrencyCode(),
                                true)
                )
        );


        super.onBeforeRender();
    }
}
