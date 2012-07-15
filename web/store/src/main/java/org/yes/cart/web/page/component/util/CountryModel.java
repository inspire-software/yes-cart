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

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.IDetachable;
import org.apache.commons.lang.StringUtils;
import org.yes.cart.domain.entity.Country;

import java.util.List;

/**
 * User: Igor Azarny iazarnmy@yahoo.com
 * Date: 15-Oct-2011
 * Time: 11:01:06 AM
 */

public class CountryModel implements IModel<Country> {

    private Country country;

    private final PropertyModel propertyModel;

    public CountryModel(final PropertyModel propertyModel, final List<Country> countryList) {
        this.propertyModel = propertyModel;
        final String singleSelectedKey = (String) propertyModel.getObject();
        if (StringUtils.isNotBlank(singleSelectedKey)) {
            for (Country c : countryList) {
                if (singleSelectedKey.equals(c.getCountryCode())) {
                    country = c;
                    break;
                }
            }
        }

    }

    public Country getObject() {
        return country;
    }

    public void setObject(final Country country) {
        this.country = country;
        if (country == null) {
            propertyModel.setObject(null);
        } else {
            propertyModel.setObject(country.getCountryCode());
        }
    }

    public void detach() {

        if (country instanceof IDetachable) {
            ((IDetachable)country).detach();
        }

    }
}
