package org.yes.cart.web.support.service.impl;

import org.yes.cart.web.support.constants.WebParametersKeys;

import javax.servlet.http.HttpServletRequest;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/4/11
 * Time: 6:44 PM
 */
public abstract class AbstractImageServiceImpl {

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
    public String getImageURI(final String imageName,
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
     * @param imageName          name of image
     * @param servletContextPath http servlet request
     * @param object             product/sku/category
     * @return image uri.
     */
    public String getImageURI(final String imageName,
                              final String servletContextPath,
                              final Object object) {
        final StringBuilder stringBuilder = new StringBuilder();
        //stringBuilder.append(servletContextPath);
        stringBuilder.append(getImageRepositoryUrlPattern(object));
        stringBuilder.append(imageName);
        return stringBuilder.toString();
    }




    /**
     * Get default image uri.
     *
     * @param imageName          name of image
     * @param width              image width
     * @param height             image height
     * @param httpServletRequest http servlet request
     * @param object             product/sku/category
     * @return image uri.
     */
    public String getImageURI(final String imageName,
                              final String width,
                              final String height,
                              final HttpServletRequest httpServletRequest,
                              final Object object) {
        return getImageURI(imageName, width, height, httpServletRequest.getContextPath(), object);
    }

    /**
     * Get default image uri.
     *
     * @param imageName          name of image
     * @param httpServletRequest http servlet request
     * @return image uri.
     */
    public String getImageURI(final String imageName,
                              final HttpServletRequest httpServletRequest,
                              final Object object) {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(httpServletRequest.getContextPath());
        stringBuilder.append(getImageRepositoryUrlPattern(object));
        stringBuilder.append(imageName);
        return stringBuilder.toString();
    }

    /**
     *
     * @return image repository url pattern.
     */
    public abstract String getImageRepositoryUrlPattern(Object object);


}
