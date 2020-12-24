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
import { Component, OnInit, Input, OnDestroy, ViewChild } from '@angular/core';
import { PaymentService, I18nEventBus, UserEventBus } from './../../shared/services/index';
import { ParameterValuesComponent } from './components/index';
import { ModalComponent, ModalResult, ModalAction } from './../../shared/modal/index';
import { PaymentGatewayVO, PaymentGatewayParameterVO, Pair } from './../../shared/model/index';
import { LogUtil } from './../../shared/log/index';

@Component({
  selector: 'cw-payment-gateways',
  templateUrl: 'payment-gateways.component.html',
})

export class PaymentGatewaysComponent implements OnInit, OnDestroy {

  private static PGS:string = 'pgs';
  private static PARAMS:string = 'params';

  @Input() system:boolean = true;

  private _shopCode:string;

  public viewMode:string = PaymentGatewaysComponent.PGS;

  public gateways:Array<PaymentGatewayVO> = [];
  public gatewayFilter:string;
  public gatewaySort:Pair<string, boolean> = { first: 'name', second: false };

  public selectedGateway:PaymentGatewayVO;

  @ViewChild('featuresModalDialog')
  private featuresModalDialog:ModalComponent;

  @ViewChild('parameterValuesComponent')
  private parameterValuesComponent:ParameterValuesComponent;

  public paramFilter:string;
  public paramSort:Pair<string, boolean> = { first: 'name', second: false };

  public selectedParam:PaymentGatewayParameterVO;

  private update:Array<Pair<PaymentGatewayParameterVO, boolean>>;

  @ViewChild('disableConfirmationModalDialog')
  private disableConfirmationModalDialog:ModalComponent;

  public offValue:String;

  public changed:boolean = false;
  public validForSave:boolean = false;

  public searchHelpShow:boolean = false;

  public loading:boolean = false;

  public includeSecure:boolean = false;
  public changeIncludeSecure:boolean = false;

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

  onIncludeSecure() {
    this.changeIncludeSecure = !this.includeSecure;
    this.getAllPgs();
  }

  onPageSelectedGateway(page:number) {
    LogUtil.debug('OrganisationRoleComponent onPageSelectedGateway', page);
  }

  onSortSelectedGateway(sort:Pair<string, boolean>) {
    LogUtil.debug('OrganisationRoleComponent ononSortSelectedGateway', sort);
    if (sort == null) {
      this.gatewaySort = { first: 'name', second: false };
    } else {
      this.gatewaySort = sort;
    }
  }

  onGatewaySelected(data:PaymentGatewayVO) {
    LogUtil.debug('PaymentGatewaysComponent onGatewaySelected', data);
    this.selectedGateway = data;
    this.paramFilter = '';
  }

  onPageSelectedParam(page:number) {
    LogUtil.debug('OrganisationRoleComponent onPageSelectedParam', page);
  }

  onSortSelectedParam(sort:Pair<string, boolean>) {
    LogUtil.debug('OrganisationRoleComponent ononSortSelectedParam', sort);
    if (sort == null) {
      this.paramSort = { first: 'name', second: false };
    } else {
      this.paramSort = sort;
    }
  }

  onParamSelected(data:PaymentGatewayParameterVO) {
    LogUtil.debug('PaymentGatewaysComponent onParamSelected', data);
    this.selectedParam = data;
  }

  onParamChange(event:any) {
    LogUtil.debug('PaymentGatewaysComponent onParamChanged', event);
    this.validForSave = event.valid;
    this.update = event.source;
    this.changed = true;
    LogUtil.debug('PaymentGatewaysComponent data changed and ' + (this.validForSave ? 'is valid' : 'is NOT valid'), event);
  }

  onBackToList() {
    LogUtil.debug('PaymentGatewaysComponent onBackToList handler');
    if (this.viewMode === PaymentGatewaysComponent.PARAMS) {
      this.selectedParam = null;
      this.viewMode = PaymentGatewaysComponent.PGS;
    }
  }

  onRowNew() {
    LogUtil.debug('PaymentGatewaysComponent onRowNew handler');
    if (this.system) {
      this.parameterValuesComponent.onRowAdd();
    }
  }

  onRowDeleteSelected() {
    if (this.selectedParam != null) {
      this.parameterValuesComponent.onRowDeleteSelected();
    }
  }

  onRowEditSelected() {
    if (this.selectedParam != null) {
      this.parameterValuesComponent.onRowEditSelected();
    }
  }

  onRowInfoSelected() {
    if (this.selectedGateway != null) {
      this.featuresModalDialog.show();
    }
  }

  onRowEnableSelected() {
    if (this.selectedGateway != null) {

      this.offValue = this.selectedGateway.name;
      this.disableConfirmationModalDialog.show();

    }
  }


  onDisableConfirmationResult(modalresult: ModalResult) {
    LogUtil.debug('PaymentGatewaysComponent onDisableConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {

      this.loading = true;
      this._paymentService.updateDisabledFlag(this.shopCode, this.selectedGateway.label, this.selectedGateway.active).subscribe( done => {
        LogUtil.debug('PaymentGatewaysComponent updateDisabledFlag', done);
        this.loading = false;
        if (this.system || this.selectedGateway.active) {
          this.selectedGateway.active = !this.selectedGateway.active;
          this.changed = false;
          this.validForSave = false;
        } else { // If we are enabling for shop we copy missing attributes, so need full refresh
          this.onRefreshHandler();
        }
      });
    }
  }



  onRowList(row:PaymentGatewayVO) {
    LogUtil.debug('PaymentGatewaysComponent onRowList handler', row);
    this.viewMode = PaymentGatewaysComponent.PARAMS;
  }


  onRefreshHandler() {
    LogUtil.debug('PaymentGatewaysComponent refresh handler');
    this.changeIncludeSecure = this.includeSecure;
    if (UserEventBus.getUserEventBus().current() != null) {
      this.getAllPgs();
    }
  }

  onRowListSelected() {
    if (this.selectedGateway != null) {
      this.onRowList(this.selectedGateway);
    }
  }

  onSaveHandler() {

    if (this.validForSave && this.changed && this.update) {


      LogUtil.debug('PaymentGatewaysComponent Save handler update', [this.shopCode, this.selectedGateway.label, this.update]);

      this._paymentService.savePaymentGatewayParameters(this.shopCode, this.selectedGateway.label, this.update, this.includeSecure).subscribe(rez => {
          LogUtil.debug('PaymentGatewaysComponent attributes', rez);
          this.selectedGateway.parameters = rez;
          this.parameterValuesComponent.paymentGateway = this.selectedGateway;
          this.changed = false;
          this.validForSave = false;
          this.selectedParam = null;
        }
      );
    }
  }

  onDiscardEventHandler() {
    LogUtil.debug('PaymentGatewaysComponent discard handler');
    this.onRefreshHandler();
  }


  onClearFilterPg() {

    this.gatewayFilter = '';

  }

  onClearFilterParam() {

    this.paramFilter = '';

  }

  onSearchHelpToggle() {
    this.searchHelpShow = !this.searchHelpShow;
  }

  onSearchValues() {
    this.searchHelpShow = false;
    this.paramFilter = '###';
  }

  onSearchValuesNew() {
    this.searchHelpShow = false;
    this.paramFilter = '##0';
  }

  onSearchValuesNewOnly() {
    this.searchHelpShow = false;
    this.paramFilter = '#00';
  }

  onSearchValuesChanges() {
    this.searchHelpShow = false;
    this.paramFilter = '#0#';
  }


  private getAllPgs() {

    if (this.system || (!this.system && this.shopCode)) {

      let lang = I18nEventBus.getI18nEventBus().current();

      this.loading = true;
      this._paymentService.getPaymentGatewaysWithParameters(lang, this.shopCode, this.changeIncludeSecure).subscribe( allgateways => {
        LogUtil.debug('PaymentGatewaysComponent getPaymentGatewaysWithParameters', allgateways);
        this.viewMode = PaymentGatewaysComponent.PGS;
        this.gateways = allgateways;
        this.selectedGateway = null;
        this.changed = false;
        this.validForSave = false;
        this.loading = false;
        this.includeSecure = this.changeIncludeSecure; // change only if we get successful result
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
