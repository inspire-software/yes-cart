/*
 * Copyright 2009 - 2016 Denys Pavlov, Igor Azarnyi
 *
 *    Licensed under the Apache License, Version 2.0 (the 'License');
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an 'AS IS' BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.yes.cart.domain.vo;

/**
 * User: denispavlov
 * Date: 23/09/2016
 * Time: 09:21
 */
public class VoDashboardWidgetInfo {

    private String widgetId;
    private String language;
    private String widgetDescription;

    public String getWidgetId() {
        return widgetId;
    }

    public void setWidgetId(final String widgetId) {
        this.widgetId = widgetId;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(final String language) {
        this.language = language;
    }

    public String getWidgetDescription() {
        return widgetDescription;
    }

    public void setWidgetDescription(final String widgetDescription) {
        this.widgetDescription = widgetDescription;
    }
}
