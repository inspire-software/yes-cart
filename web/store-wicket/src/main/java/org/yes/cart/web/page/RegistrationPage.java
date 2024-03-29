/*
 * Copyright 2009 Inspire-Software.com
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.yes.cart.web.page;

import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.protocol.https.RequireHttps;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.yes.cart.web.page.component.customer.auth.RegisterPanel;
import org.yes.cart.web.page.component.footer.StandardFooter;
import org.yes.cart.web.page.component.header.HeaderMetaInclude;
import org.yes.cart.web.page.component.header.StandardHeader;
import org.yes.cart.web.page.component.js.ServerSideJs;

/**
 * Igor Azarny iazarny@yahoo.com
 * Date: 22/10/11
 * Time: 18:45
 */
@RequireHttps
public class RegistrationPage  extends AbstractWebPage {



    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    private final static String CART_VIEW = "registrationView";
    // ------------------------------------- MARKUP IDs END ---------------------------------- //


    /**
     * Construct page.
     *
     * @param params page parameters
     */
    public RegistrationPage(final PageParameters params) {

        super(params);

        add(
                new FeedbackPanel(FEEDBACK)
        ).add(
                new RegisterPanel(CART_VIEW, RegisterPanel.NextPage.PROFILE)
        ).addOrReplace(
                new StandardFooter(FOOTER)
        ).addOrReplace(
                new StandardHeader(HEADER)
        ).addOrReplace(
                new ServerSideJs("serverSideJs")
        ).addOrReplace(
                new HeaderMetaInclude("headerInclude")
        );
    }

    /** {@inheritDoc} */
    @Override
    protected void onBeforeRender() {

        executeHttpPostedCommands();

        super.onBeforeRender();

        persistCartIfNecessary();
    }


    /**
     * Get page title.
     *
     * @return page title
     */
    @Override
    public IModel<String> getPageTitle() {
        return new StringResourceModel("registration",this);
    }
}
