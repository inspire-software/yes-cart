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
import {Component, OnInit, OnDestroy, Input, Output, ViewChild, EventEmitter} from '@angular/core';
import {NgIf} from '@angular/common';
import {FormBuilder, REACTIVE_FORM_DIRECTIVES} from '@angular/forms';
import {YcValidators} from './../../shared/validation/validators';
import {ShopVO, CustomerVO, CustomerShopLinkVO, AttrValueCustomerVO, Pair} from './../../shared/model/index';
import {FormValidationEvent, Futures, Future} from './../../shared/event/index';
import {AttributeValuesComponent} from './../../shared/attributes/index';
import {ModalComponent, ModalResult, ModalAction} from './../../shared/modal/index';
import {TAB_DIRECTIVES} from 'ng2-bootstrap/ng2-bootstrap';


@Component({
  selector: 'yc-customer',
  moduleId: module.id,
  templateUrl: 'customer.component.html',
  directives: [NgIf, REACTIVE_FORM_DIRECTIVES, TAB_DIRECTIVES, AttributeValuesComponent, ModalComponent],
})

export class CustomerComponent implements OnInit, OnDestroy {

  _customer:CustomerVO;
  _attributes:AttrValueCustomerVO[] = [];
  attributeFilter:string;

  _shops:any = {};
  resetShopName:string = null;
  resetShop:ShopVO = null;

  availableShops:Array<Pair<ShopVO, CustomerShopLinkVO>> = [];
  supportedShops:Array<Pair<ShopVO, CustomerShopLinkVO>> = [];

  _changes:Array<Pair<AttrValueCustomerVO, boolean>>;

  selectedRow:AttrValueCustomerVO;

  @Output() dataChanged: EventEmitter<FormValidationEvent<Pair<CustomerVO, Array<Pair<AttrValueCustomerVO, boolean>>>>> = new EventEmitter<FormValidationEvent<Pair<CustomerVO, Array<Pair<AttrValueCustomerVO, boolean>>>>>();

  @Output() passwordReset: EventEmitter<Pair<CustomerVO, ShopVO>> = new EventEmitter<Pair<CustomerVO, ShopVO>>();

  changed:boolean = false;
  validForSave:boolean = false;
  delayedChange:Future;

  customerForm:any;
  customerFormSub:any;

  @ViewChild('attributeValuesComponent')
  attributeValuesComponent:AttributeValuesComponent;

  @ViewChild('resetConfirmationModalDialog')
  resetConfirmationModalDialog:ModalComponent;

  constructor(fb: FormBuilder) {
    console.debug('CustomerComponent constructed');

    this.customerForm = fb.group({
      'code': [''],
      'email': ['', YcValidators.requiredValidEmail],
      'salutation': ['', YcValidators.nonBlankTrimmed],
      'firstname': ['', YcValidators.requiredNonBlankTrimmed],
      'lastname': ['', YcValidators.requiredNonBlankTrimmed],
      'middlename': ['', YcValidators.nonBlankTrimmed],
      'tag': ['', YcValidators.nonBlankTrimmed],
      'customerType': ['', YcValidators.validCode],
      'pricingPolicy': ['', YcValidators.validCode],
    });

    let that = this;
    this.delayedChange = Futures.perpetual(function() {
      that.formChange();
    }, 200);

  }

  formReset():void {
    // Hack to reset NG2 forms see https://github.com/angular/angular/issues/4933
    for(let key in this.customerForm.controls) {
      this.customerForm.controls[key]['_pristine'] = true;
      this.customerForm.controls[key]['_touched'] = false;
    }
  }


  formBind():void {
    this.customerFormSub = this.customerForm.statusChanges.subscribe((data:any) => {
      this.validForSave = this.customerForm.valid;
      if (this.changed) {
        this.delayedChange.delay();
      }
    });
  }


  formUnbind():void {
    if (this.customerFormSub) {
      console.debug('CustomerComponent unbining form');
      this.customerFormSub.unsubscribe();
    }
  }

  formChange():void {
    console.debug('CustomerComponent validating formGroup is valid: ' + this.validForSave, this._customer);
    this.dataChanged.emit({ source: new Pair(this._customer, this._changes), valid: this.validForSave });
  }

  @Input()
  set customer(customer:CustomerVO) {
    this._customer = customer;
    this.changed = false;
    this.formReset();
    this.recalculateShops();
  }

  get customer():CustomerVO {
    return this._customer;
  }

  @Input()
  set attributes(attributes:AttrValueCustomerVO[]) {
    this._attributes = attributes;
  }

  get attributes():AttrValueCustomerVO[] {
    return this._attributes;
  }


  @Input()
  set shops(shops:Array<ShopVO>) {
    shops.forEach(shop => {
      this._shops['S' + shop.shopId] = shop;
    });
    console.debug('CustomerComponent mapped shops', this._shops);
  }


  private recalculateShops():void {

    if (this._customer) {
      this.availableShops = this.getAvailableShopNames();
      this.supportedShops = this.getSupportedShopNames();
    } else {
      this.availableShops = this.getAvailableShopNames();
      this.supportedShops = [];
    }
  }

  onMainDataChange(event:any) {
    console.debug('CustomerComponent main data changed', this._customer);
    this.changed = true;
  }

  onAttributeDataChanged(event:any) {
    console.debug('CustomerComponent attr data changed', this._customer);
    this.changed = true;
    this._changes = event.source;
    this.delayedChange.delay();
  }

  ngOnInit() {
    console.debug('CustomerComponent ngOnInit');
    this.formBind();
  }

  ngOnDestroy() {
    console.debug('CustomerComponent ngOnDestroy');
    this.formUnbind();
  }

  tabSelected(tab:any) {
    console.debug('CustomerComponent tabSelected', tab);
  }


  protected onRowDeleteSelected() {
    if (this.selectedRow != null) {
      this.attributeValuesComponent.onRowDeleteSelected();
    }
  }

  protected onRowEditSelected() {
    if (this.selectedRow != null) {
      this.attributeValuesComponent.onRowEditSelected();
    }
  }

  protected onSelectRow(row:AttrValueCustomerVO) {
    console.debug('CustomerComponent onSelectRow handler', row);
    if (row == this.selectedRow) {
      this.selectedRow = null;
    } else {
      this.selectedRow = row;
    }
  }


  private getAvailableShopNames():Array<Pair<ShopVO, CustomerShopLinkVO>> {

    let supported = this._customer != null ? this._customer.customerShops : [];
    let disabledShops = <Array<number>>[];
    let skipKeys = <Array<string>>[];
    if (supported) {
      supported.forEach(customershop => {
        if (!customershop.disabled) {
          skipKeys.push('S' + customershop.shopId);
        } else {
          disabledShops.push(customershop.shopId);
        }
      });
    }
    console.debug('CustomerComponent supported shops', skipKeys);

    let labels = <Array<Pair<ShopVO, CustomerShopLinkVO>>>[];
    for (let key in this._shops) {
      if (skipKeys.indexOf(key) == -1) {
        let shop = this._shops[key];
        labels.push({
          first: shop,
          second: { customerId: this._customer != null ? this._customer.customerId : 0, shopId: shop.shopId, disabled: (disabledShops.indexOf(shop.shopId) != -1) }
        });
      }
    }

    console.debug('CustomerComponent available shops', labels);
    return labels;
  }

  private getSupportedShopNames():Array<Pair<ShopVO, CustomerShopLinkVO>> {
    let supported = this._customer.customerShops;
    let keepKeys = <Array<string>>[];
    if (supported) {
      supported.forEach(customershop => {
        if (!customershop.disabled) {
          keepKeys.push('S' + customershop.shopId);
        }
      });
    }
    console.debug('CustomerComponent supported', keepKeys);

    let labels = <Array<Pair<ShopVO, CustomerShopLinkVO>>>[];
    for (let key in this._shops) {
      let idx = keepKeys.indexOf(key);
      if (idx != -1) {
        let shop = this._shops[key];
        let customerShop = this._customer.customerShops[idx];
        labels.push({ first: shop, second: customerShop });
      }
    }

    console.debug('CustomerComponent supported', labels);
    return labels;
  }

  onSupportedShopClick(supported:Pair<ShopVO, CustomerShopLinkVO>) {
    console.debug('CustomerComponent remove supported', supported);
    let idx = this._customer.customerShops.findIndex(link =>
      link.shopId == supported.first.shopId
    );
    if (idx != -1) {
      this._customer.customerShops.splice(idx, 1);
      this.recalculateShops();
      this.changed = true;
      this.formChange();
    }
  }

  onAvailableShopClick(available:Pair<ShopVO, CustomerShopLinkVO>) {
    console.debug('CustomerComponent add supported', available);
    available.second.disabled = false;
    this._customer.customerShops.push(available.second);
    this.recalculateShops();
    this.changed = true;
    this.formChange();
  }

  onRowReset(shop:ShopVO) {
    this.resetShopName = shop.code + ' ' + shop.name;
    this.resetShop = shop;
    this.resetConfirmationModalDialog.show();
  }

  protected onResetConfirmationResult(modalresult: ModalResult) {
    console.debug('CustomerComponent onResetConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {

      this.passwordReset.emit(new Pair(this._customer, this.resetShop));

    }
  }

}
