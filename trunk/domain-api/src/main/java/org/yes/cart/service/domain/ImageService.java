/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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
     * Resize given file (if necessary) to requested width and height
     *
     * @param original path to original image
     * @param resized  path to resized image
     * @param width    requested width
     * @param height   requested height
     *
     * @return  resized image bytes
     */
    byte[] resizeImage(String original, String resized, String width, String height);


    /**
     * Resize given file to requested width and height
     *
     * @param original      original image filename
     * @param content       original image
     * @param width         requested width
     * @param height        requested height
     *
     * @return  resized image bytes
     */
    byte[] resizeImage(String original, byte[] content, String width, String height);


    /**
     * Resize given file (if necessary) to requested width and height
     *
     * @param original path to original image
     * @param resized  path to resized image
     * @param width    requested width
     * @param height   requested height
     * @param cropToFit     setting this to true will crop the image to proper ratio
     *                      prior to scaling so that scaled image fills all the space.
     *                      This is useful for those who wish to have images that fill
     *                      all space dedicated for image without having border around
     *                      the image. For those who wish images of products in the middle
     *                      e.g. as it is in YC demo better to set this to false.
     *
     * @return  resized image bytes
     */
    byte[] resizeImage(String original, String resized, String width, String height, boolean cropToFit);


    /**
     * Resize given file to requested width and height
     *
     * @param original      original image filename
     * @param content       original image
     * @param width         requested width
     * @param height        requested height
     * @param cropToFit     setting this to true will crop the image to proper ratio
     *                      prior to scaling so that scaled image fills all the space.
     *                      This is useful for those who wish to have images that fill
     *                      all space dedicated for image without having border around
     *                      the image. For those who wish images of products in the middle
     *                      e.g. as it is in YC demo better to set this to false.
     *
     * @return  resized image bytes
     */
    byte[] resizeImage(String original, byte[] content, String width, String height, boolean cropToFit);

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
     * Check if given image is in repository.
     *
     * @param fullFileName  full path to image file.
     * @param code          product or sku code.
     * @param storagePrefix storage prefix (defines image naming strategy, see how those are configured in Spring context)
     * @param pathToRepository path to repository
     *
     * @return true if image is in repository
     */
    boolean isImageInRepository(String fullFileName, String code, String storagePrefix, String pathToRepository);

    /**
     * Add the given file to image repository.
     * Used from UI to
     *
     * @param fullFileName  full path to image file.
     * @param code          product or sku code.
     * @param imgBody       image as byte array.
     * @param storagePrefix storage prefix (defines image naming strategy, see how those are configured in Spring context)
     * @param pathToRepository path to repository
     *
     * @return file name in image vault. may be different from original
     * @throws IOException in case of any I/O errors
     */
    String addImageToRepository(String fullFileName, String code, byte[] imgBody, String storagePrefix, String pathToRepository) throws IOException;

    /**
     * Read product or sku image into byte array.
     *
     * @param fileName      file name from attribute
     * @param code          product or sku code
     * @param storagePrefix storage prefix (defines image naming strategy, see how those are configured in Spring context)
     * @param pathToRepository path to repository
     * @return byte array
     * @throws IOException in case of any I/O errors
     */
    byte[] imageToByteArray(String fileName, String code, String storagePrefix, String pathToRepository) throws IOException;

    /**
     * Get the image seo data by given image name.
     *
     * @param imageName image name (including image name strategy mapping)
     * @return {@link SeoImage} or null if not found.
     */
    SeoImage getSeoImage(String imageName);

    /**
     * Delete image.
     *
     * @param imageFileName image file name
     * @param storagePrefix storage prefix (defines image naming strategy, see how those are configured in Spring context)
     * @param pathToRepository path to repository
     *
     * @return delete file operation result
     */
    boolean deleteImage(String imageFileName, String storagePrefix, String pathToRepository);


}
