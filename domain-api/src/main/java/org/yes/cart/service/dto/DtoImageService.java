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
     * @param storagePrefix storage prefix (defines image naming strategy, see how those are configured in Spring context)
     * @param pathToRepository path to repository.
     *
     * @return file name in image vault. may be different from original
     * @throws java.io.IOException in case of any I/O errors
     */
    String addImageToRepository(String fullFileName,
                                String code,
                                byte[] imgBody,
                                String storagePrefix,
                                String pathToRepository) throws IOException;

    /**
     * Read product or sku image into byte array.
     *
     * @param fileName file name from attribute
     * @param code     product or sku code
     * @param pathToRepository path to repository.
     *
     * @return byte array
     * @throws IOException in case of any I/O errors
     */
    byte[] getImageAsByteArray(String fileName,
                               String code,
                               String storagePrefix,
                               String pathToRepository) throws IOException;


    /**
     * Get {@link SeoImageDTO} by given file name.
     *
     * @param imageFileName given file name.
     * @return instance of {@link SeoImageDTO} or null if not found.
     */
    SeoImageDTO getSeoImage(String imageFileName) throws UnmappedInterfaceException, UnableToCreateInstanceException;


}
