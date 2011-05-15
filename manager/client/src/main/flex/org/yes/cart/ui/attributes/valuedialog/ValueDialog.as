package org.yes.cart.ui.attributes.valuedialog {
import mx.controls.Button;

public interface ValueDialog {

    function get value():String;

    function set value(value:String):void;

    function get windowTitle():String;

    function set windowTitle(value:String):void;

    function get oldValue():String;

    function getButtonSave(): Button;

    function get code():String;

    function set code(value:String):void;

    function get attributeGroup():String;

    function set attributeGroup(value:String):void;




}
}