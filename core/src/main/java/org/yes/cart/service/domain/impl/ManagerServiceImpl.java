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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.domain.ManagerService;
import org.yes.cart.utils.HQLUtils;

import java.util.*;

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


    private Pair<String, Object[]> findManagerQuery(final boolean count,
                                                    final String sort,
                                                    final boolean sortDescending,
                                                    final Map<String, List> filter) {

        final Map<String, List> currentFilter = filter != null ? new HashMap<>(filter) : null;

        final StringBuilder hqlCriteria = new StringBuilder();
        final List<Object> params = new ArrayList<>();

        if (count) {
            hqlCriteria.append("select count(distinct m.managerId) from ManagerEntity m left join m.shops mse ");
        } else {
            hqlCriteria.append("select distinct m from ManagerEntity m left join fetch m.shops mse ");
        }

        final List shops = currentFilter != null ? currentFilter.remove("shopIds") : null;
        if (CollectionUtils.isNotEmpty(shops)) {
            hqlCriteria.append(" where (mse.shop.shopId in (?1) or mse.shop.master.shopId in (?1)) ");
            params.add(shops);
        }

        HQLUtils.appendFilterCriteria(hqlCriteria, params, "m", currentFilter);

        if (StringUtils.isNotBlank(sort)) {

            hqlCriteria.append(" order by m." + sort + " " + (sortDescending ? "desc" : "asc"));

        }

        return new Pair<>(
                hqlCriteria.toString(),
                params.toArray(new Object[params.size()])
        );

    }



    /** {@inheritDoc } */
    @Override
    public List<Manager> findManagers(final int start, final int offset, final String sort, final boolean sortDescending, final Map<String, List> filter) {

        final Pair<String, Object[]> query = findManagerQuery(false, sort, sortDescending, filter);

        return getGenericDao().findRangeByQuery(
                query.getFirst(),
                start, offset,
                query.getSecond()
        );
    }

    /** {@inheritDoc } */
    @Override
    public int findManagerCount(final Map<String, List> filter) {

        final Pair<String, Object[]> query = findManagerQuery(true, null, false, filter);

        return getGenericDao().findCountByQuery(
                query.getFirst(),
                query.getSecond()
        );
    }
}
