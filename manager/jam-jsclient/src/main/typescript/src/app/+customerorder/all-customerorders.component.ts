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
import { CustomerOrderService, I18nEventBus, ErrorEventBus, UserEventBus } from './../shared/services/index';
import { ModalComponent, ModalResult, ModalAction } from './../shared/modal/index';
import {
  CustomerOrderInfoVO,
  CustomerOrderVO,
  CustomerOrderDeliveryInfoVO,
  CustomerOrderTransitionResultVO,
  Pair,
  SearchResultVO,
  CustomerOrderLineVO
} from './../shared/model/index';
import { Futures, Future } from './../shared/event/index';
import { StorageUtil } from './../shared/storage/index';
import { Config } from './../../environments/environment';
import { UiUtil } from './../shared/ui/index';
import { LogUtil } from './../shared/log/index';

@Component({
  selector: 'cw-all-customerorders',
  templateUrl: 'all-customerorders.component.html',
})

export class AllCustomerOrdersComponent implements OnInit, OnDestroy {

  private static CUSTOMERORDERS:string = 'customerorders';
  private static CUSTOMERORDER:string = 'customerorder';

  private static _statuses:Pair<string, boolean>[] = [
    { first: 'os.none', second: false },
    { first: 'os.pending', second: true },
    { first: 'os.waiting.approve', second: true },
    { first: 'os.waiting.payment', second: true },
    { first: 'os.waiting', second: true },
    { first: 'os.in.progress', second: true },
    { first: 'os.cancelled', second: true },
    { first: 'os.cancelled.waiting.payment', second: true },
    { first: 'os.returned', second: true },
    { first: 'os.returned.waiting.payment', second: true },
    { first: 'os.partially.shipped', second: true },
    { first: 'os.completed', second: true },
    { first: 'qs.none', second: false },
    { first: 'qs.pending', second: true },
    { first: 'qs.cancelled', second: true },
    { first: 'qs.ready', second: true },
    { first: 'qs.rejected', second: true },
    { first: 'qs.expired', second: false },
    { first: 'qs.ordered', second: true },
  ];

  private static _waiting:string[] = [
    'os.waiting.approve', 'os.waiting', 'qs.pending'
  ];

  private static _inProgress:string[] = [
    'os.in.progress', 'os.partially.shipped', 'qs.ready'
  ];

  private static _open:string[] = [
    'os.pending', 'os.waiting.approve', 'os.waiting', 'os.waiting.payment', 'os.in.progress', 'os.partially.shipped', 'qs.pending', 'qs.ready'
  ];

  private static _pendingPayments:string[] = [
    'os.waiting.payment', 'os.cancelled.waiting.payment', 'os.returned.waiting.payment'
  ];

  private static _cancelled:string[] = [
    'os.cancelled', 'os.cancelled.waiting.payment', 'os.returned', 'os.returned.waiting.payment'
  ];

  private static _completed:string[] = [
    'os.completed'
  ];

  public searchHelpShow:boolean = false;
  public forceShowAll:boolean = false;
  public viewMode:string = AllCustomerOrdersComponent.CUSTOMERORDERS;

  public customerorders:SearchResultVO<CustomerOrderInfoVO>;
  public customerorderFilter:string;
  public customerorderFilterRequired:boolean = true;

  private delayedFiltering:Future;
  private delayedFilteringMs:number = Config.UI_INPUT_DELAY;

  public selectedCustomerorder:CustomerOrderInfoVO;

  public selectedCustomerorderNotable:boolean = false;
  public selectedCustomerorderApprovable:boolean = false;
  public selectedCustomerorderApprovableValid:boolean = false;
  public selectedCustomerorderCancellable:boolean = false;
  public selectedCustomerorderReturnable:boolean = false;
  public selectedCustomerorderRefundable:boolean = false;
  public selectedCustomerorderRefundableManual:boolean = false;

  public selectedCustomerRFQApprovable:boolean = false;
  public selectedCustomerRFQCancellable:boolean = false;
  public selectedCustomerRFQOrderlineOfferable:boolean = false;

  public customerorderEdit:CustomerOrderVO;

  public selectedCustomerdelivery:CustomerOrderDeliveryInfoVO;
  public selectedOrderline:CustomerOrderLineVO;

  public selectedCustomerdeliveryPackable:boolean = false;
  public selectedCustomerdeliveryPacked:boolean = false;
  public selectedCustomerdeliveryShippable:boolean = false;
  public selectedCustomerdeliveryShippableManual:boolean = false;
  public selectedCustomerdeliveryDelivered:boolean = false;
  public selectedCustomerdeliveryTrackable:boolean = false;

  @ViewChild('orderTransitionConfirmationModalDialog')
  private orderTransitionConfirmationModalDialog:ModalComponent;
  public orderTransitionName:string = '';
  public orderTransitionNameOfflineNote:boolean;
  public orderTransitionNumber:string = '';
  public orderTransitionRequiresParam1:boolean;
  public orderTransitionParam1Key:string;
  public orderTransitionParam1Value:string;
  public orderTransitionRequiresParam2:boolean;
  public orderTransitionParam2Key:string;
  public orderTransitionParam2Value:string;
  public orderTransitionRequiresMessage:boolean;
  public orderTransitionMessage:string;
  public orderTransitionSupportsClientMessage:boolean;
  public orderTransitionClientMessage:string;
  public orderTransitionValid:boolean = false;


  @ViewChild('orderExportModalDialog')
  private orderExportModalDialog:ModalComponent;
  @ViewChild('orderExportConfirmationModalDialog')
  private orderExportConfirmationModalDialog:ModalComponent;
  public exportAction:string = 'PROCEED';

  public loading:boolean = false;

  public changed:boolean = false;
  public validForSave:boolean = false;

  constructor(private _customerorderService:CustomerOrderService,
              private _route: ActivatedRoute) {
    LogUtil.debug('AllCustomerOrdersComponent constructed');
    this.customerorders = this.newSearchResultInstance();
  }

  newSearchResultInstance():SearchResultVO<CustomerOrderInfoVO> {
    return {
      searchContext: {
        parameters: {
          filter: [],
          statuses: []
        },
        start: 0,
        size: Config.UI_TABLE_PAGE_SIZE,
        sortBy: 'orderTimestamp',
        sortDesc: true
      },
      items: [],
      total: 0
    };
  }

  ngOnInit() {
    LogUtil.debug('AllCustomerOrdersComponent ngOnInit');
    this.onRefreshHandler();
    let that = this;
    this.delayedFiltering = Futures.perpetual(function() {
      that.getFilteredCustomerorders();
    }, this.delayedFilteringMs);
    this._route.queryParams.subscribe(params => {
      LogUtil.debug('AllCustomerOrdersComponent queryParams', params);
      let filter = params['filter'];
      if (filter != null) {
        LogUtil.debug('AllCustomerOrdersComponent filter from queryParams is', filter);
        if (filter == 'ordnew') {

          let now = new Date();
          let yearStart = now.getFullYear();
          let mthStart = now.getMonth() + 1;

          this.customerorderFilter = yearStart + '-' + (mthStart < 10 ? '0'+mthStart : mthStart)  + '<';

          this.getFilteredCustomerorders();

        } else if (filter != null) {
          LogUtil.debug('AllCustomerOrdersComponent filter from queryParams is', filter);
          if (filter == 'ordwait') {

            this.statuses.forEach((_st: Pair<string, boolean>) => {
              _st.second = AllCustomerOrdersComponent._waiting.includes(_st.first);
            });
            this.forceShowAll = true;
            this.getFilteredCustomerorders();
          } else if (filter == 'ordinp') {

            this.statuses.forEach((_st: Pair<string, boolean>) => {
              _st.second = AllCustomerOrdersComponent._inProgress.includes(_st.first);
            });
            this.forceShowAll = true;
            this.getFilteredCustomerorders();
          }
        }
      }
    });
  }

  get statuses():Pair<string, boolean>[] {
    return AllCustomerOrdersComponent._statuses;
  }

  set statuses(value:Pair<string, boolean>[]) {
    AllCustomerOrdersComponent._statuses = value;
  }

  get showGrossTotal():boolean {
    return Config.UI_ORDER_TOTALS === 'gross';
  }

  set showGrossTotal(showGrossTotal:boolean) {
    Config.UI_ORDER_TOTALS = showGrossTotal ? 'gross' : 'net';
    let cookieName = 'ADM_UI_ORDER_TOTALS';
    StorageUtil.saveValue(cookieName, Config.UI_ORDER_TOTALS);
  }

  onShowGrossTotalClick() {
    let _gross = this.showGrossTotal;
    this.showGrossTotal = !_gross;
  }

  ngOnDestroy() {
    LogUtil.debug('AllCustomerOrdersComponent ngOnDestroy');
  }


  onFilterChange(event:any) {
    this.customerorders.searchContext.start = 0; // changing filter means we need to start from first page
    this.delayedFiltering.delay();
  }

  onRefreshHandler() {
    LogUtil.debug('AllCustomerOrdersComponent refresh handler');
    if (UserEventBus.getUserEventBus().current() != null) {
      if (this.customerorderEdit != null) {
        this.onRowEditCustomerorder(this.customerorderEdit);
      } else {
        this.getFilteredCustomerorders();
      }
    }
  }

  onPageSelected(page:number) {
    LogUtil.debug('AllCustomerOrdersComponent onPageSelected', page);
    this.customerorders.searchContext.start = page;
    this.delayedFiltering.delay();
  }

  onSortSelected(sort:Pair<string, boolean>) {
    LogUtil.debug('AllCustomersComponent ononSortSelected', sort);
    if (sort == null) {
      this.customerorders.searchContext.sortBy = null;
      this.customerorders.searchContext.sortDesc = false;
    } else {
      this.customerorders.searchContext.sortBy = sort.first;
      this.customerorders.searchContext.sortDesc = sort.second;
    }
    this.delayedFiltering.delay();
  }

  onCustomerorderSelected(data:CustomerOrderInfoVO) {
    LogUtil.debug('AllCustomerOrdersComponent onCustomerorderSelected', data);
    this.selectedCustomerorder = data;

    this.orderTransitionMessage = null;
    this.orderTransitionRequiresMessage = false;
    this.orderTransitionClientMessage = null;
    this.orderTransitionSupportsClientMessage = true;
    this.orderTransitionParam1Value = null;
    this.orderTransitionParam1Key = null;
    this.orderTransitionRequiresParam1 = false;
    this.orderTransitionParam2Value = null;
    this.orderTransitionParam2Key = null;
    this.orderTransitionRequiresParam2 = false;
    this.orderTransitionName = '';
    this.orderTransitionNameOfflineNote = false;
    this.orderTransitionValid = false;

    let selectedCustomerorderNotable = false;
    let selectedCustomerorderApprovable = false;
    let selectedCustomerorderApprovableValid = false;
    let selectedCustomerorderCancellable = false;
    let selectedCustomerorderReturnable = false;
    let selectedCustomerorderRefundable = false;
    let selectedCustomerorderRefundableManual = false;

    let selectedCustomerRFQApprovable = false;
    let selectedCustomerRFQCancellable = false;

    if (this.selectedCustomerorder != null) {

      this.orderTransitionNumber = this.selectedCustomerorder.ordernum + ' ' + this.selectedCustomerorder.lastname;

      let options = data.orderStatusNextOptions;
      if (options != null && options.length > 0) {
        selectedCustomerorderNotable = options.indexOf('notes.order') != -1;
        selectedCustomerorderApprovable = options.indexOf('approve.order') != -1;
        selectedCustomerorderApprovableValid = options.indexOf('approve.pending.order') != -1;
        selectedCustomerorderCancellable = options.indexOf('cancel.order') != -1;
        selectedCustomerorderReturnable = options.indexOf('return.order') != -1;
        selectedCustomerorderRefundable = options.indexOf('cancel.order.refund') != -1;
        selectedCustomerorderRefundableManual = options.indexOf('cancel.order.manual.refund') != -1;

        selectedCustomerRFQApprovable = options.indexOf('confirm.pending.rfq') != -1;
        selectedCustomerRFQCancellable = options.indexOf('cancel.rfq') != -1;
      }
    } else {

      this.orderTransitionNumber = '';

    }

    this.selectedCustomerorderNotable = selectedCustomerorderNotable;
    this.selectedCustomerorderApprovable = selectedCustomerorderApprovable;
    this.selectedCustomerorderApprovableValid = selectedCustomerorderApprovableValid;
    this.selectedCustomerorderCancellable = selectedCustomerorderCancellable;
    this.selectedCustomerorderReturnable = selectedCustomerorderReturnable;
    this.selectedCustomerorderRefundable = selectedCustomerorderRefundable;
    this.selectedCustomerorderRefundableManual = selectedCustomerorderRefundableManual;

    this.selectedCustomerRFQApprovable = selectedCustomerRFQApprovable;
    this.selectedCustomerRFQCancellable = selectedCustomerRFQCancellable;
  }


  onCustomerdeliverySelected(data:Pair<CustomerOrderDeliveryInfoVO, CustomerOrderLineVO>) {
    LogUtil.debug('AllCustomerOrdersComponent onCustomerdeliverySelected', data);

    if (data) {
      this.selectedCustomerdelivery = data.first;
      this.selectedOrderline = data.second;
    } else {
      this.selectedCustomerdelivery = null;
      this.selectedOrderline = null;
    }

    if (this.selectedCustomerdelivery || this.selectedOrderline) {


      this.orderTransitionMessage = null;
      this.orderTransitionRequiresMessage = false;
      this.orderTransitionClientMessage = null;
      this.orderTransitionSupportsClientMessage = true;
      this.orderTransitionParam1Value = null;
      this.orderTransitionParam1Key = null;
      this.orderTransitionRequiresParam1 = false;
      this.orderTransitionParam2Value = null;
      this.orderTransitionParam2Key = null;
      this.orderTransitionRequiresParam2 = false;
      this.orderTransitionName = '';
      this.orderTransitionNameOfflineNote = false;
      this.orderTransitionValid = false;
      this.selectedCustomerorderNotable = false;
      this.selectedCustomerorderApprovable = false;
      this.selectedCustomerorderApprovableValid = false;
      this.selectedCustomerorderCancellable = false;
      this.selectedCustomerorderReturnable = false;
      this.selectedCustomerorderRefundable = false;
      this.selectedCustomerorderRefundableManual = false;
      this.selectedCustomerRFQApprovable = false;
      this.selectedCustomerRFQCancellable = false;

      if (this.selectedCustomerdelivery && this.selectedOrderline) {
        this.orderTransitionNumber = this.selectedCustomerorder.ordernum + ' '
          + this.selectedCustomerorder.lastname + ' / ' + this.selectedCustomerdelivery.deliveryNum
          + ' / ' + this.selectedOrderline.skuCode + ' ' + this.selectedOrderline.skuName;
      } else if (this.selectedCustomerdelivery) {
        this.orderTransitionNumber = this.selectedCustomerorder.ordernum + ' '
          + this.selectedCustomerorder.lastname + ' / ' + this.selectedCustomerdelivery.deliveryNum;
      } else {
        this.orderTransitionNumber = this.selectedCustomerorder.ordernum + ' '
          + this.selectedCustomerorder.lastname + ' / '
          + this.selectedOrderline.skuCode + ' ' + this.selectedOrderline.skuName;
      }

      let selectedCustomerdeliveryPackable:boolean = false;
      let selectedCustomerdeliveryPacked:boolean = false;
      let selectedCustomerdeliveryShippable:boolean = false;
      let selectedCustomerdeliveryShippableManual:boolean = false;
      let selectedCustomerdeliveryDelivered:boolean = false;
      let selectedCustomerdeliveryTrackable:boolean = false;

      let selectedCustomerRFQOrderlineOfferable:boolean = false;

      if (this.selectedCustomerdelivery && !this.selectedOrderline) {
        let deliveryOptions = this.selectedCustomerdelivery.deliveryStatusNextOptions;
        if (deliveryOptions != null && deliveryOptions.length > 0) {
          selectedCustomerdeliveryPackable = deliveryOptions.indexOf('pack.delivery') != -1;
          selectedCustomerdeliveryPacked = deliveryOptions.indexOf('mark.ready.for.shipment') != -1;
          selectedCustomerdeliveryShippable = deliveryOptions.indexOf('start.shipment') != -1;
          selectedCustomerdeliveryShippableManual = deliveryOptions.indexOf('start.shipment.manual.payment') != -1;
          selectedCustomerdeliveryDelivered = deliveryOptions.indexOf('mark.shipped') != -1;
          selectedCustomerdeliveryTrackable = selectedCustomerdeliveryPackable|| selectedCustomerdeliveryPacked ||
            selectedCustomerdeliveryShippable || selectedCustomerdeliveryShippableManual || selectedCustomerdeliveryDelivered;
        }
      }
      if (this.selectedOrderline) {
        let lineOptions = this.selectedOrderline.orderLineNextOptions;
        if (lineOptions != null && lineOptions.length > 0) {
          selectedCustomerRFQOrderlineOfferable = lineOptions.indexOf('order.line.price.rfq') != -1;
        }
      }

      this.selectedCustomerdeliveryPackable = selectedCustomerdeliveryPackable;
      this.selectedCustomerdeliveryPacked = selectedCustomerdeliveryPacked;
      this.selectedCustomerdeliveryShippable = selectedCustomerdeliveryShippable;
      this.selectedCustomerdeliveryShippableManual = selectedCustomerdeliveryShippableManual;
      this.selectedCustomerdeliveryDelivered = selectedCustomerdeliveryDelivered;
      this.selectedCustomerdeliveryTrackable = selectedCustomerdeliveryTrackable;

      this.selectedCustomerRFQOrderlineOfferable = selectedCustomerRFQOrderlineOfferable;

    } else {

      this.onCustomerorderSelected(this.selectedCustomerorder);

      this.selectedCustomerdeliveryPackable = false;
      this.selectedCustomerdeliveryPacked = false;
      this.selectedCustomerdeliveryShippable = false;
      this.selectedCustomerdeliveryShippableManual = false;
      this.selectedCustomerdeliveryDelivered = false;
      this.selectedCustomerdeliveryTrackable = false;

      this.selectedCustomerRFQOrderlineOfferable = false;

    }

  }

  onSearchId() {
    this.searchHelpShow = false;
    this.customerorderFilter = '*';
  }

  onSearchNumber() {
    this.searchHelpShow = false;
    this.customerorderFilter = '#';
  }

  onSearchCustomer() {
    this.searchHelpShow = false;
    this.customerorderFilter = '?';
  }

  onSearchAddress() {
    this.searchHelpShow = false;
    this.customerorderFilter = '@';
  }

  onSearchReserved() {
    this.searchHelpShow = false;
    this.customerorderFilter = '!';
  }

  onSearchInshop() {
    this.searchHelpShow = false;
    this.customerorderFilter = '^';
  }

  onSearchDate() {
    this.searchHelpShow = false;
    this.customerorderFilter = UiUtil.exampleDateSearch();
    this.getFilteredCustomerorders();
  }

  onSearchStatusOpen() {
    //this.searchHelpShow = false;
    //this.customerorderFilter = '~~';
    this.statuses.forEach((_st:Pair<string, boolean>) => {
      _st.second = AllCustomerOrdersComponent._open.includes(_st.first);
    });
    this.getFilteredCustomerorders();
  }

  onSearchStatusPayment() {
    //this.searchHelpShow = false;
    //this.customerorderFilter = '$$';
    this.statuses.forEach((_st:Pair<string, boolean>) => {
      _st.second = AllCustomerOrdersComponent._pendingPayments.includes(_st.first);
    });
    this.getFilteredCustomerorders();
  }

  onSearchStatusCancelled() {
    //this.searchHelpShow = false;
    //this.customerorderFilter = '--';
    this.statuses.forEach((_st:Pair<string, boolean>) => {
      _st.second = AllCustomerOrdersComponent._cancelled.includes(_st.first);
    });
    this.getFilteredCustomerorders();
  }

  onSearchStatusCompleted() {
    //this.searchHelpShow = false;
    //this.customerorderFilter = '++';
    this.statuses.forEach((_st:Pair<string, boolean>) => {
      _st.second = AllCustomerOrdersComponent._completed.includes(_st.first);
    });
    this.getFilteredCustomerorders();
  }

  onSearchStatusAllOrders() {
    let _state:boolean = false;
    this.statuses.forEach((_st:Pair<string, boolean>) => {
      if (_st.first.indexOf('os.') == 0) {
        _state = _state || _st.second;
      }
    });
    this.statuses.forEach((_st:Pair<string, boolean>) => {
      if (_st.first.indexOf('os.') == 0) {
        _st.second = !_state;
      }
    });
    this.getFilteredCustomerorders();
  }

  onSearchStatusAllRFQ() {
    let _state:boolean = false;
    this.statuses.forEach((_st:Pair<string, boolean>) => {
      if (_st.first.indexOf('qs.') == 0) {
        _state = _state || _st.second;
      }
    });
    this.statuses.forEach((_st:Pair<string, boolean>) => {
      if (_st.first.indexOf('qs.') == 0) {
        _st.second = !_state;
      }
    });
    this.getFilteredCustomerorders();
  }

  onSearchHelpToggle() {
    this.searchHelpShow = !this.searchHelpShow;
  }

  onForceShowAll() {
    this.forceShowAll = !this.forceShowAll;
    this.getFilteredCustomerorders();
  }

  onBackToList() {
    LogUtil.debug('AllCustomerOrdersComponent onBackToList handler');
    if (this.viewMode === AllCustomerOrdersComponent.CUSTOMERORDER) {
      this.customerorderEdit = null;
      this.onCustomerdeliverySelected(null);
      this.onCustomerorderSelected(this.selectedCustomerorder);
      this.viewMode = AllCustomerOrdersComponent.CUSTOMERORDERS;
    }
  }


  onRowEditCustomerorder(row:CustomerOrderInfoVO) {
    LogUtil.debug('AllCustomerOrdersComponent onRowEditCustomerorder handler', row);

    this.loading = true;
    let lang = I18nEventBus.getI18nEventBus().current();
    this._customerorderService.getOrderById(lang, row.customerorderId).subscribe( customerorder => {
      LogUtil.debug('AllCustomerOrdersComponent getOrderById', customerorder);

      this.customerorderEdit = customerorder;
      // this.selectedCustomerorder = customerorder;
      this.onCustomerorderSelected(customerorder);

      this.changed = false;
      this.validForSave = false;
      this.loading = false;
      this.viewMode = AllCustomerOrdersComponent.CUSTOMERORDER;
    });

  }

  onRowEditSelected() {
    if (this.selectedCustomerorder != null) {
      this.onRowEditCustomerorder(this.selectedCustomerorder);
    }
  }

  onApproveValidSelected() {
    if (this.selectedCustomerorder != null) {
      LogUtil.debug('AllCustomerOrdersComponent onApproveValidSelected handler', this.selectedCustomerorder);

      this.orderTransitionMessage = null;
      this.orderTransitionRequiresMessage = false;
      this.orderTransitionClientMessage = null;
      this.orderTransitionSupportsClientMessage = false;
      this.orderTransitionParam1Value = null;
      this.orderTransitionParam1Key = null;
      this.orderTransitionRequiresParam1 = false;
      this.orderTransitionParam2Value = null;
      this.orderTransitionParam2Key = null;
      this.orderTransitionRequiresParam2 = false;
      this.orderTransitionName = 'approve.pending.order';
      this.orderTransitionNameOfflineNote = true;
      this.orderTransitionValid = true;

      this.orderTransitionConfirmationModalDialog.show();
    }
  }

  onApproveSelected() {
    if (this.selectedCustomerorder != null) {
      LogUtil.debug('AllCustomerOrdersComponent onApproveSelected handler', this.selectedCustomerorder);

      this.orderTransitionMessage = null;
      this.orderTransitionRequiresMessage = false;
      this.orderTransitionClientMessage = null;
      this.orderTransitionSupportsClientMessage = true;
      this.orderTransitionParam1Value = null;
      this.orderTransitionParam1Key = null;
      this.orderTransitionRequiresParam1 = false;
      this.orderTransitionParam2Value = null;
      this.orderTransitionParam2Key = null;
      this.orderTransitionRequiresParam2 = false;
      this.orderTransitionName = 'approve.order';
      this.orderTransitionNameOfflineNote = true;
      this.orderTransitionValid = true;

      this.orderTransitionConfirmationModalDialog.show();
    }
  }

  onCancelSelected() {
    if (this.selectedCustomerorder != null) {
      LogUtil.debug('AllCustomerOrdersComponent onCancelSelected handler', this.selectedCustomerorder);

      this.orderTransitionMessage = null;
      this.orderTransitionRequiresMessage = false;
      this.orderTransitionClientMessage = null;
      this.orderTransitionSupportsClientMessage = true;
      this.orderTransitionParam1Value = null;
      this.orderTransitionParam1Key = null;
      this.orderTransitionRequiresParam1 = false;
      this.orderTransitionParam2Value = null;
      this.orderTransitionParam2Key = null;
      this.orderTransitionRequiresParam2 = false;
      this.orderTransitionName = 'cancel.order';
      this.orderTransitionNameOfflineNote = true;
      this.orderTransitionValid = true;

      this.orderTransitionConfirmationModalDialog.show();
    }
  }

  onReturnSelected() {
    if (this.selectedCustomerorder != null) {
      LogUtil.debug('AllCustomerOrdersComponent onReturnSelected handler', this.selectedCustomerorder);

      this.orderTransitionMessage = null;
      this.orderTransitionRequiresMessage = false;
      this.orderTransitionClientMessage = null;
      this.orderTransitionSupportsClientMessage = true;
      this.orderTransitionParam1Value = null;
      this.orderTransitionParam1Key = null;
      this.orderTransitionRequiresParam1 = false;
      this.orderTransitionParam2Value = null;
      this.orderTransitionParam2Key = null;
      this.orderTransitionRequiresParam2 = false;
      this.orderTransitionName = 'return.order';
      this.orderTransitionNameOfflineNote = true;
      this.orderTransitionValid = true;

      this.orderTransitionConfirmationModalDialog.show();
    }
  }

  onRefundSelected() {
    if (this.selectedCustomerorder != null) {
      LogUtil.debug('AllCustomerOrdersComponent onReturnSelected handler', this.selectedCustomerorder);

      this.orderTransitionMessage = null;
      this.orderTransitionRequiresMessage = false;
      this.orderTransitionClientMessage = null;
      this.orderTransitionSupportsClientMessage = true;
      this.orderTransitionParam1Value = null;
      this.orderTransitionParam1Key = null;
      this.orderTransitionRequiresParam1 = false;
      this.orderTransitionParam2Value = null;
      this.orderTransitionParam2Key = null;
      this.orderTransitionRequiresParam2 = false;
      this.orderTransitionName = 'cancel.order.refund';
      this.orderTransitionNameOfflineNote = false;
      this.orderTransitionValid = true;

      this.orderTransitionConfirmationModalDialog.show();
    }
  }

  onRefundManualSelected() {
    if (this.selectedCustomerorder != null) {
      LogUtil.debug('AllCustomerOrdersComponent onReturnSelected handler', this.selectedCustomerorder);

      this.orderTransitionMessage = null;
      this.orderTransitionRequiresMessage = true;
      this.orderTransitionClientMessage = null;
      this.orderTransitionSupportsClientMessage = true;
      this.orderTransitionParam1Value = null;
      this.orderTransitionParam1Key = null;
      this.orderTransitionRequiresParam1 = false;
      this.orderTransitionParam2Value = null;
      this.orderTransitionParam2Key = null;
      this.orderTransitionRequiresParam2 = false;
      this.orderTransitionName = 'cancel.order.manual.refund';
      this.orderTransitionNameOfflineNote = true;
      this.orderTransitionValid = false;

      this.orderTransitionConfirmationModalDialog.show();
    }
  }

  onNotesSelected() {
    if (this.selectedCustomerorder != null) {
      LogUtil.debug('AllCustomerOrdersComponent onNotesSelected handler', this.selectedCustomerorder);

      this.orderTransitionMessage = null;
      this.orderTransitionRequiresMessage = true;
      this.orderTransitionClientMessage = null;
      this.orderTransitionSupportsClientMessage = false;
      this.orderTransitionParam1Value = null;
      this.orderTransitionParam1Key = null;
      this.orderTransitionRequiresParam1 = false;
      this.orderTransitionParam2Value = null;
      this.orderTransitionParam2Key = null;
      this.orderTransitionRequiresParam2 = false;
      this.orderTransitionName = 'notes.order';
      this.orderTransitionNameOfflineNote = false;
      this.orderTransitionValid = false;

      this.orderTransitionConfirmationModalDialog.show();
    }
  }

  onRFQConfirmSelected() {
    if (this.selectedCustomerorder != null) {
      LogUtil.debug('AllCustomerOrdersComponent onRFQConfirmSelected handler', this.selectedCustomerorder);

      this.orderTransitionMessage = null;
      this.orderTransitionRequiresMessage = true;
      this.orderTransitionClientMessage = null;
      this.orderTransitionSupportsClientMessage = true;
      this.orderTransitionParam1Value = null;
      this.orderTransitionParam1Key = null;
      this.orderTransitionRequiresParam1 = false;
      this.orderTransitionParam2Value = null;
      this.orderTransitionParam2Key = null;
      this.orderTransitionRequiresParam2 = false;
      this.orderTransitionName = 'confirm.pending.rfq';
      this.orderTransitionNameOfflineNote = false;
      this.orderTransitionValid = false;

      this.orderTransitionConfirmationModalDialog.show();
    }
  }

  onRFQCancelSelected() {
    if (this.selectedCustomerorder != null) {
      LogUtil.debug('AllCustomerOrdersComponent onRFQCancelSelected handler', this.selectedCustomerorder);

      this.orderTransitionMessage = null;
      this.orderTransitionRequiresMessage = true;
      this.orderTransitionClientMessage = null;
      this.orderTransitionSupportsClientMessage = true;
      this.orderTransitionParam1Value = null;
      this.orderTransitionParam1Key = null;
      this.orderTransitionRequiresParam1 = false;
      this.orderTransitionParam2Value = null;
      this.orderTransitionParam2Key = null;
      this.orderTransitionRequiresParam2 = false;
      this.orderTransitionName = 'cancel.rfq';
      this.orderTransitionNameOfflineNote = false;
      this.orderTransitionValid = false;

      this.orderTransitionConfirmationModalDialog.show();
    }
  }

  onRFQSetOfferSelected() {
    if (this.selectedOrderline != null) {
      LogUtil.debug('AllCustomerOrdersComponent onRFQSetOfferSelected handler', this.selectedOrderline);

      this.orderTransitionMessage = null;
      this.orderTransitionRequiresMessage = false;
      this.orderTransitionClientMessage = null;
      this.orderTransitionSupportsClientMessage = false;
      this.orderTransitionParam1Value = this.selectedOrderline.taxExclusiveOfPrice ? this.selectedOrderline.netPrice.toString() : this.selectedOrderline.grossPrice.toString();
      this.orderTransitionParam1Key = this.selectedOrderline.taxExclusiveOfPrice ? 'linenetprice' : 'linegrossprice';
      this.orderTransitionRequiresParam1 = true;
      this.orderTransitionParam2Value = null;
      this.orderTransitionParam2Key = null;
      this.orderTransitionRequiresParam2 = false;
      this.orderTransitionName = 'order.line.price.rfq';
      this.orderTransitionNameOfflineNote = false;
      this.orderTransitionValid = false;

      this.orderTransitionConfirmationModalDialog.show();
    }
  }


  onTransitionMessageChange(event:any) {

    let messageEntered = this.orderTransitionMessage != null && /\S+.*\S+/.test(this.orderTransitionMessage);
    let validMessage = !this.orderTransitionRequiresMessage || messageEntered;

    let param1Entered = this.orderTransitionParam1Value != null && /\S+.*\S+/.test(this.orderTransitionParam1Value);
    let validParam1 = !this.orderTransitionRequiresParam1 || param1Entered;

    let param2Entered = this.orderTransitionParam2Value != null && /\S+.*\S+/.test(this.orderTransitionParam2Value);
    let validParam2 = !this.orderTransitionRequiresParam2 || param2Entered;

    this.orderTransitionValid = validMessage && validParam1 && validParam2;

  }

  onOrderTransitionConfirmationResult(modalresult: ModalResult) {
    LogUtil.debug('AllCustomerOrdersComponent onOrderTransitionConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {

      let context:any = {};
      if (this.orderTransitionMessage != null && /\S+.*\S+/.test(this.orderTransitionMessage)) {
        context['message'] = this.orderTransitionMessage;
      }
      if (this.orderTransitionClientMessage != null && /\S+.*\S+/.test(this.orderTransitionClientMessage)) {
        context['clientMessage'] = this.orderTransitionClientMessage;
      }
      if (this.orderTransitionRequiresParam1 && this.orderTransitionParam1Value != null && /\S+.*\S+/.test(this.orderTransitionParam1Value)) {
        context[this.orderTransitionParam1Key] = this.orderTransitionParam1Value;
      }
      if (this.orderTransitionRequiresParam2 && this.orderTransitionParam2Value != null && /\S+.*\S+/.test(this.orderTransitionParam2Value)) {
        context[this.orderTransitionParam2Key] = this.orderTransitionParam2Value;
      }

      if (this.selectedCustomerorder != null && this.selectedOrderline != null) {
        LogUtil.debug('AllCustomerOrdersComponent onOrderTransitionConfirmationResult', this.selectedCustomerorder, this.selectedCustomerdelivery, this.selectedOrderline);

        this.loading = true;

        this._customerorderService.transitionOrderLine(
          this.selectedCustomerorder, this.selectedCustomerdelivery, this.selectedOrderline, this.orderTransitionName, context).subscribe((res:CustomerOrderTransitionResultVO) => {
          LogUtil.debug('AllCustomerOrdersComponent onOrderTransitionConfirmationResult result', res);
          if (res.errorCode === '0') {

            let lang = I18nEventBus.getI18nEventBus().current();
            this._customerorderService.getOrderById(lang, this.selectedCustomerorder.customerorderId).subscribe( customerorder => {
              LogUtil.debug('AllCustomerOrdersComponent getOrderById', customerorder);

              if (this.customerorderEdit != null && this.customerorderEdit.customerorderId == this.selectedCustomerorder.customerorderId) {
                // We are editing
                this.customerorderEdit = customerorder;
              }

              let idx = this.customerorders.items.findIndex(order => {
                return order.customerorderId === customerorder.customerorderId;
              });

              if (idx != -1) {
                this.customerorders.items[idx] = customerorder;
              }
              this.customerorders = this.customerorders; // hack to retrigger change
              this.onCustomerorderSelected(customerorder);

              this.onCustomerdeliverySelected(null);

              this.changed = false;
              this.validForSave = false;
            });

          } else {
            ErrorEventBus.getErrorEventBus().emit({ status: 500, message: res.errorCode, key: res.localizationKey, param: res.localizedMessageParameters });
          }
          this.loading = false;
        });

      } else if (this.selectedCustomerorder != null && this.selectedCustomerdelivery == null) {
        LogUtil.debug('AllCustomerOrdersComponent onOrderTransitionConfirmationResult', this.selectedCustomerorder);

        this.loading = true;

        this._customerorderService.transitionOrder(
          this.selectedCustomerorder, this.orderTransitionName, context).subscribe((res:CustomerOrderTransitionResultVO) => {
            LogUtil.debug('AllCustomerOrdersComponent onOrderTransitionConfirmationResult result', res);
            if (res.errorCode === '0') {

              let lang = I18nEventBus.getI18nEventBus().current();
              this._customerorderService.getOrderById(lang, this.selectedCustomerorder.customerorderId).subscribe( customerorder => {
                LogUtil.debug('AllCustomerOrdersComponent getOrderById', customerorder);

                if (this.customerorderEdit != null && this.customerorderEdit.customerorderId == this.selectedCustomerorder.customerorderId) {
                  // We are editing
                  this.customerorderEdit = customerorder;
                }

                let idx = this.customerorders.items.findIndex(order => {
                  return order.customerorderId === customerorder.customerorderId;
                });

                if (idx != -1) {
                  this.customerorders.items[idx] = customerorder;
                }
                this.customerorders = this.customerorders; // hack to retrigger change
                this.onCustomerorderSelected(customerorder);

                this.changed = false;
                this.validForSave = false;
              });

            } else {
              ErrorEventBus.getErrorEventBus().emit({ status: 500, message: res.errorCode, key: res.localizationKey, param: res.localizedMessageParameters });
            }
            this.loading = false;
        });

      } else if (this.selectedCustomerorder != null && this.selectedCustomerdelivery != null) {
        LogUtil.debug('AllCustomerOrdersComponent onOrderTransitionConfirmationResult', this.selectedCustomerorder, this.selectedCustomerdelivery);

        this.loading = true;

        this._customerorderService.transitionDelivery(
          this.selectedCustomerorder, this.selectedCustomerdelivery, this.orderTransitionName, context).subscribe((res:CustomerOrderTransitionResultVO) => {
            LogUtil.debug('AllCustomerOrdersComponent onOrderTransitionConfirmationResult result', res);
            if (res.errorCode === '0') {

              let lang = I18nEventBus.getI18nEventBus().current();
              this._customerorderService.getOrderById(lang, this.selectedCustomerorder.customerorderId).subscribe((customerorder:CustomerOrderVO) => {
                LogUtil.debug('AllCustomerOrdersComponent getOrderById', customerorder);

                if (this.customerorderEdit != null && this.customerorderEdit.customerorderId == this.selectedCustomerorder.customerorderId) {
                  // We are editing
                  this.customerorderEdit = customerorder;
                }

                let idx = this.customerorders.items.findIndex(order => {
                  return order.customerorderId === customerorder.customerorderId;
                });

                if (idx != -1) {
                  this.customerorders.items[idx] = customerorder;
                }
                this.customerorders = this.customerorders; // hack to retrigger change
                this.onCustomerorderSelected(customerorder);

                let delivery:CustomerOrderDeliveryInfoVO = null;
                if (this.selectedCustomerorder != null && this.selectedCustomerdelivery != null) {
                  LogUtil.debug('AllCustomerOrdersComponent trying to re-select delivery', this.selectedCustomerdelivery);
                  let idx2 = customerorder.deliveries.findIndex(delivery => {
                    return delivery.deliveryNum == this.selectedCustomerdelivery.deliveryNum;
                  });
                  if (idx2 != -1) {
                    delivery = customerorder.deliveries[idx2];
                    LogUtil.debug('AllCustomerOrdersComponent re-selected delivery', delivery);
                  }
                }
                this.onCustomerdeliverySelected({ first: delivery, second: null });

                this.changed = false;
                this.validForSave = false;
              });

            } else {
              ErrorEventBus.getErrorEventBus().emit({ status: 500, message: res.errorCode, key: res.localizationKey, param: res.localizedMessageParameters });
            }
            this.loading = false;
          });


      }
    }
  }



  onUpdateDeliveryRefSelected() {
    if (this.selectedCustomerdelivery != null) {
      LogUtil.debug('AllCustomerOrdersComponent onUpdateDeliveryRefSelected handler', this.selectedCustomerdelivery);

      this.orderTransitionMessage = null;
      this.orderTransitionRequiresMessage = true;
      this.orderTransitionClientMessage = null;
      this.orderTransitionSupportsClientMessage = true;
      this.orderTransitionParam1Value = this.selectedCustomerdelivery.refNo;
      this.orderTransitionParam1Key = 'deliveryref';
      this.orderTransitionRequiresParam1 = true;
      this.orderTransitionParam2Value = null;
      this.orderTransitionParam2Key = 'deliveryurl';
      this.orderTransitionRequiresParam2 = true;
      this.orderTransitionName = 'update.external.ref';
      this.orderTransitionNameOfflineNote = false;
      this.orderTransitionValid = false;

      this.orderTransitionConfirmationModalDialog.show();
    }
  }

  onPackDeliverySelected() {
    if (this.selectedCustomerdelivery != null) {
      LogUtil.debug('AllCustomerOrdersComponent onPackDeliverySelected handler', this.selectedCustomerdelivery);

      this.orderTransitionMessage = null;
      this.orderTransitionRequiresMessage = false;
      this.orderTransitionClientMessage = null;
      this.orderTransitionSupportsClientMessage = true;
      this.orderTransitionParam1Value = null;
      this.orderTransitionParam1Key = null;
      this.orderTransitionRequiresParam1 = false;
      this.orderTransitionParam2Value = null;
      this.orderTransitionParam2Key = null;
      this.orderTransitionRequiresParam2 = false;
      this.orderTransitionName = 'pack.delivery';
      this.orderTransitionNameOfflineNote = false;
      this.orderTransitionValid = true;

      this.orderTransitionConfirmationModalDialog.show();
    }
  }

  onMarkReadyForShippingDeliverySelected() {
    if (this.selectedCustomerdelivery != null) {
      LogUtil.debug('AllCustomerOrdersComponent onMarkReadyForShippingDeliverySelected handler', this.selectedCustomerdelivery);

      this.orderTransitionMessage = null;
      this.orderTransitionRequiresMessage = false;
      this.orderTransitionClientMessage = null;
      this.orderTransitionSupportsClientMessage = true;
      this.orderTransitionParam1Value = null;
      this.orderTransitionParam1Key = null;
      this.orderTransitionRequiresParam1 = false;
      this.orderTransitionParam2Value = null;
      this.orderTransitionParam2Key = null;
      this.orderTransitionRequiresParam2 = false;
      this.orderTransitionName = 'mark.ready.for.shipment';
      this.orderTransitionNameOfflineNote = false;
      this.orderTransitionValid = true;

      this.orderTransitionConfirmationModalDialog.show();
    }
  }

  onStartDeliverySelected() {
    if (this.selectedCustomerdelivery != null) {
      LogUtil.debug('AllCustomerOrdersComponent onStartDeliverySelected handler', this.selectedCustomerdelivery);

      this.orderTransitionMessage = null;
      this.orderTransitionRequiresMessage = false;
      this.orderTransitionClientMessage = null;
      this.orderTransitionSupportsClientMessage = true;
      this.orderTransitionParam1Value = null;
      this.orderTransitionParam1Key = null;
      this.orderTransitionRequiresParam1 = false;
      this.orderTransitionParam2Value = null;
      this.orderTransitionParam2Key = null;
      this.orderTransitionRequiresParam2 = false;
      this.orderTransitionName = 'start.shipment';
      this.orderTransitionNameOfflineNote = false;
      this.orderTransitionValid = true;

      this.orderTransitionConfirmationModalDialog.show();
    }
  }

  onShipDeliveryManualSelected() {
    if (this.selectedCustomerdelivery != null) {
      LogUtil.debug('AllCustomerOrdersComponent onShipDeliveryManualSelected handler', this.selectedCustomerdelivery);

      this.orderTransitionMessage = null;
      this.orderTransitionRequiresMessage = true;
      this.orderTransitionClientMessage = null;
      this.orderTransitionSupportsClientMessage = true;
      this.orderTransitionParam1Value = null;
      this.orderTransitionParam1Key = null;
      this.orderTransitionRequiresParam1 = false;
      this.orderTransitionParam2Value = null;
      this.orderTransitionParam2Key = null;
      this.orderTransitionRequiresParam2 = false;
      this.orderTransitionName = 'start.shipment.manual.payment';
      this.orderTransitionNameOfflineNote = true;
      this.orderTransitionValid = false;

      this.orderTransitionConfirmationModalDialog.show();
    }
  }


  onMarkShippedDeliverySelected() {
    if (this.selectedCustomerdelivery != null) {
      LogUtil.debug('AllCustomerOrdersComponent onMarkShippedDeliverySelected handler', this.selectedCustomerdelivery);

      this.orderTransitionMessage = null;
      this.orderTransitionRequiresMessage = false;
      this.orderTransitionClientMessage = null;
      this.orderTransitionSupportsClientMessage = true;
      this.orderTransitionParam1Value = null;
      this.orderTransitionParam1Key = null;
      this.orderTransitionRequiresParam1 = false;
      this.orderTransitionParam2Value = null;
      this.orderTransitionParam2Key = null;
      this.orderTransitionRequiresParam2 = false;
      this.orderTransitionName = 'mark.shipped';
      this.orderTransitionNameOfflineNote = true;
      this.orderTransitionValid = true;

      this.orderTransitionConfirmationModalDialog.show();
    }
  }



  onExportSelected() {
    LogUtil.debug('AllCustomerOrdersComponent onExportSelected handler');
    this.orderExportModalDialog.show();
  }


  onExportSelectedOption(modalresult: ModalResult) {
    LogUtil.debug('AllCustomerOrdersComponent onExportSelectedOption handler');
    if (ModalAction.POSITIVE == modalresult.action) {

      this.orderExportConfirmationModalDialog.show();

    }
  }


  onExportSelectedConfirmed(modalresult: ModalResult) {
    LogUtil.debug('AllCustomerOrdersComponent onExportSelectedConfirmed handler');
    if (ModalAction.POSITIVE == modalresult.action) {

      let lang = I18nEventBus.getI18nEventBus().current();
      this.loading = true;
      this._customerorderService.exportOrder(lang, this.selectedCustomerorder.customerorderId, this.exportAction == 'PROCEED').subscribe(exportedOrder => {
        LogUtil.debug('AllCustomerOrdersComponent exportOrder', exportedOrder);

        if (this.customerorderEdit != null && this.customerorderEdit.customerorderId == this.selectedCustomerorder.customerorderId) {
          // We are editing
          this.customerorderEdit = exportedOrder;
        }

        let idx = this.customerorders.items.findIndex(order => {
          return order.customerorderId === exportedOrder.customerorderId;
        });

        if (idx != -1) {
          this.customerorders.items[idx] = exportedOrder;
        } else {
          this.customerorders.items.splice(0, 0, exportedOrder);
          idx = 0;
        }
        this.customerorders = this.customerorders; // hack to retrigger change
        this.onCustomerorderSelected(exportedOrder);

        this.loading = false;
      });
    }
  }



  onSaveHandler() {

    if (this.validForSave && this.changed) {

      if (this.customerorderEdit != null) {

        LogUtil.debug('AllCustomerOrdersComponent Save handler customerorder', this.customerorderEdit);

      }

    }

  }

  onDiscardEventHandler() {
    LogUtil.debug('AllCustomerOrdersComponent discard handler');
    if (this.viewMode === AllCustomerOrdersComponent.CUSTOMERORDER) {
      if (this.selectedCustomerorder != null) {
        this.onRowEditSelected();
      }
    }
  }


  onClearFilter() {

    this.customerorderFilter = '';
    this.getFilteredCustomerorders();

  }


  private getFilteredCustomerorders() {
    this.customerorderFilterRequired = !this.forceShowAll && (this.customerorderFilter == null || this.customerorderFilter.length < 2);

    LogUtil.debug('AllCustomerOrdersComponent getFilteredCustomerorders' + (this.forceShowAll ? ' forcefully': ''));

    if (!this.customerorderFilterRequired) {
      this.loading = true;
      let lang = I18nEventBus.getI18nEventBus().current();

      let sts:string[] = [];
      AllCustomerOrdersComponent._statuses.forEach((_st:Pair<string, boolean>) => {
        if (_st.second) {
          sts.push(_st.first);
        }
      });

      this.customerorders.searchContext.parameters.filter = [ this.customerorderFilter ];
      this.customerorders.searchContext.parameters.statuses = sts;
      this.customerorders.searchContext.size = Config.UI_TABLE_PAGE_SIZE;

      this._customerorderService.getFilteredOrders(lang, this.customerorders.searchContext).subscribe( allcustomerorders => {
        LogUtil.debug('AllCustomerOrdersComponent getFilteredCustomerorders', allcustomerorders);
        this.customerorders = allcustomerorders;
        this.selectedCustomerorder = null;
        this.customerorderEdit = null;
        this.viewMode = AllCustomerOrdersComponent.CUSTOMERORDERS;
        this.changed = false;
        this.validForSave = false;
        this.loading = false;
      });
    } else {
      this.customerorders = this.newSearchResultInstance();
      this.selectedCustomerorder = null;
      this.customerorderEdit = null;
      this.viewMode = AllCustomerOrdersComponent.CUSTOMERORDERS;
      this.changed = false;
      this.validForSave = false;
    }
  }

}
