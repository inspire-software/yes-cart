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
