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

import org.yes.cart.report.impl.ReportPair;

import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 11/11/2014
 * Time: 22:59
 */
public interface ReportWorker {

    /**
     * Get parameter option values for param in lang.
     *
     * @param lang language
     * @param param parameter to get options for {@link org.yes.cart.report.impl.ReportParameter}
     * @param currentSelection optional param value map for complex selectors
     *
     * @return options
     */
    List<ReportPair> getParameterValues(String lang, String param, Map<String, Object> currentSelection);

    /**
     * Get results for given report criteria
     *
     * @param lang language
     * @param currentSelection  optional param value map for complex selectors
     *
     * @return list of result object
     */
    List<Object> getResult(String lang, Map<String, Object> currentSelection);

}
