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
import {ManagerVO, ManagerShopLinkVO, ManagerRoleLinkVO, ShopVO, RoleVO, Pair, ValidationRequestVO} from './../../shared/model/index';
import {FormValidationEvent, Futures, Future} from './../../shared/event/index';
import {TAB_DIRECTIVES} from 'ng2-bootstrap/ng2-bootstrap';


@Component({
  selector: 'yc-manager',
  moduleId: module.id,
  templateUrl: 'manager.component.html',
  directives: [NgIf, REACTIVE_FORM_DIRECTIVES, TAB_DIRECTIVES],
})

export class ManagerComponent implements OnInit, OnDestroy {

  _manager:ManagerVO;

  _shops:any = {};
  _roles:any = {};

  availableShops:Array<Pair<ShopVO, ManagerShopLinkVO>> = [];
  supportedShops:Array<Pair<ShopVO, ManagerShopLinkVO>> = [];

  availableRoles:Array<Pair<RoleVO, ManagerRoleLinkVO>> = [];
  supportedRoles:Array<Pair<RoleVO, ManagerRoleLinkVO>> = [];

  @Output() dataChanged: EventEmitter<FormValidationEvent<ManagerVO>> = new EventEmitter<FormValidationEvent<ManagerVO>>();

  changed:boolean = false;
  validForSave:boolean = false;
  delayedChange:Future;

  managerForm:any;
  managerFormSub:any;

  constructor(fb: FormBuilder) {
    console.debug('ManagerComponent constructed');

    let that = this;

    let validInput = function(control:any):any {

      let basic = YcValidators.requiredValidEmail(control);
      if (basic == null) {

        let email = control.value;
        if (!that.changed || that._manager == null) {
          return null;
        }

        var req:ValidationRequestVO = { subject: 'manager', subjectId: that._manager.managerId, field: 'email', value: email };
        return YcValidators.validRemoteCheck(control, req);
      }
      return basic;
    };

    this.managerForm = fb.group({
      'email': ['', validInput],
      'firstName': ['', Validators.required],
      'lastName': ['', Validators.required],
    });

    this.delayedChange = Futures.perpetual(function() {
      that.formChange();
    }, 200);

  }

  formReset():void {
    // Hack to reset NG2 forms see https://github.com/angular/angular/issues/4933
    for(let key in this.managerForm.controls) {
      this.managerForm.controls[key]['_pristine'] = true;
      this.managerForm.controls[key]['_touched'] = false;
    }
  }

  formBind():void {
    this.managerFormSub = this.managerForm.statusChanges.subscribe((data:any) => {
      this.validForSave = this.managerForm.valid;
      if (this.changed) {
        this.delayedChange.delay();
      }
    });
  }

  formUnbind():void {
    if (this.managerFormSub) {
      console.debug('ManagerComponent unbining form');
      this.managerFormSub.unsubscribe();
    }
  }

  formChange():void {
    console.debug('ManagerComponent validating formGroup is valid: ' + this.validForSave, this._manager);
    this.dataChanged.emit({ source: this._manager, valid: this.validForSave });
  }

  @Input()
  set shops(shops:Array<ShopVO>) {
    shops.forEach(shop => {
      this._shops['S' + shop.shopId] = shop;
    });
    console.debug('ManagerComponent mapped shops', this._shops);
  }

  @Input()
  set roles(roles:Array<RoleVO>) {
    roles.forEach(role => {
      this._roles['R' + role.roleId] = role;
    });
    console.debug('ManagerComponent mapped roles', this._roles);
  }

  @Input()
  set manager(manager:ManagerVO) {
    this._manager = manager;
    this.changed = false;
    this.formReset();
    this.recalculateShops();
    this.recalculateRoles();
  }

  get manager():ManagerVO {
    return this._manager;
  }

  private recalculateShops():void {

    if (this._manager) {
      this.availableShops = this.getAvailableShopNames();
      this.supportedShops = this.getSupportedShopNames();
    } else {
      this.availableShops = this.getAvailableShopNames();
      this.supportedShops = [];
    }
  }

  private recalculateRoles():void {

    if (this._manager) {
      this.availableRoles = this.getAvailableRoleNames();
      this.supportedRoles = this.getSupportedRoleNames();
    } else {
      this.availableRoles = this.getAvailableRoleNames();
      this.supportedRoles = [];
    }
  }

  private getAvailableShopNames():Array<Pair<ShopVO, ManagerShopLinkVO>> {

    let supported = this._manager != null ? this._manager.managerShops : [];
    let skipKeys = <Array<string>>[];
    if (supported) {
      supported.forEach(managershop => {
        skipKeys.push('S' + managershop.shopId);
      });
    }
    console.debug('ManagerComponent supported shops', skipKeys);

    let labels = <Array<Pair<ShopVO, ManagerShopLinkVO>>>[];
    for (let key in this._shops) {
      if (skipKeys.indexOf(key) == -1) {
        let shop = this._shops[key];
        labels.push({
          first: shop,
          second: { managerId: this._manager != null ? this._manager.managerId : 0, shopId: shop.shopId }
        });
      }
    }

    console.debug('ManagerComponent available shops', labels);
    return labels;
  }

  private getSupportedShopNames():Array<Pair<ShopVO, ManagerShopLinkVO>> {
    let supported = this._manager.managerShops;
    let keepKeys = <Array<string>>[];
    if (supported) {
      supported.forEach(managershop => {
        keepKeys.push('S' + managershop.shopId);
      });
    }
    console.debug('ManagerComponent supported', keepKeys);

    let labels = <Array<Pair<ShopVO, ManagerShopLinkVO>>>[];
    for (let key in this._shops) {
      let idx = keepKeys.indexOf(key);
      if (idx != -1) {
        let shop = this._shops[key];
        let managerShop = this._manager.managerShops[idx];
        labels.push({ first: shop, second: managerShop });
      }
    }

    console.debug('ManagerComponent supported', labels);
    return labels;
  }

  onSupportedShopClick(supported:Pair<ShopVO, ManagerShopLinkVO>) {
    console.debug('ManagerComponent remove supported', supported);
    let idx = this._manager.managerShops.findIndex(link =>
      link.shopId == supported.first.shopId
    );
    if (idx != -1) {
      this._manager.managerShops.splice(idx, 1);
      this.recalculateShops();
      this.changed = true;
      this.dataChanged.emit({ source: this._manager, valid: this.validForSave });
    }
  }

  onAvailableShopClick(available:Pair<ShopVO, ManagerShopLinkVO>) {
    console.debug('ManagerComponent add supported', available);
    this._manager.managerShops.push(available.second);
    this.recalculateShops();
    this.changed = true;
    this.dataChanged.emit({ source: this._manager, valid: this.validForSave });
  }

  private getAvailableRoleNames():Array<Pair<RoleVO, ManagerRoleLinkVO>> {

    let supported = this._manager != null ? this._manager.managerRoles : [];
    let skipKeys = <Array<string>>[];
    if (supported) {
      supported.forEach(managerrole => {
        skipKeys.push('R' + managerrole.roleId);
      });
    }
    console.debug('ManagerComponent supported roles', skipKeys);

    let labels = <Array<Pair<RoleVO, ManagerRoleLinkVO>>>[];
    for (let key in this._roles) {
      if (skipKeys.indexOf(key) == -1) {
        let role = this._roles[key];
        labels.push({
          first: role,
          second: { managerId: this._manager != null ? this._manager.managerId : 0, roleId: role.roleId }
        });
      }
    }

    console.debug('ManagerComponent available roles', labels);
    return labels;
  }

  private getSupportedRoleNames():Array<Pair<RoleVO, ManagerRoleLinkVO>> {
    let supported = this._manager.managerRoles;
    let keepKeys = <Array<string>>[];
    if (supported) {
      supported.forEach(managerrole => {
        keepKeys.push('R' + managerrole.roleId);
      });
    }
    console.debug('ManagerComponent supported roles', keepKeys);

    let labels = <Array<Pair<RoleVO, ManagerRoleLinkVO>>>[];
    for (let key in this._roles) {
      let idx = keepKeys.indexOf(key);
      if (idx != -1) {
        let role = this._roles[key];
        let managerRole = this._manager.managerRoles[idx];
        labels.push({ first: role, second: managerRole });
      }
    }

    console.debug('ManagerComponent supported roles', labels);
    return labels;
  }

  onSupportedRoleClick(supported:Pair<RoleVO, ManagerRoleLinkVO>) {
    console.debug('ManagerComponent remove supported role', supported);
    let idx = this._manager.managerRoles.findIndex(link =>
      link.roleId == supported.first.roleId
    );
    if (idx != -1) {
      this._manager.managerRoles.splice(idx, 1);
      this.recalculateRoles();
      this.changed = true;
      this.dataChanged.emit({ source: this._manager, valid: this.validForSave });
    }
  }

  onAvailableRoleClick(available:Pair<RoleVO, ManagerRoleLinkVO>) {
    console.debug('ManagerComponent add supported role', available);
    this._manager.managerRoles.push(available.second);
    this.recalculateRoles();
    this.changed = true;
    this.dataChanged.emit({ source: this._manager, valid: this.validForSave });
  }

  onMainDataChanged(event:any) {
    console.debug('ManagerComponent data changed', this._manager);
    this.changed = true;
  }

  ngOnInit() {
    console.debug('ManagerComponent ngOnInit');
    this.formBind();
  }

  ngOnDestroy() {
    console.debug('ManagerComponent ngOnDestroy');
    this.formUnbind();
  }

  tabSelected(tab:any) {
    console.debug('ManagerComponent tabSelected', tab);
  }

}
