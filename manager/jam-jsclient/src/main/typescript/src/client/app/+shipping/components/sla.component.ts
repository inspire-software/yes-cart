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
import { Component, OnInit, OnDestroy, Input, Output, EventEmitter } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { YcValidators } from './../../shared/validation/validators';
import { CarrierSlaVO, PaymentGatewayInfoVO, FulfilmentCentreInfoVO, ValidationRequestVO } from './../../shared/model/index';
import { FormValidationEvent, Futures, Future } from './../../shared/event/index';
import { UiUtil } from './../../shared/ui/index';
import { LogUtil } from './../../shared/log/index';

@Component({
  selector: 'yc-sla',
  moduleId: module.id,
  templateUrl: 'sla.component.html',
})

export class SlaComponent implements OnInit, OnDestroy {

  @Output() dataChanged: EventEmitter<FormValidationEvent<CarrierSlaVO>> = new EventEmitter<FormValidationEvent<CarrierSlaVO>>();

  private _sla:CarrierSlaVO;

  private _pgs:any = {};

  private availablePgs:Array<PaymentGatewayInfoVO> = [];
  private supportedPgs:Array<PaymentGatewayInfoVO> = [];

  private _fcs:any = {};

  private availableFcs:Array<FulfilmentCentreInfoVO> = [];
  private supportedFcs:Array<FulfilmentCentreInfoVO> = [];

  private initialising:boolean = false; // tslint:disable-line:no-unused-variable
  private delayedChange:Future;

  private slaForm:any;
  private slaFormSub:any; // tslint:disable-line:no-unused-variable

  constructor(fb: FormBuilder) {
    LogUtil.debug('SlaComponent constructed');

    let that = this;

    let validCode = function(control:any):any {

      let basic = Validators.required(control);
      if (basic == null) {

        let code = control.value;
        if (that._sla == null || !that.slaForm || (!that.slaForm.dirty && that._sla.carrierslaId > 0)) {
          return null;
        }

        basic = YcValidators.validCode(control);
        if (basic == null) {
          var req:ValidationRequestVO = {
            subject: 'carriersla',
            subjectId: that._sla.carrierslaId,
            field: 'guid',
            value: code
          };
          return YcValidators.validRemoteCheck(control, req);
        }
      }
      return basic;
    };

    this.slaForm = fb.group({
      'code': ['', validCode],
      'maxDays': ['', YcValidators.requiredPositiveWholeNumber],
      'slaType': ['', Validators.required],
      'script': ['', YcValidators.nonBlankTrimmed],
      'billingAddressNotRequired': [''],
      'deliveryAddressNotRequired': [''],
      'name': [''],
      'description': [''],
      'supportedPaymentGateways': [''],
      'supportedFulfilmentCentres': ['']
    });

    this.delayedChange = Futures.perpetual(function() {
      that.formChange();
    }, 200);
  }

  formBind():void {
    UiUtil.formBind(this, 'slaForm', 'slaFormSub', 'delayedChange', 'initialising');
  }


  formUnbind():void {
    UiUtil.formUnbind(this, 'slaFormSub');
  }

  formChange():void {
    LogUtil.debug('SlaComponent formChange', this.slaForm.valid, this._sla);
    this.dataChanged.emit({ source: this._sla, valid: this.slaForm.valid });
  }

  formMarkDirty(field:string):void {
    UiUtil.formMarkFieldDirty(this, 'slaForm', field);
  }

  @Input()
  set paymentGateways(pgs:Array<PaymentGatewayInfoVO>) {
    pgs.forEach(pg => {
      this._pgs[pg.label] = pg;
    });
    LogUtil.debug('SlaComponent mapped PGs', this._pgs);
  }

  @Input()
  set fulfilmentCentres(fcs:Array<FulfilmentCentreInfoVO>) {
    fcs.forEach(fc => {
      this._fcs[fc.code] = fc;
    });
    LogUtil.debug('SlaComponent mapped FCs', this._fcs);
  }

  @Input()
  set sla(sla:CarrierSlaVO) {

    UiUtil.formInitialise(this, 'initialising', 'slaForm', '_sla', sla);
    this.recalculatePgs();
    this.recalculateFcs();

  }

  get sla():CarrierSlaVO {
    return this._sla;
  }

  onNameDataChange(event:FormValidationEvent<any>) {
    UiUtil.formI18nDataChange(this, 'slaForm', 'name', event);
  }

  onDescriptionDataChange(event:FormValidationEvent<any>) {
    UiUtil.formI18nDataChange(this, 'slaForm', 'description', event);
  }

  onSupportedPgClick(supported:PaymentGatewayInfoVO) {
    LogUtil.debug('SlaComponent remove supported PG', supported);
    let idx = this._sla.supportedPaymentGateways.indexOf(supported.label);
    if (idx != -1) {
      this._sla.supportedPaymentGateways.splice(idx, 1);
      this.recalculatePgs();
      this.formMarkDirty('supportedPaymentGateways');
      this.formChange();
    }
  }

  onAvailablePgClick(available:PaymentGatewayInfoVO) {
    LogUtil.debug('SlaComponent add supported PG', available);
    if (this._sla.supportedPaymentGateways == null) {
      this._sla.supportedPaymentGateways = [];
    }
    let idx = this._sla.supportedPaymentGateways.indexOf(available.label);
    if (idx == -1) {
      this._sla.supportedPaymentGateways.push(available.label);
      this.recalculatePgs();
      this.formMarkDirty('supportedPaymentGateways');
      this.formChange();
    }
  }


  onSupportedFcClick(supported:FulfilmentCentreInfoVO) {
    LogUtil.debug('SlaComponent remove supported FC', supported);
    let idx = this._sla.supportedFulfilmentCentres.indexOf(supported.code);
    if (idx != -1) {
      this._sla.supportedFulfilmentCentres.splice(idx, 1);
      this.recalculateFcs();
      this.formMarkDirty('supportedFulfilmentCentres');
      this.formChange();
    }
  }

  onAvailableFcClick(available:FulfilmentCentreInfoVO) {
    LogUtil.debug('SlaComponent add supported FC', available);
    if (this._sla.supportedFulfilmentCentres == null) {
      this._sla.supportedFulfilmentCentres = [];
    }
    let idx = this._sla.supportedFulfilmentCentres.indexOf(available.code);
    if (idx == -1) {
      this._sla.supportedFulfilmentCentres.push(available.code);
      this.recalculateFcs();
      this.formMarkDirty('supportedFulfilmentCentres');
      this.formChange();
    }
  }

  ngOnInit() {
    LogUtil.debug('SlaComponent ngOnInit');
    this.formBind();
  }

  ngOnDestroy() {
    LogUtil.debug('SlaComponent ngOnDestroy');
    this.formUnbind();
  }

  tabSelected(tab:any) {
    LogUtil.debug('SlaComponent tabSelected', tab);
  }


  private getAvailablePGNames():Array<PaymentGatewayInfoVO> {
    let supported = this._sla.supportedPaymentGateways;

    let labels = <Array<PaymentGatewayInfoVO>>[];
    for (let key in this._pgs) {
      if (!supported || supported.indexOf(key) == -1) {
        labels.push(this._pgs[key]);
      }
    }

    return labels;
  }

  private getSupportedPGNames():Array<PaymentGatewayInfoVO> {

    let supported = this._sla.supportedPaymentGateways;
    if (!supported) {
      return [ ];
    }

    let labels = <Array<PaymentGatewayInfoVO>>[];
    supported.forEach(label => {
      if (this._pgs.hasOwnProperty(label)) {
        labels.push(this._pgs[label]);
      } else {
        labels.push({ name: label, label: label, active: false });
      }
    });
    return labels;
  }

  private recalculatePgs():void {
    if (this._sla) {
      this.availablePgs = this.getAvailablePGNames();
      this.supportedPgs = this.getSupportedPGNames();
    }
  }



  private getAvailableFCNames():Array<FulfilmentCentreInfoVO> {
    let supported = this._sla.supportedFulfilmentCentres;

    let labels = <Array<FulfilmentCentreInfoVO>>[];
    for (let key in this._fcs) {
      if (!supported || supported.indexOf(key) == -1) {
        labels.push(this._fcs[key]);
      }
    }

    return labels;
  }

  private getSupportedFCNames():Array<FulfilmentCentreInfoVO> {

    let supported = this._sla.supportedFulfilmentCentres;
    if (!supported) {
      return [ ];
    }

    let labels = <Array<FulfilmentCentreInfoVO>>[];
    supported.forEach(code => {
      if (this._fcs.hasOwnProperty(code)) {
        labels.push(this._fcs[code]);
      } else {
        labels.push({ warehouseId: 0, code: code, name: code, description: null, countryCode: null, stateCode: null, city: null, postcode: null, displayNames: [] });
      }
    });
    return labels;
  }


  private recalculateFcs():void {
    if (this._sla) {
      this.availableFcs = this.getAvailableFCNames();
      this.supportedFcs = this.getSupportedFCNames();
    }
  }


}
