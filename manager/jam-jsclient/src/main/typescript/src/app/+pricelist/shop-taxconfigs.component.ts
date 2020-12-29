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
import { Component, OnInit, OnDestroy, ViewChild } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { CustomValidators } from './../shared/validation/validators';
import { ShopEventBus, PricingService, UserEventBus, Util } from './../shared/services/index';
import { PromotionTestConfigComponent } from './components/index';
import { ModalComponent, ModalResult, ModalAction } from './../shared/modal/index';
import { ProductSkuSelectComponent } from './../shared/catalog/index';
import { CarrierSlaSelectComponent } from './../shared/shipping/index';
import { CountrySelectComponent, CountryStateSelectComponent } from './../shared/shipping/index';
import { TaxSelectComponent } from './../shared/price/index';
import {
  TaxVO, ShopVO, TaxConfigVO, PromotionTestVO, CartVO,
  ProductSkuVO, CarrierSlaInfoVO, CountryInfoVO, StateVO,
  Pair, SearchResultVO
} from './../shared/model/index';
import { FormValidationEvent, Futures, Future } from './../shared/event/index';
import { Config } from './../../environments/environment';
import { UiUtil } from './../shared/ui/index';
import { LogUtil } from './../shared/log/index';
import { StorageUtil } from './../shared/storage/index';

@Component({
  selector: 'cw-shop-taxconfigs',
  templateUrl: 'shop-taxconfigs.component.html',
})

export class ShopTaxConfigsComponent implements OnInit, OnDestroy {

  private static COOKIE_SHOP:string = 'ADM_UI_TAX_SHOP';
  private static COOKIE_CURRENCY:string = 'ADM_UI_TAX_CURR';

  private static _selectedShopCode:string;
  private static _selectedShop:ShopVO;
  private static _selectedCurrency:string;

  private static CONFIGS:string = 'taxconfigs';
  private static PRICELIST_TEST:string = 'pricelisttest';

  public searchHelpTaxConfigShow:boolean = false;
  public forceShowAll:boolean = false;
  public viewMode:string = ShopTaxConfigsComponent.CONFIGS;

  public taxconfigs:SearchResultVO<TaxConfigVO>;
  public taxconfigsFilter:string;
  public taxconfigsFilterRequired:boolean = true;

  private delayedFilteringTaxConfig:Future;
  private delayedFilteringMs:number = Config.UI_INPUT_DELAY;

  public selectedTaxconfig:TaxConfigVO;

  public taxconfigEdit:TaxConfigVO;
  public taxconfigEditForm:any;
  public validForSaveTaxconfig:boolean = true;

  @ViewChild('deleteConfirmationModalDialog')
  private deleteConfirmationModalDialog:ModalComponent;

  @ViewChild('editTaxconfigModalDialog')
  private editTaxconfigModalDialog:ModalComponent;

  @ViewChild('selectShopModalDialog')
  private selectShopModalDialog:ModalComponent;

  @ViewChild('selectCurrencyModalDialog')
  private selectCurrencyModalDialog:ModalComponent;

  @ViewChild('selectProductModalSkuDialog')
  private selectProductModalSkuDialog:ProductSkuSelectComponent;

  @ViewChild('carrierSlaSelectDialog')
  private carrierSlaSelectDialog:CarrierSlaSelectComponent;

  @ViewChild('selectCountryModalDialog')
  private selectCountryModalDialog:CountrySelectComponent;

  @ViewChild('selectStateModalDialog')
  private selectStateModalDialog:CountryStateSelectComponent;

  @ViewChild('selectTaxModalDialog')
  private selectTaxModalDialog:TaxSelectComponent;

  @ViewChild('runTestModalDialog')
  private runTestModalDialog:PromotionTestConfigComponent;

  public deleteValue:String;

  public loading:boolean = false;

  public testCart:CartVO;

  private userSub:any;

  constructor(private _taxService:PricingService,
              fb: FormBuilder) {
    LogUtil.debug('ShopTaxConfigsComponent constructed');

    this.taxconfigEditForm = fb.group({
      'productCode': ['', CustomValidators.validCode],
      'stateCode': ['', CustomValidators.nonBlankTrimmed64],
      'countryCode': ['', CustomValidators.validCountryCode],
      'tax': ['', Validators.required],
    });

    this.taxconfigs = this.newSearchResultInstance();
  }

  get selectedShop():ShopVO {
    return ShopTaxConfigsComponent._selectedShop;
  }

  set selectedShop(selectedShop:ShopVO) {
    ShopTaxConfigsComponent._selectedShop = selectedShop;
  }

  get selectedShopCode(): string {
    return ShopTaxConfigsComponent._selectedShopCode;
  }

  set selectedShopCode(value: string) {
    ShopTaxConfigsComponent._selectedShopCode = value;
  }

  get selectedCurrency():string {
    return ShopTaxConfigsComponent._selectedCurrency;
  }

  set selectedCurrency(selectedCurrency:string) {
    ShopTaxConfigsComponent._selectedCurrency = selectedCurrency;
  }

  newInstance():TaxConfigVO {
    return { taxConfigId: 0, tax: null, productCode: null, stateCode: null, countryCode: null, guid: null};
    //return { taxConfigId: 0, taxId: this.selectedTax, productCode: null, stateCode: null, countryCode: null, guid: null};
  }

  newSearchResultInstance():SearchResultVO<TaxConfigVO> {
    return {
      searchContext: {
        parameters: {
          filter: []
        },
        start: 0,
        size: Config.UI_TABLE_PAGE_SIZE,
        sortBy: null,
        sortDesc: false
      },
      items: [],
      total: 0
    };
  }

  ngOnInit() {
    LogUtil.debug('ShopTaxConfigsComponent ngOnInit');

    this.onRefreshHandler();

    this.userSub = UserEventBus.getUserEventBus().userUpdated$.subscribe(user => {
      this.presetFromCookie();
    });

    let that = this;
    this.delayedFilteringTaxConfig = Futures.perpetual(function() {
      that.getFilteredTaxConfig();
    }, this.delayedFilteringMs);
    this.formBind();
  }

  ngOnDestroy() {
    LogUtil.debug('ShopTaxConfigsComponent ngOnDestroy');
    this.formUnbind();
    if (this.userSub) {
      this.userSub.unsubscribe();
    }
  }

  formBind():void {
    UiUtil.formBind(this, 'taxconfigEditForm', 'formChangeTaxconfig', false);
  }

  formUnbind():void {
    UiUtil.formUnbind(this, 'taxconfigEditForm');
  }

  formChangeTaxconfig():void {
    LogUtil.debug('ShopTaxConfigsComponent formChangeTaxconfig', this.taxconfigEditForm.valid, this.taxconfigEdit);
    this.validForSaveTaxconfig = this.taxconfigEditForm.valid;
  }


  presetFromCookie() {

    if (this.selectedShop == null) {
      let shopCode = StorageUtil.readValue(ShopTaxConfigsComponent.COOKIE_SHOP, null);
      if (shopCode != null) {
        let shops = ShopEventBus.getShopEventBus().currentAll();
        if (shops != null) {
          shops.forEach(shop => {
            if (shop.code == shopCode) {
              this.selectedShop = shop;
              this.selectedShopCode = shop.code;
              LogUtil.debug('ShopTaxConfigsComponent ngOnInit presetting shop from cookie', shop);
            }
          });
        }
      }
    }
    if (this.selectedCurrency == null) {
      let curr = StorageUtil.readValue(ShopTaxConfigsComponent.COOKIE_CURRENCY, null);
      if (curr != null) {
        this.selectedCurrency = curr;
        LogUtil.debug('ShopTaxConfigsComponent ngOnInit presetting currency from cookie', curr);
      }
    }

  }


  onShopSelect() {
    LogUtil.debug('ShopTaxConfigsComponent onShopSelect');
    this.selectShopModalDialog.show();
  }

  onShopSelected(event:ShopVO) {
    LogUtil.debug('ShopTaxConfigsComponent onShopSelected');
    this.selectedShop = event;
    if (this.selectedShop != null) {
      this.selectedShopCode = event.code;
      StorageUtil.saveValue(ShopTaxConfigsComponent.COOKIE_SHOP, this.selectedShop.code);
    } else {
      this.selectedShopCode = null;
    }
  }

  onSelectShopResult(modalresult: ModalResult) {
    LogUtil.debug('ShopTaxConfigsComponent onSelectShopResult modal result is ', modalresult);
    if (this.selectedShop == null) {
      this.selectShopModalDialog.show();
    } else if (this.selectedCurrency == null) {
      this.selectCurrencyModalDialog.show();
    } else {
      this.getFilteredTaxConfig();
    }
  }

  onCurrencySelect() {
    LogUtil.debug('ShopTaxConfigsComponent onCurrencySelect');
    this.selectCurrencyModalDialog.show();
  }

  onCurrencySelected(event:string) {
    LogUtil.debug('ShopTaxConfigsComponent onCurrencySelected');
    this.selectedCurrency = event;
    if (this.selectedCurrency != null) {
      StorageUtil.saveValue(ShopTaxConfigsComponent.COOKIE_CURRENCY, this.selectedCurrency);
    }
  }

  onSelectCurrencyResult(modalresult: ModalResult) {
    LogUtil.debug('ShopTaxConfigsComponent onSelectCurrencyResult modal result is ', modalresult);
    if (this.selectedCurrency == null) {
      this.selectCurrencyModalDialog.show();
    } else {
      this.getFilteredTaxConfig();
    }
  }

  onTestRules() {
    LogUtil.debug('ShopTaxConfigsComponent onTestRules');
    this.runTestModalDialog.showDialog();
  }

  onRunTestResult(event:PromotionTestVO) {
    LogUtil.debug('ShopTaxConfigsComponent onRunTestResult', event);
    if (event != null && this.selectedShop != null) {
      this.loading = true;

      event.shopCode = this.selectedShop.code;
      event.currency = this.selectedCurrency;

      this._taxService.testPromotions(event).subscribe(
        cart => {
          this.loading = false;
          LogUtil.debug('ShopTaxConfigsComponent onTestRules', cart);
          this.viewMode = ShopTaxConfigsComponent.PRICELIST_TEST;
          this.testCart = cart;
        }
      );

    }
  }

  onTaxConfigFilterChange(event:any) {
    this.taxconfigs.searchContext.start = 0; // changing filter means we need to start from first page
    this.delayedFilteringTaxConfig.delay();
  }

  onRefreshHandler() {
    LogUtil.debug('ShopTaxConfigsComponent refresh handler');
    if (UserEventBus.getUserEventBus().current() != null) {
      this.presetFromCookie();
      if (this.viewMode == ShopTaxConfigsComponent.CONFIGS) {
        this.getFilteredTaxConfig();
      }
    }
  }

  onPageSelected(page:number) {
    LogUtil.debug('ShopTaxConfigsComponent onPageSelected', page);
    this.taxconfigs.searchContext.start = page;
    this.delayedFilteringTaxConfig.delay();
  }

  onSortSelected(sort:Pair<string, boolean>) {
    LogUtil.debug('ShopTaxConfigsComponent ononSortSelected', sort);
    if (sort == null) {
      this.taxconfigs.searchContext.sortBy = null;
      this.taxconfigs.searchContext.sortDesc = false;
    } else {
      this.taxconfigs.searchContext.sortBy = sort.first;
      this.taxconfigs.searchContext.sortDesc = sort.second;
    }
    this.delayedFilteringTaxConfig.delay();
  }

  onTaxconfigSelected(data:TaxConfigVO) {
    LogUtil.debug('ShopTaxConfigsComponent onTaxconfigSelected', data);
    this.selectedTaxconfig = data;
  }

  onSearchHelpToggleTaxConfig() {
    this.searchHelpTaxConfigShow = !this.searchHelpTaxConfigShow;
  }

  onSearchSKU() {
    this.taxconfigsFilter = '#!';
    this.searchHelpTaxConfigShow = false;
    this.getFilteredTaxConfig();
  }

  onSearchSKUExact() {
    this.selectProductModalSkuDialog.showDialog();
  }

  onProductSkuSelected(event:FormValidationEvent<ProductSkuVO>) {
    LogUtil.debug('ShopTaxConfigsComponent onProductSkuSelected', event);
    if (event.valid) {
      if (this.taxconfigEdit != null) {
        this.taxconfigEdit.productCode = event.source.code;
      } else {
        this.taxconfigsFilter = '!' + event.source.code;
        this.searchHelpTaxConfigShow = false;
        this.getFilteredTaxConfig();
      }
    }
  }



  onSearchSLAExact() {
    this.carrierSlaSelectDialog.showDialog();
  }


  onCarrierSlaSelected(event:FormValidationEvent<CarrierSlaInfoVO>) {
    LogUtil.debug('ShopTaxConfigsComponent onCarrierSlaSelected', event);
    if (this.taxconfigEdit != null) {
      this.taxconfigEdit.productCode = event.source.code;
    } else {
      this.taxconfigsFilter = '!' + event.source.code;
      this.searchHelpTaxConfigShow = false;
      this.getFilteredTaxConfig();
    }
  }


  onCountryExact() {
    this.selectCountryModalDialog.showDialog();
  }

  onCountrySelected(event:FormValidationEvent<CountryInfoVO>) {
    LogUtil.debug('ShopTaxConfigsComponent onCountrySelected', event);
    if (event.valid) {
      if (this.taxconfigEdit != null) {
        this.taxconfigEdit.stateCode = null;
        this.taxconfigEdit.countryCode = event.source.countryCode;
      } else {
        this.taxconfigsFilter = '@' + event.source.countryCode;
        this.searchHelpTaxConfigShow = false;
        this.getFilteredTaxConfig();
      }
    }
  }

  onStateExact() {
    this.selectStateModalDialog.showDialog();
  }

  onStateSelected(event:FormValidationEvent<StateVO>) {
    LogUtil.debug('ShopTaxConfigsComponent onCountrySelected', event);
    if (event.valid) {
      if (this.taxconfigEdit != null) {
        this.taxconfigEdit.stateCode = event.source.stateCode;
        this.taxconfigEdit.countryCode = event.source.countryCode;
      } else {
        this.taxconfigsFilter = '@' + event.source.stateCode;
        this.searchHelpTaxConfigShow = false;
        this.getFilteredTaxConfig();
      }
    }
  }


  onTaxExact() {
    this.selectTaxModalDialog.showDialog();
  }

  onTaxSelected(event:FormValidationEvent<TaxVO>) {
    LogUtil.debug('ShopTaxConfigsComponent onTaxSelected', event);
    if (event.valid) {
      if (this.taxconfigEdit != null) {
        this.taxconfigEdit.tax = event.source;
      } else {
        this.taxconfigsFilter = '^' + event.source.code;
        this.searchHelpTaxConfigShow = false;
        this.getFilteredTaxConfig();
      }
    }
  }

  onForceShowAll() {
    this.forceShowAll = !this.forceShowAll;
    if (this.viewMode == ShopTaxConfigsComponent.CONFIGS) {
      this.getFilteredTaxConfig();
    }
  }

  onRowNew() {
    LogUtil.debug('ShopTaxConfigsComponent onRowNew handler');
    UiUtil.formInitialise(this, 'taxconfigEditForm', 'taxconfigEdit', this.newInstance());
    this.editTaxconfigModalDialog.show();
  }

  onRowTaxConfigDelete(row:TaxConfigVO) {
    LogUtil.debug('ShopTaxConfigsComponent onRowTaxConfigDelete handler', row);
    this.deleteValue =
      (row.countryCode ? row.countryCode + '/' : '-') +
      (row.stateCode ? row.stateCode + '/' : '-') +
      (row.productCode ? row.productCode : '-');
    this.deleteConfirmationModalDialog.show();
  }

  onRowDeleteSelected() {
    if (this.selectedTaxconfig != null) {
      this.onRowTaxConfigDelete(this.selectedTaxconfig);
    }
  }

  onRowEditTaxconfig(row:TaxConfigVO) {
    LogUtil.debug('ShopTaxConfigsComponent onRowEditTaxconfig handler', row);
    this.validForSaveTaxconfig = true;
    UiUtil.formInitialise(this, 'taxconfigEditForm', 'taxconfigEdit', Util.clone(row));
    this.editTaxconfigModalDialog.show();
  }

  onRowCopySelected() {
    if (this.selectedTaxconfig != null) {
      let copyCfg:TaxConfigVO = Util.clone(this.selectedTaxconfig);
      copyCfg.taxConfigId = 0;
      this.onRowEditTaxconfig(copyCfg);
    }
  }

  onBackToList() {
    LogUtil.debug('ShopTaxConfigsComponent onBackToList handler');
    if (this.viewMode === ShopTaxConfigsComponent.PRICELIST_TEST) {
      this.viewMode = ShopTaxConfigsComponent.CONFIGS;
    }
  }

  onSaveHandler() {

    if (this.taxconfigEdit != null) {

      if (this.validForSaveTaxconfig) {

        LogUtil.debug('ShopTaxConfigsComponent Save handler config', this.taxconfigEdit);

        this.loading = true;
        this._taxService.createTaxConfig(this.taxconfigEdit).subscribe(
            rez => {
              LogUtil.debug('ShopTaxConfigsComponent config changed', rez);
              this.selectedTaxconfig = rez;
              this.taxconfigEdit = null;
              this.validForSaveTaxconfig = false;
              this.loading = false;
              this.getFilteredTaxConfig();
          }
        );
      }

    }

  }

  onDiscardEventHandler() {
    LogUtil.debug('ShopTaxConfigsComponent discard handler');
    this.onRowNew();
  }

  onEditTaxResult(modalresult: ModalResult) {
    LogUtil.debug('ShopTaxConfigsComponent onEditTaxResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {

      this.onSaveHandler();

    } else {

      if (this.taxconfigEdit != null) {
        this.taxconfigEdit = null;
      }

    }
  }

  onDeleteConfirmationResult(modalresult: ModalResult) {
    LogUtil.debug('ShopTaxConfigsComponent onDeleteConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {

      if (this.selectedTaxconfig != null) {
        LogUtil.debug('ShopTaxConfigsComponent onDeleteConfirmationResult', this.selectedTaxconfig);

        this.loading = true;
        this._taxService.removeTaxConfig(this.selectedTaxconfig).subscribe(res => {
          LogUtil.debug('ShopTaxConfigsComponent removeTax', this.selectedTaxconfig);
          this.selectedTaxconfig = null;
          this.loading = false;
          this.getFilteredTaxConfig();
        });

      }
    }
  }


  onClearFilterTaxConfig() {

    this.taxconfigsFilter = '';
    this.delayedFilteringTaxConfig.delay();

  }

  private getFilteredTaxConfig() {
    this.taxconfigsFilterRequired = !this.forceShowAll && (this.taxconfigsFilter == null || this.taxconfigsFilter.length < 2);

    LogUtil.debug('ShopTaxConfigsComponent getFilteredTaxConfig' + (this.forceShowAll ? ' forcefully': ''));

    if (this.selectedShop != null && this.selectedCurrency != null && !this.taxconfigsFilterRequired) {
      this.loading = true;

      this.taxconfigs.searchContext.parameters.filter = [ this.taxconfigsFilter ];
      this.taxconfigs.searchContext.parameters.shopCode = [ this.selectedShop.code ];
      this.taxconfigs.searchContext.parameters.currency = [ this.selectedCurrency ];
      this.taxconfigs.searchContext.size = Config.UI_TABLE_PAGE_SIZE;

      this._taxService.getFilteredTaxConfig(this.taxconfigs.searchContext).subscribe( alltaxes => {
        LogUtil.debug('ShopTaxConfigsComponent getFilteredTaxConfig', alltaxes);
        this.taxconfigs = alltaxes;
        this.selectedTaxconfig = null;
        this.taxconfigEdit = null;
        this.validForSaveTaxconfig = true;
        this.viewMode = ShopTaxConfigsComponent.CONFIGS;
        this.loading = false;
      });
    } else {
      this.taxconfigs = this.newSearchResultInstance();
      this.selectedTaxconfig = null;
      this.taxconfigEdit = null;
      this.validForSaveTaxconfig = true;
      this.viewMode = ShopTaxConfigsComponent.CONFIGS;
    }
  }

}
