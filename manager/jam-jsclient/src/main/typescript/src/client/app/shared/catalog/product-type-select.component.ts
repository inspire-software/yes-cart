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
import { Component, OnInit, OnDestroy, Output, EventEmitter, ViewChild } from '@angular/core';
import { ProductTypeVO } from './../model/index';
import { CatalogService } from './../services/index';
import { ModalComponent, ModalResult, ModalAction } from './../modal/index';
import { Futures, Future, FormValidationEvent } from './../event/index';
import { Config } from './../config/env.config';
import { LogUtil } from './../log/index';

@Component({
  selector: 'yc-product-type-select',
  moduleId: module.id,
  templateUrl: 'product-type-select.component.html',
})

export class ProductTypeSelectComponent implements OnInit, OnDestroy {

  @Output() dataSelected: EventEmitter<FormValidationEvent<ProductTypeVO>> = new EventEmitter<FormValidationEvent<ProductTypeVO>>();

  private changed:boolean = false;

  @ViewChild('productTypeModalDialog')
  private productTypeModalDialog:ModalComponent;

  private validForSelect:boolean = false;

  private filteredProductTypes : ProductTypeVO[] = [];
  private productTypeFilter : string;

  private selectedProductType : ProductTypeVO = null;

  private delayedFiltering:Future;
  private delayedFilteringMs:number = Config.UI_INPUT_DELAY;
  private filterCap:number = Config.UI_FILTER_CAP;

  private productTypeFilterRequired:boolean = true;
  private productTypeFilterCapped:boolean = false;

  private loading:boolean = false;

  constructor (private _typeService : CatalogService) {
    LogUtil.debug('ProductTypeSelectComponent constructed');
  }

  ngOnDestroy() {
    LogUtil.debug('ProductTypeSelectComponent ngOnDestroy');
  }

  ngOnInit() {
    LogUtil.debug('ProductTypeSelectComponent ngOnInit');
    let that = this;
    this.delayedFiltering = Futures.perpetual(function() {
      that.getAllProductTypes();
    }, this.delayedFilteringMs);

  }

  onSelectClick(producttype: ProductTypeVO) {
    LogUtil.debug('ProductTypeSelectComponent onSelectClick', producttype);
    this.selectedProductType = producttype;
    this.validForSelect = true;
  }

  protected onFilterChange() {

    this.delayedFiltering.delay();

  }

  public showDialog() {
    LogUtil.debug('ProductTypeSelectComponent showDialog');
    this.productTypeModalDialog.show();
  }


  protected onSelectConfirmationResult(modalresult: ModalResult) {
    LogUtil.debug('ProductTypeSelectComponent onSelectConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {
      this.dataSelected.emit({ source: this.selectedProductType, valid: true });
      this.selectedProductType = null;
    }
  }

  private getAllProductTypes() {

    this.productTypeFilterRequired = (this.productTypeFilter == null || this.productTypeFilter.length < 2);

    if (!this.productTypeFilterRequired) {
      this.loading = true;
      var _sub:any = this._typeService.getFilteredProductTypes(this.productTypeFilter, this.filterCap).subscribe(allproductTypes => {
        LogUtil.debug('ProductTypeSelectComponent getAllProductTypes', allproductTypes);
        this.selectedProductType = null;
        this.changed = false;
        this.validForSelect = false;
        this.filteredProductTypes = allproductTypes;
        this.productTypeFilterCapped = this.filteredProductTypes.length >= this.filterCap;
        this.loading = false;
        _sub.unsubscribe();
      });
    }
  }

}
