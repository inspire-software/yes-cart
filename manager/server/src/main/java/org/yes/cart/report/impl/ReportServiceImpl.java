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

package org.yes.cart.report.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.springframework.util.Assert;
import org.xml.sax.SAXException;
import org.yes.cart.report.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//JAXP
//FOP


/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/2/12
 * Time: 2:46 PM
 */
public class ReportServiceImpl implements ReportService {

    private final List<ReportDescriptor> reportDescriptors;
    private final Map<String, ReportWorker> reportWorkers;
    private final ReportGenerator reportGenerator;

    /**
     * Construct report service.
     *
     * @param reportDescriptors list of configured reports.
     * @param reportWorkers     report workers
     * @param reportGenerator   report generator
     */
    public ReportServiceImpl(final List<ReportDescriptor> reportDescriptors,
                             final Map<String, ReportWorker> reportWorkers,
                             final ReportGenerator reportGenerator) {

        this.reportDescriptors = reportDescriptors;
        this.reportWorkers = reportWorkers;
        this.reportGenerator = reportGenerator;

    }


    /**
     * {@inheritDoc}
     */
    public List<ReportPair> getParameterValues(final String lang, final String reportId, final String param, final Map<String, Object> currentSelection) {

        if (reportWorkers.containsKey(reportId)) {
            return reportWorkers.get(reportId).getParameterValues(lang, param, currentSelection);
        }
        return Collections.emptyList();

    }

    /**
     * Get the list of report descriptors.
     *
     * @return list of reports which visible on UI.
     */
    @SuppressWarnings("unchecked")
    public List<ReportDescriptor> getReportDescriptors() {

        return (List<ReportDescriptor>) CollectionUtils.select(
                reportDescriptors,
                new Predicate() {
                    public boolean evaluate(Object object) {
                        return ((ReportDescriptor) object).isVisible();
                    }
                }

        );
    }

    ReportDescriptor getReportDescriptorbyId(final String reportId) {

        Assert.notNull(reportId, "ReportId must be not null");

        return (ReportDescriptor) CollectionUtils.find(reportDescriptors, new Predicate() {
            public boolean evaluate(final Object o) {
                return reportId.equalsIgnoreCase(((ReportDescriptor) o).getReportId());
            }
        });

    }


    /**
     * {@inheritDoc}
     */
    public byte[] downloadReport(String lang, String reportId, Map<String, Object> params) throws Exception {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (createReport(lang, reportId, baos, params)) {
            return baos.toByteArray();
        } else {
            throw new Exception("Unable to create report");
        }
    }


    private boolean createReport(final String lang,
                                 final String reportId,
                                 final OutputStream reportStream,
                                 final Map<String, Object> currentSelection) throws Exception {

        final List<Object> rez = getQueryResult(lang, reportId, currentSelection);
        final Map<String, Object> enhancedSelection = getEnhancedParameterValues(reportId, rez, currentSelection);
        return createReport(lang, reportId, reportStream, rez, enhancedSelection);

    }

    private boolean createReport(final String lang,
                                 final String reportId,
                                 final OutputStream reportStream,
                                 final List<Object> rez,
                                 final Map<String, Object> currentSelection) throws SAXException, IOException {

        final ReportDescriptor descriptor = getReportDescriptorbyId(reportId);

        if (CollectionUtils.isNotEmpty(rez)) {

            this.reportGenerator.generateReport(descriptor, currentSelection, rez, lang, reportStream);
            return true;

        }

        return false;
    }

    /**
     * Get query result as object list.
     *
     * @param lang language
     * @param reportId reportId
     * @param currentSelection parameters.
     *
     * @return list of objects.
     */
    List<Object> getQueryResult(final String lang, final String reportId, final Map<String, Object> currentSelection) {

        if (reportWorkers.containsKey(reportId)) {
            return reportWorkers.get(reportId).getResult(lang, currentSelection);
        }
        return Collections.emptyList();

    }

    /**
     * Enhance parameters by adding inferred values.
     *
     * @param reportId reportId
     * @param result restls
     * @param currentSelection parameters
     *
     * @return enhanced paramteres
     */
    Map<String, Object> getEnhancedParameterValues(final String reportId, final List<Object> result, final Map<String, Object> currentSelection) {

        if (reportWorkers.containsKey(reportId)) {
            return reportWorkers.get(reportId).getEnhancedParameterValues(result, currentSelection);
        }
        final Map<String, Object> params = new HashMap<String, Object>();
        if (currentSelection != null) {
            params.putAll(currentSelection);
        }
        return params;

    }

}
