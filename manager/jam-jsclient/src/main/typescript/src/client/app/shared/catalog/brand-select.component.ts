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
import {Component,  OnInit, OnDestroy, Input, Output, EventEmitter} from '@angular/core';
import {NgFor} from '@angular/common';
import {HTTP_PROVIDERS}    from '@angular/http';
import {ROUTER_DIRECTIVES} from '@angular/router';
import {BrandVO} from './../model/index';
import {CatalogService} from './../services/index';
import {Futures, Future} from './../event/index';
import {Config} from './../config/env.config';

@Component({
  selector: 'yc-brand-select',
  moduleId: module.id,
  templateUrl: 'brand-select.component.html',
  directives: [ROUTER_DIRECTIVES, NgFor],
})

export class BrandSelectComponent implements OnInit, OnDestroy {

  private filteredBrands : BrandVO[] = [];
  private brandFilter : string;

  private selectedBrand : BrandVO = null;

  delayedFiltering:Future;
  delayedFilteringMs:number = Config.UI_INPUT_DELAY;
  filterCap:number = Config.UI_FILTER_CAP;

  brandFilterRequired:boolean = true;
  brandFilterCapped:boolean = false;

  @Output() dataSelected: EventEmitter<BrandVO> = new EventEmitter<BrandVO>();

  constructor (private _brandService : CatalogService) {
    console.debug('BrandSelectComponent constructed');
  }

  getAllBrands() {

    this.brandFilterRequired = (this.brandFilter == null || this.brandFilter.length < 2);

    if (!this.brandFilterRequired) {
      var _sub:any = this._brandService.getFilteredBrands(this.brandFilter, this.filterCap).subscribe(allbrands => {
        console.debug('BrandSelectComponent getAllBrands', allbrands);
        this.filteredBrands = allbrands;
        this.brandFilterCapped = this.filteredBrands.length >= this.filterCap;
        _sub.unsubscribe();
      });
    }
  }

  ngOnDestroy() {
    console.debug('BrandSelectComponent ngOnDestroy');
  }

  ngOnInit() {
    console.debug('BrandSelectComponent ngOnInit');
    let that = this;
    this.delayedFiltering = Futures.perpetual(function() {
      that.getAllBrands();
    }, this.delayedFilteringMs);

  }

  onSelectClick(brand: BrandVO) {
    console.debug('BrandSelectComponent onSelectClick', brand);
    this.selectedBrand = brand;
    this.dataSelected.emit(this.selectedBrand);
  }

  protected onFilterChange() {

    this.delayedFiltering.delay();

  }

}
