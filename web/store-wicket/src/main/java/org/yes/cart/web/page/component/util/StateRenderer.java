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

package org.yes.cart.web.page.component.util;

import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.yes.cart.domain.entity.State;

/**
 * User: Igor Azarny iazarnmy@yahoo.com
 * Date: 15-Oct-2011
 * Time: 11:01:06 AM
 */
public class StateRenderer extends ChoiceRenderer<State> {

    private final String language;

    public StateRenderer(final String language) {
        this.language = language;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getDisplayValue(final State state) {

        final String i18n = state.getDisplayName() != null ? state.getDisplayName().getValue(language) : null;
        if (i18n != null) {
            return i18n;
        }
        return state.getName();

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getIdValue(final State state, final int i) {
        return state.getStateCode();
    }

}
