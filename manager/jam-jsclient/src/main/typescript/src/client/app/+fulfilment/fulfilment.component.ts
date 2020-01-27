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
import { Component, OnInit, OnDestroy, ViewChild } from '@angular/core';
import { ShopEventBus, FulfilmentService, UserEventBus } from './../shared/services/index';
import { ModalComponent, ModalResult, ModalAction } from './../shared/modal/index';
import { FulfilmentCentreInfoVO, FulfilmentCentreVO, ShopVO, Pair, SearchResultVO } from './../shared/model/index';
import { FormValidationEvent, Futures, Future } from './../shared/event/index';
import { Config } from './../shared/config/env.config';
import { LogUtil } from './../shared/log/index';

@Component({
  selector: 'yc-fulfilment',
  moduleId: module.id,
  templateUrl: 'fulfilment.component.html',
})

export class FulfilmentComponent implements OnInit, OnDestroy {

  private static CENTRES:string = 'centres';
  private static CENTRE:string = 'centre';

  private viewMode:string = FulfilmentComponent.CENTRES;

  private centres:SearchResultVO<FulfilmentCentreInfoVO>;
  private centreFilter:string;

  private delayedFiltering:Future;
  private delayedFilteringMs:number = Config.UI_INPUT_DELAY;

  private selectedCentre:FulfilmentCentreInfoVO;

  private centreEdit:FulfilmentCentreVO;

  @ViewChild('deleteConfirmationModalDialog')
  private deleteConfirmationModalDialog:ModalComponent;

  private shops:Array<ShopVO> = [];

  private deleteValue:String;

  private shopAllSub:any;

  private loading:boolean = false;

  private changed:boolean = false;
  private validForSave:boolean = false;

  constructor(private _fulfilmentService:FulfilmentService) {
    LogUtil.debug('FulfilmentComponent constructed');
    this.shopAllSub = ShopEventBus.getShopEventBus().shopsUpdated$.subscribe(shopsevt => {
      this.shops = shopsevt;
    });
    this.centres = this.newSearchResultInstance();
  }

  newCentreInstance():FulfilmentCentreVO {
    return {
      warehouseId: 0, code: '', name: '', description: null,
      countryCode: null, stateCode: null, city: null, postcode: null,
      defaultStandardStockLeadTime: 0, defaultBackorderStockLeadTime: 0, multipleShippingSupported: false,
      displayNames: [], fulfilmentShops: []
    };
  }

  newSearchResultInstance():SearchResultVO<FulfilmentCentreInfoVO> {
    return {
      searchContext: {
        parameters: {
          filter: []
        },
        start: 0,
        size: Config.UI_TABLE_PAGE_SIZE,
        sortBy: null,
        sortDesc: false
      },
      items: [],
      total: 0
    };
  }

  ngOnInit() {
    LogUtil.debug('FulfilmentComponent ngOnInit');
    this.onRefreshHandler();
    let that = this;
    this.delayedFiltering = Futures.perpetual(function() {
      that.getFilteredCentres();
    }, this.delayedFilteringMs);

  }

  ngOnDestroy() {
    LogUtil.debug('FulfilmentComponent ngOnDestroy');
    if (this.shopAllSub) {
      this.shopAllSub.unsubscribe();
    }
  }


  protected onFilterChange(event:any) {
    this.centres.searchContext.start = 0; // changing filter means we need to start from first page
    this.delayedFiltering.delay();
  }

  protected onRefreshHandler() {
    LogUtil.debug('FulfilmentComponent refresh handler');
    if (UserEventBus.getUserEventBus().current() != null) {
      this.getFilteredCentres();
    }
  }

  protected onPageSelected(page:number) {
    LogUtil.debug('FulfilmentComponent onPageSelected', page);
    this.centres.searchContext.start = page;
    this.delayedFiltering.delay();
  }

  protected onSortSelected(sort:Pair<string, boolean>) {
    LogUtil.debug('FulfilmentComponent ononSortSelected', sort);
    if (sort == null) {
      this.centres.searchContext.sortBy = null;
      this.centres.searchContext.sortDesc = false;
    } else {
      this.centres.searchContext.sortBy = sort.first;
      this.centres.searchContext.sortDesc = sort.second;
    }
    this.delayedFiltering.delay();
  }

  protected onCentreSelected(data:FulfilmentCentreInfoVO) {
    LogUtil.debug('FulfilmentComponent onCentreSelected', data);
    this.selectedCentre = data;
  }

  protected onCentreChanged(event:FormValidationEvent<FulfilmentCentreVO>) {
    LogUtil.debug('FulfilmentComponent onCentreChanged', event);
    this.changed = true;
    this.validForSave = event.valid;
    this.centreEdit = event.source;
  }

  protected onBackToList() {
    LogUtil.debug('FulfilmentComponent onBackToList handler');
    if (this.viewMode === FulfilmentComponent.CENTRE) {
      this.centreEdit = null;
      this.viewMode = FulfilmentComponent.CENTRES;
    }
  }

  protected onRowNew() {
    LogUtil.debug('FulfilmentComponent onRowNew handler');
    this.changed = false;
    this.validForSave = false;
    if (this.viewMode === FulfilmentComponent.CENTRES) {
      this.centreEdit = this.newCentreInstance();
      this.viewMode = FulfilmentComponent.CENTRE;
    }
  }

  protected onRowDelete(row:any) {
    LogUtil.debug('FulfilmentComponent onRowDelete handler', row);
    this.deleteValue = row.name;
    this.deleteConfirmationModalDialog.show();
  }

  protected onRowDeleteSelected() {
    if (this.selectedCentre != null) {
      this.onRowDelete(this.selectedCentre);
    }
  }


  protected onRowEditCentre(row:FulfilmentCentreInfoVO) {
    LogUtil.debug('FulfilmentComponent onRowEditCentre handler', row);
    this.loading = true;
    let _sub:any = this._fulfilmentService.getFulfilmentCentreById(row.warehouseId).subscribe(res => {
      LogUtil.debug('FulfilmentComponent getFulfilmentCentreById', res);
      this.centreEdit = res;
      this.changed = false;
      this.validForSave = false;
      this.viewMode = FulfilmentComponent.CENTRE;
      this.loading = false;
      _sub.unsubscribe();
    });
  }

  protected onRowEditSelected() {
    this.onRowEditCentre(this.selectedCentre);
  }

  protected onSaveHandler() {

    if (this.validForSave && this.changed) {

      if (this.centreEdit != null) {

        LogUtil.debug('FulfilmentComponent Save handler centre', this.centreEdit);

        this.loading = true;
        let _sub:any = this._fulfilmentService.saveFulfilmentCentre(this.centreEdit).subscribe(
            rez => {
              _sub.unsubscribe();
              LogUtil.debug('FulfilmentComponent centre changed', rez);
              this.changed = false;
              this.selectedCentre = rez;
              this.centreEdit = null;
              this.loading = false;
              this.viewMode = FulfilmentComponent.CENTRES;
              this.getFilteredCentres();
          }
        );
      }

    }

  }

  protected onDiscardEventHandler() {
    LogUtil.debug('FulfilmentComponent discard handler');
    if (this.viewMode === FulfilmentComponent.CENTRE) {
      if (this.selectedCentre != null) {
        this.onRowEditSelected();
      } else {
        this.onRowNew();
      }
    }
  }

  protected onDeleteConfirmationResult(modalresult: ModalResult) {
    LogUtil.debug('FulfilmentComponent onDeleteConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {

     if (this.selectedCentre != null) {
        LogUtil.debug('FulfilmentComponent onDeleteConfirmationResult', this.selectedCentre);

       this.loading = true;
        let _sub:any = this._fulfilmentService.removeFulfilmentCentre(this.selectedCentre).subscribe(res => {
          _sub.unsubscribe();
          LogUtil.debug('FulfilmentComponent removeCentre', this.selectedCentre);
          this.selectedCentre = null;
          this.centreEdit = null;
          this.loading = false;
          this.getFilteredCentres();
        });
      }
    }
  }

  protected onClearFilterCentre() {

    this.centreFilter = '';
    this.getFilteredCentres();

  }

  private getFilteredCentres() {

    this.loading = true;

    this.centres.searchContext.parameters.filter = [ this.centreFilter ];
    this.centres.searchContext.size = Config.UI_TABLE_PAGE_SIZE;

    let _sub:any = this._fulfilmentService.getFilteredFulfilmentCentres(this.centres.searchContext).subscribe( allcentres => {
      LogUtil.debug('FulfilmentComponent getAllCentres', allcentres);
      this.centres = allcentres;
      this.selectedCentre = null;
      this.centreEdit = null;
      this.viewMode = FulfilmentComponent.CENTRES;
      this.changed = false;
      this.validForSave = false;
      this.loading = false;
      _sub.unsubscribe();
    });
  }

}
