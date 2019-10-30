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

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.dao.ResultsIteratorCallback;
import org.yes.cart.domain.entity.Manager;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.service.domain.ManagerService;

import java.util.List;

/**
 * User: denispavlov
 * Date: 26/05/2018
 * Time: 10:36
 */
public class ManagerServiceCachedImpl implements ManagerService {

    private final ManagerService managerService;

    public ManagerServiceCachedImpl(final ManagerService managerService) {
        this.managerService = managerService;
    }

    @Override
    @CacheEvict(value = { "managerService-managerByEmail" }, allEntries = true)
    public void resetPassword(final Manager manager) {
        managerService.resetPassword(manager);
    }

    @Override
    public Manager create(final Manager manager, final Shop shop, final String ... roles) {
        return managerService.create(manager, shop, roles);
    }

    @Override
    @Cacheable(value = "managerService-managerByEmail")
    public Manager getByEmail(final String email) {
        return managerService.getByEmail(email);
    }

    @Override
    public Manager findByEmail(final String email) {
        return managerService.findByEmail(email);
    }

    @Override
    public List<Manager> findByEmailPartial(final String email) {
        return managerService.findByEmailPartial(email);
    }

    @Override
    public List<Manager> findAll() {
        return managerService.findAll();
    }

    @Override
    public void findAllIterator(final ResultsIteratorCallback<Manager> callback) {
        managerService.findAllIterator(callback);
    }

    @Override
    public void findByCriteriaIterator(final String eCriteria, final Object[] parameters, final ResultsIteratorCallback<Manager> callback) {
        managerService.findByCriteriaIterator(eCriteria, parameters, callback);
    }

    @Override
    public Manager findById(final long pk) {
        return managerService.findById(pk);
    }

    @Override
    @CacheEvict(value = { "managerService-managerByEmail" }, allEntries = true)
    public Manager create(final Manager instance) {
        return managerService.create(instance);
    }

    @Override
    @CacheEvict(value = { "managerService-managerByEmail" }, allEntries = true)
    public Manager update(final Manager instance) {
        return managerService.update(instance);
    }

    @Override
    public void delete(final Manager instance) {
        managerService.delete(instance);
    }

    @Override
    public List<Manager> findByCriteria(final String eCriteria, final Object... parameters) {
        return managerService.findByCriteria(eCriteria, parameters);
    }

    @Override
    public int findCountByCriteria(final String eCriteria, final Object... parameters) {
        return managerService.findCountByCriteria(eCriteria, parameters);
    }

    @Override
    public Manager findSingleByCriteria(final String eCriteria, final Object... parameters) {
        return managerService.findSingleByCriteria(eCriteria, parameters);
    }

    @Override
    public GenericDAO<Manager, Long> getGenericDao() {
        return managerService.getGenericDao();
    }
}
