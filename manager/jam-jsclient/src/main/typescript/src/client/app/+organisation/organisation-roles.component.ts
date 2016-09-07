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
import {HTTP_PROVIDERS}    from '@angular/http';
import {OrganisationService, Util} from './../shared/services/index';
import {TAB_DIRECTIVES} from 'ng2-bootstrap/ng2-bootstrap';
import {RolesComponent, RoleComponent} from './components/index';
import {DataControlComponent} from './../shared/sidebar/index';
import {ModalComponent, ModalResult, ModalAction} from './../shared/modal/index';
import {RoleVO} from './../shared/model/index';
import {FormValidationEvent, Futures, Future} from './../shared/event/index';
import {Config} from './../shared/config/env.config';

@Component({
  selector: 'yc-organisation-roles',
  moduleId: module.id,
  templateUrl: 'organisation-roles.component.html',
  directives: [TAB_DIRECTIVES, NgIf, RolesComponent, RoleComponent, ModalComponent, DataControlComponent ],
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
  deleteConfirmationModalDialog:ModalComponent;

  private deleteValue:String;

  constructor(private _roleService:OrganisationService) {
    console.debug('OrganisationRoleComponent constructed');
  }

  changed:boolean = false;
  validForSave:boolean = false;

  newRoleInstance():RoleVO {
    return { roleId: 0, code: '', description: null};
  }

  ngOnInit() {
    console.debug('OrganisationRoleComponent ngOnInit');
    this.onRefreshHandler();
  }

  ngOnDestroy() {
    console.debug('OrganisationRoleComponent ngOnDestroy');
  }

  getAllRoles() {
    var _sub:any = this._roleService.getAllRoles().subscribe( allroles => {
      console.debug('OrganisationRoleComponent getAllRoles', allroles);
      this.roles = allroles;
      this.selectedRole = null;
      this.roleEdit = null;
      this.viewMode = OrganisationRoleComponent.ROLES;
      this.changed = false;
      this.validForSave = false;
      _sub.unsubscribe();
    });
  }

  protected onRefreshHandler() {
    console.debug('OrganisationRoleComponent refresh handler');
    this.getAllRoles();
  }

  onRoleSelected(data:RoleVO) {
    console.debug('OrganisationRoleComponent onRoleSelected', data);
    this.selectedRole = data;
  }

  onRoleChanged(event:FormValidationEvent<RoleVO>) {
    console.debug('OrganisationRoleComponent onRoleChanged', event);
    this.changed = true;
    this.validForSave = event.valid;
    this.roleEdit = event.source;
  }

  protected onBackToList() {
    console.debug('OrganisationRoleComponent onBackToList handler');
    if (this.viewMode === OrganisationRoleComponent.ROLE) {
      this.roleEdit = null;
      this.viewMode = OrganisationRoleComponent.ROLES;
    }
  }

  protected onRowNew() {
    console.debug('OrganisationRoleComponent onRowNew handler');
    this.changed = false;
    this.validForSave = false;
    if (this.viewMode === OrganisationRoleComponent.ROLES) {
      this.roleEdit = this.newRoleInstance();
      this.viewMode = OrganisationRoleComponent.ROLE;
    }
  }

  protected onRowDelete(row:any) {
    console.debug('OrganisationRoleComponent onRowDelete handler', row);
    this.deleteValue = row.code;
    this.deleteConfirmationModalDialog.show();
  }

  protected onRowDeleteSelected() {
    if (this.selectedRole != null) {
      this.onRowDelete(this.selectedRole);
    }
  }

  protected onRowEditRole(row:RoleVO) {
    console.debug('OrganisationRoleComponent onRowEditRole handler', row);
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

        console.debug('OrganisationRoleComponent Save handler role', this.roleEdit);

        var _sub:any = this._roleService.saveRole(this.roleEdit).subscribe(
            rez => {
            if (this.roleEdit.roleId > 0) {
              let idx = this.roles.findIndex(rez => rez.roleId == this.roleEdit.roleId);
              if (idx !== -1) {
                this.roles[idx] = rez;
                this.roles = this.roles.slice(0, this.roles.length); // reset to propagate changes
                console.debug('OrganisationRoleComponent role changed', rez);
              }
            } else {
              this.roles.push(rez);
              this.roleFilter = rez.code;
              console.debug('OrganisationRoleComponent role added', rez);
            }
            this.changed = false;
            this.selectedRole = rez;
            this.roleEdit = null;
            this.viewMode = OrganisationRoleComponent.ROLES;
            _sub.unsubscribe();
          }
        );
      }

    }

  }

  protected onDiscardEventHandler() {
    console.debug('OrganisationRoleComponent discard handler');
    if (this.viewMode === OrganisationRoleComponent.ROLE) {
      if (this.selectedRole != null) {
        this.onRowEditSelected();
      } else {
        this.onRowNew();
      }
    }
  }

  protected onDeleteConfirmationResult(modalresult: ModalResult) {
    console.debug('OrganisationRoleComponent onDeleteConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {

      if (this.selectedRole != null) {
        console.debug('OrganisationRoleComponent onDeleteConfirmationResult', this.selectedRole);

        var _sub:any = this._roleService.removeRole(this.selectedRole).subscribe(res => {
          _sub.unsubscribe();
          console.debug('OrganisationRoleComponent removeRole', this.selectedRole);
          let idx = this.roles.indexOf(this.selectedRole);
          this.roles.splice(idx, 1);
          this.roles = this.roles.slice(0, this.roles.length); // reset to propagate changes
          this.selectedRole = null;
          this.roleEdit = null;
        });
      }
    }
  }

}
