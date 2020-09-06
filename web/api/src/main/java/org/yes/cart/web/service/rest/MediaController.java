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

package org.yes.cart.web.service.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.yes.cart.domain.ro.AuthenticationResultRO;
import org.yes.cart.domain.ro.LoginRO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Date: 06/09/2020
 * Time: 08:33
 */
@Controller
@Api(value = "Media", description = "Media controller", tags = "media")
public class MediaController {

    @ApiOperation(value = "Image vault interface serves entity dependent media files. " +
            "This controller just defines the API spec, however the serving of files is done in ImageFilter. " +
            "When file is served ETag and Last-Modified headers are sent in response so that clients can use " +
            "If-None-Match and If-Modified-Since to optimise responses and reduce network traffic. If resources " +
            "have not changed a 304 status is sent with no content. ETag expiration is controlled by System " +
            "preference SYSTEM_ETAG_CACHE_IMAGES_TIME")
    @RequestMapping(
            value = "imgvault/{entity}/{media}",
            method = RequestMethod.GET
    )
    public void imgvault(final @ApiParam(value = "Image entity", allowableValues = "category,content,product,brand,shop,system") @PathVariable(value = "entity")  String entity,
                         final @ApiParam(value = "Image name") @PathVariable(value = "media")  String media,
                         final @ApiParam(value = "Image width (px) (for original size omit or use 'as' i.e. 'w=as&h=is')") @RequestParam(value = "w", required = false) String width,
                         final @ApiParam(value = "Image height (px) (for original size omit or use 'is' i.e. 'w=as&h=is')") @RequestParam(value = "h", required = false) String height,
                         final @ApiParam(value = "If-None-Match should contain ETag value from last response") @RequestHeader(value = "If-None-Match", required = false) String ifNoneMatch,
                         final @ApiParam(value = "If-Modified-Since should contain Last-Modified value from last response") @RequestHeader(value = "If-Modified-Since", required = false) String ifModifiedLast) {

    }

    @ApiOperation(value = "File vault interface serves entity dependent media files. " +
            "This controller just defines the API spec, however the serving of files is done in FileFilter")
    @RequestMapping(
            value = "filevault/{entity}/{media}",
            method = RequestMethod.GET
    )
    public void filevault(final @ApiParam(value = "File entity", allowableValues = "category,content,product,brand,shop,system") @PathVariable(value = "entity")  String entity,
                          final @ApiParam(value = "File name") @PathVariable(value = "media")  String media) {

    }

}
