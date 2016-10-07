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

import org.yes.cart.domain.misc.MutablePair;

import java.util.ArrayList;
import java.util.List;

/**
 * User: denispavlov
 * Date: 03/10/2016
 * Time: 11:21
 */
public class VoReportRequestParameter {

    private String parameterId;
    private List<MutablePair<String, String>> options;
    private String value;
    private boolean mandatory;

    public String getParameterId() {
        return parameterId;
    }

    public void setParameterId(final String parameterId) {
        this.parameterId = parameterId;
    }

    public List<MutablePair<String, String>> getOptions() {
        return options;
    }

    public void setOptions(final List<MutablePair<String, String>> options) {
        this.options = options;
    }

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(final boolean mandatory) {
        this.mandatory = mandatory;
    }
}
