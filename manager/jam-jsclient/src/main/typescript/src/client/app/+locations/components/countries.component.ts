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
import {Router, ActivatedRoute} from '@angular/router';
import {PaginationComponent} from './../../shared/pagination/index';
import {ModalComponent, ModalResult, ModalAction} from './../../shared/modal/index';


@Component({
  selector: 'yc-countries',
  moduleId: module.id,
  templateUrl: 'countries.component.html',
  directives: [NgIf, PaginationComponent, ModalComponent],
})

export class CountriesComponent implements OnInit, OnDestroy {

  _countries:Array<CountryVO>;
  _filter:string;

  filteredCountries:Array<CountryVO>;

  @Input() selectedCountry:CountryVO;

  @Output() dataSelected: EventEmitter<CountryVO> = new EventEmitter<CountryVO>();

  //paging
  maxSize:number = 5;
  itemsPerPage:number = 10;
  totalItems:number = 0;
  currentPage:number = 1;

  constructor() {
    console.debug('CountriesComponent constructed');
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
    this._filter = filter;
    this.filterCountries();
  }

  private filterCountries() {
    if (this._filter) {
      this.filteredCountries = this._countries.filter(country =>
          country.countryCode.indexOf(this._filter) !== -1 ||
          country.name.indexOf(this._filter) !== -1 ||
          country.displayName && country.displayName.indexOf(this._filter) !== -1
      );
    } else {
      this.filteredCountries = this._countries;
    }

    // This causes Exception if page is > 1 in DEV mode, see https://github.com/angular/angular/issues/6005
    let _total = this.filteredCountries.length;
    if (_total < this.itemsPerPage * this.currentPage) {
      this.currentPage = 1;
    }
    this.totalItems = _total;
  }

  ngOnDestroy() {
    console.debug('CountriesComponent ngOnDestroy');
    this.selectedCountry = null;
    this.dataSelected.emit(null);
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
