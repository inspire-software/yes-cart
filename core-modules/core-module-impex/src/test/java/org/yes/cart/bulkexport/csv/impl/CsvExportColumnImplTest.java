package org.yes.cart.bulkexport.csv.impl;

import org.junit.Test;
import org.yes.cart.bulkcommon.model.ImpExColumn;
import org.yes.cart.bulkcommon.model.ImpExTuple;
import org.yes.cart.bulkcommon.model.ValueAdapter;

import java.util.Map;
import java.util.Properties;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * User: denispavlov
 * Date: 26/11/2015
 * Time: 11:52
 */
public class CsvExportColumnImplTest {


    @Test
    public void testGetValue() {

        final ValueAdapter adapter = new ValueAdapter() {
            public Object fromRaw(final Object rawValue, final String requiredType, final ImpExColumn impExColumn, final ImpExTuple tuple) {
                return rawValue;
            }
        };

        String reExpKey = "((\\D?\\S{4,}){1,}).*-?.*";
        String reExpValue = ".*-.*\\s{1,}((\\D?\\S{4,}){1,})\\s{0,}.*";


        CsvExportColumnImpl column = new CsvExportColumnImpl();
        CsvExportTupleImpl tuple = new CsvExportTupleImpl(new Object());
        assertNull(column.getValue(null, adapter, tuple));

        String rawValue = "rawValue";
        assertEquals(rawValue, column.getValue(rawValue, adapter, tuple));

        column.setValueRegEx("(.*)");
        assertEquals(rawValue, column.getValue(rawValue, adapter, tuple));

        column.setValueRegEx("(raw)");
        assertEquals("raw", column.getValue(rawValue, adapter, tuple));

        column.setValueRegEx("(Value)");
        assertEquals("Value", column.getValue(rawValue, adapter, tuple));


        column.setValueRegEx(reExpKey);
        assertEquals("SomeKey", column.getValue("SomeKey       -  Value", adapter, tuple));

        column.setValueRegEx(reExpKey);
        assertEquals("Ключик", column.getValue("Ключик       -  Значение", adapter, tuple));

        column.setValueRegEx(reExpKey);
        assertEquals("Ключик", column.getValue(" Ключик       -  Значение", adapter, tuple));

        //The same test on unicode strign with some noise
        column.setValueRegEx(reExpKey);
        assertEquals(getprops(), "result", column.getValue("as фыф result - шум 34 123.56 значение sd ыв #", adapter, tuple));


        //The same test on unicode strign with some noise
        column.setValueRegEx(reExpKey);
        assertEquals("результат", column.getValue("as фыф результат - шум 34 123.56 значение sd ыв #", adapter, tuple));

        column.setValueRegEx(reExpKey);
        assertEquals("Діамант жовтий", column.getValue("фыв 3 фвк Діамант жовтий 3/ 6 - 0,10 Ct", adapter, tuple));

        column.setValueRegEx(reExpKey);
        assertEquals("Кубічний цирконій", column.getValue("2 Кубічний цирконій - 66 шт", adapter, tuple));

        column.setValueRegEx(reExpKey);
        assertEquals("Онікс,перли,кварц димчастий", column.getValue("0 Онікс,перли,кварц димчастий -  гр", adapter, tuple));

        column.setValueRegEx(reExpKey);
        assertEquals("Онікс", column.getValue("Онікс -", adapter, tuple));


        // a value in key - value pair
        column.setValueRegEx(".*\\s{0,}\\-\\s{0,}(Value)");
        assertEquals("Value", column.getValue("SomeKey       -  ValueZZZ", adapter, tuple));

        column.setValueRegEx(reExpValue);
        assertEquals("Value", column.getValue("SomeKey       -  Value", adapter, tuple));


        column.setValueRegEx(reExpValue);
        assertEquals("Значение", column.getValue(" Ключ       -  Значение", adapter, tuple));

        column.setValueRegEx(reExpValue);
        assertEquals("значение", column.getValue("as фыф результат-шум 34 123.56 значение sd ыв #", adapter, tuple));


        //some single unicode word regex
        column.setValueRegEx(".*\\b([a-z]{3,})\\b.*");
        assertEquals("word", column.getValue("1234sdcvdfv hfkdf34 word 2134", adapter, tuple));

        column.setValueRegExTemplate("My '$1'");
        assertEquals("My 'word'", column.getValue("1234sdcvdfv hfkdf34 word 2134", adapter, tuple));

        column.setValueRegExTemplate("");
        column.setValueRegEx("\\b(\\S\\D{3,})\\b");
        assertEquals("word", column.getValue("1234sdcvdfv hfkdf34 word 2134", adapter, tuple));

        column.setValueRegEx("\\b(\\S\\D{3,})\\b");
        assertEquals("слово", column.getValue("1234sdcvdfv hfkdf34 слово 2134", adapter, tuple));

        /*
        real examples
        35 Діамант кр57 3/5 - 0,10 Ct; 1 Сапфір   - 0,62 Ct; 1 Рубін   - 0,54 Ct
        2 Кубічний цирконій - 66 шт,                Аметист - 2,68 гр
        0 Кварц димчастий   -  Ct
        0 Онікс,перли,кварц димчастий -  гр
         */


    }

    String getprops() {
        StringBuilder stryingBuilder = new StringBuilder();
        Properties prop = System.getProperties();
        Set<Map.Entry<Object, Object>> set = prop.entrySet();
        for (Map.Entry<Object, Object> ent : set) {
            stryingBuilder.append(ent.getKey());
            stryingBuilder.append("=");
            stryingBuilder.append(ent.getValue());
            stryingBuilder.append("\n");
        }
        return stryingBuilder.toString();
    }

}