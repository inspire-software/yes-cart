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

package org.yes.cart.service.domain.impl;

import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.DataDescriptor;
import org.yes.cart.service.domain.DataDescriptorService;

import java.util.Collection;
import java.util.List;

/**
 * User: denispavlov
 * Date: 01/06/2015
 * Time: 13:00
 */
public class DataDescriptorServiceImpl extends BaseGenericServiceImpl<DataDescriptor> implements DataDescriptorService {

    public DataDescriptorServiceImpl(final GenericDAO<DataDescriptor, Long> genericDao) {
        super(genericDao);
    }

    /**
     * {@inheritDoc}
     */
    public List<DataDescriptor> findByNames(final Collection<String> names) {
        return getGenericDao().findByNamedQuery("DATADESCRIPTOR.IN.NAMES", names);
    }

    /**
     * {@inheritDoc}
     */
    public DataDescriptor findByName(final String name) {
        final List<DataDescriptor> search = getGenericDao().findByNamedQuery("DATADESCRIPTOR.BY.NAME", name);
        if (search.isEmpty()) {
            return null;
        }
        return search.get(0);
    }
}
