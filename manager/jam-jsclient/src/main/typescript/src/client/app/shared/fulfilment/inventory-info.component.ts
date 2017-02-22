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
import { Component, OnInit, Input, ViewChild } from '@angular/core';
import { CustomerOrderService, I18nEventBus } from './../services/index';
import { CustomerOrderInfoVO } from './../model/index';
import { ModalComponent, ModalResult } from './../modal/index';
import { Config } from './../config/env.config';
import { LogUtil } from './../log/index';

@Component({
  selector: 'yc-inventory-info',
  moduleId: module.id,
  templateUrl: 'inventory-info.component.html',
})

/**
 * Manage categories assigned to shop.
 */
export class InventoryInfoComponent implements OnInit {

  @ViewChild('infoModalDialog')
  private infoModalDialog:ModalComponent;

  private _skuCode:string;

  private orders:Array<CustomerOrderInfoVO> = [];

  private filterCap:number = Config.UI_FILTER_CAP;
  private customerorderFilterCapped:boolean = false;

  private loading:boolean = false;

  /**
   * Construct panel.
   * @param _customerOrderService
   */
  constructor(private _customerOrderService:CustomerOrderService) {
    LogUtil.debug('InventoryInfoComponent constructed');
  }

  @Input()
  set skuCode(skuCode:string) {
    this._skuCode = skuCode;
  }

  get skuCode():string {
    return this._skuCode;
  }

  ngOnInit() {
    LogUtil.debug('InventoryInfoComponent ngOnInit');
  }


  public showDialog() {
    LogUtil.debug('InventoryInfoComponent showDialog');
    this.loadData();
    this.infoModalDialog.show();
  }


  protected onConfirmationResult(modalresult: ModalResult) {
    LogUtil.debug('InventoryInfoComponent onSelectConfirmationResult modal result is ', modalresult);
  }

  /**
   * Load data and adapt time.
   */
  private loadData() {
    LogUtil.debug('InventoryInfoComponent loading orders', this._skuCode);
    let lang = I18nEventBus.getI18nEventBus().current();
    this.loading = true;
    var _subc:any = this._customerOrderService.getFilteredOrders(lang, '!' + this._skuCode, [], this.filterCap).subscribe(
        info => {
          LogUtil.debug('InventoryInfoComponent all orders', info);
          this.orders = info;
          this.customerorderFilterCapped = this.orders.length >= this.filterCap;
          this.loading = false;
          _subc.unsubscribe();
      }
    );
  }

}
