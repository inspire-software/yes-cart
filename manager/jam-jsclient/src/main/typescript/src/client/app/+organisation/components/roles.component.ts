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
import {HTTP_PROVIDERS}    from '@angular/http';
import {RoleVO} from './../../shared/model/index';
import {PaginationComponent} from './../../shared/pagination/index';
import {Futures, Future} from './../../shared/event/index';
import {Config} from './../../shared/config/env.config';


@Component({
  selector: 'yc-roles',
  moduleId: module.id,
  templateUrl: 'roles.component.html',
  directives: [NgIf, PaginationComponent],
})

export class RolesComponent implements OnInit, OnDestroy {

  _roles:Array<RoleVO> = [];
  _filter:string;
  delayedFiltering:Future;
  delayedFilteringMs:number = Config.UI_INPUT_DELAY;

  filteredRoles:Array<RoleVO>;

  @Input() selectedRole:RoleVO;

  @Output() dataSelected: EventEmitter<RoleVO> = new EventEmitter<RoleVO>();

  //paging
  maxSize:number = Config.UI_TABLE_PAGE_NUMS;
  itemsPerPage:number = Config.UI_TABLE_PAGE_SIZE;
  totalItems:number = 0;
  currentPage:number = 1;
  // Must use separate variables (not currentPage) for table since that causes
  // cyclic even update and then exception https://github.com/angular/angular/issues/6005
  pageStart:number = 0;
  pageEnd:number = this.itemsPerPage;

  constructor() {
    console.debug('RolesComponent constructed');
    let that = this;
    this.delayedFiltering = Futures.perpetual(function() {
      that.filterRoles();
    }, this.delayedFilteringMs);
  }

  ngOnInit() {
    console.debug('RolesComponent ngOnInit');
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

  private filterRoles() {
    if (this._filter) {
      this.filteredRoles = this._roles.filter(role =>
        role.code.toLowerCase().indexOf(this._filter) !== -1 ||
        role.description.toLowerCase().indexOf(this._filter) !== -1
      );
      console.debug('RolesComponent filterRoles', this._filter);
    } else {
      this.filteredRoles = this._roles;
      console.debug('RolesComponent filterRoles no filter');
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

  ngOnDestroy() {
    console.debug('RolesComponent ngOnDestroy');
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
    console.debug('RolesComponent onSelectRow handler', row);
    if (row == this.selectedRole) {
      this.selectedRole = null;
    } else {
      this.selectedRole = row;
    }
    this.dataSelected.emit(this.selectedRole);
  }

}
