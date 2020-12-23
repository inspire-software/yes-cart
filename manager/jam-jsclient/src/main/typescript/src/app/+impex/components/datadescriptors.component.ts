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
import { DataDescriptorVO, Pair } from './../../shared/model/index';
import { Futures, Future } from './../../shared/event/index';
import { Config } from './../../../environments/environment';
import { LogUtil } from './../../shared/log/index';


@Component({
  selector: 'cw-datadescriptors',
  templateUrl: 'datadescriptors.component.html',
})

export class DataDescriptorsComponent implements OnInit, OnDestroy {

  @Input() selectedDataDescriptor:DataDescriptorVO;

  @Output() dataSelected: EventEmitter<DataDescriptorVO> = new EventEmitter<DataDescriptorVO>();

  @Output() pageSelected: EventEmitter<number> = new EventEmitter<number>();

  @Output() sortSelected: EventEmitter<Pair<string, boolean>> = new EventEmitter<Pair<string, boolean>>();

  private _datadescriptors:Array<DataDescriptorVO> = [];
  private _filter:string;
  private delayedFiltering:Future;
  private delayedFilteringMs:number = Config.UI_INPUT_DELAY;

  public filteredDataDescriptors:Array<DataDescriptorVO>;

  //sorting
  public sortColumn:string = 'name';
  public sortDesc:boolean = false;

  //paging
  public maxSize:number = Config.UI_TABLE_PAGE_NUMS;
  public itemsPerPage:number = Config.UI_TABLE_PAGE_SIZE;
  public totalItems:number = 0;
  public currentPage:number = 1;
  // Must use separate variables (not currentPage) for table since that causes
  // cyclic even update and then exception https://github.com/angular/angular/issues/6005
  public pageStart:number = 0;
  public pageEnd:number = this.itemsPerPage;

  constructor() {
    LogUtil.debug('DataDescriptorsComponent constructed');
    let that = this;
    this.delayedFiltering = Futures.perpetual(function() {
      that.filterDataDescriptors();
    }, this.delayedFilteringMs);
  }

  ngOnInit() {
    LogUtil.debug('DataDescriptorsComponent ngOnInit');
  }

  @Input()
  set datadescriptors(datadescriptors:Array<DataDescriptorVO>) {
    this._datadescriptors = datadescriptors;
    this.filterDataDescriptors();
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
    LogUtil.debug('DataDescriptorsComponent ngOnDestroy');
    this.selectedDataDescriptor = null;
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
    this.filterDataDescriptors();
    this.sortSelected.emit({ first: this.sortColumn, second: this.sortDesc });
  }

  onSelectRow(row:DataDescriptorVO) {
    LogUtil.debug('DataDescriptorsComponent onSelectRow handler', row);
    if (row == this.selectedDataDescriptor) {
      this.selectedDataDescriptor = null;
    } else {
      this.selectedDataDescriptor = row;
    }
    this.dataSelected.emit(this.selectedDataDescriptor);
  }

  private filterDataDescriptors() {

    if (this._datadescriptors) {
      if (this._filter) {
        this.filteredDataDescriptors = this._datadescriptors.filter(descriptor =>
          descriptor.type.toLowerCase().indexOf(this._filter) !== -1 ||
          descriptor.name.toLowerCase().indexOf(this._filter) !== -1 ||
          descriptor.value.toLowerCase().indexOf(this._filter) !== -1
        );
      } else {
        this.filteredDataDescriptors = this._datadescriptors.slice(0, this._datadescriptors.length);
      }
    }

    if (this.filteredDataDescriptors === null) {
      this.filteredDataDescriptors = [];
    }

    let _sortProp = this.sortColumn;
    let _sortOrder = this.sortDesc ? -1 : 1;

    let _sort = function(a:any, b:any):number {
      return (a[_sortProp] > b[_sortProp] ? 1 : -1) * _sortOrder;
    };

    this.filteredDataDescriptors.sort(_sort);

    let _total = this.filteredDataDescriptors.length;
    this.totalItems = _total;
    if (_total > 0) {
      this.resetLastPageEnd();
    }
  }

}
