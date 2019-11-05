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

package org.yes.cart.domain.entity.impl;

import org.junit.Test;
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.misc.Pair;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 20/09/2019
 * Time: 18:54
 */
public class CustomerOrderDetAttributesImplTest {


    @Test
    public void testSingle() throws Exception {

        final String raw =
                "ORDRSP#$#MsgID=25, 20170407, 180508#$#AUDITEXPORT#$#" +
                "EXPCSV: 2017-03-22 14:43:00#$#os.in.progress 170306154215-47-0#$#AUDITEXPORT#$#" +
                "EXPCSV: 2017-04-07 18:43:00#$#os.in.progress 170306154215-47-0#$#AUDITEXPORT#$#";

        final String expected =
                "ORDRSP#$#MsgID=25, 20170407, 180508#$#xx#~#AUDITEXPORT#~##$#" +
                "EXPCSV: 2017-03-22 14:43:00#$#os.in.progress 170306154215-47-0#$#xx#~#AUDITEXPORT#~##$#" +
                "EXPCSV: 2017-04-07 18:43:00#$#os.in.progress 170306154215-47-0#$#xx#~#AUDITEXPORT#~##$#";

        final CustomerOrderDetAttributesImpl attrs = new CustomerOrderDetAttributesImpl(raw);

        assertNotNull(attrs.getAllValues());
        assertEquals(3, attrs.getAllValues().size());

        final Pair<String, I18NModel> msgId = attrs.getValue("ORDRSP");
        assertEquals("MsgID=25, 20170407, 180508", msgId.getFirst());
        final I18NModel msgIdI18n = msgId.getSecond();
        assertNotNull(msgIdI18n);
        assertEquals("AUDITEXPORT", msgIdI18n.getValue(I18NModel.DEFAULT));

        final Pair<String, I18NModel> expCsv1 = attrs.getValue("EXPCSV: 2017-03-22 14:43:00");
        assertEquals("os.in.progress 170306154215-47-0", expCsv1.getFirst());
        final I18NModel expCsv1I18n = expCsv1.getSecond();
        assertNotNull(expCsv1I18n);
        assertEquals("AUDITEXPORT", expCsv1I18n.getValue(I18NModel.DEFAULT));

        final Pair<String, I18NModel> expCsv2 = attrs.getValue("EXPCSV: 2017-04-07 18:43:00");
        assertEquals("os.in.progress 170306154215-47-0", expCsv2.getFirst());
        final I18NModel expCsv2I18n = expCsv2.getSecond();
        assertNotNull(expCsv2I18n);
        assertEquals("AUDITEXPORT", expCsv2I18n.getValue(I18NModel.DEFAULT));

        assertEquals(expected, attrs.toString());

    }

    @Test
    public void testI18n() throws Exception {

        final String raw =
                "ORDRSP#$#MsgID=25, 20170407, 180508#$#EN#~#Order#~#RU#~#Заказ#~##$#" +
                "EXPCSV: 2017-03-22 14:43:00#$#os.in.progress 170306154215-47-0#$#EN#~#Export CSV#~#RU#~#Экспорт CSV#~##$#" +
                "EXPCSV: 2017-04-07 18:43:00#$#os.in.progress 170306154215-47-0#$#EN#~#Export CSV#~#RU#~#Экспорт CSV#~##$#";

        final CustomerOrderDetAttributesImpl attrs = new CustomerOrderDetAttributesImpl(raw);

        assertNotNull(attrs.getAllValues());
        assertEquals(3, attrs.getAllValues().size());

        final Pair<String, I18NModel> msgId = attrs.getValue("ORDRSP");
        assertEquals("MsgID=25, 20170407, 180508", msgId.getFirst());
        final I18NModel msgIdI18n = msgId.getSecond();
        assertNotNull(msgIdI18n);
        assertNull(msgIdI18n.getValue(I18NModel.DEFAULT));
        assertEquals("Order", msgIdI18n.getValue("EN"));
        assertEquals("Заказ", msgIdI18n.getValue("RU"));

        final Pair<String, I18NModel> expCsv1 = attrs.getValue("EXPCSV: 2017-03-22 14:43:00");
        assertEquals("os.in.progress 170306154215-47-0", expCsv1.getFirst());
        final I18NModel expCsv1I18n = expCsv1.getSecond();
        assertNotNull(expCsv1I18n);
        assertNull(expCsv1I18n.getValue(I18NModel.DEFAULT));
        assertEquals("Export CSV", expCsv1I18n.getValue("EN"));
        assertEquals("Экспорт CSV", expCsv1I18n.getValue("RU"));

        final Pair<String, I18NModel> expCsv2 = attrs.getValue("EXPCSV: 2017-04-07 18:43:00");
        assertEquals("os.in.progress 170306154215-47-0", expCsv2.getFirst());
        final I18NModel expCsv2I18n = expCsv2.getSecond();
        assertNotNull(expCsv2I18n);
        assertNull(expCsv2I18n.getValue(I18NModel.DEFAULT));
        assertEquals("Export CSV", expCsv2I18n.getValue("EN"));
        assertEquals("Экспорт CSV", expCsv2I18n.getValue("RU"));

    }

}