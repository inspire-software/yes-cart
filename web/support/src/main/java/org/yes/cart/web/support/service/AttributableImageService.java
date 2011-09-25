package org.yes.cart.web.support.service;


import org.yes.cart.domain.entity.Attributable;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 8/8/11
 * Time: 2:30 PM
 */
public interface AttributableImageService {


    /**
     * Get the context image with image servlet url
     * and specified parameters.
     *
     * @param attributable                given attributable
     * @param httpServletContextPath http servlet request path
     * @param width                  image width
     * @param height                 image height.
     * @param attrName given image attribute name
     * @return default context image url.
     */
    String getImage(Attributable attributable,
                    String httpServletContextPath,
                    String width,
                    String height,
                    String attrName);

}
