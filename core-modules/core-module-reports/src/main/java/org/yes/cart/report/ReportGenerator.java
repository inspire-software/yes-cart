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

import java.io.OutputStream;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 23/10/2015
 * Time: 15:15
 */
public interface ReportGenerator {

    /**
     * Determine whether report generator supports given report descriptor.
     *
     * @param reportDescriptor descriptor to check
     *
     * @return true if supports
     */
    boolean supports(ReportDescriptor reportDescriptor);

    /**
     * Generate report as bytes using given descriptor for data data object provided.
     *
     * @param descriptor report descriptor
     * @param parameters parameters
     * @param data data for the report
     * @param lang language of output
     * @param outputStream stream to write report to
     */
    void generateReport(ReportDescriptor descriptor,
                        Map<String, Object> parameters,
                        Object data,
                        String lang,
                        OutputStream outputStream);

}
