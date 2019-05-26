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

package org.yes.cart.web.service.rest.impl;

import org.apache.commons.codec.binary.Base64;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import javax.servlet.http.HttpServletRequest;

import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * User: denispavlov
 * Date: 25/05/2019
 * Time: 14:22
 */
public class ConnectorAuthStrategyBasicAuthTest {

    private final Mockery context = new JUnit4Mockery();

    @Test
    public void testAuthNone() throws Exception {

        final AuthenticationManager manager = this.context.mock(AuthenticationManager.class);
        final HttpServletRequest request = this.context.mock(HttpServletRequest.class);

        final ConnectorAuthStrategyBasicAuth auth = new ConnectorAuthStrategyBasicAuth();

        auth.setAuthenticationManager(manager);

        this.context.checking(new Expectations() {{
            allowing(request).getHeader("Authorization"); will(returnValue(null));
        }});

        assertFalse(auth.authenticated(request));

    }

    @Test
    public void testAuthBearer() throws Exception {

        final AuthenticationManager manager = this.context.mock(AuthenticationManager.class);
        final HttpServletRequest request = this.context.mock(HttpServletRequest.class);

        final ConnectorAuthStrategyBasicAuth auth = new ConnectorAuthStrategyBasicAuth();

        auth.setAuthenticationManager(manager);

        this.context.checking(new Expectations() {{
            allowing(request).getHeader("Authorization"); will(returnValue("Bearer ABC"));
        }});

        assertFalse(auth.authenticated(request));

    }

    @Test(expected = BadCredentialsException.class)
    public void testAuthBasicInvalid() throws Exception {

        final AuthenticationManager manager = this.context.mock(AuthenticationManager.class);
        final HttpServletRequest request = this.context.mock(HttpServletRequest.class);

        final ConnectorAuthStrategyBasicAuth auth = new ConnectorAuthStrategyBasicAuth();

        auth.setAuthenticationManager(manager);

        this.context.checking(new Expectations() {{
            allowing(request).getHeader("Authorization"); will(returnValue("Basic ABC"));
        }});

        auth.authenticated(request);

    }

    @Test(expected = BadCredentialsException.class)
    public void testAuthBasicBad() throws Exception {

        final AuthenticationManager manager = this.context.mock(AuthenticationManager.class);
        final HttpServletRequest request = this.context.mock(HttpServletRequest.class);

        final ConnectorAuthStrategyBasicAuth auth = new ConnectorAuthStrategyBasicAuth();

        auth.setAuthenticationManager(manager);

        final UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("üsernäme", "pä$sw()rd");
        final String basic = token.getPrincipal() + ":" + token.getCredentials();
        final byte[] encodedBytes = Base64.encodeBase64(basic.getBytes(StandardCharsets.UTF_8));


        this.context.checking(new Expectations() {{
            allowing(request).getHeader("Authorization"); will(returnValue("Basic " + new String(encodedBytes)));
            allowing(manager).authenticate(token); will(throwException(new BadCredentialsException("bad")));
        }});

        auth.authenticated(request);

    }

    @Test
    public void testAuthBasicValidUTF8() throws Exception {

        final AuthenticationManager manager = this.context.mock(AuthenticationManager.class);
        final HttpServletRequest request = this.context.mock(HttpServletRequest.class);

        final ConnectorAuthStrategyBasicAuth auth = new ConnectorAuthStrategyBasicAuth();

        auth.setAuthenticationManager(manager);


        final UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("üsernäme", "pä$sw()rd");
        final String basic = token.getPrincipal() + ":" + token.getCredentials();
        final byte[] encodedBytes = Base64.encodeBase64(basic.getBytes(StandardCharsets.UTF_8));


        this.context.checking(new Expectations() {{
            allowing(request).getHeader("Authorization"); will(returnValue("Basic " + new String(encodedBytes)));
            allowing(manager).authenticate(token); will(returnValue(token));
        }});

        assertTrue(auth.authenticated(request));

    }

    @Test
    public void testAuthBasicValidRegular() throws Exception {

        final AuthenticationManager manager = this.context.mock(AuthenticationManager.class);
        final HttpServletRequest request = this.context.mock(HttpServletRequest.class);

        final ConnectorAuthStrategyBasicAuth auth = new ConnectorAuthStrategyBasicAuth();

        auth.setAuthenticationManager(manager);


        final UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("username", "pa$sw()rd");
        final String basic = token.getPrincipal() + ":" + token.getCredentials();
        final byte[] encodedBytes = Base64.encodeBase64(basic.getBytes(StandardCharsets.UTF_8));


        this.context.checking(new Expectations() {{
            allowing(request).getHeader("Authorization"); will(returnValue("Basic " + new String(encodedBytes)));
            allowing(manager).authenticate(token); will(returnValue(token));
        }});

        assertTrue(auth.authenticated(request));

    }

}