package org.yes.cart.impl {
[Bindable]
[RemoteClass(alias="org.yes.cart.domain.dto.impl.CountryDTOImpl")]
    public class CountryDTOImpl {

        public var countryId:Number;

        public var countryCode:String;

        public var isoCode:String;

        public var name:String;

        public function CountryDTOImpl() {
        }

    }
}