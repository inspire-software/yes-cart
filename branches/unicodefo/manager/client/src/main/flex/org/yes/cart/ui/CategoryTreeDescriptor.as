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
