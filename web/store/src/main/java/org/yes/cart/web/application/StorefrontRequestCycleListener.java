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
package org.yes.cart.web.application;

import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.cycle.AbstractRequestCycleListener;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.handler.RedirectRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.web.support.seo.SeoEntryIdInvalidException;

/**
 * User: iazarny@yahoo.com
 * Date: 2/3/13
 * Time: 12:10 PM
 */
public class StorefrontRequestCycleListener extends AbstractRequestCycleListener {

    private static final Logger LOG = LoggerFactory.getLogger(ShopCodeContext.getShopCode());

    /** {@inheritDoc} */
    public IRequestHandler onException(final RequestCycle cycle, final Exception ex) {
        if (ex instanceof SeoEntryIdInvalidException)  {
            LOG.error(ex.getMessage());
            return new RedirectRequestHandler("/"); //CPOINT add more sophisticated error handling if require
        }
        return super.onException(cycle, ex);
    }
}
