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
import {ProductTypeVO} from './../model/index';
import {CatalogService} from './../services/index';
import {Futures, Future} from './../event/index';
import {Config} from './../config/env.config';

@Component({
  selector: 'yc-product-type-select',
  moduleId: module.id,
  templateUrl: 'product-type-select.component.html',
  directives: [ROUTER_DIRECTIVES, NgFor],
})

export class ProductTypeSelectComponent implements OnInit, OnDestroy {

  private filteredProducttypes : ProductTypeVO[] = [];
  private producttypeFilter : string;

  private selectedProducttype : ProductTypeVO = null;

  delayedFiltering:Future;
  delayedFilteringMs:number = Config.UI_INPUT_DELAY;
  filterCap:number = Config.UI_FILTER_CAP;

  producttypeFilterRequired:boolean = true;
  producttypeFilterCapped:boolean = false;

  @Output() dataSelected: EventEmitter<ProductTypeVO> = new EventEmitter<ProductTypeVO>();

  constructor (private _producttypeService : CatalogService) {
    console.debug('ProductTypeSelectComponent constructed');
  }

  getAllProducttypes() {

    this.producttypeFilterRequired = (this.producttypeFilter == null || this.producttypeFilter.length < 2);

    if (!this.producttypeFilterRequired) {
      var _sub:any = this._producttypeService.getFilteredProductTypes(this.producttypeFilter, this.filterCap).subscribe(allproducttypes => {
        console.debug('ProductTypeSelectComponent getAllProducttypes', allproducttypes);
        this.filteredProducttypes = allproducttypes;
        this.producttypeFilterCapped = this.filteredProducttypes.length >= this.filterCap;
        _sub.unsubscribe();
      });
    }
  }

  ngOnDestroy() {
    console.debug('ProductTypeSelectComponent ngOnDestroy');
  }

  ngOnInit() {
    console.debug('ProductTypeSelectComponent ngOnInit');
    let that = this;
    this.delayedFiltering = Futures.perpetual(function() {
      that.getAllProducttypes();
    }, this.delayedFilteringMs);

  }

  onSelectClick(producttype: ProductTypeVO) {
    console.debug('ProductTypeSelectComponent onSelectClick', producttype);
    this.selectedProducttype = producttype;
    this.dataSelected.emit(this.selectedProducttype);
  }

  protected onFilterChange() {

    this.delayedFiltering.delay();

  }

}
