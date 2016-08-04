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
import {Component, OnInit, OnDestroy, Input, Output, EventEmitter} from '@angular/core';
import {NgIf} from '@angular/common';
import {FormBuilder, Validators, REACTIVE_FORM_DIRECTIVES} from '@angular/forms';
import {CarrierLocaleVO} from './../../shared/model/index';
import {FormValidationEvent} from './../../shared/event/index';
import {I18nComponent} from './../../shared/i18n/index';


@Component({
  selector: 'yc-carrier',
  moduleId: module.id,
  templateUrl: 'carrier.component.html',
  directives: [NgIf, REACTIVE_FORM_DIRECTIVES, I18nComponent],
})

export class CarrierComponent implements OnInit, OnDestroy {

  _carrier:CarrierLocaleVO;

  @Output() dataChanged: EventEmitter<FormValidationEvent<CarrierLocaleVO>> = new EventEmitter<FormValidationEvent<CarrierLocaleVO>>();

  validForSave:boolean = false;

  constructor(fb: FormBuilder) {
    console.debug('CarrierComponent constructed');
  }


  @Input()
  set carrier(carrier:CarrierLocaleVO) {
    this._carrier = carrier;
  }

  get carrier():CarrierLocaleVO {
    return this._carrier;
  }

  onDataChanged(event:any) {
    this.validForSave = (this._carrier.name != null) && (/\S+.*\S+/.test(this._carrier.name));
    console.debug('CarrierComponent data changed and ' + (this.validForSave ? 'is valid' : 'is NOT valid'), this._carrier);
    this.dataChanged.emit({ source: this._carrier, valid: this.validForSave });
  }

  ngOnInit() {
    console.debug('CarrierComponent ngOnInit');
  }

  ngOnDestroy() {
    console.debug('CarrierComponent ngOnDestroy');
  }

}
