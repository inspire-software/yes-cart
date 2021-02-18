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

package org.yes.cart.domain.vo;

import com.inspiresoftware.lib.dto.geda.annotations.Dto;
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;
import org.yes.cart.domain.misc.MutablePair;

import java.util.List;

/**
 * User: inspiresoftware
 * */
@Dto
public class VoReportParameter {

    @DtoField(readOnly = true)
    private String parameterId;
    @DtoField(readOnly = true)
    private String businesstype;
    @DtoField(readOnly = true)
    private boolean mandatory;
    @DtoField(readOnly = true)
    private String editorType;
    @DtoField(readOnly = true)
    private String editorProperty;
    @DtoField(readOnly = true)
    private String displayProperty;

    private List<MutablePair<String, String>> displayNames;

    public String getParameterId() {
        return parameterId;
    }

    public void setParameterId(final String parameterId) {
        this.parameterId = parameterId;
    }

    public String getBusinesstype() {
        return businesstype;
    }

    public void setBusinesstype(final String businesstype) {
        this.businesstype = businesstype;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(final boolean mandatory) {
        this.mandatory = mandatory;
    }

    public String getEditorType() {
        return editorType;
    }

    public void setEditorType(final String editorType) {
        this.editorType = editorType;
    }

    public String getEditorProperty() {
        return editorProperty;
    }

    public void setEditorProperty(final String editorProperty) {
        this.editorProperty = editorProperty;
    }

    public String getDisplayProperty() {
        return displayProperty;
    }

    public void setDisplayProperty(final String displayProperty) {
        this.displayProperty = displayProperty;
    }

    public List<MutablePair<String, String>> getDisplayNames() {
        return displayNames;
    }

    public void setDisplayNames(final List<MutablePair<String, String>> displayNames) {
        this.displayNames = displayNames;
    }
}
