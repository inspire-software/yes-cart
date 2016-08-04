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
import {CarrierSlaVO, PaymentGatewayInfoVO} from './../../shared/model/index';
import {FormValidationEvent} from './../../shared/event/index';
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

  validForSave:boolean = false;

  slaForm:any;

  constructor(fb: FormBuilder) {
    console.debug('SlaComponent constructed');

    this.slaForm = fb.group({
      'code': ['', Validators.compose([Validators.required, Validators.pattern('[A-Za-z0-9\-]+')])],
      'maxDays': ['', Validators.compose([Validators.required, Validators.pattern('[0-9]+')])],
      'slaType': ['', Validators.required],
      'script': [''],
      'billingAddressNotRequired': [''],
      'deliveryAddressNotRequired': [''],
    });

  }

  formReset():void {
    // Hack to reset NG2 forms see https://github.com/angular/angular/issues/4933
    for(let key in this.slaForm.controls) {
      this.slaForm.controls[key]['_pristine'] = true;
      this.slaForm.controls[key]['_touched'] = false;
    }
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
    if (!supported) {
      return [ ];
    }

    let labels = <Array<PaymentGatewayInfoVO>>[];
    for (let key in this._pgs) {
      if (supported.indexOf(key) == -1) {
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
      this.validForSave = this.slaForm.valid;
      console.debug('SlaComponent form changed PGs and ' + (this.validForSave ? 'is valid' : 'is NOT valid'));
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
      this.validForSave = this.slaForm.valid;
      console.debug('SlaComponent form changed PGs and ' + (this.validForSave ? 'is valid' : 'is NOT valid'));
      this.dataChanged.emit({ source: this._sla, valid: this.validForSave });
    }
  }


  onDataChange(event:any) {
    var _sub:any = this.slaForm.valueChanges.subscribe((data:any) => {
      this.validForSave = this.slaForm.valid;
      console.debug('SlaComponent form changed  and ' + (this.validForSave ? 'is valid' : 'is NOT valid'), data);
      _sub.unsubscribe();
      this.dataChanged.emit({ source: this._sla, valid: this.validForSave });
    });
    console.debug('SlaComponent data changed and ' + (this.validForSave ? 'is valid' : 'is NOT valid'), event);
  }

  onI18nDataChange() {
    this.validForSave = this.slaForm.valid;
    console.debug('SlaComponent form changed i18n and ' + (this.validForSave ? 'is valid' : 'is NOT valid'));
    this.dataChanged.emit({ source: this._sla, valid: this.validForSave });
  }


  ngOnInit() {
    console.debug('SlaComponent ngOnInit');
  }

  ngOnDestroy() {
    console.debug('SlaComponent ngOnDestroy');
  }

  tabSelected(tab:any) {
    console.debug('SlaComponent tabSelected', tab);
  }



}
