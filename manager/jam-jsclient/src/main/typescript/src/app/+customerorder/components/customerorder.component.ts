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
import { Component, OnInit, OnDestroy, Input, Output, EventEmitter } from '@angular/core';
import { Router } from '@angular/router';
import { FormBuilder } from '@angular/forms';
import { I18nEventBus } from './../../shared/services/index';
import { UiUtil } from './../../shared/ui/index';
import { CustomerOrderVO, CustomerOrderDeliveryInfoVO, CustomerOrderLineVO, PromotionVO, AttrValueVO, Pair } from './../../shared/model/index';
import { StorageUtil } from './../../shared/storage/index';
import { Config } from './../../../environments/environment';
import { LogUtil } from './../../shared/log/index';

@Component({
  selector: 'cw-customerorder',
  templateUrl: 'customerorder.component.html',
})

export class CustomerOrderComponent implements OnInit, OnDestroy {

  @Output() dataSelected: EventEmitter<Pair<CustomerOrderDeliveryInfoVO, CustomerOrderLineVO>> = new EventEmitter<Pair<CustomerOrderDeliveryInfoVO, CustomerOrderLineVO>>();

  private _customerorder:CustomerOrderVO;
  private _promotions:any = {};

  public selectedDelivery:CustomerOrderDeliveryInfoVO;
  public selectedLine:CustomerOrderLineVO;

  public changed:boolean = false;

  public deliveryActionsAvailable:boolean = false;

  constructor(fb: FormBuilder,
              private _router : Router) {
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
      flags += '<i title="' + row.salePrice.toFixed(2) + '" class="fa fa-cart-arrow-down"></i>&nbsp;';
    }

    if (row.salePrice < row.listPrice) {
      // sale
      flags += '<i title="' + row.listPrice.toFixed(2) + '" class="fa fa-clock-o"></i>&nbsp;';
    }

    return flags;
  }

  getLineCost(row:CustomerOrderLineVO):number {

    if (row.allValues != null) {
      let idx = row.allValues.findIndex(val => {
        return val.attribute.code === 'ItemCostPrice';
      });
      if (idx != -1) {
        return +(row.allValues[idx].val);
      }
    }
    return 0;
  }

  getOrderListPriceFlags(order:CustomerOrderVO):string {

    let flags = '';
    if (order.price < order.listPrice) {
      // sale
      flags += '<i title="' + order.listPrice.toFixed(2) + '" class="fa fa-cart-arrow-down"></i>&nbsp;';
    }

    return flags;
  }

  getXXDisplayValue(val: AttrValueVO): string {
    if (val.displayVals != null) {
      let xxPair = val.displayVals.find(av => av.first == 'xx');
      if (xxPair) {
        return  xxPair.second;
      }
    }
    return null;
  }

  getCustomValuesExclude(allValues: AttrValueVO[], exclude:string[]):AttrValueVO[] {
    let vals:AttrValueVO[] = [];
    allValues.forEach(_val => {
      let _xxValue = this.getXXDisplayValue(_val);
      if (_xxValue == null || exclude.indexOf(_xxValue) == -1) {
        vals.push(_val);
      }
    });
    return vals;
  }

  getCustomValuesInclude(allValues: AttrValueVO[], include:string[]):AttrValueVO[] {
    let vals:AttrValueVO[] = [];
    allValues.forEach(_val => {
      let _xxValue = this.getXXDisplayValue(_val);
      if (_xxValue != null && include.indexOf(_xxValue) != -1) {
        vals.push(_val);
      }
    });
    return vals;
  }


  getAttributeName(attr:AttrValueVO):string {

    let lang = I18nEventBus.getI18nEventBus().current();
    let i18n = attr.attribute.displayNames;
    let def = attr.attribute.name != null ? attr.attribute.name : attr.attribute.code;

    return UiUtil.toI18nString(i18n, def, lang);

  }

  getDisplayValue(attr:AttrValueVO, useDefault:boolean = true):string {

    let lang = I18nEventBus.getI18nEventBus().current();
    let attrName = this.getAttributeName(attr);
    let i18n = attr.displayVals;
    let def = useDefault ? attrName + ': ' + attr.val : '';

    return UiUtil.toI18nString(i18n, def, lang);

  }

  isOfflineDeliveryCost(codes:string[]):boolean {
    return codes != null && codes.indexOf('#OFFLINE#') != -1;
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

  getUserIcon(row:CustomerOrderVO) {
    if (row.customerId > 0) {
      if (row.managedOrder) {
        return '<i class="fa fa-user-plus" title="' + row.managerName + ' / ' + row.managerEmail + '"></i>';
      }
      return '<i class="fa fa-user"></i>';
    }
    return '';
  }

  getUserNumber(row:CustomerOrderVO) {
    if (row.customerId > 0) {
      return ' (' + row.customerId + ')';
    }
    return '';
  }


  onCustomerClick(row:CustomerOrderVO) {
    LogUtil.debug('CustomerOrderComponent onCustomerClick', row);
    this._router.navigate(['/customer/allcustomers', row.customerId]);
  }


  isOrderType() {

    return this._customerorder.orderStatus != null && this._customerorder.orderStatus.indexOf('os.') == 0;

  }

  isLineDeliveryHasNext(row:CustomerOrderLineVO) {
    let delivery = this._customerorder.deliveries.find(delivery => {
      return delivery.deliveryNum == row.deliveryNum;
    });
    return this.isDeliveryHasNextOption(delivery);
  }

  isDeliveryHasNextOption(row:CustomerOrderDeliveryInfoVO) {
    return row != null && row.deliveryStatusNextOptions != null && row.deliveryStatusNextOptions.length > 0;
  }

  isLineHasNext(row:CustomerOrderLineVO) {
    return row.orderLineNextOptions != null && row.orderLineNextOptions.length > 0;
  }


  onSelectLineRow(row:CustomerOrderLineVO) {
    LogUtil.debug('CustomerOrdersComponent onSelectLineRow handler', row);
    let delivery = this._customerorder.deliveries.find(delivery => {
      return delivery.deliveryNum == row.deliveryNum;
    });

    if (this.selectedLine != row /* && delivery != null && this.isDeliveryHasNextOption(delivery) */) {
      this.selectedLine = row;
      this.selectedDelivery = delivery;
      this.dataSelected.emit({ first: this.selectedDelivery, second: this.selectedLine});
    } else {
      this.selectedLine = null;
      this.selectedDelivery = null;
      this.dataSelected.emit(null);
    }
  }

  onSelectDeliveryRow(row:CustomerOrderDeliveryInfoVO) {
    LogUtil.debug('CustomerOrdersComponent onSelectDeliveryRow handler', row);
    this.selectedLine = null;
    if (this.selectedDelivery != row /* && this.isDeliveryHasNextOption(row) */) {
      this.selectedDelivery = row;
      this.dataSelected.emit({ first: this.selectedDelivery, second: null });
    } else {
      this.selectedDelivery = null;
      this.dataSelected.emit(null);
    }
  }

  getInvoiceNumbers():Pair<string, Date>[] {
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
