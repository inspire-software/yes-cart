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

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.dto.AttrValueDTO;
import org.yes.cart.domain.dto.AttrValueProductDTO;
import org.yes.cart.domain.dto.ProductDTO;
import org.yes.cart.domain.dto.ProductSkuDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.dto.impl.AttrValueProductDTOImpl;
import org.yes.cart.domain.entity.Product;
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
public class DtoProductServiceImplTezt extends BaseCoreDBTestCase {

    private DtoProductService dtoService;
    private DtoProductTypeService dtoProductTypeService;
    private DtoBrandService dtoBrandService;
    private DtoAttributeService dtoAttrService;
    private DtoFactory dtoFactory;
    private DtoProductTypeAttrService dtoProductTypeAttrService;
    private DtoProductCategoryService dtoProductCategoryService;
    //private DtoCategoryService dtoCategoryService;

    @Before
    public void setUp() throws Exception {
        dtoService = (DtoProductService) ctx().getBean(ServiceSpringKeys.DTO_PRODUCT_SERVICE);
        dtoBrandService = (DtoBrandService) ctx().getBean(ServiceSpringKeys.DTO_BRAND_SERVICE);
        dtoProductTypeService = (DtoProductTypeService) ctx().getBean(ServiceSpringKeys.DTO_PRODUCT_TYPE_SERVICE);
        dtoAttrService = (DtoAttributeService) ctx().getBean(ServiceSpringKeys.DTO_ATTRIBUTE_SERVICE);
        dtoFactory = (DtoFactory) ctx().getBean(ServiceSpringKeys.DTO_FACTORY);
        dtoProductTypeAttrService = (DtoProductTypeAttrService) ctx().getBean(ServiceSpringKeys.DTO_PRODUCT_TYPE_ATTR_SERVICE);
        dtoProductCategoryService = (DtoProductCategoryService) ctx().getBean(/*ServiceSpringKeys.DTO_PRODUCT_CATEGORY_SERVICE*/ "dtoProductCategoryService");
        //dtoCategoryService = (DtoCategoryService) ctx().getBean(ServiceSpringKeys.DTO_CATEGORY_SERVICE);

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
        Date availableFrom = new Date();
        dto.setAvailablefrom(availableFrom);
        dto.setAvailableto(availableFrom);
        dto.setName("new-name");
        dto.setDescription("new desciption");
        dto.setBrandDTO(dtoBrandService.getById(102L));
        dto.setProductTypeDTO(dtoProductTypeService.getById(2L));
        dto.setAvailability(Product.AVAILABILITY_ALWAYS);
        dtoService.update(dto);
        dto = dtoService.getById(pk);
        assertEquals(availableFrom, dto.getAvailablefrom());
        assertEquals(availableFrom, dto.getAvailableto());
        assertEquals("new-name", dto.getName());
        assertEquals("new desciption", dto.getDescription());
        assertEquals(102L, dto.getBrandDTO().getBrandId());
        assertEquals(2L, dto.getProductTypeDTO().getProducttypeId());
        assertEquals(Product.AVAILABILITY_ALWAYS, dto.getAvailability());
    }


    @Test
    public void testGetProductByCategory() throws Exception {
        List<ProductDTO> list = dtoService.getProductByCategory(211L);
        assertEquals(8, list.size()); //FEATURED-PRODUCT3 AVAILABLEFROM="2000-04-08 11:15:17.451" AVAILABLETO="2001-04-08 11:15:17.451"
        for (ProductDTO dto : list) {
            assertFalse(dto.getCode().equals("FEATURED-PRODUCT3"));
        }
        list = dtoService.getProductByCategory(208L);
        assertTrue(list.isEmpty());
    }



    @Test
    public void testGetProductByConeNameBrandType() throws Exception {
        List<ProductDTO> list = dtoService.getProductByCodeNameBrandType(null, null, 104L, 0);
        assertFalse(list.isEmpty());
        assertTrue(26 == list.size() || 28 == list.size());             //26 products with brand samsung
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
            assertNull(attrValueDTO.getVal()); // all must be empty when product is just created
        }


        AttrValueProductDTO attrValueDTO = new AttrValueProductDTOImpl();
        attrValueDTO.setProductId(dto.getProductId());
        attrValueDTO.setVal("plutonium");
        attrValueDTO.setAttributeDTO(dtoAttrService.getById(2010L));    //POWERSUPPLY

        attrValueDTO = (AttrValueProductDTO) dtoService.createEntityAttributeValue(attrValueDTO);

        list = dtoService.getEntityAttributes(dto.getProductId());

        for (AttrValueDTO attrValueDTO2 : list) {
            if ("POWERSUPPLY".equals(attrValueDTO2.getAttributeDTO().getCode())) {
                assertEquals("plutonium", attrValueDTO2.getVal()); // one has value
            } else {
                assertNull(attrValueDTO2.getVal()); // all must be empty when product is just created
            }
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
       /* ProductDTO dto = getDto();
        dto = dtoService.create(dto);
        assertTrue(dto.getProductId() > 0);
        dtoService.remove(dto.getProductId());
        dto = dtoService.getById(dto.getProductId());
        assertNull(dto);


        dto = getDto();
        dto = dtoService.create(dto);
        assertTrue(dto.getProductId() > 0);


        AttrValueProductDTO attrValueProductDTO = dtoFactory.getByIface(AttrValueProductDTO.class);
        attrValueProductDTO.setAttributeDTO(dtoAttrService.getById(2010L)); //POWERSUPPLY
        attrValueProductDTO.setProductId(dto.getProductId());
        attrValueProductDTO.setVal("Дрова"); //Firewood
        attrValueProductDTO = (AttrValueProductDTO) dtoService.createEntityAttributeValue(attrValueProductDTO);

        ProductCategoryDTO productCategoryDTO =  dtoProductCategoryService.getNew();
        productCategoryDTO.setProductId(dto.getProductId());
        productCategoryDTO.setCategoryId(117L);

        productCategoryDTO = dtoProductCategoryService.create(productCategoryDTO);


        dtoService.remove(dto.getProductId());
        dto = dtoService.getById(dto.getProductId());
        assertNull(dto);         */


        dtoService.remove(15300L); //remove_me
        ProductDTO dto = dtoService.getById(15300L);
        assertNull(dto);

    }

    private ProductDTO getDto() throws UnableToCreateInstanceException, UnmappedInterfaceException {
        ProductDTO dto = dtoService.getNew();
        dto.setCode("TESTCODE");
        dto.setName("test-name");
        dto.setBrandDTO(dtoBrandService.getById(101L));
        dto.setProductTypeDTO(dtoProductTypeService.getById(1L));
        dto.setAvailability(Product.AVAILABILITY_STANDARD);
        return dto;
    }

    private ProductDTO getDto(String suffix) throws UnableToCreateInstanceException, UnmappedInterfaceException {
        ProductDTO dto = dtoService.getNew();
        dto.setCode("TESTCODE" + suffix);
        dto.setName("test-name" + suffix);
        dto.setBrandDTO(dtoBrandService.getById(101L));
        dto.setProductTypeDTO(dtoProductTypeService.getById(1L));
        dto.setAvailability(Product.AVAILABILITY_STANDARD);
        return dto;
    }
}
