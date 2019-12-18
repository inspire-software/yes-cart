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

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.DtoServiceSpringKeys;
import org.yes.cart.domain.dto.AttrValueDTO;
import org.yes.cart.domain.dto.AttrValueProductDTO;
import org.yes.cart.domain.dto.ProductDTO;
import org.yes.cart.domain.dto.ProductSkuDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.dto.impl.AttrValueProductDTOImpl;
import org.yes.cart.domain.misc.SearchContext;
import org.yes.cart.domain.misc.SearchResult;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.dto.DtoAttributeService;
import org.yes.cart.service.dto.DtoBrandService;
import org.yes.cart.service.dto.DtoProductService;
import org.yes.cart.service.dto.DtoProductTypeService;
import org.yes.cart.utils.TimeContext;

import java.time.LocalDateTime;
import java.util.*;

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

    @Before
    public void setUp() {
        dtoService = (DtoProductService) ctx().getBean(DtoServiceSpringKeys.DTO_PRODUCT_SERVICE);
        dtoBrandService = (DtoBrandService) ctx().getBean(DtoServiceSpringKeys.DTO_BRAND_SERVICE);
        dtoProductTypeService = (DtoProductTypeService) ctx().getBean(DtoServiceSpringKeys.DTO_PRODUCT_TYPE_SERVICE);
        dtoAttrService = (DtoAttributeService) ctx().getBean(DtoServiceSpringKeys.DTO_ATTRIBUTE_SERVICE);
        dtoFactory = (DtoFactory) ctx().getBean(DtoServiceSpringKeys.DTO_FACTORY);
        super.setUp();

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
    public void testCreateAndBindAttrVal() throws Exception {

        ProductDTO dto = getDto("testCreateAndBindAttrVal");
        dto = dtoService.create(dto);
        assertTrue(dto.getProductId() > 0);

        AttrValueDTO attrValueDTO = dtoService.createAndBindAttrVal(dto.getProductId(), "cpu", "z80");

        assertNotNull(attrValueDTO);


    }

    @Test
    public void testUpdate() throws Exception {
        ProductDTO dto = getDto();
        dto.setCode("P-0000");
        dto = dtoService.create(dto);
        assertTrue(dto.getProductId() > 0);
        long pk = dto.getProductId();
        LocalDateTime availableFrom = TimeContext.getLocalDateTime();
        dto.setName("new-name");
        dto.setDescription("new desciption");
        dto.setBrandDTO(dtoBrandService.getById(102L));
        dto.setProductTypeDTO(dtoProductTypeService.getById(2L));
        dtoService.update(dto);
        dto = dtoService.getById(pk);
        assertEquals("new-name", dto.getName());
        assertEquals("new desciption", dto.getDescription());
        assertEquals(102L, dto.getBrandDTO().getBrandId());
        assertEquals(2L, dto.getProductTypeDTO().getProducttypeId());
    }

    @Test
    public void findProductSupplierCatalogCodes() throws Exception {

        final List<String> codes = dtoService.findProductSupplierCatalogCodes();
        assertNotNull(codes);
        assertEquals(3, codes.size());
        assertTrue(codes.contains("CAT001"));
        assertTrue(codes.contains("CAT002"));
        assertTrue(codes.contains("CAT003"));

    }

    @Test
    public void testCreateEntityAttributeValue() throws Exception {
        ProductDTO dto = getDto();
        dto.setCode("P-0001");
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
        dto.setCode("P-0002");
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
        dto.setCode("P-0003");
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
        dto.setCode("P-0004");
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
        return dto;
    }

    private ProductDTO getDto(String suffix) throws UnableToCreateInstanceException, UnmappedInterfaceException {
        ProductDTO dto = dtoService.getNew();
        dto.setCode("TESTCODE" + suffix);
        dto.setName("test-name" + suffix);
        dto.setBrandDTO(dtoBrandService.getById(101L));
        dto.setProductTypeDTO(dtoProductTypeService.getById(1L));
        return dto;
    }


    @Test
    public void testFindBy() throws Exception {

        // code exact
        List<ProductDTO> list = dtoService.findBy("!FEATURED-PRODUCT3", 0, 10);
        assertEquals(1, list.size());
        assertEquals("FEATURED-PRODUCT3", list.get(0).getCode());

        // code partial
        list = dtoService.findBy("#featured", 0, 10);
        assertEquals(9, list.size());
        assertTrue(list.get(0).getCode().startsWith("FEATURED-PRODUCT"));

        // PK
        list = dtoService.findBy("*15053", 0, 10);
        assertEquals(1, list.size());
        assertEquals("FEATURED-PRODUCT3", list.get(0).getCode());

        // by brand
        list = dtoService.findBy("?samsung", 0, 10);
        assertFalse(list.isEmpty());
        assertEquals("Samsung", list.get(0).getBrandDTO().getName());

        // by category
        list = dtoService.findBy("^featured products", 0, 10);
        assertFalse(list.isEmpty());
        assertEquals("Featured products", list.get(0).getProductCategoryDTOs().iterator().next().getCategoryName());

        // basic
        list = dtoService.findBy("bender", 0, 10);
        assertFalse(list.isEmpty());
        assertTrue(list.get(0).getCode().startsWith("BENDER"));

    }


    @Test
    public void testFindProduct() throws Exception {

        // code exact
        final SearchContext filterCodeExact = new SearchContext(Collections.singletonMap("filter", Collections.singletonList("! FEATURED-PRODUCT3")), 0, 10, "name", false, "filter");
        SearchResult<ProductDTO> list = dtoService.findProducts(filterCodeExact);
        assertEquals(1, list.getTotal());
        assertEquals("FEATURED-PRODUCT3", list.getItems().get(0).getCode());

        // code partial
        final SearchContext filterPartial = new SearchContext(Collections.singletonMap("filter", Collections.singletonList("#featured")), 0, 10, "name", false, "filter");
        list = dtoService.findProducts(filterPartial);
        assertEquals(9, list.getTotal());
        assertTrue(list.getItems().get(0).getCode().startsWith("FEATURED-PRODUCT"));

        // PK
        final SearchContext filterByPk = new SearchContext(Collections.singletonMap("filter", Collections.singletonList("* 15053 ")), 0, 10, "name", false, "filter");
        list = dtoService.findProducts(filterByPk);
        assertEquals(1, list.getTotal());
        assertEquals("FEATURED-PRODUCT3", list.getItems().get(0).getCode());

        // by brand
        final SearchContext filterByBrand = new SearchContext(Collections.singletonMap("filter", Collections.singletonList("?samsung")), 0, 10, "name", false, "filter");
        list = dtoService.findProducts(filterByBrand);
        assertTrue(list.getTotal() > 0);
        assertEquals("Samsung", list.getItems().get(0).getBrandDTO().getName());

        // by category
        final SearchContext filterByCategory = new SearchContext(Collections.singletonMap("filter", Collections.singletonList("^ 211")), 0, 10, "name", false, "filter");
        list = dtoService.findProducts(filterByCategory);
        assertTrue(list.getTotal() > 0);
        assertEquals("Featured products", list.getItems().get(0).getProductCategoryDTOs().iterator().next().getCategoryName());

        // basic
        final SearchContext filterBasic = new SearchContext(Collections.singletonMap("filter", Collections.singletonList("bender")), 0, 10, "name", false, "filter");
        list = dtoService.findProducts(filterBasic);
        assertTrue(list.getTotal() > 0);
        assertTrue(list.getItems().get(0).getCode().startsWith("BENDER"));

        // Federated
        final Map<String, List> federatedRestricted = new HashMap<>();
        federatedRestricted.put("filter", Collections.singletonList("001_CAT00"));
        federatedRestricted.put("supplierCatalogCodes", Arrays.asList("CAT001", "CAT002"));
        final SearchContext filterFederatedRestricted = new SearchContext(federatedRestricted, 0, 10, "name", false, "filter", "supplierCatalogCodes");
        list = dtoService.findProducts(filterFederatedRestricted);
        assertEquals(2, list.getTotal());
        assertEquals("001_CAT001", list.getItems().get(0).getCode());
        assertEquals("001_CAT002", list.getItems().get(1).getCode());

        final Map<String, List> federatedDefault = new HashMap<>();
        federatedDefault.put("filter", Collections.singletonList("* 9998 "));
        federatedDefault.put("supplierCatalogCodes", Arrays.asList("CAT001", "CAT002"));
        final SearchContext filterFederatedDefault = new SearchContext(federatedDefault, 0, 10, "name", false, "filter", "supplierCatalogCodes");
        list = dtoService.findProducts(filterFederatedDefault);
        assertEquals(1, list.getTotal());
        assertEquals("BENDER-ua", list.getItems().get(0).getCode());

    }


}
