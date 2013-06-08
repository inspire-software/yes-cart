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

package org.yes.cart.service.misc.impl;

import org.yes.cart.service.misc.PluralFormService;

/**
 * Igor Azarny iazarny@yahoo.com
 * Date: 21-Sep-2011
 * Time: 12:51:13
 */
public class PluralFormServiceImpl implements PluralFormService {

    /** {@inheritDoc} */
    public String getPluralForm(final String lang, final int num, final String[] form) {

        int index = 0;

        if ("RU".equalsIgnoreCase(lang)) { //продукт - мужской род
            index = (num % 10 == 1 && num % 100 != 11 ?
                    0 :
                    num % 10 >= 2 && num % 10 <= 4 && (num % 100 < 10 || num % 100 >= 20) ?
                            1 : 2);
        } else { // EN by default
            if (num > 1) {
                index = 1;
            }
        }

        return form[index];

    }
}
