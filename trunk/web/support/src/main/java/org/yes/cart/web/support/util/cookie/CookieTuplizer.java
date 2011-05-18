package org.yes.cart.web.support.util.cookie;

import javax.servlet.http.Cookie;
import java.io.Serializable;

/**
 * Cookie Tuplizer that allows to convert object from and to cookies.
 * <p/>
 * User: dogma
 * Date: 2011-May-17
 * Time: 1:52:56 PM
 */
public interface CookieTuplizer extends Serializable {

    /**
     * Convert object to cookies.
     *
     * @param oldCookies request cookies
     * @param object     object to convert
     * @return cookie array that represents the object
     * @throws UnableToCookielizeObjectException
     *          thrown when object cannot be converted to Cookies.
     */
    Cookie[] toCookies(Cookie[] oldCookies, Serializable object) throws UnableToCookielizeObjectException;

    /**
     * Convert cookies to object.
     *
     * @param cookies cookies available
     * @param object  object to restore from cookies
     * @return object that is restored from Cookies.
     * @throws UnableToObjectizeCookieException
     *          when Cookies cannot be converted to desired object.
     */
    <T extends Serializable> T toObject(Cookie[] cookies, final T object) throws UnableToObjectizeCookieException;

}
