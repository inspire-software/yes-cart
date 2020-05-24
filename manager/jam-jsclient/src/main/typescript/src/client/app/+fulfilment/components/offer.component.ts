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
import { Component, OnInit, OnDestroy, Input, Output, EventEmitter, ViewChild } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { YcValidators } from './../../shared/validation/validators';
import { ProductSkuVO, InventoryVO } from './../../shared/model/index';
import { FormValidationEvent, Futures, Future } from './../../shared/event/index';
import { ProductSkuSelectComponent } from './../../shared/catalog/index';
import { UiUtil } from './../../shared/ui/index';
import { LogUtil } from './../../shared/log/index';

@Component({
  selector: 'yc-offer',
  moduleId: module.id,
  templateUrl: 'offer.component.html',
})

export class OfferComponent implements OnInit, OnDestroy {

  @Output() dataChanged: EventEmitter<FormValidationEvent<InventoryVO>> = new EventEmitter<FormValidationEvent<InventoryVO>>();

  private _inventory:InventoryVO;
  private _centreAndName:string;

  private delayedChange:Future;

  private inventoryForm:any;

  @ViewChild('productSkuSelectDialog')
  private productSkuSelectDialog:ProductSkuSelectComponent;

  constructor(fb: FormBuilder) {
    LogUtil.debug('OfferComponent constructed');

    let that = this;

    this.inventoryForm = fb.group({
      'skuCode': ['', Validators.required],
      'skuName': [''],
      'centreAndName': ['', Validators.required],
      'quantity': ['', YcValidators.requiredPositiveNumber],
      'reserved': [''],
      'tag': ['', YcValidators.nonBlankTrimmed],
      'featured': [''],
      'disabled': [''],
      'releaseDate': [''],
      'availablefrom': [''],
      'availableto': [''],
      'availability': ['', YcValidators.requiredPositiveNumber],
      'minOrderQuantity': ['', YcValidators.positiveNumber],
      'maxOrderQuantity': ['', YcValidators.positiveNumber],
      'stepOrderQuantity': ['', YcValidators.positiveNumber],
      'restockDate': [''],
      'restockNotes': [''],
    });

    this.delayedChange = Futures.perpetual(function() {
      that.formChange();
    }, 200);

  }

  formBind():void {
    UiUtil.formBind(this, 'inventoryForm', 'delayedChange');
  }


  formUnbind():void {
    UiUtil.formUnbind(this, 'inventoryForm');
  }

  formChange():void {
    LogUtil.debug('OfferComponent formChange', this.inventoryForm.valid, this._inventory);
    this.dataChanged.emit({ source: this._inventory, valid: this.inventoryForm.valid });
  }

  onRestockNotesDataChange(event:FormValidationEvent<any>) {
    UiUtil.formI18nDataChange(this, 'inventoryForm', 'restockNotes', event);
  }

  @Input()
  set inventory(inventory: InventoryVO) {

    let lock = inventory == null || inventory.skuWarehouseId > 0;
    UiUtil.formInitialise(this, 'inventoryForm', '_inventory', inventory, lock, ['skuCode']);
    if (inventory != null) {
      this._centreAndName = inventory.warehouseCode + ': ' + inventory.warehouseName;
    }

  }

  get inventory(): InventoryVO {
    return this._inventory;
  }

  get centreAndName():string {
    return this._centreAndName;
  }


  onAvailableFrom(event:FormValidationEvent<any>) {
    if (event.valid) {
      this.inventory.availablefrom = event.source;
    }
    UiUtil.formDataChange(this, 'inventoryForm', 'availablefrom', event);
  }

  onAvailableTo(event:FormValidationEvent<any>) {
    if (event.valid) {
      this.inventory.availableto = event.source;
    }
    UiUtil.formDataChange(this, 'inventoryForm', 'availableto', event);
  }

  onReleaseDate(event:FormValidationEvent<any>) {
    if (event.valid) {
      this.inventory.releaseDate = event.source;
    }
    UiUtil.formDataChange(this, 'inventoryForm', 'releaseDate', event);
  }

  onRestockDate(event:FormValidationEvent<any>) {
    if (event.valid) {
      this.inventory.restockDate = event.source;
    }
    UiUtil.formDataChange(this, 'inventoryForm', 'restockDate', event);
  }


  ngOnInit() {
    LogUtil.debug('OfferComponent ngOnInit');
    this.formBind();
  }

  ngOnDestroy() {
    LogUtil.debug('OfferComponent ngOnDestroy');
    this.formUnbind();
  }

  tabSelected(tab:any) {
    LogUtil.debug('OfferComponent tabSelected', tab);
  }

  protected onSearchSKU() {
    if (this._inventory != null && this._inventory.skuWarehouseId <= 0) {
      this.productSkuSelectDialog.showDialog();
    }
  }


  protected onProductSkuSelected(event:FormValidationEvent<ProductSkuVO>) {
    LogUtil.debug('OfferComponent onProductSkuSelected');
    if (event.valid && this._inventory != null && this._inventory.skuWarehouseId <= 0) {
      this._inventory.skuCode = event.source.code;
      this._inventory.skuName = event.source.name;
      this.delayedChange.delay();
    }
  }


}
