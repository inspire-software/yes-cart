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

package org.yes.cart.web.page.payment.callback;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.yes.cart.web.page.AbstractWebPage;
import org.yes.cart.web.support.util.HttpUtil;
import org.yes.cart.web.util.WicketUtil;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 1/22/12
 * Time: 5:17 PM
 */
public class LiqPayReturnUrlPage  extends AbstractWebPage {


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
                new Label("infoLabel", "Hi there " + HttpUtil.dumpRequest(WicketUtil.getHttpServletRequest()) )
        );
    }


}
