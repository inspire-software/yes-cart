/*
 * Copyright 2009 - 2016 Denys Pavlov, Igor Azarnyi
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

package org.yes.cart.security.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.yes.cart.cluster.node.NodeService;
import org.yes.cart.cluster.node.impl.NodeImpl;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.service.domain.SystemService;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * User: denispavlov
 * Date: 31/05/2019
 * Time: 11:33
 */
public class JWTAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private SystemService systemService;

    private String secretKey = AttributeNamesKeys.System.MANAGER_JWT_SECRET;
    private String expiryKey = AttributeNamesKeys.System.MANAGER_JWT_EXPIRY_MIN;
    private String systemName = "api";

    public JWTAuthenticationFilter() {
        super(new AntPathRequestMatcher(JWTUtil.AUTH_LOGIN_URL, "POST"));
        this.setAuthenticationSuccessHandler((request, response, auth) -> {

            final long now = System.currentTimeMillis();
            final long expiry = now + this.getExpiryMs();
            final String secret = this.getSecret();

            JWTUtil.sendSuccessJWT(
                    this.systemName,
                    this.systemName,
                    auth.getName(),
                    auth.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority).collect(Collectors.toList()),
                    now,
                    expiry,
                    secret,
                    response
            );

        });
        this.setAuthenticationFailureHandler((request, response, failed) -> {

            if (failed instanceof CredentialsExpiredException) {
                JWTUtil.sendFailureJWT(JWTUtil.CredentialsState.AUTH_CREDENTAILS_EXPIRED.name(), response);
            } else {
                JWTUtil.sendFailureJWT(JWTUtil.CredentialsState.AUTH_CREDENTAILS_INVALID.name(), response);
            }

        });
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req,
                                                HttpServletResponse res) throws AuthenticationException {
        try {
            LoginData creds = objectMapper
                    .readValue(req.getInputStream(), LoginData.class);

            if (StringUtils.isBlank(creds.getUsername())) {

                final Optional<Cookie> xAuth = req.getCookies() != null ? Arrays.stream(req.getCookies())
                        .filter(cookie -> JWTUtil.COOKIE_HEADER.equalsIgnoreCase(cookie.getName())).findFirst() : Optional.empty();
                if (xAuth.isPresent()) {

                    final Jws<Claims> token = extractAndDecodeHeader(xAuth.get().getValue(), req);

                    final String username = token.getBody().getSubject();

                    final List<String> authorities = (List<String>) token.getBody().get("rol");

                    final UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                            username, null, authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));

                    if (this.logger.isDebugEnabled()) {
                        this.logger.debug("Authentication success (COOKIE): " + auth);
                    }

                    return auth;

                }

            }

            final Authentication auth = getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(
                            creds.getUsername(),
                            creds.getPassword(),
                            new ArrayList<>())
            );

            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Authentication success (CREDENTIALS): " + auth);
            }

            return auth;

        } catch (IOException e) {
            throw new BadCredentialsException(JWTUtil.CredentialsState.AUTH_CREDENTAILS_INVALID.name(), e);
        }
    }


    /**
     * Decodes the header into a username and password.
     *
     * @throws BadCredentialsException if the Basic header is not present or is not valid
     * Base64
     */
    private Jws<Claims> extractAndDecodeHeader(final String header, final HttpServletRequest request) {

        return JWTUtil.extractAndDecode(header, this.getSecret(), request);

    }


    protected long getExpiryMs() {
        return JWTUtil.getExpiryMs(systemService, expiryKey);
    }

    protected String getSecret() {
        return JWTUtil.getSecret(systemService, secretKey);
    }

    /**
     * Expiry key.
     *
     * @param expiryKey key to use in system service
     */
    public void setExpiryKey(final String expiryKey) {
        this.expiryKey = expiryKey;
    }

    /**
     * Secret key.
     *
     * @param secretKey key to use in system service
     */
    public void setSecretKey(final String secretKey) {
        this.secretKey = secretKey;
    }


    /**
     * Spring IoC.
     *
     * @param systemService system service
     */
    public void setSystemService(final SystemService systemService) {
        this.systemService = systemService;
    }


    @Override
    public void setServletContext(final ServletContext servletContext) {

        final Map<String, String> configuration = new HashMap<>();

        final Enumeration parameters = servletContext.getInitParameterNames();
        while (parameters.hasMoreElements()) {

            final String key = String.valueOf(parameters.nextElement());
            final String value = servletContext.getInitParameter(key);

            configuration.put(key, value);

        }

        final NodeImpl node = new NodeImpl(true,
                configuration.get(NodeService.NODE_ID),
                configuration.get(NodeService.NODE_TYPE),
                configuration.get(NodeService.NODE_CONFIG),
                configuration.get(NodeService.CLUSTER_ID),
                configuration.get(NodeService.VERSION),
                configuration.get(NodeService.BUILD_NO),
                true
        );
        node.setChannel(configuration.get(NodeService.CHANNEL));

        this.systemName = node.getId().concat("_").concat(node.getFullVersion());

        super.setServletContext(servletContext);
    }

}
