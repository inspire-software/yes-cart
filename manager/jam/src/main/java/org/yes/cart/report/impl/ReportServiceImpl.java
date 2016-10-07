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
import org.xml.sax.SAXException;
import org.yes.cart.domain.misc.MutablePair;
import org.yes.cart.domain.vo.VoReportDescriptor;
import org.yes.cart.domain.vo.VoReportParameter;
import org.yes.cart.domain.vo.VoReportRequest;
import org.yes.cart.domain.vo.VoReportRequestParameter;
import org.yes.cart.remote.service.FileManager;
import org.yes.cart.report.*;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * User: denispavlov
 * Date: 30/09/2016
 * Time: 18:04
 */
public class ReportServiceImpl implements ReportService {

    private final List<ReportDescriptor> reportDescriptors;
    private final Map<String, ReportWorker> reportWorkers;
    private final ReportGenerator reportGenerator;
    private final FileManager fileManager;

    /**
     * Construct report service.
     *  @param reportDescriptors list of configured reports.
     * @param reportWorkers     report workers
     * @param reportGenerator   report generator
     * @param fileManager       file manager
     */
    public ReportServiceImpl(final List<ReportDescriptor> reportDescriptors,
                             final Map<String, ReportWorker> reportWorkers,
                             final ReportGenerator reportGenerator,
                             final FileManager fileManager) {

        this.reportDescriptors = reportDescriptors;
        this.reportWorkers = reportWorkers;
        this.reportGenerator = reportGenerator;

        this.fileManager = fileManager;
    }


    /**
     * {@inheritDoc}
     */
    public List<VoReportDescriptor> getReportDescriptors() {

        final List<VoReportDescriptor> reports = new ArrayList<>();

        for (final ReportDescriptor descriptor : reportDescriptors) {

            if (descriptor.isVisible()) {
                final VoReportDescriptor report = new VoReportDescriptor();

                report.setReportId(descriptor.getReportId());

                for (final ReportParameter parameter : descriptor.getParameters()) {
                    final VoReportParameter reportParameter = new VoReportParameter();
                    reportParameter.setParameterId(parameter.getParameterId());
                    reportParameter.setBusinesstype(parameter.getBusinesstype());
                    reportParameter.setMandatory(parameter.isMandatory());
                    report.getParameters().add(reportParameter);
                }

                reports.add(report);

            }
        }

        return reports;
    }


    /**
     * {@inheritDoc}
     */
    public VoReportRequest getParameterValues(final VoReportRequest reportRequest) {

        if (reportRequest != null && reportWorkers.containsKey(reportRequest.getReportId())) {

            final ReportDescriptor descriptor = getReportDescriptorbyId(reportRequest.getReportId());
            final ReportWorker reportWorker = reportWorkers.get(reportRequest.getReportId());
            final Map<String, Object> values = getReportRequestValueMap(reportRequest);

            final List<VoReportRequestParameter> requestParams = new ArrayList<>();

            for (final ReportParameter parameter : descriptor.getParameters()) {

                final VoReportRequestParameter rp = new VoReportRequestParameter();
                rp.setParameterId(parameter.getParameterId());
                rp.setValue((String) values.get(parameter.getParameterId()));
                rp.setMandatory(parameter.isMandatory());

                if (parameter.getBusinesstype().startsWith("org.yes.cart")) {


                    final List<ReportPair> option = reportWorker.getParameterValues(
                            reportRequest.getLang(),
                            parameter.getParameterId(),
                            values
                    );

                    final List<MutablePair<String, String>> selection = new ArrayList<>();
                    for (final ReportPair choice : option) {
                        selection.add(new MutablePair<String, String>(choice.getValue(), choice.getLabel()));
                    }

                    rp.setOptions(selection);

                }
                requestParams.add(rp);

            }
            reportRequest.setParameters(requestParams);

        }
        return reportRequest;

    }


    ReportDescriptor getReportDescriptorbyId(final String reportId) {

        return (ReportDescriptor) CollectionUtils.find(reportDescriptors, new Predicate() {
            public boolean evaluate(final Object o) {
                return reportId.equalsIgnoreCase(((ReportDescriptor) o).getReportId());
            }
        });

    }


    @Override
    public String generateReport(final VoReportRequest reportRequest) throws Exception {

        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HHmmss");
        final String timestamp = format.format(new Date());
        final File target = new File(this.fileManager.home() + File.separator + "reports" + File.separator + reportRequest.getReportId() + "_" + timestamp + ".pdf");

        if (!target.getParentFile().exists()) {
            target.getParentFile().mkdirs();
        }

        final Map<String, Object> selection = getReportRequestValueMap(reportRequest);

        final FileOutputStream out = new FileOutputStream(target);
        if (createReport(reportRequest.getLang(), reportRequest.getReportId(), out, selection)) {
            return target.getName();
        }

        return null;
    }

    private Map<String, Object> getReportRequestValueMap(final VoReportRequest reportRequest) {
        final Map<String, Object> selection = new HashMap<>();
        for (final VoReportRequestParameter value : reportRequest.getParameters()) {
            selection.put(value.getParameterId(), value.getValue());
        }
        return selection;
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
     * @param result results
     * @param currentSelection parameters
     *
     * @return enhanced parameters
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
