/*
 * Copyright 2009 - 2016 Denys Pavlov, Igor Azarnyi
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
import org.yes.cart.domain.vo.VoDashboardWidget;
import org.yes.cart.domain.vo.VoDashboardWidgetInfo;

import java.util.List;

/**
 * User: denispavlov
 * Date: 23/09/2016
 * Time: 09:20
 */
@Controller
@Api(value = "Dashboard", description = "Dashboard controller", tags = "dashboard")
@RequestMapping("/reports")
public interface DashboardEndpointController {

    @ApiOperation(value = "Retrieve all available widgets")
    @PreAuthorize("isFullyAuthenticated()")
    @RequestMapping(value = "/dashboard/available", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoDashboardWidgetInfo> getAvailableWidgets(@ApiParam(value = "Language code", required = true) @RequestParam("lang") String lang) throws Exception;

    @ApiOperation(value = "Update dashboard widgets selection")
    @PreAuthorize("isFullyAuthenticated()")
    @RequestMapping(value = "/dashboard", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    void updateDashboardSelection(@ApiParam(value = "Language code", required = true) @RequestParam("lang") String lang, @ApiParam(value = "Dashboard selection") @RequestBody List<VoDashboardWidgetInfo> dashboard) throws Exception;

    @ApiOperation(value = "Retrieve widgets for current user")
    @PreAuthorize("isFullyAuthenticated()")
    @RequestMapping(value = "/dashboard", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoDashboardWidget> getDashboard(@ApiParam(value = "Language code", required = true) @RequestParam("lang") String lang) throws Exception;

    @ApiOperation(value = "Retrieve widget")
    @PreAuthorize("isFullyAuthenticated()")
    @RequestMapping(value = "/dashboard/{widget}", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoDashboardWidget> getDashboardWidget(@ApiParam(value = "Widget ID", required = true) @PathVariable("widget") String widget, @ApiParam(value = "Language code", required = true) @RequestParam("lang") String lang) throws Exception;


}
