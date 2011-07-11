package org.yes.cart.web.page;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/10/11
 * Time: 10:27 AM
 */
public class HomePage extends AbstractWebPage {

    /**
     * Construct home page.
     * @param params  page parameters
     */
    public HomePage(final PageParameters params) {
        super(params);
        add(new Label("version", getApplication().getFrameworkSettings().getVersion()));
    }
}
