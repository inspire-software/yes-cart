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

  private initialising:boolean = false; // tslint:disable-line:no-unused-variable
  private delayedChange:Future;

  private inventoryForm:any;
  private inventoryFormSub:any; // tslint:disable-line:no-unused-variable

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
      'releaseDate': ['', YcValidators.validDate],
      'availablefrom': ['', YcValidators.validDate],
      'availableto': ['', YcValidators.validDate],
      'availability': ['', YcValidators.requiredPositiveNumber],
      'minOrderQuantity': ['', YcValidators.positiveNumber],
      'maxOrderQuantity': ['', YcValidators.positiveNumber],
      'stepOrderQuantity': ['', YcValidators.positiveNumber],
      'restockDate': ['', YcValidators.validDate],
    });

    this.delayedChange = Futures.perpetual(function() {
      that.formChange();
    }, 200);

  }

  formBind():void {
    UiUtil.formBind(this, 'inventoryForm', 'inventoryFormSub', 'delayedChange', 'initialising');
  }


  formUnbind():void {
    UiUtil.formUnbind(this, 'inventoryFormSub');
  }

  formChange():void {
    LogUtil.debug('OfferComponent formChange', this.inventoryForm.valid, this._inventory);
    this.dataChanged.emit({ source: this._inventory, valid: this.inventoryForm.valid });
  }

  onRestockNotesDataChange(event:FormValidationEvent<any>) {
    UiUtil.formI18nDataChange(this, 'inventoryForm', 'restockDate', event);
  }

  formMarkDirty(field:string):void {
    UiUtil.formMarkFieldDirty(this, 'inventoryForm', field);
  }

  @Input()
  set inventory(inventory: InventoryVO) {

    UiUtil.formInitialise(this, 'initialising', 'inventoryForm', '_inventory', inventory);
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

  get availableto():string {
    return UiUtil.dateInputGetterProxy(this._inventory, 'availableto');
  }

  set availableto(availableto:string) {
    UiUtil.dateInputSetterProxy(this._inventory, 'availableto', availableto);
  }

  get availablefrom():string {
    return UiUtil.dateInputGetterProxy(this._inventory, 'availablefrom');
  }

  set availablefrom(availablefrom:string) {
    UiUtil.dateInputSetterProxy(this._inventory, 'availablefrom', availablefrom);
  }

  get releaseDate():string {
    return UiUtil.dateInputGetterProxy(this._inventory, 'releaseDate');
  }

  set releaseDate(releaseDate:string) {
    UiUtil.dateInputSetterProxy(this._inventory, 'releaseDate', releaseDate);
  }

  get restockDate():string {
    return UiUtil.dateInputGetterProxy(this._inventory, 'restockDate');
  }

  set restockDate(restockDate:string) {
    UiUtil.dateInputSetterProxy(this._inventory, 'restockDate', restockDate);
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
    LogUtil.debug('CentreInventoryComponent onProductSkuSelected');
    if (event.valid && this._inventory != null && this._inventory.skuWarehouseId <= 0) {
      this._inventory.skuCode = event.source.code;
      this._inventory.skuName = event.source.name;
      this.formMarkDirty('skuCode');
    }
  }


}
