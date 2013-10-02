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

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.yes.cart.domain.entity.CarrierSla;
import org.yes.cart.domain.i18n.impl.FailoverStringI18NModel;

/**
 * User: Igor Azarny iazarnmy@yahoo.com
 * Date: 15-Oct-2011
 * Time: 11:01:06 AM
 */

public class CarrierSlaRenderer extends ChoiceRenderer<CarrierSla> {

    private final Component component;

    public CarrierSlaRenderer(final Component component) {
        this.component = component;
    }

    /** {@inheritDoc} */
    @Override
    public Object getDisplayValue(final CarrierSla carrierSla) {
        return new FailoverStringI18NModel(carrierSla.getDisplayName(), carrierSla.getName()).getValue(getLocale());
    }

    /** {@inheritDoc} */
    @Override
    public String getIdValue(final CarrierSla carrierSla, final int i) {
        return String.valueOf(carrierSla.getCarrierslaId());
    }

    private String getLocale() {
        return this.component.getLocale().getLanguage();
    }

}
