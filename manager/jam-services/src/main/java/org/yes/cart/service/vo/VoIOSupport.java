/*
 * Copyright 2009 - 2016 Denys Pavlov, Igor Azarnyi
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

package org.yes.cart.service.vo;

import java.io.IOException;

/**
 * User: denispavlov
 * Date: 07/08/2016
 * Time: 23:10
 */
public interface VoIOSupport {

    /**
     * Store file in repository.
     *
     * @param fileName full name properly formatted
     * @param code master object code
     * @param attributeFileCode file attribute code
     * @param base64URL base64 URL string
     * @param storagePrefix storage prefix
     *
     * @return filename
     *
     * @throws IOException in case image cannot be added
     */
    String addFileToRepository(String fileName,
                               String code,
                               String attributeFileCode,
                               String base64URL,
                               String storagePrefix) throws IOException;

    /**
     * Store file in repository.
     *
     * @param fileName full name properly formatted
     * @param code master object code
     * @param attributeFileCode file attribute code
     * @param base64URL base64 URL string
     * @param storagePrefix storage prefix
     *
     * @return filename
     *
     * @throws IOException in case image cannot be added
     */
    String addSystemFileToRepository(String fileName,
                                     String code,
                                     String attributeFileCode,
                                     String base64URL,
                                     String storagePrefix) throws IOException;


    /**
     * Store image in repository.
     *
     * @param fileName full name properly formatted
     * @param code master object code
     * @param attributeImageCode image attribute code
     * @param base64URL base64 URL string
     * @param storagePrefix storage prefix
     *
     * @return filename
     *
     * @throws IOException in case image cannot be added
     */
    String addImageToRepository(String fileName,
                                String code,
                                String attributeImageCode,
                                String base64URL,
                                String storagePrefix) throws IOException;

    /**
     * Get image as BASE64 data URL.
     *
     * @param fileName filename
     * @param code code
     * @param storagePrefix storage prefix
     *
     * @return base64 URL
     */
    String getImageAsBase64(String fileName,
                            String code,
                            String storagePrefix);

}
