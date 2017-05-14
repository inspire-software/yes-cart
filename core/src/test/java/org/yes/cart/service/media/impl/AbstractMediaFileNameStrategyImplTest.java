/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
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

package org.yes.cart.service.media.impl;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.yes.cart.constants.Constants;
import org.yes.cart.service.media.MediaFileNameStrategy;
import org.yes.cart.service.misc.LanguageService;

import java.io.File;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class AbstractMediaFileNameStrategyImplTest {

    private Mockery context = new JUnit4Mockery();

    @Test
    public void testResolveRelativeInternalFileNamePath() {

        final LanguageService languageService = context.mock(LanguageService.class, "languageService");

        context.checking(new Expectations() {{
            allowing(languageService).getSupportedLanguages(); will(returnValue(Arrays.asList("en", "ru", "uk")));
        }});

        final MediaFileNameStrategy mediaFileNameStrategy = new AbstractMediaFileNameStrategyImpl("/root/img/", "root", languageService) {
            @Override
            public String resolveObjectCodeInternal(final String url) {
                return null;
            }
        };

        // Resolving filename
        assertEquals("file-name_CODE_a.jpeg",
                mediaFileNameStrategy.resolveFileName("file-name_CODE_a.jpeg"));
        assertEquals("file-name_CODE_a.jpeg",
                mediaFileNameStrategy.resolveFileName("somepath" + File.separator + "file-name_CODE_a.jpeg"));

        assertEquals("1261644759_627724_russkaya-magiya.jpg",
                mediaFileNameStrategy.resolveFileName("posts/2009-12/1261644759_627724_russkaya-magiya.jpg"));
        assertEquals("1261644759_627724_russkaya-magiya.jpg",
                mediaFileNameStrategy.resolveFileName("posts/2009-12/1261644759_627724_russkaya-magiya.jpg?w=10&h=4"));
        assertEquals("1261644759_627724_russkaya-magiya.jpg",
                mediaFileNameStrategy.resolveFileName("posts/2009-12/1261644759_627724_russkaya-magiya.jpg?w=10&h=4"));
        assertEquals("1261644759_627724_russkaya-magiya.jpg",
                mediaFileNameStrategy.resolveFileName("posts/2009-12/1261644759_627724_russkaya-magiya.jpg?w=10&h=4~!@#$%^&*()_+"));

        // Upper case code 'CODE' results in upper case 'C' intermediate directory
        assertEquals("root" + File.separator + "file-name_CODE_a.jpeg",
                mediaFileNameStrategy.resolveRelativeInternalFileNamePath("file-name_CODE_a.jpeg", null, "en"));

        assertEquals("root" + File.separator + "file-name_CODE_a.jpeg",
                mediaFileNameStrategy.resolveRelativeInternalFileNamePath("file-name_CODE_a.jpeg", null, null));

        assertEquals("root" + File.separator + "C" + File.separator + "CODE" + File.separator + "file-name_CODE_a.jpeg",
                mediaFileNameStrategy.resolveRelativeInternalFileNamePath("file-name_CODE_a.jpeg", "CODE", "en"));

        assertEquals("root" + File.separator + "C" + File.separator + "CODE" + File.separator + "file-name_CODE_a.jpeg",
                mediaFileNameStrategy.resolveRelativeInternalFileNamePath("file-name_CODE_a.jpeg", "CODE", null));

        assertEquals("root" + File.separator + "10x30" + File.separator + "C" + File.separator + "CODE" + File.separator + "file-name_CODE_a.jpeg",
                mediaFileNameStrategy.resolveRelativeInternalFileNamePath("file-name_CODE_a.jpeg", "CODE", "en", "10", "30"));

        assertEquals("root" + File.separator + "10x30" + File.separator + "C" + File.separator + "CODE" + File.separator + "file-name_CODE_a.jpeg",
                mediaFileNameStrategy.resolveRelativeInternalFileNamePath("file-name_CODE_a.jpeg", "CODE", null, "10", "30"));

        // Lower case code 'code' results in upper case 'C' intermediate directory
        assertEquals("root" + File.separator + "file-name_code_a.jpeg",
                mediaFileNameStrategy.resolveRelativeInternalFileNamePath("file-name_code_a.jpeg", null, "en"));

        assertEquals("root" + File.separator + "file-name_code_a.jpeg",
                mediaFileNameStrategy.resolveRelativeInternalFileNamePath("file-name_code_a.jpeg", null, null));

        assertEquals("root" + File.separator + "C" + File.separator + "code" + File.separator + "file-name_code_a.jpeg",
                mediaFileNameStrategy.resolveRelativeInternalFileNamePath("file-name_code_a.jpeg", "code", "en"));

        assertEquals("root" + File.separator + "C" + File.separator + "code" + File.separator + "file-name_code_a.jpeg",
                mediaFileNameStrategy.resolveRelativeInternalFileNamePath("file-name_code_a.jpeg", "code", null));

        assertEquals("root" + File.separator + "10x30" + File.separator + "C" + File.separator + "code" + File.separator + "file-name_code_a.jpeg",
                mediaFileNameStrategy.resolveRelativeInternalFileNamePath("file-name_code_a.jpeg", "code", "en", "10", "30"));

        assertEquals("root" + File.separator + "10x30" + File.separator + "C" + File.separator + "code" + File.separator + "file-name_code_a.jpeg",
                mediaFileNameStrategy.resolveRelativeInternalFileNamePath("file-name_code_a.jpeg", "code", null, "10", "30"));

    }



    @Test
    public void testGetCode() {

        final LanguageService languageService = context.mock(LanguageService.class, "languageService");

        context.checking(new Expectations() {{
            allowing(languageService).getSupportedLanguages(); will(returnValue(Arrays.asList("en", "ru", "uk")));
        }});

        final String[][] testFilenames = {
                { "some_seo_image_file-name_PRODUCT-or-SKU-CODE_a.jpeg", "PRODUCT-or-SKU-CODE" },
                { "some_seo_image_file-name_PRODUCT-or-SKU-CODE_a_ru.jpeg", "PRODUCT-or-SKU-CODE" },
                { "some_seo_image_file-name_PRODUCT1_b.jpeg", "PRODUCT1" },
                { "some_seo_image_file-name_PRODUCT1_b_en.jpeg", "PRODUCT1" },
                { "some_seo_image_file-name_PROD+UCT1_c.jpeg", "PROD+UCT1" },
                { "some_seo_image_file-name_PROD+UCT1_c_uk.jpeg", "PROD+UCT1" },
                { "some_seo_image_file_name_КОД-ПРОДУКТА_d.jpeg", "КОД-ПРОДУКТА" },
                { "some_seo_image_file_name_КОД-ПРОДУКТА_d_ru.jpeg", "КОД-ПРОДУКТА" },
                { "some-seo-image-file-name_ЕЩЕ-КОД-пРОДУКТА_e.jpg", "ЕЩЕ-КОД-пРОДУКТА" },
                { "some-seo-image-file-name_ЕЩЕ-КОД-пРОДУКТА_e_en.jpg", "ЕЩЕ-КОД-пРОДУКТА" },
                { "очень-вкусная-сосиска-с-камнями-от-Сваровски_-КОД-Сосики-_f.jpg", "-КОД-Сосики-" },
                { "очень-вкусная-сосиска-с-камнями-от-Сваровски_-КОД-Сосики-_f_uk.jpg", "-КОД-Сосики-" },
                { "Очень-Вкусная-Сосиска3-с-камнями-от-Булыжникова_ЕЩЕ-КОД-ПРОДУКТА!_g.jpg", "ЕЩЕ-КОД-ПРОДУКТА!" },
                { "Очень-Вкусная-Сосиска3-с-камнями-от-Булыжникова_ЕЩЕ-КОД-ПРОДУКТА!_g_ru.jpg", "ЕЩЕ-КОД-ПРОДУКТА!" }
        };


        final MediaFileNameStrategy mediaFileNameStrategy = new AbstractMediaFileNameStrategyImpl("/root/img/", "root", languageService) {
            @Override
            public String resolveObjectCodeInternal(final String url) {
                return null;
            }
        };

        for (String[] fileName : testFilenames) {
            String code = mediaFileNameStrategy.resolveObjectCode(fileName[0]);
            assertNotNull(code);
            assertFalse("Contains _ ", code.indexOf('_') > -1);
            assertEquals(fileName[0] + " not in expectations. The code is " + code, code, fileName[1]);
        }

        assertEquals("code", mediaFileNameStrategy.resolveObjectCode("seo_name_code_a.jpg"));
        assertEquals("code", mediaFileNameStrategy.resolveObjectCode("seo_name_code_a_ru.jpg"));
        assertEquals("code", mediaFileNameStrategy.resolveObjectCode("_code_b.jpg"));
        assertEquals("code", mediaFileNameStrategy.resolveObjectCode("_code_b_ru.jpg"));
        assertEquals(Constants.NO_IMAGE, mediaFileNameStrategy.resolveObjectCode("code_c.jpg"));
        assertEquals(Constants.NO_IMAGE, mediaFileNameStrategy.resolveObjectCode("code_c_ru.jpg"));

    }

    @Test
    public void testGetCodeForIncorrectFileName() {

        final LanguageService languageService = context.mock(LanguageService.class, "languageService");

        context.checking(new Expectations() {{
            allowing(languageService).getSupportedLanguages(); will(returnValue(Arrays.asList("en", "ru", "uk")));
        }});

        final MediaFileNameStrategy mediaFileNameStrategy = new AbstractMediaFileNameStrategyImpl("/root/img/", "root", languageService) {
            @Override
            public String resolveObjectCodeInternal(final String url) {
                return null;
            }
        };

        assertEquals(Constants.NO_IMAGE, mediaFileNameStrategy.resolveObjectCode(null));
        assertEquals(Constants.NO_IMAGE, mediaFileNameStrategy.resolveObjectCode(""));
        assertEquals(Constants.NO_IMAGE, mediaFileNameStrategy.resolveObjectCode("some-incorrect-code-in-file-name"));

    }


    @Test
    public void testGetLocale() {

        final LanguageService languageService = context.mock(LanguageService.class, "languageService");

        context.checking(new Expectations() {{
            allowing(languageService).getSupportedLanguages(); will(returnValue(Arrays.asList("en", "ru", "uk")));
        }});

        final String[][] testFilenames = {
                { "some_seo_image_file-name_PRODUCT-or-SKU-CODE_a_ru.jpeg", "ru" },
                { "some_seo_image_file-name_PRODUCT1_b_en.jpeg", "en" },
                { "some_seo_image_file-name_PROD+UCT1_c_uk.jpeg", "uk" },
                { "some_seo_image_file_name_КОД-ПРОДУКТА_d.jpeg", null },
                { "some-seo-image-file-name_ЕЩЕ-КОД-пРОДУКТА_e_zu.jpg", null },
                { "очень-вкусная-сосиска-с-камнями-от-Сваровски_-КОД-Сосики-_ru.jpg", "ru" },
                { "Очень-Вкусная-Сосиска3-с-камнями-от-Булыжникова_ЕЩЕ-КОД-ПРОДУКТА!_ru.jpg", "ru" }
        };


        final MediaFileNameStrategy mediaFileNameStrategy = new AbstractMediaFileNameStrategyImpl("/root/img/", "root", languageService) {
            @Override
            public String resolveObjectCodeInternal(final String url) {
                return null;
            }
        };

        for (String[] fileName : testFilenames) {
            String language = mediaFileNameStrategy.resolveLocale(fileName[0]);
            assertEquals(fileName[0] + " not in expectations. The code is " + language, language, fileName[1]);
        }

        assertEquals("en", mediaFileNameStrategy.resolveLocale("seo_name_code_en.jpg"));
        assertEquals("en", mediaFileNameStrategy.resolveLocale("_code_en.jpg"));
        assertEquals("en", mediaFileNameStrategy.resolveLocale("code_en.jpg"));
        assertEquals(null, mediaFileNameStrategy.resolveLocale("code_zu.jpg"));

    }

    @Test
    public void testGetSuffix() {

        final LanguageService languageService = context.mock(LanguageService.class, "languageService");

        context.checking(new Expectations() {{
            allowing(languageService).getSupportedLanguages(); will(returnValue(Arrays.asList("en", "ru", "uk")));
        }});

        final String[][] testFilenames = {
                { "some_seo_image_file-name_PRODUCT-or-SKU-CODE_a.jpeg", "0" },
                { "some_seo_image_file-name_PRODUCT-or-SKU-CODE_a_ru.jpeg", "0" },
                { "some_seo_image_file-name_PRODUCT1_b.jpeg", "1" },
                { "some_seo_image_file-name_PRODUCT1_b_en.jpeg", "1" },
                { "some_seo_image_file-name_PROD+UCT1_c.jpeg", "2" },
                { "some_seo_image_file-name_PROD+UCT1_c_uk.jpeg", "2" },
                { "some_seo_image_file_name_КОД-ПРОДУКТА_d.jpeg", "3" },
                { "some_seo_image_file_name_КОД-ПРОДУКТА_d_ru.jpeg", "3" },
                { "some-seo-image-file-name_ЕЩЕ-КОД-пРОДУКТА_e.jpg", "4" },
                { "some-seo-image-file-name_ЕЩЕ-КОД-пРОДУКТА_e_en.jpg", "4" },
                { "очень-вкусная-сосиска-с-камнями-от-Сваровски_-КОД-Сосики-_f.jpg", "5" },
                { "очень-вкусная-сосиска-с-камнями-от-Сваровски_-КОД-Сосики-_f_uk.jpg", "5" },
                { "Очень-Вкусная-Сосиска3-с-камнями-от-Булыжникова_ЕЩЕ-КОД-ПРОДУКТА!_g.jpg", "6" },
                { "Очень-Вкусная-Сосиска3-с-камнями-от-Булыжникова_ЕЩЕ-КОД-ПРОДУКТА!_g_ru.jpg", "6" }
        };


        final MediaFileNameStrategy mediaFileNameStrategy = new AbstractMediaFileNameStrategyImpl("/root/img/", "root", languageService) {
            @Override
            public String resolveObjectCodeInternal(final String url) {
                return null;
            }
        };

        for (String[] fileName : testFilenames) {
            String code = mediaFileNameStrategy.resolveSuffix(fileName[0]);
            assertNotNull(code);
            assertFalse("Contains _ ", code.indexOf('_') > -1);
            assertEquals(fileName[0] + " not in expectations. The code is " + code, code, fileName[1]);
        }

        assertEquals("2", mediaFileNameStrategy.resolveSuffix("seo_name_code_c.jpg"));
        assertEquals("2", mediaFileNameStrategy.resolveSuffix("seo_name_code_c_ru.jpg"));
        assertEquals("2", mediaFileNameStrategy.resolveSuffix("_code_c.jpg"));
        assertEquals("2", mediaFileNameStrategy.resolveSuffix("_code_c_ru.jpg"));
        assertEquals("0", mediaFileNameStrategy.resolveSuffix("code.jpg"));
        assertEquals("0", mediaFileNameStrategy.resolveSuffix("code_ru.jpg"));

    }

    @Test
    public void testCreateRollingFileName() throws Exception {

        final LanguageService languageService = context.mock(LanguageService.class, "languageService");

        context.checking(new Expectations() {{
            allowing(languageService).getSupportedLanguages(); will(returnValue(Arrays.asList("en", "ru", "uk")));
        }});


        final MediaFileNameStrategy mediaFileNameStrategy = new AbstractMediaFileNameStrategyImpl("/root/img/", "root", languageService) {
            @Override
            public String resolveObjectCodeInternal(final String url) {
                return null;
            }
        };


        assertEquals("some-file-name-1_a.jpg", mediaFileNameStrategy.createRollingFileName("some-file-name.jpg", Constants.NO_IMAGE, "0", null));
        assertEquals("some-file-name-2_a.jpg", mediaFileNameStrategy.createRollingFileName("some-file-name-1.jpg", Constants.NO_IMAGE, "0", null));
        assertEquals("some-file-name-3_a.jpg", mediaFileNameStrategy.createRollingFileName("some-file-name-2.jpg", Constants.NO_IMAGE, "0", null));
        assertEquals("some-file-name-10_a.jpg", mediaFileNameStrategy.createRollingFileName("some-file-name-9.jpg", Constants.NO_IMAGE, "0", null));
        assertEquals("some-file-name-11_a.jpg", mediaFileNameStrategy.createRollingFileName("some-file-name-10.jpg", Constants.NO_IMAGE, "0", null));
        assertEquals("some-file-name-12_a.jpg", mediaFileNameStrategy.createRollingFileName("some-file-name-11.jpg", Constants.NO_IMAGE, "0", null));


        assertEquals("some-file-name-1_code_a.jpg", mediaFileNameStrategy.createRollingFileName("some-file-name_code_a.jpg", "code", "0", null));
        assertEquals("some-file-name-2_code_a.jpg", mediaFileNameStrategy.createRollingFileName("some-file-name-1_code_a.jpg", "code", "0", null));
        assertEquals("some-file-name-3_code_a.jpg", mediaFileNameStrategy.createRollingFileName("some-file-name-2_code_a.jpg", "code", "0", null));
        assertEquals("some-file-name-10_code_a.jpg", mediaFileNameStrategy.createRollingFileName("some-file-name-9_code_a.jpg", "code", "0", null));
        assertEquals("some-file-name-11_code_a.jpg", mediaFileNameStrategy.createRollingFileName("some-file-name-10_code_a.jpg", "code", "0", null));
        assertEquals("some-file-name-12_code_a.jpg", mediaFileNameStrategy.createRollingFileName("some-file-name-11_code_a.jpg", "code", "0", null));

        assertEquals("some-file-name-1_code_b_en.jpg", mediaFileNameStrategy.createRollingFileName("some-file-name_code_b_en.jpg", "code", "1", "en"));
        assertEquals("some-file-name-2_code_b_en.jpg", mediaFileNameStrategy.createRollingFileName("some-file-name-1_code_b_en.jpg", "code", "1", "en"));
        assertEquals("some-file-name-3_code_b_en.jpg", mediaFileNameStrategy.createRollingFileName("some-file-name-2_code_b_en.jpg", "code", "1", "en"));
        assertEquals("some-file-name-10_code_b_en.jpg", mediaFileNameStrategy.createRollingFileName("some-file-name-9_code_b_en.jpg", "code", "1", "en"));
        assertEquals("some-file-name-11_code_b_en.jpg", mediaFileNameStrategy.createRollingFileName("some-file-name-10_code_b_en.jpg", "code", "1", "en"));
        assertEquals("some-file-name-12_code_b_en.jpg", mediaFileNameStrategy.createRollingFileName("some-file-name-11_code_b_en.jpg", "code", "1", "en"));

    }
}
