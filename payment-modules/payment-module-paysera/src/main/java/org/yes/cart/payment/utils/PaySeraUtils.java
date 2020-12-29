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

package org.yes.cart.payment.utils;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.payment.impl.PaySeraCheckoutPaymentGatewayImpl;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class PaySeraUtils {
	
	private static final Logger LOG = LoggerFactory.getLogger(PaySeraCheckoutPaymentGatewayImpl.class);

	/**
	 * Concatenates data query string for form post data.
	 *
	 * @param queryParams parameters for payment request
	 *
	 * @return query string
	 */
	public static String buildQueryString(final Map<String, String> queryParams) {

		final StringBuilder sb = new StringBuilder();
		
		for (final Entry<String, String> entry : queryParams.entrySet()) {
			if (StringUtils.isEmpty(entry.getValue())) {
				continue;
			}
			
			if (sb.length() > 0) {
				sb.append("&");
			}
			
			try {
				sb.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8.toString()));
				sb.append("=");
				sb.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.toString()));
			} catch (UnsupportedEncodingException e) {
				LOG.error("Error while encoding query parameter: " + entry, e);
			}
		}
		
		return sb.toString();
    }

	/**
	 * Parse query string back into paramter map (expanding callback data).
	 *
	 * @param queryString query string decoded base64
	 *
	 * @return parameter map
	 */
	public static Map<String, String> parseQueryString(final String queryString) {

		final Map<String, String> data = new LinkedHashMap<>();

		if (queryString != null) {
			final String[] keyValues = StringUtils.split(queryString, '&');
			if (keyValues != null) {
				for (final String keyValue : keyValues) {
					final String[] keyAndValue = StringUtils.split(keyValue, '=');
					try {
						data.put(keyAndValue[0], URLDecoder.decode(keyAndValue[1], StandardCharsets.UTF_8.toString()));
					} catch (UnsupportedEncodingException e) {
						LOG.error("Error while decoding query parameter: " + keyAndValue, e);
					}

				}
			}
		}

		return data;
		
	}

	/**
	 * PaySera specific special character encoding.
	 *
	 * @param dataQuery raw
	 *
	 * @return encoded
	 */
	public static String encodeBase64UrlSafe(final String dataQuery) {
		return encodeBase64(dataQuery).replace('+', '-').replace('/', '_');
	}

	/**
	 * PaySera specific special character decoding.
	 *
	 * @param encodedData encoded
	 *
	 * @return original
	 */
    public static byte[] decodeBase64UrlSafeAsByteArray(String encodedData) {
        return decodeBase64(encodedData.replace('-', '+').replace('_', '/'));
    }

	/**
	 * PaySera specific special character decoding.
	 *
	 * @param encodedData encoded
	 *
	 * @return original
	 */
    public static String decodeBase64UrlSafeToString(final String encodedData) {
    	if (StringUtils.isNotBlank(encodedData)) {
    		try {
				return decodeBase64ToString(encodedData.replace('-', '+').replace('_', '/'));
			} catch (Exception exp) {
    			LOG.error("Unable to encode: " + encodedData, exp);
			}
		}
		return null;
    }
    
	private static String encodeBase64(final String data) {
		byte[] encodedText = org.springframework.security.crypto.codec.Base64.encode(data.getBytes());
//		String encodedText = Base64.toBase64String(data.getBytes());

		return new String(encodedText, StandardCharsets.UTF_8);
	}

	private static String decodeBase64ToString(final String data) {
		byte[] decodedText = org.springframework.security.crypto.codec.Base64.decode(data.getBytes());
		
		return new String(decodedText, StandardCharsets.UTF_8);
	}

	private static byte[] decodeBase64(final String data) {
		return Base64.decodeBase64(data);
	}

	/**
	 * MD5 for SS1 signature verification.
	 *
	 * @param data data
	 *
	 * @return md5
	 */
	public static String calculateMD5(final String data) {
		return DigestUtils.md5Hex(data);
	}

	/**
	 * CSV value converter
	 *
	 * @param list CSV
	 *
	 * @return query param
	 */
	public static String toQueryParam(final List<String> list) {
		return CollectionUtils.isEmpty(list) ? null : String.join(",", list);
	}

	/**
	 * Integer value converter
	 *
	 * @param value int
	 *
	 * @return query param
	 */
	public static String toQueryParam(final Integer value) {
		return value != null ? value.toString() : null;
	}

	/**
	 * Boolean value converter
	 *
	 * @param value boolean
	 *
	 * @return query param
	 */
	public static String toQueryParam(final Boolean value) {
		return value != null ? value ? "1" : "0" : "0";
	}
	
}
