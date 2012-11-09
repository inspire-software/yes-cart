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

import org.yes.cart.domain.dto.SeoImageDTO;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.remote.service.RemoteBackdoorService;
import org.yes.cart.remote.service.RemoteImageService;
import org.yes.cart.service.dto.DtoImageService;
import org.yes.cart.service.dto.DtoShopService;
import org.yes.cart.web.service.ws.client.AsyncFlexContextImpl;

import java.io.File;
import java.io.IOException;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 15-May-2011
 * Time: 17:22:15
 */
public class RemoteImageServiceImpl extends AbstractRemoteService<SeoImageDTO> implements RemoteImageService {


    private final DtoImageService dtoImageService;
    private final DtoShopService dtoShopService;
    private final RemoteBackdoorService remoteBackdoorService;

    /**
     * Construct dtoRemote service.
     *
     * @param dtoImageService       image service
     * @param dtoShopService        shop service to
     * @param remoteBackdoorService to get path to image vault
     */
    public RemoteImageServiceImpl(final DtoImageService dtoImageService,
                                  final DtoShopService dtoShopService,
                                  final RemoteBackdoorService remoteBackdoorService) {
        super(dtoImageService);
        this.dtoImageService = dtoImageService;
        this.dtoShopService = dtoShopService;
        this.remoteBackdoorService = remoteBackdoorService;
    }


    /**
     * {@inheritDoc}
     */
    public String addImageToRepository(
            final String fullFileName,
            final String code,
            final byte[] imgBody,
            final String storagePrefix) throws IOException {

        // TODO: this is quite strange that we have this method on dtoImageService but we do not use it since it puts
        // TODO: a blank string into path? maybe we need to revise all this??
        final String realPath = remoteBackdoorService.getImageVaultPath(new AsyncFlexContextImpl()) + File.separator;
        return addImageToRepository(fullFileName, code, imgBody, storagePrefix, realPath);
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

        return dtoImageService.addImageToRepository(fullFileName, code, imgBody, storagePrefix, pathToRepository);
    }

    /**
     * {@inheritDoc}
     */
    public byte[] getImageAsByteArray(final String fileName,
                                      final String code,
                                      final String storagePrefix) throws IOException {

        // TODO: this is quite strange that we have this method on dtoImageService but we do not use it since it puts
        // TODO: a blank string into path? maybe we need to revise all this??
        final String realPath = remoteBackdoorService.getImageVaultPath(new AsyncFlexContextImpl()) + File.separator;
        return getImageAsByteArray(fileName, code, storagePrefix, realPath);
    }

    /**
     * {@inheritDoc}
     */
    public byte[] getImageAsByteArray(final String fileName,
                                      final String code,
                                      final String storagePrefix,
                                      final String pathToRepository) throws IOException {
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
