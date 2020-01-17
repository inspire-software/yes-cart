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
import { ShopEventBus, PricingService, UserEventBus, Util } from './../shared/services/index';
import { PromotionTestConfigComponent } from './components/index';
import { ModalComponent, ModalResult, ModalAction } from './../shared/modal/index';
import { ProductSkuSelectComponent } from './../shared/catalog/index';
import { CountrySelectComponent, CountryStateSelectComponent } from './../shared/shipping/index';
import { TaxSelectComponent } from './../shared/price/index';
import {
  TaxVO, ShopVO, TaxConfigVO, PromotionTestVO, CartVO,
  ProductSkuVO, CountryInfoVO, StateVO,
  Pair, SearchResultVO
} from './../shared/model/index';
import { FormValidationEvent, Futures, Future } from './../shared/event/index';
import { Config } from './../shared/config/env.config';
import { UiUtil } from './../shared/ui/index';
import { LogUtil } from './../shared/log/index';
import { CookieUtil } from './../shared/cookies/index';

@Component({
  selector: 'yc-shop-taxconfigs',
  moduleId: module.id,
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

  private searchHelpTaxConfigShow:boolean = false;
  private forceShowAll:boolean = false;
  private viewMode:string = ShopTaxConfigsComponent.CONFIGS;

  private taxconfigs:SearchResultVO<TaxConfigVO>;
  private taxconfigsFilter:string;
  private taxconfigsFilterRequired:boolean = true;

  private delayedFilteringTaxConfig:Future;
  private delayedFilteringMs:number = Config.UI_INPUT_DELAY;

  private selectedTaxconfig:TaxConfigVO;

  private taxconfigEdit:TaxConfigVO;
  private taxconfigEditForm:any;
  private validForSaveTaxconfig:boolean = true;

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

  @ViewChild('selectCountryModalDialog')
  private selectCountryModalDialog:CountrySelectComponent;

  @ViewChild('selectStateModalDialog')
  private selectStateModalDialog:CountryStateSelectComponent;

  @ViewChild('selectTaxModalDialog')
  private selectTaxModalDialog:TaxSelectComponent;

  @ViewChild('runTestModalDialog')
  private runTestModalDialog:PromotionTestConfigComponent;

  private deleteValue:String;

  private loading:boolean = false;

  private testCart:CartVO;

  private userSub:any;

  constructor(private _taxService:PricingService,
              fb: FormBuilder) {
    LogUtil.debug('ShopTaxConfigsComponent constructed');

    this.taxconfigEditForm = fb.group({
       'productCode': ['', YcValidators.validCode],
       'stateCode': ['', YcValidators.nonBlankTrimmed64],
       'countryCode': ['', YcValidators.validCountryCode],
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


  protected presetFromCookie() {

    if (this.selectedShop == null) {
      let shopCode = CookieUtil.readCookie(ShopTaxConfigsComponent.COOKIE_SHOP, null);
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
      let curr = CookieUtil.readCookie(ShopTaxConfigsComponent.COOKIE_CURRENCY, null);
      if (curr != null) {
        this.selectedCurrency = curr;
        LogUtil.debug('ShopTaxConfigsComponent ngOnInit presetting currency from cookie', curr);
      }
    }

  }


  protected onShopSelect() {
    LogUtil.debug('ShopTaxConfigsComponent onShopSelect');
    this.selectShopModalDialog.show();
  }

  protected onShopSelected(event:ShopVO) {
    LogUtil.debug('ShopTaxConfigsComponent onShopSelected');
    this.selectedShop = event;
    if (this.selectedShop != null) {
      this.selectedShopCode = event.code;
      CookieUtil.createCookie(ShopTaxConfigsComponent.COOKIE_SHOP, this.selectedShop.code, 360);
    } else {
      this.selectedShopCode = null;
    }
  }

  protected onSelectShopResult(modalresult: ModalResult) {
    LogUtil.debug('ShopTaxConfigsComponent onSelectShopResult modal result is ', modalresult);
    if (this.selectedShop == null) {
      this.selectShopModalDialog.show();
    } else if (this.selectedCurrency == null) {
      this.selectCurrencyModalDialog.show();
    } else {
      this.getFilteredTaxConfig();
    }
  }

  protected onCurrencySelect() {
    LogUtil.debug('ShopTaxConfigsComponent onCurrencySelect');
    this.selectCurrencyModalDialog.show();
  }

  protected onCurrencySelected(event:string) {
    LogUtil.debug('ShopTaxConfigsComponent onCurrencySelected');
    this.selectedCurrency = event;
    if (this.selectedCurrency != null) {
      CookieUtil.createCookie(ShopTaxConfigsComponent.COOKIE_CURRENCY, this.selectedCurrency, 360);
    }
  }

  protected onSelectCurrencyResult(modalresult: ModalResult) {
    LogUtil.debug('ShopTaxConfigsComponent onSelectCurrencyResult modal result is ', modalresult);
    if (this.selectedCurrency == null) {
      this.selectCurrencyModalDialog.show();
    } else {
      this.getFilteredTaxConfig();
    }
  }

  protected onTestRules() {
    LogUtil.debug('ShopTaxConfigsComponent onTestRules');
    this.runTestModalDialog.showDialog();
  }

  onRunTestResult(event:PromotionTestVO) {
    LogUtil.debug('ShopTaxConfigsComponent onRunTestResult', event);
    if (event != null) {
      this.loading = true;
      let _sub:any = this._taxService.testPromotions(this.selectedShop, this.selectedCurrency, event).subscribe(
        cart => {
          _sub.unsubscribe();
          this.loading = false;
          LogUtil.debug('ShopTaxConfigsComponent onTestRules', cart);
          this.viewMode = ShopTaxConfigsComponent.PRICELIST_TEST;
          this.testCart = cart;
        }
      );

    }
  }

  protected onTaxConfigFilterChange(event:any) {
    this.taxconfigs.searchContext.start = 0; // changing filter means we need to start from first page
    this.delayedFilteringTaxConfig.delay();
  }

  protected onRefreshHandler() {
    LogUtil.debug('ShopTaxConfigsComponent refresh handler');
    if (UserEventBus.getUserEventBus().current() != null) {
      this.presetFromCookie();
      if (this.viewMode == ShopTaxConfigsComponent.CONFIGS) {
        this.getFilteredTaxConfig();
      }
    }
  }

  protected onPageSelected(page:number) {
    LogUtil.debug('ShopTaxConfigsComponent onPageSelected', page);
    this.taxconfigs.searchContext.start = page;
    this.delayedFilteringTaxConfig.delay();
  }

  protected onSortSelected(sort:Pair<string, boolean>) {
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

  protected onTaxconfigSelected(data:TaxConfigVO) {
    LogUtil.debug('ShopTaxConfigsComponent onTaxconfigSelected', data);
    this.selectedTaxconfig = data;
  }

  protected onSearchHelpToggleTaxConfig() {
    this.searchHelpTaxConfigShow = !this.searchHelpTaxConfigShow;
  }

  protected onSearchSKU() {
    this.taxconfigsFilter = '#!';
    this.searchHelpTaxConfigShow = false;
    this.getFilteredTaxConfig();
  }

  protected onSearchSKUExact() {
    this.selectProductModalSkuDialog.showDialog();
  }

  protected onProductSkuSelected(event:FormValidationEvent<ProductSkuVO>) {
    LogUtil.debug('ShopTaxConfigsComponent onProductSkuSelected');
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


  protected onCountryExact() {
    this.selectCountryModalDialog.showDialog();
  }

  protected onCountrySelected(event:FormValidationEvent<CountryInfoVO>) {
    LogUtil.debug('ShopTaxConfigsComponent onCountrySelected');
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

  protected onStateExact() {
    this.selectStateModalDialog.showDialog();
  }

  protected onStateSelected(event:FormValidationEvent<StateVO>) {
    LogUtil.debug('ShopTaxConfigsComponent onCountrySelected');
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


  protected onTaxExact() {
    this.selectTaxModalDialog.showDialog();
  }

  protected onTaxSelected(event:FormValidationEvent<TaxVO>) {
    LogUtil.debug('ShopTaxConfigsComponent onTaxSelected');
    if (event.valid) {
      if (this.taxconfigEdit != null) {
        this.taxconfigEdit.tax = event.source;
      }
    }
  }

  protected onForceShowAll() {
    this.forceShowAll = !this.forceShowAll;
    if (this.viewMode == ShopTaxConfigsComponent.CONFIGS) {
      this.getFilteredTaxConfig();
    }
  }

  protected onRowNew() {
    LogUtil.debug('ShopTaxConfigsComponent onRowNew handler');
    UiUtil.formInitialise(this, 'taxconfigEditForm', 'taxconfigEdit', this.newInstance());
    this.editTaxconfigModalDialog.show();
  }

  protected onRowTaxConfigDelete(row:TaxConfigVO) {
    LogUtil.debug('ShopTaxConfigsComponent onRowTaxConfigDelete handler', row);
    this.deleteValue =
      (row.countryCode ? row.countryCode + '/' : '-') +
      (row.stateCode ? row.stateCode + '/' : '-') +
      (row.productCode ? row.productCode : '-');
    this.deleteConfirmationModalDialog.show();
  }

  protected onRowDeleteSelected() {
    if (this.selectedTaxconfig != null) {
      this.onRowTaxConfigDelete(this.selectedTaxconfig);
    }
  }

  protected onRowEditTaxconfig(row:TaxConfigVO) {
    LogUtil.debug('ShopTaxConfigsComponent onRowEditTaxconfig handler', row);
    this.validForSaveTaxconfig = true;
    UiUtil.formInitialise(this, 'taxconfigEditForm', 'taxconfigEdit', Util.clone(row));
    this.editTaxconfigModalDialog.show();
  }

  protected onRowCopySelected() {
    if (this.selectedTaxconfig != null) {
      let copyCfg:TaxConfigVO = Util.clone(this.selectedTaxconfig);
      copyCfg.taxConfigId = 0;
      this.onRowEditTaxconfig(copyCfg);
    }
  }

  protected onBackToList() {
    LogUtil.debug('ShopTaxConfigsComponent onBackToList handler');
    if (this.viewMode === ShopTaxConfigsComponent.PRICELIST_TEST) {
      this.viewMode = ShopTaxConfigsComponent.CONFIGS;
    }
  }

  protected onSaveHandler() {

    if (this.taxconfigEdit != null) {

      if (this.validForSaveTaxconfig) {

        LogUtil.debug('ShopTaxConfigsComponent Save handler config', this.taxconfigEdit);

        this.loading = true;
        let _sub:any = this._taxService.createTaxConfig(this.taxconfigEdit).subscribe(
            rez => {
              _sub.unsubscribe();
              let pk = this.taxconfigEdit.taxConfigId;
              LogUtil.debug('ShopTaxConfigsComponent config changed', rez);
              this.selectedTaxconfig = rez;
              if (pk == 0) {
                if (rez.productCode != null) {
                  this.taxconfigsFilter = '!' + rez.productCode;
                } else if (rez.stateCode != null) {
                  this.taxconfigsFilter = '@' + rez.stateCode;
                } else if (rez.countryCode != null) {
                  this.taxconfigsFilter = '@' + rez.countryCode;
                }
              }
              this.loading = false;
              this.getFilteredTaxConfig();
          }
        );
      }

    }

  }

  protected onDiscardEventHandler() {
    LogUtil.debug('ShopTaxConfigsComponent discard handler');
    this.onRowNew();
  }

  protected onEditTaxResult(modalresult: ModalResult) {
    LogUtil.debug('ShopTaxConfigsComponent onEditTaxResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {

      this.onSaveHandler();

    } else {

      if (this.taxconfigEdit != null) {
        this.taxconfigEdit = null;
      }

    }
  }

  protected onDeleteConfirmationResult(modalresult: ModalResult) {
    LogUtil.debug('ShopTaxConfigsComponent onDeleteConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {

      if (this.selectedTaxconfig != null) {
        LogUtil.debug('ShopTaxConfigsComponent onDeleteConfirmationResult', this.selectedTaxconfig);

        this.loading = true;
        let _sub:any = this._taxService.removeTaxConfig(this.selectedTaxconfig).subscribe(res => {
          _sub.unsubscribe();
          LogUtil.debug('ShopTaxConfigsComponent removeTax', this.selectedTaxconfig);
          this.selectedTaxconfig = null;
          this.loading = false;
          this.getFilteredTaxConfig();
        });

      }
    }
  }


  protected onClearFilterTaxConfig() {

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

      let _sub:any = this._taxService.getFilteredTaxConfig(this.taxconfigs.searchContext).subscribe( alltaxes => {
        LogUtil.debug('ShopTaxConfigsComponent getFilteredTaxConfig', alltaxes);
        this.taxconfigs = alltaxes;
        this.selectedTaxconfig = null;
        this.validForSaveTaxconfig = true;
        this.viewMode = ShopTaxConfigsComponent.CONFIGS;
        _sub.unsubscribe();
        this.loading = false;
      });
    } else {
      this.taxconfigs = this.newSearchResultInstance();
      this.selectedTaxconfig = null;
      this.validForSaveTaxconfig = true;
      this.viewMode = ShopTaxConfigsComponent.CONFIGS;
    }
  }

}
