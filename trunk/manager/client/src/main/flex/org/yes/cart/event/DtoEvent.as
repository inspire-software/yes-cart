/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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
