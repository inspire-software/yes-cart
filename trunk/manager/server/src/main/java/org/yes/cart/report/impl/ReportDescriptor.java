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

    private List<ReportPair> langLabel = new ArrayList<ReportPair>();
    
    private String hsqlQuery;

    private List<ReportParameter> parameters =  new ArrayList<ReportParameter>();

    private List<ReportPair> langXslfo = new ArrayList<ReportPair>();


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
     * Gett ist of lang - name pair.
     */
    public List<ReportPair> getLangLabel() {
        return langLabel;
    }

    /**
     * Set ist of lang - name pair.
     * @param langLabel ist of lang - name pair
     */
    public void setLangLabel(final List<ReportPair> langLabel) {
        this.langLabel = langLabel;
    }

    /**
     * Get hsql query to get data.
     * @return hsql query.
     */
    public String getHsqlQuery() {
        return hsqlQuery;
    }

    /**
     * Set query to get data.
     * @param hsqlQuery query.
     */
    public void setHsqlQuery(final String hsqlQuery) {
        this.hsqlQuery = hsqlQuery;
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
    public List<ReportPair> getLangXslfo() {
        return langXslfo;
    }

    /**
     * Get xsl fo file name for this reports.
     * @param lang lang
     * @return xsl fo file name for this reports.
     */
    public String getLangXslfo(final String lang) {
        for (ReportPair langFo : langXslfo) {
            if (lang.equalsIgnoreCase(langFo.getLang())) {
                return  langFo.getValue();
            }
        }
        return langXslfo.get(0).getValue();
    }

    /**
     * Set xsl fo file name for this reports.
     * @param langXslfo xsl fo file name for this reports.
     */
    public void setLangXslfo(List<ReportPair> langXslfo) {
        this.langXslfo = langXslfo;
    }


}
