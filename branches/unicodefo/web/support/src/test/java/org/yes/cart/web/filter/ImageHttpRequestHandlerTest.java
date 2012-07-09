package org.yes.cart.web.filter;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.yes.cart.service.domain.SystemService;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 2011-May-17
 * Time: 10:11:01 AM
 */
@RunWith(org.jmock.integration.junit4.JMock.class)
public class ImageHttpRequestHandlerTest {

    private final Mockery mockery = new JUnit4Mockery();

    // TODO fix or remove
    @Test
    public void testGetContentType() {
        final SystemService systemService = mockery.mock(SystemService.class, "systemService0");
        mockery.checking(new Expectations() {{
            allowing(systemService).getEtagExpirationForImages();
            will(returnValue(0));
        }});
        /*ImageFilter imageFilter = new ImageFilter(null, systemService);
        assertEquals("image/jpeg", imageFilter.getContentType("a.jpg"));
        assertEquals("image/jpeg", imageFilter.getContentType("a.JPEG"));
        assertEquals("image/jpeg", imageFilter.getContentType("a.JPE"));
        assertEquals("image/gif", imageFilter.getContentType("a.gif"));
//        assertEquals("image/png", imageFilter.getContentType("a.png"));
        assertEquals("image/bmp", imageFilter.getContentType("a.bmp"));
        assertEquals("application/x-shockwave-flash", imageFilter.getContentType("a.swf"));
        assertEquals("application/octet-stream", imageFilter.getContentType("a.x3ext"));  */
    }
}
