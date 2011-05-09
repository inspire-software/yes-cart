package org.yes.cart.service.domain.impl;

import org.yes.cart.service.domain.HashHelper;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class MD5HashHelperImpl implements HashHelper {

    /**
     * Get the md5 string from given password.
     *
     * @param password given password
     * @return md5 password string
     * @throws java.security.NoSuchAlgorithmException     NoSuchAlgorithmException
     * @throws java.io.UnsupportedEncodingException UnsupportedEncodingException
     */
    public String getHash(final String password) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] md5hash;
        md.update(password.getBytes("utf-8"), 0, password.length());
        md5hash = md.digest();
        return convertToHex(md5hash);
    }

    private String convertToHex(byte[] data) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                if ((0 <= halfbyte) && (halfbyte <= 9))
                    buf.append((char) ('0' + halfbyte));
                else
                    buf.append((char) ('a' + (halfbyte - 10)));
                halfbyte = data[i] & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }


}
