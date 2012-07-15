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
 * Date: 12-07-05
 * Time: 6:25 PM
 */
package org.yes.cart.ui {
import mx.collections.ArrayCollection;
import mx.collections.ICollectionView;
import mx.controls.treeClasses.DefaultDataDescriptor;

import org.yes.cart.impl.CategoryDTOImpl;

public class CategoryTreeDescriptor extends DefaultDataDescriptor{

    private var _dataProvider:Object;

    public function CategoryTreeDescriptor(dataProvider:Object = null) {
        super();
        _dataProvider = dataProvider;
    }

    private function isSelected(catId:Object):Boolean {
        if (_dataProvider == null || _dataProvider.dataProvider == null) {
            return false;
        }
        for each (var cat:CategoryDTOImpl in _dataProvider.dataProvider) {
            if (cat.categoryId == catId) {
                return true;
            }
        }
        return false;
    }

    override public function getChildren(node:Object, model:Object = null):ICollectionView {
        if (node is CategoryDTOImpl) {
            var cat:CategoryDTOImpl = node as CategoryDTOImpl;
            if (cat.children == null || (cat.children != null && cat.children.length == 0)) {
                return null;
            }
            var children:ArrayCollection = new ArrayCollection();
            for each (var child:CategoryDTOImpl in cat.children) {
                if (isSelected(child.categoryId)) {
                    continue;
                }
                children.addItem(child);
            }
            return children;
        }
        return super.getChildren(node,  model);
    }

    override public function hasChildren(node:Object, model:Object = null):Boolean {
        if (node is CategoryDTOImpl) {
            var cat:CategoryDTOImpl = node as CategoryDTOImpl;
            return (cat.children != null && cat.children.length > 0);
        }
        return super.hasChildren(node,  model);
    }

    override public function isBranch(node:Object, model:Object = null):Boolean {
        if (node is CategoryDTOImpl) {
            var cat:CategoryDTOImpl = node as CategoryDTOImpl;
            return (cat.children != null && cat.children.length > 0);
        }
        return super.isBranch(node,  model);
    }



}
}
