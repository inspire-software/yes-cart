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

package org.yes.cart.web.shoppingcart.impl;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 6/13/11
 * Time: 7:18 PM
 */

import org.yes.cart.shoppingcart.impl.ShoppingCartImpl;
import org.yes.cart.web.support.util.cookie.annotations.PersistentCookie;


@PersistentCookie(value = "yc", expirySeconds = 864000, path = "/")
public class WebShoppingCartImpl extends ShoppingCartImpl {
}
