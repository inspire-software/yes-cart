package org.yes.cart.service.dto.impl;

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.dto.ProductAssociationDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.service.dto.DtoAssociationService;
import org.yes.cart.service.dto.DtoProductAssociationService;

import java.util.List;

import static org.junit.Assert.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DtoProductAssociationServiceImplTest extends BaseCoreDBTestCase {

    private DtoFactory dtoFactory;
    private DtoProductAssociationService dtoProductAssociationService;
    private DtoAssociationService dtoAssociationService;

    @Before
    public void setUp() throws Exception {
        dtoProductAssociationService = (DtoProductAssociationService) ctx.getBean(ServiceSpringKeys.DTO_PRODUCT_ASSOCIATION_SERVICE);
        dtoAssociationService = (DtoAssociationService) ctx.getBean(ServiceSpringKeys.DTO_ASSOCIATION_SERVICE);
        dtoFactory = (DtoFactory) ctx.getBean(ServiceSpringKeys.DTO_FACTORY);
    }

    @Test
    public void testUpdate() throws Exception {
        ProductAssociationDTO dto = getDto();
        dto = dtoProductAssociationService.create(dto);
        assertTrue(dto.getProductassociationId() > 0);
        assertEquals(0, dto.getRank());
        dto.setRank(11324);
        dto = dtoProductAssociationService.update(dto);
        assertEquals(11324, dto.getRank());
        long pk = dto.getProductassociationId();
        dtoProductAssociationService.remove(pk);
        dto = dtoProductAssociationService.getById(pk);
        assertNull(dto);
    }

    @Test
    public void testGetProductAssociations() throws Exception {
        ProductAssociationDTO dto = getDto();
        dto = dtoProductAssociationService.create(dto);
        assertTrue(dto.getProductassociationId() > 0);
        List<ProductAssociationDTO> list = dtoProductAssociationService.getProductAssociations(11002L);
        assertEquals(1, list.size());
        list = dtoProductAssociationService.getProductAssociations(11004L);
        assertEquals(5, list.size());
        dtoProductAssociationService.remove(dto.getProductassociationId());
    }

    @Test
    public void testGetProductAssociationsByProductAssociationType() throws Exception {
        ProductAssociationDTO dto = getDto();
        dto = dtoProductAssociationService.create(dto);
        assertTrue(dto.getProductassociationId() > 0);
        long pk = dto.getProductassociationId();
        List<ProductAssociationDTO> list = dtoProductAssociationService.getProductAssociationsByProductAssociationType(11002L, "accessories");
        assertEquals(0, list.size());
        list = dtoProductAssociationService.getProductAssociationsByProductAssociationType(11002L, "up");
        assertEquals(0, list.size());
        list = dtoProductAssociationService.getProductAssociationsByProductAssociationType(11002L, "cross");
        assertEquals(1, list.size());
        list = dtoProductAssociationService.getProductAssociationsByProductAssociationType(11002L, "buywiththis");
        assertEquals(0, list.size());
    }

    private ProductAssociationDTO getDto() throws Exception {
        ProductAssociationDTO dto = dtoFactory.getByIface(ProductAssociationDTO.class);
        dto.setAssociationId(dtoAssociationService.getById(3L).getAssociationId());
        dto.setProductId(11002L);
        dto.setAssociatedProductId(11003L);
        return dto;
    }
}
