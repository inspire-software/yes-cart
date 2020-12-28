package org.yes.cart.payment.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.payment.impl.PaySeraCheckoutPaymentGatewayImpl;

public class HttpQueryUtility {
	
	private static final Logger LOG = LoggerFactory.getLogger(PaySeraCheckoutPaymentGatewayImpl.class);

	public static String buildQueryString(Map<String, String> queryParams) {
		StringBuilder sb = new StringBuilder();
		
		for (Entry<String, String> entry : queryParams.entrySet()) {
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
				LOG.debug("Error while encoding query parameter: {}", e);
			}
		}
		
		return sb.toString();
    }

	public static String encodeBase64UrlSafe(String dataQuery) {
		String encodedText = encodeBase64(dataQuery);
		encodedText = encodedText.replace('+', '-');
		encodedText = encodedText.replace('/', '_');
		
		return encodedText;
	}
	
    public static byte[] decodeBase64UrlSafeAsByteArray(String encodedData)
    {
        encodedData = encodedData.replace('-', '+');
        encodedData = encodedData.replace('_', '/');
        return decodeBase64(encodedData);
    }

    public static String decodeBase64UrlSafeToString(String encodedData)
    {
        encodedData = encodedData.replace('-', '+');
        encodedData = encodedData.replace('_', '/');
        return decodeBase64ToString(encodedData);
    }
    
	public static String encodeBase64(String data) {
		byte[] encodedText = org.springframework.security.crypto.codec.Base64.encode(data.getBytes());
//		String encodedText = Base64.toBase64String(data.getBytes());

		return new String(encodedText, StandardCharsets.UTF_8);
	}
	
	public static String decodeBase64ToString(String data) {
		byte[] decodedText = org.springframework.security.crypto.codec.Base64.decode(data.getBytes());
		
		return new String(decodedText, StandardCharsets.UTF_8);
	}
	
	public static byte[] decodeBase64(String data) {
		return Base64.decodeBase64(data);
	}

	public static String calculateMD5(String data) {
		return DigestUtils.md5Hex(data);
	}
	
	public static String toQueryParam(List<String> list) {
		return CollectionUtils.isEmpty(list) ? null : String.join(",", list);
	}

	public static String toQueryParam(Integer value) {
		return value != null ? value.toString() : null;
	}

	public static String toQueryParam(Boolean value) {
		return value != null ? value ? "1" : "0" : "0";
	}
	
    public static byte[] getPublicKeyRawDataFromPEMFile(String pemFileContents) {
		String startCertificateMark = "-----BEGIN CERTIFICATE-----";
		String endCertificateMark = "-----END CERTIFICATE-----";
		int startCertificateIndex = pemFileContents.indexOf(startCertificateMark);
		int endCertificateIndex = pemFileContents.indexOf(endCertificateMark);
		String publicKeyBase64 = pemFileContents.substring(startCertificateIndex + startCertificateMark.length(), endCertificateIndex - startCertificateIndex - endCertificateMark.length() - 2);
		publicKeyBase64 = publicKeyBase64.trim();

		return decodeBase64(publicKeyBase64);
	}
    
    public static boolean verify(byte[] plainText, byte[] signature, PublicKey publicKey) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature publicSignature = Signature.getInstance("SHA1withRSA");
        publicSignature.initVerify(publicKey);
        publicSignature.update(plainText);

        return publicSignature.verify(signature);
    }
	
}
