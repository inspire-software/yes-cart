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

package org.yes.cart.utils.impl;

import org.springframework.core.convert.converter.Converter;
import org.yes.cart.constants.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class StringValueToDateConverter implements Converter<String, Date> {

    private final SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DEFAULT_IMPORT_DATE_TIME_FORMAT);

    /** {@inheritDoc} */
    public Date convert(final String str) {
        if (str != null) {
            try {
                return dateFormat.parse(str);
            } catch (ParseException e) {
                return null;
            }            
        }
        return null;
    }
}
