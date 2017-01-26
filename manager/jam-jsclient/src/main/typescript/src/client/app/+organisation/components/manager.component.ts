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
import { ManagerVO, ManagerShopLinkVO, ManagerRoleLinkVO, ShopVO, RoleVO, Pair, ValidationRequestVO } from './../../shared/model/index';
import { FormValidationEvent, Futures, Future } from './../../shared/event/index';
import { UiUtil } from './../../shared/ui/index';
import { LogUtil } from './../../shared/log/index';

@Component({
  selector: 'yc-manager',
  moduleId: module.id,
  templateUrl: 'manager.component.html',
})

export class ManagerComponent implements OnInit, OnDestroy {

  @Output() dataChanged: EventEmitter<FormValidationEvent<ManagerVO>> = new EventEmitter<FormValidationEvent<ManagerVO>>();

  private _manager:ManagerVO;

  private _shops:any = {};
  private _roles:any = {};

  private availableShops:Array<Pair<ShopVO, ManagerShopLinkVO>> = [];
  private supportedShops:Array<Pair<ShopVO, ManagerShopLinkVO>> = [];

  private availableRoles:Array<Pair<RoleVO, ManagerRoleLinkVO>> = [];
  private supportedRoles:Array<Pair<RoleVO, ManagerRoleLinkVO>> = [];

  private initialising:boolean = false; // tslint:disable-line:no-unused-variable
  private delayedChange:Future;

  private managerForm:any;
  private managerFormSub:any; // tslint:disable-line:no-unused-variable

  constructor(fb: FormBuilder) {
    LogUtil.debug('ManagerComponent constructed');

    let that = this;

    let validInput = function(control:any):any {

      let basic = YcValidators.requiredValidEmail(control);
      if (basic == null) {

        let email = control.value;
        if (that._manager == null || !that.managerForm || (!that.managerForm.dirty && that._manager.managerId > 0)) {
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
      'managerShops': [''],
      'managerRoles': [''],
    });

    this.delayedChange = Futures.perpetual(function() {
      that.formChange();
    }, 200);
  }

  formBind():void {
    UiUtil.formBind(this, 'managerForm', 'managerFormSub', 'delayedChange', 'initialising');
  }

  formUnbind():void {
    UiUtil.formUnbind(this, 'managerFormSub');
  }

  formChange():void {
    LogUtil.debug('ManagerComponent formChange', this.managerForm.valid, this._manager);
    this.dataChanged.emit({ source: this._manager, valid: this.managerForm.valid });
  }

  formMarkDirty(field:string):void {
    UiUtil.formMarkFieldDirty(this, 'managerForm', field);
  }

  @Input()
  set shops(shops:Array<ShopVO>) {
    shops.forEach(shop => {
      this._shops['S' + shop.shopId] = shop;
    });
    LogUtil.debug('ManagerComponent mapped shops', this._shops);
  }

  @Input()
  set roles(roles:Array<RoleVO>) {
    roles.forEach(role => {
      this._roles['R' + role.roleId] = role;
    });
    LogUtil.debug('ManagerComponent mapped roles', this._roles);
  }

  @Input()
  set manager(manager:ManagerVO) {

    UiUtil.formInitialise(this, 'initialising', 'managerForm', '_manager', manager, manager != null && manager.managerId > 0, ['email']);

    this.recalculateShops();
    this.recalculateRoles();
  }

  get manager():ManagerVO {
    return this._manager;
  }

  onSupportedShopClick(supported:Pair<ShopVO, ManagerShopLinkVO>) {
    LogUtil.debug('ManagerComponent remove supported', supported);
    let idx = this._manager.managerShops.findIndex(link =>
      link.shopId == supported.first.shopId
    );
    if (idx != -1) {
      this._manager.managerShops.splice(idx, 1);
      this.recalculateShops();
      this.formMarkDirty('managerShops');
      this.formChange();
    }
  }

  onAvailableShopClick(available:Pair<ShopVO, ManagerShopLinkVO>) {
    LogUtil.debug('ManagerComponent add supported', available);
    this._manager.managerShops.push(available.second);
    this.recalculateShops();
    this.formMarkDirty('managerShops');
    this.formChange();
  }

  onSupportedRoleClick(supported:Pair<RoleVO, ManagerRoleLinkVO>) {
    LogUtil.debug('ManagerComponent remove supported role', supported);
    let idx = this._manager.managerRoles.findIndex(link =>
      link.roleId == supported.first.roleId
    );
    if (idx != -1) {
      this._manager.managerRoles.splice(idx, 1);
      this.recalculateRoles();
      this.formMarkDirty('managerRoles');
      this.formChange();
    }
  }

  onAvailableRoleClick(available:Pair<RoleVO, ManagerRoleLinkVO>) {
    LogUtil.debug('ManagerComponent add supported role', available);
    this._manager.managerRoles.push(available.second);
    this.recalculateRoles();
    this.formMarkDirty('managerRoles');
    this.formChange();
  }

  ngOnInit() {
    LogUtil.debug('ManagerComponent ngOnInit');
    this.formBind();
  }

  ngOnDestroy() {
    LogUtil.debug('ManagerComponent ngOnDestroy');
    this.formUnbind();
  }

  tabSelected(tab:any) {
    LogUtil.debug('ManagerComponent tabSelected', tab);
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
    LogUtil.debug('ManagerComponent supported shops', skipKeys);

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

    LogUtil.debug('ManagerComponent available shops', labels);
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
    LogUtil.debug('ManagerComponent supported', keepKeys);

    let labels = <Array<Pair<ShopVO, ManagerShopLinkVO>>>[];
    for (let key in this._shops) {
      let idx = keepKeys.indexOf(key);
      if (idx != -1) {
        let shop = this._shops[key];
        let managerShop = this._manager.managerShops[idx];
        labels.push({ first: shop, second: managerShop });
      }
    }

    LogUtil.debug('ManagerComponent supported', labels);
    return labels;
  }

  private getAvailableRoleNames():Array<Pair<RoleVO, ManagerRoleLinkVO>> {

    let supported = this._manager != null ? this._manager.managerRoles : [];
    let skipKeys = <Array<string>>[];
    if (supported) {
      supported.forEach(managerrole => {
        skipKeys.push('R' + managerrole.roleId);
      });
    }
    LogUtil.debug('ManagerComponent supported roles', skipKeys);

    let labels = <Array<Pair<RoleVO, ManagerRoleLinkVO>>>[];
    for (let key in this._roles) {
      if (skipKeys.indexOf(key) == -1) {
        let role = this._roles[key];
        labels.push({
          first: role,
          second: { managerId: this._manager != null ? this._manager.managerId : 0, roleId: role.roleId, code: '-' }
        });
      }
    }

    LogUtil.debug('ManagerComponent available roles', labels);
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
    LogUtil.debug('ManagerComponent supported roles', keepKeys);

    let labels = <Array<Pair<RoleVO, ManagerRoleLinkVO>>>[];
    for (let key in this._roles) {
      let idx = keepKeys.indexOf(key);
      if (idx != -1) {
        let role = this._roles[key];
        let managerRole = this._manager.managerRoles[idx];
        labels.push({ first: role, second: managerRole });
      }
    }

    LogUtil.debug('ManagerComponent supported roles', labels);
    return labels;
  }

}
