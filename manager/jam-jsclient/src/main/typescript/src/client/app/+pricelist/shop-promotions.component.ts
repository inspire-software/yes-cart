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
import {PricingService, Util} from './../shared/services/index';
import {UiUtil} from './../shared/ui/index';
import {TAB_DIRECTIVES} from 'ng2-bootstrap/ng2-bootstrap';
import {PromotionsComponent, PromotionComponent} from './components/index';
import {DataControlComponent} from './../shared/sidebar/index';
import {ShopSelectComponent} from './../shared/shop/index';
import {CurrencySelectComponent} from './../shared/price/index';
import {ModalComponent, ModalResult, ModalAction} from './../shared/modal/index';
import {PromotionVO, ShopVO, Pair} from './../shared/model/index';
import {FormValidationEvent, Futures, Future} from './../shared/event/index';
import {Config} from './../shared/config/env.config';

@Component({
  selector: 'yc-shop-promotions',
  moduleId: module.id,
  templateUrl: 'shop-promotions.component.html',
  directives: [TAB_DIRECTIVES, NgIf, PromotionsComponent, PromotionComponent, ModalComponent, DataControlComponent, ShopSelectComponent, CurrencySelectComponent ],
})

export class ShopPromotionsComponent implements OnInit, OnDestroy {

  private static PROMOTIONS:string = 'promotions';
  private static PROMOTION:string = 'promotion';

  private searchHelpShow:boolean = false;
  private forceShowAll:boolean = false;
  private viewMode:string = ShopPromotionsComponent.PROMOTIONS;

  private promotions:Array<PromotionVO> = [];
  private promotionFilter:string;
  private promotionFilterRequired:boolean = true;
  private promotionFilterCapped:boolean = false;

  delayedFiltering:Future;
  delayedFilteringMs:number = Config.UI_INPUT_DELAY;
  filterCap:number = Config.UI_FILTER_CAP;
  filterNoCap:number = Config.UI_FILTER_NO_CAP;

  static _selectedShop:ShopVO;
  static _selectedCurrency:string;
  private selectedPromotion:PromotionVO;

  private promotionEdit:PromotionVO;

  @ViewChild('deleteConfirmationModalDialog')
  deleteConfirmationModalDialog:ModalComponent;

  @ViewChild('disableConfirmationModalDialog')
  disableConfirmationModalDialog:ModalComponent;

  private deleteValue:String;

  changed:boolean = false;
  validForSave:boolean = false;


  @ViewChild('selectShopModalDialog')
  selectShopModalDialog:ModalComponent;

  @ViewChild('selectCurrencyModalDialog')
  selectCurrencyModalDialog:ModalComponent;

  constructor(private _promotionService:PricingService) {
    console.debug('ShopPromotionsComponent constructed');
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

  ngOnInit() {
    console.debug('ShopPromotionsComponent ngOnInit');
    this.onRefreshHandler();
    let that = this;
    this.delayedFiltering = Futures.perpetual(function() {
      that.getFilteredPromotions();
    }, this.delayedFilteringMs);

  }

  ngOnDestroy() {
    console.debug('ShopPromotionsComponent ngOnDestroy');
  }


  protected onShopSelect() {
    console.debug('ShopPromotionsComponent onShopSelect');
    this.selectShopModalDialog.show();
  }

  protected onShopSelected(event:ShopVO) {
    console.debug('ShopPromotionsComponent onShopSelected');
    this.selectedShop = event;
  }

  protected onSelectShopResult(modalresult: ModalResult) {
    console.debug('ShopPromotionsComponent onSelectShopResult modal result is ', modalresult);
    if (this.selectedShop == null) {
      this.selectShopModalDialog.show();
    } else if (this.selectedCurrency == null) {
      this.selectCurrencyModalDialog.show();
    } else {
      this.getFilteredPromotions();
    }
  }

  protected onCurrencySelect() {
    console.debug('ShopPromotionsComponent onCurrencySelect');
    this.selectCurrencyModalDialog.show();
  }

  protected onCurrencySelected(event:string) {
    console.debug('ShopPromotionsComponent onCurrencySelected');
    this.selectedCurrency = event;
  }

  protected onSelectCurrencyResult(modalresult: ModalResult) {
    console.debug('ShopPromotionsComponent onSelectCurrencyResult modal result is ', modalresult);
    if (this.selectedCurrency == null) {
      this.selectCurrencyModalDialog.show();
    } else {
      this.getFilteredPromotions();
    }
  }



  onFilterChange(event:any) {

    this.delayedFiltering.delay();

  }

  getFilteredPromotions() {
    this.promotionFilterRequired = !this.forceShowAll && (this.promotionFilter == null || this.promotionFilter.length < 2);

    console.debug('ShopPromotionsComponent getFilteredPromotions' + (this.forceShowAll ? ' forcefully': ''));

    if (this.selectedShop != null && this.selectedCurrency != null && !this.promotionFilterRequired) {
      let max = this.forceShowAll ? this.filterNoCap : this.filterCap;
      var _sub:any = this._promotionService.getFilteredPromotions(this.selectedShop, this.selectedCurrency, this.promotionFilter, max).subscribe( allpromotions => {
        console.debug('ShopPromotionsComponent getFilteredPromotions', allpromotions);
        this.promotions = allpromotions;
        this.selectedPromotion = null;
        this.promotionEdit = null;
        this.viewMode = ShopPromotionsComponent.PROMOTIONS;
        this.changed = false;
        this.validForSave = false;
        this.promotionFilterCapped = this.promotions.length >= max;
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

  protected onRefreshHandler() {
    console.debug('ShopPromotionsComponent refresh handler');
    this.getFilteredPromotions();
  }

  onPromotionSelected(data:PromotionVO) {
    console.debug('ShopPromotionsComponent onPromotionSelected', data);
    this.selectedPromotion = data;
  }

  onPromotionChanged(event:FormValidationEvent<PromotionVO>) {
    console.debug('ShopPromotionsComponent onPromotionChanged', event);
    this.changed = true;
    this.validForSave = event.valid;
    this.promotionEdit = event.source;
  }

  protected onSearchHelpToggle() {
    this.searchHelpShow = !this.searchHelpShow;
  }


  protected onSearchCode() {
    this.promotionFilter = '#code';
    this.searchHelpShow = false;
  }

  protected onSearchType() {
    this.promotionFilter = '?O';
    this.searchHelpShow = false;
  }

  protected onSearchEnabled() {
    this.promotionFilter = '+?O';
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
    console.debug('ShopPromotionsComponent onBackToList handler');
    if (this.viewMode === ShopPromotionsComponent.PROMOTION) {
      this.promotionEdit = null;
      this.viewMode = ShopPromotionsComponent.PROMOTIONS;
    }
  }

  protected onRowNew() {
    console.debug('ShopPromotionsComponent onRowNew handler');
    this.changed = false;
    this.validForSave = false;
    if (this.viewMode === ShopPromotionsComponent.PROMOTIONS) {
      this.promotionEdit = this.newPromotionInstance();
      this.viewMode = ShopPromotionsComponent.PROMOTION;
    }
  }

  protected onRowDelete(row:any) {
    console.debug('ShopPromotionsComponent onRowDelete handler', row);
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
    console.debug('ShopPromotionsComponent onRowEditPromotion handler', row);
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

  protected onSaveHandler() {

    if (this.validForSave && this.changed) {

      if (this.promotionEdit != null) {

        console.debug('ShopPromotionsComponent Save handler promotion', this.promotionEdit);

        var _sub:any = this._promotionService.savePromotion(this.promotionEdit).subscribe(
            rez => {
              _sub.unsubscribe();
              let pk = this.promotionEdit.promotionId;
              console.debug('ShopPromotionsComponent promotion changed', rez);
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
    console.debug('ShopPromotionsComponent discard handler');
    if (this.viewMode === ShopPromotionsComponent.PROMOTION) {
      if (this.selectedPromotion != null) {
        this.onRowEditSelected();
      } else {
        this.onRowNew();
      }
    }
  }

  protected onDeleteConfirmationResult(modalresult: ModalResult) {
    console.debug('ShopPromotionsComponent onDeleteConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {

      if (this.selectedPromotion != null) {
        console.debug('ShopPromotionsComponent onDeleteConfirmationResult', this.selectedPromotion);

        var _sub:any = this._promotionService.removePromotion(this.selectedPromotion).subscribe(res => {
          _sub.unsubscribe();
          console.debug('ShopPromotionsComponent removePromotion', this.selectedPromotion);
          this.selectedPromotion = null;
          this.promotionEdit = null;
          this.getFilteredPromotions();
        });
      }
    }
  }


  protected onDisableConfirmationResult(modalresult: ModalResult) {
    console.debug('ShopPromotionsComponent onDisableConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {

      if (this.selectedPromotion != null) {
        var _sub:any = this._promotionService.updatePromotionDisabledFlag(this.selectedPromotion, this.selectedPromotion.enabled).subscribe( done => {
          console.debug('ShopPromotionsComponent updateDisabledFlag', done);
          this.selectedPromotion.enabled = !this.selectedPromotion.enabled;
          if (this.selectedPromotion.promotionId == this.promotionEdit.promotionId) {
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


}
