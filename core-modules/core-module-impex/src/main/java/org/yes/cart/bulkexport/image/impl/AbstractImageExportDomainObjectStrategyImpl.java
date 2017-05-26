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

package org.yes.cart.bulkexport.image.impl;

import org.springframework.security.access.AccessDeniedException;
import org.yes.cart.bulkexport.image.ImageExportDomainObjectStrategy;
import org.yes.cart.dao.ResultsIterator;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.domain.ImageService;
import org.yes.cart.service.domain.SystemService;
import org.yes.cart.service.federation.FederationFacade;
import org.yes.cart.service.media.MediaFileNameStrategy;

import java.io.*;
import java.text.MessageFormat;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * User: denispavlov
 * Date: 28/11/2015
 * Time: 21:17
 */
public abstract class AbstractImageExportDomainObjectStrategyImpl<T> implements ImageExportDomainObjectStrategy {

    private final FederationFacade federationFacade;
    protected final ImageService imageService;
    protected final SystemService systemService;


    protected AbstractImageExportDomainObjectStrategyImpl(final FederationFacade federationFacade,
                                                          final ImageService imageService,
                                                          final SystemService systemService) {
        this.federationFacade = federationFacade;
        this.imageService = imageService;
        this.systemService = systemService;
    }

    /**
     * Verify access of the current user.
     *
     * @param object target object to update
     *
     * @throws org.springframework.security.access.AccessDeniedException
     */
    protected void validateAccessBeforeUpdate(final Object object, final Class objectType) throws AccessDeniedException {
        if (!federationFacade.isManageable(object, objectType)) {
            throw new AccessDeniedException("access denied");
        }
    }


    /**
     * {@inheritDoc}
     */
    public boolean doImageExport(final JobStatusListener statusListener,
                                 final String fileName) {

        final ResultsIterator<T> result = getAllObjects();

        final String base = fileName.substring(0, fileName.lastIndexOf('.'));

        int count = 0;
        int i = 0;
        final long maxBytes = 500 * 1024 * 1024; // 500MB
        long bytesLeft = maxBytes;

        try {

            String zip = "";
            ZipOutputStream zos = null;
            byte[] buff = new byte[1024];

            while (result.hasNext()) {

                if (zos == null) {
                    zip = base + (i > 0 ? "." + i : "") + ".zip";
                    zos = new ZipOutputStream(new FileOutputStream(zip));
                    bytesLeft = maxBytes;
                    i++;

                    final String msg = MessageFormat.format(
                            "Creating images zip: {0}",
                            zip);
                    statusListener.notifyMessage(msg);

                }

                final T next = result.next();

                for (final String image : getAllObjectImages(next)) {

                    statusListener.notifyPing("Adding " + image + " to zip file");

                    InputStream is = null;
                    try {
                        is = new BufferedInputStream(getObjectImageAsBytes(next, image));
                    } catch (Exception exp) {
                        statusListener.notifyError("Unable to add " + image + " to zip file\n" + exp.getMessage(), exp);
                        continue;
                    }

                    final ZipEntry entry = new ZipEntry(image);
                    zos.putNextEntry(entry);

                    int len;
                    while ((len = is.read(buff)) > 0) {
                        zos.write(buff, 0, len);
                    }

                    is.close();
                    zos.closeEntry();

                }

                if (bytesLeft <= 0) {
                    zos.close();
                    zos = null;

                    final String msg = MessageFormat.format(
                            "Created images zip: {0}",
                            zip);
                    statusListener.notifyMessage(msg);
                }


                count++;
            }

            if (zos != null) {
                zos.close();
            }
        } catch (Exception exp) {

            statusListener.notifyError(exp.getMessage(), exp);

        }

        return count > 0;

    }

    /**
     * Create image input stream for given object.
     *
     * @param next object
     * @param image image
     *
     * @return image stream
     */
    protected InputStream getObjectImageAsBytes(final T next, final String image) throws IOException {
        final MediaFileNameStrategy strategy = imageService.getImageNameStrategy(getImageRepositoryUrlPattern());
        final String code = strategy.resolveObjectCode(image);

        final byte[] bytes = imageService.imageToByteArray(image, code, strategy.getRelativeInternalRootDirectory(), systemService.getImageRepositoryDirectory());

        return new ByteArrayInputStream(bytes);
    }

    /**
     * Get this strategy's repository domain URL pattern.
     *
     * @return pattern
     */
    protected abstract String getImageRepositoryUrlPattern();


    /** {@inheritDoc} */
    @Override
    public boolean supports(final String uriPattern) {
        return getImageRepositoryUrlPattern().equals(uriPattern);
    }

    /**
     * Get list of all images for given object.
     *
     * @param next object
     *
     * @return list of images
     */
    protected abstract Set<String> getAllObjectImages(final T next);

    /**
     * Get all objects of this type as an iterator.
     *
     * @return result iterator
     */
    protected abstract ResultsIterator<T> getAllObjects();
}
