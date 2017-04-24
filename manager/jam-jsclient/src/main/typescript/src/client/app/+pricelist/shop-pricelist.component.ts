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
import { Component, OnInit, OnDestroy, ViewChild } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { YcValidators } from './../shared/validation/validators';
import { PricingService, Util } from './../shared/services/index';
import { ModalComponent, ModalResult, ModalAction } from './../shared/modal/index';
import { ProductSkuSelectComponent } from './../shared/catalog/index';
import { CarrierSlaSelectComponent } from './../shared/shipping/index';
import { PriceListVO, ShopVO, ProductSkuVO, CarrierSlaVO } from './../shared/model/index';
import { FormValidationEvent, Futures, Future } from './../shared/event/index';
import { Config } from './../shared/config/env.config';
import { UiUtil } from './../shared/ui/index';
import { LogUtil } from './../shared/log/index';

@Component({
  selector: 'yc-shop-pricelist',
  moduleId: module.id,
  templateUrl: 'shop-pricelist.component.html',
})

export class ShopPriceListComponent implements OnInit, OnDestroy {

  private static _selectedShop:ShopVO;
  private static _selectedCurrency:string;

  private searchHelpShow:boolean = false;
  private forceShowAll:boolean = false;

  private pricelist:Array<PriceListVO> = [];
  private pricelistFilter:string;
  private pricelistFilterRequired:boolean = true;
  private pricelistFilterCapped:boolean = false;

  private delayedFiltering:Future;
  private delayedFilteringMs:number = Config.UI_INPUT_DELAY;
  private filterCap:number = Config.UI_FILTER_CAP;
  private filterNoCap:number = Config.UI_FILTER_NO_CAP;

  private selectedPricelist:PriceListVO;

  private pricelistEdit:PriceListVO;
  private pricelistEditForm:any;
  private pricelistEditFormSub:any; // tslint:disable-line:no-unused-variable
  private initialising:boolean = false; // tslint:disable-line:no-unused-variable
  private validForSave:boolean = false;

  @ViewChild('deleteConfirmationModalDialog')
  private deleteConfirmationModalDialog:ModalComponent;

  @ViewChild('editPricelistModalDialog')
  private editPricelistModalDialog:ModalComponent;

  @ViewChild('selectShopModalDialog')
  private selectShopModalDialog:ModalComponent;

  @ViewChild('selectCurrencyModalDialog')
  private selectCurrencyModalDialog:ModalComponent;

  @ViewChild('productSkuSelectDialog')
  private productSkuSelectDialog:ProductSkuSelectComponent;

  @ViewChild('carrierSlaSelectDialog')
  private carrierSlaSelectDialog:CarrierSlaSelectComponent;

  private deleteValue:String;

  private loading:boolean = false;

  constructor(private _priceService:PricingService,
              fb: FormBuilder) {
    LogUtil.debug('ShopPriceListComponent constructed');

    this.pricelistEditForm = fb.group({
      'skuCode': ['', YcValidators.requiredValidCode],
      'skuName': [''],
      'shopCode': ['', Validators.required],
      'currency': ['', Validators.required],
      'pricingPolicy': ['', YcValidators.validCode],
      'quantity': ['', YcValidators.requiredPositiveNumber],
      'regularPrice': ['', YcValidators.requiredPositiveNumber],
      'salePrice': ['', YcValidators.positiveNumber],
      'minimalPrice': ['', YcValidators.positiveNumber],
      'salefrom': ['', YcValidators.validDate],
      'saleto': ['', YcValidators.validDate],
      'tag': ['', YcValidators.nonBlankTrimmed],
    });
  }

  get selectedShop():ShopVO {
     return ShopPriceListComponent._selectedShop;
  }

  set selectedShop(selectedShop:ShopVO) {
    ShopPriceListComponent._selectedShop = selectedShop;
  }

  get selectedCurrency():string {
     return ShopPriceListComponent._selectedCurrency;
  }

  set selectedCurrency(selectedCurrency:string) {
    ShopPriceListComponent._selectedCurrency = selectedCurrency;
  }


  get availableto():string {
    return UiUtil.dateInputGetterProxy(this.pricelistEdit, 'saleto');
  }

  set availableto(availableto:string) {
    UiUtil.dateInputSetterProxy(this.pricelistEdit, 'saleto', availableto);
  }

  get availablefrom():string {
    return UiUtil.dateInputGetterProxy(this.pricelistEdit, 'salefrom');
  }

  set availablefrom(availablefrom:string) {
    UiUtil.dateInputSetterProxy(this.pricelistEdit, 'salefrom', availablefrom);
  }



  newPricelistInstance():PriceListVO {
    return {
      skuPriceId: 0,
      regularPrice: 0, minimalPrice: undefined, salePrice: undefined,
      salefrom: null, saleto:null,
      quantity: 1,
      currency: this.selectedCurrency,
      skuCode: '', skuName: '',
      shopCode: this.selectedShop.code,
      tag: null, pricingPolicy: null};
  }

  ngOnInit() {
    LogUtil.debug('ShopPriceListComponent ngOnInit');
    this.onRefreshHandler();
    let that = this;
    this.delayedFiltering = Futures.perpetual(function() {
      that.getFilteredPricelist();
    }, this.delayedFilteringMs);
    this.formBind();
  }

  ngOnDestroy() {
    LogUtil.debug('ShopPriceListComponent ngOnDestroy');
    this.formUnbind();
  }

  formBind():void {
    UiUtil.formBind(this, 'pricelistEditForm', 'pricelistEditFormSub', 'formChange', 'initialising', false);
  }

  formUnbind():void {
    UiUtil.formUnbind(this, 'pricelistEditFormSub');
  }

  formChange():void {
    LogUtil.debug('ShopPriceListComponent formChange', this.pricelistEditForm.valid, this.pricelistEdit);
    this.validForSave = this.pricelistEditForm.valid;
  }


  protected onShopSelect() {
    LogUtil.debug('ShopPriceListComponent onShopSelect');
    this.selectShopModalDialog.show();
  }

  protected onShopSelected(event:ShopVO) {
    LogUtil.debug('ShopPriceListComponent onShopSelected');
    this.selectedShop = event;
  }

  protected onSelectShopResult(modalresult: ModalResult) {
    LogUtil.debug('ShopPriceListComponent onSelectShopResult modal result is ', modalresult);
    if (this.selectedShop == null) {
      this.selectShopModalDialog.show();
    } else if (this.selectedCurrency == null) {
      this.selectCurrencyModalDialog.show();
    } else {
      this.getFilteredPricelist();
    }
  }

  protected onCurrencySelect() {
    LogUtil.debug('ShopPriceListComponent onCurrencySelect');
    this.selectCurrencyModalDialog.show();
  }

  protected onCurrencySelected(event:string) {
    LogUtil.debug('ShopPriceListComponent onCurrencySelected');
    this.selectedCurrency = event;
  }

  protected onSelectCurrencyResult(modalresult: ModalResult) {
    LogUtil.debug('ShopPriceListComponent onSelectCurrencyResult modal result is ', modalresult);
    if (this.selectedCurrency == null) {
      this.selectCurrencyModalDialog.show();
    } else {
      this.getFilteredPricelist();
    }
  }

  protected onFilterChange(event:any) {

    this.delayedFiltering.delay();

  }

  protected onRefreshHandler() {
    LogUtil.debug('ShopPriceListComponent refresh handler');
    this.getFilteredPricelist();
  }

  protected onPricelistSelected(data:PriceListVO) {
    LogUtil.debug('ShopPriceListComponent onPricelistSelected', data);
    this.selectedPricelist = data;
  }

  protected onSearchTag() {
    this.pricelistFilter = '#';
    this.searchHelpShow = false;
  }

  protected onSearchDate() {
    this.pricelistFilter = UiUtil.exampleDateSearch();
    this.searchHelpShow = false;
  }

  protected onSearchExact() {
    this.pricelistFilter = '!';
    this.searchHelpShow = false;
  }

  protected onForceShowAll() {
    this.forceShowAll = !this.forceShowAll;
    this.getFilteredPricelist();
  }

  protected onSearchHelpToggle() {
    this.searchHelpShow = !this.searchHelpShow;
  }

  protected onRowNew() {
    LogUtil.debug('ShopPriceListComponent onRowNew handler');
    this.validForSave = false;
    this.selectedPricelist = null;
    UiUtil.formInitialise(this, 'initialising', 'pricelistEditForm', 'pricelistEdit', this.newPricelistInstance(), false, ['skuCode']);
    this.editPricelistModalDialog.show();
  }

  protected onRowDelete(row:PriceListVO) {
    LogUtil.debug('ShopPriceListComponent onRowDelete handler', row);
    this.deleteValue = row.skuCode;
    this.deleteConfirmationModalDialog.show();
  }

  protected onRowDeleteSelected() {
    if (this.selectedPricelist != null) {
      this.onRowDelete(this.selectedPricelist);
    }
  }


  protected onRowEditPricelist(row:PriceListVO) {
    LogUtil.debug('ShopPriceListComponent onRowEditPricelist handler', row);
    this.validForSave = false;
    UiUtil.formInitialise(this, 'initialising', 'pricelistEditForm', 'pricelistEdit', Util.clone(row), row.skuPriceId > 0, ['skuCode']);
    this.editPricelistModalDialog.show();
  }

  protected onRowEditSelected() {
    if (this.selectedPricelist != null) {
      this.onRowEditPricelist(this.selectedPricelist);
    }
  }

  protected onRowCopySelected() {
    if (this.selectedPricelist != null) {
      var copy:PriceListVO = Util.clone(this.selectedPricelist);
      copy.skuPriceId = 0;
      this.onRowEditPricelist(copy);
    }
  }

  protected onSearchSKU() {
    if (this.pricelistEdit != null && this.pricelistEdit.skuPriceId <= 0) {
      this.productSkuSelectDialog.showDialog();
    }
  }


  protected onProductSkuSelected(event:FormValidationEvent<ProductSkuVO>) {
    LogUtil.debug('ShopPriceListComponent onProductSkuSelected');
    if (event.valid && this.pricelistEdit != null && this.pricelistEdit.skuPriceId <= 0) {
      this.pricelistEdit.skuCode = event.source.code;
      this.pricelistEdit.skuName = event.source.name;
    }
  }

  protected onSearchSLA() {
    if (this.pricelistEdit != null && this.pricelistEdit.skuPriceId <= 0) {
      this.carrierSlaSelectDialog.showDialog();
    }
  }


  protected onCarrierSlaSelected(event:FormValidationEvent<CarrierSlaVO>) {
    LogUtil.debug('ShopPriceListComponent onCarrierSlaSelected');
    if (event.valid && this.pricelistEdit != null && this.pricelistEdit.skuPriceId <= 0) {
      this.pricelistEdit.skuCode = event.source.code;
      this.pricelistEdit.skuName = event.source.name;
    }
  }


  protected onSaveHandler() {

    if (this.validForSave && this.pricelistEditForm.dirty) {

      if (this.pricelistEdit != null) {

        LogUtil.debug('ShopPriceListComponent Save handler pricelist', this.pricelistEdit);

        var _sub:any = this._priceService.savePriceList(this.pricelistEdit).subscribe(
            rez => {
              _sub.unsubscribe();
              let pk = this.pricelistEdit.skuPriceId;
              LogUtil.debug('ShopPriceListComponent pricelist changed', rez);
              this.selectedPricelist = rez;
              this.validForSave = false;
              this.pricelistEdit = this.newPricelistInstance();
              if (pk == 0) {
                this.pricelistFilter = '!' + rez.skuCode;
              }
              this.getFilteredPricelist();
          }
        );
      }

    }

  }

  protected onDiscardEventHandler() {
    LogUtil.debug('ShopPriceListComponent discard handler');
    if (this.selectedPricelist != null) {
      this.onRowEditSelected();
    } else {
      this.onRowNew();
    }
  }

  protected onEditPricelistResult(modalresult: ModalResult) {
    LogUtil.debug('ShopPriceListComponent onEditPricelistResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {

      this.onSaveHandler();

    }
  }

  protected onDeleteConfirmationResult(modalresult: ModalResult) {
    LogUtil.debug('ShopPriceListComponent onDeleteConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {

      if (this.selectedPricelist != null) {
        LogUtil.debug('ShopPriceListComponent onDeleteConfirmationResult', this.selectedPricelist);

        var _sub:any = this._priceService.removePriceList(this.selectedPricelist).subscribe(res => {
          _sub.unsubscribe();
          LogUtil.debug('ShopPriceListComponent removePricelist', this.selectedPricelist);
          this.selectedPricelist = null;
          this.pricelistEdit = null;
          this.getFilteredPricelist();
        });
      }
    }
  }

  protected onClearFilter() {

    this.pricelistFilter = '';
    this.getFilteredPricelist();

  }

  private getFilteredPricelist() {
    this.pricelistFilterRequired = !this.forceShowAll && (this.pricelistFilter == null || this.pricelistFilter.length < 2);

    LogUtil.debug('ShopPriceListComponent getFilteredPricelist' + (this.forceShowAll ? ' forcefully': ''));

    if (this.selectedShop != null && !this.pricelistFilterRequired) {
      this.loading = true;
      let max = this.forceShowAll ? this.filterNoCap : this.filterCap;
      var _sub:any = this._priceService.getFilteredPriceLists(this.selectedShop, this.selectedCurrency, this.pricelistFilter, max).subscribe( allpricelist => {
        LogUtil.debug('ShopPriceListComponent getFilteredPricelist', allpricelist);
        this.pricelist = allpricelist;
        this.selectedPricelist = null;
        this.validForSave = false;
        this.pricelistFilterCapped = this.pricelist.length >= max;
        this.loading = false;
        _sub.unsubscribe();
      });
    } else {
      this.pricelist = [];
      this.selectedPricelist = null;
      this.validForSave = false;
      this.pricelistFilterCapped = false;
    }
  }

}
