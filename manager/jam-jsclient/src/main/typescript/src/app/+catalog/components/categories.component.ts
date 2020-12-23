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
import { CategoryVO, Pair, SearchResultVO } from './../../shared/model/index';
import { Config } from './../../../environments/environment';
import { LogUtil } from './../../shared/log/index';

@Component({
  selector: 'cw-categories',
  templateUrl: 'categories.component.html',
})

export class CategoriesComponent implements OnInit, OnDestroy {

  @Input() selectedCategory:CategoryVO;

  @Output() dataSelected: EventEmitter<CategoryVO> = new EventEmitter<CategoryVO>();

  @Output() pageSelected: EventEmitter<number> = new EventEmitter<number>();

  @Output() sortSelected: EventEmitter<Pair<string, boolean>> = new EventEmitter<Pair<string, boolean>>();

  private _categories:SearchResultVO<CategoryVO> = null;

  public filteredCategories:Array<CategoryVO>;

  //sorting
  public sortColumn:string = null;
  public sortDesc:boolean = false;

  //paging
  public maxSize:number = Config.UI_TABLE_PAGE_NUMS;
  public itemsPerPage:number = Config.UI_TABLE_PAGE_SIZE;
  public totalItems:number = 0;
  public currentPage:number = 1;

  constructor() {
    LogUtil.debug('CategoriesComponent constructed');
  }

  ngOnInit() {
    LogUtil.debug('CategoriesComponent ngOnInit');
  }

  @Input()
  set categories(categories:SearchResultVO<CategoryVO>) {
    this._categories = categories;
    this.filterCategories();
  }

  ngOnDestroy() {
    LogUtil.debug('CategoriesComponent ngOnDestroy');
    this.selectedCategory = null;
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

  onSelectRow(row:CategoryVO) {
    LogUtil.debug('CategoriesComponent onSelectRow handler', row);
    if (row == this.selectedCategory) {
      this.selectedCategory = null;
    } else {
      this.selectedCategory = row;
    }
    this.dataSelected.emit(this.selectedCategory);
  }

  getUri(row:CategoryVO) {
    if (row.uri) {
      return '<i  title="' + row.uri + '" class="fa fa-globe"></i>';
    }
    return '';
  }

  getLink(row:CategoryVO) {
    if (row.linkToId > 0) {
      return '<i  title="' + row.linkToName + '" class="fa fa-link"></i>';
    }
    return '';
  }

  getNavFlags(row:CategoryVO) {

    let flags = '';
    if (row.navigationByPrice) {
      flags += '<i class="fa fa-dollar"></i>&nbsp;';
    }
    if (row.navigationByAttributes) {
      flags += '<i class="fa fa-list-alt"></i>&nbsp;' + (row.productTypeName ? row.productTypeName : '');
    }
    return flags;
  }

  isAvailableFromNow(row:CategoryVO) {
    return row.availablefrom === null || (row.availablefrom < new Date());
  }

  isAvailableToNow(row:CategoryVO) {
    return row.availableto === null || (row.availableto > new Date());
  }

  private filterCategories() {

    LogUtil.debug('CategoriesComponent filterCategories', this.filteredCategories);

    if (this._categories != null) {

      this.filteredCategories = this._categories.items != null ? this._categories.items : [];
      this.maxSize = Config.UI_TABLE_PAGE_NUMS;
      this.itemsPerPage = this._categories.searchContext.size;
      this.totalItems = this._categories.total;
      this.currentPage = this._categories.searchContext.start + 1;
      this.sortColumn = this._categories.searchContext.sortBy;
      this.sortDesc = this._categories.searchContext.sortDesc;
    } else {
      this.filteredCategories = [];
      this.maxSize = Config.UI_TABLE_PAGE_NUMS;
      this.itemsPerPage = Config.UI_TABLE_PAGE_SIZE;
      this.totalItems = 0;
      this.currentPage = 1;
      this.sortColumn = null;
      this.sortDesc = false;
    }

  }

}
