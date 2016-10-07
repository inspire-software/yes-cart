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
import {CountryVO} from './../../shared/model/index';
import {PaginationComponent} from './../../shared/pagination/index';
import {Futures, Future} from './../../shared/event/index';
import {Config} from './../../shared/config/env.config';


@Component({
  selector: 'yc-countries',
  moduleId: module.id,
  templateUrl: 'countries.component.html',
  directives: [NgIf, PaginationComponent],
})

export class CountriesComponent implements OnInit, OnDestroy {

  _countries:Array<CountryVO> = [];
  _filter:string;
  delayedFiltering:Future;
  delayedFilteringMs:number = Config.UI_INPUT_DELAY;

  filteredCountries:Array<CountryVO>;

  @Input() selectedCountry:CountryVO;

  @Output() dataSelected: EventEmitter<CountryVO> = new EventEmitter<CountryVO>();

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
    console.debug('CountriesComponent constructed');
    let that = this;
    this.delayedFiltering = Futures.perpetual(function() {
      that.filterCountries();
    }, this.delayedFilteringMs);
  }

  ngOnInit() {
    console.debug('CountriesComponent ngOnInit');
  }

  @Input()
  set countries(countries:Array<CountryVO>) {
    this._countries = countries;
    this.filterCountries();
  }

  @Input()
  set filter(filter:string) {
    this._filter = filter ? filter.toLowerCase() : null;
    this.delayedFiltering.delay();
  }

  private filterCountries() {
    if (this._filter) {
      this.filteredCountries = this._countries.filter(country =>
          country.countryCode.toLowerCase().indexOf(this._filter) !== -1 ||
          country.name.toLowerCase().indexOf(this._filter) !== -1 ||
          country.displayName && country.displayName.toLowerCase().indexOf(this._filter) !== -1
      );
      console.debug('CountriesComponent filterCountries', this._filter);
    } else {
      this.filteredCountries = this._countries;
      console.debug('CountriesComponent filterCountries no filter');
    }

    if (this.filteredCountries === null) {
      this.filteredCountries = [];
    }

    let _total = this.filteredCountries.length;
    this.totalItems = _total;
    if (_total > 0) {
      this.resetLastPageEnd();
    }
  }

  ngOnDestroy() {
    console.debug('CountriesComponent ngOnDestroy');
    this.selectedCountry = null;
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

  protected onSelectRow(row:CountryVO) {
    console.debug('CountriesComponent onSelectRow handler', row);
    if (row == this.selectedCountry) {
      this.selectedCountry = null;
    } else {
      this.selectedCountry = row;
    }
    this.dataSelected.emit(this.selectedCountry);
  }

}
