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
	import flash.events.EventDispatcher;
	import flash.events.TimerEvent;
	import flash.utils.Timer;
	import flash.utils.getTimer;

	
	/**
	 * FPSMeter can be used to measure the application's framerate and
	 * frame render time. This class can be used on it's own to fetch
	 * fps/frt information or it is used by the Debug class when calling
	 * Debug.monitor().
	 */
	public class FPSMeter extends EventDispatcher
	{
		////////////////////////////////////////////////////////////////////////////////////////
		// Variables                                                                          //
		////////////////////////////////////////////////////////////////////////////////////////
		
		/**
		 * The FPSMeter.FPS_UPDATE constant defines the value of the type property
		 * of an fpsUpdate event object.
		 */
		public static const FPS_UPDATE:String = "fpsUpdate";
		
		private var _stage:Stage;
		private var _timer:Timer;
		private var _pollInterval:int;
		private var _fps:int;
		private var _frt:int;
		private var _ms:int;
		private var _isRunning:Boolean;
		
		private var _delay:int;
		private var _delayMax:int = 10;
		private var _prev:int;
		
		
		////////////////////////////////////////////////////////////////////////////////////////
		// Public Methods                                                                     //
		////////////////////////////////////////////////////////////////////////////////////////
		
		/**
		 * Constructs a new FPSMeter instance.
		 * 
		 * @param stage The Stage object for that the FPS is being measured.
		 * @param pollInterval Interval in milliseconds with that the FPS rate is polled.
		 */
		public function FPSMeter(stage:Stage, pollInterval:int = 500)
		{
			_stage = stage;
			_pollInterval = pollInterval;
			reset();
		}
		
		
		/**
		 * Starts FPS/FRT polling.
		 */
		public function start():void
		{
			if (!_isRunning)
			{
				_isRunning = true;
				_timer = new Timer(_pollInterval, 0);
				_timer.addEventListener(TimerEvent.TIMER, onTimer);
				_stage.addEventListener(Event.ENTER_FRAME, onEnterFrame);
				_timer.start();
			}
		}
		
		
		/**
		 * Stops FPS/FRT polling.
		 */
		public function stop():void
		{
			if (_isRunning)
			{
				_timer.stop();
				_timer.removeEventListener(TimerEvent.TIMER, onTimer);
				_stage.removeEventListener(Event.ENTER_FRAME, onEnterFrame);
				_timer = null;
				reset();
			}
		}
		
		
		/**
		 * Resets the FPSMeter to it's default state.
		 */
		public function reset():void
		{
			_fps = 0;
			_frt = 0;
			_ms = 0;
			_delay = 0;
			_prev = 0;
			_isRunning = false;
		}
		
		
		////////////////////////////////////////////////////////////////////////////////////////
		// Getters & Setters                                                                  //
		////////////////////////////////////////////////////////////////////////////////////////
		
		/**
		 * Returns the current FPS.
		 * 
		 * @return The currently polled frames per second.
		 */
		public function get fps():int
		{
			return _fps;
		}
		
		
		/**
		 * Returns the time that the current frame needed to render.
		 * 
		 * @return The time in milliseconds that the current frame needed to render.
		 */
		public function get frt():int
		{
			return _frt;
		}
		
		
		////////////////////////////////////////////////////////////////////////////////////////
		// Event Handlers                                                                     //
		////////////////////////////////////////////////////////////////////////////////////////
		
		/**
		 * Called on every Timer event.
		 * @private
		 */
		private function onTimer(event:TimerEvent):void
		{
			dispatchEvent(new Event(FPSMeter.FPS_UPDATE));
		}
		
		
		/**
		 * Called on every EnterFrame event.
		 * @private
		 */
		private function onEnterFrame(event:Event):void
		{
			var t:Number = getTimer();
			_delay++;
			
			if (_delay >= _delayMax)
			{
				_delay = 0;
				_fps = int((1000 * _delayMax) / (t - _prev));
				_prev = t;
			}
			
			_frt = t - _ms;
			_ms = t;
		}
	}
}
