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
import { CategoryVO } from './../../shared/model/index';
import { Config } from './../../shared/config/env.config';
import { LogUtil } from './../../shared/log/index';

@Component({
  selector: 'yc-categories',
  moduleId: module.id,
  templateUrl: 'categories.component.html',
})

export class CategoriesComponent implements OnInit, OnDestroy {

  @Input() selectedCategory:CategoryVO;

  @Output() dataSelected: EventEmitter<CategoryVO> = new EventEmitter<CategoryVO>();

  private _categories:Array<CategoryVO> = [];

  private filteredCategories:Array<CategoryVO>;

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
    LogUtil.debug('CategoriesComponent constructed');
  }

  ngOnInit() {
    LogUtil.debug('CategoriesComponent ngOnInit');
  }

  @Input()
  set categories(categories:Array<CategoryVO>) {
    this._categories = categories;
    this.filterCategories();
  }

  ngOnDestroy() {
    LogUtil.debug('CategoriesComponent ngOnDestroy');
    this.selectedCategory = null;
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

  protected onSelectRow(row:CategoryVO) {
    LogUtil.debug('CategoriesComponent onSelectRow handler', row);
    if (row == this.selectedCategory) {
      this.selectedCategory = null;
    } else {
      this.selectedCategory = row;
    }
    this.dataSelected.emit(this.selectedCategory);
  }

  protected getUri(row:CategoryVO) {
    if (row.uri) {
      return '<i  title="' + row.uri + '" class="fa fa-globe"></i>';
    }
    return '';
  }

  protected getLink(row:CategoryVO) {
    if (row.linkToId > 0) {
      return '<i  title="' + row.linkToName + '" class="fa fa-link"></i>';
    }
    return '';
  }

  protected getNavFlags(row:CategoryVO) {

    let flags = '';
    if (row.navigationByAttributes) {
      flags += '<i title="' + (row.productTypeName ? row.productTypeName : '') + '" class="fa fa-list-alt"></i>&nbsp;';
    }
    if (row.navigationByBrand) {
      flags += '<i class="fa fa-copyright"></i>&nbsp;';
    }
    if (row.navigationByPrice) {
      flags += '<i class="fa fa-dollar"></i>&nbsp;';
    }
    return flags;
  }

  protected isAvailableFromNow(row:CategoryVO) {
    return row.availablefrom === null || (row.availablefrom < new Date());
  }

  protected isAvailableToNow(row:CategoryVO) {
    return row.availableto === null || (row.availableto > new Date());
  }

  private filterCategories() {

    this.filteredCategories = this._categories;
    LogUtil.debug('CategoriesComponent filterCategories', this.filteredCategories);

    if (this.filteredCategories === null) {
      this.filteredCategories = [];
    }

    let _total = this.filteredCategories.length;
    this.totalItems = _total;
    if (_total > 0) {
      this.resetLastPageEnd();
    }
  }

}
