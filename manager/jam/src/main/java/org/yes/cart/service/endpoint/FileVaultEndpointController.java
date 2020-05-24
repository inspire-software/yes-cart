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
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * User: denispavlov
 * Date: 11/05/2017
 * Time: 19:17
 */
@Controller
@Api(value = "File Vault", description = "File vault controller", tags = "file")
@RequestMapping("/filevault")
public interface FileVaultEndpointController {


    /**
     * Download given file as bytes.
     * @param fileName file name
     */
    @ApiOperation(value = "Download image from image vault")
    @PreAuthorize("isFullyAuthenticated()")
    @RequestMapping(value = "/images/{type}", method = RequestMethod.GET)
    void downloadImage(@ApiParam(value = "Type", allowableValues = "category,content,product,brand,shop,system") @PathVariable("type") String type, @ApiParam(value = "File name") @RequestParam("fileName") String fileName, HttpServletResponse response) throws IOException;

    /**
     * Download given file as bytes.
     * @param fileName file name
     */
    @ApiOperation(value = "Download file from file vault")
    @PreAuthorize("isFullyAuthenticated()")
    @RequestMapping(value = "/files/{type}", method = RequestMethod.GET)
    void downloadFile(@ApiParam(value = "Type", allowableValues = "category,content,product,brand,shop,system") @PathVariable("type") String type, @ApiParam(value = "File name") @RequestParam("fileName") String fileName, HttpServletResponse response) throws IOException;

    /**
     * Download given file as bytes.
     * @param fileName file name
     */
    @ApiOperation(value = "Download file from system file vault")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMSHOPUSER"})
    @RequestMapping(value = "/sysfiles/{type}", method = RequestMethod.GET)
    void downloadSysFile(@ApiParam(value = "Type", allowableValues = "shop,system") @PathVariable("type") String type, @ApiParam(value = "File name") @RequestParam("fileName") String fileName, HttpServletResponse response) throws IOException;

}
