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
import { Component, OnInit, OnDestroy, Input, Output, EventEmitter } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { CustomValidators } from './../../shared/validation/validators';
import { CountryVO, StateVO, Pair } from './../../shared/model/index';
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

  @Output() stateSelected: EventEmitter<StateVO> = new EventEmitter<StateVO>();

  @Output() stateAddClick: EventEmitter<StateVO> = new EventEmitter<StateVO>();

  @Output() stateEditClick: EventEmitter<StateVO> = new EventEmitter<StateVO>();

  @Output() stateDeleteClick: EventEmitter<StateVO> = new EventEmitter<StateVO>();

  private _country:CountryVO;

  private delayedChange:Future;

  private countryForm:any;

  private stateFilter:string;
  private stateSort:Pair<string, boolean> = { first: 'stateCode', second: false };

  private selectedState:StateVO = null;

  constructor(fb: FormBuilder) {
    LogUtil.debug('CountryComponent constructed');

    this.countryForm = fb.group({
      'countryCode': ['', CustomValidators.requiredValidCountryCode],
      'isoCode': ['', CustomValidators.requiredValidCountryIsoCode],
      'name': ['']
    });

    let that = this;
    this.delayedChange = Futures.perpetual(function() {
      that.formChange();
    }, 200);
  }

  formBind():void {
    UiUtil.formBind(this, 'countryForm', 'delayedChange');
  }


  formUnbind():void {
    UiUtil.formUnbind(this, 'countryForm');
  }

  formChange():void {
    LogUtil.debug('CountryComponent formChange', this.countryForm.valid, this._country);
    this.dataChanged.emit({ source: this._country, valid: this.countryForm.valid });
  }

  onNameDataChange(event:FormValidationEvent<any>) {
    UiUtil.formI18nDataChange(this, 'countryForm', 'name', event);
  }

  @Input()
  set country(country:CountryVO) {

    UiUtil.formInitialise(this, 'countryForm', '_country', country);

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

  tabSelected(tab:any) {
    LogUtil.debug('CountryComponent tabSelected', tab);
  }


  protected onClearStateFilter() {

    this.stateFilter = '';

  }

  protected onStateSelected(data:StateVO) {
    LogUtil.debug('CountryComponent onStateSelected', data);
    this.selectedState = data;
    this.stateSelected.emit(data);
  }

  protected onRowAddState() {
    LogUtil.debug('CountryComponent onRowAddState', this.selectedState);
    this.stateAddClick.emit(this.selectedState);
  }

  protected onPageSelectedState(page:number) {
    LogUtil.debug('CountryComponent onPageSelectedState', page);
  }

  protected onSortSelectedState(sort:Pair<string, boolean>) {
    LogUtil.debug('CountryComponent onSortSelectedState', sort);
    if (sort == null) {
      this.stateSort = { first: 'stateCode', second: false };
    } else {
      this.stateSort = sort;
    }
  }

  protected onRowEditSelectedState() {
    LogUtil.debug('CountryComponent onRowEditSelectedState', this.selectedState);
    if (this.selectedState != null) {
      this.stateEditClick.emit(this.selectedState);
    }
  }

  protected onRowDeleteSelectedState() {
    LogUtil.debug('CountryComponent onRowDeleteSelectedState', this.selectedState);
    if (this.selectedState != null) {
      this.stateDeleteClick.emit(this.selectedState);
    }
  }

}
