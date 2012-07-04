package org.yes.cart.report.impl;

import org.yes.cart.domain.misc.Pair;

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

    private List<Pair<String, String>> langLabel = new ArrayList<Pair<String, String>>();
    
    private String hsqlQuery;

    private List<ReportParameter> parameters =  new ArrayList<ReportParameter>();

    private List<Pair<String, String>> langXslfo = new ArrayList<Pair<String, String>>();


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
    public List<Pair<String, String>> getLangLabel() {
        return langLabel;
    }

    /**
     * Set ist of lang - name pair.
     * @param langLabel ist of lang - name pair
     */
    public void setLangLabel(final List<Pair<String, String>> langLabel) {
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
    public List<Pair<String, String>> getLangXslfo() {
        return langXslfo;
    }

    /**
     * Get xsl fo file name for this reports.
     * @param lang lang
     * @return xsl fo file name for this reports.
     */
    public String getLangXslfo(final String lang) {
        for (Pair<String, String> langFo : langXslfo) {
            if (lang.equalsIgnoreCase(langFo.getFirst())) {
                return  langFo.getSecond();
            }
        }
        return langXslfo.get(0).getFirst();
    }

    /**
     * Set xsl fo file name for this reports.
     * @param langXslfo xsl fo file name for this reports.
     */
    public void setLangXslfo(List<Pair<String, String>> langXslfo) {
        this.langXslfo = langXslfo;
    }


}
