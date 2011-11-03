package org.yes.cart.service.dto.impl;

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.dto.ProductAssociationDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.domain.impl.BaseCoreDBTestCase;
import org.yes.cart.service.dto.DtoAssociationService;
import org.yes.cart.service.dto.DtoProductAssociationService;
import org.yes.cart.service.dto.DtoProductService;

import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DtoProductAssociationServiceImplTest extends BaseCoreDBTestCase {

    private DtoFactory dtoFactory = null;
    private DtoProductAssociationService dtoProductAssociationService = null;
    private DtoAssociationService dtoAssociationService = null;
    private DtoProductService dtoProductService = null;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        dtoProductAssociationService = (DtoProductAssociationService) ctx.getBean(ServiceSpringKeys.DTO_PRODUCT_ASSOCIATION_SERVICE);
        dtoAssociationService = (DtoAssociationService) ctx.getBean(ServiceSpringKeys.DTO_ASSOCIATION_SERVICE);
        dtoFactory = (DtoFactory) ctx.getBean(ServiceSpringKeys.DTO_FACTORY);
        dtoProductService = (DtoProductService) ctx.getBean(ServiceSpringKeys.DTO_PRODUCT_SERVICE);
    }

    @Test
    public void testUpdate() {
        try {
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

        } catch (Exception e) {
            assertTrue(e.getMessage(), false);
        }
    }


    @Test
    public void testGetProductAssociations() {
        try {
            ProductAssociationDTO dto = getDto();
            dto = dtoProductAssociationService.create(dto);
            assertTrue(dto.getProductassociationId() > 0);


            List<ProductAssociationDTO> list = dtoProductAssociationService.getProductAssociations(11002L);
            assertEquals(1, list.size());

            list = dtoProductAssociationService.getProductAssociations(11004L);
            assertEquals(5, list.size());

            dtoProductAssociationService.remove(dto.getProductassociationId());


        } catch (Exception e) {
            assertTrue(e.getMessage(), false);
        }
    }

    @Test
    public void testGetProductAssociationsByProductAssociationType() {
        try {
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

        } catch (Exception e) {
            assertTrue(e.getMessage(), false);
        }
    }


    private ProductAssociationDTO getDto() throws UnmappedInterfaceException, UnableToCreateInstanceException {
        ProductAssociationDTO dto = dtoFactory.getByIface(ProductAssociationDTO.class);
        dto.setAssociationId(dtoAssociationService.getById(3L).getAssociationId());
        dto.setProductId(11002L);
        dto.setAssociatedProductId(11003L);
        return dto;
    }

}
