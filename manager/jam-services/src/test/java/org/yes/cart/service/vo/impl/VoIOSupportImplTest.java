package org.yes.cart.service.vo.impl;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 08/08/2016
 * Time: 10:20
 */
public class VoIOSupportImplTest {

    @Test
    public void testEnsureCorrectFileName() throws Exception {

        // Valid names
        assertEquals("somefile_SHOP10_a.png", VoIOSupportImpl.ensureCorrectFileName("somefile_SHOP10_a.png", "SHOP10", "SHOP_IMAGE0"));
        assertEquals("somefile_SHOP10_b.png", VoIOSupportImpl.ensureCorrectFileName("somefile_SHOP10_b.png", "SHOP10", "SHOP_IMAGE1"));
        assertEquals("somefile_SHOP10_b_en.png", VoIOSupportImpl.ensureCorrectFileName("somefile_SHOP10_b_en.png", "SHOP10", "SHOP_IMAGE1_en"));

        // Add suffix names
        assertEquals("somefile_SHOP10_a.png", VoIOSupportImpl.ensureCorrectFileName("somefile.png", "SHOP10", "SHOP_IMAGE0"));
        assertEquals("somefile_SHOP10_b.png", VoIOSupportImpl.ensureCorrectFileName("somefile.png", "SHOP10", "SHOP_IMAGE1"));
        assertEquals("somefile_SHOP10_b_en.png", VoIOSupportImpl.ensureCorrectFileName("somefile.png", "SHOP10", "SHOP_IMAGE1_en"));

        // Correct suffix
        assertEquals("somefile_SHOP10_a.png", VoIOSupportImpl.ensureCorrectFileName("somefile_SHOP10_c.png", "SHOP10", "SHOP_IMAGE0"));
        assertEquals("somefile_SHOP10_b.png", VoIOSupportImpl.ensureCorrectFileName("somefile_SHOP10_c_ru.png", "SHOP10", "SHOP_IMAGE1"));
        assertEquals("somefile_SHOP10_b_en.png", VoIOSupportImpl.ensureCorrectFileName("somefile_SHOP10_c_en.png", "SHOP10", "SHOP_IMAGE1_en"));

        // Remove spaces names
        assertEquals("some-fi-le_SHOP10_a.png", VoIOSupportImpl.ensureCorrectFileName("some fi le_SHOP10_a.png", "SHOP10", "SHOP_IMAGE0"));
        assertEquals("some-fi-le_SHOP10_a.png", VoIOSupportImpl.ensureCorrectFileName("some fi le.png", "SHOP10", "SHOP_IMAGE0"));
        assertEquals("some-fi-le_SHOP10_a.png", VoIOSupportImpl.ensureCorrectFileName("some fi le_SHOP10_c.png", "SHOP10", "SHOP_IMAGE0"));

    }
}