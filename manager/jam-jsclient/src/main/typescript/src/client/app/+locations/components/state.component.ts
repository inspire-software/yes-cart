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
import {YcValidators} from './../../shared/validation/validators';
import {StateVO} from './../../shared/model/index';
import {FormValidationEvent, Futures, Future} from './../../shared/event/index';


@Component({
  selector: 'yc-state',
  moduleId: module.id,
  templateUrl: 'state.component.html',
  directives: [NgIf, REACTIVE_FORM_DIRECTIVES],
})

export class StateComponent implements OnInit, OnDestroy {

  _state:StateVO;

  @Output() dataChanged: EventEmitter<FormValidationEvent<StateVO>> = new EventEmitter<FormValidationEvent<StateVO>>();

  changed:boolean = false;
  validForSave:boolean = false;
  delayedChange:Future;

  stateForm:any;
  stateFormSub:any;

  constructor(fb: FormBuilder) {
    console.debug('StateComponent constructed');

    this.stateForm = fb.group({
      'countryCode': ['', YcValidators.requiredValidCountryCode],
      'stateCode': ['', YcValidators.requiredNonBlankTrimmed],
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
    for(let key in this.stateForm.controls) {
      this.stateForm.controls[key]['_pristine'] = true;
      this.stateForm.controls[key]['_touched'] = false;
    }
  }

  formBind():void {
    this.stateFormSub = this.stateForm.valueChanges.subscribe((data:any) => {
      this.validForSave = this.stateForm.valid;
      if (this.changed) {
        this.delayedChange.delay();
      }
    });
  }

  formUnbind():void {
    if (this.stateFormSub) {
      console.debug('StateComponent unbining form');
      this.stateFormSub.unsubscribe();
    }
  }

  formChange():void {
    console.debug('StateComponent validating formGroup is valid: ' + this.validForSave, this._state);
    this.dataChanged.emit({ source: this._state, valid: this.validForSave });
  }

  @Input()
  set state(state:StateVO) {
    this._state = state;
    this.changed = false;
    this.formReset();
  }

  get state():StateVO {
    return this._state;
  }

  onDataChange(event:any) {
    console.debug('StateComponent onDataChange', event);
    this.changed = true;
  }

  ngOnInit() {
    console.debug('StateComponent ngOnInit');
    this.formBind();
  }

  ngOnDestroy() {
    console.debug('StateComponent ngOnDestroy');
    this.formUnbind();
  }

}
