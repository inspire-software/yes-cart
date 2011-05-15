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
	import flash.utils.getTimer;

	
	/**
	 * Stopwatch can be used to stop the time.
	 * 
	 * Instantiate this class as follows:
	 * <p><pre>
	 *     import com.hexagonstar.util.StopWatch;
	 *     var stopWatch:StopWatch = new StopWatch();
	 * </pre>
	 * 
	 * This will create a still standing stopwatch. You can start and stop the
	 * stopwatch to record time as you please:
	 * <p><pre>
	 *     stopWatch.start();
	 *     // Do something
	 *     stopWatch.stop();
	 * </pre>
	 * 
	 * The recored time is available in milliseconds and seconds.
	 * <p><pre>
	 *     Debug.trace(stopWatch.getTimeInMilliSeconds() + " ms");
	 *     Debug.trace(stopWatch.getTimeInSeconds() + " s");
	 * </pre>
	 */
	public class StopWatch
	{
		////////////////////////////////////////////////////////////////////////////////////////
		// Variables                                                                          //
		////////////////////////////////////////////////////////////////////////////////////////
		
		private var _started:Boolean = false;
		private var _startTimeKeys:Array;
		private var _stopTimeKeys:Array;
		private var _title:String;
		
		
		////////////////////////////////////////////////////////////////////////////////////////
		// Public Methods                                                                     //
		////////////////////////////////////////////////////////////////////////////////////////
		
		/** 
		 * Constructs a new StopWatch instance.
		 */
		public function StopWatch()
		{
			reset();
		}
		
		
		/**
		 * Starts the time recording process.
		 * 
		 * @param title An optional title for the Stopwatch.
		 */
		public function start(title:String = ""):void
		{
			if (!_started)
			{
				_title = title;
				_started = true;
				_startTimeKeys.push(getTimer());
			}
		}
		
		
		/**
		 * Stops the time recording process if the process has been started before.
		 */
		public function stop():void
		{
			if (_started)
			{
				var stopTime:int = getTimer();
				_stopTimeKeys[_startTimeKeys.length - 1] = stopTime;
				_started = false;
			}
		}
		
		
		/**
		 * Resets the Stopwatch total running time.
		 */
		public function reset():void
		{
			_startTimeKeys = [];
			_stopTimeKeys = [];
			_started = false;
		}
		
		
		/**
		 * Generates a string representation of the Stopwatch that includes
		 * all start and stop times in milliseconds.
		 * 
		 * @return the string representation of the Stopwatch.
		 */
		public function toString():String
		{
			var s:String = "\n ********************* [STOPWATCH] *********************";
			if (_title != "") s += "\n * " + _title;
			
			for (var i:int = 0; i < _startTimeKeys.length; i++)
			{
				var s1:int = _startTimeKeys[i];
				var s2:int = _stopTimeKeys[i];
				s += "\n * started ["
					+ format(s1) + "ms] stopped ["
					+ format(s2) + "ms] time ["
					+ format(s2 - s1) + "ms]";
			}
			
			if (i == 0) s += "\n * never started.";
			else s += "\n * total runnning time: " + timeInSeconds + "s";
			
			s += "\n *******************************************************";
			return s;
		}
		
		
		////////////////////////////////////////////////////////////////////////////////////////
		// Getters & Setters                                                                  //
		////////////////////////////////////////////////////////////////////////////////////////
		
		/**
		 * Returns whether the Stopwatch has been started or not.
		 */
		public function get started():Boolean
		{
			return _started;
		}
		
		
		/**
		 * Calculates and returns the elapsed time in milliseconds. The Stopwatch
		 * will not be stopped by calling this method. If the Stopwatch
		 * is still running it takes the current time as stoptime for the result.
		 */
		public function get timeInMilliSeconds():int
		{
			if (_started)
			{
				_stopTimeKeys[_startTimeKeys.length - 1] = getTimer();
			}
			var r:int = 0;
			for (var i:int = 0; i < _startTimeKeys.length; i++)
			{
				r += (_stopTimeKeys[i] - _startTimeKeys[i]);
			}
			return r;		
		}
		
		
		/**
		 * Calculates and returns the elapsed time in seconds. The Stopwatch
		 * will not be stopped by calling this method. If the Stopwatch is still
		 * running it takes the current time as stoptime for the result.
		 */
		public function get timeInSeconds():Number
		{
			return timeInMilliSeconds / 1000;
		}
		
		
		////////////////////////////////////////////////////////////////////////////////////////
		// Private Methods                                                                    //
		////////////////////////////////////////////////////////////////////////////////////////
		
		/**
		 * Formats a value for toString Output.
		 * 
		 * @private
		 */
		private function format(v:int):String
		{
			var s:String = "";
			var l:int = v.toString().length;
			for (var i:int = 0; i < (5 - l); i++)
			{
				s += "0";
			}
			return s + v;
		}
	}
}
