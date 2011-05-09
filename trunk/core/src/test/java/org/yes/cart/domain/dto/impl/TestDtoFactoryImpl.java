package org.yes.cart.domain.dto.impl;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.yes.cart.domain.dto.factory.impl.DtoFactoryImpl;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * DtoFactoryImpl test.
 * <p/>
 * User: dogma
 * Date: Jan 22, 2011
 * Time: 11:51:21 PM
 */
@RunWith(org.jmock.integration.junit4.JMock.class)
public class TestDtoFactoryImpl {

    private static final String KEY = "java.util.List";

    private DtoFactoryImpl factory;
    private Map<String, String> mapping;

    private final Mockery mockery = new JUnit4Mockery();

    @Test(expected = InstantiationError.class)
    public void testGetInstanceUnmapped() throws UnableToCreateInstanceException, UnmappedInterfaceException {

        mapping = mockery.mock(Map.class);

        mockery.checking(new Expectations() { {
            allowing(mapping).containsKey(KEY); will(returnValue(false));    
        } });

        factory = new DtoFactoryImpl(mapping);
        factory.getByIface(List.class);

    }

    @Test(expected = InstantiationError.class)
    public void testGetInstanceUnableToInstanciate() throws UnableToCreateInstanceException, UnmappedInterfaceException {

        mapping = mockery.mock(Map.class);

        mockery.checking(new Expectations() { {
            allowing(mapping).containsKey(KEY); will(returnValue(true));
            allowing(mapping).get(KEY); will(returnValue("invalid.class.Name"));
        } });

        factory = new DtoFactoryImpl(mapping);
        factory.getByIface(List.class);

    }


    @Test
    public void testGetInstance() throws UnableToCreateInstanceException, UnmappedInterfaceException {

        mapping = mockery.mock(Map.class);

        mockery.checking(new Expectations() { {
            allowing(mapping).containsKey(KEY); will(returnValue(true));
            allowing(mapping).get(KEY); will(returnValue("java.util.ArrayList"));
        } });

        factory = new DtoFactoryImpl(mapping);
        final List object = factory.getByIface(List.class);
        assertNotNull("Must not be null", object);
        assertTrue("Must be valid instance", object instanceof List);
    }



}
