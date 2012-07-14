package org.yes.cart.validation {
import com.hexagonstar.util.debug.Debug;

import mx.validators.ValidationResult;
import mx.validators.Validator;

public class NotInValidator extends Validator{

    private var _inValues:Array = new Array();
    private var _errorMessage:String = " value not allowed";


    public function get errorMessage():String {
        return _errorMessage;
    }

    public function set errorMessage(value:String):void {
        _errorMessage = value;
    }

    public function get inValues():Array {
        return _inValues;
    }

    public function set inValues(value:Array):void {
        _inValues = value;
    }

    public function NotInValidator() {
        super();
    }


    /** {@inheritDoc}*/
    override protected function doValidation(value:Object):Array {
        var validatorResults:Array = new Array();
        validatorResults = super.doValidation(value);
        /*if (validatorResults.length > 0) {
            return validatorResults;
        } */

        for each (var obj:Object in inValues) {
            if (obj == value) {
                validatorResults.push(
                        new ValidationResult(
                                true, null, "valueAlreadyPresent",
                                "[" + String(value) + "] " + errorMessage
                                )
                        );
                break;
            }
        }

        return validatorResults;
    }


}
}