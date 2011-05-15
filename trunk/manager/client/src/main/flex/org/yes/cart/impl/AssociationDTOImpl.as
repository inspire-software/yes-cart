package org.yes.cart.impl {

[Bindable]
[RemoteClass(alias="org.yes.cart.impl.AssociationDTOImpl")]
public class AssociationDTOImpl {

    public var associationId:Number;

    public var code:String;

    public var name:String;

    public var description:String;

    public function AssociationDTOImpl() {
    }


    public function toString():String {
        return "AssociationDTOImpl{associationId=" + String(associationId) +
               ",code=" + String(code) +
               ",name=" + String(name) + "}";
    }
}
}