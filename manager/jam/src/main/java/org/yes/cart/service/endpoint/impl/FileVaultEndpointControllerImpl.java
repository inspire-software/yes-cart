/*
 * Copyright 2009- 2016 Denys Pavlov, Igor Azarnyi
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
package org.yes.cart.service.endpoint.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.yes.cart.service.domain.FileService;
import org.yes.cart.service.domain.ImageService;
import org.yes.cart.service.domain.SystemService;
import org.yes.cart.service.endpoint.FileVaultEndpointController;
import org.yes.cart.service.media.MediaFileNameStrategy;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * User: denispavlov
 * Date: 13/09/2016
 * Time: 12:11
 */
@Controller
public class FileVaultEndpointControllerImpl implements FileVaultEndpointController {

    private final FileService fileService;
    private final ImageService imageService;
    private final SystemService systemService;

    @Autowired
    public FileVaultEndpointControllerImpl(final FileService fileService,
                                           final ImageService imageService,
                                           final SystemService systemService) {
        this.fileService = fileService;
        this.imageService = imageService;
        this.systemService = systemService;
    }

    @Override
    public void downloadImage(@PathVariable("type") final String type, @RequestParam("fileName") final String fileName, final HttpServletResponse response) throws IOException {

        final MediaFileNameStrategy strategy = this.imageService.getImageNameStrategy("/imgvault/" + type + "/");


        final String name = strategy.resolveFileName(fileName);
        final String code = strategy.resolveObjectCode(fileName);

        byte[] file = new byte[0];
        if (this.imageService.isImageInRepository(name, code, strategy.getUrlPath(), systemService.getImageRepositoryDirectory())) {

            file = this.imageService.imageToByteArray(name, code, strategy.getUrlPath(), systemService.getImageRepositoryDirectory());

        }

        downloadFileAsZip(name, file, response);

    }

    @Override
    public void downloadFile(@PathVariable("type") final String type, @RequestParam("fileName") final String fileName, final HttpServletResponse response) throws IOException {

        final MediaFileNameStrategy strategy = this.fileService.getFileNameStrategy("/filevault/" + type + "/");


        final String name = strategy.resolveFileName(fileName);
        final String code = strategy.resolveObjectCode(fileName);

        byte[] file = new byte[0];
        if (this.fileService.isFileInRepository(name, code, strategy.getUrlPath(), systemService.getFileRepositoryDirectory())) {

            file = this.fileService.fileToByteArray(name, code, strategy.getUrlPath(), systemService.getFileRepositoryDirectory());

        }

        downloadFileAsZip(name, file, response);

    }

    @Override
    public void downloadSysFile(@PathVariable("type") final String type, @RequestParam("fileName") final String fileName, final HttpServletResponse response) throws IOException {

        final MediaFileNameStrategy strategy = this.fileService.getFileNameStrategy("/sysfilevault/" + type + "/");


        final String name = strategy.resolveFileName(fileName);
        final String code = strategy.resolveObjectCode(fileName);

        byte[] file = new byte[0];
        if (this.fileService.isFileInRepository(name, code, strategy.getUrlPath(), systemService.getSystemFileRepositoryDirectory())) {

            file = this.fileService.fileToByteArray(name, code, strategy.getUrlPath(), systemService.getSystemFileRepositoryDirectory());

        }

        downloadFileAsZip(name, file, response);

    }

    private void downloadFileAsZip(final String fileName, final byte[] file, final HttpServletResponse response) throws IOException {

        if (file == null || file.length == 0) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        } else {

            response.setContentType("application/zip, application/octet-stream");

            final String nameOnly;
            if (fileName.contains("/")) {
                nameOnly = fileName.substring(fileName.lastIndexOf('/') + 1).concat(".zip");
            } else {
                nameOnly = fileName.concat(".zip");
            }

            response.addHeader("Content-Disposition", "attachment; filename=" + nameOnly);


            byte[] content = file;
            if (!fileName.toLowerCase().endsWith(".zip")) {
                // Non zip's, zip first
                final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                final ZipOutputStream zos = new ZipOutputStream(baos);
                final InputStream is = new ByteArrayInputStream(file);

                byte[] buff = new byte[1024];
                final ZipEntry entry = new ZipEntry(fileName);
                zos.putNextEntry(entry);

                int len;
                while ((len = is.read(buff)) > 0) {
                    zos.write(buff, 0, len);
                }

                is.close();
                zos.closeEntry();
                zos.close();
                content = baos.toByteArray();
            }

            response.setContentLength(content.length);

            response.getOutputStream().write(content);
            response.flushBuffer();
        }

    }

}
