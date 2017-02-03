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
import { Component, OnInit, OnDestroy, Input, Output, ViewChild, EventEmitter } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { CustomerService, I18nEventBus } from './../../shared/services/index';
import { YcValidators } from './../../shared/validation/validators';
import { ShopVO, CustomerVO, CustomerShopLinkVO, AttrValueCustomerVO, Pair } from './../../shared/model/index';
import { FormValidationEvent, Futures, Future } from './../../shared/event/index';
import { AttributeValuesComponent } from './../../shared/attributes/index';
import { ModalComponent, ModalResult, ModalAction } from './../../shared/modal/index';
import { UiUtil } from './../../shared/ui/index';
import { LogUtil } from './../../shared/log/index';


@Component({
  selector: 'yc-customer',
  moduleId: module.id,
  templateUrl: 'customer.component.html',
})

export class CustomerComponent implements OnInit, OnDestroy {

  @Output() dataChanged: EventEmitter<FormValidationEvent<Pair<CustomerVO, Array<Pair<AttrValueCustomerVO, boolean>>>>> = new EventEmitter<FormValidationEvent<Pair<CustomerVO, Array<Pair<AttrValueCustomerVO, boolean>>>>>();

  @Output() passwordReset: EventEmitter<Pair<CustomerVO, ShopVO>> = new EventEmitter<Pair<CustomerVO, ShopVO>>();

  private _customer:CustomerVO;
  private _attributes:AttrValueCustomerVO[] = [];
  private attributeFilter:string;

  private addressShops:ShopVO[] = [];
  private _shops:any = {};
  private resetShopName:string = null;
  private resetShop:ShopVO = null;

  private availableShops:Array<Pair<ShopVO, CustomerShopLinkVO>> = [];
  private supportedShops:Array<Pair<ShopVO, CustomerShopLinkVO>> = [];

  private _changes:Array<Pair<AttrValueCustomerVO, boolean>>;

  private selectedRow:AttrValueCustomerVO;

  private initialising:boolean = false; // tslint:disable-line:no-unused-variable
  private delayedChange:Future;

  private customerForm:any;
  private customerFormSub:any; // tslint:disable-line:no-unused-variable

  @ViewChild('attributeValuesComponent')
  private attributeValuesComponent:AttributeValuesComponent;

  @ViewChild('resetConfirmationModalDialog')
  private resetConfirmationModalDialog:ModalComponent;

  @ViewChild('typeHelpModalDialog')
  private typeHelpModalDialog:ModalComponent;

  private availableCustomerTypes:Pair<string, string>[] = null;
  private selectedCustomerType:string = null;

  private searchHelpShow:boolean = false;

  private reloadAddressbook:boolean = false;

  constructor(private _customerService:CustomerService,
              fb: FormBuilder) {
    LogUtil.debug('CustomerComponent constructed');

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


  formBind():void {
    UiUtil.formBind(this, 'customerForm', 'customerFormSub', 'delayedChange', 'initialising');
  }


  formUnbind():void {
    UiUtil.formUnbind(this, 'customerFormSub');
  }

  formChange():void {
    LogUtil.debug('CustomerComponent formChange', this.customerForm.valid, this._customer);
    this.dataChanged.emit({ source: new Pair(this._customer, this._changes), valid: this.customerForm.valid });
  }

  @Input()
  set customer(customer:CustomerVO) {

    UiUtil.formInitialise(this, 'initialising', 'customerForm', '_customer', customer, customer != null && customer.customerId > 0, ['email']);
    this._changes = [];
    this.tabSelected('Main');
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
    LogUtil.debug('CustomerComponent mapped shops', this._shops);
  }

  onAttributeDataChanged(event:any) {
    LogUtil.debug('CustomerComponent attr data changed', this._customer);
    this._changes = event.source;
    this.delayedChange.delay();
  }

  ngOnInit() {
    LogUtil.debug('CustomerComponent ngOnInit');
    this.formBind();
  }

  ngOnDestroy() {
    LogUtil.debug('CustomerComponent ngOnDestroy');
    this.formUnbind();
  }

  tabSelected(tab:any) {
    LogUtil.debug('CustomerComponent tabSelected', tab);
    this.reloadAddressbook = tab === 'Addressbook';
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
    LogUtil.debug('CustomerComponent onSelectRow handler', row);
    if (row == this.selectedRow) {
      this.selectedRow = null;
    } else {
      this.selectedRow = row;
    }
  }

  onSupportedShopClick(supported:Pair<ShopVO, CustomerShopLinkVO>) {
    LogUtil.debug('CustomerComponent remove supported', supported);
    let idx = this._customer.customerShops.findIndex(link =>
      link.shopId == supported.first.shopId
    );
    if (idx != -1) {
      this._customer.customerShops[idx].disabled = true;
      this.recalculateShops();
      this.formChange();
    }
  }

  onAvailableShopClick(available:Pair<ShopVO, CustomerShopLinkVO>) {
    LogUtil.debug('CustomerComponent add supported', available);
    let idx = this._customer.customerShops.findIndex(link =>
      link.shopId == available.first.shopId
    );
    available.second.disabled = false;
    if (idx != -1) {
      this._customer.customerShops[idx].disabled = false;
    } else {
      this._customer.customerShops.push(available.second);
    }
    this.recalculateShops();
    this.formChange();
  }

  onRowReset(shop:ShopVO) {
    this.resetShopName = shop.code + ' ' + shop.name;
    this.resetShop = shop;
    this.resetConfirmationModalDialog.show();
  }

  protected onResetConfirmationResult(modalresult: ModalResult) {
    LogUtil.debug('CustomerComponent onResetConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {

      this.passwordReset.emit(new Pair(this._customer, this.resetShop));

    }
  }


  protected onClearFilter() {

    this.attributeFilter = '';

  }

  protected onSearchHelpToggle() {
    this.searchHelpShow = !this.searchHelpShow;
  }

  protected onSearchValues() {
    this.searchHelpShow = false;
    this.attributeFilter = '###';
  }

  protected onSearchValuesNew() {
    this.searchHelpShow = false;
    this.attributeFilter = '##0';
  }

  protected onSearchValuesNewOnly() {
    this.searchHelpShow = false;
    this.attributeFilter = '#00';
  }

  protected onSearchValuesChanges() {
    this.searchHelpShow = false;
    this.attributeFilter = '#0#';
  }

  protected onTypeHelpClick() {

    let lang = I18nEventBus.getI18nEventBus().current();
    let _sub:any = this._customerService.getCustomerTypes(lang).subscribe((types:Pair<string, string>[]) => {
      LogUtil.debug('CustomerComponent available types', types);
      this.availableCustomerTypes = types;
      _sub.unsubscribe();

      this.typeHelpModalDialog.show();
    });

  }

  protected onSelectCustomerType(customerType:Pair<string, string> ) {
    this.selectedCustomerType = customerType.first;
  }

  protected onSelectCustomerTypeResult(modalresult: ModalResult) {
    LogUtil.debug('CustomerComponent onSelectCustomerTypeResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {
      this._customer.customerType = this.selectedCustomerType;
      this.selectedCustomerType = null;
      this.formChange();
    }
  }

  private recalculateShops():void {

    if (this._customer) {
      this.availableShops = this.getAvailableShopNames();
      this.supportedShops = this.getSupportedShopNames();
    } else {
      this.availableShops = this.getAvailableShopNames();
      this.supportedShops = [];
    }

    let addressShops:ShopVO[] = [];
    this.supportedShops.forEach(item => {
      addressShops.push(item.first);
    });
    this.addressShops = addressShops;

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
    LogUtil.debug('CustomerComponent supported shops', skipKeys);

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

    LogUtil.debug('CustomerComponent available shops', labels);
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
    LogUtil.debug('CustomerComponent supported', keepKeys);

    let labels = <Array<Pair<ShopVO, CustomerShopLinkVO>>>[];
    for (let key in this._shops) {
      let idx = keepKeys.indexOf(key);
      if (idx != -1) {
        let shop = this._shops[key];
        let customerShop = this._customer.customerShops[idx];
        labels.push({ first: shop, second: customerShop });
      }
    }

    LogUtil.debug('CustomerComponent supported', labels);
    return labels;
  }


}
