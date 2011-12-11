package org.yes.cart.bulkimport.csv.impl;

import org.junit.Test;
import org.yes.cart.bulkimport.model.ImportColumn;

import java.util.Map;
import java.util.Properties;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 11/27/11
 * Time: 1:32 PM
 */
public class CsvImportColumnImplTest {

    @Test
    public void testGetValue() {


        String reExpKey = "((\\D?\\S{4,}){1,}).*-?.*";
        String reExpValue = ".*-.*\\s{1,}((\\D?\\S{4,}){1,})\\s{0,}.*";


        ImportColumn column = new CsvImportColumnImpl();
        assertNull(column.getValue(null));

        String rawValue = "rawValue";
        assertEquals(rawValue, column.getValue(rawValue));

        column.setRegExp("(.*)");
        assertEquals(rawValue, column.getValue(rawValue));

        column.setRegExp("(raw)");
        assertEquals("raw", column.getValue(rawValue));

        column.setRegExp("(Value)");
        assertEquals("Value", column.getValue(rawValue));


        column.setRegExp(reExpKey);
        assertEquals("SomeKey", column.getValue("SomeKey       -  Value"));

        column.setRegExp(reExpKey);
        assertEquals("Ключик", column.getValue("Ключик       -  Значение"));

        column.setRegExp(reExpKey);
        assertEquals("Ключик", column.getValue(" Ключик       -  Значение"));

        //The same test on unicode strign with some noise
        column.setRegExp(reExpKey);
        assertEquals(getprops(), "result", column.getValue("as фыф result - шум 34 123.56 значение sd ыв #"));


        //The same test on unicode strign with some noise
        column.setRegExp(reExpKey);
        assertEquals("результат", column.getValue("as фыф результат - шум 34 123.56 значение sd ыв #"));

        column.setRegExp(reExpKey);
        assertEquals("Діамант жовтий", column.getValue("фыв 3 фвк Діамант жовтий 3/ 6 - 0,10 Ct"));

        column.setRegExp(reExpKey);
        assertEquals("Кубічний цирконій", column.getValue("2 Кубічний цирконій - 66 шт"));

        column.setRegExp(reExpKey);
        assertEquals("Онікс,перли,кварц димчастий", column.getValue("0 Онікс,перли,кварц димчастий -  гр"));

        column.setRegExp(reExpKey);
        assertEquals("Онікс", column.getValue("Онікс -"));


        // a value in key - value pair
        column.setRegExp(".*\\s{0,}\\-\\s{0,}(Value)");
        assertEquals("Value", column.getValue("SomeKey       -  ValueZZZ"));

        column.setRegExp(reExpValue);
        assertEquals("Value", column.getValue("SomeKey       -  Value"));


        column.setRegExp(reExpValue);
        assertEquals("Значение", column.getValue(" Ключ       -  Значение"));

        column.setRegExp(reExpValue);
        assertEquals("значение", column.getValue("as фыф результат-шум 34 123.56 значение sd ыв #"));


        //some single unicode word re
        column.setRegExp("\\b([a-z]{3,})\\b");
        assertEquals("word", column.getValue("1234sdcvdfv hfkdf34 word 2134"));

        column.setRegExp("\\b(\\S\\D{3,})\\b");
        assertEquals("word", column.getValue("1234sdcvdfv hfkdf34 word 2134"));

        column.setRegExp("\\b(\\S\\D{3,})\\b");
        assertEquals("слово", column.getValue("1234sdcvdfv hfkdf34 слово 2134"));


        /*
        real eaxmples
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