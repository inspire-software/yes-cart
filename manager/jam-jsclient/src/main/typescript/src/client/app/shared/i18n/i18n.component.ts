/*
 * Copyright 2009 - 2016 Denys Pavlov, Igor Azarnyi
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
import { LogUtil } from './../log/index';

import { Pair } from '../model/index';
import { Util } from '../services/index';


@Component({
  selector: 'yc-i18n-table',
  moduleId: module.id,
  templateUrl: 'i18n.component.html',
})

export class I18nComponent {

  @Input()  title : string;

  @Input()  i18n : string ;

  @Input()  value : string;

  @Output() dataChanged: EventEmitter<FormValidationEvent<any>> = new EventEmitter<FormValidationEvent<any>>();

  private   _source:any;

  private dataI18n:Array<Pair<string, string>> = [];
  private dataValue:string = '';

  private expandDefault:boolean = true;

  private selectedRow:Pair<string,string> = new Pair('','');

  private _defaultRequired : boolean = false;
  private i18nForm:any;

  private delayedChange:Future;

  constructor(fb: FormBuilder) {
    LogUtil.debug('I18nComponent constructed', this.title);
    this.i18nForm = fb.group({
       'addLang':  ['', YcValidators.validLanguageCode],
       'addVal':  ['', YcValidators.nonBlankTrimmed],
       'dataValue':  ['', YcValidators.nonBlankTrimmed],
       'dataValueXL':  ['', YcValidators.nonBlankTrimmed]
    });

    let that = this;

    this.delayedChange = Futures.perpetual(function() {
      that.formChange();
    }, 200);

  }

  @Input()
  set defaultRequired(required:string) {
    this._defaultRequired = required === 'true';
    if (this._defaultRequired) {
      LogUtil.debug('I18nComponent resetting validator to required non-blank', this.value);
      this.i18nForm.controls['dataValue'].validator = YcValidators.requiredNonBlankTrimmed;
      this.i18nForm.controls['dataValueXL'].validator = YcValidators.requiredNonBlankTrimmed;
    } else {
      LogUtil.debug('I18nComponent resetting validator to non-blank', this.value);
      this.i18nForm.controls['dataValue'].validator = YcValidators.nonBlankTrimmed;
      this.i18nForm.controls['dataValueXL'].validator = YcValidators.nonBlankTrimmed;
    }
  }

  @Input()
  set source(source:any) {
    this._source = source;
    this.reloadModel();
  }

  get source():any {
    return this._source;
  }

  reloadModel():void {
    LogUtil.debug('I18nComponent source', this.source, this.value, this.i18n);
    if (this.source[this.i18n] !== null) {
      this.dataI18n = this.source[this.i18n];
    } else {
      this.dataI18n = [];
      this.source[this.i18n] = this.dataI18n;
    }
    if (this.source[this.value] !== null) {
      this.dataValue = '' + this.source[this.value];
    } else {
      this.dataValue = '';
    }
    this.i18nForm.reset({
      dataValue: this.dataValue,
      dataValueXL: this.dataValue,
      addLang: null,
      addVal: null,
    });
  }

  onExpandDefault() {
    this.expandDefault = !this.expandDefault;
  }

  onRowSelect(selectedRow:Pair<string,string>) {
    LogUtil.debug('I18nComponent onRowSelect', selectedRow);
    this.selectedRow = Util.clone(selectedRow);
  }

  onRowDelete(selectedRow:Pair<string,string>) {
    LogUtil.debug('I18nComponent onRowDelete', selectedRow);
    for(var idx = 0 ; idx < this.dataI18n.length; idx++) {
      var pair = this.dataI18n[idx];
      if(pair.first === selectedRow.first ) {
        this.dataI18n.splice(idx,1);
        this.selectedRow = new Pair('','');
        this.formChange();
        LogUtil.debug('I18nComponent Removed ' + idx, this.i18n);
        return;
      }
    }
  }

  onRowAdd():void {
    if (this.isBlank(this.selectedRow.first)) {
      return;
    }
    if (this.isBlank(this.selectedRow.second)) {
      this.onRowDelete(this.selectedRow);
      return;
    }
    for(var idx = 0 ; idx < this.dataI18n.length; idx++) {
      var pair = this.dataI18n[idx];
      if(pair.first === this.selectedRow.first ) {
        pair.second = this.selectedRow.second;
        this.selectedRow = new Pair('','');
        this.formChange();
        LogUtil.debug('Replaced ' + idx, this.dataI18n);
        return;
      }
    }

    this.dataI18n.push(this.selectedRow);
    this.selectedRow = new Pair('','');
    this.formChange();
    LogUtil.debug('I18nComponent Added', this.dataI18n);
  }

  onDefaultValueChange():void {
    if (this.i18nForm.controls['dataValue'].dirty || this.i18nForm.controls['dataValueXL'].dirty
      || (this.isBlank(this.dataValue) && this._defaultRequired)) {
      if (!this.isBlank(this.dataValue)) {
        this.source[this.value] = this.dataValue;
      } else {
        this.source[this.value] = null;
      }
      LogUtil.debug('I18nComponent default value changed', this.dataValue, this.source);
      this.delayedChange.delay();
    }
  }

  formChange():void {
    LogUtil.debug('I18nComponent formChange', this.i18nForm.valid, this._source);
    this.dataChanged.emit({ source: this._source, valid: this.i18nForm.valid });
  }

  private isBlank(value:string):boolean {
    return value === null || value === '' || /^\s+$/.test(value);
  }

}
