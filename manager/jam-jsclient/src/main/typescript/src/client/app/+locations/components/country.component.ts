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
import {FormBuilder, REACTIVE_FORM_DIRECTIVES} from '@angular/forms';
import {YcValidators} from './../../shared/validation/validators';
import {CountryVO} from './../../shared/model/index';
import {FormValidationEvent, Futures, Future} from './../../shared/event/index';


@Component({
  selector: 'yc-country',
  moduleId: module.id,
  templateUrl: 'country.component.html',
  directives: [NgIf, REACTIVE_FORM_DIRECTIVES],
})

export class CountryComponent implements OnInit, OnDestroy {

  _country:CountryVO;

  @Output() dataChanged: EventEmitter<FormValidationEvent<CountryVO>> = new EventEmitter<FormValidationEvent<CountryVO>>();

  changed:boolean = false;
  validForSave:boolean = false;
  delayedChange:Future;

  countryForm:any;
  countryFormSub:any;

  constructor(fb: FormBuilder) {
    console.debug('CountryComponent constructed');

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

  formReset():void {
    // Hack to reset NG2 forms see https://github.com/angular/angular/issues/4933
    for(let key in this.countryForm.controls) {
      this.countryForm.controls[key]['_pristine'] = true;
      this.countryForm.controls[key]['_touched'] = false;
    }
  }

  formBind():void {
    this.countryFormSub = this.countryForm.statusChanges.subscribe((data:any) => {
      this.validForSave = this.countryForm.valid;
      if (this.changed) {
        this.delayedChange.delay();
      }
    });
  }

  formUnbind():void {
    if (this.countryFormSub) {
      console.debug('CountryComponent unbining form');
      this.countryFormSub.unsubscribe();
    }
  }

  formChange():void {
    console.debug('CountryComponent validating formGroup is valid: ' + this.validForSave, this._country);
    this.dataChanged.emit({ source: this._country, valid: this.validForSave });
  }

  @Input()
  set country(country:CountryVO) {
    this._country = country;
    this.changed = false;
    this.formReset();
  }

  get country():CountryVO {
    return this._country;
  }

  onDataChange(event:any) {
    console.debug('CountryComponent onDataChange', event);
    this.changed = true;
  }

  ngOnInit() {
    console.debug('CountryComponent ngOnInit');
    this.formBind();
  }

  ngOnDestroy() {
    console.debug('CountryComponent ngOnDestroy');
    this.formUnbind();
  }

}
