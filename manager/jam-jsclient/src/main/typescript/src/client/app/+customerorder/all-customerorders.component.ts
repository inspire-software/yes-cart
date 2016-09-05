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
import {CustomerOrderInfoVO, CustomerOrderVO, CustomerOrderDeliveryInfoVO, CustomerOrderTransitionResultVO} from './../shared/model/index';
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

  private customerorderEdit:CustomerOrderVO;

  private selectedCustomerdelivery:CustomerOrderDeliveryInfoVO;

  private selectedCustomerdeliveryPackable:boolean = false;
  private selectedCustomerdeliveryPacked:boolean = false;
  private selectedCustomerdeliveryShippable:boolean = false;
  private selectedCustomerdeliveryShippableManual:boolean = false;
  private selectedCustomerdeliveryDelivered:boolean = false;


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

    this.orderTransitionMessage = null;
    this.orderTransitionRequiresMessage = false;
    this.orderTransitionName = '';
    this.orderTransitionNameOfflineNote = false;
    this.orderTransitionValid = false;

    let selectedCustomerorderApprovable = false;
    let selectedCustomerorderCancellable = false;
    let selectedCustomerorderReturnable = false;
    let selectedCustomerorderRefundable = false;
    let selectedCustomerorderRefundableManual = false;

    if (this.selectedCustomerorder != null) {

      this.orderTransitionNumber = this.selectedCustomerorder.ordernum + ' ' + this.selectedCustomerorder.lastname;

      let options = data.orderStatusNextOptions;
      if (options != null && options.length > 0) {
        selectedCustomerorderApprovable = options.indexOf('approve.order') != -1;
        selectedCustomerorderCancellable = options.indexOf('cancel.order') != -1;
        selectedCustomerorderReturnable = options.indexOf('return.order') != -1;
        selectedCustomerorderRefundable = options.indexOf('cancel.order.refund') != -1;
        selectedCustomerorderRefundableManual = options.indexOf('cancel.order.manual.refund') != -1;
      }
    } else {

      this.orderTransitionNumber = '';

    }

    this.selectedCustomerorderApprovable = selectedCustomerorderApprovable;
    this.selectedCustomerorderCancellable = selectedCustomerorderCancellable;
    this.selectedCustomerorderReturnable = selectedCustomerorderReturnable;
    this.selectedCustomerorderRefundable = selectedCustomerorderRefundable;
    this.selectedCustomerorderRefundableManual = selectedCustomerorderRefundableManual;
  }


  onCustomerdeliverySelected(data:CustomerOrderDeliveryInfoVO) {
    console.debug('AllCustomerOrdersComponent onCustomerdeliverySelected', data);
    this.selectedCustomerdelivery = data;

    if (this.selectedCustomerdelivery) {

      this.orderTransitionMessage = null;
      this.orderTransitionRequiresMessage = false;
      this.orderTransitionName = '';
      this.orderTransitionNameOfflineNote = false;
      this.orderTransitionValid = false;
      this.selectedCustomerorderApprovable = false;
      this.selectedCustomerorderCancellable = false;
      this.selectedCustomerorderReturnable = false;
      this.selectedCustomerorderRefundable = false;
      this.selectedCustomerorderRefundableManual = false;

      this.orderTransitionNumber = this.selectedCustomerorder.ordernum + ' ' + this.selectedCustomerorder.lastname + ' / ' + this.selectedCustomerdelivery.deliveryNum;

      let selectedCustomerdeliveryPackable:boolean = false;
      let selectedCustomerdeliveryPacked:boolean = false;
      let selectedCustomerdeliveryShippable:boolean = false;
      let selectedCustomerdeliveryShippableManual:boolean = false;
      let selectedCustomerdeliveryDelivered:boolean = false;

      let options = data.deliveryStatusNextOptions;
      if (options != null && options.length > 0) {
        selectedCustomerdeliveryPackable = options.indexOf('pack.delivery') != -1;
        selectedCustomerdeliveryPacked = options.indexOf('mark.ready.for.shipment') != -1;
        selectedCustomerdeliveryShippable = options.indexOf('start.shipment') != -1;
        selectedCustomerdeliveryShippableManual = options.indexOf('start.shipment.manual.payment') != -1;
        selectedCustomerdeliveryDelivered = options.indexOf('mark.shipped') != -1;
      }

      this.selectedCustomerdeliveryPackable = selectedCustomerdeliveryPackable;
      this.selectedCustomerdeliveryPacked = selectedCustomerdeliveryPacked;
      this.selectedCustomerdeliveryShippable = selectedCustomerdeliveryShippable;
      this.selectedCustomerdeliveryShippableManual = selectedCustomerdeliveryShippableManual;
      this.selectedCustomerdeliveryDelivered = selectedCustomerdeliveryDelivered;

    } else {
      this.onCustomerorderSelected(this.selectedCustomerorder);
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
      this.onCustomerdeliverySelected(null);
      this.onCustomerorderSelected(this.selectedCustomerorder);
      this.viewMode = AllCustomerOrdersComponent.CUSTOMERORDERS;
    }
  }


  protected onRowEditCustomerorder(row:CustomerOrderInfoVO) {
    console.debug('AllCustomerOrdersComponent onRowEditCustomerorder handler', row);

    let lang = I18nEventBus.getI18nEventBus().current();
    var _sub:any = this._customerorderService.getOrderById(lang, this.selectedCustomerorder.customerorderId).subscribe( customerorder => {
      console.debug('AllCustomerOrdersComponent getOrderById', customerorder);

      this.customerorderEdit = customerorder;
      this.changed = false;
      this.validForSave = false;
      this.viewMode = AllCustomerOrdersComponent.CUSTOMERORDER;

      _sub.unsubscribe();
    });

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

      if (this.selectedCustomerorder != null && this.selectedCustomerdelivery == null) {
        console.debug('AllCustomerOrdersComponent onOrderTransitionConfirmationResult', this.selectedCustomerorder);

        var _sub:any = this._customerorderService.transitionOrder(
          this.selectedCustomerorder, this.orderTransitionName, this.orderTransitionMessage).subscribe((res:CustomerOrderTransitionResultVO) => {
            console.debug('AllCustomerOrdersComponent onOrderTransitionConfirmationResult result', res);
            if (res.errorCode === '0') {

              let lang = I18nEventBus.getI18nEventBus().current();
              var _sub2:any = this._customerorderService.getOrderById(lang, this.selectedCustomerorder.customerorderId).subscribe( customerorder => {
                console.debug('AllCustomerOrdersComponent getOrderById', customerorder);

                if (this.customerorderEdit != null && this.customerorderEdit.customerorderId == this.selectedCustomerorder.customerorderId) {
                  // We are editing
                  this.customerorderEdit = customerorder;
                }

                let idx = this.customerorders.findIndex(order => {
                  return order.customerorderId === customerorder.customerorderId;
                });

                if (idx != -1) {
                  this.customerorders[idx] = customerorder;
                } else {
                  this.customerorders.splice(0, 0, customerorder);
                  idx = 0;
                }
                this.selectedCustomerorder = this.customerorders[idx];
                this.customerorders = this.customerorders.slice(0, this.customerorders.length); // hack to retrigger change

                this.changed = false;
                this.validForSave = false;
                _sub2.unsubscribe();
              });

            } else {
              ErrorEventBus.getErrorEventBus().emit({ status: 500, message: res.errorCode, key: res.localizationKey, param: res.localizedMessageParameters });
            }
            _sub.unsubscribe();
        });

      } else if (this.selectedCustomerorder != null && this.selectedCustomerdelivery != null) {
        console.debug('AllCustomerOrdersComponent onOrderTransitionConfirmationResult', this.selectedCustomerorder);

        var _sub:any = this._customerorderService.transitionDelivery(
          this.selectedCustomerorder, this.selectedCustomerdelivery, this.orderTransitionName, this.orderTransitionMessage).subscribe((res:CustomerOrderTransitionResultVO) => {
            console.debug('AllCustomerOrdersComponent onOrderTransitionConfirmationResult result', res);
            if (res.errorCode === '0') {

              let lang = I18nEventBus.getI18nEventBus().current();
              var _sub2:any = this._customerorderService.getOrderById(lang, this.selectedCustomerorder.customerorderId).subscribe((customerorder:CustomerOrderVO) => {
                console.debug('AllCustomerOrdersComponent getOrderById', customerorder);

                if (this.customerorderEdit != null && this.customerorderEdit.customerorderId == this.selectedCustomerorder.customerorderId) {
                  // We are editing
                  this.customerorderEdit = customerorder;
                }

                let idx = this.customerorders.findIndex(order => {
                  return order.customerorderId === customerorder.customerorderId;
                });

                if (idx != -1) {
                  this.customerorders[idx] = customerorder;
                } else {
                  this.customerorders.splice(0, 0, customerorder);
                  idx = 0;
                }
                this.selectedCustomerorder = this.customerorders[idx];
                this.customerorders = this.customerorders.slice(0, this.customerorders.length); // hack to retrigger change

                let delivery:CustomerOrderDeliveryInfoVO = null;
                if (this.selectedCustomerorder != null && this.selectedCustomerdelivery != null) {
                  console.debug('AllCustomerOrdersComponent trying to re-select delivery', this.selectedCustomerdelivery);
                  let idx2 = customerorder.deliveries.findIndex(delivery => {
                    return delivery.deliveryNum == this.selectedCustomerdelivery.deliveryNum;
                  });
                  if (idx2 != -1) {
                    delivery = customerorder.deliveries[idx2];
                    console.debug('AllCustomerOrdersComponent re-selected delivery', delivery);
                  }
                }
                this.onCustomerdeliverySelected(delivery);

                this.changed = false;
                this.validForSave = false;
                _sub2.unsubscribe();
              });

            } else {
              ErrorEventBus.getErrorEventBus().emit({ status: 500, message: res.errorCode, key: res.localizationKey, param: res.localizedMessageParameters });
            }
            _sub.unsubscribe();
          });


      }
    }
  }



  protected onUpdateDeliveryRefSelected() {
    if (this.selectedCustomerdelivery != null) {
      console.debug('AllCustomerOrdersComponent onUpdateDeliveryRefSelected handler', this.selectedCustomerdelivery);

      this.orderTransitionMessage = null;
      this.orderTransitionRequiresMessage = true;
      this.orderTransitionName = 'update.external.ref';
      this.orderTransitionNameOfflineNote = false;
      this.orderTransitionValid = false;

      this.orderTransitionConfirmationModalDialog.show();
    }
  }

  protected onPackDeliverySelected() {
    if (this.selectedCustomerdelivery != null) {
      console.debug('AllCustomerOrdersComponent onPackDeliverySelected handler', this.selectedCustomerdelivery);

      this.orderTransitionMessage = null;
      this.orderTransitionRequiresMessage = false;
      this.orderTransitionName = 'pack.delivery';
      this.orderTransitionNameOfflineNote = false;
      this.orderTransitionValid = true;

      this.orderTransitionConfirmationModalDialog.show();
    }
  }

  protected onMarkReadyForShippingDeliverySelected() {
    if (this.selectedCustomerdelivery != null) {
      console.debug('AllCustomerOrdersComponent onMarkReadyForShippingDeliverySelected handler', this.selectedCustomerdelivery);

      this.orderTransitionMessage = null;
      this.orderTransitionRequiresMessage = false;
      this.orderTransitionName = 'mark.ready.for.shipment';
      this.orderTransitionNameOfflineNote = false;
      this.orderTransitionValid = true;

      this.orderTransitionConfirmationModalDialog.show();
    }
  }

  protected onStartDeliverySelected() {
    if (this.selectedCustomerdelivery != null) {
      console.debug('AllCustomerOrdersComponent onStartDeliverySelected handler', this.selectedCustomerdelivery);

      this.orderTransitionMessage = null;
      this.orderTransitionRequiresMessage = false;
      this.orderTransitionName = 'start.shipment';
      this.orderTransitionNameOfflineNote = false;
      this.orderTransitionValid = true;

      this.orderTransitionConfirmationModalDialog.show();
    }
  }

  protected onShipDeliveryManualSelected() {
    if (this.selectedCustomerdelivery != null) {
      console.debug('AllCustomerOrdersComponent onShipDeliveryManualSelected handler', this.selectedCustomerdelivery);

      this.orderTransitionMessage = null;
      this.orderTransitionRequiresMessage = true;
      this.orderTransitionName = 'start.shipment.manual.payment';
      this.orderTransitionNameOfflineNote = true;
      this.orderTransitionValid = false;

      this.orderTransitionConfirmationModalDialog.show();
    }
  }


  protected onMarkShippedDeliverySelected() {
    if (this.selectedCustomerdelivery != null) {
      console.debug('AllCustomerOrdersComponent onMarkShippedDeliverySelected handler', this.selectedCustomerdelivery);

      this.orderTransitionMessage = null;
      this.orderTransitionRequiresMessage = false;
      this.orderTransitionName = 'mark.shipped';
      this.orderTransitionNameOfflineNote = true;
      this.orderTransitionValid = true;

      this.orderTransitionConfirmationModalDialog.show();
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
