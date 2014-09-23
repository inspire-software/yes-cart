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

/**
 * User: denispavlov
 * Date: 13-08-28
 * Time: 3:24 PM
 */
public interface WicketResourceMounter {

    /**
     * Enable visibility for resources in wicket application
     *
     * @param webApplication  web application to mount resources on
     */
    void enableResourceAccess(WebApplication webApplication);

    /**
     * Mount all dynamic resources that should be available on this storefront.
     *
     * @param webApplication web application to mount resources on
     */
    void mountResources(WebApplication webApplication);


}
