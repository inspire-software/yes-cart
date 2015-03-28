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
import org.yes.cart.constants.Constants;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.dto.SeoImageDTO;
import org.yes.cart.domain.entity.SeoImage;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.domain.ImageService;
import org.yes.cart.service.dto.DtoImageService;

import java.io.File;
import java.util.UUID;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;

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
        imageName = "testImageName" + UUID.randomUUID().toString() + ".jpeg";
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
        dto.setImageName(Constants.PRODUCT_IMAGE_REPOSITORY_URL_PATTERN + imageName);
        dto.setTitle("title");
        return dto;
    }

    @Test
    public void testCropImageToCenter() throws Exception {

        final ImageServiceImpl srv = new ImageServiceImpl(null, null, "", 0, 0, 0, true, 0, null);

        final int[] croppedExactEven = srv.cropImageToCenter(100, 100, 100, 100);
        assertNotNull(croppedExactEven);
        assertArrayEquals(croppedExactEven, new int[] { 0, 0, 100, 100});

        final int[] croppedExactOdd = srv.cropImageToCenter(51, 51, 51, 51);
        assertNotNull(croppedExactOdd);
        assertArrayEquals(croppedExactOdd, new int[] { 0, 0, 51, 51});

        final int[] croppedExactScaleEven = srv.cropImageToCenter(100, 100, 1000, 1000);
        assertNotNull(croppedExactScaleEven);
        assertArrayEquals(croppedExactScaleEven, new int[] { 0, 0, 1000, 1000});

        final int[] croppedExactScaleOdd = srv.cropImageToCenter(51, 51, 510, 510);
        assertNotNull(croppedExactScaleOdd);
        assertArrayEquals(croppedExactScaleOdd, new int[] { 0, 0, 510, 510});

        final int[] croppedByHeightEven = srv.cropImageToCenter(80, 80, 150, 250);
        assertNotNull(croppedByHeightEven);
        assertArrayEquals(croppedByHeightEven, new int[] { 0, 50, 150, 150});

        final int[] croppedByHeightOdd = srv.cropImageToCenter(80, 80, 151, 251);
        assertNotNull(croppedByHeightOdd);
        assertArrayEquals(croppedByHeightOdd, new int[] { 0, 50, 151, 151});

        final int[] croppedByWidthEven = srv.cropImageToCenter(80, 80, 250, 150);
        assertNotNull(croppedByWidthEven);
        assertArrayEquals(croppedByWidthEven, new int[] { 50, 0, 150, 150});

        final int[] croppedByWidthOdd = srv.cropImageToCenter(80, 80, 251, 151);
        assertNotNull(croppedByWidthOdd);
        assertArrayEquals(croppedByWidthOdd, new int[] { 50, 0, 151, 151});

    }

    @Test
    public void testScaleImageToCenter() throws Exception {

        final ImageServiceImpl srv = new ImageServiceImpl(null, null, "", 0, 0, 0, true, 50, null);

        final int[] scaledExactEven = srv.scaleImageToCenter(100, 100, 100, 100);
        assertNotNull(scaledExactEven);
        assertArrayEquals(scaledExactEven, new int[] { 0, 0, 100, 100});

        final int[] scaledExactOdd = srv.scaleImageToCenter(51, 51, 51, 51);
        assertNotNull(scaledExactOdd);
        assertArrayEquals(scaledExactOdd, new int[] { 0, 0, 51, 51});

        final int[] scaledExactScaleEven = srv.scaleImageToCenter(100, 100, 1000, 1000);
        assertNotNull(scaledExactScaleEven);
        assertArrayEquals(scaledExactScaleEven, new int[] { 0, 0, 100, 100});

        final int[] scaledExactScaleOdd = srv.scaleImageToCenter(51, 51, 510, 510);
        assertNotNull(scaledExactScaleOdd);
        assertArrayEquals(scaledExactScaleOdd, new int[] { 0, 0, 51, 51});

        final int[] scaledByHeightEven = srv.scaleImageToCenter(80, 80, 150, 250);
        assertNotNull(scaledByHeightEven);
        assertArrayEquals(scaledByHeightEven, new int[] { 16, 0, 48, 80});

        final int[] scaledByHeightOdd = srv.scaleImageToCenter(80, 80, 151, 251);
        assertNotNull(scaledByHeightOdd);
        assertArrayEquals(scaledByHeightOdd, new int[] { 16, 0, 48, 80});

        final int[] scaledByWidthEven = srv.scaleImageToCenter(80, 80, 250, 150);
        assertNotNull(scaledByWidthEven);
        assertArrayEquals(scaledByWidthEven, new int[] { 0, 16, 80, 48});

        final int[] scaledByWidthOdd = srv.scaleImageToCenter(80, 80, 251, 151);
        assertNotNull(scaledByWidthOdd);
        assertArrayEquals(scaledByWidthOdd, new int[] { 0, 16, 80, 48});

    }

    @Test
    public void testResize() throws Exception {

        imageService.resizeImage(
                "src/test/resources/imgresize/resize-150x200.jpg",
                "target/test/resources/imgresize/resize-150x200-to-80x80-scale.jpg", "80", "80", false);

        imageService.resizeImage(
                "src/test/resources/imgresize/resize-150x200.jpg",
                "target/test/resources/imgresize/resize-150x200-to-80x80-crop.jpg", "80", "80", true);

        imageService.resizeImage(
                "src/test/resources/imgresize/resize-150x200.tif",
                "target/test/resources/imgresize/resize-150x200-to-80x80-scale.tif", "80", "80", false);

        imageService.resizeImage(
                "src/test/resources/imgresize/resize-150x200.tif",
                "target/test/resources/imgresize/resize-150x200-to-80x80-crop.tif", "80", "80", true);

        imageService.resizeImage(
                "src/test/resources/imgresize/resize-150x200.png",
                "target/test/resources/imgresize/resize-150x200-to-80x80-scale.png", "80", "80", false);

        imageService.resizeImage(
                "src/test/resources/imgresize/resize-150x200.png",
                "target/test/resources/imgresize/resize-150x200-to-80x80-crop.png", "80", "80", true);

        imageService.resizeImage(
                "src/test/resources/imgresize/resize-200x200.jpg",
                "target/test/resources/imgresize/resize-200x200-to-80x80-scale.jpg", "80", "80", false);

        imageService.resizeImage(
                "src/test/resources/imgresize/resize-200x200.jpg",
                "target/test/resources/imgresize/resize-200x200-to-80x80-crop.jpg", "80", "80", true);

        imageService.resizeImage(
                "src/test/resources/imgresize/resize-200x200.tif",
                "target/test/resources/imgresize/resize-200x200-to-80x80-scale.tif", "80", "80", false);

        imageService.resizeImage(
                "src/test/resources/imgresize/resize-200x200.tif",
                "target/test/resources/imgresize/resize-200x200-to-80x80-crop.tif", "80", "80", true);

        imageService.resizeImage(
                "src/test/resources/imgresize/resize-200x200.png",
                "target/test/resources/imgresize/resize-200x200-to-80x80-scale.png", "80", "80", false);

        imageService.resizeImage(
                "src/test/resources/imgresize/resize-200x200.png",
                "target/test/resources/imgresize/resize-200x200-to-80x80-crop.png", "80", "80", true);

        imageService.resizeImage(
                "src/test/resources/imgresize/resize-200x150.jpg",
                "target/test/resources/imgresize/resize-200x150-to-80x80-scale.jpg", "80", "80", false);

        imageService.resizeImage(
                "src/test/resources/imgresize/resize-200x150.jpg",
                "target/test/resources/imgresize/resize-200x150-to-80x80-crop.jpg", "80", "80", true);

        imageService.resizeImage(
                "src/test/resources/imgresize/resize-200x150.tif",
                "target/test/resources/imgresize/resize-200x150-to-80x80-scale.tif", "80", "80", false);

        imageService.resizeImage(
                "src/test/resources/imgresize/resize-200x150.tif",
                "target/test/resources/imgresize/resize-200x150-to-80x80-crop.tif", "80", "80", true);

        imageService.resizeImage(
                "src/test/resources/imgresize/resize-200x150.png",
                "target/test/resources/imgresize/resize-200x150-to-80x80-scale.png", "80", "80", false);

        imageService.resizeImage(
                "src/test/resources/imgresize/resize-200x150.png",
                "target/test/resources/imgresize/resize-200x150-to-80x80-crop.png", "80", "80", true);

    }

    @Test
    public void testDeleteImage() throws Exception {
        imageService.deleteImage(imageName,
                Constants.PRODUCT_IMAGE_REPOSITORY_URL_PATTERN, "file:" + File.separator + File.separator + new File("target/imgrepo").getAbsolutePath() + File.separator);
        SeoImage seoImage = imageService.getSeoImage(Constants.PRODUCT_IMAGE_REPOSITORY_URL_PATTERN + imageName);
        assertNull("Found image with name " + imageName, seoImage);

    }

    @Test
    public void testGetSeoImage() throws Exception {
        SeoImage seoImage = imageService.getSeoImage(Constants.PRODUCT_IMAGE_REPOSITORY_URL_PATTERN + imageName);
        assertNotNull("Cannot find image with name " + imageName, seoImage);
    }
    
    
}
