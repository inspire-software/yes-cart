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
import {Component, OnInit, OnDestroy, ViewChild} from '@angular/core';
import {NgIf} from '@angular/common';
import {CustomerService, ShopEventBus} from './../shared/services/index';
import {UiUtil} from './../shared/ui/index';
import {TAB_DIRECTIVES} from 'ng2-bootstrap/ng2-bootstrap';
import {CustomersComponent, CustomerComponent} from './components/index';
import {DataControlComponent} from './../shared/sidebar/index';
import {ModalComponent, ModalResult, ModalAction} from './../shared/modal/index';
import {ShopVO, CustomerInfoVO, CustomerVO, AttrValueCustomerVO, Pair} from './../shared/model/index';
import {FormValidationEvent, Futures, Future} from './../shared/event/index';
import {Config} from './../shared/config/env.config';

@Component({
  selector: 'yc-all-customers',
  moduleId: module.id,
  templateUrl: 'all-customers.component.html',
  directives: [TAB_DIRECTIVES, NgIf, CustomersComponent, CustomerComponent, ModalComponent, DataControlComponent ],
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

  delayedFiltering:Future;
  delayedFilteringMs:number = Config.UI_INPUT_DELAY;
  filterCap:number = Config.UI_FILTER_CAP;
  filterNoCap:number = Config.UI_FILTER_NO_CAP;

  private selectedCustomer:CustomerInfoVO;

  private customerEdit:CustomerVO;
  private customerEditAttributes:AttrValueCustomerVO[] = [];
  private customerAttributesUpdate:Array<Pair<AttrValueCustomerVO, boolean>>;

  private shops:Array<ShopVO> = [];

  @ViewChild('deleteConfirmationModalDialog')
  deleteConfirmationModalDialog:ModalComponent;

  private deleteValue:String;
  private shopAllSub:any;

  constructor(private _customerService:CustomerService) {
    console.debug('AllCustomersComponent constructed');
    this.shopAllSub = ShopEventBus.getShopEventBus().shopsUpdated$.subscribe(shopsevt => {
      this.shops = shopsevt;
    });
  }

  changed:boolean = false;
  validForSave:boolean = false;

  newCustomerInstance():CustomerVO {
    return {
      customerId: 0, email: '',
      salutation: null, firstname: null, lastname: null, middlename: null,
      tag: null, customerType: 'B2C', pricingPolicy: null,
      attributes: [],
      customerShops: []
    };
  }

  ngOnInit() {
    console.debug('AllCustomersComponent ngOnInit');
    this.onRefreshHandler();
    let that = this;
    this.delayedFiltering = Futures.perpetual(function() {
      that.getFilteredCustomers();
    }, this.delayedFilteringMs);

  }

  ngOnDestroy() {
    console.debug('AllCustomersComponent ngOnDestroy');
    if (this.shopAllSub) {
      this.shopAllSub.unsubscribe();
    }
  }


  onFilterChange(event:any) {

    this.delayedFiltering.delay();

  }

  getFilteredCustomers() {
    this.customerFilterRequired = !this.forceShowAll && (this.customerFilter == null || this.customerFilter.length < 2);

    console.debug('AllCustomersComponent getFilteredCustomers' + (this.forceShowAll ? ' forcefully': ''));

    if (!this.customerFilterRequired) {
      let max = this.forceShowAll ? this.filterNoCap : this.filterCap;
      var _sub:any = this._customerService.getFilteredCustomer(this.customerFilter, max).subscribe( allcustomers => {
        console.debug('AllCustomersComponent getFilteredCustomers', allcustomers);
        this.customers = allcustomers;
        this.selectedCustomer = null;
        this.customerEdit = null;
        this.viewMode = AllCustomersComponent.CUSTOMERS;
        this.changed = false;
        this.validForSave = false;
        this.customerFilterCapped = this.customers.length >= max;
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

  protected onRefreshHandler() {
    console.debug('AllCustomersComponent refresh handler');
    this.getFilteredCustomers();
  }

  onCustomerSelected(data:CustomerInfoVO) {
    console.debug('AllCustomersComponent onCustomerSelected', data);
    this.selectedCustomer = data;
  }

  onCustomerChanged(event:FormValidationEvent<Pair<CustomerVO, Array<Pair<AttrValueCustomerVO, boolean>>>>) {
    console.debug('AllCustomersComponent onCustomerChanged', event);
    this.changed = true;
    this.validForSave = event.valid;
    this.customerEdit = event.source.first;
    this.customerAttributesUpdate = event.source.second;
  }

  onCustomerReset(event:Pair<CustomerVO, ShopVO>) {
    console.debug('AllCustomersComponent onCustomerReset', event);
    var _sub:any = this._customerService.resetPassword(event.first, event.second.shopId).subscribe( reset => {
      console.debug('AllCustomersComponent reset successful');
      _sub.unsubscribe();
    });
  }

  protected onSearchNumber() {
    this.searchHelpShow = false;
    this.customerFilter = '#ref';
  }

  protected onSearchCustomer() {
    this.searchHelpShow = false;
    this.customerFilter = '?keyword';
  }

  protected onSearchAddress() {
    this.searchHelpShow = false;
    this.customerFilter = '@keyword';
  }

  protected onSearchDate() {
    this.searchHelpShow = false;
    this.customerFilter = UiUtil.exampleDateSearch();
    this.getFilteredCustomers();
  }

  protected onSearchPolicy() {
    this.searchHelpShow = false;
    this.customerFilter = '$keyword';
  }

  protected onSearchHelpToggle() {
    this.searchHelpShow = !this.searchHelpShow;
  }

  protected onForceShowAll() {
    this.forceShowAll = !this.forceShowAll;
    this.getFilteredCustomers();
  }

  protected onBackToList() {
    console.debug('AllCustomersComponent onBackToList handler');
    if (this.viewMode === AllCustomersComponent.CUSTOMER) {
      this.customerEdit = null;
      this.viewMode = AllCustomersComponent.CUSTOMERS;
    }
  }

  protected onRowNew() {
    console.debug('AllCustomersComponent onRowNew handler');
    this.changed = false;
    this.validForSave = false;
    if (this.viewMode === AllCustomersComponent.CUSTOMERS) {
      this.customerEdit = this.newCustomerInstance();
      this.customerEditAttributes = [];
      this.viewMode = AllCustomersComponent.CUSTOMER;
    }
  }

  protected onRowDelete(row:any) {
    console.debug('AllCustomersComponent onRowDelete handler', row);
    this.deleteValue = row.customerId + ' ' + row.email;
    this.deleteConfirmationModalDialog.show();
  }

  protected onRowDeleteSelected() {
    if (this.selectedCustomer != null) {
      this.onRowDelete(this.selectedCustomer);
    }
  }


  protected onRowEditCustomer(row:CustomerInfoVO) {
    console.debug('AllCustomersComponent onRowEditCustomer handler', row);
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

        console.debug('AllCustomersComponent Save handler customer', this.customerEdit);

        var _sub:any = this._customerService.saveCustomer(this.customerEdit).subscribe(
            rez => {
              _sub.unsubscribe();
              let pk = this.customerEdit.customerId;
              console.debug('AllCustomersComponent customer changed', rez);
              this.customerFilter = '#' + rez.email;
              this.changed = false;
              this.selectedCustomer = rez;
              this.customerEdit = null;
              this.viewMode = AllCustomersComponent.CUSTOMERS;

              if (pk > 0 && this.customerAttributesUpdate != null && this.customerAttributesUpdate.length > 0) {

                var _sub2:any = this._customerService.saveCustomerAttributes(this.customerAttributesUpdate).subscribe(rez => {
                  _sub2.unsubscribe();
                  console.debug('AllCustomersComponent customer attributes updated', rez);
                  this.customerAttributesUpdate = null;
                  this.getFilteredCustomers();
                });
              } else {
                this.getFilteredCustomers();
              }
          }
        );
      }

    }

  }

  protected onDiscardEventHandler() {
    console.debug('AllCustomersComponent discard handler');
    if (this.viewMode === AllCustomersComponent.CUSTOMER) {
      if (this.selectedCustomer != null) {
        this.onRowEditSelected();
      } else {
        this.onRowNew();
      }
    }
  }

  protected onDeleteConfirmationResult(modalresult: ModalResult) {
    console.debug('AllCustomersComponent onDeleteConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {

      if (this.selectedCustomer != null) {
        console.debug('AllCustomersComponent onDeleteConfirmationResult', this.selectedCustomer);

        var _sub:any = this._customerService.removeCustomer(this.selectedCustomer).subscribe(res => {
          _sub.unsubscribe();
          console.debug('AllCustomersComponent removeCustomer', this.selectedCustomer);
          this.selectedCustomer = null;
          this.customerEdit = null;
          this.getFilteredCustomers();
        });
      }
    }
  }

}
