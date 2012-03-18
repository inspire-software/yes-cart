package org.yes.cart.web.page.component.product;

import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.web.page.component.BaseComponent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Igor Azarny iazarny@yahoo.com
 * Date: 17-Sep-2011
 * Time: 12:22:57
 */
public class SkuListView extends BaseComponent {

    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    private final static String SKU_LIST = "skuList";
    private final static String SKU_VIEW = "skuView";
    // ------------------------------------- MARKUP IDs END   ---------------------------------- //

    private final ProductSku currentSku;
    private final List<ProductSku> skusToShow;
    private final boolean productView;


    /**
     * Construct sku list view.
     *
     * @param id         component id
     * @param skus       list of skus
     * @param currentSku sku, that shown at this momen
     * @param productView is it product or particular sku view
     */
    public SkuListView(final String id,
                       final Collection<ProductSku> skus,
                       final ProductSku currentSku,
                       final boolean productView) {
        super(id);
        this.currentSku = currentSku;
        this.productView = productView;
        skusToShow = new ArrayList<ProductSku>(skus);
        //skusToShow.remove(currentSku);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onBeforeRender() {
        add(
                new ListView<ProductSku>(SKU_LIST, skusToShow) {
                    protected void populateItem(final ListItem<ProductSku> productSkuListItem) {
                        productSkuListItem.add(
                                new SkuInListView(SKU_VIEW, productSkuListItem.getModelObject())
                        );
                    }
                }
        );
        super.onBeforeRender();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isVisible() {
       // return super.isVisible() && !skusToShow.isEmpty() && productView;
        return super.isVisible() && skusToShow.size() > 1;
    }
}
