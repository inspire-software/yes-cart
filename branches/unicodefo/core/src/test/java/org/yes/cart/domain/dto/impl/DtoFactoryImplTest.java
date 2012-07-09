package org.yes.cart.domain.dto.impl;

import org.hamcrest.MatcherAssert;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.yes.cart.domain.dto.factory.impl.DtoFactoryImpl;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;

/**
 * DtoFactoryImpl test.
 * <p/>
 * User: dogma
 * Date: Jan 22, 2011
 * Time: 11:51:21 PM
 */
@RunWith(org.jmock.integration.junit4.JMock.class)
public class DtoFactoryImplTest {

    private static final String KEY = "java.util.List";
    private DtoFactoryImpl factory;
    private Mockery mockery = new JUnit4Mockery();
    private Map<String, String> mapping = mockery.mock(Map.class);

    @Test(expected = InstantiationError.class)
    public void testGetInstanceUnmapped() throws Exception {
        mockery.checking(new Expectations() {{
            allowing(mapping).containsKey(KEY);
            will(returnValue(false));
        }});
        factory = new DtoFactoryImpl(mapping);
        factory.getByIface(List.class);
    }

    @Test(expected = InstantiationError.class)
    public void testGetInstanceUnableToInstantiate() throws Exception {
        mockery.checking(new Expectations() {{
            allowing(mapping).containsKey(KEY);
            will(returnValue(true));
            allowing(mapping).get(KEY);
            will(returnValue("invalid.class.Name"));
        }});
        factory = new DtoFactoryImpl(mapping);
        factory.getByIface(List.class);
    }

    @Test
    public void testGetInstance() throws Exception {
        mockery.checking(new Expectations() {{
            allowing(mapping).containsKey(KEY);
            will(returnValue(true));
            allowing(mapping).get(KEY);
            will(returnValue("java.util.ArrayList"));
        }});
        factory = new DtoFactoryImpl(mapping);
        MatcherAssert.assertThat(factory.getByIface(List.class), is(notNullValue()));
    }
}
