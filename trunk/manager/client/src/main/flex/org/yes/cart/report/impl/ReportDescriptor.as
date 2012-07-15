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
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/6/12
 * Time: 11:49 PM
 */

package org.yes.cart.report.impl {
import mx.collections.ArrayCollection;

import org.yes.cart.report.impl.ReportPair;


[Bindable]
[RemoteClass(alias="org.yes.cart.report.impl.ReportDescriptor")]
public class ReportDescriptor {

    public var reportId:String;

    public var langLabel:ArrayCollection;

    public var hsqlQuery:String;

    public var parameters:ArrayCollection;

    public var langXslfo:ArrayCollection;


    public function ReportDescriptor() {
    }


    public function toString():String {
        return "ReportDescriptor{reportId=" + String(reportId) + ",langLabel=" + String(langLabel) + ",hsqlQuery=" + String(hsqlQuery) + ",parameters=" + String(parameters) + ",langXslfo=" + String(langXslfo) + "}";
    }
}
}
