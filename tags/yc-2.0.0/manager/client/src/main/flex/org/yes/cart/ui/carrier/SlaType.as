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