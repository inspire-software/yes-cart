package org.yes.cart.service.image;

/**
 * Image Name Strategy Resolver responsible for
 * getByKey the particular {@link ImageNameStrategy}
 * by given url.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface ImageNameStrategyResolver {

    /**
     * Get image name strategy by given image url
     *
     * @param imageUrl
     * @return particular instance of {@ImageNameStrategy}
     */
    ImageNameStrategy getImageNameStrategy(String imageUrl);


}
