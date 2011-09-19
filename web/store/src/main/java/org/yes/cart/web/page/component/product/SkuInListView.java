package org.yes.cart.web.page.component.product;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.web.page.HomePage;
import org.yes.cart.web.page.component.BaseComponent;
import org.yes.cart.web.support.constants.WebParametersKeys;
import org.yes.cart.web.util.WicketUtil;

/**
 * Igor Azarny iazarny@yahoo.com
 * Date: 17-Sep-2011
 * Time: 11:07:59
 */
public class SkuInListView  extends BaseComponent {

    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    private final static String SKU_CODE_LINK = "skuCodeLink";
    private final static String SKU_CODE_LABEL = "skuCode";
    private final static String SKU_NAME_LINK = "skuNameLink";
    private final static String SKU_NAME_LABEL = "skuName";
    // ------------------------------------- MARKUP IDs END ------------------------------------ //

    private final ProductSku sku;


    /**
     * Construct sku in list view.
     * @param id component id
     * @param sku given {@link ProductSku} to show
     */
    public SkuInListView(final String id, final ProductSku sku) {
        super(id);
        this.sku = sku;
    }

    /** {@inheritDoc} */
    @Override
    protected void onBeforeRender() {

        final PageParameters linkToSkuParameters = WicketUtil.getFilteredRequestParameters(getPage().getPageParameters());
        linkToSkuParameters.set(WebParametersKeys.SKU_ID, sku.getId());

        add(
                new BookmarkablePageLink(SKU_CODE_LINK, HomePage.class, linkToSkuParameters)
                    .add(new Label(SKU_CODE_LABEL, sku.getCode()))
        );

        add(
                new BookmarkablePageLink(SKU_NAME_LINK, HomePage.class, linkToSkuParameters)
                    .add(new Label(SKU_NAME_LABEL, sku.getName()))
        );

        super.onBeforeRender();

    }
}
