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

package org.yes.cart.web.service.wicketsupport.impl;

import org.yes.cart.web.service.wicketsupport.LinksSupport;
import org.yes.cart.web.service.wicketsupport.PaginationSupport;
import org.yes.cart.web.service.wicketsupport.WicketSupportFacade;

/**
 * User: denispavlov
 * Date: 13-06-28
 * Time: 9:27 AM
 */
public class WicketSupportFacadeImpl implements WicketSupportFacade {

    private final LinksSupport linksSupport;
    private final PaginationSupport paginationSupport;

    public WicketSupportFacadeImpl(final LinksSupport linksSupport,
                                   final PaginationSupport paginationSupport) {
        this.linksSupport = linksSupport;
        this.paginationSupport = paginationSupport;
    }

    /** {@inheritDoc} */
    @Override
    public LinksSupport links() {
        return linksSupport;
    }

    /** {@inheritDoc} */
    @Override
    public PaginationSupport pagination() {
        return paginationSupport;
    }
}
