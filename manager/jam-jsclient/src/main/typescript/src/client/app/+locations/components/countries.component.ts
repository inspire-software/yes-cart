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
import { CountryInfoVO, Pair, SearchResultVO } from './../../shared/model/index';
import { Config } from './../../shared/config/env.config';
import { LogUtil } from './../../shared/log/index';

@Component({
  selector: 'yc-countries',
  moduleId: module.id,
  templateUrl: 'countries.component.html',
})

export class CountriesComponent implements OnInit, OnDestroy {

  @Input() selectedCountry:CountryInfoVO;

  @Output() dataSelected: EventEmitter<CountryInfoVO> = new EventEmitter<CountryInfoVO>();

  @Output() pageSelected: EventEmitter<number> = new EventEmitter<number>();

  @Output() sortSelected: EventEmitter<Pair<string, boolean>> = new EventEmitter<Pair<string, boolean>>();

  private _countries:SearchResultVO<CountryInfoVO> = null;

  private filteredCountries:Array<CountryInfoVO>;

  //sorting
  private sortColumn:string = null;
  private sortDesc:boolean = false;

  //paging
  private maxSize:number = Config.UI_TABLE_PAGE_NUMS;
  private itemsPerPage:number = Config.UI_TABLE_PAGE_SIZE;
  private totalItems:number = 0;
  private currentPage:number = 1;

  constructor() {
    LogUtil.debug('CountriesComponent constructed');
  }

  ngOnInit() {
    LogUtil.debug('CountriesComponent ngOnInit');
  }

  @Input()
  set countries(countries:SearchResultVO<CountryInfoVO>) {
    this._countries = countries;
    this.filterCountries();
  }

  ngOnDestroy() {
    LogUtil.debug('CountriesComponent ngOnDestroy');
    this.selectedCountry = null;
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

  protected onSelectRow(row:CountryInfoVO) {
    LogUtil.debug('CountriesComponent onSelectRow handler', row);
    if (row == this.selectedCountry) {
      this.selectedCountry = null;
    } else {
      this.selectedCountry = row;
    }
    this.dataSelected.emit(this.selectedCountry);
  }

  private filterCountries() {

    LogUtil.debug('CountriesComponent filterCountries', this.filteredCountries);

    if (this._countries != null) {

      this.filteredCountries = this._countries.items != null ? this._countries.items : [];
      this.maxSize = Config.UI_TABLE_PAGE_NUMS;
      this.itemsPerPage = this._countries.searchContext.size;
      this.totalItems = this._countries.total;
      this.currentPage = this._countries.searchContext.start + 1;
      this.sortColumn = this._countries.searchContext.sortBy;
      this.sortDesc = this._countries.searchContext.sortDesc;
    } else {
      this.filteredCountries = [];
      this.maxSize = Config.UI_TABLE_PAGE_NUMS;
      this.itemsPerPage = Config.UI_TABLE_PAGE_SIZE;
      this.totalItems = 0;
      this.currentPage = 1;
      this.sortColumn = null;
      this.sortDesc = false;
    }

  }

}
