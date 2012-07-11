package org.yes.cart.report.impl;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Class represent report parameter.
 * 
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/2/12
 * Time: 1:46 PM 
 * */
public class ReportParameter {
    
    private String name;

    private List<ReportPair> langLabel = new ArrayList<ReportPair>();

    private String businesstype;

    private boolean mandatory;

    /**
     * Get parameter name.
     * @return parameter name.
     */
    public String getName() {
        return name;
    }

    /**
     * Set parameter name.
     * @param name parameter name.
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Get list of parameter name localization pair: lang - name
     * @return list of lang - name pair
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
     * Is this parameter mandatory or not.
     * @return mandatory flag.
     */
    public boolean isMandatory() {
        return mandatory;
    }

    /**
     * Set mandatory flag.
     * @param mandatory   mandatory flag.
     */
    public void setMandatory(final boolean mandatory) {
        this.mandatory = mandatory;
    }

    /**
     * Get business type of paramter. See etype for more details.
     * @return business type of paramter.
     */
    public String getBusinesstype() {
        return businesstype;

    }

    /**
     * Set business type of paramter.
     * @param businesstype business type of paramter.
     */
    public void setBusinesstype(final String businesstype) {
        this.businesstype = businesstype;
    }
}
