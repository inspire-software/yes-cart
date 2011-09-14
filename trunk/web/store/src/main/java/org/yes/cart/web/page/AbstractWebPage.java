package org.yes.cart.web.page;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/10/11
 * Time: 10:22 AM
 */
public class AbstractWebPage extends WebPage {

    /**
     *  Construct page.
     * @param params page parameters
     */
    public AbstractWebPage(final PageParameters params) {
        super(params);
        setStatelessHint(true);
    }



    /*    protected void processCommands(final Map params) {
        ShoppingCartCommand cartCommand = getShoppingCartCommandFactory().create(params);
        if (cartCommand != null) {
            getRequestRuntimeContainer().getShoppingCart().accept(cartCommand);
        }
    }
*/



}
