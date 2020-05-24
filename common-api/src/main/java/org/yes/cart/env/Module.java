/*
 * Copyright 2009 Inspire-Software.com
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

package org.yes.cart.env;

import java.io.Serializable;
import java.time.Instant;

/**
 * User: denispavlov
 * Date: 21/03/2018
 * Time: 20:26
 */
public interface Module extends Serializable {

    /**
     * Get functional area of module.
     *
     * @return functional area
     */
    String getFunctionalArea();

    /**
     * Get module name.
     *
     * @return name
     */
    String getName();

    /**
     * Get sub name.
     *
     * @return sub name
     */
    String getSubName();

    /**
     * Get load time.
     *
     * @return load time
     */
    Instant getLoaded();

}
