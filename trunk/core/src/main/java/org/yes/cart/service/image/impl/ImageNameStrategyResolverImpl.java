package org.yes.cart.service.image.impl;

import org.yes.cart.constants.Constants;
import org.yes.cart.service.image.ImageNameStrategy;
import org.yes.cart.service.image.ImageNameStrategyResolver;

/**
 * Responsible to get the particular {@link ImageNameStrategy} by fiven image url.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class ImageNameStrategyResolverImpl implements ImageNameStrategyResolver {

    final ImageNameStrategy brandImageNameStrategy;
    final ImageNameStrategy categoryImageNameStrategy;
    final ImageNameStrategy productImageNameStrategy;

    /**
     * Construct image name strategy resolver.
     *
     * @param brandImageNameStrategy    brand image name strategy
     * @param categoryImageNameStrategy category image name strategy
     * @param productImageNameStrategy  product and sku image name strategy
     */
    public ImageNameStrategyResolverImpl(
            final ImageNameStrategy brandImageNameStrategy,
            final ImageNameStrategy categoryImageNameStrategy,
            final ImageNameStrategy productImageNameStrategy) {

        this.brandImageNameStrategy = brandImageNameStrategy;
        this.categoryImageNameStrategy = categoryImageNameStrategy;
        this.productImageNameStrategy = productImageNameStrategy;
    }


    /**
     * {@inheritDoc}
     */
    public ImageNameStrategy getImageNameStrategy(final String imageUrl) {
        if (imageUrl == null) {
            return productImageNameStrategy;
        } else if (imageUrl.indexOf(Constants.CATEGOTY_IMAGE_REPOSITORY_URL_PATTERN) > -1) {
            return categoryImageNameStrategy;
        } else if (imageUrl.indexOf(Constants.BRAND_IMAGE_REPOSITORY_URL_PATTERN) > -1) {
            return brandImageNameStrategy;
        }
        return productImageNameStrategy;
    }
}
