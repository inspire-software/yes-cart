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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.bulkimport.service.ImportDirectorService;
import org.yes.cart.constants.Constants;
import org.yes.cart.remote.service.RemoteUploadService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Upload file to server side.
 * <p/>
 * Igor Azarny iazarny@yahoo.com
 * Date: 10/12/11
 * Time: 18:11
 */
public class RemoteUploadServiceImpl implements RemoteUploadService {

    private static final Logger LOG = LoggerFactory.getLogger(RemoteUploadServiceImpl.class);

    private final ImportDirectorService importDirectorService;

    public RemoteUploadServiceImpl(final ImportDirectorService importDirectorService) {
        this.importDirectorService = importDirectorService;
    }

    /**
     * {@inheritDoc}
     */
    public String upload(final byte[] bytes, final String fileName) throws IOException {

        final String folderPath = importDirectorService.getImportDirectory();

        final File folder = new File(folderPath);
        folder.mkdirs();

        if (folder.exists()) {

            FileOutputStream fos = null;
            try {
                final File file = new File(folderPath + File.separator + fileName);
                if (file.exists()) {
                    throw new IllegalArgumentException("File: " + file.getName() + " is already being processed. If this is a different file - rename it and try again.");
                }
                file.createNewFile();
                fos = new FileOutputStream(file);
                fos.write(bytes);
                return file.getAbsolutePath();
            } catch (IOException ioe) {
                LOG.error("Error during file upload", ioe);
                if (fos != null) {
                    fos.close();
                }
                throw ioe;
            } finally {
                if (fos != null) {
                    fos.close();
                }
            }
        } else {
            throw new IOException("Unable to create directory: " + folderPath);
        }
    }

}
