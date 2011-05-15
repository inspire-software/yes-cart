package com.georg
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
	
	import com.georg.datetime.DateTimeChooser;
	
	import flash.events.Event;
	import flash.events.FocusEvent;
	
	import mx.controls.DateField;
	import mx.controls.dataGridClasses.DataGridListData;
	import mx.controls.listClasses.BaseListData;
	import mx.controls.listClasses.ListData;
	import mx.core.ClassFactory;
	import mx.core.IFactory;
	import mx.events.CalendarLayoutChangeEvent;
	import mx.events.DropdownEvent;
	import mx.events.FlexEvent;
	import mx.logging.Log;
	import mx.managers.PopUpManager;
	import mx.managers.PopUpManagerChildList;
	import mx.utils.ObjectUtil;

	public class DateTimeField extends DateField
	{
		// using custom data to avoid beeing overwritten
		protected var _cData:Object;
		protected var _cListData:BaseListData;
		protected var _cSelectedDate:Date = null;
		
		// copied vars
		protected var updateDateFiller:Boolean;
		protected var selectedDateSet:Boolean;
		protected var selectedDateChanged:Boolean = false;
		protected var show:Boolean = false;		
		
		public function DateTimeField()
		{
			super();
			
			this.labelFunction = labelDateTime;
			this.addEventListener(DropdownEvent.OPEN, handleDropdown);
			this.addEventListener(DropdownEvent.CLOSE, handleDropdown);
		}
		
		// -------------------------------
		// own functions
		// -------------------------------
		
		protected function handleDropdown(e:DropdownEvent):void
		{
			if (e.type == DropdownEvent.CLOSE)
			{
				// save new date without beeing cropped
				if (ObjectUtil.dateCompare(selectedDate, dropdown.selectedDate) != 0)
				{
					selectedDate = dropdown.selectedDate;
					updateData();
				}
				
				show = false;
			}
			else
				show = true;
		}
		
		protected function updateData():void
		{
			// if new date selected, save it to the data-binding variable
			if (data && selectedDate)
			{
				if (listData && listData is DataGridListData)
					data[DataGridListData(listData).dataField] = selectedDate;
				else if (listData is ListData && ListData(listData).labelField in value)
					data[ListData(listData).labelField] = selectedDate;
				else if (value is String)
					data = selectedDate.toString();
				else
					data = value as Date;
			}
		}

		// -------------------------------
		// change Label
		// -------------------------------
		protected function numberToTime(value:Number):String
		{
			return (value >= 10) ? String(value) : ("0" + value);
		}
		
		protected function labelDateTime(value:Date):String
		{
			if (value)	return DateField.dateToString(value, formatString) + " " + numberToTime(value.hours) + ":" + numberToTime(value.minutes);
			else		return "";
		}
		
		// -------------------------------
		// overwritten data functions
		// -------------------------------
		
		override public function get data():Object
		{
			return _cData;
		}

		override public function set data(value:Object):void
		{
			var newDate:Date;
			
			_cData = value;
			
			if (_cListData && _cListData is DataGridListData)
				newDate = _cData[DataGridListData(_cListData).dataField];
			else if (_cListData is ListData && ListData(_cListData).labelField in _cData)
				newDate = _cData[ListData(_cListData).labelField];
			else if (_cData is String)
				newDate = new Date(Date.parse(data as String));
			else
				newDate = _cData as Date;
			
			if (!selectedDateSet)
			{
				selectedDate = newDate;
				selectedDateSet = false;
			}
			
			dispatchEvent(new FlexEvent(FlexEvent.DATA_CHANGE));
		}
		
		override public function get listData():BaseListData
		{
			return _cListData;
		}
		

		override public function set listData(value:BaseListData):void
		{
			_cListData = value;
		}
		
		//----------------------------------
		//  selectedDate
		//----------------------------------
		
		[Bindable("change")]
		[Bindable("valueCommit")]
		[Bindable("selectedDateChanged")]
		[Inspectable(category="General")]

		override public function get selectedDate():Date
		{
			return _cSelectedDate;
		}
		
		/**
		 *  @private
		 */
		override public function set selectedDate(value:Date):void
		{
			if (ObjectUtil.dateCompare(_cSelectedDate, value) == 0) 
				return;
			
			selectedDateSet = true;
			_cSelectedDate = value;
			updateDateFiller = true;
			selectedDateChanged = true;
			
			invalidateProperties();
			
			// Trigger bindings to 'selectedData'.
			dispatchEvent(new Event("selectedDateChanged"));
			
			dispatchEvent(new FlexEvent(FlexEvent.VALUE_COMMIT));
		}
		
		
		//--------------------------------------------------------------------------
		//
		//  Overridden methods: UIComponent
		//
		//--------------------------------------------------------------------------
		override protected function commitProperties():void
		{
			super.commitProperties();
			
			if (updateDateFiller)
			{
				updateDateFiller = false;
				dateFiller(_cSelectedDate);
			}

			if (selectedDateChanged)
			{
				selectedDateChanged = false;
				dropdown.selectedDate = _cSelectedDate;
			}
		}
		
		//--------------------------------------------------------------------------
		//
		//  Methods
		//
		//--------------------------------------------------------------------------
		private function dateFiller(value:Date):void
		{
			if (labelFunction != null)
				textInput.text = labelFunction(value);
			else
				textInput.text = dateToString(value, formatString);
		}
		
		//--------------------------------------------------------------------------
		//
		//  Event handlers
		//
		//--------------------------------------------------------------------------
		override protected function downArrowButton_buttonDownHandler(event:FlexEvent):void
		{
			if (show) 	close();
			else 		open();
			
			dateFiller(_cSelectedDate);
		}
	
		//----------------------------------
		//  dropdownFactory // Overwrite, to include the own DateTimeChooser class
		//----------------------------------
		private var _dropdownFactory:IFactory = new ClassFactory(DateTimeChooser);
		
		[Bindable("dropdownFactoryChanged")]
		override public function get dropdownFactory():IFactory
		{
			return _dropdownFactory;
		}
		
		override public function set dropdownFactory(value:IFactory):void
		{
			_dropdownFactory = value;
			dispatchEvent(new Event("dropdownFactoryChanged"));
		}
		
		// -------------------------------
		// Remove focus handler
		// -------------------------------
		override protected function focusOutHandler(event:FocusEvent):void
		{
			// disable "focus out"
		}
		
	}
}