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

    private String name;

    private List<Pair<String, String>> langLabel = new ArrayList<Pair<String, String>>();
    
    private String hsqlQuery;

    private List<ReportParameter> parameters =  new ArrayList<ReportParameter>();

    private String xslfo;

    /**
     * Get name.
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Set report name.
     * @param name name of report
     */
    public void setName(final String name) {
        this.name = name;
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
    public String getXslfo() {
        return xslfo;
    }

    /**
     * Set xsl fo file name for this reports.
     * @param xslfo xsl fo file name for this reports.
     */
    public void setXslfo(final String xslfo) {
        this.xslfo = xslfo;
    }

}
