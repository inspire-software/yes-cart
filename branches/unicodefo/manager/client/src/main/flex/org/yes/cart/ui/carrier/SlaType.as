package org.yes.cart.ui.carrier {
public class SlaType {

    private var _data:String;
    private var _label:String;


    /**
     * Construct sla type.
     * @param data data
     * @param label label
     */
    public function SlaType(data:String, label:String) {
        _data = data;
        _label = label;
    }


    public function get data():String {
        return _data;
    }

    public function set data(value:String):void {
        _data = value;
    }

    public function get label():String {
        return _label;
    }

    public function set label(value:String):void {
        _label = value;
    }


   
}
}