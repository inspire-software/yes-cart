/*
 * Copyright 2009 Inspire-Software.com
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
import { ManagerInfoVO, Pair, SearchResultVO } from './../../shared/model/index';
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

  @Output() pageSelected: EventEmitter<number> = new EventEmitter<number>();

  @Output() sortSelected: EventEmitter<Pair<string, boolean>> = new EventEmitter<Pair<string, boolean>>();

  private _managers:SearchResultVO<ManagerInfoVO> = null;

  private filteredManagers:Array<ManagerInfoVO>;

  //sorting
  private sortColumn:string = null;
  private sortDesc:boolean = false;

  //paging
  private maxSize:number = Config.UI_TABLE_PAGE_NUMS;
  private itemsPerPage:number = Config.UI_TABLE_PAGE_SIZE;
  private totalItems:number = 0;
  private currentPage:number = 1;

  constructor() {
    LogUtil.debug('ManagersComponent constructed');
  }

  ngOnInit() {
    LogUtil.debug('ManagersComponent ngOnInit');
  }

  @Input()
  set managers(managers:SearchResultVO<ManagerInfoVO>) {
    this._managers = managers;
    this.filterManagers();
  }

  ngOnDestroy() {
    LogUtil.debug('ManagersComponent ngOnDestroy');
    this.selectedManager = null;
    this.dataSelected.emit(null);
  }

  onPageChanged(event:any) {
    if (this.currentPage != event.page) {
      this.pageSelected.emit(event.page - 1);
    }
  }

  onSortClick(event:any) {
    if (event == this.sortColumn) {
      if (this.sortDesc) {  // same column already desc, remove sort
        this.sortSelected.emit(null);
      } else {  // same column asc, change to desc
        this.sortSelected.emit({ first: event, second: true });
      }
    } else { // different column, start asc sort
      this.sortSelected.emit({ first: event, second: false });
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

    LogUtil.debug('ManagersComponent filterManagers', this.filteredManagers);

    if (this._managers != null) {

      this.filteredManagers = this._managers.items != null ? this._managers.items : [];
      this.maxSize = Config.UI_TABLE_PAGE_NUMS;
      this.itemsPerPage = this._managers.searchContext.size;
      this.totalItems = this._managers.total;
      this.currentPage = this._managers.searchContext.start + 1;
      this.sortColumn = this._managers.searchContext.sortBy;
      this.sortDesc = this._managers.searchContext.sortDesc;
    } else {
      this.filteredManagers = [];
      this.maxSize = Config.UI_TABLE_PAGE_NUMS;
      this.itemsPerPage = Config.UI_TABLE_PAGE_SIZE;
      this.totalItems = 0;
      this.currentPage = 1;
      this.sortColumn = null;
      this.sortDesc = false;
    }

  }

}
