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
import {Component, OnInit, OnDestroy, ViewChild} from '@angular/core';
import {NgIf} from '@angular/common';
import {HTTP_PROVIDERS}    from '@angular/http';
import {FormBuilder, Validators, REACTIVE_FORM_DIRECTIVES} from '@angular/forms';
import {YcValidators} from './../shared/validation/validators';
import {PricingService, Util} from './../shared/services/index';
import {UiUtil} from './../shared/ui/index';
import {TAB_DIRECTIVES} from 'ng2-bootstrap/ng2-bootstrap';
import {PriceListComponent} from './components/index';
import {DataControlComponent} from './../shared/sidebar/index';
import {ShopSelectComponent} from './../shared/shop/index';
import {CurrencySelectComponent} from './../shared/price/index';
import {ModalComponent, ModalResult, ModalAction} from './../shared/modal/index';
import {PriceListVO, ShopVO} from './../shared/model/index';
import {FormValidationEvent, Futures, Future} from './../shared/event/index';
import {Config} from './../shared/config/env.config';

@Component({
  selector: 'yc-shop-pricelist',
  moduleId: module.id,
  templateUrl: 'shop-pricelist.component.html',
  directives: [REACTIVE_FORM_DIRECTIVES, TAB_DIRECTIVES, NgIf, PriceListComponent, ModalComponent, DataControlComponent, ShopSelectComponent, CurrencySelectComponent ],
})

export class ShopPriceListComponent implements OnInit, OnDestroy {

  private searchHelpShow:boolean = false;
  private forceShowAll:boolean = false;

  private pricelist:Array<PriceListVO> = [];
  private pricelistFilter:string;
  private pricelistFilterRequired:boolean = true;
  private pricelistFilterCapped:boolean = false;

  delayedFiltering:Future;
  delayedFilteringMs:number = Config.UI_INPUT_DELAY;
  filterCap:number = Config.UI_FILTER_CAP;
  filterNoCap:number = Config.UI_FILTER_NO_CAP;

  static _selectedShop:ShopVO;
  static _selectedCurrency:string;
  private selectedPricelist:PriceListVO;

  private pricelistEdit:PriceListVO;
  pricelistEditForm:any;
  pricelistEditFormSub:any;
  changedSingle:boolean = true;
  validForSave:boolean = false;

  @ViewChild('deleteConfirmationModalDialog')
  deleteConfirmationModalDialog:ModalComponent;

  @ViewChild('editPricelistModalDialog')
  editPricelistModalDialog:ModalComponent;

  @ViewChild('selectShopModalDialog')
  selectShopModalDialog:ModalComponent;

  @ViewChild('selectCurrencyModalDialog')
  selectCurrencyModalDialog:ModalComponent;

  private deleteValue:String;

  constructor(private _priceService:PricingService,
              fb: FormBuilder) {
    console.debug('ShopPriceListComponent constructed');

    this.pricelistEditForm = fb.group({
      'skuCode': ['', YcValidators.requiredValidCode],
      'skuName': [''],
      'shopCode': ['', Validators.required],
      'currency': ['', Validators.required],
      'pricingPolicy': ['', YcValidators.validCode],
      'quantity': ['', YcValidators.requiredPositiveNumber],
      'regularPrice': ['', YcValidators.requiredNonZeroPositiveNumber],
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
    return { skuPriceId: 0, regularPrice: 0, minimalPrice: undefined, salePrice: undefined, salefrom: null, saleto:null, quantity: 1, currency: this.selectedCurrency, skuCode: '', skuName: '', shopCode: this.selectedShop.code, tag: null, pricingPolicy: null};
  }

  ngOnInit() {
    console.debug('ShopPriceListComponent ngOnInit');
    this.onRefreshHandler();
    let that = this;
    this.delayedFiltering = Futures.perpetual(function() {
      that.getFilteredPricelist();
    }, this.delayedFilteringMs);
    this.formBind();
  }

  ngOnDestroy() {
    console.debug('ShopPriceListComponent ngOnDestroy');
    this.formUnbind();
  }


  formReset():void {
    // Hack to reset NG2 forms see https://github.com/angular/angular/issues/4933
    for(let key in this.pricelistEditForm.controls) {
      this.pricelistEditForm.controls[key]['_pristine'] = true;
      this.pricelistEditForm.controls[key]['_touched'] = false;
    }
  }

  formBind():void {
    this.pricelistEditFormSub = this.pricelistEditForm.statusChanges.subscribe((data:any) => {
      if (this.changedSingle) {
        this.validForSave = this.pricelistEditForm.valid;
      }
    });
  }

  formUnbind():void {
    if (this.pricelistEditFormSub) {
      console.debug('ShopCatalogComponent unbining form');
      this.pricelistEditFormSub.unsubscribe();
    }
  }


  onFormDataChange(event:any) {
    console.debug('ShopPriceListComponent data changed', event);
    this.changedSingle = true;
  }



  protected onShopSelect() {
    console.debug('ShopPriceListComponent onShopSelect');
    this.selectShopModalDialog.show();
  }

  protected onShopSelected(event:ShopVO) {
    console.debug('ShopPriceListComponent onShopSelected');
    this.selectedShop = event;
  }

  protected onSelectShopResult(modalresult: ModalResult) {
    console.debug('ShopPriceListComponent onSelectShopResult modal result is ', modalresult);
    if (this.selectedShop == null) {
      this.selectShopModalDialog.show();
    } else if (this.selectedCurrency == null) {
      this.selectCurrencyModalDialog.show();
    } else {
      this.getFilteredPricelist();
    }
  }

  protected onCurrencySelect() {
    console.debug('ShopPriceListComponent onCurrencySelect');
    this.selectCurrencyModalDialog.show();
  }

  protected onCurrencySelected(event:string) {
    console.debug('ShopPriceListComponent onCurrencySelected');
    this.selectedCurrency = event;
  }

  protected onSelectCurrencyResult(modalresult: ModalResult) {
    console.debug('ShopPriceListComponent onSelectCurrencyResult modal result is ', modalresult);
    if (this.selectedCurrency == null) {
      this.selectCurrencyModalDialog.show();
    } else {
      this.getFilteredPricelist();
    }
  }

  onFilterChange(event:any) {

    this.delayedFiltering.delay();

  }

  getFilteredPricelist() {
    this.pricelistFilterRequired = !this.forceShowAll && (this.pricelistFilter == null || this.pricelistFilter.length < 2);

    console.debug('ShopPriceListComponent getFilteredPricelist' + (this.forceShowAll ? ' forcefully': ''));

    if (this.selectedShop != null && !this.pricelistFilterRequired) {
      let max = this.forceShowAll ? this.filterNoCap : this.filterCap;
      var _sub:any = this._priceService.getFilteredPriceLists(this.selectedShop, this.selectedCurrency, this.pricelistFilter, max).subscribe( allpricelist => {
        console.debug('ShopPriceListComponent getFilteredPricelist', allpricelist);
        this.pricelist = allpricelist;
        this.selectedPricelist = null;
        this.pricelistEdit = null;
        this.changedSingle = false;
        this.validForSave = false;
        this.pricelistFilterCapped = this.pricelist.length >= max;
        _sub.unsubscribe();
      });
    } else {
      this.pricelist = [];
      this.selectedPricelist = null;
      this.pricelistEdit = null;
      this.changedSingle = false;
      this.validForSave = false;
      this.pricelistFilterCapped = false;
    }
  }

  protected onRefreshHandler() {
    console.debug('ShopPriceListComponent refresh handler');
    this.getFilteredPricelist();
  }

  onPricelistSelected(data:PriceListVO) {
    console.debug('ShopPriceListComponent onPricelistSelected', data);
    this.selectedPricelist = data;
  }

  protected onSearchHelpToggle() {
    this.searchHelpShow = !this.searchHelpShow;
  }

  protected onSearchTag() {
    this.pricelistFilter = '#tag';
    this.searchHelpShow = false;
  }

  protected onSearchDate() {
    this.pricelistFilter = UiUtil.exampleDateSearch();
    this.searchHelpShow = false;
  }

  protected onForceShowAll() {
    this.forceShowAll = !this.forceShowAll;
    this.getFilteredPricelist();
  }

  protected onRowNew() {
    console.debug('ShopPriceListComponent onRowNew handler');
    this.changedSingle = false;
    this.validForSave = false;
    this.formReset();
    this.pricelistEdit = this.newPricelistInstance();
    this.editPricelistModalDialog.show();
  }

  protected onRowDelete(row:PriceListVO) {
    console.debug('ShopPriceListComponent onRowDelete handler', row);
    this.deleteValue = row.skuCode;
    this.deleteConfirmationModalDialog.show();
  }

  protected onRowDeleteSelected() {
    if (this.selectedPricelist != null) {
      this.onRowDelete(this.selectedPricelist);
    }
  }


  protected onRowEditPricelist(row:PriceListVO) {
    console.debug('ShopPriceListComponent onRowEditPricelist handler', row);
    this.formReset();
    this.pricelistEdit = Util.clone(row);
    this.changedSingle = false;
    this.validForSave = false;
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

  protected onSaveHandler() {

    if (this.validForSave && this.changedSingle) {

      if (this.pricelistEdit != null) {

        console.debug('ShopPriceListComponent Save handler pricelist', this.pricelistEdit);

        var _sub:any = this._priceService.savePriceList(this.pricelistEdit).subscribe(
            rez => {
              _sub.unsubscribe();
              let pk = this.pricelistEdit.skuPriceId;
              console.debug('ShopPriceListComponent pricelist changed', rez);
              this.changedSingle = false;
              this.selectedPricelist = rez;
              this.pricelistEdit = null;
              if (pk == 0) {
                this.pricelistFilter = rez.skuCode;
              }
              this.getFilteredPricelist();
          }
        );
      }

    }

  }

  protected onDiscardEventHandler() {
    console.debug('ShopPriceListComponent discard handler');
    if (this.selectedPricelist != null) {
      this.onRowEditSelected();
    } else {
      this.onRowNew();
    }
  }

  protected onEditPricelistResult(modalresult: ModalResult) {
    console.debug('ShopPriceListComponent onEditPricelistResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {

      this.onSaveHandler();

    } else {

      this.pricelistEdit = null;

    }
  }

  protected onDeleteConfirmationResult(modalresult: ModalResult) {
    console.debug('ShopPriceListComponent onDeleteConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {

      if (this.selectedPricelist != null) {
        console.debug('ShopPriceListComponent onDeleteConfirmationResult', this.selectedPricelist);

        var _sub:any = this._priceService.removePriceList(this.selectedPricelist).subscribe(res => {
          _sub.unsubscribe();
          console.debug('ShopPriceListComponent removePricelist', this.selectedPricelist);
          this.selectedPricelist = null;
          this.pricelistEdit = null;
          this.getFilteredPricelist();
        });
      }
    }
  }

}
