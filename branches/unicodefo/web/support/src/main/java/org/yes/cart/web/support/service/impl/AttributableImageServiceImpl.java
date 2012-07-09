package org.yes.cart.web.support.service.impl;

import org.yes.cart.constants.Constants;
import org.yes.cart.domain.entity.Attributable;
import org.yes.cart.web.support.service.AttributableImageService;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 8/8/11
 * Time: 2:40 PM
 */
public class AttributableImageServiceImpl extends AbstractImageServiceImpl implements AttributableImageService {

    /** {@inheritDoc} */
    public String getImageRepositoryUrlPattern(final Object object) {
        return Constants.PRODUCT_IMAGE_REPOSITORY_URL_PATTERN;
    }

}
