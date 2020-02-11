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

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.service.domain.SystemService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * User: denispavlov
 * Date: 31/05/2019
 * Time: 09:40
 */
public class JWTAuthorisationFilter extends OncePerRequestFilter {

    private SystemService systemService;

    private String secretKey = AttributeNamesKeys.System.MANAGER_JWT_SECRET;
    private String expiryKey = AttributeNamesKeys.System.MANAGER_JWT_EXPIRY_MIN;

    private RequestMatcher requiresRefreshRequestMatcher = new AntPathRequestMatcher(JWTUtil.AUTH_REFRESH_URL, "POST");


    @Override
    protected void doFilterInternal(final HttpServletRequest request,
                                    final HttpServletResponse response,
                                    final FilterChain chain) throws ServletException, IOException {

        final boolean debug = this.logger.isDebugEnabled();

        String header = request.getHeader(JWTUtil.TOKEN_HEADER);

        if (header == null || !header.toLowerCase().startsWith(JWTUtil.TOKEN_PREFIX_LOWER)) {

            // Fallback on Cookie to allow simple GET links to work from browser.
            final Optional<Cookie> xAuth = request.getCookies() != null ? Arrays.stream(request.getCookies())
                    .filter(cookie -> JWTUtil.COOKIE_HEADER.equalsIgnoreCase(cookie.getName())).findFirst() : Optional.empty();
            if (xAuth.isPresent()) {
                header = xAuth.get().getValue();
            }

            if (header == null) {
                // no Auth
                chain.doFilter(request, response);
                return;
            }
        }

        try {
            final Jws<Claims> token = extractAndDecodeHeader(header, request);

            final String username = token.getBody().getSubject();

            if (debug) {
                this.logger
                        .debug("JWT Authentication Authorization header found for user '"
                                + username + "'");
            }

            if (requiresTokenRefresh(request)) {

                if (debug) {
                    this.logger.debug("JWT refresh: " + token);
                }

                onSuccessfulRefresh(request, response, token);

                return;

            }

            if (authenticationIsRequired(username)) {

                final List<String> authorities = (List<String>) token.getBody().get("rol");

                final UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        username, null, authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));

                if (debug) {
                    this.logger.debug("JWT success: " + auth);
                }

                SecurityContextHolder.getContext().setAuthentication(auth);

                onSuccessfulAuthentication(request, response, auth);

            }

        } catch (AuthenticationException failed) {

            SecurityContextHolder.clearContext();

            if (debug) {
                this.logger.debug("JWT request failed: " + failed);
            }

            onUnsuccessfulAuthentication(request, response, failed);

            return;
        }

        chain.doFilter(request, response);
    }

    private boolean requiresTokenRefresh(final HttpServletRequest request) {
        return requiresRefreshRequestMatcher.matches(request);
    }

    protected void onSuccessfulRefresh(final HttpServletRequest request,
                                       final HttpServletResponse response,
                                       final Jws<Claims> claimsJws) throws IOException {

        final long now = System.currentTimeMillis();
        final long expiry = now + this.getExpiryMs();
        final String secret = this.getSecret();

        JWTUtil.sendSuccessJWT(claimsJws, now, expiry, secret, response);

    }

    protected void onSuccessfulAuthentication(final HttpServletRequest request,
                                              final HttpServletResponse response,
                                              final Authentication authResult) throws IOException {
    }

    protected void onUnsuccessfulAuthentication(final HttpServletRequest request,
                                                final HttpServletResponse response,
                                                final AuthenticationException failed) throws IOException {

        JWTUtil.sendFailureJWT(failed.getMessage(), response);

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


    /**
     * Determine if authentication is required.
     *
     * @param username username
     *
     * @return true if need to authenticate
     */
    protected boolean authenticationIsRequired(String username) {
        // Only reauthenticate if username doesn't match SecurityContextHolder and user
        // isn't authenticated
        // (see SEC-53)
        Authentication existingAuth = SecurityContextHolder.getContext()
                .getAuthentication();

        if (existingAuth == null || !existingAuth.isAuthenticated()) {
            return true;
        }

        // Limit username comparison to providers which use usernames (ie
        // UsernamePasswordAuthenticationToken)
        // (see SEC-348)

        if (existingAuth instanceof UsernamePasswordAuthenticationToken
                && !existingAuth.getName().equals(username)) {
            return true;
        }

        // Handle unusual condition where an AnonymousAuthenticationToken is already
        // present
        // This shouldn't happen very often, as BasicProcessingFitler is meant to be
        // earlier in the filter
        // chain than AnonymousAuthenticationFilter. Nevertheless, presence of both an
        // AnonymousAuthenticationToken
        // together with a BASIC authentication request header should indicate
        // reauthentication using the
        // BASIC protocol is desirable. This behaviour is also consistent with that
        // provided by form and digest,
        // both of which force re-authentication if the respective header is detected (and
        // in doing so replace
        // any existing AnonymousAuthenticationToken). See SEC-610.
        if (existingAuth instanceof AnonymousAuthenticationToken) {
            return true;
        }

        return false;
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

}
