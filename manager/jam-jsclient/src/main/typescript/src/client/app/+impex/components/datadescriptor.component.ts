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
import { FormBuilder, Validators } from '@angular/forms';
import { CustomValidators } from './../../shared/validation/validators';
import { DataDescriptorVO } from './../../shared/model/index';
import { FormValidationEvent, Futures, Future } from './../../shared/event/index';
import { UiUtil } from './../../shared/ui/index';
import { LogUtil } from './../../shared/log/index';

@Component({
  selector: 'yc-datadescriptor',
  moduleId: module.id,
  templateUrl: 'datadescriptor.component.html',
})

export class DataDescriptorComponent implements OnInit, OnDestroy {

  @Output() dataChanged: EventEmitter<FormValidationEvent<DataDescriptorVO>> = new EventEmitter<FormValidationEvent<DataDescriptorVO>>();

  private _dataDescriptor:DataDescriptorVO;

  private delayedChange:Future;

  private dataDescriptorForm:any;

  constructor(fb: FormBuilder) {
    LogUtil.debug('DataDescriptorComponent constructed');

    let that = this;

    this.dataDescriptorForm = fb.group({
      'name': ['', CustomValidators.requiredNonBlankTrimmed128],
      'type': ['', Validators.required],
      'value': ['', Validators.required],
    });

    this.delayedChange = Futures.perpetual(function() {
      that.formChange();
    }, 200);

  }

  formBind():void {
    UiUtil.formBind(this, 'dataDescriptorForm', 'delayedChange');
  }


  formUnbind():void {
    UiUtil.formUnbind(this, 'dataDescriptorForm');
  }

  formChange():void {
    LogUtil.debug('DataDescriptorComponent formChange', this.dataDescriptorForm.valid, this._dataDescriptor);
    this.dataChanged.emit({ source: this._dataDescriptor, valid: this.dataDescriptorForm.valid });
  }

  formMarkDirty(field:string):void {
    UiUtil.formMarkFieldDirty(this, 'dataDescriptorForm', field);
  }

  @Input()
  set dataDescriptor(dataDescriptor:DataDescriptorVO) {

    UiUtil.formInitialise(this, 'dataDescriptorForm', '_dataDescriptor', dataDescriptor);

  }

  get dataDescriptor():DataDescriptorVO {
    return this._dataDescriptor;
  }


  ngOnInit() {
    LogUtil.debug('DataDescriptorComponent ngOnInit');
    this.formBind();
  }

  ngOnDestroy() {
    LogUtil.debug('DataDescriptorComponent ngOnDestroy');
    this.formUnbind();
  }

  tabSelected(tab:any) {
    LogUtil.debug('DataDescriptorComponent tabSelected', tab);
  }

}
