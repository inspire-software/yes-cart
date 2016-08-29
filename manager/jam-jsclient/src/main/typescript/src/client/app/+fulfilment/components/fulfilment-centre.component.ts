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
import {YcValidators} from './../../shared/validation/validators';
import {FulfilmentCentreVO, FulfilmentCentreShopLinkVO, ShopVO, Pair} from './../../shared/model/index';
import {FormValidationEvent, Futures, Future} from './../../shared/event/index';
import {TAB_DIRECTIVES} from 'ng2-bootstrap/ng2-bootstrap';


@Component({
  selector: 'yc-fulfilment-centre',
  moduleId: module.id,
  templateUrl: 'fulfilment-centre.component.html',
  directives: [NgIf, REACTIVE_FORM_DIRECTIVES, TAB_DIRECTIVES],
})

export class FulfilmentCentreComponent implements OnInit, OnDestroy {

  _centre:FulfilmentCentreVO;

  _shops:any = {};

  availableShops:Array<Pair<ShopVO, FulfilmentCentreShopLinkVO>> = [];
  supportedShops:Array<Pair<ShopVO, FulfilmentCentreShopLinkVO>> = [];

  @Output() dataChanged: EventEmitter<FormValidationEvent<FulfilmentCentreVO>> = new EventEmitter<FormValidationEvent<FulfilmentCentreVO>>();

  changed:boolean = false;
  validForSave:boolean = false;
  delayedChange:Future;

  centreForm:any;
  centreFormSub:any;

  constructor(fb: FormBuilder) {
    console.debug('FulfilmentCentreComponent constructed');

    this.centreForm = fb.group({
      'code': ['', YcValidators.requiredValidCode],
      'name': ['', YcValidators.requiredNonBlankTrimmed],
      'description': [''],
      'countryCode': ['', YcValidators.validCountryCode],
      'stateCode': ['', YcValidators.nonBlankTrimmed],
      'city': ['', YcValidators.nonBlankTrimmed],
      'postcode': ['', YcValidators.nonBlankTrimmed],
    });

    let that = this;
    this.delayedChange = Futures.perpetual(function() {
      that.formChange();
    }, 200);

  }

  formReset():void {
    // Hack to reset NG2 forms see https://github.com/angular/angular/issues/4933
    for(let key in this.centreForm.controls) {
      this.centreForm.controls[key]['_pristine'] = true;
      this.centreForm.controls[key]['_touched'] = false;
    }
  }


  formBind():void {
    this.centreFormSub = this.centreForm.statusChanges.subscribe((data:any) => {
      this.validForSave = this.centreForm.valid;
      if (this.changed) {
        this.delayedChange.delay();
      }
    });
  }


  formUnbind():void {
    if (this.centreFormSub) {
      console.debug('FulfilmentCentreComponent unbining form');
      this.centreFormSub.unsubscribe();
    }
  }

  formChange():void {
    console.debug('FulfilmentCentreComponent validating formGroup is valid: ' + this.validForSave, this._centre);
    this.dataChanged.emit({ source: this._centre, valid: this.validForSave });
  }

  @Input()
  set shops(shops:Array<ShopVO>) {
    shops.forEach(shop => {
      this._shops['S' + shop.shopId] = shop;
    });
    console.debug('FulfilmentCentreComponent mapped shops', this._shops);
  }

  @Input()
  set centre(centre:FulfilmentCentreVO) {
    this._centre = centre;
    this.changed = false;
    this.formReset();
    this.recalculateShops();
  }

  get centre():FulfilmentCentreVO {
    return this._centre;
  }

  private recalculateShops():void {
    if (this._centre && this._centre.warehouseId > 0) {
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
    console.debug('FulfilmentCentreComponent supported', skipKeys);

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

    console.debug('FulfilmentCentreComponent available', labels);
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
    console.debug('FulfilmentCentreComponent supported', keepKeys);

    let labels = <Array<Pair<ShopVO, FulfilmentCentreShopLinkVO>>>[];
    for (let key in this._shops) {
      let idx = keepKeys.indexOf(key);
      if (idx != -1) {
        let shop = this._shops[key];
        let centreShop = this._centre.fulfilmentShops[idx];
        labels.push({ first: shop, second: centreShop });
      }
    }

    console.debug('FulfilmentCentreComponent supported', labels);
    return labels;
  }


  onSupportedShopClick(supported:Pair<ShopVO, FulfilmentCentreShopLinkVO>) {
    console.debug('SlaComponent remove supported', supported);
    let idx = this._centre.fulfilmentShops.findIndex(link =>
      link.shopId == supported.first.shopId
    );
    if (idx != -1) {
      this._centre.fulfilmentShops.splice(idx, 1);
      this.recalculateShops();
      this.changed = true;
      this.dataChanged.emit({ source: this._centre, valid: this.validForSave });
    }
  }

  onAvailableShopClick(available:Pair<ShopVO, FulfilmentCentreShopLinkVO>) {
    console.debug('SlaComponent add supported', available);
    this._centre.fulfilmentShops.push(available.second);
    this.recalculateShops();
    this.changed = true;
    this.dataChanged.emit({ source: this._centre, valid: this.validForSave });
  }


  onDataChange(event:any) {
    console.debug('FulfilmentCentreComponent data changed', this._centre);
    this.changed = true;
  }

  ngOnInit() {
    console.debug('FulfilmentCentreComponent ngOnInit');
    this.formBind();
  }

  ngOnDestroy() {
    console.debug('FulfilmentCentreComponent ngOnDestroy');
    this.formUnbind();
  }

  tabSelected(tab:any) {
    console.debug('FulfilmentCentreComponent tabSelected', tab);
  }



}
