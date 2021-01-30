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
import org.yes.cart.domain.entity.Job;
import org.yes.cart.service.domain.JobService;

import java.util.List;

/**
 * User: inspiresoftware
 * Date: 20/01/2021
 * Time: 10:22
 */
public class JobServiceCachedImpl implements JobService {

    private final JobService jobService;

    public JobServiceCachedImpl(final JobService jobService) {
        this.jobService = jobService;
    }

    /** {@inheritDoc} */
    @Override
    @Cacheable("jobService-getById")
    public Job getById(final long jobId) {
        return jobService.getById(jobId);
    }

    /** {@inheritDoc} */
    @Override
    public List<Job> findAll() {
        return jobService.findAll();
    }

    /** {@inheritDoc} */
    @Override
    public void findAllIterator(final ResultsIteratorCallback<Job> callback) {
        jobService.findAllIterator(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void findByCriteriaIterator(final String eCriteria, final Object[] parameters, final ResultsIteratorCallback<Job> callback) {
        jobService.findByCriteriaIterator(eCriteria, parameters, callback);
    }

    /** {@inheritDoc} */
    @Override
    public Job findById(final long pk) {
        return jobService.findById(pk);
    }

    /** {@inheritDoc} */
    @Override
    @CacheEvict(value = {
            "jobService-getById"
    }, allEntries = false, key = "#instance.jobId")
    public Job create(final Job instance) {
        return jobService.create(instance);
    }

    /** {@inheritDoc} */
    @Override
    @CacheEvict(value = {
            "jobService-getById"
    }, allEntries = false, key = "#instance.jobId")
    public Job update(final Job instance) {
        return jobService.update(instance);
    }

    /** {@inheritDoc} */
    @Override
    @CacheEvict(value = {
            "jobService-getById"
    }, allEntries = false, key = "#instance.jobId")
    public void delete(final Job instance) {
        jobService.delete(instance);
    }

    /** {@inheritDoc} */
    @Override
    public List<Job> findByCriteria(final String eCriteria, final Object... parameters) {
        return jobService.findByCriteria(eCriteria, parameters);
    }

    /** {@inheritDoc} */
    @Override
    public int findCountByCriteria(final String eCriteria, final Object... parameters) {
        return jobService.findCountByCriteria(eCriteria, parameters);
    }

    /** {@inheritDoc} */
    @Override
    public Job findSingleByCriteria(final String eCriteria, final Object... parameters) {
        return jobService.findSingleByCriteria(eCriteria, parameters);
    }

    /** {@inheritDoc} */
    @Override
    public GenericDAO<Job, Long> getGenericDao() {
        return jobService.getGenericDao();
    }
}
