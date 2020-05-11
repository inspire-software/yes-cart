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
@Api(value = "Dashboard", tags = "dashboard")
@RequestMapping("/reports")
public interface DashboardEndpointController {

    @PreAuthorize("isFullyAuthenticated()")
    @RequestMapping(value = "/dashboard/{lang}/available", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoDashboardWidgetInfo> getAvailableWidgets(@PathVariable("lang") String lang) throws Exception;

    @PreAuthorize("isFullyAuthenticated()")
    @RequestMapping(value = "/dashboard/{lang}", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    void updateDashboardSelection(@RequestBody List<VoDashboardWidgetInfo> dashboard) throws Exception;

    @PreAuthorize("isFullyAuthenticated()")
    @RequestMapping(value = "/dashboard/{lang}", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoDashboardWidget> getDashboard(@PathVariable("lang") String lang) throws Exception;

    @PreAuthorize("isFullyAuthenticated()")
    @RequestMapping(value = "/dashboard/{lang}/{widget}/", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoDashboardWidget> getDashboardWidget(@PathVariable("widget") String widget, @PathVariable("lang") String lang) throws Exception;


}
