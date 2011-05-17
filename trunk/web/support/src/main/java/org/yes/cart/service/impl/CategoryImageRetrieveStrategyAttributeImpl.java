package org.yes.cart.service.impl;

import org.yes.cart.service.CategoryImageRetrieveStrategy;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.entity.AttrValue;
import org.yes.cart.constants.Constants;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 2011-May-17
 * Time: 16:18:53
 */
public class CategoryImageRetrieveStrategyAttributeImpl implements CategoryImageRetrieveStrategy {

    private final String attributeCode;

    /**
     * Construct image retreive strategy
     * @param attributeCode attribute name
     */
    public CategoryImageRetrieveStrategyAttributeImpl(final String attributeCode) {
        this.attributeCode = attributeCode;
    }

    /** {@inheritDoc} */
    public String getImageName(final Category category) {
        final AttrValue attrValue = category.getAttributeByCode(attributeCode);
        if (attrValue == null) {
            return Constants.NO_IMAGE;
        }
        return attrValue.getVal();
    }

    /**
     * Get image repository url pattern.
     *
     * @return image repository url pattern
     */
    public String getImageRepositoryUrlPattern() {
        return Constants.CATEGOTY_IMAGE_REPOSITORY_URL_PATTERN;
    }

}
