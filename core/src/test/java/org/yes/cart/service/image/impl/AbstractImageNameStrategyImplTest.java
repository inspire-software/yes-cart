package org.yes.cart.service.image.impl;

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.service.image.ImageNameStrategy;

import java.io.File;

import static org.junit.Assert.assertEquals;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class AbstractImageNameStrategyImplTest extends BaseCoreDBTestCase {

    private ImageNameStrategy imageNameStrategy;

    @Before
    public void setUp() throws Exception {
        imageNameStrategy = (ImageNameStrategy) ctx.getBean(ServiceSpringKeys.PRODUCT_IMAGE_NAME_STRATEGY);
    }

    @Test
    public void testGetFullFileNamePath() {
        assertEquals("file-name_CODE_a.jpej",
                imageNameStrategy.getFullFileNamePath("file-name_CODE_a.jpej", null));
        assertEquals("C" + File.separator + "CODE" + File.separator + "file-name_CODE_a.jpej",
                imageNameStrategy.getFullFileNamePath("file-name_CODE_a.jpej", "CODE"));
        assertEquals("10x30" + File.separator + "C" + File.separator + "CODE" + File.separator + "file-name_CODE_a.jpej",
                imageNameStrategy.getFullFileNamePath("file-name_CODE_a.jpej", "CODE", "10", "30"));
    }
}
