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

/**
 * Class represent report parameter.
 * 
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/2/12
 * Time: 1:46 PM 
 * */
public class ReportParameter {
    
    private String parameterId;

    private String businesstype;

    private boolean mandatory;

    public ReportParameter() {
    }

    /**
     * Get parameter name.
     * @return parameter name.
     */
    public String getParameterId() {
        return parameterId;
    }

    /**
     * Set parameter name.
     * @param parameterId parameter name.
     */
    public void setParameterId(final String parameterId) {
        this.parameterId = parameterId;
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
     * Get business type of parameter. See etype for more details.
     * @return business type of parameter.
     */
    public String getBusinesstype() {
        return businesstype;

    }

    /**
     * Set business type of parameter.
     * @param businesstype business type of parameter.
     */
    public void setBusinesstype(final String businesstype) {
        this.businesstype = businesstype;
    }
}
