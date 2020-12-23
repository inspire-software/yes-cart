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
import { Component, OnInit, OnDestroy, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CustomerService, ShopEventBus, UserEventBus } from './../shared/services/index';
import { ModalComponent, ModalResult, ModalAction } from './../shared/modal/index';
import { ShopVO, CustomerInfoVO, CustomerVO, AttrValueCustomerVO, Pair, SearchResultVO } from './../shared/model/index';
import { FormValidationEvent, Futures, Future } from './../shared/event/index';
import { Config } from './../../environments/environment';
import { UiUtil } from './../shared/ui/index';
import { LogUtil } from './../shared/log/index';

@Component({
  selector: 'cw-all-customers',
  templateUrl: 'all-customers.component.html',
})

export class AllCustomersComponent implements OnInit, OnDestroy {

  private static CUSTOMERS:string = 'customers';
  private static CUSTOMER:string = 'customer';

  public searchHelpShow:boolean = false;
  public forceShowAll:boolean = false;
  public viewMode:string = AllCustomersComponent.CUSTOMERS;

  public customers:SearchResultVO<CustomerInfoVO>;
  public customerFilter:string;
  public customerFilterRequired:boolean = true;

  private delayedFiltering:Future;
  private delayedFilteringMs:number = Config.UI_INPUT_DELAY;

  public selectedCustomer:CustomerInfoVO;

  public customerEdit:CustomerVO;
  public customerEditAttributes:AttrValueCustomerVO[] = [];
  private customerAttributesUpdate:Array<Pair<AttrValueCustomerVO, boolean>>;

  public shops:Array<ShopVO> = [];

  @ViewChild('deleteConfirmationModalDialog')
  private deleteConfirmationModalDialog:ModalComponent;

  public deleteValue:String;
  private shopAllSub:any;

  public loading:boolean = false;

  public changed:boolean = false;
  public validForSave:boolean = false;

  private openFirstResultOnSearch:boolean = false;

  constructor(private _customerService:CustomerService,
              private _route: ActivatedRoute) {
    LogUtil.debug('AllCustomersComponent constructed');
    this.customers = this.newSearchResultInstance();
    this.shopAllSub = ShopEventBus.getShopEventBus().shopsUpdated$.subscribe(shopsevt => {
      this.shops = shopsevt;
    });
  }

  newSearchResultInstance():SearchResultVO<CustomerInfoVO> {
    return {
      searchContext: {
        parameters: {
          filter: []
        },
        start: 0,
        size: Config.UI_TABLE_PAGE_SIZE,
        sortBy: null,
        sortDesc: false
      },
      items: [],
      total: 0
    };
  }

  newCustomerInstance():CustomerVO {
    return {
      customerId: 0, login: null, email: null, phone: null, guest: false, shop: false,
      salutation: null, firstname: null, lastname: null, middlename: null,
      tag: null, customerType: 'B2C', pricingPolicy: null,
      companyName1: null, companyName2: null, companyDepartment: null,
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
    this._route.params.subscribe(params => {
      LogUtil.debug('AllCustomersComponent params', params);
      let customerId = params['customerId'];
      if (customerId != null) {
        LogUtil.debug('AllCustomersComponent customerId from params is', customerId);
        this.customerFilter = '#' + customerId;
        this.openFirstResultOnSearch = true;
        this.getFilteredCustomers();
      }
    });
    this._route.queryParams.subscribe(params => {
      LogUtil.debug('AllCustomersComponent queryParams', params);
      let filter = params['filter'];
      if (filter != null) {
        LogUtil.debug('AllCustomersComponent filter from queryParams is', filter);
        if (filter == 'regthismth') {

          let now = new Date();
          let yearStart = now.getFullYear();
          let mthStart = now.getMonth() + 1;

          this.customerFilter = yearStart + '-' + (mthStart < 10 ? '0'+mthStart : mthStart)  + '<';
          this.getFilteredCustomers();
        }
      }
    });
  }

  ngOnDestroy() {
    LogUtil.debug('AllCustomersComponent ngOnDestroy');
    if (this.shopAllSub) {
      this.shopAllSub.unsubscribe();
    }
  }


  onFilterChange(event:any) {
    this.customers.searchContext.start = 0; // changing filter means we need to start from first page
    this.delayedFiltering.delay();
  }

  onRefreshHandler() {
    LogUtil.debug('AllCustomersComponent refresh handler');
    if (UserEventBus.getUserEventBus().current() != null) {
      this.getFilteredCustomers();
    }
  }

  onPageSelected(page:number) {
    LogUtil.debug('AllCustomersComponent onPageSelected', page);
    this.customers.searchContext.start = page;
    this.delayedFiltering.delay();
  }

  onSortSelected(sort:Pair<string, boolean>) {
    LogUtil.debug('AllCustomersComponent ononSortSelected', sort);
    if (sort == null) {
      this.customers.searchContext.sortBy = null;
      this.customers.searchContext.sortDesc = false;
    } else {
      this.customers.searchContext.sortBy = sort.first;
      this.customers.searchContext.sortDesc = sort.second;
    }
    this.delayedFiltering.delay();
  }

  onCustomerSelected(data:CustomerInfoVO) {
    LogUtil.debug('AllCustomersComponent onCustomerSelected', data);
    this.selectedCustomer = data;
  }

  onCustomerChanged(event:FormValidationEvent<Pair<CustomerVO, Array<Pair<AttrValueCustomerVO, boolean>>>>) {
    LogUtil.debug('AllCustomersComponent onCustomerChanged', event);
    this.changed = true;
    this.validForSave = event.valid;
    this.customerEdit = event.source.first;
    this.customerAttributesUpdate = event.source.second;
  }

  onCustomerReset(event:Pair<CustomerVO, ShopVO>) {
    LogUtil.debug('AllCustomersComponent onCustomerReset', event);
    this._customerService.resetPassword(event.first, event.second.shopId).subscribe( reset => {
      LogUtil.debug('AllCustomersComponent reset successful', reset);
    });
  }

  onSearchNumber() {
    this.searchHelpShow = false;
    this.customerFilter = '#';
  }

  onSearchCustomer() {
    this.searchHelpShow = false;
    this.customerFilter = '?';
  }

  onSearchAddress() {
    this.searchHelpShow = false;
    this.customerFilter = '@';
  }

  onSearchDate() {
    this.searchHelpShow = false;
    this.customerFilter = UiUtil.exampleDateSearch();
    this.getFilteredCustomers();
  }

  onSearchPolicy() {
    this.searchHelpShow = false;
    this.customerFilter = '$';
  }

  onSearchHelpToggle() {
    this.searchHelpShow = !this.searchHelpShow;
  }

  onForceShowAll() {
    this.forceShowAll = !this.forceShowAll;
    this.getFilteredCustomers();
  }

  onBackToList() {
    LogUtil.debug('AllCustomersComponent onBackToList handler');
    if (this.viewMode === AllCustomersComponent.CUSTOMER) {
      this.customerEdit = null;
      this.viewMode = AllCustomersComponent.CUSTOMERS;
    }
  }

  onRowNew() {
    LogUtil.debug('AllCustomersComponent onRowNew handler');
    this.changed = false;
    this.validForSave = false;
    if (this.viewMode === AllCustomersComponent.CUSTOMERS) {
      this.customerEdit = this.newCustomerInstance();
      this.customerEditAttributes = [];
      this.viewMode = AllCustomersComponent.CUSTOMER;
    }
  }

  onRowDelete(row:any) {
    LogUtil.debug('AllCustomersComponent onRowDelete handler', row);
    this.deleteValue = row.customerId + ' ' + row.email;
    this.deleteConfirmationModalDialog.show();
  }

  onRowDeleteSelected() {
    if (this.selectedCustomer != null) {
      this.onRowDelete(this.selectedCustomer);
    }
  }


  onRowEditCustomer(row:CustomerInfoVO) {
    LogUtil.debug('AllCustomersComponent onRowEditCustomer handler', row);
    this.loading = true;
    this._customerService.getCustomerById(this.selectedCustomer.customerId).subscribe(customer => {
      this.customerEdit = customer;
      this.customerEditAttributes = customer.attributes;
      this.changed = false;
      this.validForSave = false;
      this.loading = false;
      this.viewMode = AllCustomersComponent.CUSTOMER;
    });
  }

  onRowEditSelected() {
    if (this.selectedCustomer != null) {
      this.onRowEditCustomer(this.selectedCustomer);
    }
  }

  onSaveHandler() {

    if (this.validForSave && this.changed) {

      if (this.customerEdit != null) {

        LogUtil.debug('AllCustomersComponent Save handler customer', this.customerEdit);

        this.loading = true;
        this._customerService.saveCustomer(this.customerEdit).subscribe(
            rez => {
              let pk = this.customerEdit.customerId;
              LogUtil.debug('AllCustomersComponent customer changed', rez);
              this.changed = false;
              this.selectedCustomer = rez;
              this.customerEdit = null;
              this.loading = false;
              this.viewMode = AllCustomersComponent.CUSTOMERS;

              if (pk > 0 && this.customerAttributesUpdate != null && this.customerAttributesUpdate.length > 0) {

                this._customerService.saveCustomerAttributes(this.customerAttributesUpdate).subscribe(rez => {
                  LogUtil.debug('AllCustomersComponent customer attributes updated', rez);
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

  onDiscardEventHandler() {
    LogUtil.debug('AllCustomersComponent discard handler');
    if (this.viewMode === AllCustomersComponent.CUSTOMER) {
      if (this.selectedCustomer != null) {
        this.onRowEditSelected();
      } else {
        this.onRowNew();
      }
    }
  }

  onDeleteConfirmationResult(modalresult: ModalResult) {
    LogUtil.debug('AllCustomersComponent onDeleteConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {

      if (this.selectedCustomer != null) {
        LogUtil.debug('AllCustomersComponent onDeleteConfirmationResult', this.selectedCustomer);

        this.loading = true;
        this._customerService.removeCustomer(this.selectedCustomer).subscribe(res => {
          LogUtil.debug('AllCustomersComponent removeCustomer', this.selectedCustomer);
          this.selectedCustomer = null;
          this.customerEdit = null;
          this.loading = false;
          this.getFilteredCustomers();
        });
      }
    }
  }

  onClearFilter() {

    this.customerFilter = '';
    this.getFilteredCustomers();

  }

  private getFilteredCustomers() {
    this.customerFilterRequired = !this.forceShowAll && (this.customerFilter == null || this.customerFilter.length < 2);

    LogUtil.debug('AllCustomersComponent getFilteredCustomers' + (this.forceShowAll ? ' forcefully': ''));

    if (!this.customerFilterRequired) {
      this.loading = true;
      this.customers.searchContext.parameters.filter = [ this.customerFilter ];
      this.customers.searchContext.size = Config.UI_TABLE_PAGE_SIZE;
      // let max = this.forceShowAll ? this.filterNoCap : this.filterCap;
      this._customerService.getFilteredCustomer(this.customers.searchContext).subscribe( allcustomers => {
        LogUtil.debug('AllCustomersComponent getFilteredCustomers', allcustomers);
        this.customers = allcustomers;
        this.selectedCustomer = null;
        this.customerEdit = null;
        this.viewMode = AllCustomersComponent.CUSTOMERS;
        this.changed = false;
        this.validForSave = false;
        this.loading = false;
        if (this.openFirstResultOnSearch && this.customers.items.length > 0) {
          this.onCustomerSelected(this.customers.items[0]);
          this.onRowEditSelected();
        }
        this.openFirstResultOnSearch = false;
      });
    } else {
      this.customers = this.newSearchResultInstance();
      this.customers.total = 0;
      this.selectedCustomer = null;
      this.customerEdit = null;
      this.customerEditAttributes = null;
      this.viewMode = AllCustomersComponent.CUSTOMERS;
      this.changed = false;
      this.validForSave = false;
    }
  }

}
