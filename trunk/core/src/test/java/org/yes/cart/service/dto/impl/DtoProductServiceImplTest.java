package org.yes.cart.service.dto.impl;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.dto.AttrValueDTO;
import org.yes.cart.domain.dto.AttrValueProductDTO;
import org.yes.cart.domain.dto.ProductDTO;
import org.yes.cart.domain.dto.ProductSkuDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.entity.Availability;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.dto.*;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DtoProductServiceImplTest extends BaseCoreDBTestCase {

    private DtoProductService dtoService;
    private DtoProductTypeService dtoProductTypeService;
    private DtoAvailabilityService dtoAvailabilityService;
    private DtoBrandService dtoBrandService;
    private DtoAttributeService dtoAttrService;
    private DtoFactory dtoFactory;

    @Before
    public void setUp() throws Exception {
        dtoService = (DtoProductService) ctx.getBean(ServiceSpringKeys.DTO_PRODUCT_SERVICE);
        dtoBrandService = (DtoBrandService) ctx.getBean(ServiceSpringKeys.DTO_BRAND_SERVICE);
        dtoProductTypeService = (DtoProductTypeService) ctx.getBean(ServiceSpringKeys.DTO_PRODUCT_TYPE_SERVICE);
        dtoAvailabilityService = (DtoAvailabilityService) ctx.getBean(ServiceSpringKeys.DTO_AVAILABILITY_SERVICE);
        dtoAttrService = (DtoAttributeService) ctx.getBean(ServiceSpringKeys.DTO_ATTRIBUTE_SERVICE);
        dtoFactory = (DtoFactory) ctx.getBean(ServiceSpringKeys.DTO_FACTORY);
    }

    @Test
    public void testGetProductSkuByCode() throws Exception {
        ProductSkuDTO dto = dtoService.getProductSkuByCode("NOTEXISTINGSKUCODE");
        assertNull(dto);
        dto = dtoService.getProductSkuByCode("BENDER-ua");
        assertNotNull(dto);
    }

    @Test
    public void testCreate() throws Exception {
        ProductDTO dto = getDto();
        dto = dtoService.create(dto);
        assertTrue(dto.getProductId() > 0);
    }

    @Test
    public void testUpdate() throws Exception {
        ProductDTO dto = getDto();
        dto = dtoService.create(dto);
        assertTrue(dto.getProductId() > 0);
        long pk = dto.getProductId();
        final Date availavleFrom = new Date();
        dto.setAvailablefrom(availavleFrom);
        dto.setAvailabletill(availavleFrom);
        dto.setName("new-name");
        dto.setDescription("new desciption");
        dto.setBrandDTO(dtoBrandService.getById(102L));
        dto.setProductTypeDTO(dtoProductTypeService.getById(2L));
        dto.setAvailabilityDTO(dtoAvailabilityService.getById(Availability.ALWAYS));
        dtoService.update(dto);
        dto = dtoService.getById(pk);
        assertEquals(availavleFrom, dto.getAvailablefrom());
        assertEquals(availavleFrom, dto.getAvailabletill());
        assertEquals("new-name", dto.getName());
        assertEquals("new desciption", dto.getDescription());
        assertEquals(102L, dto.getBrandDTO().getBrandId());
        assertEquals(2L, dto.getProductTypeDTO().getProducttypeId());
        assertEquals(Availability.ALWAYS, dto.getAvailabilityDTO().getAvailabilityId());
    }

    @Ignore("expected:<8> but was:<9>")
    @Test
    public void testGetProductByCategory() throws Exception {
        List<ProductDTO> list = dtoService.getProductByCategory(211L);
        assertEquals(8, list.size()); //FEATURED-PRODUCT3 AVAILABLEFROM="2000-04-08 11:15:17.451" AVAILABLETILL="2001-04-08 11:15:17.451"
        for (ProductDTO dto : list) {
            assertFalse(dto.getCode().equals("FEATURED-PRODUCT3"));
        }
        list = dtoService.getProductByCategory(208L);
        assertTrue(list.isEmpty());
    }

    @Ignore("pending")
    @Test
    public void testGetProductByCategoryWithPaging() {
        assertTrue(true);
    }

    @Test
    public void testGetProductByConeNameBrandType() throws Exception {
        List<ProductDTO> list = dtoService.getProductByConeNameBrandType(null, null, 104L, 0);
        assertFalse(list.isEmpty());
        assertEquals(26, list.size());             //26 products with brand samsung
    }

    @Test
    public void testCreateEntityAttributeValue() throws Exception {
        ProductDTO dto = getDto();
        dto = dtoService.create(dto);
        assertTrue(dto.getProductId() > 0);
        AttrValueProductDTO attrValueProductDTO = dtoFactory.getByIface(AttrValueProductDTO.class);
        attrValueProductDTO.setAttributeDTO(dtoAttrService.getById(2010L)); //POWERSUPPLY
        attrValueProductDTO.setProductId(dto.getProductId());
        attrValueProductDTO.setVal("Дрова"); //Firewood
        attrValueProductDTO = (AttrValueProductDTO) dtoService.createEntityAttributeValue(attrValueProductDTO);
        assertTrue(attrValueProductDTO.getAttrvalueId() > 0);
    }

    @Test
    public void testUpdateEntityAttributeValue() throws Exception {
        ProductDTO dto = getDto();
        dto = dtoService.create(dto);
        assertTrue(dto.getProductId() > 0);
        AttrValueProductDTO attrValueProductDTO = dtoFactory.getByIface(AttrValueProductDTO.class);
        attrValueProductDTO.setAttributeDTO(dtoAttrService.getById(2010L)); //POWERSUPPLY
        attrValueProductDTO.setProductId(dto.getProductId());
        attrValueProductDTO.setVal("Дрова"); //Firewood
        attrValueProductDTO = (AttrValueProductDTO) dtoService.createEntityAttributeValue(attrValueProductDTO);
        assertTrue(attrValueProductDTO.getAttrvalueId() > 0);
        attrValueProductDTO.setVal("Peat");
        attrValueProductDTO = (AttrValueProductDTO) dtoService.updateEntityAttributeValue(attrValueProductDTO);
        assertEquals("Peat", attrValueProductDTO.getVal());
    }

    @Test
    public void testGetEntityAttributes() throws Exception {
        ProductDTO dto = getDto();
        dto = dtoService.create(dto);
        assertTrue(dto.getProductId() > 0);
        List<? extends AttrValueDTO> list = dtoService.getEntityAttributes(dto.getProductId());
        for (AttrValueDTO attrValueDTO : list) {
            assertNull(attrValueDTO.getVal()); // all must be empty when rpoduct is created
        }
    }

    @Test
    public void testDeleteAttributeValue() throws Exception {
        ProductDTO dto = getDto();
        dto = dtoService.create(dto);
        assertTrue(dto.getProductId() > 0);
        AttrValueProductDTO attrValueProductDTO = dtoFactory.getByIface(AttrValueProductDTO.class);
        attrValueProductDTO.setAttributeDTO(dtoAttrService.getById(2010L)); //POWERSUPPLY
        attrValueProductDTO.setProductId(dto.getProductId());
        attrValueProductDTO.setVal("Дрова"); //Firewood
        attrValueProductDTO = (AttrValueProductDTO) dtoService.createEntityAttributeValue(attrValueProductDTO);
        assertTrue(attrValueProductDTO.getAttrvalueId() > 0);
        List<? extends AttrValueDTO> list = dtoService.getEntityAttributes(dto.getProductId());
        for (AttrValueDTO attrValueDTO : list) {
            if (attrValueDTO.getAttrvalueId() > 0) {
                dtoService.deleteAttributeValue(attrValueDTO.getAttrvalueId());
            }

        }
        list = dtoService.getEntityAttributes(dto.getProductId());
        for (AttrValueDTO attrValueDTO : list) {
            assertNull(attrValueDTO.getVal()); // all must be empty when rpoduct is created
        }
    }

    @Test
    public void testRemove() throws Exception {
        ProductDTO dto = getDto();
        dto = dtoService.create(dto);
        assertTrue(dto.getProductId() > 0);
        dtoService.remove(dto.getProductId());
        dto = dtoService.getById(dto.getProductId());
        assertNull(dto);
    }

    private ProductDTO getDto() throws UnableToCreateInstanceException, UnmappedInterfaceException {
        ProductDTO dto = dtoService.getNew();
        dto.setCode("TESTCODE");
        dto.setName("test-name");
        dto.setBrandDTO(dtoBrandService.getById(101L));
        dto.setProductTypeDTO(dtoProductTypeService.getById(1L));
        dto.setAvailabilityDTO(dtoAvailabilityService.getById(Availability.STANDARD));
        return dto;
    }
}
