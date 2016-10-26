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
import {ContentVO} from './../../shared/model/index';
import {PaginationComponent} from './../../shared/pagination/index';
import {Config} from './../../shared/config/env.config';

@Component({
  selector: 'yc-contents',
  moduleId: module.id,
  templateUrl: 'contents.component.html',
  directives: [NgIf, PaginationComponent],
})

export class ContentsComponent implements OnInit, OnDestroy {

  _contents:Array<ContentVO> = [];

  filteredContents:Array<ContentVO>;

  @Input() selectedContent:ContentVO;

  @Output() dataSelected: EventEmitter<ContentVO> = new EventEmitter<ContentVO>();

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
    console.debug('ContentsComponent constructed');
  }

  ngOnInit() {
    console.debug('ContentsComponent ngOnInit');
  }

  @Input()
  set contents(contents:Array<ContentVO>) {
    this._contents = contents;
    this.filterContents();
  }

  private filterContents() {

    this.filteredContents = this._contents;
    console.debug('ContentsComponent filterContents', this.filteredContents);

    if (this.filteredContents === null) {
      this.filteredContents = [];
    }

    let _total = this.filteredContents.length;
    this.totalItems = _total;
    if (_total > 0) {
      this.resetLastPageEnd();
    }
  }

  ngOnDestroy() {
    console.debug('ContentsComponent ngOnDestroy');
    this.selectedContent = null;
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

  protected onSelectRow(row:ContentVO) {
    console.debug('ContentsComponent onSelectRow handler', row);
    if (row == this.selectedContent) {
      this.selectedContent = null;
    } else {
      this.selectedContent = row;
    }
    this.dataSelected.emit(this.selectedContent);
  }

  protected getUri(row:ContentVO) {
    if (row.uri) {
      return '<i  title="' + row.uri + '" class="fa fa-globe"></i>';
    }
    return '';
  }

  protected isAvailableFromNow(row:ContentVO) {
    return row.availablefrom === null || (row.availablefrom < new Date());
  }

  protected isAvailableToNow(row:ContentVO) {
    return row.availableto === null || (row.availableto > new Date());
  }


}
