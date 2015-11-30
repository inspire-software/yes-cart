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

package org.yes.cart.bulkcommon.model;

/**
 * User: denispavlov
 * Date: 30/11/2015
 * Time: 21:41
 */
public interface ExtensibleValueAdapter extends ValueAdapter {

    /**
     * Extend basic value adapter.
     *
     * @param extension specific custom data type adapter
     * @param customDataType custom data type
     */
    void extend(ValueAdapter extension, String customDataType);

}
