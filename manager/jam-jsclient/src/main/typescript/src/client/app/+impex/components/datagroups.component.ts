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
import { DataGroupVO, Pair } from './../../shared/model/index';
import { Futures, Future } from './../../shared/event/index';
import { Config } from './../../shared/config/env.config';
import { LogUtil } from './../../shared/log/index';


@Component({
  selector: 'yc-datagroups',
  moduleId: module.id,
  templateUrl: 'datagroups.component.html',
})

export class DataGroupsComponent implements OnInit, OnDestroy {

  @Input() selectedDataGroup:DataGroupVO;

  @Output() dataSelected: EventEmitter<DataGroupVO> = new EventEmitter<DataGroupVO>();

  @Output() pageSelected: EventEmitter<number> = new EventEmitter<number>();

  @Output() sortSelected: EventEmitter<Pair<string, boolean>> = new EventEmitter<Pair<string, boolean>>();

  private _datagroups:Array<DataGroupVO> = [];
  private _filter:string;
  private delayedFiltering:Future;
  private delayedFilteringMs:number = Config.UI_INPUT_DELAY;

  private filteredDataGroups:Array<DataGroupVO>;

  //sorting
  private sortColumn:string = 'name';
  private sortDesc:boolean = false;

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
    LogUtil.debug('DataGroupsComponent constructed');
    let that = this;
    this.delayedFiltering = Futures.perpetual(function() {
      that.filterDataGroups();
    }, this.delayedFilteringMs);
  }

  ngOnInit() {
    LogUtil.debug('DataGroupsComponent ngOnInit');
  }

  @Input()
  set datagroups(datagroups:Array<DataGroupVO>) {
    this._datagroups = datagroups;
    this.filterDataGroups();
  }

  @Input()
  set filter(filter:string) {
    this._filter = filter ? filter.toLowerCase() : null;
    this.delayedFiltering.delay();
  }

  @Input()
  set sortorder(sort:Pair<string, boolean>) {
    if (sort != null && (sort.first !== this.sortColumn || sort.second !== this.sortDesc)) {
      this.sortColumn = sort.first;
      this.sortDesc = sort.second;
      this.delayedFiltering.delay();
    }
  }

  ngOnDestroy() {
    LogUtil.debug('DataGroupsComponent ngOnDestroy');
    this.selectedDataGroup = null;
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
    if (this.currentPage != event.page) {
      this.pageSelected.emit(event.page - 1);
    }
    this.pageStart = (event.page - 1) * this.itemsPerPage;
    let _pageEnd = this.pageStart + this.itemsPerPage;
    if (_pageEnd > this.totalItems) {
      this.pageEnd = this.totalItems;
    } else {
      this.pageEnd = _pageEnd;
    }
  }

  onSortClick(event:any) {
    if (event == this.sortColumn) {
      if (this.sortDesc) {  // same column already desc, remove sort
        this.sortColumn = 'name';
        this.sortDesc = false;
      } else {  // same column asc, change to desc
        this.sortColumn = event;
        this.sortDesc = true;
      }
    } else { // different column, start asc sort
      this.sortColumn = event;
      this.sortDesc = false;
    }
    this.filterDataGroups();
    this.sortSelected.emit({ first: this.sortColumn, second: this.sortDesc });
  }

  protected onSelectRow(row:DataGroupVO) {
    LogUtil.debug('DataGroupsComponent onSelectRow handler', row);
    if (row == this.selectedDataGroup) {
      this.selectedDataGroup = null;
    } else {
      this.selectedDataGroup = row;
    }
    this.dataSelected.emit(this.selectedDataGroup);
  }

  private filterDataGroups() {

    if (this._datagroups) {
      if (this._filter) {
        this.filteredDataGroups = this._datagroups.filter(group =>
          group.type.toLowerCase().indexOf(this._filter) !== -1 ||
          group.name.toLowerCase().indexOf(this._filter) !== -1 ||
          group.descriptors.toLowerCase().indexOf(this._filter) !== -1 ||
          group.qualifier && group.qualifier.toLowerCase().indexOf(this._filter) !== -1
        );
      } else {
        this.filteredDataGroups = this._datagroups.slice(0, this._datagroups.length);
      }
    }

    if (this.filteredDataGroups === null) {
      this.filteredDataGroups = [];
    }

    let _sortProp = this.sortColumn;
    let _sortOrder = this.sortDesc ? -1 : 1;

    let _sort = function(a:any, b:any):number {
      return (a[_sortProp] > b[_sortProp] ? 1 : -1) * _sortOrder;
    };

    this.filteredDataGroups.sort(_sort);

    let _total = this.filteredDataGroups.length;
    this.totalItems = _total;
    if (_total > 0) {
      this.resetLastPageEnd();
    }
  }

}
