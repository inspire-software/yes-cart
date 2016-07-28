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
import {StateVO} from './../../shared/model/index';
import {FormValidationEvent} from './../../shared/event/index';


@Component({
  selector: 'yc-state',
  moduleId: module.id,
  templateUrl: 'state.component.html',
  directives: [NgIf, REACTIVE_FORM_DIRECTIVES],
})

export class StateComponent implements OnInit, OnDestroy {

  _state:StateVO;

  @Output() dataChanged: EventEmitter<FormValidationEvent<StateVO>> = new EventEmitter<FormValidationEvent<StateVO>>();

  validForSave:boolean = false;

  stateForm:any;

  constructor(fb: FormBuilder) {
    console.debug('StateComponent constructed');

    this.stateForm = fb.group({
      'countryCode': ['', Validators.compose([Validators.required, Validators.pattern('[A-Z]{2}')])],
      'stateCode': ['', Validators.compose([Validators.required, Validators.pattern('\\S+.*\\S+')])],
      'name': ['', Validators.compose([Validators.required, Validators.pattern('\\S+.*\\S+')])],
      'displayName': ['', Validators.pattern('\\S+.*\\S+')],
    });

  }

  formReset():void {
    // Hack to reset NG2 forms see https://github.com/angular/angular/issues/4933
    for(let key in this.stateForm.controls) {
      this.stateForm.controls[key]['_pristine'] = true;
      this.stateForm.controls[key]['_touched'] = false;
    }
  }

  @Input()
  set state(state:StateVO) {
    this._state = state;
    this.formReset();
  }

  get state():StateVO {
    return this._state;
  }

  onDataChange(event:any) {
    var _sub:any = this.stateForm.valueChanges.subscribe((data:any) => {
      this.validForSave = this.stateForm.valid;
      console.debug('StateComponent form changed  and ' + (this.validForSave ? 'is valid' : 'is NOT valid'), data);
      _sub.unsubscribe();
      this.dataChanged.emit({ source: this._state, valid: this.validForSave });
    });
    console.debug('StateComponent data changed and ' + (this.validForSave ? 'is valid' : 'is NOT valid'), event);
  }

  ngOnInit() {
    console.debug('StateComponent ngOnInit');
  }

  ngOnDestroy() {
    console.debug('StateComponent ngOnDestroy');
  }

}
