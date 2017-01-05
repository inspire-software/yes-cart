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
import { FormBuilder, Validators } from '@angular/forms';
import { YcValidators } from './../shared/validation/validators';
import { FulfilmentService, Util } from './../shared/services/index';
import { ModalComponent, ModalResult, ModalAction } from './../shared/modal/index';
import { ProductSkuSelectComponent } from './../shared/catalog/index';
import { InventoryInfoComponent } from './../shared/fulfilment/index';
import { InventoryVO, FulfilmentCentreInfoVO, ProductSkuVO } from './../shared/model/index';
import { FormValidationEvent, Futures, Future } from './../shared/event/index';
import { Config } from './../shared/config/env.config';
import { UiUtil } from './../shared/ui/index';
import { LogUtil } from './../shared/log/index';

@Component({
  selector: 'yc-centre-inventory',
  moduleId: module.id,
  templateUrl: 'centre-inventory.component.html',
})

export class CentreInventoryComponent implements OnInit, OnDestroy {

  private static _selectedCentre:FulfilmentCentreInfoVO;

  private searchHelpShow:boolean = false;
  private forceShowAll:boolean = false;

  private inventory:Array<InventoryVO> = [];
  private inventoryFilter:string;
  private inventoryFilterRequired:boolean = true;
  private inventoryFilterCapped:boolean = false;

  private delayedFiltering:Future;
  private delayedFilteringMs:number = Config.UI_INPUT_DELAY;
  private filterCap:number = Config.UI_FILTER_CAP;
  private filterNoCap:number = Config.UI_FILTER_NO_CAP;

  private selectedInventory:InventoryVO;

  private inventoryEdit:InventoryVO;
  private inventoryEditForm:any;
  private inventoryEditFormSub:any; // tslint:disable-line:no-unused-variable
  private initialising:boolean = false; // tslint:disable-line:no-unused-variable
  private validForSave:boolean = false;

  @ViewChild('deleteConfirmationModalDialog')
  private deleteConfirmationModalDialog:ModalComponent;

  @ViewChild('editInventoryModalDialog')
  private editInventoryModalDialog:ModalComponent;

  @ViewChild('selectCentreModalDialog')
  private selectCentreModalDialog:ModalComponent;

  @ViewChild('inventoryInfoDialog')
  private inventoryInfoDialog:InventoryInfoComponent;

  @ViewChild('productSkuSelectDialog')
  private productSkuSelectDialog:ProductSkuSelectComponent;

  private deleteValue:String;

  private selectedSkuCode:String;

  private loading:boolean = false;

  constructor(private _fulfilmentService:FulfilmentService,
              fb: FormBuilder) {
    LogUtil.debug('CentreInventoryComponent constructed');

    this.inventoryEditForm = fb.group({
      'skuCode': ['', YcValidators.requiredValidCode],
      'skuName': [''],
      'warehouseCode': ['', Validators.required],
      'warehouseName': [''],
      'quantity': ['', YcValidators.requiredPositiveNumber],
      'reserved': [''],
    });
  }

  get selectedCentre():FulfilmentCentreInfoVO {
     return CentreInventoryComponent._selectedCentre;
  }

  set selectedCentre(selectedCentre:FulfilmentCentreInfoVO) {
    CentreInventoryComponent._selectedCentre = selectedCentre;
  }

  newInventoryInstance():InventoryVO {
    return { skuWarehouseId: 0, skuCode: '', skuName: '', warehouseCode: this.selectedCentre.code, warehouseName: this.selectedCentre.name, quantity: 0, reserved: 0};
  }

  ngOnInit() {
    LogUtil.debug('CentreInventoryComponent ngOnInit');
    this.onRefreshHandler();
    let that = this;
    this.delayedFiltering = Futures.perpetual(function() {
      that.getFilteredInventory();
    }, this.delayedFilteringMs);
    this.formBind();
  }

  ngOnDestroy() {
    LogUtil.debug('CentreInventoryComponent ngOnDestroy');
    this.formUnbind();
  }

  formBind():void {
    UiUtil.formBind(this, 'inventoryEditForm', 'inventoryEditFormSub', 'formChange', 'initialising', false);
  }

  formUnbind():void {
    UiUtil.formUnbind(this, 'inventoryEditFormSub');
  }

  formChange():void {
    LogUtil.debug('CentreInventoryComponent formChange', this.inventoryEditForm.valid, this.inventoryEdit);
    this.validForSave = this.inventoryEditForm.valid;
  }


  protected onFulfilmentCentreSelect() {
    LogUtil.debug('CentreInventoryComponent onFulfilmentCentreSelect');
    this.selectCentreModalDialog.show();
  }

  protected onFulfilmentCentreSelected(event:FulfilmentCentreInfoVO) {
    LogUtil.debug('CentreInventoryComponent onFulfilmentCentreSelected');
    this.selectedCentre = event;
  }

  protected onSelectCentreResult(modalresult: ModalResult) {
    LogUtil.debug('CentreInventoryComponent onSelectCentreResult modal result is ', modalresult);
    if (this.selectedCentre == null) {
      this.selectCentreModalDialog.show();
    } else {
      this.getFilteredInventory();
    }
  }

  protected onFilterChange(event:any) {

    this.delayedFiltering.delay();

  }

  protected onRefreshHandler() {
    LogUtil.debug('CentreInventoryComponent refresh handler');
    this.getFilteredInventory();
  }

  protected onInventorySelected(data:InventoryVO) {
    LogUtil.debug('CentreInventoryComponent onInventorySelected', data);
    this.selectedInventory = data;
    this.selectedSkuCode = this.selectedInventory ? this.selectedInventory.skuCode : null;
  }

  protected onSearchLow() {
    this.inventoryFilter = '-5';
    this.searchHelpShow = false;
    this.getFilteredInventory();
  }

  protected onSearchReserved() {
    this.inventoryFilter = '+0.001';
    this.searchHelpShow = false;
    this.getFilteredInventory();
  }

  protected onSearchExact() {
    this.inventoryFilter = '!';
    this.searchHelpShow = false;
  }

  protected onForceShowAll() {
    this.forceShowAll = !this.forceShowAll;
    this.getFilteredInventory();
  }

  protected onSearchHelpToggle() {
    this.searchHelpShow = !this.searchHelpShow;
  }

  protected onRowInfoSelected() {
    if (this.selectedInventory != null) {
      this.inventoryInfoDialog.showDialog();
    }
  }

  protected onRowNew() {
    LogUtil.debug('CentreInventoryComponent onRowNew handler');
    this.validForSave = false;
    this.selectedInventory = null;
    UiUtil.formInitialise(this, 'initialising', 'inventoryEditForm', 'inventoryEdit', this.newInventoryInstance(), false, ['skuCode']);
    this.editInventoryModalDialog.show();
  }

  protected onRowDelete(row:InventoryVO) {
    LogUtil.debug('CentreInventoryComponent onRowDelete handler', row);
    this.deleteValue = row.skuCode;
    this.deleteConfirmationModalDialog.show();
  }

  protected onRowDeleteSelected() {
    if (this.selectedInventory != null) {
      this.onRowDelete(this.selectedInventory);
    }
  }


  protected onRowEditInventory(row:InventoryVO) {
    LogUtil.debug('CentreInventoryComponent onRowEditInventory handler', row);
    this.validForSave = false;
    UiUtil.formInitialise(this, 'initialising', 'inventoryEditForm', 'inventoryEdit', Util.clone(row), row.skuWarehouseId > 0, ['skuCode']);
    this.editInventoryModalDialog.show();
  }

  protected onRowEditSelected() {
    if (this.selectedInventory != null) {
      this.onRowEditInventory(this.selectedInventory);
    }
  }

  protected onSearchSKU() {
    if (this.inventoryEdit != null && this.inventoryEdit.skuWarehouseId <= 0) {
      this.productSkuSelectDialog.showDialog();
    }
  }


  protected onProductSkuSelected(event:FormValidationEvent<ProductSkuVO>) {
    LogUtil.debug('CentreInventoryComponent onProductSkuSelected');
    if (event.valid && this.inventoryEdit != null && this.inventoryEdit.skuWarehouseId <= 0) {
      this.inventoryEdit.skuCode = event.source.code;
      this.inventoryEdit.skuName = event.source.name;
    }
  }


  protected onSaveHandler() {

    if (this.validForSave && this.inventoryEditForm.dirty) {

      if (this.inventoryEdit != null) {

        LogUtil.debug('CentreInventoryComponent Save handler inventory', this.inventoryEdit);

        var _sub:any = this._fulfilmentService.saveInventory(this.inventoryEdit).subscribe(
            rez => {
              _sub.unsubscribe();
              let pk = this.inventoryEdit.skuWarehouseId;
              LogUtil.debug('CentreInventoryComponent inventory changed', rez);
              this.selectedInventory = rez;
              this.validForSave = false;
              this.inventoryEdit = this.newInventoryInstance();
              if (pk == 0) {
                this.inventoryFilter = '!' + rez.skuCode;
              }
              this.getFilteredInventory();
          }
        );
      }

    }

  }

  protected onDiscardEventHandler() {
    LogUtil.debug('CentreInventoryComponent discard handler');
    if (this.selectedInventory != null) {
      this.onRowEditSelected();
    } else {
      this.onRowNew();
    }
  }

  protected onEditInventoryResult(modalresult: ModalResult) {
    LogUtil.debug('CentreInventoryComponent onEditInventoryResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {

      this.onSaveHandler();

    }
  }

  protected onDeleteConfirmationResult(modalresult: ModalResult) {
    LogUtil.debug('CentreInventoryComponent onDeleteConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {

      if (this.selectedInventory != null) {
        LogUtil.debug('CentreInventoryComponent onDeleteConfirmationResult', this.selectedInventory);

        var _sub:any = this._fulfilmentService.removeInventory(this.selectedInventory).subscribe(res => {
          _sub.unsubscribe();
          LogUtil.debug('CentreInventoryComponent removeInventory', this.selectedInventory);
          this.selectedInventory = null;
          this.inventoryEdit = this.newInventoryInstance();
          this.getFilteredInventory();
        });
      }
    }
  }

  protected onClearFilter() {

    this.inventoryFilter = '';
    this.getFilteredInventory();

  }

  private getFilteredInventory() {
    this.inventoryFilterRequired = !this.forceShowAll && (this.inventoryFilter == null || this.inventoryFilter.length < 2);

    LogUtil.debug('CentreInventoryComponent getFilteredInventory' + (this.forceShowAll ? ' forcefully': ''));

    if (this.selectedCentre != null && !this.inventoryFilterRequired) {
      this.loading = true;
      let max = this.forceShowAll ? this.filterNoCap : this.filterCap;
      var _sub:any = this._fulfilmentService.getFilteredInventory(this.selectedCentre, this.inventoryFilter, max).subscribe( allinventory => {
        LogUtil.debug('CentreInventoryComponent getFilteredInventory', allinventory);
        this.inventory = allinventory;
        this.selectedInventory = null;
        this.validForSave = false;
        this.inventoryFilterCapped = this.inventory.length >= max;
        this.loading = false;
        _sub.unsubscribe();
      });
    } else {
      this.inventory = [];
      this.selectedInventory = null;
      this.validForSave = false;
      this.inventoryFilterCapped = false;
    }
  }

}
