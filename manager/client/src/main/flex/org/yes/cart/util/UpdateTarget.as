package org.yes.cart.util {

/**

*/
public class UpdateTarget {

    public static const NEW:Number = 1;
    public static const UPDATE:Number = 2;

    private var _action:int;
    private var _idx:int;


	
    public function UpdateTarget(action:int, idx:int) {
        _action = action;
        _idx = idx;
    }


    public function get action():int {
        return _action;
    }

    public function get idx():int {
        return _idx;
    }


    public function toString():String {
        return "UpdateTarget{_action=" + String(_action) + ",_idx=" + String(_idx) + "}";
    }
}
}