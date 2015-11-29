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

package org.yes.cart.bulkcommon.service.support;

import org.yes.cart.bulkcommon.model.ImpExDescriptor;
import org.yes.cart.bulkcommon.model.ImpExTuple;
import org.yes.cart.bulkcommon.model.ValueAdapter;

/**
 * User: denispavlov
 * Date: 11/06/2015
 * Time: 12:17
 */
public interface LookUpQueryParameterStrategyValueProvider {


    /**
     * @param placeholder placeholder for value (e.g. {shopCode}, {masterObject})
     * @param descriptor current import descriptor
     * @param masterObject master object for this tuple (or null)
     * @param tuple current import tuple
     * @param adapter value adapter
     * @param queryTemplate query template
     *
     * @return query object
     */
    Object getPlaceholderValue(final String placeholder,
                               final ImpExDescriptor descriptor,
                               final Object masterObject,
                               final ImpExTuple tuple,
                               final ValueAdapter adapter,
                               final String queryTemplate);


}
