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
import { FormBuilder } from '@angular/forms';
import { YcValidators } from './../../shared/validation/validators';
import { ShopVO, ValidationRequestVO } from './../../shared/model/index';
import { ShopEventBus, ShopService, Util } from './../../shared/services/index';
import { Futures, Future } from './../../shared/event/index';
import { ModalComponent, ModalResult, ModalAction } from './../../shared/modal/index';
import { UiUtil } from './../../shared/ui/index';
import { LogUtil } from './../../shared/log/index';

@Component({
  selector: 'yc-shop-main',
  moduleId: module.id,
  templateUrl: 'shop-main.component.html',
})

export class ShopMainComponent implements OnInit, OnDestroy {

  private shop:ShopVO;
  private shopDisabled:boolean = false;

  private changed:boolean = false;
  private validForSave:boolean = false;

  private initialising:boolean = false; // tslint:disable-line:no-unused-variable
  private delayedChange:Future;

  private shopMainForm:any;
  private shopMainFormSub:any; // tslint:disable-line:no-unused-variable

  private shopSub:any;

  @ViewChild('disableConfirmationModalDialog')
  private disableConfirmationModalDialog:ModalComponent;

  private offValue:String;

  constructor(private _shopService:ShopService,
              fb: FormBuilder) {

    LogUtil.debug('ShopMainComponent constructed');

    let that = this;

    let validCode = function(control:any):any {

      let code = control.value;
      if (that.shop == null|| !that.shopMainForm || (!that.shopMainForm.dirty && that.shop.shopId > 0)) {
        return null;
      }

      let basic = YcValidators.requiredValidCode(control);
      if (basic == null) {
        var req:ValidationRequestVO = { subject: 'shop', subjectId: that.shop.shopId, field: 'code', value: code };
        return YcValidators.validRemoteCheck(control, req);
      }
      return basic;
    };

    this.shopMainForm = fb.group({
        'code': ['', validCode],
        'name': ['', YcValidators.requiredNonBlankTrimmed],
        'description': [''],
        'fspointer': [''],
    });

    this.shopSub = ShopEventBus.getShopEventBus().shopUpdated$.subscribe(shopevt => {
      this.validForSave = false;
      this.shopDisabled = shopevt.disabled;
      UiUtil.formInitialise(this, 'initialising', 'shopMainForm', 'shop', Util.clone(shopevt), shopevt.shopId > 0, [ 'code' ]);
    });

    this.delayedChange = Futures.perpetual(function() {
      that.formChange();
    }, 200);
  }

  formBind():void {
    UiUtil.formBind(this, 'shopMainForm', 'shopMainFormSub', 'delayedChange', 'initialising');
  }

  formUnbind():void {
    UiUtil.formUnbind(this, 'shopMainFormSub');
  }

  formChange():void {
    LogUtil.debug('ShopMainComponent formChange', this.shopMainForm.valid, this.shop);
    this.changed = this.shopMainForm.dirty;
    this.validForSave = this.shopMainForm.valid;
  }


  ngOnInit() {

    this.onDiscardEvent();
    this.formBind();
    LogUtil.debug('ShopMainComponent ngOnInit', this.shop);

  }

  ngOnDestroy() {
    LogUtil.debug('ShopComponent ngOnDestroy');
    if (this.shopSub) {
      this.shopSub.unsubscribe();
    }
    this.formUnbind();
  }

  onSaveHandler() {
    LogUtil.debug('ShopMainComponent Save handler for shop id', this.shop);
    var _sub:any = this._shopService.saveShop(this.shop).subscribe(shop => {
      LogUtil.debug('ShopMainComponent Shop service save', shop);
      ShopEventBus.getShopEventBus().emit(shop);
      _sub.unsubscribe();
    });
  }

  onPowerOff() {
    this.offValue = this.shop.code + (this.shop.name ? (': ' + this.shop.name) : '');
    this.disableConfirmationModalDialog.show();
  }


  onDisableConfirmationResult(modalresult: ModalResult) {
    LogUtil.debug('ShopMainComponent onDisableConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {
      LogUtil.debug('ShopMainComponent Power off handler for shop', this.shop);
      var _sub:any = this._shopService.updateDisabledFlag(this.shop, !this.shop.disabled).subscribe(shop => {
        LogUtil.debug('ShopMainComponent Shop service power off', shop);
        ShopEventBus.getShopEventBus().emit(shop);
        _sub.unsubscribe();
      });
    }
  }


  onDiscardEvent() {
    LogUtil.debug('ShopMainComponent Discard handler for shop', this.shop);
    this.shop = Util.clone(ShopEventBus.getShopEventBus().current());
    this.shopDisabled = this.shop.disabled;
    this.changed = false;
    this.validForSave = false;
    this.shopMainForm.reset(this.shop);
  }

  onRefreshHandler() {
    LogUtil.debug('ShopMainComponent Refresh handler', this.shop);
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
