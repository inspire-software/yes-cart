package org.yes.cart.service.dto;

import org.yes.cart.domain.dto.SeoImageDTO;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;

import java.io.IOException;

/**
 * Dto image service to manipulate images and image seo information from UI.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface DtoImageService extends GenericDTOService<SeoImageDTO> {

    /**
     * Add the given file to image repository.
     * Used from UI to
     *
     * @param fullFileName  full path to image file.
     * @param code          product or sku code.
     * @param imgBody       image as byte array.
     * @param storagePrefix optional storage prefix {@see Constants.CATEGOTY_IMAGE_REPOSITORY_URL_PATTERN}
     *                      or {@see Constants.BRAND_IMAGE_REPOSITORY_URL_PATTERN}. If parameter not provider the product
     *                      image storage will be used.
     * @param pathToRepository path to repository.
     * @return true if file was added successfully
     * @throws java.io.IOException in case of any I/O errors
     */
    boolean addImageToRepository(String fullFileName,
                                 String code,
                                 byte[] imgBody,
                                 String storagePrefix,
                                 String pathToRepository) throws IOException;

    /**
     * Read product or sku image into byte array.
     *
     * @param fileName file name from attribute
     * @param code     product or sku code
     * @return byte array
     * @throws IOException in case of any I/O errors
     */
    byte[] getImageAsByteArray(String fileName, String code, String storagePrefix) throws IOException;


    /**
     * Get {@link SeoImageDTO} by given file name.
     *
     * @param imageFileName fiven file name.
     * @return instance of {@link SeoImageDTO} or null if not found.
     */
    SeoImageDTO getSeoImage(String imageFileName) throws UnmappedInterfaceException, UnableToCreateInstanceException;


}
