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

package org.yes.cart.web.support.util.cookie.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that defines the key for Cookie objects.
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
