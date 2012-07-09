/*
 * ``The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 */
package com.hexagonstar.util.debug
{
	import flash.display.Stage;
	import flash.events.Event;
	import flash.events.StatusEvent;
	import flash.net.LocalConnection;
	import flash.net.SharedObject;
	import flash.net.SharedObjectFlushStatus;
	import flash.system.Capabilities;
	import flash.system.Security;
	import flash.system.SecurityPanel;
	import flash.system.System;
	import flash.utils.ByteArray;
	import flash.utils.describeType;

	
	/**
	 * The Debug class is a static class that is used to send debugging information
	 * to Alcon. It can be used to simply output information with the <code>trace()
	 * </code> method, recursively output objects with <code>traceObj()</code> or
	 * inspect objects with the <code>inspect()</code> method, start monitoring the
	 * applications framerate with <code>monitor()</code> and to use other debugging
	 * assets like a Stopwatch.<br>
	 * To use this class import it into any class that should make use of it and then
	 * call any of the debugging methods anywhere in your class.
	 * 
	 * @example
	 * <p><pre>
	 * import com.hexagonstar.util.debug.Debug;
	 * 
	 * public class Test extends Sprite {
	 *     public function Test() {
	 *         Debug.monitor(stage);
	 *         Debug.trace("Test");
	 *     }
	 * }</pre>
	 */
	public final class Debug
	{
		////////////////////////////////////////////////////////////////////////////////////////
		// Constants                                                                          //
		////////////////////////////////////////////////////////////////////////////////////////
		
		/**
		 * The Debug.LEVEL_DEBUG constant defines the value of the Debug Filtering Level.
		 */
		public static const LEVEL_DEBUG:int	= 0;
		
		/**
		 * The Debug.LEVEL_INFO constant defines the value of the Info Filtering Level.
		 */
		public static const LEVEL_INFO:int	= 1;
		
		/**
		 * The Debug.LEVEL_WARN constant defines the value of the Warn Filtering Level.
		 */
		public static const LEVEL_WARN:int	= 2;
		
		/**
		 * The Debug.LEVEL_ERROR constant defines the value of the Error Filtering Level.
		 */
		public static const LEVEL_ERROR:int	= 3;
		
		/**
		 * The Debug.LEVEL_FATAL constant defines the value of the Fatal Filtering Level.
		 */
		public static const LEVEL_FATAL:int	= 4;
		
		
		////////////////////////////////////////////////////////////////////////////////////////
		// Variables                                                                          //
		////////////////////////////////////////////////////////////////////////////////////////
		
		private static var _filterLevel:int					= 0;
		private static var _isConnected:Boolean				= false;
		private static var _isPollingFPS:Boolean				= false;
		private static var _isEnabled:Boolean				= true;
		
		private static var _stage:Stage;
		private static var _connection:LocalConnection;
		private static var _fpsMeter:FPSMeter;
		private static var _stopWatch:StopWatch;
		
		
		////////////////////////////////////////////////////////////////////////////////////////
		// Public Methods                                                                     //
		////////////////////////////////////////////////////////////////////////////////////////
		
		/**
		 * Internal Constructor. The Debug class is static and doesn't need
		 * to be instantiated. Use any of the methods directly, e.g. <code>
		 * Debug.trace();</code>
		 */
		function Debug()
		{
		}
		
		
		/**
		 * Traces out any specified data to Alcon's Trace logger. The data can be of
		 * any type e.g. String, Number, Object, Array etc. For tracing Objects and
		 * Arrays recursively use the <code>traceObj()</code> method instead. To inspect
		 * any object use the <code>inspect()</code> method.<br>
		 * The trace method takes up to two arguments, the first being the data to
		 * output and the second an optional integer that determines the filter
		 * (or severity) level that the data is being output with. If specified,
		 * the filter level must be within the range of existing levels (0 to 4).
		 * 
		 * @example
		 * <p><pre>
		 * Debug.trace("Profanity is one language all
		 *     computer users know.");
		 * Debug.trace("Software never has bugs. It just
		 *     develops random features.", 0);
		 * Debug.trace(18 + 4, Debug.LEVEL_ERROR);
		 * Debug.trace(Math.PI, Debug.LEVEL_WARN);</pre>
		 * 
		 * @param arg1 The data to be traced.
		 * @param arg2 An integer that determines the Debugging Filter Level.
		 */
		public static function trace(... args):void
		{
			var l:int = (args[1] is int) ? args[1] : 1;
			
			/* Only show messages equal or higher than current filter level */
			if (l >= _filterLevel && l < 7)
			{
				send("onData", args[0], l, 0);
			}
		}
		
		
		/**
		 * Traces the specified Object, Array or Class recursively to Alcon's Trace
		 * logger. Optionally the recursion depth can be set with the depth argument.
		 * 
		 * @example
		 * <p><pre>
		 * var array:Array = ["Gerry", "Gail", "Garth"];
		 * Debug.traceObj(array);
		 * var obj:Object = {n1: "Bill", n2: "Giles"};
		 * Debug.traceObj(obj, 64, Debug.LEVEL_DEBUG);
		 * Debug.traceObj(stage, 128);</pre>
		 * 
		 * @param obj   The Object, Array or Class to output recursively.
		 * @param depth The recursion depth that is used to loop
		 *               through the objects children.
		 * @param level The filter level with that the trace is made.
		 */
		public static function traceObj(obj:Object, depth:int = 64, level:int = 1):void
		{
			/* Only show messages equal or higher than current filter level */
			if (level >= _filterLevel && level < 7)
			{
				send("onData", obj, level, depth);
			}
		}
		
		
		/**
		 * Inspects the specified Object, Array or Class with Alcon's Object
		 * Inspector. For classes only public properties and methods can be
		 * inspected. Also note that uint's are always converted to int's by
		 * the Inspector.
		 * 
		 * @example
		 * <p><pre>
		 * var array:Array = [777, 123, "Gerry", "Gail"];
		 * Debug.inspect(array);
		 * var obj:Object = {n1: "Bill", n2: "Jenny"};
		 * Debug.inspect(obj);
		 * Debug.inspect(new Bitmap());</pre>
		 * 
		 * @param obj The Object, Array or Class to inspect.
		 */
		public static function inspect(obj:Object):void
		{
			send("onInspect", obj, 1, -1);
		}
		
		
		/**
		 * Outputs a hexadecimal dump of the specified object to Alcon's Trace
		 * Logger.
		 * 
		 * @example
		 * <p><pre>
		 * var array:Array = ["Sandy", "Beth", "Kayla"];
		 * Debug.hexDump(array);
		 * var obj:Object = {n1: "Sandy", n2: "Larry"};
		 * Debug.hexDump(obj);
		 * Debug.hexDump(stage);</pre>
		 * 
		 * @param obj The object to output as a hex dump.
		 */
		public static function hexDump(obj:Object):void
		{
			/* TODO Add optional Start & End parameter. */
			send("onHexDump", obj, 0, 0);
		}
		
		
		/**
		 * Forces an immediate Garbage Collector mark/sweep. This method is only
		 * guaranteed to work for use in AIR applications!<br>It first tries to
		 * use <code>System.gc()</code> and if that fails (i.e. when run in Flash
		 * Player) it falls back to a different method to cause a garbage collection
		 * that is not officially supported by ActionScript. Handle with care when
		 * run in a non-AIR-Runtime!
		 */
		public static function forceGC():void
		{
			try
			{
				/* Disabled for now! */
				//System.gc();
			}
			catch (e1:Error)
			{
				try
				{
					new LocalConnection().connect("forceGC");
					new LocalConnection().connect("forceGC");
				}
				catch (e2:Error)
				{
				}
			}
		}
		
		
		/**
		 * Clears Alcon's Trace buffer. This does the same like sending the CLR signal
		 * manually: <code>Debug.trace("[%CLR%]");</code>
		 * 
		 */
		public static function clear():void
		{
			Debug.trace("[%CLR%]", 5);
		}
		
		
		/**
		 * Output's a delimiter String to Alcon's Trace Logger that can be used to
		 * quickly seperate different kinds of debug informations, making it easier
		 * to read them.<br>This does the same like sending the DLT signal manually:
		 * <code>Debug.trace("[%DLT%]");</code>
		 */
		public static function delimiter():void
		{
			Debug.trace("[%DLT%]", 5);
		}
		
		
		/**
		 * Pauses Alcon. With this method Alcon's Trace and File Loggers are brought
		 * into Pause mode.<br> This does the same like sending the PSE signal manually:
		 * <code>Debug.trace("[%PSE%]");</code>
		 */
		public static function pause():void
		{
			Debug.trace("[%PSE%]", 5);
		}
		
		
		/**
		 * Sends a Time & Date stamp to Alcon's Trace Logger. This does the same like
		 * sending the TME signal manually: <code>Debug.trace("[%TME%]");</code>
		 * 
		 * @example
		 * <p><pre>
		 * Debug.time();</pre>
		 * <p>Output:<p>
		 * Sun Aug 17 2008 03:35:34 PM
		 */
		public static function time():void
		{
			Debug.trace("[%TME%]", 5);
		}
		
		
		/**
		 * createCategory
		 */
		//public static function createCategory(id:int, name:String = "",
		//	textColor:uint = 0x000000, bgColor:uint = 0xFFFF00):void
		//{
			// TODO
		//	send("onCategory", [id, name, textColor, bgColor], 0, 0);
		//}
		
		
		// Monitoring Methods //////////////////////////////////////////////////////////////////
		
		/**
		 * Starts monitoring the application. When called starts measuring the currently
		 * debugged application's FPS (frames per second) and FRT (frame render time) and
		 * sends the values and the current memory consumption amount to Alcon's App Monitor.
		 * 
		 * @param stage The Stage object of the current host application.
		 * @param pollInterval The interval in milliseconds with that framerate-
		 *         related data is being polled. The lower this value is the faster
		 *         Alcon's App Monitor will update.
		 */
		public static function monitor(stage:Stage, pollInterval:int = 500):void
		{
			if (_isPollingFPS) Debug.stop();
			
			if (_isEnabled && !_fpsMeter)
			{
				_isPollingFPS = true;
				_stage = stage;
				sendCapabilities();
				_fpsMeter = new FPSMeter(_stage, pollInterval);
				_fpsMeter.addEventListener(FPSMeter.FPS_UPDATE, onFPSUpdate);
				_fpsMeter.start();
			}
		}
						/**
		 * Places a marker in Alcon's App Monitor graph. Optionally a color
		 * can be specified for the marker.
		 * 
		 * @param color The color for the marker.
		 */
		public static function mark(color:uint = 0xFF00FF):void
		{
			send("onMarker", color, 1, -1);
		}
		
		
		/**
		 * Stops monitoring the application after it has been started with
		 * <code>Debug.monitor()</code>.
		 */
		public static function stop():void
		{
			if (_fpsMeter)
			{
				_isPollingFPS = false;
				_fpsMeter.stop();
				_fpsMeter.removeEventListener(FPSMeter.FPS_UPDATE, onFPSUpdate);
				_fpsMeter = null;
				_stage = null;
			}
		}
		
		
		// Timer Methods ///////////////////////////////////////////////////////////////////////
		
		/**
		 * Starts a Stopwatch to measure a time amount. This method can be used to
		 * measure the execution time for any code in the host application.
		 * Optionally a title can be specified to describe what the Stopwatch is
		 * measuring.
		 * The stopwatch can be started and stopped several times sequencially and
		 * then will list all measured times unless the Stopwatch has been reset
		 * with <code>Debug.timerReset()</code>.
		 * 
		 * @example
		 * <p><pre>
		 * Debug.timerStart("Loop Execution Time");
		 * for (var i:int = 0; i < 100000; i++) {
		 *     ...
		 * }
		 * Debug.timerStop();
		 * Debug.timerToString();</pre>
		 * <p>Output:<p>
		 * \u002a\u002a\u002a\u002a\u002a\u002a\u002a\u002a\u002a\u002a\u002a\u002a\u002a\u002a\u002a [STOPWATCH] \u002a\u002a\u002a\u002a\u002a\u002a\u002a\u002a\u002a\u002a\u002a\u002a\u002a\u002a<br>
		 * \u002a Loop Execution Time<br>
		 * \u002a started [14435ms] stopped [17477ms] time [03042ms]<br>
		 * \u002a total runnning time: 3.042s<br>
		 * \u002a\u002a\u002a\u002a\u002a\u002a\u002a\u002a\u002a\u002a\u002a\u002a\u002a\u002a\u002a\u002a\u002a\u002a\u002a\u002a\u002a\u002a\u002a\u002a\u002a\u002a\u002a\u002a\u002a\u002a\u002a\u002a\u002a\u002a\u002a\u002a\u002a\u002a\u002a\u002a\u002a
		 * 
		 * @param title An optional title for the Stopwatch.
		 */
		public static function timerStart(title:String = ""):void
		{
			if (_isEnabled)
			{
				if (!_stopWatch) _stopWatch = new StopWatch();
				_stopWatch.start(title);
			}
		}
		
		
		/**
		 * Stops the Stopwatch after it has been started with <code>Debug.timerStart()</code>.
		 */
		public static function timerStop():void
		{
			if (_stopWatch) _stopWatch.stop();
		}
		
		
		/**
		 * Resets the Stopwatch.
		 */
		public static function timerReset():void
		{
			if (_stopWatch) _stopWatch.reset();
		}
		
		
		/**
		 * Outputs the measured Stopwatch time in milliseconds to Alcon's Trace Logger.
		 */
		public static function timerInMilliSeconds():void
		{
			if (_stopWatch) Debug.trace(_stopWatch.timeInMilliSeconds + "ms");
		}
		
		
		/**
		 * Outputs the measured Stopwatch time in seconds to Alcon's Trace Logger.
		 */
		public static function timerInSeconds():void
		{
			if (_stopWatch) Debug.trace(_stopWatch.timeInSeconds + "s");
		}
		
		
		/**
		 * Sends the measured Stopwatch time to the Trace Logger. This method
		 * automatically formats the values to seconds and milliseconds.
		 */
		public static function timerToString():void
		{
			if (_stopWatch) Debug.trace(_stopWatch.toString());
		}
		
		
		/**
		 * Stops the Stopwatch and immediately sends the measured time to the
		 * Trace Logger in the same format like <code>Debug.timerToString()</code>.
		 * 
		 * @param reset If true resets the Timer after the result has been output.
		 */
		public static function timerStopToString(reset:Boolean = false):void
		{
			if (_stopWatch)
			{
				_stopWatch.stop();
				Debug.trace(_stopWatch.toString());
				if (reset) _stopWatch.reset();
			}
		}
		
		
		////////////////////////////////////////////////////////////////////////////////////////
		// Getters & Setters                                                                  //
		////////////////////////////////////////////////////////////////////////////////////////
		
		/**
		 * The currently used Debugging Filter Level. By default this is 0 so that all
		 * levels of information are sent to the output. This property can be used to
		 * control the filter level from within the Debug class. For example setting
		 * this property to <code>Debug.LEVEL_ERROR</code> will only let information
		 * of Error and Fatal level pass.
		 */
		public static function get filterLevel():int
		{
			return _filterLevel;
		}
		public static function set filterLevel(v:int):void
		{
			if (v >= 0 && v < 5) _filterLevel = v;
		}
		
		
		/**
		 * Determines whether the Debug class is currently enabled or disabled.
		 * If this property is set to false the Debug class will not send out
		 * any information to Alcon. This can be used to disable the Debug API
		 * completely without the need to remove any debugging code.
		 */
		public static function get enabled():Boolean
		{
			return _isEnabled;
		}
		public static function set enabled(v:Boolean):void
		{
			_isEnabled = v;
		}
		
		
		////////////////////////////////////////////////////////////////////////////////////////
		// Event Handlers                                                                     //
		////////////////////////////////////////////////////////////////////////////////////////
		
		/**
		 * Called on every fpsUpdate event.
		 * 
		 * @private
		 */
		private static function onFPSUpdate(e:Event):void
		{
			send("onFPS", (_fpsMeter.fps + ","
				+ _stage.frameRate + ","
				+ _fpsMeter.frt + ","
				+ System.totalMemory));
		}
		
		
		/**
		 * onStatus method
		 * 
		 * @private
		 */
		private static function onStatus(e:StatusEvent):void
		{
		}
		
		
		////////////////////////////////////////////////////////////////////////////////////////
		// Private Methods                                                                    //
		////////////////////////////////////////////////////////////////////////////////////////
		
		/**
		 * Sends the specified data to Alcon.
		 * 
		 * @param m Method
		 * @param d Data
		 * @param l Level
		 * @param r Recursion Depth
		 * 
		 * @private
		 */
		private static function send(m:String, d:*, l:int = 1, r:int = 0):void
		{
			/* Only send if Debug is not disabled */
			if (_isEnabled)
			{
				/* Establish connection if not already done */
				if (!_isConnected)
				{
					_isConnected = true;
					_connection = new LocalConnection();
					_connection.addEventListener(StatusEvent.STATUS, onStatus);
				}
				
				/* Get the size of the data */
				var s:Number = 0;
				if (typeof(d) == "string")
				{
					s = String(d).length;
				}
				else if (typeof(d) == "object")
				{
					var byteArray:ByteArray = new ByteArray();
					byteArray.writeObject(d);
					s = byteArray.length;
					byteArray = null;
				}
				
				/* If the data size exceeds 39Kb, use a LSO instead */
				if (s > 39000)
				{
					storeDataLSO(m, d);
					m = "onLargeData";
					d = null;
				}
				
				_connection.send("_alcon_lc", m, d, l, r, "");
			}
		}
		
		
		/**
		 * sendCapabilities
		 * 
		 * @private
		 */
		private static function sendCapabilities():void
		{
			var xml:XML = describeType(Capabilities);
			var a:Array = [];
			
			for each (var node:XML in xml.*)
			{
				var n:String = node.@name.toString();
				if (n.length > 0 && n != "_internal" && n != "prototype")
				{
					var nn:String = Capabilities[n];
					a.push({p: n, v: nn});
				}
			}
			
			a.sortOn (["p"], Array.CASEINSENSITIVE);
			send("onCap", a);
		}
		
		
		/**
		 * Stores data larger than 40Kb to a Local Shared Object.
		 * 
		 * @private
		 */
		private static function storeDataLSO(m:String, d:*):void
		{
			var sharedObject:SharedObject = SharedObject.getLocal("alcon", "/");
			sharedObject.data["alconMethod"] = m;
			sharedObject.data["alconData"] = d;
			try
			{
				var flushResult:String = sharedObject.flush();
				if (flushResult == SharedObjectFlushStatus.FLUSHED)
				{
					return;
				}
			}
			catch (e:Error)
			{
				Security.showSettings(SecurityPanel.LOCAL_STORAGE);
			}
		}
	}
}
