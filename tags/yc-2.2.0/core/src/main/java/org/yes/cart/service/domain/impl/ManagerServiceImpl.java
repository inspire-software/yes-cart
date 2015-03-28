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

package org.yes.cart.service.domain.impl;

import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.Manager;
import org.yes.cart.domain.entity.ManagerShop;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.service.domain.ManagerService;

import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class ManagerServiceImpl extends BaseGenericServiceImpl<Manager> implements ManagerService {

    /**
     * Create service to manage the administrative of web shop
     * @param genericDao dao to use
     */
    public ManagerServiceImpl(final GenericDAO<Manager, Long> genericDao) {
        super(genericDao);
    }

    /** {@inheritDoc } */
    public void resetPassword(final Manager manager) {
        getGenericDao().update(manager);        
    }

    /** {@inheritDoc } */
    public Manager create(final Manager manager, final Shop shop) {
        if (shop != null) {
            final ManagerShop managerShop = getGenericDao().getEntityFactory().getByIface(ManagerShop.class);
            managerShop.setManager(manager);
            managerShop.setShop(shop);
            manager.getShops().add(managerShop);
        }
        return super.create(manager);
    }

    /** {@inheritDoc } */
    public List<Manager> findByEmail(final String email) {
        return findByCriteria(Restrictions.like("email", email, MatchMode.ANYWHERE));

    }

}
