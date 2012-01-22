package org.yes.cart.web.page.payment.callback;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.web.page.AbstractWebPage;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 1/22/12
 * Time: 5:17 PM
 */
public class LiqPayReturnUrlPage  extends AbstractWebPage {

    private static final Logger LOG = LoggerFactory.getLogger(LiqPayReturnUrlPage.class);


    /**
     * Construct page.
     *
     * @param params page parameters
     */
    public LiqPayReturnUrlPage(final PageParameters params) {
        super(params);

        add(
                new FeedbackPanel("feedback")
        ).add(
                new Label("infoLabel", "Hi there")
        );
    }


}
