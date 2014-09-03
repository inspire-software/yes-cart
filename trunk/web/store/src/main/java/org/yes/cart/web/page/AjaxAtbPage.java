package org.yes.cart.web.page;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.web.page.component.cart.SmallShoppingCartView;

/**
 * User: denispavlov
 * Date: 13-08-31
 * Time: 7:16 PM
 */
public class AjaxAtbPage extends AbstractWebPage {

    public AjaxAtbPage(final PageParameters params) {
        super(params);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onBeforeRender() {

        executeHttpPostedCommands();

        addOrReplace(new SmallShoppingCartView("smallCart"));
        addOrReplace(new Label("productAddedMsg",
                new StringResourceModel("itemAdded", this, null, getPageParameters().get(ShoppingCartCommand.CMD_ADDTOCART))));
        super.onBeforeRender();

        persistCartIfNecessary();

    }

}
