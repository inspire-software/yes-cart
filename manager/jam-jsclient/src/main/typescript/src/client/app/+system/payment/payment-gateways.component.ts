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
import {Component, OnInit, Input, OnDestroy, ViewChild} from '@angular/core';
import {NgIf} from '@angular/common';
import {HTTP_PROVIDERS}    from '@angular/http';
import {PaymentService, I18nEventBus, Util} from './../../shared/services/index';
import {TAB_DIRECTIVES} from 'ng2-bootstrap/ng2-bootstrap';
import {GatewaysComponent, ParameterValuesComponent} from './components/index';
import {DataControlComponent} from './../../shared/sidebar/index';
import {ModalComponent, ModalResult, ModalAction} from './../../shared/modal/index';
import {PaymentGatewayVO, PaymentGatewayParameterVO, Pair} from './../../shared/model/index';
import {FormValidationEvent} from './../../shared/event/index';

@Component({
  selector: 'yc-payment-gateways',
  moduleId: module.id,
  templateUrl: 'payment-gateways.component.html',
  directives: [TAB_DIRECTIVES, NgIf, GatewaysComponent, ParameterValuesComponent, ModalComponent, DataControlComponent ],
})

export class PaymentGatewaysComponent implements OnInit, OnDestroy {

  private static PGS:string = 'pgs';
  private static PARAMS:string = 'params';

  @Input() system:boolean = true;
  @Input() shopCode:string;

  private viewMode:string = PaymentGatewaysComponent.PGS;

  private gateways:Array<PaymentGatewayVO> = [];
  private gatewayFilter:string;

  private selectedGateway:PaymentGatewayVO;

  @ViewChild('featuresModalDialog')
  featuresModalDialog:ModalComponent;

  @ViewChild('parameterValuesComponent')
  parameterValuesComponent:ParameterValuesComponent;

  private paramFilter:string;

  private selectedParam:PaymentGatewayParameterVO;

  update:Array<Pair<PaymentGatewayParameterVO, boolean>>;

  constructor(private _paymentService:PaymentService) {
    console.debug('PaymentGatewaysComponent constructed');

    this.update = [];

  }

  changed:boolean = false;
  validForSave:boolean = false;

  ngOnInit() {
    console.debug('PaymentGatewaysComponent ngOnInit');
    this.onRefreshHandler();
  }

  ngOnDestroy() {
    console.debug('PaymentGatewaysComponent ngOnDestroy');
  }


  getAllPgs() {

    if (this.system || (!this.system && this.shopCode)) {

      let lang = I18nEventBus.getI18nEventBus().current();

      var _sub:any = this._paymentService.getPaymentGatewaysWithParameters(lang, this.shopCode).subscribe( allgateways => {
        console.debug('PaymentGatewaysComponent getAllCountries', allgateways);
        this.gateways = allgateways;
        this.selectedGateway = null;
        this.viewMode = PaymentGatewaysComponent.PGS;
        this.changed = false;
        this.validForSave = false;
        _sub.unsubscribe();
      });

    } else {

      this.gateways = null;
      this.selectedGateway = null;
      this.viewMode = PaymentGatewaysComponent.PGS;
      this.changed = false;
      this.validForSave = false;

    }

  }

  protected onRefreshHandler() {
    console.debug('PaymentGatewaysComponent refresh handler');
    this.getAllPgs();
  }

  onGatewaySelected(data:PaymentGatewayVO) {
    console.debug('PaymentGatewaysComponent onGatewaySelected', data);
    this.selectedGateway = data;
    this.paramFilter = '';
  }

  onParamSelected(data:PaymentGatewayParameterVO) {
    console.debug('PaymentGatewaysComponent onParamSelected', data);
    this.selectedParam = data;
  }

  onParamChange(event:any) {
    console.debug('PaymentGatewaysComponent onParamChanged', event);
    this.validForSave = event.valid;
    this.update = event.source;
    this.changed = true;
    console.debug('PaymentGatewaysComponent data changed and ' + (this.validForSave ? 'is valid' : 'is NOT valid'), event);
  }

  protected onBackToList() {
    console.debug('PaymentGatewaysComponent onBackToList handler');
    if (this.viewMode === PaymentGatewaysComponent.PARAMS) {
      this.selectedParam = null;
      this.viewMode = PaymentGatewaysComponent.PGS;
    }
  }

  protected onRowNew() {
    console.debug('PaymentGatewaysComponent onRowNew handler');
    if (this.system) {
      this.parameterValuesComponent.onRowAdd();
    }
  }

  protected onRowDeleteSelected() {
    if (this.selectedParam != null) {
      this.parameterValuesComponent.onRowDeleteSelected();
    }
  }

  protected onRowEditSelected() {
    if (this.selectedParam != null) {
      this.parameterValuesComponent.onRowEditSelected();
    }
  }

  protected onRowInfoSelected() {
    if (this.selectedGateway != null) {
      this.featuresModalDialog.show();
    }
  }

  protected onRowEnableSelected() {
    if (this.selectedGateway != null) {
      var _sub:any = this._paymentService.updateDisabledFlag(this.shopCode, this.selectedGateway.label, this.selectedGateway.active).subscribe( done => {
        console.debug('PaymentGatewaysComponent updateDisabledFlag', done);
        this.selectedGateway.active = !this.selectedGateway.active;
        this.changed = false;
        this.validForSave = false;
        _sub.unsubscribe();
      });
    }
  }


  protected onRowList(row:PaymentGatewayVO) {
    console.debug('PaymentGatewaysComponent onRowList handler', row);
    this.viewMode = PaymentGatewaysComponent.PARAMS;
  }


  protected onRowListSelected() {
    if (this.selectedGateway != null) {
      this.onRowList(this.selectedGateway);
    }
  }

  protected onSaveHandler() {

    if (this.validForSave && this.changed && this.update) {


      console.debug('PaymentGatewaysComponent Save handler update', [this.shopCode, this.selectedGateway.label, this.update]);

      var _sub:any = this._paymentService.savePaymentGatewayParameters(this.shopCode, this.selectedGateway.label, this.update).subscribe(rez => {
          console.debug('PaymentGatewaysComponent attributes', rez);
          this.selectedGateway.parameters = rez;
          this.parameterValuesComponent.paymentGateway = this.selectedGateway;
          this.changed = false;
          this.validForSave = false;
          this.selectedParam = null;
          _sub.unsubscribe();
        }
      );

    }

  }

  protected onDiscardEventHandler() {
    console.debug('PaymentGatewaysComponent discard handler');
    this.onRefreshHandler();
  }


}
