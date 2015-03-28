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

package org.yes.cart.web.theme;

import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.component.IRequestablePage;
import org.apache.wicket.util.ClassProvider;

/**
 * User: denispavlov
 * Date: 13-03-22
 * Time: 4:17 PM
 */
public interface WicketPagesMounter {

    /**
     * Mount all pages that should be available on this storefront.
     *
     * @param webApplication web application to mount pages on
     */
    void mountPages(WebApplication webApplication);

    /**
     * @return page provider that returns theme specific home page.
     */
    ClassProvider<IRequestablePage> getHomePageProvider();

    /**
     * @return page provider that returns theme specific login page.
     */
    ClassProvider<IRequestablePage> getLoginPageProvider();

}
