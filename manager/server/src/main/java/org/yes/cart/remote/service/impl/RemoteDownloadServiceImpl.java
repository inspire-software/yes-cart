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

package org.yes.cart.remote.service.impl;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.bulkcommon.service.ExportDirectorService;
import org.yes.cart.remote.service.RemoteDownloadService;

import java.io.*;
import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * User: denispavlov
 * Date: 27/11/2015
 * Time: 17:43
 */
public class RemoteDownloadServiceImpl implements RemoteDownloadService {

    private final Logger LOG = LoggerFactory.getLogger(RemoteDownloadServiceImpl.class);

    private final ExportDirectorService exportDirectorService;
    private final List<String> allowedPaths;

    public RemoteDownloadServiceImpl(final ExportDirectorService exportDirectorService,
                                     final String allowedPathsCsv) {
        this.exportDirectorService = exportDirectorService;
        this.allowedPaths = new ArrayList<String>();
        for (final String path : StringUtils.split(allowedPathsCsv, ',')) {
            this.allowedPaths.add(path.trim());
        }
    }

    /**
     * {@inheritDoc}
     */
    public byte[] download(final String fileName) throws IOException {

        final String exportRoot = this.exportDirectorService.getExportDirectory();

        final File fileToDownload;
        if (fileName.startsWith("/")) {

            boolean allowed = false;
            for (final String path : this.allowedPaths) {
                if (fileName.startsWith(path)) {
                    allowed = true;
                    break;
                }
            }

            if (!allowed && !fileName.startsWith(exportRoot)) {
                LOG.warn("Attempted to download file from {} ... access denied", fileName);
                throw new AccessDeniedException("Downloading files from specified location is prohibited");
            }

            fileToDownload = new File(fileName);

        } else {

            fileToDownload = new File(exportRoot  + File.separator + fileName);

        }

        if (fileToDownload.exists()) {

            if (fileToDownload.getName().endsWith(".zip")) {
                // Zip's just download
                return FileUtils.readFileToByteArray(fileToDownload);
            } else {
                // Non zip's, zip first
                final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                final ZipOutputStream zos = new ZipOutputStream(baos);
                final InputStream is = new BufferedInputStream(new FileInputStream(fileToDownload));

                byte[] buff = new byte[1024];
                final ZipEntry entry = new ZipEntry(fileToDownload.getName());
                zos.putNextEntry(entry);

                int len;
                while ((len = is.read(buff)) > 0) {
                    zos.write(buff, 0, len);
                }

                is.close();
                zos.closeEntry();
                zos.close();

                return baos.toByteArray();
            }

        }

        throw new IOException("File " + fileToDownload.getAbsolutePath() + " not found");

    }
}
