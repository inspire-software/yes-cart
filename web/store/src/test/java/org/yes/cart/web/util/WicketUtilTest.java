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
}
