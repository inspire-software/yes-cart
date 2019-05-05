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
import { FormBuilder, Validators } from '@angular/forms';
import { YcValidators } from './../../shared/validation/validators';
import { DataGroupVO } from './../../shared/model/index';
import { FormValidationEvent, Futures, Future } from './../../shared/event/index';
import { UiUtil } from './../../shared/ui/index';
import { LogUtil } from './../../shared/log/index';

@Component({
  selector: 'yc-datagroup',
  moduleId: module.id,
  templateUrl: 'datagroup.component.html',
})

export class DataGroupComponent implements OnInit, OnDestroy {

  @Output() dataChanged: EventEmitter<FormValidationEvent<DataGroupVO>> = new EventEmitter<FormValidationEvent<DataGroupVO>>();

  private _dataGroup:DataGroupVO;

  private initialising:boolean = false; // tslint:disable-line:no-unused-variable
  private delayedChange:Future;

  private dataGroupForm:any;
  private dataGroupFormSub:any; // tslint:disable-line:no-unused-variable

  constructor(fb: FormBuilder) {
    LogUtil.debug('DataGroupComponent constructed');

    let that = this;

    this.dataGroupForm = fb.group({
      'name': [''],
      'qualifier': ['', YcValidators.validCode36],
      'type': ['', Validators.required],
      'descriptors': ['', Validators.required],
    });

    this.delayedChange = Futures.perpetual(function() {
      that.formChange();
    }, 200);

  }

  formBind():void {
    UiUtil.formBind(this, 'dataGroupForm', 'dataGroupFormSub', 'delayedChange', 'initialising');
  }


  formUnbind():void {
    UiUtil.formUnbind(this, 'dataGroupFormSub');
  }

  formChange():void {
    LogUtil.debug('DataGroupComponent formChange', this.dataGroupForm.valid, this._dataGroup);
    this.dataChanged.emit({ source: this._dataGroup, valid: this.dataGroupForm.valid });
  }

  formMarkDirty(field:string):void {
    UiUtil.formMarkFieldDirty(this, 'dataGroupForm', field);
  }

  @Input()
  set dataGroup(dataGroup:DataGroupVO) {

    UiUtil.formInitialise(this, 'initialising', 'dataGroupForm', '_dataGroup', dataGroup);

  }

  get dataGroup():DataGroupVO {
    return this._dataGroup;
  }

  onNameDataChange(event:FormValidationEvent<any>) {
    UiUtil.formI18nDataChange(this, 'dataGroupForm', 'name', event);
  }


  ngOnInit() {
    LogUtil.debug('DataGroupComponent ngOnInit');
    this.formBind();
  }

  ngOnDestroy() {
    LogUtil.debug('DataGroupComponent ngOnDestroy');
    this.formUnbind();
  }

  tabSelected(tab:any) {
    LogUtil.debug('DataGroupComponent tabSelected', tab);
  }

}
