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
import org.yes.cart.web.page.component.customer.auth.LoginPanel;
import org.yes.cart.web.page.component.footer.StandardFooter;
import org.yes.cart.web.page.component.header.HeaderMetaInclude;
import org.yes.cart.web.page.component.header.StandardHeader;
import org.yes.cart.web.page.component.js.ServerSideJs;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 10/16/11
 * Time: 5:21 PM
 */
@RequireHttps
public class LoginPage extends AbstractWebPage {


    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    private final static String AUTH_VIEW = "authView";
    // ------------------------------------- MARKUP IDs END ---------------------------------- //


    /**
     * Construct page.
     *
     * @param params page parameters
     */
    public LoginPage(final PageParameters params) {

        super(params);

        add(
                new FeedbackPanel(FEEDBACK)
        ).add(
                new LoginPanel(AUTH_VIEW, LoginPanel.NextPage.HOME)
        ).add(
                new StandardFooter(FOOTER)
        ).add(
                new StandardHeader(HEADER)
        ).add(
                new ServerSideJs("serverSideJs")
        ).add(
                new HeaderMetaInclude("headerInclude")
        );


    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onBeforeRender() {

        executeHttpPostedCommands();

        if ("1".equals(getPageParameters().get("pending").toString("0"))) {
            info(getLocalizer().getString("accountActivationPending", this));
        }

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
        return new StringResourceModel("login",this);
    }


}
