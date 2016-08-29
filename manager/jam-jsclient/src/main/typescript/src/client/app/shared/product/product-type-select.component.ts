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
import {Component,  OnInit, OnDestroy, Input, Output, EventEmitter, ViewChild} from '@angular/core';
import {NgFor} from '@angular/common';
import {HTTP_PROVIDERS}    from '@angular/http';
import {ROUTER_DIRECTIVES} from '@angular/router';
import {ProductTypeInfoVO} from './../model/index';
import {CatalogService} from './../services/index';
import {ModalComponent, ModalResult, ModalAction} from './../modal/index';
import {Futures, Future, FormValidationEvent} from './../event/index';
import {Config} from './../config/env.config';

@Component({
  selector: 'yc-product-type-select',
  moduleId: module.id,
  templateUrl: 'product-type-select.component.html',
  directives: [ROUTER_DIRECTIVES, NgFor, ModalComponent],
})

export class ProductTypeSelectComponent implements OnInit, OnDestroy {

  changed:boolean = false;

  @ViewChild('productTypeModalDialog')
  productTypeModalDialog:ModalComponent;

  validForSelect:boolean = false;

  private filteredProductTypes : ProductTypeInfoVO[] = [];
  private productTypeFilter : string;

  private selectedProductType : ProductTypeInfoVO = null;

  delayedFiltering:Future;
  delayedFilteringMs:number = Config.UI_INPUT_DELAY;
  filterCap:number = Config.UI_FILTER_CAP;

  productTypeFilterRequired:boolean = true;
  productTypeFilterCapped:boolean = false;

  @Output() dataSelected: EventEmitter<FormValidationEvent<ProductTypeInfoVO>> = new EventEmitter<FormValidationEvent<ProductTypeInfoVO>>();

  constructor (private _typeService : CatalogService) {
    console.debug('ProductTypeSelectComponent constructed');
  }

  getAllProductTypes() {

    this.productTypeFilterRequired = (this.productTypeFilter == null || this.productTypeFilter.length < 2);

    if (!this.productTypeFilterRequired) {
      var _sub:any = this._typeService.getFilteredProductTypes(this.productTypeFilter, this.filterCap).subscribe(allproductTypes => {
        console.debug('ProductTypeSelectComponent getAllProductTypes', allproductTypes);
        this.selectedProductType = null;
        this.changed = false;
        this.validForSelect = false;
        this.filteredProductTypes = allproductTypes;
        this.productTypeFilterCapped = this.filteredProductTypes.length >= this.filterCap;
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
      that.getAllProductTypes();
    }, this.delayedFilteringMs);

  }

  onSelectClick(productType: ProductTypeInfoVO) {
    console.debug('ProductTypeSelectComponent onSelectClick', productType);
    this.selectedProductType = productType;
    this.validForSelect = true;
  }

  protected onFilterChange() {

    this.delayedFiltering.delay();

  }

  public showDialog() {
    console.debug('ProductTypeSelectComponent showDialog');
    this.productTypeModalDialog.show();
  }


  protected onSelectConfirmationResult(modalresult: ModalResult) {
    console.debug('ProductTypeSelectComponent onSelectConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {
      this.dataSelected.emit({ source: this.selectedProductType, valid: true });
      this.selectedProductType = null;
    }
  }

}
