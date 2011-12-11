package org.yes.cart.service.image.impl;

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.Constants;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.service.image.ImageNameStrategy;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class ProductImageNameStrategyImplTest extends BaseCoreDBTestCase {

    private static final String[] FILE_NAMES = {
            "some_seo_image_file-name_PRODUCT-or-SKU-CODE_a.jpeg",
            "some_seo_image_file-name_PRODUCT1_b.jpeg",
            "some_seo_image_file-name_PROD+UCT1_c.jpeg",
            "some_seo_image_file_name_КОД-ПРОДУКТА_d.jpeg",
            "some-seo-image-file-name_ЕЩЕ-КОД-пРОДУКТА_e.jpg",
            "очень-вкусная-сосиска-с-камнями-от-Сваровски_-КОД-Сосики-_f.jpg",
            "Очень-Вкусная-Сосиска3-с-камнями-от-Булыжникова_ЕЩЕ-КОД-ПРОДУКТА!_g.jpg"
    };

    private ImageNameStrategy imageNameStrategy;

    @Before
    public void setUp() throws Exception {
        imageNameStrategy = (ImageNameStrategy) ctx().getBean(ServiceSpringKeys.PRODUCT_IMAGE_NAME_STRATEGY);
    }

    @Test
    public void testGetFileName() {
        assertEquals("1261644759_627724_russkaya-magiya.jpg",
                imageNameStrategy.getFileName("posts/2009-12/1261644759_627724_russkaya-magiya.jpg"));
        assertEquals("1261644759_627724_russkaya-magiya.jpg",
                imageNameStrategy.getFileName("posts/2009-12/1261644759_627724_russkaya-magiya.jpg?w=10&h=4"));
        assertEquals("1261644759_627724_russkaya-magiya.jpg",
                imageNameStrategy.getFileName("posts/2009-12/1261644759_627724_russkaya-magiya.jpg?w=10&h=4"));
        assertEquals("1261644759_627724_russkaya-magiya.jpg",
                imageNameStrategy.getFileName("posts/2009-12/1261644759_627724_russkaya-magiya.jpg?w=10&h=4~!@#$%^&*()_+"));
    }


    @Test
    public void testGetCode() {
        List<String> expectation = new ArrayList<String>() {{
            add("PRODUCT-or-SKU-CODE");
            add("PRODUCT1");
            add("PROD+UCT1");
            add("КОД-ПРОДУКТА");
            add("ЕЩЕ-КОД-пРОДУКТА");
            add("-КОД-Сосики-");
            add("ЕЩЕ-КОД-ПРОДУКТА!");
        }};


        for (String fileName : FILE_NAMES) {
            String code = imageNameStrategy.getCode(fileName);
            assertNotNull(code);
            assertFalse("Contains _ ", code.indexOf('_') > -1);
            assertTrue(fileName + " not in expectations. The code is " + code, expectation.contains(code));
            assertTrue(expectation.remove(code));
        }
        assertTrue(expectation.isEmpty());
        assertEquals("code",
                imageNameStrategy.getCode("seo_name_code_1.jpg"));
        assertEquals("code",
                imageNameStrategy.getCode("_code_1.jpg"));
        assertEquals("code",
                imageNameStrategy.getCode("code_1.jpg"));
        //test case to support file names without product or sku code
        assertEquals("SOBOT",
                imageNameStrategy.getCode("sobot-picture.jpeg"));
    }

    @Test
    public void testGetCodeForIncorrectFileName() {
        assertEquals(Constants.NO_IMAGE, imageNameStrategy.getCode(null));
        assertEquals(Constants.NO_IMAGE, imageNameStrategy.getCode(""));
        assertEquals(Constants.NO_IMAGE, imageNameStrategy.getCode("some-incorrect-code-in-file-name"));
    }
}
