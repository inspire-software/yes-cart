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
import { Component, OnInit, OnDestroy, ViewChild, Input, Output, EventEmitter } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { CustomValidators } from './../../shared/validation/validators';
import { CountrySelectComponent, CountryStateSelectComponent } from './../../shared/shipping/index';
import {
  FulfilmentCentreVO, FulfilmentCentreShopLinkVO, ShopVO, Pair, ValidationRequestVO,
  CountryInfoVO, StateVO
} from './../../shared/model/index';
import { FormValidationEvent, Futures, Future } from './../../shared/event/index';
import { UiUtil } from './../../shared/ui/index';
import { LogUtil } from './../../shared/log/index';

@Component({
  selector: 'cw-fulfilment-centre',
  templateUrl: 'fulfilment-centre.component.html',
})

export class FulfilmentCentreComponent implements OnInit, OnDestroy {

  @Output() dataChanged: EventEmitter<FormValidationEvent<FulfilmentCentreVO>> = new EventEmitter<FormValidationEvent<FulfilmentCentreVO>>();

  private _centre:FulfilmentCentreVO;

  private _shops:any = {};

  public availableShops:Array<Pair<ShopVO, FulfilmentCentreShopLinkVO>> = [];
  public supportedShops:Array<Pair<ShopVO, FulfilmentCentreShopLinkVO>> = [];

  public delayedChange:Future;

  public centreForm:any;

  @ViewChild('selectCountryModalDialog')
  private selectCountryModalDialog:CountrySelectComponent;

  @ViewChild('selectStateModalDialog')
  private selectStateModalDialog:CountryStateSelectComponent;


  constructor(fb: FormBuilder) {
    LogUtil.debug('FulfilmentCentreComponent constructed');

    let that = this;

    let validCode = function(control:any):any {

      let basic = Validators.required(control);
      if (basic == null) {

        let code = control.value;
        if (that._centre == null || !that.centreForm || !that.centreForm.dirty) {
          return null;
        }

        basic = CustomValidators.validCode255(control);
        if (basic == null) {
          let req:ValidationRequestVO = {
            subject: 'warehouse',
            subjectId: that._centre.warehouseId,
            field: 'code',
            value: code
          };
          return CustomValidators.validRemoteCheck(control, req);
        }
      }
      return basic;
    };

    this.centreForm = fb.group({
      'code': ['', validCode],
      'name': [''],
      'description': [''],
      'countryCode': ['', CustomValidators.validCountryCode],
      'stateCode': ['', CustomValidators.nonBlankTrimmed],
      'city': ['', CustomValidators.nonBlankTrimmed],
      'postcode': ['', CustomValidators.nonBlankTrimmed],
      'defaultStandardStockLeadTime': ['', CustomValidators.requiredPositiveWholeNumber],
      'defaultBackorderStockLeadTime': ['', CustomValidators.requiredPositiveWholeNumber],
      'multipleShippingSupported': [''],
      'forceBackorderDeliverySplit': [''],
      'forceAllDeliverySplit': [''],
      'fulfilmentShops': [''],
    });

    this.delayedChange = Futures.perpetual(function() {
      that.formChange();
    }, 200);

  }

  formBind():void {
    UiUtil.formBind(this, 'centreForm', 'delayedChange');
  }


  formUnbind():void {
    UiUtil.formUnbind(this, 'centreForm');
  }

  formChange():void {
    LogUtil.debug('FulfilmentCentreComponent formChange', this.centreForm.valid, this._centre);
    this.dataChanged.emit({ source: this._centre, valid: this.centreForm.valid });
  }

  formMarkDirty(field:string):void {
    UiUtil.formMarkFieldDirty(this, 'centreForm', field);
  }

  @Input()
  set shops(shops:Array<ShopVO>) {
    if (shops != null) {
      shops.forEach(shop => {
        this._shops['S' + shop.shopId] = shop;
      });
    }
    LogUtil.debug('FulfilmentCentreComponent mapped shops', this._shops);
  }

  @Input()
  set centre(centre:FulfilmentCentreVO) {

    UiUtil.formInitialise(this, 'centreForm', '_centre', centre);
    this.recalculateShops();

  }

  get centre():FulfilmentCentreVO {
    return this._centre;
  }

  onNameDataChange(event:FormValidationEvent<any>) {
    UiUtil.formI18nDataChange(this, 'centreForm', 'name', event);
  }

  onSupportedShopClick(supported:Pair<ShopVO, FulfilmentCentreShopLinkVO>) {
    LogUtil.debug('SlaComponent remove supported', supported);
    let idx = this._centre.fulfilmentShops.findIndex(link =>
      link.shopId == supported.first.shopId
    );
    if (idx != -1) {
      this._centre.fulfilmentShops.splice(idx, 1);
      this.recalculateShops();
      this.formMarkDirty('fulfilmentShops');
      this.formChange();
    }
  }

  onAvailableShopClick(available:Pair<ShopVO, FulfilmentCentreShopLinkVO>) {
    LogUtil.debug('SlaComponent add supported', available);
    this._centre.fulfilmentShops.push(available.second);
    this.recalculateShops();
    this.formMarkDirty('fulfilmentShops');
    this.formChange();
  }


  ngOnInit() {
    LogUtil.debug('FulfilmentCentreComponent ngOnInit');
    this.formBind();
  }

  ngOnDestroy() {
    LogUtil.debug('FulfilmentCentreComponent ngOnDestroy');
    this.formUnbind();
  }

  tabSelected(tab:any) {
    LogUtil.debug('FulfilmentCentreComponent tabSelected', tab);
  }



  onCountryExact() {
    this.selectCountryModalDialog.showDialog();
  }

  onCountrySelected(event:FormValidationEvent<CountryInfoVO>) {
    LogUtil.debug('FulfilmentCentreComponent onCountrySelected');
    if (event.valid) {
      if (this._centre.stateCode != null && this._centre.stateCode != '') {
        this._centre.stateCode = null;
        this.formMarkDirty('stateCode');
      }
      this._centre.countryCode = event.source.countryCode;
      this.formMarkDirty('countryCode');
      this.formChange();
    }
  }

  onStateExact() {
    this.selectStateModalDialog.showDialog();
  }

  onStateSelected(event:FormValidationEvent<StateVO>) {
    LogUtil.debug('FulfilmentCentreComponent onCountrySelected');
    if (event.valid) {
      this._centre.stateCode = event.source.stateCode;
      this._centre.countryCode = event.source.countryCode;
      this.formMarkDirty('stateCode');
      this.formMarkDirty('countryCode');
      this.formChange();
    }
  }



  private recalculateShops():void {
    if (this._centre) {
      this.availableShops = this.getAvailableShopNames();
      this.supportedShops = this.getSupportedShopNames();
    } else {
      this.availableShops = [];
      this.supportedShops = [];
    }
  }

  private getAvailableShopNames():Array<Pair<ShopVO, FulfilmentCentreShopLinkVO>> {
    let supported = this._centre.fulfilmentShops;
    let skipKeys = <Array<string>>[];
    if (supported) {
      supported.forEach(centreshop => {
        skipKeys.push('S' + centreshop.shopId);
      });
    }
    LogUtil.debug('FulfilmentCentreComponent supported', skipKeys);

    let labels = <Array<Pair<ShopVO, FulfilmentCentreShopLinkVO>>>[];
    for (let key in this._shops) {
      if (skipKeys.indexOf(key) == -1) {
        let shop = this._shops[key];
        labels.push({
          first: shop,
          second: { warehouseId: this._centre.warehouseId, shopId: shop.shopId, disabled: false }
        });
      }
    }

    LogUtil.debug('FulfilmentCentreComponent available', labels);
    return labels;
  }

  private getSupportedShopNames():Array<Pair<ShopVO, FulfilmentCentreShopLinkVO>> {
    let supported = this._centre.fulfilmentShops;
    let keepKeys = <Array<string>>[];
    if (supported) {
      supported.forEach(centreshop => {
        keepKeys.push('S' + centreshop.shopId);
      });
    }
    LogUtil.debug('FulfilmentCentreComponent supported', keepKeys);

    let labels = <Array<Pair<ShopVO, FulfilmentCentreShopLinkVO>>>[];
    for (let key in this._shops) {
      let idx = keepKeys.indexOf(key);
      if (idx != -1) {
        let shop = this._shops[key];
        let centreShop = this._centre.fulfilmentShops[idx];
        labels.push({ first: shop, second: centreShop });
      }
    }

    LogUtil.debug('FulfilmentCentreComponent supported', labels);
    return labels;
  }


}
