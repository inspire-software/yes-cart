/*
 * Copyright 2009 - 2016 Denys Pavlov, Igor Azarnyi
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
package org.yes.cart.service.endpoint.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.yes.cart.domain.vo.VoReportDescriptor;
import org.yes.cart.domain.vo.VoReportRequest;
import org.yes.cart.report.ReportService;
import org.yes.cart.service.endpoint.ReportEndpointController;

import java.util.List;

/**
 * User: denispavlov
 * Date: 02/10/2016
 * Time: 12:43
 */
@Component
public class ReportEndpointControllerImpl implements ReportEndpointController {

    private final ReportService reportService;

    @Autowired
    public ReportEndpointControllerImpl(final ReportService reportService) {
        this.reportService = reportService;
    }

    @Override
    public @ResponseBody
    List<VoReportDescriptor> getReportDescriptors() {
        return reportService.getReportDescriptors();
    }

    @Override
    public @ResponseBody
    VoReportRequest getParameterValues(@RequestBody final VoReportRequest reportRequest) {
        return reportService.getParameterValues(reportRequest);
    }

    @Override
    public @ResponseBody
    String generateReport(@RequestBody final VoReportRequest reportRequest) throws Exception {
        return reportService.generateReport(reportRequest);
    }
}
