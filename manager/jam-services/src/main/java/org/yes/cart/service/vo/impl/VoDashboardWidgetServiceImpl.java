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

package org.yes.cart.service.vo.impl;

import org.yes.cart.domain.vo.VoDashboardWidget;
import org.yes.cart.domain.vo.VoManager;
import org.yes.cart.domain.vo.VoManagerInfo;
import org.yes.cart.service.vo.VoDashboardWidgetPlugin;
import org.yes.cart.service.vo.VoDashboardWidgetService;
import org.yes.cart.service.vo.VoManagementService;

import java.util.ArrayList;
import java.util.List;

/**
 * User: denispavlov
 * Date: 23/09/2016
 * Time: 19:32
 */
public class VoDashboardWidgetServiceImpl implements VoDashboardWidgetService {

    private final VoManagementService managementService;

    private List<VoDashboardWidgetPlugin> plugins = new ArrayList<>();

    public VoDashboardWidgetServiceImpl(final VoManagementService managementService) {
        this.managementService = managementService;
    }

    @Override
    public List<VoDashboardWidget> getDashboard() throws Exception {

        final List<VoDashboardWidget> widgets = new ArrayList<>();

        final VoManager manager = this.managementService.getMyself();
        if (manager != null) {

            for (final VoDashboardWidgetPlugin plugin : this.plugins) {

                if (plugin.applicable(manager)) {

                    widgets.add(plugin.getWidget(manager));

                }

            }

        }

        return widgets;
    }

    public void setPlugins(final List<VoDashboardWidgetPlugin> plugins) {
        this.plugins = plugins;
    }

    /** {@inheritDoc} */
    @Override
    public void registerWidgetPlugin(final VoDashboardWidgetPlugin dashboardWidgetPlugin) {
        if (!this.plugins.contains(dashboardWidgetPlugin)) {
            this.plugins.add(dashboardWidgetPlugin);
        }
    }
}
