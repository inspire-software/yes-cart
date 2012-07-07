/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/6/12
 * Time: 11:39 PM
 */

package org.yes.cart.report.impl {

[Bindable]
[RemoteClass(alias="org.yes.cart.report.impl.ReportPair")]
public class ReportPair {

    public var lang:String;

    public var value:String;


    public function ReportPair() {
    }


    public function toString():String {
        return "ReportPair{lang=" + String(lang) + ",value=" + String(value) + "}";
    }
}
}
