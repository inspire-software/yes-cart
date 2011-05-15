package org.yes.cart.impl {
[Bindable]
[RemoteClass(alias="org.yes.cart.impl.SeoDTOImpl")]
public class SeoDTOImpl {
    
    public var seoId:Number;

    public var uri:String;

    public var title:String;

    public var metakeywords:String;

    public var metadescription:String;
    
    public function SeoDTOImpl() {
    }


    public function toString():String {
        return "SeoDTOImpl{seoId=" + String(seoId)
                + ",uri=" + String(uri)
                + ",titleString=" + String(title) 
                + ",metakeywords=" + String(metakeywords)
                + ",metadescription=" + String(metadescription) + "}";
    }
}
}