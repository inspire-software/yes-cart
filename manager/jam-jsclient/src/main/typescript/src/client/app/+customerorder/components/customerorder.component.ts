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
import {Component, OnInit, OnDestroy, Input, Output, ViewChild, EventEmitter} from '@angular/core';
import {NgIf} from '@angular/common';
import {FormBuilder, Validators, REACTIVE_FORM_DIRECTIVES} from '@angular/forms';
import {YcValidators} from './../../shared/validation/validators';
import {CustomerOrderVO, CustomerOrderDeliveryInfoVO, CustomerOrderLineVO, PromotionVO} from './../../shared/model/index';
import {FormValidationEvent, Futures, Future} from './../../shared/event/index';
import {AttributeValuesComponent} from './../../shared/attributes/index';
import {TAB_DIRECTIVES} from 'ng2-bootstrap/ng2-bootstrap';


@Component({
  selector: 'yc-customerorder',
  moduleId: module.id,
  templateUrl: 'customerorder.component.html',
  directives: [NgIf, REACTIVE_FORM_DIRECTIVES, TAB_DIRECTIVES, AttributeValuesComponent],
})

export class CustomerOrderComponent implements OnInit, OnDestroy {

  _customerorder:CustomerOrderVO;
  _promotions:any = {};

  selectedDelivery:CustomerOrderDeliveryInfoVO;
  selectedLine:CustomerOrderLineVO;

  changed:boolean = false;
  validForSave:boolean = false;

  @Output() dataSelected: EventEmitter<CustomerOrderDeliveryInfoVO> = new EventEmitter<CustomerOrderDeliveryInfoVO>();

  constructor(fb: FormBuilder) {
    console.debug('CustomerOrderComponent constructed');
  }

  @Input()
  set customerorder(customerorder:CustomerOrderVO) {
    this._customerorder = customerorder;
    if (this._customerorder) {
      this._customerorder.promotions.forEach(promo => {
        this._promotions[promo.code] = promo;
      });
    } else {
      this._promotions = {};
    }
    this.selectedLine = null;
    this.selectedDelivery = null;
    this.changed = false;
  }

  get customerorder():CustomerOrderVO {
    return this._customerorder;
  }

  ngOnInit() {
    console.debug('CustomerOrderComponent ngOnInit');
  }

  ngOnDestroy() {
    console.debug('CustomerOrderComponent ngOnDestroy');
  }

  tabSelected(tab:any) {
    console.debug('CustomerOrderComponent tabSelected', tab);
  }

  getLinePriceFlags(row:CustomerOrderLineVO):string {

    let flags = '';
    if (row.price < row.salePrice) {
      // promotion
      flags += '<i title="' + row.salePrice.toFixed(2) + '" class="fa fa-dollar"></i>&nbsp;';
    }

    if (row.salePrice < row.listPrice) {
      // sale
      flags += '<i title="' + row.listPrice.toFixed(2) + '" class="fa fa-tag"></i>&nbsp;';
    }

    return flags;
  }

  getOrderListPriceFlags(order:CustomerOrderVO):string {

    let flags = '';
    if (order.price < order.listPrice) {
      // sale
      flags += '<i title="' + order.listPrice.toFixed(2) + '" class="fa fa-tag"></i>&nbsp;';
    }

    return flags;
  }

  getPromotions(codes:string[]):PromotionVO[] {
    let promos:PromotionVO[] = [];
    if (codes != null) {
      codes.forEach(code => {
        promos.push(this._promotions[code]);
      });
    }
    console.debug('CustomerOrderComponent getPromotions', codes, promos);
    return promos;
  }

  getFormattedAddress(address:string) {
    return address.replace("\r\n", "<br/>").replace("\r", "<br/>").replace("\n", "<br/>")
  }

  protected getUserIcon(row:CustomerOrderVO) {
    if (row.customerId > 0) {
      return '<i class="fa fa-user"></i>';
    }
    return '';
  }

  protected isLineDeliveryHasNext(row:CustomerOrderLineVO) {
    let delivery = this._customerorder.deliveries.find(delivery => {
      return delivery.deliveryNum == row.deliveryNum;
    });
    return this.isDeliveryHasNextOption(delivery);
  }

  protected isDeliveryHasNextOption(row:CustomerOrderDeliveryInfoVO) {
    return row.deliveryStatusNextOptions != null && row.deliveryStatusNextOptions.length > 0;
  }


  protected onSelectLineRow(row:CustomerOrderLineVO) {
    console.debug('CustomerOrdersComponent onSelectLineRow handler', row);
    let delivery = this._customerorder.deliveries.find(delivery => {
      return delivery.deliveryNum == row.deliveryNum;
    });

    if (this.selectedLine != row && delivery != null && this.isDeliveryHasNextOption(delivery)) {
      this.selectedLine = row;
      this.selectedDelivery = delivery;
    } else {
      this.selectedLine = null;
      this.selectedDelivery = null;
    }
    this.dataSelected.emit(this.selectedDelivery);
  }

  protected onSelectDeliveryRow(row:CustomerOrderDeliveryInfoVO) {
    console.debug('CustomerOrdersComponent onSelectDeliveryRow handler', row);
    if (this.selectedDelivery != row && this.isDeliveryHasNextOption(row)) {
      this.selectedDelivery = row;
    } else {
      this.selectedDelivery = null;
    }
    this.selectedLine = null;
    this.dataSelected.emit(this.selectedDelivery);
  }


}
