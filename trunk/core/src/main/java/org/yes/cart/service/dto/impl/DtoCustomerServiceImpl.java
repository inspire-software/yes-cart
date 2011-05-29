package org.yes.cart.service.dto.impl;

import dp.lib.dto.geda.adapter.repository.ValueConverterRepository;
import dp.lib.dto.geda.assembler.DTOAssembler;
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
import org.yes.cart.service.dto.DtoAttributeService;
import org.yes.cart.service.dto.DtoCustomerService;
import org.yes.cart.utils.impl.AttrValueDTOComparatorImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    private final DTOAssembler attrValueAssembler;

    /**
     * Construct base remote service.
     *
     * @param dtoFactory               {@link org.yes.cart.domain.dto.factory.DtoFactory}
     * @param customerGenericService   {@link org.yes.cart.service.domain.GenericService}
     * @param valueConverterRepository {@link dp.lib.dto.geda.adapter.repository.ValueConverterRepository}
     * @param dtoAttributeService      {@link DtoAttributeService}
     * @param attrValueEntityCustomerDao       link to customer attribute values dao
     * @param shopDao shop dao
     */
    public DtoCustomerServiceImpl(
            final DtoFactory dtoFactory,
            final GenericService<Customer> customerGenericService,
            final ValueConverterRepository valueConverterRepository,
            final DtoAttributeService dtoAttributeService,
            final GenericDAO<AttrValueEntityCustomer, Long> attrValueEntityCustomerDao,
            final GenericDAO<Shop, Long> shopDao) {

        super(dtoFactory, customerGenericService, valueConverterRepository);

        this.dtoAttributeService = dtoAttributeService;

        this.attrValueEntityCustomerDao = attrValueEntityCustomerDao;


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
        result.addAll(getById(entityPk).getAttribute());
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
        attrValueAssembler.assembleEntity(attrValueDTO, attrValueCustomer, null, dtoFactory);
        attrValueEntityCustomerDao.update(attrValueCustomer);
        return attrValueDTO;
    }

    /**
     * {@inheritDoc}
     */
    public AttrValueDTO createEntityAttributeValue(final AttrValueDTO attrValueDTO) {
        AttrValueCustomer valueEntityCustomer = getEntityFactory().getByIface(AttrValueCustomer.class);
        attrValueAssembler.assembleEntity(attrValueDTO, valueEntityCustomer, null, dtoFactory);
        Attribute atr =((AttributeService)dtoAttributeService.getService()).getById(attrValueDTO.getAttributeDTO().getAttributeId());
        valueEntityCustomer.setAttribute(atr);
        valueEntityCustomer.setCustomer(service.getById(((AttrValueCustomerDTO) attrValueDTO).getCustomerId()));
        valueEntityCustomer = attrValueEntityCustomerDao.create((AttrValueEntityCustomer) valueEntityCustomer);
        attrValueDTO.setAttrvalueId(valueEntityCustomer.getAttrvalueId());
        return attrValueDTO;

    }

    /**
     * {@inheritDoc}
     */
    public void deleteAttributeValue(final long attributeValuePk) {
        final AttrValueEntityCustomer valueEntityCategory = attrValueEntityCustomerDao.findById(attributeValuePk);
        attrValueEntityCustomerDao.delete(valueEntityCategory);
    }


    /**
     * {@inheritDoc}
     */
    public List<CustomerDTO> findCustomer(
            final String email,
            final String firstname,
            final String lastname,
            final String middlename) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<Customer> entities = ((CustomerService)service).findCustomer(email, firstname, lastname, middlename);
        final List<CustomerDTO> dtos  = new ArrayList<CustomerDTO>(entities.size());
        fillDTOs(entities, dtos);
        return dtos;
    }

    /**
     * {@inheritDoc}
     */
    public void remoteResetPassword(final CustomerDTO customer, final long shopId) {
        final Customer cust = service.getById(customer.getCustomerId());
        if (cust != null) {
            final Shop shop = shopDao.findById(shopId);
            ((CustomerService)service).resetPassword(cust, shop);
        }
    }

}
