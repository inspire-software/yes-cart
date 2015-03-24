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

package org.yes.cart.service.domain.impl;

import org.apache.commons.io.FileUtils;
import org.dbunit.util.Base64;
import org.hibernate.criterion.Criterion;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.yes.cart.constants.Constants;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.AttrValueProduct;
import org.yes.cart.domain.entity.SeoImage;
import org.yes.cart.service.image.ImageNameStrategy;
import org.yes.cart.service.image.ImageNameStrategyResolver;
import org.yes.cart.service.image.impl.ProductImageNameStrategyImpl;
import org.yes.cart.service.misc.LanguageService;
import org.yes.cart.stream.io.IOProvider;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
//In case of IDE test should be run in core folder
@RunWith(JMock.class)
public class ImageServiceTest {

    private static final String BASE64_ENCODED_JPEG_0 = "iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAAAXNSR0IArs4c6QAAAAZiS0dEAP8A/wD/oL2nkwAAAAlwSFlzAAALEwAACxMBAJqcGAAAAAd0SU1FB9oGEw8qFiJ+pnwAAAQbSURBVDgRARAE7/sAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEAAAAAAAAAAAAAAACMeHr/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAB0iIYBAAAAAAAAAAAAAAAAAgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEAAAAAAAAAAAAAAAAAAAAAAAAAAB0iIYBAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABAAAAAAAAAAAAAAAAjHh6/wAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAdIiGAQAAAAAAAAAAAAAAAAIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABAAAAAAAAAAAAAAAAAAAAAAAAAAAdIiGAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABAAAAAAAAAAAAAAAAjHh6/wAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAdIiGAQAAAAAAAAAAAAAAAAIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADRyDyIlm2rZAAAAAElFTkSuQmCC";
    private static final String BASE64_ENCODED_JPEG_1 = "/9j/4AAQSkZJRgABAQEAYABgAAD/4QBGRXhpZgAASUkqAAgAAAABAGmHBAABAAAAGgAAAAAAAAABAIaSAgASAAAALAAAAAAAAABDcmVhdGVkIHdpdGggR0lNUAD/2wBDAAgGBgcGBQgHBwcJCQgKDBQNDAsLDBkSEw8UHRofHh0aHBwgJC4nICIsIxwcKDcpLDAxNDQ0Hyc5PTgyPC4zNDL/2wBDAQkJCQwLDBgNDRgyIRwhMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjL/wAARCAAoACgDASIAAhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQAAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwD38kAZJwKzJryV5zGhKICB7nOD/WrV7PHFbuHdQWGAD3qJLSO4PnsXw53KA2Pocj6A1L7Ia7sZdXv2NUXzAX77jnj1NPttVtpo8vIkZBx8xwD9CaxNYt4xM6pIDuHA3ZIxzWcL2SGNEV49qDrnkjPT1xWbqNM0UE0d1RWDo8tyzpDMdzCMdG4UDA/l2orVO6uZtWdi9fIDcKxiDx+WwcnnuMf1rO0Us0/lSXb5iyyRjo6Ecfl1/EVsXC718kEjecH3HJx9OD/9asi50m5mkH2a4MZgjChhgZYdDj2AxUSTvdFRelmXJ7Czt7J2WJdwHDMMkc1yEnmPdmIBRMxwAUGSPr/U10cNxePCbR7ZUkAxI4z83HZe35mqlppwk1m3fcR5asxB439MY/PNZyXNaxcXy3ubVrF9n2rkOeDuHGdxGc/kPwoq8YwQAOMHPFFbpWMWOIBIOOnSgKFGAMCiimBHNEZUKq+wkYJxms+TTJY41lhnL3MeSpbgH2oopNJjTaL9vJJJEplTaxGSORRRRTQj/9k=";

    private Mockery mockery = new JUnit4Mockery();
    private ImageNameStrategyResolver imageNameStrategyResolver = mockery.mock(ImageNameStrategyResolver.class);
    private ImageServiceImpl imageService;
    private IOProvider ioProvider = mockery.mock(IOProvider.class);
    private GenericDAO<SeoImage, Long> seoImageDao = mockery.mock(GenericDAO.class, "seoImageDao");
    private GenericDAO<AttrValueProduct, Long> productDao = mockery.mock(GenericDAO.class, "productDao");
    final LanguageService languageService = mockery.mock(LanguageService.class, "languageService");
    private ImageNameStrategy imageNameStrategy = new ProductImageNameStrategyImpl("product", null, productDao, languageService);


    @Test
    public void testResizeImageJPEG() throws Exception {

        imageService = new ImageServiceImpl(seoImageDao, imageNameStrategyResolver, "50x150", 255, 255, 255, false, 50, ioProvider);


        final String originalFileName = "src/test/resources/imgrepo/a/arbuz/speli_arbuz_arbuz_a.jpeg";
        final String destinationFileName = "target/test/resources/imgrepo/50x150/a/arbuz/speli_arbuz_arbuz_a.jpeg";
        final byte[] content = FileUtils.readFileToByteArray(new File(originalFileName));
        final byte[] resized = imageService.resizeImage("speli_arbuz_arbuz_a.jpeg", content, "50", "150");
        assertNotNull(resized);
        assertTrue(resized.length > 0);
        FileUtils.writeByteArrayToFile(new File(destinationFileName), resized);
    }

    /**
     * Black border solving.
     * See private RenderedOp resizeImage(final RenderedImage image, final BigDecimal scale)
     */
    @Test
    public void testResizeImageJPEG2() throws Exception {

        imageService = new ImageServiceImpl(seoImageDao, imageNameStrategyResolver, "50x150", 255, 255, 255, false, 50, ioProvider);

        final String originalFileName = "src/test/resources/imgrepo/C/C01-00002-7B/C01-00002-7B_a.jpeg";
        final String destinationFileName = "target/test/resources/imgrepo/200x200/C/C01-00002-7B/C01-00002-7B_a.jpeg";
        final byte[] content = FileUtils.readFileToByteArray(new File(originalFileName));
        final byte[] resized = imageService.resizeImage("C01-00002-7B_a.jpeg", content, "200", "200");
        assertNotNull(resized);
        assertTrue(resized.length > 0);
        FileUtils.writeByteArrayToFile(new File(destinationFileName), resized);
    }

    @Test
    public void testResizeImagePNG() throws Exception {

        imageService = new ImageServiceImpl(seoImageDao, imageNameStrategyResolver, "50x150", 255, 255, 255, false, 50, ioProvider);

        final String originalFileName = "src/test/resources/imgrepo/a/aron/aron_a.png";
        final String destinationFileName = "target/test/resources/imgrepo/50x150/a/aron/aron_a.png";
        final byte[] content = FileUtils.readFileToByteArray(new File(originalFileName));
        final byte[] resized = imageService.resizeImage("aron_a.png", content, "50", "150");
        assertNotNull(resized);
        assertTrue(resized.length > 0);
        FileUtils.writeByteArrayToFile(new File(destinationFileName), resized);
    }

    @Test
    public void testAddImageToRepository() throws Exception {

        mockery.checking(new Expectations() {{
            allowing(imageNameStrategyResolver).getImageNameStrategy(Constants.PRODUCT_IMAGE_REPOSITORY_URL_PATTERN);
            will(returnValue(imageNameStrategy));
            allowing(languageService).getSupportedLanguages();
            will(returnValue(Arrays.asList("en", "uk", "ru")));
        }});

        imageService = new ImageServiceImpl(seoImageDao, imageNameStrategyResolver, "50x150", 255, 255, 255, false, 50, new IOProvider() {
            @Override
            public boolean supports(final String uri) {
                return true;
            }

            @Override
            public boolean exists(final String uri, final Map<String, Object> context) {
                return new File(uri).exists();
            }

            @Override
            public boolean isNewerThan(final String uriToCheck, final String uriToCheckAgainst, final Map<String, Object> context) {
                return false;
            }

            @Override
            public byte[] read(final String uri, final Map<String, Object> context) throws IOException {
                return FileUtils.readFileToByteArray(new File(uri));
            }

            @Override
            public void write(final String uri, final byte[] content, final Map<String, Object> context) throws IOException {
                FileUtils.writeByteArrayToFile(new File(uri), content);
            }

            @Override
            public void delete(final String uri, final Map<String, Object> context) throws IOException {
                assertTrue(new File(uri).delete());
            }
        });

        final String tmpFileName = "target/test/resources/some-seo-image-name_PRODUCT1.jpeg";
        byte[] image = Base64.decode(BASE64_ENCODED_JPEG_0);
        final String save1 = imageService.addImageToRepository(tmpFileName, "PRODUCT1", image, Constants.PRODUCT_IMAGE_REPOSITORY_URL_PATTERN, "target/");
        final File destination1 = new File("target/product/P/PRODUCT1/" + save1);
        assertTrue(destination1.exists());
        assertTrue("At least some info must be in the file" , destination1.length() > 1000);
        image = Base64.decode(BASE64_ENCODED_JPEG_1);
        final String save2 = imageService.addImageToRepository(tmpFileName, "PRODUCT1", image, Constants.PRODUCT_IMAGE_REPOSITORY_URL_PATTERN, "target/");
        final File destination2 = new File("target/product/P/PRODUCT1/" + save2);
        assertTrue(destination2.exists());
        assertTrue("At least some info must be in the file" , destination2.length() > 1000);
        assertFalse(save1.equals(save2));
    }

    @Test
    public void testDeleteImage() throws Exception {

        mockery.checking(new Expectations() {{
            allowing(imageNameStrategyResolver).getImageNameStrategy(Constants.PRODUCT_IMAGE_REPOSITORY_URL_PATTERN);
            will(returnValue(imageNameStrategy));
            allowing(languageService).getSupportedLanguages();
            will(returnValue(Arrays.asList("en", "uk", "ru")));
            allowing(seoImageDao).findByCriteria(with(any(Criterion.class)));
            will(returnValue(null));
            allowing(productDao).findSingleByNamedQuery(with(equal("PRODUCT.CODE.BY.IMAGE.NAME")), with(any(String.class)));
            will(returnValue("PRODUCT2"));
        }});

        imageService = new ImageServiceImpl(seoImageDao, imageNameStrategyResolver, "50x150", 255, 255, 255, false, 50, new IOProvider() {
            @Override
            public boolean supports(final String uri) {
                return true;
            }

            @Override
            public boolean exists(final String uri, final Map<String, Object> context) {
                return new File(uri).exists();
            }

            @Override
            public boolean isNewerThan(final String uriToCheck, final String uriToCheckAgainst, final Map<String, Object> context) {
                return false;
            }

            @Override
            public byte[] read(final String uri, final Map<String, Object> context) throws IOException {
                return FileUtils.readFileToByteArray(new File(uri));
            }

            @Override
            public void write(final String uri, final byte[] content, final Map<String, Object> context) throws IOException {
                FileUtils.writeByteArrayToFile(new File(uri), content);
            }

            @Override
            public void delete(final String uri, final Map<String, Object> context) throws IOException {
                assertTrue(new File(uri).delete());
            }
        });

        final String tmpFileName = "target/test/resources/some-seo-image-name_PRODUCT2.jpeg";

        byte[] image = Base64.decode(BASE64_ENCODED_JPEG_0);
        final String saved1 = imageService.addImageToRepository(tmpFileName, "PRODUCT2", image, Constants.PRODUCT_IMAGE_REPOSITORY_URL_PATTERN, "target/");
        final File destination1 = new File("target/product/P/PRODUCT2/" + saved1);
        assertTrue(destination1.exists());

        image = Base64.decode(BASE64_ENCODED_JPEG_1);
        final String saved2 = imageService.addImageToRepository(tmpFileName, "PRODUCT2", image, Constants.PRODUCT_IMAGE_REPOSITORY_URL_PATTERN, "target/");
        final File destination2 = new File("target/product/P/PRODUCT2/" + saved2);
        assertTrue(imageService.deleteImage(destination2.getName(), Constants.PRODUCT_IMAGE_REPOSITORY_URL_PATTERN, "target/"));
        assertTrue(destination1.exists());
        assertFalse(destination2.exists());
    }
}
