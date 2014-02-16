/*
 * Copyright 2014 Igor Azarnyi, Denys Pavlov
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
package org.yes.cart.payment.dto;

/**
 *
 * Payment parameters passed into payment gateway as map. This interface hold
 * additional parameters keys, that may be mixed in into map for PG.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 01-Feb-2014
 * Time: 13:24:24
 */
public interface PaymentMiscParam {

    /**
     * Ip address of client.
     */
    String CLIENT_IP = "CLIENT_IP";

}
