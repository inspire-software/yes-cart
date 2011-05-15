package org.yes.cart.impl {

[Bindable]
[RemoteClass(alias="org.yes.cart.impl.AttributeDTOImpl")]
public class AttributeDTOImpl {

    public var code:String;

    public var mandatory:Boolean;

    public var val:String;

    public var name:String ;

    public var description:String;

    public var etypeId:Number;

    public var etypeName:String ;

    public var attributeId:Number ;

    public var attributegroupId:Number;

    public var allowduplicate:Boolean;

    public var allowfailover:Boolean;

    public var regexp:String;

    public var validationFailedMessage:String;

    public var rank:int;

    public var  choiceData:String;



    public function AttributeDTOImpl() {
    }


    public function toString():String {
        return "AttributeDTOImpl{code=" + String(code) +
               ",mandatory=" + String(mandatory) +
               ",name=" + String(name) +
               ",etypeName=" + String(etypeName) +
               ",regexp=" + String(regexp) +
               ",attributegroupId=" + String(attributegroupId) +
               ",allowduplicate=" + String(allowduplicate) +
               ",allowfailover=" + String(allowfailover) + "}";
    }
}
}