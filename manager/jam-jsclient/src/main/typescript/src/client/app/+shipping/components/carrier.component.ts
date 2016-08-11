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
import {Component, OnInit, OnDestroy, Input, Output, EventEmitter} from '@angular/core';
import {NgIf} from '@angular/common';
import {FormBuilder, Validators, REACTIVE_FORM_DIRECTIVES} from '@angular/forms';
import {CarrierVO, CarrierShopLinkVO, ShopVO, Pair} from './../../shared/model/index';
import {FormValidationEvent, Futures, Future} from './../../shared/event/index';
import {I18nComponent} from './../../shared/i18n/index';
import {TAB_DIRECTIVES} from 'ng2-bootstrap/ng2-bootstrap';


@Component({
  selector: 'yc-carrier',
  moduleId: module.id,
  templateUrl: 'carrier.component.html',
  directives: [NgIf, REACTIVE_FORM_DIRECTIVES, TAB_DIRECTIVES, I18nComponent],
})

export class CarrierComponent implements OnInit, OnDestroy {

  _carrier:CarrierVO;

  _shops:any = {};

  availableShops:Array<Pair<ShopVO, CarrierShopLinkVO>> = [];
  supportedShops:Array<Pair<ShopVO, CarrierShopLinkVO>> = [];

  @Output() dataChanged: EventEmitter<FormValidationEvent<CarrierVO>> = new EventEmitter<FormValidationEvent<CarrierVO>>();

  changed:boolean = false;
  validForSave:boolean = false;
  delayedChange:Future;

  carrierForm:any;
  carrierFormSub:any;

  constructor(fb: FormBuilder) {
    console.debug('CarrierComponent constructed');

    this.carrierForm = fb.group({});

    let that = this;
    this.delayedChange = Futures.perpetual(function() {
      that.formChange();
    }, 200);

  }

  formReset():void {
    // Hack to reset NG2 forms see https://github.com/angular/angular/issues/4933
    for(let key in this.carrierForm.controls) {
      this.carrierForm.controls[key]['_pristine'] = true;
      this.carrierForm.controls[key]['_touched'] = false;
    }
  }


  formBind():void {
    this.carrierFormSub = this.carrierForm.valueChanges.subscribe((data:any) => {
      this.validForSave = this.carrierForm.valid;
      if (this.changed) {
        this.delayedChange.delay();
      }
    });
  }


  formUnbind():void {
    if (this.carrierFormSub) {
      console.debug('CarrierComponent unbining form');
      this.carrierFormSub.unsubscribe();
    }
  }

  formChange():void {
    console.debug('CarrierComponent validating formGroup is valid: ' + this.validForSave, this._carrier);
    this.dataChanged.emit({ source: this._carrier, valid: this.validForSave });
  }

  @Input()
  set shops(shops:Array<ShopVO>) {
    shops.forEach(shop => {
      this._shops['S' + shop.shopId] = shop;
    });
    console.debug('CarrierComponent mapped shops', this._shops);
  }

  @Input()
  set carrier(carrier:CarrierVO) {
    this._carrier = carrier;
    this.changed = false;
    this.formReset();
    this.recalculateShops();
  }

  get carrier():CarrierVO {
    return this._carrier;
  }

  private recalculateShops():void {
    if (this._carrier && this._carrier.carrierId > 0) {
      this.availableShops = this.getAvailableShopNames();
      this.supportedShops = this.getSupportedShopNames();
    } else {
      this.availableShops = [];
      this.supportedShops = [];
    }
  }

  private getAvailableShopNames():Array<Pair<ShopVO, CarrierShopLinkVO>> {
    let supported = this._carrier.carrierShops;
    let skipKeys = <Array<string>>[];
    if (supported) {
      supported.forEach(carriershop => {
        skipKeys.push('S' + carriershop.shopId);
      });
    }
    console.debug('CarrierComponent supported', skipKeys);

    let labels = <Array<Pair<ShopVO, CarrierShopLinkVO>>>[];
    for (let key in this._shops) {
      if (skipKeys.indexOf(key) == -1) {
        let shop = this._shops[key];
        labels.push({
          first: shop,
          second: { carrierId: this._carrier.carrierId, shopId: shop.shopId, disabled: false }
        });
      }
    }

    console.debug('CarrierComponent available', labels);
    return labels;
  }

  private getSupportedShopNames():Array<Pair<ShopVO, CarrierShopLinkVO>> {
    let supported = this._carrier.carrierShops;
    let keepKeys = <Array<string>>[];
    if (supported) {
      supported.forEach(carriershop => {
        keepKeys.push('S' + carriershop.shopId);
      });
    }
    console.debug('CarrierComponent supported', keepKeys);

    let labels = <Array<Pair<ShopVO, CarrierShopLinkVO>>>[];
    for (let key in this._shops) {
      let idx = keepKeys.indexOf(key);
      if (idx != -1) {
        let shop = this._shops[key];
        let carrierShop = this._carrier.carrierShops[idx];
        labels.push({ first: shop, second: carrierShop });
      }
    }

    console.debug('CarrierComponent supported', labels);
    return labels;
  }


  onSupportedShopClick(supported:Pair<ShopVO, CarrierShopLinkVO>) {
    console.debug('SlaComponent remove supported', supported);
    let idx = this._carrier.carrierShops.findIndex(link =>
      link.shopId == supported.first.shopId
    );
    if (idx != -1) {
      this._carrier.carrierShops.splice(idx, 1);
      this.recalculateShops();
      this.changed = true;
      this.dataChanged.emit({ source: this._carrier, valid: this.validForSave });
    }
  }

  onAvailableShopClick(available:Pair<ShopVO, CarrierShopLinkVO>) {
    console.debug('SlaComponent add supported', available);
    this._carrier.carrierShops.push(available.second);
    this.recalculateShops();
    this.changed = true;
    this.dataChanged.emit({ source: this._carrier, valid: this.validForSave });
  }


  onDataChanged(event:any) {
    console.debug('CarrierComponent data changed', this._carrier);
    this.changed = true;
  }

  ngOnInit() {
    console.debug('CarrierComponent ngOnInit');
    this.formBind();
  }

  ngOnDestroy() {
    console.debug('CarrierComponent ngOnDestroy');
    this.formUnbind();
  }

  tabSelected(tab:any) {
    console.debug('CarrierComponent tabSelected', tab);
  }



}
