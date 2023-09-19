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
import { ShopEventBus, FulfilmentService, UserEventBus } from './../shared/services/index';
import { ModalComponent, ModalResult, ModalAction } from './../shared/modal/index';
import { FulfilmentCentreInfoVO, FulfilmentCentreVO, ShopVO, Pair, SearchResultVO } from './../shared/model/index';
import { FormValidationEvent, Futures, Future } from './../shared/event/index';
import { Config } from './../../environments/environment';
import { LogUtil } from './../shared/log/index';

@Component({
  selector: 'cw-fulfilment',
  templateUrl: 'fulfilment.component.html',
})

export class FulfilmentComponent implements OnInit, OnDestroy {

  private static CENTRES:string = 'centres';
  private static CENTRE:string = 'centre';

  public viewMode:string = FulfilmentComponent.CENTRES;

  public centres:SearchResultVO<FulfilmentCentreInfoVO>;
  public centreFilter:string;

  private delayedFiltering:Future;
  private delayedFilteringMs:number = Config.UI_INPUT_DELAY;

  public selectedCentre:FulfilmentCentreInfoVO;

  public centreEdit:FulfilmentCentreVO;

  @ViewChild('deleteConfirmationModalDialog')
  private deleteConfirmationModalDialog:ModalComponent;

  public shops:Array<ShopVO> = [];

  public deleteValue:String;

  private shopAllSub:any;

  public loading:boolean = false;

  public changed:boolean = false;
  public validForSave:boolean = false;

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
      defaultStandardStockLeadTime: 0, defaultBackorderStockLeadTime: 0, multipleShippingSupported: false, forceBackorderDeliverySplit: false,
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


  onFilterChange(event:any) {
    this.centres.searchContext.start = 0; // changing filter means we need to start from first page
    this.delayedFiltering.delay();
  }

  onRefreshHandler() {
    LogUtil.debug('FulfilmentComponent refresh handler');
    if (UserEventBus.getUserEventBus().current() != null) {
      this.getFilteredCentres();
    }
  }

  onPageSelected(page:number) {
    LogUtil.debug('FulfilmentComponent onPageSelected', page);
    this.centres.searchContext.start = page;
    this.delayedFiltering.delay();
  }

  onSortSelected(sort:Pair<string, boolean>) {
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

  onCentreSelected(data:FulfilmentCentreInfoVO) {
    LogUtil.debug('FulfilmentComponent onCentreSelected', data);
    this.selectedCentre = data;
  }

  onCentreChanged(event:FormValidationEvent<FulfilmentCentreVO>) {
    LogUtil.debug('FulfilmentComponent onCentreChanged', event);
    this.changed = true;
    this.validForSave = event.valid;
    this.centreEdit = event.source;
  }

  onBackToList() {
    LogUtil.debug('FulfilmentComponent onBackToList handler');
    if (this.viewMode === FulfilmentComponent.CENTRE) {
      this.centreEdit = null;
      this.viewMode = FulfilmentComponent.CENTRES;
    }
  }

  onRowNew() {
    LogUtil.debug('FulfilmentComponent onRowNew handler');
    this.changed = false;
    this.validForSave = false;
    if (this.viewMode === FulfilmentComponent.CENTRES) {
      this.centreEdit = this.newCentreInstance();
      this.viewMode = FulfilmentComponent.CENTRE;
    }
  }

  onRowDelete(row:any) {
    LogUtil.debug('FulfilmentComponent onRowDelete handler', row);
    this.deleteValue = row.name;
    this.deleteConfirmationModalDialog.show();
  }

  onRowDeleteSelected() {
    if (this.selectedCentre != null) {
      this.onRowDelete(this.selectedCentre);
    }
  }


  onRowEditCentre(row:FulfilmentCentreInfoVO) {
    LogUtil.debug('FulfilmentComponent onRowEditCentre handler', row);
    this.loading = true;
    this._fulfilmentService.getFulfilmentCentreById(row.warehouseId).subscribe(res => {
      LogUtil.debug('FulfilmentComponent getFulfilmentCentreById', res);
      this.centreEdit = res;
      this.changed = false;
      this.validForSave = false;
      this.viewMode = FulfilmentComponent.CENTRE;
      this.loading = false;
    });
  }

  onRowEditSelected() {
    this.onRowEditCentre(this.selectedCentre);
  }

  onSaveHandler() {

    if (this.validForSave && this.changed) {

      if (this.centreEdit != null) {

        LogUtil.debug('FulfilmentComponent Save handler centre', this.centreEdit);

        this.loading = true;
        this._fulfilmentService.saveFulfilmentCentre(this.centreEdit).subscribe(
            rez => {
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

  onDiscardEventHandler() {
    LogUtil.debug('FulfilmentComponent discard handler');
    if (this.viewMode === FulfilmentComponent.CENTRE) {
      if (this.selectedCentre != null) {
        this.onRowEditSelected();
      } else {
        this.onRowNew();
      }
    }
  }

  onDeleteConfirmationResult(modalresult: ModalResult) {
    LogUtil.debug('FulfilmentComponent onDeleteConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {

      if (this.selectedCentre != null) {
        LogUtil.debug('FulfilmentComponent onDeleteConfirmationResult', this.selectedCentre);

        this.loading = true;
        this._fulfilmentService.removeFulfilmentCentre(this.selectedCentre).subscribe(res => {
          LogUtil.debug('FulfilmentComponent removeCentre', this.selectedCentre, res);
          this.selectedCentre = null;
          this.centreEdit = null;
          this.loading = false;
          this.getFilteredCentres();
        });
      }
    }
  }

  onClearFilterCentre() {

    this.centreFilter = '';
    this.getFilteredCentres();

  }

  private getFilteredCentres() {

    this.loading = true;

    this.centres.searchContext.parameters.filter = [ this.centreFilter ];
    this.centres.searchContext.size = Config.UI_TABLE_PAGE_SIZE;

    this._fulfilmentService.getFilteredFulfilmentCentres(this.centres.searchContext).subscribe( allcentres => {
      LogUtil.debug('FulfilmentComponent getAllCentres', allcentres);
      this.centres = allcentres;
      this.selectedCentre = null;
      this.centreEdit = null;
      this.viewMode = FulfilmentComponent.CENTRES;
      this.changed = false;
      this.validForSave = false;
      this.loading = false;
    });
  }

}
