/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
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
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.value.ValueMap;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.impl.LogoutCommandImpl;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.page.AbstractWebPage;
import org.yes.cart.web.page.component.BaseComponent;

/**
 * User: iazarny@yahoo.com
 * Date: 1/20/13
 * Time: 6:02 PM
 */
public class LogoutPanel  extends BaseComponent {


    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    private static final String LOGOFF_LINK = "logoff";
    private static final String LOGOFF_LABEL = "logoffLabel";
    private static final String LOGOFF_TITLE = "logoffTitle";
    // ------------------------------------- MARKUP IDs END ---------------------------------- //


    /**
     * Construct logout panel.
     * @param id component id.
     */
    public LogoutPanel(final String id) {
        super(id);
    }

    protected void onBeforeRender() {

        final Link logOff = getWicketSupportFacade().links().newLogOffLink(LOGOFF_LINK,
                getPage().getPageParameters());

        logOff.add(new AttributeModifier(
                HTML_TITLE,
                getSalutation(LOGOFF_TITLE, ApplicationDirector.getShoppingCart().getCustomerName())));
        logOff.add(new Label(
                LOGOFF_LABEL,
                getLocalizer().getString(LOGOFF_LABEL, this)));

        addOrReplace(logOff);

        super.onBeforeRender();
    }

    /**
     * Get localized salutation.
     * @param salutationKey salutation localization key
     * @param name customer name
     * @return localized salutation
     */
    private String getSalutation(final String salutationKey, final String name) {
        final ValueMap map = new ValueMap();
        map.put("name", name);
        final StringResourceModel labelModel = new StringResourceModel(salutationKey, this,
                new Model<ValueMap>(map));
        return labelModel.getString();
    }

    /** {@inheritDoc} */
    public boolean isVisible() {
        return ShoppingCart.LOGGED_IN == ApplicationDirector.getShoppingCart().getLogonState();
    }
}
