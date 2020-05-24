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
import org.yes.cart.domain.entity.Country;

/**
 * User: Igor Azarny iazarnmy@yahoo.com
 * Date: 15-Oct-2011
 * Time: 11:01:06 AM
 */

public class CountryRenderer extends ChoiceRenderer<Country> {

    private final String language;

    public CountryRenderer(final String language) {
        this.language = language;
    }

    /** {@inheritDoc} */
    @Override
    public Object getDisplayValue(final Country country) {

        final String i18n = country.getDisplayName() != null ? country.getDisplayName().getValue(language) : null;
        if (i18n != null) {
            return i18n;
        }
        return country.getName();

    }

    /** {@inheritDoc} */
    @Override
    public String getIdValue(final Country country, final int i) {
        return country.getCountryCode();
    }

}
