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
import { ShopEventBus, FulfilmentService, Util } from './../shared/services/index';
import { ModalComponent, ModalResult, ModalAction } from './../shared/modal/index';
import { FulfilmentCentreVO, ShopVO } from './../shared/model/index';
import { FormValidationEvent } from './../shared/event/index';
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

  private centres:Array<FulfilmentCentreVO> = [];
  private centreFilter:string;

  private selectedCentre:FulfilmentCentreVO;

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
  }

  newCentreInstance():FulfilmentCentreVO {
    return {
      warehouseId: 0, code: '', name: '', description: null,
      countryCode: null, stateCode: null, city: null, postcode: null,
      defaultStandardStockLeadTime: 0, defaultBackorderStockLeadTime: 0, multipleShippingSupported: false,
      displayNames: [], fulfilmentShops: []
    };
  }

  ngOnInit() {
    LogUtil.debug('FulfilmentComponent ngOnInit');
    this.onRefreshHandler();
  }

  ngOnDestroy() {
    LogUtil.debug('FulfilmentComponent ngOnDestroy');
    if (this.shopAllSub) {
      this.shopAllSub.unsubscribe();
    }
  }

  protected onRefreshHandler() {
    LogUtil.debug('FulfilmentComponent refresh handler');
    this.getAllCentres();
  }

  protected onCentreSelected(data:FulfilmentCentreVO) {
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
    this.onRowDelete(this.selectedCentre);
  }


  protected onRowEditCentre(row:FulfilmentCentreVO) {
    LogUtil.debug('FulfilmentComponent onRowEditCentre handler', row);
    this.centreEdit = Util.clone(row);
    this.changed = false;
    this.validForSave = false;
    this.viewMode = FulfilmentComponent.CENTRE;
  }

  protected onRowEditSelected() {
    this.onRowEditCentre(this.selectedCentre);
  }

  protected onSaveHandler() {

    if (this.validForSave && this.changed) {

      if (this.centreEdit != null) {

        LogUtil.debug('FulfilmentComponent Save handler centre', this.centreEdit);

        var _sub:any = this._fulfilmentService.saveFulfilmentCentre(this.centreEdit).subscribe(
            rez => {
            if (this.centreEdit.warehouseId > 0) {
              let idx = this.centres.findIndex(rez => rez.warehouseId == this.centreEdit.warehouseId);
              if (idx !== -1) {
                this.centres[idx] = rez;
                this.centres = this.centres.slice(0, this.centres.length); // reset to propagate changes
                LogUtil.debug('FulfilmentComponent centre changed', rez);
              }
            } else {
              this.centres.push(rez);
              this.centreFilter = rez.name;
              LogUtil.debug('FulfilmentComponent centre added', rez);
            }
            this.changed = false;
            this.selectedCentre = rez;
            this.centreEdit = null;
            this.viewMode = FulfilmentComponent.CENTRES;
            _sub.unsubscribe();
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

        var _sub:any = this._fulfilmentService.removeFulfilmentCentre(this.selectedCentre).subscribe(res => {
          LogUtil.debug('FulfilmentComponent removeCentre', this.selectedCentre);
          let idx = this.centres.indexOf(this.selectedCentre);
          this.centres.splice(idx, 1);
          this.centres = this.centres.slice(0, this.centres.length); // reset to propagate changes
          this.selectedCentre = null;
          this.centreEdit = null;
          _sub.unsubscribe();
        });
      }
    }
  }

  protected onClearFilterCentre() {

    this.centreFilter = '';

  }

  private getAllCentres() {
    this.loading = true;
    var _sub:any = this._fulfilmentService.getAllFulfilmentCentres().subscribe( allcentres => {
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
