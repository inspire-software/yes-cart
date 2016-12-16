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
import { Component, OnInit, Input, OnDestroy, ViewChild } from '@angular/core';
import { PaymentService, I18nEventBus } from './../../shared/services/index';
import { ParameterValuesComponent } from './components/index';
import { ModalComponent, ModalResult, ModalAction } from './../../shared/modal/index';
import { PaymentGatewayVO, PaymentGatewayParameterVO, Pair } from './../../shared/model/index';
import { LogUtil } from './../../shared/log/index';

@Component({
  selector: 'yc-payment-gateways',
  moduleId: module.id,
  templateUrl: 'payment-gateways.component.html',
})

export class PaymentGatewaysComponent implements OnInit, OnDestroy {

  private static PGS:string = 'pgs';
  private static PARAMS:string = 'params';

  @Input() system:boolean = true;

  private _shopCode:string;

  private viewMode:string = PaymentGatewaysComponent.PGS;

  private gateways:Array<PaymentGatewayVO> = [];
  private gatewayFilter:string;

  private selectedGateway:PaymentGatewayVO;

  @ViewChild('featuresModalDialog')
  private featuresModalDialog:ModalComponent;

  @ViewChild('parameterValuesComponent')
  private parameterValuesComponent:ParameterValuesComponent;

  private paramFilter:string;

  private selectedParam:PaymentGatewayParameterVO;

  private update:Array<Pair<PaymentGatewayParameterVO, boolean>>;

  @ViewChild('disableConfirmationModalDialog')
  private disableConfirmationModalDialog:ModalComponent;

  private offValue:String;

  private changed:boolean = false;
  private validForSave:boolean = false;

  private searchHelpShow:boolean = false;

  private loading:boolean = false;

  constructor(private _paymentService:PaymentService) {
    LogUtil.debug('PaymentGatewaysComponent constructed');

    this.update = [];

  }

  @Input()
  set shopCode(shopCode:string) {
    this._shopCode = shopCode;
    this.onRefreshHandler();
  }

  get shopCode():string {
    return this._shopCode;
  }

  ngOnInit() {
    LogUtil.debug('PaymentGatewaysComponent ngOnInit');
    this.onRefreshHandler();
  }

  ngOnDestroy() {
    LogUtil.debug('PaymentGatewaysComponent ngOnDestroy');
  }


  protected onRefreshHandler() {
    LogUtil.debug('PaymentGatewaysComponent refresh handler');
    this.getAllPgs();
  }

  protected onGatewaySelected(data:PaymentGatewayVO) {
    LogUtil.debug('PaymentGatewaysComponent onGatewaySelected', data);
    this.selectedGateway = data;
    this.paramFilter = '';
  }

  protected onParamSelected(data:PaymentGatewayParameterVO) {
    LogUtil.debug('PaymentGatewaysComponent onParamSelected', data);
    this.selectedParam = data;
  }

  protected onParamChange(event:any) {
    LogUtil.debug('PaymentGatewaysComponent onParamChanged', event);
    this.validForSave = event.valid;
    this.update = event.source;
    this.changed = true;
    LogUtil.debug('PaymentGatewaysComponent data changed and ' + (this.validForSave ? 'is valid' : 'is NOT valid'), event);
  }

  protected onBackToList() {
    LogUtil.debug('PaymentGatewaysComponent onBackToList handler');
    if (this.viewMode === PaymentGatewaysComponent.PARAMS) {
      this.selectedParam = null;
      this.viewMode = PaymentGatewaysComponent.PGS;
    }
  }

  protected onRowNew() {
    LogUtil.debug('PaymentGatewaysComponent onRowNew handler');
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

      this.offValue = this.selectedGateway.name;
      this.disableConfirmationModalDialog.show();

    }
  }


  protected onDisableConfirmationResult(modalresult: ModalResult) {
    LogUtil.debug('PaymentGatewaysComponent onDisableConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {

      var _sub:any = this._paymentService.updateDisabledFlag(this.shopCode, this.selectedGateway.label, this.selectedGateway.active).subscribe( done => {
        LogUtil.debug('PaymentGatewaysComponent updateDisabledFlag', done);
        if (this.system || this.selectedGateway.active) {
          this.selectedGateway.active = !this.selectedGateway.active;
          this.changed = false;
          this.validForSave = false;
        } else { // If we are enabling for shop we copy missing attributes, so need full refresh
          this.onRefreshHandler();
        }
        _sub.unsubscribe();
      });
    }
  }



  protected onRowList(row:PaymentGatewayVO) {
    LogUtil.debug('PaymentGatewaysComponent onRowList handler', row);
    this.viewMode = PaymentGatewaysComponent.PARAMS;
  }


  protected onRowListSelected() {
    if (this.selectedGateway != null) {
      this.onRowList(this.selectedGateway);
    }
  }

  protected onSaveHandler() {

    if (this.validForSave && this.changed && this.update) {


      LogUtil.debug('PaymentGatewaysComponent Save handler update', [this.shopCode, this.selectedGateway.label, this.update]);

      var _sub:any = this._paymentService.savePaymentGatewayParameters(this.shopCode, this.selectedGateway.label, this.update).subscribe(rez => {
          LogUtil.debug('PaymentGatewaysComponent attributes', rez);
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
    LogUtil.debug('PaymentGatewaysComponent discard handler');
    this.onRefreshHandler();
  }


  protected onClearFilterPg() {

    this.gatewayFilter = '';

  }

  protected onClearFilterParam() {

    this.paramFilter = '';

  }

  protected onSearchHelpToggle() {
    this.searchHelpShow = !this.searchHelpShow;
  }

  protected onSearchValues() {
    this.searchHelpShow = false;
    this.paramFilter = '###';
  }

  protected onSearchValuesNew() {
    this.searchHelpShow = false;
    this.paramFilter = '##0';
  }

  protected onSearchValuesNewOnly() {
    this.searchHelpShow = false;
    this.paramFilter = '#00';
  }

  protected onSearchValuesChanges() {
    this.searchHelpShow = false;
    this.paramFilter = '#0#';
  }


  private getAllPgs() {

    if (this.system || (!this.system && this.shopCode)) {

      let lang = I18nEventBus.getI18nEventBus().current();

      this.loading = true;
      var _sub:any = this._paymentService.getPaymentGatewaysWithParameters(lang, this.shopCode).subscribe( allgateways => {
        LogUtil.debug('PaymentGatewaysComponent getAllCountries', allgateways);
        this.gateways = allgateways;
        this.selectedGateway = null;
        this.viewMode = PaymentGatewaysComponent.PGS;
        this.changed = false;
        this.validForSave = false;
        this.loading = false;
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

}
