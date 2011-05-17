

package org.yes.cart.util.cookie;

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

