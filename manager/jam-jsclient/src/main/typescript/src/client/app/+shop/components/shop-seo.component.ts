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
import {Component, OnInit, OnDestroy, OnChanges, Input} from '@angular/core';
import {NgIf} from '@angular/common';
import {FormBuilder, Validators, REACTIVE_FORM_DIRECTIVES} from '@angular/forms';
import {ShopVO, ShopSeoVO} from './../../shared/model/index';
import {ShopService, ShopEventBus} from './../../shared/services/index';
import {I18nComponent} from './../../shared/i18n/index';
import {DataControlComponent} from './../../shared/sidebar/index';
import {Futures, Future} from './../../shared/event/index';

@Component({
  selector: 'yc-shop-seo',
  moduleId: module.id,
  templateUrl: 'shop-seo.component.html',
  directives: [ I18nComponent, NgIf, REACTIVE_FORM_DIRECTIVES, DataControlComponent],
})

export class ShopSEOComponent implements OnInit, OnDestroy {

  private _shop:ShopVO;
  private _reload:boolean = false;

  shopLocalization:ShopSeoVO;

  changed:boolean = false;
  validForSave:boolean = false;
  validForm:boolean = false;
  delayedChange:Future;

  shopSEOForm:any;
  shopSEOFormSub:any;

  constructor(private _shopService:ShopService,
              fb: FormBuilder) {
    console.debug('ShopSEOComponent constructed');

    this.shopSEOForm = fb.group({});

    let that = this;
    this.delayedChange = Futures.perpetual(function() {
      that.formChange();
    }, 200);
  }


  formReset():void {
    // Hack to reset NG2 forms see https://github.com/angular/angular/issues/4933
    for(let key in this.shopSEOForm.controls) {
      this.shopSEOForm.controls[key]['_pristine'] = true;
      this.shopSEOForm.controls[key]['_touched'] = false;
    }
  }


  formBind():void {
    this.shopSEOFormSub = this.shopSEOForm.statusChanges.subscribe((data:any) => {
      this.validForm = this.shopSEOForm.valid;
      if (this.changed) {
        this.delayedChange.delay();
      }
    });
  }


  formUnbind():void {
    if (this.shopSEOFormSub) {
      console.debug('ShopSEOComponent unbining form');
      this.shopSEOFormSub.unsubscribe();
    }
  }

  formChange():void {
    this.validForSave = this.validForm;
    console.debug('ShopSEOComponent validating formGroup is valid: ' + this.validForSave, this.shopLocalization);
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

  ngOnInit() {
    console.debug('ShopSEOComponent ngOnInit shop', this.shop);
    this.formBind();
  }

  ngOnDestroy() {
    console.debug('ShopSEOComponent ngOnDestroy');
    this.formUnbind();
  }

  onDataChanged() {
    console.debug('ShopSEOComponent data change');
    this.changed = true;
  }

  onSaveHandler() {
    console.debug('ShopSEOComponent Save handler', this.shop);
    if (this.shop.shopId > 0 && this.shopLocalization) {
      var _sub:any = this._shopService.saveShopLocalization(this.shopLocalization).subscribe(shopLocalization => {
        console.debug('ShopSEOComponent Saved i18n', shopLocalization);
        this.shopLocalization = shopLocalization;
        this.changed = false;
        this._reload = false;
        this.validForSave = false;
        _sub.unsubscribe();
      });
    }
  }

  onDiscardEvent() {
    console.debug('ShopSEOComponent Discard handler for shop', this.shop);
    if (this.shop.shopId > 0) {
      var _sub:any = this._shopService.getShopLocalization(this.shop.shopId).subscribe(shopLocalization => {
        console.debug('ShopSEOComponent Refreshed i18n', shopLocalization);
        this.shopLocalization = shopLocalization;
        this.changed = false;
        this._reload = false;
        this.validForSave = false;
        _sub.unsubscribe();
      });
    } else {
      this.shopLocalization = null;
      this.changed = false;
      this.validForSave = false;
    }
  }

  onRefreshHandler() {
    console.debug('ShopSEOComponent Refresh handler');
    this.onDiscardEvent();
  }

}
