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

package org.yes.cart.report;

import org.apache.commons.lang.StringUtils;

/**
 * User: denispavlov
 * Date: 07/10/2019
 * Time: 20:06
 */
public class ReportDescriptorPDF extends ReportDescriptor {

    private String xslfoBase;


    /**
     * Get xsl fo file name for this reports.
     * @return xsl fo file name for this reports.
     */
    public String getXslfoBase() {
        return xslfoBase;
    }

    /**
     * Get xsl fo file name for this reports.
     * @param lang lang
     * @return xsl fo file name for this reports.
     */
    public String getLangXslfo(final String lang) {
        if (StringUtils.isNotBlank(lang)) {
            return xslfoBase + "_" + lang + ".xslfo";
        }
        return xslfoBase + ".xslfo";
    }

    /**
     * Set xsl fo file name for this reports.
     * @param xslfoBase xsl fo file name for this reports.
     */
    public void setXslfoBase(String xslfoBase) {
        this.xslfoBase = xslfoBase;
    }

    /** {@inheritDoc} */
    @Override
    public String getReportType() {
        return "pdf";
    }

    /** {@inheritDoc} */
    @Override
    public String getReportFileExtension() {
        return ".pdf";
    }

    /** {@inheritDoc} */
    @Override
    public String getReportFileMimeType() {
        return "application/pdf";
    }

    @Override
    public String toString() {
        return "ReportDescriptorPDF{" +
                "reportId='" + getReportId() + '\'' +
                ", visible=" + isVisible() +
                ", xslfoBase='" + xslfoBase + '\'' +
                '}';
    }
}
