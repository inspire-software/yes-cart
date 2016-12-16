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
import { RoleVO } from './../../shared/model/index';
import { Futures, Future } from './../../shared/event/index';
import { Config } from './../../shared/config/env.config';
import { LogUtil } from './../../shared/log/index';


@Component({
  selector: 'yc-roles',
  moduleId: module.id,
  templateUrl: 'roles.component.html',
})

export class RolesComponent implements OnInit, OnDestroy {

  @Input() selectedRole:RoleVO;

  @Output() dataSelected: EventEmitter<RoleVO> = new EventEmitter<RoleVO>();

  private _roles:Array<RoleVO> = [];
  private _filter:string;
  private delayedFiltering:Future;
  private delayedFilteringMs:number = Config.UI_INPUT_DELAY;

  private filteredRoles:Array<RoleVO>;

  //paging
  private maxSize:number = Config.UI_TABLE_PAGE_NUMS; // tslint:disable-line:no-unused-variable
  private itemsPerPage:number = Config.UI_TABLE_PAGE_SIZE;
  private totalItems:number = 0;
  private currentPage:number = 1; // tslint:disable-line:no-unused-variable
  // Must use separate variables (not currentPage) for table since that causes
  // cyclic even update and then exception https://github.com/angular/angular/issues/6005
  private pageStart:number = 0;
  private pageEnd:number = this.itemsPerPage;

  constructor() {
    LogUtil.debug('RolesComponent constructed');
    let that = this;
    this.delayedFiltering = Futures.perpetual(function() {
      that.filterRoles();
    }, this.delayedFilteringMs);
  }

  ngOnInit() {
    LogUtil.debug('RolesComponent ngOnInit');
  }

  @Input()
  set roles(roles:Array<RoleVO>) {
    this._roles = roles;
    this.filterRoles();
  }

  @Input()
  set filter(filter:string) {
    this._filter = filter ? filter.toLowerCase() : null;
    this.delayedFiltering.delay();
  }

  ngOnDestroy() {
    LogUtil.debug('RolesComponent ngOnDestroy');
    this.selectedRole = null;
    this.dataSelected.emit(null);
  }

  resetLastPageEnd() {
    let _pageEnd = this.pageStart + this.itemsPerPage;
    if (_pageEnd > this.totalItems) {
      this.pageEnd = this.totalItems;
    } else {
      this.pageEnd = _pageEnd;
    }
  }

  onPageChanged(event:any) {
    this.pageStart = (event.page - 1) * this.itemsPerPage;
    let _pageEnd = this.pageStart + this.itemsPerPage;
    if (_pageEnd > this.totalItems) {
      this.pageEnd = this.totalItems;
    } else {
      this.pageEnd = _pageEnd;
    }
  }

  protected onSelectRow(row:RoleVO) {
    LogUtil.debug('RolesComponent onSelectRow handler', row);
    if (row == this.selectedRole) {
      this.selectedRole = null;
    } else {
      this.selectedRole = row;
    }
    this.dataSelected.emit(this.selectedRole);
  }

  private filterRoles() {
    if (this._filter) {
      this.filteredRoles = this._roles.filter(role =>
        role.code.toLowerCase().indexOf(this._filter) !== -1 ||
        role.description !== null && role.description.toLowerCase().indexOf(this._filter) !== -1
      );
      LogUtil.debug('RolesComponent filterRoles', this._filter);
    } else {
      this.filteredRoles = this._roles;
      LogUtil.debug('RolesComponent filterRoles no filter');
    }

    if (this.filteredRoles === null) {
      this.filteredRoles = [];
    }

    let _total = this.filteredRoles.length;
    this.totalItems = _total;
    if (_total > 0) {
      this.resetLastPageEnd();
    }
  }

}
