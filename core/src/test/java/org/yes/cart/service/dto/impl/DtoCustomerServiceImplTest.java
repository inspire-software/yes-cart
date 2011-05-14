package org.yes.cart.service.dto.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.dto.AttrValueCustomerDTO;
import org.yes.cart.domain.dto.AttrValueDTO;
import org.yes.cart.domain.dto.AttributeDTO;
import org.yes.cart.domain.dto.CustomerDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.domain.impl.BaseCoreDBTestCase;
import org.yes.cart.service.dto.DtoAttributeService;
import org.yes.cart.service.dto.DtoCustomerService;

import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DtoCustomerServiceImplTest  extends BaseCoreDBTestCase {

    private DtoFactory dtoFactory = null;
    private DtoCustomerService dtoService = null;
    private DtoAttributeService dtoAttrService = null;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        dtoFactory = (DtoFactory) ctx.getBean(ServiceSpringKeys.DTO_FACTORY);
        dtoService = (DtoCustomerService) ctx.getBean(ServiceSpringKeys.DTO_CUSTOMER_SERVICE);
        dtoAttrService  = (DtoAttributeService) ctx.getBean(ServiceSpringKeys.DTO_ATTRIBUTE_SERVICE);


    }

    @After
    public void tearDown() {
        dtoFactory = null;
        dtoService = null;
        dtoAttrService = null;
        super.tearDown();
    }


    @Test
    public void testCreate() throws UnmappedInterfaceException, UnableToCreateInstanceException {
        CustomerDTO dto = getCustomerDto("testCreate");
        dto = dtoService.create(dto);
        assertTrue(dto.getCustomerId() > 0);
        dtoService.remove(dto.getCustomerId());
    }


    @Test
    public void testUpdate() throws UnmappedInterfaceException, UnableToCreateInstanceException {
        CustomerDTO dto = getCustomerDto("testUpdate");
        dto = dtoService.create(dto);
        assertTrue(dto.getCustomerId() > 0);
        dto.setFirstname("Jane");
        dto.setLastname("Gav");
        dto = dtoService.update(dto);
        assertEquals("Jane", dto.getFirstname());
        assertEquals("Gav", dto.getLastname());
        dtoService.remove(dto.getCustomerId());
    }

    public void testCreateEntityAttributeValue()  throws UnmappedInterfaceException, UnableToCreateInstanceException {
        CustomerDTO dto = getCustomerDto("testCreateEntityAttributeValue");
        dto = dtoService.create(dto);
        assertTrue(dto.getCustomerId() > 0);

        AttributeDTO attrDto = dtoAttrService.getById(1030); //CUSTOMER_PHONE

        AttrValueCustomerDTO attrValueDTO = dtoFactory.getByIface(AttrValueCustomerDTO.class);
        attrValueDTO.setCustomerId(dto.getCustomerId());
        attrValueDTO.setVal("+380978159999");
        attrValueDTO.setAttributeDTO(attrDto);

        dtoService.createEntityAttributeValue(attrValueDTO);

        dto = dtoService.getById(dto.getCustomerId());
        assertFalse(dto.getAttribute().isEmpty());
        assertEquals("+380978159999", dto.getAttribute().iterator().next().getVal());

    }


    public void testGetEntityAttributes()   throws UnmappedInterfaceException, UnableToCreateInstanceException {
        dumpDataBase("ffffffff" , new String [] {"TCUSTOMER"});
        CustomerDTO dto = getCustomerDto("testGetEntityAttributes");
        dto = dtoService.create(dto);
        assertTrue(dto.getCustomerId() > 0);

        AttributeDTO attrDto = dtoAttrService.getById(1030); //CUSTOMER_PHONE

        AttrValueCustomerDTO attrValueDTO = dtoFactory.getByIface(AttrValueCustomerDTO.class);
        attrValueDTO.setCustomerId(dto.getCustomerId());
        attrValueDTO.setVal("+380978159999");
        attrValueDTO.setAttributeDTO(attrDto);

        dtoService.createEntityAttributeValue(attrValueDTO);

        List<? extends AttrValueDTO> list = dtoService.getEntityAttributes(dto.getCustomerId());
        assertFalse(list.isEmpty());
        assertEquals("+380978159999", list.get(0).getVal());



    }

    public void testUpdateEntityAttributeValue()  throws UnmappedInterfaceException, UnableToCreateInstanceException {
        CustomerDTO dto = getCustomerDto("testUpdateEntityAttributeValue");
        dto = dtoService.create(dto);
        assertTrue(dto.getCustomerId() > 0);

        AttributeDTO attrDto = dtoAttrService.getById(1030); //CUSTOMER_PHONE

        AttrValueCustomerDTO attrValueDTO = dtoFactory.getByIface(AttrValueCustomerDTO.class);
        attrValueDTO.setCustomerId(dto.getCustomerId());
        attrValueDTO.setVal("+380978159999");
        attrValueDTO.setAttributeDTO(attrDto);

        dtoService.createEntityAttributeValue(attrValueDTO);

        List<? extends AttrValueDTO> list = dtoService.getEntityAttributes(dto.getCustomerId());
        assertFalse(list.isEmpty());
        assertEquals("+380978159999", list.get(0).getVal());

        AttrValueCustomerDTO aDto = (AttrValueCustomerDTO) list.get(0);
        aDto.setVal("+44555123456");
        aDto = (AttrValueCustomerDTO) dtoService.updateEntityAttributeValue(aDto);
        assertEquals("+44555123456", aDto.getVal());



    }


    public void testDeleteAttributeValue() throws UnmappedInterfaceException, UnableToCreateInstanceException {
        CustomerDTO dto = getCustomerDto("testDeleteAttributeValue");
        dto = dtoService.create(dto);
        assertTrue(dto.getCustomerId() > 0);

        AttributeDTO attrDto = dtoAttrService.getById(1030); //CUSTOMER_PHONE

        AttrValueCustomerDTO attrValueDTO = dtoFactory.getByIface(AttrValueCustomerDTO.class);
        attrValueDTO.setCustomerId(dto.getCustomerId());
        attrValueDTO.setVal("+380978159999");
        attrValueDTO.setAttributeDTO(attrDto);

        dtoService.createEntityAttributeValue(attrValueDTO);

        dto = dtoService.getById(dto.getCustomerId());
        assertFalse(dto.getAttribute().isEmpty());
        assertEquals("+380978159999", dto.getAttribute().iterator().next().getVal());



        dtoService.deleteAttributeValue(dto.getAttribute().iterator().next().getAttrvalueId());
        dto = dtoService.getById(dto.getCustomerId());
        assertTrue(dto.getAttribute().isEmpty());

    }


    private CustomerDTO getCustomerDto(final String prefix) {
        CustomerDTO dto = dtoFactory.getByIface(CustomerDTO.class);
        dto.setEmail(prefix + "john@doe.com");
        dto.setFirstname(prefix + "John");
        dto.setLastname(prefix + "Doe");
        return dto;
    }
    


}
