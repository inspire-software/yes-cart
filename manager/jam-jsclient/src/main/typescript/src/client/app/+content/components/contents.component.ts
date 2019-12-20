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
import { ContentVO, Pair, SearchResultVO } from './../../shared/model/index';
import { Config } from './../../shared/config/env.config';
import { LogUtil } from './../../shared/log/index';

@Component({
  selector: 'yc-contents',
  moduleId: module.id,
  templateUrl: 'contents.component.html',
})

export class ContentsComponent implements OnInit, OnDestroy {

  @Input() selectedContent:ContentVO;

  @Output() dataSelected: EventEmitter<ContentVO> = new EventEmitter<ContentVO>();

  @Output() pageSelected: EventEmitter<number> = new EventEmitter<number>();

  @Output() sortSelected: EventEmitter<Pair<string, boolean>> = new EventEmitter<Pair<string, boolean>>();

  private _contents:SearchResultVO<ContentVO> = null;

  private filteredContents:Array<ContentVO>;

  //sorting
  private sortColumn:string = null;
  private sortDesc:boolean = false;

  //paging
  private maxSize:number = Config.UI_TABLE_PAGE_NUMS;
  private itemsPerPage:number = Config.UI_TABLE_PAGE_SIZE;
  private totalItems:number = 0;
  private currentPage:number = 1;

  constructor() {
    LogUtil.debug('ContentsComponent constructed');
  }

  ngOnInit() {
    LogUtil.debug('ContentsComponent ngOnInit');
  }

  @Input()
  set contents(contents:SearchResultVO<ContentVO>) {
    this._contents = contents;
    this.filterContents();
  }

  ngOnDestroy() {
    LogUtil.debug('ContentsComponent ngOnDestroy');
    this.selectedContent = null;
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

  protected onSelectRow(row:ContentVO) {
    LogUtil.debug('ContentsComponent onSelectRow handler', row);
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

  private filterContents() {

    LogUtil.debug('ContentsComponent filterContents', this.filteredContents);

    if (this._contents != null) {

      this.filteredContents = this._contents.items != null ? this._contents.items : [];
      this.maxSize = Config.UI_TABLE_PAGE_NUMS;
      this.itemsPerPage = this._contents.searchContext.size;
      this.totalItems = this._contents.total;
      this.currentPage = this._contents.searchContext.start + 1;
      this.sortColumn = this._contents.searchContext.sortBy;
      this.sortDesc = this._contents.searchContext.sortDesc;
    } else {
      this.filteredContents = [];
      this.maxSize = Config.UI_TABLE_PAGE_NUMS;
      this.itemsPerPage = Config.UI_TABLE_PAGE_SIZE;
      this.totalItems = 0;
      this.currentPage = 1;
      this.sortColumn = null;
      this.sortDesc = false;
    }

  }

}
