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

package org.yes.cart.service.domain;

/**
 * User: denispavlov
 * Date: 21/10/2019
 * Time: 11:59
 */
public interface SecurityAccessControlService<T> {


    /**
     * Method to determine if given request should be throttled.
     *
     * @param request request
     *
     * @return true if should be throttled
     */
    boolean restrict(final T request);


}
