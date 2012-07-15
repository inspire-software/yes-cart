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