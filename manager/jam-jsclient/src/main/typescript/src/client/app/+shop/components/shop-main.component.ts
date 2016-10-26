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
import {FormBuilder, REACTIVE_FORM_DIRECTIVES} from '@angular/forms';
import {YcValidators} from './../../shared/validation/validators';
import {ShopVO} from './../../shared/model/index';
import {ShopEventBus, ShopService, Util} from './../../shared/services/index';
import {DataControlComponent} from './../../shared/sidebar/index';
import {ModalComponent, ModalResult, ModalAction} from './../../shared/modal/index';

@Component({
  selector: 'yc-shop-main',
  moduleId: module.id,
  templateUrl: 'shop-main.component.html',
  directives: [DataControlComponent, NgIf, REACTIVE_FORM_DIRECTIVES, ModalComponent],
})

export class ShopMainComponent implements OnInit, OnDestroy {

  shop:ShopVO;
  shopDisabled:boolean = false;

  changed:boolean = false;
  validForSave:boolean = false;

  shopMainForm:any;
  shopMainFormSub:any;

  private shopSub:any;

  @ViewChild('disableConfirmationModalDialog')
  disableConfirmationModalDialog:ModalComponent;

  private offValue:String;

  constructor(private _shopService:ShopService,
              fb: FormBuilder) {

    console.debug('ShopMainComponent constructed');

    this.shopMainForm = fb.group({
        'code': ['', YcValidators.requiredValidCode],
        'name': ['', YcValidators.requiredNonBlankTrimmed],
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


  formBind():void {
    this.shopMainFormSub = this.shopMainForm.statusChanges.subscribe((data:any) => {
      if (this.changed) {
        this.validForSave = this.shopMainForm.valid;
      }
    });
  }

  formUnbind():void {
    if (this.shopMainFormSub) {
      console.debug('ShopMainComponent unbining form');
      this.shopMainFormSub.unsubscribe();
    }
  }


  ngOnInit() {

    this.onDiscardEvent();
    this.formBind();
    console.debug('ShopMainComponent ngOnInit', this.shop);

  }

  ngOnDestroy() {
    console.debug('ShopComponent ngOnDestroy');
    if (this.shopSub) {
      this.shopSub.unsubscribe();
    }
    this.formUnbind();
  }

  onDataChange(event:any) {
    console.debug('ShopMainComponent data changed', event);
    this.changed = true;
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
    this.offValue = this.shop.code + (this.shop.name ? (': ' + this.shop.name) : '');
    this.disableConfirmationModalDialog.show();
  }


  protected onDisableConfirmationResult(modalresult: ModalResult) {
    console.debug('ShopMainComponent onDisableConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {
      console.debug('ShopMainComponent Power off handler for shop', this.shop);
      var _sub:any = this._shopService.updateDisabledFlag(this.shop, !this.shop.disabled).subscribe(shop => {
        console.debug('ShopMainComponent Shop service power off', shop);
        ShopEventBus.getShopEventBus().emit(shop);
        _sub.unsubscribe();
      });
    }
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
