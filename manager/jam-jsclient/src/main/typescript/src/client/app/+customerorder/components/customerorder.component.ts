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
import { Component, OnInit, OnDestroy, Input, Output, EventEmitter } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { CustomerOrderVO, CustomerOrderDeliveryInfoVO, CustomerOrderLineVO, PromotionVO, Pair } from './../../shared/model/index';
import { LogUtil } from './../../shared/log/index';

@Component({
  selector: 'yc-customerorder',
  moduleId: module.id,
  templateUrl: 'customerorder.component.html',
})

export class CustomerOrderComponent implements OnInit, OnDestroy {

  @Output() dataSelected: EventEmitter<CustomerOrderDeliveryInfoVO> = new EventEmitter<CustomerOrderDeliveryInfoVO>();

  private _customerorder:CustomerOrderVO;
  private _promotions:any = {};

  private selectedDelivery:CustomerOrderDeliveryInfoVO;
  private selectedLine:CustomerOrderLineVO;

  private changed:boolean = false;

  private  deliveryActionsAvailable:boolean = false;

  constructor(fb: FormBuilder) {
    LogUtil.debug('CustomerOrderComponent constructed');
  }

  @Input()
  set customerorder(customerorder:CustomerOrderVO) {

    let _availableActions = false;

    this._customerorder = customerorder;
    if (this._customerorder) {
      this._customerorder.promotions.forEach(promo => {
        this._promotions[promo.code] = promo;
      });
      if (this._customerorder.deliveries) {
        this._customerorder.deliveries.forEach(del => {
          if (this.isDeliveryHasNextOption(del)) {
            _availableActions = true;
          }
        });
      }
    } else {
      this._promotions = {};
    }
    this.selectedLine = null;
    this.selectedDelivery = null;
    this.changed = false;

    this.deliveryActionsAvailable = _availableActions;
  }

  get customerorder():CustomerOrderVO {
    return this._customerorder;
  }

  ngOnInit() {
    LogUtil.debug('CustomerOrderComponent ngOnInit');
  }

  ngOnDestroy() {
    LogUtil.debug('CustomerOrderComponent ngOnDestroy');
  }

  tabSelected(tab:any) {
    LogUtil.debug('CustomerOrderComponent tabSelected', tab);
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

  getLineCost(row:CustomerOrderLineVO):number {

    if (row.allValues != null) {
      let idx = row.allValues.findIndex(val => {
        return val.first === 'ItemCostPrice';
      });
      if (idx != -1) {
        return +(row.allValues[idx].second.first);
      }
    }
    return 0;
  }

  getOrderListPriceFlags(order:CustomerOrderVO):string {

    let flags = '';
    if (order.price < order.listPrice) {
      // sale
      flags += '<i title="' + order.listPrice.toFixed(2) + '" class="fa fa-tag"></i>&nbsp;';
    }

    return flags;
  }

  getCustomValuesExclude(allValues: Pair<string, Pair<string, string>>[], exclude:string[]):Pair<string, Pair<string, string>>[] {
    let vals:Pair<string, Pair<string, string>>[] = [];
    allValues.forEach(_pair => {
      if (_pair.second.second == null || exclude.indexOf(_pair.second.second) == -1) {
        vals.push(_pair);
      }
    });
    return vals;
  }

  getCustomValuesInclude(allValues: Pair<string, Pair<string, string>>[], include:string[]):Pair<string, Pair<string, string>>[] {
    let vals:Pair<string, Pair<string, string>>[] = [];
    allValues.forEach(_pair => {
      if (_pair.second.second != null && include.indexOf(_pair.second.second) != -1) {
        vals.push(_pair);
      }
    });
    return vals;
  }

  getPromotions(codes:string[]):PromotionVO[] {
    let promos:PromotionVO[] = [];
    if (codes != null) {
      codes.forEach(code => {
        let promo = this._promotions[code];
        if (promo != null) {
          promos.push(promo);
        } else {
          promos.push({
            promotionId : 0,
            code : code, shopCode : null, currency : null, rank : 0,
            name : code, description : null,
            displayNames : [], displayDescriptions : [],
            promoType : null, promoAction : null, eligibilityCondition : null, promoActionContext : null,
            couponTriggered : false, canBeCombined : false, enabled : false,
            enabledFrom : null, enabledTo : null, tag : null
          });
        }
      });
    }
    LogUtil.debug('CustomerOrderComponent getPromotions', codes, promos);
    return promos;
  }

  getFormattedAddress(address:string) {
    return address.replace('\r\n', '<br/>').replace('\r', '<br/>').replace('\n', '<br/>');
  }

  protected getUserIcon(row:CustomerOrderVO) {
    if (row.customerId > 0) {
      return '<i class="fa fa-user"></i>';
    }
    return '';
  }

  protected isOrderType() {

    return this._customerorder.orderStatus != null && this._customerorder.orderStatus.indexOf('os.') == 0;

  }

  protected isLineDeliveryHasNext(row:CustomerOrderLineVO) {
    let delivery = this._customerorder.deliveries.find(delivery => {
      return delivery.deliveryNum == row.deliveryNum;
    });
    return this.isDeliveryHasNextOption(delivery);
  }

  protected isDeliveryHasNextOption(row:CustomerOrderDeliveryInfoVO) {
    return row != null && row.deliveryStatusNextOptions != null && row.deliveryStatusNextOptions.length > 0;
  }


  protected onSelectLineRow(row:CustomerOrderLineVO) {
    LogUtil.debug('CustomerOrdersComponent onSelectLineRow handler', row);
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
    LogUtil.debug('CustomerOrdersComponent onSelectDeliveryRow handler', row);
    if (this.selectedDelivery != row && this.isDeliveryHasNextOption(row)) {
      this.selectedDelivery = row;
    } else {
      this.selectedDelivery = null;
    }
    this.selectedLine = null;
    this.dataSelected.emit(this.selectedDelivery);
  }

  protected getInvoiceNumbers():Pair<string, Date>[] {
    let invoiceNumbers:string[] = [];
    let invoiceNumbersAndDate:Pair<string, Date>[] = [];

    if (this._customerorder != null) {

      this._customerorder.lines.forEach( _line => {

        if (_line.supplierInvoiceNo != null) {

          if (!invoiceNumbers.includes(_line.supplierInvoiceNo)) {
            invoiceNumbersAndDate.push({ first: _line.supplierInvoiceNo, second: _line.supplierInvoiceDate });
            invoiceNumbers.push(_line.supplierInvoiceNo);
          }

        }

      });

    }

    return invoiceNumbersAndDate;
  }


}
