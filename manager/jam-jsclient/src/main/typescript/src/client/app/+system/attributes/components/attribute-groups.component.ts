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
import {AttributeGroupVO} from './../../../shared/model/index';
import {PaginationComponent} from './../../../shared/pagination/index';
import {Futures, Future} from './../../../shared/event/index';
import {Config} from './../../../shared/config/env.config';


@Component({
  selector: 'yc-attribute-groups',
  moduleId: module.id,
  templateUrl: 'attribute-groups.component.html',
  directives: [NgIf, PaginationComponent],
})

export class AttributeGroupsComponent implements OnInit, OnDestroy {

  _groups:Array<AttributeGroupVO> = [];
  _filter:string;
  delayedFiltering:Future;
  delayedFilteringMs:number = Config.UI_INPUT_DELAY;

  filteredGroups:Array<AttributeGroupVO>;

  @Input() selectedGroup:AttributeGroupVO;

  @Output() dataSelected: EventEmitter<AttributeGroupVO> = new EventEmitter<AttributeGroupVO>();

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
    console.debug('AttributeGroupsComponent constructed');
    let that = this;
    this.delayedFiltering = Futures.perpetual(function() {
      that.filterGroups();
    }, this.delayedFilteringMs);
  }

  ngOnInit() {
    console.debug('AttributeGroupsComponent ngOnInit');
  }

  @Input()
  set groups(groups:Array<AttributeGroupVO>) {
    this._groups = groups;
    this.filterGroups();
  }

  @Input()
  set filter(filter:string) {
    this._filter = filter ? filter.toLowerCase() : null;
    this.delayedFiltering.delay();
  }

  private filterGroups() {
    if (this._filter) {
      this.filteredGroups = this._groups.filter(group =>
          group.code.toLowerCase().indexOf(this._filter) !== -1 ||
          group.name.toLowerCase().indexOf(this._filter) !== -1 ||
          group.description && group.description.toLowerCase().indexOf(this._filter) !== -1
      );
      console.debug('AttributeGroupsComponent filterGroups', this._filter);
    } else {
      this.filteredGroups = this._groups;
      console.debug('AttributeGroupsComponent filterGroups no filter');
    }

    if (this.filteredGroups === null) {
      this.filteredGroups = [];
    }

    let _total = this.filteredGroups.length;
    this.totalItems = _total;
    if (_total > 0) {
      this.resetLastPageEnd();
    }
  }

  ngOnDestroy() {
    console.debug('AttributeGroupsComponent ngOnDestroy');
    this.selectedGroup = null;
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

  protected onSelectRow(row:AttributeGroupVO) {
    console.debug('AttributeGroupsComponent onSelectRow handler', row);
    if (row == this.selectedGroup) {
      this.selectedGroup = null;
    } else {
      this.selectedGroup = row;
    }
    this.dataSelected.emit(this.selectedGroup);
  }

}
