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

import java.io.File;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;

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

            return FileUtils.readFileToByteArray(new File(fileName));

        }
        return FileUtils.readFileToByteArray(new File(exportRoot  + File.separator + fileName));

    }
}
