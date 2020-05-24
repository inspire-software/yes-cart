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

package org.yes.cart.bulkcommon.service.support.csv;

import org.yes.cart.bulkcommon.csv.CsvImpExDescriptor;
import org.yes.cart.bulkcommon.csv.CsvImpExTuple;
import org.yes.cart.bulkcommon.csv.CsvValueAdapter;
import org.yes.cart.bulkcommon.service.support.query.LookUpQueryParameterStrategyValueProvider;

/**
 * User: denispavlov
 * Date: 11/06/2015
 * Time: 12:17
 */
public interface CsvLookUpQueryParameterStrategyValueProvider extends
        LookUpQueryParameterStrategyValueProvider<CsvImpExDescriptor, CsvImpExTuple, CsvValueAdapter> {

}
