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

package org.yes.cart.bulkimport.model;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 08-May-2011
 * Time: 11:12:54
 */
public enum FieldTypeEnum {

    /**
     * Single Value field (Also used as PK if has lookup query to check for update
     * entities).
     */
    FIELD,
    /**
     * Foreign key field. (Uses look up queries to look up parent objects).
     */
    FK_FIELD,
    /**
     * Defines sub tuple which uses various column to populate its object.
     * This kind of field uses sub descriptor to define sub tuple columns.
     */
    SLAVE_INLINE_FIELD,
    /**
     * Defines sub tuple which is encoded fully inside current field and has
     * no access to other fields.
     * This kind of field uses sub descriptor to define sub tuple columns.
     */
    SLAVE_TUPLE_FIELD

}
