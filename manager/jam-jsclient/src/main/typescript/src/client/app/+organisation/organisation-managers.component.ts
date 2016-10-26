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
import {Component, OnInit, OnDestroy, ViewChild} from '@angular/core';
import {NgIf} from '@angular/common';
import {OrganisationService, ShopEventBus} from './../shared/services/index';
import {TAB_DIRECTIVES} from 'ng2-bootstrap/ng2-bootstrap';
import {ManagerComponent, ManagersComponent} from './components/index';
import {DataControlComponent} from './../shared/sidebar/index';
import {ModalComponent, ModalResult, ModalAction} from './../shared/modal/index';
import {ManagerInfoVO, ManagerVO, ShopVO, RoleVO} from './../shared/model/index';
import {FormValidationEvent} from './../shared/event/index';

@Component({
  selector: 'yc-organisation-managers',
  moduleId: module.id,
  templateUrl: 'organisation-managers.component.html',
  directives: [TAB_DIRECTIVES, NgIf, ManagerComponent, ManagersComponent, ModalComponent, DataControlComponent ],
})

export class OrganisationManagerComponent implements OnInit, OnDestroy {

  private static MANAGERS:string = 'managers';
  private static MANAGER:string = 'manager';

  private viewMode:string = OrganisationManagerComponent.MANAGERS;

  private managers:Array<ManagerInfoVO> = [];
  private managerFilter:string;

  private selectedManager:ManagerInfoVO;
  private managerEdit:ManagerVO;

  private shops:Array<ShopVO> = [];
  private roles:Array<RoleVO> = [];

  @ViewChild('deleteConfirmationModalDialog')
  deleteConfirmationModalDialog:ModalComponent;

  @ViewChild('disableConfirmationModalDialog')
  disableConfirmationModalDialog:ModalComponent;

  @ViewChild('resetConfirmationModalDialog')
  resetConfirmationModalDialog:ModalComponent;

  private deleteValue:String;
  private shopAllSub:any;

  constructor(private _organisationService:OrganisationService) {
    console.debug('OrganisationManagerComponent constructed');
    this.shopAllSub = ShopEventBus.getShopEventBus().shopsUpdated$.subscribe(shopsevt => {
      this.shops = shopsevt;
    });
  }

  changed:boolean = false;
  validForSave:boolean = false;

  newManagerInstance():ManagerVO {
    return { managerId: 0, email: '', firstName: '', lastName: '', enabled: null, managerShops: [], managerRoles: []};
  }

  ngOnInit() {
    console.debug('OrganisationManagerComponent ngOnInit');
    this.getAllRoles();
    this.onRefreshHandler();
  }

  ngOnDestroy() {
    console.debug('OrganisationManagerComponent ngOnDestroy');
    if (this.shopAllSub) {
      this.shopAllSub.unsubscribe();
    }
  }

  getAllManagers() {
    var _sub:any = this._organisationService.getAllManagers().subscribe( allmanagers => {
      console.debug('OrganisationManagerComponent getAllManagers', allmanagers);
      this.managers = allmanagers;
      this.selectedManager = null;
      this.managerEdit = null;
      this.viewMode = OrganisationManagerComponent.MANAGERS;
      this.changed = false;
      this.validForSave = false;
      _sub.unsubscribe();
    });
  }

  getAllRoles() {
    var _sub:any = this._organisationService.getAllRoles().subscribe( allroles => {
      console.debug('OrganisationManagerComponent getAllManagers', allroles);
      this.roles = allroles;
      _sub.unsubscribe();
    });
  }

  protected onRefreshHandler() {
    console.debug('OrganisationManagerComponent refresh handler');
    this.getAllManagers();
  }

  onManagerSelected(data:ManagerVO) {
    console.debug('OrganisationManagerComponent onManagerSelected', data);
    this.selectedManager = data;
  }

  onManagerChanged(event:FormValidationEvent<ManagerVO>) {
    console.debug('OrganisationManagerComponent onManagerChanged', event);
    this.changed = true;
    this.validForSave = event.valid;
    this.managerEdit = event.source;
  }

  protected onBackToList() {
    console.debug('OrganisationManagerComponent onBackToList handler');
    if (this.viewMode === OrganisationManagerComponent.MANAGER) {
      this.managerEdit = null;
      this.viewMode = OrganisationManagerComponent.MANAGERS;
    }
  }

  protected onRowNew() {
    console.debug('OrganisationManagerComponent onRowNew handler');
    this.changed = false;
    this.validForSave = false;
    if (this.viewMode === OrganisationManagerComponent.MANAGERS) {
      this.managerEdit = this.newManagerInstance();
      this.viewMode = OrganisationManagerComponent.MANAGER;
    }
  }

  protected onRowDelete(row:any) {
    console.debug('OrganisationManagerComponent onRowDelete handler', row);
    this.deleteValue = row.email;
    this.deleteConfirmationModalDialog.show();
  }

  protected onRowDeleteSelected() {
    if (this.selectedManager != null) {
      this.onRowDelete(this.selectedManager);
    }
  }

  protected onRowEditManager(row:ManagerInfoVO) {
    console.debug('OrganisationManagerComponent onRowEditManager handler', row);
    var _sub:any = this._organisationService.getManagerByEmail(row.email).subscribe( manager => {
      console.debug('OrganisationManagerComponent get manager by email', manager);
      this.managerEdit = manager;
      this.changed = false;
      this.validForSave = false;
      this.viewMode = OrganisationManagerComponent.MANAGER;
      _sub.unsubscribe();
    });
  }

  protected onRowEditSelected() {
    if (this.selectedManager != null) {
      this.onRowEditManager(this.selectedManager);
    }
  }

  protected onSaveHandler() {

    if (this.validForSave && this.changed) {

      if (this.managerEdit != null) {

        console.debug('OrganisationManagerComponent Save handler manager', this.managerEdit);

        var _sub:any = this._organisationService.saveManager(this.managerEdit).subscribe(
            rez => {
            if (this.managerEdit.managerId > 0) {
              let idx = this.managers.findIndex(rez => rez.managerId == this.managerEdit.managerId);
              if (idx !== -1) {
                this.managers[idx] = rez;
                this.managers = this.managers.slice(0, this.managers.length); // reset to propagate changes
                console.debug('OrganisationManagerComponent manager changed', rez);
              }
            } else {
              this.managers.push(rez);
              this.managerFilter = rez.email;
              console.debug('OrganisationManagerComponent manager added', rez);
            }
            this.changed = false;
            this.selectedManager = rez;
            this.managerEdit = null;
            this.viewMode = OrganisationManagerComponent.MANAGERS;
            _sub.unsubscribe();
          }
        );
      }

    }

  }

  protected onDiscardEventHandler() {
    console.debug('OrganisationManagerComponent discard handler');
    if (this.viewMode === OrganisationManagerComponent.MANAGER) {
      if (this.selectedManager != null) {
        this.onRowEditSelected();
      } else {
        this.onRowNew();
      }
    }
  }

  protected onDeleteConfirmationResult(modalresult: ModalResult) {
    console.debug('OrganisationManagerComponent onDeleteConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {

      if (this.selectedManager != null) {
        console.debug('OrganisationManagerComponent onDeleteConfirmationResult', this.selectedManager);

        var _sub:any = this._organisationService.removeManager(this.selectedManager.email).subscribe(res => {
          _sub.unsubscribe();
          console.debug('OrganisationManagerComponent removeManager', this.selectedManager);
          let idx = this.managers.indexOf(this.selectedManager);
          this.managers.splice(idx, 1);
          this.managers = this.managers.slice(0, this.managers.length); // reset to propagate changes
          this.selectedManager = null;
          this.managerEdit = null;
        });
      }
    }
  }

  protected onRowEnableSelected() {
    if (this.selectedManager != null) {
      this.deleteValue = this.selectedManager.email;
      this.disableConfirmationModalDialog.show();
    }
  }


  protected onDisableConfirmationResult(modalresult: ModalResult) {
    console.debug('OrganisationManagerComponent onDisableConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {

      if (this.selectedManager != null) {
        var _sub:any = this._organisationService.updateDisabledFlag(this.selectedManager.email, this.selectedManager.enabled).subscribe( done => {
          console.debug('OrganisationManagerComponent updateDisabledFlag', done);
          this.selectedManager.enabled = !this.selectedManager.enabled;
          this.changed = false;
          this.validForSave = false;
          _sub.unsubscribe();
        });
      }
    }
  }


  protected onRowResetSelected() {
    if (this.selectedManager != null) {
      this.deleteValue = this.selectedManager.email;
      this.resetConfirmationModalDialog.show();
    }
  }


  protected onResetConfirmationResult(modalresult: ModalResult) {
    console.debug('OrganisationManagerComponent onResetConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {

      if (this.selectedManager != null) {
        var _sub:any = this._organisationService.resetPassword(this.selectedManager.email).subscribe( done => {
          console.debug('OrganisationManagerComponent resetPassword', done);
          this.changed = false;
          this.validForSave = false;
          _sub.unsubscribe();
        });
      }
    }
  }


}
