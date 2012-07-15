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

package org.yes.cart.web.page.component.util;

import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.yes.cart.domain.misc.Pair;

/**
 * Render an select option from {@link Pair} of key - value.
 * <p/>
 * User: Igor Azarny iazarnmy@yahoo.com
 * Date: 15-Oct-2011
 * Time: 11:01:06 AM
 */

public class PairChoiceRenderer extends ChoiceRenderer {

    /** {@inheritDoc} */
    public Object getDisplayValue(final Object object) {
        return ((Pair<String, String>)object).getSecond();
    }

    /** {@inheritDoc} */
    @Override
    public String getIdValue(final Object object, final int i) {
        if (object instanceof Pair) {
            Pair<String, String> pair = (Pair<String, String>) object;
            return pair.getFirst();
        }
        return (String) object;
    }


}
