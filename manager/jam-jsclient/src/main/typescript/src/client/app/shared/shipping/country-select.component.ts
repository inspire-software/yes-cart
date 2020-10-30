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
import { Component, OnInit, OnDestroy, Output, EventEmitter, ViewChild } from '@angular/core';
import { CountryInfoVO, SearchContextVO } from './../model/index';
import { LocationService } from './../services/index';
import { ModalComponent, ModalResult, ModalAction } from './../modal/index';
import { Futures, Future, FormValidationEvent } from './../event/index';
import { Config } from './../config/env.config';
import { LogUtil } from './../log/index';

@Component({
  selector: 'cw-country-select',
  moduleId: module.id,
  templateUrl: 'country-select.component.html',
})

export class CountrySelectComponent implements OnInit, OnDestroy {

  @Output() dataSelected: EventEmitter<FormValidationEvent<CountryInfoVO>> = new EventEmitter<FormValidationEvent<CountryInfoVO>>();

  private changed:boolean = false;

  @ViewChild('countryModalDialog')
  private countryModalDialog:ModalComponent;

  private validForSelect:boolean = false;

  private filteredCountries : CountryInfoVO[] = [];
  private countryFilter : string;

  private selectedCountry : CountryInfoVO = null;

  private delayedFiltering:Future;
  private delayedFilteringMs:number = Config.UI_INPUT_DELAY;
  private filterCap:number = Config.UI_FILTER_CAP;

  private countryFilterRequired:boolean = true;
  private countryFilterCapped:boolean = false;

  private loading:boolean = false;

  constructor (private _locationService : LocationService) {
    LogUtil.debug('CountrySelectComponent constructed');
    let that = this;
    this.delayedFiltering = Futures.perpetual(function() {
      that.getAllCountries();
    }, this.delayedFilteringMs);
  }

  ngOnDestroy() {
    LogUtil.debug('CountrySelectComponent ngOnDestroy');
  }

  ngOnInit() {
    LogUtil.debug('CountrySelectComponent ngOnInit');
  }

  onSelectClick(country: CountryInfoVO) {
    LogUtil.debug('CountrySelectComponent onSelectClick', country);
    this.selectedCountry = country;
    this.validForSelect = true;
  }

  protected onFilterChange() {

    this.delayedFiltering.delay();

  }

  protected onClearFilter() {
    this.countryFilter = '';
    this.delayedFiltering.delay();
  }

  public showDialog() {
    LogUtil.debug('CountrySelectComponent showDialog');
    this.countryModalDialog.show();
  }


  protected onSelectConfirmationResult(modalresult: ModalResult) {
    LogUtil.debug('CountrySelectComponent onSelectConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {
      this.dataSelected.emit({ source: this.selectedCountry, valid: true });
      this.selectedCountry = null;
    }
  }

  private getAllCountries() {

    this.countryFilterRequired = (this.countryFilter == null || this.countryFilter.length < 2);

    if (!this.countryFilterRequired) {
      this.loading = true;
      let _ctx:SearchContextVO = {
        parameters : {
          filter: [ this.countryFilter ]
        },
        start : 0,
        size : this.filterCap,
        sortBy : 'name',
        sortDesc : false
      };
      let _sub:any = this._locationService.getFilteredCountries(_ctx).subscribe(allcountries => {
        LogUtil.debug('CountrySelectComponent getFilteredCountries', allcountries);
        this.selectedCountry = null;
        this.changed = false;
        this.validForSelect = false;
        this.filteredCountries = allcountries != null ? allcountries.items : [];
        this.countryFilterCapped = allcountries != null && allcountries.total > this.filterCap;
        this.loading = false;
        _sub.unsubscribe();
      });
    }
  }

}
