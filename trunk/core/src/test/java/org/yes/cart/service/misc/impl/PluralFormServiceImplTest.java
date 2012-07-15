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

import org.junit.Test;
import org.yes.cart.service.misc.PluralFormService;

import static org.junit.Assert.assertEquals;

/**
 * Igor Azarny iazarny@yahoo.com
 * Date: 21-Sep-2011
 * Time: 12:57:24
 */
public class PluralFormServiceImplTest {

    @Test
    public void testGetPluralForm() {
        PluralFormService pluralFormService = new PluralFormServiceImpl();
        assertEquals("item", pluralFormService.getPluralForm("EN", 1, new String[]{"item", "items"}));
        assertEquals("items", pluralFormService.getPluralForm("EN", 2, new String[]{"item", "items"}));
        String[] arr = new String[]{"продукт", "продукта", "продуктов"};
        assertEquals("продукт", pluralFormService.getPluralForm("ru", 1, arr));
        assertEquals("продукта", pluralFormService.getPluralForm("ru", 2, arr));
        assertEquals("продукта", pluralFormService.getPluralForm("ru", 3, arr));
        assertEquals("продукта", pluralFormService.getPluralForm("ru", 4, arr));
        assertEquals("продуктов", pluralFormService.getPluralForm("ru", 10, arr));
        assertEquals("продуктов", pluralFormService.getPluralForm("ru", 11, arr));
        assertEquals("продукт", pluralFormService.getPluralForm("ru", 21, arr));
        assertEquals("продуктов", pluralFormService.getPluralForm("ru", 111, arr));
        assertEquals("продукта", pluralFormService.getPluralForm("ru", 142, arr));
    }
}
