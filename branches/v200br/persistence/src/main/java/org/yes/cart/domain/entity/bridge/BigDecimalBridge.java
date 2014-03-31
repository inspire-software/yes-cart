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

package org.yes.cart.domain.entity.bridge;

import org.hibernate.search.bridge.StringBridge;
import org.yes.cart.constants.Constants;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 *
 * Need custom bridge for prices, because it must be in minor units and
 * must be padded to be used in lucene index.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 16:13:01 *
 *
 */
public class BigDecimalBridge implements StringBridge {


    private final DecimalFormat formatter = new DecimalFormat(Constants.MONEY_FORMAT_TOINDEX);

    /**
     * {@inheritDoc}
     */
    public String objectToString(final Object bigDecimalPriceObject) {
        if (bigDecimalPriceObject instanceof BigDecimal) {
            BigDecimal bigDecimal = (BigDecimal) bigDecimalPriceObject;
            long toIndex = bigDecimal.movePointRight(2).longValue();
            return formatter.format(toIndex);
        }
        return null;
    }

}
