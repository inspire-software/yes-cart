/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/6/12
 * Time: 11:42 PM
 */

package org.yes.cart.report.impl {
import mx.collections.ArrayCollection;

import org.yes.cart.report.impl.ReportPair;


[Bindable]
[RemoteClass(alias="org.yes.cart.report.impl.ReportParameter")]
public class ReportParameter {

    public var name:String;

    public var langLabel:ArrayCollection;

    public var businesstype:String;

    public var mandatory:Boolean;


    public function ReportParameter() {
    }


    public function toString():String {
        return "ReportParameter{name=" + String(name) + ",langLabel=" + String(langLabel) + ",businesstype=" + String(businesstype) + ",mandatory=" + String(mandatory) + "}";
    }
}
}
