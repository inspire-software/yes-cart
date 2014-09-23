/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
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

package org.yes.cart.web.util;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/11/11
 * Time: 9:12 AM
 */
public class WicketUtilTest {

    @Test
    public void testGetFilteredRequestParameters() throws Exception {
        WicketUtil wicketUtil = new WicketUtil();
        wicketUtil.setCmdKeys(new ArrayList<String>() {{
            add("cmd1");
            add("cmd2");
        }});
        assertNotNull(WicketUtil.getFilteredRequestParameters(null));
        PageParameters parametersToFilter = new PageParameters("cmd1=val1,asd=dsa,cmd2=ppp");
        assertEquals(3, parametersToFilter.getNamedKeys().size());
        PageParameters filtered = WicketUtil.getFilteredRequestParameters(parametersToFilter);
        assertNotNull(filtered);
        assertEquals(1, filtered.getNamedKeys().size());
        assertEquals("dsa", filtered.get("asd").toString());
    }

    @Test
    public void testGetFilteredRequestParametersForSearch() {
        PageParameters parametersToFilter = new PageParameters("query=val1,query=val2,query=val3");
        assertEquals(1, parametersToFilter.getNamedKeys().size());
        assertEquals(3, parametersToFilter.getValues("query").size());
        parametersToFilter.remove("query", "val2");
        assertEquals(2, parametersToFilter.getValues("query").size());
        for (StringValue val : parametersToFilter.getValues("query")) {
            assertFalse("val2".equals(val.toString()));
        }
    }


    @Test
    public void testConstructLatinStringValue() throws Exception {

        assertEquals("color123", WicketUtil.constructLatinStringValue("color", "123"));
        assertEquals("colorblue", WicketUtil.constructLatinStringValue("color", "blue"));
        assertEquals("colorblue123", WicketUtil.constructLatinStringValue("color", "blue", "123"));
        assertEquals("color10891080108510801081", WicketUtil.constructLatinStringValue("color", "синий"));
        assertEquals("color10891080108510801081123", WicketUtil.constructLatinStringValue("color", "синий", "123"));
        assertEquals("color910X1047910", WicketUtil.constructLatinStringValue("color", "\t\nXЗ\t\n"));

    }

}
