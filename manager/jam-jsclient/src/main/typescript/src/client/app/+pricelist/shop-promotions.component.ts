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
import { PricingService, Util } from './../shared/services/index';
import { ModalComponent, ModalResult, ModalAction } from './../shared/modal/index';
import { PromotionVO, ShopVO, Pair } from './../shared/model/index';
import { FormValidationEvent, Futures, Future } from './../shared/event/index';
import { Config } from './../shared/config/env.config';
import { UiUtil } from './../shared/ui/index';
import { LogUtil } from './../shared/log/index';

@Component({
  selector: 'yc-shop-promotions',
  moduleId: module.id,
  templateUrl: 'shop-promotions.component.html',
})

export class ShopPromotionsComponent implements OnInit, OnDestroy {

  private static PROMOTIONS:string = 'promotions';
  private static PROMOTION:string = 'promotion';

  private static _selectedShop:ShopVO;
  private static _selectedCurrency:string;

  private static _promoTypes:Pair<string, boolean>[] = [
    { first: 'O', second: false },
    { first: 'S', second: false },
    { first: 'I', second: false },
    { first: 'C', second: false },
  ];

  private static _promoActions:Pair<string, boolean>[] = [
    { first: 'F', second: false },
    { first: 'P', second: false },
    { first: 'G', second: false },
    { first: 'T', second: false },
  ];

  private searchHelpShow:boolean = false;
  private forceShowAll:boolean = false;
  private viewMode:string = ShopPromotionsComponent.PROMOTIONS;

  private promotions:Array<PromotionVO> = [];
  private promotionFilter:string;
  private promotionFilterRequired:boolean = true;
  private promotionFilterCapped:boolean = false;

  private delayedFiltering:Future;
  private delayedFilteringMs:number = Config.UI_INPUT_DELAY;
  private filterCap:number = Config.UI_FILTER_CAP;
  private filterNoCap:number = Config.UI_FILTER_NO_CAP;

  private selectedPromotion:PromotionVO;

  private promotionEdit:PromotionVO;

  @ViewChild('deleteConfirmationModalDialog')
  private deleteConfirmationModalDialog:ModalComponent;

  @ViewChild('disableConfirmationModalDialog')
  private disableConfirmationModalDialog:ModalComponent;

  private deleteValue:String;

  private loading:boolean = false;

  private changed:boolean = false;
  private validForSave:boolean = false;

  @ViewChild('selectShopModalDialog')
  private selectShopModalDialog:ModalComponent;

  @ViewChild('selectCurrencyModalDialog')
  private selectCurrencyModalDialog:ModalComponent;

  constructor(private _promotionService:PricingService) {
    LogUtil.debug('ShopPromotionsComponent constructed');
  }


  get selectedShop():ShopVO {
    return ShopPromotionsComponent._selectedShop;
  }

  set selectedShop(selectedShop:ShopVO) {
    ShopPromotionsComponent._selectedShop = selectedShop;
  }

  get selectedCurrency():string {
    return ShopPromotionsComponent._selectedCurrency;
  }

  set selectedCurrency(selectedCurrency:string) {
    ShopPromotionsComponent._selectedCurrency = selectedCurrency;
  }


  newPromotionInstance():PromotionVO {
    return {
      promotionId: 0, code: '', shopCode: this.selectedShop.code, currency: this.selectedCurrency,
      rank: 500, name: null,  description: null,
      displayNames: [], displayDescriptions: [],
      promoType: 'O', promoAction: 'P',
      eligibilityCondition: null, promoActionContext: null,
      couponTriggered: false, canBeCombined: true,
      enabled: false, enabledFrom: null, enabledTo: null,
      tag: null
    };
  }


  get promoTypes():Pair<string, boolean>[] {
    return ShopPromotionsComponent._promoTypes;
  }

  set promoTypes(value:Pair<string, boolean>[]) {
    ShopPromotionsComponent._promoTypes = value;
  }

  get promoActions():Pair<string, boolean>[] {
    return ShopPromotionsComponent._promoActions;
  }

  set promoActions(value:Pair<string, boolean>[]) {
    ShopPromotionsComponent._promoActions = value;
  }


  ngOnInit() {
    LogUtil.debug('ShopPromotionsComponent ngOnInit');
    this.onRefreshHandler();
    let that = this;
    this.delayedFiltering = Futures.perpetual(function() {
      that.getFilteredPromotions();
    }, this.delayedFilteringMs);

  }

  ngOnDestroy() {
    LogUtil.debug('ShopPromotionsComponent ngOnDestroy');
  }


  protected onShopSelect() {
    LogUtil.debug('ShopPromotionsComponent onShopSelect');
    this.selectShopModalDialog.show();
  }

  protected onShopSelected(event:ShopVO) {
    LogUtil.debug('ShopPromotionsComponent onShopSelected');
    this.selectedShop = event;
  }

  protected onSelectShopResult(modalresult: ModalResult) {
    LogUtil.debug('ShopPromotionsComponent onSelectShopResult modal result is ', modalresult);
    if (this.selectedShop == null) {
      this.selectShopModalDialog.show();
    } else if (this.selectedCurrency == null) {
      this.selectCurrencyModalDialog.show();
    } else {
      this.getFilteredPromotions();
    }
  }

  protected onCurrencySelect() {
    LogUtil.debug('ShopPromotionsComponent onCurrencySelect');
    this.selectCurrencyModalDialog.show();
  }

  protected onCurrencySelected(event:string) {
    LogUtil.debug('ShopPromotionsComponent onCurrencySelected');
    this.selectedCurrency = event;
  }

  protected onSelectCurrencyResult(modalresult: ModalResult) {
    LogUtil.debug('ShopPromotionsComponent onSelectCurrencyResult modal result is ', modalresult);
    if (this.selectedCurrency == null) {
      this.selectCurrencyModalDialog.show();
    } else {
      this.getFilteredPromotions();
    }
  }

  protected onFilterChange(event:any) {

    this.delayedFiltering.delay();

  }

  protected onRefreshHandler() {
    LogUtil.debug('ShopPromotionsComponent refresh handler');
    this.getFilteredPromotions();
  }

  protected onPromotionSelected(data:PromotionVO) {
    LogUtil.debug('ShopPromotionsComponent onPromotionSelected', data);
    this.selectedPromotion = data;
  }

  protected onPromotionChanged(event:FormValidationEvent<PromotionVO>) {
    LogUtil.debug('ShopPromotionsComponent onPromotionChanged', event);
    this.changed = true;
    this.validForSave = event.valid;
    this.promotionEdit = event.source;
  }

  protected onSearchHelpToggle() {
    this.searchHelpShow = !this.searchHelpShow;
  }


  protected onSearchCode() {
    this.promotionFilter = '#';
    this.searchHelpShow = false;
  }

  protected onSearchType() {
    this.promotionFilter = '!O';
    //this.searchHelpShow = false; by design, do not collapse since need to see special character types
  }

  protected onSearchCondition() {
    this.promotionFilter = '?';
    this.searchHelpShow = false;
  }

  protected onSearchEnabled() {
    this.promotionFilter = '+!O';
    this.searchHelpShow = false;
  }

  protected onSearchDate() {
    this.promotionFilter = UiUtil.exampleDateSearch();
    this.searchHelpShow = false;
  }

  protected onForceShowAll() {
    this.forceShowAll = !this.forceShowAll;
    this.getFilteredPromotions();
  }

  protected onBackToList() {
    LogUtil.debug('ShopPromotionsComponent onBackToList handler');
    if (this.viewMode === ShopPromotionsComponent.PROMOTION) {
      this.promotionEdit = null;
      this.viewMode = ShopPromotionsComponent.PROMOTIONS;
    }
  }

  protected onRowNew() {
    LogUtil.debug('ShopPromotionsComponent onRowNew handler');
    this.changed = false;
    this.validForSave = false;
    if (this.viewMode === ShopPromotionsComponent.PROMOTIONS) {
      this.promotionEdit = this.newPromotionInstance();
      this.viewMode = ShopPromotionsComponent.PROMOTION;
    }
  }

  protected onRowDelete(row:any) {
    LogUtil.debug('ShopPromotionsComponent onRowDelete handler', row);
    this.deleteValue = row.name;
    this.deleteConfirmationModalDialog.show();
  }

  protected onRowDeleteSelected() {
    if (this.selectedPromotion != null) {
      this.onRowDelete(this.selectedPromotion);
    }
  }

  protected onRowEnableSelected() {
    if (this.selectedPromotion != null) {
      this.deleteValue = this.selectedPromotion.name;
      this.disableConfirmationModalDialog.show();
    }
  }


  protected onRowEditPromotion(row:PromotionVO) {
    LogUtil.debug('ShopPromotionsComponent onRowEditPromotion handler', row);
    this.promotionEdit = Util.clone(row);
    this.changed = false;
    this.validForSave = false;
    this.viewMode = ShopPromotionsComponent.PROMOTION;
  }

  protected onRowEditSelected() {
    if (this.selectedPromotion != null) {
      this.onRowEditPromotion(this.selectedPromotion);
    }
  }

  protected onRowCopySelected() {
    if (this.selectedPromotion != null) {
      var copy:PromotionVO = Util.clone(this.selectedPromotion);
      copy.promotionId = 0;
      copy.enabled = false;
      this.onRowEditPromotion(copy);
    }
  }


  protected onSaveHandler() {

    if (this.validForSave && this.changed) {

      if (this.promotionEdit != null) {

        LogUtil.debug('ShopPromotionsComponent Save handler promotion', this.promotionEdit);

        var _sub:any = this._promotionService.savePromotion(this.promotionEdit).subscribe(
            rez => {
              _sub.unsubscribe();
              let pk = this.promotionEdit.promotionId;
              LogUtil.debug('ShopPromotionsComponent promotion changed', rez);
              this.changed = false;
              this.selectedPromotion = rez;
              this.promotionEdit = null;
              this.viewMode = ShopPromotionsComponent.PROMOTIONS;

              if (pk == 0) {
                this.promotionFilter = rez.name;
              }
              this.getFilteredPromotions();
            }
        );
      }

    }

  }

  protected onDiscardEventHandler() {
    LogUtil.debug('ShopPromotionsComponent discard handler');
    if (this.viewMode === ShopPromotionsComponent.PROMOTION) {
      if (this.selectedPromotion != null) {
        this.onRowEditSelected();
      } else {
        this.onRowNew();
      }
    }
  }

  protected onDeleteConfirmationResult(modalresult: ModalResult) {
    LogUtil.debug('ShopPromotionsComponent onDeleteConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {

      if (this.selectedPromotion != null) {
        LogUtil.debug('ShopPromotionsComponent onDeleteConfirmationResult', this.selectedPromotion);

        var _sub:any = this._promotionService.removePromotion(this.selectedPromotion).subscribe(res => {
          _sub.unsubscribe();
          LogUtil.debug('ShopPromotionsComponent removePromotion', this.selectedPromotion);
          this.selectedPromotion = null;
          this.promotionEdit = null;
          this.getFilteredPromotions();
        });
      }
    }
  }


  protected onDisableConfirmationResult(modalresult: ModalResult) {
    LogUtil.debug('ShopPromotionsComponent onDisableConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {

      if (this.selectedPromotion != null) {
        var _sub:any = this._promotionService.updatePromotionDisabledFlag(this.selectedPromotion, this.selectedPromotion.enabled).subscribe( done => {
          LogUtil.debug('ShopPromotionsComponent updateDisabledFlag', done);
          this.selectedPromotion.enabled = !this.selectedPromotion.enabled;
          if (this.promotionEdit != null && this.selectedPromotion.promotionId == this.promotionEdit.promotionId) {
            this.promotionEdit = Util.clone(this.promotionEdit); // Trigger form INIT
            this.promotionEdit.enabled = this.selectedPromotion.enabled;
            if (!this.promotionEdit.enabled) {
              this.validForSave = false;
            }
          } else {
            this.changed = false;
            this.validForSave = false;
          }
          _sub.unsubscribe();
        });
      }
    }
  }

  protected onClearFilter() {

    this.promotionFilter = '';
    this.getFilteredPromotions();

  }


  private getFilteredPromotions() {
    this.promotionFilterRequired = !this.forceShowAll && (this.promotionFilter == null || this.promotionFilter.length < 2);

    LogUtil.debug('ShopPromotionsComponent getFilteredPromotions' + (this.forceShowAll ? ' forcefully': ''));

    if (this.selectedShop != null && this.selectedCurrency != null && !this.promotionFilterRequired) {
      this.loading = true;
      let max = this.forceShowAll ? this.filterNoCap : this.filterCap;

      let types:string[] = [];
      ShopPromotionsComponent._promoTypes.forEach((_type:Pair<string, boolean>) => {
        if (_type.second) {
          types.push(_type.first);
        }
      });

      let actions:string[] = [];
      ShopPromotionsComponent._promoActions.forEach((_action:Pair<string, boolean>) => {
        if (_action.second) {
          actions.push(_action.first);
        }
      });

      var _sub:any = this._promotionService.getFilteredPromotions(this.selectedShop, this.selectedCurrency, this.promotionFilter, types, actions, max).subscribe( allpromotions => {
        LogUtil.debug('ShopPromotionsComponent getFilteredPromotions', allpromotions);
        this.promotions = allpromotions;
        this.selectedPromotion = null;
        this.promotionEdit = null;
        this.viewMode = ShopPromotionsComponent.PROMOTIONS;
        this.changed = false;
        this.validForSave = false;
        this.promotionFilterCapped = this.promotions.length >= max;
        this.loading = false;
        _sub.unsubscribe();
      });
    } else {
      this.promotions = [];
      this.selectedPromotion = null;
      this.promotionEdit = null;
      this.viewMode = ShopPromotionsComponent.PROMOTIONS;
      this.changed = false;
      this.validForSave = false;
      this.promotionFilterCapped = false;
    }
  }

}
