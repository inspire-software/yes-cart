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

package org.yes.cart.service.vo;

import org.yes.cart.domain.vo.VoDashboardWidget;
import org.yes.cart.domain.vo.VoDashboardWidgetInfo;
import org.yes.cart.domain.vo.VoManager;

/**
 * User: denispavlov
 * Date: 23/09/2016
 * Time: 19:35
 */
public interface VoDashboardWidgetPlugin {

    /**
     * @param manager manager
     * @return true if this widget is applicable
     */
    boolean applicable(VoManager manager);

    /**
     * Get Widget information.
     *
     * @param manager manager
     * @param lang language
     *
     * @return generate widget data for manager
     */
    VoDashboardWidgetInfo getWidgetInfo(VoManager manager, String lang);

    /**
     * Get widget with data.
     *
     * @param manager manager
     * @param lang language
     *
     * @return generate widget data for manager
     */
    VoDashboardWidget getWidget(VoManager manager, String lang);

    /**
     * @return widget name
     */
    String getName();

}
