/**
 * User: denispavlov
 * Date: 12-07-13
 * Time: 12:30 PM
 */
package org.yes.cart.event {
import flash.events.Event;

/**
 * Event to carry dto objects
 */
public class DtoEvent extends Event {

    public static const UPDATE:String = "update";
    public static const REMOVE:String = "remove";

    private var _dto:Object;

    public function DtoEvent(type:String, dto:Object = null, bubbles:Boolean = false, cancelable:Boolean = false) {
        super(type, bubbles, cancelable);
        _dto = dto;
    }

    public function get dto():Object {
        return _dto;
    }

    public function set dto(value:Object):void {
        _dto = value;
    }

    override public function clone():Event {
        return new DtoEvent(type, bubbles, cancelable, dto);
    }
}
}
