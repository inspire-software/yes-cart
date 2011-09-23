package org.yes.cart.web.page.component.cart;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.SkuPrice;
import org.yes.cart.service.domain.PriceService;
import org.yes.cart.service.misc.PluralFormService;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.page.component.BaseComponent;
import org.yes.cart.web.page.component.price.PriceView;

import java.math.BigDecimal;

/**
 * Igor Azarny iazarny@yahoo.com
 * Date: 18-Sep-2011
 * Time: 09:56:58
 */
public class SmallShoppingCartView extends BaseComponent {


    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    private static final String QTY_LABEL = "qtyLabel";
    private static final String SUB_TOTAL_VIEW = "subTotal";
    // ------------------------------------- MARKUP IDs END ---------------------------------- //

    private static final String [] pluralForms = new String [] {
            "item.form0",
            "item.form1",
            "item.form2"
    };

    @SpringBean(name = ServiceSpringKeys.PRICE_SERVICE)
    private PriceService priceService;

    @SpringBean(name = ServiceSpringKeys.PLURAL_FORM_SERVICE)
    private PluralFormService pluralFormService;

    /**
     * Construct small cart view.
     *
     * @param id component id.
     */
    public SmallShoppingCartView(final String id) {
        super(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onBeforeRender() {
        final ShoppingCart cart = ApplicationDirector.getShoppingCart();
        final Integer itemsInCart = cart.getCartItemsCount();
        final SkuPrice skuPrice = priceService.getGenericDao().getEntityFactory().getByIface(SkuPrice.class);
        skuPrice.setRegularPrice(cart.getCartSubTotal(cart.getCartItemList()));
        skuPrice.setCurrency(cart.getCurrencyCode());
        skuPrice.setQuantity(BigDecimal.ONE);

        final String resourceKey = pluralFormService.getPluralForm(
                cart.getCurrentLocale(),
                itemsInCart,
                pluralForms);

        add(
                new PriceView(
                        SUB_TOTAL_VIEW,
                        new Model<SkuPrice>(skuPrice),
                        true
                ).setVisible(!isCartEmpty())
        );

        add(
                new Label(
                        QTY_LABEL,
                        isCartEmpty()?
                        new StringResourceModel("no.item", this, null, itemsInCart):
                        new StringResourceModel(resourceKey, this, null, itemsInCart)
                )
        );

        super.onBeforeRender();
    }

    /**
     *
     * @return true in case of empty cart
     */
    public boolean isCartEmpty() {
        return ApplicationDirector.getShoppingCart().getCartItemsCount() == 0;
    }

}
