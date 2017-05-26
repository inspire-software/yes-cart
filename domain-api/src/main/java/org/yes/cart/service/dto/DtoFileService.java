/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
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

import java.io.IOException;

/**
 * Dto file service to manipulate images and image seo information from UI.
 * <p/>
 * User: Denis Pavlov
 */
public interface DtoFileService {

    /**
     * Add the given file to file repository.
     * Used from UI to
     *
     * @param fullFileName  full path to image file.
     * @param code          product or sku code.
     * @param fileBody      file as byte array.
     * @param storagePrefix storage prefix (defines image naming strategy, see how those are configured in Spring context)
     * @param pathToRepository path to repository.
     *
     * @return file name in file vault. may be different from original
     * @throws IOException in case of any I/O errors
     */
    String addFileToRepository(String fullFileName,
                               String code,
                               byte[] fileBody,
                               String storagePrefix,
                               String pathToRepository) throws IOException;

    /**
     * Read product or sku file into byte array.
     *
     * @param fileName file name from attribute
     * @param code     product or sku code
     * @param pathToRepository path to repository.
     *
     * @return byte array
     * @throws IOException in case of any I/O errors
     */
    byte[] getFileAsByteArray(String fileName,
                              String code,
                              String storagePrefix,
                              String pathToRepository) throws IOException;


}
