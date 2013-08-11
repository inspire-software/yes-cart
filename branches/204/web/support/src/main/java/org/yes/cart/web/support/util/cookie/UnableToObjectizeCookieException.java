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

/**
 * Exception to denote that Cookies cannot be converted to object.
 * <p/>
 * User: dogma
 * Date: 2011-May-17
 * Time: 3:22:09 PM
 */
public class UnableToObjectizeCookieException extends Exception {

    private static final long serialVersionUID = 20100116L;

    public UnableToObjectizeCookieException() {
    }

    public UnableToObjectizeCookieException(final String message) {
        super(message);
    }

    public UnableToObjectizeCookieException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public UnableToObjectizeCookieException(final Throwable cause) {
        super(cause);
    }

}

