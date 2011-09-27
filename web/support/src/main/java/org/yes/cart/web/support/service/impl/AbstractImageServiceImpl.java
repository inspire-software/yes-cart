package org.yes.cart.web.support.service.impl;

import org.yes.cart.constants.Constants;
import org.yes.cart.domain.entity.AttrValue;
import org.yes.cart.domain.entity.Attributable;
import org.yes.cart.web.support.constants.WebParametersKeys;
import org.yes.cart.web.support.service.AttributableImageService;

import javax.servlet.http.HttpServletRequest;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/4/11
 * Time: 6:44 PM
 */
public abstract class AbstractImageServiceImpl implements AttributableImageService {

    /**
     * Get default image uri.
     *
     * @param imageName          name of image
     * @param width              image width
     * @param height             image height
     * @param servletContextPath http servlet request
     * @param object             product/sku/category
     * @return image uri.
     */
    protected String getImageURI(final String imageName,
                              final String width,
                              final String height,
                              final String servletContextPath,
                              final Object object) {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(servletContextPath);
        stringBuilder.append(getImageRepositoryUrlPattern(object));
        stringBuilder.append(imageName);
        stringBuilder.append('?');
        stringBuilder.append(WebParametersKeys.WIDTH);
        stringBuilder.append('=');
        stringBuilder.append(width);
        stringBuilder.append('&');
        stringBuilder.append(WebParametersKeys.HEIGHT);
        stringBuilder.append('=');
        stringBuilder.append(height);
        return stringBuilder.toString();
    }


    /**
     * Get default image uri.
     *
     * @p aram imageName          name of image
     * @p aram servletContextPath http servlet request
     * @pa ram object             product/sku/category
     * @re turn image uri.
     */
    /*protected String getImageURI(final String imageName,
                              final String servletContextPath,
                              final Object object) {
        final StringBuilder stringBuilder = new StringBuilder();
        //stringBuilder.append(servletContextPath);
        stringBuilder.append(getImageRepositoryUrlPattern(object));
        stringBuilder.append(imageName);
        return stringBuilder.toString();
    } */




    /**
     * Get attribute value.
     * @param attributable given attributable.
     * @param attrName  attribute name
     * @return attibute value if found, otherwise noimage will be returned.
     */
    public String getImageAttributeValue(final Attributable attributable, final String attrName) {
        final AttrValue attrValue = attributable.getAttributeByCode(attrName);
        if (attrValue == null) {
            return Constants.NO_IMAGE;
        }
        return attrValue.getVal();

    }


    /** {@inheritDoc} */
    public String getImage(final Attributable attributable, final String httpServletContextPath,
                           final String width, final String height, final String attrName) {
        return getImageURI(getImageAttributeValue(attributable, attrName), width, height, httpServletContextPath, attributable);
    }


    /**
     *
     * @return image repository url pattern.
     * @param object to determinate url pattern
     */
    public abstract String getImageRepositoryUrlPattern(Object object);





}
