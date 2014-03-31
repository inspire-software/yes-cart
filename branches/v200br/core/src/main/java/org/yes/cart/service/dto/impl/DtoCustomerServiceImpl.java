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

package org.yes.cart.service.dto.impl;

import com.inspiresoftware.lib.dto.geda.adapter.repository.AdaptersRepository;
import com.inspiresoftware.lib.dto.geda.assembler.Assembler;
import com.inspiresoftware.lib.dto.geda.assembler.DTOAssembler;
import org.apache.commons.lang.StringUtils;
import org.yes.cart.constants.AttributeGroupNames;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.dto.AttrValueCustomerDTO;
import org.yes.cart.domain.dto.AttrValueDTO;
import org.yes.cart.domain.dto.AttributeDTO;
import org.yes.cart.domain.dto.CustomerDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.dto.impl.CustomerDTOImpl;
import org.yes.cart.domain.entity.AttrValueCustomer;
import org.yes.cart.domain.entity.Attribute;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.entity.impl.AttrValueEntityCustomer;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.domain.AttributeService;
import org.yes.cart.service.domain.CustomerService;
import org.yes.cart.service.domain.GenericService;
import org.yes.cart.service.domain.PassPhrazeGenerator;
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

    private final Assembler attrValueAssembler;

    private final PassPhrazeGenerator passPhrazeGenerator;

    /**
     * Construct base remote service.
     *
     * @param dtoFactory               {@link org.yes.cart.domain.dto.factory.DtoFactory}
     * @param customerGenericService   {@link org.yes.cart.service.domain.GenericService}
     * @param adaptersRepository {@link com.inspiresoftware.lib.dto.geda.adapter.repository.AdaptersRepository}
     * @param dtoAttributeService      {@link DtoAttributeService}
     * @param attrValueEntityCustomerDao       link to customer attribute values dao
     * @param shopDao shop dao
     * @param passPhrazeGenerator pass praze generator
     */
    public DtoCustomerServiceImpl(
            final DtoFactory dtoFactory,
            final GenericService<Customer> customerGenericService,
            final AdaptersRepository adaptersRepository,
            final DtoAttributeService dtoAttributeService,
            final GenericDAO<AttrValueEntityCustomer, Long> attrValueEntityCustomerDao,
            final GenericDAO<Shop, Long> shopDao,
            final PassPhrazeGenerator passPhrazeGenerator) {

        super(dtoFactory, customerGenericService, adaptersRepository);

        this.dtoAttributeService = dtoAttributeService;

        this.attrValueEntityCustomerDao = attrValueEntityCustomerDao;

        this.passPhrazeGenerator = passPhrazeGenerator;


        this.shopDao = shopDao;

        this.attrValueAssembler = DTOAssembler.newAssembler(
                dtoFactory.getImplClass(AttrValueCustomerDTO.class),
                service.getGenericDao().getEntityFactory().getImplClass(AttrValueCustomer.class));


    }


    /**
     * {@inheritDoc}
     */
    public CustomerDTO create(final CustomerDTO instance) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        Customer customer = getEntityFactory().getByIface(getEntityIFace());
        assembler.assembleEntity(instance, customer, null, dtoFactory);
        customer.setPassword(passPhrazeGenerator.getNextPassPhrase());
        customer = ((CustomerService)service).create(customer, null);
        return getById(customer.getCustomerId());
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
        result.addAll(getById(entityPk).getAttributes());
        final List<AttributeDTO> availableAttributeDTOs = dtoAttributeService.findAvailableAttributes(
                AttributeGroupNames.CUSTOMER,
                getCodes(result));
        for (AttributeDTO attributeDTO : availableAttributeDTOs) {
            AttrValueCustomerDTO attrValueCategoryDTO = getDtoFactory().getByIface(AttrValueCustomerDTO.class);
            attrValueCategoryDTO.setAttributeDTO(attributeDTO);
            attrValueCategoryDTO.setCustomerId(entityPk);
            result.add(attrValueCategoryDTO);
        }
        Collections.sort(result, new AttrValueDTOComparatorImpl());
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
        AttrValueCustomer valueEntityCustomer = getEntityFactory().getByIface(AttrValueCustomer.class);
        attrValueAssembler.assembleEntity(attrValueDTO, valueEntityCustomer, getAdaptersRepository(), dtoFactory);
        Attribute atr =((AttributeService)dtoAttributeService.getService()).findById(attrValueDTO.getAttributeDTO().getAttributeId());
        valueEntityCustomer.setAttribute(atr);
        valueEntityCustomer.setCustomer(service.findById(((AttrValueCustomerDTO) attrValueDTO).getCustomerId()));
        valueEntityCustomer = attrValueEntityCustomerDao.create((AttrValueEntityCustomer) valueEntityCustomer);
        attrValueDTO.setAttrvalueId(valueEntityCustomer.getAttrvalueId());
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
                                          final String tag) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<Customer> entities = ((CustomerService)service).findCustomer(email, firstname, lastname, middlename, tag);
        final List<CustomerDTO> dtos  = new ArrayList<CustomerDTO>(entities.size());
        fillDTOs(entities, dtos);
        return dtos;
    }

    /**
     * {@inheritDoc}
     */
    public void remoteResetPassword(final CustomerDTO customer, final long shopId) {
        final Customer cust = service.findById(customer.getCustomerId());
        if (cust != null) {
            final Shop shop = shopDao.findById(shopId);
            ((CustomerService)service).resetPassword(cust, shop);
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

}
