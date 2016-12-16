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
import { RoleVO } from './../../shared/model/index';
import { FormValidationEvent, Futures, Future } from './../../shared/event/index';
import { UiUtil } from './../../shared/ui/index';
import { LogUtil } from './../../shared/log/index';


@Component({
  selector: 'yc-role',
  moduleId: module.id,
  templateUrl: 'role.component.html',
})

export class RoleComponent implements OnInit, OnDestroy {

  @Output() dataChanged: EventEmitter<FormValidationEvent<RoleVO>> = new EventEmitter<FormValidationEvent<RoleVO>>();

  private _role:RoleVO;

  private initialising:boolean = false; // tslint:disable-line:no-unused-variable
  private delayedChange:Future;

  private roleForm:any;
  private roleFormSub:any; // tslint:disable-line:no-unused-variable

  constructor(fb: FormBuilder) {
    LogUtil.debug('RoleComponent constructed');

    this.roleForm = fb.group({
      'code': ['', YcValidators.requiredValidRole],
      'description': [''],
    });

    let that = this;
    this.delayedChange = Futures.perpetual(function() {
      that.formChange();
    }, 200);
  }

  formBind():void {
    UiUtil.formBind(this, 'roleForm', 'roleFormSub', 'delayedChange', 'initialising');
  }

  formUnbind():void {
    UiUtil.formUnbind(this, 'roleFormSub');
  }

  formChange():void {
    LogUtil.debug('RoleComponent formChange', this.roleForm.valid, this._role);
    this.dataChanged.emit({ source: this._role, valid: this.roleForm.valid });
  }

  @Input()
  set role(role:RoleVO) {

    UiUtil.formInitialise(this, 'initialising', 'roleForm', '_role', role, role != null && role.roleId > 0, ['code']);

  }

  get role():RoleVO {
    return this._role;
  }

  ngOnInit() {
    LogUtil.debug('RoleComponent ngOnInit');
    this.formBind();
  }

  ngOnDestroy() {
    LogUtil.debug('RoleComponent ngOnDestroy');
    this.formUnbind();
  }

}
