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

package org.yes.cart.bulkimport.service.support;

import org.yes.cart.bulkimport.model.ImportDescriptor;
import org.yes.cart.bulkimport.model.ImportTuple;
import org.yes.cart.bulkimport.model.ValueAdapter;

/**
 * Strategy to find the parameter values for given queryTemplate
 * and produce LookUpQuery object for ORM framework
 * User: denispavlov
 * Date: 12-08-08
 * Time: 8:43 AM
 */
public interface LookUpQueryParameterStrategy {

    /**
     * @param descriptor current import descriptor
     * @param masterObject master object for this tuple (or null)
     * @param tuple current import tuple
     * @param adapter value adapter
     * @param queryTemplate query template
     *
     * @return query object
     */
    LookUpQuery getQuery(final ImportDescriptor descriptor,
                         final Object masterObject,
                         final ImportTuple tuple,
                         final ValueAdapter adapter,
                         final String queryTemplate);

}
