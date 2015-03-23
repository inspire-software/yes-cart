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

package org.yes.cart.remote.service.impl;

import org.apache.commons.lang.StringUtils;
import org.yes.cart.domain.dto.SeoImageDTO;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.remote.service.RemoteImageService;
import org.yes.cart.service.domain.SystemService;
import org.yes.cart.service.dto.DtoImageService;

import java.io.IOException;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 15-May-2011
 * Time: 17:22:15
 */
public class RemoteImageServiceImpl extends AbstractRemoteService<SeoImageDTO> implements RemoteImageService {


    private final DtoImageService dtoImageService;
    private final SystemService systemService;

    /**
     * Construct dtoRemote service.
     *
     * @param dtoImageService       image service
     * @param systemService         system service
     */
    public RemoteImageServiceImpl(final DtoImageService dtoImageService,
                                  final SystemService systemService) {
        super(dtoImageService);
        this.dtoImageService = dtoImageService;
        this.systemService = systemService;
    }

    /**
     * {@inheritDoc}
     */
    public String addImageToRepository(
            final String fullFileName,
            final String code,
            final byte[] imgBody,
            final String storagePrefix,
            final String pathToRepository) throws IOException {

        if (StringUtils.isBlank(pathToRepository)) {

            final String path = systemService.getImageRepositoryDirectory();

            return dtoImageService.addImageToRepository(fullFileName, code, imgBody, storagePrefix, path);

        }

        return dtoImageService.addImageToRepository(fullFileName, code, imgBody, storagePrefix, pathToRepository);
    }

    /**
     * {@inheritDoc}
     */
    public byte[] getImageAsByteArray(final String fileName,
                                      final String code,
                                      final String storagePrefix,
                                      final String pathToRepository) throws IOException {


        if (StringUtils.isBlank(pathToRepository)) {

            final String path = systemService.getImageRepositoryDirectory();

            return dtoImageService.getImageAsByteArray(fileName, code, storagePrefix, path);

        }

        return dtoImageService.getImageAsByteArray(fileName, code, storagePrefix, pathToRepository);

    }

    /**
     * {@inheritDoc}
     */
    public SeoImageDTO getSeoImage(final String imageFileName)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return dtoImageService.getSeoImage(imageFileName);
    }


}
