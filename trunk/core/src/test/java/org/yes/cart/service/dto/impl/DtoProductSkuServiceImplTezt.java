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
import org.yes.cart.domain.dto.*;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.dto.impl.SkuPriceDTOImpl;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.dto.DtoAttributeService;
import org.yes.cart.service.dto.DtoProductService;
import org.yes.cart.service.dto.DtoProductSkuService;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DtoProductSkuServiceImplTezt extends BaseCoreDBTestCase {

    private DtoProductSkuService dtoService;
    private DtoProductService dtoProductService;
    private DtoFactory dtoFactory;
    private DtoAttributeService dtoAttrService;

    @Before
    public void setUp() {
        dtoService = (DtoProductSkuService) ctx().getBean(ServiceSpringKeys.DTO_PRODUCT_SKU_SERVICE);
        dtoProductService = (DtoProductService) ctx().getBean(ServiceSpringKeys.DTO_PRODUCT_SERVICE);
        dtoFactory = (DtoFactory) ctx().getBean(ServiceSpringKeys.DTO_FACTORY);
        dtoAttrService = (DtoAttributeService) ctx().getBean(ServiceSpringKeys.DTO_ATTRIBUTE_SERVICE);
        super.setUp();
    }

    @Test
    public void testCreate() throws Exception {
        ProductSkuDTO dto = getDto();
        dto = dtoService.create(dto);
        assertTrue(dto.getSkuId() > 0);
    }

    @Test
    public void testUpdate() throws Exception {
        ProductSkuDTO dto = getDto();
        dto.setCode("233456");
        dto = dtoService.create(dto);
        assertTrue(dto.getSkuId() > 0);
        dto.setName("new name");
        dto.setDescription("new description");
        dto.setBarCode("233456");
        dto = dtoService.update(dto);
        assertEquals("new name", dto.getName());
        assertEquals("new description", dto.getDescription());
        assertEquals("233456", dto.getBarCode());
    }

    @Test
    public void testCreateSkuPrice() throws Exception {
        ProductSkuDTO dto = getDto();
        dto.setCode("233456-a");
        dto = dtoService.create(dto);
        assertTrue(dto.getSkuId() > 0);
        long pk = dto.getSkuId();
        SkuPriceDTO skuPriceDTO = new SkuPriceDTOImpl();
        skuPriceDTO.setRegularPrice(new BigDecimal("1.23"));
        skuPriceDTO.setProductSkuId(pk);
        skuPriceDTO.setShopId(10L);
        skuPriceDTO.setCurrency("EUR");
        skuPriceDTO.setQuantity(BigDecimal.ONE);
        dtoService.createSkuPrice(skuPriceDTO);
        dto = dtoService.getById(pk);
        assertEquals(1, dto.getPrice().size());
        assertEquals("EUR", dto.getPrice().iterator().next().getCurrency());
        assertEquals(10L, dto.getPrice().iterator().next().getShopId());
        assertTrue((new BigDecimal("1.23")).equals(dto.getPrice().iterator().next().getRegularPrice()));
    }

    @Test
    public void testDeleteSkuPrice() throws Exception {
        ProductSkuDTO dto = getDto();
        dto.setCode("233456-b");
        dto = dtoService.create(dto);
        assertTrue(dto.getSkuId() > 0);
        long pk = dto.getSkuId();
        SkuPriceDTO skuPriceDTO = new SkuPriceDTOImpl();
        skuPriceDTO.setRegularPrice(new BigDecimal("1.23"));
        skuPriceDTO.setProductSkuId(pk);
        skuPriceDTO.setShopId(10L);
        skuPriceDTO.setCurrency("EUR");
        skuPriceDTO.setQuantity(BigDecimal.ONE);
        dtoService.createSkuPrice(skuPriceDTO);
        dto = dtoService.getById(pk);
        assertEquals(1, dto.getPrice().size());
        assertEquals("EUR", dto.getPrice().iterator().next().getCurrency());
        assertEquals(10L, dto.getPrice().iterator().next().getShopId());
        assertTrue((new BigDecimal("1.23")).equals(dto.getPrice().iterator().next().getRegularPrice()));
        dtoService.removeSkuPrice(dto.getPrice().iterator().next().getSkuPriceId());
        dto = dtoService.getById(pk);
        assertTrue(dto.getPrice().isEmpty());
    }

    @Test
    public void testUpdateSkuPrice() throws Exception {
        ProductSkuDTO dto = getDto();
        dto.setCode("233456-c");
        dto = dtoService.create(dto);
        assertTrue(dto.getSkuId() > 0);
        long pk = dto.getSkuId();
        SkuPriceDTO skuPriceDTO = new SkuPriceDTOImpl();
        skuPriceDTO.setRegularPrice(new BigDecimal("1.23"));
        skuPriceDTO.setProductSkuId(pk);
        skuPriceDTO.setShopId(10L);
        skuPriceDTO.setCurrency("EUR");
        skuPriceDTO.setQuantity(BigDecimal.ONE);
        dtoService.createSkuPrice(skuPriceDTO);
        dto = dtoService.getById(pk);
        skuPriceDTO = dto.getPrice().iterator().next();
        assertEquals(1, dto.getPrice().size());
        assertEquals("EUR", skuPriceDTO.getCurrency());
        assertEquals(10L, skuPriceDTO.getShopId());
        assertTrue((new BigDecimal("1.23")).equals(skuPriceDTO.getRegularPrice()));
        Date date = new Date();
        skuPriceDTO.setRegularPrice(new BigDecimal("2.34"));
        skuPriceDTO.setSalePrice(new BigDecimal("2.33"));
        skuPriceDTO.setMinimalPrice(new BigDecimal("2.32"));
        skuPriceDTO.setSalefrom(date);
        skuPriceDTO.setSaleto(date);
        dtoService.updateSkuPrice(skuPriceDTO);
        dto = dtoService.getById(pk);
        skuPriceDTO = dto.getPrice().iterator().next();
        assertEquals(1, dto.getPrice().size());
        assertEquals("EUR", skuPriceDTO.getCurrency());
        assertEquals(10L, skuPriceDTO.getShopId());
        assertTrue((new BigDecimal("2.34")).equals(skuPriceDTO.getRegularPrice()));
        assertTrue((new BigDecimal("2.33")).equals(skuPriceDTO.getSalePrice()));
        assertTrue((new BigDecimal("2.32")).equals(skuPriceDTO.getMinimalPrice()));


        dtoService.remove(pk);
        dto = dtoService.getById(pk);
        assertNull(dto);

        dtoService.remove(15301);
        dto = dtoService.getById(pk);
        assertNull(dto);

    }

    @Test
    public void testCreateEntityAttributeValue() throws Exception {
        ProductSkuDTO dto = getDto();
        dto.setCode("233456-d");
        dto = dtoService.create(dto);
        assertTrue(dto.getSkuId() > 0);
        AttrValueProductSkuDTO attrValueDTO = dtoFactory.getByIface(AttrValueProductSkuDTO.class);
        attrValueDTO.setAttributeDTO(dtoAttrService.getById(100L)); //IMAGE0
        attrValueDTO.setSkuId(dto.getSkuId());
        attrValueDTO.setVal("image.jpg");
        dtoService.createEntityAttributeValue(attrValueDTO);
        dto = dtoService.getById(dto.getSkuId());
        assertFalse(dto.getAttributes().isEmpty());
        for (AttrValueDTO val : dto.getAttributes()) {
            if (val.getAttributeDTO().getAttributeId() == 100L) {
                assertEquals("image.jpg", val.getVal());
                return;
            }
        }
        fail("Did not find image attribute");
    }

    @Test
    public void testGetEntityAttributes() throws Exception {
        ProductSkuDTO dto = getDto();
        dto.setCode("233456-e");
        dto = dtoService.create(dto);
        assertTrue(dto.getSkuId() > 0);
        AttrValueProductSkuDTO attrValueDTO = dtoFactory.getByIface(AttrValueProductSkuDTO.class);
        attrValueDTO.setAttributeDTO(dtoAttrService.getById(100L)); //IMAGE0
        attrValueDTO.setSkuId(dto.getSkuId());
        attrValueDTO.setVal("image.jpg");
        dtoService.createEntityAttributeValue(attrValueDTO);
        List<? extends AttrValueDTO> list = dtoService.getEntityAttributes(dto.getSkuId());
        assertFalse(list.isEmpty());
        for (AttrValueDTO val : list) {
            if (val.getAttributeDTO().getAttributeId() == 100L) {
                assertEquals("image.jpg", val.getVal());
                return;
            }
        }
        fail("Did not find image attribute");
    }

    @Test
    public void testUpdateEntityAttributeValue() throws Exception {
        ProductSkuDTO dto = getDto();
        dto.setCode("233456-f");
        dto = dtoService.create(dto);
        assertTrue(dto.getSkuId() > 0);
        AttrValueProductSkuDTO attrValueDTO = dtoFactory.getByIface(AttrValueProductSkuDTO.class);
        attrValueDTO.setAttributeDTO(dtoAttrService.getById(100L)); //IMAGE0
        attrValueDTO.setSkuId(dto.getSkuId());
        attrValueDTO.setVal("image.jpg");
        dtoService.createEntityAttributeValue(attrValueDTO);
        dto = dtoService.getById(dto.getSkuId());
        assertFalse(dto.getAttributes().isEmpty());
        AttrValueDTO image0 = null;
        for (AttrValueDTO val : dto.getAttributes()) {
            if (val.getAttributeDTO().getAttributeId() == 100L) {
                assertEquals("image.jpg", val.getVal());
                image0 = val;
            }
        }
        assertNotNull(image0);
        image0.setVal("image2.jpeg");
        dtoService.updateEntityAttributeValue(image0);
        dto = dtoService.getById(dto.getSkuId());
        assertFalse(dto.getAttributes().isEmpty());
        for (AttrValueDTO val : dto.getAttributes()) {
            if (val.getAttributeDTO().getAttributeId() == 100L) {
                assertEquals("image2.jpeg", val.getVal());
                return;
            }
        }
        fail("Did not find image attribute");
    }

    @Test
    public void testDeleteAttributeValue() throws Exception {
        ProductSkuDTO dto = getDto();
        dto.setCode("233456-g");
        dto = dtoService.create(dto);
        assertTrue(dto.getSkuId() > 0);
        AttrValueProductSkuDTO attrValueDTO = dtoFactory.getByIface(AttrValueProductSkuDTO.class);
        attrValueDTO.setAttributeDTO(dtoAttrService.getById(100L)); //IMAGE0
        attrValueDTO.setSkuId(dto.getSkuId());
        attrValueDTO.setVal("image.jpg");
        dtoService.createEntityAttributeValue(attrValueDTO);
        dto = dtoService.getById(dto.getSkuId());
        assertFalse(dto.getAttributes().isEmpty());
        AttrValueDTO image0 = null;
        for (AttrValueDTO val : dto.getAttributes()) {
            if (val.getAttributeDTO().getAttributeId() == 100L) {
                assertEquals("image.jpg", val.getVal());
                image0 = val;
            }
        }
        assertNotNull(image0);
        dtoService.deleteAttributeValue(image0.getAttrvalueId());
        dto = dtoService.getById(dto.getSkuId());
        assertTrue(dto.getAttributes().isEmpty());
    }

    @Test
    public void testGetAllProductSkus() throws Exception {
        List<ProductSkuDTO> list = dtoService.getAllProductSkus(10000L);
        assertEquals(4, list.size());
    }

    private ProductSkuDTO getDto() throws UnableToCreateInstanceException, UnmappedInterfaceException {
        ProductSkuDTO dto = dtoService.getNew();
        ProductDTO productDto = dtoProductService.getById(9999);
        dto.setProductId(productDto.getProductId());
        dto.setBarCode("123123");
        dto.setCode("BENDER-V4");
        dto.setName("Bender version v");
        dto.setDescription("Bender version v");
        dto.setRank(5);
        return dto;
    }
}
