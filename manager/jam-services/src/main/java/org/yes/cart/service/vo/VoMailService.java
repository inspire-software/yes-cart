/*
 * Copyright 2009 - 2016 Denys Pavlov, Igor Azarnyi
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

package org.yes.cart.service.vo;

/**
 * User: denispavlov
 */
public interface VoMailService {

    /**
     * Generate example email for given parameters.
     *
     * @param shopId shop PK
     * @param template template
     * @param order order number
     * @param delivery delivery number
     * @param customer customer number
     * @return HTML email with inlined resources
     * @throws Exception errors
     */
    String getShopMail(long shopId, String template, String order, String delivery, String customer) throws Exception;

}
