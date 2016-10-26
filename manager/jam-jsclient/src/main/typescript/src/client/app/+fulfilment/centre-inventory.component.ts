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
import {Component, OnInit, OnDestroy, ViewChild} from '@angular/core';
import {NgIf} from '@angular/common';
import {FormBuilder, Validators, REACTIVE_FORM_DIRECTIVES} from '@angular/forms';
import {YcValidators} from './../shared/validation/validators';
import {FulfilmentService, Util} from './../shared/services/index';
import {TAB_DIRECTIVES} from 'ng2-bootstrap/ng2-bootstrap';
import {InventoryComponent} from './components/index';
import {DataControlComponent} from './../shared/sidebar/index';
import {FulfilmentCentreSelectComponent} from './../shared/fulfilment/index';
import {ModalComponent, ModalResult, ModalAction} from './../shared/modal/index';
import {InventoryVO, FulfilmentCentreInfoVO} from './../shared/model/index';
import {Futures, Future} from './../shared/event/index';
import {Config} from './../shared/config/env.config';

@Component({
  selector: 'yc-centre-inventory',
  moduleId: module.id,
  templateUrl: 'centre-inventory.component.html',
  directives: [REACTIVE_FORM_DIRECTIVES, TAB_DIRECTIVES, NgIf, InventoryComponent, ModalComponent, DataControlComponent, FulfilmentCentreSelectComponent ],
})

export class CentreInventoryComponent implements OnInit, OnDestroy {

  private searchHelpShow:boolean = false;
  private forceShowAll:boolean = false;

  private inventory:Array<InventoryVO> = [];
  private inventoryFilter:string;
  private inventoryFilterRequired:boolean = true;
  private inventoryFilterCapped:boolean = false;

  delayedFiltering:Future;
  delayedFilteringMs:number = Config.UI_INPUT_DELAY;
  filterCap:number = Config.UI_FILTER_CAP;
  filterNoCap:number = Config.UI_FILTER_NO_CAP;

  static _selectedCentre:FulfilmentCentreInfoVO;
  private selectedInventory:InventoryVO;

  private inventoryEdit:InventoryVO;
  inventoryEditForm:any;
  inventoryEditFormSub:any;
  changedSingle:boolean = true;
  validForSave:boolean = false;

  @ViewChild('deleteConfirmationModalDialog')
  deleteConfirmationModalDialog:ModalComponent;

  @ViewChild('editInventoryModalDialog')
  editInventoryModalDialog:ModalComponent;

  @ViewChild('selectCentreModalDialog')
  selectCentreModalDialog:ModalComponent;

  private deleteValue:String;

  constructor(private _fulfilmentService:FulfilmentService,
              fb: FormBuilder) {
    console.debug('CentreInventoryComponent constructed');

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
    console.debug('CentreInventoryComponent ngOnInit');
    this.onRefreshHandler();
    let that = this;
    this.delayedFiltering = Futures.perpetual(function() {
      that.getFilteredInventory();
    }, this.delayedFilteringMs);
    this.formBind();
  }

  ngOnDestroy() {
    console.debug('CentreInventoryComponent ngOnDestroy');
    this.formUnbind();
  }


  formReset():void {
    // Hack to reset NG2 forms see https://github.com/angular/angular/issues/4933
    for(let key in this.inventoryEditForm.controls) {
      this.inventoryEditForm.controls[key]['_pristine'] = true;
      this.inventoryEditForm.controls[key]['_touched'] = false;
    }
  }

  formBind():void {
    this.inventoryEditFormSub = this.inventoryEditForm.statusChanges.subscribe((data:any) => {
      if (this.changedSingle) {
        this.validForSave = this.inventoryEditForm.valid;
      }
    });
  }

  formUnbind():void {
    if (this.inventoryEditFormSub) {
      console.debug('CentreInventoryComponent unbining form');
      this.inventoryEditFormSub.unsubscribe();
    }
  }


  onFormDataChange(event:any) {
    console.debug('CentreInventoryComponent data changed', event);
    this.changedSingle = true;
  }



  protected onFulfilmentCentreSelect() {
    console.debug('CentreInventoryComponent onFulfilmentCentreSelect');
    this.selectCentreModalDialog.show();
  }

  protected onFulfilmentCentreSelected(event:FulfilmentCentreInfoVO) {
    console.debug('CentreInventoryComponent onFulfilmentCentreSelected');
    this.selectedCentre = event;
  }

  protected onSelectCentreResult(modalresult: ModalResult) {
    console.debug('CentreInventoryComponent onSelectCentreResult modal result is ', modalresult);
    if (this.selectedCentre == null) {
      this.selectCentreModalDialog.show();
    } else {
      this.getFilteredInventory();
    }
  }

  onFilterChange(event:any) {

    this.delayedFiltering.delay();

  }

  getFilteredInventory() {
    this.inventoryFilterRequired = !this.forceShowAll && (this.inventoryFilter == null || this.inventoryFilter.length < 2);

    console.debug('CentreInventoryComponent getFilteredInventory' + (this.forceShowAll ? ' forcefully': ''));

    if (this.selectedCentre != null && !this.inventoryFilterRequired) {
      let max = this.forceShowAll ? this.filterNoCap : this.filterCap;
      var _sub:any = this._fulfilmentService.getFilteredInventory(this.selectedCentre, this.inventoryFilter, max).subscribe( allinventory => {
        console.debug('CentreInventoryComponent getFilteredInventory', allinventory);
        this.inventory = allinventory;
        this.selectedInventory = null;
        this.inventoryEdit = null;
        this.changedSingle = false;
        this.validForSave = false;
        this.inventoryFilterCapped = this.inventory.length >= max;
        _sub.unsubscribe();
      });
    } else {
      this.inventory = [];
      this.selectedInventory = null;
      this.inventoryEdit = null;
      this.changedSingle = false;
      this.validForSave = false;
      this.inventoryFilterCapped = false;
    }
  }

  protected onRefreshHandler() {
    console.debug('CentreInventoryComponent refresh handler');
    this.getFilteredInventory();
  }

  onInventorySelected(data:InventoryVO) {
    console.debug('CentreInventoryComponent onInventorySelected', data);
    this.selectedInventory = data;
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

  protected onForceShowAll() {
    this.forceShowAll = !this.forceShowAll;
    this.getFilteredInventory();
  }

  protected onSearchHelpToggle() {
    this.searchHelpShow = !this.searchHelpShow;
  }

  protected onRowNew() {
    console.debug('CentreInventoryComponent onRowNew handler');
    this.changedSingle = false;
    this.validForSave = false;
    this.formReset();
    this.inventoryEdit = this.newInventoryInstance();
    this.editInventoryModalDialog.show();
  }

  protected onRowDelete(row:InventoryVO) {
    console.debug('CentreInventoryComponent onRowDelete handler', row);
    this.deleteValue = row.skuCode;
    this.deleteConfirmationModalDialog.show();
  }

  protected onRowDeleteSelected() {
    if (this.selectedInventory != null) {
      this.onRowDelete(this.selectedInventory);
    }
  }


  protected onRowEditInventory(row:InventoryVO) {
    console.debug('CentreInventoryComponent onRowEditInventory handler', row);
    this.formReset();
    this.inventoryEdit = Util.clone(row);
    this.changedSingle = false;
    this.validForSave = false;
    this.editInventoryModalDialog.show();
  }

  protected onRowEditSelected() {
    if (this.selectedInventory != null) {
      this.onRowEditInventory(this.selectedInventory);
    }
  }

  protected onSaveHandler() {

    if (this.validForSave && this.changedSingle) {

      if (this.inventoryEdit != null) {

        console.debug('CentreInventoryComponent Save handler inventory', this.inventoryEdit);

        var _sub:any = this._fulfilmentService.saveInventory(this.inventoryEdit).subscribe(
            rez => {
              _sub.unsubscribe();
              let pk = this.inventoryEdit.skuWarehouseId;
              console.debug('CentreInventoryComponent inventory changed', rez);
              this.changedSingle = false;
              this.selectedInventory = rez;
              this.inventoryEdit = null;
              if (pk == 0) {
                this.inventoryFilter = rez.skuCode;
              }
              this.getFilteredInventory();
          }
        );
      }

    }

  }

  protected onDiscardEventHandler() {
    console.debug('CentreInventoryComponent discard handler');
    if (this.selectedInventory != null) {
      this.onRowEditSelected();
    } else {
      this.onRowNew();
    }
  }

  protected onEditInventoryResult(modalresult: ModalResult) {
    console.debug('CentreInventoryComponent onEditInventoryResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {

      this.onSaveHandler();

    } else {

      this.inventoryEdit = null;

    }
  }

  protected onDeleteConfirmationResult(modalresult: ModalResult) {
    console.debug('CentreInventoryComponent onDeleteConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {

      if (this.selectedInventory != null) {
        console.debug('CentreInventoryComponent onDeleteConfirmationResult', this.selectedInventory);

        var _sub:any = this._fulfilmentService.removeInventory(this.selectedInventory).subscribe(res => {
          _sub.unsubscribe();
          console.debug('CentreInventoryComponent removeInventory', this.selectedInventory);
          this.selectedInventory = null;
          this.inventoryEdit = null;
          this.getFilteredInventory();
        });
      }
    }
  }

}
