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
import { Component, OnInit, OnDestroy, ViewChild } from '@angular/core';
import { CustomerService, ShopEventBus } from './../shared/services/index';
import { ModalComponent, ModalResult, ModalAction } from './../shared/modal/index';
import { ShopVO, CustomerInfoVO, CustomerVO, AttrValueCustomerVO, Pair } from './../shared/model/index';
import { FormValidationEvent, Futures, Future } from './../shared/event/index';
import { Config } from './../shared/config/env.config';
import { UiUtil } from './../shared/ui/index';
import { LogUtil } from './../shared/log/index';

@Component({
  selector: 'yc-all-customers',
  moduleId: module.id,
  templateUrl: 'all-customers.component.html',
})

export class AllCustomersComponent implements OnInit, OnDestroy {

  private static CUSTOMERS:string = 'customers';
  private static CUSTOMER:string = 'customer';

  private searchHelpShow:boolean = false;
  private forceShowAll:boolean = false;
  private viewMode:string = AllCustomersComponent.CUSTOMERS;

  private customers:Array<CustomerInfoVO> = [];
  private customerFilter:string;
  private customerFilterRequired:boolean = true;
  private customerFilterCapped:boolean = false;

  private delayedFiltering:Future;
  private delayedFilteringMs:number = Config.UI_INPUT_DELAY;
  private filterCap:number = Config.UI_FILTER_CAP;
  private filterNoCap:number = Config.UI_FILTER_NO_CAP;

  private selectedCustomer:CustomerInfoVO;

  private customerEdit:CustomerVO;
  private customerEditAttributes:AttrValueCustomerVO[] = [];
  private customerAttributesUpdate:Array<Pair<AttrValueCustomerVO, boolean>>;

  private shops:Array<ShopVO> = [];

  @ViewChild('deleteConfirmationModalDialog')
  private deleteConfirmationModalDialog:ModalComponent;

  private deleteValue:String;
  private shopAllSub:any;

  private loading:boolean = false;

  private changed:boolean = false;
  private validForSave:boolean = false;

  constructor(private _customerService:CustomerService) {
    LogUtil.debug('AllCustomersComponent constructed');
    this.shopAllSub = ShopEventBus.getShopEventBus().shopsUpdated$.subscribe(shopsevt => {
      this.shops = shopsevt;
    });
  }

  newCustomerInstance():CustomerVO {
    return {
      customerId: 0, email: '',
      salutation: null, firstname: null, lastname: null, middlename: null,
      tag: null, customerType: 'B2C', pricingPolicy: null,
      checkoutBocked: false, checkoutBockedForOrdersOver: undefined,
      ordersRequireApproval: false, ordersRequireApprovalForOrdersOver: undefined,
      attributes: [],
      customerShops: []
    };
  }

  ngOnInit() {
    LogUtil.debug('AllCustomersComponent ngOnInit');
    this.onRefreshHandler();
    let that = this;
    this.delayedFiltering = Futures.perpetual(function() {
      that.getFilteredCustomers();
    }, this.delayedFilteringMs);
  }

  ngOnDestroy() {
    LogUtil.debug('AllCustomersComponent ngOnDestroy');
    if (this.shopAllSub) {
      this.shopAllSub.unsubscribe();
    }
  }


  protected onFilterChange(event:any) {

    this.delayedFiltering.delay();

  }

  protected onRefreshHandler() {
    LogUtil.debug('AllCustomersComponent refresh handler');
    this.getFilteredCustomers();
  }

  protected onCustomerSelected(data:CustomerInfoVO) {
    LogUtil.debug('AllCustomersComponent onCustomerSelected', data);
    this.selectedCustomer = data;
  }

  protected onCustomerChanged(event:FormValidationEvent<Pair<CustomerVO, Array<Pair<AttrValueCustomerVO, boolean>>>>) {
    LogUtil.debug('AllCustomersComponent onCustomerChanged', event);
    this.changed = true;
    this.validForSave = event.valid;
    this.customerEdit = event.source.first;
    this.customerAttributesUpdate = event.source.second;
  }

  protected onCustomerReset(event:Pair<CustomerVO, ShopVO>) {
    LogUtil.debug('AllCustomersComponent onCustomerReset', event);
    var _sub:any = this._customerService.resetPassword(event.first, event.second.shopId).subscribe( reset => {
      LogUtil.debug('AllCustomersComponent reset successful');
      _sub.unsubscribe();
    });
  }

  protected onSearchNumber() {
    this.searchHelpShow = false;
    this.customerFilter = '#';
  }

  protected onSearchCustomer() {
    this.searchHelpShow = false;
    this.customerFilter = '?';
  }

  protected onSearchAddress() {
    this.searchHelpShow = false;
    this.customerFilter = '@';
  }

  protected onSearchDate() {
    this.searchHelpShow = false;
    this.customerFilter = UiUtil.exampleDateSearch();
    this.getFilteredCustomers();
  }

  protected onSearchPolicy() {
    this.searchHelpShow = false;
    this.customerFilter = '$';
  }

  protected onSearchHelpToggle() {
    this.searchHelpShow = !this.searchHelpShow;
  }

  protected onForceShowAll() {
    this.forceShowAll = !this.forceShowAll;
    this.getFilteredCustomers();
  }

  protected onBackToList() {
    LogUtil.debug('AllCustomersComponent onBackToList handler');
    if (this.viewMode === AllCustomersComponent.CUSTOMER) {
      this.customerEdit = null;
      this.viewMode = AllCustomersComponent.CUSTOMERS;
    }
  }

  protected onRowNew() {
    LogUtil.debug('AllCustomersComponent onRowNew handler');
    this.changed = false;
    this.validForSave = false;
    if (this.viewMode === AllCustomersComponent.CUSTOMERS) {
      this.customerEdit = this.newCustomerInstance();
      this.customerEditAttributes = [];
      this.viewMode = AllCustomersComponent.CUSTOMER;
    }
  }

  protected onRowDelete(row:any) {
    LogUtil.debug('AllCustomersComponent onRowDelete handler', row);
    this.deleteValue = row.customerId + ' ' + row.email;
    this.deleteConfirmationModalDialog.show();
  }

  protected onRowDeleteSelected() {
    if (this.selectedCustomer != null) {
      this.onRowDelete(this.selectedCustomer);
    }
  }


  protected onRowEditCustomer(row:CustomerInfoVO) {
    LogUtil.debug('AllCustomersComponent onRowEditCustomer handler', row);
    var _sub:any = this._customerService.getCustomerById(this.selectedCustomer.customerId).subscribe(customer => {
      this.customerEdit = customer;
      this.customerEditAttributes = customer.attributes;
      this.changed = false;
      this.validForSave = false;
      this.viewMode = AllCustomersComponent.CUSTOMER;
      _sub.unsubscribe();
    });
  }

  protected onRowEditSelected() {
    if (this.selectedCustomer != null) {
      this.onRowEditCustomer(this.selectedCustomer);
    }
  }

  protected onSaveHandler() {

    if (this.validForSave && this.changed) {

      if (this.customerEdit != null) {

        LogUtil.debug('AllCustomersComponent Save handler customer', this.customerEdit);

        var _sub:any = this._customerService.saveCustomer(this.customerEdit).subscribe(
            rez => {
              _sub.unsubscribe();
              let pk = this.customerEdit.customerId;
              LogUtil.debug('AllCustomersComponent customer changed', rez);
              this.changed = false;
              this.selectedCustomer = rez;
              this.customerEdit = null;
              this.viewMode = AllCustomersComponent.CUSTOMERS;

              if (pk > 0 && this.customerAttributesUpdate != null && this.customerAttributesUpdate.length > 0) {

                var _sub2:any = this._customerService.saveCustomerAttributes(this.customerAttributesUpdate).subscribe(rez => {
                  _sub2.unsubscribe();
                  LogUtil.debug('AllCustomersComponent customer attributes updated', rez);
                  this.customerAttributesUpdate = null;
                  this.getFilteredCustomers();
                });
              } else {
                this.customerFilter = '#' + rez.email;
                this.getFilteredCustomers();
              }
          }
        );
      }

    }

  }

  protected onDiscardEventHandler() {
    LogUtil.debug('AllCustomersComponent discard handler');
    if (this.viewMode === AllCustomersComponent.CUSTOMER) {
      if (this.selectedCustomer != null) {
        this.onRowEditSelected();
      } else {
        this.onRowNew();
      }
    }
  }

  protected onDeleteConfirmationResult(modalresult: ModalResult) {
    LogUtil.debug('AllCustomersComponent onDeleteConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {

      if (this.selectedCustomer != null) {
        LogUtil.debug('AllCustomersComponent onDeleteConfirmationResult', this.selectedCustomer);

        var _sub:any = this._customerService.removeCustomer(this.selectedCustomer).subscribe(res => {
          _sub.unsubscribe();
          LogUtil.debug('AllCustomersComponent removeCustomer', this.selectedCustomer);
          this.selectedCustomer = null;
          this.customerEdit = null;
          this.getFilteredCustomers();
        });
      }
    }
  }

  protected onClearFilter() {

    this.customerFilter = '';
    this.getFilteredCustomers();

  }

  private getFilteredCustomers() {
    this.customerFilterRequired = !this.forceShowAll && (this.customerFilter == null || this.customerFilter.length < 2);

    LogUtil.debug('AllCustomersComponent getFilteredCustomers' + (this.forceShowAll ? ' forcefully': ''));

    if (!this.customerFilterRequired) {
      this.loading = true;
      let max = this.forceShowAll ? this.filterNoCap : this.filterCap;
      var _sub:any = this._customerService.getFilteredCustomer(this.customerFilter, max).subscribe( allcustomers => {
        LogUtil.debug('AllCustomersComponent getFilteredCustomers', allcustomers);
        this.customers = allcustomers;
        this.selectedCustomer = null;
        this.customerEdit = null;
        this.viewMode = AllCustomersComponent.CUSTOMERS;
        this.changed = false;
        this.validForSave = false;
        this.customerFilterCapped = this.customers.length >= max;
        this.loading = false;
        _sub.unsubscribe();
      });
    } else {
      this.customers = [];
      this.selectedCustomer = null;
      this.customerEdit = null;
      this.customerEditAttributes = null;
      this.viewMode = AllCustomersComponent.CUSTOMERS;
      this.changed = false;
      this.validForSave = false;
      this.customerFilterCapped = false;
    }
  }

}
