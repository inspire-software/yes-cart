package org.yes.cart.web.support.service.impl;

import org.yes.cart.constants.Constants;
import org.yes.cart.domain.entity.AttrValue;
import org.yes.cart.domain.entity.Product;
import org.yes.cart.web.support.service.ProductImageService;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 8/8/11
 * Time: 2:40 PM
 */
public class ProductImageServiceImpl extends AbstractImageServiceImpl implements ProductImageService {

    /** {@inheritDoc} */
    public String getProductImage(final Product product, final String httpServletContextPath) {
        return getProductImage(product, httpServletContextPath, Constants.PRODUCT_DEFAULT_IMAGE_ATTR_NAME);
    }

    /** {@inheritDoc} */
    public String getProductImage(final Product product, final String httpServletContextPath,
                                  final String width, final String height) {
        return getProductImage(product, httpServletContextPath, width, height, Constants.PRODUCT_DEFAULT_IMAGE_ATTR_NAME);
    }

    /** {@inheritDoc} */
    public String getProductImage(final Product product, final String httpServletContextPath, final String attrName) {
        return getImageURI(getImageAttributeValue(product, attrName), httpServletContextPath, product);
    }

    /** {@inheritDoc} */
    public String getProductImage(final Product product, final String httpServletContextPath,
                                  final String width, final String height, final String attrName) {
        return getImageURI(getImageAttributeValue(product, attrName), width, height, httpServletContextPath, product);
    }

    /** {@inheritDoc} */
    public String getImageRepositoryUrlPattern(final Object object) {
        return Constants.PRODUCT_IMAGE_REPOSITORY_URL_PATTERN;
    }

    private String getImageAttributeValue(final Product product, final String attrName) {
        final AttrValue attrValue = product.getAttributeByCode(attrName);
        if (attrValue == null) {
            return Constants.NO_IMAGE;
        }
        return attrValue.getVal();

    }
}
