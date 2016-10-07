package org.yes.cart.domain.vo.converter;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.yes.cart.domain.dto.ProductTypeAttrDTO;
import org.yes.cart.domain.misc.MutablePair;
import org.yes.cart.domain.vo.VoProductTypeAttrNavigationRanges;

import java.io.File;
import java.util.List;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * User: denispavlov
 * Date: 22/08/2016
 * Time: 11:14
 */
public class StringToProductTypeAttrNavigationRangesConverterTest {

    private final Mockery context = new JUnit4Mockery();

    @Test
    public void testConvert() throws Exception {

        final ProductTypeAttrDTO dto = this.context.mock(ProductTypeAttrDTO.class, "dto");
        final ProductTypeAttrDTO dto2 = this.context.mock(ProductTypeAttrDTO.class, "dto2");

        final String xml = new Scanner(new File("src/test/resources/weight_filtering_example.xml")).useDelimiter("\\Z").next();

        this.context.checking(new Expectations() {{
            allowing(dto).getRangeNavigation();
            will(returnValue(xml));
            allowing(dto).setRangeNavigation(with(any(String.class)));
        }});

        final StringToProductTypeAttrNavigationRangesConverter converter = new StringToProductTypeAttrNavigationRangesConverter();

        final VoProductTypeAttrNavigationRanges rl = (VoProductTypeAttrNavigationRanges) converter.convertToDto(dto, null);

        assertNotNull(rl);
        assertEquals(4, rl.getRanges().size());

        assertEquals("0.10-1.00", (rl.getRanges().get(0)).getRange());

        final List<MutablePair<String, String>> i18n = (rl.getRanges().get(0)).getDisplayVals();
        assertNotNull(i18n);
        assertEquals(2, i18n.size());
        assertEquals("en", i18n.get(0).getFirst());
        assertEquals("100 g - 1 kg", i18n.get(0).getSecond());
        assertEquals("ru", i18n.get(1).getFirst());
        assertEquals("100 g - 1 kg ru", i18n.get(1).getSecond());

        final String xmlStr = (String) converter.convertToEntity(rl, dto, null);

        assertNotNull(xmlStr);

        this.context.checking(new Expectations() {{
            allowing(dto2).getRangeNavigation();
            will(returnValue(xmlStr));
        }});

        final VoProductTypeAttrNavigationRanges rl2 = (VoProductTypeAttrNavigationRanges) converter.convertToDto(dto, null);

        assertNotNull(rl2);
        assertEquals(4, rl2.getRanges().size());

        assertEquals("0.10-1.00", (rl2.getRanges().get(0)).getRange());

        final List<MutablePair<String, String>> i18n2 = (rl.getRanges().get(0)).getDisplayVals();
        assertNotNull(i18n2);
        assertEquals(2, i18n2.size());
        assertEquals("en", i18n2.get(0).getFirst());
        assertEquals("100 g - 1 kg", i18n2.get(0).getSecond());
        assertEquals("ru", i18n2.get(1).getFirst());
        assertEquals("100 g - 1 kg ru", i18n2.get(1).getSecond());

        this.context.assertIsSatisfied();

    }
}