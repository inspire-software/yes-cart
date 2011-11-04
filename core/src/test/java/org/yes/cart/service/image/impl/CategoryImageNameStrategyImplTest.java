package org.yes.cart.service.image.impl;

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.service.domain.impl.BaseCoreDBTestCase;
import org.yes.cart.service.image.ImageNameStrategy;

import static org.junit.Assert.assertEquals;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class CategoryImageNameStrategyImplTest extends BaseCoreDBTestCase {

    private ImageNameStrategy imageNameStrategy;

    @Before
    public void setUp() throws Exception {
        imageNameStrategy = (ImageNameStrategy) ctx.getBean(ServiceSpringKeys.CATEGORY_IMAGE_NAME_STRATEGY);
    }

    @Test
    public void testGetFileName() {
        assertEquals(
                /*Constants.CATEGOTY_IMAGE_FILE_PREFIX + File.separator +*/ "1261644759_627724_russkaya-magiya.jpg",
                imageNameStrategy.getFileName("posts/2009-12/1261644759_627724_russkaya-magiya.jpg"));
        assertEquals(
                /*Constants.CATEGOTY_IMAGE_FILE_PREFIX + File.separator + */"1261644759_627724_russkaya-magiya.jpg",
                imageNameStrategy.getFileName("posts/2009-12/1261644759_627724_russkaya-magiya.jpg?w=10&h=4"));
        assertEquals(
                /* Constants.CATEGOTY_IMAGE_FILE_PREFIX + File.separator +*/ "1261644759_627724_russkaya-magiya.jpg",
                imageNameStrategy.getFileName("posts/2009-12/1261644759_627724_russkaya-magiya.jpg?w=10&h=4"));
        assertEquals(
                /*Constants.CATEGOTY_IMAGE_FILE_PREFIX + File.separator +*/ "1261644759_627724_russkaya-magiya.jpg",
                imageNameStrategy.getFileName("posts/2009-12/1261644759_627724_russkaya-magiya.jpg?w=10&h=4~!@#$%^&*()_+"));
    }
}
