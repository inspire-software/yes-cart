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
import { Component, OnInit, OnDestroy, Input, Output, EventEmitter } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { YcValidators } from './../../shared/validation/validators';
import { CountryVO } from './../../shared/model/index';
import { FormValidationEvent, Futures, Future } from './../../shared/event/index';
import { UiUtil } from './../../shared/ui/index';
import { LogUtil } from './../../shared/log/index';

@Component({
  selector: 'yc-country',
  moduleId: module.id,
  templateUrl: 'country.component.html',
})

export class CountryComponent implements OnInit, OnDestroy {

  @Output() dataChanged: EventEmitter<FormValidationEvent<CountryVO>> = new EventEmitter<FormValidationEvent<CountryVO>>();

  private _country:CountryVO;

  private initialising:boolean = false; // tslint:disable-line:no-unused-variable
  private delayedChange:Future;

  private countryForm:any;
  private countryFormSub:any; // tslint:disable-line:no-unused-variable

  constructor(fb: FormBuilder) {
    LogUtil.debug('CountryComponent constructed');

    this.countryForm = fb.group({
      'countryCode': ['', YcValidators.requiredValidCountryCode],
      'isoCode': ['', YcValidators.requiredValidCountryIsoCode],
      'name': ['', YcValidators.requiredNonBlankTrimmed],
      'displayName': ['', YcValidators.nonBlankTrimmed],
    });

    let that = this;
    this.delayedChange = Futures.perpetual(function() {
      that.formChange();
    }, 200);
  }

  formBind():void {
    UiUtil.formBind(this, 'countryForm', 'countryFormSub', 'delayedChange', 'initialising');
  }


  formUnbind():void {
    UiUtil.formUnbind(this, 'countryFormSub');
  }

  formChange():void {
    LogUtil.debug('CountryComponent formChange', this.countryForm.valid, this._country);
    this.dataChanged.emit({ source: this._country, valid: this.countryForm.valid });
  }

  @Input()
  set country(country:CountryVO) {

    UiUtil.formInitialise(this, 'initialising', 'countryForm', '_country', country);

  }

  get country():CountryVO {
    return this._country;
  }

  ngOnInit() {
    LogUtil.debug('CountryComponent ngOnInit');
    this.formBind();
  }

  ngOnDestroy() {
    LogUtil.debug('CountryComponent ngOnDestroy');
    this.formUnbind();
  }

}
