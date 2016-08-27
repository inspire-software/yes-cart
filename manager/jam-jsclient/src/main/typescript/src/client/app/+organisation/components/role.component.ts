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
import {RoleVO} from './../../shared/model/index';
import {FormValidationEvent, Futures, Future} from './../../shared/event/index';


@Component({
  selector: 'yc-role',
  moduleId: module.id,
  templateUrl: 'role.component.html',
  directives: [NgIf, REACTIVE_FORM_DIRECTIVES],
})

export class RoleComponent implements OnInit, OnDestroy {

  _role:RoleVO;

  @Output() dataChanged: EventEmitter<FormValidationEvent<RoleVO>> = new EventEmitter<FormValidationEvent<RoleVO>>();

  changed:boolean = false;
  validForSave:boolean = false;
  delayedChange:Future;

  roleForm:any;
  roleFormSub:any;

  constructor(fb: FormBuilder) {
    console.debug('RoleComponent constructed');

    this.roleForm = fb.group({
      'code': ['', YcValidators.requiredValidRole],
      'description': [''],
    });

    let that = this;
    this.delayedChange = Futures.perpetual(function() {
      that.formChange();
    }, 200);
  }

  formReset():void {
    // Hack to reset NG2 forms see https://github.com/angular/angular/issues/4933
    for(let key in this.roleForm.controls) {
      this.roleForm.controls[key]['_pristine'] = true;
      this.roleForm.controls[key]['_touched'] = false;
    }
  }

  formBind():void {
    this.roleFormSub = this.roleForm.valueChanges.subscribe((data:any) => {
      this.validForSave = this.roleForm.valid;
      if (this.changed) {
        this.delayedChange.delay();
      }
    });
  }

  formUnbind():void {
    if (this.roleFormSub) {
      console.debug('RoleComponent unbining form');
      this.roleFormSub.unsubscribe();
    }
  }

  formChange():void {
    console.debug('RoleComponent validating formGroup is valid: ' + this.validForSave, this._role);
    this.dataChanged.emit({ source: this._role, valid: this.validForSave });
  }

  @Input()
  set role(role:RoleVO) {
    this._role = role;
    this.changed = false;
    this.formReset();
  }

  get role():RoleVO {
    return this._role;
  }

  onDataChange(event:any) {
    console.debug('RoleComponent onDataChange', event);
    this.changed = true;
  }

  ngOnInit() {
    console.debug('RoleComponent ngOnInit');
    this.formBind();
  }

  ngOnDestroy() {
    console.debug('RoleComponent ngOnDestroy');
    this.formUnbind();
  }

}
