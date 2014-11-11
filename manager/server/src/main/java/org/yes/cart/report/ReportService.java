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

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/2/12
 * Time: 2:46 PM
 */
public interface ReportService {

    /**
     * Get list of value - name to represent on UI;
     *
     * @param hsql hsql to extract allowed values
     * @return list of pair
     */
    List<ReportPair> getParameterValues(String hsql);


    /**
     * Get the list of report descriptors.
     *
     * @return configured reports
     */
    List<ReportDescriptor> getReportDescriptors();


    /**
     * Run report by his id.
     *
     * @param reportId report descriptor.
     * @param fileName report filename
     * @param params   report parameter values to pass it into hsql query.   Consequence of parameter must correspond to parameters in repoport description.
     * @param lang     given lang to roduce report.
     * @return true in case if report was generated successfuly.
     * @throws Exception in case of errors
     */
    boolean createReport(String lang, String reportId, String fileName, Object... params) throws Exception;


    /**
     * Download report.
     *
     * @param reportId report descriptor.
     * @param params   report parameter values to pass it into hsql query.   Consequence of parameter must correspond to parameters in repoport description.
     * @param lang     given lang to roduce report.
     * @return true in case if report was generated successfuly.
     * @throws Exception in case of errors
     */
    byte[] downloadReport(String lang, String reportId, Object... params) throws Exception;


    /**
     * Download report.
     *
     * @param reportId   report descriptor.
     * @param objectList list of object for report
     * @param lang       given lang to produce report.
     * @return true in case if report was generated successfully.
     * @
     */
    byte[] produceReport(String lang, String reportId, List<Object> objectList) throws Exception;


}
