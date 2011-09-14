package org.yes.cart.web.page.component.price;

import org.yes.cart.web.page.component.BaseComponent;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.domain.entity.SkuPrice;
import org.yes.cart.domain.misc.SkuPriceQuantityComparatorImpl;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.basic.Label;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Igor Azarny iazarny@yahoo.com
 * Date: 17-Sep-2011
 * Time: 13:51:49
 */
public class PriceTierView  extends BaseComponent {

    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    /** Single item price panel*/
    private final static String PRICE_VIEW = "priceView";
    private final static String PRICE_TIERS_LIST = "skusPriceTiers";
    private final static String QUANTITY_LABEL = "quantity";
    // ------------------------------------- MARKUP IDs END ---------------------------------- //


    private List<SkuPrice> skuPrices;

    /**
     * Construct price tiers view.
     * @param id component id
     * @param rawPrices list of prices
     */
    public PriceTierView(final String id, final Collection<SkuPrice> rawPrices) {
        super(id);
        final String currency = ApplicationDirector.getShoppingCart().getCurrencyCode();
        skuPrices = new ArrayList<SkuPrice>();
        for(SkuPrice skuPrice : rawPrices) {
            if (skuPrice.getCurrency().equals(currency)) {
                skuPrices.add(skuPrice);
            }
        }
        Collections.sort(skuPrices, new SkuPriceQuantityComparatorImpl());
    }

    @Override
    protected void onBeforeRender() {
        new ListView<SkuPrice>(PRICE_TIERS_LIST, skuPrices) {
            protected void populateItem(ListItem<SkuPrice> listItem) {
                listItem.add(
                        new Label(QUANTITY_LABEL, String.valueOf(listItem.getModelObject().getQuantity().intValue()))
                );
                listItem.add(
                        new PriceView(PRICE_VIEW, listItem.getModel(), true)
                );
            }
        };
        super.onBeforeRender();
    }

    @Override
    public boolean isVisible() {
        return super.isVisible() && !skuPrices.isEmpty();
    }
}
