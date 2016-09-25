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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;
import org.yes.cart.domain.vo.VoDashboardWidget;
import org.yes.cart.service.endpoint.DashboardEndpointController;
import org.yes.cart.service.vo.VoDashboardWidgetService;

import java.util.List;

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
    List<VoDashboardWidget> getDashboard() throws Exception {
        return voDashboardWidgetService.getDashboard();
    }
}
