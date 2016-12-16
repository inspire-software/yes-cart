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
import { StateVO } from './../../shared/model/index';
import { FormValidationEvent, Futures, Future } from './../../shared/event/index';
import { UiUtil } from './../../shared/ui/index';
import { LogUtil } from './../../shared/log/index';

@Component({
  selector: 'yc-state',
  moduleId: module.id,
  templateUrl: 'state.component.html',
})

export class StateComponent implements OnInit, OnDestroy {

  @Output() dataChanged: EventEmitter<FormValidationEvent<StateVO>> = new EventEmitter<FormValidationEvent<StateVO>>();

  private _state:StateVO;

  private initialising:boolean = false; // tslint:disable-line:no-unused-variable
  private delayedChange:Future;

  private stateForm:any;
  private stateFormSub:any; // tslint:disable-line:no-unused-variable

  constructor(fb: FormBuilder) {
    LogUtil.debug('StateComponent constructed');

    this.stateForm = fb.group({
      'countryCode': ['', YcValidators.requiredValidCountryCode],
      'stateCode': ['', YcValidators.requiredNonBlankTrimmed],
      'name': ['', YcValidators.requiredNonBlankTrimmed],
      'displayName': ['', YcValidators.requiredNonBlankTrimmed],
    });

    let that = this;
    this.delayedChange = Futures.perpetual(function() {
      that.formChange();
    }, 200);
  }

  formBind():void {
    UiUtil.formBind(this, 'stateForm', 'stateFormSub', 'delayedChange', 'initialising');
  }


  formUnbind():void {
    UiUtil.formUnbind(this, 'stateFormSub');
  }

  formChange():void {
    LogUtil.debug('StateComponent formChange', this.stateForm.valid, this._state);
    this.dataChanged.emit({ source: this._state, valid: this.stateForm.valid });
  }

  @Input()
  set state(state:StateVO) {

    UiUtil.formInitialise(this, 'initialising', 'stateForm', '_state', state, true, ['countryCode']);

  }

  get state():StateVO {
    return this._state;
  }


  ngOnInit() {
    LogUtil.debug('StateComponent ngOnInit');
    this.formBind();
  }

  ngOnDestroy() {
    LogUtil.debug('StateComponent ngOnDestroy');
    this.formUnbind();
  }

}
