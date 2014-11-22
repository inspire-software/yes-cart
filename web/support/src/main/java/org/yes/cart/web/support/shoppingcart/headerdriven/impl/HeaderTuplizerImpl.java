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

package org.yes.cart.web.support.shoppingcart.headerdriven.impl;

import org.apache.commons.lang.StringUtils;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.web.support.shoppingcart.CartDetuplizationException;
import org.yes.cart.web.support.shoppingcart.CartTuplizationException;
import org.yes.cart.web.support.shoppingcart.CartTuplizer;
import org.yes.cart.web.support.shoppingcart.impl.AbstractCryptedTuplizerImpl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Default implementation of header tuplizer.
 * <p/>
 * The headers are crypted in base64 form. According to RFC 2109
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
public class HeaderTuplizerImpl extends AbstractCryptedTuplizerImpl implements CartTuplizer {

    private static final long serialVersionUID = 20100116L;

    private static final Map<String, String> EMPTY_HEADERS = new HashMap<String, String>();

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
     * @param headerName           header prefix
     * @param maxHeaderSizeInBytes maximum size of header in bytes
     */
    public HeaderTuplizerImpl(final String keyRingPassword,
                              final int chunkSize,
                              final String secretKeyFactoryName,
                              final String cipherName,
                              final String headerName,
                              final int maxHeaderSizeInBytes) {

        super(keyRingPassword, secretKeyFactoryName, cipherName);

        this.chunkSize = chunkSize;
        this.tuplizerSetting = new TuplizerSetting(headerName, maxHeaderSizeInBytes);

        if (this.tuplizerSetting.key == null) {
            final String errMsg = "header tuplizer misconfigured";
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
     * Creates header objects for given keys.
     *
     * @param values     header value to be written
     *
     * @return headers that represent requested object
     */
    Map<String, String> assembleHeadersForObject(final String[] values) {

        final TuplizerSetting meta = tuplizerSetting;

        if (values == null || values.length == 0) {
            return EMPTY_HEADERS;
        }

        final int currentHeaderCount = values.length + 1;

        final Map<String, String> headers = new HashMap<String, String>();
        for (int index = 0; index < currentHeaderCount - 1; index++) {
            headers.put(meta.key + index, values[index]);
        }
        final int terminatorIndex = currentHeaderCount - 1;
        headers.put(meta.key + (terminatorIndex), String.valueOf(meta.key.hashCode()));

        return headers;
    }

    Pattern getOrCreatePattern(final String key) {
        if (PATTERNS_CACHE.containsKey(key)) {
            return PATTERNS_CACHE.get(key);
        }
        final Pattern pattern = Pattern.compile(key + "(\\d+)");
        PATTERNS_CACHE.put(key, pattern);
        return pattern;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void tuplize(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse, final ShoppingCart shoppingCart) throws CartTuplizationException {

        final Map<String, String> headers = toHeaders(shoppingCart);
        for (final Map.Entry<String, String> entry : headers.entrySet()) {
            httpServletResponse.addHeader(entry.getKey(), entry.getValue());
        }

    }

    private Map<String, String> toHeaders(final Serializable serializable) throws CartTuplizationException {

        final String valueForCookies = toToken(serializable);
        final int sizeInBytes = valueForCookies.getBytes().length;
        if (sizeInBytes > tuplizerSetting.header75) {
            // This block will be useful for monitoring issues when cart overflows header buffer,
            // so that admins can be proactive and increase the size
            if (sizeInBytes > tuplizerSetting.headerMax) {
                ShopCodeContext.getLog(this).error("Header size ({}) exceeds allowed header size ({})", sizeInBytes, tuplizerSetting.headerMax);
            } else {
                ShopCodeContext.getLog(this).warn("Header size ({}) exceeds 75% of allowed header size ({})", sizeInBytes, tuplizerSetting.headerMax);
            }
        }
        return assembleHeadersForObject(split(valueForCookies));
    }


    /**
     * Assemble headers map to string.
     *
     * @param headers headers map to assemble the string
     *
     * @return assembled string
     */
    String assembleStringRepresentationOfObjectFromCookies(final Map<String, List<String>> headers)
            throws CartDetuplizationException {

        final TuplizerSetting meta = tuplizerSetting;

        final StringBuilder stringBuilder = new StringBuilder();
        int headerIndex = 0;
        boolean continueSearch = true;

        if (headers != null) {

            final String terminator = String.valueOf(meta.key.hashCode());
            final String objectKey = meta.key;

            while (continueSearch) {

                final String seekKey = objectKey + headerIndex;

                final List<String> headerValues = headers.get(seekKey);
                if (headerValues != null && headerValues.size() == 1) {
                    final String headerValue = headerValues.get(0);
                    continueSearch = !terminator.equals(headerValue);
                    if (continueSearch) {
                        stringBuilder.append(headerValue);
                        headerIndex++;
                    }
                } else {
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

        return httpServletRequest.getHeader(tuplizerSetting.key + "0") != null;

    }


    private Map<String, List<String>> createHeadersMap(final HttpServletRequest httpRequest) {

        final Map<String, List<String>> headers = new HashMap<String, List<String>>();
        final Enumeration headerNames = httpRequest.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            final String nextKey = (String) headerNames.nextElement();
            if (!headers.containsKey(nextKey)) {
                headers.put(nextKey, new ArrayList<String>());
            }
            final List<String> values = headers.get(nextKey);
            final Enumeration headerValues = httpRequest.getHeaders(nextKey);
            while (headerValues.hasMoreElements()) {
                final String nextValue = (String) headerValues.nextElement();
                values.add(nextValue);
            }
        }
        return headers;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public ShoppingCart detuplize(final HttpServletRequest httpServletRequest) throws CartDetuplizationException {

        final Map<String, List<String>> headers = createHeadersMap(httpServletRequest);
        return toObject(headers);

    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public <T extends Serializable> T toObject(final Map<String, List<String>> headers) throws CartDetuplizationException {

        final String input = assembleStringRepresentationOfObjectFromCookies(headers);
        return (T) toObject(input);

    }

    /**
     * Convenience class for meta data of object.
     */
    private class TuplizerSetting {

        private final String key;

        private final int headerMax;
        private final int header75;


        TuplizerSetting(final String key, final int maxHeaderSizeInBytes) {
            this.key = key;
            this.headerMax = maxHeaderSizeInBytes;
            this.header75 = (int) (((double) maxHeaderSizeInBytes) * 0.75d);
        }
    }


}
