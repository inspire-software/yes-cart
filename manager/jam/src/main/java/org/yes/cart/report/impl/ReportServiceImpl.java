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

package org.yes.cart.report.impl;

import org.apache.commons.collections.CollectionUtils;
import org.xml.sax.SAXException;
import org.yes.cart.domain.misc.MutablePair;
import org.yes.cart.domain.vo.VoReportDescriptor;
import org.yes.cart.domain.vo.VoReportRequest;
import org.yes.cart.domain.vo.VoReportRequestParameter;
import org.yes.cart.remote.service.FileManager;
import org.yes.cart.report.*;
import org.yes.cart.service.vo.VoAssemblySupport;
import org.yes.cart.utils.DateUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

/**
 * User: denispavlov
 * Date: 30/09/2016
 * Time: 18:04
 */
public class ReportServiceImpl implements ReportService {

    private final Map<String, ReportDescriptor> reportDescriptors;
    private final Map<String, ReportWorker> reportWorkers;
    private final List<ReportGenerator> reportGenerators;
    private final FileManager fileManager;
    private final VoAssemblySupport voAssemblySupport;

    /**
     * Construct report service.
     *  @param reportDescriptors list of configured reports.
     * @param reportWorkers      report workers
     * @param reportGenerators   report generator
     * @param fileManager        file manager
     * @param voAssemblySupport  VO support
     */
    public ReportServiceImpl(final Map<String, ReportDescriptor> reportDescriptors,
                             final Map<String, ReportWorker> reportWorkers,
                             final List<ReportGenerator> reportGenerators,
                             final FileManager fileManager,
                             final VoAssemblySupport voAssemblySupport) {

        this.reportDescriptors = reportDescriptors;
        this.reportWorkers = reportWorkers;
        this.reportGenerators = reportGenerators;

        this.fileManager = fileManager;
        this.voAssemblySupport = voAssemblySupport;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<VoReportDescriptor> getReportDescriptors(final String language) {

        final List<ReportDescriptor> raw = new ArrayList<>(reportDescriptors.values());

        raw.sort((a, b) -> {
            final String aName = a.getDisplayNames().get(language);
            final String bName = b.getDisplayNames().get(language);
            final int compare = aName.compareToIgnoreCase(bName);
            if (compare == 0) {
                return a.getReportId().compareTo(b.getReportId());
            }
            return compare;
        });

        return voAssemblySupport.assembleVos(
                VoReportDescriptor.class, ReportDescriptor.class, raw);

    }


    /**
     * {@inheritDoc}
     */
    @Override
    public VoReportRequest getParameterValues(final VoReportRequest reportRequest) {

        if (reportRequest != null && reportWorkers.containsKey(reportRequest.getReportId())) {

            final ReportDescriptor descriptor = getReportDescriptorById(reportRequest.getReportId());
            final ReportWorker reportWorker = reportWorkers.get(reportRequest.getReportId());
            final Map<String, Object> values = getReportRequestValueMap(reportRequest);

            final VoReportRequest updated = voAssemblySupport.assembleVo(
                    VoReportRequest.class, ReportDescriptor.class, new VoReportRequest(), descriptor);
            updated.setLang(reportRequest.getLang());

            for (final VoReportRequestParameter parameter : updated.getParameters()) {

                if ("DomainObject".equals(parameter.getBusinesstype())) {

                    final List<ReportPair> option = reportWorker.getParameterValues(
                            reportRequest.getLang(),
                            parameter.getParameterId(),
                            values
                    );

                    final List<MutablePair<String, String>> selection = new ArrayList<>();
                    for (final ReportPair choice : option) {
                        selection.add(new MutablePair<>(choice.getValue(), choice.getLabel()));
                    }

                    parameter.setOptions(selection);

                }

            }

            return updated;

        }
        return reportRequest;

    }


    ReportDescriptor getReportDescriptorById(final String reportId) {

        return reportDescriptors.get(reportId);

    }


    @Override
    public String generateReport(final VoReportRequest reportRequest) throws Exception {

        final String timestamp = DateUtils.impexFileTimestamp();
        final File target = new File(this.fileManager.home() + File.separator + "reports" + File.separator + reportRequest.getReportId() + "_" + timestamp + determineExtension(reportRequest));

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

    private String determineExtension(final VoReportRequest reportRequest) {

        final ReportDescriptor descriptor = getReportDescriptorById(reportRequest.getReportId());
        if (descriptor != null) {
            return descriptor.getReportFileExtension();
        }
        return ".out";
        
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

        final ReportDescriptor descriptor = getReportDescriptorById(reportId);

        if (CollectionUtils.isNotEmpty(rez)) {

            for (final ReportGenerator generator : this.reportGenerators) {
                if (generator.supports(descriptor)) {
                    generator.generateReport(descriptor, currentSelection, rez, lang, reportStream);
                    return true;
                }
            }

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
        final Map<String, Object> params = new HashMap<>();
        if (currentSelection != null) {
            params.putAll(currentSelection);
        }
        return params;

    }


}
