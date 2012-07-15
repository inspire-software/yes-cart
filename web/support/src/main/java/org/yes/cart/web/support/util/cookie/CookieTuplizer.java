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
