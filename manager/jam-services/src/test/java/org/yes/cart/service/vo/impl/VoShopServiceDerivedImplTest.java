package org.yes.cart.service.vo.impl;

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.domain.misc.MutablePair;
import org.yes.cart.domain.vo.VoShop;
import org.yes.cart.domain.vo.VoShopLocale;
import org.yes.cart.service.vo.VoShopService;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;


/**
 * Created by iazarnyi on 1/20/16.
 */
//@ContextConfiguration("/testApplicationContext.xml")
public class VoShopServiceDerivedImplTest  extends BaseCoreDBTestCase {

    VoShopService voShopService = null;

    @Before
    public void setUp() {
        voShopService = (VoShopService) ctx().getBean("voShopService");
        super.setUp();
    }

    @Test
    public void testUpdateVoShopLocale() throws Exception {
        VoShop voShop = new VoShop();
        voShop.setCode("vo-0001");
        voShop.setName("vo-0001-name");
        voShop.setFspointer("vo-0001-fspointer");
        voShop = voShopService.create(voShop);

        VoShopLocale voShopLocale = voShopService.getShopLocale(voShop.getShopId());
        assertThat(voShopLocale.getDisplayMetadescriptions().size(), equalTo(0));
        assertThat(voShopLocale.getDisplayMetakeywords().size(), equalTo(0));
        assertThat(voShopLocale.getDisplayTitles().size(), equalTo(0));

        voShopLocale.getDisplayMetadescriptions().add(MutablePair.of("en", "Meta description"));
        voShopLocale.getDisplayMetakeywords().add(MutablePair.of("en", "Meta, key, word"));
        voShopLocale.getDisplayTitles().add(MutablePair.of("en", "Title"));

        voShopLocale = voShopService.update(voShopLocale);
        assertThat(voShopLocale.getDisplayMetadescriptions().size(), equalTo(1));
        assertThat(voShopLocale.getDisplayMetakeywords().size(), equalTo(1));
        assertThat(voShopLocale.getDisplayTitles().size(), equalTo(1));

        voShopLocale.getDisplayMetadescriptions().clear();
        voShopLocale.getDisplayMetakeywords().clear();
        voShopLocale.getDisplayTitles().clear();

        voShopLocale = voShopService.update(voShopLocale);
        assertThat(voShopLocale.getDisplayMetadescriptions().size(), equalTo(0));
        assertThat(voShopLocale.getDisplayMetakeywords().size(), equalTo(0));
        assertThat(voShopLocale.getDisplayTitles().size(), equalTo(0));
    }



}