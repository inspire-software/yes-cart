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
 * Date: 12-07-13
 * Time: 8:24 AM
 */
package org.yes.cart.util {
import mx.collections.ListCollectionView;
import mx.utils.ObjectUtil;

/**
 * Utility class to allow easier use of domain objects with flex UI components
 */
public class DataModelUtils {
    public function DataModelUtils() {
    }

    /**
     * Preselect combo box value.
     *
     * @param comboBox combo box control (must support the following methods: dataProvider, selectedItem, selectedIndex)
     * @param dto dto object
     * @param dtoKeyProp property which would uniquely match the dataProvider item against dto
     * @param comboItemKeyProp property for combo items (if for those differ)
     * @return true if the selection was made
     */
    public static function selectCombo(comboBox:Object, dto:Object, dtoKeyProp:String = null, comboItemKeyProp:String = null):Boolean {
        if (dtoKeyProp != null) {
            if (!dto.hasOwnProperty(dtoKeyProp)) {
                throw new Error('No PK field ' + dtoKeyProp + ' found on DTO: ' + dto);
            } else if (comboItemKeyProp == null) {
                comboItemKeyProp = dtoKeyProp;
            }
        }

        var provider:ListCollectionView = comboBox.dataProvider;
        var i:int, ds:int, candidate:Object;
        if (dtoKeyProp != null) {
            for (i = 0, ds = provider.length; i < ds; i++) {
                candidate = provider.getItemAt(i);
                if (candidate[comboItemKeyProp] == dto[dtoKeyProp]) {
                    comboBox.selectedItem = candidate;
                    comboBox.selectedIndex = i;
                    return true;
                }
            }
        } else if (comboItemKeyProp != null) {
            for (i = 0, ds = provider.length; i < ds; i++) {
                candidate = provider.getItemAt(i);
                if (ObjectUtil.compare(candidate[comboItemKeyProp], dto) == 0) {
                    comboBox.selectedItem = candidate;
                    comboBox.selectedIndex = i;
                    return true;
                }
            }
        } else {
            for (i = 0, ds = provider.length; i < ds; i++) {
                candidate = provider.getItemAt(i);
                if (ObjectUtil.compare(candidate, dto) == 0) {
                    comboBox.selectedItem = candidate;
                    comboBox.selectedIndex = i;
                    return true;
                }
            }
        }
        return false;
    }

}
}
