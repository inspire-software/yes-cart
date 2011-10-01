package org.yes.cart.web.support.entity.decorator;

import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.entity.Product;
import org.yes.cart.domain.entity.SeoImage;
import org.yes.cart.domain.misc.Pair;

import java.util.List;

/**
 * Depictable interface for sku, product , brands, etc.
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/13/11
 * Time: 9:39 PM
 */
public interface Depictable {


    String PRODUCT_DEFAULT_IMAGE_WIDTH = "360";
    String PRODUCT_DEFAULT_IMAGE_HEIGHT = "360";

    String PRODUCT_THUMBNAIL_IMAGE_WIDTH = "80";
    String PRODUCT_THUMBNAIL_IMAGE_HEIGHT = "80";

    /**
     * Get images attributes names.
     * @return images attributes names.
     */
    List<String> getImageAttributeNames();

    /**
     * Get pair of images attributes names - image file name.
     * @return images attributes names.
     */
    List<Pair<String, String>> getImageAttributeFileNames();



    /**
     * Get product image with give width and height.
     * @param width image width to get correct url
     * @param height image height to get correct url
     * @param imageAttributeName particular attribute name.
     *
     * @return product image url, depending from strategy.
     */
    String getImage(String width, String height, String imageAttributeName);


    /**
     * Get product image with give width and height.
     * @param width image width to get correct url
     * @param height image height to get correct url
     *
     * @return product image url, depending from strategy.
     */
    String getDefaultImage(String width, String height);

    /**
     * Get product image width in particular category.
     * @param category optional given category
     * @return  image width.
     */
    String getDefaultImageWidth(Category category);

    /**
     * Get product image height in partucular category.
     * @param category given category
     * @return     image height.
     */
    String getDefaultImageHeight(Category category);

    /**
     * Get product image width in particular category.
     * @param category optional given category
     * @return  image width.
     */
    String getThumbnailImageWidth(Category category);

    /**
     * Get product image height in partucular category.
     * @param category given category
     * @return     image height.
     */
    String getThumbnailImageHeight(Category category);


     /** Get default image attribute name.
      * @return default image attibute name
      * */
    String getDefaultImageAttributeName();


    /**
     * Get image seo information.
     * @param fileName image filename.
     * @return {@link SeoImage}  in cae if it present for given filename, otherwise null
     */
    SeoImage getSeoImage(String fileName);

}
