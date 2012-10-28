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
import org.yes.cart.domain.entity.CarrierSla;

/**
 * User: Igor Azarny iazarnmy@yahoo.com
 * Date: 15-Oct-2011
 * Time: 11:01:06 AM
 */

public class CarrierSlaRenderer extends ChoiceRenderer<CarrierSla> {
    
    /** {@inheritDoc} */
    @Override
    public Object getDisplayValue(final CarrierSla carrierSla) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(carrierSla.getName());
        stringBuilder.append('(');
        stringBuilder.append(carrierSla.getMaxDays());
        stringBuilder.append(") ");
        stringBuilder.append(carrierSla.getPrice()); //TODO v2 calculate shipping price
        stringBuilder.append(' ');
        stringBuilder.append(carrierSla.getCurrency());        
        return stringBuilder.toString();
    }

    /** {@inheritDoc} */
    @Override
    public String getIdValue(final CarrierSla carrierSla, final int i) {
        return String.valueOf(carrierSla.getCarrierslaId());
    }
    
    
}
