package org.yes.cart.service.image.impl;

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.service.image.ImageNameStrategy;

import static org.junit.Assert.assertEquals;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class BrandImageNameStrategyImplTest extends BaseCoreDBTestCase {

    private ImageNameStrategy imageNameStrategy;

    @Before
    public void setUp() throws Exception {
        imageNameStrategy = (ImageNameStrategy) ctx().getBean(ServiceSpringKeys.BRAND_IMAGE_NAME_STRATEGY);
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
}
