package org.yes.cart.service.image.impl;

import org.yes.cart.constants.Constants;
import org.yes.cart.service.domain.SystemService;

import java.io.File;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class CategoryImageNameStrategyImpl extends AbstractImageNameStrategyImpl {

    private final static String PATH_PREFIX = Constants.CATEGOTY_IMAGE_FILE_PREFIX + File.separator;

    /** {@inheritDoc} */
    protected String getPathPrefix() {
        return PATH_PREFIX;
    }

    /** {@inheritDoc} */
    public String getCode(final String fileName) {
        return null;
    }


}
