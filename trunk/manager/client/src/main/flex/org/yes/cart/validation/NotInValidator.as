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