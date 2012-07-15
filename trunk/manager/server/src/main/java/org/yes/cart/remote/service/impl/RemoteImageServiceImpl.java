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

import flex.messaging.FlexContext;
import org.yes.cart.domain.dto.SeoImageDTO;
import org.yes.cart.domain.dto.ShopDTO;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.remote.service.RemoteImageService;
import org.yes.cart.service.dto.DtoImageService;
import org.yes.cart.service.dto.DtoShopService;

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

    /**
     * Construct dtoRemote service.
     *
     * @param dtoImageService image service
     * @param dtoShopService shop service to 
     */
    public RemoteImageServiceImpl(final DtoImageService dtoImageService,
                                  final DtoShopService dtoShopService) {
        super(dtoImageService);
        this.dtoImageService = dtoImageService;
        this.dtoShopService = dtoShopService;
    }


    /**
     * {@inheritDoc}
     */
    public boolean addImageToRepository(
            final String fullFileName,
            final String code,
            final byte[] imgBody,
            final String storagePrefix) throws IOException {

        return addImageToRepository(fullFileName, code, imgBody, storagePrefix, getRealPathPrefix());
    }

    /**
     * {@inheritDoc}
     */
    public boolean addImageToRepository(
            final String fullFileName,
            final String code,
            final byte[] imgBody,
            final String storagePrefix,
            final String pathToRepository) throws IOException {

        return dtoImageService.addImageToRepository(fullFileName, code, imgBody, storagePrefix, pathToRepository);
    }

    /**
     * TODO
     * TODO yes-shop !!! This is dirty hack !!!!! Eliminate this !!!!
     * TODO it work under tomcat, but not sure about other AS
     * @return   path to external web context
     */
    private String getRealPathPrefix() {

        final ShopDTO shopDTO = dtoShopService.getShopDtoByDomainName(
                FlexContext.getHttpRequest().getServerName().toLowerCase()
        );
        //TODO storefront context
        return FlexContext.getServletContext().getRealPath("/../yes-shop" + shopDTO.getImageVaultFolder()) + File.separator;

    }

    /**
     * {@inheritDoc}
     */
    public byte[] getImageAsByteArray(final String fileName,
                                      final String code,
                                      final String storagePrefix) throws IOException {
        return getImageAsByteArray(fileName, code, storagePrefix, getRealPathPrefix());
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
