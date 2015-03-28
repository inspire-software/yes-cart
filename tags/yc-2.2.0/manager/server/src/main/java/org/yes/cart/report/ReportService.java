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

package org.yes.cart.report;

import org.yes.cart.report.impl.ReportDescriptor;
import org.yes.cart.report.impl.ReportPair;

import java.util.List;
import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/2/12
 * Time: 2:46 PM
 */
public interface ReportService {

    /**
     * Get parameter option values for param in lang.
     *
     * @param lang language
     * @param reportId report id for {@link org.yes.cart.report.impl.ReportDescriptor}
     * @param param parameter to get options for {@link org.yes.cart.report.impl.ReportParameter}
     * @param currentSelection optional param value map for complex selectors
     *
     * @return options
     */
    List<ReportPair> getParameterValues(String lang, String reportId, String param, Map<String, Object> currentSelection);


    /**
     * Get the list of report descriptors.
     *
     * @return configured reports
     */
    List<ReportDescriptor> getReportDescriptors();

    /**
     * Download report.
     *
     * @param reportId report descriptor.
     * @param params   report parameter values.
     * @param lang     given lang to produce report.
     * @return true in case if report was generated successfully.
     * @throws Exception in case of errors
     */
    byte[] downloadReport(String lang, String reportId, Map<String, Object> params) throws Exception;


}
