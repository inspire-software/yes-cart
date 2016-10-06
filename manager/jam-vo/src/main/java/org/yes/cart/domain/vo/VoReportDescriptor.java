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


import java.util.ArrayList;
import java.util.List;

/**
 * User: denispavlov
 */
public class VoReportDescriptor {

    private String reportId;

    private List<VoReportParameter> parameters =  new ArrayList<VoReportParameter>();

    public String getReportId() {
        return reportId;
    }

    public void setReportId(final String reportId) {
        this.reportId = reportId;
    }

    public List<VoReportParameter> getParameters() {
        return parameters;
    }

    public void setParameters(final List<VoReportParameter> parameters) {
        this.parameters = parameters;
    }
}
