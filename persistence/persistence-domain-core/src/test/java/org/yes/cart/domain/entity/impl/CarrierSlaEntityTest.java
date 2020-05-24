/*
 * Copyright 2009 Inspire-Software.com
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
import org.yes.cart.utils.DateUtils;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 10/04/2017
 * Time: 08:18
 */
public class CarrierSlaEntityTest {

    @Test
    public void testGetCarrierSlaExcludedDatesNone() throws Exception {

        final CarrierSlaEntity sla = new CarrierSlaEntity();

        final Map<LocalDate, LocalDate> dates = sla.getExcludeDatesAsMap();

        assertNotNull(dates);
        assertTrue(dates.isEmpty());

    }


    @Test
    public void testGetCarrierSlaExcludedDates() throws Exception {

        final CarrierSlaEntity sla = new CarrierSlaEntity();

        sla.setExcludeDates("2017-01-08,2017-01-10:2017-01-15,2017-01-17");

        LocalDate date, date2;

        final Map<LocalDate, LocalDate> dates = sla.getExcludeDatesAsMap();


        assertNotNull(dates);
        assertEquals(3, dates.size());
        date = DateUtils.ldParseSDT("2017-01-08");
        assertEquals(date, dates.get(date));
        date = DateUtils.ldParseSDT("2017-01-10");
        date2 = DateUtils.ldParseSDT("2017-01-15");
        assertEquals(date2, dates.get(date));
        date = DateUtils.ldParseSDT("2017-01-17");
        assertEquals(date, dates.get(date));

    }

}