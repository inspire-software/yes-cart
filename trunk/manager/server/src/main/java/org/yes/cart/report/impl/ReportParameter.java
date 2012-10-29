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

    private String allowedValuesQuery;

    /**
     * Construct report parmeter.
     * @param name name
     * @param langLabel representation of name in different languages
     * @param businesstype type
     * @param mandatory is this parameret must be filled from UI
     * @param allowedValuesQuery optional hsql query to retreive
     */
    public ReportParameter(final String name, final List<ReportPair> langLabel, final String businesstype, final boolean mandatory, final String allowedValuesQuery) {
        this.name = name;
        this.langLabel = langLabel;
        this.businesstype = businesstype;
        this.mandatory = mandatory;
        this.allowedValuesQuery = allowedValuesQuery;
    }


    /**
     * Construct report parmeter.
     * @param name name
     * @param langLabel representation of name in different languages
     * @param businesstype type
     * @param mandatory is this parameret must be filled from UI
     */
    public ReportParameter(final String name, final List<ReportPair>  langLabel, final String businesstype, final boolean mandatory) {
        this.name = name;
        this.langLabel = langLabel;
        this.businesstype = businesstype;
        this.mandatory = mandatory;
    }

    /**
     * Get allowed values query. Which will be respesented on UI.
     * This just draft solution and should be reviewed for more complex cases.
     * Something like ref to value provideres.
     *
     * @return   hsql query to get value-representation pairs
     */
    public String getAllowedValuesQuery() {
        return allowedValuesQuery;
    }

    /**
     * Set allowed value hsql query.
     * @param allowedValuesQuery    hsql query
     */
    public void setAllowedValuesQuery(final String allowedValuesQuery) {
        this.allowedValuesQuery = allowedValuesQuery;
    }

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
