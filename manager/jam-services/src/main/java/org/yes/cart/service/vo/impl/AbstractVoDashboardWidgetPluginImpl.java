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

package org.yes.cart.service.vo.impl;

import org.yes.cart.constants.AttributeGroupNames;
import org.yes.cart.domain.entity.Attribute;
import org.yes.cart.domain.i18n.impl.FailoverStringI18NModel;
import org.yes.cart.domain.vo.VoDashboardWidget;
import org.yes.cart.domain.vo.VoDashboardWidgetInfo;
import org.yes.cart.domain.vo.VoManager;
import org.yes.cart.service.domain.AttributeService;
import org.yes.cart.service.vo.VoDashboardWidgetPlugin;

/**
 * User: denispavlov
 * Date: 24/05/2018
 * Time: 08:01
 */
public abstract class AbstractVoDashboardWidgetPluginImpl implements VoDashboardWidgetPlugin {

    private final String name;
    private final String config;
    private final AttributeService attributeService;

    protected AbstractVoDashboardWidgetPluginImpl(final AttributeService attributeService,
                                                  final String widgetName) {
        this.attributeService = attributeService;
        this.name = widgetName;
        this.config = AttributeGroupNames.WIDGET.concat("_").concat(widgetName);
    }

    /**
     * Get attribute holding configurations for the widget
     *
     * @return attribute or null if not found
     */
    protected Attribute getWidgetConfig() {

        return this.attributeService.getByAttributeCode(this.config);

    }

    /** {@inheritDoc} */
    @Override
    public VoDashboardWidgetInfo getWidgetInfo(final VoManager manager, final String lang) {

        final VoDashboardWidgetInfo widget = new VoDashboardWidgetInfo();
        widget.setWidgetId(this.name);
        widget.setLanguage(lang);

        processWidgetInfo(manager, widget, getWidgetConfig());

        return widget;

    }

    /**
     * Hook for processing widget info.
     *
     * @param manager manager
     * @param widgetInfo basic object
     * @param config attribute (or null)
     */
    protected void processWidgetInfo(final VoManager manager,
                                     final VoDashboardWidgetInfo widgetInfo,
                                     final Attribute config) {

        if (config != null) {
            final String name = new FailoverStringI18NModel(config.getDisplayName(), config.getName())
                    .getValue(widgetInfo.getLanguage());
            widgetInfo.setWidgetDescription(name);
        } else {
            widgetInfo.setWidgetDescription(widgetInfo.getWidgetId());
        }
    }

    /** {@inheritDoc} */
    @Override
    public VoDashboardWidget getWidget(final VoManager manager, final String lang) {

        final VoDashboardWidget widget = new VoDashboardWidget();
        widget.setWidgetId(this.name);
        widget.setLanguage(lang);

        final Attribute config = getWidgetConfig();
        processWidgetInfo(manager, widget, config);
        processWidgetData(manager, widget, config);

        return widget;

    }

    /**
     * Hook for processing widget info.
     *
     * @param manager manager
     * @param widget widget object
     * @param config attribute (or null)
     */
    protected abstract void processWidgetData(final VoManager manager,
                                              final VoDashboardWidget widget,
                                              final Attribute config);


    /** {@inheritDoc} */
    @Override
    public String getName() {
        return name;
    }
}
