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

package org.yes.cart.report;

import org.yes.cart.domain.vo.VoReportDescriptor;
import org.yes.cart.domain.vo.VoReportRequest;

import java.util.List;

/**
 * User: denispavlov
 * Date: 02/10/2016
 * Time: 12:35
 */
public interface ReportService {

    /**
     * Get the list of report descriptors.
     *
     * @return configured reports
     */
    List<VoReportDescriptor> getReportDescriptors();

    /**
     * Get parameter option values for param in lang.
     *
     * @param reportRequest request
     *
     * @return options
     */
    VoReportRequest getParameterValues(VoReportRequest reportRequest);


    /**
     * Generate report.
     *
     * @param reportRequest request
     *
     * @return generated file path
     *
     * @throws Exception in case of errors
     */
    String generateReport(VoReportRequest reportRequest) throws Exception;


}
