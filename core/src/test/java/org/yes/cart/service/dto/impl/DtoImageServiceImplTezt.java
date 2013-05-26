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
import org.yes.cart.domain.dto.SeoImageDTO;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.dto.DtoImageService;

import static org.junit.Assert.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DtoImageServiceImplTezt extends BaseCoreDBTestCase {

    private DtoImageService dtoService;

    @Before
    public void setUp() {
        dtoService = (DtoImageService) ctx().getBean(ServiceSpringKeys.DTO_IMAGE_SERVICE);
        super.setUp();
    }

    @Test
    public void testCreate() throws Exception {
        SeoImageDTO dto = getDto();
        dto = dtoService.create(dto);
        assertTrue(dto.getSeoImageId() > 0);
    }

    private SeoImageDTO getDto() throws UnableToCreateInstanceException, UnmappedInterfaceException {
        SeoImageDTO dto = dtoService.getNew();
        dto.setAlt("alt");
        dto.setImageName("image1.jpeg");
        dto.setTitle("title");
        return dto;
    }

    @Test
    public void testUpdate() throws Exception {
        SeoImageDTO dto = getDto();
        dto = dtoService.create(dto);
        assertTrue(dto.getSeoImageId() > 0);
        dto.setAlt("alt2");
        dto.setImageName("image1.jpeg2");
        dto.setTitle("title2");
        dto = dtoService.update(dto);
        assertEquals("alt2", dto.getAlt());
        assertEquals("image1.jpeg2", dto.getImageName());
        assertEquals("title2", dto.getTitle());
    }

    @Test
    public void testRemove() throws Exception {
        SeoImageDTO dto = getDto();
        dto = dtoService.create(dto);
        assertTrue(dto.getSeoImageId() > 0);
        long pk = dto.getSeoImageId();
        dtoService.remove(pk);
        dto = dtoService.getById(pk);
        assertNull(dto);
    }
}
