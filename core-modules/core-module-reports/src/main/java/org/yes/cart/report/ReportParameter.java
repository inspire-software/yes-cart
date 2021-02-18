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

package org.yes.cart.report;

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
    private String editorType;
    private String editorProperty;
    private String displayProperty;
    private boolean mandatory;

    public ReportParameter() {
    }

    /**
     * Get parameter name.
     *
     * @return parameter name.
     */
    public String getParameterId() {
        return parameterId;
    }

    /**
     * Set parameter name.
     *
     * @param parameterId parameter name.
     */
    public void setParameterId(final String parameterId) {
        this.parameterId = parameterId;
    }

    /**
     * Is this parameter mandatory or not.
     *
     * @return mandatory flag.
     */
    public boolean isMandatory() {
        return mandatory;
    }

    /**
     * Set mandatory flag.
     *
     * @param mandatory   mandatory flag.
     */
    public void setMandatory(final boolean mandatory) {
        this.mandatory = mandatory;
    }

    /**
     * Get business type of parameter. See etype for more details.
     *
     * @return business type of parameter.
     */
    public String getBusinesstype() {
        return businesstype;

    }

    /**
     * Set business type of parameter.
     *
     * @param businesstype business type of parameter.
     */
    public void setBusinesstype(final String businesstype) {
        this.businesstype = businesstype;
    }


    /**
     * UI editor type.
     *
     * @return editor type
     */
    public String getEditorType() {
        return editorType;
    }

    /**
     * UI editor type.
     *
     * @param editorType editor type
     */
    public void setEditorType(final String editorType) {
        this.editorType = editorType;
    }

    /**
     * Selection value property.
     *
     * @return property to extract from the selection of the editor
     */
    public String getEditorProperty() {
        return editorProperty;
    }

    /**
     * Selection value property.
     *
     * @param editorProperty property to use as value
     */
    public void setEditorProperty(final String editorProperty) {
        this.editorProperty = editorProperty;
    }

    /**
     * Display property on the ui. E.g. editorProperty could be shopId and displayProperty could be name or code
     *
     * @return display property
     */
    public String getDisplayProperty() {
        return displayProperty;
    }

    /**
     * Display property
     *
     * @param displayProperty display property
     */
    public void setDisplayProperty(final String displayProperty) {
        this.displayProperty = displayProperty;
    }

}
