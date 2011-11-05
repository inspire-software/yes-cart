package org.yes.cart.service.dto.impl;

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.dto.EtypeDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.service.dto.DtoEtypeService;

import java.util.List;

import static org.junit.Assert.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DtoEtypeServiceImplTest extends BaseCoreDBTestCase {

    private DtoEtypeService dtoEtypeService;
    private DtoFactory dtoFactory;

    @Before
    public void setUp() throws Exception {
        dtoEtypeService = (DtoEtypeService) ctx.getBean(ServiceSpringKeys.DTO_ETYPE_SERVICE);
        dtoFactory = (DtoFactory) ctx.getBean(ServiceSpringKeys.DTO_FACTORY);
    }

    @Test
    public void testCreate() throws Exception {
        EtypeDTO etypeDTO = getDto();
        assertEquals(0, etypeDTO.getEtypeId());
        etypeDTO = dtoEtypeService.create(etypeDTO);
        assertTrue(etypeDTO.getEtypeId() > 0);
    }

    @Test
    public void testUpdate() throws Exception {
        EtypeDTO etypeDTO = getDto();
        assertEquals(0, etypeDTO.getEtypeId());
        etypeDTO = dtoEtypeService.create(etypeDTO);
        long id = etypeDTO.getEtypeId();
        assertTrue(etypeDTO.getEtypeId() > 0);
        etypeDTO.setJavatype("java.math.BigDecimal");
        dtoEtypeService.update(etypeDTO);
        etypeDTO = dtoEtypeService.getById(id);
        assertEquals("java.math.BigDecimal", etypeDTO.getJavatype());
    }

    @Test
    public void testGetAll() throws Exception {
        List<EtypeDTO> list = dtoEtypeService.getAll();
        assertFalse(list.isEmpty());
    }

    @Test
    public void testRemove() throws Exception {
        EtypeDTO etypeDTO = getDto();
        assertEquals(0, etypeDTO.getEtypeId());
        etypeDTO = dtoEtypeService.create(etypeDTO);
        long id = etypeDTO.getEtypeId();
        assertTrue(etypeDTO.getEtypeId() > 0);
        dtoEtypeService.remove(id);
        etypeDTO = dtoEtypeService.getById(id);
        assertNull(etypeDTO);
    }

    private EtypeDTO getDto() {
        EtypeDTO etypeDTO = dtoFactory.getByIface(EtypeDTO.class);
        etypeDTO.setBusinesstype("DISCOUNT");
        etypeDTO.setJavatype("java.lang.Float");
        return etypeDTO;
    }
}
