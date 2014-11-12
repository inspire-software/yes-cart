/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
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

package org.yes.cart.report.impl;


import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 *
 *  Class describe reports
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/2/12
 * Time: 1:46 PM
 */
public class ReportDescriptor {

    private String reportId;
    private boolean visible = true;
    private String xslfoBase;

    private List<ReportParameter> parameters =  new ArrayList<ReportParameter>();

    /**
     * Is report visible on UI.
     * @return   true report visible on UI.
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Set report visible on UI.
     * @param visible flag report visible on UI.
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * Get reportId.
     * @return reportId
     */
    public String getReportId() {
        return reportId;
    }

    /**
     * Set report name.
     * @param reportId name of report
     */
    public void setReportId(final String reportId) {
        this.reportId = reportId;
    }

    /**
     * Get list of parameters.
     * @return list of parameters.
     */
    public List<ReportParameter> getParameters() {
        return parameters;
    }

    /**
     * Set list of parameters.
     * @param parameters parameters.
     */
    public void setParameters(List<ReportParameter> parameters) {
        this.parameters = parameters;
    }

    /**
     * Get xsl fo file name for this reports.
     * @return xsl fo file name for this reports.
     */
    public String getXslfoBase() {
        return xslfoBase;
    }

    /**
     * Get xsl fo file name for this reports.
     * @param lang lang
     * @return xsl fo file name for this reports.
     */
    public String getLangXslfo(final String lang) {
        if (StringUtils.isNotBlank(lang)) {
            return xslfoBase + "_" + lang + ".xslfo";
        }
        return xslfoBase + ".xslfo";
    }

    /**
     * Set xsl fo file name for this reports.
     * @param xslfoBase xsl fo file name for this reports.
     */
    public void setXslfoBase(String xslfoBase) {
        this.xslfoBase = xslfoBase;
    }


}
