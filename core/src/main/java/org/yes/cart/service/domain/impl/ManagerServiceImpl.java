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

import org.apache.commons.lang.StringUtils;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.*;
import org.yes.cart.service.domain.ManagerService;

import java.util.Collections;
import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class ManagerServiceImpl extends BaseGenericServiceImpl<Manager> implements ManagerService {


    private final GenericDAO<Role, Long> roleDao;
    private final GenericDAO<ManagerRole, Long> managerRoleDao;

    /**
     * Create service to manage the administrative of web shop
     * @param genericDao dao to use
     */
    public ManagerServiceImpl(final GenericDAO<Manager, Long> genericDao,
                              final GenericDAO<Role, Long> roleDao,
                              final GenericDAO<ManagerRole, Long> managerRoleDao) {
        super(genericDao);
        this.roleDao = roleDao;
        this.managerRoleDao = managerRoleDao;
    }

    /** {@inheritDoc } */
    @Override
    public void resetPassword(final Manager manager) {
        getGenericDao().update(manager);        
    }

    /** {@inheritDoc } */
    @Override
    public Manager create(final Manager manager, final Shop shop, final String ... roles) {
        if (shop != null) {
            final ManagerShop managerShop = getGenericDao().getEntityFactory().getByIface(ManagerShop.class);
            managerShop.setManager(manager);
            managerShop.setShop(shop);
            manager.getShops().add(managerShop);
        }
        final Manager created = super.create(manager);
        if (roles != null) {
            for (final String role : roles) {
                final Role roleEntity = roleDao.findSingleByCriteria(" where e.code = ?1 ", role);
                if (roleEntity != null) {
                    final ManagerRole managerRole = managerRoleDao.getEntityFactory().getByIface(ManagerRole.class);
                    managerRole.setCode(role);
                    managerRole.setEmail(created.getEmail());
                    managerRoleDao.create(managerRole);
                }
            }
        }
        return created;
    }

    /** {@inheritDoc } */
    @Override
    public Manager getByEmail(final String email) {
        return findByEmail(email);

    }

    /** {@inheritDoc } */
    @Override
    public Manager findByEmail(final String email) {
        if (StringUtils.isBlank(email)) {
            return null;
        }
        return findSingleByCriteria(" where lower(e.email) = ?1 ", email.toLowerCase());

    }

    /** {@inheritDoc } */
    @Override
    public List<Manager> findByEmailPartial(final String email) {
        if (StringUtils.isBlank(email)) {
            return Collections.emptyList();
        }
        return findByCriteria(" where lower(e.email) like ?1 ", '%' + email.toLowerCase() + '%');

    }

}
