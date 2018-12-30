/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
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
package org.yes.cart.web.page.component.customer.logout;

import org.apache.wicket.Application;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.shoppingcart.ShoppingCartCommandFactory;
import org.yes.cart.web.page.AbstractWebPage;
import org.yes.cart.web.page.component.BaseComponent;

import java.util.HashMap;
import java.util.Map;

/**
 * User: iazarny@yahoo.com
 * Date: 1/20/13
 * Time: 6:02 PM
 */
public class LogoutPanel  extends BaseComponent {


    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    private static final String LOGOFF_LINK = "logoff";
    // ------------------------------------- MARKUP IDs END ---------------------------------- //


    @SpringBean(name = ServiceSpringKeys.CART_COMMAND_FACTORY)
    private ShoppingCartCommandFactory shoppingCartCommandFactory;

    /**
     * Construct logout panel.
     * @param id component id.
     */
    public LogoutPanel(final String id) {
        super(id);

        final StatelessForm form = new StatelessForm(LOGOFF_LINK);

        final Button submit = new Button("logoffCmd") {
            @Override
            public void onSubmit() {
                final Map<String, Object> cmd = new HashMap<>();
                cmd.put(ShoppingCartCommand.CMD_LOGOUT, ShoppingCartCommand.CMD_LOGOUT);
                shoppingCartCommandFactory.execute(ShoppingCartCommand.CMD_LOGOUT, getCurrentCart(), cmd);
                ((AbstractWebPage) getPage()).persistCartIfNecessary();
                LogoutPanel.this.setResponsePage(Application.get().getHomePage());
            }
        };

        submit.setDefaultFormProcessing(false);

        form.add(submit);

        add(form);

    }


    /** {@inheritDoc} */
    @Override
    public boolean isVisible() {
        return ShoppingCart.LOGGED_IN == getCurrentCart().getLogonState()
                || AuthenticatedWebSession.get().isSignedIn();
    }
}
