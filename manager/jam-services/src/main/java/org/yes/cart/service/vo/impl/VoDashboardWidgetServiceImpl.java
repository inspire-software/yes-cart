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

import org.apache.commons.collections.CollectionUtils;
import org.yes.cart.domain.vo.VoDashboardWidget;
import org.yes.cart.domain.vo.VoDashboardWidgetInfo;
import org.yes.cart.domain.vo.VoManager;
import org.yes.cart.service.vo.VoDashboardWidgetPlugin;
import org.yes.cart.service.vo.VoDashboardWidgetService;
import org.yes.cart.service.vo.VoManagementService;

import java.util.*;

/**
 * User: denispavlov
 * Date: 23/09/2016
 * Time: 19:32
 */
public class VoDashboardWidgetServiceImpl implements VoDashboardWidgetService {

    private final VoManagementService managementService;

    private List<VoDashboardWidgetPlugin> plugins = new ArrayList<>();

    private List<String> defaultWidgets;

    public VoDashboardWidgetServiceImpl(final VoManagementService managementService) {
        this.managementService = managementService;
    }

    @Override
    public List<VoDashboardWidgetInfo> getAvailableWidgets(final String lang) throws Exception {

        final List<VoDashboardWidgetInfo> widgets = new ArrayList<>();

        final VoManager manager = this.managementService.getMyself();
        if (manager != null) {

            final Set<String> selected = CollectionUtils.isNotEmpty(manager.getDashboardWidgets()) ? new HashSet<>(manager.getDashboardWidgets()) : null;

            for (final VoDashboardWidgetPlugin plugin : this.plugins) {

                if (isAvailable(selected, plugin, manager)) {

                    widgets.add(plugin.getWidgetInfo(manager, lang));

                }

            }

        }

        Collections.sort(widgets, (a, b) -> {
            final String nameA = a.getWidgetDescription() != null ? a.getWidgetDescription() : a.getWidgetId();
            final String nameB = b.getWidgetDescription() != null ? b.getWidgetDescription() : b.getWidgetId();
            return nameA.compareToIgnoreCase(nameB);
        });

        return widgets;
    }

    @Override
    public void updateDashboardSelection(final String dashboard) throws Exception {
        final VoManager manager = this.managementService.getMyself();
        if (manager != null) {
            this.managementService.updateDashboard(manager.getManagerId(), dashboard);
        }
    }

    @Override
    public List<VoDashboardWidget> getDashboard(final List<String> filter, final String lang) throws Exception {

        final List<VoDashboardWidget> widgets = new ArrayList<>();

        final VoManager manager = this.managementService.getMyself();
        if (manager != null) {

            final Map<String, VoDashboardWidget> widgetsMap = new HashMap<>();

            final List<String> ordered;
            if (filter == null && CollectionUtils.isNotEmpty(manager.getDashboardWidgets())) {
                ordered = new ArrayList<>(manager.getDashboardWidgets());
            } else if (filter == null) {
                ordered = defaultWidgets;
            } else {
                ordered = filter;
            }

            final Set<String> nonNullFilter = CollectionUtils.isNotEmpty(ordered) ? new HashSet<>(ordered) : null;

            for (final VoDashboardWidgetPlugin plugin : this.plugins) {

                if (isApplicable(nonNullFilter, plugin, manager)) {

                    widgetsMap.put(plugin.getName(), plugin.getWidget(manager, lang));

                }

            }

            for (final String item : ordered) {

                final VoDashboardWidget widget = widgetsMap.get(item);
                if (widget != null) {
                    widgets.add(widget);
                }

            }

        }

        return widgets;
    }

    private boolean isAvailable(final Set<String> selected, final VoDashboardWidgetPlugin plugin, final VoManager manager) {
        return (selected == null || !selected.contains(plugin.getName())) && plugin.applicable(manager);
    }

    private boolean isApplicable(final Set<String> filter, final VoDashboardWidgetPlugin plugin, final VoManager manager) {
        return (filter == null || filter.contains(plugin.getName())) && plugin.applicable(manager);
    }

    public void setPlugins(final List<VoDashboardWidgetPlugin> plugins) {
        this.plugins = plugins;
    }

    public void setDefaultWidgets(final List<String> defaultWidgets) {
        this.defaultWidgets = defaultWidgets;
    }

    /** {@inheritDoc} */
    @Override
    public void registerWidgetPlugin(final VoDashboardWidgetPlugin dashboardWidgetPlugin) {
        if (!this.plugins.contains(dashboardWidgetPlugin)) {
            this.plugins.add(dashboardWidgetPlugin);
        }
    }
}
