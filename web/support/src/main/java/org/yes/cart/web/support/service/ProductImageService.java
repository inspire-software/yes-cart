package org.yes.cart.web.support.service;

import org.yes.cart.domain.entity.Product;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 8/8/11
 * Time: 2:30 PM
 */
public interface ProductImageService {

    /**
     * Get the context image with image servlet url
     * and specified parameters.
     *
     * @param product                given product
     * @param httpServletContextPath http servlet request path
     * @return default context image url.
     */
    String getProductImage(Product product,
                           String httpServletContextPath);

    /**
     * Get the context image with image servlet url
     * and specified parameters.
     *
     * @param product                given product
     * @param httpServletContextPath http servlet request path
     * @param width                  image width
     * @param height                 image height.
     * @return default context image url.
     */
    String getProductImage(Product product,
                           String httpServletContextPath,
                           String width,
                           String height);

    /**
     * Get the context image with image servlet url
     * and specified parameters.
     *
     * @param product                given product
     * @param httpServletContextPath http servlet request path
     * @param attrName given image attribute name
     * @return default context image url.
     */
    String getProductImage(Product product,
                           String httpServletContextPath,
                           String attrName);

    /**
     * Get the context image with image servlet url
     * and specified parameters.
     *
     * @param product                given product
     * @param httpServletContextPath http servlet request path
     * @param width                  image width
     * @param height                 image height.
     * @param attrName given image attribute name
     * @return default context image url.
     */
    String getProductImage(Product product,
                           String httpServletContextPath,
                           String width,
                           String height,
                           String attrName);

}
