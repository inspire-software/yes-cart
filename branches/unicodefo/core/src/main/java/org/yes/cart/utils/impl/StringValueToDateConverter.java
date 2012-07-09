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

    private final SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DEFAULT_DATE_TIME_FORMAT);

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
