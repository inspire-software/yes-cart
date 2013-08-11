/*
 * Copyright 2013 Igor Azarnyi, Denys Pavlov
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

package org.yes.cart.service.domain.impl;

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.dto.SeoImageDTO;
import org.yes.cart.domain.entity.SeoImage;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.domain.ImageService;
import org.yes.cart.service.dto.DtoImageService;

import java.util.UUID;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * User: igora Igor Azarny
 * Date: 6/2/13
 * Time: 9:27 AM
 */
public class ImageServiceImplTest  extends BaseCoreDBTestCase {

    private DtoImageService dtoService;
    private ImageService imageService;
    private String imageName;


    @Before
    public void setUp() {
        dtoService = (DtoImageService) ctx().getBean(ServiceSpringKeys.DTO_IMAGE_SERVICE);
        imageService = (ImageService) ctx().getBean(ServiceSpringKeys.IMAGE_SERVICE);
        imageName = "tesiImageName" + UUID.randomUUID().toString() + ".jpeg";
        try {
            dtoService.create(getDto());
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.setUp();
    }

    private SeoImageDTO getDto() throws UnableToCreateInstanceException, UnmappedInterfaceException {
        SeoImageDTO dto = dtoService.getNew();
        dto.setAlt("alt");
        dto.setImageName(imageName);
        dto.setTitle("title");
        return dto;
    }

    
    
    
    @Test
    public void testDeleteImage() throws Exception {
        imageService.deleteImage(imageName);
        SeoImage seoImage = imageService.getSeoImage(imageName);
        assertNull("Found image with name " + imageName, seoImage);

    }

    @Test
    public void testGetSeoImage() throws Exception {
        SeoImage seoImage = imageService.getSeoImage(imageName);
        assertNotNull("Cannot find image with name " + imageName, seoImage);
    }
    
    
}
