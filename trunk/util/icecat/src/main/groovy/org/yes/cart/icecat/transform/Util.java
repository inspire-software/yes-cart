package org.yes.cart.icecat.transform;

/**
 * User: igora Igor Azarny
 * Date: 6/10/12
 * Time: 1:04 PM
 */
public class Util {

    public static boolean isNotBlank(final String str) {
        return !isBlank(str);
    }

    public static boolean isBlank(final String str) {

        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if ((Character.isWhitespace(str.charAt(i)) == false)) {
                return false;
            }
        }
        return true;

    }

}
