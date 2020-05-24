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

import org.yes.cart.domain.entity.Attribute;
import org.yes.cart.domain.vo.VoDashboardWidget;
import org.yes.cart.domain.vo.VoManager;
import org.yes.cart.service.domain.AttributeService;
import org.yes.cart.service.vo.VoDashboardWidgetPlugin;

/**
 * User: denispavlov
 * Date: 25/05/2018
 * Time: 08:56
 */
public class VoDashboardWidgetPluginUiSettings extends AbstractVoDashboardWidgetPluginImpl implements VoDashboardWidgetPlugin {


    public VoDashboardWidgetPluginUiSettings(final AttributeService attributeService,
                                             final String widgetName) {
        super(attributeService, widgetName);
    }

    @Override
    protected void processWidgetData(final VoManager manager, final VoDashboardWidget widget, final Attribute config) {
        // nothing, all in cookies
    }

    @Override
    public boolean applicable(final VoManager manager) {
        return true;
    }
}
