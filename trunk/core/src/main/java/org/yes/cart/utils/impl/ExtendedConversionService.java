package org.yes.cart.utils.impl;

import org.springframework.core.convert.support.ConversionServiceFactory;
import org.springframework.core.convert.support.GenericConversionService;


/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class ExtendedConversionService extends GenericConversionService {

    public ExtendedConversionService() {
        super();
        ConversionServiceFactory.addDefaultConverters(this);
        addConverter(new StringValueToPairListConverter());
        addConverter(new StringValueToDateConverter());
    }
    
}
