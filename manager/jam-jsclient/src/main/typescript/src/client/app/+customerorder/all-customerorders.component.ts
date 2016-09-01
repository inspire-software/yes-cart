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
import {HTTP_PROVIDERS}    from '@angular/http';
import {CustomerOrderService, I18nEventBus, Util} from './../shared/services/index';
import {UiUtil} from './../shared/ui/index';
import {TAB_DIRECTIVES} from 'ng2-bootstrap/ng2-bootstrap';
import {CustomerOrdersComponent, CustomerOrderComponent} from './components/index';
import {DataControlComponent} from './../shared/sidebar/index';
import {ModalComponent, ModalResult, ModalAction} from './../shared/modal/index';
import {CustomerOrderInfoVO} from './../shared/model/index';
import {FormValidationEvent, Futures, Future} from './../shared/event/index';
import {Config} from './../shared/config/env.config';

@Component({
  selector: 'yc-all-customerorders',
  moduleId: module.id,
  templateUrl: 'all-customerorders.component.html',
  directives: [TAB_DIRECTIVES, NgIf, CustomerOrdersComponent, CustomerOrderComponent, ModalComponent, DataControlComponent ],
})

export class AllCustomerOrdersComponent implements OnInit, OnDestroy {

  private static CUSTOMERORDERS:string = 'customerorders';
  private static CUSTOMERORDER:string = 'customerorder';

  private searchHelpShow:boolean = false;
  private forceShowAll:boolean = false;
  private viewMode:string = AllCustomerOrdersComponent.CUSTOMERORDERS;

  private customerorders:Array<CustomerOrderInfoVO> = [];
  private customerorderFilter:string;
  private customerorderFilterRequired:boolean = true;
  private customerorderFilterCapped:boolean = false;

  delayedFiltering:Future;
  delayedFilteringMs:number = Config.UI_INPUT_DELAY;
  filterCap:number = Config.UI_FILTER_CAP;
  filterNoCap:number = Config.UI_FILTER_NO_CAP;

  private selectedCustomerorder:CustomerOrderInfoVO;

  private customerorderEdit:CustomerOrderInfoVO;

  constructor(private _customerorderService:CustomerOrderService) {
    console.debug('AllCustomerOrdersComponent constructed');
  }

  changed:boolean = false;
  validForSave:boolean = false;

  ngOnInit() {
    console.debug('AllCustomerOrdersComponent ngOnInit');
    this.onRefreshHandler();
    let that = this;
    this.delayedFiltering = Futures.perpetual(function() {
      that.getFilteredCustomerorders();
    }, this.delayedFilteringMs);

  }

  ngOnDestroy() {
    console.debug('AllCustomerOrdersComponent ngOnDestroy');
  }


  onFilterChange(event:any) {

    this.delayedFiltering.delay();

  }

  getFilteredCustomerorders() {
    this.customerorderFilterRequired = !this.forceShowAll && (this.customerorderFilter == null || this.customerorderFilter.length < 2);

    console.debug('AllCustomerOrdersComponent getFilteredCustomerorders' + (this.forceShowAll ? ' forcefully': ''));

    if (!this.customerorderFilterRequired) {
      let lang = I18nEventBus.getI18nEventBus().current();
      let max = this.forceShowAll ? this.filterNoCap : this.filterCap;
      var _sub:any = this._customerorderService.getFilteredOrders(lang, this.customerorderFilter, max).subscribe( allcustomerorders => {
        console.debug('AllCustomerOrdersComponent getFilteredCustomerorders', allcustomerorders);
        this.customerorders = allcustomerorders;
        this.selectedCustomerorder = null;
        this.customerorderEdit = null;
        this.viewMode = AllCustomerOrdersComponent.CUSTOMERORDERS;
        this.changed = false;
        this.validForSave = false;
        this.customerorderFilterCapped = this.customerorders.length >= max;
        _sub.unsubscribe();
      });
    } else {
      this.customerorders = [];
      this.selectedCustomerorder = null;
      this.customerorderEdit = null;
      this.viewMode = AllCustomerOrdersComponent.CUSTOMERORDERS;
      this.changed = false;
      this.validForSave = false;
      this.customerorderFilterCapped = false;
    }
  }

  protected onRefreshHandler() {
    console.debug('AllCustomerOrdersComponent refresh handler');
    this.getFilteredCustomerorders();
  }

  onCustomerorderSelected(data:CustomerOrderInfoVO) {
    console.debug('AllCustomerOrdersComponent onCustomerorderSelected', data);
    this.selectedCustomerorder = data;
  }

  protected onSearchNumber() {
    this.searchHelpShow = false;
    this.customerorderFilter = '#ordernumber';
  }

  protected onSearchCustomer() {
    this.searchHelpShow = false;
    this.customerorderFilter = '?customer';
  }

  protected onSearchAddress() {
    this.searchHelpShow = false;
    this.customerorderFilter = '@address';
  }

  protected onSearchDate() {
    this.searchHelpShow = false;
    this.customerorderFilter = UiUtil.exampleDateSearch();
    this.getFilteredCustomerorders();
  }

  protected onSearchStatusOpen() {
    this.searchHelpShow = false;
    this.customerorderFilter = '~~';
    this.getFilteredCustomerorders();
  }

  protected onSearchStatusPayment() {
    this.searchHelpShow = false;
    this.customerorderFilter = '$$';
    this.getFilteredCustomerorders();
  }

  protected onSearchStatusCancelled() {
    this.searchHelpShow = false;
    this.customerorderFilter = '--';
    this.getFilteredCustomerorders();
  }

  protected onSearchStatusCompleted() {
    this.searchHelpShow = false;
    this.customerorderFilter = '++';
    this.getFilteredCustomerorders();
  }

  protected onSearchHelpToggle() {
    this.searchHelpShow = !this.searchHelpShow;
  }

  protected onForceShowAll() {
    this.forceShowAll = !this.forceShowAll;
    this.getFilteredCustomerorders();
  }

  protected onBackToList() {
    console.debug('AllCustomerOrdersComponent onBackToList handler');
    if (this.viewMode === AllCustomerOrdersComponent.CUSTOMERORDER) {
      this.customerorderEdit = null;
      this.viewMode = AllCustomerOrdersComponent.CUSTOMERORDERS;
    }
  }


  protected onRowEditCustomerorder(row:CustomerOrderInfoVO) {
    console.debug('AllCustomerOrdersComponent onRowEditCustomerorder handler', row);
    this.customerorderEdit = null; // TODO: get by id full
    this.changed = false;
    this.validForSave = false;
    this.viewMode = AllCustomerOrdersComponent.CUSTOMERORDER;
  }

  protected onRowEditSelected() {
    if (this.selectedCustomerorder != null) {
      this.onRowEditCustomerorder(this.selectedCustomerorder);
    }
  }

  protected onSaveHandler() {

    if (this.validForSave && this.changed) {

      if (this.customerorderEdit != null) {

        console.debug('AllCustomerOrdersComponent Save handler customerorder', this.customerorderEdit);

      }

    }

  }

  protected onDiscardEventHandler() {
    console.debug('AllCustomerOrdersComponent discard handler');
    if (this.viewMode === AllCustomerOrdersComponent.CUSTOMERORDER) {
      if (this.selectedCustomerorder != null) {
        this.onRowEditSelected();
      }
    }
  }



}
