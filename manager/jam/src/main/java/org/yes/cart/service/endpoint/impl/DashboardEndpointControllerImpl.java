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
package org.yes.cart.service.endpoint.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.yes.cart.domain.vo.VoDashboardWidget;
import org.yes.cart.domain.vo.VoDashboardWidgetInfo;
import org.yes.cart.service.endpoint.DashboardEndpointController;
import org.yes.cart.service.vo.VoDashboardWidgetService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * User: denispavlov
 * Date: 23/09/2016
 * Time: 09:25
 */
@Component
public class DashboardEndpointControllerImpl implements DashboardEndpointController {

    private final VoDashboardWidgetService voDashboardWidgetService;

    @Autowired
    public DashboardEndpointControllerImpl(final VoDashboardWidgetService voDashboardWidgetService) {
        this.voDashboardWidgetService = voDashboardWidgetService;
    }

    @Override
    public @ResponseBody
    List<VoDashboardWidgetInfo> getAvailableWidgets(final @PathVariable("lang") String lang) throws Exception {
        return voDashboardWidgetService.getAvailableWidgets(lang);
    }

    @Override
    public @ResponseBody
    void updateDashboardSelection(final @RequestBody List<VoDashboardWidgetInfo> dashboard) throws Exception {
        String dashboardCsv = null;
        if (CollectionUtils.isNotEmpty(dashboard)) {
            final List<String> wid = dashboard.stream().map(VoDashboardWidgetInfo::getWidgetId).collect(Collectors.toList());
            dashboardCsv = StringUtils.join(wid, ',');
        }
        voDashboardWidgetService.updateDashboardSelection(dashboardCsv);
    }

    @Override
    public @ResponseBody
    List<VoDashboardWidget> getDashboard(final @PathVariable("lang") String lang) throws Exception {
        return voDashboardWidgetService.getDashboard(null, lang);
    }

    @Override
    public @ResponseBody
    List<VoDashboardWidget> getDashboardWidget(final @PathVariable("widget") String widget, final @PathVariable("lang") String lang) throws Exception {
        return voDashboardWidgetService.getDashboard(Collections.singletonList(widget), lang);
    }
}
