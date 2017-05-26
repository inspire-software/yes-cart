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
import { CarrierSlaVO, PaymentGatewayInfoVO, FulfilmentCentreInfoVO, ValidationRequestVO, Pair } from './../../shared/model/index';
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

  private _excludeMonday:boolean = false;
  private _excludeTuesday:boolean = false;
  private _excludeWednesday:boolean = false;
  private _excludeThursday:boolean = false;
  private _excludeFriday:boolean = false;
  private _excludeSaturday:boolean = false;
  private _excludeSunday:boolean = false;

  private _excludefrom:Date;
  private _excludeto:Date;

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
      'maxDays': ['', YcValidators.positiveWholeNumber],
      'minDays': ['', YcValidators.requiredPositiveWholeNumber],
      'excludeMonday': [''],
      'excludeTuesday': [''],
      'excludeWednesday': [''],
      'excludeThursday': [''],
      'excludeFriday': [''],
      'excludeSaturday': [''],
      'excludeSunday': [''],
      'excludefrom': ['', YcValidators.validDate],
      'excludeto': ['', YcValidators.validDate],
      'guaranteed': [''],
      'namedDay': [''],
      'excludeCustomerTypes': [''],
      'slaType': ['', Validators.required],
      'script': ['', YcValidators.nonBlankTrimmed],
      'billingAddressNotRequired': [''],
      'deliveryAddressNotRequired': [''],
      'name': [''],
      'description': [''],
      'supportedPaymentGateways': [''],
      'supportedFulfilmentCentres': [''],
      'externalRef': ['', YcValidators.nonBlankTrimmed]
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
    this.recalculateWeekDayExclusions();

  }

  get sla():CarrierSlaVO {
    return this._sla;
  }

  get excludeMonday():boolean {
    return this._excludeMonday;
  }

  set excludeMonday(value:boolean) {
    this._excludeMonday = value;
    this.setWeekDayExclusion('2', value);
  }

  get excludeTuesday():boolean {
    return this._excludeTuesday;
  }

  set excludeTuesday(value:boolean) {
    this._excludeTuesday = value;
    this.setWeekDayExclusion('3', value);
  }

  get excludeWednesday():boolean {
    return this._excludeWednesday;
  }

  set excludeWednesday(value:boolean) {
    this._excludeWednesday = value;
    this.setWeekDayExclusion('4', value);
  }

  get excludeThursday():boolean {
    return this._excludeThursday;
  }

  set excludeThursday(value:boolean) {
    this._excludeThursday = value;
    this.setWeekDayExclusion('5', value);
  }

  get excludeFriday():boolean {
    return this._excludeFriday;
  }

  set excludeFriday(value:boolean) {
    this._excludeFriday = value;
    this.setWeekDayExclusion('6', value);
  }

  get excludeSaturday():boolean {
    return this._excludeSaturday;
  }

  set excludeSaturday(value:boolean) {
    this._excludeSaturday = value;
    this.setWeekDayExclusion('7', value);
  }

  get excludeSunday():boolean {
    return this._excludeSunday;
  }

  set excludeSunday(value:boolean) {
    this._excludeSunday = value;
    this.setWeekDayExclusion('1', value);
  }


  get excludeto():string {
    return UiUtil.dateInputGetterProxy(this, '_excludeto');
  }

  set excludeto(availableto:string) {
    UiUtil.dateInputSetterProxy(this, '_excludeto', availableto);
  }

  get excludefrom():string {
    return UiUtil.dateInputGetterProxy(this, '_excludefrom');
  }

  set excludefrom(availablefrom:string) {
    UiUtil.dateInputSetterProxy(this, '_excludefrom', availablefrom);
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

  onExclusionDateRemove(range:Pair<Date, Date>) {
    LogUtil.debug('SlaComponent onExclusionDateRemove', this._sla, range);
    if (this._sla.excludeDates != null) {
      let idx = this._sla.excludeDates.findIndex(pair => {
        return pair.first == range.first;
      });
      if (idx != -1) {
        this._sla.excludeDates.splice(idx, 1);
        this._sla.excludeDates = this._sla.excludeDates.slice(0, this._sla.excludeDates.length); // reset to propagate changes
        this.formChange();
      }
    }
  }

  onExclusionDateNew() {
    if (this._excludefrom != null) {
      if (this._sla.excludeDates == null) {
        this._sla.excludeDates = [];
      }
      this._sla.excludeDates.push({first: this._excludefrom, second: this._excludeto});
      this._excludefrom = null;
      this._excludeto = null;
      LogUtil.debug('SlaComponent onExclusionDateNew', this._sla);
    }
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
        labels.push({
          warehouseId: 0, code: code, name: code, description: null,
          countryCode: null, stateCode: null, city: null, postcode: null,
          defaultStandardStockLeadTime: 0, defaultBackorderStockLeadTime: 0,
          multipleShippingSupported: false,
          displayNames: []
        });
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

  private setWeekDayExclusion(day:string, exclude:boolean) {
    if (this._sla != null) {
      if (this._sla.excludeWeekDays == null) {
        this._sla.excludeWeekDays = [];
      }
      LogUtil.debug('SlaComponent setWeekDayExclusion', day, exclude, this._sla.excludeWeekDays);
      let idx = this._sla.excludeWeekDays.indexOf(day);
      if (exclude && idx == -1) { // excluded day, was not in exclusions
        this._sla.excludeWeekDays.push(day);
        LogUtil.debug('SlaComponent setWeekDayExclusion push', day, exclude, this._sla.excludeWeekDays);
      } else if (!exclude && idx != -1) { // included day, was in exclusions
        this._sla.excludeWeekDays.splice(idx, 1);
        LogUtil.debug('SlaComponent setWeekDayExclusion splice', day, exclude, this._sla.excludeWeekDays);
      }
    }
  }

  private recalculateWeekDayExclusions():void {

    this._excludefrom = null;
    this._excludeto = null;

    if (this._sla != null && this._sla.excludeWeekDays != null) {
      this._excludeMonday = this._sla.excludeWeekDays.indexOf('2') != -1;
      this._excludeTuesday = this._sla.excludeWeekDays.indexOf('3') != -1;
      this._excludeWednesday = this._sla.excludeWeekDays.indexOf('4') != -1;
      this._excludeThursday = this._sla.excludeWeekDays.indexOf('5') != -1;
      this._excludeFriday = this._sla.excludeWeekDays.indexOf('6') != -1;
      this._excludeSaturday = this._sla.excludeWeekDays.indexOf('7') != -1;
      this._excludeSunday = this._sla.excludeWeekDays.indexOf('1') != -1;
    } else {
      this._excludeMonday = false;
      this._excludeTuesday = false;
      this._excludeWednesday = false;
      this._excludeThursday = false;
      this._excludeFriday = false;
      this._excludeSaturday = false;
      this._excludeSunday = false;
    }
  }

}
