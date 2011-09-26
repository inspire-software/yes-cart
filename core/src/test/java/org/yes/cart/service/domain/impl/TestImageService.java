package org.yes.cart.service.domain.impl;

import org.apache.commons.lang.StringUtils;
import org.dbunit.util.Base64;
import org.hibernate.criterion.Criterion;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.SeoImage;
import org.yes.cart.service.domain.SystemService;
import org.yes.cart.service.image.ImageNameStrategyResolver;
import org.yes.cart.service.image.impl.ProductImageNameStrategyImpl;
import org.yes.cart.constants.Constants;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
@RunWith(JMock.class)
public class TestImageService {

    private final Mockery mockery = new JUnit4Mockery();

    private ImageNameStrategyResolver imageNameStrategyResolver;

    private ImageServiceImpl imageService;

    private SystemService systemService;

    private ProductImageNameStrategyImpl productImageNameStrategy;

    private GenericDAO<SeoImage, Long> seoImageDao;


    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {


        imageNameStrategyResolver = mockery.mock(ImageNameStrategyResolver.class);
        
        systemService = mockery.mock(SystemService.class);

        seoImageDao = mockery.mock(GenericDAO.class);

        productImageNameStrategy = new ProductImageNameStrategyImpl(null, null);

        mockery.checking(new Expectations() {{

            allowing(imageNameStrategyResolver).getImageNameStrategy(StringUtils.EMPTY);
            will(returnValue(productImageNameStrategy));

            allowing(systemService).getImageRepositoryDirectory();
            will(returnValue("target/test/resources/imgrepo/"));

            allowing(seoImageDao).findByCriteria(with(any(Criterion.class)));
            will(returnValue(null));


        }});



        imageService = new ImageServiceImpl(
                seoImageDao, imageNameStrategyResolver, "50x150", 255, 255, 255
        );
    }

   
    @Test
    public void testResizeImageJPEG() {
        final String originalFileName = "src/test/resources/imgrepo/a/arbuz/speli_arbuz_arbuz_a.jpeg";
        final String desctinationFileName = "target/test/resources/imgrepo/50x150/a/arbuz/speli_arbuz_arbuz_a.jpeg";
        imageService.resizeImage(originalFileName, desctinationFileName, "50", "150");
        assertTrue((new File(desctinationFileName).exists()));
    }

    /**
     * Black border solving.
     * See private RenderedOp resizeImage(final RenderedImage image, final BigDecimal scale)
     */
    @Test
    public void testResizeImageJPEG2() {
        final String originalFileName = "src/test/resources/imgrepo/C/C01-00002-7B/C01-00002-7B_a.jpeg";
        final String desctinationFileName = "target/test/resources/imgrepo/200x200/C/C01-00002-7B/C01-00002-7B_a.jpeg";
        imageService.resizeImage(originalFileName, desctinationFileName, "200", "200");
        assertTrue((new File(desctinationFileName).exists()));
    }


    @Test
    public void testResizeImagePNG() {
        final String originalFileName = "src/test/resources/imgrepo/a/aron/aron_a.png";
        final String desctinationFileName = "target/test/resources/imgrepo/50x150/a/aron/aron_a.png";
        imageService.resizeImage(originalFileName, desctinationFileName, "50", "150");
        assertTrue((new File(desctinationFileName).exists()));
    }

    @Test
    public void testAddImageToRepository() {
        final String tmpFileName = "target/test/resources/some-seo-image-name_PRODUCT1.jpeg";

        byte [] image = Base64.decode(base64EncodedJpeg0);
        try {
            imageService.addImageToRepository(tmpFileName, "PRODUCT1", image, StringUtils.EMPTY);
            File destination = new File("P/PRODUCT1/some-seo-image-name_PRODUCT1.jpeg");
            assertTrue(destination.exists());
            
            image = Base64.decode(base64EncodedJpeg1);
            imageService.addImageToRepository(tmpFileName, "PRODUCT1", image, StringUtils.EMPTY);
            assertEquals(1097, destination.length());
        } catch (IOException e) {
            assertTrue(e.getMessage(),false);
        }
    }


    @Test
    public void testDeleteImage() {

        final String tmpFileName = "target/test/resources/some-seo-image-name_PRODUCT2.jpeg";

        byte [] image = Base64.decode(base64EncodedJpeg0);
        try {
            imageService.addImageToRepository(tmpFileName, "PRODUCT2", image, StringUtils.EMPTY);
            File destination = new File("P/PRODUCT2/some-seo-image-name_PRODUCT2.jpeg");
            assertTrue(destination.exists());

            image = Base64.decode(base64EncodedJpeg1);
            imageService.addImageToRepository(tmpFileName, "PRODUCT2", image, StringUtils.EMPTY);

            assertTrue(imageService.deleteImage(destination.getPath()));

            assertFalse(destination.exists());

        } catch (IOException e) {
            assertTrue(e.getMessage(),false);
        }

    }


    private String base64EncodedJpeg0 = "iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAAAXNSR0IArs4c6QAAAAZiS0dEAP8A/wD/oL2nkwAAAAlwSFlzAAALEwAACxMBAJqcGAAAAAd0SU1FB9oGEw8qFiJ+pnwAAAQbSURBVDgRARAE7/sAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEAAAAAAAAAAAAAAACMeHr/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAB0iIYBAAAAAAAAAAAAAAAAAgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEAAAAAAAAAAAAAAAAAAAAAAAAAAB0iIYBAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABAAAAAAAAAAAAAAAAjHh6/wAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAdIiGAQAAAAAAAAAAAAAAAAIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABAAAAAAAAAAAAAAAAAAAAAAAAAAAdIiGAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABAAAAAAAAAAAAAAAAjHh6/wAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAdIiGAQAAAAAAAAAAAAAAAAIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADRyDyIlm2rZAAAAAElFTkSuQmCC";
    private String base64EncodedJpeg1 = "/9j/4AAQSkZJRgABAQEAYABgAAD/4QBGRXhpZgAASUkqAAgAAAABAGmHBAABAAAAGgAAAAAAAAABAIaSAgASAAAALAAAAAAAAABDcmVhdGVkIHdpdGggR0lNUAD/2wBDAAgGBgcGBQgHBwcJCQgKDBQNDAsLDBkSEw8UHRofHh0aHBwgJC4nICIsIxwcKDcpLDAxNDQ0Hyc5PTgyPC4zNDL/2wBDAQkJCQwLDBgNDRgyIRwhMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjL/wAARCAAoACgDASIAAhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQAAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwD38kAZJwKzJryV5zGhKICB7nOD/WrV7PHFbuHdQWGAD3qJLSO4PnsXw53KA2Pocj6A1L7Ia7sZdXv2NUXzAX77jnj1NPttVtpo8vIkZBx8xwD9CaxNYt4xM6pIDuHA3ZIxzWcL2SGNEV49qDrnkjPT1xWbqNM0UE0d1RWDo8tyzpDMdzCMdG4UDA/l2orVO6uZtWdi9fIDcKxiDx+WwcnnuMf1rO0Us0/lSXb5iyyRjo6Ecfl1/EVsXC718kEjecH3HJx9OD/9asi50m5mkH2a4MZgjChhgZYdDj2AxUSTvdFRelmXJ7Czt7J2WJdwHDMMkc1yEnmPdmIBRMxwAUGSPr/U10cNxePCbR7ZUkAxI4z83HZe35mqlppwk1m3fcR5asxB439MY/PNZyXNaxcXy3ubVrF9n2rkOeDuHGdxGc/kPwoq8YwQAOMHPFFbpWMWOIBIOOnSgKFGAMCiimBHNEZUKq+wkYJxms+TTJY41lhnL3MeSpbgH2oopNJjTaL9vJJJEplTaxGSORRRRTQj/9k=";


}
