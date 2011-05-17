

package org.yes.cart.util.cookie;

/**
 * Exception to denote that Object cannot be converted to cookies.
 * <p/>
 * User: dogma
 * Date: 2011-May-17
 * Time: 3:22:09 PM
 */
public class UnableToCookielizeObjectException extends Exception {

    private static final long serialVersionUID = 20100116L;

    public UnableToCookielizeObjectException() {
    }

    public UnableToCookielizeObjectException(final String message) {
        super(message);
    }

    public UnableToCookielizeObjectException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public UnableToCookielizeObjectException(final Throwable cause) {
        super(cause);
    }
}