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
import {ShopVO, ShopLocaleVO} from './../../shared/model/index';
import {ShopService, ShopEventBus} from './../../shared/services/index';
import {I18nComponent} from './../../shared/i18n/index';
import {DataControlComponent} from './../../shared/sidebar/index';
import {Futures, Future} from './../../shared/event/index';

@Component({
  selector: 'yc-shop-i18n',
  moduleId: module.id,
  templateUrl: 'shop-i18n.component.html',
  directives: [ I18nComponent, NgIf, REACTIVE_FORM_DIRECTIVES, DataControlComponent],
})

export class ShopI18nComponent implements OnInit, OnDestroy, OnChanges {

  @Input() shop:ShopVO;

  shopLocalization:ShopLocaleVO;

  changed:boolean = false;
  validForSave:boolean = false;
  validForm:boolean = false;
  delayedChange:Future;

  shopI18nForm:any;
  shopI18nFormSub:any;

  constructor(private _shopService:ShopService,
              fb: FormBuilder) {
    console.debug('ShopI18nComponent constructed');

    this.shopI18nForm = fb.group({});

    let that = this;
    this.delayedChange = Futures.perpetual(function() {
      that.formChange();
    }, 200);
  }


  formReset():void {
    // Hack to reset NG2 forms see https://github.com/angular/angular/issues/4933
    for(let key in this.shopI18nForm.controls) {
      this.shopI18nForm.controls[key]['_pristine'] = true;
      this.shopI18nForm.controls[key]['_touched'] = false;
    }
  }


  formBind():void {
    this.shopI18nFormSub = this.shopI18nForm.valueChanges.subscribe((data:any) => {
      this.validForm = this.shopI18nForm.valid;
      if (this.changed) {
        this.delayedChange.delay();
      }
    });
  }


  formUnbind():void {
    if (this.shopI18nFormSub) {
      console.debug('ShopI18nComponent unbining form');
      this.shopI18nFormSub.unsubscribe();
    }
  }

  formChange():void {
    this.validForSave = this.validForm;
    console.debug('ShopI18nComponent validating formGroup is valid: ' + this.validForSave, this.shopLocalization);
  }


  ngOnInit() {
    console.debug('ShopI18nComponent ngOnInit shop', this.shop);
    this.formBind();
    this.onRefreshHandler();
  }

  ngOnDestroy() {
    console.debug('ShopI18nComponent ngOnDestroy');
    this.formUnbind();
  }

  ngOnChanges(changes:any) {
    console.debug('ShopI18nComponent ngOnChanges', changes);
    this.onRefreshHandler();
  }

  onDataChanged() {
    console.debug('ShopI18nComponent data change');
    this.changed = true;
  }

  onSaveHandler() {
    console.debug('ShopI18nComponent Save handler', this.shop);
    if (this.shop.shopId > 0 && this.shopLocalization) {
      var _sub:any = this._shopService.saveShopLocalization(this.shopLocalization).subscribe(shopLocalization => {
        console.debug('ShopI18nComponent Saved i18n', shopLocalization);
        this.shopLocalization = shopLocalization;
        this.changed = false;
        this.validForSave = false;
        _sub.unsubscribe();
      });
    }
  }

  onDiscardEvent() {
    console.debug('ShopI18nComponent Discard handler for shop', this.shop);
    if (this.shop.shopId > 0) {
      var _sub:any = this._shopService.getShopLocalization(this.shop.shopId).subscribe(shopLocalization => {
        console.debug('ShopI18nComponent Refreshed i18n', shopLocalization);
        this.shopLocalization = shopLocalization;
        this.changed = false;
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
    console.debug('ShopI18nComponent Refresh handler');
    this.onDiscardEvent();
  }

}
