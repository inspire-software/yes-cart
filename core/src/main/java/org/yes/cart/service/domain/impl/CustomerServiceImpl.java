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
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.yes.cart.constants.AttributeGroupNames;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.constants.Constants;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.misc.SearchContext;
import org.yes.cart.service.customer.CustomerNameFormatter;
import org.yes.cart.service.domain.*;
import org.yes.cart.utils.HQLUtils;
import org.yes.cart.utils.log.Markers;

import java.util.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class CustomerServiceImpl extends BaseGenericServiceImpl<Customer> implements CustomerService {

    private static final Logger LOG = LoggerFactory.getLogger(CustomerServiceImpl.class);

    private static final AttributeRankComparator ATTRIBUTE_RANK_COMPARATOR = new AttributeRankComparator();

    private final HashHelper passwordHashHelper;

    private final GenericDAO<Object, Long> customerShopDao;

    private final AttributeService attributeService;

    private final ShopService shopService;

    private final CustomerNameFormatter customerNameFormatter;


    /**
     * Construct customer service.
     *
     * @param genericDao customer dao to use.
     * @param passwordHashHelper to generate password hash
     * @param customerShopDao    to delete
     * @param shopService shop service (reuse retrieval + caching)
     * @param customerNameFormatter customer name formatter
     */
    public CustomerServiceImpl(final GenericDAO<Customer, Long> genericDao,
                               final HashHelper passwordHashHelper,
                               final GenericDAO<Object, Long> customerShopDao,
                               final AttributeService attributeService,
                               final ShopService shopService,
                               final CustomerNameFormatter customerNameFormatter) {
        super(genericDao);
        this.passwordHashHelper = passwordHashHelper;
        this.customerShopDao = customerShopDao;
        this.attributeService = attributeService;
        this.shopService = shopService;
        this.customerNameFormatter = customerNameFormatter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Customer getCustomerByEmail(final String email, Shop shop) {
        if (StringUtils.isNotBlank(email)) {
            Customer customer = getGenericDao().findSingleByNamedQuery("CUSTOMER.BY.EMAIL.SHOP", email.toLowerCase(), shop.getShopId(), Boolean.FALSE);
            if (customer != null) {
                Hibernate.initialize(customer.getAttributes());
            }
            return customer;
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Customer getCustomerByToken(final String token) {
        Customer customer = getGenericDao().findSingleByCriteria(" where e.authToken = ?1", token);
        if (customer != null) {
            Hibernate.initialize(customer.getAttributes());
        }
        return customer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Customer getCustomerByPublicKey(final String publicKey, final String lastName) {
        Customer customer = getGenericDao().findSingleByCriteria(" where e.publicKey = ?1 and e.lastname = ?2", publicKey, lastName);
        if (customer != null) {
            Hibernate.initialize(customer.getAttributes());
        }
        return customer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Shop> getCustomerShops(final Customer customer) {
        final List<Shop> shops = new ArrayList<>();
        if (customer != null) {
            Customer cust = getGenericDao().findById(customer.getCustomerId());
            if (cust != null) {
                for (CustomerShop customerShop : cust.getShops()) {
                    if (!customerShop.isDisabled()) {
                        final Shop shop = shopService.getById(customerShop.getShop().getShopId());
                        if (shop != null && !shop.isDisabled()) {
                            shops.add(shop);
                        }
                    }
                }
            }
        }
        return shops;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String formatNameFor(final Customer customer, final Shop shop) {

        if (shop != null) {
            final String format = shop.getAttributeValueByCode(AttributeNamesKeys.Shop.CUSTOMER_NAME_FORMATTER);
            return customerNameFormatter.formatName(customer, format);
        }
        return customerNameFormatter.formatName(customer);

    }

    private Pair<String, Object[]> findCustomerQuery(final boolean count,
                                                     final String sort,
                                                     final boolean sortDescending,
                                                     final Set<Long> shops,
                                                     final Map<String, List> filter) {

        final Map<String, List> currentFilter = filter != null ? new HashMap<>(filter) : null;

        final StringBuilder hqlCriteria = new StringBuilder();
        final List<Object> params = new ArrayList<>();

        if (count) {
            hqlCriteria.append("select count(distinct c.customerId) from CustomerEntity c left join c.shops cse ");
        } else {
            hqlCriteria.append("select distinct c from CustomerEntity c left join fetch c.shops cse ");
        }

        Boolean disabled = Boolean.FALSE;
        if (currentFilter != null) {
            final List disabledParam = currentFilter.remove("disabled");
            if (CollectionUtils.isNotEmpty(disabledParam)) {
                final Object disabledFilter = disabledParam.get(0);
                if (disabledFilter instanceof String) {
                    disabled = Boolean.valueOf((String) disabledFilter);
                } else if (disabledFilter instanceof Boolean) {
                    disabled = (Boolean) disabledFilter;
                } else if (disabledFilter == SearchContext.MatchMode.ANY) {
                    disabled = null;
                }
            }
        }

        if (CollectionUtils.isNotEmpty(shops)) {
            hqlCriteria.append(" where (cse.shop.shopId in (?1) or cse.shop.master.shopId in (?1)) ");
            params.add(shops);
        }

        if (disabled != null) {
            if (params.isEmpty()) {
                hqlCriteria.append(" where (cse.disabled = ?1) ");
            } else {
                hqlCriteria.append(" and (cse.disabled = ?2) ");
            }
            params.add(disabled);
        }

        HQLUtils.appendFilterCriteria(hqlCriteria, params, "c", currentFilter);

        if (StringUtils.isNotBlank(sort)) {

            hqlCriteria.append(" order by c." + sort + " " + (sortDescending ? "desc" : "asc"));

        }

        return new Pair<>(
                hqlCriteria.toString(),
                params.toArray(new Object[params.size()])
        );

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Customer> findCustomers(final int start,
                                        final int offset,
                                        final String sort,
                                        final boolean sortDescending,
                                        final Set<Long> shops,
                                        final Map<String, List> filter) {

        final Pair<String, Object[]> query = findCustomerQuery(false, sort, sortDescending, shops, filter);

        return getGenericDao().findRangeByQuery(
                query.getFirst(),
                start, offset,
                query.getSecond()
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int findCustomerCount(final Set<Long> shops,
                                 final Map<String, List> filter) {

        final Pair<String, Object[]> query = findCustomerQuery(true, null, false, shops, filter);

        return getGenericDao().findCountByQuery(
                query.getFirst(),
                query.getSecond()
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCustomerExists(final String email, final Shop shop) {
        if (StringUtils.isNotBlank(email)) {
            final int counts = getGenericDao().findCountByNamedQuery("EMAIL.CHECK", email.toLowerCase(), shop.getShopId());
            return counts > 0;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isPasswordValid(final String email, final Shop shop, final String password) {
        try {

            if (StringUtils.isBlank(password)) {
                return false;
            }

            final String hash = passwordHashHelper.getHash(password);

            final int validLoginsFound = getGenericDao().findCountByNamedQuery("PASS.CHECK", email.toLowerCase(), shop.getShopId(), hash, Boolean.FALSE);

            if (validLoginsFound == 1) {
                return true;
            } else if (validLoginsFound > 1) {
                LOG.warn(Markers.alert(), "Customer {} assigned to multiple shops when checking {} ... password will be marked as invalid", email, shop.getCode());
            }
            return false;

        } catch (Exception exp) {
            LOG.error(exp.getMessage(), exp);
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AttrValueCustomer> getRankedAttributeValues(final Customer customer) {
        final Map<String, AttrValueCustomer> filledAttributes;
        if (customer != null) {
            filledAttributes = new HashMap<>(customer.getAttributes().size());
            for (AttrValueCustomer attrVal : customer.getAttributes()) {
                filledAttributes.put(attrVal.getAttributeCode(), attrVal);
            }
        } else {
            filledAttributes = Collections.EMPTY_MAP;
        }
        final List<Attribute> attributes = new ArrayList<Attribute>(attributeService.findAvailableAttributes(AttributeGroupNames.CUSTOMER, Collections.EMPTY_LIST));
        attributes.sort(ATTRIBUTE_RANK_COMPARATOR);

        final List<AttrValueCustomer> rez = new ArrayList<>(attributes.size());
        for (Attribute attr : attributes) {
            final AttrValueCustomer existing = filledAttributes.get(attr.getCode());
            if (existing != null) {
                rez.add(existing);
            } else {
                AttrValueCustomer attrValueCustomer = attributeService.getGenericDao().getEntityFactory().getByIface(AttrValueCustomer.class);
                attrValueCustomer.setAttributeCode(attr.getCode());
                attrValueCustomer.setCustomer(customer);
                rez.add(attrValueCustomer);
            }
        }
        return rez;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void resetPassword(final Customer customer, final Shop shop, final String authToken) {
        super.update(customer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updatePassword(final Customer customer, final Shop shop, final String newPassword) {
        try {
            customer.setPassword(passwordHashHelper.getHash(newPassword));
            customer.setPasswordExpiry(null); // TODO: YC-906 Create password expiry flow for customers
            customer.setAuthToken(null);
            customer.setAuthTokenExpiry(null);
            super.update(customer);
        } catch (Exception exp) {
            LOG.error(exp.getMessage(), exp);
            throw new BadCredentialsException(Constants.PASSWORD_RESET_PASSWORD_INVALID);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Customer create(final Customer customer, final Shop shop) {
        if (shop != null) {
            final String customerType = StringUtils.isNotBlank(customer.getCustomerType()) ? customer.getCustomerType() : AttributeNamesKeys.Cart.CUSTOMER_TYPE_REGULAR;
            final boolean requiredApproval = shop.getMaster() != null ?
                    shop.getMaster().isSfRequireCustomerRegistrationApproval(customerType) :
                    shop.isSfRequireCustomerRegistrationApproval(customerType);
            final CustomerShop customerShop = getGenericDao().getEntityFactory().getByIface(CustomerShop.class);
            customerShop.setCustomer(customer);
            customerShop.setShop(shop);
            customerShop.setDisabled(requiredApproval);
            customer.getShops().add(customerShop);
        }
        return super.create(customer);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Customer updateActivate(final Customer customer, final Shop shop, final boolean soft) {

        if (shop != null) {
            final Collection<CustomerShop> assigned = customer.getShops();
            for (final CustomerShop customerShop : assigned) {
                if (customerShop.getShop().getShopId() == shop.getShopId()) {
                    if (customerShop.isDisabled() && !soft) {
                        customerShop.setDisabled(false);
                        update(customer);
                    }
                    return customer;
                }
            }
            final CustomerShop customerShop = getGenericDao().getEntityFactory().getByIface(CustomerShop.class);
            customerShop.setCustomer(customer);
            customerShop.setShop(shop);
            customerShop.setDisabled(soft);
            assigned.add(customerShop);
            update(customer);
        }
        return customer;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Customer updateDeactivate(final Customer customer, final Shop shop, final boolean soft) {

        if (shop != null) {
            final Iterator<CustomerShop> assigned = customer.getShops().iterator();
            while (assigned.hasNext()) {
                final CustomerShop customerShop = assigned.next();
                if (customerShop.getShop().getShopId() == shop.getShopId()) {
                    if (soft) {
                        customerShop.setDisabled(true);
                    } else {
                        assigned.remove();
                    }
                    return update(customer);
                }
            }
        }
        return customer;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Customer create(final Customer instance) {
        throw new UnsupportedOperationException("Please use create(final Customer customer, final Shop shop)");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Customer update(final String email, final String shopCode) {
        final Shop shop = shopService.getShopByCode(shopCode);
        if (shop != null) {
            final Customer customer = getCustomerByEmail(email, shop);
            for (final CustomerShop customerShop : customer.getShops()) {
                if (shop.getShopId() == customerShop.getShop().getShopId()) {
                    return customer;
                }
            }
            final CustomerShop customerShop = getGenericDao().getEntityFactory().getByIface(CustomerShop.class);
            customerShop.setCustomer(customer);
            customerShop.setShop(shop);
            customer.getShops().add(customerShop);
            return super.update(customer);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Customer update(final Customer instance) {
        return super.update(instance);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(final Customer instance) {
        for(CustomerShop cshop : instance.getShops()) {
            customerShopDao.delete(cshop);
        }
        super.delete(instance);
    }
}
