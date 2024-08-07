/*
 * Copyright 2009 Inspire-Software.com
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.yes.cart.domain.misc.MutablePair;
import org.yes.cart.remote.service.FileManager;
import org.yes.cart.service.endpoint.FileManagerEndpointController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLConnection;
import java.util.List;

/**
 * User: denispavlov
 * Date: 13/09/2016
 * Time: 12:11
 */
@Controller
public class FileManagerEndpointControllerImpl implements FileManagerEndpointController {

    private final FileManager fileManager;

    @Autowired
    public FileManagerEndpointControllerImpl(final FileManager fileManager) {
        this.fileManager = fileManager;
    }

    @Override
    public @ResponseBody
    List<MutablePair<String, String>> list(@RequestParam("mode") final String mode) throws IOException {
        return fileManager.list(mode);
    }

    @Override
    public void download(@RequestParam("fileName") final String fileName, @RequestParam(value = "rawFile", required = false) final boolean rawFile, final HttpServletResponse response) throws IOException {

        final byte[] content = this.fileManager.download(fileName, rawFile);

        final String nameOnly;
        if (fileName.contains("/")) {
            nameOnly = fileName.substring(fileName.lastIndexOf('/') + 1);
        } else {
            nameOnly = fileName;
        }

        final String attachmentFileName;
        if (rawFile) {
            response.setContentType(URLConnection.guessContentTypeFromName(fileName));
            attachmentFileName = nameOnly;
        } else {
            response.setContentType("application/zip, application/octet-stream");
            attachmentFileName = nameOnly.concat(".zip");
        }

        response.addHeader("Content-Disposition", "attachment; filename=" + attachmentFileName);

        response.setContentLength(content.length);

        response.getOutputStream().write(content);

        response.flushBuffer();

    }

    @Override
    public @ResponseBody
    void upload(@RequestParam("file") final MultipartFile file) throws IOException {
        this.fileManager.upload(file.getBytes(), file.getOriginalFilename());
    }

    @Override
    public @ResponseBody
    void delete(@RequestParam("fileName") final String fileName) throws IOException {
        this.fileManager.delete(fileName);
    }
}
