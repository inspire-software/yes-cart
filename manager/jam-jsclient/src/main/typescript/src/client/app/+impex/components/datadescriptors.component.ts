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
import { DataDescriptorVO } from './../../shared/model/index';
import { Futures, Future } from './../../shared/event/index';
import { Config } from './../../shared/config/env.config';
import { LogUtil } from './../../shared/log/index';


@Component({
  selector: 'yc-datadescriptors',
  moduleId: module.id,
  templateUrl: 'datadescriptors.component.html',
})

export class DataDescriptorsComponent implements OnInit, OnDestroy {

  @Input() selectedDataDescriptor:DataDescriptorVO;

  @Output() dataSelected: EventEmitter<DataDescriptorVO> = new EventEmitter<DataDescriptorVO>();

  private _datadescriptors:Array<DataDescriptorVO> = [];
  private _filter:string;
  private delayedFiltering:Future;
  private delayedFilteringMs:number = Config.UI_INPUT_DELAY;

  private filteredDataDescriptors:Array<DataDescriptorVO>;

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
    this.pageStart = (event.page - 1) * this.itemsPerPage;
    let _pageEnd = this.pageStart + this.itemsPerPage;
    if (_pageEnd > this.totalItems) {
      this.pageEnd = this.totalItems;
    } else {
      this.pageEnd = _pageEnd;
    }
  }

  protected onSelectRow(row:DataDescriptorVO) {
    LogUtil.debug('DataDescriptorsComponent onSelectRow handler', row);
    if (row == this.selectedDataDescriptor) {
      this.selectedDataDescriptor = null;
    } else {
      this.selectedDataDescriptor = row;
    }
    this.dataSelected.emit(this.selectedDataDescriptor);
  }


  private filterDataDescriptors() {
    if (this._filter) {
      this.filteredDataDescriptors = this._datadescriptors.filter(descriptor =>
        descriptor.type.toLowerCase().indexOf(this._filter) !== -1 ||
        descriptor.name.toLowerCase().indexOf(this._filter) !== -1 ||
        descriptor.value.toLowerCase().indexOf(this._filter) !== -1
      );
      LogUtil.debug('DataDescriptorsComponent filterDataDescriptors', this._filter);
    } else {
      this.filteredDataDescriptors = this._datadescriptors;
      LogUtil.debug('DataDescriptorsComponent filterDataDescriptors no filter');
    }

    if (this.filteredDataDescriptors === null) {
      this.filteredDataDescriptors = [];
    }

    let _total = this.filteredDataDescriptors.length;
    this.totalItems = _total;
    if (_total > 0) {
      this.resetLastPageEnd();
    }
  }


}
