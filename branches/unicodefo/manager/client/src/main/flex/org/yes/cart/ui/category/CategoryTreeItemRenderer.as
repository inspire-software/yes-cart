package org.yes.cart.ui.category {

import com.hexagonstar.util.debug.Debug;
import org.yes.cart.impl.CategoryDTOImpl;

import mx.controls.treeClasses.*;
import mx.collections.*;

[Bindable]
public class CategoryTreeItemRenderer  extends TreeItemRenderer {

    private var categoryDto:CategoryDTOImpl;

    public function CategoryTreeItemRenderer() {
        super();
    }

    override public function set data(value:Object):void {
        if (value != null) {
            super.data = value;
            categoryDto = CategoryDTOImpl(value);
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