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

package org.yes.cart.domain.entity;

/**
 * Taggable interface allows to mark taggable domain objects.
 * Each tag must be a single word with no spaces and special characters.
 *
 * Tags are used only for internal functionality such as product indexing,
 * promotion rules etc. and generally should not be exposed to customer.
 *
 * User: denispavlov
 * Date: 13-10-11
 * Time: 11:39 AM
 */
public interface Taggable {

    /**
     * Get space separated product tags.
     *
     * @return space separated tags
     */
    String getTag();

    /**
     * Set space separated product tags.
     *
     * @param tag space separated tags.
     */
    void setTag(String tag);

}
