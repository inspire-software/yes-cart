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

package org.yes.cart.service.dto.impl;

import com.inspiresoftware.lib.dto.geda.adapter.repository.AdaptersRepository;
import com.inspiresoftware.lib.dto.geda.assembler.Assembler;
import com.inspiresoftware.lib.dto.geda.assembler.DTOAssembler;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.yes.cart.constants.AttributeGroupNames;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.dto.*;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.dto.impl.AttrValueCustomerDTOImpl;
import org.yes.cart.domain.dto.impl.CustomerDTOImpl;
import org.yes.cart.domain.dto.impl.ShopDTOImpl;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.entity.impl.AttrValueEntityCustomer;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.domain.AttributeService;
import org.yes.cart.service.domain.CustomerService;
import org.yes.cart.service.domain.GenericService;
import org.yes.cart.service.dto.DtoAttributeService;
import org.yes.cart.service.dto.DtoCustomerService;
import org.yes.cart.utils.impl.AttrValueDTOComparatorImpl;

import java.util.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 *
 */
public class DtoCustomerServiceImpl
        extends AbstractDtoServiceImpl<CustomerDTO, CustomerDTOImpl, Customer>
        implements DtoCustomerService {

    private final DtoAttributeService dtoAttributeService;

    private final GenericDAO<AttrValueEntityCustomer, Long> attrValueEntityCustomerDao;

    private final GenericDAO<Shop, Long> shopDao;

    private final GenericDAO<Address, Long> addressDao;

    private final Assembler attrValueAssembler;

    private final Assembler shopAssembler;

    /**
     * Construct base remote service.
     *  @param dtoFactory               {@link DtoFactory}
     * @param customerGenericService   {@link GenericService}
     * @param adaptersRepository {@link AdaptersRepository}
     * @param dtoAttributeService      {@link DtoAttributeService}
     * @param attrValueEntityCustomerDao       link to customer attribute values dao
     * @param shopDao shop dao
     * @param addressDao address dao
     */
    public DtoCustomerServiceImpl(
            final DtoFactory dtoFactory,
            final GenericService<Customer> customerGenericService,
            final AdaptersRepository adaptersRepository,
            final DtoAttributeService dtoAttributeService,
            final GenericDAO<AttrValueEntityCustomer, Long> attrValueEntityCustomerDao,
            final GenericDAO<Shop, Long> shopDao,
            final GenericDAO<Address, Long> addressDao) {

        super(dtoFactory, customerGenericService, adaptersRepository);

        this.dtoAttributeService = dtoAttributeService;

        this.attrValueEntityCustomerDao = attrValueEntityCustomerDao;

        this.shopDao = shopDao;
        this.addressDao = addressDao;

        this.attrValueAssembler = DTOAssembler.newAssembler(
                dtoFactory.getImplClass(AttrValueCustomerDTO.class),
                service.getGenericDao().getEntityFactory().getImplClass(AttrValueCustomer.class));
        shopAssembler = DTOAssembler.newAssembler(ShopDTOImpl.class, Shop.class);

    }

    /**
     * {@inheritDoc}
     */
    public CustomerDTO createForShop(final CustomerDTO instance, final long shopId) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        Customer customer = getPersistenceEntityFactory().getByIface(getEntityIFace());
        assembler.assembleEntity(instance, customer, null, dtoFactory);
        customer = ((CustomerService)service).create(customer, shopDao.findById(shopId));
        return getById(customer.getCustomerId());
    }

    /**
     * {@inheritDoc}
     */
    public CustomerDTO create(final CustomerDTO instance) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        throw new UnsupportedOperationException("Customers must have at least one shop assigned use #createForShop()");
    }



    /**
     * {@inheritDoc}
     */
    public Class<CustomerDTO> getDtoIFace() {
        return CustomerDTO.class;
    }

    /**
     * {@inheritDoc}
     */
    public Class<CustomerDTOImpl> getDtoImpl() {
        return CustomerDTOImpl.class;
    }

    /**
     * {@inheritDoc}
     */
    public Class<Customer> getEntityIFace() {
        return Customer.class;
    }

    /**
     * {@inheritDoc}
     */
    public List<? extends AttrValueDTO> getEntityAttributes(final long entityPk)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final List<AttrValueCustomerDTO> result = new ArrayList<AttrValueCustomerDTO>();
        if (entityPk > 0L) {

            final CustomerDTO customerDTO = getById(entityPk);
            result.addAll(getById(entityPk).getAttributes());
            final List<AttributeDTO> availableAttributeDTOs = dtoAttributeService.findAvailableAttributes(
                    AttributeGroupNames.CUSTOMER,
                    getCodes(result));
            for (AttributeDTO attributeDTO : availableAttributeDTOs) {
                AttrValueCustomerDTO attrValueCategoryDTO = getAssemblerDtoFactory().getByIface(AttrValueCustomerDTO.class);
                attrValueCategoryDTO.setAttributeDTO(attributeDTO);
                attrValueCategoryDTO.setCustomerId(entityPk);
                if ("salutation".equals(attrValueCategoryDTO.getAttributeDTO().getCode())) {
                    attrValueCategoryDTO.setVal(customerDTO.getSalutation());
                } else if ("firstname".equals(attrValueCategoryDTO.getAttributeDTO().getCode())) {
                    attrValueCategoryDTO.setVal(customerDTO.getFirstname());
                } else if ("middlename".equals(attrValueCategoryDTO.getAttributeDTO().getCode())) {
                    attrValueCategoryDTO.setVal(customerDTO.getMiddlename());
                } else if ("lastname".equals(attrValueCategoryDTO.getAttributeDTO().getCode())) {
                    attrValueCategoryDTO.setVal(customerDTO.getLastname());
                } else if ("customertype".equals(attrValueCategoryDTO.getAttributeDTO().getCode())) {
                    attrValueCategoryDTO.setVal(customerDTO.getCustomerType());
                } else if ("pricingpolicy".equals(attrValueCategoryDTO.getAttributeDTO().getCode())) {
                    attrValueCategoryDTO.setVal(customerDTO.getPricingPolicy());
                }
                result.add(attrValueCategoryDTO);
            }
            Collections.sort(result, new AttrValueDTOComparatorImpl());
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public AttrValueDTO updateEntityAttributeValue(final AttrValueDTO attrValueDTO) {
        AttrValueEntityCustomer attrValueCustomer = attrValueEntityCustomerDao.findById(attrValueDTO.getAttrvalueId());
        attrValueAssembler.assembleEntity(attrValueDTO, attrValueCustomer, getAdaptersRepository(), dtoFactory);
        attrValueEntityCustomerDao.update(attrValueCustomer);
        return attrValueDTO;
    }

    /**
     * {@inheritDoc}
     */
    public AttrValueDTO createEntityAttributeValue(final AttrValueDTO attrValueDTO) {

        final Attribute atr = ((AttributeService) dtoAttributeService.getService()).findById(attrValueDTO.getAttributeDTO().getAttributeId());
        final boolean multivalue = atr.isAllowduplicate();
        final Customer customer = service.findById(((AttrValueCustomerDTO) attrValueDTO).getCustomerId());
        if ("salutation".equals(atr.getCode())) {
            if (StringUtils.isNotBlank(attrValueDTO.getVal())) {
                customer.setSalutation(attrValueDTO.getVal());
            } else {
                customer.setSalutation(null);
            }
            getService().update(customer);
        } else if ("firstname".equals(atr.getCode())) {
            if (StringUtils.isNotBlank(attrValueDTO.getVal())) {
                customer.setFirstname(attrValueDTO.getVal());
            }
            getService().update(customer);
        } else if ("middlename".equals(atr.getCode())) {
            if (StringUtils.isNotBlank(attrValueDTO.getVal())) {
                customer.setMiddlename(attrValueDTO.getVal());
            } else {
                customer.setMiddlename(null);
            }
            getService().update(customer);
        } else if ("lastname".equals(atr.getCode())) {
            if (StringUtils.isNotBlank(attrValueDTO.getVal())) {
                customer.setLastname(attrValueDTO.getVal());
            }
            getService().update(customer);
        } else if ("customertype".equals(atr.getCode())) {
            if (StringUtils.isNotBlank(attrValueDTO.getVal())) {
                customer.setCustomerType(attrValueDTO.getVal());
            }
            getService().update(customer);
        }  else if ("pricingpolicy".equals(atr.getCode())) {
            customer.setPricingPolicy(attrValueDTO.getVal());
        } else {
            if (!multivalue) {
                for (final AttrValueCustomer avp : customer.getAttributes()) {
                    if (avp.getAttribute().getCode().equals(atr.getCode())) {
                        // this is a duplicate, so need to update
                        attrValueDTO.setAttrvalueId(avp.getAttrvalueId());
                        return updateEntityAttributeValue(attrValueDTO);
                    }
                }
            }


            AttrValueCustomer valueEntityCustomer = getPersistenceEntityFactory().getByIface(AttrValueCustomer.class);
            attrValueAssembler.assembleEntity(attrValueDTO, valueEntityCustomer, getAdaptersRepository(), dtoFactory);
            valueEntityCustomer.setAttribute(atr);
            valueEntityCustomer.setCustomer(customer);
            valueEntityCustomer = attrValueEntityCustomerDao.create((AttrValueEntityCustomer) valueEntityCustomer);
            attrValueDTO.setAttrvalueId(valueEntityCustomer.getAttrvalueId());
        }
        return attrValueDTO;

    }

    /**
     * {@inheritDoc}
     */
    public long deleteAttributeValue(final long attributeValuePk) {
        final AttrValueEntityCustomer valueEntityCustomer = attrValueEntityCustomerDao.findById(attributeValuePk);
        attrValueEntityCustomerDao.delete(valueEntityCustomer);
        return valueEntityCustomer.getCustomer().getCustomerId();
    }


    /**
     * {@inheritDoc}
     */
    public List<CustomerDTO> findCustomer(final String email,
                                          final String firstname,
                                          final String lastname,
                                          final String middlename,
                                          final String tag,
                                          final String customerType,
                                          final String pricingPolicy) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<Customer> entities = ((CustomerService)service).findCustomer(email, firstname, lastname, middlename, tag, customerType, pricingPolicy);
        final List<CustomerDTO> dtos  = new ArrayList<CustomerDTO>(entities.size());
        fillDTOs(entities, dtos);
        return dtos;
    }

    private final static char[] REF_OR_CUSTOMER_OR_ADDRESS = new char[] { '#', '?', '@', '$' };
    static {
        Arrays.sort(REF_OR_CUSTOMER_OR_ADDRESS);
    }

    /**
     * {@inheritDoc}
     */
    public List<CustomerDTO> findBy(final String filter, final int page, final int pageSize) throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final List<CustomerDTO> customers = new ArrayList<>();

        final List<Criterion> criteria = new ArrayList<Criterion>();
        final List<Order> order = new ArrayList<Order>();

        if (StringUtils.isNotBlank(filter)) {

            final Pair<String, String> refOrCustomerOrAddressOrPolicy = ComplexSearchUtils.checkSpecialSearch(filter, REF_OR_CUSTOMER_OR_ADDRESS);
            final Pair<Date, Date> dateSearch = refOrCustomerOrAddressOrPolicy == null ? ComplexSearchUtils.checkDateRangeSearch(filter) : null;

            if (refOrCustomerOrAddressOrPolicy != null) {

                if ("#".equals(refOrCustomerOrAddressOrPolicy.getFirst())) {
                    // order number search
                    final String refNumber = refOrCustomerOrAddressOrPolicy.getSecond();
                    criteria.add(Restrictions.or(
                            Restrictions.eq("customerId", NumberUtils.toLong(refNumber)),
                            Restrictions.ilike("email", refNumber, MatchMode.ANYWHERE),
                            Restrictions.ilike("tag", refNumber, MatchMode.ANYWHERE)
                    ));
                    order.add(Order.asc("email"));
                } else if ("?".equals(refOrCustomerOrAddressOrPolicy.getFirst())) {
                    // customer search
                    final String customer = refOrCustomerOrAddressOrPolicy.getSecond();
                    criteria.add(Restrictions.or(
                            Restrictions.ilike("email", customer, MatchMode.ANYWHERE),
                            Restrictions.ilike("firstname", customer, MatchMode.ANYWHERE),
                            Restrictions.ilike("lastname", customer, MatchMode.ANYWHERE)
                    ));
                    order.add(Order.asc("lastname"));
                    order.add(Order.asc("email"));
                } else if ("@".equals(refOrCustomerOrAddressOrPolicy.getFirst())) {
                    // address search
                    final String address = refOrCustomerOrAddressOrPolicy.getSecond();
                    final List<Long> ids = (List) this.addressDao.
                            findQueryObjectsRangeByNamedQuery("CUSTOMER.IDS.BY.ADDRESS", page * pageSize, pageSize, "%" + address.toLowerCase() + "%");

                    if (ids.isEmpty()) {
                        return Collections.emptyList();
                    }

                    criteria.add(Restrictions.in("customerId", ids));
                    order.add(Order.asc("lastname"));
                    order.add(Order.asc("email"));

                } else if ("$".equals(refOrCustomerOrAddressOrPolicy.getFirst())) {
                    // address search
                    final String policy = refOrCustomerOrAddressOrPolicy.getSecond();
                    criteria.add(Restrictions.or(
                            Restrictions.ilike("customerType", policy, MatchMode.EXACT),
                            Restrictions.ilike("pricingPolicy", policy, MatchMode.EXACT)
                    ));
                    order.add(Order.asc("lastname"));
                    order.add(Order.asc("email"));
                }

            } else if (dateSearch != null) {

                final Date from = dateSearch.getFirst();
                final Date to = dateSearch.getSecond();

                // time search
                if (from != null) {
                    criteria.add(Restrictions.ge("createdTimestamp", from));
                }
                if (to != null) {
                    criteria.add(Restrictions.le("createdTimestamp", to));
                }

                order.add(Order.desc("createdTimestamp"));

            } else {

                criteria.add(Restrictions.or(
                        Restrictions.ilike("email", filter, MatchMode.ANYWHERE),
                        Restrictions.ilike("firstname", filter, MatchMode.ANYWHERE),
                        Restrictions.ilike("lastname", filter, MatchMode.ANYWHERE),
                        Restrictions.ilike("customerType", filter, MatchMode.EXACT),
                        Restrictions.ilike("pricingPolicy", filter, MatchMode.EXACT)
                ));
                order.add(Order.desc("createdTimestamp"));
                order.add(Order.asc("lastname"));
                order.add(Order.asc("email"));

            }

        } else {
            order.add(Order.desc("createdTimestamp"));
            order.add(Order.asc("lastname"));
            order.add(Order.asc("email"));
        }

        final List<Customer> entities = service.getGenericDao().findByCriteria(page * pageSize, pageSize,
                criteria.toArray(new Criterion[criteria.size()]), order.toArray(new Order[order.size()]));

        fillDTOs(entities, customers);

        return customers;

    }

    /**
     * {@inheritDoc}
     */
    public void remoteResetPassword(final CustomerDTO customer, final long shopId) {
        final Customer cust = service.findById(customer.getCustomerId());
        if (cust != null) {
            final Shop shop = shopDao.findById(shopId);
            final String av = (shop.getMaster() != null ? shop.getMaster() : shop).getAttributeValueByCode(AttributeNamesKeys.Shop.SHOP_CUSTOMER_PASSWORD_RESET_CC);
            String authToken = "invalid";
            if (StringUtils.isNotBlank(av)) {
                authToken = av;
            }
            ((CustomerService)service).resetPassword(cust, shop, authToken);
        }
    }

    @Override
    public void updateCustomerTags(final CustomerDTO customer, final String tags) {
        final Customer cust = service.findById(customer.getCustomerId());
        if (cust != null) {
            if (StringUtils.isBlank(tags)) {
                cust.setTag(null);
            } else {
                final Set<String> unique = new TreeSet<String>();
                final String[] values = tags.split(" ");
                for (final String value : values) {
                    if (StringUtils.isNotBlank(value)) {
                        unique.add(value.trim());
                    }
                }
                if (!unique.isEmpty()) {
                    final StringBuilder tagsUnique = new StringBuilder();
                    for (final String tag : unique) {
                        tagsUnique.append(tag).append(' ');
                    }
                    tagsUnique.deleteCharAt(tagsUnique.length() - 1);
                    cust.setTag(tagsUnique.toString());
                } else {
                    cust.setTag(null);
                }
            }
            service.update(cust);
        }
    }

    /**
     * {@inheritDoc}
     */
    public AttrValueDTO createAndBindAttrVal(final long entityPk, final String attrName, final String attrValue)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        throw new UnmappedInterfaceException("Not implemented");
    }

    /**
     * {@inheritDoc}
     */
    public Map<ShopDTO, Boolean> getAssignedShop(final long customerId) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final Collection<CustomerShop> assigned = getService().findById(customerId).getShops();
        final Map<Long, Boolean> enabledMap = new HashMap<>(assigned.size() * 2);
        for (final CustomerShop shop : assigned) {
            enabledMap.put(shop.getShop().getShopId(), shop.isDisabled());
        }
        final List<ShopDTO> shopDTOs = new ArrayList<ShopDTO>(assigned.size());
        fillCarrierShopsDTOs(shopDTOs, assigned);
        final Map<ShopDTO, Boolean> dtoPairs = new LinkedHashMap<>(shopDTOs.size());
        for (final ShopDTO dto : shopDTOs) {
            dtoPairs.put(dto, enabledMap.get(dto.getShopId()));
        }
        return dtoPairs;
    }

    private void fillCarrierShopsDTOs(final List<ShopDTO> result, final Collection<CustomerShop> shops)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        for (CustomerShop shop : shops) {
            final ShopDTO shopDTO = dtoFactory.getByIface(ShopDTO.class);
            shopAssembler.assembleDto(shopDTO, shop.getShop(), getAdaptersRepository(), dtoFactory);
            result.add(shopDTO);
        }
    }


    /**
     * {@inheritDoc}
     */
    public List<ShopDTO> getAvailableShop(final long customerId) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<Shop> all = shopDao.findAll();
        final Set<ShopDTO> assigned = getAssignedShop(customerId).keySet();
        final List<ShopDTO> available = new ArrayList<ShopDTO>(all.size());
        for (final Shop shop : all) {
            boolean match = false;
            for (final ShopDTO existing : assigned) {
                if (shop.getShopId() == existing.getShopId()) {
                    match = true;
                }
            }
            if (!match) {
                final ShopDTO shopDTO = dtoFactory.getByIface(ShopDTO.class);
                shopAssembler.assembleDto(shopDTO, shop, getAdaptersRepository(), dtoFactory);
                available.add(shopDTO);
            }
        }
        return available;
    }

    /**
     * {@inheritDoc}
     */
    public void grantShop(final long customerId, final long shopId, final boolean soft) {

        final Customer customer = getService().findById(customerId);
        final Shop shop = shopDao.findById(shopId);
        if (customer != null && shop != null) {
            ((CustomerService) getService()).updateActivate(customer, shop, soft);
        }

    }

    /**
     * {@inheritDoc}
     */
    public void revokeShop(final long customerId, final long shopId, final boolean soft) {

        final Customer customer = getService().findById(customerId);
        final Shop shop = shopDao.findById(shopId);
        if (customer != null && shop != null) {
            ((CustomerService) getService()).updateDeactivate(customer, shop, soft);
        }

    }


    /**
     * {@inheritDoc}
     */
    public AttrValueDTO getNewAttribute(final long entityPk) throws UnableToCreateInstanceException, UnmappedInterfaceException {
        final AttrValueCustomerDTO dto = new AttrValueCustomerDTOImpl();
        dto.setCustomerId(entityPk);
        return dto;
    }

}
