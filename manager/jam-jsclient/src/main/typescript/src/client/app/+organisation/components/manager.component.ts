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
import { Component, OnInit, OnDestroy, Input, ViewChild, Output, EventEmitter } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { YcValidators } from './../../shared/validation/validators';
import {
  ManagerVO, ManagerShopLinkVO, ManagerRoleLinkVO, ManagerSupplierCatalogVO,
  ShopVO, RoleVO, ProductSupplierCatalogVO,
  Pair, ValidationRequestVO
} from './../../shared/model/index';
import { ModalComponent, ModalResult, ModalAction } from './../../shared/modal/index';
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
  private _suppliers:any = {};

  private availableShops:Array<Pair<ShopVO, ManagerShopLinkVO>> = [];
  private supportedShops:Array<Pair<ShopVO, ManagerShopLinkVO>> = [];

  private availableRoles:Array<Pair<RoleVO, ManagerRoleLinkVO>> = [];
  private supportedRoles:Array<Pair<RoleVO, ManagerRoleLinkVO>> = [];

  private availableSuppliers:Array<Pair<ProductSupplierCatalogVO, ManagerSupplierCatalogVO>> = [];
  private supportedSuppliers:Array<Pair<ProductSupplierCatalogVO, ManagerSupplierCatalogVO>> = [];

  private delayedChange:Future;

  private managerForm:any;

  private mustSelectShop:boolean = false;

  private newSupplier:ProductSupplierCatalogVO;
  @ViewChild('editNewSupplierCode')
  private editNewSupplierCode:ModalComponent;

  private newSupplierForm:any;
  private validForSave:boolean = false;

  private reloadCatalogue:boolean = false;

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

        let req:ValidationRequestVO = { subject: 'manager', subjectId: that._manager.managerId, field: 'email', value: email };
        return YcValidators.validRemoteCheck(control, req);
      }
      return basic;
    };

    this.managerForm = fb.group({
      'email': ['', validInput],
      'firstName': ['', YcValidators.requiredNonBlankTrimmed128],
      'lastName': ['', YcValidators.requiredNonBlankTrimmed128],
      'managerShops': [''],
      'managerRoles': [''],
      'managerSupplierCatalogs': [''],
      'companyName1': ['', YcValidators.nonBlankTrimmed255],
      'companyName2': ['', YcValidators.nonBlankTrimmed255],
      'companyDepartment': ['', YcValidators.nonBlankTrimmed255],
    });

    this.newSupplierForm = fb.group({
      'code': ['', YcValidators.requiredValidCode]
    });

    this.newSupplier = this.newSupplierInstance();

    this.delayedChange = Futures.perpetual(function() {
      that.formChange();
    }, 200);
  }

  formBind():void {
    UiUtil.formBind(this, 'managerForm', 'delayedChange');
  }

  formUnbind():void {
    UiUtil.formUnbind(this, 'managerForm');
  }

  formChange():void {
    LogUtil.debug('ManagerComponent formChange', this.managerForm.valid, this._manager);
    this.dataChanged.emit({ source: this._manager, valid: this.managerForm.valid && !this.mustSelectShop });
  }

  formMarkDirty(field:string):void {
    UiUtil.formMarkFieldDirty(this, 'managerForm', field);
  }


  formBindAdd():void {
    UiUtil.formBind(this, 'newSupplierForm', 'formChangeAdd', false);
  }

  formUnbindAdd():void {
    UiUtil.formUnbind(this, 'newSupplierForm');
  }

  formChangeAdd():void {
    LogUtil.debug('ManagerComponent formChangeAdd', this.newSupplierForm.valid, this.newSupplier);
    this.validForSave = this.newSupplierForm.valid;
  }


  @Input()
  set shops(shops:Array<ShopVO>) {
    if (shops != null) {
      shops.forEach(shop => {
        this._shops['S' + shop.shopId] = shop;
      });
    }
    LogUtil.debug('ManagerComponent mapped shops', this._shops);
  }

  @Input()
  set roles(roles:Array<RoleVO>) {
    roles.forEach(role => {
      this._roles[role.code] = role;
    });
    LogUtil.debug('ManagerComponent mapped roles', this._roles);
  }

  @Input()
  set suppliers(suppliers:Array<ProductSupplierCatalogVO>) {
    suppliers.forEach(supplier => {
      this._suppliers[supplier.code] = supplier;
    });
    LogUtil.debug('ManagerComponent mapped suppliers', this._suppliers);
  }

  @Input()
  set manager(manager:ManagerVO) {

    UiUtil.formInitialise(this, 'managerForm', '_manager', manager, manager != null && manager.managerId > 0, ['email']);

    this.recalculateShops();
    this.recalculateRoles();
    this.recalculateSuppliers();
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
      link.code == supported.first.code
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

  onSupportedSupplierClick(supported:Pair<ProductSupplierCatalogVO, ManagerSupplierCatalogVO>) {
    LogUtil.debug('ManagerComponent remove supported supplier', supported);
    let idx = this._manager.managerSupplierCatalogs.findIndex(link =>
      link.code == supported.first.code
    );
    if (idx != -1) {
      this._manager.managerSupplierCatalogs.splice(idx, 1);
      this.recalculateSuppliers();
      this.formMarkDirty('managerSupplierCatalogs');
      this.formChange();
    }
  }

  onAvailableSupplierClick(available:Pair<ProductSupplierCatalogVO, ManagerSupplierCatalogVO>) {
    LogUtil.debug('ManagerComponent add supported supplier', available);
    this._manager.managerSupplierCatalogs.push(available.second);
    this.recalculateSuppliers();
    this.formMarkDirty('managerSupplierCatalogs');
    this.formChange();
  }

  onCategoriesDataChanged(event:any) {
    LogUtil.debug('ManagerComponent cat data changed', this._manager, event);
    this._manager.managerCategoryCatalogs = event;
    this.delayedChange.delay();
  }

  ngOnInit() {
    LogUtil.debug('ManagerComponent ngOnInit');
    this.formBind();
    this.formBindAdd();
  }

  ngOnDestroy() {
    LogUtil.debug('ManagerComponent ngOnDestroy');
    this.formUnbind();
    this.formUnbindAdd();
  }

  tabSelected(tab:any) {
    LogUtil.debug('ManagerComponent tabSelected', tab);
    this.reloadCatalogue = tab === 'Catalogue';
  }

  newSupplierInstance():ProductSupplierCatalogVO {
    return { code: '' };
  }

  /**
   * Create new supplier.
   */
  createNew() {
    LogUtil.debug('ManagerComponent createNew');
    this.validForSave = false;
    UiUtil.formInitialise(this, 'newSupplierForm', 'newSupplier', this.newSupplierInstance());
    this.editNewSupplierCode.show();
  }

  /**
   * Handle result of new supplier modal dialog.
   * @param modalresult
   */
  editNewSupplierCodeResult(modalresult:ModalResult) {
    LogUtil.debug('ManagerComponent editNewCategoryNameModalResult modal result', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {
      let supplier:ProductSupplierCatalogVO = { code: this.newSupplier.code };
      this._suppliers[supplier.code] = supplier;
      this.onAvailableSupplierClick({ first: supplier, second: { managerId: this.manager.managerId, code: supplier.code }});
    }
  }


  private recalculateShops():void {

    if (this._manager) {
      this.availableShops = this.getAvailableShopNames();
      this.supportedShops = this.getSupportedShopNames();
    } else {
      this.availableShops = this.getAvailableShopNames();
      this.supportedShops = [];
    }

    this.mustSelectShop = this._manager != null && !(this._manager.managerId > 0) && (this._manager.managerShops == null || this._manager.managerShops.length == 0);
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

  private recalculateSuppliers():void {

    if (this._manager) {
      this.availableSuppliers = this.getAvailableSupplierNames();
      this.supportedSuppliers = this.getSupportedSupplierNames();
    } else {
      this.availableSuppliers = this.getAvailableSupplierNames();
      this.supportedSuppliers = [];
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
        skipKeys.push(managerrole.code);
      });
    }
    LogUtil.debug('ManagerComponent supported roles', skipKeys);

    let labels = <Array<Pair<RoleVO, ManagerRoleLinkVO>>>[];
    for (let key in this._roles) {
      if (skipKeys.indexOf(key) == -1) {
        let role = this._roles[key];
        labels.push({
          first: role,
          second: { managerId: this._manager != null ? this._manager.managerId : 0, code: role.code }
        });
      }
    }

    labels.sort((a, b) => {
      return (a.first.description.toLowerCase() < b.first.description.toLowerCase()) ? -1 : 1;
    });

    LogUtil.debug('ManagerComponent available roles', labels);
    return labels;
  }

  private getSupportedRoleNames():Array<Pair<RoleVO, ManagerRoleLinkVO>> {
    let supported = this._manager.managerRoles;
    let keepKeys = <Array<string>>[];
    if (supported) {
      supported.forEach(managerrole => {
        keepKeys.push(managerrole.code);
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

    labels.sort((a, b) => {
      return (a.first.description.toLowerCase() < b.first.description.toLowerCase()) ? -1 : 1;
    });

    LogUtil.debug('ManagerComponent supported roles', labels);
    return labels;
  }

  private getAvailableSupplierNames():Array<Pair<ProductSupplierCatalogVO, ManagerSupplierCatalogVO>> {

    let supported = this._manager != null ? this._manager.managerSupplierCatalogs : [];
    let skipKeys = <Array<string>>[];
    if (supported) {
      supported.forEach(managersup => {
        skipKeys.push(managersup.code);
      });
    }
    LogUtil.debug('ManagerComponent supported suppliers', skipKeys);

    let labels = <Array<Pair<ProductSupplierCatalogVO, ManagerSupplierCatalogVO>>>[];
    for (let key in this._suppliers) {
      if (skipKeys.indexOf(key) == -1) {
        let supplier = this._suppliers[key];
        labels.push({
          first: supplier,
          second: { managerId: this._manager != null ? this._manager.managerId : 0, code: supplier.code }
        });
      }
    }

    labels.sort((a, b) => {
      return (a.first.code.toLowerCase() < b.first.code.toLowerCase()) ? -1 : 1;
    });

    LogUtil.debug('ManagerComponent available suppliers', labels);
    return labels;
  }

  private getSupportedSupplierNames():Array<Pair<ProductSupplierCatalogVO, ManagerSupplierCatalogVO>> {
    let supported = this._manager.managerSupplierCatalogs;
    let keepKeys = <Array<string>>[];
    if (supported) {
      supported.forEach(managersup => {
        keepKeys.push(managersup.code);
        if (!this._suppliers.hasOwnProperty(managersup.code)) {
          this._suppliers[managersup.code] = { code: managersup.code };
        }
      });
    }
    LogUtil.debug('ManagerComponent supported suppliers', keepKeys);

    let labels = <Array<Pair<ProductSupplierCatalogVO, ManagerSupplierCatalogVO>>>[];
    for (let key in this._suppliers) {
      let idx = keepKeys.indexOf(key);
      if (idx != -1) {
        let supplier = this._suppliers[key];
        let managerSupplier = this._manager.managerSupplierCatalogs[idx];
        labels.push({ first: supplier, second: managerSupplier });
      }
    }

    labels.sort((a, b) => {
      return (a.first.code.toLowerCase() < b.first.code.toLowerCase()) ? -1 : 1;
    });

    LogUtil.debug('ManagerComponent supported suppliers', labels);
    return labels;
  }

}
