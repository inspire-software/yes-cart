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

package org.yes.cart.bulkcommon.service.support.impl;

import org.yes.cart.bulkcommon.model.ImpExDescriptor;
import org.yes.cart.bulkcommon.model.ImpExTuple;
import org.yes.cart.bulkcommon.model.ValueAdapter;
import org.yes.cart.bulkcommon.service.support.LookUpQueryParameterStrategyValueProvider;
import org.yes.cart.domain.entity.Identifiable;

/**
 * User: denispavlov
 * Date: 11/06/2015
 * Time: 12:22
 */
public class MasterObjectIdLookUpQueryParameterStrategyValueProviderImpl implements LookUpQueryParameterStrategyValueProvider {

    /**
     * {@inheritDoc}
     */
    public Object getPlaceholderValue(final String placeholder,
                                      final ImpExDescriptor descriptor,
                                      final Object masterObject,
                                      final ImpExTuple tuple,
                                      final ValueAdapter adapter,
                                      final String queryTemplate) {
        if (masterObject instanceof Identifiable) {
            return ((Identifiable) masterObject).getId();
        } else {
            throw new IllegalArgumentException("Master object cannot be resolved for look up query: " + queryTemplate + " for a field in tuple: " + tuple);
        }
    }
}
