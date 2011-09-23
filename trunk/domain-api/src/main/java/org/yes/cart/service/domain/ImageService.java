package org.yes.cart.service.domain;

import org.yes.cart.domain.entity.SeoImage;
import org.yes.cart.service.image.ImageNameStrategy;

import java.io.IOException;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface ImageService extends GenericService<SeoImage> {
    /**
     * Resize given file to requested width and height
     *
     * @param original path to original image
     * @param resized  path to resized image
     * @param width    requested width
     * @param height   requested height
     */
    void resizeImage(String original, String resized, String width, String height);

    /**
     * Is given image size allowed check.
     *
     * @param size size in widthxheight format, for example 50x60
     * @return true if size is allowed
     */
    boolean isSizeAllowed(String size);

    /**
     * Is given image size allowed check.
     *
     * @param width  image width
     * @param height image height
     * @return true if size is allowed
     */
    boolean isSizeAllowed(String width, String height);

    /**
     * Get the image name strategy.
     *
     * @param url the
     * @return image name strategy
     */
    ImageNameStrategy getImageNameStrategy(String url);

    /**
     * Add the given file to image repository during bulk import.
     * At this momen only product images can be imported.
     *
     * @param fullFileName full path to image file.
     * @param code         product or sku code.
     * @return true if file was added successfully
     * @throws IOException in case of io errors.
     */
    boolean addImageToRepository(String fullFileName, String code) throws IOException;

    /**
     * Add the given file to image repository.
     * Used from UI to
     *
     * @param fullFileName  full path to image file.
     * @param code          product or sku code.
     * @param imgBody       image as byte array.
     * @param storagePrefix optional storage prefix {@see Constants.CATEGOTY_IMAGE_REPOSITORY_URL_PATTERN}
     *                      or {@see Constants.BRAND_IMAGE_REPOSITORY_URL_PATTERN}. If parameter not provider the product image storage will be used.
     * @return true if file was added successfully
     * @throws IOException in case of any I/O errors
     */
    boolean addImageToRepository(String fullFileName, String code, byte[] imgBody, String storagePrefix) throws IOException;

    /**
     * Add the given file to image repository.
     * Used from UI to
     *
     * @param fullFileName  full path to image file.
     * @param code          product or sku code.
     * @param imgBody       image as byte array.
     * @param storagePrefix optional storage prefix {@see Constants.CATEGOTY_IMAGE_REPOSITORY_URL_PATTERN}
     *                      or {@see Constants.BRAND_IMAGE_REPOSITORY_URL_PATTERN}. If parameter not provider the product image storage will be used.
     * @param pathToRepository path to repository
     * @return true if file was added successfully
     * @throws IOException in case of any I/O errors
     */
    boolean addImageToRepository(String fullFileName, String code, byte[] imgBody, String storagePrefix, String pathToRepository) throws IOException;

    /**
     * Read product or sku image into byte array.
     *
     * @param fileName      file name from attribute
     * @param code          product or sku code
     * @param storagePrefix optional storage prefix {@see Constants.CATEGOTY_IMAGE_REPOSITORY_URL_PATTERN}
     *                      or {@see Constants.BRAND_IMAGE_REPOSITORY_URL_PATTERN}. If parameter not provider the product image storage will be used.
     * @param pathToRepository
     * @return byte array
     * @throws IOException in case of any I/O errors
     */
    byte[] getImageAsByteArray(String fileName, String code, String storagePrefix, String pathToRepository) throws IOException;

    /**
     * Get the image seo data by given image name.
     *
     * @param imageName image name
     * @return {@link SeoImage} or null if not found.
     */
    SeoImage getSeoImage(String imageName);

    /**
     * Delete image.
     *
     * @param imageFileName image file name
     * @return delete file operation result
     */
    boolean deleteImage(String imageFileName);


}
