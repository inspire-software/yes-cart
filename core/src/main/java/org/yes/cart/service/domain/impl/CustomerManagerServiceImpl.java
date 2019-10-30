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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.*;
import org.yes.cart.service.customer.CustomerNameFormatter;
import org.yes.cart.service.domain.*;
import org.yes.cart.utils.log.Markers;

import java.time.Instant;
import java.util.*;

/**
 * User: denispavlov
 * Date: 26/10/2019
 * Time: 10:11
 */
public class CustomerManagerServiceImpl implements CustomerManagerService {

    private static final Logger LOG = LoggerFactory.getLogger(CustomerServiceImpl.class);

    private static final AttributeRankComparator ATTRIBUTE_RANK_COMPARATOR = new AttributeRankComparator();

    private static final String LOGIN_ROLE = "ROLE_SMCALLCENTERLOGINSF";

    private final HashHelper passwordHashHelper;

    private final GenericDAO<Manager, Long> managerDao;

    private final AttributeService attributeService;

    private final ShopService shopService;

    private final CustomerNameFormatter customerNameFormatter;

    /**
     * Constructor.
     *
     * @param managerDao            manager DAO
     * @param passwordHashHelper    password hash helper
     * @param attributeService      attribute service
     * @param shopService           shop service
     * @param customerNameFormatter customewr name formatter
     */
    public CustomerManagerServiceImpl(final GenericDAO<Manager, Long> managerDao,
                                      final HashHelper passwordHashHelper,
                                      final AttributeService attributeService,
                                      final ShopService shopService,
                                      final CustomerNameFormatter customerNameFormatter) {
        this.passwordHashHelper = passwordHashHelper;
        this.managerDao = managerDao;
        this.attributeService = attributeService;
        this.shopService = shopService;
        this.customerNameFormatter = customerNameFormatter;
    }

    @Override
    public boolean isCustomerManagerLoginEnabled(final Shop shop) {

        return isSfManagersLoginEnabled(shop);
        
    }

    private boolean isSfManagersLoginEnabled(final Shop shop) {

        final Boolean allowed = shop.isSfManagersLoginEnabled();
        if (allowed != null) {
            return allowed;
        }

        if (shop.getMaster() != null) {
            return isSfManagersLoginEnabled(shop.getMaster());
        }

        return false;

    }

    @Override
    public Customer getCustomerByEmail(final String email, final Shop shop) {

        if (!isSfManagersLoginEnabled(shop)) {
            return null;
        }

        final Manager manager = managerDao.findSingleByCriteria(" where lower(e.email) = ?1 and e.enabled = ?2", email.toLowerCase(), Boolean.TRUE);
        if (manager != null) {
            final int countsRole = managerDao.findCountByNamedQuery("MANAGER.ROLE.CHECK", email.toLowerCase(), LOGIN_ROLE);
            if (countsRole > 0) {
                final List<String> roles = (List) managerDao.findQueryObjectByNamedQuery("MANAGER.ROLE.CODES", email.toLowerCase());
                return new ManagerCustomerReadOnlyAdapter(manager, roles);
            }
        }
        return null;

    }

    @Override
    public boolean isManagerExists(final String email, final Shop shop) {

        if (!isSfManagersLoginEnabled(shop)) {
            return false;
        }

        if (StringUtils.isNotBlank(email)) {
            final int countsEmail = managerDao.findCountByNamedQuery("MANAGER.EMAIL.CHECK", email.toLowerCase(), shop.getShopId(), Boolean.TRUE);
            final boolean exists = countsEmail > 0;
            if (exists) {
                final int countsRole = managerDao.findCountByNamedQuery("MANAGER.ROLE.CHECK", email.toLowerCase(), LOGIN_ROLE);
                if (countsRole > 0) {
                    return true;
                } else {
                    LOG.warn(Markers.alert(), "Manager {} does not have enough roles to login to SF {} ... password will be marked as invalid", email, shop.getCode());
                }
            }
        }
        return false;
    }

    @Override
    public List<Shop> getCustomerShops(final Customer customer) {

        final List<Shop> shops = new ArrayList<>();
        if (customer != null) {
            Manager mgr = managerDao.findById(customer.getCustomerId());
            if (mgr != null && mgr.getEnabled()) {
                for (ManagerShop managerShop : mgr.getShops()) {
                    final Shop shop = shopService.getById(managerShop.getShop().getShopId());
                    if (shop != null && !shop.isDisabled()) {
                        shops.add(shop);
                    }
                }
            }
        }
        return shops;

    }

    @Override
    public boolean isPasswordValid(final String email, final Shop shop, final String password) {
        try {

            if (StringUtils.isBlank(password)) {
                return false;
            }

            final String hash = passwordHashHelper.getHash(password);

            final int counts = managerDao.findCountByNamedQuery("MANAGER.PASS.CHECK", email.toLowerCase(), shop.getShopId(), hash);
            return counts > 0;

        } catch (Exception exp) {
            LOG.error(exp.getMessage(), exp);
        }
        return false;
    }

    private static class ManagerCustomerReadOnlyAdapter implements Customer {

        private final Manager manager;
        private final List<CustomerShop> customerShops = new ArrayList<>();
        private final Set<String> roles = new TreeSet<>();

        private ManagerCustomerReadOnlyAdapter(final Manager manager,
                                               final List<String> roles) {
            this.manager = manager;
            if (CollectionUtils.isNotEmpty(manager.getShops())) {
                for (final ManagerShop managerShop : manager.getShops()) {
                    customerShops.add(new ManagerShopCustomerReadOnlyAdapter(this, managerShop));
                }
            }
            if (CollectionUtils.isNotEmpty(roles)) {
                this.roles.addAll(roles);
            }
        }

        @Override
        public long getCustomerId() {
            return manager.getManagerId();
        }

        @Override
        public void setCustomerId(final long customerId) {

        }

        @Override
        public String getPublicKey() {
            return null;
        }

        @Override
        public void setPublicKey(final String publicKey) {

        }

        @Override
        public String getCustomerType() {
            return AttributeNamesKeys.Cart.CUSTOMER_TYPE_MANAGER;
        }

        @Override
        public void setCustomerType(final String customerType) {

        }

        @Override
        public String getPricingPolicy() {
            return null;
        }

        @Override
        public void setPricingPolicy(final String pricingPolicy) {

        }

        @Override
        public boolean isGuest() {
            return false;
        }

        @Override
        public void setGuest(final boolean guest) {

        }

        @Override
        public String getGuestEmail() {
            return null;
        }

        @Override
        public void setGuestEmail(final String email) {

        }

        @Override
        public String getContactEmail() {
            return manager.getEmail();
        }

        @Override
        public Collection<CustomerOrder> getOrders() {
            return Collections.emptyList();
        }

        @Override
        public void setOrders(final Collection<CustomerOrder> orders) {

        }

        @Override
        public Collection<CustomerWishList> getWishList() {
            return Collections.emptyList();
        }

        @Override
        public void setWishList(final Collection<CustomerWishList> wishList) {

        }

        @Override
        public Collection<AttrValueCustomer> getAttributes() {
            return Collections.emptyList();
        }

        @Override
        public Collection<AttrValueCustomer> getAttributesByCode(final String attributeCode) {
            return null;
        }

        @Override
        public AttrValueCustomer getAttributeByCode(final String attributeCode) {
            return null;
        }

        @Override
        public List<Address> getAddresses(final String addressType) {
            return Collections.emptyList();
        }

        @Override
        public Address getDefaultAddress(final String addressType) {
            return null;
        }

        @Override
        public void setAttributes(final Collection<AttrValueCustomer> attribute) {

        }

        @Override
        public Collection<Address> getAddress() {
            return Collections.emptyList();
        }

        @Override
        public void setAddress(final Collection<Address> address) {

        }

        @Override
        public Collection<CustomerShop> getShops() {
            return customerShops;
        }

        @Override
        public void setShops(final Collection<CustomerShop> shops) {

        }

        @Override
        public Collection<AttrValue> getAllAttributes() {
            return Collections.emptyList();
        }

        @Override
        public Map<String, AttrValue> getAllAttributesAsMap() {
            return Collections.emptyMap();
        }

        @Override
        public String getAttributeValueByCode(final String attributeCode) {
            return null;
        }

        @Override
        public boolean isAttributeValueByCodeTrue(final String attributeCode) {
            if (this.roles.contains(attributeCode)) {
                return true;
            }
            return false;
        }

        @Override
        public String getName() {
            final StringBuilder name = new StringBuilder();
            if (StringUtils.isNotBlank(manager.getSalutation())) {
                name.append(manager.getSalutation());
            }
            if (StringUtils.isNotBlank(manager.getFirstname())) {
                if (name.length() > 0) {
                    name.append(' ');
                }
                name.append(manager.getFirstname());
            }
            if (StringUtils.isNotBlank(manager.getMiddlename())) {
                if (name.length() > 0) {
                    name.append(' ');
                }
                name.append(manager.getMiddlename());
            }
            if (StringUtils.isNotBlank(manager.getLastname())) {
                if (name.length() > 0) {
                    name.append(' ');
                }
                name.append(manager.getLastname());
            }
            return name.toString();
        }

        @Override
        public void setName(final String name) {

        }

        @Override
        public String getDescription() {
            return manager.getEmail();
        }

        @Override
        public void setDescription(final String description) {

        }

        @Override
        public Instant getCreatedTimestamp() {
            return manager.getCreatedTimestamp();
        }

        @Override
        public void setCreatedTimestamp(final Instant createdTimestamp) {

        }

        @Override
        public Instant getUpdatedTimestamp() {
            return manager.getUpdatedTimestamp();
        }

        @Override
        public void setUpdatedTimestamp(final Instant updatedTimestamp) {

        }

        @Override
        public String getCreatedBy() {
            return manager.getCreatedBy();
        }

        @Override
        public void setCreatedBy(final String createdBy) {

        }

        @Override
        public String getUpdatedBy() {
            return manager.getUpdatedBy();
        }

        @Override
        public void setUpdatedBy(final String updatedBy) {

        }

        @Override
        public String getGuid() {
            return manager.getGuid();
        }

        @Override
        public void setGuid(final String guid) {

        }

        @Override
        public long getId() {
            return manager.getManagerId();
        }

        @Override
        public String getEmail() {
            return manager.getEmail();
        }

        @Override
        public void setEmail(final String email) {

        }

        @Override
        public String getFirstname() {
            return manager.getFirstname();
        }

        @Override
        public void setFirstname(final String firstname) {

        }

        @Override
        public String getLastname() {
            return manager.getLastname();
        }

        @Override
        public void setLastname(final String lastname) {

        }

        @Override
        public String getMiddlename() {
            return manager.getMiddlename();
        }

        @Override
        public void setMiddlename(final String middlename) {

        }

        @Override
        public String getSalutation() {
            return manager.getSalutation();
        }

        @Override
        public void setSalutation(final String salutation) {

        }

        @Override
        public String getPassword() {
            return manager.getPassword();
        }

        @Override
        public void setPassword(final String password) {

        }

        @Override
        public Instant getPasswordExpiry() {
            return manager.getPasswordExpiry();
        }

        @Override
        public void setPasswordExpiry(final Instant passwordExpiry) {

        }

        @Override
        public String getAuthToken() {
            return manager.getAuthToken();
        }

        @Override
        public void setAuthToken(final String authToken) {

        }

        @Override
        public Instant getAuthTokenExpiry() {
            return manager.getAuthTokenExpiry();
        }

        @Override
        public void setAuthTokenExpiry(final Instant authTokenExpiry) {

        }

        @Override
        public String getCompanyName1() {
            return manager.getCompanyName1();
        }

        @Override
        public void setCompanyName1(final String companyName1) {

        }

        @Override
        public String getCompanyName2() {
            return manager.getCompanyName2();
        }

        @Override
        public void setCompanyName2(final String companyName2) {

        }

        @Override
        public String getCompanyDepartment() {
            return manager.getCompanyDepartment();
        }

        @Override
        public void setCompanyDepartment(final String companyDepartment) {

        }

        @Override
        public String getTag() {
            return null;
        }

        @Override
        public void setTag(final String tag) {

        }

        @Override
        public long getVersion() {
            return manager.getVersion();
        }
        
    }

    private static class ManagerShopCustomerReadOnlyAdapter implements CustomerShop {

        private final Customer customer;
        private final ManagerShop managerShop;

        private ManagerShopCustomerReadOnlyAdapter(final Customer customer,
                                                   final ManagerShop managerShop) {
            this.customer = customer;
            this.managerShop = managerShop;
        }

        @Override
        public long getCustomerShopId() {
            return managerShop.getManagerShopId();
        }

        @Override
        public void setCustomerShopId(final long customerShopId) {

        }

        @Override
        public Customer getCustomer() {
            return customer;
        }

        @Override
        public void setCustomer(final Customer customer) {

        }

        @Override
        public Shop getShop() {
            return managerShop.getShop();
        }

        @Override
        public void setShop(final Shop shop) {

        }

        @Override
        public boolean isDisabled() {
            return false;
        }

        @Override
        public void setDisabled(final boolean disabled) {

        }

        @Override
        public String getGuid() {
            return managerShop.getGuid();
        }

        @Override
        public void setGuid(final String guid) {

        }

    }

}
