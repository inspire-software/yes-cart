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
import {TaxVO} from './../../shared/model/index';
import {PaginationComponent} from './../../shared/pagination/index';
import {Config} from './../../shared/config/env.config';

@Component({
  selector: 'yc-taxes',
  moduleId: module.id,
  templateUrl: 'taxes.component.html',
  directives: [NgIf, PaginationComponent],
})

export class TaxesComponent implements OnInit, OnDestroy {

  _taxes:Array<TaxVO> = [];

  filteredTaxes:Array<TaxVO>;

  @Input() selectedTax:TaxVO;

  @Output() dataSelected: EventEmitter<TaxVO> = new EventEmitter<TaxVO>();

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
    console.debug('TaxesComponent constructed');
  }

  ngOnInit() {
    console.debug('TaxesComponent ngOnInit');
  }

  @Input()
  set taxes(taxes:Array<TaxVO>) {
    this._taxes = taxes;
    this.filterTaxes();
  }

  private filterTaxes() {

    this.filteredTaxes = this._taxes;
    console.debug('TaxesComponent filterTaxes', this.filteredTaxes);

    if (this.filteredTaxes === null) {
      this.filteredTaxes = [];
    }

    let _total = this.filteredTaxes.length;
    this.totalItems = _total;
    if (_total > 0) {
      this.resetLastPageEnd();
    }
  }

  ngOnDestroy() {
    console.debug('TaxesComponent ngOnDestroy');
    this.selectedTax = null;
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

  protected onSelectRow(row:TaxVO) {
    console.debug('TaxesComponent onSelectRow handler', row);
    if (row == this.selectedTax) {
      this.selectedTax = null;
    } else {
      this.selectedTax = row;
    }
    this.dataSelected.emit(this.selectedTax);
  }

}
