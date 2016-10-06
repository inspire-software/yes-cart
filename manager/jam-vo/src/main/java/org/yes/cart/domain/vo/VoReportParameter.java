/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
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

/**
 * User: denispavlov
 * */
public class VoReportParameter {

    private String parameterId;

    private String businesstype;

    private boolean mandatory;

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
}
