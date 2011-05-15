package org.yes.cart.impl {
[Bindable]
[RemoteClass(alias="org.yes.cart.impl.StateDTOImpl")]
    public class StateDTOImpl {

        public var stateId:Number;

        public var countryCode:String;

        public var stateCode:String;

        public var name:String;


        public function StateDTOImpl() {

            stateId = 0;

        }


        public function toString():String {
            return "StateDTOImpl{stateId=" + String(stateId) + ",countryCode=" + String(countryCode) + ",stateCode=" + String(stateCode) + ",name=" + String(name) + "}";
        }
}


}