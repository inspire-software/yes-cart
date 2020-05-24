/*
 * Copyright 2009 Inspire-Software.com
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

import io.jsonwebtoken.*;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.security.authentication.BadCredentialsException;
import org.yes.cart.service.domain.SystemService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * User: denispavlov
 * Date: 31/05/2019
 * Time: 11:49
 */
final class JWTUtil {

    enum CredentialsState {
        AUTH_TOKEN_INVALID,
        AUTH_TOKEN_EXPIRED,
        AUTH_CREDENTAILS_INVALID,
        AUTH_CREDENTAILS_EXPIRED,
        AUTH_CHANGEPWD_SAMEASOLD,
        AUTH_CHANGEPWD_NOMATCH
    }

    ;

    static final String AUTH_LOGIN_URL = "/service/authenticate";
    static final String AUTH_REFRESH_URL = "/service/refreshtoken";
    static final String AUTH_CHANGE_PWD_URL = "/service/changepwd";

    // JWT token defaults
    static final String TOKEN_HEADER = "Authorization";
    static final String COOKIE_HEADER = "X-Auth";
    static final String TOKEN_PREFIX = "Bearer ";
    static final String TOKEN_PREFIX_ENCODED = "Bearer%20";
    static final String TOKEN_PREFIX_LOWER = TOKEN_PREFIX.toLowerCase();
    static final String TOKEN_TYPE = "JWT";

    private static final String DEFAULT_SECRET = UUID.randomUUID().toString().replace("-", "");
    private static final long DEFAULT_EXPIRY_MIN = 15;

    /**
     * Get JWT expiry millis.
     *
     * @param systemService system service
     * @param key           key to use
     * @return expiry millis
     */
    static long getExpiryMs(final SystemService systemService, final String key) {
        final String expMin = systemService.getAttributeValue(key);
        return NumberUtils.toLong(expMin, DEFAULT_EXPIRY_MIN) * 60000L;
    }


    /**
     * Get JWT secret.
     *
     * @param systemService system service
     * @param key           key to use
     * @return secret
     */
    static String getSecret(final SystemService systemService, final String key) {
        return systemService.getAttributeValueOrDefault(key, DEFAULT_SECRET);
    }

    /**
     * Send JWT success response.
     *
     * @param claimsJws  claims from previous token
     * @param issuedAt   time of issue (usually now)
     * @param expiration expiration time in ms
     * @param secret     secret
     * @param response   response to render
     * @throws IOException errors
     */
    static void sendSuccessJWT(final Jws<Claims> claimsJws,
                               final long issuedAt,
                               final long expiration,
                               final String secret,
                               final HttpServletResponse response) throws IOException {
        sendSuccessJWT(
                claimsJws.getBody().getIssuer(),
                claimsJws.getBody().getAudience(),
                claimsJws.getBody().getSubject(),
                (List<String>) claimsJws.getBody().get("rol"),
                issuedAt,
                expiration,
                secret,
                response
        );
    }

    /**
     * Send JWT success response.
     *
     * @param issuer     issuer, usually the system itself
     * @param audience   audience, usually the system itself
     * @param subject    login
     * @param roles      roles
     * @param issuedAt   time of issue (usually now)
     * @param expiration expiration time in ms
     * @param secret     secret
     * @param response   response to render
     * @throws IOException errors
     */
    static void sendSuccessJWT(final String issuer,
                               final String audience,
                               final String subject,
                               final List<String> roles,
                               final long issuedAt,
                               final long expiration,
                               final String secret,
                               final HttpServletResponse response) throws IOException {

        String token = Jwts.builder()
                .setHeaderParam("typ", JWTUtil.TOKEN_TYPE)
                .setIssuer(issuer)
                .setAudience(audience)
                .setSubject(subject)
                .claim("rol", roles)
                .setIssuedAt(new Date(issuedAt))
                .setExpiration(new Date(expiration))
                .signWith(SignatureAlgorithm.HS512, secret.getBytes(StandardCharsets.UTF_8))
                .compact();

        response.addHeader(JWTUtil.TOKEN_HEADER, JWTUtil.TOKEN_PREFIX + token);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        final Cookie xAuth = new Cookie(JWTUtil.COOKIE_HEADER, JWTUtil.TOKEN_PREFIX_ENCODED + token);
        xAuth.setHttpOnly(true);
        xAuth.setMaxAge(-1);
        xAuth.setVersion(1);
        response.addCookie(xAuth);

        final StringBuilder jsonRsp = new StringBuilder();
        jsonRsp.append("{\"token\":\"").append(JWTUtil.TOKEN_PREFIX).append(token).append("\"}");
        response.getWriter().write(jsonRsp.toString());

    }


    /**
     * Send JWT error response.
     *
     * @param errorValue   error value (usually code)
     * @param response     response to render
     *
     * @throws IOException error
     */
    static void sendFailureJWT(final String errorValue,
                               final HttpServletResponse response) throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        final Cookie xAuth = new Cookie(JWTUtil.COOKIE_HEADER, "");
        xAuth.setMaxAge(0);
        xAuth.setVersion(1);
        response.addCookie(xAuth);

        final StringBuilder error = new StringBuilder();
        error.append("{\"error\":\"").append(errorValue).append("\"}");
        response.getWriter().write(error.toString());

    }

    /**
     * Send JWT log off response.
     *
     * @param response     response to render
     *
     * @throws IOException error
     */
    static void sendLogOffJWT(final HttpServletResponse response) throws IOException {

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        final Cookie xAuth = new Cookie(JWTUtil.COOKIE_HEADER, "");
        xAuth.setMaxAge(0);
        xAuth.setVersion(1);
        response.addCookie(xAuth);

        response.getWriter().write("{\"token\": null }");

    }


    /**
     * Decodes the header into a username and password.
     *
     * @param value    Bearer + JWT
     * @param secret   JWT secret
     * @param request  request to process
     *
     * @throws BadCredentialsException if the Basic header is not present or is not valid
     * Base64
     */
    static Jws<Claims> extractAndDecode(final String value,
                                        final String secret,
                                        final HttpServletRequest request) {

        final String token = value.startsWith(JWTUtil.TOKEN_PREFIX) ? value.substring(JWTUtil.TOKEN_PREFIX.length()) :  value.substring(JWTUtil.TOKEN_PREFIX_ENCODED.length());

        try {
            return Jwts.parser().setSigningKey(secret.getBytes(StandardCharsets.UTF_8)).parseClaimsJws(token);
        } catch (ExpiredJwtException expired) {
            throw new BadCredentialsException(JWTUtil.CredentialsState.AUTH_TOKEN_EXPIRED.name());
        } catch (Exception exp) {
            throw new BadCredentialsException(JWTUtil.CredentialsState.AUTH_TOKEN_INVALID.name());
        }

    }


}
