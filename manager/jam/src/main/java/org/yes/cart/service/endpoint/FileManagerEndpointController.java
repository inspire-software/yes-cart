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
package org.yes.cart.service.endpoint;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.yes.cart.domain.misc.MutablePair;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * User: denispavlov
 * Date: 13/09/2016
 * Time: 11:38
 */
@Controller
@Api(value = "File Manager", description = "File manager controller", tags = "file")
@RequestMapping("/filemanager")
public interface FileManagerEndpointController {


    /**
     * List available files
     *
     * @return pair of absolute and human friendly paths
     * @throws IOException errors
     */
    @ApiOperation(value = "List files available in current user directory")
    @PreAuthorize("isFullyAuthenticated()")
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<MutablePair<String, String>> list(@ApiParam(value = "Access mode", allowableValues = "import,export,*", required = true) @RequestParam("mode") String mode) throws IOException;

    /**
     * Download given file as bytes.
     * @param fileName file name
     */
    @ApiOperation(value = "Download specified file")
    @PreAuthorize("isFullyAuthenticated()")
    @RequestMapping(value = "/download", method = RequestMethod.GET)
    void download(@ApiParam(value = "Fully qualified file path (see /list)") @RequestParam("fileName") String fileName, HttpServletResponse response) throws IOException;

    /**
     * Store given bytes as file.
     * @param file file body.
     */
    @ApiOperation(value = "Upload a file")
    @PreAuthorize("isFullyAuthenticated()")
    @RequestMapping(value = "/upload", method = RequestMethod.POST, consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    @ResponseBody
    void upload(@ApiParam(value = "File as multipart/form-data") @RequestParam("file") MultipartFile file) throws IOException;

    /**
     * Delete given file.
     * @param fileName file name
     */
    @ApiOperation(value = "Delete a file")
    @PreAuthorize("isFullyAuthenticated()")
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    @ResponseBody
    void delete(@ApiParam(value = "Fully qualified file path (see /list)") @RequestParam("fileName") String fileName) throws IOException;


}
