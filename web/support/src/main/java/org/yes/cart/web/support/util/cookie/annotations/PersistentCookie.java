package org.yes.cart.web.support.util.cookie.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotation that defines the key for Cookie objects.
 * <p/>
 * User: dogma
 * Date: 2011-May-17
 * Time: 1:57:17 PM
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PersistentCookie {

    /**
     * @return key to be used for object's Cookies
     */
    String value();

    /**
     * @return expiry time for Cookie in seconds. By Default set to 0.
     */
    int expirySeconds() default 0;

    /**
     * @return path for the cookie to prevent duplication in nested paths. By default set to root ("/").
     */
    String path() default "/";

}
