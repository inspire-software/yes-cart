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
import {CarrierSlaVO, PaymentGatewayInfoVO, ValidationRequestVO} from './../../shared/model/index';
import {FormValidationEvent, Futures, Future} from './../../shared/event/index';
import {I18nComponent} from './../../shared/i18n/index';
import {TAB_DIRECTIVES} from 'ng2-bootstrap/ng2-bootstrap';


@Component({
  selector: 'yc-sla',
  moduleId: module.id,
  templateUrl: 'sla.component.html',
  directives: [NgIf, REACTIVE_FORM_DIRECTIVES, TAB_DIRECTIVES, I18nComponent],
})

export class SlaComponent implements OnInit, OnDestroy {

  _sla:CarrierSlaVO;

  _pgs:any = {};

  availablePgs:Array<PaymentGatewayInfoVO> = [];
  supportedPgs:Array<PaymentGatewayInfoVO> = [];

  @Output() dataChanged: EventEmitter<FormValidationEvent<CarrierSlaVO>> = new EventEmitter<FormValidationEvent<CarrierSlaVO>>();

  changed:boolean = false;
  validForSave:boolean = false;
  delayedChange:Future;

  slaForm:any;
  slaFormSub:any;

  constructor(fb: FormBuilder) {
    console.debug('SlaComponent constructed');

    let that = this;

    let validCode = function(control:any):any {

      let basic = Validators.required(control);
      if (basic == null) {

        let code = control.value;
        if (!that.changed || that._sla == null) {
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
    });

    this.delayedChange = Futures.perpetual(function() {
      that.formChange();
    }, 200);

  }

  formReset():void {
    // Hack to reset NG2 forms see https://github.com/angular/angular/issues/4933
    for(let key in this.slaForm.controls) {
      this.slaForm.controls[key]['_pristine'] = true;
      this.slaForm.controls[key]['_touched'] = false;
    }
  }

  formBind():void {
    this.slaFormSub = this.slaForm.statusChanges.subscribe((data:any) => {
      this.validForSave = this.slaForm.valid;
      if (this.changed) {
        this.delayedChange.delay();
      }
    });
  }

  formUnbind():void {
    if (this.slaFormSub) {
      console.debug('SlaComponent unbining form');
      this.slaFormSub.unsubscribe();
    }
  }

  formChange():void {
    console.debug('SlaComponent validating formGroup is valid: ' + this.validForSave, this._sla);
    this.dataChanged.emit({ source: this._sla, valid: this.validForSave });
  }

  @Input()
  set paymentGateways(pgs:Array<PaymentGatewayInfoVO>) {
    pgs.forEach(pg => {
      this._pgs[pg.label] = pg;
    })
  }

  @Input()
  set sla(sla:CarrierSlaVO) {
    this._sla = sla;
    this.changed = false;
    this.formReset();
    this.recalculatePgs();
  }

  get sla():CarrierSlaVO {
    return this._sla;
  }

  private recalculatePgs():void {
    if (this._sla) {
      this.availablePgs = this.getAvailablePGNames();
      this.supportedPgs = this.getSupportedPGNames();
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

  onSupportedPgClick(supported:PaymentGatewayInfoVO) {
    console.debug('SlaComponent remove supported', supported);
    let idx = this._sla.supportedPaymentGateways.indexOf(supported.label);
    if (idx != -1) {
      this._sla.supportedPaymentGateways.splice(idx, 1);
      this.recalculatePgs();
      this.changed = true;
      this.validForSave = this.slaForm.valid;
      console.debug('SlaComponent form changed PGs and valid: ' + this.validForSave);
      this.dataChanged.emit({ source: this._sla, valid: this.validForSave });
    }
  }

  onAvailablePgClick(available:PaymentGatewayInfoVO) {
    console.debug('SlaComponent add supported', available);
    if (this._sla.supportedPaymentGateways == null) {
      this._sla.supportedPaymentGateways = [];
    }
    let idx = this._sla.supportedPaymentGateways.indexOf(available.label);
    if (idx == -1) {
      this._sla.supportedPaymentGateways.push(available.label);
      this.recalculatePgs();
      this.changed = true;
      this.validForSave = this.slaForm.valid;
      console.debug('SlaComponent form changed PGs and valid: ' + this.validForSave);
      this.dataChanged.emit({ source: this._sla, valid: this.validForSave });
    }
  }


  onDataChange(event:any) {
    console.debug('SlaComponent onDataChange', event);
    this.changed = true;
  }


  ngOnInit() {
    console.debug('SlaComponent ngOnInit');
    this.formBind();
  }

  ngOnDestroy() {
    console.debug('SlaComponent ngOnDestroy');
    this.formUnbind();
  }

  tabSelected(tab:any) {
    console.debug('SlaComponent tabSelected', tab);
  }



}
