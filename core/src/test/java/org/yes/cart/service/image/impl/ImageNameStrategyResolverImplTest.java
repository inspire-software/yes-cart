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

package org.yes.cart.service.image.impl;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.yes.cart.service.image.ImageNameStrategy;

import java.util.Arrays;

import static org.junit.Assert.assertSame;

/**
 * User: denispavlov
 * Date: 29/08/2014
 * Time: 21:31
 */
public class ImageNameStrategyResolverImplTest {

    private final Mockery context = new JUnit4Mockery();

    @Test
    public void testGetImageNameStrategy() throws Exception {


        final ImageNameStrategy defStrategy = context.mock(ImageNameStrategy.class, "defStrategy");
        final ImageNameStrategy oneStrategy = context.mock(ImageNameStrategy.class, "oneStrategy");
        final ImageNameStrategy twoStrategy = context.mock(ImageNameStrategy.class, "twoStrategy");

        context.checking(new Expectations() {{
            one(defStrategy).getUrlPath(); will(returnValue("/def/"));
            one(oneStrategy).getUrlPath(); will(returnValue("/one/"));
            one(twoStrategy).getUrlPath(); will(returnValue("/two/"));
        }});

        final ImageNameStrategyResolverImpl resolver =
                new ImageNameStrategyResolverImpl(defStrategy, Arrays.asList(
                        oneStrategy,
                        twoStrategy,
                        defStrategy
                ));

        assertSame(oneStrategy, resolver.getImageNameStrategy("/one/somefile.jpeg"));
        assertSame(oneStrategy, resolver.getImageNameStrategy("/yes-cart/one/somefile.jpeg"));
        assertSame(twoStrategy, resolver.getImageNameStrategy("/two/somefile.jpeg"));
        assertSame(twoStrategy, resolver.getImageNameStrategy("/yes-cart/two/somefile.jpeg"));
        assertSame(defStrategy, resolver.getImageNameStrategy("/def/somefile.jpeg"));
        assertSame(defStrategy, resolver.getImageNameStrategy("/yes-cart/def/somefile.jpeg"));
        assertSame(defStrategy, resolver.getImageNameStrategy("/yes-cart/somefile.jpeg"));

    }


}
