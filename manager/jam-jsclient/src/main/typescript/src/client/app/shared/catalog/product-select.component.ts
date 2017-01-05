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
import { ProductVO } from './../model/index';
import { PIMService } from './../services/index';
import { ModalComponent, ModalResult, ModalAction } from './../modal/index';
import { Futures, Future, FormValidationEvent } from './../event/index';
import { Config } from './../config/env.config';
import { LogUtil } from './../log/index';

@Component({
  selector: 'yc-product-select',
  moduleId: module.id,
  templateUrl: 'product-select.component.html',
})

export class ProductSelectComponent implements OnInit, OnDestroy {

  @Output() dataSelected: EventEmitter<FormValidationEvent<ProductVO>> = new EventEmitter<FormValidationEvent<ProductVO>>();

  private changed:boolean = false;

  @ViewChild('productModalDialog')
  private productModalDialog:ModalComponent;

  private validForSelect:boolean = false;

  private filteredProducts : ProductVO[] = [];
  private productFilter : string;

  private selectedProduct : ProductVO = null;

  private delayedFiltering:Future;
  private delayedFilteringMs:number = Config.UI_INPUT_DELAY;
  private filterCap:number = Config.UI_FILTER_CAP;

  private productFilterRequired:boolean = true;
  private productFilterCapped:boolean = false;

  private loading:boolean = false;

  constructor (private _productService : PIMService) {
    LogUtil.debug('ProductSelectComponent constructed');
  }

  ngOnDestroy() {
    LogUtil.debug('ProductSelectComponent ngOnDestroy');
  }

  ngOnInit() {
    LogUtil.debug('ProductSelectComponent ngOnInit');
    let that = this;
    this.delayedFiltering = Futures.perpetual(function() {
      that.getAllProducts();
    }, this.delayedFilteringMs);

  }

  onSelectClick(producttype: ProductVO) {
    LogUtil.debug('ProductSelectComponent onSelectClick', producttype);
    this.selectedProduct = producttype;
    this.validForSelect = true;
  }

  protected onFilterChange() {

    this.delayedFiltering.delay();

  }

  public showDialog() {
    LogUtil.debug('ProductSelectComponent showDialog');
    this.productModalDialog.show();
  }


  protected onSelectConfirmationResult(modalresult: ModalResult) {
    LogUtil.debug('ProductSelectComponent onSelectConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {
      this.dataSelected.emit({ source: this.selectedProduct, valid: true });
      this.selectedProduct = null;
    }
  }

  private getAllProducts() {

    this.productFilterRequired = (this.productFilter == null || this.productFilter.length < 2);

    if (!this.productFilterRequired) {
      this.loading = true;
      var _sub:any = this._productService.getFilteredProducts(this.productFilter, this.filterCap).subscribe(allproducts => {
        LogUtil.debug('ProductSelectComponent getAllProducts', allproducts);
        this.selectedProduct = null;
        this.changed = false;
        this.validForSelect = false;
        this.filteredProducts = allproducts;
        this.productFilterCapped = this.filteredProducts.length >= this.filterCap;
        this.loading = false;
        _sub.unsubscribe();
      });
    }
  }

}
