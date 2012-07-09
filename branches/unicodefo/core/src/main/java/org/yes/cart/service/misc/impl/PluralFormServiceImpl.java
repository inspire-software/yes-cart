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
