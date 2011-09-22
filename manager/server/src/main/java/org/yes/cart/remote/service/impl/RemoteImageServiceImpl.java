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
     * @param dtoImageService
     */
    public RemoteImageServiceImpl(final DtoImageService dtoImageService,
                                  final DtoShopService dtoShopService) {
        super(dtoImageService);
        this.dtoImageService = dtoImageService;
        this.dtoShopService = dtoShopService;
    }


    /**
     * Add the given file to image repository.
     * Used from UI to
     *
     * @param fullFileName full path to image file.
     * @param code         product or sku code.
     * @param imgBody      image as byte array.
     * @param pathToRepository not used
     * @return true if file was added successfully
     * @throws java.io.IOException in case of any I/O errors
     */
    public boolean addImageToRepository(
            final String fullFileName,
            final String code,
            final byte[] imgBody,
            final String storagePrefix,
            final String pathToRepository) throws IOException {

        return dtoImageService.addImageToRepository(fullFileName, code, imgBody, storagePrefix, getRealPathPrefix());
    }

    private String getRealPathPrefix() {

        final ShopDTO shopDTO = dtoShopService.getShopDtoByDomainName(
                FlexContext.getHttpRequest().getServerName().toLowerCase()
        );

        return FlexContext.getServletContext().getRealPath("/../yes-shop" + shopDTO.getImageVaultFolder()) + File.separator;

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
        return dtoImageService.getImageAsByteArray(fileName, code, storagePrefix);
    }

    /**
     * {@inheritDoc}
     */
    public SeoImageDTO getSeoImage(final String imageFileName)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return dtoImageService.getSeoImage(imageFileName);
    }


}
