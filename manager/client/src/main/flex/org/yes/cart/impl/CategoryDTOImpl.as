package org.yes.cart.impl {
import mx.collections.ArrayCollection;

[Bindable]
[RemoteClass(alias="org.yes.cart.impl.CategoryDTOImpl")]
public class CategoryDTOImpl {
    
    public var categoryId:Number ;

    public var parentId:Number;

    public var rank:int;

    public var productTypeId:Number;
    
    public var productTypeName:String;

    public var name:String;

    public var description:String;

    public var uitemplate:String;

    public var availablefrom:Date ;

    public var availabletill:Date ;

    public var seoId:Number;

    public var navigationByAttributes:Boolean ;

    public var navigationByBrand:Boolean ;

    public var navigationByPrice:Boolean ;

    public var navigationByPriceTiers:String ;

    public var attribute:ArrayCollection;

    public var children:ArrayCollection;

    public var changed:Boolean = false;
    
    
    public function CategoryDTOImpl() {
    }

    public function addChild(child:CategoryDTOImpl):CategoryDTOImpl {
        if (children == null) {
            children = new ArrayCollection();
        }
        child.parentId = this.categoryId;
        children.addItem(child);
        return child;
    }


    public function toString():String {
        return "CategoryDTOImpl{categoryId=" + String(categoryId)
                + ",parentId=" + String(parentId)
                + ",rankLint=" + String(rank)
                + ",productTypeId=" + String(productTypeId)
                + ",name=" + String(name)
                + ",description=" + String(description)
                + ",uitemplate=" + String(uitemplate)
                + ",availablefrom=" + String(availablefrom)
                + ",availabletill=" + String(availabletill)
                + ",seoId=" + String(seoId)
                + ",navigationByAttributes=" + String(navigationByAttributes)
                + ",navigationByBrand=" + String(navigationByBrand)
                + ",navigationByPrice=" + String(navigationByPrice)
                + ",changed=" + String(changed)
                + ",attribute=\n["+ String(attribute)
                + "]\n,children=" + String(children) + "}";
    }
}
}