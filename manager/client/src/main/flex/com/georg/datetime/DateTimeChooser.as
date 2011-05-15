package com.georg.datetime
{
	/*
	The MIT License
	
	Copyright (c) 2010 Georg Graf
	
	Permission is hereby granted, free of charge, to any person obtaining a copy
	of this software and associated documentation files (the "Software"), to deal
	in the Software without restriction, including without limitation the rights
	to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
	copies of the Software, and to permit persons to whom the Software is
	furnished to do so, subject to the following conditions:
	
	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
	IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
	FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
	AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
	LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
	OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
	THE SOFTWARE.
	
	Most of this code is copied from the base class to modify small behaviours
	*/
	
	import flash.events.FocusEvent;
	import flash.events.KeyboardEvent;
	import flash.ui.Keyboard;
	
	import mx.controls.DateChooser;
	import mx.controls.Label;
	import mx.controls.TextInput;
	import mx.events.CalendarLayoutChangeEvent;
	
	public class DateTimeChooser extends DateChooser
	{
		private var textInput:TextInput;
		private var label:Label;
		
		[Bindable] private var startTime:String = "";
		private var _timeLabel:String = "";
		
		public function DateTimeChooser()
		{
			super();
			timeLabel = "Time";
		}
		
		override public function get selectedDate():Date
		{
			// add time
			var d:Date = (super.selectedDate) ? super.selectedDate : new Date();
			
			var times:Array = (textInput) ? textInput.text.split(":") : startTime.split(":");
			if (times.length > 1)
			{
				d.hours = times[0];
				d.minutes = times[1];
			}
			
			// update data
			super.selectedDate = d;

			return d;
		}
		
		override public function set selectedDate(value:Date):void
		{
			if (value)
			{
				startTime = to2Digits(value.hours) + ":" + to2Digits(value.minutes);
				if (textInput) textInput.text = startTime;
				
				super.selectedDate = value;
			}
		}
		
		[Bindable]
		public function get timeLabel():String { return _timeLabel; }
		public function set timeLabel(value:String):void { _timeLabel = value; }
		
		private function to2Digits(value:Number = NaN):String
		{
			if (isNaN(value))	return "00";
			else if (value < 10)return "0" + value;
			else				return "" + value;
		}
		
		private function checkTimeBox(e:KeyboardEvent):void
		{
			if (e.keyCode == Keyboard.ENTER) // bei ENTER schließen
			{
				dispatchEvent(new CalendarLayoutChangeEvent(CalendarLayoutChangeEvent.CHANGE, false, false, selectedDate));
			}
			else // ansonsten überprüfe ob der String stimmt
			{
				var values:Array = String(textInput.text).match(/[0-9]{2}/g);
				textInput.text = to2Digits(values[0]) + ":" + to2Digits(values[1]);
			}
		}
		private function timeFocusOut(e:FocusEvent):void
		{
			dispatchEvent(new CalendarLayoutChangeEvent(CalendarLayoutChangeEvent.CHANGE, false, false, selectedDate));
		}
		
		/**
		 *  @private
		 *  Erstellt noch den Time NumericStepper
		 */
		override protected function createChildren():void
		{
			super.createChildren();
			
			if (!textInput){
				textInput = new TextInput();
				textInput.text = startTime;
				textInput.maxChars = 5;
				textInput.addEventListener(KeyboardEvent.KEY_DOWN, checkTimeBox);
				textInput.owner = this;
				textInput.addEventListener(FocusEvent.FOCUS_OUT, timeFocusOut);
				addChild(textInput);
			}
			if (!label) {
				label = new Label();
				label.setStyle("textAlign", "right");
				label.text = timeLabel;
				addChild(label);
			}
		}
		
		/**
		 *  @private
		 */
		override protected function updateDisplayList(unscaledWidth:Number, unscaledHeight:Number):void
		{
			super.updateDisplayList(unscaledWidth, unscaledHeight);
			
			var borderThickness:Number = getStyle("borderThickness");
			var cornerRadius:Number = getStyle("cornerRadius");
			var borderColor:Number = getStyle("borderColor");
			
			var w:Number = unscaledWidth - borderThickness*2;
			var h:Number = unscaledHeight - borderThickness*2;
			
			textInput.setActualSize(48, 20);
			textInput.move(w - 50, h - 22);
			
			label.setActualSize(36, 20);
			label.move(textInput.x - 40, h - 21);
		}
	}
}