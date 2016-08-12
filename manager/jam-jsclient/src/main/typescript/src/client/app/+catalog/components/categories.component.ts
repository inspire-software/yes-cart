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
import {HTTP_PROVIDERS}    from '@angular/http';
import {CategoryVO} from './../../shared/model/index';
import {PaginationComponent} from './../../shared/pagination/index';
import {Config} from './../../shared/config/env.config';

@Component({
  selector: 'yc-categories',
  moduleId: module.id,
  templateUrl: 'categories.component.html',
  directives: [NgIf, PaginationComponent],
})

export class CategoriesComponent implements OnInit, OnDestroy {

  _categories:Array<CategoryVO> = [];

  filteredCategories:Array<CategoryVO>;

  @Input() selectedCategory:CategoryVO;

  @Output() dataSelected: EventEmitter<CategoryVO> = new EventEmitter<CategoryVO>();

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
    console.debug('CategoriesComponent constructed');
  }

  ngOnInit() {
    console.debug('CategoriesComponent ngOnInit');
  }

  @Input()
  set categories(categories:Array<CategoryVO>) {
    this._categories = categories;
    this.filterCategories();
  }

  private filterCategories() {

    this.filteredCategories = this._categories;
    console.debug('CategoriesComponent filterCategories', this.filteredCategories);

    if (this.filteredCategories === null) {
      this.filteredCategories = [];
    }

    let _total = this.filteredCategories.length;
    this.totalItems = _total;
    if (_total > 0) {
      this.resetLastPageEnd();
    }
  }

  ngOnDestroy() {
    console.debug('CategoriesComponent ngOnDestroy');
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
    console.debug('CategoriesComponent onSelectRow handler', row);
    if (row == this.selectedCategory) {
      this.selectedCategory = null;
    } else {
      this.selectedCategory = row;
    }
    this.dataSelected.emit(this.selectedCategory);
  }

  protected getUri(row:CategoryVO) {
    if (row.uri) {
      return '<i  title="' + row.uri + '" class="fa fa-link"></i>';
    }
    return '';
  }

  protected getNavFlags(row:CategoryVO) {

    let flags = '';
    if (row.navigationByAttributes) {
      flags += '<i title="' + (row.productTypeName ? row.productTypeName : '') + '" class="fa fa-list-alt"></i>&nbsp;';
    }
    if (row.navigationByBrand) {
      flags += '<i class="fa fa-trademark"></i>&nbsp;';
    }
    if (row.navigationByPrice) {
      flags += '<i class="fa fa-dollar"></i>&nbsp;';
    }
    return flags;
  }


}
