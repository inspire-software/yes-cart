package org.yes.cart.shopmanager {

import com.hexagonstar.util.debug.Debug;

import mx.controls.Alert;
import mx.messaging.ChannelSet;
import mx.messaging.channels.AMFChannel;
import mx.rpc.events.FaultEvent;
import mx.rpc.events.ResultEvent;

[Bindable]
public class ShopManagerGlobal {
    
    private static var _instance:ShopManagerGlobal = null;

    private var _duplicateErrorToMsgBox:Boolean = false;

    private var _username:String;

    private var _password:String;

    private var _url:String;

    private var _channelSet:ChannelSet;

    private var _amfChannel:AMFChannel;

    public function ShopManagerGlobal() {
        //throw new Error("Use getInstance() method instead");
    }

    public static function getInstance():ShopManagerGlobal {
        if (_instance == null) {
            _instance = new ShopManagerGlobal();
        }
        return _instance;
    }

    
    public static function get instance():ShopManagerGlobal {
        if (_instance == null) {
            _instance = new ShopManagerGlobal();
        }
        return _instance;
    }

    public function get channelSet():ChannelSet {
        return _channelSet;
    }

    public function set channelSet(value:ChannelSet):void {
        _channelSet = value;
    }

    public function get amfChannel():AMFChannel {
        return _amfChannel;
    }

    public function set amfChannel(value:AMFChannel):void {
        _amfChannel = value;
    }


    public function get username():String {
        return _username;
    }

    public function set username(value:String):void {
        _username = value;
    }

    public function get password():String {
        return _password;
    }

    public function set password(value:String):void {
        _password = value;
    }


    public function get url():String {
        return _url;
    }

    public function set url(value:String):void {
        _url = value;
    }

    public function get duplicateErrorToMsgBox():Boolean {
        return _duplicateErrorToMsgBox;
    }

    public function set duplicateErrorToMsgBox(value:Boolean):void {
        _duplicateErrorToMsgBox = value;
    }

    public function defaultResultHandler(event:ResultEvent, obj:Object = null): void {
        Debug.trace("Result handler:" + event.toString());
    }


    public function defaultFaultHandler(event:FaultEvent, obj:Object = null): void {
        /*[I] remoteWarehouseService is failed, reason:[FaultEvent fault=[RPC Fault faultString="An Authentication object was not found in the SecurityContext" faultCode="Client.Authentication" faultDetail="null"] messageId="F8BFFAB2-6F1D-E716-3A94-BC50F759F99F" type="fault" bubbles=false cancelable=true eventPhase=2]*/
        var errorMsg:String = "Fault handler:" + event.toString() + " token is " + obj;
        Debug.trace(errorMsg);
        if (duplicateErrorToMsgBox) {
            Alert.show(errorMsg, "Error");
        }
        
    }


}
}