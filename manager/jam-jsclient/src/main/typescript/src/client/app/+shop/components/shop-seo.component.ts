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
import { Component, OnInit, OnDestroy, Input } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ShopVO, ShopSeoVO } from './../../shared/model/index';
import { ShopService } from './../../shared/services/index';
import { FormValidationEvent, Futures, Future } from './../../shared/event/index';
import { UiUtil } from './../../shared/ui/index';
import { LogUtil } from './../../shared/log/index';

@Component({
  selector: 'yc-shop-seo',
  moduleId: module.id,
  templateUrl: 'shop-seo.component.html',
})

export class ShopSEOComponent implements OnInit, OnDestroy {

  private _shop:ShopVO;
  private _reload:boolean = false;

  private shopLocalization:ShopSeoVO;

  private changed:boolean = false;
  private validForSave:boolean = false;

  private initialising:boolean = false; // tslint:disable-line:no-unused-variable
  private delayedChange:Future;

  private shopSEOForm:any;
  private shopSEOFormSub:any; // tslint:disable-line:no-unused-variable

  private loading:boolean = false;

  constructor(private _shopService:ShopService,
              fb: FormBuilder) {
    LogUtil.debug('ShopSEOComponent constructed');

    this.shopSEOForm = fb.group({
      'title': [''],
      'keywords': [''],
      'meta': [''],
    });

    let that = this;
    this.delayedChange = Futures.perpetual(function() {
      that.formChange();
    }, 200);
  }

  formBind():void {
    UiUtil.formBind(this, 'shopSEOForm', 'shopSEOFormSub', 'delayedChange', 'initialising');
  }


  formUnbind():void {
    UiUtil.formUnbind(this, 'shopSEOFormSub');
  }

  formChange():void {
    LogUtil.debug('ShopSEOComponent formChange', this.shopSEOForm.valid, this.shopLocalization);
    this.changed = this.shopSEOForm.dirty;
    this.validForSave = this.shopSEOForm.valid;
  }

  @Input()
  set reload(reload:boolean) {
    if (reload && !this._reload) {
      this._reload = true;
      this.onRefreshHandler();
    }
  }

  @Input()
  set shop(shop:ShopVO) {
    this._shop = shop;
    if (this._reload || this.shopLocalization != null) {
      this.onRefreshHandler();
    }
  }

  get shop():ShopVO  {
    return this._shop;
  }


  onTitleDataChange(event:FormValidationEvent<any>) {
    UiUtil.formI18nDataChange(this, 'shopSEOForm', 'title', event);
  }

  onKeywordsDataChange(event:FormValidationEvent<any>) {
    UiUtil.formI18nDataChange(this, 'shopSEOForm', 'keywords', event);
  }

  onMetaDataChange(event:FormValidationEvent<any>) {
    UiUtil.formI18nDataChange(this, 'shopSEOForm', 'meta', event);
  }


  ngOnInit() {
    LogUtil.debug('ShopSEOComponent ngOnInit shop', this.shop);
    this.formBind();
  }

  ngOnDestroy() {
    LogUtil.debug('ShopSEOComponent ngOnDestroy');
    this.formUnbind();
  }

  onSaveHandler() {
    LogUtil.debug('ShopSEOComponent Save handler', this.shop);
    if (this.shop.shopId > 0 && this.shopLocalization) {
      var _sub:any = this._shopService.saveShopLocalization(this.shopLocalization).subscribe(shopLocalization => {
        LogUtil.debug('ShopSEOComponent Saved i18n', shopLocalization);
        this.initialising = true;
        this.shopLocalization = shopLocalization;
        this.shopSEOForm.reset(this.shopLocalization);
        this.changed = false;
        this._reload = false;
        this.validForSave = false;
        this.initialising = false;
        _sub.unsubscribe();
      });
    }
  }

  onDiscardEvent() {
    LogUtil.debug('ShopSEOComponent Discard handler for shop', this.shop);
    if (this.shop.shopId > 0) {
      this.loading = true;
      var _sub:any = this._shopService.getShopLocalization(this.shop.shopId).subscribe(shopLocalization => {
        LogUtil.debug('ShopSEOComponent Refreshed i18n', shopLocalization);
        this.initialising = true;
        this.shopLocalization = shopLocalization;
        this.shopSEOForm.reset(this.shopLocalization);
        this.changed = false;
        this._reload = false;
        this.validForSave = false;
        this.initialising = false;
        _sub.unsubscribe();
        this.loading = false;
      });
    } else {
      this.shopLocalization = null;
      this.changed = false;
      this.validForSave = false;
    }
  }

  onRefreshHandler() {
    LogUtil.debug('ShopSEOComponent Refresh handler');
    this.onDiscardEvent();
  }

}
