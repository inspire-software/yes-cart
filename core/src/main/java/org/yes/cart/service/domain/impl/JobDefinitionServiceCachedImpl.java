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

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.dao.ResultsIteratorCallback;
import org.yes.cart.domain.entity.JobDefinition;
import org.yes.cart.service.domain.JobDefinitionService;

import java.util.List;

/**
 * User: inspiresoftware
 * Date: 20/01/2021
 * Time: 10:28
 */
public class JobDefinitionServiceCachedImpl implements JobDefinitionService {

    private final JobDefinitionService jobDefinitionService;

    public JobDefinitionServiceCachedImpl(final JobDefinitionService jobDefinitionService) {
        this.jobDefinitionService = jobDefinitionService;
    }

    /** {@inheritDoc} */
    @Override
    @Cacheable("jobDefinitionService-getById")
    public JobDefinition getById(final long jobDefinitionId) {
        return jobDefinitionService.getById(jobDefinitionId);
    }

    /** {@inheritDoc} */
    @Override
    public List<JobDefinition> findAll() {
        return jobDefinitionService.findAll();
    }

    /** {@inheritDoc} */
    @Override
    public void findAllIterator(final ResultsIteratorCallback<JobDefinition> callback) {
        jobDefinitionService.findAllIterator(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void findByCriteriaIterator(final String eCriteria, final Object[] parameters, final ResultsIteratorCallback<JobDefinition> callback) {
        jobDefinitionService.findByCriteriaIterator(eCriteria, parameters, callback);
    }

    /** {@inheritDoc} */
    @Override
    public JobDefinition findById(final long pk) {
        return jobDefinitionService.findById(pk);
    }

    /** {@inheritDoc} */
    @Override
    @CacheEvict(value = {
            "jobService-getById"
    }, allEntries = false, key = "#instance.jobDefinitionId")
    public JobDefinition create(final JobDefinition instance) {
        return jobDefinitionService.create(instance);
    }

    /** {@inheritDoc} */
    @Override
    @CacheEvict(value = {
            "jobService-getById"
    }, allEntries = false, key = "#instance.jobDefinitionId")
    public JobDefinition update(final JobDefinition instance) {
        return jobDefinitionService.update(instance);
    }

    /** {@inheritDoc} */
    @Override
    @CacheEvict(value = {
            "jobService-getById"
    }, allEntries = false, key = "#instance.jobDefinitionId")
    public void delete(final JobDefinition instance) {
        jobDefinitionService.delete(instance);
    }

    /** {@inheritDoc} */
    @Override
    public List<JobDefinition> findByCriteria(final String eCriteria, final Object... parameters) {
        return jobDefinitionService.findByCriteria(eCriteria, parameters);
    }

    /** {@inheritDoc} */
    @Override
    public int findCountByCriteria(final String eCriteria, final Object... parameters) {
        return jobDefinitionService.findCountByCriteria(eCriteria, parameters);
    }

    /** {@inheritDoc} */
    @Override
    public JobDefinition findSingleByCriteria(final String eCriteria, final Object... parameters) {
        return jobDefinitionService.findSingleByCriteria(eCriteria, parameters);
    }

    /** {@inheritDoc} */
    @Override
    public GenericDAO<JobDefinition, Long> getGenericDao() {
        return jobDefinitionService.getGenericDao();
    }
}
