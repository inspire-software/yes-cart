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

package org.yes.cart.service.domain.impl;

import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.JobDefinition;
import org.yes.cart.service.domain.JobDefinitionService;

/**
 * User: inspiresoftware
 * Date: 19/01/2021
 * Time: 16:12
 */
public class JobDefinitionServiceImpl extends BaseGenericServiceImpl<JobDefinition> implements JobDefinitionService {

    public JobDefinitionServiceImpl(final GenericDAO<JobDefinition, Long> genericDao) {
        super(genericDao);
    }

    /** {@inheritDoc} */
    @Override
    public JobDefinition getById(final long jobDefinitionId) {
        return findById(jobDefinitionId);
    }

}
