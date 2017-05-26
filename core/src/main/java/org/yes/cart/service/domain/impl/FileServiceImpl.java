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

package org.yes.cart.service.domain.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.yes.cart.service.domain.FileService;
import org.yes.cart.service.domain.SystemService;
import org.yes.cart.service.media.MediaFileNameStrategy;
import org.yes.cart.service.media.MediaFileNameStrategyResolver;
import org.yes.cart.stream.io.IOProvider;

import java.io.IOException;
import java.util.Collections;
import java.util.Properties;

/**
 * Image service to resize and store resized image.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class FileServiceImpl implements FileService {

    private static final Logger LOG = LoggerFactory.getLogger(FileServiceImpl.class);

    private boolean replaceFilesModeOn;

    private final MediaFileNameStrategyResolver mediaFileNameStrategyResolver;

    private final IOProvider ioProvider;


    /**
     * Construct file service.
     *
     * @param mediaFileNameStrategyResolver the image name strategy resolver
     * @param replaceFilesModeOn if set to true will overwrite existing file if name is
     *                           the same. Recommended setting is 'true'.
     * @param ioProvider IO provider
     */
    public FileServiceImpl(final MediaFileNameStrategyResolver mediaFileNameStrategyResolver,
                           final boolean replaceFilesModeOn,
                           final IOProvider ioProvider) {

        this.mediaFileNameStrategyResolver = mediaFileNameStrategyResolver;
        this.ioProvider = ioProvider;

        this.replaceFilesModeOn = replaceFilesModeOn;
    }

    public void setConfig(final Resource config) throws IOException {

        final Properties properties = new Properties();
        properties.load(config.getInputStream());

    }

    /** {@inheritDoc} */
    public MediaFileNameStrategy getFileNameStrategy(final String url) {
        return mediaFileNameStrategyResolver.getMediaFileNameStrategy(url);
    }

    /** {@inheritDoc} */
    public boolean isFileInRepository(final String fullFileName,
                                      final String code,
                                      final String storagePrefix,
                                      final String pathToRepository) {

        final MediaFileNameStrategy strategy = getFileNameStrategy(storagePrefix);
        final String filename = strategy.resolveFileName(fullFileName);

        String pathInRepository = pathToRepository + strategy.resolveRelativeInternalFileNamePath(filename, code, null);

        return ioProvider.exists(pathInRepository, Collections.EMPTY_MAP);

    }

    /** {@inheritDoc} */
    public String addFileToRepository(final String fullFileName,
                                      final String code,
                                      final byte[] fileBody,
                                      final String storagePrefix,
                                      final String pathToRepository) throws IOException {

        final MediaFileNameStrategy strategy = getFileNameStrategy(storagePrefix);
        final String filename = strategy.resolveFileName(fullFileName);
        final String suffix = strategy.resolveSuffix(fullFileName);
        final String locale = strategy.resolveLocale(fullFileName);

        String pathInRepository = pathToRepository + strategy.resolveRelativeInternalFileNamePath(filename, code, null);

        final String uniqueName = createRepositoryUniqueName(pathInRepository, ioProvider, strategy, code, suffix, locale);

        ioProvider.write(uniqueName, fileBody, Collections.EMPTY_MAP);

        return strategy.resolveFileName(uniqueName);
    }


    /*
     * Check is given file name present on disk and create new if necessary.
     * Return sequential file name.
     */
    String createRepositoryUniqueName(final String fileName,
                                      final IOProvider ioProvider,
                                      final MediaFileNameStrategy strategy,
                                      final String code,
                                      final String suffix,
                                      final String locale) {
        if (this.replaceFilesModeOn) {
            return fileName;
        }
        if (ioProvider.exists(fileName, Collections.EMPTY_MAP)) {
            final String newFileName = strategy.createRollingFileName(fileName, code, suffix, locale);
            return createRepositoryUniqueName(newFileName, ioProvider, strategy, code, suffix, locale);
        }
        return fileName;
    }

    /** {@inheritDoc} */
    public byte[] fileToByteArray(final String fileName,
                                  final String code,
                                  final String storagePrefix,
                                  final String pathToRepository) throws IOException {

        final MediaFileNameStrategy strategy = getFileNameStrategy(storagePrefix);
        final String file = strategy.resolveFileName(fileName);
        String pathInRepository = pathToRepository + strategy.resolveRelativeInternalFileNamePath(file, code, null);

        return ioProvider.read(pathInRepository, Collections.EMPTY_MAP);

    }

    /** {@inheritDoc}*/
    public boolean deleteFile(final String imageFileName,
                              final String storagePrefix,
                              final String pathToRepository) {

        final MediaFileNameStrategy strategy = getFileNameStrategy(storagePrefix);
        final String file = strategy.resolveFileName(imageFileName);
        final String code = strategy.resolveObjectCode(imageFileName);
        String pathInRepository = pathToRepository + strategy.resolveRelativeInternalFileNamePath(file, code, null);

        try {
            ioProvider.delete(pathInRepository, Collections.EMPTY_MAP);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Spring IoC.
     *
     * @return system service
     */
    public SystemService getSystemService() {
        return null;
    }

}
