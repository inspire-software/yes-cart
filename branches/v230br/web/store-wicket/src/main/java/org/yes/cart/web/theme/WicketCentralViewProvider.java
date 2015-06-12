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

package org.yes.cart.web.theme;

import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.queryobject.NavigationContext;
import org.yes.cart.web.page.component.AbstractCentralView;

/**
 * User: denispavlov
 * Date: 10/11/2014
 * Time: 10:31
 */
public interface WicketCentralViewProvider {

    /**
     * Central view resolution.
     *
     * @param rendererLabel render view type (fir)
     * @param wicketComponentId component id
     * @param categoryId category id
     * @param navigationContext navigation context
     *
     * @return central view component
     */
    AbstractCentralView getCentralPanel(Pair<String, String> rendererLabel,
                                        String wicketComponentId,
                                        long categoryId,
                                        NavigationContext navigationContext);

}
