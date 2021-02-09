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

package org.yes.cart.domain.vo;

import com.inspiresoftware.lib.dto.geda.annotations.Dto;
import com.inspiresoftware.lib.dto.geda.annotations.DtoCollection;
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;
import org.yes.cart.domain.misc.MutablePair;
import org.yes.cart.domain.vo.matcher.NoopMatcher;
import org.yes.cart.report.ReportParameter;

import java.util.ArrayList;
import java.util.List;

/**
 * User: denispavlov
 * Date: 30/09/2016
 * Time: 17:55
 */
@Dto
public class VoReportRequest {

    @DtoField(readOnly = true)
    private String reportId;
    @DtoField(value = "displayNames", converter = "DisplayValues", readOnly = true)
    private List<MutablePair<String, String>> displayNames;

    @DtoCollection(
            value = "parameters",
            dtoBeanKey = "VoReportRequestParameter",
            entityGenericType = ReportParameter.class,
            entityCollectionClass = ArrayList.class,
            dtoCollectionClass = ArrayList.class,
            dtoToEntityMatcher = NoopMatcher.class,
            readOnly = true
    )
    private List<VoReportRequestParameter> parameters = new ArrayList<>();

    @DtoField(readOnly = true)
    private String reportType;
    @DtoField(readOnly = true)
    private String reportFileExtension;
    @DtoField(readOnly = true)
    private String reportFileMimeType;

    private String lang;


    public String getReportId() {
        return reportId;
    }

    public void setReportId(final String reportId) {
        this.reportId = reportId;
    }

    public List<MutablePair<String, String>> getDisplayNames() {
        return displayNames;
    }

    public void setDisplayNames(final List<MutablePair<String, String>> displayNames) {
        this.displayNames = displayNames;
    }

    public List<VoReportRequestParameter> getParameters() {
        return parameters;
    }

    public void setParameters(final List<VoReportRequestParameter> parameters) {
        this.parameters = parameters;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(final String reportType) {
        this.reportType = reportType;
    }

    public String getReportFileExtension() {
        return reportFileExtension;
    }

    public void setReportFileExtension(final String reportFileExtension) {
        this.reportFileExtension = reportFileExtension;
    }

    public String getReportFileMimeType() {
        return reportFileMimeType;
    }

    public void setReportFileMimeType(final String reportFileMimeType) {
        this.reportFileMimeType = reportFileMimeType;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(final String lang) {
        this.lang = lang;
    }

}
