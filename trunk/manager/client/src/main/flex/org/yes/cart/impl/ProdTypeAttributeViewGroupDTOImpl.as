/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 6/30/12
 * Time: 11:16 AM
 */

package org.yes.cart.impl {

[Bindable]
[RemoteClass(alias="org.yes.cart.domain.dto.impl.ProdTypeAttributeViewGroupDTOImpl")]
public class ProdTypeAttributeViewGroupDTOImpl {

    public var prodTypeAttributeViewGroupId:Number;

    public var producttypeId:Number;

    public var attrCodeList:String;

    public var rank:Number;

    public var name:String;

    public function ProdTypeAttributeViewGroupDTOImpl() {
    }


    public function toString():String {
        return "ProdTypeAttributeViewGroupDTOImpl{prodTypeAttributeViewGroupId="
                + String(prodTypeAttributeViewGroupId) + ",producttypeId="
                + String(producttypeId) + ",attrCodeList="
                + String(attrCodeList) + ",rank="
                + String(rank) + ",name=" + String(name) + "}";
    }
}

}
