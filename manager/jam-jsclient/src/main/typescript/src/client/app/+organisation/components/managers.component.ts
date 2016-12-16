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
import { ManagerInfoVO } from './../../shared/model/index';
import { Futures, Future } from './../../shared/event/index';
import { Config } from './../../shared/config/env.config';
import { LogUtil } from './../../shared/log/index';


@Component({
  selector: 'yc-managers',
  moduleId: module.id,
  templateUrl: 'managers.component.html',
})

export class ManagersComponent implements OnInit, OnDestroy {

  @Input() selectedManager:ManagerInfoVO;

  @Output() dataSelected: EventEmitter<ManagerInfoVO> = new EventEmitter<ManagerInfoVO>();

  private _managers:Array<ManagerInfoVO> = [];
  private _filter:string;
  private delayedFiltering:Future;
  private delayedFilteringMs:number = Config.UI_INPUT_DELAY;

  private filteredManagers:Array<ManagerInfoVO>;

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
    LogUtil.debug('ManagersComponent constructed');
    let that = this;
    this.delayedFiltering = Futures.perpetual(function() {
      that.filterManagers();
    }, this.delayedFilteringMs);
  }

  ngOnInit() {
    LogUtil.debug('ManagersComponent ngOnInit');
  }

  @Input()
  set managers(managers:Array<ManagerInfoVO>) {
    this._managers = managers;
    this.filterManagers();
  }

  @Input()
  set filter(filter:string) {
    this._filter = filter ? filter.toLowerCase() : null;
    this.delayedFiltering.delay();
  }

  ngOnDestroy() {
    LogUtil.debug('ManagersComponent ngOnDestroy');
    this.selectedManager = null;
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

  protected onSelectRow(row:ManagerInfoVO) {
    LogUtil.debug('ManagersComponent onSelectRow handler', row);
    if (row == this.selectedManager) {
      this.selectedManager = null;
    } else {
      this.selectedManager = row;
    }
    this.dataSelected.emit(this.selectedManager);
  }

  private filterManagers() {
    if (this._filter) {
      this.filteredManagers = this._managers.filter(manager =>
        manager.email.toLowerCase().indexOf(this._filter) !== -1 ||
        manager.firstName !== null && manager.firstName.toLowerCase().indexOf(this._filter) !== -1 ||
        manager.lastName !== null && manager.lastName.toLowerCase().indexOf(this._filter) !== -1
      );
      LogUtil.debug('ManagersComponent filterManagers', this._filter);
    } else {
      this.filteredManagers = this._managers;
      LogUtil.debug('ManagersComponent filterManagers no filter');
    }

    if (this.filteredManagers === null) {
      this.filteredManagers = [];
    }

    let _total = this.filteredManagers.length;
    this.totalItems = _total;
    if (_total > 0) {
      this.resetLastPageEnd();
    }
  }

}
