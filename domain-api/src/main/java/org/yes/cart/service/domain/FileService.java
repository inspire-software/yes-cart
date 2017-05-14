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

package org.yes.cart.service.domain;

import org.yes.cart.service.media.MediaFileNameStrategy;

import java.io.IOException;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface FileService {

    /**
     * Get the media file name strategy.
     *
     * @param url the
     * @return media file name strategy
     */
    MediaFileNameStrategy getFileNameStrategy(String url);

    /**
     * Check if given media is in repository.
     *
     * @param fullFileName  full path to media file.
     * @param code          product or sku code.
     * @param storagePrefix storage prefix (defines media file naming strategy, see how those are configured in Spring context)
     * @param pathToRepository path to repository
     *
     * @return true if media file is in repository
     */
    boolean isFileInRepository(String fullFileName, String code, String storagePrefix, String pathToRepository);

    /**
     * Add the given file to media repository.
     * Used from UI to
     *
     * @param fullFileName  full path to file.
     * @param code          product or sku code.
     * @param imgBody       file as byte array.
     * @param storagePrefix storage prefix (defines file naming strategy, see how those are configured in Spring context)
     * @param pathToRepository path to repository
     *
     * @return file name in file vault. may be different from original
     * @throws IOException in case of any I/O errors
     */
    String addFileToRepository(String fullFileName, String code, byte[] imgBody, String storagePrefix, String pathToRepository) throws IOException;

    /**
     * Read product or sku file into byte array.
     *
     * @param fileName      file name from attribute
     * @param code          product or sku code
     * @param storagePrefix storage prefix (defines file naming strategy, see how those are configured in Spring context)
     * @param pathToRepository path to repository
     * @return byte array
     * @throws IOException in case of any I/O errors
     */
    byte[] fileToByteArray(String fileName, String code, String storagePrefix, String pathToRepository) throws IOException;

    /**
     * Delete file.
     *
     * @param mediaFileName media file name
     * @param storagePrefix storage prefix (defines file naming strategy, see how those are configured in Spring context)
     * @param pathToRepository path to repository
     *
     * @return delete file operation result
     */
    boolean deleteFile(String mediaFileName, String storagePrefix, String pathToRepository);


}
