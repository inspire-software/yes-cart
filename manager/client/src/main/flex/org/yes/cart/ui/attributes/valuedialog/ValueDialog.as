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

package org.yes.cart.ui.attributes.valuedialog {
import mx.controls.Button;

public interface ValueDialog {

    /**
     * Attribute value
     */
    function get value():String;

    /**
     * Attribute value
     *
     * @param value value
     */
    function set value(value:String):void;

    /**
     * Attribute value type (etype.businessname)
     */
    function get valueTypeName():String;

    /**
     * Attribute value type (etype.businessname)
     *
     * @param value value
     */
    function set valueTypeName(value:String):void;

    /**
     * Attribute display value
     */
    function get displayValues():Object;

    /**
     * Attribute display value
     *
     * @param value value
     */
    function set displayValues(value:Object):void;

    /**
     * Pop up window title
     */
    function get windowTitle():String;

    /**
     * Pop up window title
     *
     * @param value value
     */
    function set windowTitle(value:String):void;

    /**
     * Old value
     */
    function get oldValue():String;

    /**
     * Pop up window save button
     */
    function getButtonSave(): Button;

    /**
     * Product or sku code
     */
    function get code():String;

    /**
     * Product or sku code
     *
     * @param value value
     */
    function set code(value:String):void;

    /**
     * Attribute code.
     */
    function get attributeCode():String;

    /**
     * Attribute code.
     *
     * @param value value
     */
    function set attributeCode(value:String):void;

    /**
     * Attribute group
     */
    function get attributeGroup():String;

    /**
     * Attribute group
     *
     * @param value value
     */
    function set attributeGroup(value:String):void;

    /**
     * Additional information about value.
     *
     * @param value value
     */
    function setInformation(value:String):void;


}
}