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

/**
 * User: denispavlov
 * Date: 12-07-11
 * Time: 8:01 AM
 */
package org.yes.cart.util {
import org.yes.cart.impl.CategoryDTOImpl;

public class DomainUtils {
    public function DomainUtils() {
    }

    /**
     * Check if this domain object is new (i.e. not persisted yet)
     * @param dto dto object
     * @param idField property that denotes PK (e.g. 'attrId')
     * @return true if PK isNaN or 0
     */
    public static function isNew(dto:Object, idField:String):Boolean {
        if (dto.hasOwnProperty(idField)) {
            return isNewPK(dto[idField]);
        }
        throw new Error('No PK field ' + idField + ' found on DTO: ' + dto);
    }

    /**
     * Check if this domain object is new (i.e. not persisted yet)
     * @param pk dto PK
     * @return true if PK isNaN or 0
     */
    public static function isNewPK(pk:Number):Boolean {
        return isNaN(pk) || pk === 0;
    }

    /**
     * Check if this domain object is persistent
     * @param dto dto object
     * @param idField property that denotes PK (e.g. 'attrId')
     * @return true if PK not isNaN and not 0
     */
    public static function isPersistent(dto:Object, idField:String):Boolean {
        if (dto.hasOwnProperty(idField)) {
            return isPersistentPK(dto[idField]);
        }
        throw new Error('No PK field ' + idField + ' found on DTO: ' + dto);

    }

    /**
     * Check if this domain object is persistent
     * @param pk dto object PK
     * @return true if PK not isNaN and not 0
     */
    public static function isPersistentPK(pk:Number):Boolean {
        return !isNaN(pk) && pk !== 0;
    }

    /**
     * Determines if this category dto is root category object
     * @param cat dto
     * @return true if this is root category
     */
    public static function isRootCategory(cat:Object):Boolean {
        return cat != null && cat is CategoryDTOImpl && isPersistent(cat, 'categoryId') && cat['categoryId'] == cat['parentId'];
    }


    /**
     * @param str string to check
     * @return true if this string is not null and has length
     */
    public static function isNotBlankString(str:String):Boolean {
        return str != null && str.length > 0;
    }

}
}
