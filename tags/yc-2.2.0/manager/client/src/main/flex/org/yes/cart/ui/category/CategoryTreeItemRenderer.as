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

package org.yes.cart.ui.category {

import com.hexagonstar.util.debug.Debug;
import org.yes.cart.impl.CategoryDTOImpl;

import mx.controls.treeClasses.*;
import mx.collections.*;

[Bindable]
public class CategoryTreeItemRenderer  extends TreeItemRenderer {

    public function CategoryTreeItemRenderer() {
        super();
    }

    override public function set data(value:Object):void {
        if (value != null) {
            super.data = value;
            var categoryDto:CategoryDTOImpl = value as CategoryDTOImpl;
            if (categoryDto.changed) {
                setStyle("color", 0x660000);
                setStyle("fontWeight", 'bold');
            } else {
                setStyle("color", 0x000000);
                setStyle("fontWeight", 'normal');
            }
        }
    }

}
}