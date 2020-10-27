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
import { Component, OnInit, OnDestroy, Input, Output, EventEmitter } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { CustomValidators } from './../../shared/validation/validators';
import { CarrierVO, CarrierSlaInfoVO, CarrierShopLinkVO, ShopVO, PaymentGatewayInfoVO, FulfilmentCentreInfoVO, Pair, ValidationRequestVO } from './../../shared/model/index';
import { FormValidationEvent, Futures, Future } from './../../shared/event/index';
import { UiUtil } from './../../shared/ui/index';
import { LogUtil } from './../../shared/log/index';

@Component({
  selector: 'yc-carrier',
  moduleId: module.id,
  templateUrl: 'carrier.component.html',
})

export class CarrierComponent implements OnInit, OnDestroy {

  @Output() dataChanged: EventEmitter<FormValidationEvent<CarrierVO>> = new EventEmitter<FormValidationEvent<CarrierVO>>();

  @Output() slaSelected: EventEmitter<CarrierSlaInfoVO> = new EventEmitter<CarrierSlaInfoVO>();

  @Output() slaAddClick: EventEmitter<CarrierSlaInfoVO> = new EventEmitter<CarrierSlaInfoVO>();

  @Output() slaEditClick: EventEmitter<CarrierSlaInfoVO> = new EventEmitter<CarrierSlaInfoVO>();

  @Output() slaDeleteClick: EventEmitter<CarrierSlaInfoVO> = new EventEmitter<CarrierSlaInfoVO>();

  private _carrier:CarrierVO;

  private _paymentGateways:Array<PaymentGatewayInfoVO>;
  private _fulfilmentCentres:Array<FulfilmentCentreInfoVO>;
  private _shops:any = {};

  private availableShops:Array<Pair<ShopVO, CarrierShopLinkVO>> = [];
  private supportedShops:Array<Pair<ShopVO, CarrierShopLinkVO>> = [];

  private delayedChange:Future;

  private carrierForm:any;

  private slaFilter:string;
  private slaSort:Pair<string, boolean> = { first: 'name', second: false };

  private selectedSla:CarrierSlaInfoVO = null;

  constructor(fb: FormBuilder) {
    LogUtil.debug('CarrierComponent constructed');

    let that = this;

    let validCode = function(control:any):any {

      let basic = CustomValidators.requiredValidCode255(control);
      if (basic == null) {

        let code = control.value;
        if (that._carrier == null || !that.carrierForm || (!that.carrierForm.dirty && that._carrier.carrierId > 0)) {
          return null;
        }

        let req:ValidationRequestVO = { subject: 'carrier', subjectId: that._carrier.carrierId, field: 'guid', value: code };
        return CustomValidators.validRemoteCheck(control, req);
      }
      return basic;
    };

    this.carrierForm = fb.group({
      'code': ['', validCode],
      'name': [''],
      'description': [''],
      'carrierShops': ['']
    });

    this.delayedChange = Futures.perpetual(function() {
      that.formChange();
    }, 200);

  }

  formBind():void {
    UiUtil.formBind(this, 'carrierForm', 'delayedChange');
  }


  formUnbind():void {
    UiUtil.formUnbind(this, 'carrierForm');
  }

  formChange():void {
    LogUtil.debug('CarrierComponent formChange', this.carrierForm.valid, this._carrier);
    this.dataChanged.emit({ source: this._carrier, valid: this.carrierForm.valid });
  }

  formMarkDirty(field:string):void {
    UiUtil.formMarkFieldDirty(this, 'carrierForm', field);
  }

  @Input()
  set shops(shops:Array<ShopVO>) {
    if (shops != null) {
      shops.forEach(shop => {
        this._shops['S' + shop.shopId] = shop;
      });
    }
    LogUtil.debug('CarrierComponent mapped shops', this._shops);
  }

  @Input()
  set paymentGateways(pgs:Array<PaymentGatewayInfoVO>) {
    this._paymentGateways = pgs;
  }

  get paymentGateways():Array<PaymentGatewayInfoVO> {
    return this._paymentGateways;
  }


  @Input()
  set fulfilmentCentres(fcs:Array<FulfilmentCentreInfoVO>) {
    this._fulfilmentCentres = fcs;
  }

  get fulfilmentCentres():Array<FulfilmentCentreInfoVO> {
    return this._fulfilmentCentres;
  }

  @Input()
  set carrier(carrier:CarrierVO) {

    UiUtil.formInitialise(this, 'carrierForm', '_carrier', carrier);
    this.recalculateShops();

  }

  get carrier():CarrierVO {
    return this._carrier;
  }

  onNameDataChange(event:FormValidationEvent<any>) {
    UiUtil.formI18nDataChange(this, 'carrierForm', 'name', event);
  }

  onDescriptionDataChange(event:FormValidationEvent<any>) {
    UiUtil.formI18nDataChange(this, 'carrierForm', 'description', event);
  }

  onSupportedShopClick(supported:Pair<ShopVO, CarrierShopLinkVO>) {
    LogUtil.debug('SlaComponent remove supported', supported);
    let idx = this._carrier.carrierShops.findIndex(link =>
      link.shopId == supported.first.shopId
    );
    if (idx != -1) {
      this._carrier.carrierShops.splice(idx, 1);
      this.recalculateShops();
      this.formMarkDirty('carrierShops');
      this.formChange();
    }
  }

  onAvailableShopClick(available:Pair<ShopVO, CarrierShopLinkVO>) {
    LogUtil.debug('SlaComponent add supported', available);
    this._carrier.carrierShops.push(available.second);
    this.recalculateShops();
    this.formMarkDirty('carrierShops');
    this.formChange();
  }


  ngOnInit() {
    LogUtil.debug('CarrierComponent ngOnInit');
    this.formBind();
  }

  ngOnDestroy() {
    LogUtil.debug('CarrierComponent ngOnDestroy');
    this.formUnbind();
  }

  tabSelected(tab:any) {
    LogUtil.debug('CarrierComponent tabSelected', tab);
  }


  protected onClearSlaFilter() {

    this.slaFilter = '';

  }

  protected onSlaSelected(data:CarrierSlaInfoVO) {
    LogUtil.debug('CarrierComponent onSlaSelected', data);
    this.selectedSla = data;
    this.slaSelected.emit(data);
  }

  protected onRowAddSLA() {
    LogUtil.debug('CarrierComponent onRowAddSLA', this.selectedSla);
    this.slaAddClick.emit(this.selectedSla);
  }

  protected onPageSelectedSla(page:number) {
    LogUtil.debug('CarrierComponent onPageSelectedSla', page);
  }

  protected onSortSelectedSla(sort:Pair<string, boolean>) {
    LogUtil.debug('CarrierComponent onSortSelectedSla', sort);
    if (sort == null) {
      this.slaSort = { first: 'name', second: false };
    } else {
      this.slaSort = sort;
    }
  }

  protected onRowEditSelectedSLA() {
    LogUtil.debug('CarrierComponent onRowEditSelectedSLA', this.selectedSla);
    if (this.selectedSla != null) {
      this.slaEditClick.emit(this.selectedSla);
    }
  }

  protected onRowDeleteSelectedSLA() {
    LogUtil.debug('CarrierComponent onRowDeleteSelectedSLA', this.selectedSla);
    if (this.selectedSla != null) {
      this.slaDeleteClick.emit(this.selectedSla);
    }
  }


  private recalculateShops():void {
    if (this._carrier) {
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
    LogUtil.debug('CarrierComponent supported', skipKeys);

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

    LogUtil.debug('CarrierComponent available', labels);
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
    LogUtil.debug('CarrierComponent supported', keepKeys);

    let labels = <Array<Pair<ShopVO, CarrierShopLinkVO>>>[];
    for (let key in this._shops) {
      let idx = keepKeys.indexOf(key);
      if (idx != -1) {
        let shop = this._shops[key];
        let carrierShop = this._carrier.carrierShops[idx];
        labels.push({ first: shop, second: carrierShop });
      }
    }

    LogUtil.debug('CarrierComponent supported', labels);
    return labels;
  }


}
