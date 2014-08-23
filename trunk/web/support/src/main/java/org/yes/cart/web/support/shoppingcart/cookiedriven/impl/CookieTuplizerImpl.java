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

package org.yes.cart.web.support.shoppingcart.cookiedriven.impl;

import org.apache.commons.lang.StringUtils;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.web.support.shoppingcart.CartDetuplizationException;
import org.yes.cart.web.support.shoppingcart.CartTuplizationException;
import org.yes.cart.web.support.shoppingcart.CartTuplizer;
import org.yes.cart.web.support.shoppingcart.impl.AbstractCryptedTuplizerImpl;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Default implementation of cookie tuplizer.
 * <p/>
 * The cookies are crypted in base64 form. According to RFC 2109
 * specification cookie storage limit at least 300 cookies at least 4096
 * bytes per cookie, so it allow to store 1228800 bytes aprox 1 Mb.
 * Base64 representation will be split to chunks.
 *
 * However a lot of servers DO cap the size. The minimal limit is 4K, Tomcat default
 * and apache HTTP is 8K, which is the size of all headers provided in the request.
 *
 * But we allow to provide a setting (maxHeaderSizeInBytes) that will allow logging
 * 75% limit breach and max limit breach, so that admins can be proactive and detect
 * if header size should be increased.
 * <p/>
 * User: dogma
 * Date: 2011-May-17
 * Time: 2:17:57 PM
 */
public class CookieTuplizerImpl extends AbstractCryptedTuplizerImpl implements CartTuplizer {

    private static final long serialVersionUID = 20100116L;

    private static final Cookie[] EMPTY_COOKIES = new Cookie[0];

    private static final Map<String, Pattern> PATTERNS_CACHE = new HashMap<String, Pattern>();

    private final int chunkSize;

    private final TuplizerSetting tuplizerSetting;

    /**
     * Default Constructor.
     *
     * @param keyRingPassword      key ring password to use.
     * @param chunkSize            Base64 chunk size.
     * @param secretKeyFactoryName Secret Key Factory Name.
     * @param cipherName           Cipher name.
     * @param cookieName           cookie prefix
     * @param expirySeconds        seconds after which cookie expires
     * @param cookiePath           path for which the cookie will be saved
     * @param maxHeaderSizeInBytes maximum size of header in bytes
     */
    public CookieTuplizerImpl(final String keyRingPassword,
                              final int chunkSize,
                              final String secretKeyFactoryName,
                              final String cipherName,
                              final String cookieName,
                              final int expirySeconds,
                              final String cookiePath,
                              final int maxHeaderSizeInBytes) {

        super(keyRingPassword, secretKeyFactoryName, cipherName);

        this.chunkSize = chunkSize;
        this.tuplizerSetting = new TuplizerSetting(cookieName, expirySeconds, cookiePath, maxHeaderSizeInBytes);

        if (this.tuplizerSetting.key == null || this.tuplizerSetting.expiry == null) {
            final String errMsg = "cookie tuplizer misconfigured";
            ShopCodeContext.getLog(this).error(errMsg);
            throw new RuntimeException(errMsg);
        }

    }

    /**
     * Split string to chunks by size.
     *
     * @param stringToSplit string to split.
     * @return array of string chunks or null if string is blank.
     */
    String[] split(final String stringToSplit) {
        if (StringUtils.isNotBlank(stringToSplit)) {
            int strLenght = stringToSplit.length();
            int splitNum = strLenght / chunkSize;
            if (strLenght % chunkSize > 0) {
                splitNum += 1;
            }
            String[] result = new String[splitNum];
            for (int i = 0; i < splitNum; i++) {
                int startPos = i * chunkSize; // the substring starts here
                int endPos = startPos + chunkSize; // the substring ends here
                if (endPos > strLenght) { // make sure we don't cause an IndexOutOfBoundsException
                    endPos = strLenght;
                }
                result[i] = stringToSplit.substring(startPos, endPos);
            }
            return result;
        }
        return null;
    }

    /**
     * Creates Cookie objects for given keys.
     *
     * @param oldCookies cookies that have come from request
     * @param values     cookie value to be written
     *
     * @return cookies that represent requested object
     */
    Cookie[] assembleCookiesForObject(final Cookie[] oldCookies, final String[] values) {

        final TuplizerSetting meta = tuplizerSetting;

        if (values == null || values.length == 0) {
            return EMPTY_COOKIES;
        }

        final int previousCookieCount = countOldCookies(oldCookies, meta.key);
        final int currentCookieCount = values.length + 1;

        final Cookie[] cookies = new Cookie[Math.max(previousCookieCount, currentCookieCount)];
        for (int index = 0; index < currentCookieCount - 1; index++) {
            cookies[index] = createNewCookie(meta.key + index, values[index], meta.expiry, meta.path);
        }
        final int terminatorIndex = currentCookieCount - 1;
        cookies[terminatorIndex] = createNewCookie(meta.key + (terminatorIndex), String.valueOf(meta.key.hashCode()), meta.expiry, meta.path);


        if (currentCookieCount < previousCookieCount) {
            for (int index = currentCookieCount; index < previousCookieCount; index++) {
                cookies[index] = createNewCookie(meta.key + index, "", 0, meta.path); // overwrite surplus cookies.
            }
        }
        return cookies;
    }

    Pattern getOrCreatePattern(final String key) {
        if (PATTERNS_CACHE.containsKey(key)) {
            return PATTERNS_CACHE.get(key);
        }
        final Pattern pattern = Pattern.compile(key + "(\\d+)");
        PATTERNS_CACHE.put(key, pattern);
        return pattern;
    }

    int countOldCookies(final Cookie[] oldCookies, final String objectKey) {

        if (oldCookies == null || oldCookies.length == 0) {
            return 0;
        }

        final Pattern pattern = getOrCreatePattern(objectKey);

        int maxIndex = -1;
        for (Cookie cookie : oldCookies) {
            if (cookie.getName().startsWith(objectKey)) {
                final Matcher matcher = pattern.matcher(cookie.getName());
                if (matcher.matches()) {
                    final Integer digits = Integer.valueOf(matcher.group(1));
                    maxIndex = Math.max(digits, maxIndex);
                }
            }
        }
        return maxIndex + 1;

    }

    private Cookie createNewCookie(final String name, final String value, final int maxAgeInSeconds, final String path) {
        final Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(maxAgeInSeconds);
        cookie.setPath(path);
        cookie.setVersion(1); // allow to have base64 encoded value in cookie
        return cookie;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void tuplize(final HttpServletRequest httpServletRequest,
                        final HttpServletResponse httpServletResponse,
                        final ShoppingCart shoppingCart) throws CartTuplizationException {

        final Cookie[] oldCookies = httpServletRequest.getCookies();
        final Cookie[] newCookies = toCookies(oldCookies, shoppingCart);

        for (Cookie cookie : newCookies) {
            httpServletResponse.addCookie(cookie);
        }

    }

    private Cookie[] toCookies(final Cookie[] oldCookies, final Serializable serializable) throws CartTuplizationException {

        final String valueForCookies = toToken(serializable);
        final int sizeInBytes = valueForCookies.getBytes().length;
        if (sizeInBytes > tuplizerSetting.header75) {
            // This block will be useful for monitoring issues when cart overflows header buffer,
            // so that admins can be proactive and increase the size
            if (sizeInBytes > tuplizerSetting.headerMax) {
                ShopCodeContext.getLog(this).error("Cookie size ({}) exceeds allowed header size ({})", sizeInBytes, tuplizerSetting.headerMax);
            } else {
                ShopCodeContext.getLog(this).warn("Cookie size ({}) exceeds 75% of allowed header size ({})", sizeInBytes, tuplizerSetting.headerMax);
            }
        }
        return assembleCookiesForObject(oldCookies, split(valueForCookies));
    }


    /**
     * Assemble cookies array to string.
     *
     * @param cookies cookies array to assemble the string
     *
     * @return assembled string
     */
    String assembleStringRepresentationOfObjectFromCookies(final Cookie[] cookies)
            throws CartDetuplizationException {

        final TuplizerSetting meta = tuplizerSetting;

        final StringBuilder stringBuilder = new StringBuilder();
        int cookieIndex = 0;
        boolean continueSearch = true;

        if (cookies != null) {

            final String terminator = String.valueOf(meta.key.hashCode());
            final String objectKey = meta.key;

            while (continueSearch) {

                final String seekKey = objectKey + cookieIndex;
                boolean keyFound = false;

                for (Cookie cookie : cookies) {


                    if (cookie.getName().equals(seekKey)) {
                        keyFound = true;
                        continueSearch = !terminator.equals(cookie.getValue());
                        if (continueSearch) {
                            stringBuilder.append(cookie.getValue());
                            cookieIndex++;
                        }
                        break;
                    }
                }

                if (!keyFound) {
                    continueSearch = false;
                }
            }
        }
        return stringBuilder.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(final HttpServletRequest httpServletRequest) {

        final Cookie[] cookies = httpServletRequest.getCookies();
        for (final Cookie cookie : cookies) {
            if (cookie.getName().startsWith(tuplizerSetting.key)) {
                return true;
            }
        }
        return false;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ShoppingCart detuplize(final HttpServletRequest httpServletRequest) throws CartDetuplizationException {

        final Cookie[] cookies = httpServletRequest.getCookies();
        return toObject(cookies);

    }

    private <T extends Serializable> T toObject(final Cookie[] cookies) throws CartDetuplizationException {

        final String input = assembleStringRepresentationOfObjectFromCookies(cookies);
        return toObject(input);

    }

    /**
     * Convenience class for meta data of object.
     */
    private class TuplizerSetting {

        private final String key;
        private final Integer expiry;
        private final String path;

        private final int headerMax;
        private final int header75;


        TuplizerSetting(final String key, final Integer expiry, final String path, final int maxHeaderSizeInBytes) {
            this.key = key;
            this.expiry = expiry;
            this.path = path;
            this.headerMax = maxHeaderSizeInBytes;
            this.header75 = (int) (((double) maxHeaderSizeInBytes) * 0.75d);
        }
    }


}
