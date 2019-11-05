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
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.yes.cart.constants.AttributeGroupNames;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.constants.Constants;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.dto.*;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.dto.impl.AttrValueCustomerDTOImpl;
import org.yes.cart.domain.dto.impl.CustomerDTOImpl;
import org.yes.cart.domain.dto.impl.ShopDTOImpl;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.entity.impl.AttrValueEntityCustomer;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.misc.SearchContext;
import org.yes.cart.domain.misc.SearchResult;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.domain.AttributeService;
import org.yes.cart.service.domain.CustomerRemoveService;
import org.yes.cart.service.domain.CustomerService;
import org.yes.cart.service.domain.GenericService;
import org.yes.cart.service.dto.AttrValueDTOComparator;
import org.yes.cart.service.dto.DtoAttributeService;
import org.yes.cart.service.dto.DtoCustomerService;
import org.yes.cart.utils.DateUtils;
import org.yes.cart.utils.HQLUtils;

import java.time.Instant;
import java.time.LocalDateTime;
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

    private static final AttrValueDTOComparator ATTR_VALUE_DTO_COMPARATOR = new AttrValueDTOComparator();

    private final CustomerRemoveService customerRemoveService;

    private final DtoAttributeService dtoAttributeService;

    private final GenericDAO<AttrValueEntityCustomer, Long> attrValueEntityCustomerDao;

    private final GenericDAO<Shop, Long> shopDao;

    private final GenericDAO<Address, Long> addressDao;

    private final Assembler attrValueAssembler;

    private final Assembler shopAssembler;

    /**
     * Construct base remote service.
     *
     * @param dtoFactory               {@link DtoFactory}
     * @param customerGenericService   {@link GenericService}
     * @param customerRemoveService    remove service
     * @param adaptersRepository {@link AdaptersRepository}
     * @param dtoAttributeService      {@link DtoAttributeService}
     * @param attrValueEntityCustomerDao       link to customer attribute values dao
     * @param shopDao shop dao
     * @param addressDao address dao
     */
    public DtoCustomerServiceImpl(
            final DtoFactory dtoFactory,
            final GenericService<Customer> customerGenericService,
            final CustomerRemoveService customerRemoveService,
            final AdaptersRepository adaptersRepository,
            final DtoAttributeService dtoAttributeService,
            final GenericDAO<AttrValueEntityCustomer, Long> attrValueEntityCustomerDao,
            final GenericDAO<Shop, Long> shopDao,
            final GenericDAO<Address, Long> addressDao) {

        super(dtoFactory, customerGenericService, adaptersRepository);

        this.customerRemoveService = customerRemoveService;

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
    @Override
    public CustomerDTO createForShop(final CustomerDTO instance, final long shopId) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        Customer customer = getPersistenceEntityFactory().getByIface(getEntityIFace());
        assembler.assembleEntity(instance, customer, null, dtoFactory);
        customer = ((CustomerService)service).create(customer, shopDao.findById(shopId));
        return getById(customer.getCustomerId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CustomerDTO create(final CustomerDTO instance) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        throw new UnsupportedOperationException("Customers must have at least one shop assigned use #createForShop()");
    }



    /**
     * {@inheritDoc}
     */
    @Override
    public Class<CustomerDTO> getDtoIFace() {
        return CustomerDTO.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<CustomerDTOImpl> getDtoImpl() {
        return CustomerDTOImpl.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<Customer> getEntityIFace() {
        return Customer.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<? extends AttrValueDTO> getEntityAttributes(final long entityPk)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final List<AttrValueCustomerDTO> result = new ArrayList<>();
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
                } else if ("companyname1".equals(attrValueCategoryDTO.getAttributeDTO().getCode())) {
                    attrValueCategoryDTO.setVal(customerDTO.getCompanyName1());
                } else if ("companyname2".equals(attrValueCategoryDTO.getAttributeDTO().getCode())) {
                    attrValueCategoryDTO.setVal(customerDTO.getCompanyName2());
                } else if ("companydepartment".equals(attrValueCategoryDTO.getAttributeDTO().getCode())) {
                    attrValueCategoryDTO.setVal(customerDTO.getCompanyDepartment());
                }
                result.add(attrValueCategoryDTO);
            }
            result.sort(ATTR_VALUE_DTO_COMPARATOR);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AttrValueDTO updateEntityAttributeValue(final AttrValueDTO attrValueDTO) {
        AttrValueEntityCustomer attrValueCustomer = attrValueEntityCustomerDao.findById(attrValueDTO.getAttrvalueId());
        attrValueAssembler.assembleEntity(attrValueDTO, attrValueCustomer, getAdaptersRepository(), dtoFactory);
        attrValueEntityCustomerDao.update(attrValueCustomer);
        return attrValueDTO;
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
        } else if ("pricingpolicy".equals(atr.getCode())) {
            customer.setPricingPolicy(attrValueDTO.getVal());
        } else if ("companyname1".equals(atr.getCode())) {
            customer.setCompanyName1(attrValueDTO.getVal());
        } else if ("companyname2".equals(atr.getCode())) {
            customer.setCompanyName2(attrValueDTO.getVal());
        } else if ("companydepartment".equals(atr.getCode())) {
            customer.setCompanyDepartment(attrValueDTO.getVal());
        } else {
            if (!multivalue) {
                for (final AttrValueCustomer avp : customer.getAttributes()) {
                    if (avp.getAttributeCode().equals(atr.getCode())) {
                        // this is a duplicate, so need to update
                        attrValueDTO.setAttrvalueId(avp.getAttrvalueId());
                        return updateEntityAttributeValue(attrValueDTO);
                    }
                }
            }


            AttrValueCustomer valueEntityCustomer = getPersistenceEntityFactory().getByIface(AttrValueCustomer.class);
            attrValueAssembler.assembleEntity(attrValueDTO, valueEntityCustomer, getAdaptersRepository(), dtoFactory);
            valueEntityCustomer.setAttributeCode(atr.getCode());
            valueEntityCustomer.setCustomer(customer);
            valueEntityCustomer = attrValueEntityCustomerDao.create((AttrValueEntityCustomer) valueEntityCustomer);
            attrValueDTO.setAttrvalueId(valueEntityCustomer.getAttrvalueId());
        }
        return attrValueDTO;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long deleteAttributeValue(final long attributeValuePk) {
        final AttrValueEntityCustomer valueEntityCustomer = attrValueEntityCustomerDao.findById(attributeValuePk);
        attrValueEntityCustomerDao.delete(valueEntityCustomer);
        return valueEntityCustomer.getCustomer().getCustomerId();
    }



    /**
     * {@inheritDoc}
     */
    @Override
    public List<CustomerDTO> findCustomer(final String email) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<Customer> entities = service.findByCriteria(
                " where lower(e.email) = ?1",
                email.toLowerCase()
        );
        final List<CustomerDTO> dtos  = new ArrayList<>(entities.size());
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
    @Override
    public SearchResult<CustomerDTO> findCustomer(final Set<Long> shopIds,
                                                  final SearchContext filter) throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final Map<String, List> params = filter.reduceParameters("filter");
        final List filterParam = params.get("filter");

        final int pageSize = filter.getSize();
        final int startIndex = filter.getStart() * pageSize;

        final Map<String, List> currentFilter = new HashMap<>();

        if (CollectionUtils.isNotEmpty(filterParam) && filterParam.get(0) instanceof String && StringUtils.isNotBlank((String) filterParam.get(0))) {

            final String textFilter = (String) filterParam.get(0);
            final Pair<String, String> refOrCustomerOrAddressOrPolicy = ComplexSearchUtils.checkSpecialSearch(textFilter, REF_OR_CUSTOMER_OR_ADDRESS);
            final Pair<LocalDateTime, LocalDateTime> dateSearch = refOrCustomerOrAddressOrPolicy == null ? ComplexSearchUtils.checkDateRangeSearch(textFilter) : null;

            if (refOrCustomerOrAddressOrPolicy != null) {

                if ("#".equals(refOrCustomerOrAddressOrPolicy.getFirst())) {
                    // order number search
                    final String refNumber = refOrCustomerOrAddressOrPolicy.getSecond();

                    SearchContext.JoinMode.OR.setMode(currentFilter);
                    currentFilter.put("customerId", Collections.singletonList(NumberUtils.toLong(refNumber)));
                    currentFilter.put("email", Collections.singletonList(refNumber));
                    currentFilter.put("tag", Collections.singletonList(refNumber));
                    currentFilter.put("companyName1", Collections.singletonList(refNumber));
                    currentFilter.put("companyName2", Collections.singletonList(refNumber));

                } else if ("?".equals(refOrCustomerOrAddressOrPolicy.getFirst())) {
                    // customer search
                    final String customer = refOrCustomerOrAddressOrPolicy.getSecond();

                    SearchContext.JoinMode.OR.setMode(currentFilter);
                    currentFilter.put("email", Collections.singletonList(customer));
                    currentFilter.put("firstname", Collections.singletonList(customer));
                    currentFilter.put("lastname", Collections.singletonList(customer));

                } else if ("@".equals(refOrCustomerOrAddressOrPolicy.getFirst())) {
                    // address search
                    final String address = refOrCustomerOrAddressOrPolicy.getSecond();
                    final List<Long> ids = (List) this.addressDao.
                            findQueryObjectsRangeByNamedQuery(
                                    "CUSTOMER.IDS.BY.ADDRESS",
                                    startIndex, pageSize,
                                    HQLUtils.criteriaIlikeAnywhere(address)
                            );

                    if (ids.isEmpty()) {
                        return new SearchResult<>(filter, Collections.emptyList(), 0);
                    }

                    currentFilter.put("customerId", Collections.singletonList(SearchContext.MatchMode.ANY.toParam(ids)));

                } else if ("$".equals(refOrCustomerOrAddressOrPolicy.getFirst())) {
                    // address search
                    final String policy = refOrCustomerOrAddressOrPolicy.getSecond();

                    SearchContext.JoinMode.OR.setMode(currentFilter);
                    currentFilter.put("customerType", Collections.singletonList(policy));
                    currentFilter.put("pricingPolicy", Collections.singletonList(policy));

                }

            } else if (dateSearch != null) {

                final Instant from = DateUtils.iFrom(dateSearch.getFirst());
                final Instant to = DateUtils.iFrom(dateSearch.getSecond());

                final List range = new ArrayList(2);
                if (from != null) {
                    range.add(SearchContext.MatchMode.GT.toParam(from));
                }
                if (to != null) {
                    range.add(SearchContext.MatchMode.LE.toParam(to));
                }

                currentFilter.put("createdTimestamp", range);

            } else {

                final String basic = textFilter;

                SearchContext.JoinMode.OR.setMode(currentFilter);
                currentFilter.put("email", Collections.singletonList(basic));
                currentFilter.put("firstname", Collections.singletonList(basic));
                currentFilter.put("lastname", Collections.singletonList(basic));
                currentFilter.put("customerType", Collections.singletonList(basic));
                currentFilter.put("pricingPolicy", Collections.singletonList(basic));

            }

        }

        final CustomerService customerService = (CustomerService) service;

        // See all customers assigned
        currentFilter.put("disabled", Collections.singletonList(SearchContext.MatchMode.ANY));

        final int count = customerService.findCustomerCount(shopIds, currentFilter);
        if (count > startIndex) {

            final List<CustomerDTO> entities = new ArrayList<>();
            final List<Customer> customers = customerService.findCustomer(startIndex, pageSize, filter.getSortBy(), filter.isSortDesc(), shopIds, currentFilter);

            fillDTOs(customers, entities);

            return new SearchResult<>(filter, entities, count);

        }
        return new SearchResult<>(filter, Collections.emptyList(), count);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void resetPassword(final CustomerDTO customer, final long shopId) {
        final Customer cust = service.findById(customer.getCustomerId());
        if (cust != null) {
            final Shop shop = shopDao.findById(shopId);
            final String authToken = (shop.getMaster() != null ? shop.getMaster() : shop).getAttributeValueByCode(AttributeNamesKeys.Shop.SHOP_CUSTOMER_PASSWORD_RESET_CC);
            ((CustomerService)service).resetPassword(cust, shop, authToken);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(final long id) {
        final Customer cust = service.findById(id);
        if (cust != null) {
            this.customerRemoveService.deleteAccount(cust);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateCustomerTags(final CustomerDTO customer, final String tags) {
        final Customer cust = service.findById(customer.getCustomerId());
        if (cust != null) {
            if (StringUtils.isBlank(tags)) {
                cust.setTag(null);
            } else {
                final Set<String> unique = new TreeSet<>();
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
    @Override
    public AttrValueDTO createAndBindAttrVal(final long entityPk, final String attrName, final String attrValue)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        throw new UnmappedInterfaceException("Not implemented");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<ShopDTO, Boolean> getAssignedShop(final long customerId) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final Collection<CustomerShop> assigned = getService().findById(customerId).getShops();
        final Map<Long, Boolean> enabledMap = new HashMap<>(assigned.size() * 2);
        for (final CustomerShop shop : assigned) {
            enabledMap.put(shop.getShop().getShopId(), shop.isDisabled());
        }
        final List<ShopDTO> shopDTOs = new ArrayList<>(assigned.size());
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
    @Override
    public List<ShopDTO> getAvailableShop(final long customerId) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<Shop> all = shopDao.findAll();
        final Set<ShopDTO> assigned = getAssignedShop(customerId).keySet();
        final List<ShopDTO> available = new ArrayList<>(all.size());
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
    @Override
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
    @Override
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
    @Override
    public AttrValueDTO getNewAttribute(final long entityPk) throws UnableToCreateInstanceException, UnmappedInterfaceException {
        final AttrValueCustomerDTO dto = new AttrValueCustomerDTOImpl();
        dto.setCustomerId(entityPk);
        return dto;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getImageRepositoryUrlPattern() {
        return Constants.CUSTOMER_IMAGE_REPOSITORY_URL_PATTERN;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFileRepositoryUrlPattern() {
        return Constants.CUSTOMER_FILE_REPOSITORY_URL_PATTERN;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSysFileRepositoryUrlPattern() {
        return Constants.CUSTOMER_SYSFILE_REPOSITORY_URL_PATTERN;
    }

}
