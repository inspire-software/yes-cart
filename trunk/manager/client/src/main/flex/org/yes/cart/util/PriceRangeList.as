package org.yes.cart.util {
[Bindable]
public class PriceRangeList {

    private var _currency:String;

    private var _entry:XML;

    private var _tag:Object;


    public function get tag():Object {
        return _tag;
    }

    public function set tag(value:Object):void {
        _tag = value;
    }

    public function get currency():String {
        return _currency;
    }

    public function set currency(value:String):void {
        _currency = value;
    }

    public function get entry():XML {
        return _entry;
    }

    public function set entry(value:XML):void {
        _entry = value;
    }


    public function PriceRangeList(currency:String, entry:XML, tag:Object) {
        _currency = currency;
        _entry = entry;
        _tag = tag;
    }


    public function toString():String {
        return "PriceRangeList{_currency=" + String(_currency) + ",_entry=" + String(_entry) + ",_tag=" + String(_tag) + "}";
    }
}
}