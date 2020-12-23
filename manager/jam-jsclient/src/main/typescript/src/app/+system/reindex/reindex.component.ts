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
import { ShopVO, JobStatusVO } from './../../shared/model/index';
import { ModalComponent, ModalResult, ModalAction } from './../../shared/modal/index';
import { SystemService, UserEventBus } from './../../shared/services/index';
import { Futures, Future } from './../../shared/event/index';
import { Config } from './../../../environments/environment';
import { LogUtil } from './../../shared/log/index';

@Component({
  selector: 'cw-reindex',
  templateUrl: 'reindex.component.html',
})

export class ReindexComponent implements OnInit, OnDestroy {

  private static _selectedShop:ShopVO;

  private static _jobStatus:JobStatusVO;
  private static _jobRunning:boolean = false;
  private static _jobCompleted:boolean = false;
  private static _lastReport:string = '';

  public shopSelection:ShopVO;
  public selectedShopCode:string;

  @ViewChild('selectShopModalDialog')
  private selectShopModalDialog:ModalComponent;

  public delayedUpdate:Future;
  private _delayedFilteringMs:number = Config.UI_BULKSERVICE_DELAY;


  /**
   * Construct shop attribute panel
   *
   * @param _systemService system service
   */
  constructor(private _systemService:SystemService) {
    LogUtil.debug('ReindexComponent constructed');

  }


  public get selectedShop():ShopVO {
    return ReindexComponent._selectedShop;
  }

  public set selectedShop(value:ShopVO) {
    ReindexComponent._selectedShop = value;
    this.selectedShopCode = value != null ? value.code : '';
  }

  public get jobStatus():JobStatusVO {
    return ReindexComponent._jobStatus;
  }

  public set jobStatus(value:JobStatusVO) {
    ReindexComponent._jobStatus = value;
  }

  public get jobRunning():boolean {
    return ReindexComponent._jobRunning;
  }

  public set jobRunning(value:boolean) {
    ReindexComponent._jobRunning = value;
  }

  public get jobCompleted():boolean {
    return ReindexComponent._jobCompleted;
  }

  public set jobCompleted(value:boolean) {
    ReindexComponent._jobCompleted = value;
  }

  public get lastReport():string {
    return ReindexComponent._lastReport;
  }

  public set lastReport(value:string) {
    ReindexComponent._lastReport = value;
  }

  public get delayedFilteringMs():number {
    return this._delayedFilteringMs;
  }

  ngOnInit() {
    LogUtil.debug('ReindexComponent ngOnInit');
    let that = this;
    this.delayedUpdate = Futures.perpetual(function() {
      that.getStatusUpdate();
    }, this._delayedFilteringMs);
    if (this.jobRunning) {
      LogUtil.debug('ReindexComponent ngOnInit resuming updates');
      this.getStatusUpdate();
    }
  }

  ngOnDestroy() {
    LogUtil.debug('ReindexComponent ngOnDestroy');
    this.delayedUpdate.cancel();
  }


  onShopSelect() {
    if (!this.jobRunning) {
      LogUtil.debug('ShopPriceListComponent onShopSelect');
      this.selectShopModalDialog.show();
    }
  }

  onShopSelected(event:ShopVO) {
    LogUtil.debug('ShopPriceListComponent onShopSelected');
    this.shopSelection = event;
  }

  onSelectShopResult(modalresult: ModalResult) {
    LogUtil.debug('ShopPriceListComponent onSelectShopResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action && this.shopSelection != null) {
      this.selectedShop = this.shopSelection;
    }
  }


  selectShop(shop:ShopVO) {
    if (!this.jobRunning) {
      this.selectedShop = shop;
    }
  }

  onRefreshResults() {
    LogUtil.debug('ReindexComponent onRefreshResults');
    this.jobStatus = null;
    this.jobRunning = false;
    this.jobCompleted = false;
  }

  onReindexAll() {
    LogUtil.debug('ReindexComponent onReindexAll');
    if (!this.jobRunning) {
      this.jobRunning = true;
      this._systemService.reindexAllProducts().subscribe(status => {
        LogUtil.debug('ReindexComponent onReindexAll', status);
        this.jobStatus = status;
        this.lastReport = this.jobStatus.report;
        this.jobCompleted = this.jobStatus.completion != null;
        if (!this.jobCompleted) {
          this.delayedUpdate.delay();
        } else {
          this.jobRunning = false;
        }
      });
    }
  }


  onReindexOne() {
    LogUtil.debug('ReindexComponent onReindexOne', this.selectedShop);
    if (!this.jobRunning && this.selectedShop != null) {
      this.jobRunning = true;
      this._systemService.reindexShopProducts(this.selectedShop.shopId).subscribe(status => {
        LogUtil.debug('ReindexComponent onReindexOne', status);
        this.jobStatus = status;
        this.lastReport = this.jobStatus.report;
        this.jobCompleted = this.jobStatus.completion != null;
        if (!this.jobCompleted) {
          this.delayedUpdate.delay();
        } else {
          this.jobRunning = false;
        }
      });
    }
  }


  getStatusUpdate() {
    LogUtil.debug('ReindexComponent getStatusUpdate before', this.jobStatus);
    if (UserEventBus.getUserEventBus().current() != null) {
      if (!this.jobCompleted && this.jobStatus != null) {
        this.jobRunning = true;
        this._systemService.getIndexJobStatus(this.jobStatus.token).subscribe(status => {
          LogUtil.debug('ReindexComponent getStatusUpdate after', status);
          this.jobStatus = status;
          this.lastReport = this.jobStatus.report;
          this.jobCompleted = this.jobStatus.completion != null;
          if (!this.jobCompleted && this.jobStatus != null) {
            this.delayedUpdate.delay();
          } else {
            this.jobRunning = false;
          }
        });
      }
    }
  }

}
