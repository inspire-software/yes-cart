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
import {CountryVO} from './../../shared/model/index';
import {Router, ActivatedRoute} from '@angular/router';


@Component({
  selector: 'yc-country',
  moduleId: module.id,
  templateUrl: 'country.component.html',
  directives: [NgIf, REACTIVE_FORM_DIRECTIVES],
})

export class CountryComponent implements OnInit, OnDestroy {

  _country:CountryVO;

  @Output() dataChanged: EventEmitter<any> = new EventEmitter<any>();

  validForSave:boolean = false;

  countryForm:any;

  constructor(fb: FormBuilder) {
    console.debug('CountryComponent constructed');

    this.countryForm = fb.group({
      'countryCode': ['', Validators.compose([Validators.required, Validators.pattern('[A-Z]{2}')])],
      'isoCode': ['', Validators.compose([Validators.required, Validators.pattern('[0-9]{3}')])],
      'name': ['', Validators.compose([Validators.required, Validators.pattern('\\S+.*\\S+')])],
      'displayName': ['', Validators.pattern('\\S+.*\\S+')],
    });

  }

  formReset():void {
    // Hack to reset NG2 forms see https://github.com/angular/angular/issues/4933
    for(let key in this.countryForm.controls) {
      this.countryForm.controls[key]['_pristine'] = true;
      this.countryForm.controls[key]['_touched'] = false;
    }
  }

  @Input()
  set country(country:CountryVO) {
    this._country = country;
    this.formReset();
  }

  get country():CountryVO {
    return this._country;
  }

  onDataChange(event:any) {
    var _sub:any = this.countryForm.valueChanges.subscribe((data:any) => {
      this.validForSave = this.countryForm.valid;
      console.debug('CountryComponent form changed  and ' + (this.validForSave ? 'is valid' : 'is NOT valid'), data);
      _sub.unsubscribe();
      this.dataChanged.emit({ country: this._country, valid: this.validForSave });
    });
    console.debug('CountryComponent data changed and ' + (this.validForSave ? 'is valid' : 'is NOT valid'), event);
  }

  ngOnInit() {
    console.debug('CountryComponent ngOnInit');
  }

  ngOnDestroy() {
    console.debug('CountryComponent ngOnDestroy');
  }

}
