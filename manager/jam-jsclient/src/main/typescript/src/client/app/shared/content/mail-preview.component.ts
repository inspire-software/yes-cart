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
import { Component, OnInit, OnDestroy, Output, EventEmitter, ViewChild } from '@angular/core';
import { CustomerOrderInfoVO } from './../model/index';
import { CustomerOrderService, I18nEventBus } from './../services/index';
import { ModalComponent, ModalResult, ModalAction } from './../modal/index';
import { Futures, Future, FormValidationEvent } from './../event/index';
import { Config } from './../config/env.config';
import { LogUtil } from './../log/index';

@Component({
  selector: 'yc-mail-preview',
  moduleId: module.id,
  templateUrl: 'mail-preview.component.html',
})

export class MailPreviewComponent implements OnInit, OnDestroy {

  @Output() dataSelected: EventEmitter<FormValidationEvent<CustomerOrderInfoVO>> = new EventEmitter<FormValidationEvent<CustomerOrderInfoVO>>();

  private changed:boolean = false;

  @ViewChild('customerorderModalDialog')
  private customerorderModalDialog:ModalComponent;

  private filteredCustomerOrders : CustomerOrderInfoVO[] = [];
  private customerorderFilter : string;

  private selectedShop : number = 0;
  private selectedTemplate : string = null;
  private selectedCustomerOrder : CustomerOrderInfoVO = null;

  private delayedFiltering:Future;
  private delayedFilteringMs:number = Config.UI_INPUT_DELAY;
  private filterCap:number = Config.UI_FILTER_CAP;

  private customerorderFilterRequired:boolean = true;
  private customerorderFilterCapped:boolean = false;

  private loading:boolean = false;

  constructor (private _customerorderService : CustomerOrderService) {
    LogUtil.debug('MailPreviewComponent constructed');
  }

  ngOnDestroy() {
    LogUtil.debug('MailPreviewComponent ngOnDestroy');
  }

  ngOnInit() {
    LogUtil.debug('MailPreviewComponent ngOnInit');
    let that = this;
    this.delayedFiltering = Futures.perpetual(function() {
      that.getAllCustomerOrders();
    }, this.delayedFilteringMs);

  }

  onSelectClick(customerorder: CustomerOrderInfoVO) {
    LogUtil.debug('MailPreviewComponent onSelectClick', customerorder);
    this.selectedCustomerOrder = customerorder;
    window.open(Config.CONTEXT_PATH + '/service/content/mail/' + this.selectedShop + '/' + this.selectedTemplate + '?order=' + customerorder.ordernum, 'PREVIEW', 'width=800,height=500');
  }

  protected onFilterChange() {

    this.delayedFiltering.delay();

  }

  protected onClearFilter() {
    this.customerorderFilter = '';
    this.delayedFiltering.delay();
  }

  public showDialog(shop:number, template:string) {
    this.selectedShop = shop;
    this.selectedTemplate = template;
    LogUtil.debug('MailPreviewComponent showDialog', this.selectedShop, this.selectedTemplate);
    this.customerorderModalDialog.show();
  }


  protected onSelectConfirmationResult(modalresult: ModalResult) {
    LogUtil.debug('ProductTypeSelectComponent onSelectConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {
      this.dataSelected.emit({ source: this.selectedCustomerOrder, valid: true });
      this.selectedCustomerOrder = null;
    }
  }

  private getAllCustomerOrders() {

    this.customerorderFilterRequired = (this.customerorderFilter == null || this.customerorderFilter.length < 2);

    if (!this.customerorderFilterRequired) {
      this.loading = true;
      let lang = I18nEventBus.getI18nEventBus().current();
      let _sub:any = this._customerorderService.getFilteredOrders(lang, this.customerorderFilter, [],this.filterCap).subscribe(allcustomerorders => {
        LogUtil.debug('MailPreviewComponent getAllCustomerOrders', allcustomerorders);
        this.selectedCustomerOrder = null;
        this.changed = false;
        this.filteredCustomerOrders = allcustomerorders;
        this.customerorderFilterCapped = this.filteredCustomerOrders.length >= this.filterCap;
        this.loading = false;
        _sub.unsubscribe();
      });
    }
  }

}
