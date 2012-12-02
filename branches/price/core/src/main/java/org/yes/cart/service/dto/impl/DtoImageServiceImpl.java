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

package org.yes.cart.service.dto.impl;

import com.inspiresoftware.lib.dto.geda.adapter.repository.AdaptersRepository;
import org.apache.commons.lang.StringUtils;
import org.yes.cart.domain.dto.SeoImageDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.dto.impl.SeoImageDTOImpl;
import org.yes.cart.domain.entity.SeoImage;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.domain.GenericService;
import org.yes.cart.service.domain.ImageService;
import org.yes.cart.service.dto.DtoImageService;

import java.io.IOException;

/**
* User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DtoImageServiceImpl
        extends AbstractDtoServiceImpl<SeoImageDTO, SeoImageDTOImpl, SeoImage>
        implements DtoImageService {

    private final ImageService imageService;

    /**
     * Construct base remote service.
     *
     * @param dtoFactory               {@link org.yes.cart.domain.dto.factory.DtoFactory}
     * @param seoImageGenericService                  {@link org.yes.cart.service.domain.GenericService}
     * @param adaptersRepository {@link com.inspiresoftware.lib.dto.geda.adapter.repository.AdaptersRepository}
     */
    public DtoImageServiceImpl(final DtoFactory dtoFactory,
                               final GenericService<SeoImage> seoImageGenericService,
                               final AdaptersRepository adaptersRepository) {
        super(dtoFactory, seoImageGenericService, adaptersRepository);
        imageService = (ImageService) seoImageGenericService;
    }

    /**
     * {@inheritDoc}
     */
    public String addImageToRepository(final String fullFileName,
                                        final String code,
                                        final byte[] imgBody,
                                        final String storagePrefix) throws IOException {
        return imageService.addImageToRepository(fullFileName, code, imgBody, storagePrefix, StringUtils.EMPTY);
    }

    /**
     * Add the given file to image repository.
     * Used from UI to
     *
     * @param fullFileName full path to image file.
     * @param code         product or sku code.
     * @param imgBody      image as byte array.
     * @param storagePrefix optional storage prefix {@see Constants.CATEGOTY_IMAGE_REPOSITORY_URL_PATTERN}
     * or {@see Constants.BRAND_IMAGE_REPOSITORY_URL_PATTERN}. If parameter not provider the product image storage will be used.
     * @param pathToRepository path to repository
     * @return true if file was added successfully
     * @throws java.io.IOException in case of any I/O errors
     */
    public String addImageToRepository(final String fullFileName,
                                        final String code,
                                        final byte[] imgBody,
                                        final String storagePrefix,
                                        final String pathToRepository) throws IOException {
        return imageService.addImageToRepository(fullFileName, code, imgBody, storagePrefix, pathToRepository);
    }

    /**
     * Read product or sku image into byte array.
     *
     * @param fileName file name from attribute
     * @param code     product or sku code
     * @return byte array
     * @throws java.io.IOException in case of any I/O errors
     */
    public byte[] getImageAsByteArray(final String fileName, final String code, final String storagePrefix) throws IOException {
        return getImageAsByteArray(fileName, code, storagePrefix, StringUtils.EMPTY);
    }

    /**
     * Read product or sku image into byte array.
     *
     * @param fileName file name from attribute
     * @param code     product or sku code
     * @param pathToRepository path to repository
     * @return byte array
     * @throws java.io.IOException in case of any I/O errors
     */
    public byte[] getImageAsByteArray(final String fileName, final String code, final String storagePrefix, final String pathToRepository) throws IOException {
        return imageService.getImageAsByteArray(fileName, code, storagePrefix, pathToRepository);
    }

    /**
     * Get {@link SeoImageDTO} by given file name.
     * @param imageFileName fiven file name.
     * @return instance of {@link SeoImageDTO} or null if not found.
     */
    public SeoImageDTO getSeoImage(final String imageFileName) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final SeoImage seoImage = imageService.getSeoImage(imageFileName);
        if (seoImage != null) {
            final SeoImageDTO seoImageDTO = getNew();
            assembler.assembleDto(seoImageDTO, seoImage, getAdaptersRepository(), dtoFactory);
            return seoImageDTO;
        }
        return null;
    }



    /**
     * Get the dto interface.
     *
     * @return dto interface.
     */
    public Class<SeoImageDTO> getDtoIFace() {
        return SeoImageDTO.class;
    }

    /**
     * Get the dto implementation class.
     *
     * @return dto implementation class.
     */
    public Class<SeoImageDTOImpl> getDtoImpl() {
        return SeoImageDTOImpl.class;
    }

    /**
     * Get the entity interface.
     *
     * @return entity interface.
     */
    public Class<SeoImage> getEntityIFace() {
        return SeoImage.class;
    }
}
