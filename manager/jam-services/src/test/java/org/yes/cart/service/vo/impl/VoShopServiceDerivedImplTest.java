/*
 * Copyright 2009 - 2016 Denys Pavlov, Igor Azarnyi
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

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;
import static org.junit.Assert.assertThat;


/**
 * Created by iazarnyi on 1/20/16.
 */
//@ContextConfiguration("/testApplicationContext.xml")
public class VoShopServiceDerivedImplTest extends BaseCoreDBTestCase {

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
        assertThat(voShopSeo.getDisplayMetadescriptions().size(), equalTo(0));
        assertThat(voShopSeo.getDisplayMetakeywords().size(), equalTo(0));
        assertThat(voShopSeo.getDisplayTitles().size(), equalTo(0));

        voShopSeo.getDisplayMetadescriptions().add(MutablePair.of("en", "Meta description"));
        voShopSeo.getDisplayMetakeywords().add(MutablePair.of("en", "Meta, key, word"));
        voShopSeo.getDisplayTitles().add(MutablePair.of("en", "Title"));

        voShopSeo = voShopService.update(voShopSeo);
        assertThat(voShopSeo.getDisplayMetadescriptions().size(), equalTo(1));
        assertThat(voShopSeo.getDisplayMetakeywords().size(), equalTo(1));
        assertThat(voShopSeo.getDisplayTitles().size(), equalTo(1));

        voShopSeo.getDisplayMetadescriptions().clear();
        voShopSeo.getDisplayMetakeywords().clear();
        voShopSeo.getDisplayTitles().clear();

        voShopSeo = voShopService.update(voShopSeo);
        assertThat(voShopSeo.getDisplayMetadescriptions().size(), equalTo(0));
        assertThat(voShopSeo.getDisplayMetakeywords().size(), equalTo(0));
        assertThat(voShopSeo.getDisplayTitles().size(), equalTo(0));
    }


    @Test
    public void testUpdateUrls() throws Exception {

        VoShop voShop = getTestVoShop("0002");
        voShop = voShopService.create(voShop);

        VoShopUrl voShopUrl = voShopService.getShopUrls(voShop.getShopId());
        assertTrue(voShopUrl.getUrls().isEmpty());
        voShopUrl.getUrls().add(new VoShopUrlDetail(0, "url", "theme", true));

        voShopUrl = voShopService.update(voShopUrl);
        assertThat(voShopUrl.getUrls().size(), equalTo(1));

        voShopUrl.getUrls().get(0).setUrl("url0");
        voShopUrl.getUrls().get(0).setTheme("theme0");
        voShopUrl = voShopService.update(voShopUrl);

        assertThat(voShopUrl.getUrls().get(0).getTheme(), equalTo("theme0"));
        assertThat(voShopUrl.getUrls().get(0).getUrl(), equalTo("url0"));

        voShopUrl.getUrls().clear();

        voShopUrl = voShopService.update(voShopUrl);
        assertTrue(voShopUrl.getUrls().isEmpty());

    }


    @Test
    public void testUpdateCurrency() throws Exception {


        VoShop voShop = getTestVoShop("0003");
        voShop = voShopService.create(voShop);

        VoShopSupportedCurrencies voSsc = voShopService.getShopCurrencies(voShop.getShopId());
        assertThat(voSsc.getSupported().size(), equalTo(0));

        voSsc.setSupported(Arrays.asList("USD,EUR".split(",")));
        voSsc = voShopService.update(voSsc);

        assertThat(voSsc.getSupported().size(), equalTo(2));
        assertThat(voSsc.getSupported().get(0), equalTo("USD"));
        assertThat(voSsc.getSupported().get(1), equalTo("EUR"));
    }

    private VoShop getTestVoShop(String suf) {
        VoShop voShop = new VoShop();
        voShop.setCode("vo-" + suf);
        voShop.setName("vo-" + suf + "-name");
        voShop.setFspointer("vo-" + suf + "-fspointer");
        return voShop;
    }


}