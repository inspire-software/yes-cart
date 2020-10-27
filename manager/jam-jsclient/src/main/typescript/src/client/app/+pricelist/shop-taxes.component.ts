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
import { TaxVO, ShopVO, PromotionTestVO, CartVO, Pair, SearchResultVO, ValidationRequestVO } from './../shared/model/index';
import { Futures, Future } from './../shared/event/index';
import { Config } from './../shared/config/env.config';
import { UiUtil } from './../shared/ui/index';
import { LogUtil } from './../shared/log/index';
import { CookieUtil } from './../shared/cookies/index';

@Component({
  selector: 'yc-shop-taxes',
  moduleId: module.id,
  templateUrl: 'shop-taxes.component.html',
})

export class ShopTaxesComponent implements OnInit, OnDestroy {

  private static COOKIE_SHOP:string = 'ADM_UI_TAX_SHOP';
  private static COOKIE_CURRENCY:string = 'ADM_UI_TAX_CURR';

  private static _selectedShopCode:string;
  private static _selectedShop:ShopVO;
  private static _selectedCurrency:string;

  private static TAXES:string = 'taxes';
  private static PRICELIST_TEST:string = 'pricelisttest';

  private searchHelpTaxShow:boolean = false;
  //private forceShowAll:boolean = false;
  private viewMode:string = ShopTaxesComponent.TAXES;

  private taxes:SearchResultVO<TaxVO>;
  private taxesFilter:string;
  private taxesFilterRequired:boolean = true;

  private delayedFilteringTax:Future;
  private delayedFilteringMs:number = Config.UI_INPUT_DELAY;

  private selectedTax:TaxVO;

  private taxEdit:TaxVO;
  private taxEditForm:any;
  private validForSaveTax:boolean = false;

  @ViewChild('deleteConfirmationModalDialog')
  private deleteConfirmationModalDialog:ModalComponent;

  @ViewChild('editTaxModalDialog')
  private editTaxModalDialog:ModalComponent;

  @ViewChild('selectShopModalDialog')
  private selectShopModalDialog:ModalComponent;

  @ViewChild('selectCurrencyModalDialog')
  private selectCurrencyModalDialog:ModalComponent;

  @ViewChild('runTestModalDialog')
  private runTestModalDialog:PromotionTestConfigComponent;

  private deleteValue:String;

  private loading:boolean = false;

  private testCart:CartVO;

  private userSub:any;

  constructor(private _taxService:PricingService,
              fb: FormBuilder) {
    LogUtil.debug('ShopTaxesComponent constructed');

    let that = this;


    let validCode = function(control:any):any {

      let basic = CustomValidators.requiredValidCode(control);
      if (basic == null) {

        let code = control.value;
        if (that.taxEdit == null || !that.taxEditForm || (!that.taxEditForm.dirty && that.taxEdit.taxId > 0)) {
          return null;
        }

        let req:ValidationRequestVO = { subject: 'tax', subjectId: that.taxEdit.taxId, field: 'code', value: code };
        return CustomValidators.validRemoteCheck(control, req);
      }
      return basic;
    };

    this.taxEditForm = fb.group({
      'code': ['', validCode],
      'description': ['', CustomValidators.requiredNonBlankTrimmed],
      'exclusiveOfPrice': [''],
      'taxRate': ['', CustomValidators.requiredPositiveNumber],
      'shopCode': ['', Validators.required],
      'currency': ['', Validators.required],
    });

    this.taxes = this.newSearchResultInstance();
  }

  get selectedShop():ShopVO {
     return ShopTaxesComponent._selectedShop;
  }

  set selectedShop(selectedShop:ShopVO) {
    ShopTaxesComponent._selectedShop = selectedShop;
  }

  get selectedShopCode(): string {
    return ShopTaxesComponent._selectedShopCode;
  }

  set selectedShopCode(value: string) {
    ShopTaxesComponent._selectedShopCode = value;
  }

  get selectedCurrency():string {
     return ShopTaxesComponent._selectedCurrency;
  }

  set selectedCurrency(selectedCurrency:string) {
    ShopTaxesComponent._selectedCurrency = selectedCurrency;
  }

  newTaxInstance():TaxVO {
    return { taxId: 0, taxRate: 0, exclusiveOfPrice: false, shopCode: this.selectedShop.code, currency: this.selectedCurrency, code: '', guid: null, description: null};
  }

  newSearchResultInstance():SearchResultVO<TaxVO> {
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
    LogUtil.debug('ShopTaxesComponent ngOnInit');

    this.onRefreshHandler();

    this.userSub = UserEventBus.getUserEventBus().userUpdated$.subscribe(user => {
      this.presetFromCookie();
    });

    let that = this;
    this.delayedFilteringTax = Futures.perpetual(function() {
      that.getFilteredTax();
    }, this.delayedFilteringMs);
    this.formBind();
  }

  ngOnDestroy() {
    LogUtil.debug('ShopTaxesComponent ngOnDestroy');
    this.formUnbind();
    if (this.userSub) {
      this.userSub.unsubscribe();
    }
  }

  formBind():void {
    UiUtil.formBind(this, 'taxEditForm', 'formChangeTax', false);
  }

  formUnbind():void {
    UiUtil.formUnbind(this, 'taxEditForm');
  }


  formChangeTax():void {
    LogUtil.debug('ShopTaxesComponent formChangeTax', this.taxEditForm.valid, this.taxEdit);
    this.validForSaveTax = this.taxEditForm.valid;
  }


  protected presetFromCookie() {

    if (this.selectedShop == null) {
      let shopCode = CookieUtil.readCookie(ShopTaxesComponent.COOKIE_SHOP, null);
      if (shopCode != null) {
        let shops = ShopEventBus.getShopEventBus().currentAll();
        if (shops != null) {
          shops.forEach(shop => {
            if (shop.code == shopCode) {
              this.selectedShop = shop;
              this.selectedShopCode = shop.code;
              LogUtil.debug('ShopTaxesComponent ngOnInit presetting shop from cookie', shop);
            }
          });
        }
      }
    }
    if (this.selectedCurrency == null) {
      let curr = CookieUtil.readCookie(ShopTaxesComponent.COOKIE_CURRENCY, null);
      if (curr != null) {
        this.selectedCurrency = curr;
        LogUtil.debug('ShopTaxesComponent ngOnInit presetting currency from cookie', curr);
      }
    }

  }


  protected onShopSelect() {
    LogUtil.debug('ShopTaxesComponent onShopSelect');
    this.selectShopModalDialog.show();
  }

  protected onShopSelected(event:ShopVO) {
    LogUtil.debug('ShopTaxesComponent onShopSelected');
    this.selectedShop = event;
    if (this.selectedShop != null) {
      this.selectedShopCode = event.code;
      CookieUtil.createCookie(ShopTaxesComponent.COOKIE_SHOP, this.selectedShop.code, 360);
    } else {
      this.selectedShopCode = null;
    }
  }

  protected onSelectShopResult(modalresult: ModalResult) {
    LogUtil.debug('ShopTaxesComponent onSelectShopResult modal result is ', modalresult);
    if (this.selectedShop == null) {
      this.selectShopModalDialog.show();
    } else if (this.selectedCurrency == null) {
      this.selectCurrencyModalDialog.show();
    } else {
      this.getFilteredTax();
    }
  }

  protected onCurrencySelect() {
    LogUtil.debug('ShopTaxesComponent onCurrencySelect');
    this.selectCurrencyModalDialog.show();
  }

  protected onCurrencySelected(event:string) {
    LogUtil.debug('ShopTaxesComponent onCurrencySelected');
    this.selectedCurrency = event;
    if (this.selectedCurrency != null) {
      CookieUtil.createCookie(ShopTaxesComponent.COOKIE_CURRENCY, this.selectedCurrency, 360);
    }
  }

  protected onSelectCurrencyResult(modalresult: ModalResult) {
    LogUtil.debug('ShopTaxesComponent onSelectCurrencyResult modal result is ', modalresult);
    if (this.selectedCurrency == null) {
      this.selectCurrencyModalDialog.show();
    } else {
      this.getFilteredTax();
    }
  }

  protected onTestRules() {
    LogUtil.debug('ShopTaxesComponent onTestRules');
    this.runTestModalDialog.showDialog();
  }

  onRunTestResult(event:PromotionTestVO) {
    LogUtil.debug('ShopTaxesComponent onRunTestResult', event);
    if (event != null && this.selectedShop != null) {
      this.loading = true;

      event.shopCode = this.selectedShop.code;
      event.currency = this.selectedCurrency;

      let _sub:any = this._taxService.testPromotions(event).subscribe(
        cart => {
          _sub.unsubscribe();
          this.loading = false;
          LogUtil.debug('ShopTaxesComponent onTestRules', cart);
          this.viewMode = ShopTaxesComponent.PRICELIST_TEST;
          this.testCart = cart;
        }
      );

    }
  }

  protected onTaxFilterChange(event:any) {
    this.taxes.searchContext.start = 0; // changing filter means we need to start from first page
    this.delayedFilteringTax.delay();
  }

  protected onRefreshHandler() {
    LogUtil.debug('ShopTaxesComponent refresh handler');
    if (UserEventBus.getUserEventBus().current() != null) {
      this.presetFromCookie();
      this.getFilteredTax();
    }
  }

  protected onPageSelected(page:number) {
    LogUtil.debug('ShopPromotionsComponent onPageSelected', page);
    this.taxes.searchContext.start = page;
    this.delayedFilteringTax.delay();
  }

  protected onSortSelected(sort:Pair<string, boolean>) {
    LogUtil.debug('ShopPromotionsComponent ononSortSelected', sort);
    if (sort == null) {
      this.taxes.searchContext.sortBy = null;
      this.taxes.searchContext.sortDesc = false;
    } else {
      this.taxes.searchContext.sortBy = sort.first;
      this.taxes.searchContext.sortDesc = sort.second;
    }
    this.delayedFilteringTax.delay();
  }

  protected onTaxSelected(data:TaxVO) {
    LogUtil.debug('ShopTaxesComponent onTaxSelected', data);
    this.selectedTax = data;
    // this.getFilteredTaxConfig();
  }

  protected onSearchHelpToggleTax() {
    this.searchHelpTaxShow = !this.searchHelpTaxShow;
  }

  protected onSearchRate() {
    this.taxesFilter = '%';
    this.searchHelpTaxShow = false;
  }

  protected onSearchType() {
    this.taxesFilter = '-';
    this.searchHelpTaxShow = false;
  }

  protected onRowNew() {
    LogUtil.debug('ShopTaxesComponent onRowNew handler');
    this.validForSaveTax = false;
    UiUtil.formInitialise(this, 'taxEditForm', 'taxEdit', this.newTaxInstance(), false, ['code']);
    this.editTaxModalDialog.show();
  }

  protected onRowTaxDelete(row:TaxVO) {
    LogUtil.debug('ShopTaxesComponent onRowDelete handler', row);
    this.deleteValue = row.code + (row.description ? ': ' + row.description : '');
    this.deleteConfirmationModalDialog.show();
  }

  protected onRowDeleteSelected() {
    if (this.selectedTax != null) {
      this.onRowTaxDelete(this.selectedTax);
    }
  }


  protected onRowEditTax(row:TaxVO) {
    LogUtil.debug('ShopTaxesComponent onRowEditTax handler', row);
    this.validForSaveTax = false;
    UiUtil.formInitialise(this, 'taxEditForm', 'taxEdit', Util.clone(row), row.taxId > 0, ['code']);
    this.editTaxModalDialog.show();
  }

  protected onRowEditSelected() {
    if (this.selectedTax != null) {
      this.onRowEditTax(this.selectedTax);
    }
  }

  protected onRowCopySelected() {
    if (this.selectedTax != null) {
      let copyTax:TaxVO = Util.clone(this.selectedTax);
      copyTax.taxId = 0;
      copyTax.shopCode = this.selectedShopCode;
      this.onRowEditTax(copyTax);
    }
  }


  protected onBackToList() {
    LogUtil.debug('ShopTaxesComponent onBackToList handler');
    if (this.viewMode === ShopTaxesComponent.PRICELIST_TEST) {
      this.viewMode = ShopTaxesComponent.TAXES;
    }
  }

  protected onSaveHandler() {

    if (this.taxEdit != null) {

      if (this.validForSaveTax) {

        LogUtil.debug('ShopTaxesComponent Save handler tax', this.taxEdit);

        this.loading = true;
        let _sub:any = this._taxService.saveTax(this.taxEdit).subscribe(
            rez => {
              _sub.unsubscribe();
              LogUtil.debug('ShopTaxesComponent tax changed', rez);
              this.selectedTax = rez;
              this.loading = false;
              this.getFilteredTax();
          }
        );
      }

    }

  }

  protected onDiscardEventHandler() {
    LogUtil.debug('ShopTaxesComponent discard handler');
    if (this.selectedTax != null) {
      this.onRowEditSelected();
    } else {
      this.onRowNew();
    }
  }

  protected onEditTaxResult(modalresult: ModalResult) {
    LogUtil.debug('ShopTaxesComponent onEditTaxResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {

      this.onSaveHandler();

    } else {

      this.taxEdit = null;

    }
  }

  protected onDeleteConfirmationResult(modalresult: ModalResult) {
    LogUtil.debug('ShopTaxesComponent onDeleteConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {

      if (this.selectedTax != null) {
        LogUtil.debug('ShopTaxesComponent onDeleteConfirmationResult', this.selectedTax);

        this.loading = true;
        let _sub:any = this._taxService.removeTax(this.selectedTax).subscribe(res => {
          _sub.unsubscribe();
          LogUtil.debug('ShopTaxesComponent removeTax', this.selectedTax);
          this.selectedTax = null;
          this.loading = false;
          this.getFilteredTax();
        });
      }
    }
  }

  protected onClearFilterTax() {

    this.taxesFilter = '';
    this.delayedFilteringTax.delay();

  }


  private getFilteredTax() {
    this.taxesFilterRequired = false; // !this.forceShowAll && (this.taxesFilter == null || this.taxesFilter.length < 2);

    LogUtil.debug('ShopTaxesComponent getFilteredTax');

    if (this.selectedShop != null && this.selectedCurrency != null && !this.taxesFilterRequired) {
      this.loading = true;

      this.taxes.searchContext.parameters.filter = [ this.taxesFilter ];
      this.taxes.searchContext.parameters.shopCode = [ this.selectedShop.code ];
      this.taxes.searchContext.parameters.currency = [ this.selectedCurrency ];
      this.taxes.searchContext.size = Config.UI_TABLE_PAGE_SIZE;

      let _sub:any = this._taxService.getFilteredTax(this.taxes.searchContext).subscribe( alltaxes => {
        LogUtil.debug('ShopTaxesComponent getFilteredTax', alltaxes);
        this.taxes = alltaxes;
        this.selectedTax = null;
        this.validForSaveTax = false;
        _sub.unsubscribe();
        this.viewMode = ShopTaxesComponent.TAXES;
        this.loading = false;
      });
    } else {
      this.taxes = this.newSearchResultInstance();
      this.selectedTax = null;
      this.validForSaveTax = false;
      this.viewMode = ShopTaxesComponent.TAXES;
    }
  }

}
