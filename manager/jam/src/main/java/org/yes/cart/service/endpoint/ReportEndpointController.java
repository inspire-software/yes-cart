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
import org.yes.cart.domain.vo.VoReportDescriptor;
import org.yes.cart.domain.vo.VoReportRequest;

import java.util.List;

/**
 * User: denispavlov
 * Date: 02/10/2016
 * Time: 12:35
 */
@Controller
@Api(value = "Reports", description = "Reports controller", tags = "reports")
@RequestMapping("/reports")
public interface ReportEndpointController {

    @ApiOperation(value = "Retrieve all available reports")
    @PreAuthorize("isFullyAuthenticated()")
    @RequestMapping(value = "", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoReportDescriptor> getReportDescriptors(@ApiParam(value = "Language code", required = true) @RequestParam("lang") String language);

    @ApiOperation(value = "Configure report parameters")
    @PreAuthorize("isFullyAuthenticated()")
    @RequestMapping(value = "/configure", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoReportRequest getParameterValues(@ApiParam(value = "Report request", name = "vo", required = true) @RequestBody VoReportRequest reportRequest);

    @ApiOperation(value = "Generate report")
    @PreAuthorize("isFullyAuthenticated()")
    @RequestMapping(value = "/generate", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    String generateReport(@ApiParam(value = "Report request", name = "vo", required = true) @RequestBody VoReportRequest reportRequest) throws Exception;

}
