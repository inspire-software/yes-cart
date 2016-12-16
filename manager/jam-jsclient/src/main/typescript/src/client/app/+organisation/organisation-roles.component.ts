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
import { Component, OnInit, OnDestroy, ViewChild } from '@angular/core';
import { OrganisationService, Util } from './../shared/services/index';
import { ModalComponent, ModalResult, ModalAction } from './../shared/modal/index';
import { RoleVO } from './../shared/model/index';
import { FormValidationEvent } from './../shared/event/index';
import { LogUtil } from './../shared/log/index';

@Component({
  selector: 'yc-organisation-roles',
  moduleId: module.id,
  templateUrl: 'organisation-roles.component.html',
})

export class OrganisationRoleComponent implements OnInit, OnDestroy {

  private static ROLES:string = 'roles';
  private static ROLE:string = 'role';

  private viewMode:string = OrganisationRoleComponent.ROLES;

  private roles:Array<RoleVO> = [];
  private roleFilter:string;

  private selectedRole:RoleVO;

  private roleEdit:RoleVO;

  @ViewChild('deleteConfirmationModalDialog')
  private deleteConfirmationModalDialog:ModalComponent;

  private deleteValue:String;

  private loading:boolean = false;

  private changed:boolean = false;
  private validForSave:boolean = false;

  constructor(private _roleService:OrganisationService) {
    LogUtil.debug('OrganisationRoleComponent constructed');
  }

  newRoleInstance():RoleVO {
    return { roleId: 0, code: '', description: null};
  }

  ngOnInit() {
    LogUtil.debug('OrganisationRoleComponent ngOnInit');
    this.onRefreshHandler();
  }

  ngOnDestroy() {
    LogUtil.debug('OrganisationRoleComponent ngOnDestroy');
  }

  protected onRefreshHandler() {
    LogUtil.debug('OrganisationRoleComponent refresh handler');
    this.getAllRoles();
  }

  protected onRoleSelected(data:RoleVO) {
    LogUtil.debug('OrganisationRoleComponent onRoleSelected', data);
    this.selectedRole = data;
  }

  protected onRoleChanged(event:FormValidationEvent<RoleVO>) {
    LogUtil.debug('OrganisationRoleComponent onRoleChanged', event);
    this.changed = true;
    this.validForSave = event.valid;
    this.roleEdit = event.source;
  }

  protected onBackToList() {
    LogUtil.debug('OrganisationRoleComponent onBackToList handler');
    if (this.viewMode === OrganisationRoleComponent.ROLE) {
      //this.roleEdit = null;
      this.changed = false;
      this.validForSave = false;
      this.viewMode = OrganisationRoleComponent.ROLES;
    }
  }

  protected onRowNew() {
    LogUtil.debug('OrganisationRoleComponent onRowNew handler');
    this.changed = false;
    this.validForSave = false;
    if (this.viewMode === OrganisationRoleComponent.ROLES) {
      this.roleEdit = this.newRoleInstance();
      this.viewMode = OrganisationRoleComponent.ROLE;
    }
  }

  protected onRowDelete(row:any) {
    LogUtil.debug('OrganisationRoleComponent onRowDelete handler', row);
    this.deleteValue = row.code;
    this.deleteConfirmationModalDialog.show();
  }

  protected onRowDeleteSelected() {
    if (this.selectedRole != null) {
      this.onRowDelete(this.selectedRole);
    }
  }

  protected onRowEditRole(row:RoleVO) {
    LogUtil.debug('OrganisationRoleComponent onRowEditRole handler', row);
    this.roleEdit = Util.clone(row);
    this.changed = false;
    this.validForSave = false;
    this.viewMode = OrganisationRoleComponent.ROLE;
  }

  protected onRowEditSelected() {
    if (this.selectedRole != null) {
      this.onRowEditRole(this.selectedRole);
    }
  }

  protected onSaveHandler() {

    if (this.validForSave && this.changed) {

      if (this.roleEdit != null) {

        LogUtil.debug('OrganisationRoleComponent Save handler role', this.roleEdit);

        var _sub:any = this._roleService.saveRole(this.roleEdit).subscribe(
            rez => {
            if (this.roleEdit.roleId > 0) {
              let idx = this.roles.findIndex(rez => rez.roleId == this.roleEdit.roleId);
              if (idx !== -1) {
                this.roles[idx] = rez;
                this.roles = this.roles.slice(0, this.roles.length); // reset to propagate changes
                LogUtil.debug('OrganisationRoleComponent role changed', rez);
              }
            } else {
              this.roles.push(rez);
              this.roleFilter = rez.code;
              LogUtil.debug('OrganisationRoleComponent role added', rez);
            }
            this.changed = false;
            this.validForSave = false;
            this.selectedRole = rez;
            this.roleEdit = rez;
            this.viewMode = OrganisationRoleComponent.ROLES;
            _sub.unsubscribe();
          }
        );
      }

    }

  }

  protected onDiscardEventHandler() {
    LogUtil.debug('OrganisationRoleComponent discard handler');
    if (this.viewMode === OrganisationRoleComponent.ROLE) {
      if (this.selectedRole != null) {
        this.onRowEditSelected();
      } else {
        this.onRowNew();
      }
    }
  }

  protected onDeleteConfirmationResult(modalresult: ModalResult) {
    LogUtil.debug('OrganisationRoleComponent onDeleteConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {

      if (this.selectedRole != null) {
        LogUtil.debug('OrganisationRoleComponent onDeleteConfirmationResult', this.selectedRole);

        var _sub:any = this._roleService.removeRole(this.selectedRole).subscribe(res => {
          _sub.unsubscribe();
          LogUtil.debug('OrganisationRoleComponent removeRole', this.selectedRole);
          let idx = this.roles.indexOf(this.selectedRole);
          this.roles.splice(idx, 1);
          this.roles = this.roles.slice(0, this.roles.length); // reset to propagate changes
          this.selectedRole = null;
          this.changed = false;
          this.validForSave = false;
          //this.roleEdit = null;
          this.viewMode = OrganisationRoleComponent.ROLES;
        });
      }
    }
  }

  protected onClearFilter() {

    this.roleFilter = '';

  }


  private getAllRoles() {
    this.loading = true;
    var _sub:any = this._roleService.getAllRoles().subscribe( allroles => {
      LogUtil.debug('OrganisationRoleComponent getAllRoles', allroles);
      this.roles = allroles;
      this.selectedRole = null;
      //this.roleEdit = null;
      this.viewMode = OrganisationRoleComponent.ROLES;
      this.changed = false;
      this.validForSave = false;
      this.loading = false;
      _sub.unsubscribe();
    });
  }

}
