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
import {Component, OnInit, OnDestroy} from '@angular/core';
import {NgIf} from '@angular/common';
import {FormBuilder, Validators, REACTIVE_FORM_DIRECTIVES} from '@angular/forms';
import {ShopVO} from './../../shared/model/index';
import {ShopEventBus, ShopService, Util} from './../../shared/services/index';
import {DataControlComponent} from './../../shared/sidebar/index';

@Component({
  selector: 'shop-main',
  moduleId: module.id,
  templateUrl: 'shop-main.component.html',
  directives: [DataControlComponent, NgIf, REACTIVE_FORM_DIRECTIVES],
  providers: [ShopService, ShopEventBus]
})

export class ShopMainComponent implements OnInit, OnDestroy {

  shop:ShopVO;
  shopDisabled:boolean = false;

  changed:boolean = false;
  validForSave:boolean = false;

  shopMainForm:any;

  private shopSub:any;

  constructor(private _shopService:ShopService,
              fb: FormBuilder) {

    console.debug('ShopMainComponent constructed');

    this.shopMainForm = fb.group({
        'code': ['', Validators.compose([Validators.required, Validators.pattern('[A-Za-z0-9]+')])],
        'name': ['', Validators.compose([Validators.required, Validators.pattern('\\S+.*\\S+')])],
        'description': [''],
        'fspointer': [''],
    });

    this.shopSub = ShopEventBus.getShopEventBus().shopUpdated$.subscribe(shopevt => {
      this.shop = Util.clone(shopevt);
      this.changed = false;
      this.validForSave = false;
      this.shopDisabled = shopevt.disabled;
      this.formReset();
    });

  }

  formReset():void {
    // Hack to reset NG2 forms see https://github.com/angular/angular/issues/4933
    for(let key in this.shopMainForm.controls) {
      this.shopMainForm.controls[key]['_pristine'] = true;
      this.shopMainForm.controls[key]['_touched'] = false;
    }
  }

  ngOnInit() {

    this.onDiscardEvent();
    console.debug('ShopMainComponent ngOnInit', this.shop);

  }

  ngOnDestroy() {
    console.debug('ShopComponent ngOnDestroy');
    if (this.shopSub) {
      this.shopSub.unsubscribe();
    }
  }

  onDataChange(event:any) {
    this.changed = true;
    var _sub:any = this.shopMainForm.valueChanges.subscribe((data:any) => {
      this.validForSave = this.shopMainForm.valid;
      console.debug('ShopMainComponent form changed  and ' + (this.validForSave ? 'is valid' : 'is NOT valid'), data);
      _sub.unsubscribe();
    });
    console.debug('ShopMainComponent data changed and ' + (this.validForSave ? 'is valid' : 'is NOT valid'), event);
  }

  onSaveHandler() {
    console.debug('ShopMainComponent Save handler for shop id', this.shop);
    var _sub:any = this._shopService.saveShop(this.shop).subscribe(shop => {
      console.debug('ShopMainComponent Shop service save', shop);
      ShopEventBus.getShopEventBus().emit(shop);
      _sub.unsubscribe();
    });
  }

  onPowerOff() {
    console.debug('ShopMainComponent Power off handler for shop', this.shop);
    var _sub:any = this._shopService.updateDisabledFlag(this.shop, !this.shop.disabled).subscribe(shop => {
      console.debug('ShopMainComponent Shop service power off', shop);
      ShopEventBus.getShopEventBus().emit(shop);
      _sub.unsubscribe();
    });
  }

  onDiscardEvent() {
    console.debug('ShopMainComponent Discard handler for shop', this.shop);
    this.shop = Util.clone(ShopEventBus.getShopEventBus().current());
    this.shopDisabled = this.shop.disabled;
    this.changed = false;
    this.validForSave = false;
    this.formReset();
  }

  onRefreshHandler() {
    console.debug('ShopMainComponent Refresh handler', this.shop);
    if (this.shop.shopId > 0) {
      var _sub:any = this._shopService.getShop(this.shop.shopId).subscribe(shop => {
        ShopEventBus.getShopEventBus().emit(shop);
        _sub.unsubscribe();
      });
    } else {
      this.onDiscardEvent();
    }
  }

}
