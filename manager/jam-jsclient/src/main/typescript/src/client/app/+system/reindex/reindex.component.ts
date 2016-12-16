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
import { Component, OnInit, OnDestroy } from '@angular/core';
import { ShopVO, JobStatusVO } from './../../shared/model/index';
import { SystemService } from './../../shared/services/index';
import { Futures, Future } from './../../shared/event/index';
import { Config } from './../../shared/config/env.config';
import { LogUtil } from './../../shared/log/index';

@Component({
  selector: 'yc-reindex',
  moduleId: module.id,
  templateUrl: 'reindex.component.html',
})

export class ReindexComponent implements OnInit, OnDestroy {

  private static _selectedShop:ShopVO;

  private static _jobStatus:JobStatusVO;
  private static _jobRunning:boolean = false;
  private static _jobCompleted:boolean = false;
  private static _lastReport:string = '';

  private delayedUpdate:Future;
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

  protected selectShop(shop:ShopVO) {
    if (!this.jobRunning) {
      this.selectedShop = shop;
    }
  }

  protected onRefreshResults() {
    LogUtil.debug('ReindexComponent onRefreshResults');
    this.jobStatus = null;
    this.jobRunning = false;
    this.jobCompleted = false;
  }

  protected onReindexAll() {
    LogUtil.debug('ReindexComponent onReindexAll');
    if (!this.jobRunning) {
      this.jobRunning = true;
      var _sub:any = this._systemService.reindexAllProducts().subscribe(status => {
        LogUtil.debug('ReindexComponent onReindexAll', status);
        this.jobStatus = status;
        this.lastReport = this.jobStatus.report;
        this.jobCompleted = this.jobStatus.completion != null;
        if (!this.jobCompleted) {
          this.delayedUpdate.delay();
        } else {
          this.jobRunning = false;
        }
        _sub.unsubscribe();
      });
    }
  }


  protected onReindexOne() {
    LogUtil.debug('ReindexComponent onReindexOne', this.selectedShop);
    if (!this.jobRunning && this.selectedShop != null) {
      this.jobRunning = true;
      var _sub:any = this._systemService.reindexShopProducts(this.selectedShop.shopId).subscribe(status => {
        LogUtil.debug('ReindexComponent onReindexOne', status);
        this.jobStatus = status;
        this.lastReport = this.jobStatus.report;
        this.jobCompleted = this.jobStatus.completion != null;
        if (!this.jobCompleted) {
          this.delayedUpdate.delay();
        } else {
          this.jobRunning = false;
        }
        _sub.unsubscribe();
      });
    }
  }


  protected getStatusUpdate() {
    LogUtil.debug('ReindexComponent getStatusUpdate before', this.jobStatus);
    if (!this.jobCompleted && this.jobStatus != null) {
      this.jobRunning = true;
      var _sub:any = this._systemService.getIndexJobStatus(this.jobStatus.token).subscribe(status => {
        LogUtil.debug('ReindexComponent getStatusUpdate after', status);
        this.jobStatus = status;
        this.lastReport = this.jobStatus.report;
        this.jobCompleted = this.jobStatus.completion != null;
        if (!this.jobCompleted && this.jobStatus != null) {
          this.delayedUpdate.delay();
        } else {
          this.jobRunning = false;
        }
        _sub.unsubscribe();
      });
    }
  }

}
