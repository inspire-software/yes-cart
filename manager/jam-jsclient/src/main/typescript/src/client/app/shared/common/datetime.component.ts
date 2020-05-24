/*
 * Copyright 2009 Inspire-Software.com
 *
 *    Licensed under the Apache License, Version 2.0 (the 'License');
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an 'AS IS' BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
import { Component, Input, Output, EventEmitter } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { YcValidators } from '../validation/validators';
import { FormValidationEvent, Futures, Future } from '../event/index';
import { UiUtil } from './../ui/index';
import { LogUtil } from './../log/index';

@Component({
  selector: 'yc-datetime',
  moduleId: module.id,
  templateUrl: 'datetime.component.html',
})

export class DateTimeComponent {

  private static TZ:string = '?';

  @Input()  title:string = null;

  @Output() dataChanged: EventEmitter<FormValidationEvent<any>> = new EventEmitter<FormValidationEvent<any>>();

  private _value : any = null;
  private _valueOrigin : string = null;
  private _valueTime : string = null;
  private _valueInternal : string = null;
  private _valueLast : Date = null;
  private _defaultRequired : boolean = false;

  private dateTimeForm:any;

  private delayedChange:Future;

  constructor(fb: FormBuilder) {
    LogUtil.debug('DateTimeComponent constructed', this.title, this.value);
    this.dateTimeForm = fb.group({
       'dateTime':  ['', YcValidators.validDate ],
    });

    let that = this;

    this.delayedChange = Futures.perpetual(function() {
      that.formChange();
    }, 200);

    if (DateTimeComponent.TZ === '?') {
      try {
        DateTimeComponent.TZ = new Date().toLocaleDateString(undefined, { timeZoneName: 'short' }).split(' ').pop();
      } catch(e) {
        LogUtil.error('Unable to parse date', e);
      }
    }

  }

  @Input()
  set defaultRequired(required:string) {
    this._defaultRequired = required === 'true';
    if (this._defaultRequired) {
      LogUtil.debug('DateTimeComponent resetting validator to required non-blank', this._value);
      this.dateTimeForm.controls['dateTime'].validator = YcValidators.requiredValidDate;
    } else {
      LogUtil.debug('DateTimeComponent resetting validator to non-blank', this._value);
      this.dateTimeForm.controls['dateTime'].validator = YcValidators.validDate;
    }
  }

  @Input()
  set value(value: any) {

    this._value = value;
    this._valueInternal = UiUtil.toDateString(value, true);
    this._valueOrigin = this._valueInternal;
    // keep original time
    this._valueTime = this._valueOrigin != null ? this._valueOrigin.split(' ')[1] : null;
    // remember last valid date (to pass to bsDatepicker)
    if (value != null) {
      this._valueLast = value instanceof Date ? value : new Date(value);
    } else {
      this._valueLast = null;
    }
    LogUtil.debug('DateTimeComponent value', this._value, this._valueInternal);

  }

  get value(): any {
    return this._value;
  }

  set valueInternal(value: string) {
    this._valueInternal = value;
    // ensure we update time after change
    if (this._valueInternal != null && this._valueInternal.length == 19) {
      this._valueTime = this._valueInternal != null ? this._valueInternal.split(' ')[1] : null;
    } else if (this._valueInternal == null) {
      this._valueTime = null;
    }
  }

  get valueInternal(): string {
    return this._valueInternal;
  }

  get timeZoneAbbr():string {
    return DateTimeComponent.TZ;
  }

  onValueChange(event:any):void {
    LogUtil.debug('DateTimeComponent onValueChange', event);
    this.delayedChange.delay();
  }

  onDatepickerClick(dp:any):void {
    LogUtil.debug('DateTimeComponent onDatepickerClick bsValue/last', dp.bsValue, this._valueLast);
    if (this._valueLast) {
      dp.bsValue = this._valueLast;
    } // else do nothing, because if we set it to new Date() it will trigger an update event, best to leave it
    dp.show();
  }

  onBsValueChange(event:any):void {
    LogUtil.debug('DateTimeComponent onBsValueChange', event);
    let date = UiUtil.toDateString(event, false);
    if (date) {
      // Reuse manually entered time, or set to noon as current calendar does not support time
      date += ' ' + (this._valueTime != null ? this._valueTime : '00:00:00');
      if (date != this._valueInternal) {
        this._valueInternal = date;
        this.delayedChange.delay();
      }
    }
  }

  onClearDateTime():void {
    if (null != this.valueInternal) {
      this.valueInternal = null;
      this.delayedChange.delay();
    }
  }

  formChange():void {
    if (this._valueInternal != this._valueOrigin) {
      let date = UiUtil.toDate(this._valueInternal);
      if (this.dateTimeForm.valid) {
        this._valueLast = date;
      }
      LogUtil.debug('DateTimeComponent formChange valid/origin/internal/parsed/last', this.dateTimeForm.valid, this._value, this._valueInternal, date, this._valueLast);
      this.dataChanged.emit({source: date != null ? date.getTime() : null, valid: this.dateTimeForm.valid});
    }
  }

}
