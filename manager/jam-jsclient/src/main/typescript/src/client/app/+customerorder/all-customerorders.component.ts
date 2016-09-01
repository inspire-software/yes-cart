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
import {CustomerOrderService, I18nEventBus, ErrorEventBus, Util} from './../shared/services/index';
import {YcValidators} from './../shared/validation/validators';
import {UiUtil} from './../shared/ui/index';
import {TAB_DIRECTIVES} from 'ng2-bootstrap/ng2-bootstrap';
import {CustomerOrdersComponent, CustomerOrderComponent} from './components/index';
import {DataControlComponent} from './../shared/sidebar/index';
import {ModalComponent, ModalResult, ModalAction} from './../shared/modal/index';
import {CustomerOrderInfoVO, CustomerOrderTransitionResultVO} from './../shared/model/index';
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

  private selectedCustomerorderApprovable:boolean = false;
  private selectedCustomerorderCancellable:boolean = false;
  private selectedCustomerorderReturnable:boolean = false;
  private selectedCustomerorderRefundable:boolean = false;
  private selectedCustomerorderRefundableManual:boolean = false;

  private customerorderEdit:CustomerOrderInfoVO;

  @ViewChild('orderTransitionConfirmationModalDialog')
  orderTransitionConfirmationModalDialog:ModalComponent;
  orderTransitionName:string = '';
  orderTransitionNameOfflineNote:boolean;
  orderTransitionNumber:string = '';
  orderTransitionRequiresMessage:boolean;
  orderTransitionMessage:string;
  orderTransitionValid:boolean = false;

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

    this.orderTransitionNumber = this.selectedCustomerorder.ordernum + ' ' + this.selectedCustomerorder.lastname;
    this.orderTransitionMessage = null;
    this.orderTransitionRequiresMessage = false;
    this.orderTransitionName = '';
    this.orderTransitionNameOfflineNote = false;
    this.orderTransitionValid = false;

    let options = data.orderStatusNextOptions;
    if (options != null && options.length > 0) {
      this.selectedCustomerorderApprovable = options.indexOf('approve.order') != -1;
      this.selectedCustomerorderCancellable = options.indexOf('cancel.order') != -1;
      this.selectedCustomerorderReturnable = options.indexOf('return.order') != -1;
      this.selectedCustomerorderRefundable = options.indexOf('cancel.order.refund') != -1;
      this.selectedCustomerorderRefundableManual = options.indexOf('cancel.order.manual.refund') != -1;
    } else {
      this.selectedCustomerorderApprovable = false;
      this.selectedCustomerorderCancellable = false;
      this.selectedCustomerorderReturnable = false;
      this.selectedCustomerorderRefundable = false;
      this.selectedCustomerorderRefundableManual = false;
    }
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

  protected onApproveSelected() {
    if (this.selectedCustomerorder != null) {
      console.debug('AllCustomerOrdersComponent onApproveSelected handler', this.selectedCustomerorder);

      this.orderTransitionMessage = null;
      this.orderTransitionRequiresMessage = false;
      this.orderTransitionName = 'approve.order';
      this.orderTransitionNameOfflineNote = true;
      this.orderTransitionValid = true;

      this.orderTransitionConfirmationModalDialog.show();
    }
  }

  protected onCancelSelected() {
    if (this.selectedCustomerorder != null) {
      console.debug('AllCustomerOrdersComponent onCancelSelected handler', this.selectedCustomerorder);

      this.orderTransitionMessage = null;
      this.orderTransitionRequiresMessage = false;
      this.orderTransitionName = 'cancel.order';
      this.orderTransitionNameOfflineNote = true;
      this.orderTransitionValid = true;

      this.orderTransitionConfirmationModalDialog.show();
    }
  }

  protected onReturnSelected() {
    if (this.selectedCustomerorder != null) {
      console.debug('AllCustomerOrdersComponent onReturnSelected handler', this.selectedCustomerorder);

      this.orderTransitionMessage = null;
      this.orderTransitionRequiresMessage = false;
      this.orderTransitionName = 'return.order';
      this.orderTransitionNameOfflineNote = true;
      this.orderTransitionValid = true;

      this.orderTransitionConfirmationModalDialog.show();
    }
  }

  protected onRefundSelected() {
    if (this.selectedCustomerorder != null) {
      console.debug('AllCustomerOrdersComponent onReturnSelected handler', this.selectedCustomerorder);

      this.orderTransitionMessage = null;
      this.orderTransitionRequiresMessage = false;
      this.orderTransitionName = 'cancel.order.refund';
      this.orderTransitionNameOfflineNote = false;
      this.orderTransitionValid = true;

      this.orderTransitionConfirmationModalDialog.show();
    }
  }

  protected onRefundManualSelected() {
    if (this.selectedCustomerorder != null) {
      console.debug('AllCustomerOrdersComponent onReturnSelected handler', this.selectedCustomerorder);

      this.orderTransitionMessage = null;
      this.orderTransitionRequiresMessage = true;
      this.orderTransitionName = 'cancel.order.manual.refund';
      this.orderTransitionNameOfflineNote = true;
      this.orderTransitionValid = false;

      this.orderTransitionConfirmationModalDialog.show();
    }
  }

  protected onTransitionMessageChange(event:any) {

    this.orderTransitionValid = this.orderTransitionMessage != null && /\S+.*\S+/.test(this.orderTransitionMessage);

  }

  protected onOrderTransitionConfirmationResult(modalresult: ModalResult) {
    console.debug('AllCustomerOrdersComponent onOrderTransitionConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {

      if (this.selectedCustomerorder != null) {
        console.debug('AllCustomerOrdersComponent onOrderTransitionConfirmationResult', this.selectedCustomerorder);

        var _sub:any = this._customerorderService.transitionOrder(
          this.selectedCustomerorder, this.orderTransitionName, this.orderTransitionMessage).subscribe((res:CustomerOrderTransitionResultVO) => {
            console.debug('AllCustomerOrdersComponent onOrderTransitionConfirmationResult result', res);
            if (res.errorCode === '0') {
              console.debug('AllCustomerOrdersComponent onOrderTransitionConfirmationResult result success');
              // TODO: OK done, now refresh order
            } else {
              ErrorEventBus.getErrorEventBus().emit({ status: 500, message: res.errorCode, key: res.localizationKey, param: res.localizedMessageParameters });
            }
            _sub.unsubscribe();
        });

        //var _sub:any = this._brandService.removeBrand(this.selectedBrand).subscribe(res => {
        //  _sub.unsubscribe();
        //  console.debug('CatalogBrandComponent removeBrand', this.selectedBrand);
        //  this.selectedBrand = null;
        //  this.brandEdit = null;
        //  this.getFilteredBrands();
        //});
      }
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
