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

package org.yes.cart.service.image.impl;

import org.junit.Test;
import org.yes.cart.constants.Constants;
import org.yes.cart.service.image.ImageNameStrategy;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class AbstractImageNameStrategyImplTest {

    @Test
    public void testResolveRelativeInternalFileNamePath() {

        final ImageNameStrategy imageNameStrategy = new AbstractImageNameStrategyImpl("/root/img/", "root") {
            @Override
            public String resolveObjectCodeInternal(final String url) {
                return null;
            }
        };

        assertEquals("file-name_CODE_a.jpeg",
                imageNameStrategy.resolveFileName("file-name_CODE_a.jpeg"));
        assertEquals("file-name_CODE_a.jpeg",
                imageNameStrategy.resolveFileName("somepath" + File.separator + "file-name_CODE_a.jpeg"));

        assertEquals("1261644759_627724_russkaya-magiya.jpg",
                imageNameStrategy.resolveFileName("posts/2009-12/1261644759_627724_russkaya-magiya.jpg"));
        assertEquals("1261644759_627724_russkaya-magiya.jpg",
                imageNameStrategy.resolveFileName("posts/2009-12/1261644759_627724_russkaya-magiya.jpg?w=10&h=4"));
        assertEquals("1261644759_627724_russkaya-magiya.jpg",
                imageNameStrategy.resolveFileName("posts/2009-12/1261644759_627724_russkaya-magiya.jpg?w=10&h=4"));
        assertEquals("1261644759_627724_russkaya-magiya.jpg",
                imageNameStrategy.resolveFileName("posts/2009-12/1261644759_627724_russkaya-magiya.jpg?w=10&h=4~!@#$%^&*()_+"));


        assertEquals("root" + File.separator + "file-name_CODE_a.jpeg",
                imageNameStrategy.resolveRelativeInternalFileNamePath("file-name_CODE_a.jpeg", null));

        assertEquals("root" + File.separator + "C" + File.separator + "CODE" + File.separator + "file-name_CODE_a.jpeg",
                imageNameStrategy.resolveRelativeInternalFileNamePath("file-name_CODE_a.jpeg", "CODE"));

        assertEquals("root" + File.separator + "10x30" + File.separator + "C" + File.separator + "CODE" + File.separator + "file-name_CODE_a.jpeg",
                imageNameStrategy.resolveRelativeInternalFileNamePath("file-name_CODE_a.jpeg", "CODE", "10", "30"));

    }



    @Test
    public void testGetCode() {

        final String[][] testFilenames = {
                { "some_seo_image_file-name_PRODUCT-or-SKU-CODE_a.jpeg", "PRODUCT-or-SKU-CODE" },
                { "some_seo_image_file-name_PRODUCT1_b.jpeg", "PRODUCT1" },
                { "some_seo_image_file-name_PROD+UCT1_c.jpeg", "PROD+UCT1" },
                { "some_seo_image_file_name_КОД-ПРОДУКТА_d.jpeg", "КОД-ПРОДУКТА" },
                { "some-seo-image-file-name_ЕЩЕ-КОД-пРОДУКТА_e.jpg", "ЕЩЕ-КОД-пРОДУКТА" },
                { "очень-вкусная-сосиска-с-камнями-от-Сваровски_-КОД-Сосики-_f.jpg", "-КОД-Сосики-" },
                { "Очень-Вкусная-Сосиска3-с-камнями-от-Булыжникова_ЕЩЕ-КОД-ПРОДУКТА!_g.jpg", "ЕЩЕ-КОД-ПРОДУКТА!" }
        };


        final ImageNameStrategy imageNameStrategy = new AbstractImageNameStrategyImpl("/root/img/", "root") {
            @Override
            public String resolveObjectCodeInternal(final String url) {
                return null;
            }
        };

        for (String[] fileName : testFilenames) {
            String code = imageNameStrategy.resolveObjectCode(fileName[0]);
            assertNotNull(code);
            assertFalse("Contains _ ", code.indexOf('_') > -1);
            assertEquals(fileName[0] + " not in expectations. The code is " + code, code, fileName[1]);
        }

        assertEquals("code", imageNameStrategy.resolveObjectCode("seo_name_code_a.jpg"));
        assertEquals("code", imageNameStrategy.resolveObjectCode("_code_b.jpg"));
        assertEquals(Constants.NO_IMAGE, imageNameStrategy.resolveObjectCode("code_c.jpg"));

    }

    @Test
    public void testGetCodeForIncorrectFileName() {

        final ImageNameStrategy imageNameStrategy = new AbstractImageNameStrategyImpl("/root/img/", "root") {
            @Override
            public String resolveObjectCodeInternal(final String url) {
                return null;
            }
        };

        assertEquals(Constants.NO_IMAGE, imageNameStrategy.resolveObjectCode(null));
        assertEquals(Constants.NO_IMAGE, imageNameStrategy.resolveObjectCode(""));
        assertEquals(Constants.NO_IMAGE, imageNameStrategy.resolveObjectCode("some-incorrect-code-in-file-name"));

    }


}
