/*
 * Copyright 2009 Inspire-Software.com
 *
 *    Licensed under the Apache License, Version 2.0 (the 'License');
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an 'AS IS' BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.yes.cart.service.vo.impl;

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.domain.misc.MutablePair;
import org.yes.cart.domain.vo.*;
import org.yes.cart.service.vo.VoShopService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;


/**
 * Created by iazarnyi on 1/20/16.
 */
public class VoShopServiceImplTest extends BaseCoreDBTestCase {

    VoShopService voShopService = null;

    @Before
    public void setUp() {
        voShopService = (VoShopService) ctx().getBean("voShopService");
        super.setUp();
    }

    @Test
    public void testUpdateVoShopLocale() throws Exception {
        VoShop voShop = getTestVoShop("0001");
        voShop = voShopService.create(voShop);

        VoShopSeo voShopSeo = voShopService.getShopLocale(voShop.getShopId());
        assertNull(voShopSeo.getDisplayMetadescriptions());
        assertNull(voShopSeo.getDisplayMetakeywords());
        assertNull(voShopSeo.getDisplayTitles());

        voShopSeo.setDisplayMetadescriptions(new ArrayList(Collections.singletonList(MutablePair.of("en", "Meta description"))));
        voShopSeo.setDisplayMetakeywords(new ArrayList(Collections.singletonList(MutablePair.of("en", "Meta, key, word"))));
        voShopSeo.setDisplayTitles(new ArrayList(Collections.singletonList(MutablePair.of("en", "Title"))));

        voShopSeo = voShopService.update(voShopSeo);
        assertEquals(1, voShopSeo.getDisplayMetadescriptions().size());
        assertEquals(1, voShopSeo.getDisplayMetakeywords().size());
        assertEquals(1, voShopSeo.getDisplayTitles().size());

        voShopSeo.getDisplayMetadescriptions().clear();
        voShopSeo.getDisplayMetakeywords().clear();
        voShopSeo.getDisplayTitles().clear();

        voShopSeo = voShopService.update(voShopSeo);
        assertEquals(0, voShopSeo.getDisplayMetadescriptions().size());
        assertEquals(0, voShopSeo.getDisplayMetakeywords().size());
        assertEquals(0, voShopSeo.getDisplayTitles().size());
    }


    @Test
    public void testUpdateUrls() throws Exception {

        VoShop voShop = getTestVoShop("0002");
        voShop = voShopService.create(voShop);

        VoShopUrl voShopUrl = voShopService.getShopUrls(voShop.getShopId());
        assertTrue(voShopUrl.getUrls().isEmpty());
        voShopUrl.getUrls().add(new VoShopUrlDetail(0, "url", "theme", true));

        voShopUrl = voShopService.update(voShopUrl);
        assertEquals(1, voShopUrl.getUrls().size());

        voShopUrl.getUrls().get(0).setUrl("url0");
        voShopUrl.getUrls().get(0).setTheme("theme0");
        voShopUrl = voShopService.update(voShopUrl);

        assertEquals("theme0", voShopUrl.getUrls().get(0).getTheme());
        assertEquals("url0", voShopUrl.getUrls().get(0).getUrl());

        voShopUrl.getUrls().clear();

        voShopUrl = voShopService.update(voShopUrl);
        assertTrue(voShopUrl.getUrls().isEmpty());

    }


    @Test
    public void testUpdateCurrency() throws Exception {


        VoShop voShop = getTestVoShop("0003");
        voShop = voShopService.create(voShop);

        VoShopSupportedCurrencies voSsc = voShopService.getShopCurrencies(voShop.getShopId());
        assertEquals(0, voSsc.getSupported().size());

        voSsc.setSupported(Arrays.asList(MutablePair.of("USD", "US Dollar"), MutablePair.of("EUR", "Euro")));
        voSsc = voShopService.update(voSsc);

        assertEquals(2, voSsc.getSupported().size());
        assertEquals("USD", voSsc.getSupported().get(0).getFirst());
        assertEquals("EUR", voSsc.getSupported().get(1).getFirst());
    }

    private VoShop getTestVoShop(String suf) {
        VoShop voShop = new VoShop();
        voShop.setCode("vo-" + suf);
        voShop.setName("vo-" + suf + "-name");
        voShop.setFspointer("vo-" + suf + "-fspointer");
        return voShop;
    }


}