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

package org.yes.cart.service.dto.impl;

import org.yes.cart.service.domain.FileService;
import org.yes.cart.service.dto.DtoFileService;

import java.io.IOException;

/**
 * User: Denis Pavlov
 */
public class DtoFileServiceImpl
        implements DtoFileService {

    private final FileService fileService;

    /**
     * Construct base remote service.
     *
     */
    public DtoFileServiceImpl(final FileService fileService) {
        this.fileService = fileService;
    }

    /** {@inheritDoc} */
    public String addFileToRepository(final String fullFileName,
                                      final String code,
                                      final byte[] fileBody,
                                      final String storagePrefix,
                                      final String pathToRepository) throws IOException {
        return fileService.addFileToRepository(fullFileName, code, fileBody, storagePrefix, pathToRepository);
    }

    /** {@inheritDoc} */
    public byte[] getFileAsByteArray(final String fileName, final String code, final String storagePrefix, final String pathToRepository) throws IOException {
        return fileService.fileToByteArray(fileName, code, storagePrefix, pathToRepository);
    }

}
