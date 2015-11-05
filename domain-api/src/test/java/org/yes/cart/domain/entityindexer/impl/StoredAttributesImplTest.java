package org.yes.cart.domain.entityindexer.impl;

import org.junit.Test;
import org.yes.cart.domain.entityindexer.StoredAttributes;
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.i18n.impl.StringI18NModel;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 04/11/2015
 * Time: 17:47
 */
public class StoredAttributesImplTest {

    @Test
    public void testStringNull() throws Exception {
        final StoredAttributes model = new StoredAttributesImpl();
        assertNotNull(model.getAllValues());
        assertTrue(model.getAllValues().isEmpty());
    }

    @Test
    public void testStringEmpty() throws Exception {
        final StoredAttributes model = new StoredAttributesImpl("");
        assertNotNull(model.getAllValues());
        assertTrue(model.getAllValues().isEmpty());
    }

    @Test
    public void testStringInvalid() throws Exception {
        final StoredAttributes model = new StoredAttributesImpl("some text");
        assertNotNull(model.getAllValues());
        assertTrue(model.getAllValues().isEmpty());
    }

    @Test
    public void testString() throws Exception {
        final StoredAttributes model = new StoredAttributesImpl("ATT1#$#Some text#$##$#ATT2#$#Текст#$##$#ATT3#$#");
        assertNotNull(model.getAllValues());
        assertEquals(2, model.getAllValues().size());
        assertEquals("Some text", model.getValue("ATT1").getFirst());
        assertNull(model.getValue("ATT1").getSecond());
        assertEquals("Текст", model.getValue("ATT2").getFirst());
        assertNull("Текст", model.getValue("ATT2").getSecond());
        assertNull(model.getValue("ATT3"));
    }

    @Test
    public void testStringI18n() throws Exception {
        final StoredAttributes model = new StoredAttributesImpl("ATT1#$#Some text#$#EN#~#Some text en#~#RU#~#Текст ru#$#ATT2#$#Текст#$##$#ATT3#$#");
        assertNotNull(model.getAllValues());
        assertEquals(2, model.getAllValues().size());
        assertEquals("Some text", model.getValue("ATT1").getFirst());
        assertNotNull(model.getValue("ATT1").getSecond());
        final I18NModel modelI18n = model.getValue("ATT1").getSecond();
        assertEquals("Some text en", modelI18n.getValue("EN"));
        assertEquals("Текст ru", modelI18n.getValue("RU"));
        assertEquals("Текст", model.getValue("ATT2").getFirst());
        assertNull("Текст", model.getValue("ATT2").getSecond());
        assertNull(model.getValue("ATT3"));
    }

    @Test
    public void testStringBlankValues() throws Exception {
        final StoredAttributes model = new StoredAttributesImpl();
        assertNotNull(model.getAllValues());
        assertEquals(0, model.getAllValues().size());
        model.putValue("ATT1", "Some text", null);
        model.putValue("ATT2", null, null);
        model.putValue("ATT3", "", "EN#~#Some text en");
        final StoredAttributes restored = new StoredAttributesImpl(model.toString());
        assertNotNull(restored.getAllValues());
        assertEquals(1, restored.getAllValues().size());
        assertEquals("Some text", restored.getValue("ATT1").getFirst());
        assertNull(restored.getValue("ATT1").getSecond());
        assertNull(restored.getValue("ATT2"));
        assertNull(restored.getValue("ATT3"));
    }

    @Test
    public void testRestore() throws Exception {
        final StoredAttributes model = new StoredAttributesImpl();
        assertNotNull(model.getAllValues());
        assertEquals(0, model.getAllValues().size());
        model.putValue("ATT1", "Some text", null);
        model.putValue("ATT2", "value2", new StringI18NModel("RU#~#Текст ru"));
        model.putValue("ATT3", "value3", "EN#~#Some text en");
        final StoredAttributes restored = new StoredAttributesImpl(model.toString());
        assertNotNull(restored.getAllValues());
        assertEquals(3, restored.getAllValues().size());
        assertEquals("Some text", restored.getValue("ATT1").getFirst());
        assertNull(restored.getValue("ATT1").getSecond());
        assertEquals("value2", restored.getValue("ATT2").getFirst());
        final I18NModel modelI18n1 = restored.getValue("ATT2").getSecond();
        assertNotNull(modelI18n1);
        assertEquals(1, modelI18n1.getAllValues().size());
        assertEquals("Текст ru", modelI18n1.getValue("RU"));
        assertEquals("value3", restored.getValue("ATT3").getFirst());
        final I18NModel modelI18n2 = restored.getValue("ATT3").getSecond();
        assertNotNull(modelI18n2);
        assertEquals(1, modelI18n2.getAllValues().size());
        assertEquals("Some text en", modelI18n2.getValue("EN"));
    }

}