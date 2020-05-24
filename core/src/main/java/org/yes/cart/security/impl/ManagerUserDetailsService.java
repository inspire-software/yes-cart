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

package org.yes.cart.security.impl;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.Manager;
import org.yes.cart.domain.entity.ManagerRole;
import org.yes.cart.service.domain.ManagerService;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * User: denispavlov
 * Date: 18/05/2018
 * Time: 18:29
 */
public class ManagerUserDetailsService implements UserDetailsService {

    private ManagerService managerService;
    private GenericDAO<ManagerRole, Long> managerRoleDao;

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {

        final Manager manager = this.managerService.findByEmail(username);

        if (manager == null) {
            throw new UsernameNotFoundException(username);
        }

        final Instant now = Instant.now();
        final boolean credentialsNonExpired = manager.getPasswordExpiry() == null || manager.getPasswordExpiry().isAfter(now);

        final List<GrantedAuthority> authorities = new ArrayList<>();
        final List<ManagerRole> assignedRoles = managerRoleDao.findByCriteria(" where e.email = ?1 ", manager.getEmail());
        for (ManagerRole managerRole : assignedRoles) {
            authorities.add(new SimpleGrantedAuthority(managerRole.getCode()));
        }

        return new User(manager.getEmail(), manager.getPassword(), manager.getEnabled(), true, credentialsNonExpired, true, authorities);

    }

    /**
     * Spring IoC.
     *
     * @param managerService manager service
     */
    public void setManagerService(final ManagerService managerService) {
        this.managerService = managerService;
    }

    /**
     * Spring IoC.
     *
     * @param managerRoleDao manager role DAO
     */
    public void setManagerRoleDao(final GenericDAO<ManagerRole, Long> managerRoleDao) {
        this.managerRoleDao = managerRoleDao;
    }
}
