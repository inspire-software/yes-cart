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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.yes.cart.domain.misc.MutablePair;
import org.yes.cart.remote.service.FileManager;
import org.yes.cart.service.endpoint.FileManagerEndpointController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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

    public @ResponseBody
    List<MutablePair<String, String>> list(@PathVariable("mode") final String mode) throws IOException {
        return fileManager.list(mode);
    }

    public void download(@RequestParam("fileName") final String fileName, final HttpServletResponse response) throws IOException {

        final byte[] content = this.fileManager.download(fileName);

        response.setContentType("application/zip, application/octet-stream");

        final String nameOnly;
        if (fileName.contains("/")) {
            nameOnly = fileName.substring(fileName.lastIndexOf('/') + 1).concat(".zip");
        } else {
            nameOnly = fileName.concat(".zip");
        }

        response.addHeader("Content-Disposition", "attachment; filename=" + nameOnly);

        response.setContentLength(content.length);

        response.getOutputStream().write(content);

        response.flushBuffer();

    }

    public String upload(@RequestParam("file") MultipartFile file) throws IOException {
        this.fileManager.upload(file.getBytes(), file.getOriginalFilename());
        return "upload-success";
    }

    public @ResponseBody
    void delete(@RequestParam("fileName") final String fileName) throws IOException {
        this.fileManager.delete(fileName);
    }
}
