package org.yes.cart.utils.impl;

import org.junit.Test;
import org.yes.cart.domain.misc.Pair;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
* User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class StringValueToPairListConverterTest {

    @Test
    public void testConvert() {

        StringValueToPairListConverter converter = new StringValueToPairListConverter();
        String values = "Red,Green,Blue";
        List<Pair<String, String>> pairList = converter.getChoises(values);
        assertEquals(3, pairList.size());
        assertEquals("Red", pairList.get(0).getFirst());
        assertEquals("Red", pairList.get(0).getSecond());
        assertEquals("Green", pairList.get(1).getFirst());
        assertEquals("Green", pairList.get(1).getSecond());

        values = "Red,G-Green,Blue";
        pairList = converter.getChoises(values);
        assertEquals(3, pairList.size());
        assertEquals("Red", pairList.get(0).getFirst());
        assertEquals("Red", pairList.get(0).getSecond());        
        assertEquals("G", pairList.get(1).getFirst());
        assertEquals("Green", pairList.get(1).getSecond());

    }
    
}
