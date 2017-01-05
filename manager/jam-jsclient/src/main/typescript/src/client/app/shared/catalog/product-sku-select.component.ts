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
import { ProductSkuVO } from './../model/index';
import { PIMService } from './../services/index';
import { ModalComponent, ModalResult, ModalAction } from './../modal/index';
import { Futures, Future, FormValidationEvent } from './../event/index';
import { Config } from './../config/env.config';
import { LogUtil } from './../log/index';

@Component({
  selector: 'yc-product-sku-select',
  moduleId: module.id,
  templateUrl: 'product-sku-select.component.html',
})

export class ProductSkuSelectComponent implements OnInit, OnDestroy {

  @Output() dataSelected: EventEmitter<FormValidationEvent<ProductSkuVO>> = new EventEmitter<FormValidationEvent<ProductSkuVO>>();

  private changed:boolean = false;

  @ViewChild('productSkuModalDialog')
  private productSkuModalDialog:ModalComponent;

  private validForSelect:boolean = false;

  private filteredProductSkus : ProductSkuVO[] = [];
  private productSkuFilter : string;

  private selectedProductSku : ProductSkuVO = null;

  private delayedFiltering:Future;
  private delayedFilteringMs:number = Config.UI_INPUT_DELAY;
  private filterCap:number = Config.UI_FILTER_CAP;

  private productSkuFilterRequired:boolean = true;
  private productSkuFilterCapped:boolean = false;

  private loading:boolean = false;

  constructor (private _productService : PIMService) {
    LogUtil.debug('ProductSkuSelectComponent constructed');
  }

  ngOnDestroy() {
    LogUtil.debug('ProductSkuSelectComponent ngOnDestroy');
  }

  ngOnInit() {
    LogUtil.debug('ProductSkuSelectComponent ngOnInit');
    let that = this;
    this.delayedFiltering = Futures.perpetual(function() {
      that.getAllProductSkus();
    }, this.delayedFilteringMs);

  }

  onSelectClick(producttype: ProductSkuVO) {
    LogUtil.debug('ProductSkuSelectComponent onSelectClick', producttype);
    this.selectedProductSku = producttype;
    this.validForSelect = true;
  }

  protected onFilterChange() {

    this.delayedFiltering.delay();

  }

  public showDialog() {
    LogUtil.debug('ProductSkuSelectComponent showDialog');
    this.productSkuModalDialog.show();
  }


  protected onSelectConfirmationResult(modalresult: ModalResult) {
    LogUtil.debug('ProductSkuSelectComponent onSelectConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {
      this.dataSelected.emit({ source: this.selectedProductSku, valid: true });
      this.selectedProductSku = null;
    }
  }

  private getAllProductSkus() {

    this.productSkuFilterRequired = (this.productSkuFilter == null || this.productSkuFilter.length < 2);

    if (!this.productSkuFilterRequired) {
      this.loading = true;
      var _sub:any = this._productService.getFilteredProductSkus(this.productSkuFilter, this.filterCap).subscribe(allproducts => {
        LogUtil.debug('ProductSkuSelectComponent getAllProductSkus', allproducts);
        this.selectedProductSku = null;
        this.changed = false;
        this.validForSelect = false;
        this.filteredProductSkus = allproducts;
        this.productSkuFilterCapped = this.filteredProductSkus.length >= this.filterCap;
        this.loading = false;
        _sub.unsubscribe();
      });
    }
  }

}
