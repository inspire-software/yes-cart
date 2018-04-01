/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
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

package org.yes.cart.util.log;

import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * User: denispavlov
 * Date: 07/03/2017
 * Time: 16:09
 */
public class Markers {

    /**
     * Marker for event that triggers alert message for admin.
     *
     * @return alert marker
     */
    public static Marker alert() {
        return MarkerFactory.getMarker("alert");
    }

    /**
     * Marker for event that has to be emailed.
     *
     * @return email marker
     */
    public static Marker email() {
        return MarkerFactory.getMarker("email");
    }

}
