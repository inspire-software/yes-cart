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

package org.yes.cart.service.media.impl;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.yes.cart.service.media.MediaFileNameStrategy;

import java.util.Arrays;

import static org.junit.Assert.assertSame;

/**
 * User: denispavlov
 * Date: 29/08/2014
 * Time: 21:31
 */
public class MediaFileNameStrategyResolverImplTest {

    private final Mockery context = new JUnit4Mockery();

    @Test
    public void testGetImageNameStrategy() throws Exception {


        final MediaFileNameStrategy defStrategy = context.mock(MediaFileNameStrategy.class, "defStrategy");
        final MediaFileNameStrategy oneStrategy = context.mock(MediaFileNameStrategy.class, "oneStrategy");
        final MediaFileNameStrategy twoStrategy = context.mock(MediaFileNameStrategy.class, "twoStrategy");

        context.checking(new Expectations() {{
            one(defStrategy).getUrlPath(); will(returnValue("/def/"));
            one(oneStrategy).getUrlPath(); will(returnValue("/one/"));
            one(twoStrategy).getUrlPath(); will(returnValue("/two/"));
        }});

        final MediaFileNameStrategyResolverImpl resolver =
                new MediaFileNameStrategyResolverImpl(defStrategy, Arrays.asList(
                        oneStrategy,
                        twoStrategy,
                        defStrategy
                ));

        assertSame(oneStrategy, resolver.getMediaFileNameStrategy("/one/somefile.jpeg"));
        assertSame(oneStrategy, resolver.getMediaFileNameStrategy("/yes-cart/one/somefile.jpeg"));
        assertSame(twoStrategy, resolver.getMediaFileNameStrategy("/two/somefile.jpeg"));
        assertSame(twoStrategy, resolver.getMediaFileNameStrategy("/yes-cart/two/somefile.jpeg"));
        assertSame(defStrategy, resolver.getMediaFileNameStrategy("/def/somefile.jpeg"));
        assertSame(defStrategy, resolver.getMediaFileNameStrategy("/yes-cart/def/somefile.jpeg"));
        assertSame(defStrategy, resolver.getMediaFileNameStrategy("/yes-cart/somefile.jpeg"));

    }


}
