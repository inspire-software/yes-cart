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
import { Component, OnInit, OnDestroy, Input, Output, EventEmitter, ViewChild } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { YcValidators } from './../../shared/validation/validators';
import { ProductSkuVO, PriceListVO, CarrierSlaVO, FulfilmentCentreInfoVO } from './../../shared/model/index';
import { FormValidationEvent, Futures, Future } from './../../shared/event/index';
import { ProductSkuSelectComponent } from './../../shared/catalog/index';
import { CarrierSlaSelectComponent } from './../../shared/shipping/index';
import { FulfilmentCentreSelectComponent } from './../../shared/fulfilment/index';
import { UiUtil } from './../../shared/ui/index';
import { LogUtil } from './../../shared/log/index';

@Component({
  selector: 'yc-price',
  moduleId: module.id,
  templateUrl: 'price.component.html',
})

export class PriceComponent implements OnInit, OnDestroy {

  @Output() dataChanged: EventEmitter<FormValidationEvent<PriceListVO>> = new EventEmitter<FormValidationEvent<PriceListVO>>();

  private _pricelist:PriceListVO;

  private initialising:boolean = false; // tslint:disable-line:no-unused-variable
  private delayedChange:Future;

  private pricelistForm:any;
  private pricelistFormSub:any; // tslint:disable-line:no-unused-variable

  @ViewChild('productSkuSelectDialog')
  private productSkuSelectDialog:ProductSkuSelectComponent;

  @ViewChild('carrierSlaSelectDialog')
  private carrierSlaSelectDialog:CarrierSlaSelectComponent;

  @ViewChild('selectCentreModalDialog')
  private selectCentreModalDialog:FulfilmentCentreSelectComponent;

  constructor(fb: FormBuilder) {
    LogUtil.debug('PriceComponent constructed');

    let that = this;

    this.pricelistForm = fb.group({
      'skuCode': ['', YcValidators.requiredValidCode],
      'skuName': [''],
      'shopCode': ['', Validators.required],
      'supplier': ['', YcValidators.validCode255],
      'currency': ['', Validators.required],
      'pricingPolicy': ['', YcValidators.validCode],
      'priceUponRequest': [''],
      'priceOnOffer': [''],
      'quantity': ['', YcValidators.requiredPositiveNumber],
      'regularPrice': ['', YcValidators.requiredPositiveNumber],
      'salePrice': ['', YcValidators.positiveNumber],
      'minimalPrice': ['', YcValidators.positiveNumber],
      'salefrom': [''],
      'saleto': [''],
      'tag': ['', YcValidators.nonBlankTrimmed],
      'ref': ['', YcValidators.nonBlankTrimmed],
    });

    this.delayedChange = Futures.perpetual(function() {
      that.formChange();
    }, 200);

  }

  formBind():void {
    UiUtil.formBind(this, 'pricelistForm', 'pricelistFormSub', 'delayedChange', 'initialising');
  }


  formUnbind():void {
    UiUtil.formUnbind(this, 'pricelistFormSub');
  }

  formChange():void {
    LogUtil.debug('PriceComponent formChange', this.pricelistForm.valid, this._pricelist);
    this.dataChanged.emit({ source: this._pricelist, valid: this.pricelistForm.valid });
  }

  formMarkDirty(field:string):void {
    UiUtil.formMarkFieldDirty(this, 'pricelistForm', field);
  }

  @Input()
  set pricelist(pricelist: PriceListVO) {

    let lock = pricelist == null || pricelist.skuPriceId > 0;
    UiUtil.formInitialise(this, 'initialising', 'pricelistForm', '_pricelist', pricelist, lock, ['skuCode']);

  }

  get pricelist(): PriceListVO {
    return this._pricelist;
  }


  onAvailableFrom(event:FormValidationEvent<any>) {
    if (event.valid) {
      this.pricelist.salefrom = event.source;
    }
    UiUtil.formDataChange(this, 'pricelistForm', 'salefrom', event);
  }

  onAvailableTo(event:FormValidationEvent<any>) {
    if (event.valid) {
      this.pricelist.saleto = event.source;
    }
    UiUtil.formDataChange(this, 'pricelistForm', 'saleto', event);
  }


  ngOnInit() {
    LogUtil.debug('PriceComponent ngOnInit');
    this.formBind();
  }

  ngOnDestroy() {
    LogUtil.debug('PriceComponent ngOnDestroy');
    this.formUnbind();
  }

  tabSelected(tab:any) {
    LogUtil.debug('PriceComponent tabSelected', tab);
  }

  protected onSearchSKU() {
    if (this._pricelist != null && this._pricelist.skuPriceId <= 0) {
      this.productSkuSelectDialog.showDialog();
    }
  }


  protected onProductSkuSelected(event:FormValidationEvent<ProductSkuVO>) {
    LogUtil.debug('PriceComponent onProductSkuSelected');
    if (event.valid && this._pricelist != null && this._pricelist.skuPriceId <= 0) {
      this._pricelist.skuCode = event.source.code;
      this._pricelist.skuName = event.source.name;
      this.formMarkDirty('skuCode');
    }
  }


  protected onSearchSLA() {
    if (this.pricelist != null && this.pricelist.skuPriceId <= 0) {
      this.carrierSlaSelectDialog.showDialog();
    }
  }


  protected onCarrierSlaSelected(event:FormValidationEvent<CarrierSlaVO>) {
    LogUtil.debug('PriceComponent onCarrierSlaSelected');
    if (event.valid && this.pricelist != null && this.pricelist.skuPriceId <= 0) {
      this.pricelist.skuCode = event.source.code;
      this.pricelist.skuName = event.source.name;
      if (this.pricelist.tag == null || this.pricelist.tag == '') {
        this.pricelist.tag = 'shipping'; // suggest shipping tag so that it is easier to find shipping prices
      }
    }
  }


  protected onSearchFC() {
    if (this.pricelist != null && this.pricelist.skuPriceId <= 0) {
      this.selectCentreModalDialog.showDialog();
    }
  }


  protected onFulfilmentCentreSelected(event:FormValidationEvent<FulfilmentCentreInfoVO>) {
    LogUtil.debug('PriceComponent onFulfilmentCentreSelected');
    if (event.valid) {
      this.pricelist.supplier = event.source.code;
    }
  }

}
